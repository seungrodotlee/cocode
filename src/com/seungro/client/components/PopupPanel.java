package com.seungro.client.components;

import javax.swing.*;
import java.awt.*;

public class PopupPanel extends JPanel {
    public final static int CHAT = 0;
    public final static int LOG = 1;

    private JLabel titleBar;
    private JScrollPane mainPane;
    private JTextArea textArea;
    private JPanel chatPane;
    private JTextArea messageInput;

    public PopupPanel(int type) {
        titleBar = new JLabel();
        mainPane = new JScrollPane();
        textArea = new JTextArea();
        chatPane = new JPanel();
        messageInput = new JTextArea();
        chatPane.setLayout(new BoxLayout(chatPane, BoxLayout.Y_AXIS));

        if(type == CHAT) {
            titleBar.setText("채팅");
            mainPane.add(chatPane);
            add(messageInput, BorderLayout.PAGE_END);
        }

        if(type == LOG) {
            titleBar.setText("로그");
            mainPane.add(textArea);
        }

        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.PAGE_START);
        add(mainPane, BorderLayout.CENTER);
    }

    protected void setText(String val) {
        textArea.setText(val);
    }

    protected void addText(String val) {
        textArea.setText(textArea.getText() + "\n" + val);
    }

    protected String getText() {
        return textArea.getText();
    }

    protected void receiveMessage(String val) {
        chatPane.add(new JLabel(val));
    }

    protected void sendMessage(String val) {
        chatPane.add(new JLabel(val));
    }
}
