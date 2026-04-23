import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Solution{
	private final int NUM_BUFFER;
	private final int BUFFER_SIZE;

	volatile boolean stop;
	private List<Integer>[] buffers;

	private Lock[] bufferLocks;
	private Semaphore[] bufferNotFull;
	private Semaphore bufferNotEmpty; //any buffer
	
	Solution(int num_buffer, int buffer_size){
		NUM_BUFFER = num_buffer; 
		BUFFER_SIZE = buffer_size;
		stop = false;

		buffers = new LinkedList[NUM_BUFFER];
		bufferLocks = new ReentrantLock[NUM_BUFFER];
		bufferNotFull = new Semaphore[NUM_BUFFER];
		for(int i=0; i<NUM_BUFFER; i++){
			buffers[i] = new LinkedList<>();

			bufferLocks[i] = new ReentrantLock();
			bufferNotFull[i] = new Semaphore(BUFFER_SIZE);
			bufferNotEmpty = new Semaphore(0); //initially all empty
		}
	}

	void producer(int id){
		Random random = new Random();
		while(!stop){
			int data = random.nextInt(100);
			try{
				bufferNotFull[id].acquire();
				bufferLocks[id].lock();
				buffers[id].add(data);
				System.out.println("produce: "+data+ " : by " + Thread.currentThread().getName());
				
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
			finally{
				bufferLocks[id].unlock();
				bufferNotEmpty.release();
			}

			try{
				Thread.sleep(100);
			}
			catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}

		}
	}

	public void consumer(){
		while(!stop){
			try{
				bufferNotEmpty.acquire();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}

			int data = -1, chosen = -1;
			for(int i=0; i<NUM_BUFFER; i++){
				bufferLocks[i].lock();
				try{		
					if(buffers[i].size()>0){
						chosen = i;
						data = buffers[i].get(0);
						buffers[i].remove(0);
					}
				}finally{
					bufferLocks[i].unlock();
				}
			}

			if(chosen != -1){
				System.out.println("consume: "+data+ " : from " + chosen);
				bufferNotFull[chosen].release();
			}
			else{
				System.out.println("yield");
				Thread.yield(); //do nothing
			}
		}
	}
}

class MultiProducerConsumer{
	public static void main(String[] args) {
		int n = 4;
		Solution sol = new Solution(n, 5);
		Thread[] producerList = new Thread[n];
		for(int i=0; i<n; i++){
			final int id = i;
			producerList[i] = new Thread( () -> sol.producer(id) );
			producerList[i].start();
		}

		Thread w1 = new Thread(() -> sol.consumer());
		Thread w2 = new Thread(() -> sol.consumer());
		w1.start(); w2.start();

		try{
			System.out.println("here1");
			Thread.sleep(1000);
		}catch(InterruptedException e){}

		sol.stop = true;
		try{
			for(Thread t:producerList)
				t.join();
			w1.join(); w2.join();
		}catch(InterruptedException e){}

	}
}

