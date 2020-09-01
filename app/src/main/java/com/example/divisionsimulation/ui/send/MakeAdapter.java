package com.example.divisionsimulation.ui.send;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.MakeNamedDBAdapter;

import java.util.ArrayList;

public class MakeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MakeItem> makeItems;
    private boolean exoticed = false;

    private int[] weaponresource = {R.drawable.rdowp1custom, R.drawable.rdowp2custom, R.drawable.rdowp3custom, R.drawable.rdowp4custom, R.drawable.rdowp5custom, R.drawable.rdowp6custom, R.drawable.rdowp7custom};
    private String[] weapontypes = {"돌격소총", "소총", "기관단총", "경기관총", "지정사수소총", "산탄총", "권총"};
    private int[] sheldresource = {R.drawable.rdoeq1custom, R.drawable.rdoeq2custom, R.drawable.rdoeq3custom, R.drawable.rdoeq4custom, R.drawable.rdoeq5custom, R.drawable.rdoeq6custom};
    private String[] sheldtypes = {"마스크", "조끼", "권총집", "백팩", "장갑", "무릎보호대"};

    private MakeNamedDBAdapter makeNamedDBAdapter;

    public MakeAdapter(Context context, ArrayList<MakeItem> makeItems, boolean exoticed) {
        this.context = context;
        this.makeItems = makeItems;
        this.exoticed = exoticed;
        makeNamedDBAdapter = new MakeNamedDBAdapter(context);
    }

    @Override
    public int getCount() {
        return makeItems.size();
    }

    @Override
    public Object getItem(int position) {
        return makeItems.get(position);
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
        else if (makeItems.get(position).getGear()) viewLine.setBackgroundColor(Color.parseColor("#2BBE2B"));
        else viewLine.setBackgroundColor(Color.parseColor("#fdae13"));

        if (isWeapon(makeItems.get(position).getType())) {
            imgWeaponType.setImageResource(setWeaponImageResource(makeItems.get(position).getType()));
        } else {
            imgWeaponType.setImageResource(setSheldImageResource(makeItems.get(position).getType()));
        }

        txtName.setText(makeItems.get(position).getName());

        makeNamedDBAdapter.open();
        if (makeNamedDBAdapter.haveItem(makeItems.get(position).getName())) {
            txtName.setTextColor(Color.parseColor("#B18912"));
        } else if (exoticed) {
            txtName.setTextColor(Color.parseColor("#fe6e0e"));
        } else if (makeItems.get(position).getGear()) {
            txtName.setTextColor(Color.parseColor("#2BBE2B"));
        } else {
            txtName.setTextColor(Color.parseColor("#AAAAAA"));
        }
        makeNamedDBAdapter.close();

        return convertView;
    }

    private boolean isWeapon(String type) {
        for (int i = 0; i < weapontypes.length; i++) if (type.equals(weapontypes[i])) return true;
        return false;
    }

    private boolean isSheld(String type) {
        for (int i = 0; i < sheldtypes.length; i++) if (type.equals(sheldtypes[i])) return true;
        return false;
    }

    private int setWeaponImageResource(String type) {
        for (int i = 0; i < weapontypes.length; i++) if (type.equals(weapontypes[i])) return weaponresource[i];
        return weaponresource[0];
    }

    private int setSheldImageResource(String type) {
        for (int i = 0; i < sheldtypes.length; i++) if (type.equals(sheldtypes[i])) return sheldresource[i];
        return sheldresource[0];
    }
}
