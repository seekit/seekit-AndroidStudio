package com.example.seekit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Array;
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

import com.example.backgroundTasks.MyAlarmReceiver;
import com.example.backgroundTasks.MyAlarmReceiver2;

import contenedor.Tri;
import contenedor.TriCompartido;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private ListView lista;

	JSONObject json = null;
	int statusCode = -1;
	JSONArray jsonArray = null;
	String parentActivity = null;
	private String identificadoresSchedule="";
	//empecemos con algo de guardar los datos
	SharedPreferences pref = null;
	Editor editor = null;
    String ip=null;
	// fin pruebas guardar datos

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listado);
        //preferencias
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        editor = pref.edit();
        //fin preferencias

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


				JSONArray listaTris = json.getJSONArray("listaTris");
				ArrayList<Tri> arrayListaTris = new ArrayList<Tri>();
				for (int i = 0; i < listaTris.length(); i++) {
					Tri triObj = new Tri(listaTris.getJSONObject(i).getString(
							"idTri"), listaTris.getJSONObject(i).getString(
							"identificador"), listaTris.getJSONObject(i)
							.getString("nombre"), listaTris.getJSONObject(i)
							.getString("foto"), listaTris.getJSONObject(i)
							.getString("activo"), listaTris.getJSONObject(i)
							.getString("latitud"), listaTris.getJSONObject(i)
                            .getString("longitud"), listaTris
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
            String triExistente;
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
										"latitud"),
                                listaTris.getJSONObject(i).getString(
                                        "longitud"), listaTris
										.getJSONObject(i).getString("perdido"),
								listaTris.getJSONObject(i).getString(
										"compartido"), listaTris
                                .getJSONObject(i).getString("descripcion"));


                        //ese metodo va a crear una entrada en las preferencias, para q puedan ser accedidas desde otro lado
                        cargarLasSharedPreferences(listaTris.getJSONObject(i)
                                .getString("identificador"));


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
						triObjComp.setLatitud(listaTrisCompartido
                                .getJSONObject(j).getString("latitud"));
                        triObjComp.setLongitud(listaTrisCompartido
                                .getJSONObject(j).getString("longitud"));
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

                        //ese metodo va a crear una entrada en las preferencias, para q puedan ser accedidas desde otro lado
                        cargarLasSharedPreferences(listaTris.getJSONObject(j)
                                .getString("identificador"));

					}
					reloadList(arrayListaTris);
					reloadListCompartidos(arrayListaTrisCompartidos);

					// scheduler
					//scheduleAlarm();
                    //scheduler2
                    //scheduleAlarm2();

				} catch (JSONException e) {

					e.printStackTrace();
				}

			} else if (statusCode == 0) {

			} else {

			}

		}
        //cargo en las preferencias los Tris
        private void cargarLasSharedPreferences(String identificador) {
            String aux = pref.getString(identificador, "null");
            if (aux.equals("null")) {
                editor.putString(identificador, "1");
                editor.commit();
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
                        imagen_entrada.setImageBitmap(convertToImage(((Tri) entrada).getFoto()));

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
												.getLatitud();
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



	public void reloadList(ArrayList<Tri> listaTris) {
        Array[] trisActivos =new Array[listaTris.size()];
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
								.getDescripcion());
                    //aqui haremos lo del toggle!!, actualizaremos los tris.
                    ToggleButton toggle = (ToggleButton) view.findViewById(R.id.muteToggleButton);
                    toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                editor.putString(((Tri) entrada).getIdentificador(),"1");
                                editor.commit();

                            } else {
                                editor.putString(((Tri) entrada).getIdentificador(),"0");
                                editor.commit();

                            }
                        }
                    });

                    //fin del toggle.



					final ImageView imagenEntradaImg = (ImageView) view
							.findViewById(R.id.imageView_imagen);
					if (imagenEntradaImg != null) {

						//imagenEntradaImg.setImageResource(R.drawable.im_pavo);
                        imagenEntradaImg.setImageBitmap(convertToImage(((Tri) entrada).getFoto()));


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
										String identificador = ((Tri) entrada).getIdentificador();
										String nombre = textoSuperiorNombre
												.getText().toString();
										String img = ((Tri) entrada).getFoto();
										String perdido = ((Tri) entrada)
												.getPerdido();
										String ubicacion = ((Tri) entrada)
												.getLatitud()+" "+((Tri) entrada)
                                                .getLongitud();
                                        String descripcion = ((Tri) entrada)
                                                .getDescripcion();
										Log.d("MAin Acivity", " identificador:"
												+ identificador);
										Log.d("MAin Acivity", "nom:" + nombre);
										Log.d("MAin Acivity", "img:" + img);
                                        Log.d("MAin Acivity", "descripcion:" + descripcion);

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
                                        intent.putExtra("descripcion", descripcion);

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
				Log.d("HOUSTONNNN", "WIIIIIUUUU WIIIIUUUU WIIIIUUUU");
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
		int intervalMillis = 20000; // 20 seconds
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
        int intervalMillis = 15000; // 15 seconds
        AlarmManager alarm = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                intervalMillis, pIntent);
    }
//cancelamos las alarmas.
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
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    //transforma el string a una imagen
    public Bitmap convertToImage(String image){
        try{
            Log.d("imagen es", image);
            //image="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";
            //Log.d("imagen es", image);
            /*byte[] decodedByte = Base64.decode(image, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);*/

            InputStream stream = new ByteArrayInputStream(Base64.decode(image.getBytes(), Base64.DEFAULT));

            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            Log.v("Ben", "Image Converted");
            return bitmap;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
