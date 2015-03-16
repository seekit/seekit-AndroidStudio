package com.example.seekit;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

    private String imagenTri = "null";


    // Reference to our image view we will use
    private ImageView triImage;

    // Code for our image picker select action.
    private static final int IMAGE_PICKER_SELECT = 999;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

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


        triImage = (ImageView) findViewById(R.id.photoEditTri);
        triImage.setImageBitmap(convertToImage(img.replaceAll("\\s+","")));

        triImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_PICKER_SELECT);*/

                selectImage();
            }
        });

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditarTri.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.png");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
        if (id == R.id.action_eliminar_tri) {
            Log.d("Editar Tri", "Eliminemos el Tri");

            if (isNetworkAvailable()) {

                GetEliminarTriTask getEliminarTriTask = new GetEliminarTriTask();
                getEliminarTriTask.execute();
            } else {
                Toast.makeText(this, "Network is unavailable!",
                        Toast.LENGTH_LONG).show();
            }
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



                HttpClient client = new DefaultHttpClient();
                String nombreUTF8 =nombre;
                String descripcionUTF8=descripcion;
                String identificadorUTF8=identificador;

                try {
                    nombreUTF8 = URLEncoder.encode(nombre, "utf-8");
                    descripcionUTF8 = URLEncoder.encode(descripcion, "utf-8");
                    identificadorUTF8 =  URLEncoder.encode(identificador, "utf-8");
                } catch (UnsupportedEncodingException e1) {

                    e1.printStackTrace();
                }


                String url = "http://" + ip + "/seekit/seekit/editarTri?idTri="
                        + idTri
                        + "&nombre="
                        + nombreUTF8
                        + "&identificador="
                        + identificadorUTF8
                        + "&foto="
                        + img + "&descripcion="
                        + descripcionUTF8;

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
                intent.putExtra("json", json.toString());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.png")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);

                    // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);



                    //pasar imagen a String

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap photo = bm;
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    imagenTri = encodedImage.replaceAll("\\s+","");


                    triImage.setImageBitmap(convertToImage(imagenTri));
                    Log.d("imaegen", imagenTri);
                    //fin decodificacion


                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".png");
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();


                try {
                    triImage.setImageBitmap(getRoundedCornerBitmap(getBitmapFromUri(selectedImageUri)));
                    //pasar imagen a String

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap photo = getRoundedCornerBitmap(getBitmapFromUri(selectedImageUri));
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    imagenTri = encodedImage.replaceAll("\\s+","");


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public Bitmap convertToImage(String u){

        try{
            InputStream stream = new ByteArrayInputStream(Base64.decode(u.getBytes(),Base64.DEFAULT));
            Bitmap b= BitmapFactory.decodeStream(stream);
            return b;
        }catch ( Exception e){

            return null;
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

    private class GetEliminarTriTask extends
            AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {

            JSONObject jsonResponse = null;

            try {
                HttpClient client = new DefaultHttpClient();
                String url="http://"+ip+"/seekit/seekit/getEliminarTri?idTri="
                        + idTri;
                Log.d("url",
                        url);

                HttpGet httpGet = new HttpGet(url);

                try {

                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    statusCode = statusLine.getStatusCode();

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



            if (statusCode == 200) {
                Intent intent = new Intent(EditarTri.this, MainActivity.class);
                try {

                    intent.putExtra("json", json.toString());
                    intent.putExtra("PARENT_NAME", "EditarTri");
                    startActivity(intent);
                    EditarTri.this.finish();
                } catch (Exception e) {
                    Log.d("ACA NUNCA DEBO ESTAR", "ACA NUNCA DEBO ESTAR");
                }
            } else if (statusCode == 0) {

            } else {

            }

        }

    }
}
