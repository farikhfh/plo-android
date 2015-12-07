package com.plo.ploworks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.R;
import com.plo.ploworks.berita.Berita;
import com.plo.ploworks.KomentarBeritaActivity;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateImageRequest;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class DetailBeritaActivity extends AppCompatActivity {
    private RequestQueue detailQueue;
    private String detailURL , komentarURL,  auth;
    private Berita berita;

    private TextView textNama,textUsername,textWaktu,textJudul,textIsiSingkat,textIsiLengkap, textUrl;
    private ImageView mProfilePicture, mContentPicture;
    private Button buttonKomentar;

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_berita);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //view initialization
        textNama = (TextView) findViewById(R.id.textNamaDetailBerita);
        textUsername = (TextView) findViewById(R.id.textUsernameDetailBerita);
        textWaktu = (TextView) findViewById(R.id.textWaktuDetailBerita);
        textJudul = (TextView) findViewById(R.id.textJudulDetailBerita);
        textIsiSingkat = (TextView) findViewById(R.id.textIsiSingkatDetailBerita);
        textIsiLengkap = (TextView) findViewById(R.id.textIsiLengkapDetailBerita);
        buttonKomentar = (Button) findViewById(R.id.buttonKomentar);

        //image content initialization
        mProfilePicture = (ImageView) findViewById(R.id.profilePictureImage);
        mContentPicture = (ImageView) findViewById(R.id.contentImage);

        Intent intent = getIntent();
        final String ID_POST = intent.getStringExtra("ID_POST");

        detailURL = this.detailUrl(ID_POST);
        komentarURL = this.komentarURL(ID_POST);
        Log.d("URL",detailURL);
        Log.d("URL",komentarURL);

        berita = new Berita();

        //request for first fragment generated
        detailQueue = Volley.newRequestQueue(this);
        CreateRequest jsonDetail = new CreateRequest(Request.Method.GET, detailURL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jarray = response.getJSONArray("detail");
                        try {
                            JSONObject obj = jarray.getJSONObject(0);
                            layoutContentSet(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
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

        buttonKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("ID_POST", ID_POST);
                Intent intent = new Intent(DetailBeritaActivity.this, KomentarBeritaActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        //request add for first time
        detailQueue.add(jsonDetail);
    }

    private String detailUrl(String ID_POST) {
        //Build URL to request
        String timelineURL = "news/detail.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("kode_news", ID_POST);

        String buildURL = url.build();

        return buildURL;
    }

    private void layoutContentSet (JSONObject obj){
        try {
            berita.setIdPost(obj.getString("no"));
            berita.setKodeUser(obj.getString("kode"));
            berita.setUserName(obj.getString("username"));
            berita.setUrlFotoUser(obj.getString("url_pp"));
            berita.setNama(obj.getString("nama"));
            berita.setWaktu(obj.getString("waktu"));
            berita.setJudul(obj.getString("judul"));
            berita.setIsiSingkat(obj.getString("isi_singkat"));
            berita.setIsiLengkap(obj.getString("isi_lengkap"));
            berita.setUrlGambar(obj.getString("gambar"));
            berita.setJumlahKomentar(obj.getString("jumlah_komentar"));
            berita.setKomentarTerakhir(obj.getString("komentar_terakhir"));

            //set view item
            textNama.setText(berita.getNama());
            textUsername.setText("("+berita.getUserName()+")");
            textWaktu.setText(berita.getWaktu());
            textJudul.setText(berita.getJudul());
            textIsiSingkat.setText(Html.fromHtml(berita.getIsiSingkat()));
            buttonKomentar.setText("Komentar "+"("+berita.getJumlahKomentar()+")");

            Picasso.with(getApplicationContext())
                    .load(berita.getUrlFotoUser())
                    .resize(60,60)
                    .centerCrop()
                    .placeholder(getResources().getDrawable(R.drawable.def_image))
                    .into(mProfilePicture);

//            if(berita.getUrlGambar().equals("none")){
//            } else {
//
//            }
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            Picasso.with(getApplicationContext())
                    .load(berita.getUrlGambar())
                    .resize(width,height)
                    .centerInside()
                    .into(mContentPicture);

            if (berita.getIsiLengkap().equals("")){
                textIsiLengkap.setVisibility(View.GONE);
            }
            else {
                textIsiLengkap.setText(Html.fromHtml(berita.getIsiLengkap()));
            }

            buttonKomentar.setText("Komentar ("+berita.getJumlahKomentar()+")");

        } catch (JSONException e){

        }
    }
    private String komentarURL(String ID_POST){
        String timelineURL = "news/komentar.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("kode_news", ID_POST);

        String buildURL = url.build();

        return buildURL;
    }
}
