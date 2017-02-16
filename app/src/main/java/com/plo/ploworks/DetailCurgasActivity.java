package com.plo.ploworks;

import android.app.Activity;
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
import com.plo.ploworks.curgas.Curgas;
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

public class DetailCurgasActivity extends Activity {
    private RequestQueue detailQueue;
    private String detailURL , komentarURL,  auth;
    private Curgas curgas;

    private TextView textNama,textUsername,textWaktu,textJudul,textIsiSingkat,textIsiLengkap, textUrl;
    private ImageView mProfilePicture, mContentPicture;
    private Button buttonKomentar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_curgas);

        //view initialization
        textNama = (TextView) findViewById(R.id.textNamaDetailCurgas);
        textUsername = (TextView) findViewById(R.id.textUsernameDetailCurgas);
        textWaktu = (TextView) findViewById(R.id.textWaktuDetailCurgas);
        textJudul = (TextView) findViewById(R.id.textJudulDetailCurgas);
        textIsiSingkat = (TextView) findViewById(R.id.textIsiSingkatDetailCurgas);
        textIsiLengkap = (TextView) findViewById(R.id.textIsiLengkapDetailCurgas);
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

        curgas = new Curgas();

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
                Intent intent = new Intent(DetailCurgasActivity.this, KomentarCurgasActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        //request add for first time
        detailQueue.add(jsonDetail);
    }

    private String detailUrl(String ID_POST) {
        //Build URL to request
        String timelineURL = "curgas/detail.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("kode_curgas", ID_POST);

        String buildURL = url.build();

        return buildURL;
    }

    private void layoutContentSet (JSONObject obj){
        try {
            curgas.setNo(obj.getString("no"));
            curgas.setKode(obj.getString("kode"));
            curgas.setUsername(obj.getString("username"));
            curgas.setUrl_pp(obj.getString("url_pp"));
            curgas.setNama(obj.getString("nama"));
            curgas.setWaktu(obj.getString("waktu"));
            curgas.setJudul(obj.getString("judul"));
            curgas.setIsiSingkat(obj.getString("isi_singkat"));
            curgas.setIsiLengkap(obj.getString("isi_lengkap"));
            curgas.setGambar(obj.getString("gambar"));
            curgas.setJumlahKomentar(obj.getString("jumlah_komentar"));
            curgas.setKomentarTerakhir(obj.getString("komentar_terakhir"));

            //set view item
            textNama.setText(curgas.getNama());
            textUsername.setText("("+curgas.getUsername()+")");
            textWaktu.setText(curgas.getWaktu());
            textJudul.setText(curgas.getJudul());
            textIsiSingkat.setText(Html.fromHtml(curgas.getIsiSingkat()));
            buttonKomentar.setText("Komentar "+"("+curgas.getJumlahKomentar()+")");

            Picasso.with(getApplicationContext())
                    .load(curgas.getUrl_pp())
                    .resize(60,60)
                    .centerCrop()
                    .placeholder(getResources().getDrawable(R.drawable.def_image))
                    .into(mProfilePicture);

//            if(curgas.getUrlGambar().equals("none")){
//            } else {
//
//            }
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            Picasso.with(getApplicationContext())
                    .load(curgas.getGambar())
                    .resize(width,height)
                    .centerInside()
                    .into(mContentPicture);

            if (curgas.getIsiLengkap().equals("")){
                textIsiLengkap.setVisibility(View.GONE);
            }
            else {
                textIsiLengkap.setText(Html.fromHtml(curgas.getIsiLengkap()));
            }

            buttonKomentar.setText("Komentar ("+curgas.getJumlahKomentar()+")");

        } catch (JSONException e){

        }
    }
    private String komentarURL(String ID_POST){
        String timelineURL = "curgas/komentar.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("kode_curgas", ID_POST);

        String buildURL = url.build();

        return buildURL;
    }
}
