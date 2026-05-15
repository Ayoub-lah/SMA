package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class BarChartPanel extends JPanel {

    // Mode simple (critique/normal)
    private int critique = 0;
    private int normal   = 0;

    // Mode consultation
    private Map<String, Integer> consultData = null;

    public BarChartPanel() {
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(300, 230));
        setMinimumSize(new Dimension(150, 150));
        setDoubleBuffered(true);
    }

    public void setData(int critique, int normal) {
        this.critique    = critique;
        this.normal      = normal;
        this.consultData = null;
        revalidate();
        repaint();
    }

    public void setConsultData(Map<String, Integer> data) {
        this.consultData = data;
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (consultData != null && !consultData.isEmpty())
            drawConsultChart((Graphics2D) g);
        else
            drawSimpleChart((Graphics2D) g);
    }

    // ── Chart simple Critique / Normal
    private void drawSimpleChart(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int padL = 50, padR = 20, padT = 30, padB = 50;
        int chartW = w - padL - padR;
        int chartH = h - padT - padB;
        if (chartW <= 0 || chartH <= 0) return;

        int max = Math.max(Math.max(critique, normal), 1);
        drawGrid(g2, padL, padT, chartW, chartH, max, 4);

        String[] labels = {"Critique", "Normal"};
        int[]    values = {critique, normal};
        Color[]  colors = {new Color(0xC62828), new Color(0x00897B)};
        Color[]  lights = {new Color(0xFFEBEE), new Color(0xE0F2F1)};

        int groupW = chartW / 2;
        int barW   = Math.max(30, (int)(groupW * 0.5));

        for (int i = 0; i < 2; i++) {
            int barH = values[i] == 0 ? 4
                    : (int)((double) values[i] / max * chartH);
            int bx = padL + i * groupW + (groupW - barW) / 2;
            int by = padT + chartH - barH;

            // Background
            g2.setColor(lights[i]);
            g2.fillRoundRect(bx, padT, barW, chartH, 8, 8);

            // Bar gradient
            GradientPaint gp = new GradientPaint(
                    bx, by, colors[i], bx, by + barH, colors[i].darker());
            g2.setPaint(gp);
            g2.fillRoundRect(bx, by, barW, barH, 8, 8);

            // Value
            g2.setColor(colors[i]);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String v = String.valueOf(values[i]);
            g2.drawString(v,
                    bx + (barW - fm.stringWidth(v)) / 2, by - 8);

            // Label
            g2.setColor(new Color(0x546E7A));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm2 = g2.getFontMetrics();
            g2.drawString(labels[i],
                    bx + (barW - fm2.stringWidth(labels[i])) / 2,
                    padT + chartH + 20);
        }
        drawAxes(g2, padL, padT, chartW, chartH);
    }

    // ── Chart par consultation
    private void drawConsultChart(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int padL = 50, padR = 10, padT = 30, padB = 60;
        int chartW = w - padL - padR;
        int chartH = h - padT - padB;
        if (chartW <= 0 || chartH <= 0) return;

        String[] keys   = consultData.keySet().toArray(new String[0]);
        int[]    values = consultData.values().stream()
                .mapToInt(Integer::intValue).toArray();
        int n   = keys.length;
        int max = Arrays.stream(values).max().orElse(1);

        // Palette de couleurs
        Color[] palette = {
                new Color(0x1565C0), new Color(0x00897B),
                new Color(0xC62828), new Color(0xEF6C00),
                new Color(0x6A1B9A), new Color(0x0097A7),
                new Color(0x2E7D32), new Color(0xAD1457),
                new Color(0x4527A0), new Color(0x00695C)
        };

        drawGrid(g2, padL, padT, chartW, chartH, max, 4);

        int groupW = chartW / Math.max(n, 1);
        int barW   = Math.max(16, Math.min(40, (int)(groupW * 0.6)));

        for (int i = 0; i < n; i++) {
            Color c   = palette[i % palette.length];
            int barH  = values[i] == 0 ? 4
                    : (int)((double) values[i] / max * chartH);
            int bx    = padL + i * groupW + (groupW - barW) / 2;
            int by    = padT + chartH - barH;

            // Light bg
            Color light = new Color(c.getRed(), c.getGreen(), c.getBlue(), 30);
            g2.setColor(light);
            g2.fillRoundRect(bx, padT, barW, chartH, 6, 6);

            // Bar
            GradientPaint gp = new GradientPaint(
                    bx, by, c, bx, by + barH, c.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(bx, by, barW, barH, 6, 6);

            // Value
            g2.setColor(c);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            String v = String.valueOf(values[i]);
            g2.drawString(v,
                    bx + (barW - fm.stringWidth(v)) / 2, by - 6);

            // Label rotatif
            g2.setColor(new Color(0x546E7A));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            Graphics2D g2r = (Graphics2D) g2.create();
            g2r.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int lx = bx + barW / 2;
            int ly = padT + chartH + 14;
            g2r.translate(lx, ly);
            g2r.rotate(Math.toRadians(-35));

            // Raccourcir le label si trop long
            String lbl = keys[i].length() > 10
                    ? keys[i].substring(0, 9) + "." : keys[i];
            g2r.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2r.setColor(new Color(0x546E7A));
            g2r.drawString(lbl, 0, 0);
            g2r.dispose();
        }

        drawAxes(g2, padL, padT, chartW, chartH);
    }

    // ── Grid helper
    private void drawGrid(Graphics2D g2, int padL, int padT,
                          int chartW, int chartH, int max, int lines) {
        for (int i = 0; i <= lines; i++) {
            int y = padT + chartH - (i * chartH / lines);
            g2.setColor(new Color(0xF0F4FF));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(padL, y, padL + chartW, y);

            g2.setColor(new Color(0x90A4AE));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String val = String.valueOf(i * max / lines);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(val, padL - fm.stringWidth(val) - 6, y + 4);
        }
    }

    // ── Axes helper
    private void drawAxes(Graphics2D g2, int padL, int padT,
                          int chartW, int chartH) {
        g2.setColor(new Color(0xDDE6F0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(padL, padT, padL, padT + chartH);
        g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);
    }
}