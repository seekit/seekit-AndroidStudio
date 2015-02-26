package com.example.seekit.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.example.seekit.FriendElement;
import com.example.seekit.R;
import com.example.seekit.ShareActivity;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.List;
import java.util.ResourceBundle;

import contenedor.Usuario;

/**
 * Created by nicoB on 12/15/14.
 */
public class FriendListAdapter extends ArrayAdapter<Usuario> {
    protected Context mContext;
    protected List<Usuario> entradas;
    int statusCode = -1;
    String ip = null;
    String idTri = null;
    String identificador=null;
    String nombreTri=null;
    String img=null;
    String json = null;

    public FriendListAdapter(Context context, List<Usuario> entradas, String idTri, String ip, String identificador, String nombreTri, String img, String json) {
        super(context, R.layout.friend_element_list, entradas);

        this.mContext = context;
        this.entradas = entradas;
        this.idTri=idTri;
        this.ip=ip;
        this.identificador=identificador;
        this.nombreTri=nombreTri;
        this.img=img;
        this.json=json;



    }

    @Override
    public int getCount() {
        return entradas.size();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_element_list, parent, false);

            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.friendName_list);
            holder.emailLabel = (TextView) convertView.findViewById(R.id.friendEmail_list);
            holder.imgDescompartir = (Button) convertView.findViewById(R.id.botondescompatirtri);
            //holder.switchShare = (Switch) convertView.findViewById(R.id.switchShare);

            convertView.setTag(holder);

        } else{
          	holder = (ViewHolder)convertView.getTag();

        }

        Usuario friend = entradas.get(position);
        String friendName = friend.getNombre();
        String friendMail = friend.getMail();
        Boolean isShare = false;

        holder.nameLabel.setText(friendName);
        holder.emailLabel.setText(friendMail);
        holder.imgDescompartir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    GetDescompartirTriTask getDescompartirTriTask = new GetDescompartirTriTask();
                    getDescompartirTriTask.setIdUsuario((entradas.get(position)).getIdUsuario());

                    getDescompartirTriTask.execute();

                } else {

                }
            }

        });
        //holder.switchShare.setChecked(isShare);


        return convertView;
    }

    private boolean isNetworkAvailable() {
        return true;
    }


    private static class ViewHolder {
        TextView nameLabel;
        TextView emailLabel;
        Switch switchShare;
        Button imgDescompartir;
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
            String url = "http://"+ip+"/seekit/seekit/descompartirTri?idTri="+idTri+"&idUsuario="+idUsuario;
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
                Intent intent = new Intent(mContext,
                        ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);



                try {

                    intent.putExtra("json", json);

                } catch (Exception e) {
                    Log.d("Pantalla Rastreo", "ACA NUNCA DEBO ESTAR");
                }
                intent.putExtra("identificador",identificador);
                intent.putExtra("nombreTri",nombreTri);
                intent.putExtra("img", img);
                intent.putExtra("idTri", idTri);

                mContext.startActivity(intent);




            }
        }

    }


}


