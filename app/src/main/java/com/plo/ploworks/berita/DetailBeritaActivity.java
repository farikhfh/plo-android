package com.plo.ploworks.berita;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.R;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailBeritaActivity extends AppCompatActivity {
    private RequestQueue detailQueue;
    private String URL , auth;
    private Berita berita;
    private TextView textNama;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_berita);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String ID_POST = intent.getStringExtra("ID_POST");
        URL = this.urlBuilder(ID_POST);
        berita = new Berita();

        //request for first fragment generated
        detailQueue = Volley.newRequestQueue(this);
        CreateRequest jsonDetail = new CreateRequest(Request.Method.GET, URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jarray = response.getJSONArray("detail");
                        try {
                            JSONObject obj = jarray.getJSONObject(0);
                            berita.setIdPost(obj.getString("no"));
                            berita.setKodeUser(obj.getString("kode"));
                            berita.setUserName(obj.getString("username"));
                            berita.setUrlFotoUser(obj.getString("url_pp"));
                            berita.setNama(obj.getString("nama"));
                            berita.setWaktu(obj.getString("waktu"));
                            berita.setJudul(obj.getString("judul"));
                            berita.setIsiSingkat(obj.getString("isi_singkat"));
                            berita.setUrlGambar(obj.getString("gambar"));
                            berita.setJumlahKomentar(obj.getString("jumlah_komentar"));
                            berita.setKomentarTerakhir(obj.getString("komentar_terakhir"));

                            textNama.setText(berita.getNama());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Data Received", Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Network Timeout", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //read from sharedPreferences
                SharedPreferences prefs = getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
                auth = prefs.getString("authorization", "");
                headers.put("authorization",auth);
                return headers;
            }
        };
        Log.d("Response", "Berita = " + berita.getNama());
        //request add for first time
        detailQueue.add(jsonDetail);

        textNama = (TextView) findViewById(R.id.textNamaDetailBerita);
    }

    private String urlBuilder(String ID_POST) {
        //Build URL to request
        String timelineURL = "news/detail.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("kode_news", ID_POST);

        String buildURL = url.build();

        return buildURL;
    }
}
