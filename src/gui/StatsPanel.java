package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import database.DBConnection;

public class StatsPanel extends JPanel {

    private JLabel totalLabel, critiqueLabel, normalLabel, dbLabel;
    private BarChartPanel   barChart;
    private DonutChartPanel donut;
    private ActivityPanel   activity;

    public StatsPanel() {
        setBackground(HospitalDashboard.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // ── Header ──────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel t = new JLabel("Tableau de Bord Médical");
        t.setFont(HospitalDashboard.FONT_TITLE);
        t.setForeground(HospitalDashboard.TEXT_DARK);

        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        subRow.setOpaque(false);

        JPanel liveDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(HospitalDashboard.ACCENT_GREEN);
                g2.fillOval(0, 4, 8, 8);
                g2.setColor(new Color(0x00897B, false));
                g2.setColor(new Color(0, 137, 123, 50));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(-2, 2, 12, 12);
            }
        };
        liveDot.setOpaque(false);
        liveDot.setPreferredSize(new Dimension(14, 18));

        JLabel sub = new JLabel(
                "Surveillance temps réel — JADE Multi-Agent Platform");
        sub.setFont(HospitalDashboard.FONT_BODY);
        sub.setForeground(HospitalDashboard.TEXT_MID);

        subRow.add(liveDot);
        subRow.add(sub);
        titleBlock.add(t);
        titleBlock.add(Box.createVerticalStrut(5));
        titleBlock.add(subRow);
        header.add(titleBlock, BorderLayout.WEST);

