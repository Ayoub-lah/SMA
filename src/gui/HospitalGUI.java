package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HospitalGUI extends JFrame {

    // ─── Palette de couleurs ───────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(10, 15, 30);
    private static final Color BG_CARD      = new Color(18, 25, 45);
    private static final Color BG_INPUT     = new Color(25, 35, 60);
    private static final Color ACCENT_BLUE  = new Color(56, 139, 253);
    private static final Color ACCENT_TEAL  = new Color(29, 158, 117);
    private static final Color ACCENT_RED   = new Color(220, 53, 69);
    private static final Color ACCENT_AMBER = new Color(255, 193, 7);
    private static final Color TEXT_PRIMARY = new Color(230, 237, 243);
    private static final Color TEXT_MUTED   = new Color(110, 130, 160);
    private static final Color BORDER_COLOR = new Color(35, 50, 80);

    // ─── Polices ───────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  18);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 12);

    // ─── Composants ────────────────────────────────────────────────────────────
    private JTextArea          logArea;
    private JTextArea          urgLog;
    private JTable             patientTable;
    private DefaultTableModel  tableModel;
    private JLabel             statTotal, statCritique, statEnAttente, statResolus;
    private JTextField         fieldNom, fieldConsultation;
    private JComboBox<String>  comboPriorite;
    private JLabel             statusBar;
    private JPanel             urgencePanel;

    // ─── Compteurs ─────────────────────────────────────────────────────────────
    private int totalPatients = 0;
    private int casCritiques  = 0;
    private int casResolus    = 0;

    // ══════════════════════════════════════════════════════════════════════════
    // Constructeur
    // ══════════════════════════════════════════════════════════════════════════
    public HospitalGUI() {
        initWindow();
        buildUI();
    }

    // ─── Initialisation fenetre ────────────────────────────────────────────────
    private void initWindow() {
        setTitle("HospitalSMA - Systeme de Gestion Medicale");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    // ─── Construction UI principale ────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BARRE SUPERIEURE
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(0, 56));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        left.setOpaque(false);

        JLabel cross = new JLabel("+");
        cross.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cross.setForeground(ACCENT_BLUE);

        JLabel title = new JLabel("HospitalSMA");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);

        JLabel version = new JLabel("v1.0");
        version.setFont(FONT_SMALL);
        version.setForeground(TEXT_MUTED);

        left.add(cross);
        left.add(title);
        left.add(version);

        // Horloge temps reel
        JLabel clock = new JLabel();
        clock.setFont(FONT_BODY);
        clock.setForeground(TEXT_MUTED);
        clock.setBorder(new EmptyBorder(0, 0, 0, 20));
        new Timer(1000, e ->
                clock.setText(new SimpleDateFormat("HH:mm:ss  |  dd/MM/yyyy").format(new Date()))
        ).start();

        bar.add(left,  BorderLayout.WEST);
        bar.add(clock, BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ZONE CENTRALE
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(14, 14, 0, 14));

        JPanel topRow = new JPanel(new BorderLayout(12, 0));
        topRow.setOpaque(false);
        topRow.add(buildStatsPanel(),   BorderLayout.WEST);
        topRow.add(buildFormPanel(),    BorderLayout.CENTER);
        topRow.add(buildUrgencePanel(), BorderLayout.EAST);

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 12, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(buildTablePanel());
        bottomRow.add(buildLogPanel());

        center.add(topRow,    BorderLayout.NORTH);
        center.add(bottomRow, BorderLayout.CENTER);
        return center;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PANNEAU STATISTIQUES
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(320, 130));

        statTotal     = buildStatLabel("0");
        statCritique  = buildStatLabel("0");
        statEnAttente = buildStatLabel("0");
        statResolus   = buildStatLabel("0");

        panel.add(wrapStatCard(statTotal,     "Total patients", ACCENT_BLUE));
        panel.add(wrapStatCard(statCritique,  "Cas critiques",  ACCENT_RED));
        panel.add(wrapStatCard(statEnAttente, "En attente",     ACCENT_AMBER));
        panel.add(wrapStatCard(statResolus,   "Resolus",        ACCENT_TEAL));
        return panel;
    }

    private JLabel buildStatLabel(String value) {
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        return val;
    }

    private JPanel wrapStatCard(JLabel val, String label, Color accent) {
        val.setForeground(accent);
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 3, 0, 0, accent),
                new EmptyBorder(8, 10, 8, 10)
        ));
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FORMULAIRE PATIENT
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setBackground(BG_CARD);
        outer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel title = new JLabel("Nouvelle demande medicale");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);

        JPanel fields = new JPanel(new GridLayout(1, 3, 10, 0));
        fields.setOpaque(false);

        fieldNom          = styledField("ex: Ayoub Lahlaibi");
        fieldConsultation = styledField("ex: Cardiologie");
        comboPriorite     = new JComboBox<>(new String[]{"NORMALE", "URGENTE", "CRITIQUE"});
        styleCombo(comboPriorite);

        fields.add(labelWrap("Nom du patient", fieldNom));
        fields.add(labelWrap("Specialite",     fieldConsultation));
        fields.add(labelWrap("Priorite",       comboPriorite));

        JButton btnReset   = buildButton("Reinitialiser",   BG_INPUT);
        JButton btnEnvoyer = buildButton("Envoyer demande", ACCENT_BLUE);
        btnReset.addActionListener(e -> resetForm());
        btnEnvoyer.addActionListener(e -> envoyerDemande());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnReset);
        btnRow.add(btnEnvoyer);

        outer.add(title,   BorderLayout.NORTH);
        outer.add(fields,  BorderLayout.CENTER);
        outer.add(btnRow,  BorderLayout.SOUTH);
        return outer;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(TEXT_MUTED);
                    g.setFont(FONT_SMALL);
                    g.drawString(placeholder, 8, getHeight() / 2 + 4);
                }
            }
        };
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT_BLUE);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return f;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(FONT_BODY);
        combo.setBorder(new LineBorder(BORDER_COLOR, 1));
    }

    private JPanel labelWrap(String text, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        p.add(lbl,  BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JButton buildButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            final Color orig = bg;
            public void mouseEntered(MouseEvent e) { btn.setBackground(orig.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(orig); }
        });
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PANNEAU URGENCES
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildUrgencePanel() {
        urgencePanel = new JPanel(new BorderLayout(0, 8));
        urgencePanel.setBackground(BG_CARD);
        urgencePanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 3, 0, 0, ACCENT_RED),
                new EmptyBorder(10, 12, 10, 12)
        ));
        urgencePanel.setPreferredSize(new Dimension(210, 0));

        JLabel title = new JLabel("[!] Urgences actives");
        title.setFont(FONT_HEADER);
        title.setForeground(ACCENT_RED);

        urgLog = new JTextArea("Aucune urgence active.");
        urgLog.setBackground(BG_CARD);
        urgLog.setForeground(ACCENT_RED);
        urgLog.setFont(FONT_SMALL);
        urgLog.setEditable(false);
        urgLog.setLineWrap(true);
        urgLog.setWrapStyleWord(true);

        urgencePanel.add(title,  BorderLayout.NORTH);
        urgencePanel.add(urgLog, BorderLayout.CENTER);
        return urgencePanel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TABLEAU PATIENTS
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(new LineBorder(BORDER_COLOR, 1));

        JLabel title = new JLabel("  Liste des patients");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        title.setOpaque(true);
        title.setBackground(BG_CARD);
        title.setBorder(new EmptyBorder(10, 6, 10, 0));

        String[] cols = {"#", "Nom", "Consultation", "Priorite", "Statut", "Heure"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        patientTable = new JTable(tableModel);
        styleTable(patientTable);

        JScrollPane scroll = new JScrollPane(patientTable);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(title,  BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setBackground(BG_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(BG_INPUT);
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_MUTED);
        header.setFont(FONT_SMALL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Renderer base (toutes colonnes)
        DefaultTableCellRenderer baseRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? BG_INPUT : BG_DARK);
                setForeground(TEXT_PRIMARY);
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(0, 8, 0, 0));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(baseRenderer);

        // Renderer colonne Priorite (coloree)
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String v = String.valueOf(val);
                setBackground(sel ? BG_INPUT : BG_DARK);
                switch (v) {
                    case "CRITIQUE": setForeground(ACCENT_RED);   break;
                    case "URGENTE":  setForeground(ACCENT_AMBER); break;
                    default:         setForeground(ACCENT_TEAL);  break;
                }
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(new EmptyBorder(0, 8, 0, 0));
                return this;
            }
        });

        // Renderer colonne Statut (coloree)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String v = String.valueOf(val);
                setBackground(sel ? BG_INPUT : BG_DARK);
                if      (v.contains("Urgence")) setForeground(ACCENT_RED);
                else if (v.contains("Traite"))  setForeground(ACCENT_TEAL);
                else                            setForeground(ACCENT_AMBER);
                setFont(FONT_SMALL);
                setBorder(new EmptyBorder(0, 8, 0, 0));
                return this;
            }
        });

        // Largeurs colonnes
        int[] widths = {30, 130, 140, 85, 120, 80};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PANNEAU LOGS
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(new LineBorder(BORDER_COLOR, 1));

        JPanel logHeader = new JPanel(new BorderLayout());
        logHeader.setBackground(BG_CARD);
        logHeader.setBorder(new EmptyBorder(10, 10, 8, 10));

        JLabel title = new JLabel("Logs systeme - temps reel");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);

        JButton btnClear = buildButton("Effacer", BG_INPUT);
        btnClear.setFont(FONT_SMALL);
        btnClear.setBorder(new EmptyBorder(4, 10, 4, 10));
        btnClear.addActionListener(e -> logArea.setText(""));

        logHeader.add(title,    BorderLayout.WEST);
        logHeader.add(btnClear, BorderLayout.EAST);

        // Zone terminal
        logArea = new JTextArea();
        logArea.setBackground(new Color(8, 12, 24));
        logArea.setForeground(new Color(80, 200, 120));
        logArea.setFont(FONT_MONO);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(8, 12, 24));

        panel.add(logHeader, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BARRE DE STATUT
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(0, 26));

        statusBar = new JLabel("  Systeme pret - JADE Multi-Agent Platform");
        statusBar.setFont(FONT_SMALL);
        statusBar.setForeground(TEXT_MUTED);

        JLabel jade = new JLabel("  JADE actif    MySQL actif  ");
        jade.setFont(FONT_SMALL);
        jade.setForeground(ACCENT_TEAL);

        bar.add(statusBar, BorderLayout.WEST);
        bar.add(jade,      BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LOGIQUE METIER
    // ══════════════════════════════════════════════════════════════════════════
    private void envoyerDemande() {
        String nom          = fieldNom.getText().trim();
        String consultation = fieldConsultation.getText().trim();
        String priorite     = (String) comboPriorite.getSelectedItem();

        if (nom.isEmpty() || consultation.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean critique = "CRITIQUE".equals(priorite);
        boolean urgente  = "URGENTE".equals(priorite);
        String  statut   = critique ? "Urgence !!!" : urgente ? "En traitement" : "Traite";
        String  heure    = new SimpleDateFormat("HH:mm:ss").format(new Date());

        totalPatients++;
        tableModel.addRow(new Object[]{totalPatients, nom, consultation, priorite, statut, heure});

        if (critique)     casCritiques++;
        else if (!urgente) casResolus++;
        updateStats();

        addLog("[" + heure + "] Demande envoyee - " + nom + " | " + consultation + " | " + priorite);

        if (critique) {
            addLog("[!] CAS CRITIQUE DETECTE - EmergencyAgent active !");
            addLog("   -> Ambulance envoyee");
            addLog("   -> Medecin urgent notifie");
            addLog("   -> AdminAgent : sauvegarde MySQL en cours...");
            updateUrgence(nom + " (" + consultation + ")  " + heure);
            setStatus("URGENCE : " + nom + " - EmergencyAgent actif !", ACCENT_RED);
        } else {
            addLog("   -> RDVAgent      : patient oriente vers DoctorAgent");
            addLog("   -> DoctorAgent   : diagnostic etabli");
            addLog("   -> PharmacyAgent : ordonnance preparee");
            addLog("   -> AdminAgent    : dossier sauvegarde (MySQL)");
            setStatus("Patient " + nom + " pris en charge avec succes.", ACCENT_TEAL);
        }

        scrollLogToBottom();
        resetForm();
    }

    private void updateStats() {
        statTotal.setText(String.valueOf(totalPatients));
        statCritique.setText(String.valueOf(casCritiques));
        statEnAttente.setText(String.valueOf(Math.max(0, totalPatients - casResolus - casCritiques)));
        statResolus.setText(String.valueOf(casResolus));
    }

    private void updateUrgence(String info) {
        if (urgLog.getText().equals("Aucune urgence active.")) {
            urgLog.setText("-> " + info);
        } else {
            urgLog.setText(urgLog.getText() + "\n-> " + info);
        }
    }

    private void resetForm() {
        fieldNom.setText("");
        fieldConsultation.setText("");
        comboPriorite.setSelectedIndex(0);
    }

    private void setStatus(String msg, Color color) {
        statusBar.setText("  " + msg);
        statusBar.setForeground(color);
    }

    private void scrollLogToBottom() {
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // API PUBLIQUE - appelee par les agents JADE
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Ajoute un message dans la console de logs.
     * Thread-safe : peut etre appele depuis n'importe quel agent JADE.
     */
    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            scrollLogToBottom();
        });
    }

    /**
     * Ajoute un patient dans le tableau.
     * Appele par AdminAgent apres sauvegarde MySQL.
     */
    public void addPatient(String nom, String consultation, String priorite, String statut) {
        SwingUtilities.invokeLater(() -> {
            totalPatients++;
            String heure = new SimpleDateFormat("HH:mm:ss").format(new Date());
            tableModel.addRow(new Object[]{totalPatients, nom, consultation, priorite, statut, heure});
            if ("CRITIQUE".equals(priorite))     casCritiques++;
            else if ("NORMALE".equals(priorite)) casResolus++;
            updateStats();
        });
    }

    /**
     * Met a jour la barre de statut depuis un agent.
     * Appele par n'importe quel agent JADE.
     */
    public void setStatusFromAgent(String msg, boolean isError) {
        SwingUtilities.invokeLater(() ->
                setStatus(msg, isError ? ACCENT_RED : ACCENT_TEAL)
        );
    }

    /**
     * Affiche une urgence dans le panneau dedie.
     * Appele par EmergencyAgent.
     */
    public void notifyUrgence(String info) {
        SwingUtilities.invokeLater(() -> updateUrgence(info));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // POINT D'ENTREE STANDALONE (test sans JADE)
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HospitalGUI gui = new HospitalGUI();
            gui.setVisible(true);
            gui.addLog("[SYSTEME] HospitalSMA demarre avec succes");
            gui.addLog("[SYSTEME] JADE Main Container initialise");
            gui.addLog("[AGENT ] PatientAgent      -> pret");
            gui.addLog("[AGENT ] RDVAgent          -> pret");
            gui.addLog("[AGENT ] DoctorAgent       -> pret");
            gui.addLog("[AGENT ] PharmacyAgent     -> pret");
            gui.addLog("[AGENT ] AdminAgent        -> pret");
            gui.addLog("[AGENT ] EmergencyAgent    -> pret");
            gui.addLog("[DB    ] MySQL hospital_sma -> connecte");
            gui.addLog("-------------------------------------------");
            gui.addLog("[INFO  ] Systeme operationnel. En attente de patients...");
        });
    }
}