# https://cses.fi/problemset/task/1631

def main():
    n = int(input())
    ar = list(map(int, input().split()))
    maxx = 0
    summ = 0
    for x in ar:
        if x>maxx:
            maxx = x
        summ += x
    if maxx > (summ-maxx):
        print(2*maxx)
    else:
        print(summ)



if __name__ == "__main__":
    main()
