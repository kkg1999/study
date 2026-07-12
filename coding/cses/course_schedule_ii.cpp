#include <iostream>
#include <vector>
#include <queue>
#include <algorithm>
using namespace std;


int main(){
	int n, m;
	cin>>n>>m;
	vector<vector<int>> adj(n+1);
	vector<int> indegree(n+1, 0);
	while(m--){
		int u,v;
		cin>>u>>v;
		// we will use reverse graph - CSES needs weird ordering
		adj[v].push_back(u);
		indegree[u]++;
	}

	priority_queue<int> q;
	for(int i=1; i<=n; i++)
		if(indegree[i] == 0)
			q.push(i);

	vector<int> ans;
	ans.reserve(n);
	while(q.size()>0){
		int u = q.top(); q.pop();
		ans.push_back(u);
		for(int& v:adj[u]){
			indegree[v]--;
			if(indegree[v] == 0)
				q.push(v);
		}
	}
	reverse(ans.begin(), ans.end());
	for(const int& i:ans)
		cout<<i<<" ";
	cout<<'\n';
	return 0;
}