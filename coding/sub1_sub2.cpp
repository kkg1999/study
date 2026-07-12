//max length of subsequence of string x that is substring of string y
#include <iostream>
using namespace std;


int find_maxlen(string x, string y){
	int maxmatch = 0;
	for(int i=0; i<y.size(); i++){
		int xp = 0, yp = i, match = 0;
		while(xp<x.size() && yp<y.size()){
			if(x[xp] == y[yp]){
				match++;
				yp++;
			}
			xp++;
		}

		maxmatch = max(maxmatch, match);
	}
	return maxmatch;
}

int main(){
	cout<<find_maxlen("abcdez", "zzbcda")<<endl;
	return 0;
}