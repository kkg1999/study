import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.PriorityQueue;
public class Playlist {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        var map = new HashMap<Integer, Integer>();
        var temp = br.readLine().split(" ");
        int start = 0, maxlen = 0;
        for(int i=0; i<n; i++){
            int x = Integer.parseInt(temp[i]);

            if (map.get(x) != null){
                start = Math.max(start, map.get(x)+1); // +1 to move to next element
            }
            maxlen = Math.max(maxlen, i - start + 1);
            map.put(x, i);
        }
        System.out.println(maxlen);
        br.close();
    }
}
