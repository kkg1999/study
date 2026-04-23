#include <iostream>
#include <set>
#include <vector>
#include <algorithm>
#include <unordered_map>
using namespace std;

unordered_map<int, vector<int>> adj;
int maxdist;

void dfs(int u, int parent, int d, set<int>& edges){
	if(d>maxdist){
		edges.clear();
		maxdist = d;
	}

	if(d == maxdist){
		edges.insert(u);
	}

	for(int& v:adj[u]){
		if(v == parent) continue;
		dfs(v, u, d+1, edges);
	}
}

vector<int> find_edges(int n, vector<int> v1, vector<int> v2){
	adj.clear();
	for(int i=0; i<v1.size(); i++){
		adj[v1[i]].push_back(v2[i]);
		adj[v2[i]].push_back(v1[i]);
	}
	
	set<int> edges;
	// step1: dfs from a random node
	maxdist = 0;
	dfs(1, -1, 0, edges);

	//step 2: dfs from any node in edges
	int x = *edges.begin();
	set<int> new_edges;
	dfs(x, -1, 0, new_edges);
	
	vector<int> vec;
	set_union(edges.begin(), edges.end(), new_edges.begin(), new_edges.end(), back_inserter(vec));
	cout<<"here\n";
	vector<int> ans(n, 0);
	for(int& tmp:vec){
		ans[tmp-1] = 1;
	}
	
	for(int& i:ans){
		cout<<i<<" ";
	}	
	cout<<endl;
	return ans;
}


int main(){
	find_edges(7, {1,2,3,3,1,1}, {2,3,4,5,6,7});
	find_edges(2, {2}, {1});
	return 0;
}