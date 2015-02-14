package com.example.seekit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.backgroundTasks.MyAlarmReceiver;
import com.example.backgroundTasks.MyAlarmReceiver2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import contenedor.Tri;
import contenedor.TriCompartido;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView lista;

	JSONObject json = null;
	int statusCode = -1;
	JSONArray jsonArray = null;
	String parentActivity = null;
	private String identificadoresSchedule = ",";
	// empecemos con algo de guardar los datos
	SharedPreferences pref = null;
	Editor editor = null;
    String ip=null;
	// fin pruebas guardar datos

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listado);
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

			e1.printStackTrace();
		}
		// List<Tri> listaTris = ObtenerJson.obtenerTrisDeUsuario(idUsuario);
		// ObtenerJson.obtenerTrisDeUsuario(idUsuario);

		// obtenerTrisDeUsuario(idUsuario);
		// fin obtencion Json
		//
		parentActivity = getIntent().getStringExtra("PARENT_NAME");

		if (parentActivity.equals("Login2")) {
			try {

				Log.d("Actividad padre?", parentActivity);

				json.getJSONObject("usuario").getString("nombre");
				JSONArray listaTris = json.getJSONArray("listaTris");
				ArrayList<Tri> arrayListaTris = new ArrayList<Tri>();
				for (int i = 0; i < listaTris.length(); i++) {
					Tri triObj = new Tri(listaTris.getJSONObject(i).getString(
							"idTri"), listaTris.getJSONObject(i).getString(
							"identificador"), listaTris.getJSONObject(i)
							.getString("nombre"), listaTris.getJSONObject(i)
							.getString("foto"), listaTris.getJSONObject(i)
							.getString("activo"), listaTris.getJSONObject(i)
							.getString("localizacion"), listaTris
							.getJSONObject(i).getString("perdido"), listaTris
							.getJSONObject(i).getString("compartido"), listaTris
                            .getJSONObject(i).getString("descripcion"));
					arrayListaTris.add(triObj);
					Log.d("i=", i + "");
					if (i == listaTris.length() - 1) {
						Log.d("Nest step=", "Reload Lista De Tris");
						reloadList(arrayListaTris);
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}
		} else if (parentActivity.equals("Registro")) {

		} else if (parentActivity.equals("addTri")) {
			Log.d("Estoy en el main, mi padre es el:", "addTri");
			if (isNetworkAvailable()) {

				GetMainActivityTask getMainActivityTask = new GetMainActivityTask();
				getMainActivityTask.execute();
			} else {
				Toast.makeText(this, "Network is unavailable!",
						Toast.LENGTH_LONG).show();
			}
		} else if (parentActivity.equals("EditarUsuario")) {
			Log.d("Estoy en el main, mi padre es el:", "EditarUsuario");
			if (isNetworkAvailable()) {

				GetMainActivityTask getMainActivityTask = new GetMainActivityTask();
				getMainActivityTask.execute();
			} else {
				Toast.makeText(this, "Network is unavailable!",
						Toast.LENGTH_LONG).show();
			}
		} else if (parentActivity.equals("EditarTri")) {
			Log.d("Estoy en el main, mi padre es el:", "EditarTri");
			if (isNetworkAvailable()) {

				GetMainActivityTask getMainActivityTask = new GetMainActivityTask();
				getMainActivityTask.execute();
			} else {
				Toast.makeText(this, "Network is unavailable!",
						Toast.LENGTH_LONG).show();
			}
		} else if (parentActivity.equals("Login")) {

			Log.d("Estoy en el main, mi padre es el:", "login");
			if (isNetworkAvailable()) {

				GetMainActivityTask getMainActivityTask = new GetMainActivityTask();
				getMainActivityTask.execute();
			} else {
				Toast.makeText(this, "Network is unavailable!",
						Toast.LENGTH_LONG).show();
			}

		} else if (parentActivity.equals("PantallaRastreo")) {

			Log.d("Estoy en el main, mi padre es el:", "pantalla reastreo");
			if (isNetworkAvailable()) {

				GetMainActivityTask getMainActivityTask = new GetMainActivityTask();
				getMainActivityTask.execute();
			} else {
				Toast.makeText(this, "Network is unavailable!",
						Toast.LENGTH_LONG).show();
			}

		} else if (parentActivity.equals("tester")) {
			Log.d("Estoy en el main, mi padre es el:", "MyAlarmTestService");
			if (isNetworkAvailable()) {

				GetMainActivityTask getMainActivityTask = new GetMainActivityTask();
				getMainActivityTask.execute();
			} else {
				Toast.makeText(this, "Network is unavailable!",
						Toast.LENGTH_LONG).show();
			}

		}

	}

	private class GetMainActivityTask extends
			AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {

			JSONObject jsonResponse = null;

			try {

				// ///////////7

				Log.d("Usuario logueado:", json.toString());
				HttpClient client = new DefaultHttpClient();
				String id = json.getString("idUsuario");
                String url="http://"+ip+"/seekit/seekit/getTris?idUsuario="
                                + id;
				Log.d("url",
						url);

				HttpGet httpGet = new HttpGet(url);

				try {
					StringBuilder builder = new StringBuilder();
					HttpResponse response = client.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					statusCode = statusLine.getStatusCode();
					if (statusCode == 200) {
						HttpEntity entity = response.getEntity();
						InputStream content = entity.getContent();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(content));
						String line;

						while ((line = reader.readLine()) != null) {
							builder.append(line);
							Log.d("mainactivity", line);
							jsonResponse = new JSONObject(line);

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

			handleResult(result);
		}

		private void handleResult(JSONObject result) {
			// si anda bien, voy a pasar el objeto a la otra intent
			Log.d("satuscode=", statusCode + "");
			if (statusCode == 200) {
				try {

					final ArrayList<Tri> arrayListaTris = new ArrayList<Tri>();
					final ArrayList<TriCompartido> arrayListaTrisCompartidos = new ArrayList<TriCompartido>();
					JSONArray listaTris = null;
					JSONArray listaTrisCompartido = null;

					listaTris = result.getJSONArray("listaTri");
					listaTrisCompartido = result
							.getJSONArray("listaTriCompartido");

					for (int i = 0; i < listaTris.length(); i++) {
						Log.d("Nest step=", "lista personal");
						Tri triObj = new Tri(listaTris.getJSONObject(i)
								.getString("idTri"), listaTris.getJSONObject(i)
								.getString("identificador"), listaTris
								.getJSONObject(i).getString("nombre"),
								listaTris.getJSONObject(i).getString("foto"),
								listaTris.getJSONObject(i).getString("activo"),
								listaTris.getJSONObject(i).getString(
										"localizacion"), listaTris
										.getJSONObject(i).getString("perdido"),
								listaTris.getJSONObject(i).getString(
										"compartido"), listaTris
                                .getJSONObject(i).getString("descripcion"));
						// es lo que se va a pasar al scheduler; para que los
						// busque.

						Log.d("aaa",
								listaTris.getJSONObject(i).getString(
										"identificador"));

						identificadoresSchedule = identificadoresSchedule
								.concat(listaTris.getJSONObject(i).getString(
										"identificador")
										+ ",");

						arrayListaTris.add(triObj);
						Log.d("i=", i + "");
						if (i == listaTris.length() - 1) {

						}
					}

					Log.d("tamanio de ilista compartisos",
							listaTrisCompartido.length() + "");
					for (int j = 0; j < listaTrisCompartido.length(); j++) {
						Log.d("Nest step=", "lista de compartidos");
						TriCompartido triObjComp = new TriCompartido();
						triObjComp.setActivo(listaTrisCompartido.getJSONObject(
								j).getString("activo"));
						triObjComp.setCompartido(listaTrisCompartido
								.getJSONObject(j).getString("compartido"));
						triObjComp.setFoto(listaTrisCompartido.getJSONObject(j)
								.getString("foto"));
						triObjComp.setHabilitado(listaTrisCompartido
								.getJSONObject(j).getString("habilitado"));
						triObjComp.setIdentificador(listaTrisCompartido
								.getJSONObject(j).getString("identificador"));
						triObjComp.setIdTri(listaTrisCompartido
								.getJSONObject(j).getString("idTri"));
						triObjComp.setLocalizacion(listaTrisCompartido
								.getJSONObject(j).getString("localizacion"));
						triObjComp.setNombre(listaTrisCompartido.getJSONObject(
								j).getString("nombre"));
						triObjComp.setPerdido(listaTrisCompartido
								.getJSONObject(j).getString("perdido"));
						arrayListaTrisCompartidos.add(triObjComp);
                        triObjComp.setDescripcion(listaTrisCompartido
                                .getJSONObject(j).getString("descripcion"));
						// es lo que se va a pasar al scheduler; para que los
						// busque.
						identificadoresSchedule = identificadoresSchedule
								.concat(listaTrisCompartido.getJSONObject(j)
										.getString("identificador") + ",");
					}
					reloadList(arrayListaTris);
					reloadListCompartidos(arrayListaTrisCompartidos);

					// scheduler
					scheduleAlarm();
                    //scheduler2
                    scheduleAlarm2();

				} catch (JSONException e) {

					e.printStackTrace();
				}

			} else if (statusCode == 0) {

			} else {

			}

		}
		// pruebas alarm
		// scheduleAlarm();

		// fin pruebas
	}

	private void reloadListCompartidos(
			ArrayList<TriCompartido> arrayListaTrisCompartidos) {
		lista = (ListView) findViewById(R.id.ListView_listadoCompartidos);
		lista.setAdapter(new Lista_adaptador(this, R.layout.entrada,
				arrayListaTrisCompartidos) {
			@Override
			// con el onEntrada voy a insertar todos los textos
			// corerespondientes a cada entrada.
			public void onEntrada(final Object entrada, View view) {
				if (entrada != null) {
					final TextView texto_superior_entrada = (TextView) view
							.findViewById(R.id.textView_superior);
					if (texto_superior_entrada != null)
						texto_superior_entrada
								.setText(((TriCompartido) entrada).getNombre());

					final TextView texto_inferior_entrada = (TextView) view
							.findViewById(R.id.textView_inferior);
					if (texto_inferior_entrada != null)
						texto_inferior_entrada
								.setText(((TriCompartido) entrada)
										.getIdentificador());

					final ImageView imagen_entrada = (ImageView) view
							.findViewById(R.id.imageView_imagen);
					if (imagen_entrada != null) {
						imagen_entrada.setImageResource(R.drawable.im_pavo);

						// hago que la imagen sea clickeable
						imagen_entrada
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										// al hacer click ira a la pantalla de
										// rastreo
										Intent intent = new Intent(
												MainActivity.this,
												PantallaRastreo.class);
										String identificador = texto_inferior_entrada
												.getText().toString();
										String nombre = texto_superior_entrada
												.getText().toString();
										String img = ((TriCompartido) entrada)
												.getFoto();
										String ubicacion = ((TriCompartido) entrada)
												.getLocalizacion();
										Log.d("MAin Acivity", " identificador:"
												+ identificador);
										Log.d("MAin Acivity", "nom:" + nombre);
										Log.d("MAin Acivity", "img:" + img);
										Log.d("MAin Acivity",
												" entrada:"
														+ ((TriCompartido) entrada)
																.getIdTri());

										if (parentActivity.equals("Login")) {
											intent.putExtra("json",
													json.toString());

										} else {
											intent.putExtra("json",
													json.toString());
										}

										intent.putExtra("identificador",
												identificador);
										intent.putExtra("nombreTri", nombre);
										intent.putExtra("img", img);
										intent.putExtra("ubicacion", ubicacion);
										intent.putExtra("idTri",
												((TriCompartido) entrada)
														.getIdTri());
										startActivity(intent);
										// Toast toast =
										// Toast.makeText(MainActivity.this,
										// Auxiliar,
										// Toast.LENGTH_LONG);
										// toast.show();
									}

								});
						// fin
					}

				}

			}
		});

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

	public void reloadList(ArrayList<Tri> listaTris) {

		Log.d("Main Activity", "Recargando la cachoputa lista");
		lista = (ListView) findViewById(R.id.ListView_listado);
		lista.setAdapter(new Lista_adaptador(this, R.layout.entrada, listaTris) {
			@Override
			// con el onEntrada voy a insertar todos los textos
			// corerespondientes a cada entrada.
			public void onEntrada(final Object entrada, View view) {
				if (entrada != null) {
					final TextView textoSuperiorNombre = (TextView) view
							.findViewById(R.id.textView_superior);
					if (textoSuperiorNombre != null)
						textoSuperiorNombre.setText(((Tri) entrada).getNombre());

					final TextView textoInferiorIdentificador = (TextView) view
							.findViewById(R.id.textView_inferior);
					if (textoInferiorIdentificador != null)
						textoInferiorIdentificador.setText(((Tri) entrada)
								.getNombre());

					final ImageView imagenEntradaImg = (ImageView) view
							.findViewById(R.id.imageView_imagen);
					if (imagenEntradaImg != null) {
						imagenEntradaImg.setImageResource(R.drawable.im_pavo);

						// hago que la imagen sea clickeable
						imagenEntradaImg
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										// al hacer click ira a la pantalla de
										// rastreo
										Intent intent = new Intent(
												MainActivity.this,
												PantallaRastreo.class);
										String identificador = textoInferiorIdentificador
												.getText().toString();
										String nombre = textoSuperiorNombre
												.getText().toString();
										String img = ((Tri) entrada).getFoto();
										String perdido = ((Tri) entrada)
												.getPerdido();
										String ubicacion = ((Tri) entrada)
												.getLocalizacion();
										Log.d("MAin Acivity", " identificador:"
												+ identificador);
										Log.d("MAin Acivity", "nom:" + nombre);
										Log.d("MAin Acivity", "img:" + img);

										if (parentActivity.equals("Login")) {
											intent.putExtra("json",
													json.toString());

										} else {
											intent.putExtra("json",
													json.toString());
										}

										intent.putExtra("identificador",
												identificador);
										intent.putExtra("nombreTri", nombre);
										intent.putExtra("img", img);
										intent.putExtra("ubicacion", ubicacion);
										intent.putExtra("idTri",
												((Tri) entrada).getIdTri());
										intent.putExtra("perdido", perdido);
										startActivity(intent);
										// Toast toast =
										// Toast.makeText(MainActivity.this,
										// Auxiliar,
										// Toast.LENGTH_LONG);
										// toast.show();
									}

								});
						// fin
					}

				}

			}
		});

		// esto es para que toda la lisa sea clicckeable, pero no queremos esto,
		// lo de por si acazooooo... acazoooooo
		/*
		 * lista.setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> pariente, View view,
		 * int posicion, long id) { Log.d("Myactivity", "asdasd"); Lista_entrada
		 * elegido = (Lista_entrada) pariente .getItemAtPosition(posicion);
		 * 
		 * CharSequence texto = "Seleccionado: " + elegido.get_textoDebajo();
		 * Toast toast = Toast.makeText(MainActivity.this, texto,
		 * Toast.LENGTH_LONG); toast.show(); } });
		 */

	}

	/*
	 * public void obtenerTrisDeUsuario(String idUsuario) { final List<Tri>
	 * listaTris = new ArrayList<Tri>(); AsyncHttpClient client = new
	 * AsyncHttpClient(); String url =
	 * "http://192.168.56.1:8080/seekit/seekit/getTris?idUsuario=" + idUsuario;
	 * Log.d("la urlll", url); client.get(url, new JsonHttpResponseHandler() {
	 * 
	 * @Override public void onSuccess(int statusCode, Header[] headers,
	 * JSONArray arrayTris) {
	 * 
	 * // Pull out the first event on the public timeline try {
	 * 
	 * for (int i = 0; i < arrayTris.length(); i++) {
	 * 
	 * JSONObject tri = (JSONObject) arrayTris.get(i); Tri triAux = new
	 * Tri(tri.getString("identificador"), tri.getString("nombre"),
	 * tri.getString("foto"), tri.getString("activo"), tri
	 * .getString("localizacion"), tri .getString("perdido"), tri
	 * .getString("compartido")); Log.d("la urlll", triAux.getNombre());
	 * listaTris.add(triAux);
	 * 
	 * }
	 * 
	 * reloadList(listaTris); } catch (JSONException e) { Log.e("MyActivity",
	 * "JSONException encontrada", e); }
	 * 
	 * }
	 * 
	 * }); // return listaTris; }
	 */

	// Infla todo lo que este en la action Bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		// MenuInflater inflater = getMenuInflater();
		// getMenuInflater().inflate(R.menu.main, menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * public boolean onCreateOptionsMenu(Menu menu) { // Inflate the menu; this
	 * adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */

	// When the user presses one of the action buttons or another item in the
	// action overflow, the system calls your activity's onOptionsItemSelected()
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_new) {
			Log.d("mainActrivity", "Agregar tri");
			Intent intent = new Intent(MainActivity.this, AddTri.class);
			try {
				Log.d("Add Tri", json.toString());
				// pongo solo el usuario, el resto no lo quiero ya q lo voy a
				// recuperar cada vez q entre.
				if (parentActivity.equals("Login")) {
					intent.putExtra("json", json.toString());
				} else {
					intent.putExtra("json", json.toString());
				}

			} catch (Exception e) {
				Log.d("ACA NUNCA DEBO ESTAR", "ACA NUNCA DEBO ESTAR");
			}

			startActivity(intent);

			return true;
		}
		if (id == R.id.action_edit_user) {
			Log.d("mainActrivity", "Vayamos a la pantalla edicion de usuario");
			Intent intent = new Intent(MainActivity.this, EditarUsuario.class);
			try {

				// pongo solo el usuario, el resto no lo quiero ya q lo voy a
				// recuperar cada vez q entre.
				if (parentActivity.equals("Login")) {
					intent.putExtra("json", json.toString());
				} else {
					intent.putExtra("json", json.toString());
				}

				startActivity(intent);
			} catch (Exception e) {
				Log.d("ACA NUNCA DEBO ESTAR", "ACA NUNCA DEBO ESTAR");
			}

			return true;
		}
		if (id == R.id.action_notification) {
			Log.d("mainActrivity", "Vayamos a la notificaiciones");
			Intent intent = new Intent(MainActivity.this,
					PantallaNotoficaciones.class);
			try {

				// pongo solo el usuario, el resto no lo quiero ya q lo voy a
				// recuperar cada vez q entre.

				intent.putExtra("json", json.toString());

				startActivity(intent);
			} catch (Exception e) {
				Log.d("ACA NUNCA DEBO ESTAR", "ACA NUNCA DEBO ESTAR");
			}

			return true;
		}

		if (id == R.id.action_logout) {
			Log.d("mainActrivity", "Realizando Logout");

			try {

				// pongo solo el usuario, el resto no lo quiero ya q lo voy a
				// recuperar cada vez q entre.
				pref = getApplicationContext()
						.getSharedPreferences("MyPref", 0);
				editor = pref.edit();
				editor.clear().commit();
				cancelAlarm();
                cancelAlarm2();
				Intent intent = new Intent(MainActivity.this, Login.class);
				startActivity(intent);
				MainActivity.this.finish();

			} catch (Exception e) {
				Log.d("ACA NUNCA DEBO ESTAR", "ACA NUNCA DEBO ESTAR");
			}

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// pruebas schedule
	public void scheduleAlarm() {
		// Construct an intent that will execute the AlarmReceiver
		Intent intent = new Intent(getApplicationContext(),
				MyAlarmReceiver.class);

		intent.putExtra("identificadores", identificadoresSchedule);
		intent.putExtra("json", json.toString());
		// Create a PendingIntent to be triggered when the alarm goes off
		final PendingIntent pIntent = PendingIntent.getBroadcast(this,
				MyAlarmReceiver.REQUEST_CODE, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// Setup periodic alarm every 50 seconds
		long firstMillis = System.currentTimeMillis(); // first run of alarm is
														// immediate
		int intervalMillis = 10000; // 5 seconds
		AlarmManager alarm = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
				intervalMillis, pIntent);
	}

    // pruebas schedule2
    public void scheduleAlarm2() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(),
                MyAlarmReceiver2.class);

        intent.putExtra("json", json.toString());
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this,
                MyAlarmReceiver.REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 50 seconds
        long firstMillis = System.currentTimeMillis(); // first run of alarm is
        // immediate
        int intervalMillis = 60000; // 60 seconds
        AlarmManager alarm = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                intervalMillis, pIntent);
    }

	public void cancelAlarm() {
		Intent intent = new Intent(getApplicationContext(),
				MyAlarmReceiver.class);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this,
				MyAlarmReceiver.REQUEST_CODE, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pIntent);
	}

    public void cancelAlarm2() {
        Intent intent = new Intent(getApplicationContext(),
                MyAlarmReceiver2.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this,
                MyAlarmReceiver.REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

	// fin pruebas schedule

}
