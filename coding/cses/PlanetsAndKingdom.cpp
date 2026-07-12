#include <iostream>
#include <vector>
#include <stack>
using namespace std;

// strongly connected component
// kosajaru

vector<vector<int>> adj, adj2; 
vector<bool> vis;
vector<int> component;
stack<int> st;

void dfs1(int u){
	vis[u] = true;
	for(int& v:adj[u])
		if(!vis[v])
			dfs1(v);
	st.push(u);
}

void dfs2(int u, const int& cid){
	component[u] = cid;
	for(int v:adj2[u])
		if(component[v] == -1)
			dfs2(v, cid);
}


int main(){
	int n, m;
	cin >> n >> m;
	adj.resize(n);
	adj2.resize(n);
	for(int i=0; i<m; i++){
		int u, v;
		cin>>u>>v;
		u--; v--;
		adj[u].push_back(v);
		adj2[v].push_back(u); //reverse graph
	}

	vis.assign(n, false);
	for(int i=0; i<n; i++)
		if(!vis[i])
			dfs1(i);

	component.assign(n, -1);
	int cid = 0;
	
	while(st.size()){
		int u = st.top(); st.pop();
		if(component[u] == -1)
			dfs2(u, ++cid);
	}

	cout<<cid<<'\n';
	for(int& x:component){
		cout<<x<<" ";
	}
	cout<<'\n';
	
	return 0;
}