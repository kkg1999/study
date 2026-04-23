#include <iostream>
#include <vector>
using namespace std;

int main(){
	int m, n;
	cin >> m >> n;

	vector<vector<int>> dp(m+1, vector<int>(n+1, 0));
	for(int i=1; i<=m; i++){
		for(int j=1; j<=n; j++){
			if(i == j){
				dp[i][j] = 0;
				continue;
			}

			dp[i][j] = 1e9;
			for(int k=1; k<i; k++)
				dp[i][j] = min(dp[i][j], 1+dp[i-k][j]+dp[k][j]);
			for(int k=1; k<j; k++)
				dp[i][j] = min(dp[i][j], 1+dp[i][j-k]+dp[i][k]);

		}
	}

	cout << dp[m][n]<<endl;

	return 0;
}