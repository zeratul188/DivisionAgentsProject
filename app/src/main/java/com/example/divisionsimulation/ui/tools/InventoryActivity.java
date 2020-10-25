package com.example.divisionsimulation.ui.tools;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.SeekBar;
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
import com.example.divisionsimulation.librarydatas.ARLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.ARTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.BRLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.BRTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.BackpackLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.BackpackTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.GloveLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.HolsterLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.KneepedLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.MMRLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.MMRTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.MaskLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.PTLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.PTTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.RFLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.RFTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.SGLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.SGTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.SRLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.SRTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.VestLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.VestTalentDBAdapter;
import com.example.divisionsimulation.thread.ItemAnimationThread;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InventoryActivity extends AppCompatActivity {

    private ListView listItem;
    private ArrayList<Item> itemList;
    private InventoryDBAdapter inventoryDBAdapter;
    private ItemAdapter itemAdapter;
    private String title;
    private Cursor cursor;
    private Handler handler;

    private String[] weapon_type = {"돌격소총", "소총", "지정사수소총", "산탄총", "기관단총", "경기관총"};
    private boolean weaponed, exotic;
    private ItemAnimationThread[] animationThread = new ItemAnimationThread[5];

    private ExoticFMDBAdapter exoticDBAdapter;
    private NamedFMDBAdapter namedDBAdapter;
    private SheldFMDBAdapter sheldDBAdapter;
    private MaxOptionsFMDBAdapter maxDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private MaterialDbAdapter materialDbAdapter;
    private LibraryDBAdapter libraryDBAdapter;
    private MakeExoticDBAdapter makeExoticDBAdapter;
    private MakeNamedDBAdapter makeNamedDBAdapter;

    private ARLibraryDBAdapter arLibraryDBAdapter;
    private BRLibraryDBAdapter brLibraryDBAdapter;
    private MMRLibraryDBAdapter mmrLibraryDBAdapter;
    private PTLibraryDBAdapter ptLibraryDBAdapter;
    private RFLibraryDBAdapter rfLibraryDBAdapter;
    private SGLibraryDBAdapter sgLibraryDBAdapter;
    private SRLibraryDBAdapter srLibraryDBAdapter;

    private MaskLibraryDBAdapter maskLibraryDBAdapter;
    private VestLibraryDBAdapter vestLibraryDBAdapter;
    private HolsterLibraryDBAdapter holsterLibraryDBAdapter;
    private BackpackLibraryDBAdapter backpackLibraryDBAdapter;
    private GloveLibraryDBAdapter gloveLibraryDBAdapter;
    private KneepedLibraryDBAdapter kneepedLibraryDBAdapter;

    private ARTalentDBAdapter arTalentDBAdapter;
    private BRTalentDBAdapter brTalentDBAdapter;
    private MMRTalentDBAdapter mmrTalentDBAdapter;
    private PTTalentDBAdapter ptTalentDBAdapter;
    private RFTalentDBAdapter rfTalentDBAdapter;
    private SGTalentDBAdapter sgTalentDBAdapter;
    private SRTalentDBAdapter srTalentDBAdapter;
    private VestTalentDBAdapter vestTalentDBAdapter;
    private BackpackTalentDBAdapter backpackTalentDBAdapter;

    private int[] material = new int[10];
    private String[] material_name = {"총몸부품", "보호용 옷감", "강철", "세라믹", "폴리카보네이트", "탄소섬유", "전자부품", "티타늄", "다크존 자원", "특급 부품"};

    private AlertDialog alertDialog;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventorylayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handler = new Handler();

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

        arLibraryDBAdapter = new ARLibraryDBAdapter(this);
        brLibraryDBAdapter = new BRLibraryDBAdapter(this);
        mmrLibraryDBAdapter = new MMRLibraryDBAdapter(this);
        ptLibraryDBAdapter = new PTLibraryDBAdapter(this);
        rfLibraryDBAdapter = new RFLibraryDBAdapter(this);
        sgLibraryDBAdapter = new SGLibraryDBAdapter(this);
        srLibraryDBAdapter = new SRLibraryDBAdapter(this);

        maskLibraryDBAdapter = new MaskLibraryDBAdapter(this);
        vestLibraryDBAdapter = new VestLibraryDBAdapter(this);
        holsterLibraryDBAdapter = new HolsterLibraryDBAdapter(this);
        backpackLibraryDBAdapter = new BackpackLibraryDBAdapter(this);
        gloveLibraryDBAdapter = new GloveLibraryDBAdapter(this);
        kneepedLibraryDBAdapter = new KneepedLibraryDBAdapter(this);

        arTalentDBAdapter = new ARTalentDBAdapter(this);
        brTalentDBAdapter = new BRTalentDBAdapter(this);
        mmrTalentDBAdapter = new MMRTalentDBAdapter(this);
        ptTalentDBAdapter = new PTTalentDBAdapter(this);
        rfTalentDBAdapter = new RFTalentDBAdapter(this);
        sgTalentDBAdapter = new SGTalentDBAdapter(this);
        srTalentDBAdapter = new SRTalentDBAdapter(this);
        vestTalentDBAdapter = new VestTalentDBAdapter(this);
        backpackTalentDBAdapter = new BackpackTalentDBAdapter(this);

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
                if (itemList.get(position).getNew_item() > 0) {
                    inventoryDBAdapter.open();
                    inventoryDBAdapter.showData(itemList.get(position).getRowId());
                    inventoryDBAdapter.close();
                    itemList.get(position).setNew_item(0);
                    itemAdapter.notifyDataSetChanged();
                }

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
                final ImageView imgFavorite = dialogView.findViewById(R.id.imgFavorite);
                final SeekBar seekWMain1 = dialogView.findViewById(R.id.seekWMain1);
                final SeekBar seekWMain2 = dialogView.findViewById(R.id.seekWMain2);
                final SeekBar seekWSub = dialogView.findViewById(R.id.seekWSub);
                final SeekBar seekSMain = dialogView.findViewById(R.id.seekSMain);
                final SeekBar seekSSub1 = dialogView.findViewById(R.id.seekSSub1);
                final SeekBar seekSSub2 = dialogView.findViewById(R.id.seekSSub2);
                final LinearLayout layoutTalentContent = dialogView.findViewById(R.id.layoutTalentContent);
                seekWMain1.setEnabled(false);
                seekWMain2.setEnabled(false);
                seekWSub.setEnabled(false);
                seekSMain.setEnabled(false);
                seekSSub1.setEnabled(false);
                seekSSub2.setEnabled(false);

                final LinearLayout layoutSheldSub3 = dialogView.findViewById(R.id.layoutSheldSub3);
                final ImageView imgSSub3 = dialogView.findViewById(R.id.imgSSub3);
                final TextView txtSSub3 = dialogView.findViewById(R.id.txtSSub3);
                final ProgressBar progressSSub3 = dialogView.findViewById(R.id.progressSSub3);

                final int index = position;
                btnDestroy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                        TextView txtContent = view.findViewById(R.id.txtContent);
                        Button btnCancel = view.findViewById(R.id.btnCancel);
                        Button btnOK = view.findViewById(R.id.btnOK);

                        btnOK.setText("분해");
                        txtContent.setText(itemList.get(index).getName()+"("+itemList.get(index).getType()+")을(를) 분해하시겠습니까?");

                        btnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
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

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                        dialog_builder.setView(view);

                        dialog = dialog_builder.create();
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
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
                        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                        TextView txtContent = view.findViewById(R.id.txtContent);
                        Button btnCancel = view.findViewById(R.id.btnCancel);
                        Button btnOK = view.findViewById(R.id.btnOK);

                        btnOK.setText("버리기");
                        txtContent.setText(itemList.get(index).getName()+"("+itemList.get(index).getType()+")을(를) 버리시겠습니까?");

                        btnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                inventoryDBAdapter.open();
                                inventoryDBAdapter.deleteData(itemList.get(index).getRowId());
                                inventoryDBAdapter.close();
                                addArray();
                                setTitle(title+" ("+itemList.size()+")");
                                itemAdapter.notifyDataSetChanged();
                                alertDialog.dismiss();
                            }
                        });

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                        dialog_builder.setView(view);

                        dialog = dialog_builder.create();
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });

                txtName.setText(itemList.get(position).getName());
                changeColorName(position, txtName);
                txtType.setText(itemList.get(position).getType());

                exoticDBAdapter.open();
                makeExoticDBAdapter.open();
                exotic = exoticDBAdapter.haveItem(itemList.get(position).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName());
                makeExoticDBAdapter.close();
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

                if (itemList.get(position).getFavorite() == 1) {
                    imgFavorite.setImageResource(R.drawable.ic_star_black_40dp);
                    btnDestroy.setEnabled(false);
                    btnDrop.setEnabled(false);
                    btnDestroy.setTextColor(Color.parseColor("#FF7777"));
                    btnDrop.setTextColor(Color.parseColor("#FF7777"));
                } else {
                    imgFavorite.setImageResource(R.drawable.ic_star_border_black_40dp);
                    btnDestroy.setEnabled(true);
                    btnDrop.setEnabled(true);
                    btnDestroy.setTextColor(Color.parseColor("#FE6E0E"));
                    btnDrop.setTextColor(Color.parseColor("#AAAAAA"));
                }

                imgFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inventoryDBAdapter.open();
                        if (inventoryDBAdapter.isFavorite(itemList.get(position).getRowId())) {
                            inventoryDBAdapter.updateFavoriteData(itemList.get(position).getRowId(), false);
                            imgFavorite.setImageResource(R.drawable.ic_star_border_black_40dp);
                            itemList.get(index).setFavorite(0);
                            btnDestroy.setEnabled(true);
                            btnDrop.setEnabled(true);
                            btnDestroy.setTextColor(Color.parseColor("#FE6E0E"));
                            btnDrop.setTextColor(Color.parseColor("#AAAAAA"));
                        } else {
                            inventoryDBAdapter.updateFavoriteData(itemList.get(position).getRowId(), true);
                            imgFavorite.setImageResource(R.drawable.ic_star_black_40dp);
                            itemList.get(index).setFavorite(1);
                            btnDestroy.setEnabled(false);
                            btnDrop.setEnabled(false);
                            btnDestroy.setTextColor(Color.parseColor("#FF7777"));
                            btnDrop.setTextColor(Color.parseColor("#FF7777"));
                        }
                        inventoryDBAdapter.close();
                        itemAdapter.notifyDataSetChanged();
                    }
                });

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
                    progressWMain1.setMax((int)(max*100));
                    seekWMain1.setMax((int)(max*100));
                    animationThread[0] = new ItemAnimationThread(progressWMain1, itemList.get(position).getCore1_value(), handler);
                    animationThread[0].start();

                    setSecondaryProgess(itemList.get(position).getCore1(), seekWMain1, "weapon_core1", itemList.get(position).getType());

                    if (itemList.get(position).getCore1_value() >= max) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackgroundcustom);
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackgroundcustom);
                    if (itemList.get(position).getName().equals("보조 붐스틱")) {
                        namedDBAdapter.open();
                        cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                        txtWMain1.setText(cursor.getString(2));
                        progressWMain1.setMax(100);
                        progressWMain1.setProgress(100);
                        layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackgroundcustom);
                        txtWMain1.setTextColor(Color.parseColor("#c99700"));
                        namedDBAdapter.close();
                        layoutWeaponMain1.setEnabled(false);
                    }
                    if (title.equals("권총")) {
                        if (itemList.get(position).getName().equals("맞춤형 TDI \"Kard\"")) {
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            namedDBAdapter.open();
                            cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                            txtWMain2.setText(cursor.getString(2));
                            progressWMain2.setMax(100);
                            progressWMain2.setProgress(100);
                            layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackgroundcustom);
                            txtWMain2.setTextColor(Color.parseColor("#c99700"));
                            namedDBAdapter.close();
                            layoutWeaponMain2.setEnabled(false);
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                    }
                    else {
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        if (namedDBAdapter.haveNoTalentData(itemList.get(position).getName()) && !itemList.get(position).getName().equals("밀대")) {
                            cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                            txtWMain2.setText(cursor.getString(2));
                            progressWMain2.setMax(100);
                            progressWMain2.setProgress(100);
                            txtWMain2.setTextColor(Color.parseColor("#c99700"));
                            layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackgroundcustom);
                            layoutWeaponMain2.setEnabled(false);
                        } else {
                            txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                            maxDBAdapter.open();
                            cursor = maxDBAdapter.fetchTypeData(itemList.get(position).getType());
                           max = cursor.getDouble(2);
                            end = cursor.getString(5);
                            maxDBAdapter.close();
                            if (end.equals("-")) end = "";
                            txtWMain2.setText("+"+formatD(itemList.get(position).getCore2_value())+end+" "+itemList.get(position).getCore2());
                            progressWMain2.setMax((int)(max*100));
                    seekWMain2.setMax((int)(max*100));
                            animationThread[1] = new ItemAnimationThread(progressWMain2, itemList.get(position).getCore2_value(), handler);
                            animationThread[1].start();
                            if (itemList.get(position).getCore2_value() >= max) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackgroundcustom);
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackgroundcustom);

                            setSecondaryProgess(itemList.get(position).getCore2(), seekWMain2, "weapon_core2", itemList.get(position).getType());

                        }
                        namedDBAdapter.close();
                    }
                    if (itemList.get(position).getName().equals("밀대")) {
                        namedDBAdapter.open();
                        cursor = namedDBAdapter.fetchData(itemList.get(position).getName());
                        txtWSub.setText(cursor.getString(2));
                        progressWSub.setMax(100);
                        progressWSub.setProgress(100);
                        layoutWeaponSub.setBackgroundResource(R.drawable.maxbackgroundcustom);
                        layoutWeaponSub.setEnabled(false);
                        namedDBAdapter.close();
                        txtWSub.setTextColor(Color.parseColor("#c99700"));
                    } else {
                        txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        maxDBAdapter.open();
                        cursor = maxDBAdapter.fetchSubData(itemList.get(position).getSub1());
                        max = cursor.getDouble(2);
                        end = cursor.getString(5);
                        maxDBAdapter.close();
                        if (end.equals("-")) end = "";
                        txtWSub.setText("+"+formatD(itemList.get(position).getSub1_value())+end+" "+itemList.get(position).getSub1());
                        progressWSub.setMax((int)(max*100));
                        seekWSub.setMax((int)(max*100));
                        animationThread[2] = new ItemAnimationThread(progressWSub, itemList.get(position).getSub1_value(), handler);
                        animationThread[2].start();
                        if (itemList.get(position).getSub1_value() >= max) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackgroundcustom);
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackgroundcustom);
                    }

                    setSecondaryProgess(itemList.get(position).getSub1(), seekWSub, "weapon_sub", itemList.get(position).getType());

                    txtWTalent.setText(itemList.get(position).getTalent());
                    namedDBAdapter.open();
                    makeNamedDBAdapter.open();
                    if (namedDBAdapter.haveTalentData(itemList.get(position).getTalent()) && namedDBAdapter.haveItem(itemList.get(position).getName())) {
                        String content = namedDBAdapter.fetchTalentData(itemList.get(position).getTalent());
                        txtWTalentContent.setText(transformString(content));
                        layoutTalent.setEnabled(false);
                    } else if (makeNamedDBAdapter.haveItem(itemList.get(position).getName())) {
                        String content = makeNamedDBAdapter.fetchTalentData(itemList.get(position).getTalent());
                        txtWTalentContent.setText(transformString(content));
                        layoutTalent.setEnabled(false);
                    } else {
                        talentDBAdapter.open();
                        cursor = talentDBAdapter.fetchData(itemList.get(position).getTalent());
                        talent_content = cursor.getString(11);
                        talentDBAdapter.close();
                        txtWTalentContent.setText(transformString(talent_content));
                    }
                    makeNamedDBAdapter.close();
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
                    progressSMain.setMax((int)(max*100));
                    seekSMain.setMax((int)(max*100));
                    animationThread[0] = new ItemAnimationThread(progressSMain, itemList.get(position).getCore1_value(), handler);
                    animationThread[0].start();
                    if (itemList.get(position).getCore1_value() >= max && !itemList.get(position).getCore1().equals("스킬 등급")) layoutSheldMain.setBackgroundResource(R.drawable.maxbackgroundcustom);
                    else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackgroundcustom);

                    setSecondaryProgess(itemList.get(position).getCore1(), seekSMain, "sheld_core", itemList.get(position).getType());

                    setImageAttribute(imgSMain, progressSMain, itemList.get(position).getCore1(), true);
                    if (itemList.get(position).getCore1().equals("스킬 등급")) seekSMain.setVisibility(View.GONE);
                    else seekSMain.setVisibility(View.VISIBLE);
                    namedDBAdapter.open();
                    makeNamedDBAdapter.open();
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
                        animationThread[2] = new ItemAnimationThread(progressSSub1, 100, handler);
                        animationThread[2].start();
                        layoutSheldSub1.setBackgroundResource(R.drawable.maxbackgroundcustom);
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
                    } else if (makeNamedDBAdapter.haveNoTalentData(itemList.get(position).getName())) {
                        cursor = makeNamedDBAdapter.fetchData(itemList.get(position).getName());
                        String result = cursor.getString(2);
                        if (result.indexOf("\n") > -1) {
                            String[] split_str = result.split("\n");
                            txtSSub1.setText(split_str[0]);
                        } else {
                            txtSSub1.setText(result);
                        }
                        progressSSub1.setMax(100);
                        animationThread[2] = new ItemAnimationThread(progressSSub1, 100, handler);
                        animationThread[2].start();
                        layoutSheldSub1.setBackgroundResource(R.drawable.maxbackgroundcustom);
                        String asp = cursor.getString(4);
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
                        progressSSub1.setMax((int)(max*100));
                    seekSSub1.setMax((int)(max*100));
                        animationThread[2] = new ItemAnimationThread(progressSSub1, itemList.get(position).getSub1_value(), handler);
                        animationThread[2].start();
                        if (itemList.get(position).getSub1_value() >= max) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackgroundcustom);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackgroundcustom);

                        setSecondaryProgess(itemList.get(position).getSub1(), seekSSub1, "sheld_sub1", itemList.get(position).getType());
                        setImageSubAttribute(imgSSub1, progressSSub1, itemList.get(position).getSub1(), false);
                    }
                    makeNamedDBAdapter.close();
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
                        progressSSub2.setMax((int)(max*100));
                    seekSSub2.setMax((int)(max*100));
                        animationThread[3] = new ItemAnimationThread(progressSSub2, itemList.get(position).getSub2_value(), handler);
                        animationThread[3].start();
                        if (itemList.get(position).getSub2_value() >= max) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackgroundcustom);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackgroundcustom);

                        setSecondaryProgess(itemList.get(position).getSub2(), seekSSub2, "sheld_sub2", itemList.get(position).getType());
                        setImageSubAttribute(imgSSub2, progressSSub2, itemList.get(position).getSub2(), false);
                    }
                    sheldDBAdapter.close();
                    switch (title) {
                        case "조끼":
                        case "백팩":
                            txtWTalent.setText(itemList.get(position).getTalent());
                            namedDBAdapter.open();
                            makeNamedDBAdapter.open();
                            if (namedDBAdapter.haveTalentData(itemList.get(position).getTalent())) {
                                String content = namedDBAdapter.fetchTalentData(itemList.get(position).getTalent());
                                txtWTalentContent.setText(transformString(content));
                            } else if (makeNamedDBAdapter.haveTalentData(itemList.get(position).getTalent())) {
                                String content = makeNamedDBAdapter.fetchTalentData(itemList.get(position).getTalent());
                                txtWTalentContent.setText(transformString(content));
                            } else {
                                talentDBAdapter.open();
                                cursor = talentDBAdapter.fetchData(itemList.get(position).getTalent());
                                talent_content = cursor.getString(11);
                                talentDBAdapter.close();
                                txtWTalentContent.setText(transformString(talent_content));
                            }
                            makeNamedDBAdapter.close();
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
                            animationThread[4] = new ItemAnimationThread(progressSSub3, 100, handler);
                            animationThread[4].start();
                            layoutSheldSub3.setBackgroundResource(R.drawable.maxbackgroundcustom);
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
                            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                            TextView txtContent = view.findViewById(R.id.txtContent);
                            Button btnCancel = view.findViewById(R.id.btnCancel);
                            Button btnOK = view.findViewById(R.id.btnOK);

                            exoticDBAdapter.open();
                            makeExoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                btnOK.setText("재조정");
                                txtContent.setText(itemList.get(index).getName()+"을(를) 재조정하시겠습니까?");
                            } else {
                                btnOK.setText("보정");
                                txtContent.setText(itemList.get(index).getCore1()+"을(를) 보정하시겠습니까?");
                            }
                            makeExoticDBAdapter.close();
                            exoticDBAdapter.close();

                            btnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    dialog.dismiss();
                                    Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                                    exoticDBAdapter.open();
                                    makeExoticDBAdapter.open();
                                    if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                        intent.putExtra("exoticed", true);
                                        intent.putExtra("exoticname", itemList.get(index).getName());
                                    } else {
                                        intent.putExtra("name", itemList.get(index).getCore1());
                                        intent.putExtra("value", itemList.get(index).getCore1_value());
                                        intent.putExtra("option_type", "weapon_core1");
                                        namedDBAdapter.open();
                                        intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                        namedDBAdapter.close();
                                    }
                                    intent.putExtra("itemID", itemList.get(index).getRowId());
                                    intent.putExtra("type", itemList.get(index).getType());
                                    intent.putExtra("talented", false);
                                    makeExoticDBAdapter.close();
                                    exoticDBAdapter.close();
                                    startActivity(intent);
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                            dialog_builder.setView(view);

                            dialog = dialog_builder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
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
                        if (namedDBAdapter.haveItem(itemList.get(position).getName()) && !itemList.get(position).getName().equals("밀대")) {
                            if (!itemList.get(index).getName().equals("보조 붐스틱") && namedDBAdapter.haveNoTalentData(itemList.get(index).getName())) {
                                Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        namedDBAdapter.close();
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isEdit2()) {
                            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                            TextView txtContent = view.findViewById(R.id.txtContent);
                            Button btnCancel = view.findViewById(R.id.btnCancel);
                            Button btnOK = view.findViewById(R.id.btnOK);

                            exoticDBAdapter.open();
                            makeExoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                btnOK.setText("재조정");
                                txtContent.setText(itemList.get(index).getName()+"을(를) 재조정하시겠습니까?");
                            } else {
                                btnOK.setText("보정");
                                txtContent.setText(itemList.get(index).getCore2()+"을(를) 보정하시겠습니까?");
                            }
                            makeExoticDBAdapter.close();
                            exoticDBAdapter.close();

                            btnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    alertDialog.dismiss();
                                    Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                                    exoticDBAdapter.open();
                                    makeExoticDBAdapter.open();
                                    if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                        intent.putExtra("exoticed", true);
                                        intent.putExtra("exoticname", itemList.get(index).getName());
                                    } else {
                                        intent.putExtra("name", itemList.get(index).getCore2());
                                        intent.putExtra("value", itemList.get(index).getCore2_value());
                                        intent.putExtra("option_type", "weapon_core2");
                                        namedDBAdapter.open();
                                        intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                        namedDBAdapter.close();
                                    }
                                    intent.putExtra("itemID", itemList.get(index).getRowId());
                                    intent.putExtra("type", itemList.get(index).getType());
                                    intent.putExtra("talented", false);
                                    makeExoticDBAdapter.close();
                                    exoticDBAdapter.close();
                                    startActivity(intent);
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                            dialog_builder.setView(view);

                            dialog = dialog_builder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
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
                            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                            TextView txtContent = view.findViewById(R.id.txtContent);
                            Button btnCancel = view.findViewById(R.id.btnCancel);
                            Button btnOK = view.findViewById(R.id.btnOK);

                            exoticDBAdapter.open();
                            makeExoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                btnOK.setText("재조정");
                                txtContent.setText(itemList.get(index).getName()+"을(를) 재조정하시겠습니까?");
                            } else {
                                btnOK.setText("보정");
                                txtContent.setText(itemList.get(index).getSub1()+"을(를) 보정하시겠습니까?");
                            }
                            makeExoticDBAdapter.close();
                            exoticDBAdapter.close();

                            btnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    alertDialog.dismiss();
                                    Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                                    exoticDBAdapter.open();
                                    makeExoticDBAdapter.open();
                                    if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                        intent.putExtra("exoticed", true);
                                        intent.putExtra("exoticname", itemList.get(index).getName());
                                    } else {
                                        intent.putExtra("name", itemList.get(index).getSub1());
                                        intent.putExtra("value", itemList.get(index).getSub1_value());
                                        intent.putExtra("option_type", "weapon_sub");
                                        namedDBAdapter.open();
                                        intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                        namedDBAdapter.close();
                                    }
                                    intent.putExtra("itemID", itemList.get(index).getRowId());
                                    intent.putExtra("type", itemList.get(index).getType());
                                    intent.putExtra("talented", false);
                                    makeExoticDBAdapter.close();
                                    exoticDBAdapter.close();
                                    startActivity(intent);
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                            dialog_builder.setView(view);

                            dialog = dialog_builder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
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
                            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                            TextView txtContent = view.findViewById(R.id.txtContent);
                            Button btnCancel = view.findViewById(R.id.btnCancel);
                            Button btnOK = view.findViewById(R.id.btnOK);

                            exoticDBAdapter.open();
                            makeExoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                btnOK.setText("재조정");
                                txtContent.setText(itemList.get(index).getName()+"을(를) 재조정하시겠습니까?");
                            } else {
                                btnOK.setText("보정");
                                txtContent.setText(itemList.get(index).getCore1()+"을(를) 보정하시겠습니까?");
                            }
                            makeExoticDBAdapter.close();
                            exoticDBAdapter.close();

                            btnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    alertDialog.dismiss();
                                    Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                                    exoticDBAdapter.open();
                                    makeExoticDBAdapter.open();
                                    if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                        intent.putExtra("exoticed", true);
                                        intent.putExtra("exoticname", itemList.get(index).getName());
                                    } else {
                                        intent.putExtra("name", itemList.get(index).getCore1());
                                        intent.putExtra("value", itemList.get(index).getCore1_value());
                                        intent.putExtra("option_type", "sheld_core");
                                        namedDBAdapter.open();
                                        intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                        namedDBAdapter.close();
                                    }
                                    intent.putExtra("itemID", itemList.get(index).getRowId());
                                    intent.putExtra("type", itemList.get(index).getType());
                                    intent.putExtra("talented", false);
                                    makeExoticDBAdapter.close();
                                    exoticDBAdapter.close();
                                    startActivity(intent);
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                            dialog_builder.setView(view);

                            dialog = dialog_builder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
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
                            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                            TextView txtContent = view.findViewById(R.id.txtContent);
                            Button btnCancel = view.findViewById(R.id.btnCancel);
                            Button btnOK = view.findViewById(R.id.btnOK);

                            exoticDBAdapter.open();
                            makeExoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                btnOK.setText("재조정");
                                txtContent.setText(itemList.get(index).getName()+"을(를) 재조정하시겠습니까?");
                            } else {
                                btnOK.setText("보정");
                                txtContent.setText(itemList.get(index).getSub1()+"을(를) 보정하시겠습니까?");
                            }
                            makeExoticDBAdapter.close();
                            exoticDBAdapter.close();

                            btnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    alertDialog.dismiss();
                                    Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                                    exoticDBAdapter.open();
                                    makeExoticDBAdapter.open();
                                    if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                        intent.putExtra("exoticed", true);
                                        intent.putExtra("exoticname", itemList.get(index).getName());
                                    } else {
                                        intent.putExtra("name", itemList.get(index).getSub1());
                                        intent.putExtra("value", itemList.get(index).getSub1_value());
                                        intent.putExtra("option_type", "sheld_sub1");
                                        intent.putExtra("sheld_sub", true);
                                        intent.putExtra("other_name", itemList.get(index).getSub2());
                                        namedDBAdapter.open();
                                        intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                        namedDBAdapter.close();
                                    }
                                    intent.putExtra("itemID", itemList.get(index).getRowId());
                                    intent.putExtra("type", itemList.get(index).getType());
                                    intent.putExtra("talented", false);
                                    makeExoticDBAdapter.close();
                                    exoticDBAdapter.close();
                                    startActivity(intent);
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                            dialog_builder.setView(view);

                            dialog = dialog_builder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
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
                            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                            TextView txtContent = view.findViewById(R.id.txtContent);
                            Button btnCancel = view.findViewById(R.id.btnCancel);
                            Button btnOK = view.findViewById(R.id.btnOK);

                            exoticDBAdapter.open();
                            makeExoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                btnOK.setText("재조정");
                                txtContent.setText(itemList.get(index).getName()+"을(를) 재조정하시겠습니까?");
                            } else {
                                btnOK.setText("보정");
                                txtContent.setText(itemList.get(index).getSub2()+"을(를) 보정하시겠습니까?");
                            }
                            makeExoticDBAdapter.close();
                            exoticDBAdapter.close();

                            btnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    alertDialog.dismiss();
                                    Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                                    exoticDBAdapter.open();
                                    makeExoticDBAdapter.open();
                                    if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                        intent.putExtra("exoticed", true);
                                        intent.putExtra("exoticname", itemList.get(index).getName());
                                    } else {
                                        intent.putExtra("name", itemList.get(index).getSub2());
                                        intent.putExtra("value", itemList.get(index).getSub2_value());
                                        intent.putExtra("option_type", "sheld_sub2");
                                        intent.putExtra("sheld_sub", true);
                                        intent.putExtra("other_name", itemList.get(index).getSub1());
                                        namedDBAdapter.open();
                                        intent.putExtra("darked", namedDBAdapter.haveDarkItem(itemList.get(index).getName()));
                                        namedDBAdapter.close();
                                    }
                                    intent.putExtra("type", itemList.get(index).getType());
                                    intent.putExtra("itemID", itemList.get(index).getRowId());
                                    intent.putExtra("talented", false);
                                    makeExoticDBAdapter.close();
                                    exoticDBAdapter.close();
                                    startActivity(intent);
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                            dialog_builder.setView(view);

                            dialog = dialog_builder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
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
                        makeNamedDBAdapter.open();
                        if (makeNamedDBAdapter.haveItem(itemList.get(index).getName())) {
                            if (!makeNamedDBAdapter.haveNoTalentData(itemList.get(index).getName())) {
                                Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        makeNamedDBAdapter.close();
                        sheldDBAdapter.open();
                        if (sheldDBAdapter.haveItem(itemList.get(index).getName())) {
                            Toast.makeText(getApplicationContext(), "이 옵션은 보정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sheldDBAdapter.close();
                        namedDBAdapter.close();
                        if (!inventoryDBAdapter.isEdited(itemList.get(index).getRowId()) || itemList.get(index).isTalentedit()) {
                            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                            TextView txtContent = view.findViewById(R.id.txtContent);
                            Button btnCancel = view.findViewById(R.id.btnCancel);
                            Button btnOK = view.findViewById(R.id.btnOK);

                            exoticDBAdapter.open();
                            makeExoticDBAdapter.open();
                            if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
                                btnOK.setText("재조정");
                                btnOK.setEnabled(false);
                                btnOK.setTextColor(Color.parseColor("#FF7777"));
                                txtContent.setText("특급 아이템은 탤런트를 보정하실 수 없습니다.");
                            } else {
                                btnOK.setText("보정");
                                txtContent.setText(itemList.get(index).getTalent()+"을(를) 보정하시겠습니까?");
                            }
                            makeExoticDBAdapter.close();
                            exoticDBAdapter.close();

                            btnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    alertDialog.dismiss();
                                    Intent intent = new Intent(InventoryActivity.this, ItemEditActivity.class);
                                    exoticDBAdapter.open();
                                    makeExoticDBAdapter.open();
                                    if (exoticDBAdapter.haveItem(itemList.get(index).getName()) || makeExoticDBAdapter.haveItem(itemList.get(index).getName())) {
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
                                    makeExoticDBAdapter.close();
                                    exoticDBAdapter.close();
                                    startActivity(intent);
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(InventoryActivity.this);
                            dialog_builder.setView(view);

                            dialog = dialog_builder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 보정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        inventoryDBAdapter.close();
                    }
                });

                setNamedTalent(position, txtWTalent);
                setNamed(position, txtWMain2, txtSSub1);

                haveTelantLibrary(layoutTalentContent, String.valueOf(txtWTalent.getText()), String.valueOf(txtType.getText()));

                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        itemAdapter.notifyDataSetChanged();
                    }
                });
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
        int favorite, new_item;
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
                    favorite = cursor.getInt(16);
                    new_item = cursor.getInt(17);
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
                    item.setFavorite(favorite);
                    item.setNew_item(new_item);
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
                favorite = cursor.getInt(16);
                new_item = cursor.getInt(17);
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
                item.setFavorite(favorite);
                item.setNew_item(new_item);
                itemList.add(item);
            }
        }
        inventoryDBAdapter.close();
        Comparator<Item> sortComparator = new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                int ret;
                int type1, type2;

                exoticDBAdapter.open();
                makeExoticDBAdapter.open();
                sheldDBAdapter.open();
                if (exoticDBAdapter.haveItem(item1.getName()) || makeExoticDBAdapter.haveItem(item1.getName())) type1 = 0;
                else if (sheldDBAdapter.haveItem(item1.getName())) type1 = 1;
                else type1 = 2;
                if (exoticDBAdapter.haveItem(item2.getName()) || makeExoticDBAdapter.haveItem(item2.getName())) type2 = 0;
                else if (sheldDBAdapter.haveItem(item2.getName())) type2 = 1;
                else type2 = 2;
                sheldDBAdapter.close();
                makeExoticDBAdapter.close();
                exoticDBAdapter.close();

                if (type1 < type2) {
                    ret = -1;
                } else if (type1 == type2) {
                    if (item1.getType().compareTo(item2.getType()) == 0) {
                        ret = item1.getName().compareTo(item2.getName());
                    } else {
                        ret = item1.getType().compareTo(item2.getType());
                    }
                } else {
                    ret = 1;
                }

                return ret;
            }
        };
        Collections.sort(itemList, sortComparator);
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
        makeNamedDBAdapter.open();
        if (namedDBAdapter.haveNoTalentData(itemList.get(position).getName()) && !itemList.get(position).getName().equals("밀대")) {
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
        } else if (makeNamedDBAdapter.haveNoTalentData(itemList.get(position).getName())) {
            String str = makeNamedDBAdapter.fetchNoTalentData(itemList.get(position).getName());
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
        makeNamedDBAdapter.close();
        namedDBAdapter.close();
    }

    private void setNamedTalent(int position, TextView textView) {
        namedDBAdapter.open();
        makeNamedDBAdapter.open();
        if (namedDBAdapter.haveTalentData(itemList.get(position).getTalent()) && namedDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#c99700"));
        else if (makeNamedDBAdapter.haveItem(itemList.get(position).getName())) textView.setTextColor(Color.parseColor("#c99700"));
        else textView.setTextColor(Color.parseColor("#aaaaaa"));
        makeNamedDBAdapter.close();
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
        String[] changes = {"+", "-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "%", "m", "초", "번", "개", "명", "배", "배율", "발", "."};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        for (int i = 0; i < changes.length; i++) { //뉴욕의 지배자 확장팩 출시 후 등장한 엑조틱 장비들을 특급 색으로 변경해준다.
            find_index = 0;
            while(true) {
                word = changes[i]; //찾을 문자열에 새로운 특급 장비 이름을 넣는다. 반복문으로 모든 엑조틱과 비교가 된다.
                start = content.indexOf(word, find_index); //찾을 문자열과 같은 문자열을 찾게되면 시작 번호를 알려줘 start 변수에 대입한다.
                find_index = start+1;
                end = start + word.length(); //시작번호로부터 찾을 문자열의 길이를 추가해 끝번호를 찾는다.
                if (start > 0) {
                    if ((isFrontNumber(content, start) && changes[i].equals("초")) ||
                            (!changes[i].equals("초") && !changes[i].equals("번") && !changes[i].equals("개") && !changes[i].equals("명") && !changes[i].equals("배") && !changes[i].equals("발") && !changes[i].equals(".")) ||
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
        for (int i = 0; i < numbers.length; i++) {
            word = numbers[i]; //찾을 문자열에 새로운 특급 장비 이름을 넣는다. 반복문으로 모든 엑조틱과 비교가 된다.
            start = content.indexOf(word);
            end = start + word.length();
            if (start == 0) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#B18912")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    private boolean isFrontNumber(String content, int index) {
        String result = "";
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        if (index > 0) result = content.substring(index-1, index);
        else return false;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i].equals(result)) return true;
        }
        return false;
    }

    private void setSecondaryProgess(String name, SeekBar seekbar, String option_type, String type) {
        Cursor cursor = null;
        double max = 0;
        switch (option_type) {
            case "weapon_core1":
                switch (type) {
                    case "돌격소총":
                        arLibraryDBAdapter.open();
                        cursor = arLibraryDBAdapter.fetchTypeData("무기");
                        arLibraryDBAdapter.close();
                        break;
                    case "소총":
                        rfLibraryDBAdapter.open();
                        cursor = rfLibraryDBAdapter.fetchTypeData("무기");
                        rfLibraryDBAdapter.close();
                        break;
                    case "산탄총":
                        sgLibraryDBAdapter.open();
                        cursor = sgLibraryDBAdapter.fetchTypeData("무기");
                        sgLibraryDBAdapter.close();
                        break;
                    case "지정사수소총":
                        mmrLibraryDBAdapter.open();
                        cursor = mmrLibraryDBAdapter.fetchTypeData("무기");
                        mmrLibraryDBAdapter.close();
                        break;
                    case "기관단총":
                        srLibraryDBAdapter.open();
                        cursor = srLibraryDBAdapter.fetchTypeData("무기");
                        srLibraryDBAdapter.close();
                        break;
                    case "경기관총":
                        brLibraryDBAdapter.open();
                        cursor = brLibraryDBAdapter.fetchTypeData("무기");
                        brLibraryDBAdapter.close();
                        break;
                    case "권총":
                        ptLibraryDBAdapter.open();
                        cursor = ptLibraryDBAdapter.fetchTypeData("무기");
                        ptLibraryDBAdapter.close();
                        break;
                }
                break;
            case "weapon_core2":
                switch (type) {
                    case "돌격소총":
                        arLibraryDBAdapter.open();
                        cursor = arLibraryDBAdapter.fetchTypeData(type);
                        arLibraryDBAdapter.close();
                        break;
                    case "소총":
                        rfLibraryDBAdapter.open();
                        cursor = rfLibraryDBAdapter.fetchTypeData(type);
                        rfLibraryDBAdapter.close();
                        break;
                    case "산탄총":
                        sgLibraryDBAdapter.open();
                        cursor = sgLibraryDBAdapter.fetchTypeData(type);
                        sgLibraryDBAdapter.close();
                        break;
                    case "지정사수소총":
                        mmrLibraryDBAdapter.open();
                        cursor = mmrLibraryDBAdapter.fetchTypeData(type);
                        mmrLibraryDBAdapter.close();
                        break;
                    case "기관단총":
                        srLibraryDBAdapter.open();
                        cursor = srLibraryDBAdapter.fetchTypeData(type);
                        srLibraryDBAdapter.close();
                        break;
                    case "경기관총":
                        brLibraryDBAdapter.open();
                        cursor = brLibraryDBAdapter.fetchTypeData(type);
                        brLibraryDBAdapter.close();
                        break;
                }
                break;
            case "weapon_sub":
                switch (type) {
                    case "돌격소총":
                        arLibraryDBAdapter.open();
                        cursor = arLibraryDBAdapter.fetchSubData(name);
                        arLibraryDBAdapter.close();
                        break;
                    case "소총":
                        rfLibraryDBAdapter.open();
                        cursor = rfLibraryDBAdapter.fetchSubData(name);
                        rfLibraryDBAdapter.close();
                        break;
                    case "산탄총":
                        sgLibraryDBAdapter.open();
                        cursor = sgLibraryDBAdapter.fetchSubData(name);
                        sgLibraryDBAdapter.close();
                        break;
                    case "지정사수소총":
                        mmrLibraryDBAdapter.open();
                        cursor = mmrLibraryDBAdapter.fetchSubData(name);
                        mmrLibraryDBAdapter.close();
                        break;
                    case "기관단총":
                        srLibraryDBAdapter.open();
                        cursor = srLibraryDBAdapter.fetchSubData(name);
                        srLibraryDBAdapter.close();
                        break;
                    case "경기관총":
                        brLibraryDBAdapter.open();
                        cursor = brLibraryDBAdapter.fetchSubData(name);
                        brLibraryDBAdapter.close();
                        break;
                    case "권총":
                        ptLibraryDBAdapter.open();
                        cursor = ptLibraryDBAdapter.fetchSubData(name);
                        ptLibraryDBAdapter.close();
                        break;
                }
                break;
            case "sheld_core":
                switch (type) {
                    case "조끼":
                        vestLibraryDBAdapter.open();
                        cursor = vestLibraryDBAdapter.fetchSheldCoreData(name);
                        vestLibraryDBAdapter.close();
                        break;
                    case "마스크":
                        maskLibraryDBAdapter.open();
                        cursor = maskLibraryDBAdapter.fetchSheldCoreData(name);
                        maskLibraryDBAdapter.close();
                        break;
                    case "권총집":
                        holsterLibraryDBAdapter.open();
                        cursor = holsterLibraryDBAdapter.fetchSheldCoreData(name);
                        holsterLibraryDBAdapter.close();
                        break;
                    case "백팩":
                        backpackLibraryDBAdapter.open();
                        cursor = backpackLibraryDBAdapter.fetchSheldCoreData(name);
                        backpackLibraryDBAdapter.close();
                        break;
                    case "장갑":
                        gloveLibraryDBAdapter.open();
                        cursor = gloveLibraryDBAdapter.fetchSheldCoreData(name);
                        gloveLibraryDBAdapter.close();
                        break;
                    case "무릎보호대":
                        kneepedLibraryDBAdapter.open();
                        cursor = kneepedLibraryDBAdapter.fetchSheldCoreData(name);
                        kneepedLibraryDBAdapter.close();
                        break;
                }
                break;
            case "sheld_sub1":
            case "sheld_sub2":
                switch (type) {
                    case "조끼":
                        vestLibraryDBAdapter.open();
                        cursor = vestLibraryDBAdapter.fetchSheldSubData(name);
                        vestLibraryDBAdapter.close();
                        break;
                    case "마스크":
                        maskLibraryDBAdapter.open();
                        cursor = maskLibraryDBAdapter.fetchSheldSubData(name);
                        maskLibraryDBAdapter.close();
                        break;
                    case "권총집":
                        holsterLibraryDBAdapter.open();
                        cursor = holsterLibraryDBAdapter.fetchSheldSubData(name);
                        holsterLibraryDBAdapter.close();
                        break;
                    case "백팩":
                        backpackLibraryDBAdapter.open();
                        cursor = backpackLibraryDBAdapter.fetchSheldSubData(name);
                        backpackLibraryDBAdapter.close();
                        break;
                    case "장갑":
                        gloveLibraryDBAdapter.open();
                        cursor = gloveLibraryDBAdapter.fetchSheldSubData(name);
                        gloveLibraryDBAdapter.close();
                        break;
                    case "무릎보호대":
                        kneepedLibraryDBAdapter.open();
                        cursor = kneepedLibraryDBAdapter.fetchSheldSubData(name);
                        kneepedLibraryDBAdapter.close();
                        break;
                }
                break;
        }
        if (cursor != null) max = Double.parseDouble(cursor.getString(2));
        seekbar.setProgress((int)(max*100));
        if (seekbar.getProgress() >= seekbar.getMax()) seekbar.setThumb(getResources().getDrawable(R.drawable.ic_max_second_40dp));
        else seekbar.setThumb(getResources().getDrawable(R.drawable.ic_second_40dp));
    }

    private void haveTelantLibrary(LinearLayout layout, String talent, String type) {
        switch (type) {
            case "돌격소총":
                arTalentDBAdapter.open();
                if (arTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                arTalentDBAdapter.close();
                break;
            case "소총":
                rfTalentDBAdapter.open();
                if (rfTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                rfTalentDBAdapter.close();
                break;
            case "기관단총":
                srTalentDBAdapter.open();
                if (srTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                srTalentDBAdapter.close();
                break;
            case "경기관총":
                brTalentDBAdapter.open();
                if (brTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                brTalentDBAdapter.close();
                break;
            case "지정사수소총":
                mmrTalentDBAdapter.open();
                if (mmrTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                mmrTalentDBAdapter.close();
                break;
            case "산탄총":
                sgTalentDBAdapter.open();
                if (sgTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                sgTalentDBAdapter.close();
                break;
            case "권총":
                ptTalentDBAdapter.open();
                if (ptTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                ptTalentDBAdapter.close();
                break;
            case "조끼":
                vestTalentDBAdapter.open();
                if (vestTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                vestTalentDBAdapter.close();
                break;
            case "백팩":
                backpackTalentDBAdapter.open();
                if (backpackTalentDBAdapter.haveTalent(talent)) layout.setBackgroundResource(R.drawable.talentbackgroundcustom);
                else layout.setBackgroundResource(R.drawable.notalentbackground);
                backpackTalentDBAdapter.close();
                break;
        }
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
