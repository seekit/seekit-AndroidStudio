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
    String ip=null;
    // fin pruebas guardar datos
    int statusCode = -1;
    static String json;

    @Override
    protected void onHandleIntent(Intent intent) {
        json = intent.getStringExtra("json");
        identificadores = intent.getStringExtra("identificadores");

        List<String> items = Arrays.asList(identificadores.split("\\s*,\\s*"));
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip = bundle1.getString("ip");
        } catch (Exception e) {

            e.printStackTrace();
        }

        ArrayList<String> listaBleRastreo= rastreoBle();
        ArrayList<String> listaBleFaltante = seMePerdioBle(listaBleRastreo, items);
        if(listaBleFaltante.size()==0){
            /* todo bajo control, no perdi un solo BLE */
        }else{
            deboNotificarBleNoEncontrado(listaBleFaltante);

        }

       // Do the task here
       Log.i("MyTestService", "Service running");




       



    
    }

    private ArrayList<String> deboNotificarBleNoEncontrado(ArrayList<String> listaBleFaltante) {
        ArrayList<String> res=new ArrayList<String>();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        for(int i=0; i<listaBleFaltante.size();i++){
            String tri=pref.getString(listaBleFaltante.get(i), "null");
            if(!tri.equals("null")){
                if(tri.equals("1")){
                    res.add(listaBleFaltante.get(i));

                }
            }

        }

        return res;




    }

    private ArrayList<String> seMePerdioBle(ArrayList<String> listaBleRastreo, List<String> items) {
        ArrayList<String> res= new ArrayList<String>();
        for(int i=0; i < items.size() ;i++){
            for(int j=0;j<listaBleRastreo.size();j++){

                if(listaBleRastreo.get(j).equals(items.get(i))){
                    //Lo tenfo, entonces no hao nada. Todo Ok

                }else{
                    //donde eesta mi ble en items de la pos j?? lo he perdido.
                    res.add(items.get(i));

                }

            }

        }


        return res;


    }

    private ArrayList<String> rastreoBle() {
        return null;
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


    private void notificacion(String idTri){
        Intent notificationIntent = new Intent(this, MainActivity.class);
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
                .setContentTitle("sadasd")
                .setContentText("asdasdasdasdasd");
        Notification n = builder.build();

        nm.notify(3, n);
    }
}

