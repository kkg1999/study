import java.util.concurrent.BlockingQueue;

class Producer implements Runnable {
    private final BlockingQueue<Object> bq;

    public Producer(BlockingQueue<Object> bq) {
        this.bq = bq;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                bq.put(i);
                System.out.println("Produced: " + i);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Producer interrupted: " + Thread.currentThread().getName());
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Consumer implements Runnable {
    private final BlockingQueue<Object> bq;

    public Consumer(BlockingQueue<Object> bq) {
        this.bq = bq;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                consume(bq.take());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Consumer interrupted: " + Thread.currentThread().getName());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void consume(Object item) {
        System.out.println("Consuming: " + item + " by " + Thread.currentThread().getName());
    }
}

public class ProducerConsumer {
    public static void main(String[] args) {
        BlockingQueue<Object> bq = new java.util.concurrent.ArrayBlockingQueue<>(5);
        Producer p1 = new Producer(bq);
        Consumer c1 = new Consumer(bq);
        Consumer c2 = new Consumer(bq);

        new Thread(p1).start();
        new Thread(c1).start();
        new Thread(c2).start();
    }
}
