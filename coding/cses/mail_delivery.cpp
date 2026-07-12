#include <vector>
#include <stack>
#include <iostream>
using namespace std;


int main(){
	ios_base::sync_with_stdio(false);
	cin.tie(nullptr);

	int n, m;
	cin >> n >> m;
	vector<vector<pair<int,int>>> adj;
	adj.resize(n+1);
	vector<int> degree(n+1, 0);
	for(int i=0; i<m; i++){
		int u, v;
		cin >> u >> v;
		adj[u].push_back({v, i});
		adj[v].push_back({u, i});
		degree[u]++; degree[v]++;
	}

	for(int x:degree){
		if(x%2 != 0){
			cout<<"IMPOSSIBLE\n";
			return 0;
		}
	}

	vector<bool> used(m, false);
	stack<int> st;
	vector<int> circuit;


	st.push(1); //start node
	while(st.size()>0){
		int u = st.top();
		bool found_new = false;
		for(auto& next:adj[u]){

			int v = next.first, id = next.second;
			if(!used[id]){
				found_new = true;
				used[id] = true;
				st.push(v);
				break;
			}
		}

		if(!found_new){
			// complete all visits from here
			circuit.push_back(u);
			st.pop();
		}
	}

	if(circuit.size() != m+1){
		cout<<"IMPOSSIBLE\n";
		return 0;
	}

	//citcuit is in reverse order. fine for undirected
	for(int x:circuit){
		cout<<x<<" ";
	}
	cout<<endl;

	return 0;
}



