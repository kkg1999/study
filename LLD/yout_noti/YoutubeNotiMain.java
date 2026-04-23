import java.util.*;
import java.util.concurrent.*;

// 1. Notification Preferences
enum NotificationPreference {
    ALL,
    PERSONALIZED,
    NONE
}

// 2. Observer Interface
interface Subscriber {
    void update(String channelName, String videoTitle);
    NotificationPreference getPreference();
}

class User implements Subscriber {
    private String name;
    private NotificationPreference preference;

    public User(String name, NotificationPreference preference) {
        this.name = name;
        this.preference = preference;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        // In reality, this might trigger a mobile push or email via another service
        System.out.println("User " + name + " received notification: " + channelName + " uploaded '" + videoTitle + "'");
    }

    @Override
    public NotificationPreference getPreference() {
        return preference;
    }
}

class YoutubeChannel {
    private String channelName;
    
    // Smart Subject: Grouping subscribers by preference
    private Map<NotificationPreference, List<Subscriber>> subscribers;
    
    // Thread pool for handling fan-out
    private ExecutorService executorService;
    private static final int BATCH_SIZE = 1_000;

    public YoutubeChannel(String channelName) {
        this.channelName = channelName;
        this.subscribers = new EnumMap<>(NotificationPreference.class);
        
        // Initialize lists for each preference
        for (NotificationPreference pref : NotificationPreference.values()) {
            // Note for interview: Use CopyOnWriteArrayList in a real multi-threaded system
            subscribers.put(pref, new CopyOnWriteArrayList<>()); 
        }
        
        // A fixed thread pool of 50 workers
        this.executorService = Executors.newFixedThreadPool(10);
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
        List<Subscriber> targetAudience = new ArrayList<>(subscribers.get(NotificationPreference.ALL));
        if (isHighlight) {
            targetAudience.addAll(subscribers.get(NotificationPreference.PERSONALIZED));
        }

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
                        System.out.println("handled by: " + Thread.currentThread().getName());
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

public class YoutubeNotiMain {
    public static void main(String[] args) {
        YoutubeChannel techChannel = new YoutubeChannel("TechWithGemini");

        // Create users with different preferences
        User alice = new User("Alice", NotificationPreference.ALL);
        User bob = new User("Bob", NotificationPreference.PERSONALIZED);
        User charlie = new User("Charlie", NotificationPreference.NONE);

        techChannel.subscribe(alice);
        techChannel.subscribe(bob);
        techChannel.subscribe(charlie);

        // Upload 1: Standard video (Only 'ALL' should get it)
        techChannel.uploadVideo("Java Observer Pattern Tutorial", false);

        // Upload 2: Major highlight (Both 'ALL' and 'PERSONALIZED' should get it)
        techChannel.uploadVideo("1 Million Subscribers Celebration!", true);

        // Clean up
        techChannel.shutdown();
    }
}
