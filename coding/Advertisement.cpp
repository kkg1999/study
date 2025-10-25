#include <iostream>
#include <vector>
using namespace  std;


int main(){
	int n; 
	cin >> n;
	vector<int> ar(n),left(n), right(n);
	for(int i=0; i<n; i++)
		cin >> ar[i];

	left[0] = -1;
	for(int i=1; i<n; i++)
	{
		int p = i-1;
		while(p>=0 && ar[p]>=ar[i])
			p = left[p];
		left[i] = p;
	}

	right[n-1] = n;
	for (int i = n-1; i >= 0; i--){
		int p = i+1;
		while(p<n && ar[p]>=ar[i]){
			p = right[p];
		}
		right[i] = p;
	}

	long maxarea = 0;
	for(int i=0; i<n; i++){
		int h = ar[i];
		long w = right[i] - left[i] - 1;
		maxarea = max(maxarea, h*w);
	}
	cout<< maxarea << endl;
	return 0;
}