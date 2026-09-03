#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    const int DIR_COUNT = 4;
    const int rowOffset[DIR_COUNT] = {-1, 1, 0, 0};
    const int colOffset[DIR_COUNT] = { 0, 0, -1, 1};
    const char dirLetter[DIR_COUNT] = {'U', 'D', 'L', 'R'};
    const int UNREACHED = INT_MAX;

    int rows, cols;
    cin >> rows >> cols;

    vector<string> grid(rows);
    for (string &line : grid) cin >> line;

    vector<vector<int>> monsterTime(rows, vector<int>(cols, UNREACHED));
    vector<vector<int>> playerTime (rows, vector<int>(cols, UNREACHED));
    vector<vector<int>> arrivedFrom(rows, vector<int>(cols, -1));

    queue<pair<int,int>> bfsQueue;
    int startRow = -1, startCol = -1;

    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
            if (grid[row][col] == 'M') {
                monsterTime[row][col] = 0;
                bfsQueue.push({row, col});
            } else if (grid[row][col] == 'A') {
                startRow = row;
                startCol = col;
            }
        }
    }

    auto insideGrid = [&](int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    };
    auto onBoundary = [&](int row, int col) {
        return row == 0 || col == 0 || row == rows - 1 || col == cols - 1;
    };

    // Phase 1: multi-source BFS from every monster.
    while (!bfsQueue.empty()) {
        auto [row, col] = bfsQueue.front();
        bfsQueue.pop();
        for (int dir = 0; dir < DIR_COUNT; dir++) {
            int nextRow = row + rowOffset[dir];
            int nextCol = col + colOffset[dir];
            if (!insideGrid(nextRow, nextCol)) continue;
            if (grid[nextRow][nextCol] == '#') continue;
            if (monsterTime[nextRow][nextCol] != UNREACHED) continue;
            monsterTime[nextRow][nextCol] = monsterTime[row][col] + 1;
            bfsQueue.push({nextRow, nextCol});
        }
    }

    // Phase 2: BFS from the player, only entering cells we reach first.
    playerTime[startRow][startCol] = 0;
    bfsQueue.push({startRow, startCol});

    int exitRow = -1, exitCol = -1;
    if (onBoundary(startRow, startCol)) {
        exitRow = startRow;
        exitCol = startCol;
    }

    while (!bfsQueue.empty() && exitRow == -1) {
        auto [row, col] = bfsQueue.front();
        bfsQueue.pop();
        for (int dir = 0; dir < DIR_COUNT; dir++) {
            int nextRow = row + rowOffset[dir];
            int nextCol = col + colOffset[dir];
            if (!insideGrid(nextRow, nextCol)) continue;
            if (grid[nextRow][nextCol] == '#') continue;
            if (playerTime[nextRow][nextCol] != UNREACHED) continue;

            int arrivalTime = playerTime[row][col] + 1;
            if (arrivalTime >= monsterTime[nextRow][nextCol]) continue; // monster is there first

            playerTime[nextRow][nextCol] = arrivalTime;
            arrivedFrom[nextRow][nextCol] = dir;

            if (onBoundary(nextRow, nextCol)) {
                exitRow = nextRow;
                exitCol = nextCol;
                break;
            }
            bfsQueue.push({nextRow, nextCol});
        }
    }

    if (exitRow == -1) {
        cout << "NO\n";
        return 0;
    }

    // Walk the parent pointers back to the start, then reverse.
    string escapePath;
    int row = exitRow, col = exitCol;
    while (arrivedFrom[row][col] != -1) {
        int dir = arrivedFrom[row][col];
        escapePath += dirLetter[dir];
        row -= rowOffset[dir];
        col -= colOffset[dir];
    }
    reverse(escapePath.begin(), escapePath.end());

    cout << "YES\n" << escapePath.size() << "\n" << escapePath << "\n";
    return 0;
}