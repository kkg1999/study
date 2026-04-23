#include <vector>
#include <iostream>
#include <algorithm>
using namespace std;

vector<vector<int>> adj;
vector<int> parent;
int cycle_start, cycle_end;


bool dfs(int x, vector<int>& vis){
	if(vis[x] == 1)
		return true;
	vis[x] = 1;
	for(int y:adj[x]){
		if(vis[y] == 0){
			parent[y] = x;
			if(dfs(y, vis))
				return true;
		}
		else if(vis[y] == 1){
			cycle_start = y;
			cycle_end = x;
			return true;
		}
	}
	vis[x] = 2;
	return false;
}

int main(){
	int n, m;
	cin >> n >> m;
	adj.resize(n+1);
	for(int i=0; i<m; i++){
		int a, b;
		cin >> a >> b;
		adj[a].push_back(b);
	}

	cycle_start = -1;
	vector<int> visited(n+1, 0);
	parent.assign(n+1, 0);
	for(int i=1; i<=n; i++){
		if(visited[i] == 0){
			if(dfs(i, visited))
				break;
		}
	}

	if(cycle_start == -1){
		cout<<"IMPOSSIBLE\n";
		return 0;
	}

	vector<int> cycle;
	cycle.push_back(cycle_start);
	for(int v=cycle_end; v!= cycle_start; v = parent[v])
		cycle.push_back(v);
	cycle.push_back(cycle_start);
	reverse(cycle.begin(), cycle.end());
	
	cout<<cycle.size()<<'\n';
	for(int x:cycle)
		cout << x <<" ";
	cout<<endl;

	return 0;	
}
