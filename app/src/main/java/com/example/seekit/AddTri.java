package com.example.seekit;

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

import com.example.seekit.R;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddTri extends Activity {

    // Code for our image picker select action.
    private static final int IMAGE_PICKER_SELECT = 999;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    // Reference to our image view we will use
    private ImageView triImage;

    int statusCode = -1;
    private String ip=null;
    private String imagenTri = "null";
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tri);
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip = bundle1.getString("ip");
        } catch (Exception e) {

            e.printStackTrace();
        }
        try {
            json = new JSONObject(getIntent().getStringExtra(
                    "json"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        triImage = (ImageView) findViewById(R.id.photoNewTri);

        triImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_PICKER_SELECT);*/

                selectImage();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_tri, menu);
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


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddTri.this);
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
                    triImage.setImageBitmap(bm);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap photo = bm;
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    imagenTri = encodedImage.replaceAll("\\s+", "");


                    //triImage.setImageBitmap(convertToImage(imagenTri));
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

                    imagenTri = encodedImage.replaceAll("\\s+", "");


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

    public void addTri(View view) {

        EditText eEditNombe = (EditText) findViewById(R.id.nombreNuevoTri);
        String nombre = eEditNombe.getText().toString();

        EditText eEditMAC = (EditText) findViewById(R.id.triCode);
        String editMAC = eEditMAC.getText().toString();

        EditText eEditDescripcion = (EditText) findViewById(R.id.descNuevoTri);
        String editDescripcion = eEditDescripcion.getText().toString();


        // EditText eEditDescripcionAddTri = (EditText)
        // findViewById(R.id.editDescripcionAddTri);
        // String descripcion = eEditDescripcionAddTri.getText().toString();

        //EditText eEditViewFotoAddTri = (EditText) findViewById(R.id.editViewFotoAddTri);
        //String foto = eEditViewFotoAddTri.getText().toString();

        if (TextUtils.isEmpty(nombre)) {
            eEditNombe.setError("Por favor, introduzca un nombre");
            return;
        } else {
            if (!isInputTextValid(nombre)) {
                eEditNombe.setError("Formato invalido");
                return;
            }
        }
        if (TextUtils.isEmpty(editMAC)) {
            eEditMAC.setError("!");
            return;
        }

        if (!isInputTextValid(editDescripcion)) {
            eEditDescripcion.setError("Formato invalido");
            return;
        }

        if (!isInputTextValid2(editMAC)) {
            eEditMAC.setError("Formato invalido");
            return;
        }


        if (isNetworkAvailable()) {

            GetAddTriTask getAddTriTask = new GetAddTriTask();
            getAddTriTask.execute();
        } else {
            Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG)
                    .show();
        }


    }


    private class GetAddTriTask extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {

            try {
//
//                JSONObject obj = new JSONObject(getIntent().getStringExtra(
//                        "json"));
//                Log.d("addTri", obj.toString());


                EditText eEditNombe = (EditText) findViewById(R.id.nombreNuevoTri);
                String nombre = eEditNombe.getText().toString();

                EditText eEditMAC1 = (EditText) findViewById(R.id.triCode);
                String identificador = eEditMAC1.getText().toString();

                EditText eEditDescripcion = (EditText) findViewById(R.id.descNuevoTri);
                String descripcion = eEditDescripcion.getText().toString();
                HttpClient client = new DefaultHttpClient();

                String nombreUTF8 = null;
                String descripcionUTF8 = null;
                String identificadorUTF8=identificador;
                try {
                    nombreUTF8 = URLEncoder.encode(nombre, "utf-8");
                    descripcionUTF8 = URLEncoder.encode(descripcion, "utf-8");
                    identificadorUTF8=URLEncoder.encode(identificador, "utf-8");

                } catch (UnsupportedEncodingException e1) {

                    e1.printStackTrace();
                }
                String aux="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";
                try {

                    aux=URLEncoder.encode(aux, "utf-8");

                } catch (UnsupportedEncodingException e1) {

                    e1.printStackTrace();
                }
                String id = json.getString("idUsuario");
                String url = "http://" + ip + "/seekit/seekit/addTri?idUsuario=" + id

                        + "&identificador="
                        + identificadorUTF8 + "&nombre=" + nombreUTF8
                        + "&foto=" + aux + "&descripcion=" + descripcionUTF8;
                //imagenTri

                Log.d("URL", url);

                HttpPost httpGet = new HttpPost(url);

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
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            handleResult();
        }

        private void handleResult() {
            if (statusCode == 200) {
                Intent intent = new Intent(AddTri.this, MainActivity.class);
                intent.putExtra("PARENT_NAME", "addTri");
                intent.putExtra("json", json.toString());
                startActivity(intent);
                finish();

            } else {
                if (statusCode == 0) {
                    Toast.makeText(AddTri.this,
                            "Nuestros servidores estan caidos.",
                            Toast.LENGTH_LONG).show();

                } else {

                    if(statusCode==406){
                        Toast.makeText(AddTri.this,
                                "Este identificador ya existe",
                                Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(AddTri.this,
                                "Houston, we have a problem",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }

        }

    }


    public boolean isInputTextValid(String inputText) {
        String regExpn = "[a-zA-Z ]+$";

        CharSequence inputStr = inputText;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }
    public boolean isInputTextValid2(String inputText) {
        String regExpn = "[a-zA-Z]+$";

        CharSequence inputStr = inputText;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
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


    public Bitmap convertToImage(String u) {

        try {
            InputStream stream = new ByteArrayInputStream(Base64.decode(u.getBytes(), Base64.DEFAULT));
            Bitmap b = BitmapFactory.decodeStream(stream);
            return b;
        } catch (Exception e) {

            return null;
        }

    }
}