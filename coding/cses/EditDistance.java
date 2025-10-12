import java.io.*;

class EditDistance{
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader( new InputStreamReader(System.in) );
		PrintWriter pw = new PrintWriter(System.out);

		char[] str1 = br.readLine().toCharArray();
		char[] str2 = br.readLine().toCharArray();

		int n1 = str1.length, n2 = str2.length;
		int[][] mem = new int[n1+1][n2+1];
		
		for(int i=0; i<=n1; i++) 
			mem[i][0] = i;
		for(int j=0; j<=n2; j++) 
			mem[0][j] = j;
		
		for(int i=1; i<=n1; i++){
			for(int j=1; j<=n2; j++){
				if (str1[i-1] == str2[j-1]){
					mem[i][j] = mem[i-1][j-1];
				}
				else{
					mem[i][j] = 1 + Math.min(mem[i-1][j-1], Math.min(mem[i][j-1], mem[i-1][j]));
				}
			}
		}

		pw.println(mem[n1][n2]);
		pw.close();
	}
}