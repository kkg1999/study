from collections import deque
# https://cses.fi/problemset/task/1679
def main():
    n, m = map(int, input().split())
    adj = [[] for _ in range(n+1)]
    indegree = [0]*(n+1)
    
    for _ in range(m):
        u,v = map(int, input().split())
        adj[u].append(v)
        indegree[v] += 1
    
    q = deque()
    for x in range(1,n+1):
        if indegree[x]==0:
            q.append(x)
    ans = []
    while q:
        cur = q.popleft()
        ans.append(cur)
        for nei in adj[cur]:
            indegree[nei] -= 1
            if indegree[nei] == 0:
                q.append(nei)
    if len(ans) != n:
        print("IMPOSSIBLE")
    else:
        for x in ans:
            print(x, end=" ")



if __name__ == "__main__":
    main()
    print()