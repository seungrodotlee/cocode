package com.seungro.client;

import com.seungro.client.components.ChatBubble;
import com.seungro.client.components.CodeArea;
import com.seungro.client.components.IconNode;
import com.seungro.client.elements.ChatPanel;
import com.seungro.client.elements.LogPanel;
import com.seungro.client.elements.UserListPanel;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.client.utils.User;
import com.seungro.data.Unit;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ClientThread extends Thread {
    private Client client;
    private Socket socket;
    private ObjectInputStream input;
    private GlobalUtility global;

    public ClientThread(Client client, Socket socket, ObjectInputStream input) {
        global = GlobalUtility.getInstance();
        this.client = client;
        this.socket = socket;
        this.input = input;
    }

    @Override
    public void run() {
        while(true) {
            try {
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

                    if(logType.equals("request_share")) {
                        String reqUser = receive.getUserName();
                        System.out.println("[CLENT] request share by " + reqUser);

                        LogPanel logPanel = global.getLogPane();
                        logPanel.appendLog(reqUser + "님이 코드공유를 요청하셨습니다.");
                        logPanel.revalidate();

                        global.setRequester(reqUser);
                    }

                    if(logType.equals("finish_share")) {
                        String editor = receive.getUserName();

                        System.out.println("[CLENT] finish share by " + editor);

                        LogPanel logPanel = global.getLogPane();
                        logPanel.appendLog(editor + "님이 코드공유를 중지하셨습니다.");

                        global.setCurrentEditor(null);
                        global.getUserMap().get(editor).getBtn().setRequester(false);
                    }

                    if(logType.equals("accept_share")) {
                        String editor = receive.getUserName();

                        System.out.println("[CLENT] start share by " + editor);

                        System.out.println("[CLENT] current editor = " + global.getCurrentEditor());

                        global.resetUserStatus(editor);

                        if(global.getCurrentEditor() != null && global.getUserName().equals(global.getCurrentEditor().getName())) {
                            client.stop();
                        }

                        TimerTask r = new TimerTask() {
                            @Override
                            public void run() {
                                if(editor.equals(global.getUserName())) {
                                    client.share();
                                } else {
                                    global.setCurrentEditor(editor);
                                }

                                LogPanel logPanel = global.getLogPane();
                                logPanel.appendLog(editor + "님이 코드공유를 시작하셨습니다.");
                            }
                        };

                        Timer t = new Timer();
                        t.schedule(r, 100);
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
