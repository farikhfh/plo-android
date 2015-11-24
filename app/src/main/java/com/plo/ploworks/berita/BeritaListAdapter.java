package com.plo.ploworks.berita;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.plo.ploworks.R;
import com.plo.ploworks.network.CreateImageRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BeritaListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Berita> beritaItem;
    private NetworkImageView mProfilePicture;
    private NetworkImageView mContentPicture;
    private ImageLoader mImageLoader;

    public BeritaListAdapter(Activity activity, List<Berita> beritaItem) {
        this.activity = activity;
        this.beritaItem = beritaItem;
    }

    @Override
    public int getCount() {
        return beritaItem.size();
    }

    @Override
    public Object getItem(int location) {
        return beritaItem.get(location);
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
            convertView = inflater.inflate(R.layout.list_berita, null);
        }

        TextView textNama = (TextView)convertView.findViewById(R.id.textNamaBerita);
        TextView textUsername = (TextView)convertView.findViewById(R.id.textUsernameBerita);
        TextView textWaktu = (TextView)convertView.findViewById(R.id.textWaktuBerita);
        TextView textJudul = (TextView)convertView.findViewById(R.id.textJudulBerita);
        TextView textIsiSingkat = (TextView)convertView.findViewById(R.id.textIsiSingkatBerita);
        TextView textKomentarTerakhir = (TextView)convertView.findViewById(R.id.textTerakhirKomentarBerita);

        mProfilePicture = (NetworkImageView)convertView.findViewById(R.id.profilePictureBerita);
        mContentPicture = (NetworkImageView)convertView.findViewById(R.id.imageIsiBerita);

        Berita b = beritaItem.get(position);

        mImageLoader = CreateImageRequest.getInstance(this.activity).getImageLoader();
        //profile picture
        mProfilePicture.setDefaultImageResId(R.drawable.def_image);
        mProfilePicture.setMaxHeight(100);
        mProfilePicture.setMaxWidth(100);

        if(b.getUrlFotoUser() != "none"){
            mProfilePicture.setImageUrl(b.getUrlFotoUser(), mImageLoader);
        }

        //name
        textNama.setText(b.getNama());

        //username
        textUsername.setText("(" + b.getUserName() + ")");

        //waktu
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
        Date date = new Date();
        try {
            date = dateFormat.parse(b.getWaktu());
        } catch (ParseException e){
            e.printStackTrace();
        }
        textWaktu.setText(date.toString());

        //judul
        textJudul.setText(b.getJudul());

        //isi singkat
        textIsiSingkat.setText(Html.fromHtml(b.getIsiSingkat()));

        //komentar terakhir
        textKomentarTerakhir.setText("Terakhir dikomentari oleh " + Html.fromHtml("<b>" + b.getKomentarTerakhir() + "</b>"));

        //Content image
        if(b.getUrlGambar() != "none"){
            mContentPicture.setImageUrl(b.getUrlGambar(),mImageLoader);
        }
        return convertView;
    }

}
