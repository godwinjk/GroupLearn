package com.grouplearn.project.app.uiManagement.socket;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Godwin on 27-03-2017 16:50 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class SocketHelper {
    Socket socket;
    public void connect(){
        try {
            socket= IO.socket("host url");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
