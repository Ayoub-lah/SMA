package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import database.DBConnection;

public class HospitalDashboard extends JFrame {

    // ── Palette
    public static final Color BG_MAIN        = new Color(0xEFF4FB);
    public static final Color BG_SIDEBAR     = new Color(0x0A2342);
    public static final Color BG_CARD        = Color.WHITE;
    public static final Color ACCENT_BLUE    = new Color(0x1565C0);
    public static final Color ACCENT_BLUE2   = new Color(0x1E88E5);
    public static final Color ACCENT_GREEN   = new Color(0x00897B);
    public static final Color ACCENT_RED     = new Color(0xC62828);
    public static final Color ACCENT_ORANGE  = new Color(0xEF6C00);
    public static final Color ACCENT_PURPLE  = new Color(0x6A1B9A);
    public static final Color ACCENT_CYAN    = new Color(0x0097A7);
    public static final Color TEXT_DARK      = new Color(0x0D1B2A);
    public static final Color TEXT_MID       = new Color(0x546E7A);
    public static final Color TEXT_LIGHT     = new Color(0x90A4AE);
    public static final Color BORDER_COLOR   = new Color(0xDDE6F0);
    public static final Color SIDEBAR_ACTIVE = new Color(0x1565C0);
    public static final Color SIDEBAR_TEXT   = new Color(0x7B9BB8);
    public static final Color SIDEBAR_HOVER  = new Color(0x112D4E);
    public static final Color TOPBAR_BG      = new Color(0x0D2137);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_H2    = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_H3    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_NUM   = new Font("Segoe UI", Font.BOLD, 32);

    // ── State
    private boolean sidebarExpanded = true;
    private static final int SIDEBAR_W_OPEN   = 240;
    private static final int SIDEBAR_W_CLOSED = 64;

    // ── Components
    private JPanel            sidebarPanel;
    private CardLayout        cardLayout;
    private JPanel            contentArea;
    private LogPanel          logPanel;
    private PatientTablePanel tablePanel;
    private StatsPanel        statsPanel;
    private JLabel            breadcrumb;

    // Nav items
    private JPanel navDashboard, navPatients, navLogs;

    // Nav labels (pour cacher/afficher)
    private JLabel lblDashboard, lblPatients, lblLogs;
    private JLabel lblNavSection, lblSysSection;
    private JLabel lblMysql, lblJade, lblAgents;
    private JLabel badgeMysql, badgeJade, badgeAgents;
    private JLabel footerLabel;
    private JLabel l1Logo, l2Logo;
    private JButton toggleBtn;

