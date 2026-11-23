import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** FindIt - Campus Lost & Found System (main window + all screens). */
public class FindItApp extends JFrame {

    // ====================== base page ======================
    abstract static class Page extends JPanel {
        final JPanel body = new JPanel(new BorderLayout());
        Page(String title, String sub) {
            setLayout(new BorderLayout());
            setBackground(UI.BG);
            setBorder(new EmptyBorder(24, 28, 24, 28));
            JPanel h = new JPanel();
            h.setLayout(new BoxLayout(h, BoxLayout.Y_AXIS));
            h.setOpaque(false);
            h.setBorder(new EmptyBorder(0, 0, 16, 0));
            h.add(UI.label(title, UI.H1, UI.TEXT));
            h.add(Box.createVerticalStrut(2));
            h.add(UI.label(sub, UI.SMALL, UI.MUTED));
            add(h, BorderLayout.NORTH);
            body.setOpaque(false);
            add(body, BorderLayout.CENTER);
        }
        abstract void refresh();
    }

    // ====================== dashboard ======================
    class DashboardPage extends Page {
        final JLabel lost = new JLabel(), found = new JLabel(), matched = new JLabel(), returned = new JLabel();
        final DefaultListModel<String> recent = new DefaultListModel<>();

        DashboardPage() {
            super("FindIt Dashboard", "Campus Lost & Found - everything at a glance");
            JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
            stats.setOpaque(false);
            stats.add(statCard("LOST", lost, UI.RED));
            stats.add(statCard("FOUND", found, UI.ACCENT));
            stats.add(statCard("MATCHED", matched, UI.ORANGE));
            stats.add(statCard("RETURNED", returned, UI.GREEN));

            UI.Card updates = new UI.Card();
            updates.setLayout(new BorderLayout(0, 10));
            updates.add(UI.label("Recent Updates", new Font("SansSerif", Font.BOLD, 17), UI.TEXT), BorderLayout.NORTH);
            JList<String> list = new JList<>(recent);
            list.setFixedCellHeight(34);
            list.setSelectionBackground(Color.WHITE);
            list.setSelectionForeground(UI.TEXT);
            updates.add(new JScrollPane(list) {{ setBorder(null); }}, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            actions.setOpaque(false);
            UI.RoundButton b1 = new UI.RoundButton("+ Report Lost Item", UI.RED);
            UI.RoundButton b2 = new UI.RoundButton("+ Report Found Item", UI.ACCENT);
            UI.RoundButton b3 = new UI.RoundButton("Find Matches", UI.PURPLE);
            b1.addActionListener(e -> { report.setType(Item.LOST); FindItApp.this.show("Report Item"); });
            b2.addActionListener(e -> { report.setType(Item.FOUND); FindItApp.this.show("Report Item"); });
            b3.addActionListener(e -> FindItApp.this.show("Find Matches"));
            actions.add(b1); actions.add(b2); actions.add(b3);
            actions.setBorder(new EmptyBorder(16, 0, 0, 0));

            JPanel center = new JPanel(new BorderLayout(0, 16));
            center.setOpaque(false);
            center.add(stats, BorderLayout.NORTH);
            center.add(updates, BorderLayout.CENTER);
            body.add(center, BorderLayout.CENTER);
            body.add(actions, BorderLayout.SOUTH);
        }

        JPanel statCard(String caption, JLabel num, Color c) {
            UI.Card card = new UI.Card();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            num.setFont(UI.BIG); num.setForeground(c);
            card.add(num);
            card.add(UI.label(caption, UI.BOLD, UI.MUTED));
            return card;
        }

        void refresh() {
            lost.setText("" + Store.count(Item.LOST));
            found.setText("" + Store.count(Item.FOUND));
            matched.setText("" + Store.count(Item.MATCHED));
            returned.setText("" + Store.count(Item.RETURNED));
            recent.clear();
            for (int i = 0; i < Math.min(8, Store.notifs.size()); i++)
                recent.addElement("<html><span style='color:#6B7280'>" + Store.notifs.get(i)[0] + "</span> &nbsp; " + Store.notifs.get(i)[1] + "</html>");
        }
    }

