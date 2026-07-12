import java.io.*;
import java.util.Random;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

class Node<T>{
	T val;
	volatile Node<T> next;
	public Node(T x){
		val = x;
		next = null;
	}
}

class BlockingQueueLL<T>{
	private Lock enqLock, deqLock;
	private Condition notFull, notEmpty;
	private AtomicInteger size;
	private int capacity;
	volatile Node<T> head, tail;

	BlockingQueueLL(int capacity){
		this.capacity = capacity;
		enqLock = new ReentrantLock();
		notFull = enqLock.newCondition();
		deqLock = new ReentrantLock();
		notEmpty = deqLock.newCondition();
		size = new AtomicInteger(0);
		head = new Node<>(null);
		tail = head;
	}


	public void enq(T x) throws InterruptedException{
		boolean wakeUpDeq = false;
		enqLock.lock();
		try{
			while(size.get() == capacity)
				notFull.await();
			Node<T> node = new Node<>(x);
			tail.next = node;
			tail = tail.next;
			if(size.getAndIncrement() == 0)
				wakeUpDeq = true;
			
		}finally{
			enqLock.unlock();
		}

		if(wakeUpDeq){
			deqLock.lock();
			try{
				notEmpty.signalAll();
			}finally{
				deqLock.unlock();
			}
		}
	}

	public T deq() throws InterruptedException{
		T result;
		boolean wakeUpEnq = false;
		deqLock.lock();
		try{
			while(head.next == null)
				notEmpty.await();
			result = head.next.val;
			head = head.next;
			if(size.getAndDecrement() == capacity)
				wakeUpEnq = true;
		}finally{
			deqLock.unlock();
		}

		if(wakeUpEnq){
			enqLock.lock();
			try{
				notFull.signalAll();
			}finally{
				enqLock.unlock();
			}
		}

		return result;
	}
}


class BlockingQueue1{
	static volatile boolean stop = false;
	static BlockingQueueLL<Integer> buffer;
	static Random random = new Random();
	public static void main(String[] args) {
		buffer = new BlockingQueueLL<>(3);
		Thread t1 = new Thread(BlockingQueue1::producer, "producer-1");
		Thread t2 = new Thread(BlockingQueue1::producer, "producer-2");
		Thread t3 = new Thread(BlockingQueue1::consumer, "consumer-1");
		Thread t4 = new Thread(BlockingQueue1::consumer, "consumer-2");

		t1.start();
		t2.start();
		t3.start();
		t4.start();

		try{
			Thread.sleep(5000);
			stop = true;

			t1.interrupt();
			t2.interrupt(); //we need to wake them up from sleep
			t3.interrupt();
			t4.interrupt();
			t1.join();
			t2.join();
			t3.join();
			t4.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}

	}

	private static void producer(){
		int counter = 1;
		while(!stop){
			int x = random.nextInt(100);
			System.out.println(counter++ + " enq: "+x + " by: "+ Thread.currentThread().getName());
			
			try{
				buffer.enq(x);
				Thread.sleep(300);
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
	}

	private static void consumer(){
		int counter = 1;
		while(!stop){
			try{
				int x = buffer.deq();
				System.out.println(counter++ + " deq: " + x + " by: "+ Thread.currentThread().getName());
				Thread.sleep(500);
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}			
		}
	}

}

