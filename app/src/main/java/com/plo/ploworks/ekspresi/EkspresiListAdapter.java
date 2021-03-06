package com.plo.ploworks.ekspresi;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.plo.ploworks.R;
import com.plo.ploworks.network.CreateImageRequest;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EkspresiListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Ekspresi> ekspresiItem;
    private String isEmpty = "none";

    public EkspresiListAdapter(Activity activity, List<Ekspresi> ekspresiItem) {
        this.activity = activity;
        this.ekspresiItem = ekspresiItem;
    }

    @Override
    public int getCount() {
        return ekspresiItem.size();
    }

    @Override
    public Object getItem(int location) {
        return ekspresiItem.get(location);
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
            convertView = inflater.inflate(R.layout.list_ekspresi, null);

        TextView textNama = (TextView) convertView.findViewById(R.id.textNamaEkspresi);
        TextView textUsername = (TextView) convertView.findViewById(R.id.textUsernameEkspresi);
        TextView textWaktu = (TextView) convertView.findViewById(R.id.textWaktuEkspresi);
        TextView textEkspresi = (TextView) convertView.findViewById(R.id.textEkspresi);
        TextView textKomentator = (TextView) convertView.findViewById(R.id.textKomentatorEkspresi);
        TextView textKomentar = (TextView) convertView.findViewById(R.id.textKomentarEkspresi);

        ImageView mProfilePicture = (ImageView) convertView.findViewById(R.id.profilePictureEkspresi);
        ImageView mImageContent = (ImageView) convertView.findViewById(R.id.imageIsiEkspresi);

        Ekspresi e = ekspresiItem.get(position);
        textNama.setText(e.getNama());
        textUsername.setText(e.getUsername());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
        Date date = new Date();
        try {
            date = dateFormat.parse(e.getWaktu());
        } catch (ParseException pe){
            pe.printStackTrace();
        }
        textWaktu.setText(date.toString());

        textEkspresi.setText(e.getEkspresi());

        textKomentar.setText(e.getKomentar());
        textKomentator.setText(e.getKomentator());

        if (e.getKomentar().equals(isEmpty)) {
            textKomentar.setVisibility(View.GONE);
            textKomentator.setVisibility(View.GONE);
        }

        Picasso.with(convertView.getContext())
                .load(e.getUrl_pp())
                .placeholder(R.drawable.def_image)
                .resize(50,50)
                .centerCrop()
                .into(mProfilePicture);

        //Content image
        if(e.getGambar() != "none"){
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            Picasso.with(convertView.getContext())
                    .load(e.getGambar())
                    .resize(width,height)
                    .centerInside()
                    .into(mImageContent);
        }
        return convertView;
    }

}
