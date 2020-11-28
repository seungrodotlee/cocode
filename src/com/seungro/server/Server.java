package com.seungro.server;

import com.seungro.server.utils.ClientData;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final static int PORT = 10001;
    private ServerSocket serverSocket;
    private Socket socket;
    ArrayList<ClientData> members = new ArrayList<ClientData>();
    private String auth = "";

    public Server() {
        try {
            System.out.println("server on");
            serverSocket = new ServerSocket(PORT);

            while(true) {
                socket = serverSocket.accept();
                
                ServerThread thread = new ServerThread(socket, members, auth);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new Server();
    }
}
