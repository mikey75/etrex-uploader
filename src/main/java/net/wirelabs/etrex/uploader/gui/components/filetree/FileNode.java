package net.wirelabs.etrex.uploader.gui.components.filetree;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.tree.*;
import java.io.File;

/**
 * Created 9/14/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor
@Getter
@Setter
public class FileNode extends DefaultMutableTreeNode {
    
    private File file;
    private boolean isSystemRoot;
    private boolean isGarminSystemDrive;

    FileNode(Object a) {
        super(a);
    }

    FileNode(File file) {
        super(file);
        this.file = file;
    }

}
