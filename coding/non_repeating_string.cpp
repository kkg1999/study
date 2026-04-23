#include <iostream>
#include <vector>

using namespace std;

int solve() {
    int n, k;
    if (!(cin >> n >> k)) return 0;

    // If k is 1, no character can appear 1 time consecutively, 
    // which means no string can exist.
    if (k == 1) return 0;

    long long MOD = 1e9 + 7;
    vector<long long> dp(n + 1, 0);
    
    dp[0] = 1;
    long long running_sum = 0;

    for (int i = 1; i <= n; ++i) {
        // Add the newest dp value to our window
        running_sum = (running_sum + dp[i-1]) % MOD;

        // If the window exceeds size k-1, remove the oldest value
        if (i - k >= 0) {
            running_sum = (running_sum - dp[i-k] + MOD) % MOD;
        }

        // Standard case: last block is length 1 to k-1, 
        // and it must differ from the previous character.
        dp[i] = (running_sum * 25) % MOD;

        // Special case: the entire string is one single block of length i.
        // Since there is no "previous character", we have 26 options 
        // instead of the 25 already calculated.
        if (i < k) {
            dp[i] = (dp[i] + 1) % MOD; 
            // We add 1 because (running_sum * 25) already included 
            // 25 of the 26 'all-identical' strings.
        }
    }

    return dp[n];
}

int main() {
    cout << solve() << endl;
    return 0;
}
