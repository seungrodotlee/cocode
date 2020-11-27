package com.seungro.client.elements;

import com.seungro.client.components.PopupPanel;
import com.seungro.client.components.SidebarIcon;
import com.seungro.client.utils.ColorPack;
import com.seungro.client.utils.GlobalUtility;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
    private GlobalUtility global;
    private JButton titleBar;
    private JScrollPane mainPane;
    private JPanel chatPane;
    private JTextArea messageInput;

    public ChatPanel() {
        global = GlobalUtility.getInstance();
        setLayout(new BorderLayout());
        titleBar = new JButton("채팅", new SidebarIcon("keyboard_arrow_down", Color.WHITE).imageIcon());
        mainPane = new JScrollPane();
        chatPane = new JPanel();
        messageInput = new JTextArea();

        titleBar.setHorizontalAlignment(SwingConstants.LEFT);
        titleBar.setForeground(Color.WHITE);
        titleBar.setBackground(ColorPack.BG);
        titleBar.setPreferredSize(new Dimension(1, 36));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));
        chatPane.setLayout(new BoxLayout(chatPane, BoxLayout.Y_AXIS));
        mainPane.add(chatPane);
        mainPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));

        add(messageInput, BorderLayout.PAGE_END);
        add(titleBar, BorderLayout.PAGE_START);
        add(mainPane, BorderLayout.CENTER);
    }

    protected void receiveMessage(String val) {
        chatPane.add(new JLabel(val));
    }

    protected void sendMessage(String val) {
        chatPane.add(new JLabel(val));
    }

    public JButton getToggler() {
        return titleBar;
    }

    public JScrollPane getMainPane() {
        return mainPane;
    }
}
