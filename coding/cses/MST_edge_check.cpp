#include <iostream>
#include <algorithm>
#include <vector>
using namespace std;

#define FASTIO ios_base::sync_with_stdio(false); cin.tie(nullptr);

vector<int> parent;
vector<int> psize; // component size

int find(int x){
	if(parent[x] == x)
		return x;
	int px = find(parent[x]);
	parent[x] = px;
	return px;
}

bool unify(int x, int y){
	int px = find(x);
	int py = find(y);
	if(px == py) 
		return false;
	
	if(psize[py]<psize[px])
		swap(px, py);
	parent[px] = py;
	psize[py] += psize[px];
	return true;
}

void init_dsu(int n){
	parent.resize(n);
	psize.resize(n);
	for(int i=0; i<n; i++){
		parent[i] = i;
		psize[i] = 1;
	}
}

int main()
{
	FASTIO;
	int V, E, u, v, w;
	cin >> V >> E;

	vector<vector<int>> edges;
	for(int i=0; i<E; i++){
		cin>>u>>v>>w;
		u--; v--;
		edges.push_back({u, v, w, i});
	}

	sort(edges.begin(), edges.end(), [](auto& x, auto& y){
		if(x[2] != y[2])
			return x[2]<y[2];
		return x[0]<y[0];
	});
	
	init_dsu(V);
	vector<bool> ans(E, false);
	int l = 0;
	while(l<E){
		int r=l;
		while(r<E && edges[r][2] == edges[l][2])
			r++;

		// step 1: check for all edges with same weight
		for(int i=l; i<r; i++){
			if(find(edges[i][0]) != find(edges[i][1]))
				ans[edges[i][3]] = true; // we can use this edge
		}

		// step 2: we unite all edges with same weight
		for(int i=l; i<r; i++){
			unify(edges[i][0], edges[i][1]);
		}

		l = r;
	}

	for(bool b:ans){
		if(b)
			cout<<"YES\n";
		else
			cout<<"NO\n";
	}

	return 0;
}