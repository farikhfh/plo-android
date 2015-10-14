package com.plo.ploworks.ekspresi;
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

        TextView textWaktu = (TextView)convertView.findViewById(R.id.textWaktu);
        TextView textUsername = (TextView)convertView.findViewById(R.id.textUsername);
        TextView textNama = (TextView)convertView.findViewById(R.id.textNama);
        TextView textEkspresi = (TextView)convertView.findViewById(R.id.textEkspresi);
        TextView textJumlah = (TextView)convertView.findViewById(R.id.textJumlah);
        TextView textKomentar = (TextView)convertView.findViewById(R.id.textKomentar);

        Ekspresi e = ekspresiItem.get(position);
        textWaktu.setText(e.getWaktu());
        textUsername.setText(e.getUsername());
        textNama.setText(e.getNama());
        textEkspresi.setText(e.getEkspresi());
        textJumlah.setText(e.getJumlah());
        textKomentar.setText(e.getKomentar());

        return convertView;
    }

}
