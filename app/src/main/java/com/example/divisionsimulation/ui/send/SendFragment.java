package com.example.divisionsimulation.ui.send;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.divisionsimulation.MaterialDbAdapter;
import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.InventoryDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeExoticDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeNamedDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeSheldDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeWeaponDBAdapter;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.dbdatas.NamedFMDBAdapter;
import com.example.divisionsimulation.dbdatas.SheldFMDBAdapter;
import com.example.divisionsimulation.dbdatas.TalentFMDBAdapter;
import com.example.divisionsimulation.dbdatas.WeaponFMDBAdapter;
import com.example.divisionsimulation.ui.share.Item;
import com.example.divisionsimulation.ui.share.OptionItem;
import com.example.divisionsimulation.ui.slideshow.SheldDbAdapter;
import com.example.divisionsimulation.ui.tools.LibraryDBAdapter;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;

    private ListView listWeapon, listSheld, listExotic;
    private RadioGroup rgWeapon, rgSheld, rgType;
    private RadioButton rdoWeaponType, rdoSheldType, rdoExoticType;
    private RadioButton[] rdoWeapon = new RadioButton[7];
    private RadioButton[] rdoSheld = new RadioButton[6];
    private LinearLayout layoutWeapon, layoutSheld, layoutExotic;
    private Button btnMaterialList;
    private int[] images = new int[32];

    private ArrayList<MakeItem> makeItems;
    private MakeAdapter makeAdapter;
    private Cursor cursor;
    private String[] weapontypes = {"돌격소총", "소총", "기관단총", "경기관총", "지정사수소총", "산탄총", "권총"};
    private String[] sheldtypes = {"마스크", "조끼", "권총집", "백팩", "장갑", "무릎보호대"};
    private int[] sheldresource = {R.drawable.rdoeq1custom, R.drawable.rdoeq2custom, R.drawable.rdoeq3custom, R.drawable.rdoeq4custom, R.drawable.rdoeq5custom, R.drawable.rdoeq6custom};
    private int[] weaponresource = {R.drawable.rdowp1custom, R.drawable.rdowp2custom, R.drawable.rdowp3custom, R.drawable.rdowp4custom, R.drawable.rdowp5custom, R.drawable.rdowp6custom, R.drawable.rdowp7custom};

    private int[] material = new int[10];
    private String[] material_name = {"총몸부품", "보호용 옷감", "강철", "세라믹", "폴리카보네이트", "탄소섬유", "전자부품", "티타늄", "다크존 자원", "특급 부품"};
    private int[] now_material = new int[4];
    private int[] need_material;
    private String[] material_limit;

    private MakeExoticDBAdapter makeExoticDBAdapter;
    private MakeNamedDBAdapter makeNamedDBAdapter;
    private MakeSheldDBAdapter makeSheldDBAdapter;
    private MakeWeaponDBAdapter makeWeaponDBAdapter;
    private MaterialDbAdapter materialDbAdapter;
    private InventoryDBAdapter inventoryDBAdapter;
    private MaxOptionsFMDBAdapter maxoptionDBAdapter;
    private LibraryDBAdapter libraryDBAdapter;
    private NamedFMDBAdapter namedDBAdapter;
    private SheldFMDBAdapter sheldDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private WeaponFMDBAdapter weaponDBAdpater;
    private SheldDbAdapter sheldItemDBAdapter;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private boolean btnEnd, openSheld = false, openWeapon = false, making = false;
    private int reset_count = 0;
    private CircleProgressBar progressMake;
    private int check_index = 0;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (reset_count >= 1500) btnEnd = true; //리셋카운트가 1500 이상이 되면 작동 (3초 이후)
            if (btnEnd) { //btnEnd가 참이 될 경우 작동한다.
                alertDialog.dismiss(); //다이얼로그를 닫는다.
                materialDbAdapter.open();
                for (int i = 0; i < need_material.length; i++) {
                    now_material[i] = materialDbAdapter.getMaterial(material_limit[i]);
                    now_material[i] -= need_material[i];
                    materialDbAdapter.updateMaterial(material_limit[i], now_material[i]);
                }
                materialDbAdapter.close();
                makeExoticDBAdapter.open();
                makeNamedDBAdapter.open();
                if (makeExoticDBAdapter.haveItem(makeItems.get(check_index).getName())) {
                    if (isWeapon(makeItems.get(check_index).getType())) {
                        makeWeaponExotic(check_index);
                    } else {
                        makeSheldExotic(check_index);
                    }
                } else if (makeNamedDBAdapter.haveItem(makeItems.get(check_index).getName())) {
                    if (isWeapon(makeItems.get(check_index).getType())) {
                        makeWeaponNamed(check_index);
                    } else {
                        makeSheldNamed(check_index);
                    }
                } else {
                    if (isWeapon(makeItems.get(check_index).getType())) {
                        makeWeapon(check_index);
                    } else {
                        makeSheld(check_index);
                    }
                }
                makeNamedDBAdapter.close();
                makeExoticDBAdapter.close();
                mHandler.removeMessages(0); //현재 핸들러를 종료시킨다.
            } else { //아직 리셋카운트로 인해 btnEnd가 참이 되지 않았을 경우 작동
                reset_count += 10; //10을 늘려준다. (1500까지 3초 걸린다.)
                progressMake.setProgress(reset_count); //리셋 카운트만큼 진행도를 설정한다.
            }
            Log.v("LC버튼", "Long클릭"); //로그를 남긴다.
            mHandler.sendEmptyMessageDelayed(0, 20); //핸들러를 0.02초만큼 반복시킨다. (다시 핸들러를 불러오는 방식으로 반복시키는 것이다.)
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        /*final TextView textView = root.findViewById(R.id.text_send);
        sendViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        makeExoticDBAdapter = new MakeExoticDBAdapter(getActivity());
        makeNamedDBAdapter = new MakeNamedDBAdapter(getActivity());
        makeSheldDBAdapter = new MakeSheldDBAdapter(getActivity());
        makeWeaponDBAdapter = new MakeWeaponDBAdapter(getActivity());
        materialDbAdapter = new MaterialDbAdapter(getActivity());
        inventoryDBAdapter = new InventoryDBAdapter(getActivity());
        maxoptionDBAdapter = new MaxOptionsFMDBAdapter(getActivity());
        libraryDBAdapter = new LibraryDBAdapter(getActivity());
        namedDBAdapter = new NamedFMDBAdapter(getActivity());
        sheldDBAdapter = new SheldFMDBAdapter(getActivity());
        talentDBAdapter = new TalentFMDBAdapter(getActivity());
        makeItems = new ArrayList<MakeItem>();
        weaponDBAdpater = new WeaponFMDBAdapter(getActivity());
        sheldItemDBAdapter = new SheldDbAdapter(getActivity());
        for (int i = 0; i < images.length; i++) images[i] = getActivity().getResources().getIdentifier("eq"+(i+1), "drawable", getActivity().getPackageName());
        copyExcelDataToDatabase(images);

        listWeapon = root.findViewById(R.id.listWeapon);
        listSheld = root.findViewById(R.id.listSheld);
        listExotic = root.findViewById(R.id.listExotic);
        rgType = root.findViewById(R.id.rgType);
        rgWeapon = root.findViewById(R.id.rgWeapon);
        rgSheld = root.findViewById(R.id.rgSheld);
        layoutWeapon = root.findViewById(R.id.layoutWeapon);
        layoutSheld = root.findViewById(R.id.layoutSheld);
        layoutExotic = root.findViewById(R.id.layoutExotic);
        btnMaterialList = root.findViewById(R.id.btnMaterialList);
        rdoWeaponType = root.findViewById(R.id.rdoWeaponType);
        rdoSheldType = root.findViewById(R.id.rdoSheldType);
        rdoExoticType = root.findViewById(R.id.rdoExoticType);

        int resource;
        for (int i = 0; i < rdoWeapon.length; i++) {
            resource = root.getResources().getIdentifier("rdoWeapon"+(i+1), "id", getActivity().getPackageName());
            rdoWeapon[i] = root.findViewById(resource);
        }
        for (int i = 0; i < rdoSheld.length; i++) {
            resource = root.getResources().getIdentifier("rdoSheld"+(i+1), "id", getActivity().getPackageName());
            rdoSheld[i] = root.findViewById(resource);
        }

        weaponInterface();

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdoWeaponType:
                        rdoWeaponType.setTextColor(Color.parseColor("#fe6e0e"));
                        rdoExoticType.setTextColor(Color.parseColor("#aaaaaa"));
                        rdoSheldType.setTextColor(Color.parseColor("#aaaaaa"));
                        layoutWeapon.setVisibility(View.VISIBLE);
                        layoutSheld.setVisibility(View.GONE);
                        layoutExotic.setVisibility(View.GONE);
                        rdoWeapon[0].setChecked(true);
                        weaponInterface();
                        break;
                    case R.id.rdoSheldType:
                        rdoWeaponType.setTextColor(Color.parseColor("#aaaaaa"));
                        rdoExoticType.setTextColor(Color.parseColor("#aaaaaa"));
                        rdoSheldType.setTextColor(Color.parseColor("#fe6e0e"));
                        layoutWeapon.setVisibility(View.GONE);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutExotic.setVisibility(View.GONE);
                        rdoSheld[0].setChecked(true);
                        sheldInterface();
                        break;
                    case R.id.rdoExoticType:
                        rdoWeaponType.setTextColor(Color.parseColor("#aaaaaa"));
                        rdoExoticType.setTextColor(Color.parseColor("#fe6e0e"));
                        rdoSheldType.setTextColor(Color.parseColor("#aaaaaa"));
                        layoutWeapon.setVisibility(View.GONE);
                        layoutSheld.setVisibility(View.GONE);
                        layoutExotic.setVisibility(View.VISIBLE);

                        makeItems.clear();
                        makeExoticDBAdapter.open();
                        cursor = makeExoticDBAdapter.fetchAll();
                        makeExoticDBAdapter.close();
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            String name = cursor.getString(1);
                            String type = cursor.getString(2);
                            MakeItem item = new MakeItem(name, type);
                            makeItems.add(item);
                            cursor.moveToNext();
                        }
                        makeAdapter = new MakeAdapter(getActivity(), makeItems, true);
                        listExotic.setAdapter(makeAdapter);
                        break;
                }
            }
        });

        rgWeapon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < rdoWeapon.length; i++) {
                    if (rdoWeapon[i].isChecked()) rdoWeapon[i].setTextColor(Color.parseColor("#FF6337"));
                    else rdoWeapon[i].setTextColor(Color.parseColor("#F0F0F0"));
                }

                makeItems.clear();
                for (int i = 0; i < rdoWeapon.length; i++) {
                    if (rdoWeapon[i].isChecked()) {
                        makeNamedDBAdapter.open();
                        cursor = makeNamedDBAdapter.fetchTypeData(weapontypes[i]);
                        makeNamedDBAdapter.close();
                        while (!cursor.isAfterLast()) {
                            String name = cursor.getString(1);
                            MakeItem item = new MakeItem(name, weapontypes[i]);
                            makeItems.add(item);
                            cursor.moveToNext();
                        }
                        makeWeaponDBAdapter.open();
                        cursor = makeWeaponDBAdapter.fetchTypeData(weapontypes[i]);
                        makeWeaponDBAdapter.close();
                        while (!cursor.isAfterLast()) {
                            String name = cursor.getString(1);
                            MakeItem item = new MakeItem(name, weapontypes[i]);
                            makeItems.add(item);
                            cursor.moveToNext();
                        }
                        makeAdapter = new MakeAdapter(getActivity(), makeItems, false);
                        listWeapon.setAdapter(makeAdapter);
                    }
                }
            }
        });

        rgSheld.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < rdoSheld.length; i++) {
                    if (rdoSheld[i].isChecked()) rdoSheld[i].setTextColor(Color.parseColor("#FF6337"));
                    else rdoSheld[i].setTextColor(Color.parseColor("#F0F0F0"));
                }

                makeItems.clear();
                for (int i = 0; i < rdoSheld.length; i++) {
                    if (rdoSheld[i].isChecked()) {
                        makeNamedDBAdapter.open();
                        cursor = makeNamedDBAdapter.fetchTypeData(sheldtypes[i]);
                        makeNamedDBAdapter.close();
                        while (!cursor.isAfterLast()) {
                            String name = cursor.getString(1);
                            String asp = cursor.getString(4);
                            MakeItem item = new MakeItem(name, sheldtypes[i]);
                            item.setAsp(asp);
                            makeItems.add(item);
                            cursor.moveToNext();
                        }
                        makeSheldDBAdapter.open();
                        cursor = makeSheldDBAdapter.fetchTypeData(sheldtypes[i]);
                        makeSheldDBAdapter.close();
                        while (!cursor.isAfterLast()) {
                            String name = cursor.getString(1);
                            int gear = cursor.getInt(3);
                            String asp = cursor.getString(4);
                            boolean isGear = false;
                            if (gear == 1) isGear = true;
                            MakeItem item = new MakeItem(name, sheldtypes[i]);
                            item.setAsp(asp);
                            item.setGear(isGear);
                            makeItems.add(item);
                            cursor.moveToNext();
                        }
                        makeAdapter = new MakeAdapter(getActivity(), makeItems, false);
                        listSheld.setAdapter(makeAdapter);
                    }
                }
            }
        });

        listWeapon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean possible = true;

                View dialogView = getLayoutInflater().inflate(R.layout.makedialog, null);

                TextView txtName = dialogView.findViewById(R.id.txtName);
                TextView txtType = dialogView.findViewById(R.id.txtType);
                TextView txtNormal = dialogView.findViewById(R.id.txtNormal);
                TextView txtRare = dialogView.findViewById(R.id.txtRare);
                TextView txtEpic = dialogView.findViewById(R.id.txtEpic);
                TextView txtNowNormal = dialogView.findViewById(R.id.txtNowNormal);
                TextView txtNowRare = dialogView.findViewById(R.id.txtNowRare);
                TextView txtNowEpic = dialogView.findViewById(R.id.txtNowEpic);
                TextView txtNeedNormal = dialogView.findViewById(R.id.txtNeedNormal);
                TextView txtNeedRare = dialogView.findViewById(R.id.txtNeedRare);
                TextView txtNeedEpic = dialogView.findViewById(R.id.txtNeedEpic);
                Button btnExit = dialogView.findViewById(R.id.btnExit);
                Button btnMake = dialogView.findViewById(R.id.btnMake);
                ImageView imgType = dialogView.findViewById(R.id.imgType);
                LinearLayout layoutCore = dialogView.findViewById(R.id.layoutCore);
                LinearLayout layoutOption = dialogView.findViewById(R.id.layoutOption);
                TextView txtTalent = dialogView.findViewById(R.id.txtTalent);

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                if (isWeapon(makeItems.get(position).getType())) {
                    material_limit = new String[]{"총몸부품", "강철", "탄소섬유"};
                } else if (isSheldAType(makeItems.get(position).getType())) {
                    material_limit = new String[]{"보호용 옷감", "세라믹", "전자부품"};
                } else {
                    material_limit = new String[]{"보호용 옷감", "폴리카보네이트", "티타늄"};
                }
                need_material = new int[]{60, 36, 26};

                makeNamedDBAdapter.open();
                if (makeNamedDBAdapter.haveItem(makeItems.get(position).getName())) {
                    layoutCore.setVisibility(View.VISIBLE);
                    layoutOption.setVisibility(View.GONE);
                    cursor = makeNamedDBAdapter.fetchData(makeItems.get(position).getName());
                    txtTalent.setText(cursor.getString(2));
                }
                makeNamedDBAdapter.close();

                txtName.setText(makeItems.get(position).getName());
                txtType.setText(makeItems.get(position).getType());
                if (isTypeWeapon(makeItems.get(position).getType())) {
                    imgType.setImageResource(setWeaponImageResource(makeItems.get(position).getType()));
                } else {
                    imgType.setImageResource(setSheldImageResource(makeItems.get(position).getType()));
                }

                materialDbAdapter.open();
                for (int i = 0; i < need_material.length; i++) {
                    now_material[i] = materialDbAdapter.getMaterial(material_limit[i]);
                }
                materialDbAdapter.close();

                txtNormal.setText(material_limit[0]);
                txtRare.setText(material_limit[1]);
                txtEpic.setText(material_limit[2]);
                txtNowNormal.setText(Integer.toString(now_material[0]));
                txtNowRare.setText(Integer.toString(now_material[1]));
                txtNowEpic.setText(Integer.toString(now_material[2]));
                txtNeedNormal.setText(Integer.toString(need_material[0]));
                txtNeedRare.setText(Integer.toString(need_material[1]));
                txtNeedEpic.setText(Integer.toString(need_material[2]));

                if (need_material[0] > now_material[0]) {
                    txtNowNormal.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }
                if (need_material[1] > now_material[1]) {
                    txtNowRare.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }
                if (need_material[2] > now_material[2]) {
                    txtNowEpic.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }

                makeExoticDBAdapter.open();
                makeNamedDBAdapter.open();
                if (makeNamedDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#B18912"));
                } else if (makeExoticDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#fe6e0e"));
                } else if (makeItems.get(position).getGear()) {
                    txtName.setTextColor(Color.parseColor("#2BBE2B"));
                } else {
                    txtName.setTextColor(Color.parseColor("#F0F0F0"));
                }
                makeNamedDBAdapter.close();
                makeExoticDBAdapter.close();

                final int index = position;
                final boolean final_possible = possible;
                btnMake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!final_possible) {
                            Toast.makeText(getActivity(), "재료가 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        alertDialog.dismiss();
                        check_index = index;
                        making = false;

                        View makingView = getLayoutInflater().inflate(R.layout.makingdialog, null);

                        ImageView imgIcon = makingView.findViewById(R.id.imgIcon);
                        progressMake = makingView.findViewById(R.id.progressMake);
                        Button btnExit = makingView.findViewById(R.id.btnExit);
                        final TextView txtMaking = makingView.findViewById(R.id.txtMaking);
                        TextView txtName = makingView.findViewById(R.id.txtName);
                        TextView txtType = makingView.findViewById(R.id.txtType);
                        
                        txtName.setText(makeItems.get(index).getName());
                        txtType.setText(makeItems.get(index).getType());
                        makeExoticDBAdapter.open();
                        makeNamedDBAdapter.open();
                        if (makeNamedDBAdapter.haveItem(makeItems.get(index).getName())) {
                            txtName.setTextColor(Color.parseColor("#B18912"));
                        } else if (makeExoticDBAdapter.haveItem(makeItems.get(index).getName())) {
                            txtName.setTextColor(Color.parseColor("#fe6e0e"));
                        } else if (makeItems.get(index).getGear()) {
                            txtName.setTextColor(Color.parseColor("#2BBE2B"));
                        } else {
                            txtName.setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        makeNamedDBAdapter.close();
                        makeExoticDBAdapter.close();

                        if (isTypeWeapon(makeItems.get(index).getType())) {
                            imgIcon.setImageResource(setWeaponImageResource(makeItems.get(index).getType()));
                        } else {
                            imgIcon.setImageResource(setSheldImageResource(makeItems.get(index).getType()));
                        }

                        reset_count = 0;
                        btnEnd = false;
                        progressMake.setMax(1500);
                        progressMake.setProgress(0);

                        btnExit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                reset_count = 0;
                                progressMake.setProgress(0);
                                mHandler.removeMessages(0);
                            }
                        });

                        imgIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!making) {
                                    making = true;
                                    txtMaking.setText("제작 중...");
                                    mHandler.sendEmptyMessageDelayed(0, 20); //0.02초 딜레이를 주고 핸들러 메시지를 보내 작업한다.
                                }
                            }
                        });

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setView(makingView);

                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                reset_count = 0;
                                progressMake.setProgress(0);
                                mHandler.removeMessages(0);
                            }
                        });
                    }
                });

                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        listSheld.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean possible = true;

                View dialogView = getLayoutInflater().inflate(R.layout.makedialog, null);

                TextView txtName = dialogView.findViewById(R.id.txtName);
                TextView txtType = dialogView.findViewById(R.id.txtType);
                TextView txtNormal = dialogView.findViewById(R.id.txtNormal);
                TextView txtRare = dialogView.findViewById(R.id.txtRare);
                TextView txtEpic = dialogView.findViewById(R.id.txtEpic);
                TextView txtNowNormal = dialogView.findViewById(R.id.txtNowNormal);
                TextView txtNowRare = dialogView.findViewById(R.id.txtNowRare);
                TextView txtNowEpic = dialogView.findViewById(R.id.txtNowEpic);
                TextView txtNeedNormal = dialogView.findViewById(R.id.txtNeedNormal);
                TextView txtNeedRare = dialogView.findViewById(R.id.txtNeedRare);
                TextView txtNeedEpic = dialogView.findViewById(R.id.txtNeedEpic);
                Button btnExit = dialogView.findViewById(R.id.btnExit);
                Button btnMake = dialogView.findViewById(R.id.btnMake);
                ImageView imgType = dialogView.findViewById(R.id.imgType);
                LinearLayout layoutCore = dialogView.findViewById(R.id.layoutCore);
                ImageView imgCore = dialogView.findViewById(R.id.imgCore);
                TextView txtCore = dialogView.findViewById(R.id.txtCore);
                LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
                TextView txtInfo = dialogView.findViewById(R.id.txtInfo);
                TextView txtTalent = dialogView.findViewById(R.id.txtTalent);
                ImageView imgTalent = dialogView.findViewById(R.id.imgTalent);

                LinearLayout layoutSet = dialogView.findViewById(R.id.layoutSet);
                TextView txtFirstSet = dialogView.findViewById(R.id.txtFirstSet);
                TextView txtSecondSet = dialogView.findViewById(R.id.txtSecondSet);
                TextView txtThirdSet = dialogView.findViewById(R.id.txtThirdSet);

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                if (isWeapon(makeItems.get(position).getType())) {
                    material_limit = new String[]{"총몸부품", "강철", "탄소섬유"};
                } else if (isSheldAType(makeItems.get(position).getType())) {
                    material_limit = new String[]{"보호용 옷감", "세라믹", "전자부품"};
                } else {
                    material_limit = new String[]{"보호용 옷감", "폴리카보네이트", "티타늄"};
                }
                need_material = new int[]{60, 36, 26};

                txtName.setText(makeItems.get(position).getName());
                txtType.setText(makeItems.get(position).getType());
                if (isTypeWeapon(makeItems.get(position).getType())) {
                    imgType.setImageResource(setWeaponImageResource(makeItems.get(position).getType()));
                } else {
                    imgType.setImageResource(setSheldImageResource(makeItems.get(position).getType()));
                }

                layoutCore.setVisibility(View.VISIBLE);
                makeNamedDBAdapter.open();
                if (makeNamedDBAdapter.haveItem(makeItems.get(position).getName())) {
                    if (makeNamedDBAdapter.noTalent(makeItems.get(position).getName())) {
                        txtInfo.setText("속성");
                        imgTalent.setVisibility(View.VISIBLE);
                        cursor = makeNamedDBAdapter.fetchData(makeItems.get(position).getName());
                        String asp = cursor.getString(4);
                        if (asp.equals("공격")) {
                            imgTalent.setImageResource(R.drawable.attack);
                        } else if (asp.equals("방어")) {
                            imgTalent.setImageResource(R.drawable.sheld);
                        } else {
                            imgTalent.setImageResource(R.drawable.power);
                        }
                    }
                    cursor = makeNamedDBAdapter.fetchData(makeItems.get(position).getName());
                    txtTalent.setText(cursor.getString(2));
                } else {
                    layoutTalent.setVisibility(View.GONE);
                    layoutSet.setVisibility(View.VISIBLE);
                    sheldItemDBAdapter.open();
                    cursor = sheldItemDBAdapter.fetchName(makeItems.get(position).getName());
                    String first = cursor.getString(2);
                    String second = cursor.getString(3);
                    String third = cursor.getString(4);
                    sheldItemDBAdapter.close();
                    txtFirstSet.setText(first);
                    txtSecondSet.setText(second);
                    txtThirdSet.setText(third);
                }
                makeNamedDBAdapter.close();

                materialDbAdapter.open();
                for (int i = 0; i < need_material.length; i++) {
                    now_material[i] = materialDbAdapter.getMaterial(material_limit[i]);
                }
                materialDbAdapter.close();

                txtNormal.setText(material_limit[0]);
                txtRare.setText(material_limit[1]);
                txtEpic.setText(material_limit[2]);
                txtNowNormal.setText(Integer.toString(now_material[0]));
                txtNowRare.setText(Integer.toString(now_material[1]));
                txtNowEpic.setText(Integer.toString(now_material[2]));
                txtNeedNormal.setText(Integer.toString(need_material[0]));
                txtNeedRare.setText(Integer.toString(need_material[1]));
                txtNeedEpic.setText(Integer.toString(need_material[2]));

                if (need_material[0] > now_material[0]) {
                    txtNowNormal.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }
                if (need_material[1] > now_material[1]) {
                    txtNowRare.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }
                if (need_material[2] > now_material[2]) {
                    txtNowEpic.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }

                makeExoticDBAdapter.open();
                makeNamedDBAdapter.open();
                if (makeNamedDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#B18912"));
                } else if (makeExoticDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#fe6e0e"));
                } else if (makeItems.get(position).getGear()) {
                    txtName.setTextColor(Color.parseColor("#2BBE2B"));
                } else {
                    txtName.setTextColor(Color.parseColor("#F0F0F0"));
                }
                makeNamedDBAdapter.close();
                makeExoticDBAdapter.close();

                final int index = position;
                final boolean final_possible = possible;
                btnMake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!final_possible) {
                            Toast.makeText(getActivity(), "재료가 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        alertDialog.dismiss();
                        check_index = index;
                        making = false;

                        View makingView = getLayoutInflater().inflate(R.layout.makingdialog, null);

                        ImageView imgIcon = makingView.findViewById(R.id.imgIcon);
                        progressMake = makingView.findViewById(R.id.progressMake);
                        Button btnExit = makingView.findViewById(R.id.btnExit);
                        final TextView txtMaking = makingView.findViewById(R.id.txtMaking);
                        TextView txtName = makingView.findViewById(R.id.txtName);
                        TextView txtType = makingView.findViewById(R.id.txtType);
                        
                        txtName.setText(makeItems.get(index).getName());
                        txtType.setText(makeItems.get(index).getType());
                        makeExoticDBAdapter.open();
                        makeNamedDBAdapter.open();
                        if (makeNamedDBAdapter.haveItem(makeItems.get(index).getName())) {
                            txtName.setTextColor(Color.parseColor("#B18912"));
                        } else if (makeExoticDBAdapter.haveItem(makeItems.get(index).getName())) {
                            txtName.setTextColor(Color.parseColor("#fe6e0e"));
                        } else if (makeItems.get(index).getGear()) {
                            txtName.setTextColor(Color.parseColor("#2BBE2B"));
                        } else {
                            txtName.setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        makeNamedDBAdapter.close();
                        makeExoticDBAdapter.close();

                        if (isTypeWeapon(makeItems.get(index).getType())) {
                            imgIcon.setImageResource(setWeaponImageResource(makeItems.get(index).getType()));
                        } else {
                            imgIcon.setImageResource(setSheldImageResource(makeItems.get(index).getType()));
                        }

                        reset_count = 0;
                        btnEnd = false;
                        progressMake.setMax(1500);
                        progressMake.setProgress(0);

                        btnExit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                reset_count = 0;
                                progressMake.setProgress(0);
                                mHandler.removeMessages(0);
                            }
                        });

                        imgIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!making) {
                                    making = true;
                                    txtMaking.setText("제작 중...");
                                    mHandler.sendEmptyMessageDelayed(0, 20); //0.02초 딜레이를 주고 핸들러 메시지를 보내 작업한다.
                                }
                            }
                        });

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setView(makingView);

                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                reset_count = 0;
                                progressMake.setProgress(0);
                                mHandler.removeMessages(0);
                            }
                        });
                    }
                });

                if (makeItems.get(position).getAsp().equals("공격")) {
                    imgCore.setImageResource(R.drawable.attack);
                    txtCore.setText("무기 데미지");
                } else if (makeItems.get(position).getAsp().equals("방어")) {
                    imgCore.setImageResource(R.drawable.sheld);
                    txtCore.setText("방어도");
                } else {
                    imgCore.setImageResource(R.drawable.power);
                    txtCore.setText("스킬 등급");
                }

                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        listExotic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean possible = true;

                View dialogView = getLayoutInflater().inflate(R.layout.makedialog, null);

                TextView txtName = dialogView.findViewById(R.id.txtName);
                TextView txtType = dialogView.findViewById(R.id.txtType);
                TextView txtNormal = dialogView.findViewById(R.id.txtNormal);
                TextView txtRare = dialogView.findViewById(R.id.txtRare);
                TextView txtEpic = dialogView.findViewById(R.id.txtEpic);
                TextView txtNowNormal = dialogView.findViewById(R.id.txtNowNormal);
                TextView txtNowRare = dialogView.findViewById(R.id.txtNowRare);
                TextView txtNowEpic = dialogView.findViewById(R.id.txtNowEpic);
                TextView txtNeedNormal = dialogView.findViewById(R.id.txtNeedNormal);
                TextView txtNeedRare = dialogView.findViewById(R.id.txtNeedRare);
                TextView txtNeedEpic = dialogView.findViewById(R.id.txtNeedEpic);
                LinearLayout layoutExotic = dialogView.findViewById(R.id.layoutExotic);
                TextView txtExotic = dialogView.findViewById(R.id.txtExotic);
                TextView txtNowExotic = dialogView.findViewById(R.id.txtNowExotic);
                TextView txtNeedExotic = dialogView.findViewById(R.id.txtNeedExotic);
                Button btnExit = dialogView.findViewById(R.id.btnExit);
                Button btnMake = dialogView.findViewById(R.id.btnMake);
                ImageView imgType = dialogView.findViewById(R.id.imgType);
                LinearLayout layoutCore = dialogView.findViewById(R.id.layoutCore);
                ImageView imgCore = dialogView.findViewById(R.id.imgCore);
                TextView txtCore = dialogView.findViewById(R.id.txtCore);

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                if (isWeapon(makeItems.get(position).getType())) {
                    material_limit = new String[]{"총몸부품", "강철", "탄소섬유", "특급 부품"};
                } else if (isSheldAType(makeItems.get(position).getType())) {
                    material_limit = new String[]{"보호용 옷감", "세라믹", "전자부품", "특급 부품"};
                } else {
                    material_limit = new String[]{"보호용 옷감", "폴리카보네이트", "티타늄", "특급 부품"};
                }
                need_material = new int[]{60, 36, 26, 2};

                txtName.setText(makeItems.get(position).getName());
                txtType.setText(makeItems.get(position).getType());
                if (isTypeWeapon(makeItems.get(position).getType())) {
                    imgType.setImageResource(setWeaponImageResource(makeItems.get(position).getType()));
                } else {
                    imgType.setImageResource(setSheldImageResource(makeItems.get(position).getType()));
                }

                materialDbAdapter.open();
                for (int i = 0; i < need_material.length; i++) {
                    now_material[i] = materialDbAdapter.getMaterial(material_limit[i]);
                }
                materialDbAdapter.close();

                txtNormal.setText(material_limit[0]);
                txtRare.setText(material_limit[1]);
                txtEpic.setText(material_limit[2]);
                txtExotic.setText(material_limit[3]);
                txtNowNormal.setText(Integer.toString(now_material[0]));
                txtNowRare.setText(Integer.toString(now_material[1]));
                txtNowEpic.setText(Integer.toString(now_material[2]));
                txtNowExotic.setText(Integer.toString(now_material[3]));
                txtNeedNormal.setText(Integer.toString(need_material[0]));
                txtNeedRare.setText(Integer.toString(need_material[1]));
                txtNeedEpic.setText(Integer.toString(need_material[2]));

                if (need_material[0] > now_material[0]) {
                    txtNowNormal.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }
                if (need_material[1] > now_material[1]) {
                    txtNowRare.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }
                if (need_material[2] > now_material[2]) {
                    txtNowEpic.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }
                if (need_material[3] > now_material[3]) {
                    txtNowExotic.setTextColor(Color.parseColor("#FF0000"));
                    possible = false;
                }

                layoutExotic.setVisibility(View.VISIBLE);
                txtNormal.setText(material_limit[0]);
                txtRare.setText(material_limit[1]);
                txtEpic.setText(material_limit[2]);
                txtExotic.setText(material_limit[3]);
                materialDbAdapter.open();
                txtNowNormal.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[0])));
                txtNowRare.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[1])));
                txtNowEpic.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[2])));
                txtNowExotic.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[3])));
                materialDbAdapter.close();
                txtNeedNormal.setText(Integer.toString(need_material[0]));
                txtNeedRare.setText(Integer.toString(need_material[1]));
                txtNeedEpic.setText(Integer.toString(need_material[2]));
                txtNeedExotic.setText(Integer.toString(need_material[3]));

                makeExoticDBAdapter.open();
                makeNamedDBAdapter.open();
                if (makeNamedDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#B18912"));
                } else if (makeExoticDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#fe6e0e"));
                } else if (makeItems.get(position).getGear()) {
                    txtName.setTextColor(Color.parseColor("#2BBE2B"));
                } else {
                    txtName.setTextColor(Color.parseColor("#F0F0F0"));
                }
                makeNamedDBAdapter.close();
                makeExoticDBAdapter.close();

                /*if (!isWeapon(makeItems.get(position).getType())) {
                    layoutCore.setVisibility(View.VISIBLE);
                    if (makeItems.get(position).getAsp().equals("공격")) {
                        imgCore.setImageResource(R.drawable.attack);
                        txtCore.setText("무기 데미지");
                    } else if (makeItems.get(position).getAsp().equals("방어")) {
                        imgCore.setImageResource(R.drawable.sheld);
                        txtCore.setText("방어도");
                    } else {
                        imgCore.setImageResource(R.drawable.power);
                        txtCore.setText("스킬 등급");
                    }
                }*/

                final int index = position;
                final boolean final_possible = possible;
                btnMake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!final_possible) {
                            Toast.makeText(getActivity(), "재료가 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        alertDialog.dismiss();
                        check_index = index;
                        making = false;

                        View makingView = getLayoutInflater().inflate(R.layout.makingdialog, null);

                        ImageView imgIcon = makingView.findViewById(R.id.imgIcon);
                        progressMake = makingView.findViewById(R.id.progressMake);
                        Button btnExit = makingView.findViewById(R.id.btnExit);
                        final TextView txtMaking = makingView.findViewById(R.id.txtMaking);
                        TextView txtName = makingView.findViewById(R.id.txtName);
                        TextView txtType = makingView.findViewById(R.id.txtType);
                        
                        txtName.setText(makeItems.get(index).getName());
                        txtType.setText(makeItems.get(index).getType());
                        makeExoticDBAdapter.open();
                        makeNamedDBAdapter.open();
                        if (makeNamedDBAdapter.haveItem(makeItems.get(index).getName())) {
                            txtName.setTextColor(Color.parseColor("#B18912"));
                        } else if (makeExoticDBAdapter.haveItem(makeItems.get(index).getName())) {
                            txtName.setTextColor(Color.parseColor("#fe6e0e"));
                        } else if (makeItems.get(index).getGear()) {
                            txtName.setTextColor(Color.parseColor("#2BBE2B"));
                        } else {
                            txtName.setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        makeNamedDBAdapter.close();
                        makeExoticDBAdapter.close();

                        if (isTypeWeapon(makeItems.get(index).getType())) {
                            imgIcon.setImageResource(setWeaponImageResource(makeItems.get(index).getType()));
                        } else {
                            imgIcon.setImageResource(setSheldImageResource(makeItems.get(index).getType()));
                        }

                        reset_count = 0;
                        btnEnd = false;
                        progressMake.setMax(1500);
                        progressMake.setProgress(0);

                        btnExit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                reset_count = 0;
                                progressMake.setProgress(0);
                                mHandler.removeMessages(0);
                            }
                        });

                        imgIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!making) {
                                    making = true;
                                    txtMaking.setText("제작 중...");
                                    mHandler.sendEmptyMessageDelayed(0, 20); //0.02초 딜레이를 주고 핸들러 메시지를 보내 작업한다.
                                }
                            }
                        });

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setView(makingView);

                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                reset_count = 0;
                                progressMake.setProgress(0);
                                mHandler.removeMessages(0);
                            }
                        });
                    }
                });

                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        btnMaterialList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.materialtoolslayout, null); //다이얼로그에 추가할 뷰 생성
                Button btnMaterialExit = dialogView.findViewById(R.id.btnMaterialExit); //팝업창을 닫을 버튼

                btnMaterialExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //버튼을 누르면 작동
                        alertDialog.dismiss(); //다이얼로그를 닫는다.
                    }
                });

                Cursor cursor;
                materialDbAdapter.open();
                final int[] material = new int[materialDbAdapter.getCount()];
                final String[] material_name = new String[materialDbAdapter.getCount()];
                final int[] material_max = new int[materialDbAdapter.getCount()];
                cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material_name[count] = cursor.getString(1);
                    material[count] = cursor.getInt(2);
                    material_max[count] = cursor.getInt(3);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();

                TextView[] txtNormal = new TextView[2];
                TextView[] txtRare = new TextView[3];
                TextView[] txtEpic = new TextView[3];
                TextView txtDark = dialogView.findViewById(R.id.txtDark);
                ProgressBar progressDark = dialogView.findViewById(R.id.progressDark);
                TextView txtExotic = dialogView.findViewById(R.id.txtExotic);
                ProgressBar progressExotic = dialogView.findViewById(R.id.progressExotic);

                ProgressBar[] progressNormal = new ProgressBar[2];
                ProgressBar[] progressRare = new ProgressBar[3];
                ProgressBar[] progressEpic = new ProgressBar[3];

                int resource;
                for (int i = 0; i < txtNormal.length; i++) {
                    resource = dialogView.getResources().getIdentifier("txtNormal"+(i+1), "id", getActivity().getPackageName());
                    txtNormal[i] = dialogView.findViewById(resource);
                    resource = dialogView.getResources().getIdentifier("progressNormal"+(i+1), "id", getActivity().getPackageName());
                    progressNormal[i] = dialogView.findViewById(resource);
                    progressNormal[i].setMax(2000);
                    progressNormal[i].setProgress(material[i]);
                    txtNormal[i].setText(Integer.toString(material[i]));
                }
                for (int i = 0; i < txtRare.length; i++) {
                    resource = dialogView.getResources().getIdentifier("txtRare"+(i+1), "id", getActivity().getPackageName());
                    txtRare[i] = dialogView.findViewById(resource);
                    resource = dialogView.getResources().getIdentifier("txtEpic"+(i+1), "id", getActivity().getPackageName());
                    txtEpic[i] = dialogView.findViewById(resource);
                    resource = dialogView.getResources().getIdentifier("progressRare"+(i+1), "id", getActivity().getPackageName());
                    progressRare[i] = dialogView.findViewById(resource);
                    resource = dialogView.getResources().getIdentifier("progressEpic"+(i+1), "id", getActivity().getPackageName());
                    progressEpic[i] = dialogView.findViewById(resource);
                    progressRare[i].setMax(1500);
                    progressEpic[i].setMax(1500);
                    progressRare[i].setProgress(material[i+2]);
                    progressEpic[i].setProgress(material[i+5]);
                    txtRare[i].setText(Integer.toString(material[i+2]));
                    txtEpic[i].setText(Integer.toString(material[i+5]));
                }
                txtDark.setText(Integer.toString(material[8]));
                progressDark.setMax(300);
                progressDark.setProgress(material[8]);

                txtExotic.setText(Integer.toString(material[9]));
                progressExotic.setMax(20);
                progressExotic.setProgress(material[9]);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        return root;
    }

    private void makeSheldNamed(int index) {
        String item_name, item_type, item_talent = "";
        String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
        String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
        double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
        double max_core1, max_core2, max_sub1, max_sub2;
        String brand = "";
        openWeapon = false;
        openSheld = true;
        View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null); //아이템 드랍할때마다 보여줄 뷰이다.
        TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
        TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
        Button btnChange = dialogView.findViewById(R.id.btnChange); //특급, 네임드일 경우 내용을 바로 보여주지 않고 이 버튼으로 누르면 보여주도록 해준다.
        LinearLayout tableMain = dialogView.findViewById(R.id.tableMain); //내용이 들어있는 테이블 레이아웃

        Button btnExit = dialogView.findViewById(R.id.btnExit); //닫기 버튼\
        Button btnDestroy = dialogView.findViewById(R.id.btnDestroy);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        ImageView imgType = dialogView.findViewById(R.id.imgType);

        TextView txtWMain1 = dialogView.findViewById(R.id.txtWMain1); //첫번째 무기 핵심속성
        TextView txtWMain2 = dialogView.findViewById(R.id.txtWMain2); //두번째 무기 핵심속성
        TextView txtWSub = dialogView.findViewById(R.id.txtWSub); //무기 속성
        ProgressBar progressWMain1 = dialogView.findViewById(R.id.progressWMain1); //첫번째 무기 핵심속성 진행도
        ProgressBar progressWMain2 = dialogView.findViewById(R.id.progressWMain2); //두번재 무기 핵심속성 진행도
        ProgressBar progressWSub = dialogView.findViewById(R.id.progressWSub); //무기 속성 진행도
        TextView txtWTalent = dialogView.findViewById(R.id.txtWTalent); //무기 탤런트

        TextView txtSMain = dialogView.findViewById(R.id.txtSMain); //보호장구 핵심속성
        TextView txtSSub1 = dialogView.findViewById(R.id.txtSSub1); //첫번째 보호장구 속성
        TextView txtSSub2 = dialogView.findViewById(R.id.txtSSub2); //두번째 보호장구 속성
        ProgressBar progressSMain = dialogView.findViewById(R.id.progressSMain); //보호장구 핵심속성 진행도
        ProgressBar progressSSub1 = dialogView.findViewById(R.id.progressSSub1); //첫번째 보호장구 속성 진행도
        ProgressBar progressSSub2 = dialogView.findViewById(R.id.progressSSub2); //두번째 보호장구 속성 진행도
        ImageView imgSMain = dialogView.findViewById(R.id.imgSMain); //보호장구 핵심속성 타입 이미지
        ImageView imgSSub1 = dialogView.findViewById(R.id.imgSSub1); //첫번재 보호장구 속성 타입 이미지
        ImageView imgSSub2 = dialogView.findViewById(R.id.imgSSub2); //두번째 보호장구 속성 타입 이미지
        LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
        LinearLayout layoutTalentButton = dialogView.findViewById(R.id.layoutTalentButton);

        LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
        LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
        LinearLayout layoutSSub2 = dialogView.findViewById(R.id.layoutSSub2);

        TextView txtInventory = dialogView.findViewById(R.id.txtInventory);
        ImageView imgInventory = dialogView.findViewById(R.id.imgInventory);
        LinearLayout layoutInventory = dialogView.findViewById(R.id.layoutInventory);

        TextView txtWTalentContent = dialogView.findViewById(R.id.txtWTalentContent);

        LinearLayout layoutWeaponMain1 = dialogView.findViewById(R.id.layoutWeaponMain1);
        LinearLayout layoutWeaponMain2 = dialogView.findViewById(R.id.layoutWeaponMain2);
        LinearLayout layoutWeaponSub = dialogView.findViewById(R.id.layoutWeaponSub);
        LinearLayout layoutSheldMain = dialogView.findViewById(R.id.layoutSheldMain);
        LinearLayout layoutSheldSub1 = dialogView.findViewById(R.id.layoutSheldSub1);
        LinearLayout layoutSheldSub2 = dialogView.findViewById(R.id.layoutSheldSub2);

        SeekBar seekWMain1 = dialogView.findViewById(R.id.seekWMain1);
        SeekBar seekWMain2 = dialogView.findViewById(R.id.seekWMain2);
        SeekBar seekWSub = dialogView.findViewById(R.id.seekWSub);
        SeekBar seekSMain = dialogView.findViewById(R.id.seekSMain);
        SeekBar seekSSub1 = dialogView.findViewById(R.id.seekSSub1);
        SeekBar seekSSub2 = dialogView.findViewById(R.id.seekSSub2);
        seekWMain1.setEnabled(false);
        seekWMain2.setEnabled(false);
        seekWSub.setEnabled(false);
        seekSMain.setEnabled(false);
        seekSSub1.setEnabled(false);
        seekSSub2.setEnabled(false);

        inventoryDBAdapter.open();
        txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
        inventoryDBAdapter.close();
        imgInventory.setImageResource(R.drawable.inven);
        btnAdd.setVisibility(View.VISIBLE);
        Cursor cursor;
        int pick, temp_percent; //램덤 난수가 저장될 변수
        tableMain.setBackgroundResource(R.drawable.rareitem);
        String temp_option; //옵션 이름
        tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
        btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
        layoutSheld.setVisibility(View.VISIBLE); //보호장구 옵션 레이아웃을 숨긴다.
        layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
        txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
        layoutSheldSub2.setVisibility(View.VISIBLE);

        txtName.setTextColor(Color.parseColor("#c99700"));
        item_name = makeItems.get(index).getName();
        item_type = makeItems.get(index).getType();
        txtName.setText(item_name);
        txtType.setText(item_type);
        if (sheldTalent(item_type)) {
            txtWTalent.setTextColor(Color.parseColor("#c99700"));
            layoutTalent.setVisibility(View.VISIBLE);
            makeNamedDBAdapter.open();
            cursor = makeNamedDBAdapter.fetchData(makeItems.get(index).getName());
            item_talent = cursor.getString(2);
            txtWTalent.setText(item_talent);
            txtWTalentContent.setText(transformString(cursor.getString(6)));
            makeNamedDBAdapter.close();
        } else layoutTalent.setVisibility(View.GONE);
        makeNamedDBAdapter.open();
        cursor = makeNamedDBAdapter.fetchData(makeItems.get(index).getName());
        brand = cursor.getString(7);
        String brandset = "";
        makeNamedDBAdapter.close();
        sheldDBAdapter.open();
        cursor = sheldDBAdapter.fetchData(brand);
        brandset = cursor.getString(3);
        sheldDBAdapter.close();
        maxoptionDBAdapter.open();
        if (brandset.equals("공격")) {
            cursor = maxoptionDBAdapter.fetchSheldCoreData("무기 데미지");
            item_core1 = "무기 데미지";
            max_core1 = Double.parseDouble(cursor.getString(2));
            tail_core1 = cursor.getString(5);
            imgSMain.setImageResource(R.drawable.attack);
            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
            progressSMain.setVisibility(View.VISIBLE);
        } else if (brandset.equals("방어")) {
            cursor = maxoptionDBAdapter.fetchSheldCoreData("방어도");
            item_core1 = "방어도";
            max_core1 = Double.parseDouble(cursor.getString(2));
            tail_core1 = cursor.getString(5);
            imgSMain.setImageResource(R.drawable.sheld);
            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
            progressSMain.setVisibility(View.VISIBLE);
        } else {
            cursor = maxoptionDBAdapter.fetchSheldCoreData("스킬 등급");
            item_core1 = "스킬 등급";
            max_core1 = Double.parseDouble(cursor.getString(2));
            tail_core1 = cursor.getString(5);
            imgSMain.setImageResource(R.drawable.power);
            //progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
            progressSMain.setVisibility(View.GONE);
        }
        maxoptionDBAdapter.close();
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
        else core1 = max_core1;
        if ((int)Math.floor(core1) >= max_core1 && !item_core1.equals("스킬 등급")) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
        progressSMain.setMax((int)(max_core1*10));
        seekSMain.setMax((int)(max_core1*10));
        progressSMain.setProgress((int)(core1*10));
        if (tail_core1.equals("-")) tail_core1 = "";
        txtSMain.setText("+"+formatD(core1)+tail_core1+" "+item_core1);
        makeNamedDBAdapter.open();
        if (makeNamedDBAdapter.noTalent(makeItems.get(index).getName())) {
            cursor = makeNamedDBAdapter.fetchData(makeItems.get(index).getName());
            txtSSub1.setTextColor(Color.parseColor("#c99700"));
            txtSSub1.setText(cursor.getString(2));
            String asp = cursor.getString(4);
            progressSSub1.setMax(100);
            progressSSub1.setProgress(100);
            if (asp.equals("공격")) {
                imgSSub1.setImageResource(R.drawable.attack_sub);
                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

            } else if (asp.equals("방어")) {
                imgSSub1.setImageResource(R.drawable.sheld_sub);
                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

            } else {
                imgSSub1.setImageResource(R.drawable.power_sub);
                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

            }
            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
        } else {
            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
            maxoptionDBAdapter.open();
            ArrayList<OptionItem> optionItems = maxoptionDBAdapter.fetchOptionItemData("보호장구 부속성");
            for (int i = 0; i < optionItems.size(); i++) {
                if (optionItems.get(i).getContent().equals(item_sub1)) optionItems.remove(i);
            }
            int random_value = percent(0, optionItems.size());
            OptionItem optionItem = optionItems.get(random_value);
            maxoptionDBAdapter.close();
            item_sub1 = optionItem.getContent();
            max_sub1 = optionItem.getValue();
            tail_sub1 = optionItem.getReter();
            if (optionItem.getOption().equals("공격")) {
                imgSSub1.setImageResource(R.drawable.attack_sub);
                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

            } else if (optionItem.getOption().equals("방어")) {
                imgSSub1.setImageResource(R.drawable.sheld_sub);
                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

            } else {
                imgSSub1.setImageResource(R.drawable.power_sub);
                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

            }
            pick = percent(1, 100);
            if (pick <= 20) temp_percent = 100;
            else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
            else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
            progressSSub1.setMax((int)(max_sub1*10));
        seekSSub1.setMax((int)(max_sub1*10));
            progressSSub1.setProgress((int)(sub1*10));
            if (tail_sub1.equals("-")) tail_sub1 = "";
            txtSSub1.setText("+"+formatD(sub1)+tail_sub1+" "+item_sub1);
        }
        maxoptionDBAdapter.open();
        makeNamedDBAdapter.close();

        //OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
        ArrayList<OptionItem> optionItems = maxoptionDBAdapter.fetchOptionItemData("보호장구 부속성");
        for (int i = 0; i < optionItems.size(); i++) {
            if (optionItems.get(i).getContent().equals(item_sub1)) optionItems.remove(i);
        }
        int random_value = percent(0, optionItems.size());
        OptionItem optionItem = optionItems.get(random_value);

        maxoptionDBAdapter.close();
        item_sub2 = optionItem.getContent();
        max_sub2 = optionItem.getValue();
        tail_sub2 = optionItem.getReter();
        if (optionItem.getOption().equals("공격")) {
            imgSSub2.setImageResource(R.drawable.attack_sub);
            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

        } else if (optionItem.getOption().equals("방어")) {
            imgSSub2.setImageResource(R.drawable.sheld_sub);
            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

        } else {
            imgSSub2.setImageResource(R.drawable.power_sub);
            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

        }
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
        progressSSub2.setMax((int)(max_sub2*10));
        seekSSub2.setMax((int)(max_sub2*10));
        progressSSub2.setProgress((int)(sub2*10));
        if (tail_sub2.equals("-")) tail_sub2 = "";
        txtSSub2.setText("+"+formatD(sub2)+tail_sub2+" "+item_sub2);
        setSemiInterface(String.valueOf(txtType.getText()), imgType);

        if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
            ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
        //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
        builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

        Item item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
        item.setCore1(item_core1);
        item.setCore2(item_core2);
        item.setSub1(item_sub1);
        item.setSub2(item_sub2);
        item.setCore1_value(core1);
        item.setCore2_value(core2);
        item.setSub1_value(sub1);
        item.setSub2_value(sub2);
        item.setTalent(item_talent);

        makeNamedDBAdapter.open();
        if (openWeapon) {
            if (!item.getName().equals("보조 붐스틱")) setSecondaryProgess(item_core1, seekWMain1, "weapon_core1", item_type);
            if (!makeNamedDBAdapter.haveNoTalentData(item.getName()) && !item.getType().equals("권총")) setSecondaryProgess(item_core2, seekWMain2, "weapon_core2", item_type);
            setSecondaryProgess(item_sub1, seekWSub, "weapon_sub", item_type);
        } else {
            setSecondaryProgess(item_core1, seekSMain, "sheld_core", item_type);
            if (!makeNamedDBAdapter.haveNoTalentData(item.getName())) setSecondaryProgess(item_sub1, seekSSub1, "sheld_sub1", item_type);
            setSecondaryProgess(item_sub2, seekSSub2, "sheld_sub2", item_type);
        }
        makeNamedDBAdapter.close();

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        final int final_index = index;
        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                materialDbAdapter.open();
                Cursor cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material[count] = cursor.getInt(2);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();

                String str = String.valueOf(makeItems.get(final_index).getType());
                String normal_str = "", rare_str = "", epic_str = "";
                int normal = 0, rare = 0, epic = 0;
                int random_select;

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
                Toast.makeText(getActivity(), normal_str+" +"+normal+", "+rare_str+" +"+rare+", "+epic_str+" +"+epic, Toast.LENGTH_SHORT).show();
            }
        });

        final Item final_item = item;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputItem(final_item);
            }
        });
    }

    private boolean sheldTalent(String type) {
        switch (type) {
            case "마스크": case "장갑": case "권총집": case "무릎보호대":
                return false;
            case "조끼": case "백팩":
                return true;
        }
        return true;
    }

    private void makeSheld(int index) {
        String item_name, item_type, item_talent = "";
        String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
        String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
        double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
        double max_core1, max_core2, max_sub1, max_sub2;
        openWeapon = false;
        openSheld = true;
        View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null); //아이템 드랍할때마다 보여줄 뷰이다.
        TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
        TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
        Button btnChange = dialogView.findViewById(R.id.btnChange); //특급, 네임드일 경우 내용을 바로 보여주지 않고 이 버튼으로 누르면 보여주도록 해준다.
        LinearLayout tableMain = dialogView.findViewById(R.id.tableMain); //내용이 들어있는 테이블 레이아웃

        Button btnExit = dialogView.findViewById(R.id.btnExit); //닫기 버튼\
        Button btnDestroy = dialogView.findViewById(R.id.btnDestroy);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        ImageView imgType = dialogView.findViewById(R.id.imgType);

        TextView txtWMain1 = dialogView.findViewById(R.id.txtWMain1); //첫번째 무기 핵심속성
        TextView txtWMain2 = dialogView.findViewById(R.id.txtWMain2); //두번째 무기 핵심속성
        TextView txtWSub = dialogView.findViewById(R.id.txtWSub); //무기 속성
        ProgressBar progressWMain1 = dialogView.findViewById(R.id.progressWMain1); //첫번째 무기 핵심속성 진행도
        ProgressBar progressWMain2 = dialogView.findViewById(R.id.progressWMain2); //두번재 무기 핵심속성 진행도
        ProgressBar progressWSub = dialogView.findViewById(R.id.progressWSub); //무기 속성 진행도
        TextView txtWTalent = dialogView.findViewById(R.id.txtWTalent); //무기 탤런트

        TextView txtSMain = dialogView.findViewById(R.id.txtSMain); //보호장구 핵심속성
        TextView txtSSub1 = dialogView.findViewById(R.id.txtSSub1); //첫번째 보호장구 속성
        TextView txtSSub2 = dialogView.findViewById(R.id.txtSSub2); //두번째 보호장구 속성
        ProgressBar progressSMain = dialogView.findViewById(R.id.progressSMain); //보호장구 핵심속성 진행도
        ProgressBar progressSSub1 = dialogView.findViewById(R.id.progressSSub1); //첫번째 보호장구 속성 진행도
        ProgressBar progressSSub2 = dialogView.findViewById(R.id.progressSSub2); //두번째 보호장구 속성 진행도
        ImageView imgSMain = dialogView.findViewById(R.id.imgSMain); //보호장구 핵심속성 타입 이미지
        ImageView imgSSub1 = dialogView.findViewById(R.id.imgSSub1); //첫번재 보호장구 속성 타입 이미지
        ImageView imgSSub2 = dialogView.findViewById(R.id.imgSSub2); //두번째 보호장구 속성 타입 이미지
        LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
        LinearLayout layoutTalentButton = dialogView.findViewById(R.id.layoutTalentButton);

        LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
        LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
        LinearLayout layoutSSub2 = dialogView.findViewById(R.id.layoutSSub2);

        TextView txtInventory = dialogView.findViewById(R.id.txtInventory);
        ImageView imgInventory = dialogView.findViewById(R.id.imgInventory);
        LinearLayout layoutInventory = dialogView.findViewById(R.id.layoutInventory);

        TextView txtWTalentContent = dialogView.findViewById(R.id.txtWTalentContent);

        LinearLayout layoutWeaponMain1 = dialogView.findViewById(R.id.layoutWeaponMain1);
        LinearLayout layoutWeaponMain2 = dialogView.findViewById(R.id.layoutWeaponMain2);
        LinearLayout layoutWeaponSub = dialogView.findViewById(R.id.layoutWeaponSub);
        LinearLayout layoutSheldMain = dialogView.findViewById(R.id.layoutSheldMain);
        LinearLayout layoutSheldSub1 = dialogView.findViewById(R.id.layoutSheldSub1);
        LinearLayout layoutSheldSub2 = dialogView.findViewById(R.id.layoutSheldSub2);

        SeekBar seekWMain1 = dialogView.findViewById(R.id.seekWMain1);
        SeekBar seekWMain2 = dialogView.findViewById(R.id.seekWMain2);
        SeekBar seekWSub = dialogView.findViewById(R.id.seekWSub);
        SeekBar seekSMain = dialogView.findViewById(R.id.seekSMain);
        SeekBar seekSSub1 = dialogView.findViewById(R.id.seekSSub1);
        SeekBar seekSSub2 = dialogView.findViewById(R.id.seekSSub2);
        seekWMain1.setEnabled(false);
        seekWMain2.setEnabled(false);
        seekWSub.setEnabled(false);
        seekSMain.setEnabled(false);
        seekSSub1.setEnabled(false);
        seekSSub2.setEnabled(false);

        inventoryDBAdapter.open();
        txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
        inventoryDBAdapter.close();
        imgInventory.setImageResource(R.drawable.inven);
        btnAdd.setVisibility(View.VISIBLE);
        Cursor cursor;
        int pick, temp_percent; //램덤 난수가 저장될 변수
        tableMain.setBackgroundResource(R.drawable.rareitem);
        String temp_option; //옵션 이름
        tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
        btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
        layoutSheld.setVisibility(View.VISIBLE); //보호장구 옵션 레이아웃을 숨긴다.
        layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
        txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
        layoutSheldSub2.setVisibility(View.VISIBLE);

        item_name = makeItems.get(index).getName();
        item_type = makeItems.get(index).getType();
        txtName.setText(item_name);
        txtType.setText(item_type);
        if (sheldTalent(item_type)) {
            layoutTalent.setVisibility(View.VISIBLE);
            if (makeItems.get(index).getGear()) {
                if (item_type.equals("백팩")) {
                    sheldDBAdapter.open();
                    item_talent = sheldDBAdapter.getBackpackTalent(makeItems.get(index).getName());
                    sheldDBAdapter.close();
                    txtWTalent.setText(item_talent);
                    talentDBAdapter.open();
                    txtWTalentContent.setText(transformString(talentDBAdapter.findContent(item_talent)));
                    talentDBAdapter.close();
                } else if (item_type.equals("조끼")) {
                    sheldDBAdapter.open();
                    item_talent = sheldDBAdapter.getVestTalent(makeItems.get(index).getName());
                    sheldDBAdapter.close();
                    txtWTalent.setText(item_talent);
                    talentDBAdapter.open();
                    txtWTalentContent.setText(transformString(talentDBAdapter.findContent(item_talent)));
                    talentDBAdapter.close();
                } else {
                    layoutTalent.setVisibility(View.GONE);
                }
            } else {
                talentDBAdapter.open();
                item_talent = talentDBAdapter.fetchRandomData(item_type);
                txtWTalentContent.setText(transformString(talentDBAdapter.findContent(item_talent)));
                talentDBAdapter.close();
                txtWTalent.setText(item_talent);
            }
        } else layoutTalent.setVisibility(View.GONE);
        String brandset = makeItems.get(index).getAsp();
        maxoptionDBAdapter.open();
        if (brandset.equals("공격")) {
            cursor = maxoptionDBAdapter.fetchSheldCoreData("무기 데미지");
            item_core1 = "무기 데미지";
            max_core1 = Double.parseDouble(cursor.getString(2));
            tail_core1 = cursor.getString(5);
            imgSMain.setImageResource(R.drawable.attack);
            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
            progressSMain.setVisibility(View.VISIBLE);

        } else if (brandset.equals("방어")) {
            cursor = maxoptionDBAdapter.fetchSheldCoreData("방어도");
            item_core1 = "방어도";
            max_core1 = Double.parseDouble(cursor.getString(2));
            tail_core1 = cursor.getString(5);
            imgSMain.setImageResource(R.drawable.sheld);
            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
            progressSMain.setVisibility(View.VISIBLE);

        } else {
            cursor = maxoptionDBAdapter.fetchSheldCoreData("스킬 등급");
            item_core1 = "스킬 등급";
            max_core1 = Double.parseDouble(cursor.getString(2));
            tail_core1 = cursor.getString(5);
            imgSMain.setImageResource(R.drawable.power);
            //progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
            progressSMain.setVisibility(View.GONE);

        }
        maxoptionDBAdapter.close();
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
        else core1 = max_core1;
        if ((int)Math.floor(core1) >= max_core1 && !item_core1.equals("스킬 등급")) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
        progressSMain.setMax((int)(max_core1*10));
        seekSMain.setMax((int)(max_core1*10));
        progressSMain.setProgress((int)(core1*10));
        if (tail_core1.equals("-")) tail_core1 = "";
        txtSMain.setText("+"+formatD(core1)+tail_core1+" "+item_core1);
        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
        maxoptionDBAdapter.open();
        ArrayList<OptionItem> optionItems = maxoptionDBAdapter.fetchOptionItemData("보호장구 부속성");
        for (int i = 0; i < optionItems.size(); i++) {
            if (optionItems.get(i).getContent().equals(item_sub1)) optionItems.remove(i);
        }
        int random_value = percent(0, optionItems.size());
        OptionItem optionItem = optionItems.get(random_value);
        maxoptionDBAdapter.close();
        item_sub1 = optionItem.getContent();
        max_sub1 = optionItem.getValue();
        tail_sub1 = optionItem.getReter();
        if (optionItem.getOption().equals("공격")) {
            imgSSub1.setImageResource(R.drawable.attack_sub);
            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

        } else if (optionItem.getOption().equals("방어")) {
            imgSSub1.setImageResource(R.drawable.sheld_sub);
            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

        } else {
            imgSSub1.setImageResource(R.drawable.power_sub);
            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

        }
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
        progressSSub1.setMax((int)(max_sub1*10));
        seekSSub1.setMax((int)(max_sub1*10));
        progressSSub1.setProgress((int)(sub1*10));
        if (tail_sub1.equals("-")) tail_sub1 = "";
        txtSSub1.setText("+"+formatD(sub1)+tail_sub1+" "+item_sub1);
        if (!makeItems.get(index).getGear()) {
            maxoptionDBAdapter.open();
            optionItems = maxoptionDBAdapter.fetchOptionItemData("보호장구 부속성");
            for (int i = 0; i < optionItems.size(); i++) {
                if (optionItems.get(i).getContent().equals(item_sub1)) optionItems.remove(i);
            }
            random_value = percent(0, optionItems.size());
            optionItem = optionItems.get(random_value);
            maxoptionDBAdapter.close();
            item_sub2 = optionItem.getContent();
            max_sub2 = optionItem.getValue();
            tail_sub2 = optionItem.getReter();
            if (optionItem.getOption().equals("공격")) {
                imgSSub2.setImageResource(R.drawable.attack_sub);
                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

            } else if (optionItem.getOption().equals("방어")) {
                imgSSub2.setImageResource(R.drawable.sheld_sub);
                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

            } else {
                imgSSub2.setImageResource(R.drawable.power_sub);
                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

            }
            pick = percent(1, 100);
            if (pick <= 20) temp_percent = 100;
            else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
            else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
            progressSSub2.setMax((int)(max_sub2*10));
        seekSSub2.setMax((int)(max_sub2*10));
            progressSSub2.setProgress((int)(sub2*10));
            if (tail_sub2.equals("-")) tail_sub2 = "";
            txtSSub2.setText("+"+formatD(sub2)+tail_sub2+" "+item_sub2);
        } else {
            layoutSheldSub2.setVisibility(View.GONE);
            tableMain.setBackgroundResource(R.drawable.gearitem);
            txtName.setTextColor(Color.parseColor("#009900"));
        }
        setSemiInterface(String.valueOf(txtType.getText()), imgType);

        if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
            ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
        //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
        builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

        Item item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
        item.setCore1(item_core1);
        item.setCore2(item_core2);
        item.setSub1(item_sub1);
        item.setSub2(item_sub2);
        item.setCore1_value(core1);
        item.setCore2_value(core2);
        item.setSub1_value(sub1);
        item.setSub2_value(sub2);
        item.setTalent(item_talent);

        namedDBAdapter.open();
        if (openWeapon) {
            if (!item.getName().equals("보조 붐스틱")) setSecondaryProgess(item_core1, seekWMain1, "weapon_core1", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName()) && !item.getType().equals("권총")) setSecondaryProgess(item_core2, seekWMain2, "weapon_core2", item_type);
            setSecondaryProgess(item_sub1, seekWSub, "weapon_sub", item_type);
        } else {
            setSecondaryProgess(item_core1, seekSMain, "sheld_core", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName())) setSecondaryProgess(item_sub1, seekSSub1, "sheld_sub1", item_type);
            if (!makeItems.get(index).getGear()) setSecondaryProgess(item_sub2, seekSSub2, "sheld_sub2", item_type);
        }
        namedDBAdapter.close();

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        final int final_index = index;
        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                materialDbAdapter.open();
                Cursor cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material[count] = cursor.getInt(2);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();

                String str = String.valueOf(makeItems.get(final_index).getType());
                String normal_str = "", rare_str = "", epic_str = "";
                int normal = 0, rare = 0, epic = 0;
                int random_select;

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
                Toast.makeText(getActivity(), normal_str+" +"+normal+", "+rare_str+" +"+rare+", "+epic_str+" +"+epic, Toast.LENGTH_SHORT).show();
            }
        });

        final Item final_item = item;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputItem(final_item);
            }
        });
    }

    private void makeWeapon(int index) {
        String item_name, item_type, item_talent = "";
        String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
        String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
        double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
        double max_core1, max_core2, max_sub1, max_sub2;
        openWeapon = true;
        openSheld = false;
        View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null); //아이템 드랍할때마다 보여줄 뷰이다.
        TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
        TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
        Button btnChange = dialogView.findViewById(R.id.btnChange); //특급, 네임드일 경우 내용을 바로 보여주지 않고 이 버튼으로 누르면 보여주도록 해준다.
        LinearLayout tableMain = dialogView.findViewById(R.id.tableMain); //내용이 들어있는 테이블 레이아웃

        Button btnExit = dialogView.findViewById(R.id.btnExit); //닫기 버튼\
        Button btnDestroy = dialogView.findViewById(R.id.btnDestroy);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        ImageView imgType = dialogView.findViewById(R.id.imgType);

        TextView txtWMain1 = dialogView.findViewById(R.id.txtWMain1); //첫번째 무기 핵심속성
        TextView txtWMain2 = dialogView.findViewById(R.id.txtWMain2); //두번째 무기 핵심속성
        TextView txtWSub = dialogView.findViewById(R.id.txtWSub); //무기 속성
        ProgressBar progressWMain1 = dialogView.findViewById(R.id.progressWMain1); //첫번째 무기 핵심속성 진행도
        ProgressBar progressWMain2 = dialogView.findViewById(R.id.progressWMain2); //두번재 무기 핵심속성 진행도
        ProgressBar progressWSub = dialogView.findViewById(R.id.progressWSub); //무기 속성 진행도
        TextView txtWTalent = dialogView.findViewById(R.id.txtWTalent); //무기 탤런트

        TextView txtSMain = dialogView.findViewById(R.id.txtSMain); //보호장구 핵심속성
        TextView txtSSub1 = dialogView.findViewById(R.id.txtSSub1); //첫번째 보호장구 속성
        TextView txtSSub2 = dialogView.findViewById(R.id.txtSSub2); //두번째 보호장구 속성
        ProgressBar progressSMain = dialogView.findViewById(R.id.progressSMain); //보호장구 핵심속성 진행도
        ProgressBar progressSSub1 = dialogView.findViewById(R.id.progressSSub1); //첫번째 보호장구 속성 진행도
        ProgressBar progressSSub2 = dialogView.findViewById(R.id.progressSSub2); //두번째 보호장구 속성 진행도
        ImageView imgSMain = dialogView.findViewById(R.id.imgSMain); //보호장구 핵심속성 타입 이미지
        ImageView imgSSub1 = dialogView.findViewById(R.id.imgSSub1); //첫번재 보호장구 속성 타입 이미지
        ImageView imgSSub2 = dialogView.findViewById(R.id.imgSSub2); //두번째 보호장구 속성 타입 이미지
        LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
        LinearLayout layoutTalentButton = dialogView.findViewById(R.id.layoutTalentButton);

        LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
        LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
        LinearLayout layoutSSub2 = dialogView.findViewById(R.id.layoutSSub2);

        TextView txtInventory = dialogView.findViewById(R.id.txtInventory);
        ImageView imgInventory = dialogView.findViewById(R.id.imgInventory);
        LinearLayout layoutInventory = dialogView.findViewById(R.id.layoutInventory);

        TextView txtWTalentContent = dialogView.findViewById(R.id.txtWTalentContent);

        LinearLayout layoutWeaponMain1 = dialogView.findViewById(R.id.layoutWeaponMain1);
        LinearLayout layoutWeaponMain2 = dialogView.findViewById(R.id.layoutWeaponMain2);
        LinearLayout layoutWeaponSub = dialogView.findViewById(R.id.layoutWeaponSub);
        LinearLayout layoutSheldMain = dialogView.findViewById(R.id.layoutSheldMain);
        LinearLayout layoutSheldSub1 = dialogView.findViewById(R.id.layoutSheldSub1);
        LinearLayout layoutSheldSub2 = dialogView.findViewById(R.id.layoutSheldSub2);

        SeekBar seekWMain1 = dialogView.findViewById(R.id.seekWMain1);
        SeekBar seekWMain2 = dialogView.findViewById(R.id.seekWMain2);
        SeekBar seekWSub = dialogView.findViewById(R.id.seekWSub);
        SeekBar seekSMain = dialogView.findViewById(R.id.seekSMain);
        SeekBar seekSSub1 = dialogView.findViewById(R.id.seekSSub1);
        SeekBar seekSSub2 = dialogView.findViewById(R.id.seekSSub2);
        seekWMain1.setEnabled(false);
        seekWMain2.setEnabled(false);
        seekWSub.setEnabled(false);
        seekSMain.setEnabled(false);
        seekSSub1.setEnabled(false);
        seekSSub2.setEnabled(false);

        inventoryDBAdapter.open();
        txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
        inventoryDBAdapter.close();
        imgInventory.setImageResource(R.drawable.inven);
        btnAdd.setVisibility(View.VISIBLE);
        Cursor cursor;
        int pick, temp_percent; //램덤 난수가 저장될 변수
        tableMain.setBackgroundResource(R.drawable.rareitem);
        String temp_option; //옵션 이름
        tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
        btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
        layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
        layoutWeapon.setVisibility(View.VISIBLE); //무기 옵션 레이아웃을 숨긴다.
        txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
        layoutSheldSub2.setVisibility(View.VISIBLE);

        item_name = makeItems.get(index).getName();
        item_type = makeItems.get(index).getType();
        txtName.setText(item_name);
        txtType.setText(item_type);
        talentDBAdapter.open();
        item_talent = talentDBAdapter.fetchRandomData(item_type);
        txtWTalentContent.setText(transformString(talentDBAdapter.findContent(item_talent)));
        talentDBAdapter.close();
        txtWTalent.setText(item_talent);
        maxoptionDBAdapter.open();
        cursor = maxoptionDBAdapter.fetchTypeData("무기");
        item_core1 = makeItems.get(index).getType()+" 데미지";
        max_core1 = Double.parseDouble(cursor.getString(2));
        tail_core1 = cursor.getString(5);

        maxoptionDBAdapter.close();
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        if (!item_type.equals("권총")) {
            maxoptionDBAdapter.open();
            cursor = maxoptionDBAdapter.fetchTypeData(makeItems.get(index).getType());
            item_core2 = cursor.getString(1);
            max_core2 = Double.parseDouble(cursor.getString(2));
            tail_core2 = cursor.getString(5);
            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
            max_core2 = Double.parseDouble(cursor.getString(2));
            tail_core2 = cursor.getString(5);
            item_core2 = cursor.getString(1);
            maxoptionDBAdapter.close();
            pick = percent(1, 100);
            if (pick <= 20) temp_percent = 100;
            else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
            else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
            layoutWeaponMain2.setVisibility(View.VISIBLE);
            if (tail_core2.equals("-")) tail_core2 = "";
            txtWMain2.setText("+"+formatD(core2)+tail_core2+" "+item_core2);
            progressWMain2.setMax((int)(max_core2*10));
        seekWMain2.setMax((int)(max_core2*10));
            progressWMain2.setProgress((int)(core2*10));
        } else {
            layoutWeaponMain2.setVisibility(View.GONE);
        }
        maxoptionDBAdapter.open();
        ArrayList<OptionItem> optionItems = maxoptionDBAdapter.fetchOptionItemData("무기 부속성");
        for (int i = 0; i < optionItems.size(); i++) {
            if (optionItems.get(i).getContent().equals(item_core2)) optionItems.remove(i);
        }
        int random_value = percent(0, optionItems.size());
        OptionItem option_item = optionItems.get(random_value);
        item_sub1 = option_item.getContent();

        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
        max_sub1 = Double.parseDouble(cursor.getString(2));
        tail_sub1 = cursor.getString(5);
        maxoptionDBAdapter.close();
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        if (tail_core1.equals("-")) tail_core1 = "";
        txtWMain1.setText("+"+formatD(core1)+tail_core1+" "+item_type+" 데미지");
        progressWMain1.setMax((int)(max_core1*10));
        seekWMain1.setMax((int)(max_core1*10));
        progressWMain1.setProgress((int)(core1*10));
        txtWSub.setText("+"+formatD(sub1)+tail_sub1+" "+item_sub1);
        progressWSub.setMax((int)(max_sub1*10));
        seekWSub.setMax((int)(max_sub1*10));
        progressWSub.setProgress((int)(sub1*10));

        if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
            ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
        //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
        builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

        Item item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
        item.setCore1(item_core1);
        item.setCore2(item_core2);
        item.setSub1(item_sub1);
        item.setSub2(item_sub2);
        item.setCore1_value(core1);
        item.setCore2_value(core2);
        item.setSub1_value(sub1);
        item.setSub2_value(sub2);
        item.setTalent(item_talent);

        namedDBAdapter.open();
        if (openWeapon) {
            if (!item.getName().equals("보조 붐스틱")) setSecondaryProgess(item_core1, seekWMain1, "weapon_core1", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName()) && !item.getType().equals("권총")) setSecondaryProgess(item_core2, seekWMain2, "weapon_core2", item_type);
            setSecondaryProgess(item_sub1, seekWSub, "weapon_sub", item_type);
        } else {
            setSecondaryProgess(item_core1, seekSMain, "sheld_core", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName())) setSecondaryProgess(item_sub1, seekSSub1, "sheld_sub1", item_type);
            setSecondaryProgess(item_sub2, seekSSub2, "sheld_sub2", item_type);
        }
        namedDBAdapter.close();

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        final int final_index = index;
        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                materialDbAdapter.open();
                Cursor cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material[count] = cursor.getInt(2);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();

                String str = String.valueOf(makeItems.get(final_index).getType());
                String normal_str = "", rare_str = "", epic_str = "";
                int normal = 0, rare = 0, epic = 0;
                int random_select;

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
                Toast.makeText(getActivity(), normal_str+" +"+normal+", "+rare_str+" +"+rare+", "+epic_str+" +"+epic, Toast.LENGTH_SHORT).show();
            }
        });

        final Item final_item = item;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputItem(final_item);
            }
        });
    }

    private void makeWeaponNamed(int index) {
        String item_name, item_type, item_talent = "";
        String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
        String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
        double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
        double max_core1, max_core2, max_sub1, max_sub2;
        openWeapon = true;
        openSheld = false;
        View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null); //아이템 드랍할때마다 보여줄 뷰이다.
        TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
        TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
        Button btnChange = dialogView.findViewById(R.id.btnChange); //특급, 네임드일 경우 내용을 바로 보여주지 않고 이 버튼으로 누르면 보여주도록 해준다.
        LinearLayout tableMain = dialogView.findViewById(R.id.tableMain); //내용이 들어있는 테이블 레이아웃

        Button btnExit = dialogView.findViewById(R.id.btnExit); //닫기 버튼\
        Button btnDestroy = dialogView.findViewById(R.id.btnDestroy);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        ImageView imgType = dialogView.findViewById(R.id.imgType);

        TextView txtWMain1 = dialogView.findViewById(R.id.txtWMain1); //첫번째 무기 핵심속성
        TextView txtWMain2 = dialogView.findViewById(R.id.txtWMain2); //두번째 무기 핵심속성
        TextView txtWSub = dialogView.findViewById(R.id.txtWSub); //무기 속성
        ProgressBar progressWMain1 = dialogView.findViewById(R.id.progressWMain1); //첫번째 무기 핵심속성 진행도
        ProgressBar progressWMain2 = dialogView.findViewById(R.id.progressWMain2); //두번재 무기 핵심속성 진행도
        ProgressBar progressWSub = dialogView.findViewById(R.id.progressWSub); //무기 속성 진행도
        TextView txtWTalent = dialogView.findViewById(R.id.txtWTalent); //무기 탤런트

        TextView txtSMain = dialogView.findViewById(R.id.txtSMain); //보호장구 핵심속성
        TextView txtSSub1 = dialogView.findViewById(R.id.txtSSub1); //첫번째 보호장구 속성
        TextView txtSSub2 = dialogView.findViewById(R.id.txtSSub2); //두번째 보호장구 속성
        ProgressBar progressSMain = dialogView.findViewById(R.id.progressSMain); //보호장구 핵심속성 진행도
        ProgressBar progressSSub1 = dialogView.findViewById(R.id.progressSSub1); //첫번째 보호장구 속성 진행도
        ProgressBar progressSSub2 = dialogView.findViewById(R.id.progressSSub2); //두번째 보호장구 속성 진행도
        ImageView imgSMain = dialogView.findViewById(R.id.imgSMain); //보호장구 핵심속성 타입 이미지
        ImageView imgSSub1 = dialogView.findViewById(R.id.imgSSub1); //첫번재 보호장구 속성 타입 이미지
        ImageView imgSSub2 = dialogView.findViewById(R.id.imgSSub2); //두번째 보호장구 속성 타입 이미지
        LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
        LinearLayout layoutTalentButton = dialogView.findViewById(R.id.layoutTalentButton);

        LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
        LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
        LinearLayout layoutSSub2 = dialogView.findViewById(R.id.layoutSSub2);

        TextView txtInventory = dialogView.findViewById(R.id.txtInventory);
        ImageView imgInventory = dialogView.findViewById(R.id.imgInventory);
        LinearLayout layoutInventory = dialogView.findViewById(R.id.layoutInventory);

        TextView txtWTalentContent = dialogView.findViewById(R.id.txtWTalentContent);

        LinearLayout layoutWeaponMain1 = dialogView.findViewById(R.id.layoutWeaponMain1);
        LinearLayout layoutWeaponMain2 = dialogView.findViewById(R.id.layoutWeaponMain2);
        LinearLayout layoutWeaponSub = dialogView.findViewById(R.id.layoutWeaponSub);
        LinearLayout layoutSheldMain = dialogView.findViewById(R.id.layoutSheldMain);
        LinearLayout layoutSheldSub1 = dialogView.findViewById(R.id.layoutSheldSub1);
        LinearLayout layoutSheldSub2 = dialogView.findViewById(R.id.layoutSheldSub2);

        SeekBar seekWMain1 = dialogView.findViewById(R.id.seekWMain1);
        SeekBar seekWMain2 = dialogView.findViewById(R.id.seekWMain2);
        SeekBar seekWSub = dialogView.findViewById(R.id.seekWSub);
        SeekBar seekSMain = dialogView.findViewById(R.id.seekSMain);
        SeekBar seekSSub1 = dialogView.findViewById(R.id.seekSSub1);
        SeekBar seekSSub2 = dialogView.findViewById(R.id.seekSSub2);
        seekWMain1.setEnabled(false);
        seekWMain2.setEnabled(false);
        seekWSub.setEnabled(false);
        seekSMain.setEnabled(false);
        seekSSub1.setEnabled(false);
        seekSSub2.setEnabled(false);

        inventoryDBAdapter.open();
        txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
        inventoryDBAdapter.close();
        imgInventory.setImageResource(R.drawable.inven);
        btnAdd.setVisibility(View.VISIBLE);
        Cursor cursor;
        int pick, temp_percent; //램덤 난수가 저장될 변수
        tableMain.setBackgroundResource(R.drawable.rareitem);
        String temp_option; //옵션 이름
        tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
        btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
        layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
        layoutWeapon.setVisibility(View.VISIBLE); //무기 옵션 레이아웃을 숨긴다.
        txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
        layoutSheldSub2.setVisibility(View.VISIBLE);

        txtName.setTextColor(Color.parseColor("#c99700"));
        item_name = makeItems.get(index).getName();
        item_type = makeItems.get(index).getType();
        txtName.setText(item_name);
        txtType.setText(item_type);
        makeNamedDBAdapter.open();
        if (makeNamedDBAdapter.noTalent(makeItems.get(index).getName())) {
            talentDBAdapter.open();
            item_talent = talentDBAdapter.fetchRandomData(item_type);
            txtWTalentContent.setText(transformString(talentDBAdapter.findContent(item_talent)));
            talentDBAdapter.close();
            txtWTalent.setText(item_talent);
        } else {
            txtWTalent.setTextColor(Color.parseColor("#c99700"));
            cursor = makeNamedDBAdapter.fetchData(makeItems.get(index).getName());
            item_talent = cursor.getString(2);
            txtWTalent.setText(item_talent);
            txtWTalentContent.setText(transformString(cursor.getString(6)));
        }
        makeNamedDBAdapter.close();
        maxoptionDBAdapter.open();
        cursor = maxoptionDBAdapter.fetchTypeData("무기");
        item_core1 = makeItems.get(index).getType()+" 데미지";
        max_core1 = Double.parseDouble(cursor.getString(2));
        tail_core1 = cursor.getString(5);
        maxoptionDBAdapter.close();
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        if (!item_type.equals("권총")) {
            maxoptionDBAdapter.open();
            cursor = maxoptionDBAdapter.fetchTypeData(makeItems.get(index).getType());
            item_core2 = cursor.getString(1);
            max_core2 = Double.parseDouble(cursor.getString(2));
            tail_core2 = cursor.getString(5);
            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
            max_core2 = Double.parseDouble(cursor.getString(2));
            tail_core2 = cursor.getString(5);
            item_core2 = cursor.getString(1);
            maxoptionDBAdapter.close();
            pick = percent(1, 100);
            if (pick <= 20) temp_percent = 100;
            else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
            else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
            layoutWeaponMain2.setVisibility(View.VISIBLE);
            txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
            if (tail_core2.equals("-")) tail_core2 = "";
            txtWMain2.setText("+"+formatD(core2)+tail_core2+" "+item_core2);
            progressWMain2.setMax((int)(max_core2*10));
        seekWMain2.setMax((int)(max_core2*10));
            progressWMain2.setProgress((int)(core2*10));
        } else {
            layoutWeaponMain2.setVisibility(View.GONE);
        }
        maxoptionDBAdapter.open();
        ArrayList<OptionItem> optionItems = maxoptionDBAdapter.fetchOptionItemData("무기 부속성");
        for (int i = 0; i < optionItems.size(); i++) {
            if (optionItems.get(i).getContent().equals(item_core2)) optionItems.remove(i);
        }
        int random_value = percent(0, optionItems.size());
        OptionItem option_item = optionItems.get(random_value);
        item_sub1 = option_item.getContent();

        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
        max_sub1 = Double.parseDouble(cursor.getString(2));
        tail_sub1 = cursor.getString(5);
        maxoptionDBAdapter.close();
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 40) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(21, 40); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
        if (tail_core1.equals("-")) tail_core1 = "";
        txtWMain1.setText("+"+formatD(core1)+tail_core1+" "+item_type+" 데미지");
        progressWMain1.setMax((int)(max_core1*10));
        seekWMain1.setMax((int)(max_core1*10));
        progressWMain1.setProgress((int)(core1*10));
        txtWSub.setText("+"+formatD(sub1)+tail_sub1+" "+item_sub1);
        progressWSub.setMax((int)(max_sub1*10));
        seekWSub.setMax((int)(max_sub1*10));
        progressWSub.setProgress((int)(sub1*10));
        setSemiInterface(String.valueOf(txtType.getText()), imgType);

        if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
            ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
        //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
        builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

        Item item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
        item.setCore1(item_core1);
        item.setCore2(item_core2);
        item.setSub1(item_sub1);
        item.setSub2(item_sub2);
        item.setCore1_value(core1);
        item.setCore2_value(core2);
        item.setSub1_value(sub1);
        item.setSub2_value(sub2);
        item.setTalent(item_talent);

        namedDBAdapter.open();
        if (openWeapon) {
            if (!item.getName().equals("보조 붐스틱")) setSecondaryProgess(item_core1, seekWMain1, "weapon_core1", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName()) && !item.getType().equals("권총")) setSecondaryProgess(item_core2, seekWMain2, "weapon_core2", item_type);
            setSecondaryProgess(item_sub1, seekWSub, "weapon_sub", item_type);
        } else {
            setSecondaryProgess(item_core1, seekSMain, "sheld_core", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName())) setSecondaryProgess(item_sub1, seekSSub1, "sheld_sub1", item_type);
            setSecondaryProgess(item_sub2, seekSSub2, "sheld_sub2", item_type);
        }
        namedDBAdapter.close();

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        final int final_index = index;
        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                materialDbAdapter.open();
                Cursor cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material[count] = cursor.getInt(2);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();

                String str = String.valueOf(makeItems.get(final_index).getType());
                String normal_str = "", rare_str = "", epic_str = "";
                int normal = 0, rare = 0, epic = 0;
                int random_select;

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
                Toast.makeText(getActivity(), normal_str+" +"+normal+", "+rare_str+" +"+rare+", "+epic_str+" +"+epic, Toast.LENGTH_SHORT).show();
            }
        });

        final Item final_item = item;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputItem(final_item);
            }
        });
    }

    private void makeSheldExotic(int index) {
        String item_name, item_type, item_talent = "";
        String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
        String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
        double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
        double max_core1, max_core2, max_sub1, max_sub2;
        openWeapon = false;
        openSheld = true;
        View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null); //아이템 드랍할때마다 보여줄 뷰이다.
        TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
        TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
        Button btnChange = dialogView.findViewById(R.id.btnChange); //특급, 네임드일 경우 내용을 바로 보여주지 않고 이 버튼으로 누르면 보여주도록 해준다.
        LinearLayout tableMain = dialogView.findViewById(R.id.tableMain); //내용이 들어있는 테이블 레이아웃

        Button btnExit = dialogView.findViewById(R.id.btnExit); //닫기 버튼\
        Button btnDestroy = dialogView.findViewById(R.id.btnDestroy);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        ImageView imgType = dialogView.findViewById(R.id.imgType);

        TextView txtWMain1 = dialogView.findViewById(R.id.txtWMain1); //첫번째 무기 핵심속성
        TextView txtWMain2 = dialogView.findViewById(R.id.txtWMain2); //두번째 무기 핵심속성
        TextView txtWSub = dialogView.findViewById(R.id.txtWSub); //무기 속성
        ProgressBar progressWMain1 = dialogView.findViewById(R.id.progressWMain1); //첫번째 무기 핵심속성 진행도
        ProgressBar progressWMain2 = dialogView.findViewById(R.id.progressWMain2); //두번재 무기 핵심속성 진행도
        ProgressBar progressWSub = dialogView.findViewById(R.id.progressWSub); //무기 속성 진행도
        TextView txtWTalent = dialogView.findViewById(R.id.txtWTalent); //무기 탤런트

        TextView txtSMain = dialogView.findViewById(R.id.txtSMain); //보호장구 핵심속성
        TextView txtSSub1 = dialogView.findViewById(R.id.txtSSub1); //첫번째 보호장구 속성
        TextView txtSSub2 = dialogView.findViewById(R.id.txtSSub2); //두번째 보호장구 속성
        ProgressBar progressSMain = dialogView.findViewById(R.id.progressSMain); //보호장구 핵심속성 진행도
        ProgressBar progressSSub1 = dialogView.findViewById(R.id.progressSSub1); //첫번째 보호장구 속성 진행도
        ProgressBar progressSSub2 = dialogView.findViewById(R.id.progressSSub2); //두번째 보호장구 속성 진행도
        ImageView imgSMain = dialogView.findViewById(R.id.imgSMain); //보호장구 핵심속성 타입 이미지
        ImageView imgSSub1 = dialogView.findViewById(R.id.imgSSub1); //첫번재 보호장구 속성 타입 이미지
        ImageView imgSSub2 = dialogView.findViewById(R.id.imgSSub2); //두번째 보호장구 속성 타입 이미지
        LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
        LinearLayout layoutTalentButton = dialogView.findViewById(R.id.layoutTalentButton);

        LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
        LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
        LinearLayout layoutSSub2 = dialogView.findViewById(R.id.layoutSSub2);

        TextView txtInventory = dialogView.findViewById(R.id.txtInventory);
        ImageView imgInventory = dialogView.findViewById(R.id.imgInventory);
        LinearLayout layoutInventory = dialogView.findViewById(R.id.layoutInventory);

        TextView txtWTalentContent = dialogView.findViewById(R.id.txtWTalentContent);

        LinearLayout layoutWeaponMain1 = dialogView.findViewById(R.id.layoutWeaponMain1);
        LinearLayout layoutWeaponMain2 = dialogView.findViewById(R.id.layoutWeaponMain2);
        LinearLayout layoutWeaponSub = dialogView.findViewById(R.id.layoutWeaponSub);
        LinearLayout layoutSheldMain = dialogView.findViewById(R.id.layoutSheldMain);
        LinearLayout layoutSheldSub1 = dialogView.findViewById(R.id.layoutSheldSub1);
        LinearLayout layoutSheldSub2 = dialogView.findViewById(R.id.layoutSheldSub2);

        SeekBar seekWMain1 = dialogView.findViewById(R.id.seekWMain1);
        SeekBar seekWMain2 = dialogView.findViewById(R.id.seekWMain2);
        SeekBar seekWSub = dialogView.findViewById(R.id.seekWSub);
        SeekBar seekSMain = dialogView.findViewById(R.id.seekSMain);
        SeekBar seekSSub1 = dialogView.findViewById(R.id.seekSSub1);
        SeekBar seekSSub2 = dialogView.findViewById(R.id.seekSSub2);
        seekWMain1.setEnabled(false);
        seekWMain2.setEnabled(false);
        seekWSub.setEnabled(false);
        seekSMain.setEnabled(false);
        seekSSub1.setEnabled(false);
        seekSSub2.setEnabled(false);

        inventoryDBAdapter.open();
        txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
        inventoryDBAdapter.close();
        imgInventory.setImageResource(R.drawable.inven);
        btnAdd.setVisibility(View.VISIBLE);
        Cursor cursor;
        int pick, temp_percent; //램덤 난수가 저장될 변수
        tableMain.setBackgroundResource(R.drawable.rareitem);
        String temp_option; //옵션 이름
        tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
        btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
        layoutSheld.setVisibility(View.VISIBLE); //보호장구 옵션 레이아웃을 숨긴다.
        layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
        txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
        layoutSheldSub2.setVisibility(View.VISIBLE);

        tableMain.setBackgroundResource(R.drawable.exoticitem);
        layoutTalent.setVisibility(View.VISIBLE);
        txtName.setTextColor(Color.parseColor("#ff3c00"));
        item_name = makeItems.get(index).getName();
        item_type = makeItems.get(index).getType();
        makeExoticDBAdapter.open();
        cursor = makeExoticDBAdapter.fetchData(makeItems.get(index).getName());
        item_core1 = cursor.getString(3);
        item_sub1 = cursor.getString(4);
        item_sub2 = cursor.getString(5);
        item_core1_type = cursor.getString(6);
        item_sub1_type = cursor.getString(7);
        item_sub2_type = cursor.getString(8);
        item_talent = cursor.getString(10);
        txtWTalentContent.setText(transformString(cursor.getString(11)));
        makeExoticDBAdapter.close();
        txtName.setText(item_name);
        txtType.setText(item_type);
        maxoptionDBAdapter.open();
        cursor = maxoptionDBAdapter.fetchSheldCoreData(item_core1);
        max_core1 = Double.parseDouble(cursor.getString(2));
        tail_core1 = cursor.getString(5);
        if (tail_core1.equals("-")) tail_core1 = "";
        cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub1);
        max_sub1 = Double.parseDouble(cursor.getString(2));
        tail_sub1 = cursor.getString(5);
        if (tail_sub1.equals("-")) tail_sub1 = "";
        cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub2);
        max_sub2 = Double.parseDouble(cursor.getString(2));
        tail_sub2 = cursor.getString(5);
        if (tail_sub2.equals("-")) tail_sub2 = "";
        maxoptionDBAdapter.close();
        progressSMain.setMax((int)(max_core1*10));
        seekSMain.setMax((int)(max_core1*10));
        core1 = max_core1;
        if ((int)Math.floor(core1) >= max_core1 && !item_core1.equals("스킬 등급")) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        progressSMain.setProgress((int)(core1*10));
        txtSMain.setText("+"+(int)core1+tail_core1+" "+item_core1);
        changeImageCoreType(item_core1_type, imgSMain, progressSMain);
        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 60) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(41, 20); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        progressSSub1.setMax((int)(max_sub1*10));
        seekSSub1.setMax((int)(max_sub1*10));
        progressSSub1.setProgress((int)(sub1*10)); //속성1의 진행도 설정
        txtSSub1.setText("+"+formatD(sub1)+tail_sub1+" "+item_sub1);
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 60) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(41, 20); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        progressSSub2.setMax((int)(max_sub2*10));
        seekSSub2.setMax((int)(max_sub2*10));
        progressSSub2.setProgress((int)(sub2*10)); //속성1의 진행도 설정
        txtSSub2.setText("+"+formatD(sub2)+tail_sub2+" "+item_sub2);
        txtWTalent.setText(item_talent);
        setSemiInterface(String.valueOf(txtType.getText()), imgType);

        if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
            ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
        //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
        builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

        Item item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
        item.setCore1(item_core1);
        item.setCore2(item_core2);
        item.setSub1(item_sub1);
        item.setSub2(item_sub2);
        item.setCore1_value(core1);
        item.setCore2_value(core2);
        item.setSub1_value(sub1);
        item.setSub2_value(sub2);
        item.setTalent(item_talent);

        namedDBAdapter.open();
        if (openWeapon) {
            if (!item.getName().equals("보조 붐스틱")) setSecondaryProgess(item_core1, seekWMain1, "weapon_core1", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName()) && !item.getType().equals("권총")) setSecondaryProgess(item_core2, seekWMain2, "weapon_core2", item_type);
            setSecondaryProgess(item_sub1, seekWSub, "weapon_sub", item_type);
        } else {
            setSecondaryProgess(item_core1, seekSMain, "sheld_core", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName())) setSecondaryProgess(item_sub1, seekSSub1, "sheld_sub1", item_type);
            setSecondaryProgess(item_sub2, seekSSub2, "sheld_sub2", item_type);
        }
        namedDBAdapter.close();

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                materialDbAdapter.open();
                Cursor cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material[count] = cursor.getInt(2);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();
                material[9]++;
                if (material[9] >= 20) material[9] = 20;
                materialDbAdapter.open();
                materialDbAdapter.updateMaterial(material_name[9], material[9]);
                materialDbAdapter.close();
                Toast.makeText(getActivity(), "특급 부품을 획득하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        final Item final_item = item;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputItem(final_item);
            }
        });
    }
    
    private void makeWeaponExotic(int index) {
        String item_name, item_type, item_talent = "";
        String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
        String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
        double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
        double max_core1, max_core2, max_sub1, max_sub2;
        openWeapon = true;
        openSheld = false;
        View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null); //아이템 드랍할때마다 보여줄 뷰이다.

        TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
        TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
        Button btnChange = dialogView.findViewById(R.id.btnChange); //특급, 네임드일 경우 내용을 바로 보여주지 않고 이 버튼으로 누르면 보여주도록 해준다.
        LinearLayout tableMain = dialogView.findViewById(R.id.tableMain); //내용이 들어있는 테이블 레이아웃

        Button btnExit = dialogView.findViewById(R.id.btnExit); //닫기 버튼\
        Button btnDestroy = dialogView.findViewById(R.id.btnDestroy);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        ImageView imgType = dialogView.findViewById(R.id.imgType);

        TextView txtWMain1 = dialogView.findViewById(R.id.txtWMain1); //첫번째 무기 핵심속성
        TextView txtWMain2 = dialogView.findViewById(R.id.txtWMain2); //두번째 무기 핵심속성
        TextView txtWSub = dialogView.findViewById(R.id.txtWSub); //무기 속성
        ProgressBar progressWMain1 = dialogView.findViewById(R.id.progressWMain1); //첫번째 무기 핵심속성 진행도
        ProgressBar progressWMain2 = dialogView.findViewById(R.id.progressWMain2); //두번재 무기 핵심속성 진행도
        ProgressBar progressWSub = dialogView.findViewById(R.id.progressWSub); //무기 속성 진행도
        TextView txtWTalent = dialogView.findViewById(R.id.txtWTalent); //무기 탤런트

        TextView txtSMain = dialogView.findViewById(R.id.txtSMain); //보호장구 핵심속성
        TextView txtSSub1 = dialogView.findViewById(R.id.txtSSub1); //첫번째 보호장구 속성
        TextView txtSSub2 = dialogView.findViewById(R.id.txtSSub2); //두번째 보호장구 속성
        ProgressBar progressSMain = dialogView.findViewById(R.id.progressSMain); //보호장구 핵심속성 진행도
        ProgressBar progressSSub1 = dialogView.findViewById(R.id.progressSSub1); //첫번째 보호장구 속성 진행도
        ProgressBar progressSSub2 = dialogView.findViewById(R.id.progressSSub2); //두번째 보호장구 속성 진행도
        ImageView imgSMain = dialogView.findViewById(R.id.imgSMain); //보호장구 핵심속성 타입 이미지
        ImageView imgSSub1 = dialogView.findViewById(R.id.imgSSub1); //첫번재 보호장구 속성 타입 이미지
        ImageView imgSSub2 = dialogView.findViewById(R.id.imgSSub2); //두번째 보호장구 속성 타입 이미지
        LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
        LinearLayout layoutTalentButton = dialogView.findViewById(R.id.layoutTalentButton);

        LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
        LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
        LinearLayout layoutSSub2 = dialogView.findViewById(R.id.layoutSSub2);

        TextView txtInventory = dialogView.findViewById(R.id.txtInventory);
        ImageView imgInventory = dialogView.findViewById(R.id.imgInventory);
        LinearLayout layoutInventory = dialogView.findViewById(R.id.layoutInventory);

        TextView txtWTalentContent = dialogView.findViewById(R.id.txtWTalentContent);

        LinearLayout layoutWeaponMain1 = dialogView.findViewById(R.id.layoutWeaponMain1);
        LinearLayout layoutWeaponMain2 = dialogView.findViewById(R.id.layoutWeaponMain2);
        LinearLayout layoutWeaponSub = dialogView.findViewById(R.id.layoutWeaponSub);
        LinearLayout layoutSheldMain = dialogView.findViewById(R.id.layoutSheldMain);
        LinearLayout layoutSheldSub1 = dialogView.findViewById(R.id.layoutSheldSub1);
        LinearLayout layoutSheldSub2 = dialogView.findViewById(R.id.layoutSheldSub2);

        SeekBar seekWMain1 = dialogView.findViewById(R.id.seekWMain1);
        SeekBar seekWMain2 = dialogView.findViewById(R.id.seekWMain2);
        SeekBar seekWSub = dialogView.findViewById(R.id.seekWSub);
        SeekBar seekSMain = dialogView.findViewById(R.id.seekSMain);
        SeekBar seekSSub1 = dialogView.findViewById(R.id.seekSSub1);
        SeekBar seekSSub2 = dialogView.findViewById(R.id.seekSSub2);
        seekWMain1.setEnabled(false);
        seekWMain2.setEnabled(false);
        seekWSub.setEnabled(false);
        seekSMain.setEnabled(false);
        seekSSub1.setEnabled(false);
        seekSSub2.setEnabled(false);

        inventoryDBAdapter.open();
        txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
        inventoryDBAdapter.close();
        imgInventory.setImageResource(R.drawable.inven);
        btnAdd.setVisibility(View.VISIBLE);
        Cursor cursor;
        int pick, temp_percent; //램덤 난수가 저장될 변수
        tableMain.setBackgroundResource(R.drawable.rareitem);
        String temp_option; //옵션 이름
        tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
        btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
        layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
        layoutWeapon.setVisibility(View.VISIBLE); //무기 옵션 레이아웃을 숨긴다.
        txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
        txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
        txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
        layoutSheldSub2.setVisibility(View.VISIBLE);

        tableMain.setBackgroundResource(R.drawable.exoticitem);
        layoutTalent.setVisibility(View.VISIBLE);
        txtName.setTextColor(Color.parseColor("#ff3c00"));
        item_name = makeItems.get(index).getName();
        item_type = makeItems.get(index).getType();
        makeExoticDBAdapter.open();
        cursor = makeExoticDBAdapter.fetchData(makeItems.get(index).getName());
        item_sub1 = cursor.getString(4);
        item_sub1_type = cursor.getString(7);
        item_talent = cursor.getString(10);
        txtWTalentContent.setText(transformString(cursor.getString(11)));
        makeExoticDBAdapter.close();
        txtName.setText(item_name);
        txtType.setText(item_type);
        item_core1 = item_type+" 데미지";
        txtWTalent.setText(item_talent);
        maxoptionDBAdapter.open();
        cursor = maxoptionDBAdapter.fetchTypeData("무기");
        max_core1 = Double.parseDouble(cursor.getString(2));
        tail_core1 = cursor.getString(5);
        maxoptionDBAdapter.close();
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 60) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(41, 20); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        if (!item_type.equals("권총")) {
            maxoptionDBAdapter.open();
            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
            max_core2 = Double.parseDouble(cursor.getString(2));
            tail_core2 = cursor.getString(5);
            item_core2 = cursor.getString(1);
            maxoptionDBAdapter.close();
            pick = percent(1, 100);
            if (pick <= 20) temp_percent = 100;
            else if (pick <= 60) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
            else temp_percent = percent(41, 20); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
            layoutWeaponMain2.setVisibility(View.VISIBLE);
            if (tail_core2.equals("-")) tail_core2 = "";
            txtWMain2.setText("+"+formatD(core2)+tail_core2+" "+item_core2);
            progressWMain2.setMax((int)(max_core2*10));
        seekWMain2.setMax((int)(max_core2*10));
            progressWMain2.setProgress((int)(core2*10));
        } else {
            layoutWeaponMain2.setVisibility(View.GONE);
        }
        maxoptionDBAdapter.open();
        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
        max_sub1 = Double.parseDouble(cursor.getString(2));
        tail_sub1 = cursor.getString(5);
        maxoptionDBAdapter.close();
        pick = percent(1, 100);
        if (pick <= 20) temp_percent = 100;
        else if (pick <= 60) temp_percent = percent(60, 41); //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
        else temp_percent = percent(41, 20); //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
        if (tail_core1.equals("-")) tail_core1 = "";
        txtWMain1.setText("+"+formatD(core1)+tail_core1+" "+item_type+" 데미지");
        progressWMain1.setMax((int)(max_core1*10));
        seekWMain1.setMax((int)(max_core1*10));
        progressWMain1.setProgress((int)(core1*10));
        if (tail_sub1.equals("-")) tail_sub1 = "";
        txtWSub.setText("+"+formatD(sub1)+tail_sub1+" "+item_sub1);
        progressWSub.setMax((int)(max_sub1*10));
        seekWSub.setMax((int)(max_sub1*10));
        progressWSub.setProgress((int)(sub1*10));
        setSemiInterface(String.valueOf(txtType.getText()), imgType);

        if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
            ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
        //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
        builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

        Item item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
        item.setCore1(item_core1);
        item.setCore2(item_core2);
        item.setSub1(item_sub1);
        item.setSub2(item_sub2);
        item.setCore1_value(core1);
        item.setCore2_value(core2);
        item.setSub1_value(sub1);
        item.setSub2_value(sub2);
        item.setTalent(item_talent);

        namedDBAdapter.open();
        if (openWeapon) {
            if (!item.getName().equals("보조 붐스틱")) setSecondaryProgess(item_core1, seekWMain1, "weapon_core1", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName()) && !item.getType().equals("권총")) setSecondaryProgess(item_core2, seekWMain2, "weapon_core2", item_type);
            setSecondaryProgess(item_sub1, seekWSub, "weapon_sub", item_type);
        } else {
            setSecondaryProgess(item_core1, seekSMain, "sheld_core", item_type);
            if (!namedDBAdapter.haveNoTalentData(item.getName())) setSecondaryProgess(item_sub1, seekSSub1, "sheld_sub1", item_type);
            setSecondaryProgess(item_sub2, seekSSub2, "sheld_sub2", item_type);
        }
        namedDBAdapter.close();

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                materialDbAdapter.open();
                Cursor cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material[count] = cursor.getInt(2);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();
                material[9]++;
                if (material[9] >= 20) material[9] = 20;
                materialDbAdapter.open();
                materialDbAdapter.updateMaterial(material_name[9], material[9]);
                materialDbAdapter.close();
                Toast.makeText(getActivity(), "특급 부품을 획득하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        final Item final_item = item;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputItem(final_item);
            }
        });


    }

    private void inputItem(Item item) {
        sheldDBAdapter.open();
        if (sheldDBAdapter.haveItem(item.getName())) {
            item.setSub2("-");
            item.setSub2_value(0);
        }
        sheldDBAdapter.close();
        inventoryDBAdapter.open();
        if (inventoryDBAdapter.getCount() < 300) {
            switch (item.getType()) {
                case "돌격소총":
                case "소총":
                case "지정사수소총":
                case "기관단총":
                case "산탄총":
                case "경기관총":
                case "권총":
                    inventoryDBAdapter.insertWeaponData(item.getName(), item.getType(), item.getCore1(), item.getCore2(), item.getSub1(), item.getCore1_value(), item.getCore2_value(), item.getSub1_value(), item.getTalent());
                    break;
                case "마스크":
                case "백팩":
                case "조끼":
                case "장갑":
                case "권총집":
                case "무릎보호대":
                    inventoryDBAdapter.insertSheldData(item.getName(), item.getType(), item.getCore1(), item.getSub1(), item.getSub2(), item.getCore1_value(), item.getSub1_value(), item.getSub2_value(), item.getTalent());
                    break;
            }
            Toast.makeText(getActivity(), item.getName()+"("+item.getType()+")을 인벤토리에 추가하였습니다.", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        } else Toast.makeText(getActivity(), "인벤토리가 가득찼습니다.", Toast.LENGTH_SHORT).show();
        inventoryDBAdapter.close();
    }

    private void changeImageType(String type, ImageView view, ProgressBar progress) {
        if (type.equals("공격")) {
            view.setImageResource(R.drawable.attack);
            progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
        } else if (type.equals("방어")) {
            view.setImageResource(R.drawable.sheld);
            progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
        } else {
            view.setImageResource(R.drawable.power);
            progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
        }
    }

    private void changeImageCoreType(String type, ImageView view, ProgressBar progress) {
        if (type.equals("공격")) {
            view.setImageResource(R.drawable.attack);
            progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
            progress.setVisibility(View.VISIBLE);
        } else if (type.equals("방어")) {
            view.setImageResource(R.drawable.sheld);
            progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
            progress.setVisibility(View.VISIBLE);
        } else {
            view.setImageResource(R.drawable.power);
            //progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
            progress.setVisibility(View.GONE);
        }
    }

    private boolean isTypeWeapon(String type) {
        for (int i = 0; i < weapontypes.length; i++) if (type.equals(weapontypes[i])) return true;
        return false;
    }

    private boolean isTypeSheld(String type) {
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

    private boolean isWeapon(String type) {
        switch (type) {
            case "돌격소총":
            case "소총":
            case "지정사수소총":
            case "산탄총":
            case "기관단총":
            case "경기관총":
            case "권총":
                return true;
        }
        return false;
    }

    private boolean isSheldAType(String type) {
        switch (type) {
            case "마스크":
            case "조끼":
            case "백팩":
                return true;
        }
        return false;
    }

    private boolean isSheldBType(String type) {
        switch (type) {
            case "권총집":
            case "장갑":
            case "무릎보호대":
                return true;
        }
        return false;
    }

    private void sheldInterface() {
        makeItems.clear();
        makeNamedDBAdapter.open();
        cursor = makeNamedDBAdapter.fetchTypeData(sheldtypes[0]);
        makeNamedDBAdapter.close();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            String asp = cursor.getString(4);
            MakeItem item = new MakeItem(name, sheldtypes[0]);
            item.setAsp(asp);
            makeItems.add(item);
            cursor.moveToNext();
        }
        makeSheldDBAdapter.open();
        cursor = makeSheldDBAdapter.fetchTypeData(sheldtypes[0]);
        makeSheldDBAdapter.close();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            int gear = cursor.getInt(3);
            String asp = cursor.getString(4);
            boolean isGear = false;
            if (gear == 1) isGear = true;
            MakeItem item = new MakeItem(name, sheldtypes[0]);
            item.setAsp(asp);
            item.setGear(isGear);
            makeItems.add(item);
            cursor.moveToNext();
        }
        makeAdapter = new MakeAdapter(getActivity(), makeItems, false);
        listSheld.setAdapter(makeAdapter);
    }

    private SpannableString transformString(String content) {
        SpannableString spannableString = new SpannableString(content);
        String word;
        int start, end;
        int find_index = 0;
        String[] changes = {"+", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "%", "m", "초", "번", "개", "명", "배", "배율", "발", "."};
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
        Cursor cursor;
        double max = 0;
        libraryDBAdapter.open();
        switch (option_type) {
            case "weapon_core1":
                cursor = libraryDBAdapter.fetchTypeData("무기");
                break;
            case "weapon_core2":
                cursor = libraryDBAdapter.fetchTypeData(type);
                break;
            case "weapon_sub":
                cursor = libraryDBAdapter.fetchSubData(name);
                break;
            case "sheld_core":
                cursor = libraryDBAdapter.fetchSheldCoreData(name);
                break;
            case "sheld_sub1":
            case "sheld_sub2":
                cursor = libraryDBAdapter.fetchSheldSubData(name);
                break;
            default:
                cursor = libraryDBAdapter.fetchTypeData("무기");
        }
        libraryDBAdapter.close();
        max = Double.parseDouble(cursor.getString(2));
        seekbar.setProgress((int)(max*10));
        if (seekbar.getProgress() >= seekbar.getMax()) seekbar.setThumb(getResources().getDrawable(R.drawable.ic_max_second_40dp));
        else seekbar.setThumb(getResources().getDrawable(R.drawable.ic_second_40dp));
    }

    private void setSemiInterface(String type_name, ImageView view) { //무기 종류에 따라 갯수를 표시한다. 진행도 또한 설정한다.
        ImageView temp = view;
        switch (type_name) {
            case "돌격소총":
                temp.setImageResource(R.drawable.wp1custom);
                break;
            case "소총":
                temp.setImageResource(R.drawable.wp2custom);
                break;
            case "지정사수소총":
                temp.setImageResource(R.drawable.wp3custom);
                break;
            case "기관단총":
                temp.setImageResource(R.drawable.wp4custom);
                break;
            case "경기관총":
                temp.setImageResource(R.drawable.wp5custom);
                break;
            case "산탄총":
                temp.setImageResource(R.drawable.wp6custom);
                break;
            case "권총":
                temp.setImageResource(R.drawable.wp7custom);
                break;
            case "마스크":
                temp.setImageResource(R.drawable.sd1custom);
                break;
            case "백팩":
                temp.setImageResource(R.drawable.sd4custom);
                break;
            case "조끼":
                temp.setImageResource(R.drawable.sd2custom);
                break;
            case "장갑":
                temp.setImageResource(R.drawable.sd5custom);
                break;
            case "권총집":
                temp.setImageResource(R.drawable.sd3custom);
                break;
            case "무릎보호대":
                temp.setImageResource(R.drawable.sd6custom);
                break;
        }
    }

    private void weaponInterface() {
        makeItems.clear();
        makeNamedDBAdapter.open();
        cursor = makeNamedDBAdapter.fetchTypeData(weapontypes[0]);
        makeNamedDBAdapter.close();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            MakeItem item = new MakeItem(name, weapontypes[0]);
            makeItems.add(item);
            cursor.moveToNext();
        }
        makeWeaponDBAdapter.open();
        cursor = makeWeaponDBAdapter.fetchTypeData(weapontypes[0]);
        makeWeaponDBAdapter.close();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            MakeItem item = new MakeItem(name, weapontypes[0]);
            makeItems.add(item);
            cursor.moveToNext();
        }
        makeAdapter = new MakeAdapter(getActivity(), makeItems, false);
        listWeapon.setAdapter(makeAdapter);
    }

    private void copyExcelDataToDatabase(int[] images) {
        Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

        Workbook workbook = null;
        Sheet sheet = null;

        try {
            InputStream is = getActivity().getBaseContext().getResources().getAssets().open("sheld.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = 9;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(1).length - 1;

                    sheldItemDBAdapter.open();
                    sheldItemDBAdapter.databaseReset();

                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String first = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String second = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                        String third = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                        String core = sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                        String sub = sheet.getCell(nColumnStartIndex+5, nRow).getContents();
                        String type = sheet.getCell(nColumnStartIndex+6, nRow).getContents();
                        String vest = sheet.getCell(nColumnStartIndex+7, nRow).getContents();
                        String backpack = sheet.getCell(nColumnStartIndex+8, nRow).getContents();
                        String image = Integer.toString(images[nRow]);

                        sheldItemDBAdapter.createWeapon(name, first, second, third, core, sub, type, vest, backpack, image);
                    }

                    sheldItemDBAdapter.close();
                    //Toast.makeText(getApplicationContext(), "불러오기 성공", Toast.LENGTH_SHORT).show();
                } else System.out.println("Sheet is null!!!");
            } else System.out.println("WorkBook is null!!!");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "불러오기 오류", Toast.LENGTH_SHORT).show();
        } finally {
            if (workbook != null) workbook.close();
        }
    }

    private String formatD(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }

    public int percent(int min, int length) {
        return (int)(Math.random()*1234567)%length + min;
    }
}