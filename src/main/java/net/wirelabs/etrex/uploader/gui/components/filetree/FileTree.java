package net.wirelabs.etrex.uploader.gui.components.filetree;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.*;

@Slf4j
public class FileTree extends JTree {

    private final FileTreeCellRenderer renderer = new FileTreeCellRenderer();
    private final FileNode treeTop  = new FileNode();
    private final DefaultTreeModel model= new DefaultTreeModel(treeTop);

    public FileTree() {
        setModel(model);
        addTreeExpansionListener(new DirExpansionListener()); // this is responsible for 'interacting with file tree' 
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setRootVisible(false);
        setShowsRootHandles(false);
        setEditable(false);
        setCellRenderer(renderer);

    }

    public void addDrive(File drive) {
        FileNode fileNode = new FileNode(drive);
        fileNode.setSystemRoot(true);
        treeTop.add(fileNode);
        expandNode(fileNode);
        model.reload();
    }

    public void removeDrive(File drive) {
        List<TreeNode> roots = getRootNodes();
        for (TreeNode t: roots) {
            FileNode fn = (FileNode) t;
            if (fn.getFile().getPath().equals(drive.getPath())) {
                treeTop.remove(fn);
            }
        }
        model.reload();
    }

    public void addPopupMenu(JPopupMenu popupMenu) {

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tryPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                tryPopup(e);
            }

            private void tryPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    TreePath path = getPathForLocation(e.getX(), e.getY());
                    Rectangle pathBounds = getUI().getPathBounds(FileTree.this, path);
                    if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
                        popupMenu.show(FileTree.this, pathBounds.x, pathBounds.y + pathBounds.height);
                    }
                }
            }
        });
    }
    
    public List<TreeNode> getRootNodes() {
        FileNode topNode = (FileNode) treeModel.getRoot();
        return Collections.list(topNode.children());
    }

    private void expandNode(FileNode nodeToExpand) {

        nodeToExpand.removeAllChildren();
        List<File> files = listFiles(nodeToExpand);

        if (files.isEmpty()) {
            return;
        }

        for (File f : files) {
            FileNode fileNode = new FileNode(f);
            nodeToExpand.add(fileNode);
            if (nodeHasContent(fileNode)){
                fileNode.add(new FileNode());
            } 
        }

    }

    private boolean nodeHasContent(FileNode node) {
        return !listFiles(node).isEmpty();
    }

    private List<File> listFiles(FileNode node) {

        File[] files = node.getFile().listFiles();
        if (files != null && node.getFile().isDirectory()) {
            Arrays.sort(files, new DirectoriesFirstComparator());
            return Arrays.asList(files);
        } else {
            return new ArrayList<>();
        }

    }
    // Make sure expansion is threaded and updating the tree model
    // only occurs within the event dispatching thread.
    private class DirExpansionListener implements TreeExpansionListener {
        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            final FileNode node = (FileNode) event.getPath().getLastPathComponent();
            // run system thread for node expansion,
            // but gui update (model.reload) in EDT
            ThreadUtils.runAsync(() -> {
                if (node != null) {
                    expandNode(node);
                    SwingUtilities.invokeLater(() -> model.reload(node));
                }
            });

        }
        public void treeCollapsed(TreeExpansionEvent event) {
            // nothing
        }

    }
    private static class DirectoriesFirstComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            if (o1.isDirectory() && !o2.isDirectory()) {
                // directory before non-directory.
                return -1;
            }
            if (!o1.isDirectory() && o2.isDirectory()) {
                // non-directory after directory
                return 1;
            }
            // compare two path names lexicographically
            return o1.compareTo(o2);
        }

    }
}

