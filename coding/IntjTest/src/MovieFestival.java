import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MovieFestival {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
        int n = Integer.parseInt(br.readLine());
        var list = new ArrayList<int[]>();

        for(int i=0; i<n; i++){
            var temp = br.readLine().split(" ");
            int x = Integer.parseInt(temp[0]);
            int y = Integer.parseInt(temp[1]);
            list.add(new int[]{x,y});
        }

        list.sort((a,b)-> a[1]-b[1]);
        int count = 0; int endtime = 0;
        for(int[] loop:list){
            if (loop[0]>=endtime){
                endtime = Math.max(endtime, loop[1]);
                count++;
            }
        }
        pw.println(count);
        br.close(); pw.close();
    }
}
