package com.plo.ploworks.berita;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.plo.ploworks.R;

import java.util.List;

public class BeritaListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Berita> beritaItem;

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

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_berita, null);

        TextView textNama = (TextView)convertView.findViewById(R.id.textNama);
        TextView textUsername = (TextView)convertView.findViewById(R.id.textUsername);
        TextView textWaktu = (TextView)convertView.findViewById(R.id.textWaktu);
        TextView textJudul = (TextView)convertView.findViewById(R.id.textJudul);
        TextView textIsiSingkat = (TextView)convertView.findViewById(R.id.textIsiSingkat);

        Berita b = beritaItem.get(position);

        textNama.setText(b.getNama());
        textUsername.setText(b.getUserName());
        textWaktu.setText(b.getWaktu());
        textJudul.setText(b.getJudul());
        textIsiSingkat.setText(b.getIsiSingkat());
        Log.d("Adapter",b.getNama());

        return convertView;
    }

}
