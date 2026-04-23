#include <iostream>
#include <vector>
using namespace std;


int main(){
	int n;
	cin >> n;
	vector<int> ar(n);
	for(int i=0; i<n; i++)
		cin>>ar[i];
	int maxlen = 1;
	int incr = 1, decr = 1;
	for(int i=0; i<n-1; i++){
		if(ar[i]<ar[i+1]){
			incr++;
			decr = 1;
		}
		else if(ar[i]>ar[i+1]){
			decr++;
			incr = 1;
		}
		else{
			incr = 1;
			decr = 1;
		}

		maxlen = max(maxlen, max(incr, decr));
	}
	cout<<maxlen<<endl;
	return 0;
}