#include <iostream>
#include <vector>
using namespace std;

typedef long long ll;
vector<ll> tree;

void add(int k, int delta){
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
	vector<int> ar(n);
	tree.resize(n+1); // 1 based indexing for easier implementation of p(k) = k&-k // book: CPH
	for(int i=0; i<n; i++){
		cin >> ar[i];
		tree[i+1] = 0;
	}

	for(int i=0; i<n; i++){
		add(i+1, ar[i]);
	}

	while(q--){
		int a, b;
		cin >> a >> b;
		cout << sum(b) - sum(a-1) << '\n';
	}

	return 0;
}