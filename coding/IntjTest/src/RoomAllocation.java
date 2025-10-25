import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class RoomAllocation {
    // https://cses.fi/problemset/task/1164/
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        int n = Integer.parseInt(br.readLine());
        int[][] list = new int[2*n][];
        int k = 0;
        for(int i=0; i<n; i++){
            var temp = br.readLine().split(" ");
            int arrival = Integer.parseInt(temp[0]);
            int departure = Integer.parseInt(temp[1]);
            list[k++] = new int[]{i, arrival, 1};
            list[k++] = new int[]{i, departure, 2};
        }
        Arrays.sort(list, (a,b)-> {
            if (a[1] == b[1]) return a[2]-b[2];
            return a[1]-b[1];
        });

        var freed = new ArrayDeque<Integer>();
        int counter = 0;
        int[] allocation = new int[n];
        for(int [] cur: list){
            int room = 0;
            if (cur[2] == 1){ //arrival
                if (freed.isEmpty()){
                    counter++;
                    freed.push(counter);
                }
                allocation[cur[0]] = freed.peek();
                freed.pop();
            }
            if (cur[2] == 2){
                // this customer's room is free now
                freed.push(allocation[cur[0]]);
            }
        }

        pw.println(counter);
        for (int i = 0; i < n; i++) {
            pw.print(allocation[i]);
            pw.print(' ');
        }
        pw.println();
        br.close(); pw.close();
    }
}
