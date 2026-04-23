import java.io.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

class Solution{
	CyclicBarrier barrier;
	Semaphore h = new Semaphore(2);
	Semaphore o = new Semaphore(1);
	int n;
	Solution(int n){
		this.n = n;
		barrier = new CyclicBarrier(3, ()->{
						this.n--;
						System.out.println("\nlast: "+Thread.currentThread().getName());
					}); //last thread to reach will print a newline
	}

	void printH(){
		while(true){
			try{
				h.acquire();
				barrier.await();
				if(n<=0) return;
				System.out.print("H");
			}catch(BrokenBarrierException | InterruptedException e){

			}finally{
				h.release();
			}
		}
	}

	void printO(){
		while(true){
			try{
				o.acquire();
				barrier.await();
				if(n<=0) return;
				System.out.print("O");
			}catch(BrokenBarrierException | InterruptedException e){

			}finally{
				o.release();	
			}
		}
	}
}

class BuildingH2O{
	public static void main(String[] args) {
		Solution sol = new Solution(5);
		Thread t1 = new Thread(sol::printH);
		Thread t2 = new Thread(sol::printH);
		Thread t3 = new Thread(sol::printO);

		t1.start();
		t2.start();
		t3.start();

		try{
			t1.join();
			t2.join();
			t3.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}

