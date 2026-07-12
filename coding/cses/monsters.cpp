#include <iostream>
using namespace std;

vector<string> mat;
int d1[1001][1001];
int d2[1001][1001];
int moves[4][2] = {{1,0}, {-1,0}, {0,-1}, {0,1}};
int main(){
	int row, col;
	cin>>row>>col;
	mat.resize(row);
	memset(d1, (int)1e9, sizeof(d1));

	queue<tuple<int, int, int>> q1, q2;
	for(int i=0; i<row; i++){
		cin>>mat[i];
		for(int j=0; j<mat[i].size(); j++){
			if(mat[i][j] == 'M')
				q1.push({i,j,0});
			else if(mat[i][j]=='A')
				q2.push({i,j,0});
		}
	}

	
	while(q1.size()>0){
		auto [x, y, level] = q.front(); q.pop();
		if(level >= d1[x][y])
			continue; //stale data
		d1[x][y] = level;

		for(auto& [dx, dy]:moves){
			int x1 = x+dx;
			int y1 = y+dy;
			if(x1<0 || y1<0 || x1>=row || y1>=col || mat[x1][y1]!='.')
				continue;
			q1.push({x1, y1, level+1});
		}
	}




	return 0;
}