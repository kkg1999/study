#include <iostream>
using namespace std;

bool check_match(const string& s1, const string& s2, int index){
	for(int i=0; i<s2.size(); i++){
		if(s1[index+i] != s2[i])
			return false;
	}
	return true;
}

int main(){
	string s1, s2;
	cin>> s1>> s2;
	if(s2.size()>s1.size()){
		cout<<0<<'\n';
		return 0;
	}

	int l2 = s2.size();

	const long long MOD = 1e10 + 7;
	long long power = 1;
	for(int i=1; i<l2; i++)
		power = (power*31)%MOD;
	long long h2 = 0;
	for(char& ch:s2){
		h2 = (h2*31 + (ch-'a'+1))%MOD;;
	}

	long long h1 = 0;
	for(int i=0; i<s2.size(); i++){
		h1 = (h1*31 + (s1[i]-'a'+1))%MOD;
	}

	int count = 0;
	if(h1 == h2){
		count++;
	}
	for(int i=l2; i<s1.size(); i++){
		h1 = (h1 - (s1[i-l2]-'a'+1)*power % MOD + MOD)%MOD;
		h1 = (h1*31 + s1[i]-'a'+1)%MOD;
		
		if(h1 == h2){
			count++;
		}
	}
	cout<<count<<'\n';

	return 0;
}