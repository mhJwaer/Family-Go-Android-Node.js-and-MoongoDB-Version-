package com.mh.jwaer.familygo.ui.main.hosts.chat;


import com.mh.jwaer.familygo.data.network.RetrofitClient;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketClient {

    private static Socket instance;

    public static Socket getInstance(){
        if (instance == null){
            try {
                IO.Options opts = new IO.Options();

                opts.forceNew = true;
                opts.reconnection = true;
                opts.secure = true;
                instance = IO.socket(RetrofitClient.BASE_URL);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        return instance;
        }
        return  instance;
    }
}
