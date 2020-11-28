package com.seungro.client.components;

import com.seungro.client.utils.ColorPack;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.client.utils.User;

import javax.swing.*;
import java.awt.*;

public class UserButton extends JButton {
    private GlobalUtility global;
    private Color originColor = Color.WHITE;
    private Color circleColor;
    private User u;

    public UserButton(User u) {
        this.u = u;
        global = GlobalUtility.getInstance();

        if(u.isAuth()) {
            originColor = new Color(255, 224, 23);
        }

        circleColor = originColor;

        if(u.getName().equals(global.getUserName())) {
            setText("   나 (" + u.getName() + ")      ");
        } else {
            setText("   " + u.getName() + "      ");
        }

        setHorizontalAlignment(SwingConstants.RIGHT);
        setPreferredSize(new Dimension(1, 36));
        setForeground(Color.WHITE);
        setBackground(ColorPack.BG);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPack.BG_DARK));
    }

    public void setCircleColor(Color color) {
        circleColor = color;
        repaint();
    }

    public void resetCircleColor() {
        circleColor = originColor;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(circleColor);
        g.fillOval(getWidth() - 14, (getHeight() / 2) - 3, 6, 6);
    }
}
