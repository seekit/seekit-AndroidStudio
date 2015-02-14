package com.example.seekit;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
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


import com.example.backgroundTasks.MyTestReceiver;
import com.example.backgroundTasks.MyTestService;

import android.app.ActionBar;
import android.app.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
    TextView textViewRegistrarse;

    public final static String IDUSU = null;

    // empecemos con algo de guardar los datos
    SharedPreferences pref = null;
    Editor editor = null;
    // fin pruebas guardar datos

    // realizaremos las pruebas para obtener un servicio que corra en
    // background.
    public MyTestReceiver receiverForTest;
    // fin pruebas.

    int statusCode = -1;
    String ip = null;
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // pruebas de acuerdo a las preferencies

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        editor = pref.edit();
        String usuario = null;
        usuario = pref.getString("usuario", "null");
//IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip = bundle1.getString("ip");
        } catch (Exception e) {

            e.printStackTrace();
        }
        //fin IP

        if (!usuario.equals("null")) {

            Intent itn = new Intent(Login.this, MainActivity.class);
            itn.putExtra("json", usuario);
            itn.putExtra("PARENT_NAME", "Login");
            startActivity(itn);
            finish();
        } else {

            setContentView(R.layout.activity_login);

            // registrarse
            inicializadores();
        }

        // realizaremos las pruebas para obtener un servicio que corra en
        // background.

        // setupServiceReceiver();


    }


    // Starts the IntentService
    private void onStartService() {
        Intent i = new Intent(this, MyTestService.class);
        i.putExtra("foo", "bar");
        i.putExtra("receiver", receiverForTest);
        Log.d("Login", "onStartService");
        startService(i);
    }

    // Setup the callback for when data is received from the service
    private void setupServiceReceiver() {
        Log.d("Login", "SetupServiceReceiver1");
        receiverForTest = new MyTestReceiver(new Handler());
        Log.d("Login", "SetupServiceReceiver2");
        // This is where we specify what happens when data is received from the
        // service
        receiverForTest.setReceiver(new MyTestReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                Log.d("Login", "SetupServiceReceiver3");
                if (resultCode == RESULT_OK) {
                    String resultValue = resultData.getString("resultValue");
                    Log.d("Login", resultValue);
                    Toast.makeText(Login.this, resultValue, Toast.LENGTH_SHORT)
                            .show();
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

    //registro
    void inicializadores() {
        textViewRegistrarse = (TextView) findViewById(R.id.registrarse);
        textViewRegistrarse.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, Registro.class);
                startActivity(intent);

            }

        });

    }

    public void Ingresar(View view) {
        // chequeo q hayan puesto algo
        EditText mail = (EditText) findViewById(R.id.loginEmailField);
        String sMail = mail.getText().toString();
        EditText contrasena = (EditText) findViewById(R.id.loginPasswordField);
        String sContrasena = contrasena.getText().toString();

        if (sMail.equals("") || sContrasena.equals("")) {
            Toast.makeText(Login.this, "Ingrese algo por favor.",
                    Toast.LENGTH_SHORT).show();

        } else {
            if (!isEmailValid(sMail)) {
                mail.setError("Por favor, introduzca su mail");
                return;
            }

            // obtengo los datos del usuario
            // ObtenerJson Ojson = new ObtenerJson();
            // String[] login=Ojson.obtenerLogin(sMail, sContrasena);

            // PRUEBAS PA QUE QUEDE OK

            if (isNetworkAvailable()) {
                // onStartService();
                GetLoginTask getloginTask = new GetLoginTask();
                getloginTask.execute();
            } else {
                Toast.makeText(this, "Network is unavailable!",
                        Toast.LENGTH_LONG).show();
            }

            // fin pruebas

            // EMPIEZAAA
            /*
			 * final String[] arrayAux = new String[4]; final String mailAux =
			 * sMail; AsyncHttpClient client = new AsyncHttpClient(); String url
			 * = "http://192.168.56.1:8080/seekit/seekit/login" + "?mail=" +
			 * sMail + "&contrasenia=" + sContrasena; Log.d("asdasdasd", url);
			 * client.get(url, new JsonHttpResponseHandler() {
			 * 
			 * @Override public void onSuccess(int statusCode, Header[] headers,
			 * JSONObject response) { // If the response is JSONObject instead
			 * of expected // JSONArray try { if (statusCode == 200) {
			 * arrayAux[0] = response.getString("nombre"); arrayAux[1] =
			 * response.getString("apellido"); arrayAux[2] = mailAux;
			 * arrayAux[3] = response.getString("idUsuario");
			 * 
			 * Intent itn = new Intent(Login.this, MainActivity.class);
			 * itn.putExtra(IDUSU, arrayAux[3]);
			 * 
			 * startActivity(itn); } else { Toast.makeText( Login.this,
			 * "Acazooo, ese suario no existe o algo no anda bien, pero por ahora solo hize un mensaje solo"
			 * , Toast.LENGTH_SHORT).show(); } } catch (JSONException e) {
			 * 
			 * Log.e("MyActivity", "JSONException encontrada", e); } }
			 * 
			 * });
			 */
            // TERMINA

            // Intent itn = new Intent(Login.this, MainActivity.class);
            // itn.putExtra(IDUSU, arrayAux[3]);

            // startActivity(itn);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    // Metodos fuleros para el trabajo asyncrono
    private class GetLoginTask extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Object... arg0) {

            JSONObject jsonResponse = null;

            try {

                // ///////////7
                EditText editMail = (EditText) findViewById(R.id.loginEmailField);
                String mail = editMail.getText().toString();

                EditText editPass = (EditText) findViewById(R.id.loginPasswordField);
                String pass = editPass.getText().toString();
                HttpClient client = new DefaultHttpClient();
                String url = "http://" + ip + "/seekit/seekit/login?mail="
                        + mail + "&pass=" + pass;
                Log.d("url", url);
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
                            Log.d("Login", line);
                            jsonResponse = new JSONObject(line);
                            jObj=jsonResponse;
                        }
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

                // ////////////

				/*
				 * URL urlWS = new URL(
				 * "http://blog.teamtreehouse.com/api/get_recent_summary/?count=2"
				 * ); Log.i("viene bien2", "Cree la url"); HttpURLConnection
				 * connection = (HttpURLConnection) urlWS .openConnection();
				 * connection.connect(); responseCode =
				 * connection.getResponseCode(); if (responseCode ==
				 * HttpURLConnection.HTTP_OK) { InputStream inputStream =
				 * connection.getInputStream(); Reader reader = new
				 * InputStreamReader(inputStream); int contentLength =
				 * connection.getContentLength();
				 * char[] charArray = new char[contentLength];
				 * reader.read(charArray); String responseData = new
				 * String(charArray); Log.i("viene bien3", "Es OK el http");
				 * jsonResponse = new JSONObject(responseData); } else {
				 * Log.i("PORQUE NO ANDA1", "Unsuccessful HTTP Response Code: "
				 * + responseCode); } } catch (MalformedURLException e) {
				 * Log.i("PORQUE NO ANDA2", "Unsuccessful HTTP Response Code: "
				 * + responseCode); } catch (IOException e) {
				 * Log.i("PORQUE NO ANDA3", "Unsuccessful HTTP Response Code: "
				 * + responseCode);
				 */

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

        private void handleResult(JSONObject jsonObj) {
            // si anda bien, voy a pasar el objeto a la otra intent
            if (statusCode == 200) {
                String idusu = null;
                String mitoken = null;
                try {
                    idusu = jsonObj.getString("idusuario");
                    mitoken = jsonObj.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                chequearToken(idusu, mitoken);
 /*
                // para el guardado de datos del usuario.
                editor.putString("usuario", jsonObj.toString());
                editor.commit();
                // fin datos
                Intent itn = new Intent(Login.this, MainActivity.class);
                itn.putExtra("json", jsonObj.toString());
                itn.putExtra("PARENT_NAME", "Login");
                startActivity(itn);
                finish();
                */
            } else if (statusCode == 0) {
                Toast.makeText(Login.this, "El servidor no ha respondido",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Login.this, "No existe dicho mail/pass",
                        Toast.LENGTH_SHORT).show();
            }


        }
    }

    //aqui hay que chequear el token
    private void chequearToken(String idusu, String mitoken) {
        if (isNetworkAvailable()) {
            // onStartService();
            GetTokenTask getTokenTask = new GetTokenTask();
            getTokenTask.setIdusuToken(idusu);
            getTokenTask.setMitoken(mitoken);
            getTokenTask.execute();
        } else {
            Toast.makeText(this, "Network is unavailable!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private class GetTokenTask extends AsyncTask<Object, Void, JSONObject> {
        private String idusuToken=null;
        private String mitoken=null;

        public String getIdusuToken() {
            return idusuToken;
        }

        public void setIdusuToken(String idusuToken) {
            this.idusuToken = idusuToken;
        }

        public String getMitoken() {
            return mitoken;
        }

        public void setMitoken(String mitoken) {
            this.mitoken = mitoken;
        }

        @Override
        protected JSONObject doInBackground(Object... arg0) {

            JSONObject jsonResponse = null;

            try {

                HttpClient client = new DefaultHttpClient();
                String url = "http://" + ip + "/seekit/seekit/getToken?idUsuario="+getIdusuToken()+"&token="+getMitoken();
                Log.d("url", url);
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
                            Log.d("Login", line);
                            jsonResponse = new JSONObject(line);

                        }
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

                // ////////////

				/*
				 * URL urlWS = new URL(
				 * "http://blog.teamtreehouse.com/api/get_recent_summary/?count=2"
				 * ); Log.i("viene bien2", "Cree la url"); HttpURLConnection
				 * connection = (HttpURLConnection) urlWS .openConnection();
				 * connection.connect(); responseCode =
				 * connection.getResponseCode(); if (responseCode ==
				 * HttpURLConnection.HTTP_OK) { InputStream inputStream =
				 * connection.getInputStream(); Reader reader = new
				 * InputStreamReader(inputStream); int contentLength =
				 * connection.getContentLength();
				 * char[] charArray = new char[contentLength];
				 * reader.read(charArray); String responseData = new
				 * String(charArray); Log.i("viene bien3", "Es OK el http");
				 * jsonResponse = new JSONObject(responseData); } else {
				 * Log.i("PORQUE NO ANDA1", "Unsuccessful HTTP Response Code: "
				 * + responseCode); } } catch (MalformedURLException e) {
				 * Log.i("PORQUE NO ANDA2", "Unsuccessful HTTP Response Code: "
				 * + responseCode); } catch (IOException e) {
				 * Log.i("PORQUE NO ANDA3", "Unsuccessful HTTP Response Code: "
				 * + responseCode);
				 */

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
                // para el guardado de datos del usuario.
                editor.putString("usuario", jObj.toString());
                editor.commit();
                // fin datos
                Intent itn = new Intent(Login.this, MainActivity.class);
                itn.putExtra("json", jObj.toString());
                itn.putExtra("PARENT_NAME", "Login");
                startActivity(itn);
                finish();
            } else if (statusCode == 0) {
                Toast.makeText(Login.this, "El servidor no ha respondido",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Login.this, "No existe dicho mail/pass",
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

}
