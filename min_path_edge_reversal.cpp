#include <vector>
#include <iostream>
#include <queue>
#include <unordered_map>
using namespace std;

class Solution {
public:
    int minCost(int n, vector<vector<int>>& edges) {
        vector<int> dist(n, (int)1e9);

        vector<unordered_map<int, int>> adj(n); 
        for(auto& e:edges){
            int u = e[0], v = e[1], w = e[2];
            if(adj[u].count(v))
                adj[u][v] = min(adj[u][v], w);
            else
                adj[u][v] = w;
            
            if(adj[v].count(u))
                adj[v][u] = min(adj[v][u], 2*w);
            else
                adj[v][u] = 2*w;
        }

        int ccc = 0;
        for(auto& itr:adj){
            cout<<ccc++<<" : ";
            for(auto& [key,val]:itr){
                cout<<key<<","<<val<<" ";
            }
            cout<<endl;
        }
        
        auto cmp = [](const auto& p1, const auto& p2){
            return p1.second > p2.second;
        };
        priority_queue<pair<int, int>, vector<pair<int, int>>, decltype(cmp)> pq(cmp);
        dist[0] = 0;
        pq.push({0,0});

        while(pq.size()){
            auto [u, d] = pq.top(); pq.pop();
            if(d > dist[u]) continue; //stale

            for(auto& [v, uv]:adj[u]){
                if(d + uv < dist[v]){
                    dist[v] = d+uv;
                    pq.push({v, d+uv});
                }
            }
        }

        if(dist[n-1] == (int)1e9)
            return -1;
        return dist[n-1];
    }
};

int main(){
    vector<vector<int>> inp1 = {{0,1,3},{3,1,1},{2,3,4},{0,2,2}};
    Solution sol;
    cout<<sol.minCost(4, inp1)<<'\n';

    vector<vector<int>> inp2 = {{0,2,1},{2,1,1},{1,3,1},{2,3,3}};
    cout<<sol.minCost(4, inp2)<<'\n';

    vector<vector<int>> inp3 = {{0,1,13},{1,0,1}};
    cout<<sol.minCost(2, inp3)<<'\n';
    return 0;
}