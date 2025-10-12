import module java.base;

class ValidWordAbbr
{
	boolean isValid(String word, String abbr)
	{
		int n = word.length(), m = abbr.length();
		int i=0,j=0;
		
		while(j<m)
		{
			if(!Character.isDigit(abbr.charAt(j)))
			{
				if( i>=n || abbr.charAt(j) != word.charAt(i)) return false;
				i++; j++;
			}
			else
			{
				if( abbr.charAt(j) == '0' ) return false ; //leading 0
		
				int x = 0;
				while( j<m && Character.isDigit(abbr.charAt(j)) ){
					x = x*10 + (abbr.charAt(j) - '0');
					j++;
				}
				i+=x;
			}
		}
		return ( i == n && j == m);
	}

	void test()
	{
		System.out.println("test1: "+ isValid("algorithms", "algo5s") );
		System.out.println("test2: "+ isValid("mindset", "mind3t"));
		System.out.println("test3: "+ isValid("internationalization", "i18n"));
	}
}

class StrobogrammaticN
{
	boolean isValid(String num)
	{
		var map = new HashMap<Character, Character>();
		map.put('1', '1');
		map.put('0', '0');
		map.put('8', '8');
		map.put('6', '9');
		map.put('9', '6');

		char[] ar = num.toCharArray();
		int n = ar.length;
		for(int i=0, j=n-1; i<j; i++, j--){
			char temp = ar[i];
			ar[i] = ar[j];
			ar[j] = temp;
		}
		System.out.println(ar);
		for(int i=0; i<n; i++){
			ar[i] = map.getOrDefault(ar[i], '*');

		}
		return num.equals(String.valueOf(ar));
	}

	boolean isValid1pass(String num)
	{
		var map = new HashMap<Character, Character>();
		map.put('1', '1');
		map.put('0', '0');
		map.put('8', '8');
		map.put('6', '9');
		map.put('9', '6');

		int n = num.length();
		for(int i=0, j=n-1; i<j; i++, j--){
			if ( !map.containsKey(num.charAt(i)) ) return false;
			if ( map.get(num.charAt(i)) != num.charAt(j) ) return false;
		}

		return true; 
	}

	void test()
	{
		System.out.println("test1: " + isValid("101") );
		System.out.println("test2: " + isValid("1234") );
	}
}

class NextPalindrome
{
	boolean findNextPal(char[] str)
	{
		System.out.println("debug0: "+ String.valueOf(str));
		int n = str.length;
		int i = n-2;
		while(i>=0 && str[i]>=str[i+1]) i--;

		if(i<0) return false; //not possible

		int x = i; //small digit found

		i = n-1;
		while(i>x && str[i]<=str[x]) i--; 
		int y = i; //next big digit

		//swap (x) and (y)
		char tmp = str[x]; str[x] = str[y]; str[y] = tmp;
		System.out.println("debug1: "+ String.valueOf(str));
		//reverse x+1 ... n
		// since: this part is increasing -> make it decreasing to get the shortest value
		x++; i = n-1;
		while(x<i){
			tmp = str[x]; str[x] = str[i]; str[i] = tmp;
			x++; i--;
		}
		System.out.println("debug2: "+ String.valueOf(str));
		return true;
	}

	String findNextPalindrome(String str)
	{
		int n = str.length();

		Character midDig = null;
		if (n%2 != 0)
			midDig = str.charAt(n/2);
		
		char[] ar = str.substring(0, n/2).toCharArray();
		
		if( !findNextPal(ar) ) return "";

		StringBuilder ans = new StringBuilder(String.valueOf(ar));
		return ans + (midDig==null? "":String.valueOf(midDig)) + ans.reverse();
	}

	void test()
	{
		System.out.println("test1: "+ findNextPalindrome("354453"));
		System.out.println("test2: "+ findNextPalindrome("2354532"));
	}
}

class MergeInterval1
{
	int[][] mergeIntervals(int[][] ar) 
	{
		Arrays.sort( ar, new Comparator<int[]>(){
			public int compare(int[] a, int[] b){
				return a[0]-b[0];
			}
		} );

		List<int[]> list = new LinkedList<>();
		list.add(ar[0]);

		for(int i=1; i<ar.length; i++)
		{
			int s = ar[i][0], e = ar[i][1];
			int target = list.get(list.size()-1)[1];
			if( s <= target)
			{	//overlap
				list.get(list.size()-1)[1] = Math.max(e, target); 
			}
			else
			{
				//add current interval
				list.add(new int[]{s,e});
			}
		}
		return list.toArray(new int[0][2]);

	}

