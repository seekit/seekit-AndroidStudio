package com.example.backgroundTasks;


import com.example.seekit.MainActivity;
import com.example.seekit.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

//Este alarm sera el que busca los Tris y los reporta al servidor!!!
public class MyAlarmTestService extends IntentService {
    public MyAlarmTestService() {
        super("MyTestService");
    }

    String identificadores;
    // empecemos con algo de guardar los datos
    SharedPreferences pref = null;
    SharedPreferences.Editor editor = null;
    String ip = null;
    // fin pruebas guardar datos
    int statusCode = -1;
    static String json;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("MyTestService", "Service running");
        json = intent.getStringExtra("json");
        identificadores = intent.getStringExtra("identificadores");
        Log.d("identificadores",identificadores);
        List<String> items = Arrays.asList(identificadores.split("\\s*,\\s*"));
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip = bundle1.getString("ip");
        } catch (Exception e) {

            e.printStackTrace();
        }

        //se guarda en listaBleRastreo todos los Tris rastreados en una pasada de BLE
        ArrayList<String> listaBleRastreo = rastreoBle();

        //De mis BLEs y de los que me compartireron, cuales no fueron encontrados en el rastreo? se guarda en listaBleFaltante
        if(listaBleRastreo.isEmpty()){
             Log.d("La lista de rastreo","esta vacia");
        }else{


            ArrayList<String> listaBleFaltante = seMePerdioBle(listaBleRastreo, items);
            if (listaBleFaltante.size() == 0) {
                /* todo bajo control, no perdi un solo BLE */
            } else {
                //en caso de que alguno de esos tris faltantes este habilitado, debo notificarlo al usuario
                ArrayList<String> listaBleANotificar = deboNotificarBleNoEncontrado(listaBleFaltante);
                if (listaBleANotificar.size() == 0) {
                    /* todo bajo control, no perdi un solo BLE habilitado*/
                } else {
                    for (int i = 0; i < listaBleANotificar.size(); i++) {
                        notificacion(listaBleANotificar.get(i),i);
                    }
                }

            }
        }




    }

    private ArrayList<String> deboNotificarBleNoEncontrado(ArrayList<String> listaBleFaltante) {
        ArrayList<String> res = new ArrayList<String>();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        for (int i = 0; i < listaBleFaltante.size(); i++) {
            String tri = pref.getString(listaBleFaltante.get(i), "null");
            Log.d("el valor es: "+tri,"del tri"+listaBleFaltante.get(i));
            if (!tri.equals("null")) {
                if (tri.equals("1")) {
                    res.add(listaBleFaltante.get(i));


                }
            }

        }

        return res;


    }

    private ArrayList<String> seMePerdioBle(ArrayList<String> listaBleRastreo, List<String> items) {
        ArrayList<String> res = new ArrayList<String>();
        boolean loTengo;
        for (int i = 0; i < items.size(); i++) {
            Log.d("mis tris y los que me compartieron",items.get(i));
            for (int j = 0; j < listaBleRastreo.size(); j++) {

                loTengo=false;
                Log.d("Los tris que rastree",listaBleRastreo.get(j));
                if (listaBleRastreo.get(j).equals(items.get(i))) {
                    //Lo tenfo, entonces no hao nada. Todo Ok
                    Log.d("el tri",items.get(i)+" esta OK");
                    j=listaBleRastreo.size();
                    loTengo=true;

                }
                if((!loTengo) && (j == (listaBleRastreo.size() - 1))) {
                    //donde eesta mi ble en items de la pos j?? lo he perdido.
                    Log.d("el tri",items.get(i)+" esta PERDIDO");
                    res.add(items.get(i));

                }

            }

        }


        return res;


    }

    private ArrayList<String> rastreoBle() {
        ArrayList<String> res=new ArrayList<String>();
        String aux="hnnhhh";
        res.add(aux);
        aux="aaaaa";
        res.add(aux);
        aux="bbbbb";
        res.add(aux);
        return res;
    }

    private class GetTrisEncontradosTask extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Object... arg0) {

            JSONObject jsonResponse = null;

            try {

                HttpClient client = new DefaultHttpClient();
                String url = "http://" + ip + "/seekit/seekit/actualizargps?idusuarioorigen=&latitud=&longitud=&idtri1=&idtri2=&idtri3=&idtri4=&idtri5=&idtri6=&idtri7=&idtri8=&idtri9=&idtri10=";

                Log.d("url", url);
                HttpGet httpGet = new HttpGet(url);

                try {

                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) {
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

        private void handleResult(JSONObject jsonObj) {
            // si anda bien, voy a pasar el objeto a la otra intent
            if (statusCode == 200) {

            } else if (statusCode == 0) {

            } else {

            }


        }
    }


    private void notificacion(String codigoTri,int id) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("json",json);
        notificationIntent.putExtra("PARENT_NAME","tester");
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
                .setContentTitle(codigoTri)
                .setContentText(codigoTri);
        Notification n = builder.build();

        nm.notify(id, n);
    }
}

