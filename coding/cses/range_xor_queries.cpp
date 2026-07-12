#include <iostream>
#include <vector>
using namespace std;

typedef long long ll;

vector<ll> tree;
int n;
ll getxor(int a, int b){
	a += n; b += n;
	ll s = 0;
	while(a<=b){
		if(a%2 == 1){
			s ^= tree[a];
			a++;
		}
		if(b%2 == 0){
			s ^= tree[b];
			b--;
		}

		a /= 2; b /= 2;
	}
	return s;
}

void update(int k, int x){
	k += n;
	tree[k] = x;
	k /= 2;
	while(k>0){
		tree[k] = tree[2*k] ^ tree[2*k+1];
		k /= 2;
	}
}


int main(){
	int n1, q;
	cin >> n1 >> q;
	n = n1;
	tree.assign(2*n, 0);
	for(int i=0; i<n; i++){
		ll val;
		cin >> val;
		update(i, val);
	}

	while(q--){
		int a, b;
		cin >> a >> b;
		cout << getxor(a-1, b-1)<<endl;
	}


	return 0;
}