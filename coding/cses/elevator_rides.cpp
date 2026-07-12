#include <iostream>
#include <vector>
using namespace std;

int main(){
	int n, maxw;
	cin >> n >> maxw;
	vector<int> weights(n);
	for(int i=0; i<n; i++)
		cin>>weights[i];

	vector<pair<int, int>> dp(1<<n);
	dp[0] = {1, 0}; // rides, weight

	for(int mask=1; mask<(1<<n); mask++){
		dp[mask] = {n+1, 0}; //init
		for(int i=0; i<n; i++){
			if((mask & (1<<i)) != 0){
				int pre = mask ^ (1<<i); //unset this bit -> we will add this person now
				auto [rides, w] = dp[pre];

				if(w + weights[i] <= maxw)
					w += weights[i];
				else{
					rides++;
					w = weights[i];
				}

				dp[mask] = min(dp[mask], {rides, w}); //pair default comparison -> first val, then second val
			}
		}
		
	}

	cout<< dp[(1<<n)-1].first<<endl;

	return 0;
}