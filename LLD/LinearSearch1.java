import java.io.*;



class LinearSearch1{
	private static final Object obj = new Object();
	private static final int NUM_THREADS = 4;

	private static volatile int found = -1;

	public static void linear_search(int threadId, int[] ar, int key){
		int chunk_size = ar.length / NUM_THREADS;
		int start = chunk_size*threadId;
		int end = (threadId == NUM_THREADS-1) ? ar.length : start + chunk_size;
		for(int i=start; i<end; i++){
			System.out.println("index: " + i + "| item: "+ar[i] + "|thread: "+threadId);
			try{
				Thread.sleep(100);
			}catch(InterruptedException e){

			}

			synchronized(obj){
				if(found != -1)
					break;
			}

			if(ar[i] == key){
				synchronized(obj){
					System.out.println("found by: "+threadId);
					found = i;
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		int[] ar = new int[100];
		for(int i=0; i<ar.length; i++){
			ar[i] = (int)(Math.random()*100);
		}
		int key = 19;
		Thread[] threads = new Thread[NUM_THREADS];
		for(int i=0; i<NUM_THREADS; i++){
			final int id = i;
			threads[i] = new Thread(() -> linear_search(id, ar, key));
			threads[i].start();
		}

		for(int i=0; i<NUM_THREADS; i++){
			try{
				threads[i].join();
			}catch(Exception e){

			}
		}

		System.out.println("found at: "+found);
	}
}