	void test()
	{
		int[][][] allIntervals = {
            { {3, 7}, {1, 5}, {4, 6} },
            { {1, 5}, {6, 8}, {4, 6}, {11, 15} },
            { {3, 7}, {10, 12}, {6, 8}, {11, 15} },
            { {1, 5} },
            { {1, 9}, {4, 4}, {3, 8} },
            { {1, 2}, {8, 8}, {3, 4} },
            { {1, 5}, {1, 3} },
            { {1, 5}, {6, 9} },
            { {0, 0}, {1, 18}, {1, 3} }
        };

        for (int i = 0; i < allIntervals.length; i++) {
            System.out.println((i + 1) + ".\tIntervals to merge: " + Arrays.deepToString(allIntervals[i]));
            int[][] ans = mergeIntervals(allIntervals[i]);
            System.out.println("After merge: " + Arrays.deepToString(ans));
            System.out.println(new String(new char[100]).replace('\0', '-'));
        }
	}
}

class InsertIntervalClass
{
	int[][] insertInterval(int[][] intervals, int[] target) {

    	int i = 0, n = intervals.length;
    	List<int[]> list = new ArrayList<>();

    	// Case 1: Before target
    	while( i<n && intervals[i][1] < target[0] )
    	{
    		list.add(intervals[i]);
    		i++;
    	}

    	int lastend = 0; 
    	if (list.size()>0) lastend = list.get( list.size()-1 )[1];

    	if (list.size() == 0 || lastend < target[0])
    	{
    		list.add(target); //adding as it is
    	}
    	else
    	{
    		list.get(list.size()-1)[1] = Math.max(lastend, target[1]); //merge target with the last interval
    	}

    	while(i<n)
    	{
    		int[] last = list.get(list.size()-1);
    		if (last[1] < intervals[i][0])
    			list.add(intervals[i]);
    		else
    			last[1] = Math.max(last[1], intervals[i][1]);
    		i++;
    	}
    	return list.toArray( new int[0][2] );
	}

	void test()
	{
		int[][] newIntervals = {
            {5, 7}, {8, 9}, {10, 12}, {1, 3}, {1, 10}
        };

        int[][][] existingIntervals = {
            { {1, 2}, {3, 5}, {6, 8} },
            { {1, 3}, {5, 7}, {10, 12} },
            { {8, 10}, {12, 15} },
            { {5, 7}, {8, 9} },
            { {3, 5} }
        };

        for (int i = 0; i < newIntervals.length; i++) {
            System.out.println((i + 1) + ".\tExisting intervals: " + Arrays.deepToString(existingIntervals[i]));
            System.out.println("\tNew interval: " + Arrays.toString(newIntervals[i]));
            int[][] output = insertInterval(existingIntervals[i], newIntervals[i]);
            System.out.println("\tUpdated intervals: " + Arrays.deepToString(output));
            System.out.println(String.join("", Collections.nCopies(100, "-")));
        }
	}
}

class IPOClass
{
	int maximumCapital(int c, int k, int[] capitals,int[] profits)
	{
		PriorityQueue<int[]> minpq = new PriorityQueue<>((x,y) -> x[0]-y[0] ); //will store: capital, index
		PriorityQueue<Integer> maxpq = new PriorityQueue<>((x,y) -> y-x); // will store: profit of affordable projects

		for(int i=0; i<capitals.length; i++)
		{
			minpq.offer( new int[]{capitals[i],i} );
		}

		int i=0;
		while(i<k)
		{
			while(minpq.size()>0 && minpq.peek()[0]<=c )
			{
				//current project is affordable
				int cur = minpq.poll()[1];
				maxpq.offer(profits[cur]); //add current project's profit to maxpq
			}

			if(maxpq.size()==0) break; // we cannot pick any more project
			c += maxpq.poll();
			i++;
		}

		System.out.println("maximized total profit: " + c);
		return c;
	}

	void test()
	{
		int[] c = { 1, 2, 1, 7, 2 };
		int[] k = { 2, 3, 3, 2, 4 };
		int[][] capitals = {
			{ 1, 2, 2, 3 },
			{ 1, 3, 4, 5, 6 },
			{ 1, 2, 3, 4 },
			{ 2, 7, 8, 10 },
			{ 2, 3, 5, 6, 8, 12 }
		};
		int[][] profits = {
			{ 2, 4, 6, 8 },
			{ 1, 2, 3, 4, 5 },
			{ 1, 3, 5, 7 },
			{ 4, 8, 12, 14 },
			{ 1, 2, 5, 6, 8, 9 }
		};

		for (int i = 0; i < k.length; i++) {
			System.out.println((i + 1) + ".\tGiven profits: " + Arrays.toString(profits[i]));
			System.out.println("  \tSelecting profits...");
			maximumCapital(c[i], k[i], capitals[i], profits[i]);
			System.out.println(String.join("", Collections.nCopies(100, "-")));
		}
	}
}


class AlienDictionaryClass
{
	String alienOrder(List<String> words)
	{
		Map<Character, Set<Character>> adjList = new HashMap<>();
		Map<Character, Integer> indeg = new HashMap<>();

		getAdjList( words, adjList, indeg );

		StringBuilder result = new StringBuilder();
		Queue<Character> q = new LinkedList<>();

		System.out.println(indeg.toString());
		for( char c : indeg.keySet() )
		{
			if ( indeg.get(c) == 0 ) q.offer(c);
		}
		System.out.println(q.toString());

		while(q.size()>0)
		{
			char cur = q.poll();
			result.append(cur);

			if(adjList.get(cur) == null) continue;
			for(char next : adjList.get(cur))
			{
				indeg.put(next, indeg.get(next)-1);
				if( indeg.get(next) == 0 ) q.offer(next);
			}
		}

		
		if (result.length() != indeg.size()) return "";
		return result.toString();
	}

