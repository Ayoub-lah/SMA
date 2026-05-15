package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogPanel extends JPanel {

    private JTextPane   logArea;
    private StringBuilder html;

    public LogPanel() {
        setBackground(HospitalDashboard.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildLogArea(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Logs des Agents JADE");
        title.setFont(HospitalDashboard.FONT_TITLE);
        title.setForeground(HospitalDashboard.TEXT_DARK);

        JLabel sub = new JLabel(
                "Activité en temps réel de tous les agents du système");
        sub.setFont(HospitalDashboard.FONT_BODY);
        sub.setForeground(HospitalDashboard.TEXT_MID);

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);
        p.add(left, BorderLayout.WEST);

        JButton clearBtn = new JButton("🗑  Effacer les Logs") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = HospitalDashboard.ACCENT_RED;
                g2.setColor(getModel().isRollover() ? c.darker() : c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        clearBtn.setContentAreaFilled(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.setPreferredSize(new Dimension(175, 40));
        clearBtn.addActionListener(e -> clearLogs());
        p.add(clearBtn, BorderLayout.EAST);

        return p;
    }

    private JPanel buildLogArea() {
        // Stats bar
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0,          new Color(0x0A2342),
                        getWidth(), 0, new Color(0x1565C0)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        statsBar.setOpaque(false);
        statsBar.setPreferredSize(new Dimension(0, 46));

        for (String[] item : new String[][]{
                {"👤", "PatientAgent",   "#4FC3F7"},
                {"🩺", "DoctorAgent",    "#81C784"},
                {"🚨", "EmergencyAgent", "#EF9A9A"},
                {"💊", "PharmacyAgent",  "#FFB74D"},
                {"🔧", "AdminAgent",     "#CE93D8"},
                {"📅", "RDVAgent",       "#80DEEA"}
        }) {
            JLabel l = new JLabel(item[0] + " " + item[1]);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            try {
                l.setForeground(Color.decode(item[2]));
            } catch (Exception ex) {
                l.setForeground(Color.WHITE);
            }
            statsBar.add(l);
        }

        // Log area
        logArea = new JTextPane();
        logArea.setContentType("text/html");
        logArea.setEditable(false);
        logArea.setBackground(new Color(0x060F1A));

        html = new StringBuilder(
                "<html><body style='font-family:Consolas,monospace;" +
                        "font-size:12px;background:#060F1A;padding:12px;'>"
        );
        logArea.setText(html + "</body></html>");

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0x060F1A));
        card.setBorder(new RoundedBorder(16, new Color(0x1565C0)));
        card.add(statsBar,  BorderLayout.NORTH);
        card.add(scroll,    BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(card);
        return outer;
    }

    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String color = "#64748B", bg = "#0D1B2A", prefix = "ℹ";

            String up = message.toUpperCase();
            if (up.contains("URGENCE") || up.contains("CRITIQUE")) {
                color = "#EF9A9A"; bg = "#1A0A0A"; prefix = "🚨";
            } else if (up.contains("PATIENT") || up.contains("DEMANDE")) {
                color = "#4FC3F7"; bg = "#0A1929"; prefix = "👤";
            } else if (up.contains("DATABASE") || up.contains("MYSQL")
                    || up.contains("SAUVEGARDÉ")) {
                color = "#CE93D8"; bg = "#12082A"; prefix = "🗄️";
            } else if (up.contains("DOCTOR") || up.contains("DIAGNOSTIC")
                    || up.contains("ORDONNANCE")) {
                color = "#81C784"; bg = "#0A1A0A"; prefix = "🩺";
            } else if (up.contains("PHARMACY")
                    || up.contains("MÉDICAMENT")) {
                color = "#FFB74D"; bg = "#1A1000"; prefix = "💊";
            } else if (up.contains("ADMIN")) {
                color = "#80CBC4"; bg = "#0A1A18"; prefix = "🔧";
            } else if (up.contains("RDV")) {
                color = "#80DEEA"; bg = "#001A20"; prefix = "📅";
            }

            html.append(String.format(
                    "<div style='margin:3px 0;padding:8px 12px;" +
                            "background:%s;border-left:3px solid %s;" +
                            "border-radius:6px;'>" +
                            "<span style='color:#2E5B84;font-size:10px;'>%s</span>" +
                            "&nbsp;&nbsp;" +
                            "<span style='font-size:13px;'>%s</span>" +
                            "&nbsp;" +
                            "<span style='color:%s;'>%s</span>" +
                            "</div>",
                    bg, color, time, prefix, color, message
            ));
            logArea.setText(html + "</body></html>");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void clearLogs() {
        html = new StringBuilder(
                "<html><body style='font-family:Consolas,monospace;" +
                        "font-size:12px;background:#060F1A;padding:12px;'>"
        );
        logArea.setText(html + "</body></html>");
    }
}