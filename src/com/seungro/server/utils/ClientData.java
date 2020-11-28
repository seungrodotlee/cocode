package com.seungro.server.utils;

import java.io.Serializable;
import java.net.Socket;

public class ClientData {
    private String name;
    private Socket socket;

    public ClientData(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }
}
