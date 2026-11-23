import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;

/** All colors, fonts and custom styled components in one place. */
public class UI {
    static final Color SIDEBAR = new Color(0x1B2540), ACCENT = new Color(0x4F7CFF), BG = new Color(0xF3F5FB);
    static final Color TEXT = new Color(0x1F2937), MUTED = new Color(0x6B7280), BORDER = new Color(0xE1E5EE);
    static final Color GREEN = new Color(0x22A06B), RED = new Color(0xE5484D), ORANGE = new Color(0xF59E0B), PURPLE = new Color(0x8B5CF6);

    static final Font BASE = new Font("SansSerif", Font.PLAIN, 14);
    static final Font BOLD = new Font("SansSerif", Font.BOLD, 14);
    static final Font H1 = new Font("SansSerif", Font.BOLD, 26);
    static final Font SMALL = new Font("SansSerif", Font.PLAIN, 13);
    static final Font BIG = new Font("SansSerif", Font.BOLD, 34);

    static void setup() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
        for (Object k : java.util.Collections.list(UIManager.getDefaults().keys()))
            if (k.toString().endsWith(".font")) UIManager.put(k, BASE);
        UIManager.put("OptionPane.messageFont", BASE);
        UIManager.put("OptionPane.buttonFont", BASE);
    }

    static Color statusColor(String s) {
        switch (s) {
            case "LOST": return RED;
            case "FOUND": return ACCENT;
            case "MATCHED": return ORANGE;
            case "RETURNED": return GREEN;
            default: return MUTED;
        }
    }

    // ---------- components ----------

    static class RoundButton extends JButton {
        final Color base;
        RoundButton(String text, Color base) {
            super(text);
            this.base = base;
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setRolloverEnabled(true); setForeground(Color.WHITE); setFont(BOLD);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 20, 9, 20));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = base;
            if (getModel().isPressed()) c = base.darker();
            else if (getModel().isRollover()) c = base.brighter();
            g2.setColor(isEnabled() ? c : new Color(0xC5CAD6));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class NavButton extends JButton {
        boolean active;
        NavButton(String text) {
            super(text);
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setRolloverEnabled(true); setFont(new Font("SansSerif", Font.BOLD, 15));
            setHorizontalAlignment(SwingConstants.LEFT); setForeground(new Color(0xC7D0E8));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 20, 12, 12));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        }
        void setActive(boolean a) { active = a; setForeground(a ? Color.WHITE : new Color(0xC7D0E8)); repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) { g2.setColor(ACCENT); g2.fillRoundRect(10, 2, getWidth() - 20, getHeight() - 4, 12, 12); }
            else if (getModel().isRollover()) { g2.setColor(new Color(255, 255, 255, 25)); g2.fillRoundRect(10, 2, getWidth() - 20, getHeight() - 4, 12, 12); }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class Card extends JPanel {
        Card() { setOpaque(false); setBorder(new EmptyBorder(18, 20, 18, 20)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.setColor(BORDER); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static JLabel label(String t, Font f, Color c) { JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); return l; }

    static void styleField(JComponent c) {
        c.setFont(BASE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
    }

    static JScrollPane styleTable(JTable t) {
        t.setRowHeight(34); t.setFont(BASE); t.setShowVerticalLines(false);
        t.setGridColor(new Color(0xEEF0F6)); t.setSelectionBackground(new Color(0xDCE6FF));
        t.setSelectionForeground(TEXT); t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);
        JTableHeader h = t.getTableHeader();
        h.setPreferredSize(new Dimension(0, 38)); h.setReorderingAllowed(false);
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, s, f, r, c);
                l.setBackground(new Color(0xF0F3FA)); l.setForeground(MUTED); l.setFont(BOLD);
                l.setBorder(new EmptyBorder(0, 10, 0, 10)); l.setOpaque(true);
                return l;
            }
        });
        DefaultTableCellRenderer padded = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, s, f, r, c);
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                return l;
            }
        };
        t.setDefaultRenderer(Object.class, padded);
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    /** Shows the status column as colored bold text. */
    static void colorStatusColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, s, f, r, c);
                l.setForeground(statusColor(String.valueOf(v))); l.setFont(BOLD);
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                return l;
            }
        });
    }

    static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }
}