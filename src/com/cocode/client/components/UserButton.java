package com.cocode.client.components;

import com.cocode.client.utils.ColorPack;
import com.cocode.client.utils.GlobalUtility;
import com.cocode.client.utils.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserButton extends JButton {
    private UserButton me;
    private GlobalUtility global;
    private UserPopup popup;
    private Color originColor = Color.WHITE;
    private Color circleColor;
    private User u;
    private Boolean requester;

    public UserButton(User u) {
        me = this;
        this.u = u;
        global = GlobalUtility.getInstance();

        popup = new UserPopup();

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
        addMouseListener(new UserClickListener());
    }

    public void setCircleColor(Color color) {
        circleColor = color;
        repaint();
    }

    public void resetCircleColor() {
        circleColor = originColor;
        repaint();
    }

    public Boolean isRequester() {
        return requester;
    }

    public void setRequester(Boolean requester) {
        this.requester = requester;

        if(!global.amIAuth()) {
            return;
        }

        if(requester) {
            popup.addItem("공유 허용");
        } else {
            popup.removeItem("공유 허용");
        }
    }

    public String getUserName() {
        return u.getName();
    }

    public UserPopup getPopup() {
        return popup;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(circleColor);
        g.fillOval(getWidth() - 14, (getHeight() / 2) - 3, 6, 6);
    }

    private class UserClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            if (SwingUtilities.isRightMouseButton(e)) {
                popup.show(me, e.getX(), e.getY());
            }
        }
    }
}
