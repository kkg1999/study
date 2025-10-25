#include <bits/stdc++.h>
using namespace std;

struct Customer {
    int a, b, idx;
};

int main() {    
    int n;
    cin >> n;
    vector<Customer> v(n);
    for (int i = 0; i < n; ++i) {
        cin >> v[i].a >> v[i].b;
        v[i].idx = i;
    }
    
    sort(v.begin(), v.end(), [](const Customer& x, const Customer& y) {
        if (x.a != y.a) return x.a < y.a;
        return x.b < y.b;
    });
    
    // Min-heap by departure day: (departure, roomNumber)
    using P = pair<int,int>;
    priority_queue<P, vector<P>, greater<P>> active;
    
    vector<int> freeRooms;          // stack of reusable room numbers
    vector<int> assignment(n, 0);   // answer per original index
    int roomCount = 0;
    
    for (const auto& cust : v) {
        // Free all rooms whose departure < current arrival (strict rule)
        while (!active.empty() && active.top().first < cust.a) {
            freeRooms.push_back(active.top().second);
            active.pop();
        }
        int room;
        if (!freeRooms.empty()) {
            room = freeRooms.back();
            freeRooms.pop_back();
        } else {
            room = ++roomCount;
        }
        assignment[cust.idx] = room;
        active.emplace(cust.b, room);
    }
    
    cout << roomCount << "\n";
    for (int i = 0; i < n; ++i) {
        if (i) cout << ' ';
        cout << assignment[i];
    }
    cout << "\n";
    return 0;
}