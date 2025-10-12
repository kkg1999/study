import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Collections;

public class NearestSmallerValues {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        int n = Integer.parseInt(br.readLine());
        var temp = br.readLine().split(" ");
        var st = new ArrayDeque<int[]>(); // we maintain an increasing stack
        for(int i=0; i<n; i++) {
            int cur = Integer.parseInt(temp[i]);
            while( st.size()>0 && st.getFirst()[0]>=cur){
                st.pop();
            }
            if (st.size()>0){
                pw.print(st.getFirst()[1]);
            }
            else{
                pw.print(0);
            }
            pw.print(" ");
            st.push(new int[]{cur, i+1});
        }
        pw.println();
        br.close(); pw.close();
    }
}
