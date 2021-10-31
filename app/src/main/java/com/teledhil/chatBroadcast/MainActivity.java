package com.teledhil.chatBroadcast;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.os.Build;

import java.io.IOException;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	public static final int MENSAJE_UDP_BROADCAST_OK = 1;
	public static final int MENSAJE_UDP_BROADCAST_RECIBIDO = 2;
	public static final int MENSAJE_UDP_BROADCAST_ERROR = 3;

	UDPBroadcast carretera;

	String textoEnviado;

	EditText ventanaEnviado;
	ListView ventanaRecibido;
    private Handler mHandler;
	private ArrayAdapter<String> arrayRecibidos;

	public void setTextRecibido(String mensaje) {
		arrayRecibidos.add(mensaje);
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

		arrayRecibidos = new ArrayAdapter<>(this, R.layout.mensaje);
		ventanaRecibido = findViewById(R.id.ListView01);
        ventanaRecibido.setAdapter(arrayRecibidos);
		Log.d(TAG, "ventanaRecibido listo");

		mHandler = new MessageHandler(this);

		carretera = new UDPBroadcast(
				(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE),
				mHandler);
		Log.d(TAG, "carretera lista");

		Button boton = findViewById(R.id.Button01);
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


        Window w = getWindow(); // in Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(0x00000000);  // transparent
            w.setNavigationBarColor(0x0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        }

	}
	
	@Override
	 public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        super.onPause();

        //Cierra el socket
        carretera.cerrar();
    }

	


}
