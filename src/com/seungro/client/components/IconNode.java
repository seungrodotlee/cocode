package com.seungro.client.components;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.UUID;

public class IconNode extends DefaultMutableTreeNode {
    public final static int FOLDER_NODE = 0;
    public final static int FILE_NODE = 1;

    private int nodeType;
    private UUID uuid;

    protected Icon icon;

    protected String iconName;

    public IconNode() {
        this(null);
    }

    public IconNode(Object userObject) {
        this(userObject, true, null);
    }

    public IconNode(Object userObject, boolean allowsChildren, Icon icon) {
        super(userObject, allowsChildren);
        this.icon = icon;
        this.uuid = UUID.randomUUID();
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getIconName() {
        if (iconName != null) {
            return iconName;
        } else {
            String str = userObject.toString();
            int index = str.lastIndexOf(".");
            if (index != -1) {
                return str.substring(++index);
            } else {
                return null;
            }
        }
    }

    public void setIconName(String name) {
        iconName = name;
    }

    public void setMaterialIcon(String iconName) {
        setIcon(new SidebarIcon(iconName).imageIcon());
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public int getNodeType() {
        return nodeType;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public ArrayList<IconNode> getChildren() {
        ArrayList<IconNode> nodes = new ArrayList<IconNode>();
        int length = getChildCount();

        for(int i = 0; i < length; i++) {
            IconNode node = (IconNode) getChildAt(i);

            nodes.add(node);
        }

        return nodes;
    }
}
