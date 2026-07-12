import java.util.*;

class LFUCache {
    Map<Integer, Node> map1; //key, node
    Map<Integer, DLL> map2; //freq, <all elements in their access order>
    int minFreq;
    int capacity;
    public LFUCache(int capacity) {
        this.capacity = capacity;
        minFreq = 0;
        map1 = new HashMap<>();
        map2 = new HashMap<>();
    }
    
    public int get(int key) {
        System.out.println("get called: " + key);
        if(!map1.containsKey(key))
            return -1;
        Node cur = map1.get(key);
        int f = cur.freq;
        map2.get(f).remove(cur);
        f++; cur.freq++;
        map2.putIfAbsent(f, new DLL());
        map2.get(f).addFirst(cur);
        if(map2.get(minFreq).size == 0){
            minFreq++;
        }
        return cur.val;
    }
    
    public void put(int key, int value) {
        System.out.println("put called: " + key);
        if(map1.containsKey(key)){
            get(key);
            Node cur = map1.get(key);
            cur.val = value;
            return;
        }
        // new key
        Node cur = new Node(key, value);
        cur.freq = 1;
        map1.put(key, cur);
        map2.putIfAbsent(1, new DLL());
        map2.get(1).addFirst(cur);

        if(map1.size()>capacity){
            Node lfu = map2.get(minFreq).tail.prev;
            map2.get(minFreq).remove(lfu);
            int key1 = lfu.key;
            map1.remove(key1);
        }
        minFreq = 1;
    }
}

class DLL{
    Node head, tail;
    int size;
    DLL(){
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    void remove(Node node){
        node.prev.next = node.next;
        node.next.prev = node.prev;

        node.prev = null;
        node.next = null;
        size--;
    }

    void addFirst(Node node){
        node.next = head.next;
        head.next.prev = node;
        
        head.next = node;
        node.prev = head;
        size++;
    }
}

class Node{
    int key, val, freq;
    Node prev, next;
    Node(){
        this(0, 0);
    }

    Node(int k, int v){
        key = k; val = v; freq = 1;
        prev = null; next = null;
    }
}


class lfu_cache{
    public static void main(String[] args) {
        // test1();
        test2();
    }

    static void test1(){
        LFUCache lfucache = new LFUCache(2);
        lfucache.put(1, 1);
        lfucache.put(2, 2);
        System.out.println( lfucache.get(1));
        lfucache.put(3, 3);
        System.out.println( lfucache.get(2));
        System.out.println( lfucache.get(3));
        lfucache.put(4, 4);
        System.out.println( lfucache.get(1));
        System.out.println( lfucache.get(3));
        System.out.println( lfucache.get(4));
    }

    static void test2(){
        LFUCache lfucache = new LFUCache(1);
        lfucache.put(1, 1);
        lfucache.put(2, 2);
        System.out.println( lfucache.get(1));
        lfucache.put(3, 3);
        System.out.println( lfucache.get(2));
        System.out.println( lfucache.get(3));
        lfucache.put(4, 4);
        System.out.println( lfucache.get(1));
        System.out.println( lfucache.get(3));
        System.out.println( lfucache.get(4));
    }
}
