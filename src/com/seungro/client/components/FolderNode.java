package com.seungro.client.components;

public class FolderNode extends IconNode {
    public FolderNode(String title) {
        super(title);

        setMaterialIcon("folder");
        setNodeType(FOLDER_NODE);
    }
}
