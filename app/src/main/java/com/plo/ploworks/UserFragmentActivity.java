package com.plo.ploworks;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.ekspresi.Ekspresi;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farikh F H on 12/28/2015.
 */
public class UserFragmentActivity extends Fragment {
    private String auth,username;
    private TextView textNama,
                     textEmail,
                     textBiodata,
                     textEdit,
                     textLabelInstitusi,
                     textLabelMobile,
                     textInstitusi,
                     textMobile;
    private ImageView profilePicture;

    public UserFragmentActivity() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_user_profile, container, false);

        //view initialization
        textNama = (TextView) rootView.findViewById(R.id.textNamaProfile);
        textEmail = (TextView) rootView.findViewById(R.id.textEmailProfile);
        textBiodata = (TextView) rootView.findViewById(R.id.textBiodataProifle);
        textEdit = (TextView) rootView.findViewById(R.id.textEditBiodataProfile);
        textLabelInstitusi = (TextView) rootView.findViewById(R.id.textLabelInstitusiProfile);
        textLabelMobile = (TextView) rootView.findViewById(R.id.textLabelMobileProfile);
        textInstitusi = (TextView) rootView.findViewById(R.id.textInstitusiProfile);
        textMobile = (TextView) rootView.findViewById(R.id.textMobileProfile);
        profilePicture = (ImageView) rootView.findViewById(R.id.profilePictureProfile);

        //read from sharedPreferences
        SharedPreferences prefs = this.getActivity().getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
        auth = prefs.getString("authorization", "");
        username = prefs.getString("username","");

        //URL initialization
        String URL = this.urlBuilder();

        //request for first fragment generated
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        CreateRequest jsonProfile = new CreateRequest(Request.Method.GET, URL, onSuccessListener(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Network Timeout", Toast.LENGTH_LONG).show();
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
        requestQueue.add(jsonProfile);

        return rootView;
    }

    private String urlBuilder(){
        //Build URL to request
        String profileURL = "user/profil.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(profileURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("username", username);
        String buildURL = url.build();
        return buildURL;
    }
    private Response.Listener<JSONObject> onSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jobj = response.getJSONObject("profil");
                    String nama = jobj.getString("nama");
                    String email = jobj.getString("email");
                    String urlPp = jobj.getString("url_pp");
                    String biodata = jobj.getString("biodata");
                    String institusi = jobj.getString("institusi");
                    String noHp = jobj.getString("no_hp");

                    textNama.setText(nama.toString().toUpperCase());
                    textEmail.setText(email.toString());

                    //set profile picture
                    Picasso.with(getActivity())
                            .load(urlPp.toString())
                            .placeholder(R.drawable.def_image)
                            .resize(100,100)
                            .centerCrop()
                            .into(profilePicture);

                    if(biodata.toString().equals("none")){
                        textBiodata.setText("Biodata tidak diisi, klik edit untuk diisi");
                    } else {
                        textBiodata.setText(biodata.toString());
                    }

                    if (!institusi.toString().equals("none")){
                        textLabelInstitusi.setVisibility(View.VISIBLE);
                        textInstitusi.setVisibility(View.VISIBLE);
                        textInstitusi.setText(institusi.toString());
                    }

                    if (!noHp.toString().equals("none")){
                        textLabelMobile.setVisibility(View.VISIBLE);
                        textMobile.setVisibility(View.VISIBLE);
                        textMobile.setText(noHp.toString());
                    }
                } catch (Exception e) {
                    Log.d("Response",response.toString());
                    Toast.makeText(getActivity(), "No Data Received", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
}
