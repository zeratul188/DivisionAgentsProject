package com.example.divisionsimulation.ui.gallery;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.divisionsimulation.R;

import java.util.ArrayList;

public class WeaponAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<WeaponItem> weaponItems;
    private boolean exoticed = false;

    private int[] weaponresource = {R.drawable.rdowp1custom, R.drawable.rdowp2custom, R.drawable.rdowp3custom, R.drawable.rdowp4custom, R.drawable.rdowp5custom, R.drawable.rdowp6custom, R.drawable.rdowp7custom};
    private String[] weapontypes = {"돌격소총", "소총", "기관단총", "경기관총", "지정사수소총", "산탄총", "권총"};

    public WeaponAdapter(Context context, ArrayList<WeaponItem> weaponItems, boolean exoticed) {
        this.context = context;
        this.weaponItems = weaponItems;
        this.exoticed = exoticed;
    }

    @Override
    public int getCount() {
        return weaponItems.size();
    }

    @Override
    public Object getItem(int position) {
        return weaponItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.makeitem, null);

        View viewLine = convertView.findViewById(R.id.viewLine);
        ImageView imgWeaponType = convertView.findViewById(R.id.imgWeaponType);
        TextView txtName = convertView.findViewById(R.id.txtName);

        if (exoticed) viewLine.setBackgroundColor(Color.parseColor("#fe6e0e"));
        else viewLine.setBackgroundColor(Color.parseColor("#fdae13"));

        imgWeaponType.setImageResource(setWeaponImageResource(weaponItems.get(position).getType()));

        txtName.setText(weaponItems.get(position).getName());

        if (exoticed) {
            txtName.setTextColor(Color.parseColor("#fe6e0e"));
        } else {
            txtName.setTextColor(Color.parseColor("#F0F0F0"));
        }

        return convertView;
    }
    private int setWeaponImageResource(String type) {
        for (int i = 0; i < weapontypes.length; i++) if (type.equals(weapontypes[i])) return weaponresource[i];
        return weaponresource[0];
    }
}
