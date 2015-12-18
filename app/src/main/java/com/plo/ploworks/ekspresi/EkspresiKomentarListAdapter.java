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

public class EkspresiKomentarListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Ekspresi> ekspresiItem;

    public EkspresiKomentarListAdapter(Activity activity, List<Ekspresi> ekspresiItem) {
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
        TextView textKomentar = (TextView) convertView.findViewById(R.id.textKomentarEkspresi);
        TextView textKomentator = (TextView) convertView.findViewById(R.id.textKomentatorEkspresi);

        textKomentar.setVisibility(View.GONE);
        textKomentator.setVisibility(View.GONE);

        ImageView mProfilePicture = (ImageView) convertView.findViewById(R.id.profilePictureEkspresi);

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

        Picasso.with(convertView.getContext())
                .load(e.getUrl_pp())
                .placeholder(R.drawable.def_image)
                .resize(50,50)
                .centerCrop()
                .into(mProfilePicture);
        return convertView;
    }

}
