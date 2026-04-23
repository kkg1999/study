#include <iostream>
#include <vector>
#include <stack>
using namespace std;
typedef long long ll;

vector<vector<int>> adj, rev_adj, dag_adj;
vector<int> vis;
stack<int> st;
vector<int> scc_id;
vector<ll> coins, scc_coins, mem;

void dfs1(int u){
	vis[u] = true;
	for(int v:adj[u]){
		if(!vis[v])
			dfs1(v);
	}
	st.push(u);
}

void dfs2(int u, int id){
	vis[u] = true;
	scc_id[u] = id;
	scc_coins[id] += coins[u];
	for(int v:rev_adj[u]){
		if(!vis[v])
			dfs2(v, id);
	}
}


ll dp(int u){
	// longest path in DAG
	if(mem[u]!=-1){
		return mem[u];
	}

	ll max_coins = 0;
	for(int v:dag_adj[u]){
		max_coins = max(max_coins, dp(v));
	}

	mem[u] = max_coins + scc_coins[u];
	return mem[u];
}

int main(){
	int n, m, u, v;
	cin>>n>>m;

	coins.resize(n+1);
	adj.resize(n+1);
	rev_adj.resize(n+1);
	dag_adj.resize(n+1);

	for(int i=1; i<=n; i++){
		cin >> coins[i];	
	}
	
	for(int i=0; i<m; i++){
		cin>>u>>v;
		adj[u].push_back(v);
		rev_adj[v].push_back(u);
	}
	
	vis.resize(n+1, false);
	for(int i=1; i<=n; i++){
		if(!vis[i])
			dfs1(i);
	}
	
	fill(vis.begin(), vis.end(), false);
	scc_coins.resize(n+1);
	scc_id.resize(n+1);
	int cur_scc_id = 0;
	while(st.size()>0){
		int u = st.top(); st.pop();
		if(!vis[u]){
			cur_scc_id++;
			dfs2(u, cur_scc_id);
		}
	}
	
	// make adj graph for scc
	for(int u=1; u<=n; u++){
		for(int v:adj[u]){
			if(scc_id[u] != scc_id[v])
				dag_adj[scc_id[u]].push_back(scc_id[v]);
		}
	}

	
	mem.resize(n+1, -1);
	// we start dp from all nodes
	ll max_val = 0;
	for(int u=1; u<=n; u++){
		max_val = max(max_val, dp(u));
	}
	cout<<max_val<<endl;

	return 0;
}