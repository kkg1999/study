#include <iostream>
#include <vector>
#include <queue>
#include <unordered_set>
using namespace std;

// Idea 1: 2 pass BFS

pair<int, int> bfs(int src, const vector<vector<int>>& adj){
	queue<pair<int, int>> q;
	unordered_set<int> vis;
	pair<int, int> p;

	q.push({src, 0});
	vis.insert(src);
	while(q.size()>0){
		p = q.front(); q.pop();
		int u = p.first, d = p.second;
		for(int v:adj[u]){
			if(vis.count(v) == 0){
				q.push({v, d+1});
				vis.insert(v);
			}
		}
	}

	return p;
}

int main(){
	int n, u, v;
	cin >> n;
	vector<vector<int>> adj(n+1);
	for(int loop=0; loop<n-1; loop++){
		cin>>u>>v;
		adj[u].push_back(v);
		adj[v].push_back(u);
	}

	auto [n1, _] = bfs(1, adj);
	auto [n2, d2] = bfs(n1, adj);
	cout<<d2<<'\n';
	return 0;
}