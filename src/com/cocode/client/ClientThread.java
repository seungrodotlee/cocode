package com.cocode.client;

import com.cocode.client.components.CodeArea;
import com.cocode.client.components.IconNode;
import com.cocode.client.elements.ChatPanel;
import com.cocode.client.elements.LogPanel;
import com.cocode.client.elements.UserListPanel;
import com.cocode.client.utils.GlobalUtility;
import com.cocode.client.utils.User;
import com.cocode.data.Unit;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
    private SourceDataLine line;
    private AudioFormat format;

    public ClientThread(Client client, Socket socket, ObjectInputStream input) {
        global = GlobalUtility.getInstance();
        this.client = client;
        this.socket = socket;
        this.input = input;
        line = global.getLine();
        format = global.getAudioFormat();
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

                if(type == Unit.VOICE_DATA) {
                    if(!global.isListening()) {
                        continue;
                    }

                        System.out.println("[VOICE] in");
                        byte audio[] = (byte[]) receive.getValue();


                    int bufferSize = (int)format.getSampleRate()
                            * format.getFrameSize();

                        InputStream input =
                                new ByteArrayInputStream(audio);
                        final AudioInputStream ais =
                                new AudioInputStream(input, format,
                                        audio.length / format.getFrameSize());

                        byte buffer[] = new byte[bufferSize];
                        int count;
                        while ((count = ais.read(
                                buffer, 0, buffer.length)) != -1) {
                            if (count > 0) {
                                line.write(buffer, 0, count);
                            }
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
                line.drain();
                line.close();
            }
        }
    }
}
