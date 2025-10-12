import java.io.*;
import java.util.ArrayDeque;

class LCScses{
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter pw = new PrintWriter(System.out);
		var temp = br.readLine().split(" ");
		int n1 = Integer.parseInt(temp[0]);
		int n2 = Integer.parseInt(temp[1]);
		int[] ar1 = new int[n1];
		int[] ar2 = new int[n2];

		temp = br.readLine().split(" ");
		for(int i=0; i<n1; i++)
			ar1[i] = Integer.parseInt(temp[i]);

		temp = br.readLine().split(" ");
		for(int i=0; i<n2; i++)
			ar2[i] = Integer.parseInt(temp[i]);

		int[][] mem = new int[n1+1][n2+1];
		for(int i=1; i<=n1; i++){
			for(int j=1; j<=n2; j++){
				if (ar1[i-1] == ar2[j-1]){
					mem[i][j] = 1 + mem[i-1][j-1];
				}
				else{
					mem[i][j] = Math.max(mem[i-1][j], mem[i][j-1]);
				}
			}
		}

		pw.println(mem[n1][n2]);
		ArrayDeque<Integer> ans = new ArrayDeque<>();
		int i = n1, j=n2;
		while(i>0&&j>0){
			if (ar1[i-1] == ar2[j-1]){
				ans.push(ar2[j-1]);
				i--; j--;
			}
			else if (mem[i-1][j] > mem[i][j-1]){
				i--;
			}
			else
				j--;
		}
		while(ans.size()>0){
			pw.print(ans.pop());
			pw.print(' ');
		}
		pw.println();
		pw.close();
	}
}