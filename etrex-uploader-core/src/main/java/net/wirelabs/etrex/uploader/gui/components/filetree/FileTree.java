package net.wirelabs.etrex.uploader.gui.components.filetree;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.map.MapUtil;
import net.wirelabs.etrex.uploader.hardware.threads.ThreadUtils;


import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class FileTree extends JTree {

    private DefaultTreeModel model;
    @Setter
    private List<File> roots;
    private UploadDialog uploadDialog;

    public FileTree withUploadDialog(UploadDialog uploadDialog) {
        this.uploadDialog = uploadDialog;
        addPopupMenu();
        return this;
    }

    public FileTree(List<File> roots) {
        setRoots(roots);
        loadModel();

        FileTreeCellRenderer renderer = new FileTreeCellRenderer();
        addTreeExpansionListener(new DirExpansionListener());
        addTreeSelectionListener(new DirSelectionListener());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        setCellRenderer(renderer);
        setRootVisible(false);
        setShowsRootHandles(false);
        setEditable(false);

    }

    public void loadModel() {

        FileNode treeTop = new FileNode();
        for (File root : roots) {
            FileNode fileNode = new FileNode(root);
            fileNode.isSystemRoot = true;
            treeTop.add(fileNode);
            expandNode(fileNode);
        }

        model = new DefaultTreeModel(treeTop);
        setModel(model);

    }

    @Override
    public TreeModel getModel() {
        return model;
    }

    private void expandNode(FileNode thisNode) {

        thisNode.removeAllChildren();
        List<File> files = listFiles(thisNode);

        if (files.isEmpty()) {
            return;
        }

        for (File f : files) {
            FileNode fileNode = new FileNode(f);
            thisNode.add(fileNode);
            if (nodeHasContent(fileNode)) fileNode.add(new FileNode(new Boolean(true)));
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
        }
    }

    private static class DirSelectionListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent event) {
            FileNode node = (FileNode) event.getPath().getLastPathComponent();
            MapUtil.drawTrackFromSelectedFileNode(node);
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
            // compare two pathnames lexicographically
            return o1.compareTo(o2);
        }
    }

    private void addPopupMenu() {

        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Upload to Strava");

        menuItem.addActionListener(e -> {
            FileNode node = (FileNode) getLastSelectedPathComponent();
            log.info("Uploading {}", node.getFile().getPath());
            uploadDialog.setTrackFile(node.getFile());
            uploadDialog.clearInputAndStatus();
            uploadDialog.setVisible(true);

        });

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
                        menu.add(menuItem);
                        menu.show(FileTree.this, pathBounds.x, pathBounds.y + pathBounds.height);
                    }
                }
            }
        });
    }
}

