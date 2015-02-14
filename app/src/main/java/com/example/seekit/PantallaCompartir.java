package com.example.seekit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import contenedor.Tri;
import contenedor.Usuario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaCompartir extends Activity {
	String identificador = null;
	String nombreTri = null;
	String img = null;
	int statusCode = -1;
	String idTri = null;
	JSONArray jsonArray = null;
	String parentActivity = null;
	private ListView lista;
	JSONObject json = null;
	String idUsuarioLogueado = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pantalla_compartir);

		identificador = getIntent().getStringExtra("identificador");
		nombreTri = getIntent().getStringExtra("nombreTri");
		img = getIntent().getStringExtra("img");
		idTri = getIntent().getStringExtra("idTri");

		try {
			json = new JSONObject(getIntent().getStringExtra("json"));
			idUsuarioLogueado = json.getString("idUsuario").toString();
		} catch (JSONException e) {

			e.printStackTrace();
		}

		if (isNetworkAvailable()) {

			GetCompartidosTriTask getPendientesCompartirTriTask = new GetCompartidosTriTask();
			getPendientesCompartirTriTask.execute();
		} else {
			Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pantalla_compartir, menu);
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

	public void compartirTri(View view) {
		EditText eEditMail = (EditText) findViewById(R.id.editMailPantallaCompartir);
		String mail = eEditMail.getText().toString();

		EditText eEditDescripcion = (EditText) findViewById(R.id.editDescripcionPantallaCompartir);
		String editDescripcion = eEditDescripcion.getText().toString();

		if (TextUtils.isEmpty(editDescripcion)) {
			eEditDescripcion.setError("Por favor, ingrese comentario");
			return;
		}
		if (TextUtils.isEmpty(mail)) {
			eEditMail.setError("Por favor, introduzca un mail");
			return;
		} else {
			if (!isEmailValid(mail)) {
				eEditMail.setError("Formato invalido");
				return;
			}
		}

		if (isNetworkAvailable()) {

			GetCompartirTriTask getCompartirTriTask = new GetCompartirTriTask();
			getCompartirTriTask.execute();
		} else {
			Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG)
					.show();
		}

	}

	// esto es para obtener todos a quienes compartiste este hermoso tri
	private class GetCompartidosTriTask extends
			AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {

			JSONObject jsonResponse = null;
			HttpClient client = new DefaultHttpClient();
			String url = "http://192.168.56.1:8080/seekit/seekit/triCompartido?idTri="
					+ idTri;
			Log.d("pantalla Compartir", url);
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

						jsonArray = new JSONArray(line);

					}
				}
			} catch (Exception e) {

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
			Log.d("pantalla compartir", statusCode + "");
			if (statusCode == 200) {
				try {

					// JSONObject obj = new
					// JSONObject(getIntent().getStringExtra(
					// "json"));
					ArrayList<Usuario> arrayListaUsuario = new ArrayList<Usuario>();
					JSONArray listaTris = null;

					listaTris = jsonArray;

					for (int i = 0; i < listaTris.length(); i++) {
						Usuario usuObj = new Usuario();
						usuObj.setIdUsuario(listaTris.getJSONObject(i)
								.getString("idUsuario"));
						usuObj.setNombre(listaTris.getJSONObject(i).getString(
								"nombre"));
						usuObj.setApellido(listaTris.getJSONObject(i)
								.getString("apellido"));
						usuObj.setMail(listaTris.getJSONObject(i).getString(
								"mail"));

						arrayListaUsuario.add(usuObj);

						if (i == listaTris.length() - 1) {
							Log.d("Nest step=", "Reload Lista De Tris");

							reloadList(arrayListaUsuario);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (statusCode == 0) {
				Toast.makeText(PantallaCompartir.this,
						"El servidor no ha respondido", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(PantallaCompartir.this,
						"Si este mensaje aparece, entra en panico",
						Toast.LENGTH_SHORT).show();
			}
			// String aux = aux =
			// result.getJSONObject("usuario").getString("nombre");

		}

	}

	private void reloadList(ArrayList<Usuario> arrayListaUsuario) {
		lista = (ListView) findViewById(R.id.ListView_listadoPantallaCompartir);
		lista.setAdapter(new Lista_adaptador(this,
				R.layout.entradacompatidostri, arrayListaUsuario) {
			@Override
			// con el onEntrada voy a insertar todos los textos
			// corerespondientes a cada entrada.
			public void onEntrada(final Object entrada, View view) {
				if (entrada != null) {
					final TextView texto_nombre = (TextView) view
							.findViewById(R.id.textViewNombrePantallaCompartir);
					if (texto_nombre != null)
						texto_nombre.setText(((Usuario) entrada).getNombre());

					final TextView texto_apellido = (TextView) view
							.findViewById(R.id.textViewApellidoPantallaCompartir);
					if (texto_apellido != null)
						texto_apellido.setText(((Usuario) entrada)
								.getApellido());
					
					final TextView texto_descripcion = (TextView) view
							.findViewById(R.id.textViewMailPantallaCompartir);
					if (texto_descripcion != null)
						texto_descripcion.setText(((Usuario) entrada)
								.getMail());

					final ImageView imagen_cancelar = (ImageView) view
							.findViewById(R.id.imageButtonAceptPantallaCompartir);
					if (imagen_cancelar != null) {

						// hago que la imagen sea clickeable
						imagen_cancelar
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										if (isNetworkAvailable()) {
											GetDescompartirTriTask getDescompartirTriTask = new GetDescompartirTriTask();
											getDescompartirTriTask.setIdUsuario(((Usuario) entrada)
													.getIdUsuario());
											
											getDescompartirTriTask.execute();
											
										} else {

										}
									}

								});

					}

				}

			}
		});

	}

	private class GetDescompartirTriTask extends
			AsyncTask<Object, Void, JSONObject> {
		
		public String idUsuario=null;
		void setIdUsuario(String idUsuario){
			this.idUsuario=idUsuario;
		}

		@Override
		protected JSONObject doInBackground(Object... params) {
			

			JSONObject jsonResponse = null;
			HttpClient client = new DefaultHttpClient();
			String url = "http://192.168.56.1:8080/seekit/seekit/descompartirTri?idTri="+idTri+"&idUsuario="+idUsuario;
			Log.d("panalla compartir", url);
			HttpGet httpGet = new HttpGet(url);

			try {

				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				statusCode = statusLine.getStatusCode();

			} catch (Exception e) {

				e.printStackTrace();

			}
			return jsonResponse;

		}
		@Override
		protected void onPostExecute(JSONObject result) {

			handleResult(result);
		}

		private void handleResult(JSONObject result) {
			if(statusCode==200){
			recreate();
			}
		}

	}

	// esto es en el caso de que el muchacho quiera compartir un TRI
	private class GetCompartirTriTask extends
			AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			EditText eEditMail = (EditText) findViewById(R.id.editMailPantallaCompartir);
			String mail = eEditMail.getText().toString();

			EditText eEditDescripcion = (EditText) findViewById(R.id.editDescripcionPantallaCompartir);
			String editDescripcion = eEditDescripcion.getText().toString();

			JSONObject jsonResponse = null;
			HttpClient client = new DefaultHttpClient();
			String descUTF8 = null;
			try {
				descUTF8 = URLEncoder.encode(editDescripcion, "utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String url = "http://192.168.56.1:8080/seekit/seekit/compartirTri?mailUsuACompartir="
					+ mail
					+ "&idUsuario="
					+ idUsuarioLogueado
					+ "&idTri="
					+ idTri
					+ "&habilitado="
					+ "0"
					+ "&descripcion="
					+ descUTF8;
			Log.d("panalla compartir", url);
			HttpGet httpGet = new HttpGet(url);

			try {
				StringBuilder builder = new StringBuilder();
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				statusCode = statusLine.getStatusCode();
				Log.d("Pantalla compartir, status code=", statusCode + "");
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;

					while ((line = reader.readLine()) != null) {
						builder.append(line);
						jsonResponse = new JSONObject(line);

					}
				}
			} catch (Exception e) {

				e.printStackTrace();

			}
			return jsonResponse;

		}

		@Override
		protected void onPostExecute(JSONObject result) {

			handleResult(result);
		}

		private void handleResult(JSONObject jsonObj) {
			// si anda bien, voy a pasar el objeto a la otra intent
			if (statusCode == 200) {
				recreate();
			} else if (statusCode == 0) {
				Toast.makeText(PantallaCompartir.this,
						"El servidor no ha respondido", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(PantallaCompartir.this,
						"No se ha podido compartir este dispositivo",
						Toast.LENGTH_SHORT).show();
			}
			// String aux = aux =
			// result.getJSONObject("usuario").getString("nombre");

		}
	}

	public boolean isEmailValid(String email) {
		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches())
			return true;
		else
			return false;
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
}
