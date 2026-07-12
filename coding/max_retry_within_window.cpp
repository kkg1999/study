#include <vector>
#include <algorithm>
#include <iostream>
using namespace std;

int maxWindow(vector<int> vec, int window){
	sort(vec.begin(), vec.end());
	int count = 0;
	int l = 0, r = 0;
	while(r<vec.size()){
		while(vec[r]-vec[l] >= window)
			l++;
		count = max(count, r-l+1);
		r++;
	}

	return count;
}

int main(){
	cout<<maxWindow({2,2,3}, 1)<<endl;
	cout<<maxWindow({1,3,7,5}, 4)<<endl;

	return 0;
}