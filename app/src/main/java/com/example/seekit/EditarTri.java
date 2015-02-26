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

public class EditarTri extends Activity {
    String identificador = null;
    String nombreTri = null;
    String img = null;
    int statusCode = -1;
    String idTri = null;
    String descripcion = null;
    String ip = null;

    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_tri);

        //IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip = bundle1.getString("ip");
        } catch (Exception e) {

            e.printStackTrace();
        }
        //fin IP

        identificador = getIntent().getStringExtra(
                "identificador");
        nombreTri = getIntent().getStringExtra(
                "nombreTri");
        img = getIntent().getStringExtra(
                "img");
        idTri = getIntent().getStringExtra(
                "idTri");
        descripcion = getIntent().getStringExtra(
                "descripcion");
        try {
            json = new JSONObject(getIntent().getStringExtra(
                    "json"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //cargemos los valores
        EditText eEditNombre = (EditText) findViewById(R.id.editarTrieditName);
        eEditNombre.setText(nombreTri);

        EditText eEditMAC = (EditText) findViewById(R.id.editTriEditMAC);
        eEditMAC.setText(identificador);

        EditText eEditDescripcion = (EditText) findViewById(R.id.editTriEditDescripcion);
        eEditDescripcion.setText(descripcion);

        //EditText eEditViewFotoAddTri = (EditText) findViewById(R.id.editViewFotoEditarTri);
        //eEditViewFotoAddTri.setText(img);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editar_tri, menu);
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

    public void editarTri(View view) {
        EditText eEditNombre = (EditText) findViewById(R.id.editarTrieditName);
        String nombre = eEditNombre.getText().toString();

        EditText eEditMAC = (EditText) findViewById(R.id.editTriEditMAC);
        String editMAC = eEditMAC.getText().toString();

        EditText eEditDescripcion = (EditText) findViewById(R.id.editTriEditDescripcion);
        String descripcion = eEditDescripcion.getText().toString();
        //EditText eEditViewFotoAddTri = (EditText) findViewById(R.id.editViewFotoEditarTri);
        //String foto = eEditViewFotoAddTri.getText().toString();

        if (!isInputTextValid(descripcion)) {
            eEditDescripcion.setError("Formato invalido");
            return;
        }

        if (TextUtils.isEmpty(nombre)) {
            eEditNombre.setError("Por favor, introduzca un nombre");
            return;
        } else {
            if (!isInputTextValid(nombre)) {
                eEditNombre.setError("Formato invalido");
                return;
            }
        }
        if (TextUtils.isEmpty(editMAC)) {
            eEditMAC.setError("!");
            return;
        }

		/*
         * if (TextUtils.isEmpty(descripcion)) {
		 * eEditDescripcionAddTri.setError("Indtroducir breve descripcion");
		 * return; } else { if (!isInputTextValid(nombre)) {
		 * eEditNombe.setError("Formato invalido"); return; } }
		 */

        if (isNetworkAvailable()) {

            GetEditarTriTask getEditarTriTask = new GetEditarTriTask();
            getEditarTriTask.execute();
        } else {
            Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private class GetEditarTriTask extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonResponse = null;
            try {
                EditText eEditNombre = (EditText) findViewById(R.id.editarTrieditName);
                String nombre = eEditNombre.getText().toString();

                EditText eEditMAC = (EditText) findViewById(R.id.editTriEditMAC);
                String identificador = eEditMAC.getText().toString();

                EditText eEditDescripcion = (EditText) findViewById(R.id.editTriEditDescripcion);
                String descripcion = eEditDescripcion.getText().toString();

                //EditText eEditViewFotoAddTri = (EditText) findViewById(R.id.editViewFotoEditarTri);
                //String foto = eEditViewFotoAddTri.getText().toString();

                HttpClient client = new DefaultHttpClient();


                img = "null";
                String url = "http://" + ip + "/seekit/seekit/editarTri?idTri="
                        + idTri
                        + "&nombre="
                        + nombre
                        + "&identificador="
                        + identificador
                        + "&foto="
                        + img + "&descripcion="
                        + descripcion;

                Log.d("editar usuario", url);
                HttpGet httpGet = new HttpGet(url);

                try {

                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    statusCode = statusLine.getStatusCode();
                    StringBuilder builder = new StringBuilder();

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

            handleResult();
        }

        private void handleResult() {
            if (statusCode == 200) {
                Intent intent = new Intent(EditarTri.this, MainActivity.class);
                intent.putExtra("PARENT_NAME", "EditarTri");
                intent.putExtra("json", getIntent().getStringExtra("json"));
                Log.d("EditarTri", getIntent().getStringExtra("json"));
                startActivity(intent);
                finish();

            } else {
                if (statusCode == 0) {
                    Toast.makeText(EditarTri.this,
                            "Nuestros servidores estan caidos.",
                            Toast.LENGTH_LONG).show();
                }

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

    private boolean isInputTextValid(String inputText) {

        String regExpn = "[a-zA-Z0-9 ]+$";

        CharSequence inputStr = inputText;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }
}
