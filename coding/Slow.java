import java.util.*;

public class Slow {
    // two distinct hot paths so the flame graph has something to show
    static String buildKey(String a, String b) {
        return String.format("%s|%s", a, b);        // very slow: format + regex-ish parsing
    }

    static long parseSum(String line) {
        long sum = 0;
        for (String part : line.split(",")) {        // slow: regex compile per call + garbage
            sum += Integer.parseInt(part.trim());
        }
        return sum;
    }

    public static void main(String[] args) {
        Random r = new Random(42);
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < 200_000; i++) {
            lines.add(r.nextInt(1000) + " , " + r.nextInt(1000) + " , " + r.nextInt(1000));
        }

        Map<String, Long> agg = new HashMap<>();     // no presize, boxing on values
        long total = 0;
        for (int round = 0; round < 40; round++) {   // long enough to sample (~30s)
            for (String line : lines) {
                long s = parseSum(line);
                String key = buildKey("bucket" + (s % 50), "r" + round);
                agg.merge(key, s, Long::sum);
                total += s;
            }
        }
        System.out.println(total + " " + agg.size());
    }
}