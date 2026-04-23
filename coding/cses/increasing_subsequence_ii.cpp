#include <iostream>
#include <algorithm>
#include <vector>
using namespace std;

typedef long long ll;
vector<ll> tree;
int n;
ll MOD = 1e9 + 7;

ll sum(int a, int b){
	a += n; b += n;
	ll s = 0;
	while(a<=b){
		if(a%2 == 1){
			s = (s+tree[a])%MOD;
			a++;
		}
		if(b%2 == 0){
			s = (s+tree[b])%MOD;
			b--;
		}
		a/=2;
		b/=2;
	}
	return s;
}

void update(int k, int x){
	k += n;
	tree[k] += x;
	k /= 2;
	while(k>0){
		tree[k] = (tree[2*k] + tree[2*k+1])%MOD;
		k /= 2;
	}
}


int main(){
	int n1;
	cin >> n1;
	n = n1;
	tree.assign(2*n, 0);

	vector<int> ar(n), ar1(n);
	for(int i=0; i<n; i++){
		cin >> ar[i];
		ar1[i] = ar[i];
	}

	// seg tree + coordinate compression
	sort(ar1.begin(), ar1.end());
	ar1.erase(unique(ar1.begin(), ar1.end()), ar1.end()); 
	// unique will move all the first elements to beginning and all the dups towards end and return itr

	for(int i=0; i<n; i++){
		int rank = lower_bound(ar1.begin(), ar1.end(), ar[i]) - ar1.begin();
		ll sum_of_smaller = 0;
		if(rank > 0)
			sum_of_smaller = sum(0, rank-1);
		ll dp_i = (1 + sum_of_smaller)%MOD ; // all increasing subs ending with ar[i]
		update(rank, dp_i);
	}

	cout<< sum(0, n-1) <<endl;

	return 0;
}