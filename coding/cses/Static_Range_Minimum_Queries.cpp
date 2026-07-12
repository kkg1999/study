#include <vector>
#include <iostream>
#include <algorithm>
#include <climits>
using namespace std;
typedef long long ll;

vector<ll> tree; // 2n space
int n;
ll findmin(int a, int b){
	a += n; b += n; //move to leaves side
	ll s = LLONG_MAX;
	while(a<=b){
		if(a%2 == 1){ //right child
			s = min(s, tree[a]);
			a++;
		}

		if(b%2 == 0){ //left child
			s = min(s, tree[b]);
			b--;
		}
		a /= 2;
		b /= 2; 
	}

	return s;
}

void update(int k, ll x){
	k += n; //start from leaf level
	tree[k] = x;
	k/=2;

	while(k>0){
		tree[k] = min(tree[2*k], tree[2*k+1]);
		k /= 2;
	}
}

int main(){
	int n1, q;
	cin >> n1 >> q;
	n = n1;

	tree.assign(2*n, 0); // n elements -> leaf nodes. n-1 parent nodes 

	for(int i=0; i<n; i++){
		cin >> tree[i+n]; //fill the leaf nodes [n, 2*n-1]
	}

	for(int i=n-1; i>0; i--)
		tree[i] = min(tree[2*i], tree[2*i+1]);

	while(q--){
		int t, a, b;
		cin >> t >> a >> b;
		if(t==1)
			update(a-1, b);
		else if(t==2)
			cout << findmin(a-1, b-1) <<endl;
	}

	return 0;
}