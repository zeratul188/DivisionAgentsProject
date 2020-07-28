package com.example.divisionsimulation.ui.tools;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.ExoticFMDBAdapter;
import com.example.divisionsimulation.dbdatas.NamedFMDBAdapter;
import com.example.divisionsimulation.dbdatas.SheldFMDBAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Item> itemList;
    private ExoticFMDBAdapter exoticDBAdapter;
    private NamedFMDBAdapter namedDBAdapter;
    private SheldFMDBAdapter sheldDBAdapter;

    public ItemAdapter(Context context, ArrayList<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        exoticDBAdapter = new ExoticFMDBAdapter(context);
        namedDBAdapter = new NamedFMDBAdapter(context);
        sheldDBAdapter = new SheldFMDBAdapter(context);
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
        if (convertView == null) convertView = View.inflate(context, R.layout.item, null);

        ImageView imgType = convertView.findViewById(R.id.imgType);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtType = convertView.findViewById(R.id.txtType);

        txtName.setText(itemList.get(position).getName());
        txtType.setText(itemList.get(position).getType());

        LinearLayout tableMain = convertView.findViewById(R.id.tableMain);

        changeColorName(position, txtName);
        changeTable(position, tableMain);

        switch (itemList.get(position).getType()) {
            case "돌격소총":
                imgType.setImageResource(R.drawable.wp1custom);
                break;
            case "소총":
                imgType.setImageResource(R.drawable.wp2custom);
                break;
            case "지정사수소총":
                imgType.setImageResource(R.drawable.wp3custom);
                break;
            case "기관단총":
                imgType.setImageResource(R.drawable.wp4custom);
                break;
            case "경기관총":
                imgType.setImageResource(R.drawable.wp5custom);
                break;
            case "산탄총":
                imgType.setImageResource(R.drawable.wp6custom);
                break;
            case "권총":
                imgType.setImageResource(R.drawable.wp7custom);
                break;
            case "마스크":
                imgType.setImageResource(R.drawable.sd1custom);
                break;
            case "조끼":
                imgType.setImageResource(R.drawable.sd2custom);
                break;
            case "권총집":
                imgType.setImageResource(R.drawable.sd3custom);
                break;
            case "백팩":
                imgType.setImageResource(R.drawable.sd4custom);
                break;
            case "장갑":
                imgType.setImageResource(R.drawable.sd5custom);
                break;
            case "무릎보호대":
                imgType.setImageResource(R.drawable.sd6custom);
                break;
        }

        return convertView;
    }
    private void changeTable(int position, LinearLayout layout) {
        exoticDBAdapter.open();
        namedDBAdapter.open();
        sheldDBAdapter.open();
        if (exoticDBAdapter.haveItem(itemList.get(position).getName())) layout.setBackgroundResource(R.drawable.exoticitem);
        else if (sheldDBAdapter.haveItem(itemList.get(position).getName())) layout.setBackgroundResource(R.drawable.gearitem);
        else layout.setBackgroundResource(R.drawable.rareitem);
        sheldDBAdapter.close();
        namedDBAdapter.close();
        exoticDBAdapter.close();
    }

    private void changeColorName(int position, TextView textView) {
        exoticDBAdapter.open();
        namedDBAdapter.open();
        sheldDBAdapter.open();
        if (exoticDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#ff3c00"));
        else if (namedDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#c99700"));
        else if (sheldDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#009900"));
        else textView.setTextColor(Color.parseColor("#f0f0f0"));
        sheldDBAdapter.close();
        namedDBAdapter.close();
        exoticDBAdapter.close();
    }
}
