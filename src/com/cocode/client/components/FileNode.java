package com.cocode.client.components;

public class FileNode extends IconNode {
    public FileNode(String title) {
        super(title);

        setMaterialIcon("description");
        setNodeType(IconNode.FILE_NODE);
    }
}
