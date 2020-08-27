package com.example.divisionsimulation;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LibraryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LibraryItem> libraryList = null;
    private ArrayList<String> talentList = null;
    private boolean talented = false, core = false;
    private String option_type;

    private MaxOptionsFMDBAdapter maxDBAdapter;
    private Cursor cursor;

    public LibraryAdapter(Context context, ArrayList<LibraryItem> libraryList, ArrayList<String> talentList, boolean talented, String option_type, boolean core) {
        this.context = context;
        this.libraryList = libraryList;
        this.talentList = talentList;
        this.talented = talented;
        this.option_type = option_type;
        this.core = core;
        maxDBAdapter = new MaxOptionsFMDBAdapter(context);
    }

    public LibraryAdapter(Context context, ArrayList<LibraryItem> libraryList, ArrayList<String> talentList, boolean talented, String option_type) {
        this.context = context;
        this.libraryList = libraryList;
        this.talentList = talentList;
        this.talented = talented;
        this.option_type = option_type;
        maxDBAdapter = new MaxOptionsFMDBAdapter(context);
    }

    @Override
    public int getCount() {
        if (libraryList != null) return libraryList.size();
        if (talentList != null) return talentList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (libraryList != null) return libraryList.get(position);
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
        ProgressBar progressOption = convertView.findViewById(R.id.progressOption);
        LinearLayout layoutMain = convertView.findViewById(R.id.layoutMain);

        if (talented) {
            imgType.setVisibility(View.GONE);
            layoutNoTalent.setVisibility(View.GONE);
            progressOption.setVisibility(View.GONE);
        }

        if (libraryList != null) {
            txtName.setText(libraryList.get(position).getName());
            txtMax.setText(formatD(libraryList.get(position).getMax()));
            maxDBAdapter.open();
            switch (option_type) {
                case "weapon_core1":
                    cursor = maxDBAdapter.fetchTypeData("무기");
                    break;
                case "weapon_core2":
                    cursor = maxDBAdapter.fetchTypeData(libraryList.get(position).getWeaponType());
                    break;
                case "weapon_sub":
                    cursor = maxDBAdapter.fetchSubData(libraryList.get(position).getName());
                    break;
                case "sheld_core":
                    cursor = maxDBAdapter.fetchSheldCoreData(libraryList.get(position).getName());
                    break;
                case "sheld_sub":
                    cursor = maxDBAdapter.fetchSheldSubData(libraryList.get(position).getName());
                    break;
            }
            double max = Double.parseDouble(cursor.getString(2));
            maxDBAdapter.close();
            switch (libraryList.get(position).getType()) {
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
            progressOption.setProgress((int)(libraryList.get(position).getMax()*10));
            if (libraryList.get(position).getMax() >= max) layoutMain.setBackgroundResource(R.drawable.maxbackground);
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
