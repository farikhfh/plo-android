package com.plo.ploworks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.komentar.Komentar;
import com.plo.ploworks.komentar.KomentarBeritaListAdapter;
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

public class KomentarCurgasActivity extends AppCompatActivity {
    private RequestQueue komentarQueue;
    private KomentarBeritaListAdapter adapter;
    private List<Komentar> komentarList = new ArrayList<>();

    private String ID_POST;
    private String auth;

    private EditText komentarText;
    private Button komentarButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komentar_berita);

        //view initialization
        ListView listKomentar = (ListView) findViewById(R.id.list_view_komentar);
        komentarButton = (Button) findViewById(R.id.sendButtonKomentar);
        komentarText = (EditText) findViewById(R.id.editTextKomentar);
        komentarText.setBackgroundResource(R.drawable.komentar_edittext_background);

        //get intent from previous activity
        Intent intent = getIntent();
        ID_POST = intent.getStringExtra("ID_POST");
        //get initialize url
        String URL = this.urlTimelineKomentar(ID_POST);

        //read from sharedPreferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
        auth = prefs.getString("authorization", "");

        //request for first fragment generated
        komentarQueue = Volley.newRequestQueue(this);
        CreateRequest jsonKomentar = new CreateRequest(Request.Method.GET, URL, onSuccessListener(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Network Timeout", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("authorization",auth);
                return headers;
            }
        };

        //request add for first time
        komentarQueue.add(jsonKomentar);

        //load the list adapter
        adapter = new KomentarBeritaListAdapter(this, komentarList);
        listKomentar.setAdapter(adapter);

        komentarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable komentar = komentarText.getText();
                String url = urlPostKomentar(ID_POST);

                RequestQueue komentarPostQueue = Volley.newRequestQueue(getApplicationContext());
                CreateRequest postKomentarRequest = new CreateRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        Log.d("Response", response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Network Timeout", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<String, String>();
                        param.put("apikey", Constants.API_KEY);
                        param.put("komentar", komentar.toString());
                        return param;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("authorization", auth);
                        return headers;
                    }
                };

                komentarPostQueue.add(postKomentarRequest);
            }
        });

        listKomentar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    private String urlTimelineKomentar(String ID_POST) {
        //Build URL to request
        String komentarURL = "curgas/komentar.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(komentarURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("kode_curgas", ID_POST);

        String buildURL = url.build();

        return buildURL;
    }

    private String urlPostKomentar(String ID_POST){
        //Build URL to request
        String komentarURL = "curgas/tambah/"+Constants.API_KEY+"/komentar-"+ID_POST+".json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(komentarURL);
        String buildURL = url.build();

        return buildURL;
    }

    private Response.Listener<JSONObject> onSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jarray = response.getJSONArray("komentar");
                    for (int i = 0; i < jarray.length(); i++) {
                        try {
                            JSONObject obj = jarray.getJSONObject(i);
                            Komentar komentar = new Komentar();
                            komentar.setUsername(obj.getString("username"));
                            komentar.setNo(Integer.parseInt(obj.getString("no")));
                            komentar.setUrl_gambar(obj.getString("gambar"));
                            komentar.setWaktu(obj.getString("waktu"));
                            komentar.setIsi(obj.getString("isi"));
                            komentar.setNama(obj.getString("nama"));
                            komentar.setUrl_pp(obj.getString("url_pp"));
                            if(obj.get("tanggapan").equals("none")){
                                komentar.setHasTanggapan(false);
                            } else {
                                komentar.setHasTanggapan(true);
                                komentar.setTanggapan(obj.getJSONArray("tanggapan"));
                            }
                            komentarList.add(komentar);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Data Received", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    //TODO Create tanggapan in komentar :: KomentarCurgas
}
