import java.util.*;

class Solution {
    public String rearrangeString(String s, int k) {
        var freq = new HashMap<Character, Integer>();
        for(char ch:s.toCharArray())
            freq.put(ch, 1 + freq.getOrDefault(ch, 0));
        
        var pq = new PriorityQueue<Map.Entry<Character,Integer>>((x,y) -> {
            if (Integer.compare(x.getValue(), y.getValue()) != 0)
                return (int)y.getValue() - (int)x.getValue();
            return x.getKey() - y.getKey();
        });
        pq.addAll(freq.entrySet());
        StringBuilder sb = new StringBuilder();
        while(!pq.isEmpty()){
            var q = new ArrayList<Map.Entry<Character, Integer>>();
            int period = Math.min(k, s.length());
            while(period>0){
                if (pq.size() == 0) 
                    return "";
                var top = pq.poll();
                sb.append(top.getKey());
                top.setValue(top.getValue()-1);
                q.add(top);
                period--;
            }

            for(var loop:q){
                if(loop.getValue()>0)
                    pq.add(loop);
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}

class RearrangeString{
    public static void main(String[] args) {
        Solution sol = new Solution();
        sol.rearrangeString("a", 3);
    }
}