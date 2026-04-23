# https://cses.fi/problemset/task/1660
# we can use two pointer approach

from sys import stdin

input = stdin.readline

n, k = map(int, input().split())
ar = list(map(int, input().split()))
l = 0
count = 0
cursum = 0

for r in range(n):
    cursum += ar[r]
    while cursum > k and l < r:
        cursum -= ar[l]
        l += 1
    if cursum == k:
        count += 1
        cursum -= ar[l]
        l += 1
print(count)