	void getAdjList(List<String> words, Map<Character, Set<Character>> adjList, Map<Character, Integer> indeg)
	{
		
		for(int i=0; i<words.size()-1; i++)
		{
			String word1 = words.get(i);
			String word2 = words.get(i+1);

			for(char ch:word1.toCharArray()) indeg.putIfAbsent(ch, 0);
			for(char ch:word2.toCharArray()) indeg.putIfAbsent(ch, 0);

			int j = 0;
			for ( ; j<word1.length() && j<word2.length() ; j++ )
			{
				char c = word1.charAt(j);
				char d = word2.charAt(j);

				if(c==d) continue;

				adjList.putIfAbsent(c, new HashSet<>());
				adjList.get(c).add(d);

				indeg.put(d, indeg.getOrDefault(d,0)+1);
				break;
			}
		}
		for(char c : adjList.keySet())
		{
			System.out.println( c + " : "+ adjList.get(c).toString());
		}
		System.out.println(indeg.toString());
	}

	void test()
	{
		List<String> words = Arrays.asList("alpha", "bravo", "charlie", "delta");
		List<String> words1 = Arrays.asList("wrt", "wrf", "er", "ett", "rftt");
		System.out.println( alienOrder(words) );
	}
}

/*
class TreeDiameter
{

	public int treeDiameter(int[][] edges) 
	{
		Map<Integer, Set<Integer>> adjList = new HashMap<>();
		for(int[] edge in edges)
		{
			int a = edge[0], b = edge[1];
			adjList.putIfAbsent(a, new HashSet<>());
			adjList.putIfAbsent(b, new HashSet<>());

			adjList.get(a).add(b);
			adjList.get(b).add(a);
		}    	


		return 0;
   	}

   	int helper(int cur, Integer maxLen, Map<Integer, Set<Integer>> adjList, Set<Integer> visited)
   	{

   		return 0;

   	}

	void test()
	{

	}
}
*/



class NoOfIslandsClass
{
	int NoOfIslands(List<List<Character>> grid)
	{
		Character[][] mat = new Character[grid.size()][];
		for(int i=0; i<grid.size(); i++)
		{
			mat[i] = grid.get(i).toArray(new Character[0]);
		}

		int count = 0;
		for(int i=0; i<mat.length; i++)
		{
			for(int j=0; j<mat[i].length; j++)
			{
				if((char)mat[i][j] == '1') 
				{
					count++;
					helper(i, j, mat);
				}
			}
		}
		System.out.println("count: "+count);
		return count;
	}

	void helper(int r, int c, Character[][] mat)
	{
		if( r<0 || c<0 ) return;
		if( r>=mat.length || c>=mat[0].length ) return;
		if((char)mat[r][c] == '0') return;

		mat[r][c] = '0';

		helper(r-1, c, mat);
		helper(r+1, c, mat);
		helper(r, c+1, mat);
		helper(r, c-1, mat);
	}

	void test()
	{
		List<List<Character>> grid1 = Arrays.asList(
                Arrays.asList('1', '1', '1'),
                Arrays.asList('0', '1', '0'),
                Arrays.asList('1', '0', '0'),
                Arrays.asList('1', '0', '1')
        );
		NoOfIslands(grid1);
	}
}

class Trie {
	private class Node{
		int value = 0;
		Node[] next = new Node[26];
	}  

	Node root; 
    public Trie() {
    }

    // inserting string in trie
    public void insert(String word) {
        root = insert(root, word.toCharArray(), 0);
    }

    private Node insert(Node node, char[] str, int index){
    	if (node == null) node = new Node();
    	if (index == str.length) { 
    		node.value++;
    		return node;
    	}
    	int c = str[index]-'a'; //child
    	node.next[c] =  insert(node.next[c], str, index+1);
    	return node;
    }

    // searching for a string
    public boolean search(String word) {
        Node x = search(root, word.toCharArray(), 0);
        if (x==null) return false;
        return x.value>0;
    }

    private Node search(Node x, char[] str, int index){
    	if (x==null) return x;
    	if (index == str.length) return x; 
    	int child = str[index]-'a';
    	return search(x.next[child], str, index+1);
    }

    // searching for a prefix
    public boolean searchPrefix(String prefix) {
        Node x = search(root, prefix.toCharArray(), 0);
        if (x==null) return false;
        return true;
    }
}


public class Educative 
{
	public static void main(String[] args) {
		Object[] pair = new Object[2];
		pair[0] = 1;
		pair[1] = "one";
		IO.println(Arrays.toString(pair));

		var obj = new Trie();
		obj.insert("kushal");
		IO.println(obj.searchPrefix("kas"));
	}
}