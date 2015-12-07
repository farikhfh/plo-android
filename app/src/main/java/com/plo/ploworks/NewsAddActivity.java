package com.plo.ploworks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farikh F H on 12/5/2015.
 */
public class NewsAddActivity extends AppCompatActivity {
    protected String URL;
    protected String AUTH;

    private String judulText, isiSingkatText, isiLengkapText;

    protected RequestQueue addNewsQueue;
    protected CreateRequest addNewsRequest;

    private EditText judul, isiSingkat, isiLengkap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_news_layout);

        //view initialization
        FloatingActionButton sendButton = (FloatingActionButton) findViewById(R.id.sendButton);
        judul = (EditText) findViewById(R.id.input_judul);
        isiSingkat = (EditText) findViewById(R.id.input_isi_singkat);
        isiLengkap = (EditText) findViewById(R.id.input_iai_lengkap);

        //url initialization
        URL = this.urlBuilder();

        //read from sharedPreferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
        AUTH = prefs.getString("authorization", "");

        judulText = judul.getText().toString();
        isiSingkatText = isiSingkat.getText().toString();
        isiLengkapText = isiLengkap.getText().toString();

        addNewsQueue = Volley.newRequestQueue(getApplicationContext());
        addNewsRequest = new CreateRequest(Request.Method.POST, URL,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                try {
//                    String error = response.getJSONObject("error").toString();
//                    if (error != null && error == "false"){
//                    }
//                } catch (JSONException e){
//                    e.printStackTrace();
//                }
                Log.d("Response",response.toString());
                finishActivity(RESULT_OK);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("judul", judul.getText().toString());
                param.put("singkat",isiSingkat.getText().toString());
                param.put("lengkap",isiLengkap.getText().toString());
                Log.d("param",param.toString());
                return param;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("authorization", AUTH);
                return headers;
            }
        };

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean validateJudul = validateForm(judul);
                Boolean validateIsiSingkat = validateForm(isiSingkat);
                Boolean validateIsiLengkap = validateForm(isiLengkap);

                if (validateJudul && validateIsiSingkat && validateIsiLengkap){
                    addNewsQueue.add(addNewsRequest);
                } else {
                    Toast.makeText(getApplicationContext(),"Silahkan lengkapi form diatas!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected String urlBuilder() {
        //Build URL to request
        String urlPost = "news/tambah/"+Constants.API_KEY+"/post.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(urlPost);
        String buildURL = url.build();

        return buildURL;
    }

    private Boolean validateForm(EditText text){
        if (text.getText().toString().length() == 0){
            text.setError("Required");
            return false;
        } else {
            return true;
        }
    }
}
