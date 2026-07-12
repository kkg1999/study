#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

typedef long long ll;

int main(){
	int n;
	cin>>n;
	vector<vector<ll>> jobs;
	jobs.reserve(n);
	for(int i=0; i<n; i++){
		int x,y,z;
		cin >> x>>y>>z;
		jobs.push_back({x, y, z});
	}

	sort(jobs.begin(), jobs.end(), [](const auto& x, const auto& y){
		return x[1]<y[1];
	});

	vector<ll> dp(n, 0);
	for(int i=0; i<n; i++){
		ll take = 0, skip = 0;
		if(i>0) skip = dp[i-1];

		int key = jobs[i][0];
		int l=0, r=i-1, j=-1;
		while(l<=r){
			int mid = l + (r-l)/2;
			if(jobs[mid][1]<key){
				j = mid;
				l = mid+1;
			}
			else
				r = mid-1;
		}

		take = jobs[i][2];
		if(j>-1)
			take += dp[j];
		dp[i] = max(skip, take);
	}

	cout<<dp[n-1]<<'\n';
	return 0;
}