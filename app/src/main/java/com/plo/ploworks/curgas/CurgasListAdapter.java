package com.plo.ploworks.curgas;
import android.app.Activity;
import android.content.Context;
import android.media.Image;
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

public class CurgasListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Curgas> curgasItem;
    private NetworkImageView mProfilePicture;
    private NetworkImageView mContentPicture;
    private ImageLoader mImageLoader;

    public CurgasListAdapter(Activity activity, List<Curgas> curgasItem) {
        this.activity = activity;
        this.curgasItem = curgasItem;
    }

    @Override
    public int getCount() {
        return curgasItem.size();
    }

    @Override
    public Object getItem(int location) {
        return curgasItem.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_curgas, null);

        TextView textNama = (TextView)convertView.findViewById(R.id.textNamaCurgas);
        TextView textUsername = (TextView)convertView.findViewById(R.id.textUsernameCurgas);
        TextView textWaktu = (TextView)convertView.findViewById(R.id.textWaktuCurgas);
        TextView textJudul = (TextView)convertView.findViewById(R.id.textJudulCurgas);
        TextView textIsiSingkat = (TextView)convertView.findViewById(R.id.textIsiSingkatCurgas);
        TextView textKomentarTerakhir = (TextView)convertView.findViewById(R.id.textTerakhirKomentarCurgas);

        mProfilePicture = (NetworkImageView)convertView.findViewById(R.id.profilePictureCurgas);
        mContentPicture = (NetworkImageView)convertView.findViewById(R.id.imageIsiCurgas);

        Curgas b = curgasItem.get(position);

        //profile picture
        mImageLoader = CreateImageRequest.getInstance(this.activity).getImageLoader();
        //profile picture
        mProfilePicture.setDefaultImageResId(R.drawable.def_image);
        mProfilePicture.setMaxHeight(100);
        mProfilePicture.setMaxWidth(100);

        if(b.getUrl_pp() != "none"){
            mProfilePicture.setImageUrl(b.getUrl_pp(), mImageLoader);
        }

        //name
        textNama.setText(b.getNama());

        //username
        textUsername.setText("(" + b.getUsername() + ")");

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
        if(b.getGambar() != "none"){
            mContentPicture.setImageUrl(b.getGambar(),mImageLoader);
        }

        return convertView;
    }

}
