import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.TreeSet;

public class DistinctNumbers  {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        var set = new HashSet<Integer>();
        var temp = br.readLine().split(" ");
        for(int i=0; i<n; i++){
            int x = Integer.parseInt(temp[i]);
            set.add(x);
        }
        System.out.println(set.size());
        br.close();
    }
}
