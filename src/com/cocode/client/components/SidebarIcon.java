package com.cocode.client.components;

import com.github.fcannizzaro.material.icons.IconMaterial;

import java.awt.*;

public class SidebarIcon extends IconMaterial {
    public SidebarIcon(String name) {
        super(name);

        color(new Color(103, 110, 149));
        size(18);
    }

    public SidebarIcon(String name, Color color) {
        super(name);

        color(color);
        size(18);
    }
}
