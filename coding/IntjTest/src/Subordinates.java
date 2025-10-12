import java.io.*;
import java.util.ArrayDeque;

public class Subordinates {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
        int n = Integer.parseInt(br.readLine());

        var temp = br.readLine().split(" ");
        int[] parent = new int[n+1];
        int[] childcount = new int[n+1];
        int[] size = new int[n+1];

        for(int i=0; i<n-1; i++) {
            int p = Integer.parseInt(temp[i]);
            parent[i+2] = p;
            childcount[p]++;
        }

        var q = new ArrayDeque<Integer>();
        for(int i=1; i<=n; i++){
            if (childcount[i] == 0)
                q.offer(i);
        }
        while(q.size()>0){
            int cur = q.poll();
            if (cur == 1) continue; // no parent
            int p = parent[cur];
            size[p] += size[cur]+1;
            if (--childcount[p] == 0)
                q.offer(p);
        }

        for(int i=1; i<=n; i++){
            pw.print(size[i]);
            pw.print(" ");
        }
        pw.println();
        br.close(); pw.close();
    }

}
