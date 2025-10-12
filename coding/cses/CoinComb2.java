import java.io.*;

class CoinComb2 {
    private static final int MOD = (int)1e9 + 7;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);


        int n, amount;
        var temp = br.readLine().split(" ");
        n = Integer.parseInt(temp[0]);
        amount = Integer.parseInt(temp[1]);

        int[] coins = new int[n];
        temp = br.readLine().split(" ");
        for(int i=0; i<n; i++)
            coins[i] = Integer.parseInt(temp[i]);

        int[] dp = new int[amount+1];
        dp[0] = 1; //we can make (0) in only one way
        // coins need to be outer loop to avoid duplicates
        for(int c:coins){
            for(int x=1; x<=amount; x++){
                if (x-c>=0)
                    dp[x] = (dp[x] + dp[x-c])%MOD;
            }
        }

        pw.println(dp[amount]);
        pw.close();
    }
}