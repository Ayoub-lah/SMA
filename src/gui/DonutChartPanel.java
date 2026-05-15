package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class DonutChartPanel extends JPanel {

    private int critique = 0;
    private int normal   = 0;

    public DonutChartPanel() {
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(300, 220));
        setMinimumSize(new Dimension(150, 150));
        setDoubleBuffered(true);
    }

    public void setData(int critique, int normal) {
        this.critique = critique;
        this.normal   = normal;
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        int legendH = 30;
        int availH  = h - legendH - 20;
        int size    = Math.min(w - 40, availH);
        if (size <= 0) return;

        int cx = w / 2;
        int cy = (availH) / 2 + 10;
        int x  = cx - size / 2;
        int y  = cy - size / 2;

        int strokeW = Math.max(18, size / 6);
        int inner   = size - strokeW * 2;
        int ix      = cx - inner / 2;
        int iy      = cy - inner / 2;

        int total = critique + normal;

        if (total == 0) {
            g2.setColor(new Color(0xE2E8F0));
            g2.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.drawOval(x + strokeW/2, y + strokeW/2, inner, inner);

            g2.setColor(new Color(0x94A3B8));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            String msg = "Aucun";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, cx - fm.stringWidth(msg)/2, cy + 6);
            return;
        }

        double critAngle   = 360.0 * critique / total;
        double normalAngle = 360.0 - critAngle;

        // Shadow ring
        g2.setColor(new Color(0, 0, 0, 12));
        g2.setStroke(new BasicStroke(strokeW + 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.drawOval(x + strokeW/2 + 2, y + strokeW/2 + 2, inner, inner);

        // Normal arc (green)
        g2.setColor(new Color(0x10B981));
        g2.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.drawArc(x + strokeW/2, y + strokeW/2, inner, inner,
                90, -(int)normalAngle);

        // Critique arc (red)
        g2.setColor(new Color(0xEF4444));
        g2.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.drawArc(x + strokeW/2, y + strokeW/2, inner, inner,
                (int)(90 - critAngle), -(int)critAngle == 0 ? -1 : (int)critAngle);

        // Center: total number
        g2.setColor(new Color(0x1E293B));
        g2.setFont(new Font("Segoe UI", Font.BOLD, size / 5));
        String totalStr = String.valueOf(total);
        FontMetrics fm1 = g2.getFontMetrics();
        g2.drawString(totalStr, cx - fm1.stringWidth(totalStr)/2, cy + fm1.getAscent()/2 - 4);

        g2.setColor(new Color(0x94A3B8));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm2 = g2.getFontMetrics();
        String sub = "patients";
        g2.drawString(sub, cx - fm2.stringWidth(sub)/2, cy + fm1.getAscent()/2 + 14);

        // Legend
        int legendY = h - legendH + 8;
        int legendX = cx - 110;

        drawDot(g2, legendX, legendY, new Color(0xEF4444));
        g2.setColor(new Color(0x64748B));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int pctCrit = (int)(100.0 * critique / total);
        g2.drawString("Critique " + pctCrit + "%", legendX + 14, legendY + 4);

        drawDot(g2, legendX + 110, legendY, new Color(0x10B981));
        int pctNorm = 100 - pctCrit;
        g2.drawString("Normal " + pctNorm + "%", legendX + 124, legendY + 4);
    }

    private void drawDot(Graphics2D g2, int x, int y, Color c) {
        g2.setStroke(new BasicStroke(1));
        g2.setColor(c);
        g2.fillOval(x, y - 5, 10, 10);
    }
}