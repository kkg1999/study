#include <vector>
#include <cstdint>

// Perfect binary tree stored in an array.
// Invariant: parent = left & right  =>  node is 1 iff its whole subtree is 1.
// Root at index 1, children of i are 2i and 2i+1, leaf k at index n + k.
class BitTree {
public:
    using Bit = uint8_t;

    explicit BitTree(int numLeaves) {   // numLeaves must be a power of two
        n_ = numLeaves;
        t_.assign(2 * n_, 0);           // all zeros already satisfies the invariant
    }

    void set(int point)   { assign(point, 1); }
    void clear(int point) { assign(point, 0); }

    bool get(int point) const { return t_[n_ + point]; }

private:
    int n_;
    std::vector<Bit> t_;

    void assign(int point, Bit v) {
        int i = n_ + point;
        if (t_[i] == v) return;         // leaf unchanged => nothing changes

        t_[i] = v;
        while (i > 1) {                 // fix ancestors, child before parent
            i >>= 1;
            Bit nv = t_[2 * i] & t_[2 * i + 1];
            if (t_[i] == nv) break;     // node unchanged => nothing above changes
            t_[i] = nv;
        }
    }
};
