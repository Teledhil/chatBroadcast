/**
 * 
 */
package com.teledhil.chatBroadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

//import android.util.Log;

/*
 * This class tries to send a broadcast UDP packet over your wifi network to discover the boxee service. 
 */

public class CopyOfUDPBroadcast {
	private static final String TAG = "UDPBroadcast";
	//private static final String REMOTE_KEY = "b0xeeRem0tE!";
	private static final int PUERTO = 12543;
	private static final int TIMEOUT_MS = 500;

	private WifiManager mWifi;
	@SuppressWarnings("unused")
	private String mAplicacion;
	private DatagramSocket mSocket;
	private final Handler mHandler;

	interface DiscoveryReceiver {
		void addAnnouncedServers(InetAddress[] host, int port[]);
	}

	CopyOfUDPBroadcast(WifiManager wifi, String aplicacion, Handler handler) {
		mWifi = wifi;
		mAplicacion = aplicacion;
		mHandler = handler;
		
		try {
			mSocket = new DatagramSocket(PUERTO);
			mSocket.setBroadcast(true);
			mSocket.setSoTimeout(TIMEOUT_MS); // Tiempo de esperar de respuesta
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Could not send discovery request", e);
			
			try {
				mSocket = new DatagramSocket();
				mSocket.setBroadcast(true);
				mSocket.setSoTimeout(TIMEOUT_MS); // Tiempo de esperar de respuesta
				Log.d(TAG, "Puerto usado: "+mSocket.getLocalPort());
			} catch (SocketException e2) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Could not send discovery request with any port", e2);
				e.printStackTrace();
			}
		}
		

	}


		// String datos =
		// "<msg app=\""+mAplicacion+"\" application=\"iphone_remote\" challenge=\"%s\"/>";

		



	/**
	 * Send a broadcast UDP packet containing a request for boxee services to
	 * announce themselves.
	 * 
	 * @throws IOException
	 */
	public void enviarMensaje(String datos) throws IOException {

		//String firma = getFirma(datos);
		//String mensaje = datos + firma;
		Log.d(TAG, "Sending data " + datos);

		DatagramPacket packet = new DatagramPacket(datos.getBytes(), datos
				.length(), getBroadcastAddress(), mSocket.getLocalPort());
		mSocket.send(packet);
	}

	/**
	 * Calculate the broadcast IP we need to send the packet along. If we send
	 * it to 255.255.255.255, it never gets sent. I guess this has something to
	 * do with the mobile network not wanting to do broadcast.
	 */
	private InetAddress getBroadcastAddress() throws IOException {
		DhcpInfo dhcp = mWifi.getDhcpInfo();
		if (dhcp == null) {
			Log.d(TAG, "Could not get dhcp info");
			return null;
		}

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	/**
	 * Listen on socket for responses, timing out after TIMEOUT_MS
	 * 
	 * @param socket
	 *            socket on which the announcement request was sent
	 * @throws IOException
	 */
	public String recibir() {
			byte[] buf = new byte[1024];
			String s = "";

			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				mSocket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s = new String(packet.getData(), 0, packet.getLength());
			Log.d(TAG, "Received response " + s);

			mHandler.obtainMessage(1, s).sendToTarget();
			return s;
		}
	

	/**
	 * Calculate the signature we need to send with the request. It is a string
	 * containing the hex md5sum of the challenge and REMOTE_KEY.
	 * 
	 * @return signature string
	 */
	/*private String getFirma(String challenge) {
		MessageDigest digest;
		byte[] md5sum = null;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(challenge.getBytes());
			digest.update(REMOTE_KEY.getBytes());
			md5sum = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		StringBuffer hexString = new StringBuffer();
		for (int k = 0; k < md5sum.length; ++k) {
			String s = Integer.toHexString((int) md5sum[k] & 0xFF);
			if (s.length() == 1)
				hexString.append('0');
			hexString.append(s);
		}
		return hexString.toString();
	}*/

	public static void main(String[] args) {
		new CopyOfUDPBroadcast(null, "chatBroadcast", null);
		while (true) {
		}
	}
}
