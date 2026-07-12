#include <vector>
#include <algorithm>
#include <iostream>
using namespace std;

vector<vector<int>> adj;
vector<int> parent;
int cycle_start, cycle_end;

bool dfs(int x, int p, vector<bool>& vis){
	vis[x] = true;
	for(int y:adj[x]){
		if(y==p) continue; //do not go to parent
		parent[y] = x;
		if(vis[y]){
			cycle_start = y;
			cycle_end = x;
			return true;
		}
		else{
			if(dfs(y, x, vis))
				return true;
		}
	}
	return false;
}


int main(){
	int n, m;
	cin >> n >> m;
	adj.resize(n+1);
	parent.resize(n+1);

	for(int i=0; i<m; i++){
		int a,b;
		cin>>a>>b;
		adj[a].push_back(b);
		adj[b].push_back(a);
	}

	cycle_start = -1;
	vector<bool> visited(n+1, false);
	for(int i=1; i<=n; i++){
		if(visited[i] == false)
			if(dfs(i, -1, visited))
				break;
	}

	if(cycle_start == -1){
		cout<<"IMPOSSIBLE\n";
		return 0;
	}

	vector<int> cycle;
	cycle.push_back(cycle_start);
	for(int v=cycle_end; v!=cycle_start; v = parent[v])
		cycle.push_back(v);
	cycle.push_back(cycle_start);
	// reverse(cycle.begin(), cycle_end());
	cout<<cycle.size()<<'\n';
	for(int x:cycle)
		cout<<x<<" ";
	cout << endl;

	return 0;
}