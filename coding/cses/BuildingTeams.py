# cses link: https://cses.fi/problemset/task/1668
from collections import deque

def bfs(node, color, adj):
    color[node] = 1
    dq = deque()
    dq.append(node)
    while dq:
        u = dq.popleft()
        for v in adj[u]:
            if color[v] == color[u]:
                return False
            elif color[v] == 0:
                color[v] = -color[u]
                dq.append(v)
    return True

def main():
    n, m = map(int, input().split())
    adj = [[] for _ in range(n+1)]
    
    for i in range(m):
        x, y = map(int, input().split())
        adj[x].append(y)
        adj[y].append(x)
    
    color = [0 for _ in range(n+1)]
    for node in range(1,n+1):
        if color[node] != 0:
            continue
        valid = bfs(node, color, adj)
        if not valid:
            print("IMPOSSIBLE")
            return

    for i in range(1,n+1):
        if color[i] == -1: color[i] = 2
        print(color[i], end=" ")
        


if __name__ == "__main__":
    main()
    
        