n = int(input())
for _ in range(n):
	s = input()
	if len(s) <= 10:
		print(s)
	else:
		x = len(s)-2
		print(s[0] + str(x) + s[-1])