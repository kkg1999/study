import java.io.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

class ParallelSort3 extends RecursiveAction {
	private int[] ar;
	private int left, right;

	public ParallelSort3(int[] ar, int left, int right){
		this.ar = ar;
		this.left = left;
		this.right = right;
	}

	@Override
	public void compute(){
		if(left>=right)
			return ;
		int[] pivot = partition3();
		ParallelSort3 leftHalf = new ParallelSort3(ar, left, pivot[0]-1);
		ParallelSort3 rightHalf = new ParallelSort3(ar, pivot[1]+1, right);

		invokeAll(leftHalf, rightHalf);
	}

	private int[] partition3(){
		int pivot = ar[right];
		int lt = left, gt = right;
		int i = left;
		while(i<=gt){
			if(ar[i]<pivot){
				swap(i, lt);
				i++; lt++;
			}
			else if(ar[i]>pivot){
				swap(i, gt);
				gt--;
			}
			else{
				i++;
			}
		}

		return new int[]{lt, gt};
	}

	private void swap(int x, int y){
		int t = ar[x];
		ar[x] = ar[y];
		ar[y] = t;
	}
}


class ParallelQuickSort{
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()-1);
		int[] ar = {5, 4, 4, 2, 1, 2, 6};
		pool.invoke(new ParallelSort3(ar, 0, ar.length-1));
		for(int x:ar){
			System.out.print(x + " ");
		}
		System.out.println();
		pool.shutdown();
	}
}