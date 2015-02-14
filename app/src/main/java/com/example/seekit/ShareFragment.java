package com.example.seekit;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class ShareFragment extends Fragment {
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
String ip=null;

	protected SwipeRefreshLayout mSwipeRefreshLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.share_view,
				container, false);
		
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
//IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip=bundle1.getString("ip");
        }catch(Exception e){

            e.printStackTrace();
        }
        //fin IP
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		getActivity().setProgressBarIndeterminateVisibility(false);
		

	}


    /*
    public void compartirTri(View view) {
        EditText eEditMail = (EditText) getView().findViewById(R.id.shareEmailField);
        String mail = eEditMail.getText().toString();
        Log.d("mail",mail);
        EditText eEditDescripcion = (EditText) getView().findViewById(R.id.whyField);
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
            Toast.makeText(getActivity(), "Network is unavailable!", Toast.LENGTH_LONG)
                    .show();
        }

    }
*/
    /*
    private class GetCompartirTriTask extends
            AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            EditText eEditMail = (EditText) getView().findViewById(R.id.shareEmailField);
            String mail = eEditMail.getText().toString();

            EditText eEditDescripcion = (EditText) getView().findViewById(R.id.whyField);
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

            String url = "http://"+ip+"/seekit/seekit/compartirTri?mailUsuACompartir="
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
                Toast.makeText(getActivity(),
                        "Se ha compartido con exito", Toast.LENGTH_SHORT)
                        .show();
            } else if (statusCode == 0) {
                Toast.makeText(getActivity(),
                        "El servidor no ha respondido", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getActivity(),
                        "No se ha podido compartir este dispositivo",
                        Toast.LENGTH_SHORT).show();
            }
            // String aux = aux =
            // result.getJSONObject("usuario").getString("nombre");

        }
    }
*/
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
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








