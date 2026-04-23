# https://cses.fi/problemset/task/1671

import heapq
from math import inf
import sys
input = sys.stdin.readline

def main():
    n,m = map(int, input().split())
    adj = [[] for _ in range(n+1)]
    dist = [inf for _ in range(n+1)]

    for _ in range(m):
        x, y, w = map(int, input().split())
        adj[x].append((y,w))
    
    pq = [(0,1)]
    dist[1] = 0
    while pq:
        du, u = heapq.heappop(pq)
        if du > dist[u]:
            # stale entry
            continue
        for (v,uv) in adj[u]:
            nd = du + uv
            if nd < dist[v]:
                heapq.heappush(pq, (nd, v))
                dist[v] = nd
    
    for i in range(1, n+1):
        print(dist[i], end=" ")
    print()

if __name__ == "__main__":
    main()