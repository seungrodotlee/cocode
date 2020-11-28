package com.cocode.client.utils;

import com.cocode.client.components.CodeArea;
import com.cocode.client.components.IconNode;
import com.cocode.client.components.UserButton;
import com.cocode.client.elements.ChatPanel;
import com.cocode.client.elements.LogPanel;
import com.cocode.client.elements.UserListPanel;
import com.cocode.data.Unit;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

//싱글톤
public class GlobalUtility {
    private static GlobalUtility instance = new GlobalUtility();

    private JTabbedPane mainTabPane;
    private LogPanel logPane;
    private ChatPanel chatPane;
    private UserListPanel userListPanel;
    private JTree tree;
    private IconNode treeRoot;
    private User currentEditor = null;
    private Socket socket;
    private Boolean auth = false;
    private String userName;
    private String[] fe = {
            "java", "javascript", "html", "css", "c", "cpp", "json", "jsp", "php", "xml"
    };

    private ArrayList<String> fileExts = new ArrayList<>(Arrays.asList(fe));
    private HashMap<String, CodeArea> codeAreaMap = new HashMap<String, CodeArea>();
    private HashMap<UUID, IconNode> folderMap = new HashMap<UUID, IconNode>();
    private HashMap<IconNode, String> tabMap = new HashMap<IconNode, String>();
    private HashMap<String, User> userMap = new HashMap<String, User>();

    public static GlobalUtility getInstance() {
        return instance;
    }

    public void setMainTabPane(JTabbedPane mainTabPane) {
        this.mainTabPane = mainTabPane;
    }

    public JTabbedPane getMainTabPane() {
        return mainTabPane;
    }

    public LogPanel getLogPane() {
        return logPane;
    }

    public void setLogPane(LogPanel logPane) {
        this.logPane = logPane;
    }

    public ChatPanel getChatPane() {
        return chatPane;
    }

    public void setChatPane(ChatPanel chatPane) {
        this.chatPane = chatPane;
    }

    public UserListPanel getUserListPanel() {
        return userListPanel;
    }

    public void setUserListPanel(UserListPanel userListPanel) {
        this.userListPanel = userListPanel;
    }

    public void setTree(JTree tree) {
        this.tree = tree;
        treeRoot = (IconNode) tree.getModel().getRoot();
        folderMap.put(treeRoot.getUUID(), treeRoot);
    }

    public IconNode getTreeRoot() {
        return treeRoot;
    }

    public void addUser(User u) {
        userMap.put(u.getName(), u);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public Boolean amIAuth() {
        return auth;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public HashMap<String, User> getUserMap() {
        return userMap;
    }

    public HashMap<IconNode, String> getTabMap() {
        return tabMap;
    }

    public void resetUserStatus(String name) {
        UserButton b = userMap.get(name).getBtn();
        b.resetCircleColor();
        b.setRequester(false);
    }

    public void setRequester(String name) {
        UserButton b = userMap.get(name).getBtn();
        b.setCircleColor(ColorPack.RED_ORANGE);
        b.setRequester(true);
    }

    public void setCurrentEditor(String name) {
        if(currentEditor != null) {
            UserButton old = currentEditor.getBtn();
            old.resetCircleColor();
        }

        if(name == null) {
            currentEditor = null;
            return;
        }

        currentEditor = userMap.get(name);
        currentEditor.getBtn().setCircleColor(ColorPack.GREEN);
    }

    public User getCurrentEditor() {
        return currentEditor;
    }

    public void attachNode(IconNode node, IconNode parent) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        int found = 0;
        int index = 1;
        String parentName = (String) parent.getUserObject();
        String origin = (String) node.getUserObject();
        String temp = origin;

        for(int i = 0; i < model.getChildCount(parent); i++) {
            String child = (String) ((IconNode) parent.getChildAt(i)).getUserObject();

            if(temp.equals(child)) {
                index++;
                temp = origin + " " + index;
            }
        }

        node.setUserObject(temp);

        IconNode[] data = {node, parent};
        attachNodeProcess(node, parent);
        sendMessage(new Unit(Unit.FILE_TREE_DATA, getUserName(), "add", data));
    }

    public void attachNodeByReceive(IconNode node, IconNode parent) {
        NodeWrapper realParent = new NodeWrapper();
        findNode(parent, getTreeRoot(), realParent);
        if(realParent.node != null) {
            attachNodeProcess(node, realParent.node);
        }
    }

    public void attachNodeProcess(IconNode node, IconNode parent) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        model.insertNodeInto(node, parent, parent.getChildCount());

        tree.setSelectionPath(new TreePath(node.getPath()));

        int found = 0;
        int index = 0;
        String parentName = (String) parent.getUserObject();
        String origin = (String) node.getUserObject();
        String temp = origin;

        while(true) {
            found = mainTabPane.indexOfTab(temp);

            if(found != -1) {

                index++;

                if(index >= 2) {
                    temp = parentName + "(" + index + ")/" + origin;
                } else {
                    temp = parentName + "/" + origin;
                }
            }

            if(found == -1) {
                break;
            }
        }

        folderMap.remove(parent.getUUID());
        folderMap.put(parent.getUUID(), parent);

        if(node.getNodeType() == IconNode.FOLDER_NODE) {
            folderMap.put(node.getUUID(), node);
        }

        if(node.getNodeType() == IconNode.FILE_NODE) {
            tabMap.put(node, temp);
            newFileData(temp, "");
            setCurrentFile(temp);
        }
    }

    public void deleteNode(IconNode node) {
        sendMessage(new Unit(Unit.FILE_TREE_DATA, null, "del", node));
        deleteNodeProcess(node);
    }

