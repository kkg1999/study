import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestaurantCustomers {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
        List<int[]> list = new ArrayList<>();

        int n = Integer.parseInt(br.readLine());
        for(int i=0; i<n; i++){
            var temp = br.readLine().split(" ");
            int x = Integer.parseInt(temp[0]);
            int y = Integer.parseInt(temp[1]);

            list.add(new int[]{x, 1}); // 1 marks arrival
            list.add(new int[]{y, -1});
        }

        list.sort((a,b) -> a[0]-b[0]);

        int count = 0, maxcount = 0;
        for(int[] cur:list){
            count += cur[1];
            maxcount = Math.max(maxcount, count);
        }
        pw.println(maxcount);
        br.close(); pw.close();
    }

}
