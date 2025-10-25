import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TasksandDeadlines {
    // https://cses.fi/problemset/task/1630

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        int n = Integer.parseInt(br.readLine());
        var list = new ArrayList<int[]>();
        for(int i=0; i<n; i++){
            var temp = br.readLine().split(" ");
            int duration = Integer.parseInt(temp[0]);
            int deadline = Integer.parseInt(temp[1]);
            list.add(new int[]{duration, deadline});
        }

        list.sort( (x,y) -> { //sort by duration
            if (x[0] == y[0]) return x[1]-y[1];
            return x[0]-y[0];
        });

        // we do: shortest job first
        long counter = 0, ans = 0;
        for(int[] cur:list){
            counter += cur[0]; // completion time of this task
            ans += ( cur[1] - counter ); // add (deadline - finish time)
        }
        pw.println(ans);
        br.close(); pw.close();
    }
}
