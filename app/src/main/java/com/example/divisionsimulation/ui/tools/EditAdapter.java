package com.example.divisionsimulation.ui.tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.divisionsimulation.R;

import java.util.ArrayList;

public class EditAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<EditItem> editList;

    public EditAdapter(Context context, ArrayList<EditItem> editList) {
        this.context = context;
        this.editList = editList;
    }

    @Override
    public int getCount() {
        return editList.size();
    }

    @Override
    public Object getItem(int position) {
        return editList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.editoption, null);

        ImageView imgType = convertView.findViewById(R.id.imgType);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtMax = convertView.findViewById(R.id.txtMax);
        LinearLayout layoutNoTalent = convertView.findViewById(R.id.layoutNoTalent);

        if (editList.get(position).getType().equals("특수효과")) {
            layoutNoTalent.setVisibility(View.GONE);
            imgType.setVisibility(View.GONE);
        }

        switch (editList.get(position).getType()) {
            case "공격":
                imgType.setImageResource(R.drawable.attack);
                break;
            case "방어":
                imgType.setImageResource(R.drawable.sheld);
                break;
            case "다용도":
                imgType.setImageResource(R.drawable.power);
                break;
            default:
                imgType.setImageResource(R.drawable.weaponicon);
        }

        txtName.setText(editList.get(position).getName());
        txtMax.setText(Double.toString(editList.get(position).getMax()));

        return convertView;
    }
}
