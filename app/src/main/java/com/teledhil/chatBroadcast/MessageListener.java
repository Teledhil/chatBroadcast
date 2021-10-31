package com.teledhil.chatBroadcast;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Listen on socket for responses, timing out after TIMEOUT_MS
 *
 * @throws IOException
 */
public class MessageListener extends Thread {
    private static final String TAG = "MessageListener";
    private DatagramSocket socket_;
    private Handler handler_;
    private boolean keepGoing_;

    public MessageListener(DatagramSocket socket, Handler handler) {
        socket_ = socket;
        handler_ = handler;
    }

    public void run() {

        keepGoing_ = true;
        while (keepGoing_ && (socket_ != null)) {
            recibir();
        }
        socket_.close();
    }

    public void pleaseDie() {
        keepGoing_ = false;
    }

    private String recibir() {
        byte[] buf = new byte[1024];
        String s = "";

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket_.receive(packet);
            s = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
            Log.d(TAG, "Received response " + s);

            handler_.obtainMessage(MainActivity.MENSAJE_UDP_BROADCAST_RECIBIDO, s).sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "En recibir: ", e);
            handler_.obtainMessage(MainActivity.MENSAJE_UDP_BROADCAST_ERROR, "No se pudo recibir").sendToTarget();
        }
        return s;
    }
}
