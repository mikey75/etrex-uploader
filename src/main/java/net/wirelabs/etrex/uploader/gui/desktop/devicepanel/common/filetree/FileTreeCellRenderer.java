package net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.filetree;


import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;

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

        if (node.getFile() != null) {

            File userObject = node.getFile();

            if (userObject.isDirectory()) {
                if (node.isGarminSystemDrive()) {
                    icon = iconGarmin;
                } else {
                    icon = node.isSystemRoot() ? iconDisk : iconDir;
                }
            } else {
                icon = iconRegularFile;
            }
            result.setIcon(icon);
            result.setText(!userObject.getName().isEmpty() ? userObject.getName() : userObject.getAbsolutePath());
        }
        return result;
    }
}
