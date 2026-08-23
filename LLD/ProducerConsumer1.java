// Without using any library like BlockingQueue

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 1. The Custom Broker (The "Middle")
class MessageBroker<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int maxCapacity; // The "N" size for backpressure

    private final Lock lock = new ReentrantLock();
    // Conditions to manage backpressure and blocking
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public MessageBroker(int capacity) {
        this.maxCapacity = capacity;
    }

    // Producer calls this
    public void publish(T message) throws InterruptedException {
        lock.lock();
        try {
            // BACKPRESSURE: If the queue size crosses N, block the producer.
            // (Using a while loop prevents spurious wakeups)
            while (queue.size() == maxCapacity) {
                System.out.println(Thread.currentThread().getName() + " -> Queue full! Applying backpressure...");
                notFull.await(); 
            }
            
            queue.add(message);
            System.out.println(Thread.currentThread().getName() + " published: " + message);
            
            // Signal any waiting subscribers that the queue is no longer empty
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Subscriber calls this
    public T consume() throws InterruptedException {
        lock.lock();
        try {
            // If the queue is empty, block the subscriber.
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            
            T message = queue.poll();
            System.out.println(Thread.currentThread().getName() + " consumed: " + message);
            
            // Signal any waiting producers that space has opened up in the queue
            notFull.signalAll();
            
            return message;
        } finally {
            lock.unlock();
        }
    }
}

// 2. Main Execution (Multiple Producers & Subscribers)
public class ProducerConsumer1 {
    public static void main(String[] args) {
        // Initialize our custom broker with N = 5 (max capacity)
        MessageBroker<String> broker = new MessageBroker<>(5);

        // Create Multiple Producers (e.g., 3 producers)
        for (int i = 1; i <= 3; i++) {
            final int producerId = i;
            Thread producerThread = new Thread(() -> {
                int messageCount = 1;
                try {
                    while (true) {
                        String msg = "Msg-" + messageCount + " from P" + producerId;
                        broker.publish(msg);
                        messageCount++;
                        
                        // Simulate fast production
                        Thread.sleep(100); 
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Producer-" + i);
            producerThread.start();
        }

        // Create Multiple Subscribers (e.g., 2 subscribers)
        for (int i = 1; i <= 2; i++) {
            Thread subscriberThread = new Thread(() -> {
                try {
                    while (true) {
                        broker.consume();
                        
                        // Simulate slow consumption to purposely trigger backpressure
                        Thread.sleep(800); 
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Subscriber-" + i);
            subscriberThread.start();
        }
    }
}
