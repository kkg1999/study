# https://cses.fi/problemset/task/1669/
def dfs(node, parent, visited, adj):
    st = [(node, -1)]
    parent[node] = -1
    while st:
        u, p = st.pop()
        if visited[u]:
            continue
        visited[u] = True
        for v in adj[u]:
            if v == p: continue
            if not visited[v]:
                parent[v] = u
                st.append([v,u])
            else:
                # print("cycle found")
                # construct cycle
                cycle = [v]
                cur = u
                while cur!=v and cur!=-1:
                    cycle.append(cur)
                    cur = parent[cur]
                # print(cycle)
                if cur == v and len(cycle)>=3:
                    cycle.reverse()
                    cycle.append(cycle[0]) # closed cycle
                    return cycle
                    
    return []
    

def main():
    n, m = map(int, input().split())
    adj = [[] for _ in range(n+1)]
    
    for i in range(m):
        u, v = map(int, input().split())
        adj[u].append(v)
        adj[v].append(u)
    
    vis = [False] * (n+1)
    parent = [-1]*(n+1)
    found = False
    for u in range(1,n+1):       
        cycle = dfs(u, parent, vis, adj)
        if len(cycle)>0:
            # print(cycle)
            found = True
            break
    if not found:
        print("IMPOSSIBLE")
    else:
        print(len(cycle))
        for x in cycle:
            print(x, end=" ")
        print()
        

if __name__ == "__main__":
    main()