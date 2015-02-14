package com.example.seekit;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.MapActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import contenedor.Tri;

import android.util.Log;

public class ObtenerJson {

	void obtenerJson(String url) {

		AsyncHttpClient client = new AsyncHttpClient();
		/*
		 * client.get(url, new RequestParams("id", "15"), new
		 * AsyncHttpResponseHandler() {
		 * 
		 * @Override public void onSuccess(int statusCode, Header[] headers,
		 * byte[] responseBody) { Log.d("MyActivity", "Codigo: " + statusCode +
		 * " - Contenido: " + new String(responseBody)); Log.d("MyActivity",
		 * "Codigo: " + statusCode + " - LALALA: " + new
		 * String(responseBody).toString().toUpperCase()); }
		 * 
		 * @Override public void onFailure(int statusCode, Header[] headers,
		 * byte[] responseBody, Throwable error) { Log.e("MyActivity",
		 * "onFailure, codigo: " + statusCode, error); } });
		 */
		client.get(url, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// If the response is JSONObject instead of expected JSONArray
				Log.d("MyActivity",
						"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA1");
				try {

					String person = response.getString("name");
					Log.d("MyActivity", person);

				} catch (JSONException e) {
					Log.e("MyActivity", "JSONException encontrada", e);
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray arrayPersonas) {
				// Pull out the first event on the public timeline
				Log.d("MyActivity",
						"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA2");
				try {
					for (int i = 0; i < arrayPersonas.length(); i++) {
						JSONObject person = (JSONObject) arrayPersonas.get(i);
						Log.d("MyActivity", person.getString("name"));
					}
				} catch (JSONException e) {
					Log.e("MyActivity", "JSONException encontrada", e);
				}
			}
		});

	}

	String[] obtenerLogin(String mail, String contrasena) {
		final String[] arrayAux = new String[4];
		final String mailAux = mail;
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://192.168.56.1:8080/seekit/seekit/login";
		// +"?mail="
		// + mail + "&contrasenia=" + contrasena;
		Log.d("asdasdasd", url);
		client.get(url, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// If the response is JSONObject instead of expected JSONArray
				try {
					Log.d("AAAAAAAAAA", "AAAAAAAAA");
					arrayAux[0] = response.getString("nombre");
					arrayAux[1] = response.getString("apellido");
					arrayAux[2] = mailAux;
					arrayAux[3] = response.getString("idUsuario");

				} catch (JSONException e) {
					Log.d("BBBBBB", "BBBBBB");
					Log.e("MyActivity", "JSONException encontrada", e);
				}
			}

		});
		Log.d("CCCCCC", "CCCCCCCCCCc");
		return arrayAux;
	}

	public static void obtenerTrisDeUsuario(String idUsuario) {
		final List<Tri> listaTris = new ArrayList<Tri>();
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://192.168.56.1:8080/seekit/seekit/getTris?idUsuario="
				+ idUsuario;
		Log.d("la urlll", url);
		client.get(url, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray arrayTris) {
				
				// Pull out the first event on the public timeline
				try {
					
					for (int i = 0; i < arrayTris.length(); i++) {

						JSONObject tri = (JSONObject) arrayTris.get(i);
						Tri triAux = new Tri(tri.getString("idTri"),tri.getString("identificador"),
								tri.getString("nombre"), tri.getString("foto"),
								tri.getString("activo"), tri
										.getString("localizacion"), tri
										.getString("perdido"), tri
										.getString("compartido"), tri
                                .getString("descripcion"));
						Log.d("la urlll",triAux.getNombre());
						listaTris.add(triAux);
						
					}
					
					//MainActivity.reloadList(listaTris);
				} catch (JSONException e) {
					Log.e("MyActivity", "JSONException encontrada", e);
				}
				
			}

		});
		//return listaTris;
	}
}
