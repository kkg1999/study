import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MaximumSubarraySum {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        int n = Integer.parseInt(br.readLine());
        var temp = br.readLine().split(" ");
//        int[] ar = new int[n];
        long maxsum = 0, cursum = 0, maxval = Long.MIN_VALUE;
        for(int i=0; i<n; i++){
            int cur = Integer.parseInt(temp[i]);
            cursum += cur;
            if (cursum<0) cursum = 0;
            if (cursum>maxsum) maxsum = cursum;
            if (cur>maxval) maxval = cur;
        }
        if (maxval<0)
            pw.println(maxval);
        else
            pw.println(maxsum);
        br.close(); pw.close();
    }
}
