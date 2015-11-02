package com.plo.ploworks.ekspresi;
import android.app.Activity;
import android.content.Context;
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

public class EkspresiListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Ekspresi> ekspresiItem;

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

        NetworkImageView mProfilePicture = (NetworkImageView) convertView.findViewById(R.id.profilePictureEkspresi);
        NetworkImageView mImageContent = (NetworkImageView) convertView.findViewById(R.id.imageIsiEkspresi);

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
        textKomentator.setText(e.getKomentator());
        textKomentar.setText(e.getKomentar());

        //profile picture
        ImageLoader mImageLoader = CreateImageRequest.getInstance(this.activity).getImageLoader();
        mProfilePicture.setImageUrl(e.getUrl_pp(), mImageLoader);
        mProfilePicture.setMaxWidth(100);
        mProfilePicture.setMaxHeight(100);
        mProfilePicture.setDefaultImageResId(R.drawable.def_image);

        //Content image
        if(e.getGambar() != "none"){
            mImageContent.setImageUrl(e.getGambar(),mImageLoader);
        }
        return convertView;
    }

}
