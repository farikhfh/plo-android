package com.plo.ploworks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.curgas.Curgas;
import com.plo.ploworks.curgas.CurgasListAdapter;
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


public class CurgasFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean flag_loading;
    private CurgasListAdapter adapter;
    private List<Curgas> curgasList = new ArrayList<>();

    private int PAGE_LOADED = 1;
    private final static String TOTAL_POSTS = "10";
    private final static String SORT_TYPE = "DESC";

    public static CurgasFragment newInstance(int page) {
        CurgasFragment fragment = new CurgasFragment();
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

            //request for first fragment generated
            RequestQueue timelineQueue = Volley.newRequestQueue(getActivity());
            CreateRequest jsonTimeLine = new CreateRequest(Request.Method.GET, URL, onSuccessListener(),
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Network Timeout", Toast.LENGTH_LONG).show();
                            flag_loading = false;
                        }
                    });


            //request add for first time
            timelineQueue.add(jsonTimeLine);

            ListView listCurgas = (ListView) rootView.findViewById(R.id.list_view);

            //top swiping to get new item
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    addItem();
                }
            });

            //load the list adapter
            adapter = new CurgasListAdapter(getActivity(), curgasList);
            listCurgas.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    public void onRefresh(){
        addItem();
    }

    //Volley curgas on success listener
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
                            Curgas curgas = new Curgas();
                            curgas.setNo(obj.getString("no"));
                            curgas.setKode(obj.getString("kode"));
                            curgas.setUrl_pp(obj.getString("url_pp"));
                            curgas.setNama(obj.getString("nama"));
                            curgas.setWaktu(obj.getString("waktu"));
                            curgas.setJudul(obj.getString("judul"));
                            curgas.setIsiSingkat(obj.getString("isi_singkat"));
                            curgas.setGambar(obj.getString("gambar"));
                            curgas.setJumlahKomentar(obj.getString("jumlah_komentar"));
                            curgasList.add(curgas);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

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
        String timelineURL = "curgas/timeline.json";
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
        CreateRequest jsonTimeLine = new CreateRequest(Request.Method.GET, URL, onSuccessListener(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Network Timeout", Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });


        //request add for first time
        timelineQueue.add(jsonTimeLine);


    }


}
