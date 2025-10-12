import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

public class TwoSum {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
        var map = new HashMap<Integer, Integer>();

        var temp = br.readLine().split(" ");
        int n = Integer.parseInt(temp[0]);
        int sum = Integer.parseInt(temp[1]);
        boolean found = false;
        temp = br.readLine().split(" ");
        for(int i=0; i<n; i++){
            int cur = Integer.parseInt(temp[i]);
            if (map.containsKey(sum - cur)){
                int j = map.get(sum-cur)+1;
                pw.println(j +" "+ (i+1));
                found = true;
                break;
            }
            map.put(cur, i);
        }

        if (!found) pw.println("IMPOSSIBLE");
        br.close(); pw.close();
    }
}
