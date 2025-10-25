# https://cses.fi/problemset/task/1094/
def main():
    n = int(input())
    ar = [int(x) for x in input().split()]
    move = 0
    for i in range(1,n):
        if ar[i]<ar[i-1]:
            move += ar[i-1]-ar[i]
            ar[i] = ar[i-1]
    print(move)

if __name__ == "__main__":
    main()