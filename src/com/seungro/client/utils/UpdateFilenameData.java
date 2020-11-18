package com.seungro.client.utils;

import com.seungro.client.components.IconNode;

import java.io.Serializable;

class UpdateFilenameData implements Serializable {
    private IconNode node;
    private String old, newKey;

    public UpdateFilenameData(IconNode node, String old, String newKey) {
        this.node = node;
        this.old = old;
        this.newKey = newKey;
    }

    public IconNode getNode() {
        return node;
    }

    public String getOld() {
        return old;
    }

    public String getNewKey() {
        return newKey;
    }
}