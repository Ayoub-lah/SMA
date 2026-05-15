package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.FileWriter;
import java.sql.*;
import database.DBConnection;

public class PatientTablePanel extends JPanel {

    private DefaultTableModel    model;
    private JTable               table;
    private JLabel               countLabel;
    private JTextField           searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public PatientTablePanel() {
        setBackground(HospitalDashboard.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);

        refresh();
    }

    // ── Header
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        // Title block
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Liste ");
        title.setFont(HospitalDashboard.FONT_TITLE);
        title.setForeground(HospitalDashboard.TEXT_DARK);
        countLabel = new JLabel("Chargement...");
        countLabel.setFont(HospitalDashboard.FONT_BODY);
        countLabel.setForeground(HospitalDashboard.TEXT_MID);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(countLabel);
        p.add(titleBlock, BorderLayout.WEST);

        // Right buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        // Search field
        searchField = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        searchField.setPreferredSize(new Dimension(220, 38));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(HospitalDashboard.TEXT_DARK);
        searchField.setOpaque(false);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, HospitalDashboard.BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        searchField.putClientProperty("JTextField.placeholderText",
                "🔍  Rechercher un patient...");

        // Search listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filterTable(); }
            public void removeUpdate(DocumentEvent e)  { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        JButton addBtn = buildBtn("＋  Nouveau Patient",
                HospitalDashboard.ACCENT_GREEN);
        addBtn.addActionListener(e -> {
            PatientFormDialog dlg = new PatientFormDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this));
            dlg.setVisible(true);
            if (dlg.isConfirmed()) {
                main.Main.sendPatient(
                        dlg.getNom(),
                        dlg.getConsultation(),
                        dlg.getPriorite()
                );
                JOptionPane.showMessageDialog(this,
                        "✓ Patient \"" + dlg.getNom() +
                                "\" envoyé aux agents JADE !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                javax.swing.Timer t =
                        new javax.swing.Timer(2000, ev -> refresh());
                t.setRepeats(false);
                t.start();
            }
        });

        JButton refreshBtn = buildBtn("↻  Actualiser",
                HospitalDashboard.ACCENT_BLUE);
        refreshBtn.addActionListener(e -> refresh());

        JButton exportBtn = buildBtn("📥  Export CSV",
                new Color(0x00897B));
        exportBtn.addActionListener(e -> exportCSV());

        JButton clearBtn = buildBtn("🗑  Vider BD",
                HospitalDashboard.ACCENT_RED);
        clearBtn.addActionListener(e -> clearDatabase());

        right.add(searchField);
        right.add(addBtn);
        right.add(refreshBtn);
        right.add(exportBtn);
        right.add(clearBtn);
        p.add(right, BorderLayout.EAST);

        return p;
    }

    // ── Table
    private JPanel buildTable() {
        String[] cols = {"#", "Nom", "Consultation", "Priorité", "Diagnostic"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(46);
        table.setFont(HospitalDashboard.FONT_BODY);
        table.setForeground(HospitalDashboard.TEXT_DARK);
        table.setBackground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xF3F4F6));
        table.setSelectionBackground(new Color(0xE3F2FD));
        table.setSelectionForeground(HospitalDashboard.TEXT_DARK);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setReorderingAllowed(false);

        // Row sorter pour la recherche
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xF0F4FF));
        header.setForeground(HospitalDashboard.TEXT_MID);
        header.setPreferredSize(new Dimension(0, 44));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                new Color(0xDDE6F0)));

        // Column widths
        int[] widths = {50, 140, 160, 110, 280};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer Priorité
        table.getColumnModel().getColumn(3).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object v, boolean sel,
                            boolean foc, int row, int col) {
                        boolean crit = "CRITIQUE".equalsIgnoreCase(
                                v == null ? "" : v.toString());
                        Color bg = sel ? new Color(0xE3F2FD)
                                : (row % 2 == 0 ? Color.WHITE : new Color(0xFAFBFF));

                        JPanel badge = new JPanel(
                                new FlowLayout(FlowLayout.CENTER, 0, 10));
                        badge.setBackground(bg);
                        JLabel b = new JLabel(v == null ? "" : v.toString());
                        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        b.setForeground(crit
                                ? HospitalDashboard.ACCENT_RED
                                : HospitalDashboard.ACCENT_GREEN);
                        b.setOpaque(true);
                        b.setBackground(crit
                                ? new Color(0xFFEBEE)
                                : new Color(0xE0F2F1));
                        b.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));
                        badge.add(b);
                        return badge;
                    }
                }
        );

        // Alternating rows renderer
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel,
                    boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0
                        ? Color.WHITE : new Color(0xFAFBFF));
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return this;
            }
        };
        for (int i = 0; i < model.getColumnCount(); i++)
            if (i != 3)
                table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new RoundedBorder(16, HospitalDashboard.BORDER_COLOR));
        card.add(scroll);
        return card;
    }

    // ── Filter
    private void filterTable() {
        String txt = searchField.getText().trim();
        if (txt.isEmpty()) {
            sorter.setRowFilter(null);
            countLabel.setText(model.getRowCount() + " patient(s)");
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt));
            countLabel.setText(table.getRowCount() +
                    " résultat(s) pour \"" + txt + "\"");
        }
    }

    // ── Refresh
    public void refresh() {
        model.setRowCount(0);
        Connection conn = DBConnection.connect();
        if (conn == null) {
            countLabel.setText("✗ Erreur de connexion");
            return;
        }
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT id, nom, consultation, priorite, diagnostic " +
                            "FROM patients ORDER BY id DESC"
            );
            int count = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("consultation"),
                        rs.getString("priorite"),
                        rs.getString("diagnostic")
                });
                count++;
            }
            countLabel.setText(count + " patient(s) enregistré(s)");
        } catch (Exception e) {
            countLabel.setText("✗ Erreur : " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(conn);
        }
    }

    // ── Clear DB
    private void clearDatabase() {
        int choice = JOptionPane.showOptionDialog(this,
                "Supprimer TOUS les patients ?\nCette action est irréversible.",
                "Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null,
                new String[]{"Oui, supprimer", "Annuler"}, "Annuler");

        if (choice == 0) {
            Connection conn = DBConnection.connect();
            if (conn == null) return;
            try {
                conn.createStatement().executeUpdate("DELETE FROM patients");
                conn.createStatement().executeUpdate(
                        "ALTER TABLE patients AUTO_INCREMENT = 1");
                refresh();
                JOptionPane.showMessageDialog(this,
                        "✓ Base de données vidée.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "✗ Erreur : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } finally {
                DBConnection.closeQuietly(conn);
            }
        }
    }

    // ── Export CSV
    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("patients_export.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Connection conn = DBConnection.connect();
        if (conn == null) return;
        try {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT * FROM patients ORDER BY id");
            FileWriter fw = new FileWriter(fc.getSelectedFile());
            fw.write("ID,Nom,Consultation,Priorité,Diagnostic\n");
            while (rs.next()) {
                fw.write(rs.getInt("id")             + "," +
                        rs.getString("nom")          + "," +
                        rs.getString("consultation") + "," +
                        rs.getString("priorite")     + "," +
                        rs.getString("diagnostic")   + "\n");
            }
            fw.close();
            JOptionPane.showMessageDialog(this,
                    "✓ Export : " + fc.getSelectedFile().getName(),
                    "CSV", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "✗ Erreur : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeQuietly(conn);
        }
    }

    // ── Button helper
    private JButton buildBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(155, 38));
        return btn;
    }
}