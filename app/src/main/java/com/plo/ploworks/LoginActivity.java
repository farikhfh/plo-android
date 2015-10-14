package com.plo.ploworks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String API_KEY = "tcn17PjFsyrBXWRd";
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private String LOGIN_URL;
    private String URL;
    private ProgressDialog progressDialog;

    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

    }

    private void userLogin(){
        //define username and password and retrieve it from view
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();

        //adding POST parameter to request
        Map<String,String> param = new HashMap<String,String>();
        param.put("username",username);
        param.put("password",password);
        param.put("mode","1");

        //URL to login
        LOGIN_URL = "user/login.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(LOGIN_URL);
        url.appendUrlQuery("apikey", API_KEY);
        URL = url.build();
        Log.d("URL", "Url = " + url.toString());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        CreateRequest jsonObjectReq = new CreateRequest(Method.POST , URL,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        //check username and password if its empty or not change
                        if(username.equals("") | username.equals("username") | password.equals("") | password.equals("password")){
                            Toast.makeText(getApplicationContext(),"Username dan Password tidak boleh kosong",Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                //it should have to set sharedPreferences and save the login state
                                if (response.get("error").equals(false)) {
                                    //put response information to sharedPreferences
                                    SharedPreferences userDetail = getSharedPreferences("userDetail",MODE_PRIVATE);
                                    SharedPreferences.Editor edit = userDetail.edit();
                                    edit.clear();
                                    edit.putString("username", username);
                                    edit.putString("password",password);
                                    edit.putString("authorization",response.get("authorization").toString());
                                    edit.commit();

                                    //dismiss progressDialog
                                    progressDialog.dismiss();

                                    //start main activity after login
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Username dan Password Salah", Toast.LENGTH_LONG).show();
                                }
                                Log.d("Response Log", "Response = " + response.toString() + "Username = " + username + " Password = " + password + " Authorization = " + response.get("authorization").toString());
                                Log.d("URL", "Url = " + url.toString());
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                Log.e("Error Log", "Error");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Network Timeout", Toast.LENGTH_LONG).show();
                    }
                }){
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
        };
        requestQueue.add(jsonObjectReq);

        //adding progress dialog
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
