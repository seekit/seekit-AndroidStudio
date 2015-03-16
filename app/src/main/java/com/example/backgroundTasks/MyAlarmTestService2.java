package com.example.backgroundTasks;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.seekit.MainActivity;
import com.example.seekit.R;

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
import java.util.ArrayList;
import java.util.ResourceBundle;

import contenedor.Tri;
import contenedor.TriCompartido;

public class MyAlarmTestService2 extends IntentService {
    public MyAlarmTestService2() {
       super("MyTestService");
    }
    JSONObject json;
    JSONArray respuestaServer;
    String ip = null;
    int statusCode = -1;

    @Override
    protected void onHandleIntent(Intent intent) {
       // Do the task here
       Log.i("MyTestService2", "Service running");

        //IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip=bundle1.getString("ip");
        }catch(Exception e){

            e.printStackTrace();
        }
        //fin IP

        try {
            json = new JSONObject(intent.getStringExtra(
                    "json"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (isNetworkAvailable()) {

            SeEncontroTriPerdido getMainActivityTask = new SeEncontroTriPerdido();
            getMainActivityTask.execute();
        } else {
            Toast.makeText(this, "Network is unavailable!",
                    Toast.LENGTH_LONG).show();
        }



    
    }

    private void notificacion(String codigoTri, int id){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("json", json.toString());
        notificationIntent.putExtra("PARENT_NAME", "tester");
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = this.getResources();
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_action_new)
                .setTicker(res.getString(R.string.accept))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Se econtro.")
                .setContentText(codigoTri);
        Notification n = builder.build();

        nm.notify(id+20, n);
    }



    private class SeEncontroTriPerdido extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Object... arg0) {

            JSONObject jsonResponse = null;

            try {

                HttpClient client = new DefaultHttpClient();
                String url = "http://" + ip + "/seekit/seekit/seEncontroTriPerdido?idusuario="+json.getString("idUsuario");

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
                            Log.d("Segundo servicio", line);
                            respuestaServer = new JSONArray(line);

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
                   if(!respuestaServer.equals(null)){
                      Log.d("El reslutado del swegundo shecedule",respuestaServer.toString());

                       final ArrayList<Tri> arrayListaTris = new ArrayList<Tri>();

                       for(int i=0;i<respuestaServer.length();i++){
                           Tri triAux=new Tri();
                           try {
                               triAux.setIdentificador(respuestaServer.getJSONObject(i).getString("identificador"));
                               triAux.setIdTri(respuestaServer.getJSONObject(i).getString("idTri"));
                               triAux.setNombre(respuestaServer.getJSONObject(i).getString("nombre"));
                               triAux.setFoto(respuestaServer.getJSONObject(i).getString("foto"));
                               triAux.setActivo(respuestaServer.getJSONObject(i).getString("activo"));
                               triAux.setLatitud(respuestaServer.getJSONObject(i).getString("latitud"));
                               triAux.setLongitud(respuestaServer.getJSONObject(i).getString("longitud"));
                               triAux.setCompartido(respuestaServer.getJSONObject(i).getString("compartido"));
                               triAux.setDescripcion(respuestaServer.getJSONObject(i).getString("descripcion"));

                             arrayListaTris.add(triAux);
                               notificacion(triAux.getIdentificador(),i);


                           } catch (JSONException e) {
                               e.printStackTrace();
                           }

                       }


                   }
            } else if (statusCode == 0) {

            } else {

            }


        }
    }
    boolean isNetworkAvailable(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;

    }
}

/*
 *        //lo que vamos a querer es que en cierto momento salte el mensaje de que se perdio el cel.       
       NotificationCompat.Builder mBuilder =
    		    new NotificationCompat.Builder(this)
    		    .setSmallIcon(R.drawable.ic_action_new)
    		    .setContentTitle("My notification")
    		    .setContentText("Hello World!");
      
       
       Intent resultIntent = new Intent(this, MainActivity.class);
    // Because clicking the notification opens a new ("special") activity, there's
    // no need to create an artificial back stack.
    PendingIntent resultPendingIntent =
        PendingIntent.getActivity(
        this,
        0,
        resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    );
    
    mBuilder.setContentIntent(resultPendingIntent);
   	
 // Sets an ID for the notification
    int mNotificationId = 001;
    // Gets an instance of the NotificationManager service
    NotificationManager mNotifyMgr = 
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    // Builds the notification and issues it.
    mNotifyMgr.notify(mNotificationId, mBuilder.build());
    
*/