    // ====================== report item ======================
    class ReportPage extends Page {
        final JRadioButton rbLost = new JRadioButton("I lost something", true), rbFound = new JRadioButton("I found something");
        final JTextField name = new JTextField(), date = new JTextField(LocalDate.now().toString()), reporter = new JTextField();
        final JComboBox<String> cat = new JComboBox<>(Store.CATEGORIES), color = new JComboBox<>(Store.COLORS), loc = new JComboBox<>(Store.LOCATIONS);
        final JTextArea desc = new JTextArea(4, 20);

        ReportPage() {
            super("Report Item", "Fill in the details of the lost or found item");
            UI.Card card = new UI.Card();
            card.setLayout(new GridBagLayout());
            ButtonGroup g = new ButtonGroup();
            g.add(rbLost); g.add(rbFound);
            for (JRadioButton r : new JRadioButton[]{rbLost, rbFound}) { r.setOpaque(false); r.setFont(UI.BOLD); }
            JPanel types = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
            types.setOpaque(false); types.add(rbLost); types.add(rbFound);
            desc.setLineWrap(true); desc.setWrapStyleWord(true);
            for (JComponent c : new JComponent[]{name, date, reporter, desc}) UI.styleField(c);

            int r = 0;
            row(card, r++, "Report type", types);
            row(card, r++, "Item name *", name);
            row(card, r++, "Category", cat);
            row(card, r++, "Color", color);
            row(card, r++, "Campus location", loc);
            row(card, r++, "Date (yyyy-mm-dd)", date);
            row(card, r++, "Your name *", reporter);
            row(card, r++, "Description", new JScrollPane(desc) {{ setBorder(null); }});

            UI.RoundButton submit = new UI.RoundButton("Submit Report", UI.ACCENT);
            submit.addActionListener(e -> submit());
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridx = 1; gc.gridy = r; gc.anchor = GridBagConstraints.WEST; gc.insets = new Insets(14, 0, 0, 0);
            card.add(submit, gc);

            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.add(card, BorderLayout.NORTH);
            body.add(new JScrollPane(wrap) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }});
        }

        void row(JPanel p, int y, String label, JComponent c) {
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridx = 0; gc.gridy = y; gc.anchor = GridBagConstraints.NORTHWEST;
            gc.insets = new Insets(8, 0, 8, 20);
            p.add(UI.label(label, UI.BOLD, UI.TEXT), gc);
            gc = new GridBagConstraints();
            gc.gridx = 1; gc.gridy = y; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(6, 0, 6, 0);
            if (c instanceof JComboBox) { c.setFont(UI.BASE); ((JComboBox<?>) c).setBackground(Color.WHITE); }
            p.add(c, gc);
        }

        void setType(String t) { (t.equals(Item.LOST) ? rbLost : rbFound).setSelected(true); }

