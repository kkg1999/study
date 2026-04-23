#include <iostream>
#include <vector>
using namespace std;

vector<string> ans;

void helper(int index, string& s, int sum, int N){
	if(index == s.size()){
		if(sum == N)
			ans.push_back(s);
		return;
	}

	if(s[index]!='#'){
		helper(index+1, s, sum+(s[index]-'0'), N);
	}
	else{
		for(int x=0; x<=9; x++){
			s[index] = (char)(x+'0');
			helper(index+1, s, sum+x, N);
			s[index] = '#'; //backtrack
		}
	}
}

void find_all_sum(string s, int N){
	ans.clear();
	helper(0, s, 0, N);
	for(string& s:ans){
		cout<<s<<endl;
	}
}

int main(){

	find_all_sum("345##21", 17);
	return 0;
}

