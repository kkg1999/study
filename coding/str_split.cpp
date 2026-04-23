#include <iostream>
#include <deque>
#include <string>
using namespace std;


deque<string> s_split(const string str, const string delim){
    int start, pos;
    deque<string> tokens;
    start = str.find_first_not_of(delim);
    pos = str.find_first_of(delim, start);
    while(pos!=-1 || start != -1){
        tokens.push_back(move(str.substr(start, pos-start)));
        start = str.find_first_not_of(delim, pos);
        pos = str.find_first_of(delim, start);
    }

    for(string& s:tokens){
        cout<<s<<" ";
    }
    cout<<endl;
    return tokens;
}

int main(){
    s_split(", ab,c,def g,", ", ");
    return 0;
}