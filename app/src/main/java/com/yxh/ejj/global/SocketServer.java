package com.yxh.ejj.global;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author 小红妹
 * @date 2023/3/1
 * @email L2279833535@163.com
 * @package com.xuanyi.webserver.service
 * @describe Java-WebSocket服务端（server）
 * @copyright
 */

public class SocketServer extends WebSocketServer {

    private static final String TAG = "SocketServer";
    private WebSocketReceivedMessageCallback callback;

    public SocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public SocketServer(int port, WebSocketReceivedMessageCallback callback) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.callback = callback;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // This method sends a message to the new client
        conn.send("Welcome to the server!");
        // This method sends a message to all clients connected
        broadcast("new connection: " + handshake.getResourceDescriptor());
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
//        broadcast(message);
        System.out.println(conn + ": " + message);
        if (callback != null) {
            callback.onReceived(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        Log.d(TAG, "onError:  conn = " + conn + ", ex = " + ex.getMessage());
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific
            // websocket
        }
    }


    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}
