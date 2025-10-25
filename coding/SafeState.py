from collections import deque
class Solution:
    def eventualSafeNodes(self, graph: List[List[int]]) -> List[int]:
        n = len(graph)
        adj = [[] for _ in range(n)]
        indeg = [0]*n
        for i in range(n):
            for node in graph[i]:
                adj[node].append(i)
                indeg[i] += 1
        print(indeg)
        q = deque()
        
        for i in range(n):
            if indeg[i]==0:
                q.append(i)
        
        safelist = []
        while q:
            cur = q.popleft()
            safelist.append(cur)
            for nei in adj[cur]:
                indeg[nei] -= 1
                if indeg[nei]==0:
                    q.append(nei)
        
                    
        print(safelist)
        return safelist


if __name__ == '__main__':
    sol = Solution()
    param = [[1,2],[2,3],[5],[0],[5],[],[]]
    sol.eventualSafeNodes(param)