package com.devetapps.anonychat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private static final String SERVER_URL = "http://192.168.56.1:3000";
    private static Socket mSocket;
    private SocketManager() {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            System.out.println("Erreur : " + e);
        }
    }
    public static Socket getInstance() {
        if (mSocket == null) {
            new SocketManager();
        }
        return mSocket;
    }
}