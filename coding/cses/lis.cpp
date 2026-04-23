#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;


int main(){
	int n;
	cin>>n;
	vector<int> vec(n);
	for(int i=0; i<n; i++){
		cin >> vec[i];
	}

	vector<int> lis;
	for(int i=0; i<n; i++){
		auto itr = lower_bound(lis.begin(), lis.end(), vec[i]);
		if(itr == lis.end())
			lis.push_back(vec[i]);
		else
			*itr = vec[i];
	}

	cout<<lis.size()<<'\n';
	return 0;
}