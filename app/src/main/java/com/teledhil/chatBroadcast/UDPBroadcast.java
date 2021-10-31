/**
 * 
 */
package com.teledhil.chatBroadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;


/*
 * This class tries to send a broadcast UDP packet over your wifi network to discover the boxee service. 
 */

public class UDPBroadcast {
	private static final String TAG = "UDPBroadcast";
	private static final int PUERTO = 12543;
	private static final int TIMEOUT_MS = 0;

	private WifiManager wifiManager_;
	private DatagramSocket socket_;
	private final Handler handler_;
	private MessageListener messageListener_;

	interface DiscoveryReceiver {
		void addAnnouncedServers(InetAddress[] host, int port[]);
	}

	UDPBroadcast(WifiManager wifi, Handler handler) {
		wifiManager_ = wifi;
		handler_ = handler;

		if(!obtenerPuerto()) {
			handler_.obtainMessage(MainActivity.MENSAJE_UDP_BROADCAST_ERROR, "No se pudo reservar el puerto").sendToTarget();
			Log.e(TAG, "No se pudo reservar el puerto");
		} else {
			messageListener_ = new MessageListener(socket_, handler_);
			messageListener_.start();
		}
		

	}
	
	private boolean obtenerPuerto() {
		try {
			socket_ = new DatagramSocket(PUERTO);
			socket_.setBroadcast(true);
			socket_.setSoTimeout(TIMEOUT_MS); // Tiempo de esperar de respuesta
			handler_.obtainMessage(MainActivity.MENSAJE_UDP_BROADCAST_OK, "Puerto defecto: "+ socket_.getLocalPort()).sendToTarget();
			return true;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Could not send discovery request", e);
			return false;
		}
		
	}

	/**
	 * Send a broadcast UDP packet containing a request for boxee services to
	 * announce themselves.
	 * 
	 * @throws IOException
	 */
	public void enviarMensaje(String datos) throws IOException {

		InetAddress direccion = NetworkUtils.getBroadcastAddress(wifiManager_);
		Log.d(TAG, "Sending data " + datos);
		DatagramPacket packet = new DatagramPacket(datos.getBytes("UTF-8"), datos.getBytes("UTF-8").length, direccion, PUERTO);

        if (socket_ != null) {
		try {

                socket_.send(packet);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "No se pudo enviar. Estás conectado por WiFi?", e);
			handler_.obtainMessage(MainActivity.MENSAJE_UDP_BROADCAST_ERROR, "No se pudo enviar. Estás conectado por WiFi?").sendToTarget();
		}
		handler_.obtainMessage(MainActivity.MENSAJE_UDP_BROADCAST_OK, "Enviado: \""+ datos +"\" a "+direccion).sendToTarget();

        }
	}

	
	public void cerrar() {
	    if(messageListener_ != null)
        messageListener_.pleaseDie();
	}
}