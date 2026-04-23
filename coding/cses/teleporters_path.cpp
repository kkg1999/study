#include <iostream>
#include <vector>
#include <stack>
using namespace std;



int main()
{
	int n, m;
	cin >> n >> m;
	vector<vector<pair<int, int>>> adj;
	adj.resize(n+1);

	vector<int> indegree(n+1, 0);
	vector<int> outdegree(n+1, 0);

	for(int i=0; i<m; i++){
		int u, v;
		cin >> u >> v;
		indegree[v]++;
		outdegree[u]++;

		adj[u].push_back({v, i});
	}

	if(outdegree[1] - indegree[1] != 1 || indegree[n] - outdegree[n] != 1){
		cout << "IMPOSSIBLE\n";
		return 0;
	} 

	for(int i=2; i<n; i++){
		if(indegree[i] != outdegree[i]){
			cout << "IMPOSSIBLE\n";
			return 0;
		}
	}

	stack<int> st;
	vector<int> path;
	vector<bool> taken(m, false);

	st.push(1);
	while(st.size()>0){
		int u = st.top();

		bool found_new = false;
		while(adj[u].size()>0){
			auto next = adj[u].back();
			adj[u].pop_back(); //optimization to avoid looping over taken edges

			int v = next.first, id = next.second;
			if(!taken[id]){
				taken[id] = true;
				found_new = true;
				st.push(v);
				break;
			}
		}

		if(!found_new){
			path.push_back(u);
			st.pop();
		}
	}

	if(path.size() != m+1){
		cout << "IMPOSSIBLE\n";
		return 0;
	}

	for(int i=path.size()-1; i>=0; i--)
		cout<<path[i]<<" ";
	cout << endl;

	return 0;
}