        void submit() {
            if (name.getText().trim().isEmpty() || reporter.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Item name and your name are required.", "Missing info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try { LocalDate.parse(date.getText().trim()); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Date must be in yyyy-mm-dd format (e.g. 2026-10-23).", "Invalid date", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String type = rbLost.isSelected() ? Item.LOST : Item.FOUND;
            Item it = Store.add(type, name.getText(), (String) cat.getSelectedItem(), (String) color.getSelectedItem(),
                    (String) loc.getSelectedItem(), date.getText().trim(), desc.getText(), reporter.getText());
            List<Matcher.Match> ms = Matcher.find(it);
            String msg = "Report saved! ID: " + it.id;
            if (!ms.isEmpty()) {
                Store.notify("Possible match found for " + it.id + " (" + it.name + "): " + ms.size() + " suggestion(s)");
                msg += "\n\nPossible match found! " + ms.size() + " suggestion(s) - check the 'Find Matches' page.";
            }
            JOptionPane.showMessageDialog(this, msg, "FindIt", JOptionPane.INFORMATION_MESSAGE);
            name.setText(""); desc.setText("");
            refreshAll();
        }

        void refresh() { date.setText(date.getText().isEmpty() ? LocalDate.now().toString() : date.getText()); }
    }

    // ====================== find matches ======================
    class MatchPage extends Page {
        final JComboBox<Item> picker = new JComboBox<>();
        final DefaultTableModel model = UI.model("Match", "ID", "Item", "Category", "Color", "Location", "Reporter");
        final JTable table = new JTable(model);
        final JLabel info = UI.label(" ", UI.SMALL, UI.MUTED);
        List<Matcher.Match> results = new ArrayList<>();
        Item source;

        MatchPage() {
            super("Find Matches", "Smart suggestions based on category, color and location");
            UI.Card top = new UI.Card();
            top.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
            picker.setFont(UI.BASE); picker.setBackground(Color.WHITE);
            picker.setPreferredSize(new Dimension(360, 36));
            UI.RoundButton go = new UI.RoundButton("Find Matches", UI.PURPLE);
            go.addActionListener(e -> search());
            top.add(UI.label("Open report:", UI.BOLD, UI.TEXT)); top.add(picker); top.add(go);

            JScrollPane sp = UI.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(90);

            UI.RoundButton confirm = new UI.RoundButton("Confirm Selected Match", UI.GREEN);
            confirm.addActionListener(e -> confirm());
            JPanel bottom = new JPanel(new BorderLayout());
            bottom.setOpaque(false);
            bottom.setBorder(new EmptyBorder(14, 0, 0, 0));
            bottom.add(info, BorderLayout.CENTER);
            bottom.add(confirm, BorderLayout.EAST);

            JPanel mid = new JPanel(new BorderLayout(0, 14));
            mid.setOpaque(false);
            mid.add(top, BorderLayout.NORTH); mid.add(sp, BorderLayout.CENTER);
            body.add(mid, BorderLayout.CENTER); body.add(bottom, BorderLayout.SOUTH);
        }

        void search() {
            source = (Item) picker.getSelectedItem();
            model.setRowCount(0);
            if (source == null) { info.setText("No open reports available."); return; }
            results = Matcher.find(source);
            for (Matcher.Match m : results)
                model.addRow(new Object[]{m.score + "%", m.item.id, m.item.name, m.item.category, m.item.color, m.item.location, m.item.reporter});
            info.setText(results.isEmpty() ? "No match found yet. Try again when new reports arrive."
                    : results.size() + " possible match(es) found. Select one and confirm.");
        }

        void confirm() {
            int r = table.getSelectedRow();
            if (r < 0) { info.setText("Please select a match from the table first."); return; }
            Item other = results.get(r).item;
            Store.confirmMatch(source, other);
            JOptionPane.showMessageDialog(this, source.id + " and " + other.id + " have been matched! Both reporters have been notified.");
            refreshAll();
        }

        void refresh() {
            Item keep = (Item) picker.getSelectedItem();
            picker.removeAllItems();
            for (Item i : Store.items) if (i.isOpen()) picker.addItem(i);
            if (keep != null) for (int i = 0; i < picker.getItemCount(); i++) if (picker.getItemAt(i).id.equals(keep.id)) picker.setSelectedIndex(i);
            model.setRowCount(0); results.clear(); info.setText(" ");
        }
    }

    // ====================== manage reports ======================
    class ManagePage extends Page {
        final JTextField search = new JTextField();
        final JComboBox<String> locFilter = new JComboBox<>(), statusFilter = new JComboBox<>(new String[]{"All Status", "LOST", "FOUND", "MATCHED", "RETURNED"});
        final DefaultTableModel model = UI.model("ID", "Type", "Item", "Category", "Color", "Location", "Date", "Reporter", "Status");
        final JTable table = new JTable(model);
        final List<Item> shown = new ArrayList<>();

        @SuppressWarnings("unchecked")
        ManagePage() {
            super("Manage Reports", "Search, filter and update report status");
            locFilter.addItem("All Locations");
            for (String l : Store.LOCATIONS) locFilter.addItem(l);
            UI.styleField(search);
            search.setPreferredSize(new Dimension(240, 36));
            for (JComboBox<String> c : new JComboBox[]{locFilter, statusFilter}) { c.setFont(UI.BASE); c.setBackground(Color.WHITE); c.addActionListener(e -> refresh()); }
            search.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { refresh(); }
                public void removeUpdate(DocumentEvent e) { refresh(); }
                public void changedUpdate(DocumentEvent e) { refresh(); }
            });

            UI.Card filters = new UI.Card();
            filters.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
            filters.add(UI.label("Search:", UI.BOLD, UI.TEXT)); filters.add(search);
            filters.add(locFilter); filters.add(statusFilter);

            JScrollPane sp = UI.styleTable(table);
            UI.colorStatusColumn(table, 8);
            int[] w = {70, 75, 170, 110, 75, 130, 95, 90, 90};
            for (int i = 0; i < w.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
            table.getColumnModel().getColumn(0).setMaxWidth(90);
            table.getColumnModel().getColumn(1).setMaxWidth(90);

            UI.RoundButton ret = new UI.RoundButton("Mark as Returned", UI.GREEN);
            UI.RoundButton del = new UI.RoundButton("Delete", UI.RED);
            UI.RoundButton view = new UI.RoundButton("View Details", UI.ACCENT);
            ret.addActionListener(e -> {
                Item it = selected(); if (it == null) return;
                String err = Store.markReturned(it);
                if (err != null) JOptionPane.showMessageDialog(this, err, "FindIt", JOptionPane.WARNING_MESSAGE);
                else JOptionPane.showMessageDialog(this, "Item returned successfully! The finder earned " + Store.REWARD_POINTS + " points.");
                refreshAll();
            });
            del.addActionListener(e -> {
                Item it = selected(); if (it == null) return;
                if (JOptionPane.showConfirmDialog(this, "Delete report " + it.id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    Store.delete(it); refreshAll();
                }
            });
            view.addActionListener(e -> {
                Item it = selected(); if (it == null) return;
                JOptionPane.showMessageDialog(this,
                        "ID: " + it.id + "\nType: " + it.type + "\nItem: " + it.name + "\nCategory: " + it.category + "\nColor: " + it.color
                                + "\nLocation: " + it.location + "\nDate: " + it.date + "\nReporter: " + it.reporter
                                + "\nStatus: " + it.status + (it.pairId.isEmpty() ? "" : "\nLinked with: " + it.pairId)
                                + "\nDescription: " + it.description, "Item Details", JOptionPane.INFORMATION_MESSAGE);
            });
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            btns.setOpaque(false); btns.setBorder(new EmptyBorder(14, 0, 0, 0));
            btns.add(ret); btns.add(view); btns.add(del);

            JPanel mid = new JPanel(new BorderLayout(0, 14));
            mid.setOpaque(false);
            mid.add(filters, BorderLayout.NORTH); mid.add(sp, BorderLayout.CENTER);
            body.add(mid, BorderLayout.CENTER); body.add(btns, BorderLayout.SOUTH);
        }

        Item selected() {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Please select an item from the table first."); return null; }
            return shown.get(r);
        }

        void refresh() {
            model.setRowCount(0); shown.clear();
            String q = search.getText().trim().toLowerCase();
            String loc = (String) locFilter.getSelectedItem(), st = (String) statusFilter.getSelectedItem();
            for (Item i : Store.items) {
                if (!loc.equals("All Locations") && !i.location.equals(loc)) continue;
                if (!st.equals("All Status") && !i.status.equals(st)) continue;
                if (!q.isEmpty() && !(i.id + " " + i.name + " " + i.category + " " + i.color + " " + i.reporter + " " + i.description).toLowerCase().contains(q)) continue;
                shown.add(i);
                model.addRow(new Object[]{i.id, i.type, i.name, i.category, i.color, i.location, i.date, i.reporter, i.status});
            }
        }
    }

    // ====================== rewards ======================
    class RewardsPage extends Page {
        final DefaultTableModel model = UI.model("Rank", "Good Samaritan", "Points", "Items Returned", "Badge");
        RewardsPage() {
            super("Rewards", "Finders earn " + Store.REWARD_POINTS + " points and an appreciation badge for every verified return");
            UI.Card info = new UI.Card();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.add(UI.label("Badges:  1 return = Good Samaritan   |   3 returns = Campus Hero   |   5 returns = Campus Legend", UI.BASE, UI.TEXT));
            JTable t = new JTable(model);
            JScrollPane sp = UI.styleTable(t);
            t.getColumnModel().getColumn(0).setMaxWidth(80);
            JPanel mid = new JPanel(new BorderLayout(0, 14));
            mid.setOpaque(false);
            mid.add(info, BorderLayout.NORTH); mid.add(sp, BorderLayout.CENTER);
            body.add(mid);
        }
        void refresh() {
            model.setRowCount(0);
            List<Map.Entry<String, int[]>> list = new ArrayList<>(Store.users.entrySet());
            list.sort((a, b) -> b.getValue()[0] - a.getValue()[0]);
            int rank = 1;
            for (Map.Entry<String, int[]> e : list)
                model.addRow(new Object[]{"#" + rank++, e.getKey(), e.getValue()[0] + " pts", e.getValue()[1], Store.badge(e.getValue()[1])});
        }
    }

    // ====================== updates ======================
    class UpdatesPage extends Page {
        final DefaultListModel<String> lm = new DefaultListModel<>();
        UpdatesPage() {
            super("Updates", "In-app email-style notifications");
            UI.Card card = new UI.Card();
            card.setLayout(new BorderLayout());
            JList<String> list = new JList<>(lm);
            list.setFixedCellHeight(46);
            list.setSelectionBackground(new Color(0xEEF3FF));
            list.setSelectionForeground(UI.TEXT);
            card.add(new JScrollPane(list) {{ setBorder(null); }}, BorderLayout.CENTER);
            UI.RoundButton clear = new UI.RoundButton("Clear All", UI.RED);
            clear.addActionListener(e -> { Store.notifs.clear(); Store.save(); refreshAll(); });
            JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            s.setOpaque(false); s.setBorder(new EmptyBorder(14, 0, 0, 0)); s.add(clear);
            body.add(card, BorderLayout.CENTER); body.add(s, BorderLayout.SOUTH);
        }
        void refresh() {
            lm.clear();
            for (String[] n : Store.notifs)
                lm.addElement("<html><b>FindIt</b> &nbsp;<span style='color:#6B7280'>" + n[0] + "</span><br>" + n[1] + "</html>");
        }
    }

    // ====================== main frame ======================
    final CardLayout cards = new CardLayout();
    final JPanel content = new JPanel(cards);
    final Map<String, Page> pages = new java.util.LinkedHashMap<>();
    final Map<String, UI.NavButton> navs = new java.util.LinkedHashMap<>();
    DashboardPage dash; ReportPage report;

    FindItApp() {
        super("FindIt - Campus Lost & Found");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1120, 720);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        dash = new DashboardPage(); report = new ReportPage();
        pages.put("Dashboard", dash);
        pages.put("Report Item", report);
        pages.put("Find Matches", new MatchPage());
        pages.put("Manage Reports", new ManagePage());
        pages.put("Rewards", new RewardsPage());
        pages.put("Updates", new UpdatesPage());

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(UI.SIDEBAR);
        side.setPreferredSize(new Dimension(230, 0));
        JLabel logo = UI.label("FindIt", new Font("SansSerif", Font.BOLD, 28), Color.WHITE);
        logo.setBorder(new EmptyBorder(26, 24, 2, 0));
        JLabel tag = UI.label("Campus Lost & Found", UI.SMALL, new Color(0x8D9AC0));
        tag.setBorder(new EmptyBorder(0, 24, 24, 0));
        side.add(logo); side.add(tag);

        for (String k : pages.keySet()) {
            content.add(pages.get(k), k);
            UI.NavButton b = new UI.NavButton(k);
            b.addActionListener(e -> show(k));
            navs.put(k, b);
            side.add(b);
        }
        side.add(Box.createVerticalGlue());
        JLabel foot = UI.label("v1.0  |  Core Java + Swing", UI.SMALL, new Color(0x6D7A9E));
        foot.setBorder(new EmptyBorder(0, 24, 18, 0));
        side.add(foot);

        setLayout(new BorderLayout());
        add(side, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        show("Dashboard");
    }

    void show(String name) {
        for (Map.Entry<String, UI.NavButton> e : navs.entrySet()) e.getValue().setActive(e.getKey().equals(name));
        pages.get(name).refresh();
        cards.show(content, name);
    }

    void refreshAll() {
        for (Page p : pages.values()) p.refresh();
    }

    public static void main(String[] args) {
        UI.setup();
        Store.load();
        SwingUtilities.invokeLater(() -> new FindItApp().setVisible(true));
    }
}