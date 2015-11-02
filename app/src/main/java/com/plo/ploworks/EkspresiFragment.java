package com.plo.ploworks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

public class EkspresiFragment extends Fragment{
    private ProgressDialog progress;
    private List<Ekspresi> ekspresiList = new ArrayList<>();
    private EkspresiListAdapter adapter;
    private Boolean flag_loading = false;
    private String auth;
    private final static String TOTAL_POSTS = "10";
    private final static String SORT_TYPE = "DESC";
    private int PAGE_LOADED = 1;

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
        ListView listEkspresi = (ListView) rootView.findViewById(R.id.list_view);

        //read from sharedPreferences
        SharedPreferences prefs = this.getActivity().getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
        auth = prefs.getString("authorization", "");
        Log.d("auth",auth.toString());

        this.progressDialogStart();
        //get initialize url
        String URL = this.urlBuilder();

        //request for first fragment generated
        RequestQueue timelineQueue = Volley.newRequestQueue(getActivity());
        CreateRequest jsonTimeLine = new CreateRequest(Request.Method.GET, URL, onSuccessListener(),
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
        timelineQueue.add(jsonTimeLine);
        listEkspresi.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (flag_loading == false) {
                        progressDialogStart();
                        flag_loading = true;
                        addItem();
                        progress.dismiss();
                    }
                }
            }
        });
        //load the list adapter
        adapter = new EkspresiListAdapter(getActivity(), ekspresiList);
        listEkspresi.setAdapter(adapter);

        progress.dismiss();
        return rootView;
    }

    private String urlBuilder(){
        //Build URL to request
        String timelineURL = "ekspresi/timeline.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("sort", SORT_TYPE);
        url.appendUrlQuery("jumlah", TOTAL_POSTS);
        url.appendUrlQuery("page", Integer.toString(PAGE_LOADED));

        String buildURL = url.build();

        return buildURL;
    }

    private void addItem(){
        flag_loading = false;

        //define page to next page for api
        PAGE_LOADED = PAGE_LOADED + 1;

        String URL = this.urlBuilder();
        //request for first fragment generated
        RequestQueue timelineQueue = Volley.newRequestQueue(getActivity());
        CreateRequest jsonTimeLine = new CreateRequest(Request.Method.GET, URL, onSuccessListener(),
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
        timelineQueue.add(jsonTimeLine);
    }

    //Volley ekspresi on success listener refresh
    private Response.Listener<JSONObject> onSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                   JSONArray jarray = response.getJSONArray("timeline");
                    for (int i = 0; i < jarray.length(); i++) {
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
//                            offset = Integer.parseInt(ekspresiList.get(ekspresiList.lastIndexOf(ekspresiList)).getNo());
//                            if(ekspresiList.isEmpty()){
//                                ekspresiList.add(e);
//                            }else if (Integer.parseInt(e.getNo()) < offset){
//                                ekspresiList.add(e);
//                            }
                            Log.d("Response","Ekspresi = "+e.getNo());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "No Data Received", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void progressDialogStart(){
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
    }
}
