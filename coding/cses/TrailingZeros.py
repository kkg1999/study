# https://cses.fi/problemset/task/1618
n = int(input())
count = 0
while n>0:
    count += (n//5)
    n = (n//5)
print(count)