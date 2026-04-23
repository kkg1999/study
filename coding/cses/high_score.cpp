#include <iostream>
#include <vector>
#include <array>
using namespace std;

const long long INF = 1e17;
const long long NINF = - 1e17;

int main(){

	int n, m;
	cin >> n >> m;
	vector<array<int, 3>> edges(m);
	for(int i=0; i<m; i++){
		cin >> edges[i][0] >> edges[i][1] >> edges[i][2];
	}

	vector<long long> dist(n+1, NINF);
	dist[1] = 0;
	for(int i=0; i<n-1; i++){ //n-1 times
		for(auto& e:edges){
			int u = e[0], v = e[1], w = e[2];
			if(dist[u] != NINF && dist[u]+w > dist[v]) //inverse of bellman ford
				dist[v] = dist[u]+w;
		}
	}


	for(int i=0; i<n; i++){
		for(auto& e:edges){
			int u = e[0], v = e[1], w = e[2];
			if(dist[u] == INF)
				dist[v] = INF;
			else if(dist[u]!=NINF && dist[u]+w > dist[v]) //can still be improved -> inf loop
				dist[v] = INF;
		}
	}

	if(dist[n] == INF)
		cout<<-1;
	else
		cout << dist[n];
	cout<<endl;
	return 0;
}
