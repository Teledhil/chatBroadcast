package com.teledhil.chatBroadcast;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	public static final int MENSAJE_UDP_BROADCAST_OK = 1;
	public static final int MENSAJE_UDP_BROADCAST_RECIBIDO = 2;
	public static final int MENSAJE_UDP_BROADCAST_ERROR = 3;

	UDPBroadcast carretera;

	String textoEnviado;

	EditText ventanaEnviado;
	ListView ventanaRecibido;
	private ArrayAdapter<String> arrayRecibidos;

	public void setTextRecibido(String mensaje) {
		arrayRecibidos.add("> " + mensaje);
	}
	
	public void setTextRecibidoOk(String mensaje) {
		//arrayRecibidos.add("DEBUG> " + mensaje);
	}
	
	public void setTextRecibidoError(String mensaje) {
		arrayRecibidos.add("ERROR> " + mensaje);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate se inicia");

		setContentView(R.layout.main);
		Log.d(TAG, "Cargado el layout");

		textoEnviado = "";
		ventanaEnviado = (EditText) findViewById(R.id.EditText01);
		ventanaEnviado.setText(textoEnviado);
		Log.d(TAG, "ventanaEnviado listo");

		arrayRecibidos = new ArrayAdapter<String>(this, R.layout.mensaje);
		ventanaRecibido = (ListView) findViewById(R.id.ListView01);
        ventanaRecibido.setAdapter(arrayRecibidos);
		Log.d(TAG, "ventanaRecibido listo");

		carretera = new UDPBroadcast(
				(WifiManager) getSystemService(Context.WIFI_SERVICE),
				"chatBroadcast", mHandler);
		Log.d(TAG, "carretera lista");

		Button boton = (Button) findViewById(R.id.Button01);
		boton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				try {
					carretera.enviarMensaje(ventanaEnviado.getText().toString()
							.trim());
					ventanaEnviado.setText("");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, "carretera.enviarMensaje() ha fallado");
					e.printStackTrace();
				}
			}
		});
		Log.d(TAG, "Boton listo");

	}
	
	@Override
	 public void onDestroy() {
		// TODO Auto-generated method stub
		super.onPause();
		
		//Cierra el socket
		carretera.cerrar();
	}

	

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			// byte[] writeBuf = (byte[]) msg.obj;
			// construct a string from the buffer
			// String writeMessage = new String(writeBuf);
			// setTextRecibido(writeMessage);
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

	};

}
