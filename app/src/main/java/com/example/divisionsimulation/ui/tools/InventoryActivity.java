package com.example.divisionsimulation.ui.tools;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.MaterialDbAdapter;
import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.ExoticFMDBAdapter;
import com.example.divisionsimulation.dbdatas.InventoryDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeExoticDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeNamedDBAdapter;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.dbdatas.NamedFMDBAdapter;
import com.example.divisionsimulation.dbdatas.SheldFMDBAdapter;
import com.example.divisionsimulation.dbdatas.TalentFMDBAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class InventoryActivity extends AppCompatActivity {

    private ListView listItem;
    private ArrayList<Item> itemList;
    private InventoryDBAdapter inventoryDBAdapter;
    private ItemAdapter itemAdapter;
    private String title;
    private Cursor cursor;

    private String[] weapon_type = {"돌격소총", "소총", "지정사수소총", "산탄총", "기관단총", "경기관총"};
    private boolean weaponed, exotic;

    private ExoticFMDBAdapter exoticDBAdapter;
    private NamedFMDBAdapter namedDBAdapter;
    private SheldFMDBAdapter sheldDBAdapter;
    private MaxOptionsFMDBAdapter maxDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private MaterialDbAdapter materialDbAdapter;
    private LibraryDBAdapter libraryDBAdapter;
    private MakeExoticDBAdapter makeExoticDBAdapter;
    private MakeNamedDBAdapter makeNamedDBAdapter;

    private int[] material = new int[10];
    private String[] material_name = {"총몸부품", "보호용 옷감", "강철", "세라믹", "폴리카보네이트", "탄소섬유", "전자부품", "티타늄", "다크존 자원", "특급 부품"};

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventorylayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listItem = findViewById(R.id.listItem);
        inventoryDBAdapter = new InventoryDBAdapter(this);
        exoticDBAdapter = new ExoticFMDBAdapter(this);
        namedDBAdapter = new NamedFMDBAdapter(this);
        sheldDBAdapter = new SheldFMDBAdapter(this);
        maxDBAdapter = new MaxOptionsFMDBAdapter(this);
        talentDBAdapter = new TalentFMDBAdapter(this);
        materialDbAdapter = new MaterialDbAdapter(this);
        libraryDBAdapter = new LibraryDBAdapter(this);
        makeExoticDBAdapter = new MakeExoticDBAdapter(this);
        makeNamedDBAdapter = new MakeNamedDBAdapter(this);

        resetMaterial();

        title = getIntent().getStringExtra("type");
        itemList = new ArrayList<Item>();
        addArray();
        setTitle(title+" ("+itemList.size()+")");
        itemAdapter = new ItemAdapter(this, itemList);
        listItem.setAdapter(itemAdapter);

        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                View dialogView = getLayoutInflater().inflate(R.layout.itemdialog, null);

                final TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
                final TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
                final ImageView imgType = dialogView.findViewById(R.id.imgType);
                final TextView txtWMain1 = dialogView.findViewById(R.id.txtWMain1); //첫번째 무기 핵심속성
                final TextView txtWMain2 = dialogView.findViewById(R.id.txtWMain2); //두번째 무기 핵심속성
                final TextView txtWSub = dialogView.findViewById(R.id.txtWSub); //무기 속성
                final ProgressBar progressWMain1 = dialogView.findViewById(R.id.progressWMain1); //첫번째 무기 핵심속성 진행도
                final ProgressBar progressWMain2 = dialogView.findViewById(R.id.progressWMain2); //두번재 무기 핵심속성 진행도
                final ProgressBar progressWSub = dialogView.findViewById(R.id.progressWSub); //무기 속성 진행도
                final TextView txtWTalent = dialogView.findViewById(R.id.txtWTalent); //무기 탤런트
                final TextView txtSMain = dialogView.findViewById(R.id.txtSMain); //보호장구 핵심속성
                final TextView txtSSub1 = dialogView.findViewById(R.id.txtSSub1); //첫번째 보호장구 속성
                final TextView txtSSub2 = dialogView.findViewById(R.id.txtSSub2); //두번째 보호장구 속성
                final ProgressBar progressSMain = dialogView.findViewById(R.id.progressSMain); //보호장구 핵심속성 진행도
                final ProgressBar progressSSub1 = dialogView.findViewById(R.id.progressSSub1); //첫번째 보호장구 속성 진행도
                final ProgressBar progressSSub2 = dialogView.findViewById(R.id.progressSSub2); //두번째 보호장구 속성 진행도
                final ImageView imgSMain = dialogView.findViewById(R.id.imgSMain); //보호장구 핵심속성 타입 이미지
                final ImageView imgSSub1 = dialogView.findViewById(R.id.imgSSub1); //첫번재 보호장구 속성 타입 이미지
                final ImageView imgSSub2 = dialogView.findViewById(R.id.imgSSub2); //두번째 보호장구 속성 타입 이미지
                final LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
                final LinearLayout layoutTalentButton = dialogView.findViewById(R.id.layoutTalentButton);
                final LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
                final LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
                final LinearLayout layoutSSub2 = dialogView.findViewById(R.id.layoutSSub2);
                final TextView txtWTalentContent = dialogView.findViewById(R.id.txtWTalentContent);
                final LinearLayout layoutWeaponMain1 = dialogView.findViewById(R.id.layoutWeaponMain1);
                final LinearLayout layoutWeaponMain2 = dialogView.findViewById(R.id.layoutWeaponMain2);
                final LinearLayout layoutWeaponSub = dialogView.findViewById(R.id.layoutWeaponSub);
                final LinearLayout layoutSheldMain = dialogView.findViewById(R.id.layoutSheldMain);
                final LinearLayout layoutSheldSub1 = dialogView.findViewById(R.id.layoutSheldSub1);
                final LinearLayout layoutSheldSub2 = dialogView.findViewById(R.id.layoutSheldSub2);
                final LinearLayout tableMain = dialogView.findViewById(R.id.tableMain);
                final Button btnDrop = dialogView.findViewById(R.id.btnDrop);
                final Button btnDestroy = dialogView.findViewById(R.id.btnDestroy);
                final Button btnExit = dialogView.findViewById(R.id.btnExit);
                final ImageView imgWeaponEdit1 = dialogView.findViewById(R.id.imgWeaponEdit1);
                final ImageView imgWeaponEdit2 = dialogView.findViewById(R.id.imgWeaponEdit2);
                final ImageView imgWeaponEdit3 = dialogView.findViewById(R.id.imgWeaponEdit3);
                final ImageView imgSheldEdit1 = dialogView.findViewById(R.id.imgSheldEdit1);
                final ImageView imgSheldEdit2 = dialogView.findViewById(R.id.imgSheldEdit2);
                final ImageView imgSheldEdit3 = dialogView.findViewById(R.id.imgSheldEdit3);
                final ImageView imgTalentEdit = dialogView.findViewById(R.id.imgTalentEdit);

                final LinearLayout layoutSheldSub3 = dialogView.findViewById(R.id.layoutSheldSub3);
                final ImageView imgSSub3 = dialogView.findViewById(R.id.imgSSub3);
                final TextView txtSSub3 = dialogView.findViewById(R.id.txtSSub3);
                final ProgressBar progressSSub3 = dialogView.findViewById(R.id.progressSSub3);

                final int index = position;
                btnDestroy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder destroy_builder = new AlertDialog.Builder(InventoryActivity.this, R.style.MyAlertDialogStyle);
                        destroy_builder.setTitle("분해");
                        destroy_builder.setMessage(itemList.get(index).getName()+"을 분해하시겠습니까?");
                        destroy_builder.setPositiveButton("분해", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetMaterial();
                                String str = String.valueOf(txtType.getText());
                                String normal_str = "", rare_str = "", epic_str = "";
                                int normal = 0, rare = 0, epic = 0;
                                int random_select;
                                if (exotic) {
                                    material[9]++;
                                    if (material[9] >= 20) material[9] = 20;
                                    materialDbAdapter.open();
                                    materialDbAdapter.updateMaterial(material_name[9], material[9]);
                                    materialDbAdapter.close();
                                    Toast.makeText(getApplicationContext(), "특급 부품을 획득하였습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    switch (str) {
                                        case "돌격소총": case "소총": case "지정사수소총": case "기관단총": case "경기관총": case "산탄총": case "권총":
                                            normal = percent(10, 12);
                                            if (material[0] < 2000) material[0] += normal;
                                            if (material[0] >= 2000) material[0] = 2000;
                                            normal_str = material_name[0];
                                            break;
                                        case "마스크": case "조끼": case "백팩": case "장갑": case "권총집": case "무릎보호대":
                                            normal = percent(10, 12);
                                            if (material[1] < 2000) material[1] += normal;
                                            if (material[1] >= 2000) material[1] = 2000;
                                            normal_str = material_name[1];
                                            break;
                                    }
                                    random_select = percent(2, 3);
                                    rare = percent(7, 6);
                                    material[random_select] += rare;
                                    if (material[random_select] >= 1500) material[random_select] = 1500;
                                    rare_str = material_name[random_select];
                                    random_select = percent(5, 3);
                                    epic = percent(3, 5);
                                    material[random_select] += epic;
                                    if (material[random_select] >= 1500) material[random_select] = 1500;
                                    epic_str = material_name[random_select];
                                    materialDbAdapter.open();
                                    for (int i = 0; i < material.length; i++) {
                                        materialDbAdapter.updateMaterial(material_name[i], material[i]);
                                    }
                                    materialDbAdapter.close();
                                    Toast.makeText(getApplicationContext(), normal_str+" +"+normal+", "+rare_str+" +"+rare+", "+epic_str+" +"+epic, Toast.LENGTH_SHORT).show();
                                }
                                inventoryDBAdapter.open();
                                inventoryDBAdapter.deleteData(itemList.get(index).getRowId());
                                inventoryDBAdapter.close();
                                addArray();
                                setTitle(title+" ("+itemList.size()+")");
                                itemAdapter.notifyDataSetChanged();
                                alertDialog.dismiss();
                            }
                        });
                        destroy_builder.setNegativeButton("취소", null);

                        AlertDialog destroy_alertDialog = destroy_builder.create();
                        destroy_alertDialog.setCancelable(false);
                        destroy_alertDialog.show();
                    }
                });

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnDrop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder drop_builder = new AlertDialog.Builder(InventoryActivity.this, R.style.MyAlertDialogStyle);
                        drop_builder.setTitle("버리기");
                        drop_builder.setMessage(itemList.get(index).getName()+"을 버리시겠습니까?");
                        drop_builder.setPositiveButton("버리기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                inventoryDBAdapter.open();
                                inventoryDBAdapter.deleteData(itemList.get(index).getRowId());
                                inventoryDBAdapter.close();
                                addArray();
                                setTitle(title+" ("+itemList.size()+")");
                                itemAdapter.notifyDataSetChanged();
                                alertDialog.dismiss();
                            }
                        });
                        drop_builder.setNegativeButton("취소", null);

                        AlertDialog drop_alertDialog = drop_builder.create();
                        drop_alertDialog.setCancelable(false);
                        drop_alertDialog.show();
                    }
                });

                txtName.setText(itemList.get(position).getName());
                changeColorName(position, txtName);
                txtType.setText(itemList.get(position).getType());

                exoticDBAdapter.open();
                exotic = exoticDBAdapter.haveItem(itemList.get(position).getName());
                exoticDBAdapter.close();

                switch (itemList.get(position).getType()) {
                    case "돌격소총":
                        imgType.setImageResource(R.drawable.wp1custom);
                        weaponed = true;
                        break;
                    case "소총":
                        imgType.setImageResource(R.drawable.wp2custom);
                        weaponed = true;
                        break;
                    case "지정사수소총":
                        imgType.setImageResource(R.drawable.wp3custom);
                        weaponed = true;
                        break;
                    case "기관단총":
                        imgType.setImageResource(R.drawable.wp4custom);
                        weaponed = true;
                        break;
                    case "경기관총":
                        imgType.setImageResource(R.drawable.wp5custom);
                        weaponed = true;
                        break;
                    case "산탄총":
                        imgType.setImageResource(R.drawable.wp6custom);
                        weaponed = true;
                        break;
                    case "권총":
                        imgType.setImageResource(R.drawable.wp7custom);
                        weaponed = true;
                        break;
                    case "마스크":
                        imgType.setImageResource(R.drawable.sd1custom);
                        weaponed = false;
                        break;
                    case "조끼":
                        imgType.setImageResource(R.drawable.sd2custom);
                        weaponed = false;
                        break;
                    case "권총집":
                        imgType.setImageResource(R.drawable.sd3custom);
                        weaponed = false;
                        break;
                    case "백팩":
                        imgType.setImageResource(R.drawable.sd4custom);
                        weaponed = false;
                        break;
                    case "장갑":
                        imgType.setImageResource(R.drawable.sd5custom);
                        weaponed = false;
                        break;
                    case "무릎보호대":
                        imgType.setImageResource(R.drawable.sd6custom);
                        weaponed = false;
                        break;
                }

                if (weaponed) {
                    layoutWeapon.setVisibility(View.VISIBLE);
                    layoutSheld.setVisibility(View.GONE);
                } else {
                    layoutWeapon.setVisibility(View.GONE);
                    layoutSheld.setVisibility(View.VISIBLE);
                }

                changeTable(position, tableMain);

                String end, talent_content;
                double max, second_max;

                if (weaponed) {
                    if (itemList.get(position).isEdit1()) imgWeaponEdit1.setVisibility(View.VISIBLE);
                    if (itemList.get(position).isEdit2()) imgWeaponEdit2.setVisibility(View.VISIBLE);
                    if (itemList.get(position).isEdit3()) imgWeaponEdit3.setVisibility(View.VISIBLE);
                    if (itemList.get(position).isTalentedit()) imgTalentEdit.setVisibility(View.VISIBLE);
                    maxDBAdapter.open();
                    cursor = maxDBAdapter.fetchTypeData("무기");
                    max = cursor.getDouble(2);
                            end = cursor.getString(5);
                            maxDBAdapter.close();
                            if (end.equals("-")) end = "";
                    txtWMain1.setText("+"+formatD(itemList.get(position).getCore1_value())+end+" "+itemList.get(position).getCore1());
                    progressWMain1.setMax((int)(max*10));
                    progressWMain1.setProgress((int)(itemList.get(position).getCore1_value()*10));
                    libraryDBAdapter.open();
                    cursor = libraryDBAdapter.fetchTypeData("무기");
                    second_max = cursor.getDouble(2);
                    libraryDBAdapter.close();
                    progressWMain1.setSecondaryProgress((int)(second_max*10));
                    if (itemList.get(position).getCore1_value() >= max) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground);
                    if (itemList.get(position).getName().equals("보조 붐스틱")) {
                        namedDBAdapter.open();
                        cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                        txtWMain1.setText(cursor.getString(2));
                        progressWMain1.setMax(100);
                        progressWMain1.setProgress(100);
                        layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        txtWMain1.setTextColor(Color.parseColor("#c99700"));
                        namedDBAdapter.close();
                        layoutWeaponMain1.setEnabled(false);
                    }
                    if (title.equals("권총")) layoutWeaponMain2.setVisibility(View.GONE);
                    else {
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        if (namedDBAdapter.haveNoTalentData(itemList.get(position).getName())) {
                            cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                            txtWMain2.setText(cursor.getString(2));
                            progressWMain2.setMax(100);
                            progressWMain2.setProgress(100);
                            layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            layoutWeaponMain2.setEnabled(false);
                        } else {
                            maxDBAdapter.open();
                            cursor = maxDBAdapter.fetchTypeData(itemList.get(position).getType());
                           max = cursor.getDouble(2);
                            end = cursor.getString(5);
                            maxDBAdapter.close();
                            if (end.equals("-")) end = "";
                            txtWMain2.setText("+"+formatD(itemList.get(position).getCore2_value())+end+" "+itemList.get(position).getCore2());
                            progressWMain2.setMax((int)(max*10));
                            progressWMain2.setProgress((int)(itemList.get(position).getCore2_value()*10));
                            if (itemList.get(position).getCore2_value() >= max) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground);
                            libraryDBAdapter.open();
                            cursor = libraryDBAdapter.fetchTypeData(itemList.get(position).getType());
                            second_max = cursor.getDouble(2);
                            libraryDBAdapter.close();
                            progressWMain2.setSecondaryProgress((int)(second_max*10));
                        }
                        namedDBAdapter.close();
                    }
                    maxDBAdapter.open();
                    cursor = maxDBAdapter.fetchSubData(itemList.get(position).getSub1());
                    max = cursor.getDouble(2);
                            end = cursor.getString(5);
                            maxDBAdapter.close();
                            if (end.equals("-")) end = "";
                    txtWSub.setText("+"+formatD(itemList.get(position).getSub1_value())+end+" "+itemList.get(position).getSub1());
                    progressWSub.setMax((int)(max*10));
                    progressWSub.setProgress((int)(itemList.get(position).getSub1_value()*10));
                    if (itemList.get(position).getSub1_value() >= max) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground);
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground);
                    libraryDBAdapter.open();
                    cursor = libraryDBAdapter.fetchSubData(itemList.get(position).getSub1());
                    second_max = cursor.getDouble(2);
                    libraryDBAdapter.close();
                    progressWSub.setSecondaryProgress((int)(second_max*10));
                    txtWTalent.setText(itemList.get(position).getTalent());
                    namedDBAdapter.open();
                    if (namedDBAdapter.haveTalentData(itemList.get(position).getTalent()) && namedDBAdapter.haveItem(itemList.get(position).getName())) {
                        String content = namedDBAdapter.fetchTalentData(itemList.get(position).getTalent());
                        txtWTalentContent.setText(transformString(content));
                        layoutTalent.setEnabled(false);
                    } else {
                        talentDBAdapter.open();
                        cursor = talentDBAdapter.fetchData(itemList.get(position).getTalent());
                        talent_content = cursor.getString(11);
                        talentDBAdapter.close();
                        txtWTalentContent.setText(transformString(talent_content));
                    }
                    namedDBAdapter.close();
                } else {
                    if (itemList.get(position).isEdit1()) imgSheldEdit1.setVisibility(View.VISIBLE);
                    if (itemList.get(position).isEdit2()) imgSheldEdit2.setVisibility(View.VISIBLE);
                    if (itemList.get(position).isEdit3()) imgSheldEdit3.setVisibility(View.VISIBLE);
                    if (itemList.get(position).isTalentedit()) imgTalentEdit.setVisibility(View.VISIBLE);
                    maxDBAdapter.open();
                    cursor = maxDBAdapter.fetchSheldCoreData(itemList.get(position).getCore1());
                    max = cursor.getDouble(2);
                            end = cursor.getString(5);
                            maxDBAdapter.close();
                            if (end.equals("-")) end = "";
                    txtSMain.setText("+"+formatD(itemList.get(position).getCore1_value())+end+" "+itemList.get(position).getCore1());
                    progressSMain.setMax((int)(max*10));
                    progressSMain.setProgress((int)(itemList.get(position).getCore1_value()*10));
                    if (itemList.get(position).getCore1_value() >= max && !itemList.get(position).getCore1().equals("스킬 등급")) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                    else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                    libraryDBAdapter.open();
                    cursor = libraryDBAdapter.fetchSheldCoreData(itemList.get(position).getCore1());
                    second_max = cursor.getDouble(2);
                    libraryDBAdapter.close();
                    progressSMain.setSecondaryProgress((int)(second_max*10));
                    setImageAttribute(imgSMain, progressSMain, itemList.get(position).getCore1(), true);
                    namedDBAdapter.open();
                    if (namedDBAdapter.haveNoTalentData(itemList.get(position).getName())) {
                        cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                        String result = cursor.getString(2);
                        if (result.indexOf("\n") > -1) {
                            String[] split_str = result.split("\n");
                            txtSSub1.setText(split_str[0]);
                        } else {
                            txtSSub1.setText(result);
                        }
                        progressSSub1.setMax(100);
                        progressSSub1.setProgress(100);
                        layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        String asp = cursor.getString(9);
                        switch (asp) {
                            case "공격":
                                imgSSub1.setImageResource(R.drawable.attack_sub);
                                progressSSub1.setProgressDrawable(getResources().getDrawable(R.drawable.attack_progress));
                                break;
                            case "방어":
                                imgSSub1.setImageResource(R.drawable.sheld_sub);
                                progressSSub1.setProgressDrawable(getResources().getDrawable(R.drawable.sheld_progress));
                                break;
                            case "다용도":
                                imgSSub1.setImageResource(R.drawable.power_sub);
                                progressSSub1.setProgressDrawable(getResources().getDrawable(R.drawable.power_progress));
                                break;
                        }
                        layoutSheldSub1.setEnabled(false);
                    } else {
                        maxDBAdapter.open();
                        cursor = maxDBAdapter.fetchSheldSubData(itemList.get(position).getSub1());
                        max = cursor.getDouble(2);
                            end = cursor.getString(5);
                            maxDBAdapter.close();
                            if (end.equals("-")) end = "";
                        txtSSub1.setText("+"+formatD(itemList.get(position).getSub1_value())+end+" "+itemList.get(position).getSub1());
                        progressSSub1.setMax((int)(max*10));
                        progressSSub1.setProgress((int)(itemList.get(position).getSub1_value()*10));
                        if (itemList.get(position).getSub1_value() >= max) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        libraryDBAdapter.open();
                        cursor = libraryDBAdapter.fetchSheldSubData(itemList.get(position).getSub1());
                        second_max = cursor.getDouble(2);
                        libraryDBAdapter.close();
                        progressSSub1.setSecondaryProgress((int)(second_max*10));
                        setImageSubAttribute(imgSSub1, progressSSub1, itemList.get(position).getSub1(), false);
                    }
                    namedDBAdapter.close();
                    sheldDBAdapter.open();
                    if (sheldDBAdapter.haveItem(itemList.get(position).getName())) layoutSheldSub2.setVisibility(View.GONE);
                    else {
                        layoutSheldSub2.setVisibility(View.VISIBLE);
                        maxDBAdapter.open();
                        cursor = maxDBAdapter.fetchSheldSubData(itemList.get(position).getSub2());
                        max = cursor.getDouble(2);
                            end = cursor.getString(5);
                            maxDBAdapter.close();
                            if (end.equals("-")) end = "";
                        txtSSub2.setText("+"+formatD(itemList.get(position).getSub2_value())+end+" "+itemList.get(position).getSub2());
                        progressSSub2.setMax((int)(max*10));
                        progressSSub2.setProgress((int)(itemList.get(position).getSub2_value()*10));
                        if (itemList.get(position).getSub2_value() >= max) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        libraryDBAdapter.open();
                        cursor = libraryDBAdapter.fetchSheldSubData(itemList.get(position).getSub2());
                        second_max = cursor.getDouble(2);
                        libraryDBAdapter.close();
                        progressSSub2.setSecondaryProgress((int)(second_max*10));
                        setImageSubAttribute(imgSSub2, progressSSub2, itemList.get(position).getSub2(), false);
                    }
                    sheldDBAdapter.close();
                    switch (title) {
                        case "조끼":
                        case "백팩":
                            txtWTalent.setText(itemList.get(position).getTalent());
                            namedDBAdapter.open();
                            if (namedDBAdapter.haveTalentData(itemList.get(position).getTalent())) {
                                String content = namedDBAdapter.fetchTalentData(itemList.get(position).getTalent());
                                txtWTalentContent.setText(transformString(content));
                            } else {
                                talentDBAdapter.open();
                                cursor = talentDBAdapter.fetchData(itemList.get(position).getTalent());
                                talent_content = cursor.getString(11);
                                talentDBAdapter.close();
                                txtWTalentContent.setText(transformString(talent_content));
                            }
                            namedDBAdapter.close();
                            break;
                        default:
                            layoutTalent.setVisibility(View.GONE);
                    }
                    namedDBAdapter.open();
                    if (namedDBAdapter.haveNoTalentData(itemList.get(position).getName())) {
                        cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                        String result = cursor.getString(2);
                        if (result.indexOf("\n") > -1) {
                            layoutSheldSub3.setVisibility(View.VISIBLE);
                            String[] split_str = result.split("\n");
                            txtSSub3.setText(split_str[1]);
                            progressSSub3.setMax(100);
                            progressSSub3.setProgress(100);
                            layoutSheldSub3.setBackgroundResource(R.drawable.maxbackground);
                            String asp = cursor.getString(9);
                            switch (asp) {
                                case "공격":
                                    imgSSub3.setImageResource(R.drawable.attack_sub);
                                    progressSSub3.setProgressDrawable(getResources().getDrawable(R.drawable.attack_progress));
                                    break;
                                case "방어":
                                    imgSSub3.setImageResource(R.drawable.sheld_sub);
                                    progressSSub3.setProgressDrawable(getResources().getDrawable(R.drawable.sheld_progress));
                                    break;
                                case "다용도":
                                    imgSSub3.setImageResource(R.drawable.power_sub);
                                    progressSSub3.setProgressDrawable(getResources().getDrawable(R.drawable.power_progress));
                                    break;
                            }
                        } else {
                            layoutSheldSub3.setVisibility(View.GONE);
                        }
                    }
                    namedDBAdapter.close();
                }

                exoticDBAdapter.open();
                if (exoticDBAdapter.haveItem(itemList.get(position).getName())) {
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtWTalent.setText(itemList.get(position).getTalent());
                    cursor = exoticDBAdapter.fetchData(itemList.get(position).getName());
                    talent_content = cursor.getString(12);
                    txtWTalentContent.setText(transformString(talent_content));
                    layoutTalent.setEnabled(false);
                }
                exoticDBAdapter.close();

                layoutWeaponMain1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inventoryDBAdapter.open();
                        if (itemList.get(index).getName().equals("보조 붐스틱")) {
                            Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isEdit1()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                            exoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                intent.putExtra("exoticed", true);
                            } else {
                                intent.putExtra("name", itemList.get(index).getCore1());
                                intent.putExtra("value", itemList.get(index).getCore1_value());
                                intent.putExtra("type", itemList.get(index).getType());
                                intent.putExtra("option_type", "weapon_core1");
                                namedDBAdapter.open();
                                intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                namedDBAdapter.close();
                            }
                            intent.putExtra("itemID", itemList.get(index).getRowId());
                            intent.putExtra("talented", false);
                            exoticDBAdapter.close();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 보정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        inventoryDBAdapter.close();
                    }
                });
                layoutWeaponMain2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inventoryDBAdapter.open();
                        namedDBAdapter.open();
                        if (namedDBAdapter.haveItem(itemList.get(position).getName())) {
                            if (!itemList.get(index).getName().equals("보조 붐스틱") && namedDBAdapter.haveNoTalentData(itemList.get(index).getName())) {
                                Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        namedDBAdapter.close();
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isEdit2()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                            exoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                intent.putExtra("exoticed", true);
                            } else {
                                intent.putExtra("name", itemList.get(index).getCore2());
                                intent.putExtra("value", itemList.get(index).getCore2_value());
                                intent.putExtra("type", itemList.get(index).getType());
                                intent.putExtra("option_type", "weapon_core2");
                                namedDBAdapter.open();
                                intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                namedDBAdapter.close();
                            }
                            intent.putExtra("itemID", itemList.get(index).getRowId());
                            intent.putExtra("talented", false);
                            exoticDBAdapter.close();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 보정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        inventoryDBAdapter.close();
                    }
                });
                layoutWeaponSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inventoryDBAdapter.open();
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isEdit3()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                            exoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                intent.putExtra("exoticed", true);
                            } else {
                                intent.putExtra("name", itemList.get(index).getSub1());
                                intent.putExtra("value", itemList.get(index).getSub1_value());
                                intent.putExtra("type", itemList.get(index).getType());
                                intent.putExtra("option_type", "weapon_sub");
                                namedDBAdapter.open();
                                intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                namedDBAdapter.close();
                            }
                            intent.putExtra("itemID", itemList.get(index).getRowId());
                            intent.putExtra("talented", false);
                            exoticDBAdapter.close();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 보정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        inventoryDBAdapter.close();
                    }
                });
                layoutSheldMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inventoryDBAdapter.open();
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isEdit1()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                            exoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                intent.putExtra("exoticed", true);
                            } else {
                                intent.putExtra("name", itemList.get(index).getCore1());
                                intent.putExtra("value", itemList.get(index).getCore1_value());
                                intent.putExtra("type", itemList.get(index).getType());
                                intent.putExtra("option_type", "sheld_core");
                                namedDBAdapter.open();
                                intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                namedDBAdapter.close();
                            }
                            intent.putExtra("itemID", itemList.get(index).getRowId());
                            intent.putExtra("talented", false);
                            exoticDBAdapter.close();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 보정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        inventoryDBAdapter.close();
                    }
                });
                layoutSheldSub1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inventoryDBAdapter.open();
                        namedDBAdapter.open();
                        if (namedDBAdapter.haveItem(itemList.get(index).getName())) {
                            if (namedDBAdapter.haveNoTalentData(itemList.get(index).getName())) {
                                Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        namedDBAdapter.close();
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isEdit2()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                            exoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                intent.putExtra("exoticed", true);
                            } else {
                                intent.putExtra("name", itemList.get(index).getSub1());
                                intent.putExtra("value", itemList.get(index).getSub1_value());
                                intent.putExtra("type", itemList.get(index).getType());
                                intent.putExtra("option_type", "sheld_sub1");
                                intent.putExtra("sheld_sub", true);
                                intent.putExtra("other_name", itemList.get(index).getSub2());
                                namedDBAdapter.open();
                                intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                namedDBAdapter.close();
                            }
                            intent.putExtra("itemID", itemList.get(index).getRowId());
                            intent.putExtra("talented", false);
                            exoticDBAdapter.close();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 보정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        inventoryDBAdapter.close();
                    }
                });
                layoutSheldSub2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inventoryDBAdapter.open();
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isEdit3()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                            exoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                intent.putExtra("exoticed", true);
                            } else {
                                intent.putExtra("name", itemList.get(index).getSub2());
                                intent.putExtra("value", itemList.get(index).getSub2_value());
                                intent.putExtra("type", itemList.get(index).getType());
                                intent.putExtra("option_type", "sheld_sub2");
                                intent.putExtra("sheld_sub", true);
                                intent.putExtra("other_name", itemList.get(index).getSub1());
                                namedDBAdapter.open();
                                intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                namedDBAdapter.close();
                            }
                            intent.putExtra("itemID", itemList.get(index).getRowId());
                            intent.putExtra("talented", false);
                            exoticDBAdapter.close();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 보정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        inventoryDBAdapter.close();
                    }
                });
                layoutTalentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inventoryDBAdapter.open();
                        namedDBAdapter.open();
                        if (namedDBAdapter.haveItem(itemList.get(index).getName())) {
                            if (!namedDBAdapter.haveNoTalentData(itemList.get(index).getName())) {
                                Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        sheldDBAdapter.open();
                        if (sheldDBAdapter.haveItem(itemList.get(index).getName())) {
                            Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sheldDBAdapter.close();
                        namedDBAdapter.close();
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isTalentedit()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                            exoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                intent.putExtra("name", itemList.get(index).getTalent());
                                intent.putExtra("talented", true);
                                intent.putExtra("type", itemList.get(index).getType());
                                namedDBAdapter.open();
                                intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                namedDBAdapter.close();
                            }
                            intent.putExtra("itemID", itemList.get(index).getRowId());
                            exoticDBAdapter.close();

                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 보정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        inventoryDBAdapter.close();
                    }
                });

                setNamedTalent(position, txtWTalent);
                setNamed(position, txtWMain2, txtSSub1);

                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });
    }

    private void addArray() {
        String name, type, core1, core2, sub1, sub2, talent;
        long rowId;
        double core1_value, core2_value, sub1_value, sub2_value;
        boolean edit1, edit2, edit3, talentedit;
        itemList.clear();
        inventoryDBAdapter.open();
        if (title.equals("무기")) {
            for (int i = 0; i < weapon_type.length; i++) {
                cursor = inventoryDBAdapter.fetchData(weapon_type[i]);
                while (!cursor.isAfterLast()) {
                    rowId = cursor.getLong(0);
                    name = cursor.getString(1);
                    type = cursor.getString(2);
                    core1 = cursor.getString(3);
                    core2 = cursor.getString(4);
                    sub1 = cursor.getString(5);
                    sub2 = cursor.getString(6);
                    core1_value = cursor.getDouble(7);
                    core2_value = cursor.getDouble(8);
                    sub1_value = cursor.getDouble(9);
                    sub2_value = cursor.getDouble(10);
                    talent = cursor.getString(11);
                    edit1 = Boolean.parseBoolean(cursor.getString(12));
                    edit2 = Boolean.parseBoolean(cursor.getString(13));
                    edit3 = Boolean.parseBoolean(cursor.getString(14));
                    talentedit = Boolean.parseBoolean(cursor.getString(15));
                    cursor.moveToNext();
                    Item item = new Item(rowId, name, type);
                    item.setCore1(core1);
                    item.setCore2(core2);
                    item.setSub1(sub1);
                    item.setSub2(sub2);
                    item.setCore1_value(core1_value);
                    item.setCore2_value(core2_value);
                    item.setSub1_value(sub1_value);
                    item.setSub2_value(sub2_value);
                    item.setTalent(talent);
                    item.setEdit1(edit1);
                    item.setEdit2(edit2);
                    item.setEdit3(edit3);
                    item.setTalentedit(talentedit);
                    itemList.add(item);
                }
            }
        } else {
            cursor = inventoryDBAdapter.fetchData(title);
            while (!cursor.isAfterLast()) {
                rowId = cursor.getLong(0);
                name = cursor.getString(1);
                type = cursor.getString(2);
                core1 = cursor.getString(3);
                core2 = cursor.getString(4);
                sub1 = cursor.getString(5);
                sub2 = cursor.getString(6);
                core1_value = cursor.getDouble(7);
                core2_value = cursor.getDouble(8);
                sub1_value = cursor.getDouble(9);
                sub2_value = cursor.getDouble(10);
                talent = cursor.getString(11);
                edit1 = Boolean.parseBoolean(cursor.getString(12));
                edit2 = Boolean.parseBoolean(cursor.getString(13));
                edit3 = Boolean.parseBoolean(cursor.getString(14));
                talentedit = Boolean.parseBoolean(cursor.getString(15));
                cursor.moveToNext();
                Item item = new Item(rowId, name, type);
                item.setCore1(core1);
                item.setCore2(core2);
                item.setSub1(sub1);
                item.setSub2(sub2);
                item.setCore1_value(core1_value);
                item.setCore2_value(core2_value);
                item.setSub1_value(sub1_value);
                item.setSub2_value(sub2_value);
                item.setTalent(talent);
                item.setEdit1(edit1);
                item.setEdit2(edit2);
                item.setEdit3(edit3);
                item.setTalentedit(talentedit);
                itemList.add(item);
            }
        }
        inventoryDBAdapter.close();
        Collections.sort(itemList);
    }

    private void changeTable(int position, LinearLayout layout) {
        exoticDBAdapter.open();
        namedDBAdapter.open();
        sheldDBAdapter.open();
        makeNamedDBAdapter.open();
        makeExoticDBAdapter.open();
        if (exoticDBAdapter.haveItem(itemList.get(position).getName()) || makeExoticDBAdapter.haveItem(itemList.get(position).getName())) layout.setBackgroundResource(R.drawable.exoticitem);
        else if (sheldDBAdapter.haveItem(itemList.get(position).getName())) layout.setBackgroundResource(R.drawable.gearitem);
        else layout.setBackgroundResource(R.drawable.rareitem);
        makeExoticDBAdapter.close();
        makeNamedDBAdapter.close();
        sheldDBAdapter.close();
        namedDBAdapter.close();
        exoticDBAdapter.close();
    }

    private void changeColorName(int position, TextView textView) {
        exoticDBAdapter.open();
        namedDBAdapter.open();
        sheldDBAdapter.open();
        makeNamedDBAdapter.open();
        makeExoticDBAdapter.open();
        if (exoticDBAdapter.haveItem(itemList.get(position).getName()) || makeExoticDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#ff3c00"));
        else if (namedDBAdapter.haveItem(itemList.get(position).getName()) || makeNamedDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#c99700"));
        else if (sheldDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#009900"));
        else textView.setTextColor(Color.parseColor("#f0f0f0"));
        makeExoticDBAdapter.close();
        makeNamedDBAdapter.close();
        sheldDBAdapter.close();
        namedDBAdapter.close();
        exoticDBAdapter.close();
    }

    private void toast(String message, boolean longer) {
        int length;
        if (longer) length = Toast.LENGTH_LONG;
        else length = Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    private void setNamed(int position, TextView weapon_TextView, TextView sheld_TextView) {
        String[] split_str;
        namedDBAdapter.open();
        if (namedDBAdapter.haveNoTalentData(itemList.get(position).getName())) {
            String str = namedDBAdapter.fetchNoTalentData(itemList.get(position).getName());
            if (str.indexOf("\n") > -1) {
                split_str = str.split("\n");
                if (weaponed) {
                    weapon_TextView.setText(split_str[0]);
                    weapon_TextView.setTextColor(Color.parseColor("#c99700"));
                } else {
                    sheld_TextView.setText(split_str[0]);
                    sheld_TextView.setTextColor(Color.parseColor("#c99700"));
                }
            } else {
                if (weaponed) {
                    weapon_TextView.setText(str);
                    weapon_TextView.setTextColor(Color.parseColor("#c99700"));
                } else {
                    sheld_TextView.setText(str);
                    sheld_TextView.setTextColor(Color.parseColor("#c99700"));
                }
            }
        } else {
            weapon_TextView.setTextColor(Color.parseColor("#aaaaaa"));
            sheld_TextView.setTextColor(Color.parseColor("#aaaaaa"));
        }
        namedDBAdapter.close();
    }

    private void setNamedTalent(int position, TextView textView) {
        namedDBAdapter.open();
        if (namedDBAdapter.haveTalentData(itemList.get(position).getTalent()) && namedDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#c99700"));
        else textView.setTextColor(Color.parseColor("#aaaaaa"));
        namedDBAdapter.close();
    }

    private void setImageSubAttribute(ImageView imgView, ProgressBar progress, String content, boolean core) {
        String str;
        maxDBAdapter.open();
        if (core) cursor = maxDBAdapter.fetchSheldCoreData(content);
        else cursor = maxDBAdapter.fetchSheldSubData(content);
        str = cursor.getString(4);
        maxDBAdapter.close();
        switch (str) {
            case "공격":
                imgView.setImageResource(R.drawable.attack_sub);
                progress.setProgressDrawable(getResources().getDrawable(R.drawable.attack_progress));
                break;
            case "방어":
                imgView.setImageResource(R.drawable.sheld_sub);
                progress.setProgressDrawable(getResources().getDrawable(R.drawable.sheld_progress));
                break;
            case "다용도":
                imgView.setImageResource(R.drawable.power_sub);
                progress.setProgressDrawable(getResources().getDrawable(R.drawable.power_progress));
                break;
        }
    }

    private void setImageAttribute(ImageView imgView, ProgressBar progress, String content, boolean core) {
        String str;
        progress.setVisibility(View.VISIBLE);
        maxDBAdapter.open();
        if (core) cursor = maxDBAdapter.fetchSheldCoreData(content);
        else cursor = maxDBAdapter.fetchSheldSubData(content);
        str = cursor.getString(4);
        maxDBAdapter.close();
        switch (str) {
            case "공격":
                imgView.setImageResource(R.drawable.attack);
                progress.setProgressDrawable(getResources().getDrawable(R.drawable.attack_progress));
                break;
            case "방어":
                imgView.setImageResource(R.drawable.sheld);
                progress.setProgressDrawable(getResources().getDrawable(R.drawable.sheld_progress));
                break;
            case "다용도":
                imgView.setImageResource(R.drawable.power);
                //progress.setProgressDrawable(getResources().getDrawable(R.drawable.power_progress));
                progress.setVisibility(View.GONE);
                break;
        }
    }

    private int percent(int min, int length) {
        return (int)(Math.random()*1234567)%length + min;
    }

    private void resetMaterial() {
        materialDbAdapter.open();
        cursor = materialDbAdapter.fetchAllMaterial();
        cursor.moveToFirst();
        int index = 0;
        while (!cursor.isAfterLast()) {
            material[index] = cursor.getInt(2);
            cursor.moveToNext();
            index++;
        }
        materialDbAdapter.close();
    }

    private String formatD(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }

    private SpannableString transformString(String content) {
        SpannableString spannableString = new SpannableString(content);
        String word;
        int start, end;
        int find_index = 0;
        String[] changes = {"+", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "%", "m", "초", "번", "개", "명", "배", "배율", "발", "."};
        for (int i = 0; i < changes.length; i++) { //뉴욕의 지배자 확장팩 출시 후 등장한 엑조틱 장비들을 특급 색으로 변경해준다.
            find_index = 0;
            while(true) {
                word = changes[i]; //찾을 문자열에 새로운 특급 장비 이름을 넣는다. 반복문으로 모든 엑조틱과 비교가 된다.
                start = content.indexOf(word, find_index); //찾을 문자열과 같은 문자열을 찾게되면 시작 번호를 알려줘 start 변수에 대입한다.
                find_index = start+1;
                end = start + word.length(); //시작번호로부터 찾을 문자열의 길이를 추가해 끝번호를 찾는다.
                if (start > 0) {
                    if ((isFrontNumber(content, start) && changes[i].equals("초")) ||
                            (!changes[i].equals("초") && !changes[i].equals('번') && !changes[i].equals("개") && !changes[i].equals("명") && !changes[i].equals("배") && !changes[i].equals("발") && !changes[i].equals(".")) ||
                            (isFrontNumber(content, start) && changes[i].equals("번")) ||
                            (isFrontNumber(content, start) && changes[i].equals("개")) ||
                            (isFrontNumber(content, start) && changes[i].equals("배")) ||
                            (isFrontNumber(content, start) && changes[i].equals("발")) ||
                            (isFrontNumber(content, start) && changes[i].equals(".")) ||
                            (isFrontNumber(content, start) && changes[i].equals("명"))) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#B18912")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    break;
                }
            }
        }
        return spannableString;
    }

    private boolean isFrontNumber(String content, int index) {
        String result = "";
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        if (index > 0) result = content.substring(index-1, index);
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i].equals(result)) return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        addArray();
        setTitle(title+" ("+itemList.size()+")");
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
