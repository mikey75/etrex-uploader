package net.wirelabs.etrex.uploader.gui.components.filetree;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

/**
 * Created 9/14/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor
public class FileNode extends DefaultMutableTreeNode {
    @Getter
    private File file;
    boolean isSystemRoot;

    FileNode(Object a) {
        super(a);
    }

    FileNode(File file) {
        super(file);
        this.file = file;
    }

}
