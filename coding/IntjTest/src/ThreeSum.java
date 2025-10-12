import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class ThreeSum {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        var temp = br.readLine().split(" ");
        int n = Integer.parseInt(temp[0]);
        int sum = Integer.parseInt(temp[1]);

        temp = br.readLine().split(" ");
        var list = new ArrayList<int[]>();
        for(int i=0; i<n; i++)
            list.add( new int[]{Integer.parseInt(temp[i]), i});
        list.sort((x,y)->x[0]-y[0]);

        boolean found = false;
        for(int i=0; i<n; i++){
            int j = i+1, k = n-1;
            int target = sum - list.get(i)[0];
            while(j<k){
                int csum = list.get(j)[0] + list.get(k)[0];
                if (csum == target){
                    pw.println((list.get(i)[1]+1)+" "+(list.get(j)[1]+1)+" "+(list.get(k)[1]+1));
                    found = true;
                    break;
                }
                if (csum > target) //need to reduce
                    k--;
                else j++;
            }
            if (found) break;
        }
        if (!found) pw.println("IMPOSSIBLE");
        br.close(); pw.close();
    }
}
