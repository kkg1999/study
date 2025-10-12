import java.io.*;
import java.util.Arrays;

class EditDistanceSpace{
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader( new InputStreamReader(System.in) );
		PrintWriter pw = new PrintWriter(System.out);

		char[] str1 = br.readLine().toCharArray();
		char[] str2 = br.readLine().toCharArray();

		int n1 = str1.length, n2 = str2.length;
		int[] mem = new int[n2+1];
		int[] cur = new int[n2+1];
		
		for(int i=1; i<=n2; i++) 
			mem[i] = i;
		
		for(int i=1; i<=n1; i++){
			cur[0] = i;
			for(int j=1; j<=n2; j++){
				if (str1[i-1] == str2[j-1]){
					cur[j] = mem[j-1];
				}
				else{
					cur[j] = 1 + Math.min(cur[j-1], Math.min(mem[j], mem[j-1]));
				}
			}

			mem = cur.clone();
			Arrays.fill(cur, 0);
		}

		pw.println(mem[n2]);
		pw.close();
	}
}