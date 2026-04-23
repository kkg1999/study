import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskScheduler {

    // --- 1. Core Data Structures ---
    private final PriorityQueue<ScheduledTask> queue;
    private final Lock lock;
    private final Condition condition;
    private final AtomicBoolean isRunning;
    private final List<Thread> workerThreads;

    // --- 2. Constructor ---
    public TaskScheduler(int workerCount) {
        this.queue = new PriorityQueue<>();
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.isRunning = new AtomicBoolean(true);
        this.workerThreads = new ArrayList<>();

        // Initialize and start worker threads
        for (int i = 0; i < workerCount; i++) {
            Thread worker = new Thread(new Worker(), "Worker-Thread-" + (i + 1));
            workerThreads.add(worker);
            worker.start();
        }
    }

    // --- 3. Producer Methods ---

    // Standard schedule for Runnable (no return value)
    public void schedule(Runnable task, long delay, TimeUnit unit) {
        scheduleTask(new ScheduledTask(task, delay, unit));
    }

    // Advanced schedule for Callable (returns a Future)
    public <T> FutureTask<T> schedule(Callable<T> callable, long delay, TimeUnit unit) {
        FutureTask<T> futureTask = new FutureTask<>(callable);
        scheduleTask(new ScheduledTask(futureTask, delay, unit));
        return futureTask;
    }

    // Internal helper to handle the locking logic
    private void scheduleTask(ScheduledTask scheduledTask) {
        lock.lock();
        try {
            queue.offer(scheduledTask);
            // If the new task is at the very top, wake up workers to re-evaluate
            if (queue.peek() == scheduledTask) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    // --- 4. Graceful Shutdown ---
    public void shutdown() {
        lock.lock();
        try {
            isRunning.set(false);
            condition.signalAll(); // Wake up all sleeping threads so they can exit
        } finally {
            lock.unlock();
        }
        
        // Optional: Wait for threads to finish their current execution loop
        for (Thread worker : workerThreads) {
            try {
                worker.join(1000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Scheduler shut down successfully.");
    }

    // --- 5. The Worker (Consumer) ---
    private class Worker implements Runnable {
        @Override
        public void run() {
            while (isRunning.get()) {
                ScheduledTask taskToRun = null;

                lock.lock();
                try {
                    // Wait if the queue is empty
                    while (queue.isEmpty() && isRunning.get()) {
                        condition.await();
                    }
                    
                    // If awakened for shutdown, break out of the loop
                    if (!isRunning.get()) {
                        break;
                    }

                    ScheduledTask topTask = queue.peek();
                    long timeToWait = topTask.getScheduledTime() - System.currentTimeMillis();

                    if (timeToWait <= 0) {
                        // Task is ready! Remove from queue.
                        taskToRun = queue.poll();
                    } else {
                        // Task is in the future. Sleep until it's ready.
                        condition.await(timeToWait, TimeUnit.MILLISECONDS);
                    }
                } catch (InterruptedException e) {
                    // Restore interrupt status and check if we are shutting down
                    Thread.currentThread().interrupt();
                    if (!isRunning.get()) {
                        break;
                    }
                } finally {
                    lock.unlock();
                }

                // Execute OUTSIDE the lock to allow concurrent submissions
                if (taskToRun != null) {
                    try {
                        taskToRun.execute();
                    } catch (Exception e) {
                        System.err.println("Task failed: " + e.getMessage());
                    }
                }
            }
        }
    }

    // --- 6. The Task Wrapper ---
    private static class ScheduledTask implements Comparable<ScheduledTask> {
        private final Runnable task;
        private final long scheduledTime;

        public ScheduledTask(Runnable task, long delay, TimeUnit unit) {
            this.task = task;
            this.scheduledTime = System.currentTimeMillis() + unit.toMillis(delay);
        }

        public void execute() {
            task.run();
        }

        public long getScheduledTime() {
            return scheduledTime;
        }

        @Override
        public int compareTo(ScheduledTask other) {
            return Long.compare(this.scheduledTime, other.scheduledTime);
        }
    }

    // --- 7. Driver / Main Method for Testing ---
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Scheduler...");
        TaskScheduler scheduler = new TaskScheduler(2); // 2 worker threads

        // Schedule a simple Runnable
        scheduler.schedule(() -> {
            System.out.println(Thread.currentThread().getName() + " executed Task 1 (Immediate)");
        }, 0, TimeUnit.SECONDS);

        // Schedule a task in the future
        scheduler.schedule(() -> {
            System.out.println(Thread.currentThread().getName() + " executed Task 2 (3 seconds delay)");
        }, 3, TimeUnit.SECONDS);

        // Schedule a Callable that returns a result
        FutureTask<String> futureResult = scheduler.schedule(() -> {
            System.out.println(Thread.currentThread().getName() + " executed Task 3 (Callable, 1 second delay)");
            return "SUCCESS: Database updated!";
        }, 1, TimeUnit.SECONDS);

        // Main thread waits for the Callable result
        System.out.println("Main thread waiting for Callable result...");
        String result = futureResult.get(); // This will block until Task 3 finishes
        System.out.println("Main thread received: " + result);

        // Wait a bit to let Task 2 finish, then shut down
        Thread.sleep(3000);
        scheduler.shutdown();
    }
}
