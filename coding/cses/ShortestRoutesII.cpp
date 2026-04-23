#include <iostream>
#include <climits>
using namespace std;

int main(){
    int n,m,q;
    const long long INF = 1e12;
    cin>>n>>m>>q;
    n++;
    long long dist[n][n];
    for (int i=1; i<n; i++){
        for(int j=1; j<n; j++){
            dist[i][j] = (i==j ? 0:INF);
        }
    }

    for(int i=0; i<m; i++){
        int x,y; long long w;
        cin>> x >> y >> w;
        dist[y][x] = dist[x][y] = min(dist[x][y], w);
    }

    for(int k=1; k<n; k++){
        for (int i=1; i<n; i++){
                for(int j=1; j<n; j++){
                    dist[i][j] = min(dist[i][j], dist[i][k]+dist[k][j]);
                }
            }   
    }

    for(int i=0; i<q; i++){
        int x,y;
        cin>>x>>y;
        if (dist[x][y] == INF)
            cout<<-1;
        else cout<<dist[x][y];
        cout<<'\n';
    }


    return 0;
}