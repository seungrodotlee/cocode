package com.seungro.client.elements;

import com.seungro.client.components.ChatBubble;
import com.seungro.client.components.PopupPanel;
import com.seungro.client.components.SidebarIcon;
import com.seungro.client.utils.ColorPack;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.data.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class ChatPanel extends JPanel {
    private int count = 0;
    private GlobalUtility global;
    private JButton titleBar;
    private JScrollPane mainPane;
    private JTextArea chatPane;
    private JPanel sendPane;
    private JTextArea messageInput;
    private JButton sendBtn;

    public ChatPanel() {
        global = GlobalUtility.getInstance();
        titleBar = new JButton("채팅", new SidebarIcon("keyboard_arrow_down", Color.WHITE).imageIcon());
        mainPane = new JScrollPane();
        chatPane = new JTextArea();
        sendPane = new JPanel();
        messageInput = new JTextArea();
        sendBtn = new JButton("전송");

        titleBar.setHorizontalAlignment(SwingConstants.LEFT);
        titleBar.setForeground(Color.WHITE);
        titleBar.setBackground(ColorPack.BG);
        titleBar.setPreferredSize(new Dimension(1, 36));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));
        chatPane.setEditable(false);
        chatPane.setWrapStyleWord(true);
        chatPane.setLineWrap(true);
        mainPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPane.setViewportView(chatPane);
        mainPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));
        sendPane.setLayout(new BorderLayout());
        sendPane.add(messageInput, BorderLayout.CENTER);
        sendPane.add(sendBtn, BorderLayout.LINE_END);
        sendBtn.addActionListener(new SendBtnListener());

        setLayout(new BorderLayout());
        add(sendPane, BorderLayout.PAGE_END);
        add(titleBar, BorderLayout.PAGE_START);
        add(mainPane, BorderLayout.CENTER);

        global.setChatPane(this);
    }

    private void sendMessage(String val) {
        appendMessage("나", val);
        global.sendMessage(new Unit(Unit.CHAT_DATA, global.getUserName(), "", val));
    }

    public void appendMessage(String sender, String val) {
        chatPane.append("[" + sender + "]\n" + val + "\n");
    }

    public JButton getToggler() {
        return titleBar;
    }

    public JScrollPane getMainPane() {
        return mainPane;
    }

    private class SendBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage(messageInput.getText());
            messageInput.setText("");
        }
    }
}
