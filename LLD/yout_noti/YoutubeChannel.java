class YoutubeChannel {
    private String channelName;
    
    // Smart Subject: Grouping subscribers by preference
    private Map<NotificationPreference, List<Subscriber>> subscribers;
    
    // Thread pool for handling fan-out
    private ExecutorService executorService;
    private static final int BATCH_SIZE = 1000;

    public YoutubeChannel(String channelName) {
        this.channelName = channelName;
        this.subscribers = new EnumMap<>(NotificationPreference.class);
        
        // Initialize lists for each preference
        for (NotificationPreference pref : NotificationPreference.values()) {
            // Note for interview: Use CopyOnWriteArrayList in a real multi-threaded system
            subscribers.put(pref, new ArrayList<>()); 
        }
        
        // A fixed thread pool of 50 workers
        this.executorService = Executors.newFixedThreadPool(50);
    }

    public void subscribe(Subscriber subscriber) {
        subscribers.get(subscriber.getPreference()).add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.get(subscriber.getPreference()).remove(subscriber);
    }

    // Triggered when a creator uploads a video
    public void uploadVideo(String videoTitle, boolean isHighlight) {
        System.out.println("\n--- " + channelName + " successfully uploaded: " + videoTitle + " ---");
        
        // 1. Filtering Logic
        List<Subscriber> targetAudience = new ArrayList<>(subscribers.get(NotificationPreference.ALL));
        
        // If it's a major highlight, we also notify the PERSONALIZED group
        if (isHighlight) {
            targetAudience.addAll(subscribers.get(NotificationPreference.PERSONALIZED));
        }
        // (We completely ignore the NONE list, saving memory and CPU!)

        // 2. Fan-out Logic
        fanOutNotifications(targetAudience, videoTitle);
    }

    private void fanOutNotifications(List<Subscriber> targetAudience, String videoTitle) {
        int totalSubscribers = targetAudience.size();
        
        // Split the audience into chunks
        for (int i = 0; i < totalSubscribers; i += BATCH_SIZE) {
            final int start = i;
            final int end = Math.min(i + BATCH_SIZE, totalSubscribers);
            
            // Extract the batch
            List<Subscriber> batch = targetAudience.subList(start, end);

            // Submit the batch to the background thread pool
            executorService.submit(() -> {
                for (Subscriber sub : batch) {
                    try {
                        sub.update(this.channelName, videoTitle);
                    } catch (Exception e) {
                        // Fault Tolerance: Catch exception so the loop doesn't break for the rest of the batch
                        System.err.println("Failed to notify user. Continuing...");
                    }
                }
            });
        }
    }
    
    // Graceful shutdown for the thread pool
    public void shutdown() {
        executorService.shutdown();
    }
}