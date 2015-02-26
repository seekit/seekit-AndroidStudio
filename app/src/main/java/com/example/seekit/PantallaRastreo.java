package com.example.seekit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaRastreo extends Activity {
	// para la btencion de datos del servidor
	int statusCode = -1;
    String ip=null;
	// datos del Tri recuperado
	String nombreTri = null;
	String idTri = null;
	String identificador = null;
	String imgTri = null;
	String ubicacion = null;
	String perdido = null;
    String descripcion=null;


	// los 3 botones identificando las acciones posibles
	ImageButton imageButtonShare;
	ImageButton imageButtonLost;
	ImageButton imageButtonMap;

	JSONObject json;

	String parentActivity = null;
	boolean deboRecargarMainActvity=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pantalla_rastreo);

        //IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip=bundle1.getString("ip");
        }catch(Exception e){

            e.printStackTrace();
        }
        //fin IP
		// recupero los datos necesarios (por el momento) de el tri clikeado

		nombreTri = getIntent().getStringExtra("nombreTri");
		idTri = getIntent().getStringExtra("idTri");
		identificador = getIntent().getStringExtra("identificador");
		ubicacion = getIntent().getStringExtra("ubicacion");
		perdido = getIntent().getStringExtra("perdido");
        descripcion = getIntent().getStringExtra("descripcion");
		
		TextView informe = (TextView) findViewById(R.id.textViewInformePerdido);
			if(perdido.equals("0")){
				informe.setText("Tri NO perdido");
			}else{
				informe.setText("Tri perdido");
			}
		JSONObject obj;
		try {
			obj = new JSONObject(getIntent().getStringExtra("json"));
			json = obj;
		} catch (JSONException e) {
			Log.d("Pantalla Rastreo", "ACA NO DEBOE STAR ");
			e.printStackTrace();
		}

		ImagenesListeners();
	}

	private void ImagenesListeners() {
		imageButtonShare = (ImageButton) findViewById(R.id.imageButtonShare);
		imageButtonShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Log.d("pantalla Rastreo", "Share");
				Intent intent = new Intent(PantallaRastreo.this,
						ShareActivity.class);
				try {

					Log.d("Pantala Rastreo", json.toString());
					// pongo solo el usuario, el resto no lo quiero ya q lo voy
					// a
					// recuperar cada vez q entre.

					intent.putExtra("json", json.toString());

				} catch (Exception e) {
					Log.d("Pantalla Rastreo", "ACA NUNCA DEBO ESTAR");
				}
				intent.putExtra("identificador",
						getIntent().getStringExtra("identificador"));
				intent.putExtra("nombreTri",
						getIntent().getStringExtra("nombreTri"));
				intent.putExtra("img", getIntent().getStringExtra("img"));
				intent.putExtra("idTri", getIntent().getStringExtra("idTri"));
				startActivity(intent);

			}

		});
		// en caso de que presiona el botton marcar como perdido, cosas deben
		// pasar.
		imageButtonLost = (ImageButton) findViewById(R.id.imageButtonLost);
		imageButtonLost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				marcarComoPerdido();

			}

		});
		imageButtonMap = (ImageButton) findViewById(R.id.imageButtonMap);
		imageButtonMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(PantallaRastreo.this, Maps.class);
				startActivity(intent);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pantalla_rastreo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_editTri) {
			Log.d("Pantalla Rastreo", "editar Tri");
			Intent intent = new Intent(PantallaRastreo.this, EditarTri.class);
			try {

				Log.d("Pantala Rastreo", json.toString());
				// pongo solo el usuario, el resto no lo quiero ya q lo voy a
				// recuperar cada vez q entre.

				intent.putExtra("json", json.toString());

			} catch (Exception e) {
				Log.d("Pantalla RastreoR", "ACA NUNCA DEBO ESTAR");
			}
			intent.putExtra("identificador",
					getIntent().getStringExtra("identificador"));
			intent.putExtra("nombreTri", getIntent()
					.getStringExtra("nombreTri"));
			intent.putExtra("img", getIntent().getStringExtra("img"));
			intent.putExtra("idTri", getIntent().getStringExtra("idTri"));
            intent.putExtra("descripcion", getIntent().getStringExtra("descripcion"));
            intent.putExtra("foto",getIntent().getStringExtra("foto"));

			startActivity(intent);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void marcarComoPerdido() {
		if (isNetworkAvailable()) {

			GetLostTask getLostTask = new GetLostTask();
			getLostTask.execute();
		} else {
			Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG)
					.show();
		}

	}

	private class GetLostTask extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			JSONObject jsonResponse = null;
			try {

				HttpClient client = new DefaultHttpClient();

				// si el Tri no esta perdido, se tiene que perder, si esta
				// perdido, se
				// tiene que desperder (no es lo mismo que encontrar)
				String url = null;
				if (perdido.equals("0")) {

					url = "http://"+ip+"/seekit/seekit/marcarComoPerdido?idTri="
							+ idTri + "&idUsuario=" + json.getString("idUsuario");
					perdido="1";
				} else if(perdido.equals("1")){
					url = "http://"+ip+"/seekit/seekit/desmarcarComoPerdido?idTri="
							+ idTri + "&idUsuario=" + json.getString("idUsuario");
					perdido="0";
				}else{
					
					//aun no se
				}

				Log.d("rastreo", url);
				HttpGet httpGet = new HttpGet(url);

				try {

					HttpResponse response = client.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					statusCode = statusLine.getStatusCode();

					if (statusCode == 200) {

					}

				} catch (Exception e) {

					e.printStackTrace();

				}

			} catch (Exception e) {
				Log.d("PORQUE NO ANDA4", "Unsuccessful HTTP Response Code:");
				e.printStackTrace();
			}
			return jsonResponse;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			handleResult();
		}

		private void handleResult() {
			if (statusCode == 200) {
				TextView informe = (TextView) findViewById(R.id.textViewInformePerdido);
				deboRecargarMainActvity=true;
				if(perdido.equals("0")){
					informe.setText("Tri NO perdido");					
				}else if(perdido.equals("1")){
					informe.setText("Tri perdido");
				}


			} else {
				mostrarMensaje();
			}
			
		}

	}
	
	
	//SI alguien presiono el boton de ir hacia atras de un android antes de presionar el cargar como perdido, debo recrear el padre, ya que la info esta corrupta.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		Log.d("aaa",deboRecargarMainActvity+"");
	    if (deboRecargarMainActvity==true) {
	    	Intent intent=new Intent(PantallaRastreo.this,MainActivity.class);
	    	intent.putExtra("PARENT_NAME","PantallaRastreo");
	    	intent.putExtra("json", json.toString());
	    	startActivity(intent);
	    	finish();
	    	
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private boolean isNetworkAvailable() {

		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}

		return isAvailable;
	}
	private void mostrarMensaje(){
		Toast.makeText(this, "Error Inesperado", Toast.LENGTH_LONG)
		.show();
	}



}