        // Buttons
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        JButton addBtn = buildBtn("＋  Nouveau Patient",
                HospitalDashboard.ACCENT_GREEN);
        addBtn.addActionListener(e -> {
            PatientFormDialog dlg = new PatientFormDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this));
            dlg.setVisible(true);
            if (dlg.isConfirmed()) {
                main.Main.sendPatient(
                        dlg.getNom(), dlg.getConsultation(), dlg.getPriorite());
                JOptionPane.showMessageDialog(this,
                        "✓ Patient \"" + dlg.getNom() +
                                "\" envoyé aux agents JADE !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                javax.swing.Timer timer =
                        new javax.swing.Timer(2000, ev -> refresh());
                timer.setRepeats(false);
                timer.start();
            }
        });

        JButton refreshBtn = buildBtn("↻  Actualiser",
                HospitalDashboard.ACCENT_BLUE);
        refreshBtn.addActionListener(e -> refresh());

        btnGroup.add(addBtn);
        btnGroup.add(refreshBtn);
        header.add(btnGroup, BorderLayout.EAST);

        // ── KPI Cards ───────────────────────────────────────────────
        totalLabel    = new JLabel("0");
        critiqueLabel = new JLabel("0");
        normalLabel   = new JLabel("0");


        JPanel kpiRow = new JPanel(new GridLayout(1, 3, 16, 0));
        kpiRow.setOpaque(false);
        kpiRow.setPreferredSize(new Dimension(0, 145));
        kpiRow.setMinimumSize(new Dimension(0, 145));

        kpiRow.add(kpiCard("Total Patients", totalLabel,
                "👥", HospitalDashboard.ACCENT_BLUE,
                new Color(0xE3F2FD), "+2 aujourd'hui"));
        kpiRow.add(kpiCard("Cas Critiques", critiqueLabel,
                "🚨", HospitalDashboard.ACCENT_RED,
                new Color(0xFFEBEE), "Priorité haute"));
        kpiRow.add(kpiCard("Cas Normaux", normalLabel,
                "💚", HospitalDashboard.ACCENT_GREEN,
                new Color(0xE0F2F1), "Stables"));

        // ── Charts ──────────────────────────────────────────────────
        barChart = new BarChartPanel();
        donut    = new DonutChartPanel();
        barChart.setPreferredSize(new Dimension(300, 230));
        barChart.setMinimumSize(new Dimension(200, 180));
        donut.setPreferredSize(new Dimension(300, 230));
        donut.setMinimumSize(new Dimension(200, 180));

        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 16, 0));
        chartsRow.setOpaque(false);
        chartsRow.setPreferredSize(new Dimension(0, 290));
        chartsRow.add(chartCard("📈  Consultations par Priorité", barChart));
        chartsRow.add(chartCard("🥧  Répartition des Patients",   donut));

        // ── Activity ────────────────────────────────────────────────
        activity = new ActivityPanel();
        JPanel actCard = chartCard(
                "⚡  Activité Récente — Agents JADE", activity);
        actCard.setPreferredSize(new Dimension(0, 240));

        // ── Body ────────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(kpiRow);
        body.add(Box.createVerticalStrut(20));
        body.add(chartsRow);
        body.add(Box.createVerticalStrut(20));
        body.add(actCard);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
        refresh();
        // Auto-refresh toutes les 5 secondes
        javax.swing.Timer autoRefresh = new javax.swing.Timer(5000, e -> refresh());
        autoRefresh.setInitialDelay(5000);
        autoRefresh.start();
    }

    public void refresh() {
        // Vérification null avant tout
        if (barChart == null || donut == null || activity == null) return;

        Connection conn = DBConnection.connect();
        if (conn == null) return;

        try {
            Statement st = conn.createStatement();

            // Total
            ResultSet r1 = st.executeQuery("SELECT COUNT(*) FROM patients");
            r1.next();
            int total = r1.getInt(1);

            // Critiques
            ResultSet r2 = st.executeQuery(
                    "SELECT COUNT(*) FROM patients WHERE priorite='CRITIQUE'");
            r2.next();
            int crit = r2.getInt(1);
            int normal = total - crit;

            // Consultation stats
            ResultSet r3 = st.executeQuery(
                    "SELECT consultation, COUNT(*) as cnt " +
                            "FROM patients GROUP BY consultation ORDER BY cnt DESC");
            java.util.LinkedHashMap<String, Integer> consultMap =
                    new java.util.LinkedHashMap<>();
            while (r3.next())
                consultMap.put(r3.getString("consultation"), r3.getInt("cnt"));

            // Activité
            activity.refresh(conn);

            // Stocker résultats pour Swing thread
            final int fTotal  = total;
            final int fCrit   = crit;
            final int fNormal = normal;
            final java.util.LinkedHashMap<String, Integer> fMap = consultMap;

            conn.close();

            // Mise à jour UI sur EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    if (totalLabel    != null) totalLabel.setText(String.valueOf(fTotal));
                    if (critiqueLabel != null) critiqueLabel.setText(String.valueOf(fCrit));
                    if (normalLabel   != null) normalLabel.setText(String.valueOf(fNormal));

                    if (barChart != null) {
                        if (!fMap.isEmpty())
                            barChart.setConsultData(fMap);
                        else
                            barChart.setData(fCrit, fNormal);
                    }
                    if (donut != null) donut.setData(fCrit, fNormal);

                    if (barChart != null) { barChart.revalidate(); barChart.repaint(); }
                    if (donut    != null) { donut.revalidate();    donut.repaint(); }
                    revalidate();
                    repaint();

                } catch (Exception e) {

                }
            });

        } catch (Exception e) {

            DBConnection.closeQuietly(conn);
        }
    }

    // ── KPI card standard
    private JPanel kpiCard(String label, JLabel valLbl, String icon,
                           Color accent, Color bg, String trend) {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, HospitalDashboard.BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 18, 14, 18)
        ));

        // Icon circle
        JPanel iconCircle = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(46, 46));
        JLabel ico = new JLabel(icon, SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconCircle.add(ico);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(HospitalDashboard.TEXT_MID);
        topRow.add(lbl,        BorderLayout.WEST);
        topRow.add(iconCircle, BorderLayout.EAST);

        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valLbl.setForeground(HospitalDashboard.TEXT_DARK);

        JLabel trendLbl = new JLabel("↑ " + trend);
        trendLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        trendLbl.setForeground(accent);

        // Bottom accent bar
        JPanel accentBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, accent, getWidth(), 0, accent.brighter());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
            }
        };
        accentBar.setOpaque(false);
        accentBar.setPreferredSize(new Dimension(0, 4));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(topRow);
        center.add(Box.createVerticalStrut(8));
        center.add(valLbl);
        center.add(Box.createVerticalStrut(6));
        center.add(trendLbl);

        card.add(center,    BorderLayout.CENTER);
        card.add(accentBar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel chartCard(String title, JPanel content) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, HospitalDashboard.BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 18, 14, 18)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(HospitalDashboard.TEXT_DARK);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                HospitalDashboard.BORDER_COLOR));
        lbl.setPreferredSize(new Dimension(0, 36));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(content, BorderLayout.CENTER);

        card.add(lbl,  BorderLayout.NORTH);
        card.add(wrap, BorderLayout.CENTER);
        return card;
    }

    private JButton buildBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(178, 40));
        return btn;
    }
}