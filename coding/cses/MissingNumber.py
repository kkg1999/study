# https://cses.fi/problemset/task/1083/


def main():
    n = int(input())
    target = (n*(n+1))//2
    listt = [int(x) for x in input().split()]
    summ = 0
    for x in listt:
        summ += x
    print(target - summ)

if __name__ == "__main__":
    main()