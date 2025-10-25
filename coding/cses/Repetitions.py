# https://cses.fi/problemset/task/1069
def main():
    s = input()
    cur = '#'
    count = 0
    maxcount = 0
    for c in s:
        if c != cur:
            cur = c
            count = 1
        else:
            count += 1
        maxcount = max(maxcount, count)
    print(maxcount)

if __name__ == "__main__":
    main()