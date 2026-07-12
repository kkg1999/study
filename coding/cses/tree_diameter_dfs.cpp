#include <iostream>
#include <vector>
#include <unordered_map>
using namespace std;

// Idea 2: DFS + meori
unordered_map<int, int> mem;
vector<vector<int>> adj;
int diameter;

void dfs(int node, int parent){
	int max_dist = 0;
	for(int v:adj[node]){
		if(v==parent) continue;
		dfs(v, node);
		diameter = max(diameter, max_dist + mem[v] + 1); //diameter on this node
		max_dist = max(max_dist, mem[v]+1); // populate upward
	}

	mem[node] = max_dist;
}

int main(){
	int n, u, v;
	cin >> n;
	adj.resize(n+1);
	for(int loop=0; loop<n-1; loop++){
		cin>>u>>v;
		adj[u].push_back(v);
		adj[v].push_back(u);
	}

	mem.clear();
	diameter = 0;
	dfs(1, 1);
	cout<<diameter<<endl;
	return 0;
}