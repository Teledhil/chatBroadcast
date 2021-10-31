package com.teledhil.chatBroadcast;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;

public class MessageHandler extends Handler {
    public static final int MENSAJE_UDP_BROADCAST_OK = 1;
    public static final int MENSAJE_UDP_BROADCAST_RECIBIDO = 2;
    public static final int MENSAJE_UDP_BROADCAST_ERROR = 3;

    private ArrayAdapter<String> arrayRecibidos;

    MessageHandler(Context ctx) {
        arrayRecibidos = new ArrayAdapter<>(ctx, R.layout.mensaje);
    }

    private void setTextRecibido(String mensaje) {
        arrayRecibidos.add(mensaje);
    }

    private void setTextRecibidoOk(String mensaje) {
        arrayRecibidos.add("DEBUG> " + mensaje);
    }

    private void setTextRecibidoError(String mensaje) {

        arrayRecibidos.add("ERROR> " + mensaje);
    }


    @Override
    public void handleMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleMessage(msg);

        switch (msg.what) {
            case MENSAJE_UDP_BROADCAST_RECIBIDO:
                setTextRecibido(msg.obj.toString());
                break;
            case MENSAJE_UDP_BROADCAST_OK:
                setTextRecibidoOk(msg.obj.toString());
                break;
            case MENSAJE_UDP_BROADCAST_ERROR:
                setTextRecibidoError(msg.obj.toString());
                break;
        }
    }
}
