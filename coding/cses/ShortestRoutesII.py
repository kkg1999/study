import sys
input = sys.stdin.readline
 
BIG = int(1e18)
n, m, q = map(int, input().split())
dist = [[BIG for _ in range(n+1)] for _ in range(n+1)]
 
for i in range(1, n + 1):
    dist[i][i] = 0
 
for _ in range(m):
    a,b,c = map(int, input().split())
    if c<dist[a][b]:
        dist[a][b] = dist[b][a] = c
#this did not work for CSES- have to cache for loops
#Floyd-Warshall
for k in range(1, n+1):
    distk = dist[k]
    for i in range(1, n+1):
        disi = dist[i]
        disik =  disi[k]
        if disik==BIG: continue
        for j in range(1, n+1):
            new_dist = disik + distk[j]
            if new_dist<disi[j]:
                disi[j] = new_dist
 
out = []
for _ in range(q):
    u, v = map(int, input().split())
    d = dist[u][v]
    out.append(str(-1 if d == BIG else d))
print("\n".join(out))