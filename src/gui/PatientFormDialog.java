package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class PatientFormDialog extends JDialog {

    private JTextField        nomField;
    private JComboBox<String> consultBox;
    private JComboBox<String> prioBox;
    private boolean confirmed = false;

    private static final String[] CONSULTATIONS = {
            "Cardiologie", "Neurologie", "Orthopédie",
            "Pédiatrie",   "Dermatologie", "Pneumologie",
            "Gastroentérologie", "Ophtalmologie", "Urgence", "Générale"
    };

    public PatientFormDialog(Frame parent) {
        super(parent, "Nouveau Patient", true);
        setSize(560, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ── Header gradient bleu
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0,          new Color(0x0A2342),
                        getWidth(), 0, new Color(0x1565C0)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, 90));
        p.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        // Icon circle
        JPanel iconCircle = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(52, 52));
        JLabel ico = new JLabel("🏥", SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconCircle.add(ico);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Nouveau Patient");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Enregistrement dans le système JADE");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x7BB3D6));
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(sub);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 19));
        row.setOpaque(false);
        row.add(iconCircle);
        row.add(text);
        p.add(row, BorderLayout.WEST);
        return p;
    }

    // ── Form fields
    private JPanel buildForm() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(28, 34, 12, 34));

        // ── Nom
        p.add(fieldLabel("👤  Nom complet du patient"));
        p.add(Box.createVerticalStrut(8));
        nomField = new JTextField();
        nomField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nomField.setForeground(new Color(0x0D1B2A));
        nomField.setBackground(new Color(0xF4F8FF));
        nomField.setCaretColor(new Color(0x1565C0));
        nomField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(0xDDE6F0)),
                BorderFactory.createEmptyBorder(13, 16, 13, 16)
        ));
        nomField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        nomField.setAlignmentX(LEFT_ALIGNMENT);
        nomField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                nomField.setBackground(new Color(0xEBF4FF));
                nomField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(10, new Color(0x1565C0)),
                        BorderFactory.createEmptyBorder(13, 16, 13, 16)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                nomField.setBackground(new Color(0xF4F8FF));
                nomField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(10, new Color(0xDDE6F0)),
                        BorderFactory.createEmptyBorder(13, 16, 13, 16)
                ));
            }
        });
        p.add(nomField);
        p.add(Box.createVerticalStrut(22));

        // ── Consultation
        p.add(fieldLabel("🏥  Service de consultation"));
        p.add(Box.createVerticalStrut(8));
        consultBox = new JComboBox<>(CONSULTATIONS);
        styleCombo(consultBox, new Color(0x1565C0));
        p.add(consultBox);
        p.add(Box.createVerticalStrut(22));

        // ── Priorité
        p.add(fieldLabel("⚡  Niveau de priorité"));
        p.add(Box.createVerticalStrut(8));
        prioBox = new JComboBox<>(new String[]{"NORMAL", "CRITIQUE"});
        styleCombo(prioBox, new Color(0x00897B));
        prioBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(
                        list, value, index, sel, focus);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                if (!sel) {
                    setBackground(Color.WHITE);
                    setForeground("CRITIQUE".equals(value)
                            ? new Color(0xC62828)
                            : new Color(0x00897B));
                } else {
                    setBackground(new Color(0x1565C0));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });
        p.add(prioBox);
        p.add(Box.createVerticalStrut(22));

        // ── Info box
        JPanel info = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xE3F2FD));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        info.setOpaque(false);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        info.setAlignmentX(LEFT_ALIGNMENT);
        info.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        JPanel infoInner = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 14));
        infoInner.setOpaque(false);
        JLabel infoIco = new JLabel("ℹ");
        infoIco.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoIco.setForeground(new Color(0x1565C0));
        JLabel infoL = new JLabel(
                "Le patient sera traité automatiquement par les 6 agents JADE");
        infoL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoL.setForeground(new Color(0x1565C0));
        infoInner.add(infoIco);
        infoInner.add(infoL);
        info.add(infoInner, BorderLayout.CENTER);
        p.add(info);

        return p;
    }

    // ── Footer
    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 16));
        p.setBackground(new Color(0xF4F8FF));
        p.setPreferredSize(new Dimension(0, 70));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(0xDDE6F0)));

        // Annuler
        JButton cancel = new JButton("Annuler") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                        ? new Color(0xDDE6F0) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancel.setForeground(new Color(0x546E7A));
        cancel.setContentAreaFilled(false);
        cancel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(0xDDE6F0)),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));
        cancel.setFocusPainted(false);
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.setPreferredSize(new Dimension(120, 42));
        cancel.addActionListener(e -> dispose());

        // Envoyer
        JButton send = new JButton("🏥  Envoyer Patient") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0,          new Color(0x1565C0),
                        getWidth(), 0, new Color(0x1E88E5)
                );
                g2.setPaint(getModel().isRollover()
                        ? new GradientPaint(0, 0, new Color(0x0D47A1),
                        getWidth(), 0, new Color(0x1565C0))
                        : gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        send.setFont(new Font("Segoe UI", Font.BOLD, 13));
        send.setForeground(Color.WHITE);
        send.setContentAreaFilled(false);
        send.setBorderPainted(false);
        send.setFocusPainted(false);
        send.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        send.setPreferredSize(new Dimension(175, 42));
        send.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty()) {
                nomField.setBackground(new Color(0xFFEBEE));
                nomField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(10, new Color(0xC62828)),
                        BorderFactory.createEmptyBorder(13, 16, 13, 16)
                ));
                JOptionPane.showMessageDialog(this,
                        "⚠  Veuillez saisir le nom du patient.",
                        "Champ requis", JOptionPane.WARNING_MESSAGE);
                nomField.requestFocus();
                return;
            }
            confirmed = true;
            dispose();
        });

        p.add(cancel);
        p.add(send);
        return p;
    }

    // ── Helpers
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(0x0D1B2A));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void styleCombo(JComboBox<String> cb, Color accentColor) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setForeground(new Color(0x0D1B2A));
        cb.setBackground(new Color(0xF4F8FF));
        cb.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(0xDDE6F0)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cb.setAlignmentX(LEFT_ALIGNMENT);
        cb.setFocusable(false);
    }

    public boolean isConfirmed()     { return confirmed; }
    public String  getNom()          { return nomField.getText().trim(); }
    public String  getConsultation() { return (String) consultBox.getSelectedItem(); }
    public String  getPriorite()     { return (String) prioBox.getSelectedItem(); }
}