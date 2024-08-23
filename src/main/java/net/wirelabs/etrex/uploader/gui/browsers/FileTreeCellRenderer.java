package net.wirelabs.etrex.uploader.gui.browsers;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.Objects;

import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        
        Icon iconDir = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/folder.png")));
        Icon iconRegularFile = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/unknownMime.png")));
        Icon iconDisk = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/drive-harddisk.png")));
        Icon iconGarmin = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/garmin-triangle.png")));
        
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
                if (node.isGarminSystemDrive()) {
                    icon = iconGarmin;
                } else {
                    icon = node.isSystemRoot() ? iconDisk : iconDir;
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
