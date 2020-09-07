package com.example.divisionsimulation.ui.tools;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class EditAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<EditItem> editList = null;
    private ArrayList<String> talentList = null;
    private boolean talented = false, core = false;
    private String option_type, option;

    private MaxOptionsFMDBAdapter maxDBAdapter;
    private Cursor cursor;

    public EditAdapter(Context context, ArrayList<EditItem> editList, ArrayList<String> talentList, boolean talented, String option_type, String option, boolean core) {
        this.context = context;
        this.editList = editList;
        this.talentList = talentList;
        this.talented = talented;
        this.option_type = option_type;
        this.option = option;
        this.core = core;
        maxDBAdapter = new MaxOptionsFMDBAdapter(context);
    }

    public EditAdapter(Context context, ArrayList<EditItem> editList, ArrayList<String> talentList, boolean talented, String option_type, String option) {
        this.context = context;
        this.editList = editList;
        this.talentList = talentList;
        this.talented = talented;
        this.option_type = option_type;
        this.option = option;
        maxDBAdapter = new MaxOptionsFMDBAdapter(context);
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
        TextView txtEnd = convertView.findViewById(R.id.txtEnd);
        LinearLayout layoutNoTalent = convertView.findViewById(R.id.layoutNoTalent);
        ProgressBar progressOption = convertView.findViewById(R.id.progressOption);
        LinearLayout layoutMain = convertView.findViewById(R.id.layoutMain);

        if (talented) {
            imgType.setVisibility(View.GONE);
            layoutNoTalent.setVisibility(View.GONE);
            progressOption.setVisibility(View.GONE);
        }

        if (editList != null) {
            txtName.setText(editList.get(position).getName());
            txtMax.setText(formatD(editList.get(position).getMax()));
            maxDBAdapter.open();
            switch (option_type) {
                case "weapon_core1":
                    cursor = maxDBAdapter.fetchTypeData("무기");
                    break;
                case "weapon_core2":
                    cursor = maxDBAdapter.fetchTypeData(option);
                    break;
                case "weapon_sub":
                    cursor = maxDBAdapter.fetchSubData(editList.get(position).getName());
                    break;
                case "sheld_core":
                    cursor = maxDBAdapter.fetchSheldCoreData(editList.get(position).getName());
                    break;
                case "sheld_sub1":
                case "sheld_sub2":
                    cursor = maxDBAdapter.fetchSheldSubData(editList.get(position).getName());
                    break;
            }
            double max = Double.parseDouble(cursor.getString(2));
            String end = cursor.getString(5);
            if (!end.equals("-")) txtEnd.setText(end);
            else txtEnd.setText("");
            maxDBAdapter.close();
            switch (editList.get(position).getType()) {
                case "공격":
                    if (core) imgType.setImageResource(R.drawable.attack);
                    else imgType.setImageResource(R.drawable.attack_sub);
                    progressOption.setProgressDrawable(context.getResources().getDrawable(R.drawable.attack_progress));
                    break;
                case "방어":
                    if (core) imgType.setImageResource(R.drawable.sheld);
                    else imgType.setImageResource(R.drawable.sheld_sub);
                    progressOption.setProgressDrawable(context.getResources().getDrawable(R.drawable.sheld_progress));
                    break;
                case "다용도":
                    if (core) imgType.setImageResource(R.drawable.power);
                    else imgType.setImageResource(R.drawable.power_sub);
                    progressOption.setProgressDrawable(context.getResources().getDrawable(R.drawable.power_progress));
                    break;
                default:
                    if (core) imgType.setImageResource(R.drawable.weaponicon);
                    else imgType.setImageResource(R.drawable.weaponsub);
                    progressOption.setProgressDrawable(context.getResources().getDrawable(R.drawable.progressbar_progressbar_gage));
            }
            progressOption.setMax((int)(max*10));
            progressOption.setProgress((int)(editList.get(position).getMax()*10));
            if (editList.get(position).getMax() >= max) layoutMain.setBackgroundResource(R.drawable.maxbackground);
            else layoutMain.setBackgroundResource(R.drawable.notmaxbackground);
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
