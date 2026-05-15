package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class ActivityPanel extends JPanel {

    private JPanel list;

    public ActivityPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane scroll = new JScrollPane(list);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh(Connection conn) {
        list.removeAll();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT nom, consultation, priorite, diagnostic " +
                            "FROM patients ORDER BY id DESC LIMIT 6"
            );
            boolean first = true;
            boolean hasData = false;
            while (rs.next()) {
                if (!first)
                    list.add(Box.createRigidArea(new Dimension(0, 8)));
                list.add(buildRow(
                        rs.getString("nom"),
                        rs.getString("consultation"),
                        rs.getString("priorite"),
                        rs.getString("diagnostic")
                ));
                first = false;
                hasData = true;
            }
            if (!hasData) {
                JPanel empty = new JPanel(new BorderLayout());
                empty.setOpaque(false);
                JLabel msg = new JLabel(
                        "Aucun patient enregistré",
                        SwingConstants.CENTER);
                msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                msg.setForeground(HospitalDashboard.TEXT_LIGHT);
                empty.add(msg, BorderLayout.CENTER);
                list.add(empty);
            }
        } catch (Exception e) {
            JLabel err = new JLabel("Erreur de chargement : " + e.getMessage());
            err.setForeground(HospitalDashboard.ACCENT_RED);
            err.setFont(HospitalDashboard.FONT_SMALL);
            list.add(err);
        }
        list.revalidate();
        list.repaint();
    }

    private JPanel buildRow(String nom, String consult,
                            String priorite, String diag) {
        boolean crit    = "CRITIQUE".equalsIgnoreCase(priorite);
        Color   accent  = crit ? HospitalDashboard.ACCENT_RED
                : HospitalDashboard.ACCENT_GREEN;
        Color   bgBadge = crit ? new Color(0xFFEBEE)
                : new Color(0xE0F2F1);

        // Row container
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, HospitalDashboard.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        row.setMinimumSize(new Dimension(100, 62));
        row.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.NONE;

        // ── Dot
        gc.gridx   = 0;
        gc.gridy   = 0;
        gc.gridheight = 2;
        gc.insets  = new Insets(0, 0, 0, 12);
        gc.weightx = 0;
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillOval(0, (getHeight() - 10) / 2, 10, 10);
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(12, 12));
        row.add(dot, gc);

        // ── Nom + Consultation (ligne 1)
        gc.gridx      = 1;
        gc.gridy      = 0;
        gc.gridheight = 1;
        gc.weightx    = 1.0;
        gc.fill       = GridBagConstraints.HORIZONTAL;
        gc.insets     = new Insets(0, 0, 2, 12);

        JLabel nameL = new JLabel(
                "<html><b>" + escHtml(nom) + "</b>"
                        + "  <span style='color:#546E7A;'>—  "
                        + escHtml(consult) + "</span></html>"
        );
        nameL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameL.setForeground(HospitalDashboard.TEXT_DARK);
        row.add(nameL, gc);

        // ── Diagnostic (ligne 2)
        gc.gridx   = 1;
        gc.gridy   = 1;
        gc.insets  = new Insets(2, 0, 0, 12);
        JLabel diagL = new JLabel(
                "<html><span style='color:#90A4AE;font-size:11px;'>"
                        + escHtml(diag) + "</span></html>"
        );
        diagL.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        row.add(diagL, gc);

        // ── Badge priorité (span 2 rows)
        gc.gridx      = 2;
        gc.gridy      = 0;
        gc.gridheight = 2;
        gc.weightx    = 0;
        gc.fill       = GridBagConstraints.NONE;
        gc.anchor     = GridBagConstraints.EAST;
        gc.insets     = new Insets(0, 0, 0, 0);

        JLabel badge = new JLabel(priorite);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(accent);
        badge.setOpaque(true);
        badge.setBackground(bgBadge);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        row.add(badge, gc);

        return row;
    }

    private String escHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}