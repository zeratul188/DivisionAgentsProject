package com.example.divisionsimulation.ui.home;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.divisionsimulation.R;

import java.util.ArrayList;

public class LoadoutAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Loadout> itemList;

    private Cursor cursor = null;
    private String[] titles = {"명사수", "기관포병", "생존전문가", "기술전문가", "화염방사병", "폭파전문가"};

    public LoadoutAdapter(Context context, ArrayList<Loadout> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.loadoutitem, null);

        TextView txtNumber = convertView.findViewById(R.id.txtNumber);
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtDate = convertView.findViewById(R.id.txtDate);

        txtNumber.setText(Integer.toString(position+1));
        txtTitle.setText(titles[random(titles.length, 0)]);
        txtDate.setText(itemList.get(position).getMake());

        return convertView;
    }

    private int random(int length, int min) {
        return (int)(Math.random()*1234567)%length+min;
    }
}
