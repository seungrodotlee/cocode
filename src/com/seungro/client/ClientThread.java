package com.seungro.client;

import com.seungro.client.components.ChatBubble;
import com.seungro.client.components.CodeArea;
import com.seungro.client.components.IconNode;
import com.seungro.client.elements.ChatPanel;
import com.seungro.client.elements.UserListPanel;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.client.utils.User;
import com.seungro.data.Unit;
import com.seungro.server.utils.ClientData;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ClientThread extends Thread {
    private Socket socket;
    private ObjectInputStream input;
    private GlobalUtility global;
    public ClientThread(Socket socket, ObjectInputStream input) {
        this.socket = socket;
        this.input = input;
    }

    @Override
    public void run() {
        while(true) {
            try {
                global = GlobalUtility.getInstance();

                input = new ObjectInputStream(socket.getInputStream());

                Unit receive = (Unit) input.readObject();

                int type = receive.getType();

                if(type == Unit.LOG_DATA) {
                    String logType = receive.getTitle();

                    if(logType.equals("join")) {
                        ArrayList<String> members = (ArrayList<String>) receive.getValue();
                        UserListPanel userListPanel = global.getUserListPanel();
                        Set<String> current = global.getUserMap().keySet();

                        for(String member : members) {
                            if(!current.contains(member)) {
                                userListPanel.addUser(new User(member));
                                userListPanel.revalidate();
                            }
                        }
                    }
                }

                if(type == Unit.FILE_DATA) {
                    JTabbedPane tab = global.getMainTabPane();
                    CodeArea codeArea = (CodeArea) tab.getComponentAt(tab.indexOfTab(receive.getTitle()));
                    codeArea.setText((String) receive.getValue());
                }

                if(type == Unit.CHAT_DATA) {
                    ChatPanel chatPane = global.getChatPane();
                    String val = receive.getUserName() + ": " + receive.getValue();
                    System.out.println("[CLIENT] receive msg: " + val);
                    chatPane.appendMessage(receive.getUserName(), (String) receive.getValue());
                    chatPane.revalidate();
                }

                if(type == Unit.FILE_TREE_DATA) {
                    if(receive.getTitle().equals("add")) {
                        IconNode[] data = (IconNode[]) receive.getValue();
                        global.attachNodeByReceive(data[0], data[1]);
                    }

                    if(receive.getTitle().equals("del")) {
                        global.deleteNodeByReceive((IconNode) receive.getValue());
                    }

                    if(receive.getTitle().equals("edt")) {
                        global.updateFileNameByReceive(receive.getValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
