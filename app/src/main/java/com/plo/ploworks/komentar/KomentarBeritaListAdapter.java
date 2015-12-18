package com.plo.ploworks.komentar;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.plo.ploworks.R;
import com.plo.ploworks.komentar.Komentar;
import com.plo.ploworks.network.CreateImageRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.jar.JarException;

public class KomentarBeritaListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Komentar> komentarItem;

    private LinearLayout tanggapanLayout;

    public KomentarBeritaListAdapter(Activity activity, List<Komentar> komentarItem) {
        this.activity = activity;
        this.komentarItem = komentarItem;
    }
    @Override
    public int getCount() {
        return komentarItem.size();
    }

    @Override
    public Object getItem(int location) {
        return komentarItem.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_komentar_berita, null);
        }

        TextView textNama = (TextView) convertView.findViewById(R.id.textNamaKomentar);
        TextView textUsername = (TextView) convertView.findViewById(R.id.textUsernameKomentar);
        TextView textWaktu = (TextView) convertView.findViewById(R.id.textWaktuKomentar);
        TextView textKomentar = (TextView) convertView.findViewById(R.id.textKomentar);
        ImageView mProfilePicture = (ImageView) convertView.findViewById(R.id.profilePictureKomentar);
        ImageView mImageContent = (ImageView) convertView.findViewById(R.id.imageIsiKomentar);

        Komentar komentar = komentarItem.get(position);

        textNama.setText(komentar.getNama());
        textUsername.setText("("+komentar.getUsername()+")");
        textWaktu.setText(komentar.getWaktu());
        textKomentar.setText(Html.fromHtml(komentar.getIsi()));

        Picasso.with(convertView.getContext())
                .load(komentar.getUrl_pp())
                .placeholder(R.drawable.def_image)
                .resize(50,50)
                .centerCrop()
                .into(mProfilePicture);

        //Content image
        if(komentar.getUrl_gambar() != "none"){
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            Picasso.with(convertView.getContext())
                    .load(komentar.getUrl_gambar())
                    .resize(width,height)
                    .centerInside()
                    .into(mImageContent);
        }

        if (komentar.getHasTanggapan() == true) {
            LinearLayout tanggapanLayout = (LinearLayout) convertView.findViewById(R.id.listTanggapanLayout);
            JSONArray tanggapan = komentar.getTanggapan();
            for (int i = 0; i < tanggapan.length(); i++){
                try {
                    JSONObject tanggapanObj = tanggapan.getJSONObject(i);
                    Komentar tanggapanItem = new Komentar();
                    tanggapanItem.setNama(tanggapanObj.getString("nama"));
                    tanggapanItem.setNo(Integer.parseInt(tanggapanObj.getString("no")));
                    tanggapanItem.setWaktu(tanggapanObj.getString("waktu"));
                    tanggapanItem.setUrl_pp(tanggapanObj.getString("url_pp"));
                    tanggapanItem.setIsi(tanggapanObj.getString("pesan"));

                    View tanggapanView = inflater.inflate(R.layout.list_tanggapan_berita,null);
                    ImageView profilePictureTanggapan = (ImageView) tanggapanView.findViewById(R.id.profilePictureTanggapanBerita);
                    TextView namaTanggapan = (TextView) tanggapanView.findViewById(R.id.textTanggapanBeritaNama);
                    TextView waktuTanggapan = (TextView) tanggapanView.findViewById(R.id.textTanggapanBeritaWaktu);
                    TextView isiTanggapan = (TextView) tanggapanView.findViewById(R.id.textTanggapanBerrita);

                    Drawable d = convertView.getResources().getDrawable(R.drawable.def_image);
                    Picasso.with(tanggapanView.getContext())
                            .load(tanggapanItem.getUrl_pp())
                            .resize(40,40)
                            .centerCrop()
                            .placeholder(d)
                            .into(profilePictureTanggapan);

                    namaTanggapan.setText(tanggapanItem.getNama());
                    waktuTanggapan.setText(tanggapanItem.getWaktu());
                    isiTanggapan.setText(tanggapanItem.getIsi());

                    tanggapanLayout.addView(tanggapanView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return convertView;
    }

}
