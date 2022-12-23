package net.wirelabs.etrex.uploader.gui.strava.activities;

import net.wirelabs.etrex.uploader.common.utils.SystemUtils;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Created 12/23/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaActivityIconClickListener extends MouseAdapter {

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
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (column == table.getModel().findColumn("Title")) {
            Rectangle r = table.getCellRect(row, column, true);
            if (clickedOnIcon(e.getPoint(), r)) {
                String url = "https://www.strava.com/activities/" + table.getModel().getActivityAtRow(row).getId();
                try {
                    SystemUtils.openSystemBrowser(url);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }
    }

    private boolean clickedOnIcon(Point point, Rectangle r) {
        return (point.x >= r.x && point.x <= r.x + iconWidth) &&
                (point.y >= r.y && point.y <= r.y + iconHeight);
    }


}
