class Set {
    std::vector<int> elements;   // dense: elements[0..count-1] are the live members
    std::vector<int> index_of;   // sparse: index_of[val] = position of val in elements
    int count;                   // number of live members
    int capacity;                // values allowed are 0 .. capacity-1

public:
    explicit Set(int n) : elements(n), index_of(n), count(0), capacity(n) {}

    bool lookup(int val) const {
        if (val < 0 || val >= capacity) return false;
        int i = index_of[val];
        return i < count && elements[i] == val;
    }

    void put(int val) {
        if (val < 0 || val >= capacity) return;   // or throw
        if (lookup(val)) return;                  // already present
        elements[count] = val;
        index_of[val] = count;
        count++;
    }

    void remove(int val) {
        if (!lookup(val)) return;

        int slot = index_of[val];      // where val sits
        int last = elements[count - 1];// last live element

        elements[slot] = last;         // move last into the hole
        index_of[last] = slot;         // update its bookkeeping
        count--;                       // shrink
    }

    void clear() { count = 0; }        // O(1)

    int size() const { return count; }

    template <typename Fn>
    void iterate(Fn fn) const {        // O(count), not O(n)
        for (int i = 0; i < count; i++) fn(elements[i]);
    }
};
