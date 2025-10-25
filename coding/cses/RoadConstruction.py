# https://cses.fi/problemset/task/1676/

import sys
input = sys.stdin.readline

n, m = map(int, input().split())
parent = [ i for i in range(n+1)]
size = [1]*(n+1)
component = n
maxsize = 1

def find(x):
    global parent, size
    if x == parent[x]:
        return x
    px = find(parent[x])
    parent[x] = px # path compression
    return px

def union(x, y):
    global parent, size, component, maxsize
    px = find(x)
    py = find(y)
    if px == py:
        return False
    parent[px] = py
    size[py] += size[px]
    component -= 1 # count of component decreases
    maxsize = max(maxsize, size[py])
    return True


for _ in range(m):
    u, v = map(int, input().split())
    union(u, v)
    print(component, maxsize)