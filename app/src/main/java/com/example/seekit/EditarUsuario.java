package com.example.seekit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditarUsuario extends Activity {

	int statusCode = -1;
    String ip=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_usuario);

        //IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip=bundle1.getString("ip");
        }catch(Exception e){

            e.printStackTrace();
        }
        //fin IP
		JSONObject obj =null;
		try {
			obj = new JSONObject(getIntent().getStringExtra(
					"json"));
		} catch (JSONException e) {
			Log.d("Si estoy aca","Marchamos");
			e.printStackTrace();
		}
		//Debo re cargar los edits
		EditText eIngresarNombre = (EditText) findViewById(R.id.editUsuarioUserName);
		EditText eIngresarApellido = (EditText) findViewById(R.id.editUserUserApellido);
		EditText eIngresarMail = (EditText) findViewById(R.id.editUsuarioUserMail);
		EditText eIngresarContrasena = (EditText) findViewById(R.id.editUsuarioUserPass);
		try {
			eIngresarNombre.setText(obj.getString("nombre"));
			eIngresarApellido.setText(obj.getString("apellido"));
			eIngresarMail.setText(obj.getString("mail"));
			eIngresarContrasena.setText(obj.getString("contrasenia"));
		} catch (JSONException e) {
			Log.d("Si estoy aca","Marchamos");
			e.printStackTrace();
		}		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editar_usuario, menu);
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

	public void editarUsuario(View view) {

		EditText eIngresarNombre = (EditText) findViewById(R.id.editUsuarioUserName);
		String nombre = eIngresarNombre.getText().toString();

		EditText eIngresarApellido = (EditText) findViewById(R.id.editUserUserApellido);
		String apellido = eIngresarApellido.getText().toString();

		EditText eIngresarMail = (EditText) findViewById(R.id.editUsuarioUserMail);
		String mail = eIngresarMail.getText().toString();

		EditText eIngresarContrasena = (EditText) findViewById(R.id.editUsuarioUserPass);
		String contrasena = eIngresarContrasena.getText().toString();

		if (TextUtils.isEmpty(nombre)) {
			eIngresarNombre.setError("Por favor, introduzca su nombre");
			return;
		}
		if (TextUtils.isEmpty(apellido)) {
			eIngresarApellido.setError("Por favor, introduzca su apellido");
			return;
		}
		if (TextUtils.isEmpty(mail)) {
			eIngresarMail.setError("Por favor, introduzca su mail");
			return;
		} else {
			if (!isEmailValid(mail)) {
				eIngresarMail.setError("Formato invalido");
				return;
			}
		}
		if (TextUtils.isEmpty(contrasena)) {
			eIngresarContrasena.setError("Introduzca una contrasenia valida");
			return;
		}
		if (isNetworkAvailable()) {

			GetEditUserTask getEditUserTask = new GetEditUserTask();
			getEditUserTask.execute();
		} else {
			Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG)
					.show();
		}

	}

	private class GetEditUserTask extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			JSONObject jsonResponse = null;
			try {
				EditText eIngresarNombre = (EditText) findViewById(R.id.editUsuarioUserName);
				String nombre = eIngresarNombre.getText().toString();

				EditText eIngresarApellido = (EditText) findViewById(R.id.editUserUserApellido);
				String apellido = eIngresarApellido.getText().toString();

				EditText eIngresarMail = (EditText) findViewById(R.id.editUsuarioUserMail);
				String mail = eIngresarMail.getText().toString();

				EditText eIngresarContrasena = (EditText) findViewById(R.id.editUsuarioUserPass);
				String contrasenia = eIngresarContrasena.getText().toString();

				HttpClient client = new DefaultHttpClient();

				JSONObject obj = new JSONObject(getIntent().getStringExtra(
						"json"));
				
				String url = "http://"+ip+"/seekit/seekit/editarUsuario?idUsuario="
						+ obj.getString("idUsuario")
						+ "&nombre="
						+ nombre
						+ "&apellido="
						+ apellido
						+ "&mail="
						+ mail
						+ "&contrasenia=" + contrasenia;
Log.d("editar usuario",url);
				HttpGet httpGet = new HttpGet(url);
				
				try {

					HttpResponse response = client.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					statusCode = statusLine.getStatusCode();
					StringBuilder builder = new StringBuilder();
					// Ahora ya lo registre, pero quiero que luego del registro
					// vaya al mainativity. Lo ideal es pasarle al main ya el
					// json con el usuario, por lo que una ves registrarlo debo
					// de obtenerlo.
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
			Log.d("el status cde es",statusCode+"");
			if (statusCode == 200) {
				Intent itn = new Intent(EditarUsuario.this, MainActivity.class);

				itn.putExtra("PARENT_NAME", "EditarUsuario");
				itn.putExtra("json", result.toString());
				Log.d("asd",result.toString());
				startActivity(itn);
				finish();

			} else {
				if (statusCode == 0) {
					Toast.makeText(EditarUsuario.this,
							"Nuestros servidores estan caidos.",
							Toast.LENGTH_LONG).show();
				}
				Toast.makeText(EditarUsuario.this,
						"Por favor, seleccione otro mail.", Toast.LENGTH_LONG)
						.show();
			}

		}
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

	private boolean isEmailValid(String email) {
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
}
