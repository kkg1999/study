# include <iostream>
# include <vector>
# include <array>
# include <algorithm>
using namespace std;

class unionfind{
	vector<int> parent;
	vector<int> size;
public:
	unionfind(int n){
		parent.resize(n);
		size.resize(n);
		for(int i=0; i<n; i++){
			parent[i] = i;
			size[i] = 1;
		}
	}

	int find(int x){
		if(parent[x] == x)
			return x;
		int px = find(parent[x]);
		parent[x] = px;
		return px;
	}

	bool unify(const int& x, const int& y){
		int px = find(x);
		int py = find(y);
		if(px == py)
			return false; 
		if(size[px] > size[py])
			swap(px, py);
		parent[px] = py;
		size[py] += size[px];
		return true;
	}
};


int main(){
	int n, m;
	cin >> n >> m;
	vector<array<int, 3>> edges;
	edges.reserve(m+1);
	for(int i=0; i<m; i++){
		int u, v, w;
		cin >> u >> v >> w;
		edges.push_back({u, v, w});
	}

	sort(edges.begin(), edges.end(), [](const auto& x, const auto& y){
		return x[2] < y[2];
	});

	unionfind uf(n+1);
	long long cost = 0; int connect = 0;
	for(auto& edge:edges){
		if(uf.unify(edge[0], edge[1])){
			cost += edge[2];
			connect++;
		}
	}

	if(connect != n-1)
		cout<<"IMPOSSIBLE\n";
	else
		cout<<cost<<'\n';

	return 0;
}