import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class FactoryMachine {
    private static boolean isvalid(int[] ar, long t, int target){
        long count = 0;
        for(int x:ar){
            count += (t/x);
            if (count>=target) return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        var temp = br.readLine().split(" ");
        int n = Integer.parseInt(temp[0]);
        int target = Integer.parseInt(temp[1]);

        temp = br.readLine().split(" ");
        int[] ar = new int[n];
        for(int i=0; i<n; i++)
            ar[i] = Integer.parseInt(temp[i]);

        long lo = 1, hi = (long)1e18, ans = 0;
        while(lo<=hi){
            long mid = lo + (hi-lo)/2;
            if (isvalid(ar, mid, target)){
                ans = mid;
                hi = mid-1;
            }
            else
                lo = mid+1;
        }
        pw.println(ans);
        pw.close();
    }
}
