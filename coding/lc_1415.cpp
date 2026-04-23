#include <vector>
#include <iostream>
#include <deque>
using namespace std;

class Solution {
public:
    string getHappyString(int n, int k) {
        const string str = "abc";
        deque<string> vec = {"a", "b", "c"};
        while(--n){
            int sz = vec.size();
            while(sz--){
                string cur = vec[0];
                vec.pop_front();
                cout<< cur<<endl;
                for(char ch:str){
                    cout<<"ch:"<<ch<<endl;
                    if(cur.back()!=ch){
                        cur.push_back(ch);
                        cout<<cur<<endl;
                        vec.push_back(cur); //creates a copy
                        cur.pop_back();
                    }
                }
                cout<<endl;
            }
        }

        // for(string& s:vec){
        //     cout << s << endl;
        // }
        k--;
        if(k>=vec.size()) return "";
        return vec[k];
    }
};

int main(){
    Solution sol;
    cout<< sol.getHappyString(1, 3) <<endl;
    return 0;
}