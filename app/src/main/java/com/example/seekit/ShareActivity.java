package com.example.seekit;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seekit.adapters.SectionsPagerAdapter;

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
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import contenedor.Usuario;

public class ShareActivity extends FragmentActivity implements
		ActionBar.TabListener {
    String identificador = null;
    String nombreTri = null;
    String img = null;
    int statusCode = -1;
    String idTri = null;
    JSONObject json = null;
    String idUsuarioLogueado = null;
String ip=null;


	public static final String TAG = MainActivity.class.getSimpleName();


	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link android.support.v4.view.ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_share);

//IP
        try {
            ResourceBundle bundle1 = ResourceBundle.getBundle("assets/configuration");
            ip=bundle1.getString("ip");
        }catch(Exception e){

            e.printStackTrace();
        }
        //fin IP

        identificador = getIntent().getStringExtra("identificador");
        nombreTri = getIntent().getStringExtra("nombreTri");
        img = getIntent().getStringExtra("img");
        idTri = getIntent().getStringExtra("idTri");

        try {
            json = new JSONObject(getIntent().getStringExtra("json"));
            idUsuarioLogueado = json.getString("idUsuario").toString();
        } catch (JSONException e) {

            e.printStackTrace();
        }

        // Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this, 
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setIcon(mSectionsPagerAdapter.getIcon(i))
					.setTabListener(this));
		}
	}
	


	private void navigateToLogin() {
		Intent intent = new Intent(this, Login.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	


	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}


    public void compartir(View view) {
        EditText eEditMail = (EditText) findViewById(R.id.shareEmailField);
        String mail = eEditMail.getText().toString();

        EditText eEditDescripcion = (EditText) findViewById(R.id.whyField);
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
            Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG)
                    .show();
        }

    }
    private class GetCompartirTriTask extends
            AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            EditText eEditMail = (EditText) findViewById(R.id.shareEmailField);
            String mail = eEditMail.getText().toString();

            EditText eEditDescripcion = (EditText) findViewById(R.id.whyField);
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
                recreate();
            } else if (statusCode == 0) {
                Toast.makeText(ShareActivity.this,
                        "El servidor no ha respondido", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(ShareActivity.this,
                        "No se ha podido compartir este dispositivo",
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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }
}
