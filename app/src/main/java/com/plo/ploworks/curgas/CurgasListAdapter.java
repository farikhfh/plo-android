package com.plo.ploworks.curgas;
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

public class CurgasListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Curgas> curgasItem;

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
            convertView = inflater.inflate(R.layout.list_berita, null);

        TextView textNama = (TextView)convertView.findViewById(R.id.textNama);
        TextView textUsername = (TextView)convertView.findViewById(R.id.textUsername);
        TextView textWaktu = (TextView)convertView.findViewById(R.id.textWaktu);
        TextView textJudul = (TextView)convertView.findViewById(R.id.textJudul);
        TextView textIsiSingkat = (TextView)convertView.findViewById(R.id.textIsiSingkat);

        Curgas c = curgasItem.get(position);

        textNama.setText(c.getNama());
        textUsername.setText(c.getUsername());
        textWaktu.setText(c.getWaktu());
        textJudul.setText(c.getJudul());
        textIsiSingkat.setText(c.getIsiSingkat());
        Log.d("Adapter", c.getNama());

        return convertView;
    }

}
