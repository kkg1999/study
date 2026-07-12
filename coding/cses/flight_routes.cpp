#include <vector>
#include <queue>
#include <iostream>
#include <deque>
using namespace std;

/*
Dijkstra - pick min path from each node
K-Dijkstra - 
	- we maintain k smallest distances for each node
	- we do this by maintaining PQ for each node
	- K cheapest distances found from 1 to each city
*/


int main(){
	int n, m, k;
	cin >> n >> m >> k;
	vector<vector<pair<int,int>>> adj(n+1);
	for(int i=0; i<m; i++){
		int u, v, w;
		cin >> u >> v >> w;
		adj[u].push_back({v, w});
	}

	priority_queue<pair<long long, int>, vector<pair<long long, int>>, greater<>> pq; //min heap
	vector<priority_queue<long long>> dist(n+1); //we maintain max heap of min-k values for each node

	pq.push({0, 1});
	dist[1].push(0);

	while(pq.size()>0){
		auto [d, u] = pq.top();
		pq.pop();

		if(dist[u].size() == k && d > dist[u].top())
			continue; //stale entry
		for(auto& [v, uv]:adj[u]){
			if(dist[v].size()<k || d+uv < dist[v].top()){
				dist[v].push(d+uv);
				pq.push({d+uv, v});
			}
			if(dist[v].size()>k)
				dist[v].pop();
		}
	}

	deque<long long> ans;
	while(dist[n].size()>0){
		ans.push_front(dist[n].top());
		dist[n].pop();
	}

	for(long long x:ans)
		cout << x <<" ";
	cout<<endl;

	return 0;
}


