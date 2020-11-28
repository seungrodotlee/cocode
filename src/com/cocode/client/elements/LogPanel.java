package com.cocode.client.elements;

import com.cocode.client.components.SidebarIcon;
import com.cocode.client.utils.ColorPack;
import com.cocode.client.utils.GlobalUtility;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private GlobalUtility global;
    private JButton titleBar;
    private JScrollPane mainPane;
    private JTextArea textArea;

    public LogPanel() {
        global = GlobalUtility.getInstance();

        titleBar = new JButton("로그", new SidebarIcon("keyboard_arrow_down", Color.WHITE).imageIcon());
        mainPane = new JScrollPane();
        textArea = new JTextArea();

        titleBar.setHorizontalAlignment(SwingConstants.LEFT);
        titleBar.setForeground(Color.WHITE);
        titleBar.setBackground(ColorPack.BG);
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));
        titleBar.setPreferredSize(new Dimension(1, 36));
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        mainPane.setViewportView(textArea);
        mainPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));

        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.PAGE_START);
        add(mainPane, BorderLayout.CENTER);

        global.setLogPane(this);
    }

    public void appendLog(String val) {
        System.out.println("[CLENT] appendLog");
        textArea.append(val + "\n");
        textArea.revalidate();
    }

    public String getText() {
        return textArea.getText();
    }

    public JButton getToggler() {
        return titleBar;
    }

    public JScrollPane getMainPane() {
        return mainPane;
    }
}
