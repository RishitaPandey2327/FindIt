/** One lost or found report. */
public class Item {
    public static final String LOST = "LOST", FOUND = "FOUND", MATCHED = "MATCHED", RETURNED = "RETURNED";

    String id, type, name, category, color, location, date, description, reporter, status, pairId;

    Item(String id, String type, String name, String category, String color, String location,
         String date, String description, String reporter, String status, String pairId) {
        this.id = id; this.type = type; this.name = clean(name); this.category = category;
        this.color = color; this.location = location; this.date = date;
        this.description = clean(description); this.reporter = clean(reporter);
        this.status = status; this.pairId = pairId == null ? "" : pairId;
    }

    /** Remove '|' and new lines so they cannot break the file format. */
    static String clean(String s) {
        return s == null ? "" : s.replace("|", "/").replace("\n", " ").replace("\r", " ").trim();
    }

    String toLine() {
        return String.join("|", id, type, name, category, color, location, date, description, reporter, status, pairId);
    }

    static Item fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 11) return null;
        return new Item(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9], p[10]);
    }

    boolean isOpen() { return status.equals(type); }   // not matched yet

    @Override public String toString() { return id + " - " + name + " (" + type + ")"; }
}