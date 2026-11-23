import java.util.*;

/** Simple rule-based matching (no AI): category 40 + color 30 + location 20 + common word in name 10. */
public class Matcher {
    static class Match {
        Item item; int score;
        Match(Item i, int s) { item = i; score = s; }
    }

    static final int THRESHOLD = 60;   // category must match + at least one more strong clue

    static List<Match> find(Item src) {
        String want = src.type.equals(Item.LOST) ? Item.FOUND : Item.LOST;
        List<Match> out = new ArrayList<>();
        for (Item o : Store.items) {
            if (!o.type.equals(want) || !o.isOpen()) continue;
            if (!o.category.equals(src.category)) continue;
            int s = 40;
            if (o.color.equals(src.color)) s += 30;
            if (o.location.equals(src.location)) s += 20;
            if (shareWord(o.name, src.name)) s += 10;
            if (s >= THRESHOLD) out.add(new Match(o, s));
        }
        out.sort((a, b) -> b.score - a.score);
        return out;
    }

    static boolean shareWord(String a, String b) {
        Set<String> wa = new HashSet<>(Arrays.asList(a.toLowerCase().split("\\s+")));
        for (String w : b.toLowerCase().split("\\s+")) if (w.length() > 2 && wa.contains(w)) return true;
        return false;
    }
}