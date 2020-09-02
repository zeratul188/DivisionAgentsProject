package com.example.divisionsimulation.ui.tools;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.ExoticFMDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeExoticDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeNamedDBAdapter;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.dbdatas.NamedFMDBAdapter;
import com.example.divisionsimulation.dbdatas.SheldFMDBAdapter;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Item> itemList;
    private ExoticFMDBAdapter exoticDBAdapter;
    private NamedFMDBAdapter namedDBAdapter;
    private SheldFMDBAdapter sheldDBAdapter;
    private MaxOptionsFMDBAdapter maxDBAdapter;
    private MakeExoticDBAdapter makeExoticDBAdapter;
    private MakeNamedDBAdapter makeNamedDBAdapter;

    private Cursor cursor = null;
    private String[] weapon_type = {"돌격소총", "지정사수소총", "산탄총", "기관단총", "경기관총", "소총", "권총"};

    public ItemAdapter(Context context, ArrayList<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        exoticDBAdapter = new ExoticFMDBAdapter(context);
        namedDBAdapter = new NamedFMDBAdapter(context);
        sheldDBAdapter = new SheldFMDBAdapter(context);
        maxDBAdapter = new MaxOptionsFMDBAdapter(context);
        makeExoticDBAdapter = new MakeExoticDBAdapter(context);
        makeNamedDBAdapter = new MakeNamedDBAdapter(context);
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
        if (convertView == null) convertView = View.inflate(context, R.layout.item_tools, null);

        ImageView imgType = convertView.findViewById(R.id.imgType);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtType = convertView.findViewById(R.id.txtType);
        TextView txtTalent = convertView.findViewById(R.id.txtTalent);
        ImageView imgEdit = convertView.findViewById(R.id.imgEdit);

        txtName.setText(itemList.get(position).getName());
        txtType.setText(itemList.get(position).getType());
        txtTalent.setText(itemList.get(position).getTalent());

        namedDBAdapter.open();
        exoticDBAdapter.open();
        sheldDBAdapter.open();
        makeNamedDBAdapter.open();
        makeExoticDBAdapter.open();
        if (namedDBAdapter.haveItem(itemList.get(position).getName()) && !namedDBAdapter.haveNoTalentData(itemList.get(position).getName())) txtTalent.setTextColor(Color.parseColor("#c99700"));
        else if (exoticDBAdapter.haveItem(itemList.get(position).getName()) || makeExoticDBAdapter.haveItem(itemList.get(position).getName())) txtTalent.setTextColor(Color.parseColor("#ff3c00"));
        else if (sheldDBAdapter.haveItem(itemList.get(position).getName())) txtTalent.setTextColor(Color.parseColor("#009900"));
        else txtTalent.setTextColor(Color.parseColor("#f0f0f0"));
        makeExoticDBAdapter.close();
        makeNamedDBAdapter.close();
        sheldDBAdapter.close();
        exoticDBAdapter.close();
        namedDBAdapter.close();

        LinearLayout tableMain = convertView.findViewById(R.id.tableMain);

        TextView txtCoreName = convertView.findViewById(R.id.txtCoreName);
        TextView txtCore = convertView.findViewById(R.id.txtCore);
        ImageView[] imgAttribute = new ImageView[3];

        if (itemList.get(position).isEditOR()) imgEdit.setVisibility(View.VISIBLE);
        else imgEdit.setVisibility(View.GONE);

        int resource;
        for (int i = 0; i < imgAttribute.length; i++) {
            resource = convertView.getResources().getIdentifier("imgAttribute"+(i+1), "id", context.getPackageName());
            imgAttribute[i] = convertView.findViewById(resource);
        }

        maxDBAdapter.open();
        if (maxDBAdapter.isSheldCore(itemList.get(position).getCore1())) {
            cursor = maxDBAdapter.fetchSheldCoreData(itemList.get(position).getCore1());
            String asp = cursor.getString(4);
            String end = "";
            switch (asp) {
                case "공격":
                    imgAttribute[0].setImageResource(R.drawable.attack);
                    end = "%";
                    break;
                case "방어":
                    imgAttribute[0].setImageResource(R.drawable.sheld);
                    end = "";
                    break;
                case "다용도":
                    imgAttribute[0].setImageResource(R.drawable.power);
                    end = "";
                    break;
            }
            txtCoreName.setText(itemList.get(position).getCore1());
            txtCore.setText("+"+formatD(itemList.get(position).getCore1_value())+end);
        } else {
            imgAttribute[0].setImageResource(R.drawable.weaponicon);
            txtCoreName.setText(itemList.get(position).getCore1());
            txtCore.setText("+"+formatD(itemList.get(position).getCore1_value())+"%");
        }

        boolean weaponed = false;
        for (int i = 0; i < weapon_type.length; i++) {
            if (itemList.get(position).getType().equals(weapon_type[i])) {
                weaponed = true;
                break;
            }
        }
        if (weaponed) {
            for (int i = 0; i < imgAttribute.length; i++) {
                if (i != 2) imgAttribute[i].setImageResource(R.drawable.weaponicon);
                else imgAttribute[i].setImageResource(R.drawable.weaponsub);
            }
            if (!itemList.get(position).getName().equals("보조 붐스틱")) {
                cursor = maxDBAdapter.fetchTypeData("무기");
                if (itemList.get(position).getCore1_value() >= cursor.getDouble(2)) imgAttribute[0].setBackgroundResource(R.drawable.maxitembackground);
                else imgAttribute[0].setBackgroundResource(R.drawable.notmaxbackground);
            } else {
                namedDBAdapter.open();
                cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                txtCoreName.setText("산탄총 데미지");
                txtCore.setText("+16%");
                txtCoreName.setTextColor(Color.parseColor("#c99700"));
                namedDBAdapter.close();
                imgAttribute[0].setBackgroundResource(R.drawable.maxitembackground);
            }
            if (!itemList.get(position).getType().equals("권총")) {
                imgAttribute[1].setVisibility(View.VISIBLE);
                cursor = maxDBAdapter.fetchTypeData(itemList.get(position).getType());
                if (itemList.get(position).getCore2_value() >= cursor.getDouble(2)) imgAttribute[1].setBackgroundResource(R.drawable.maxitembackground);
                else imgAttribute[1].setBackgroundResource(R.drawable.notmaxbackground);
            } else {
                imgAttribute[1].setVisibility(View.GONE);
            }
            cursor = maxDBAdapter.fetchSubData(itemList.get(position).getSub1());
            if (itemList.get(position).getSub1_value() >= cursor.getDouble(2)) imgAttribute[2].setBackgroundResource(R.drawable.maxitembackground);
            else imgAttribute[2].setBackgroundResource(R.drawable.notmaxbackground);
        } else {
            try {
                String asp;
                cursor = maxDBAdapter.fetchSheldCoreData(itemList.get(position).getCore1());
                asp = cursor.getString(4);
                switch (asp) {
                    case "공격":
                        imgAttribute[0].setImageResource(R.drawable.attack);
                        break;
                    case "방어":
                        imgAttribute[0].setImageResource(R.drawable.sheld);
                        break;
                    case "다용도":
                        imgAttribute[0].setImageResource(R.drawable.power);
                        break;
                }
                if (itemList.get(position).getCore1_value() >= cursor.getDouble(2) && !itemList.get(position).getCore1().equals("스킬 등급")) imgAttribute[0].setBackgroundResource(R.drawable.maxitembackground);
                else imgAttribute[0].setBackgroundResource(R.drawable.notmaxbackground);
                namedDBAdapter.open();
                if (namedDBAdapter.haveNoTalentData(itemList.get(position).getName())) {
                    cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                    asp = cursor.getString(9);
                    switch (asp) {
                        case "공격":
                            imgAttribute[1].setImageResource(R.drawable.attack_sub);
                            break;
                        case "방어":
                            imgAttribute[1].setImageResource(R.drawable.sheld_sub);
                            break;
                        case "다용도":
                            imgAttribute[1].setImageResource(R.drawable.power_sub);
                            break;
                    }
                    imgAttribute[1].setBackgroundResource(R.drawable.maxitembackground);
                } else {
                    cursor = maxDBAdapter.fetchSheldSubData(itemList.get(position).getSub1());
                    asp = cursor.getString(4);
                    switch (asp) {
                        case "공격":
                            imgAttribute[1].setImageResource(R.drawable.attack_sub);
                            break;
                        case "방어":
                            imgAttribute[1].setImageResource(R.drawable.sheld_sub);
                            break;
                        case "다용도":
                            imgAttribute[1].setImageResource(R.drawable.power_sub);
                            break;
                    }
                    if (itemList.get(position).getSub1_value() >= cursor.getDouble(2)) imgAttribute[1].setBackgroundResource(R.drawable.maxitembackground);
                    else imgAttribute[1].setBackgroundResource(R.drawable.notmaxbackground);
                }
                namedDBAdapter.close();
                sheldDBAdapter.open();
                if (!sheldDBAdapter.haveItem(itemList.get(position).getName())) {
                    imgAttribute[2].setVisibility(View.VISIBLE);
                    cursor = maxDBAdapter.fetchSheldSubData(itemList.get(position).getSub2());
                    asp = cursor.getString(4);
                    switch (asp) {
                        case "공격":
                            imgAttribute[2].setImageResource(R.drawable.attack_sub);
                            break;
                        case "방어":
                            imgAttribute[2].setImageResource(R.drawable.sheld_sub);
                            break;
                        case "다용도":
                            imgAttribute[2].setImageResource(R.drawable.power_sub);
                            break;
                    }
                    if (itemList.get(position).getSub2_value() >= cursor.getDouble(2)) imgAttribute[2].setBackgroundResource(R.drawable.maxitembackground);
                    else imgAttribute[2].setBackgroundResource(R.drawable.notmaxbackground);
                } else {
                    imgAttribute[2].setVisibility(View.GONE);
                }
                sheldDBAdapter.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        maxDBAdapter.close();

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
        makeExoticDBAdapter.open();
        if (exoticDBAdapter.haveItem(itemList.get(position).getName()) || makeExoticDBAdapter.haveItem(itemList.get(position).getName())) layout.setBackgroundResource(R.drawable.exoticitem);
        else if (sheldDBAdapter.haveItem(itemList.get(position).getName())) layout.setBackgroundResource(R.drawable.gearitem);
        else layout.setBackgroundResource(R.drawable.rareitem);
        makeExoticDBAdapter.close();
        sheldDBAdapter.close();
        namedDBAdapter.close();
        exoticDBAdapter.close();
    }

    private String formatD(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }

    private void changeColorName(int position, TextView textView) {
        exoticDBAdapter.open();
        namedDBAdapter.open();
        sheldDBAdapter.open();
        makeExoticDBAdapter.open();
        makeNamedDBAdapter.open();
        if (exoticDBAdapter.haveItem(itemList.get(position).getName()) || makeExoticDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#ff3c00"));
        else if (namedDBAdapter.haveItem(itemList.get(position).getName()) || makeNamedDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#c99700"));
        else if (sheldDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#009900"));
        else textView.setTextColor(Color.parseColor("#f0f0f0"));
        makeNamedDBAdapter.close();
        makeExoticDBAdapter.close();
        sheldDBAdapter.close();
        namedDBAdapter.close();
        exoticDBAdapter.close();
    }
}
