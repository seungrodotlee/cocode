package com.seungro.client.elements;

import com.seungro.client.components.*;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.data.Unit;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.UUID;

public class Sidebar extends JPanel {
    private GlobalUtility global;

    private JTree tree;
    private FolderNode root;
    private RootPopup rootPopup = new RootPopup();
    private FolderPopup folderPopup = new FolderPopup();
    private FilePopup filePopup = new FilePopup();
    private MouseListener ml;
    private String editingNodeName;

    public Sidebar() {
        global = GlobalUtility.getInstance();

        setLayout(new BorderLayout());

        root = new FolderNode("Project");
        root.setUUID(UUID.fromString("b34c3519-085c-452d-b58b-c45b3bba60de"));

        tree = new JTree(root);
        tree.setCellRenderer(new IconNodeRenderer());
        tree.setCellEditor(new CellEditor(tree, (DefaultTreeCellRenderer) tree.getCellRenderer()));
        tree.expandRow(0);
        tree.addMouseListener(new TreeMouseListener());

        global.setTree(tree);

        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.addTreeModelListener(new ModelListener());

        add(tree);
        setPreferredSize(new Dimension(180, 1));
    }

    class IconNodeRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean sel, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                    row, hasFocus);

            Icon icon = ((IconNode) value).getIcon();

            if (icon == null) {
                Hashtable icons = (Hashtable) tree.getClientProperty("JTree.icons");
                String name = ((IconNode) value).getIconName();
                if ((icons != null) && (name != null)) {
                    icon = (Icon) icons.get(name);
                    if (icon != null) {
                        setIcon(icon);
                    }
                }
            } else {
                setIcon(icon);
            }

            return this;
        }
    }

    class CellEditor extends DefaultTreeCellEditor {
        public CellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
        }

        @Override
        public Component getTreeCellEditorComponent(JTree tree,
                                                    Object value, boolean isSelected, boolean expanded,
                                                    boolean leaf, int row) {
            System.out.println("editing ");
            editingNodeName = (String) ((IconNode) value).getUserObject();
            return super.getTreeCellEditorComponent(tree, value, isSelected, expanded,leaf, row);
        }
    }

    class TreeMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int selRow = tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
            tree.setSelectionPath(selPath);
            if (selRow > -1) {
                tree.setSelectionRow(selRow);
            }

            IconNode selected = (IconNode) tree.getLastSelectedPathComponent();

            if(selected != null) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if(!global.amIAuth()) {
                        return;
                    }

                    int nodeType = selected.getNodeType();
                    if(nodeType == IconNode.FOLDER_NODE) {
                        if(selected == root) {
                            rootPopup.show(tree, selected, e.getX(), e.getY());
                        } else {
                            folderPopup.show(tree, selected, e.getX(), e.getY());
                        }
                    }

                    if(nodeType == IconNode.FILE_NODE) {
                        filePopup.show(tree, selected, e.getX(), e.getY());
                    }

                    System.out.println(selected.getNodeType());
                }

                if(selected.getNodeType() == IconNode.FILE_NODE) {
                    if (e.getClickCount() == 2 && !e.isConsumed()) {
                        e.consume();

                        global.setCurrentFile(global.getTabMap().get(selected));
                        global.getFileData(global.getTabMap().get(selected));
                    }
                }
            }
        }
    }

    class ModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            IconNode node;
            node = (IconNode) (e.getTreePath().getLastPathComponent());

            try {
                int index = e.getChildIndices()[0];
                node = (IconNode) (node.getChildAt(index));
            } catch (NullPointerException exc) {
            }

            if(editingNodeName == null) return;
            String newKey = (String) node.getUserObject();

            System.out.println("node = " + editingNodeName + ", new name = " + node);
            IconNode parent = global.findParentNode(node);
            System.out.println("parent = " + parent);

            for(int i = 0; i < parent.getChildCount(); i++) {
                IconNode child = (IconNode) parent.getChildAt(i);

                if(child.equals(node)) continue;

                if(child.getUserObject().equals(node.getUserObject())) {
                    System.out.println("이름 중복!");

                    node.setUserObject(editingNodeName);
                    tree.setEditable(false);
                    return;
                }
            }

            if(node.getNodeType() == IconNode.FILE_NODE) {
                global.updateFileName(node, editingNodeName, newKey);
                tree.setEditable(false);
            }
        }

        public void treeNodesInserted(TreeModelEvent e) {
        }

        public void treeNodesRemoved(TreeModelEvent e) {
        }

        public void treeStructureChanged(TreeModelEvent e) {
        }
    }


}
