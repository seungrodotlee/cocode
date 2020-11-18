package com.seungro.client;

import com.seungro.client.components.CodeArea;
import com.seungro.client.components.IconNode;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.data.Unit;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.net.Socket;

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

                if(type == Unit.FILE_DATA || type == Unit.CHAT_DATA) {
                    JTabbedPane tab = global.getMainTabPane();
                    CodeArea codeArea = (CodeArea) tab.getComponentAt(tab.indexOfTab(receive.getTitle()));
                    codeArea.setText((String) receive.getValue());
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