    public void deleteNodeByReceive(IconNode node) {
        System.out.println("del node = " + node);

        NodeWrapper realNode = new NodeWrapper();
        findNode(node, getTreeRoot(), realNode);

        if(realNode.node != null) {
            System.out.println("삭제 가능");
            deleteNodeProcess(realNode.node);
        }
    }

    public void deleteNodeProcess(IconNode node) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        String tabName = tabMap.get(node);
        int nodeType = node.getNodeType();

        if(nodeType == IconNode.FILE_NODE) {
            System.out.println("node = " + node.getUserObject() + ", tab = " + tabMap.get(node));
            int index = mainTabPane.indexOfTab(tabName);
            mainTabPane.removeTabAt(index);
        }

        if(nodeType == IconNode.FOLDER_NODE) {
            destroyTabOfChildren(node);
            folderMap.remove(tabName);
        }

        model.removeNodeFromParent(node);
    }

    private void destroyTabOfChildren(IconNode root) {
        ArrayList<IconNode> children = root.getChildren();

        for (IconNode node : children) {
            if (node.getNodeType() == IconNode.FILE_NODE) {
                int index = mainTabPane.indexOfTab(tabMap.get(node));
                System.out.println("tab = " + tabMap.get(node) + ", index = " + index + ", tab length = "  + mainTabPane.getTabCount());
                mainTabPane.removeTabAt(index);
            }

            if (node.getNodeType() == IconNode.FOLDER_NODE) {
                destroyTabOfChildren(node);
            }
        }
    }

    private void findNode(IconNode node, IconNode root, NodeWrapper resultNode) {
        ArrayList<IconNode> children = root.getChildren();

        if(node.getUUID().equals(root.getUUID())) {
            resultNode.node = root;
            return;
        }

        for(IconNode child : children) {
            if(child.getNodeType() == IconNode.FILE_NODE) {
                if(node.getUUID().equals(child.getUUID())) {
                    resultNode.node = child;
                    return;
                } else {
                    continue;
                }
            }

            if(child.getNodeType() == IconNode.FOLDER_NODE) {
                if(node.getUUID().equals(child.getUUID())) {
                    resultNode.node = child;
                    return;
                } else {
                    findNode(node, child, resultNode);
                }
            }
        }

        return;
    }

    public IconNode findParentNode(IconNode node) {
        for (Map.Entry<UUID, IconNode> entry : folderMap.entrySet()) {
            IconNode folder = entry.getValue();

            for(int i = 0; i < folder.getChildCount(); i++) {
                IconNode child = (IconNode) folder.getChildAt(i);

                if(child.getUUID().equals(node.getUUID())) {
                    return folder;
                }
            }
        }

        return treeRoot;
    }

    public void newFileData(String key, String val) {
        CodeArea textArea = new CodeArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

        codeAreaMap.put(key, textArea);

        mainTabPane.add(key, textArea);
        textArea.setText(val);
    }

    public void updateFileName(IconNode node, String old, String newKey) {
        UpdateFilenameData data = new UpdateFilenameData(node, old, newKey);
        sendMessage(new Unit(Unit.FILE_TREE_DATA, null, "edt", data));
        updateFileNameProcess(node, old, newKey);
    }

    public void updateFileNameByReceive(Object obj) {
        UpdateFilenameData data = (UpdateFilenameData) obj;
        IconNode key = null;

        IconNode node = data.getNode();
        String old = data.getOld();
        String newKey= data.getNewKey();

        NodeWrapper realNode = new NodeWrapper();
        findNode(node, treeRoot, realNode);
        key = updateFileNameProcess(realNode.node, old, newKey);

        key.setUserObject(newKey);
        ((DefaultTreeModel) tree.getModel()).nodeChanged(key);
    }

    public IconNode updateFileNameProcess(IconNode node, String old, String newKey) {
        System.out.println("old = " + old);
        CodeArea textArea = codeAreaMap.get(old);
        IconNode key = null;

        codeAreaMap.remove(old);

        System.out.println("new = " + newKey);

        int found = 0;
        int i = 0;
        String parentName = (String) findParentNode(node).getUserObject();
        String origin = newKey;
        String temp = origin;

        while(true) {
            found = mainTabPane.indexOfTab(temp);

            if(found != -1) {

                i++;

                if(i >= 2) {
                    temp = parentName + "(" + i + ")/" + origin;
                } else {
                    temp = parentName + "/" + origin;
                }
            }

            if(found == -1) {
                break;
            }
        }

        newKey = temp;
        System.out.println("new key = " + newKey);
        codeAreaMap.put(newKey, textArea);

        for (Map.Entry<IconNode, String> entry : tabMap.entrySet()) {
            System.out.println("old = " + old + ", itr = " + entry.getValue());
            if (entry.getValue().equals(old)) {
                key = entry.getKey();
                break;
            }
        }

        tabMap.remove(key);
        tabMap.put(key, newKey);

        int index = mainTabPane.indexOfComponent(textArea);
        mainTabPane.setTitleAt(index, newKey);

        String[] pieces = newKey.split("\\.");
        String ext = pieces[pieces.length - 1];

        if(fileExts.indexOf(ext) >= 0) {
            textArea.setSyntaxEditingStyle("text/" + ext);
        }

        return key;
    }

    public String getFileData(String key) {
        return codeAreaMap.get(key).getTextArea().getText();
    }

    public void setCurrentFile(String key) {
        System.out.println("req key = " + key);
        mainTabPane.setSelectedComponent(codeAreaMap.get(key));
    }

    public void sendMessage(Unit u) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(u);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class NodeWrapper {
        IconNode node;
    }
}