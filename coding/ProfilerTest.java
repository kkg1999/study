import java.util.*;

public class ProfilerTest {
    public static void main(String[] args) {
        List<Integer> data = new ArrayList<>();
        Random r = new Random(42);
        for (int i = 0; i < 200_000; i++) data.add(r.nextInt(1_000_000));

        long start = System.nanoTime();
        List<Integer> unique = new ArrayList<>();
        for (Integer x : data) {
            if (!unique.contains(x)) {   // O(n) inside O(n) => O(n^2). This is the hotspot.
                unique.add(x);
            }
        }
        System.out.println("unique=" + unique.size()
            + " took " + (System.nanoTime() - start) / 1_000_000 + "ms");
    }
}
