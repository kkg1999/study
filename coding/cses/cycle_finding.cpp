#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;


// do bellman-ford - modified version - use 0 not INF - since we are looking for
// -ve cycles in general
// normal bellman ford will check for -ve cycle from a specific point
// if -ve cycle is there - in nth iteration also the dist will go down
typedef long long ll;
int main(){
	const ll INF = 1e12;
	int n, m;
	cin >> n >> m;
	vector<tuple<int, int, ll>> edges;
	for(int i=0; i<m; i++){
		int u, v; ll w;
		cin >> u >> v >> w;
		u--; v--;
		edges.push_back({u, v, w});
	}

	int x;
	vector<ll> dist(n, 0);
	vector<int> parent(n, -1);
		
	for(int i=0; i<n; i++){
		x = -1;
		for(auto& [u, v, w]:edges){
			if(dist[u]+w < dist[v]){
				dist[v] = max(-INF, dist[u]+w);
				parent[v] = u;
				x = v;
			}
		}
	}

	if(x == -1){
		cout<<"NO\n";
		return 0;
	}

	for(int i=0; i<n; i++)
		x = parent[x];
	
	vector<int> cycle;
	int v = x;
	while(true){
		cycle.push_back(v);
		if(v == x && cycle.size()>1)
			break;
		v = parent[v];
	}
	reverse(cycle.begin(), cycle.end());
	cout<<"YES\n";
	for(int x:cycle)
		cout<<x+1<<" ";
	cout<<endl;
	return 0;
}
