n = int(input())

ar = [0]
for i in range(n):
	x = (1<<i) # 2^i
	sz = len(ar)
	for j in range(sz-1,-1,-1):
		ar.append(ar[j] | x)

for x in ar:
	print(f"{x:0{n}b}")

