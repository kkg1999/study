# https://cses.fi/problemset/task/1074

def main():
    n = int(input())
    ar = [int(x) for x in input().split()]
    ar.sort()
    median = ar[n//2]
    moves = 0
    for x in ar:
        moves += abs(x-median)
    print(moves)


if __name__ == "__main__":
    main()