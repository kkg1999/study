#include <iostream>
#include <vector>
using namespace std;

typedef long long ll;
vector<ll> tree;

void add(int k, ll delta){
	while(k<tree.size()){
		tree[k] += delta;
		k += k&-k;
	}
}

ll sum(int k){
	ll s = 0;
	while(k>0){
		s += tree[k];
		k -= k&-k;
	}
	return s;
}


int main(){
	int n, q;
	cin >> n>> q;
	vector<ll> ar(n);
	tree.resize(n+1); // 1 based indexing for easier implementation of p(k) = k&-k // book: CPH
	for(int i=0; i<n; i++){
		cin >> ar[i];
		tree[i+1] = 0;
	}

	for(int i=0; i<n; i++){
		add(i+1, ar[i]);
	}

	while(q--){
		int t, a, b;
		cin >> t >> a >> b;
		if(t == 1){
			add(a, b-ar[a-1]);
			ar[a-1] = b;
		}
		else if(t == 2)
			cout << sum(b) - sum(a-1) << '\n';
	}

	return 0;
}