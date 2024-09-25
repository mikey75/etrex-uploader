package net.wirelabs.etrex.uploader.gui.strava.activities;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created 12/23/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class StravaActivityIconClickListener extends MouseAdapter implements MouseMotionListener {

    private final ActivitiesTable table;
    private final int iconWidth;
    private final int iconHeight;

    public StravaActivityIconClickListener(ActivitiesTable table, int iconWidth, int iconHeight) {
        this.table = table;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (mouseOverIcon(e.getPoint())) {
            String activityId = String.valueOf(table.getModel().getActivityAtRow(table.getSelectedRow()).getId());
            String url = Constants.STRAVA_ACTIVITY_URL+ "/" + activityId;

            try {
                SystemUtils.openSystemBrowser(url);
            } catch (IOException ex) {
                log.error("Couldn't open this strava activity in browser");
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        Point p = new Point(e.getX(), e.getY());
        JComponent comp = (JComponent) table.getComponentAt(p);
        if (mouseOverIcon(p)) {
            table.setCursor(new Cursor(Cursor.HAND_CURSOR));
            comp.setToolTipText("Click to view this activity on Strava!");
        } else {
            table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            comp.setToolTipText(null);
        }
    }

    private boolean mouseOverIcon(Point point) {
        int row = table.rowAtPoint(point);
        int column = table.columnAtPoint(point);
        if (column == table.getModel().findColumn("Title")) {
            Rectangle r = table.getCellRect(row, column, true);
            return (point.x >= r.x && point.x <= r.x + iconWidth) &&
                    (point.y >= r.y && point.y <= r.y + iconHeight);
        }
        return false;
    }


}
