# https://cses.fi/problemset/task/1195
# Idea: 
# dijkstra from 1 
# dijkstra from n
# then: find min for all nodes: 1->u + (discount)uv + v->n


import heapq
from math import inf
from sys import stdin
input = stdin.readline

def dijkstra(src:int, adj:List[List[(int, int)]]):
    n = len(adj)
    dist = [inf for _ in range(n)]

    pq = [(0, src)]
    dist[src] = 0
    while pq:
        du, u = heapq.heappop(pq)
        if du>dist[u]:
            #stale data
            continue
        for (v, uv) in adj[u]:
            nd = du + uv
            if nd < dist[v]:
                dist[v] = nd
                heapq.heappush(pq, (nd, v))
    return dist

def main():
    n, m = map(int, input().split())
    adj = [[] for _ in range(n+1)]
    rev = [[] for _ in range(n+1)]
    for _ in range(m):
        x, y, w = map(int, input().split())
        adj[x].append((y, w))
        rev[y].append((x, w))
    
    d1 = dijkstra(1, adj)
    d2 = dijkstra(n, rev)
    
    minn = inf
    for u in range(1, n):
        if d1[u] == inf: continue
        for (v, w) in adj[u]:
            if d2[v] == inf: continue
            
            # 1->u + (discount)uv + v->n
            minn = min(minn, d1[u] + w//2 + d2[v])
    print(minn)

if __name__ == "__main__":
    main()