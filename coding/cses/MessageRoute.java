import java.io.*;
import java.util.*;

class MessageRoute{
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader( new InputStreamReader(System.in) );
		PrintWriter  pw = new PrintWriter(System.out);
		var temp = br.readLine().split(" ");
		int n = Integer.parseInt(temp[0]);
		int m = Integer.parseInt(temp[1]);

		List<List<Integer>> adj = new ArrayList<>();
		for(int i=0; i<=n; i++) //1 based indexing
			adj.add(new ArrayList<>());
		boolean[] visited = new boolean[n+1];
		int[] parent = new int[n+1];

		for(int i=0; i<m; i++){
			temp = br.readLine().split(" ");
			int x = Integer.parseInt(temp[0]);
			int y = Integer.parseInt(temp[1]);

			adj.get(x).add(y);
			adj.get(y).add(x);
		}

		Queue<Integer> q = new ArrayDeque<>();
		q.offer(1);
		visited[1] = true;
		while(q.size()>0){
			int cur = q.poll();
			for(int next:adj.get(cur)){
				if(visited[next]) continue;
				q.offer(next);
				visited[next] = true;
				parent[next] = cur;
			}
		}

		if (visited[n] == false){
			pw.println("IMPOSSIBLE");
			pw.close();
			return;
		}

		List<Integer> ans = new ArrayList<>();
		int x = n;
		while(x!=1){
			ans.add(x);
			x = parent[x];
		}
		ans.add(1);
		Collections.reverse(ans);
		pw.println(ans.size());
		for(int loop:ans){
			pw.print(loop);
			pw.print(' ');
		}
		pw.println();
		pw.close();
	}
}