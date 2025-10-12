import java.io.*;

public class CoinCombinations {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        int n,sum;
        var temp = br.readLine().split(" ");
        n = Integer.parseInt(temp[0]);
        sum = Integer.parseInt(temp[1]);
        int[] ar = new int[n];

        temp = br.readLine().split(" ");
        for(int i=0; i<n; i++)
            ar[i] = Integer.parseInt(temp[i]);


        // now we have: n, ar[n], and sum
        int[] mem = new int[sum+1];
        for(int x=1; x<=sum; x++){
            mem[x] = (int)1e9;
            for(int cur:ar){
                if (x-cur>=0){
                    mem[x] = Math.min( mem[x], 1 + mem[x-cur]);
                }
            }
        }

        if (mem[sum] == (int)1e9)
            pw.println(-1);
        else
            pw.println(mem[sum]);
        pw.close();
    }
}
