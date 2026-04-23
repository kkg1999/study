import java.io.*;
import java.util.concurrent.locks.*;
import java.util.Random;

class SafeStack<T>{
	private Node<T> head, tail;
	private Lock lock;
	private Condition notEmpty, notFull;
	private int size, capacity;

	SafeStack(int _capacity){
		Node<T> node = new Node<>(null);
		head = node;
		tail = head;

		lock = new ReentrantLock();
		notEmpty = lock.newCondition();
		notFull = lock.newCondition();

		size = 0;
		capacity = _capacity;
	}

	void push(T x){
		Node<T> node = new Node<>(x);
		lock.lock();
		try{
			while(size == capacity)
				notFull.await();
			tail.next = node;
			node.prev = tail;
			tail = tail.next;
			size++;

			notEmpty.signal();
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
		}finally{
			lock.unlock();
		}
	}

	T pop(){
		T result = null;
		lock.lock();
		try{
			while(tail == head)
				notEmpty.await();
			result = tail.val;
			tail = tail.prev;
			tail.next = null;
			size--;

			notFull.signal();
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
		}finally{
			lock.unlock();
		}
		return result;
	}


	static class Node<T>{
		T val;
		Node<T> prev, next; // no need to use volatile here since we are using one lock
		Node(T value){
			val = value;
			prev = null;
			next = null;
		}
	}
}


public class StackThreadSafe{
	static SafeStack<Integer> st;
	static volatile boolean stop = false;
	static Random random;
	public static void main(String[] args) {
		st = new SafeStack<>(4);
		random = new Random();
		Thread t1 = new Thread(StackThreadSafe::addstuff);
		Thread t2 = new Thread(StackThreadSafe::addstuff);
		Thread t3 = new Thread(StackThreadSafe::removestuff);

		t1.start(); t2.start(); t3.start();


		try{
			Thread.sleep(10000);
			stop = true;
			t1.interrupt();
			t2.interrupt();
			t3.interrupt();
			t1.join();
			t2.join();
			t3.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	static void addstuff(){
		while(!stop){
			int x = random.nextInt(100);
			st.push(x);
			System.out.println("push: "+x);
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
	}

	static void removestuff(){
		while(!stop){
			int x = st.pop();
			System.out.println("\tpop: "+x);
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
	}
}