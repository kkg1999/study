import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


class PrintLeapYears{
	private final Lock lock = new ReentrantLock();
	private final Condition c_leap = lock.newCondition();
	private final Condition c_non_leap = lock.newCondition();

	private static final int MAX_YEAR = 2500;
	private int cur = 2000;

	private void printLeap()  {
		while(cur < MAX_YEAR){
			lock.lock();
			try{
				if(checkIfLeap(cur)){
					Thread.sleep(1000);
					System.out.println(cur+" : leap. By: "+Thread.currentThread().getName());
					cur++;
					c_non_leap.signal();
				}
				else{
					c_leap.await();
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			finally{
				lock.unlock();
			}
		}

	}

	private void printNonLeap() {
		while(cur < MAX_YEAR){
			lock.lock();
			try{
				if(checkIfLeap(cur))
					c_non_leap.await();
				else{
					Thread.sleep(1000);
					System.out.println(cur+" : NOT leap. By: "+Thread.currentThread().getName());
					cur++;
					c_leap.signal();
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			finally{
				lock.unlock();
			}
		}
	}

	private boolean checkIfLeap(int x){
		return x%4 == 0 && (x%100 != 0 || x%400 == 0);
	}


	public static void main(String[] args) {
		PrintLeapYears dummyObj = new PrintLeapYears();
		Thread t1 = new Thread(dummyObj::printLeap);
		Thread t2 = new Thread(dummyObj::printNonLeap);
		t1.start();
		t2.start();

		try{
			t1.join();
			t2.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}




