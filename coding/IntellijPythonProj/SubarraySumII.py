# https://cses.fi/problemset/task/1661
# CSES testcase getting TLE with integer key in dict
# passing with string key

from sys import stdin
input = stdin.readline
ints = lambda : list(map(int,input().split()))

n, k = ints()
ar = ints()
hashmap = {"0":1}
g = hashmap.get
cursum = 0
count = 0
for x in ar:
    cursum += x
    count += g(str(cursum-k), 0)
    st = str(cursum)
    hashmap[st] = 1 + g(st, 0)
print(count)
