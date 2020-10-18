package com.example.divisionsimulation.ui.share;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.divisionsimulation.MaterialDbAdapter;
import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.ExoticFMDBAdapter;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.dbdatas.NamedFMDBAdapter;
import com.example.divisionsimulation.dbdatas.SheldFMDBAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DarkzoneAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Item> dark_items = null;
    private MaxOptionsFMDBAdapter maxOptionsFMDBAdapter;
    private SheldFMDBAdapter sheldDbAdapter;
    private NamedFMDBAdapter namedFMDBAdapter;
    private ExoticFMDBAdapter exoticFMDBAdapter;
    private MaterialDbAdapter materialDbAdapter;

    private String end;
    private Double max;
    private TextView textView = null, txtDarkzone = null;
    private Button btnOutput = null;

    private Cursor cursor;
    private String[] sheld_type = {"마스크", "백팩", "조끼", "장갑", "권총집", "무릎보호대"};
    private String[] weapon_type = {"돌격소총", "소총", "기관단총", "경기관총", "산탄총", "지정사수소총", "권총"};

    public DarkzoneAdapter(Context context, ArrayList<Item> dark_items, TextView textView, Button btnOutput, TextView txtDarkzone) {
        this.context = context;
        this.dark_items = dark_items;
        maxOptionsFMDBAdapter = new MaxOptionsFMDBAdapter(context);
        sheldDbAdapter = new SheldFMDBAdapter(context);
        namedFMDBAdapter = new NamedFMDBAdapter(context);
        exoticFMDBAdapter = new ExoticFMDBAdapter(context);
        materialDbAdapter = new MaterialDbAdapter(context);
        this.textView = textView;
        this.btnOutput = btnOutput;
        this.txtDarkzone = txtDarkzone;
    }

    @Override
    public int getCount() {
        return dark_items.size();
    }

    @Override
    public Object getItem(int position) {
        return dark_items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.darkzonebackpacklayout, null);

        ImageView imgType = convertView.findViewById(R.id.imgType);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtType = convertView.findViewById(R.id.txtType);
        ImageView imgDestroy = convertView.findViewById(R.id.imgDestroy);
        LinearLayout layoutCore1 = convertView.findViewById(R.id.layoutCore1);
        TextView txtCore1 = convertView.findViewById(R.id.txtCore1);
        TextView txtCoreValue1 = convertView.findViewById(R.id.txtCoreValue1);
        ProgressBar progressCore1 = convertView.findViewById(R.id.progressCore1);
        LinearLayout layoutCore2 = convertView.findViewById(R.id.layoutCore2);
        TextView txtCore2 = convertView.findViewById(R.id.txtCore2);
        TextView txtCoreValue2 = convertView.findViewById(R.id.txtCoreValue2);
        ProgressBar progressCore2 = convertView.findViewById(R.id.progressCore2);
        LinearLayout layoutSub1 = convertView.findViewById(R.id.layoutSub1);
        TextView txtSub1 = convertView.findViewById(R.id.txtSub1);
        TextView txtSubValue1 = convertView.findViewById(R.id.txtSubValue1);
        ProgressBar progressSub1 = convertView.findViewById(R.id.progressSub1);
        LinearLayout layoutSub2 = convertView.findViewById(R.id.layoutSub2);
        TextView txtSub2 = convertView.findViewById(R.id.txtSub2);
        TextView txtSubValue2 = convertView.findViewById(R.id.txtSubValue2);
        ProgressBar progressSub2 = convertView.findViewById(R.id.progressSub2);
        LinearLayout layoutTalent = convertView.findViewById(R.id.layoutTalent);
        TextView txtTalent = convertView.findViewById(R.id.txtTalent);

        for (int i = 0; i < weapon_type.length; i++) {
            if (dark_items.get(position).getType().equals(weapon_type[i])) dark_items.get(position).setWeapon(true);
        }
        for (int i = 0; i < sheld_type.length; i++) {
            if (dark_items.get(position).getType().equals(sheld_type[i])) dark_items.get(position).setSheld(true);
        }

        setImageView(imgType, dark_items.get(position).getType());
        txtName.setText(dark_items.get(position).getName());
        txtType.setText(dark_items.get(position).getType());

        imgDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDbAdapter.open();
                cursor = materialDbAdapter.fetchMaterial("다크존 자원");
                int material = cursor.getInt(2);
                material++;
                materialDbAdapter.updateMaterial("다크존 자원", material);
                materialDbAdapter.close();
                dark_items.remove(position);
                Toast.makeText(context, "다크존 자원을 획득하였습니다.", Toast.LENGTH_SHORT).show();
                if (dark_items.size() == 0) textView.setVisibility(View.VISIBLE);
                btnOutput.setText("이송하기 ("+dark_items.size()+"/10)"); //위와 동일한 방식
                txtDarkzone.setText(dark_items.size()+"/10");
                notifyDataSetChanged();
            }
        });

        txtCore1.setText(dark_items.get(position).getCore1());
        maxOptionsFMDBAdapter.open();
        if (dark_items.get(position).isWeapon()) cursor = maxOptionsFMDBAdapter.fetchSearchData("무기군 기본 데미지", "무기");
        else cursor = maxOptionsFMDBAdapter.fetchSearchData(dark_items.get(position).getCore1(), "보호장구 핵심속성");
        maxOptionsFMDBAdapter.close();
        end = cursor.getString(5);
        max = Double.parseDouble(cursor.getString(2));
        if (end.equals("-")) end = "";
        txtCoreValue1.setText(formatD(dark_items.get(position).getCore1_value())+end);
        progressCore1.setMax((int)(max*10));
        progressCore1.setProgress((int)(dark_items.get(position).getCore1_value()*10));

        layoutCore2.setVisibility(View.GONE);
        if (dark_items.get(position).isWeapon() && !dark_items.get(position).getType().equals("권총")) {
            layoutCore2.setVisibility(View.VISIBLE);
            txtCore2.setText(dark_items.get(position).getCore2());
            maxOptionsFMDBAdapter.open();
            cursor = maxOptionsFMDBAdapter.fetchSearchData(dark_items.get(position).getCore2(), dark_items.get(position).getType());
            maxOptionsFMDBAdapter.close();
            end = cursor.getString(5);
            max = Double.parseDouble(cursor.getString(2));
            if (end.equals("-")) end = "";
            txtCoreValue2.setText(formatD(dark_items.get(position).getCore2_value())+end);
            progressCore2.setMax((int)(max*10));
            progressCore2.setProgress((int)(dark_items.get(position).getCore2_value()*10));
        }

        txtSub1.setText(dark_items.get(position).getSub1());
        namedFMDBAdapter.open();
        if (!namedFMDBAdapter.haveNoTalentData(dark_items.get(position).getName())) {
            txtSub1.setTextColor(Color.parseColor("#aaaaaa"));
            maxOptionsFMDBAdapter.open();
            if (dark_items.get(position).isWeapon()) cursor = maxOptionsFMDBAdapter.fetchSearchData(dark_items.get(position).getSub1(), "무기 부속성");
            else cursor = maxOptionsFMDBAdapter.fetchSearchData(dark_items.get(position).getSub1(), "보호장구 부속성");
            maxOptionsFMDBAdapter.close();
            end = cursor.getString(5);
            max = Double.parseDouble(cursor.getString(2));
            if (end.equals("-")) end = "";
            progressSub1.setMax((int)(max*10));
            progressSub1.setProgress((int)(dark_items.get(position).getSub1_value()*10));
            txtSubValue1.setText(formatD(dark_items.get(position).getSub1_value())+end);
        } else {
            cursor = namedFMDBAdapter.fetchData(dark_items.get(position).getName());
            String content = cursor.getString(2);
            txtSub1.setText(content);
            end = "";
            progressSub1.setMax(100);
            progressSub1.setProgress(100);
            txtSubValue1.setText("");
            txtSub1.setTextColor(Color.parseColor("#c99700"));
        }
        namedFMDBAdapter.close();

        layoutSub2.setVisibility(View.VISIBLE);
        sheldDbAdapter.open();
        if (dark_items.get(position).isSheld() && !sheldDbAdapter.haveItem(dark_items.get(position).getName())) {
            txtSub2.setText(dark_items.get(position).getSub2());
            maxOptionsFMDBAdapter.open();
            cursor = maxOptionsFMDBAdapter.fetchSearchData(dark_items.get(position).getSub2(), "보호장구 부속성");
            maxOptionsFMDBAdapter.close();
            end = cursor.getString(5);
            max = Double.parseDouble(cursor.getString(2));
            if (end.equals("-")) end = "";
            txtSubValue2.setText(formatD(dark_items.get(position).getSub2_value())+end);
            progressSub2.setMax((int)(max*10));
            progressSub2.setProgress((int)(dark_items.get(position).getSub2_value()*10));
        } else {
            layoutSub2.setVisibility(View.GONE);
        }
        sheldDbAdapter.close();

        layoutTalent.setVisibility(View.VISIBLE);
        if (!dark_items.get(position).getTalent().equals("") && dark_items.get(position).getTalent() != null) {
            txtTalent.setText(dark_items.get(position).getTalent());
            namedFMDBAdapter.open();
            if (namedFMDBAdapter.haveItem(dark_items.get(position).getName()) && !namedFMDBAdapter.haveNoTalentData(dark_items.get(position).getName())) {
                txtTalent.setTextColor(Color.parseColor("#c99700"));
            } else {
                txtTalent.setTextColor(Color.parseColor("#aaaaaa"));
            }
        } else {
            layoutTalent.setVisibility(View.GONE);
        }

        exoticFMDBAdapter.open();
        namedFMDBAdapter.open();
        sheldDbAdapter.open();
        if (exoticFMDBAdapter.haveItem(dark_items.get(position).getName())) {
            txtName.setTextColor(Color.parseColor("#ff3c00"));
        } else if (namedFMDBAdapter.haveItem(dark_items.get(position).getName())) {
            txtName.setTextColor(Color.parseColor("#c99700"));
        } else if (sheldDbAdapter.haveItem(dark_items.get(position).getName())) {
            txtName.setTextColor(Color.parseColor("#009900"));
        } else {
            txtName.setTextColor(Color.parseColor("#aaaaaa"));
        }
        sheldDbAdapter.close();
        namedFMDBAdapter.close();
        exoticFMDBAdapter.close();

        return convertView;
    }

    private void setImageView(ImageView imgView, String type) {
        switch (type) {
            case "돌격소총":
                imgView.setImageResource(R.drawable.wp1custom);
                break;
            case "소총":
                imgView.setImageResource(R.drawable.wp2custom);
                break;
            case "지정사수소총":
                imgView.setImageResource(R.drawable.wp3custom);
                break;
            case "기관단총":
                imgView.setImageResource(R.drawable.wp4custom);
                break;
            case "경기관총":
                imgView.setImageResource(R.drawable.wp5custom);
                break;
            case "산탄총":
                imgView.setImageResource(R.drawable.wp6custom);
                break;
            case "권총":
                imgView.setImageResource(R.drawable.wp7custom);
                break;
            case "마스크":
                imgView.setImageResource(R.drawable.sd1custom);
                break;
            case "백팩":
                imgView.setImageResource(R.drawable.sd4custom);
                break;
            case "조끼":
                imgView.setImageResource(R.drawable.sd2custom);
                break;
            case "장갑":
                imgView.setImageResource(R.drawable.sd5custom);
                break;
            case "권총집":
                imgView.setImageResource(R.drawable.sd3custom);
                break;
            case "무릎보호대":
                imgView.setImageResource(R.drawable.sd6custom);
                break;
        }
    }

    private String formatD(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }
}
