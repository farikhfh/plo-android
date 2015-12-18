package com.plo.ploworks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.plo.ploworks.berita.Berita;
import com.plo.ploworks.curgas.Curgas;
import com.plo.ploworks.curgas.CurgasListAdapter;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CurgasFragment extends Fragment{
    private ProgressDialog progress;
    private List<Curgas> curgasList = new ArrayList<>();
    private CurgasListAdapter adapter;
    private Boolean flag_loading = false;
    private int offset = 0;
    private final static String TOTAL_POSTS = "10";
    private final static String SORT_TYPE = "DESC";
    private int PAGE_LOADED = 1;

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
        ListView listCurgas = (ListView) rootView.findViewById(R.id.list_view);
        FloatingActionButton postCurgasButton = (FloatingActionButton) rootView.findViewById(R.id.fab);

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
        listCurgas.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (flag_loading == false) {
                        flag_loading = true;
                        addItem();
                    }
                }
            }
        });
        //load the list adapter
        adapter = new CurgasListAdapter(getActivity(), curgasList);
        listCurgas.setAdapter(adapter);

        listCurgas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Curgas item = (Curgas) parent.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putString("ID_POST", item.getKode());
                Intent intent = new Intent(getActivity(), DetailCurgasActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        postCurgasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt("CODE",2);
                Intent intent = new Intent(getActivity(),NewsAddActivity.class);
                intent.putExtras(b);
                startActivityForResult(intent,getTargetRequestCode());
            }
        });

        return rootView;
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
        flag_loading = false;

        //define page to next page for request
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
                });
        //request add for first time
        timelineQueue.add(jsonTimeLine);
    }

    //Volley curgas on success listener refresh
    private Response.Listener<JSONObject> onSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {


                    JSONArray jarray = response.getJSONArray("timeline");
                    for (int i = 0; i < jarray.length(); i++) {
                        try {
                            JSONObject obj = jarray.getJSONObject(i);
                            Curgas curgas = new Curgas();
                            curgas.setNo(obj.getString("no"));
                            curgas.setKode(obj.getString("kode"));
                            curgas.setUrl_pp(obj.getString("url_pp"));
                            curgas.setNama(obj.getString("nama"));
                            curgas.setUsername(obj.getString("username"));
                            curgas.setWaktu(obj.getString("waktu"));
                            curgas.setJudul(obj.getString("judul"));
                            curgas.setIsiSingkat(obj.getString("isi_singkat"));
                            curgas.setGambar(obj.getString("gambar"));
                            curgas.setKomentarTerakhir(obj.getString("komentar_terakhir"));
                            curgas.setJumlahKomentar(obj.getString("jumlah_komentar"));
                            curgasList.add(curgas);

                            Log.d("Response", "Curgas = "+curgas.getNo());
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
}
