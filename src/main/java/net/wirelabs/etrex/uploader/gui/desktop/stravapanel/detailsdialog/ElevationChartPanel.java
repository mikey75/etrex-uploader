package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class ElevationChartPanel extends JPanel {

    private List<Float> elevations = Collections.emptyList();

    private float minElevation;
    private float maxElevation;

    public ElevationChartPanel() {
        setBorder(new TitledBorder("Elevation profile"));
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
        int padding = 20;

        int usableWidth = width - 2 * padding;
        int usableHeight = height - 2 * padding;

        int n = elevations.size();
        if (n < 2) {
            g2.dispose();
            return;
        }

        double xStep = usableWidth / (double) (n - 1);

        g2.setColor(Color.BLUE);

        for (int i = 0; i < n - 1; i++) {
            int x1 = padding + (int) (i * xStep);
            int x2 = padding + (int) ((i + 1) * xStep);

            int y1 = mapElevationToY(elevations.get(i), usableHeight, padding);
            int y2 = mapElevationToY(elevations.get(i + 1), usableHeight, padding);

            g2.drawLine(x1, y1, x2, y2);
        }

        g2.dispose();
    }

    private int mapElevationToY(float elevation, int usableHeight, int padding) {
        float normalized =
                (elevation - minElevation) / (maxElevation - minElevation);

        // invert Y-axis
        return padding + Math.round((1f - normalized) * usableHeight);
    }
}