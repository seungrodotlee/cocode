package com.seungro.client.elements;

import com.seungro.client.components.SidebarIcon;
import com.seungro.client.utils.ColorPack;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private JButton titleBar;
    private JScrollPane mainPane;
    private JTextArea textArea;

    public LogPanel() {
        titleBar = new JButton("로그", new SidebarIcon("keyboard_arrow_down", Color.WHITE).imageIcon());
        mainPane = new JScrollPane();
        textArea = new JTextArea();

        titleBar.setHorizontalAlignment(SwingConstants.LEFT);
        titleBar.setForeground(Color.WHITE);
        titleBar.setBackground(ColorPack.BG);
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));
        titleBar.setPreferredSize(new Dimension(1, 36));
        mainPane.add(textArea);
        mainPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));

        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.PAGE_START);
        add(mainPane, BorderLayout.CENTER);


        setPreferredSize(new Dimension(1, 36));
    }

    protected void addText(String val) {
        textArea.setText(textArea.getText() + "\n" + val);
    }

    protected String getText() {
        return textArea.getText();
    }

    public JButton getToggler() {
        return titleBar;
    }

    public JScrollPane getMainPane() {
        return mainPane;
    }
}
