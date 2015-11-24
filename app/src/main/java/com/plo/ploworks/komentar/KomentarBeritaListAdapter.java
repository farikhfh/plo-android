package com.plo.ploworks.komentar;

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
import com.plo.ploworks.komentar.Komentar;
import com.plo.ploworks.network.CreateImageRequest;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KomentarBeritaListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Komentar> komentarItem;

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

        Komentar komentar = komentarItem.get(position);

        textNama.setText(komentar.getNama());
        textUsername.setText(komentar.getUsername());
        textWaktu.setText(komentar.getWaktu());
        textKomentar.setText(komentar.getIsi());

        return convertView;
    }

}
