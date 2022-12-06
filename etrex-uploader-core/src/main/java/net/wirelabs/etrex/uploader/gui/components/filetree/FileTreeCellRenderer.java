package net.wirelabs.etrex.uploader.gui.components.filetree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        
        Icon iconDir = new ImageIcon(getClass().getResource("/icons/places/folder.png"));
        Icon iconRegularFile = new ImageIcon(getClass().getResource("/icons/mimetypes/unknown.png"));
        Icon iconDisk = new ImageIcon(getClass().getResource("/icons/devices/drive-harddisk.png"));
        Icon iconGarmin = new ImageIcon(getClass().getResource("/icons/garmin-triangle.png"));
        FileNode node = (FileNode) value;
        Icon icon;

        JLabel result = (JLabel) super.getTreeCellRendererComponent(tree,
                node, sel, expanded, leaf, row, hasFocus);

        result.setIcon(iconDir); // default icon

        if (node.getUserObject() instanceof Boolean) {
            setText("Retrieving data...");
            result.setIcon(iconRegularFile);
        }

        if (node.getFile() != null) {

            File userobject = node.getFile();

            if (userobject.isDirectory()) {
                if (node.isGarminSystemDrive) {
                    icon = iconGarmin;
                } else {
                    icon = node.isSystemRoot ? iconDisk : iconDir;
                }
            } else {
                icon = iconRegularFile;
            }
            result.setIcon(icon);
            result.setText(userobject.getName().length() > 0 ? userobject.getName() : userobject.getAbsolutePath());
        }
        return result;
    }
}
