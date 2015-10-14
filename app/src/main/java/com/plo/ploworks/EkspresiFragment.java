package com.plo.ploworks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.ekspresi.Ekspresi;
import com.plo.ploworks.ekspresi.EkspresiListAdapter;
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


public class EkspresiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean flag_loading;
    private EkspresiListAdapter adapter;
    private List<Ekspresi> ekspresiList = new ArrayList<>();

    private int PAGE_LOADED = 1;
    private final static String TOTAL_POSTS = "10";
    private final static String SORT_TYPE = "DESC";
    private String auth;

    public static EkspresiFragment newInstance(int page) {
        EkspresiFragment fragment = new EkspresiFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //set root view with fragment
        View rootView = inflater.inflate(R.layout.fragment_list,container,false);

        if(savedInstanceState == null) {
            //get initialize url
            String URL = this.urlBuilder();

            //read from sharedPreferences
            SharedPreferences prefs = this.getActivity().getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
            auth = prefs.getString("authorization", "");
            Log.d("Auth",auth);

            //request for first fragment generated
            RequestQueue timelineQueue = Volley.newRequestQueue(getActivity());
            CreateRequest jsonTimeLine = new CreateRequest(Request.Method.GET, URL, onSuccessListener(),
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Network Timeout", Toast.LENGTH_LONG).show();
                            flag_loading = false;
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError{
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("authorization", auth);
                    return params;
                }
            };

            //request add for first time
            timelineQueue.add(jsonTimeLine);
            ListView listEkspresi = (ListView) rootView.findViewById(R.id.list_view);

            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    addItem();
                }
            });

            adapter = new EkspresiListAdapter(getActivity(), ekspresiList);
            listEkspresi.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    public void onRefresh(){
        addItem();
    }

    //Volley ekspresi on success listener
    private Response.Listener<JSONObject> onSuccessListener(){
        return new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //define page to next page for api
                    PAGE_LOADED = PAGE_LOADED + 1;

                    JSONArray jarray = response.getJSONArray("timeline");
                    for (int i = 0; i < jarray.length(); i++){
                        try {
                            JSONObject obj = jarray.getJSONObject(i);
                            Ekspresi e = new Ekspresi();
                            e.setNo(obj.getString("no"));
                            e.setWaktu(obj.getString("waktu"));
                            e.setUsername(obj.getString("username"));
                            e.setEkspresi(obj.getString("ekspresi").toString());
                            e.setNama(obj.getString("nama"));
                            e.setGambar(obj.getString("gambar"));
                            e.setJumlah(obj.getString("jumlah"));
                            ekspresiList.add(e);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    //dismiss progressDialog
                    flag_loading = false;

                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d("Response", "Response = " + jarray.toString());
                }catch(Exception e){
                    Toast.makeText(getActivity(), "No Data Received", Toast.LENGTH_LONG).show();

                    flag_loading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
    }

    private String urlBuilder(){
        //Build URL to request
        String timelineURL = "ekspresi/timeline.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("sort", SORT_TYPE);
        url.appendUrlQuery("jumlah",TOTAL_POSTS);
        url.appendUrlQuery("page",Integer.toString(PAGE_LOADED));

        String buildURL = url.build();

        return buildURL;
    }

    private void addItem(){
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);


        String URL = this.urlBuilder();

        //request for first fragment generated
        RequestQueue timelineQueue = Volley.newRequestQueue(getActivity());
        CreateRequest jsonTimeLine = new CreateRequest(Request.Method.GET, URL,onSuccessListener(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Network Timeout", Toast.LENGTH_LONG).show();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("authorization", auth);
                return params;
            }
        };


        //request add for first time
        timelineQueue.add(jsonTimeLine);


    }


}
