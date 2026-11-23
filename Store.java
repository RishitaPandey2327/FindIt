import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** All data is kept in ArrayLists and saved permanently in text files. */
public class Store {
    static final Path DIR = Paths.get("data");
    static final String[] CATEGORIES = {"Electronics", "Books", "ID Card", "Clothing", "Accessories", "Bags", "Keys", "Wallet", "Other"};
    static final String[] COLORS = {"Black", "White", "Blue", "Red", "Green", "Yellow", "Brown", "Grey", "Other"};
    static final String[] LOCATIONS = {"Library", "Computer Lab", "Cafeteria", "Auditorium", "Main Gate", "Hostel", "Sports Ground", "Classroom Block"};
    static final int REWARD_POINTS = 10;

    static final List<Item> items = new ArrayList<>();
    static final List<String[]> notifs = new ArrayList<>();          // {time, message}, newest first
    static final Map<String, int[]> users = new LinkedHashMap<>();   // name -> {points, returns}

    static Path f(String n) { return DIR.resolve(n); }

    static List<String> read(String name) {
        try { return Files.exists(f(name)) ? Files.readAllLines(f(name)) : new ArrayList<>(); }
        catch (IOException e) { return new ArrayList<>(); }
    }

    static void load() {
        try { Files.createDirectories(DIR); } catch (IOException ignored) {}
        items.clear(); notifs.clear(); users.clear();
        for (String l : read("items.txt")) { Item it = Item.fromLine(l); if (it != null) items.add(it); }
        for (String l : read("notifs.txt")) { String[] p = l.split("\\|", 2); if (p.length == 2) notifs.add(p); }
        for (String l : read("users.txt")) {
            String[] p = l.split("\\|");
            if (p.length == 3) users.put(p[0], new int[]{Integer.parseInt(p[1]), Integer.parseInt(p[2])});
        }
    }

    static void save() {
        try {
            Files.createDirectories(DIR);
            List<String> a = new ArrayList<>(), b = new ArrayList<>(), c = new ArrayList<>();
            for (Item i : items) a.add(i.toLine());
            for (String[] n : notifs) b.add(n[0] + "|" + n[1]);
            for (Map.Entry<String, int[]> e : users.entrySet()) c.add(e.getKey() + "|" + e.getValue()[0] + "|" + e.getValue()[1]);
            Files.write(f("items.txt"), a); Files.write(f("notifs.txt"), b); Files.write(f("users.txt"), c);
        } catch (IOException e) { e.printStackTrace(); }
    }

    static void notify(String msg) {
        String t = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a"));
        notifs.add(0, new String[]{t, Item.clean(msg)});
        save();
    }

    static String nextId(String type) {
        String p = type.equals(Item.LOST) ? "L-" : "F-";
        int max = 0;
        for (Item i : items) if (i.id.startsWith(p)) max = Math.max(max, Integer.parseInt(i.id.substring(2)));
        return String.format("%s%03d", p, max + 1);
    }

    static Item find(String id) {
        for (Item i : items) if (i.id.equals(id)) return i;
        return null;
    }

    static int count(String status) {
        int n = 0;
        for (Item i : items) if (i.status.equals(status)) n++;
        return n;
    }

    static String badge(int returns) {
        if (returns >= 5) return "Campus Legend";
        if (returns >= 3) return "Campus Hero";
        if (returns >= 1) return "Good Samaritan";
        return "-";
    }

    // ---------- workflow ----------

    static Item add(String type, String name, String cat, String color, String loc, String date, String desc, String reporter) {
        Item it = new Item(nextId(type), type, name, cat, color, loc, date, desc, reporter, type, "");
        items.add(it);
        save();
        notify((type.equals(Item.LOST) ? "Lost" : "Found") + " report " + it.id + " (" + it.name + ") added by " + it.reporter);
        return it;
    }

    static void confirmMatch(Item a, Item b) {
        a.status = b.status = Item.MATCHED;
        a.pairId = b.id; b.pairId = a.id;
        Item lost = a.type.equals(Item.LOST) ? a : b, found = lost == a ? b : a;
        save();
        notify("Possible match found: " + lost.name + " (" + lost.id + ") <-> " + found.id + ". "
                + lost.reporter + ", please collect it from " + found.reporter + " (" + found.location + ")");
    }

    /** @return error message or null if everything is fine. */
    static String markReturned(Item it) {
        Item pair = it.pairId.isEmpty() ? null : find(it.pairId);
        if (it.status.equals(Item.RETURNED)) return "This item has already been returned.";
        if (pair == null && it.type.equals(Item.LOST)) return "Please match this lost item with a found item first.";
        Item found = it.type.equals(Item.FOUND) ? it : pair;
        it.status = Item.RETURNED;
        if (pair != null) pair.status = Item.RETURNED;
        int[] u = users.computeIfAbsent(found.reporter, k -> new int[2]);
        u[0] += REWARD_POINTS; u[1]++;
        save();
        notify("Item returned successfully: " + found.name + " (" + found.id + "). " + found.reporter
                + " earned " + REWARD_POINTS + " points - badge: " + badge(u[1]));
        return null;
    }

    static void delete(Item it) {
        if (!it.pairId.isEmpty()) {
            Item p = find(it.pairId);
            if (p != null && !p.status.equals(Item.RETURNED)) { p.status = p.type; p.pairId = ""; }
        }
        items.remove(it);
        save();
        notify("Report " + it.id + " deleted");
    }
}