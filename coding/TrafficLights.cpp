#include <iostream>
#include <set>
using namespace std;

int main() {
	// need these two lines to avoid TLE
	ios::sync_with_stdio(false);
	cin.tie(NULL);



    int x, n;
    cin >> x >> n;

    set<int> lights;
    multiset<int> lengths;
    lights.insert(0);
    lights.insert(x);
    lengths.insert(x);

    for (int i = 1; i <= n; i++) {
        int p;
        cin >> p;
        auto it2 = lights.upper_bound(p);
        auto it1 = it2;
        it1--;
        lights.insert(p);
        lengths.erase(lengths.find(*it2 - *it1));
        lengths.insert(p - *it1);
        lengths.insert(*it2 - p);
        cout << *lengths.rbegin() << " ";
    }
    cout << "\n";
}