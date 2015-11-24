package com.plo.ploworks.komentar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.R;
import com.plo.ploworks.berita.Berita;
import com.plo.ploworks.berita.BeritaListAdapter;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Farikh Fadlul Huda on 11/19/2015.
 */
public class KomentarBeritaActivity extends AppCompatActivity {
    private ListView listKomentar;
    private String komentarUrl;
    private RequestQueue komentarQueue;
    private List<Komentar> komentarList = new ArrayList<>();
    private KomentarBeritaListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_komentar_berita);

        //get ID_POST from bundle
        Intent intent = getIntent();
        final String ID_POST = intent.getStringExtra("ID_POST");

        //build url for komentar
        komentarUrl = this.urlBuilder(ID_POST);

        //listview initialization
        listKomentar = (ListView) findViewById(R.id.list_view_komentar);

        //volley initialization
        komentarQueue = Volley.newRequestQueue(this);
        CreateRequest komentarRequest = new CreateRequest(Request.Method.GET, komentarUrl, this.onSuccessListener(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Network Timeout", Toast.LENGTH_LONG).show();
                    }
                }){
            //add headers for authorization
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //read from sharedPreferences to get authorization string
                SharedPreferences prefs = getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
                String auth = prefs.getString("authorization", "");
                headers.put("authorization",auth);
                return headers;
            }
        };

        adapter = new KomentarBeritaListAdapter(getParent(), komentarList);
        listKomentar.setAdapter(adapter);

        komentarQueue.add(komentarRequest);
    }

    //url builder for komentar request
    private String urlBuilder(String ID_POST) {
        //Build URL to request
        String timelineURL = "news/komentar.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("kode_news",ID_POST);

        String buildURL = url.build();

        return buildURL;
    }

    //komentar list volley success listener
    private Response.Listener<JSONObject> onSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getJSONObject("error").equals("false")){
                        JSONArray jarray = response.getJSONArray("timeline");
                        for (int i = 0; i < jarray.length(); i++) {
                            try {
                                JSONObject obj = jarray.getJSONObject(i);
                                Komentar komentar = new Komentar();
                                komentar.setNo(Integer.parseInt(obj.getString("no")));
                                komentar.setUsername(obj.getString("username"));
                                komentar.setUrl_pp(obj.getString("url_pp"));
                                komentar.setNama(obj.getString("nama"));
                                komentar.setIsi(obj.getString("isi"));
                                komentar.setWaktu(obj.getString("waktu"));
                                komentar.setUrl_gambar(obj.getString("gambar"));

                                komentarList.add(komentar);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Data Received", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
}
