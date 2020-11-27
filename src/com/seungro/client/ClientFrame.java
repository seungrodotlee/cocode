package com.seungro.client;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme;
import com.seungro.client.utils.ColorPack;

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
        UIManager.put("*.borderColor", ColorPack.BG_DARK);
        UIManager.put("*.separatorColor", ColorPack.BG_DARK);
        UIManager.put("*.lineSeparatorColor", ColorPack.BG_DARK);
        UIManager.put("TabbedPane.focusColor", ColorPack.BG_LIGHT);
        UIManager.put("TabbedPane.hoverColor", ColorPack.BG_LIGHT);
        UIManager.put("TabbedPane.contentAreaColor", ColorPack.BG_DARK);
        UIManager.put("SplitPane.background", ColorPack.BG_DARK);
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
