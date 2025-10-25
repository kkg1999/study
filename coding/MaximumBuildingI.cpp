#include <iostream>
#include <vector>
using namespace std;

int histogram(vector<int>&);

int main(){
	int row, col;
	cin >> row >> col;
	vector<string> mat(row);
	for(int i=0; i<row; i++)
		cin >> mat[i];

	vector<vector<int>> dp (row, vector<int>(col));
	for(int i=0; i<row; i++){
		for(int j=0; j<col; j++){
			if (mat[i][j] == '.'){
				dp[i][j] = 1;
				if (i>0) dp[i][j] += dp[i-1][j];
			}
		}
	}

	int maxarea = 0;
	for(auto loop:dp){
		int cur = histogram(loop);
		maxarea = max(maxarea, cur);
	}
	cout<<maxarea<<endl;
	return 0;
}

int histogram(vector<int>& ar){
	int i, p, n = ar.size();
	vector<int> left(n), right(n);

	left[0] = -1;
	for(i=1; i<n; i++){
		p = i-1;
		while(p>=0 && ar[p]>=ar[i]){
			p = left[p];
		}
		left[i] = p;
	}

	right[n-1] = n;
	for(i=n-2; i>=0; i--){
		p = i+1;
		while(p<n && ar[p]>=ar[i]){
			p = right[p];
		}
		right[i] = p;
	}

	int maxarea = 0;
	for(i=0; i<n; i++){
		int h = ar[i];
		int w = right[i] - left[i] - 1;
		maxarea = max(maxarea, h*w);
	}
	return maxarea;
}

