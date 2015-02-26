package com.example.seekit;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seekit.adapters.FriendListAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import contenedor.Usuario;

public class FriendsFragment extends ListFragment {
    String identificador = null;
    String nombreTri = null;
    String img = null;
    int statusCode = -1;
    String idTri = null;
    JSONArray jsonArray = null;
    String parentActivity = null;
    private ListView lista;
    String json = null;
    String idUsuarioLogueado = null;
    String ip=null;



    ArrayList<Usuario> listFriends= new ArrayList<Usuario>();


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.friends_list,
				container, false);

        View header = inflater.inflate(R.layout.friend_header_list, null);
//IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip=bundle1.getString("ip");
        }catch(Exception e){

            e.printStackTrace();
        }
        //fin IP




        if (isNetworkAvailable()) {

            GetCompartidosTriTask getPendientesCompartirTriTask = new GetCompartidosTriTask();
            getPendientesCompartirTriTask.execute();
        } else {
            Toast.makeText(getActivity(), "Network is unavailable!", Toast.LENGTH_LONG)
                    .show();
        }




        return rootView;
	}

    private List<Usuario> createFriendList() {
        Usuario friend= new Usuario();

        listFriends.add(friend);
        return listFriends;
    }

    @Override
	public void onResume() {
		super.onResume();

		getActivity().setProgressBarIndeterminateVisibility(false);
		



    }


    private class GetCompartidosTriTask extends
            AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {

            JSONObject jsonResponse = null;
            HttpClient client = new DefaultHttpClient();
            String url = "http://"+ip+"/seekit/seekit/triCompartido?idTri="
                    + idTri;
            Log.d("pantalla Compartir Friend fragment", url);
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

                            //reloadList(arrayListaUsuario);
                            listFriends=arrayListaUsuario;

                        }
                        llamadaAlAdaptador();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (statusCode == 0) {
                Toast.makeText(getActivity(),
                        "El servidor no ha respondido", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getActivity(),
                        "Si este mensaje aparece, entra en panico",
                        Toast.LENGTH_SHORT).show();
            }
            // String aux = aux =
            // result.getJSONObject("usuario").getString("nombre");

        }

    }

    private void llamadaAlAdaptador() {
        Log.d("lista del orto",listFriends.size()+"");
        FriendListAdapter adapter= new FriendListAdapter(getActivity().getApplicationContext(),listFriends,idTri, ip, identificador,nombreTri,img,json);
        setListAdapter(adapter);
    }
/*
    private void reloadList(ArrayList<Usuario> arrayListaUsuario) {
        Log.d("asdasd","sadasd");
        lista = (ListView) getView().findViewById(R.id.list);
        lista.setAdapter(new Lista_adaptador(getActivity(),
                R.layout.friend_element_list, arrayListaUsuario) {
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

    public String getIdTri() {
        return idTri;
    }

    public void setIdTri(String idTri) {
        this.idTri = idTri;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNombreTri() {
        return nombreTri;
    }

    public void setNombreTri(String nombreTri) {
        this.nombreTri = nombreTri;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIdUsuarioLogueado() {
        return idUsuarioLogueado;
    }

    public void setIdUsuarioLogueado(String idUsuarioLogueado) {
        this.idUsuarioLogueado = idUsuarioLogueado;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
