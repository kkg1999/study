import java.io.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.Condition;

class PrintWithCondition{
	private Lock lock = new ReentrantLock();
	private Condition cond = lock.newCondition();
	private final int n;
	private int cur;

	PrintWithCondition(int n){
		this.n = n;
		cur = 1;
	}

	public void fizz() throws InterruptedException{
		while(true){
			lock.lock();
			try{
				while(cur<=n && !(cur%3 == 0 && cur%5 != 0))
					cond.await();
				if(cur>n) break;
				System.out.println(cur + " fizz " + Thread.currentThread().getName());
				cur++;
				cond.signalAll();
			}finally{
				lock.unlock();
			}
		}
				
	}

	public void buzz() throws InterruptedException{
		while(true){
			lock.lock();
			try{
				while(cur<=n && !(cur%3 != 0 && cur%5 == 0))
					cond.await();
				if(cur>n) break;
				System.out.println(cur + " buzz " + Thread.currentThread().getName());
				cur++;
				cond.signalAll();
			}finally{
				lock.unlock();
			}
		}
	}

	public void fizzbuzz() throws InterruptedException{
		while(true){
			lock.lock();
			try{
				while(cur<=n && cur%15!=0)
					cond.await();
				if(cur > n) break;
				System.out.println(cur + " fizzbuzz " + Thread.currentThread().getName());		
				cur++;
				cond.signalAll();
			}finally{
				lock.unlock();
			}
		}
	}

	public void number() throws InterruptedException{
		while(true){
			lock.lock();
			try{
				while(cur<=n && !(cur%3!=0 && cur%5!=0))
					cond.await();
				if(cur>n) break;
				System.out.println(cur + " " + Thread.currentThread().getName());
				cur++;
				cond.signalAll();
			}finally{
				lock.unlock();
			}
		}
	}
}

class PrintMultiSemaphore{
	private Semaphore sem_f = new Semaphore(0);
	private Semaphore sem_b = new Semaphore(0);
	private Semaphore sem_fb = new Semaphore(0);
	private Semaphore sem_n = new Semaphore(1);

	private final int n;
	PrintMultiSemaphore(int n){
		this.n = n;
	}


	public void fizz() throws InterruptedException{
		for(int i=1; i<=n; i++){
			if(i%3 == 0 && i%5 != 0){
				sem_f.acquire();
				System.out.println(i + " fizz " + Thread.currentThread().getName());
				sem_n.release();
			}
		}
	}

	public void buzz() throws InterruptedException{
		for(int i=1; i<=n; i++){
			if(i%3 != 0 && i%5 == 0){
				sem_b.acquire();
				System.out.println(i + " buzz " + Thread.currentThread().getName());
				sem_n.release();
			}
		}
	}

	public void fizzbuzz() throws InterruptedException{
		for(int i=1; i<=n; i++){
			if(i%15 == 0){
				sem_fb.acquire();
				System.out.println(i + " fizzbuzz " + Thread.currentThread().getName());
				sem_n.release();
			}
		}
	}

	public void number() throws InterruptedException{
		for(int i=1; i<=n; i++){
			sem_n.acquire();
			Thread.sleep(1000);
			if(i%15 == 0){
				sem_fb.release();
			}
			else if(i%3 == 0){
				sem_f.release();
			}
			else if(i%5 == 0){
				sem_b.release();
			}
			else{
				System.out.println(i + " " + Thread.currentThread().getName());
				sem_n.release();
			}
		}
	}
}



class FizzBuzzMultithreaded{
	public static void main(String[] args) {
		// PrintMultiSemaphore fb = new PrintMultiSemaphore(15);
		PrintWithCondition fb = new PrintWithCondition(20);
		
		Thread t1 = new Thread(() -> {
			try{
				fb.fizz();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}, "thread-1-fizz");

		Thread t2 = new Thread(() -> {
			try{
				fb.buzz();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}, "thread-2-buzz");

		Thread t3 = new Thread(() -> {
			try{
				fb.fizzbuzz();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}, "thread-3-fizzbuzz");

		Thread t4 = new Thread(() -> {
			try{
				fb.number();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}, "thread-4");


		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
}





