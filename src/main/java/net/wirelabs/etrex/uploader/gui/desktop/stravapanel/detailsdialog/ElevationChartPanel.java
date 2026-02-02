package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.List;

public class ElevationChartPanel extends BasePanel {

    private List<Float> elevations = Collections.emptyList();

    private static final Color BLACK_ALPHA60 = new Color(0,0,0,60);
    private static final Color BLACK_ALPHA170 = new Color(0, 0, 0, 170);
    private static final Color WHITISH = new Color(220,220,220);
    private static final Color GRADIENT_HIGH = new Color(100, 150, 255, 160);
    private static final Color GRADIENT_LOW = new Color(100, 150, 255, 40);

    private int hoverIndex = -1;
    private float minElevation;
    private float maxElevation;

    private static final int PADDING = 30;
    private static final int GRID_LINES = 5;

    public ElevationChartPanel() {
        super("Elevation profile");

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e.getX());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverIndex = -1;
                repaint();
            }
        });
    }

    private void updateHover(int mouseX) {
        if (elevations.isEmpty()) {
            return;
        }

        int usableWidth = getWidth() - 2 * PADDING;
        if (usableWidth <= 0) {
            return;
        }

        double xStep = usableWidth / (double) (elevations.size() - 1);
        int index = Math.round((mouseX - PADDING) / (float) xStep);

        index = Math.max(0, Math.min(elevations.size() - 1, index));

        if (index != hoverIndex) {
            hoverIndex = index;
            repaint();
        }
    }
    public void populate(List<Float> elevations) {
        if (elevations == null || elevations.isEmpty()) {
            this.elevations = Collections.emptyList();
            repaint();
            return;
        }

        this.elevations = elevations;
        computeMinMax();
        repaint();
    }

    private void computeMinMax() {
        minElevation = elevations.stream().min(Float::compare).orElse(0f);
        maxElevation = elevations.stream().max(Float::compare).orElse(0f);

        // Prevent flat-line divide-by-zero
        if (Float.compare(minElevation, maxElevation) == 0) {
            maxElevation = minElevation + 1f;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (elevations.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int usableWidth = width - 2 * PADDING;
        int usableHeight = height - 2 * PADDING;

        int n = elevations.size();
        if (n < 2) {
            g2.dispose();
            return;
        }


        // draw grid y-axis
        g2.setColor(WHITISH);
        g2.setFont(getFont().deriveFont(10f));

        for (int i = 0; i <= GRID_LINES; i++) {
            float ratio = i / (float) GRID_LINES;
            int y = PADDING + Math.round(ratio * usableHeight);

            // horizontal grid line
            g2.drawLine(PADDING, y, PADDING + usableWidth, y);

            // elevation label
            float value = maxElevation - ratio * (maxElevation - minElevation);
            String label = String.format("%.0f", value);

            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(label, PADDING - labelWidth - 5, y + 4);
            g2.setColor(WHITISH);
        }


        // draw grid x-axis
        int verticalLines = Math.min(n - 1, 10);
        double xStepGrid = usableWidth / (double) verticalLines;

        for (int i = 0; i <= verticalLines; i++) {
            int x = PADDING + (int) (i * xStepGrid);
            g2.drawLine(x, PADDING, x, PADDING + usableHeight);
        }

        // gradient fill under the graph
        double xStep = usableWidth / (double) (n - 1);

        Path2D.Float area = new Path2D.Float();

        // start at bottom-left
        int yBottom = PADDING + usableHeight;
        area.moveTo(PADDING, yBottom);

        // follow elevation line
        for (int i = 0; i < n; i++) {
            int x = PADDING + (int) (i * xStep);
            int y = mapElevationToY(elevations.get(i), usableHeight);
            area.lineTo(x, y);
        }

        // close shape at bottom-right
        int xEnd = PADDING + usableWidth;
        area.lineTo(xEnd, yBottom);
        area.closePath();

        // gradient (top → bottom)
        LinearGradientPaint gradient = new LinearGradientPaint(
                0, PADDING,
                0, (float) PADDING + usableHeight,
                new float[]{0f, 1f},
                new Color[]{GRADIENT_HIGH,GRADIENT_LOW}
        );

        g2.setPaint(gradient);
        g2.fill(area);


        // top elevation line
        g2.setColor(Color.BLUE);

        for (int i = 0; i < n - 1; i++) {
            int x1 = PADDING + (int) (i * xStep);
            int x2 = PADDING + (int) ((i + 1) * xStep);

            int y1 = mapElevationToY(elevations.get(i), usableHeight);
            int y2 = mapElevationToY(elevations.get(i + 1), usableHeight);

            g2.drawLine(x1, y1, x2, y2);
        }

        // altitude hover indicator
        if (hoverIndex >= 0 && hoverIndex < elevations.size()) {
            usableHeight = getHeight() - 2 * PADDING;
            xStep = (getWidth() - 2 * PADDING) / (double) (elevations.size() - 1);

            int x = PADDING + (int) (hoverIndex * xStep);
            int y = mapElevationToY(elevations.get(hoverIndex), usableHeight);

            // vertical guide line
            g2.setColor(BLACK_ALPHA60);
            g2.drawLine(x, PADDING, x, PADDING + usableHeight);

            // dot
            g2.setColor(Color.RED);
            g2.fillOval(x - 4, y - 4, 8, 8);

            // tooltip box
            String text = String.format("%.1f m", elevations.get(hoverIndex));
            FontMetrics fm = g2.getFontMetrics();

            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            int boxX = x + 8;
            int boxY = y - textHeight - 8;

            // keep tooltip inside panel
            if (boxX + textWidth + 8 > getWidth()) {
                boxX = x - textWidth - 16;
            }
            if (boxY < PADDING) {
                boxY = y + 8;
            }

            g2.setColor(BLACK_ALPHA170);
            g2.fillRoundRect(boxX, boxY, textWidth + 8, textHeight + 4, 8, 8);

            g2.setColor(Color.WHITE);
            g2.drawString(text, boxX + 4, boxY + fm.getAscent() + 2);
        }

        // axis border
        g2.setColor(Color.GRAY);
        g2.drawRect(PADDING, PADDING, usableWidth, usableHeight);
        g2.dispose();
    }

    private int mapElevationToY(float elevation, int usableHeight) {
        float normalized = (elevation - minElevation) / (maxElevation - minElevation);
        // invert Y-axis
        return PADDING + Math.round((1f - normalized) * usableHeight);
    }
}