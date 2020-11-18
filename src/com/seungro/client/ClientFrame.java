package com.seungro.client;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.util.Enumeration;

public class ClientFrame extends JFrame {
    public ClientFrame() {
        adjustTheme();
    }

    public void ready() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void adjustTheme() {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Spoqa.ttf")).deriveFont(14f);
            setUIFont(new FontUIResource(font));
        } catch (Exception e) {
            e.printStackTrace();
        }

        FlatMaterialPalenightIJTheme.install();
        UIManager.put("*.borderColor", new Color(32, 35, 49));
        UIManager.put("*.separatorColor", new Color(32, 35, 49));
        UIManager.put("*.lineSeparatorColor", new Color(32, 35, 49));
        UIManager.put("TabbedPane.focusColor", new Color(60, 67, 95));
        UIManager.put("TabbedPane.hoverColor", new Color(60, 67, 95));
        UIManager.put("TabbedPane.contentAreaColor", new Color(32, 35, 49));
    }

    public static void setUIFont(FontUIResource f) {
        Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                FontUIResource orig = (FontUIResource) value;
                Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }
}
