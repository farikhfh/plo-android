package com.plo.ploworks;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.plo.ploworks.berita.Berita;
import com.plo.ploworks.berita.BeritaListAdapter;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BeritaFragment extends Fragment{
    private ProgressDialog progress;
    private List<Berita> beritaList = new ArrayList<>();
    private BeritaListAdapter adapter;
    private Boolean flag_loading = false;
    private int offset = 0;
    private final static String TOTAL_POSTS = "10";
    private final static String SORT_TYPE = "DESC";
    private int PAGE_LOADED = 1;

    public static BeritaFragment newInstance(int page) {
        BeritaFragment fragment = new BeritaFragment();
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
        ListView listBerita = (ListView) rootView.findViewById(R.id.list_view);

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
                    });
            //request add for first time
            timelineQueue.add(jsonTimeLine);
        listBerita.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        adapter = new BeritaListAdapter(getActivity(), beritaList);
        listBerita.setAdapter(adapter);

        progress.dismiss();
        return rootView;
    }

    private String urlBuilder(){
        //Build URL to request
        String timelineURL = "news/timeline.json";
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
        flag_loading = false;
        String URL = this.urlBuilder();
        //request for first fragment generated
        RequestQueue timelineQueue = Volley.newRequestQueue(getActivity());
        CreateRequest jsonTimeLine = new CreateRequest(Request.Method.GET, URL, onSuccessListener(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Network Timeout", Toast.LENGTH_LONG).show();
                    }
                });
        //request add for first time
        timelineQueue.add(jsonTimeLine);
    }

    //Volley berita on success listener refresh
    private Response.Listener<JSONObject> onSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //define page to next page for api
                    PAGE_LOADED = PAGE_LOADED + 1;

                    JSONArray jarray = response.getJSONArray("timeline");
                    for (int i = 0; i < jarray.length(); i++) {
                        try {
                            JSONObject obj = jarray.getJSONObject(i);
                            Berita berita = new Berita();
                            berita.setIdPost(obj.getString("no"));
                            berita.setKodeUser(obj.getString("kode"));
                            berita.setUserName(obj.getString("username"));
                            berita.setUrlFotoUser(obj.getString("url_pp"));
                            berita.setNama(obj.getString("nama"));
                            berita.setWaktu(obj.getString("waktu"));
                            berita.setJudul(obj.getString("judul"));
                            berita.setIsiSingkat(obj.getString("isi_singkat"));
                            berita.setUrlGambar(obj.getString("gambar"));
                            berita.setJumlahKomentar(obj.getString("jumlah_komentar"));
                            berita.setKomentarTerakhir(obj.getString("komentar_terakhir"));

//                            offset = Integer.parseInt(beritaList.get(beritaList.lastIndexOf(beritaList)).getIdPost());
//                            if(beritaList.isEmpty()){
//                                beritaList.add(berita);
//                            }else if (Integer.parseInt(berita.getIdPost()) < offset){
//                                beritaList.add(berita);
//                            }
                            beritaList.add(berita);
                            Log.d("Response", response.toString());
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
