from sys import stdin
input = stdin.readline

class DSU:
	def __init__(self, n, size_list):
		self.parents = list(range(n))
		self.component_sizes = size_list.copy()
		self.max_size = max(size_list)

	def find(self, i):
		if self.parents[i] == i:
			return i
		self.parents[i] = self.find(self.parents[i])
		return self.parents[i]

	def union(self, i, j):
		pi = self.find(i)
		pj = self.find(j)
		
		if pi == pj:
			return

		self.parents[pj] = pi
		self.component_sizes[pi] += self.component_sizes[pj]
		self.max_size = max(self.max_size, self.component_sizes[pi])


def main():
	x, n = map(int, input().split())
	lights = list(map(int, input().split()))
	
	positions = [0, x] + lights
	positions = sorted(set(positions))
	component_sizes = []
	point_to_index = {}
	for i in range(len(positions)):
		point_to_index[positions[i]] = i
		if i>0:
			component_sizes.append(positions[i] - positions[i-1])
	dsu = DSU(len(positions), component_sizes)

	answers = [0]*n
	if n>0:
		answers[n-1] = dsu.max_size

	for i in range(n-1, 0, -1):
		light_pos = lights[i]
		index = point_to_index[light_pos]
		dsu.union(index-1, index)
		answers[i-1] = dsu.max_size

	print(" ".join(map(str, answers)))


if __name__ == "__main__":
	main()