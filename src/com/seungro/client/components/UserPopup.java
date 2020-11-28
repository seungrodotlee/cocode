package com.seungro.client.components;

import com.seungro.client.utils.GlobalUtility;
import com.seungro.data.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UserPopup extends JPopupMenu {
    private GlobalUtility global;
    private UserButton parent;
    private ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();

    public UserPopup() {
        global = GlobalUtility.getInstance();
    }

    public void addItem(String name) {
        JMenuItem item = new JMenuItem(name);
        add(item);
        items.add(item);
        item.addActionListener(new Listener());
    }

    public void removeItem(String name) {
        for(int i = 0; i < items.size(); i++) {
            JMenuItem item = items.get(i);

            if(item.getText().equals(name)) {
                System.out.println("[CLIENT] remove popup item " + name);
                items.remove(i);
                remove(item);
                break;
            }
        }
    }

    public void show(Component comp, int x, int y) {
        super.show(comp, x, y);

        parent = (UserButton) comp;
    }

    private class Listener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            String selected = item.getText();

            if(selected == null) {
                return;
            }

            if(selected.equals("공유 허용")) {
                global.sendMessage(new Unit(Unit.LOG_DATA, parent.getUserName(), "accept_share", null));
            }
        }
    }
}
