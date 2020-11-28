package com.seungro.client.elements;

import com.seungro.client.components.UserButton;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.client.utils.User;

import javax.swing.*;
import java.awt.*;

public class UserListPanel extends JPanel {
    private GlobalUtility global;
    private GridBagLayout grid;
    private GridBagConstraints con;

    public UserListPanel() {
        global = GlobalUtility.getInstance();
        global.setUserListPanel(this);
        grid = new GridBagLayout();
        con = new GridBagConstraints();
        con.fill = GridBagConstraints.BOTH;
        con.weightx = 1.0;
        setLayout(grid);
    }

    public void addUser(User u) {
        int order = global.getUserMap().size();
        UserButton userButton = new UserButton(u);
        u.setBtn(userButton);
        global.addUser(u);

        con.anchor = GridBagConstraints.NORTHWEST;
        con.gridx = 0;
        con.gridy = order;
        grid.setConstraints(userButton, con);
        add(userButton);
    }
}
