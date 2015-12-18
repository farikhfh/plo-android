package com.plo.ploworks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.ekspresi.Ekspresi;
import com.plo.ploworks.ekspresi.EkspresiKomentarListAdapter;
import com.plo.ploworks.ekspresi.EkspresiListAdapter;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Farikh F H on 12/7/2015.
 */
public class DetailEkspresiActivity extends AppCompatActivity{
    private EkspresiKomentarListAdapter adapter;
    private List<Ekspresi> listKomentarEkspresi = new ArrayList<>();
    private RequestQueue komentarQueue;

    private Button komentarButton;
    private EditText komentarText;
    private TextView textNama, textWaktu, textUsername, textEkspresi;

    private String NO_POST;
    private String AUTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ekspresi);

        //view initialization
        textNama = (TextView) findViewById(R.id.textNamaEkspresi);
        textWaktu = (TextView) findViewById(R.id.textWaktuEkspresi);
        textUsername = (TextView) findViewById(R.id.textUsernameEkspresi);
        textEkspresi = (TextView) findViewById(R.id.textEkspresi);
        ImageView mProfilePicture = (ImageView) findViewById(R.id.profilePicture);

        ListView listKomentar = (ListView) findViewById(R.id.list_view_komentar_ekspresi);
        komentarButton = (Button) findViewById(R.id.sendButtonKomentar);
        komentarText = (EditText) findViewById(R.id.editTextKomentar);
        komentarText.setBackgroundResource(R.drawable.komentar_edittext_background);

        //get intent from previous activity
        Intent intent = getIntent();
        NO_POST = intent.getStringExtra("NO_POST");
        Log.d("id",NO_POST);

        //set textView get from intent
        textNama.setText(intent.getStringExtra("NAMA_POST"));
        textUsername.setText(intent.getStringExtra("USERNAME_POST"));
        textWaktu.setText(intent.getStringExtra("WAKTU_POST"));
        textEkspresi.setText(intent.getStringExtra("EKSPRESI_POST"));

        //set profile picture
        Picasso.with(getApplicationContext())
                .load(intent.getStringExtra("URL_PP_POST"))
                .placeholder(R.drawable.def_image)
                .resize(50,50)
                .centerCrop()
                .into(mProfilePicture);

        //get initialize url
        String URL = this.urlListKomentar(NO_POST);


        //read from sharedPreferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
        AUTH = prefs.getString("authorization", "");

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
                headers.put("authorization",AUTH);
                return headers;
            }
        };

        //request add for first time
        komentarQueue.add(jsonKomentar);

        //load the list adapter
        adapter = new EkspresiKomentarListAdapter(this, listKomentarEkspresi);
        listKomentar.setAdapter(adapter);

        //post komentar
        komentarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable komentar = komentarText.getText();
                String url = urlPostKomentar(NO_POST);

                RequestQueue komentarPostQueue = Volley.newRequestQueue(getApplicationContext());
                CreateRequest postKomentarRequest = new CreateRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        finish();
                        startActivity(getIntent());
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
                        headers.put("authorization", AUTH);
                        return headers;
                    }
                };

                komentarPostQueue.add(postKomentarRequest);
            }
        });
    }

    private String urlListKomentar(String ID_POST) {
        //Build URL to request
        String komentarURL = "ekspresi/detail.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(komentarURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("nomer", ID_POST);

        String buildURL = url.build();

        return buildURL;
    }

    private String urlPostKomentar(String ID_POST){
        //Build URL to request
        String komentarURL = "ekspresi/tambah/"+Constants.API_KEY+"/komentar-"+ID_POST+".json";
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
                    JSONArray jarray = response.getJSONArray("detail");
                    JSONObject detailObject = jarray.getJSONObject(0);
                    if(!detailObject.get("komentar").equals("none")){
                        JSONArray komentarArray = detailObject.getJSONArray("komentar");
                        for (int i = 0; i < komentarArray.length(); i++){
                            try {
                                JSONObject komentarObj = komentarArray.getJSONObject(i);
                                Ekspresi ekspresi = new Ekspresi();
                                ekspresi.setNo(komentarObj.get("urut").toString());
                                ekspresi.setNama(komentarObj.get("nama").toString());
                                ekspresi.setUsername(komentarObj.get("username").toString());
                                ekspresi.setUrl_pp(komentarObj.get("url_pp").toString());
                                ekspresi.setWaktu(komentarObj.get("waktu").toString());
                                ekspresi.setEkspresi(komentarObj.get("pesan").toString());
                                listKomentarEkspresi.add(ekspresi);
                            } catch (JSONException e){
                                e.printStackTrace();
                                Log.d("Error","Error Here A");
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Data Received", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
}
