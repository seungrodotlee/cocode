package com.seungro.client.elements;

import com.seungro.client.utils.GlobalUtility;
import com.seungro.client.utils.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class UserListPanel extends JPanel {
    private GlobalUtility global;
    private GridBagLayout grid;
    private GridBagConstraints con;

    public UserListPanel() {
        global = GlobalUtility.getInstance();
        grid = new GridBagLayout();
        con = new GridBagConstraints();
        con.fill = GridBagConstraints.BOTH;
        con.weightx = 1.0;
        setLayout(grid);
    }

    public void addUser(User u) {
        int order = global.getUserMap().size();
        if(order == 0) {
            u.setAuth(true);
        }

        UserButton userButton = new UserButton(u);
        u.setBtn(userButton);
        global.addUser(u);

        con.gridx = 0;
        con.gridy = order;
        grid.setConstraints(userButton, con);
        add(userButton);
    }
}