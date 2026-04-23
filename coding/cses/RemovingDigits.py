
def helper(x):
	if x in mem:
		return mem[x]
	y = x
	while y>0:
		digit = y%10
		y /= 10
		helper()


def main():
	n = int(input())
	mem = {0:0}
	helper(n)

if __name__ == "__main__":
	main()