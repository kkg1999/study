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