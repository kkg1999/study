#include <vector>
#include <iostream>
using namespace std;

// 1 indexed BIT
vector<vector<int>> tree;
int sum(int r, int c){
	int s = 0;
	for(int i=r; i>0; i -= i&-i){
		for(int j=c; j>0; j -= j&-j)
			s += tree[i][j];
	}
	return s;
}

void update(int r, int c, int val){
	for(int i=r; i<tree.size(); i += i&-i){
		for(int j=c; j<tree.size(); j += j&-j)
			tree[i][j] += val;
	}
}


int main(){
	int n, q;
	cin >> n >> q;
	tree.assign(n+1, vector<int>(n+1, 0));

	string s;
	for(int i=0; i<n; i++){
		cin >> s;
		for(int j=0; j<s.size(); j++){
			if(s[j] == '*')
				update(i+1, j+1, 1);
		}
	}

	int r1, c1, r2, c2;
	for(int i=0; i<q; i++){
		cin >> r1 >> c1 >> r2 >> c2;

		int val = sum(r2, c2) - sum(r2, c1-1) - sum(r1-1, c2) + sum(r1-1, c1-1);
		cout << val << endl;
	}


	return 0;
}