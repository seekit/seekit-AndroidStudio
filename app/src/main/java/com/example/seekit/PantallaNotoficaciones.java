package com.example.seekit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import contenedor.TriCompartido;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaNotoficaciones extends Activity {
	JSONObject json = null;
	int statusCode = -1;
	JSONArray jsonArray = null;
	private ListView lista;
	String confirmarTri=null;
	String idUsuarioPropietario=null;
	String idTri=null;
	String ip=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pantalla_notoficaciones);
        //IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip=bundle1.getString("ip");
        }catch(Exception e){

            e.printStackTrace();
        }
        //fin IP

		try {
			json = new JSONObject(getIntent().getStringExtra("json"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (isNetworkAvailable()) {

			GetNotificacionesTask getNotificacionesTask = new GetNotificacionesTask();
			getNotificacionesTask.execute();
		} else {
			Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG)
					.show();
		}
	}

	private class GetNotificacionesTask extends
			AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {

			JSONObject jsonResponse = null;

			try {

				HttpClient client = new DefaultHttpClient();
				String id = json.getString("idUsuario");
				String url = "http://"+ ip +"/seekit/seekit/getNotificaciones?idUsuario="
						+ id;
				Log.d("url",url);
				HttpGet httpGet = new HttpGet(url);

				try {
					StringBuilder builder = new StringBuilder();
					HttpResponse response = client.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					statusCode = statusLine.getStatusCode();
					if (statusCode == 200) {
						Log.i("viene bien1,5", "Con el default client");
						HttpEntity entity = response.getEntity();
						InputStream content = entity.getContent();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(content));
						String line;

						while ((line = reader.readLine()) != null) {
							builder.append(line);
							Log.d("Pantalla Notificaciones", line);
							jsonArray = new JSONArray(line);

						}
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
			// si anda bien, voy a pasar el objeto a la otra intent
			if (statusCode == 200) {
				try {

					

					ArrayList<TriCompartido> arrayListaTrisCompartidos = new ArrayList<TriCompartido>();
					
					JSONArray listaTrisCompartido=null;

						listaTrisCompartido=jsonArray;
						


					
						
						Log.d("tamanio de ilista compartisos", listaTrisCompartido.length()+"");
						for (int j = 0; j < listaTrisCompartido.length(); j++) {
							Log.d("Nest step=", "lista de compartidos");
							TriCompartido triObjComp =new TriCompartido();
							triObjComp.setActivo(listaTrisCompartido.getJSONObject(j).getString("activo"));
							triObjComp.setCompartido(listaTrisCompartido.getJSONObject(j).getString("compartido"));
							triObjComp.setFoto(listaTrisCompartido.getJSONObject(j).getString("foto"));
							triObjComp.setHabilitado(listaTrisCompartido.getJSONObject(j).getString("habilitado"));
							triObjComp.setIdentificador(listaTrisCompartido.getJSONObject(j).getString("identificador"));
							triObjComp.setIdTri(listaTrisCompartido.getJSONObject(j).getString("idTri"));
							triObjComp.setLatitud(listaTrisCompartido.getJSONObject(j).getString("latitud"));
                            triObjComp.setLongitud(listaTrisCompartido.getJSONObject(j).getString("longitud"));
							triObjComp.setNombre(listaTrisCompartido.getJSONObject(j).getString("nombre"));
							triObjComp.setPerdido(listaTrisCompartido.getJSONObject(j).getString("perdido"));
							triObjComp.setNombrePropetario(listaTrisCompartido.getJSONObject(j).getString("nombreUsuario"));
							triObjComp.setApellidoPropietario(listaTrisCompartido.getJSONObject(j).getString("apellidoUsuario"));
							triObjComp.setMailPropietario(listaTrisCompartido.getJSONObject(j).getString("mailUsuario"));
							triObjComp.setIdUsuarioPropietario(listaTrisCompartido.getJSONObject(j).getString("idUsuario"));
							
							arrayListaTrisCompartidos.add(triObjComp);
							
						}
		                
		              reloadListCompartidos(arrayListaTrisCompartidos);

					
					
					

				} catch (JSONException e) {

					e.printStackTrace();
				}

			} else if (statusCode == 0) {

			} else {

			}

		}

	}
	private void reloadListCompartidos(
			ArrayList<TriCompartido> arrayListaTrisCompartidos) {
		lista = (ListView) findViewById(R.id.ListViewNotificaciones);
		lista.setAdapter(new Lista_adaptador(this, R.layout.entradanotificaciones, arrayListaTrisCompartidos) {
			@Override
			// con el onEntrada voy a insertar todos los textos
			// corerespondientes a cada entrada.
			public void onEntrada(final Object entrada, View view) {
				if (entrada != null) {
					final TextView nombreUsuario = (TextView) view
							.findViewById(R.id.textViewNombreUsuarioPantallNotificaciones);
					if (nombreUsuario != null)
						nombreUsuario
								.setText(((TriCompartido) entrada)
										.getNombrePropetario());

					final TextView nombreApellido = (TextView) view
							.findViewById(R.id.textViewApellidoUsuarioPantallaNotificaciones);
					if (nombreApellido != null)
						nombreApellido
								.setText(((TriCompartido) entrada)
										.getApellidoPropietario());
					
					final TextView mailUsuario = (TextView) view
							.findViewById(R.id.textViewMailUsuarioPantallaNotificaciones);
					if (mailUsuario != null)
						mailUsuario
								.setText(((TriCompartido) entrada)
										.getMailPropietario());
					
					final TextView nombreTri = (TextView) view
							.findViewById(R.id.textViewNombreTriPantallaNotificaciones);
					if (nombreTri != null)
						nombreTri
								.setText(((TriCompartido) entrada)
										.getNombre());
					
//imagen en el caso de que se quiera aceptarar el Tri
					final ImageView imagen_aceptar = (ImageView) view
							.findViewById(R.id.imageButtonAceptPantallaNotificaciones);
					if (imagen_aceptar != null) {

						// hago que la imagen sea clickeable
						imagen_aceptar
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										if (isNetworkAvailable()) {
											idUsuarioPropietario=((TriCompartido) entrada).getIdUsuarioPropietario();
											confirmarTri="0";
											idTri=((TriCompartido) entrada).getIdTri();
											GetConfirmarTask getConfirmarTask = new GetConfirmarTask();
											getConfirmarTask.execute();
										} else {
											Toast.makeText(PantallaNotoficaciones.this, "Network is unavailable!", Toast.LENGTH_LONG)
													.show();
										}
											
									}

								});
						// fin
					}
					//imagen en el caso de que se quiera denegar el tri
					final ImageView imagen_negar = (ImageView) view
							.findViewById(R.id.imageButtonDenyPantallaNotificaciones);
					if (imagen_negar != null) {

						// hago que la imagen sea clickeable
						imagen_negar
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										if (isNetworkAvailable()) {
											confirmarTri="1";
											idUsuarioPropietario=((TriCompartido) entrada).getIdUsuarioPropietario();
											idTri=((TriCompartido) entrada).getIdTri();
											GetConfirmarTask getConfirmarTask = new GetConfirmarTask();
											getConfirmarTask.execute();
										} else {
											Toast.makeText(PantallaNotoficaciones.this, "Network is unavailable!", Toast.LENGTH_LONG)
													.show();
										}

									}

								});
						// fin
					}

				}

			}
		});
		
	}
	private class GetConfirmarTask extends
	AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {

			JSONObject jsonResponse = null;

			try {

				HttpClient client = new DefaultHttpClient();
				String id = json.getString("idUsuario");
				String url = "http://"+ip+"/seekit/seekit/confirmarTri?idUsuario="
						+ id+"&idUsuarioPropietario="+idUsuarioPropietario+"&idTri="+idTri+"&confirmar="+confirmarTri;
				Log.d("confirmando?",url);
				HttpGet httpGet = new HttpGet(url);

				try {
					
					HttpResponse response = client.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					statusCode = statusLine.getStatusCode();
					if (statusCode == 200) {					}
                        recreate();
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
			if(statusCode==200){
				refresquemos();
			}else{
				Toast.makeText(PantallaNotoficaciones.this, "No se pudo procesar su solicitud.", Toast.LENGTH_LONG)
				.show();
			}
			
		}
		
	}

    private void refresquemos() {
        recreate();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pantalla_notoficaciones, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
