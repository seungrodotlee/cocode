package com.seungro.client.components;

import com.seungro.client.utils.GlobalUtility;
import com.seungro.data.Unit;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SidebarPopup extends JPopupMenu {
    private GlobalUtility global;
    private JTree tree;
    private IconNode parent;
    private ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();

    public SidebarPopup() {
        global = GlobalUtility.getInstance();
    }

    public void addItem(String name) {
        JMenuItem item = new JMenuItem(name);
        add(item);
        items.add(item);
        item.addActionListener(new listener());
    }

    public void show(Component comp, IconNode node, int x, int y) {
        show(comp, x, y);

        tree = (JTree) comp;
        parent = node;
    }

    class listener implements ActionListener {
        String selected;
        DefaultTreeModel model;
        TreePath selectionPath;
        JTabbedPane tab = global.getMainTabPane();

        @Override
        public void actionPerformed(ActionEvent event) {
            JMenuItem item = (JMenuItem) event.getSource();
            selected = item.getText();
            model = (DefaultTreeModel) tree.getModel();

            if(selected == null) {
                return;
            }

            if(selected.equals("새 파일")) {
                FileNode newFile = new FileNode("새문서");
                global.attachNode(newFile, parent);
            }

            if(selected.equals("새 폴더")) {
                FolderNode newFolder = new FolderNode("새폴더");
                global.attachNode(newFolder, parent);
            }

            if(selected.equals("새 파일") ||selected.equals("새 폴더") || selected.equals("이름 수정")) {
                tree.setEditable(true);
                selectionPath = tree.getSelectionPath();
                tree.startEditingAtPath(selectionPath);
            }

            if(selected.equals("삭제")) {
                global.deleteNode(parent);

            }
        }






    }
}
