package com.example.divisionsimulation.ui.tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.divisionsimulation.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class EditAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<EditItem> editList = null;
    private ArrayList<String> talentList = null;
    private boolean talented = false;

    public EditAdapter(Context context, ArrayList<EditItem> editList, ArrayList<String> talentList, boolean talented) {
        this.context = context;
        this.editList = editList;
        this.talentList = talentList;
        this.talented = talented;
    }

    @Override
    public int getCount() {
        if (editList != null) return editList.size();
        if (talentList != null) return talentList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (editList != null) return editList.get(position);
        if (talentList != null) return talentList.get(position);
        return null;
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

        if (talented) {
            imgType.setVisibility(View.GONE);
            layoutNoTalent.setVisibility(View.GONE);
        }

        if (editList != null) {
            txtName.setText(editList.get(position).getName());
            txtMax.setText(formatD(editList.get(position).getMax()));
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
        }

        if (talentList != null) {
            txtName.setText(talentList.get(position));
        }

        return convertView;
    }

    private String formatD(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }
}
