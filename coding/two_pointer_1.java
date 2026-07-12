import java.util.*;

class Solution{
	public int[] score(int[] a, int[] b){
		int n1 = a.length, n2 = b.length;
		int[] ans = new int[n1];
		int L=0, R=0;
		while(L<n1 && R<n2){
			if(a[L]>b[R]){
				R++;
			}
			else{
				ans[L] = R;
				L++;
			}
		}

		while(L<n1){
			ans[L] = R;
			L++;
		}
		printar(ans);
		return ans;
	}

	void printar(int[] ar){
		for(int x:ar){
			System.out.print(x + " ");
		}
		System.out.println();		
	}
}

class two_pointer_1{
	public static void main(String[] args) {
		Solution sol = new Solution();
		sol.score(new int[]{5, 9, 17, 28}, new int[]{8, 11, 20, 25} );
		sol.score(new int[]{5, 9, 17, 28}, new int[]{2, 3, 3} );
		sol.score(new int[]{5, 9, 17}, new int[]{20, 25} );
	}
}
