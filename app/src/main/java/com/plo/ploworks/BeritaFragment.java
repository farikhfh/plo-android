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
import com.plo.ploworks.berita.BeritaListAdapter;
import com.plo.ploworks.network.Constants;
import com.plo.ploworks.network.CreateRequest;
import com.plo.ploworks.network.RequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BeritaFragment extends Fragment {
    private ProgressDialog progress;
    private List<Berita> beritaList = new ArrayList<>();
    private BeritaListAdapter adapter;
    private RequestQueue timelineQueue;
    private Boolean flag_loading = false;
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
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ListView listBerita = (ListView) rootView.findViewById(R.id.list_view);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        this.progressDialogStart();
        //get initialize url
        String URL = this.urlBuilder();

        //request for first fragment generated
        timelineQueue = Volley.newRequestQueue(getActivity());
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
        listBerita.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Berita item = (Berita) parent.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putString("ID_POST", item.getKodeUser());
                Intent intent = new Intent(getActivity(), DetailBeritaActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        //load the list adapter
        adapter = new BeritaListAdapter(getActivity(), beritaList);
        listBerita.setAdapter(adapter);
        progress.dismiss();

        fab.setOnClickListener(new View.OnClickListener() {
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

    private String urlBuilder() {
        //Build URL to request
        String timelineURL = "news/timeline.json";
        final RequestBuilder.UrlBuilder url = new RequestBuilder.UrlBuilder();
        url.setUrl(timelineURL);
        url.appendUrlQuery("apikey", Constants.API_KEY);
        url.appendUrlQuery("sort", SORT_TYPE);
        url.appendUrlQuery("jumlah", TOTAL_POSTS);
        url.appendUrlQuery("page", Integer.toString(PAGE_LOADED));

        String buildURL = url.build();

        return buildURL;
    }

    private void addItem() {
        flag_loading = false;
        PAGE_LOADED = PAGE_LOADED + 1;
        String URL = this.urlBuilder();
        //request for first fragment generated
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

                            beritaList.add(berita);
                            Log.d("Response", "Berita = " + berita.getIdPost());
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

    private void progressDialogStart() {
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
    }

}
