package com.seungro.client.elements;

import com.github.fcannizzaro.material.icons.IconMaterial;
import com.seungro.client.components.SidebarIcon;
import com.seungro.client.utils.ColorPack;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.client.utils.User;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatLogPanel extends JSplitPane {
    private Boolean chatOpened = true;
    private Boolean logOpened = true;
    private LogPanel logPanel;
    private ChatPanel chatPanel;
    private UserListPanel listPanel;

    public ChatLogPanel() {
        super(JSplitPane.VERTICAL_SPLIT);

        listPanel = new UserListPanel();
        logPanel = new LogPanel();
        chatPanel = new ChatPanel();
        logPanel.getToggler().addActionListener(new ToggleListener());
        chatPanel.getToggler().addActionListener(new ToggleListener());
        setTopComponent(logPanel);
        setBottomComponent(chatPanel);
        setDividerLocation(0.5);
        setDividerSize(0);
        setResizeWeight(0.5);
        setBackground(ColorPack.BG);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, ColorPack.BG_DARK));
        setPreferredSize(new Dimension(320, 1));
    }

    private class ToggleListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton target = (JButton) e.getSource();
            String title = target.getText();

            if(title.equals("로그")) {
                logOpened = !logOpened;
                logPanel.getMainPane().setVisible(logOpened);

                if(logOpened) {
                    setDividerLocation(0.5);
                    target.setIcon(new SidebarIcon("keyboard_arrow_down", Color.WHITE).imageIcon());
                } else {
                    setDividerLocation(36);
                    target.setIcon(new SidebarIcon("keyboard_arrow_right", Color.WHITE).imageIcon());
                }
            }

            if(title.equals("채팅")) {
                chatOpened = !chatOpened;
                chatPanel.getMainPane().setVisible(chatOpened);

                if(chatOpened) {
                    target.setIcon(new SidebarIcon("keyboard_arrow_down", Color.WHITE).imageIcon());
                } else {
                    target.setIcon(new SidebarIcon("keyboard_arrow_right", Color.WHITE).imageIcon());
                }
            }

            JSplitPane parent = (JSplitPane) getParent();
            if(!logOpened && !chatOpened) {
                parent.setDividerLocation(parent.getWidth() - 80);
            } else {
                parent.setDividerLocation(parent.getWidth() - 320);
            }
        }
    }
}
