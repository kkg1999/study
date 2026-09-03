#include <vector>
#include <cstdint>

// Same tree, but with range assignment.
// A fully covered node stores a pending tag instead of rewriting its subtree.
class BitRangeTree {
public:
    using Bit = uint8_t;

    explicit BitRangeTree(int capacity) : cap_(capacity) {
        n_ = 1;
        while (n_ < capacity) n_ <<= 1;   // round up to a power of two
        t_.assign(2 * n_, 0);
        lz_.assign(2 * n_, -1);           // -1 = no pending assignment
    }

    void set_bit(int offset, int length)   { assign(offset, length, 1); }
    void clear_bit(int offset, int length) { assign(offset, length, 0); }

    bool get_bit(int i) { return query(1, 0, n_ - 1, i); }

private:
    int cap_, n_;
    std::vector<Bit>    t_;
    std::vector<int8_t> lz_;   // must be signed: it holds -1

    void assign(int offset, int length, Bit v) {
        if (length <= 0) return;
        update(1, 0, n_ - 1, offset, offset + length - 1, v);
    }

    void applyTag(int node, Bit v) {
        t_[node] = v;
        if (node < n_) lz_[node] = v;     // leaves have no children to tag
    }

    void push(int node) {
        if (lz_[node] == -1) return;
        Bit v = lz_[node];
        applyTag(2 * node, v);
        applyTag(2 * node + 1, v);
        lz_[node] = -1;
    }

    void update(int node, int nl, int nr, int l, int r, Bit v) {
        if (r < nl || nr < l) return;                           // disjoint
        if (l <= nl && nr <= r) { applyTag(node, v); return; }  // fully covered

        push(node);
        int mid = (nl + nr) / 2;
        update(2 * node,     nl,      mid, l, r, v);
        update(2 * node + 1, mid + 1, nr,  l, r, v);
        t_[node] = t_[2 * node] & t_[2 * node + 1];             // fixup
    }

    bool query(int node, int nl, int nr, int i) {
        if (nl == nr) return t_[node];
        push(node);
        int mid = (nl + nr) / 2;
        return i <= mid ? query(2 * node,     nl,      mid, i)
                        : query(2 * node + 1, mid + 1, nr,  i);
    }
};