    public HospitalDashboard() {
        setTitle("HospitalSMA — Blue Medical Dashboard");
        setSize(1400, 860);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        sidebarPanel = buildSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(BG_MAIN);

        statsPanel = new StatsPanel();
        logPanel   = new LogPanel();
        tablePanel = new PatientTablePanel();

        contentArea.add(statsPanel, "dashboard");
        contentArea.add(tablePanel, "patients");
        contentArea.add(logPanel,   "logs");

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_MAIN);
        main.add(buildTopBar(), BorderLayout.NORTH);
        main.add(contentArea,   BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);
        setVisible(true);
        showPage("dashboard", navDashboard, lblDashboard);
    }

    // ════════════════════════════════════════════════════
    //  SIDEBAR
    // ════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sb = new JPanel(new BorderLayout());
        sb.setBackground(BG_SIDEBAR);
        sb.setPreferredSize(new Dimension(SIDEBAR_W_OPEN, 0));

        JPanel inner = new JPanel();
        inner.setBackground(BG_SIDEBAR);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // ── Logo block
        inner.add(buildLogoBlock());

        // ── Toggle button
        inner.add(buildToggleRow());

        // ── Divider
        inner.add(hLine());

        // ── NAVIGATION
        inner.add(gap(10));
        lblNavSection = sectionLbl("NAVIGATION");
        inner.add(lblNavSection);
        inner.add(gap(4));

        navDashboard = navItem("📊", "Dashboard",   "dashboard");
        navPatients  = navItem("🏥", "Patients",    "patients");
        navLogs      = navItem("📋", "Logs Agents", "logs");
        inner.add(navDashboard);
        inner.add(navPatients);
        inner.add(navLogs);

        // ── SYSTÈME
        inner.add(gap(10));
        inner.add(hLine());
        inner.add(gap(10));
        lblSysSection = sectionLbl("ÉTAT SYSTÈME");
        inner.add(lblSysSection);
        inner.add(gap(4));
        inner.add(buildSysRow());

        // ── Footer
        inner.add(Box.createVerticalGlue());
        inner.add(hLine());
        footerLabel = new JLabel("FST Tanger — SIT & Big Data 2025");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(0x2E5B84));
        footerLabel.setMaximumSize(new Dimension(240, 34));
        footerLabel.setPreferredSize(new Dimension(240, 34));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        footerLabel.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(footerLabel);

        sb.add(inner, BorderLayout.CENTER);
        return sb;
    }

    // ── Logo
    private JPanel buildLogoBlock() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x0D2137),
                        getWidth(), 0, new Color(0x1565C0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(SIDEBAR_W_OPEN, 68));
        p.setMinimumSize(new Dimension(SIDEBAR_W_OPEN, 68));
        p.setPreferredSize(new Dimension(SIDEBAR_W_OPEN, 68));
        p.setAlignmentX(LEFT_ALIGNMENT);

        // Icon
        JPanel icon = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1565C0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(36, 36));
        icon.setMinimumSize(new Dimension(36, 36));
        icon.setMaximumSize(new Dimension(36, 36));
        JLabel h = new JLabel("H");
        h.setFont(new Font("Segoe UI", Font.BOLD, 18));
        h.setForeground(Color.WHITE);
        icon.add(h);

        // Text
        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        l1Logo = new JLabel("HospitalSMA");
        l1Logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l1Logo.setForeground(Color.WHITE);
        l2Logo = new JLabel("Multi-Agents JADE");
        l2Logo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l2Logo.setForeground(new Color(0x5B8BB8));
        txt.add(l1Logo);
        txt.add(Box.createVerticalStrut(2));
        txt.add(l2Logo);

        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, 14, 0, 10);
        p.add(icon, gc);
        gc.insets = new Insets(0, 0, 0, 0);
        gc.weightx = 1.0;
        p.add(txt, gc);

        return p;
    }

    // ── Toggle row
    private JPanel buildToggleRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(0x071828));
        row.setMaximumSize(new Dimension(SIDEBAR_W_OPEN, 38));
        row.setMinimumSize(new Dimension(SIDEBAR_W_OPEN, 38));
        row.setPreferredSize(new Dimension(SIDEBAR_W_OPEN, 38));
        row.setAlignmentX(LEFT_ALIGNMENT);

        toggleBtn = new JButton("◀  Réduire") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                        ? new Color(0x112D4E) : new Color(0x071828));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        toggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toggleBtn.setForeground(new Color(0x5B8BB8));
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBtn.setHorizontalAlignment(SwingConstants.LEFT);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));

        toggleBtn.addActionListener(e -> toggleSidebar());

        row.add(toggleBtn, BorderLayout.CENTER);
        return row;
    }

    // ── Toggle sidebar
    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        int targetW = sidebarExpanded ? SIDEBAR_W_OPEN : SIDEBAR_W_CLOSED;

        // Animate
        javax.swing.Timer anim = new javax.swing.Timer(8, null);
        anim.addActionListener(e -> {
            Dimension cur = sidebarPanel.getPreferredSize();
            int curW = cur.width;
            int step = sidebarExpanded ? 12 : -12;
            int newW = curW + step;

            boolean done = sidebarExpanded
                    ? newW >= targetW
                    : newW <= targetW;

            if (done) {
                newW = targetW;
                anim.stop();
            }

            sidebarPanel.setPreferredSize(new Dimension(newW, 0));
            sidebarPanel.revalidate();
            getContentPane().revalidate();
            getContentPane().repaint();

            // Afficher/cacher les labels selon la taille
            updateSidebarLabels(newW);
        });
        anim.start();
    }

    private void updateSidebarLabels(int width) {
        boolean show = width > 120;

        // Textes nav
        if (lblDashboard != null) lblDashboard.setVisible(show);
        if (lblPatients  != null) lblPatients.setVisible(show);
        if (lblLogs      != null) lblLogs.setVisible(show);

        // Section labels
        if (lblNavSection != null) lblNavSection.setVisible(show);
        if (lblSysSection != null) lblSysSection.setVisible(show);

        // Système labels
        if (lblMysql  != null) lblMysql.setVisible(show);
        if (lblJade   != null) lblJade.setVisible(show);
        if (lblAgents != null) lblAgents.setVisible(show);
        if (badgeMysql  != null) badgeMysql.setVisible(show);
        if (badgeJade   != null) badgeJade.setVisible(show);
        if (badgeAgents != null) badgeAgents.setVisible(show);

        // Logo text
        if (l1Logo != null) l1Logo.setVisible(show);
        if (l2Logo != null) l2Logo.setVisible(show);

        // Footer
        if (footerLabel != null) footerLabel.setVisible(show);

        // Toggle button text
        if (toggleBtn != null) {
            toggleBtn.setText(show ? "◀  Réduire" : "▶");
            toggleBtn.setHorizontalAlignment(
                    show ? SwingConstants.LEFT : SwingConstants.CENTER);
        }
    }

    // ── Nav item
    private JPanel navItem(String emoji, String label, String card) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_SIDEBAR);
        p.setMaximumSize(new Dimension(SIDEBAR_W_OPEN, 46));
        p.setMinimumSize(new Dimension(SIDEBAR_W_OPEN, 46));
        p.setPreferredSize(new Dimension(SIDEBAR_W_OPEN, 46));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, 20, 0, 0);

        JLabel icoL = new JLabel(emoji);
        icoL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        p.add(icoL, gc);

        gc.insets  = new Insets(0, 12, 0, 0);
        gc.weightx = 1.0;
        gc.fill    = GridBagConstraints.HORIZONTAL;
        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblL.setForeground(SIDEBAR_TEXT);
        p.add(lblL, gc);

        // Stocker la référence du label
        switch (card) {
            case "dashboard": lblDashboard = lblL; break;
            case "patients":  lblPatients  = lblL; break;
            case "logs":      lblLogs      = lblL; break;
        }

        // Tooltip quand sidebar fermé
        p.setToolTipText(label);

        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!SIDEBAR_ACTIVE.equals(p.getBackground())) {
                    p.setBackground(SIDEBAR_HOVER);
                    lblL.setForeground(Color.WHITE);
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!SIDEBAR_ACTIVE.equals(p.getBackground())) {
                    p.setBackground(BG_SIDEBAR);
                    lblL.setForeground(SIDEBAR_TEXT);
                }
            }
            @Override public void mouseClicked(MouseEvent e) {
                showPage(card, p, lblL);
            }
        });
        return p;
    }

    // ── Système rows
    private JPanel buildSysRow() {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setAlignmentX(LEFT_ALIGNMENT);

        String[][] data = {
                {"MySQL",  "Connecté"},
                {"JADE",   "Running"},
                {"Agents", "6 Actifs"}
        };
        Color[] colors = {ACCENT_GREEN, ACCENT_BLUE2, ACCENT_CYAN};
        JLabel[] svcLabels   = new JLabel[3];
        JLabel[] badgeLabels = new JLabel[3];

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            JPanel row = new JPanel(new GridBagLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(240, 32));
            row.setMinimumSize(new Dimension(240, 32));
            row.setPreferredSize(new Dimension(240, 32));
            row.setAlignmentX(LEFT_ALIGNMENT);

            GridBagConstraints gc = new GridBagConstraints();
            gc.anchor = GridBagConstraints.WEST;

            // Dot
            gc.insets = new Insets(0, 20, 0, 0);
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(colors[idx]);
                    g2.fillOval(0, (getHeight()-8)/2, 8, 8);
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(10, 10));
            row.add(dot, gc);

            // Service
            gc.insets  = new Insets(0, 10, 0, 0);
            gc.weightx = 1.0;
            gc.fill    = GridBagConstraints.HORIZONTAL;
            svcLabels[i] = new JLabel(data[i][0]);
            svcLabels[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            svcLabels[i].setForeground(SIDEBAR_TEXT);
            row.add(svcLabels[i], gc);

            // Badge
            gc.weightx = 0;
            gc.fill    = GridBagConstraints.NONE;
            gc.insets  = new Insets(0, 4, 0, 10);
            badgeLabels[i] = new JLabel(data[i][1]);
            badgeLabels[i].setFont(new Font("Segoe UI", Font.BOLD, 10));
            badgeLabels[i].setForeground(colors[i]);
            badgeLabels[i].setOpaque(true);
            badgeLabels[i].setBackground(new Color(
                    colors[i].getRed(),
                    colors[i].getGreen(),
                    colors[i].getBlue(), 35));
            badgeLabels[i].setBorder(
                    BorderFactory.createEmptyBorder(2, 7, 2, 7));
            row.add(badgeLabels[i], gc);

            container.add(row);
        }

        // Stocker références
        lblMysql    = svcLabels[0];
        lblJade     = svcLabels[1];
        lblAgents   = svcLabels[2];
        badgeMysql  = badgeLabels[0];
        badgeJade   = badgeLabels[1];
        badgeAgents = badgeLabels[2];

        return container;
    }

    // ── showPage
    private void showPage(String card, JPanel active, JLabel activeLabel) {
        for (JPanel n : new JPanel[]{navDashboard, navPatients, navLogs}) {
            if (n == null) continue;
            n.setBackground(BG_SIDEBAR);
            for (Component c : n.getComponents()) {
                if (c instanceof JLabel) {
                    JLabel l = (JLabel) c;
                    if (l.getText().length() > 3)
                        l.setForeground(SIDEBAR_TEXT);
                }
            }
        }
        if (active != null) {
            active.setBackground(SIDEBAR_ACTIVE);
            if (activeLabel != null)
                activeLabel.setForeground(Color.WHITE);
        }
        cardLayout.show(contentArea, card);
        if (card.equals("patients"))  tablePanel.refresh();
        if (card.equals("dashboard")) statsPanel.refresh();
        if (breadcrumb != null) {
            String[] cards = {"dashboard","patients","logs"};
            String[] names = {"Tableau de Bord","Patients","Logs Agents"};
            for (int i = 0; i < cards.length; i++)
                if (cards[i].equals(card)) breadcrumb.setText(names[i]);
        }
    }

    // ════════════════════════════════════════════════════
    //  TOP BAR
    // ════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x0D2137),
                        getWidth(), 0, new Color(0x1565C0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 62));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        // Left breadcrumb
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);
        JLabel app = new JLabel("HospitalSMA");
        app.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        app.setForeground(new Color(0x5B8BB8));
        JLabel sep = new JLabel("  ›  ");
        sep.setForeground(new Color(0x5B8BB8));
        sep.setFont(FONT_BODY);
        breadcrumb = new JLabel("Tableau de Bord");
        breadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        breadcrumb.setForeground(Color.WHITE);
        JPanel lw = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        lw.setOpaque(false);
        lw.add(app); lw.add(sep); lw.add(breadcrumb);

        // Right
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 14));
        right.setOpaque(false);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x112D4E));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        search.setOpaque(false);
        search.setPreferredSize(new Dimension(180, 32));
        JLabel sIco = new JLabel("🔍");
        sIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        JLabel sLbl = new JLabel("Rechercher...");
        sLbl.setFont(FONT_SMALL);
        sLbl.setForeground(new Color(0x5B8BB8));
        search.add(sIco); search.add(sLbl);

        JLabel date = new JLabel(
                new java.text.SimpleDateFormat("EEE dd MMM yyyy",
                        java.util.Locale.FRENCH).format(new java.util.Date()));
        date.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        date.setForeground(new Color(0x5B8BB8));

        JPanel chip = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 5)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x004D40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        chip.setOpaque(false);
        JLabel chipL = new JLabel("● JADE Running");
        chipL.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chipL.setForeground(new Color(0x4DB6AC));
        chip.add(chipL);

        JLabel clock = new JLabel();
        clock.setFont(new Font("Segoe UI", Font.BOLD, 15));
        clock.setForeground(Color.WHITE);
        javax.swing.Timer t = new javax.swing.Timer(1000, e ->
                clock.setText(new java.text.SimpleDateFormat("HH:mm:ss")
                        .format(new java.util.Date())));
        t.start();
        clock.setText(new java.text.SimpleDateFormat("HH:mm:ss")
                .format(new java.util.Date()));

        right.add(search);
        right.add(date);
        right.add(chip);
        right.add(clock);

        bar.add(lw,    BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════
    private JLabel sectionLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 9));
        l.setForeground(new Color(0x2E5B84));
        l.setMaximumSize(new Dimension(240, 20));
        l.setPreferredSize(new Dimension(240, 20));
        l.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel hLine() {
        JPanel p = new JPanel();
        p.setBackground(new Color(0x112D4E));
        p.setMaximumSize(new Dimension(240, 1));
        p.setMinimumSize(new Dimension(240, 1));
        p.setPreferredSize(new Dimension(240, 1));
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private Component gap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    public void addLog(String msg)  { if (logPanel   != null) logPanel.addLog(msg); }
    public void refreshStats()      { if (statsPanel  != null) statsPanel.refresh(); }
}