#include <iostream>
#include <vector>
#include <queue>
using namespace std;

void dfs(int);
vector<bool> visited;
vector<int> pathlen;
vector<int> destination;
queue<int> path;
int steps;

int main(){
	int n;
	cin >> n;
	destination.resize(n);
	for(int i=0; i<n; i++){
		cin >> destination[i];
		destination[i]--;
	}

	visited.assign(n, false);
	pathlen.assign(n, 0);

	for(int i=0; i<n; i++){
		if(!visited[i]){
			steps = 0;
			dfs(i);

			int decrement = 1; // for nodes outside cycle -> pathlen goes down by 1 for each next node
			while(path.size()>0){
				if(path.front() == path.back()) decrement = 0; // we are inside cycle now
				pathlen[path.front()] = steps;
				steps -= decrement;
				path.pop();
			}
		}
	}

	for(int i=0; i<n; i++)
		cout<<pathlen[i]<<" ";
	cout<<endl;
	return 0;
}

void dfs(int u){
	path.push(u);
	if(visited[u]){
		steps += pathlen[u];
		return;
	}

	visited[u] = true;
	steps++;
	dfs(destination[u]);
}