package com.cocode.server;

import com.cocode.data.Unit;
import com.cocode.server.utils.ClientData;

import javax.sound.sampled.AudioFormat;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private Socket socket;
    private ArrayList<ClientData> members;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String id;
    private ClientData me;
    private int type;

    public ServerThread(Socket socket, ArrayList<ClientData> members) {
        this.socket = socket;
        this.members = members;
    }

    public void run() {
        System.out.println("[" + socket.getInetAddress() + "] 접속");

        while(true) {
            try {
                input = new ObjectInputStream(socket.getInputStream());

                Unit u = (Unit) input.readObject();

                type = u.getType();
                id = u.getUserName();

                if(type == Unit.ENTER_DATA) {
                    me = new ClientData(id, socket);
                    String value = (String) u.getValue();
                    String[] receive = value.split("/");
                    String roomKey = value.replace(receive[0] + "/", "");
                    System.out.println(receive[0] + "/" + roomKey);

                    if (receive[0].equals("new")) {
                        synchronized (members) {
                            members.add(me);
                            receive(new Unit(Unit.LOG_DATA, null, roomKey, null));
                        }
                    }

                    if (receive[0].equals("join")) {
                        synchronized (members) {
                            int i = 2;
                            for (ClientData member : members) {
                                if (member.getName().equals(me.getName())) {
                                    me.setName(id + " " + i);
                                }

                                i++;
                            }

                            members.add(me);

                            ArrayList<String> mems  = new ArrayList<String>();

                            for(ClientData member : members) {
                                mems.add(member.getName());
                            }

                            System.out.println("[SERVER] auth = " + members.get(0).getName());
                            receive(new Unit(Unit.LOG_DATA, members.get(0).getName(), "success", mems));
                            broadcast(new Unit(Unit.LOG_DATA, me.getName(), "join", mems));
                        }
                    }
                } else if(type == Unit.LOG_DATA) {
                    String logType = u.getTitle();

                    if(logType.equals("request_share")) {
                        broadcast(u);
                    }

                    if(logType.equals("accept_share") || logType.equals("finish_share")) {
                        broadcast(u);
                        receive(u);
                    }
                } else {
                    System.out.println("[SERVER] get");
                    if(type == Unit.VOICE_DATA) {
                        receive(u);
                    }
                    broadcast(u);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[" + socket.getInetAddress() + "] 연결종료");

                members.remove(me);

                broadcast(new Unit(0, id, null, "disconnect"));

                try {
                    socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                break;
            }
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 8000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate,
                sampleSizeInBits, channels, signed, bigEndian);
    }

    private void receive(Unit u) {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(u);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void whisper(String user, Unit u) {
        try {
            for(ClientData member : members) {
                if(member.getName().equals(user)) {
                    System.out.println("[SERVER] whisper to " + user);
                    output = new ObjectOutputStream(member.getSocket().getOutputStream());
                    output.writeObject(u);
                    output.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void broadcast(Unit u) {
        try {
            for (ClientData member : members) {
                if (member.getSocket().equals(me.getSocket())) {
                    continue;
                }

                output = new ObjectOutputStream(member.getSocket().getOutputStream());
                output.writeObject(u);
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
