#include <vector>
#include <iostream>
using namespace std;


typedef long long ll;
class Solution {
    ll mincost;
    void helper(int index, ll cost, int k, int dist, vector<int>& vec){
        if(k==0){
            mincost = min(mincost, cost);
            return;
        }

        if(index>=vec.size()){
            return;
        }


        for(int i=index+1; i<=dist && i<vec.size(); i++){
            helper(i, cost+vec[i], k-1, dist, vec);
        }
    }

public:
    long long minimumCost(vector<int> vec, int k, int dist) {
        mincost = LONG_MAX;
        ll a1 = vec[0];
        for(int i=1; i<vec.size(); i++){
            helper(i, a1 + vec[i], k-2, i+dist, vec);
        }

        return mincost;
    }
};


int main(){
    Solution sol;
    cout<<sol.minimumCost({1,3,2,6,4,2}, 3, 3)<<endl;

    return 0;
}