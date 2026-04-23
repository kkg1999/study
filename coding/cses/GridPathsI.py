# https://cses.fi/problemset/task/1638

from math import inf


def main():
	n = int(input())
	mat = [[0]*n for _ in range(n)]
	for i in range(n):
		s = input()
		for j in range(len(s)):
			if s[j] == '*':
				mat[i][j] = inf
			
	if mat[0][0] or mat[n-1][n-1] == inf:
		print(0)
		return

	modulo = int(1e9) + 7
	mat[0][0] = 1

	for i in range(n):
		for j in range(n):
			if i == 0 and j == 0: continue
			if mat[i][j] == inf: continue
			x = 0; y = 0
			if i>0 and mat[i-1][j] != inf:
				x = mat[i-1][j]
			if j>0 and mat[i][j-1] != inf:
				y = mat[i][j-1]
			mat[i][j] = ( x + y )%modulo
	
	print(mat[n-1][n-1])

if __name__ == "__main__":
	main()