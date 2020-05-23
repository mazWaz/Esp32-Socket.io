package com.example.sockettouchkitkat;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketConnection {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.10:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public Socket getSocket() {
        return mSocket;
    }
}
