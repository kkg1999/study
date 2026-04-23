#include <iostream>
#include <vector>
using namespace std;

vector<vector<int>> adj, adj2;
void dfs(int, vector<vector<int>>&, vector<bool>&);
int main(){
	int n, m;
	cin >> n >> m;
	adj.resize(n);
	adj2.resize(n);
	for(int i=0; i<m; i++){
		int u, v;
		cin>> u >> v;
		u--; v--;
		adj[u].push_back(v);
		adj2[v].push_back(u);
	}

	vector<bool> visit1(n, false);
	dfs(0, adj, visit1);
	for(int i=0; i<n; i++){
		if(!visit1[i]){
			cout<<"NO\n";
			cout<<1<<" "<<i+1<<endl;
			return 0;
		}
	}
	vector<bool> visit2(n, false);
	dfs(0, adj2, visit2);
	for(int i=0; i<n; i++){
		if(!visit2[i]){
			cout<<"NO\n";
			cout<<i+1<<" "<<1<<endl;
			return 0;
		}
	}

	cout<<"YES\n";

	return 0;
}

void dfs(int u, vector<vector<int>>& adj, vector<bool>& visited){
	visited[u] = true;
	for(int v:adj[u]){
		if(!visited[v]){
			dfs(v, adj, visited);
		}
	}
}
