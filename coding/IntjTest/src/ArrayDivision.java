import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ArrayDivision {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
        var temp = br.readLine().split(" ");
        int n = Integer.parseInt(temp[0]);
        int target = Integer.parseInt(temp[1]);
        temp = br.readLine().split(" ");
        long[] ar = new long[n];
        long lo = 0, hi = 0;
        for(int i=0; i<n; i++){
            ar[i] = Long.parseLong(temp[i]);
            hi += ar[i];
            if (ar[i]>lo) lo = ar[i];
        }

        long ans = 0;
        while(lo<=hi){
            long mid = (lo+hi)/2;
            if (isvalid(ar, mid, target)){
                ans = mid;
                hi = mid-1;
            }
            else
                lo = mid+1;
        }
        pw.println(ans);
        br.close();
        pw.close();
    }

    private static boolean isvalid(long[] ar, long sum, int target){
        long cursum = 0L, maxsum = 0L;
        int count = 0;
        for(long x:ar){
            if (x>sum) return false;
            if (cursum+x>sum){
                if (cursum>maxsum) maxsum = cursum;
                cursum=0L;
                count++;
            }
            cursum += x;
        }
        if (cursum > 0) count++;
        return count<=target;
    }
}