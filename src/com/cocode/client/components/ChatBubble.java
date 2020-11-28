package com.cocode.client.components;

import com.cocode.client.utils.ColorPack;
import com.cocode.client.utils.GlobalUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChatBubble extends JPanel {
    private GlobalUtility global;
    private JLabel senderLabel;
    private JTextArea messageLabel;

    public ChatBubble(String sender, String message) {
        global = GlobalUtility.getInstance();
        senderLabel = new JLabel(sender);
        messageLabel = new JTextArea(message);
        senderLabel.setFont(messageLabel.getFont().deriveFont(12f));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        messageLabel.setEditable(false);
        messageLabel.setWrapStyleWord(true);
        messageLabel.setLineWrap(true);

        if(sender.equals(global.getUserName())) {
            senderLabel.setText("나");
            senderLabel.setAlignmentX(RIGHT_ALIGNMENT);
            messageLabel.setAlignmentX(RIGHT_ALIGNMENT);
            messageLabel.setBackground(ColorPack.ACCENT);
            messageLabel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, ColorPack.ACCENT));
        } else {
            messageLabel.setBackground(ColorPack.GREY);
            messageLabel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, ColorPack.GREY));
        }

        add(senderLabel);
        add(messageLabel);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);

                System.out.println(getWidth());
                messageLabel.setMaximumSize(new Dimension(getWidth(), Integer.MAX_VALUE));
            }
        });

        setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, ColorPack.BG));
    }
}
