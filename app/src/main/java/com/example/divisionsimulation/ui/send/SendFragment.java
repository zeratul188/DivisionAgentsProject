package com.example.divisionsimulation.ui.send;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.MaterialDbAdapter;
import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.MakeExoticDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeNamedDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeSheldDBAdapter;
import com.example.divisionsimulation.dbdatas.MakeWeaponDBAdapter;

import java.util.ArrayList;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;

    private ListView listWeapon, listSheld, listExotic;
    private RadioGroup rgWeapon, rgSheld, rgType;
    //private RadioButton rdoWeaponType, rdoSheldType, rdoExoticType;
    private RadioButton[] rdoWeapon = new RadioButton[7];
    private RadioButton[] rdoSheld = new RadioButton[6];
    private LinearLayout layoutWeapon, layoutSheld, layoutExotic;
    private Button btnMaterialList;

    private ArrayList<MakeItem> makeItems;
    private MakeAdapter makeAdapter;
    private Cursor cursor;
    private String[] weapontypes = {"돌격소총", "소총", "기관단총", "경기관총", "지정사수소총", "산탄총", "권총"};
    private String[] sheldtypes = {"마스크", "조끼", "권총집", "백팩", "장갑", "무릎보호대"};
    private int[] sheldresource = {R.drawable.rdoeq1custom, R.drawable.rdoeq2custom, R.drawable.rdoeq3custom, R.drawable.rdoeq4custom, R.drawable.rdoeq5custom, R.drawable.rdoeq6custom};
    private int[] weaponresource = {R.drawable.rdowp1custom, R.drawable.rdowp2custom, R.drawable.rdowp3custom, R.drawable.rdowp4custom, R.drawable.rdowp5custom, R.drawable.rdowp6custom, R.drawable.rdowp7custom};

    private MakeExoticDBAdapter makeExoticDBAdapter;
    private MakeNamedDBAdapter makeNamedDBAdapter;
    private MakeSheldDBAdapter makeSheldDBAdapter;
    private MakeWeaponDBAdapter makeWeaponDBAdapter;
    private MaterialDbAdapter materialDbAdapter;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

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
        makeItems = new ArrayList<MakeItem>();

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
                        layoutWeapon.setVisibility(View.VISIBLE);
                        layoutSheld.setVisibility(View.GONE);
                        layoutExotic.setVisibility(View.GONE);
                        rdoWeapon[0].setChecked(true);
                        weaponInterface();
                        break;
                    case R.id.rdoSheldType:
                        layoutWeapon.setVisibility(View.GONE);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutExotic.setVisibility(View.GONE);
                        rdoSheld[0].setChecked(true);
                        sheldInterface();
                        break;
                    case R.id.rdoExoticType:
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
                            MakeItem item = new MakeItem(name, sheldtypes[i]);
                            makeItems.add(item);
                            cursor.moveToNext();
                        }
                        makeSheldDBAdapter.open();
                        cursor = makeSheldDBAdapter.fetchTypeData(sheldtypes[i]);
                        makeSheldDBAdapter.close();
                        while (!cursor.isAfterLast()) {
                            String name = cursor.getString(1);
                            int gear = cursor.getInt(3);
                            boolean isGear = false;
                            if (gear == 1) isGear = true;
                            MakeItem item = new MakeItem(name, sheldtypes[i]);
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

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                String[] material_limit;
                if (isWeapon(makeItems.get(position).getType())) {
                    material_limit = new String[]{"총몸부품", "강철", "탄소섬유"};
                } else if (isSheldAType(makeItems.get(position).getType())) {
                    material_limit = new String[]{"보호용 옷감", "세라믹", "전자부품"};
                } else {
                    material_limit = new String[]{"보호용 옷감", "폴리카보네이트", "티타늄"};
                }
                int[] need_material = {60, 36, 36};

                txtName.setText(makeItems.get(position).getName());
                txtType.setText(makeItems.get(position).getType());
                if (isTypeWeapon(makeItems.get(position).getType())) {
                    imgType.setImageResource(setWeaponImageResource(makeItems.get(position).getType()));
                } else {
                    imgType.setImageResource(setSheldImageResource(makeItems.get(position).getType()));
                }

                txtNormal.setText(material_limit[0]);
                txtRare.setText(material_limit[1]);
                txtEpic.setText(material_limit[2]);
                materialDbAdapter.open();
                txtNowNormal.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[0])));
                txtNowRare.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[1])));
                txtNowEpic.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[2])));
                materialDbAdapter.close();
                txtNeedNormal.setText(Integer.toString(need_material[0]));
                txtNeedRare.setText(Integer.toString(need_material[1]));
                txtNeedEpic.setText(Integer.toString(need_material[2]));

                makeExoticDBAdapter.open();
                makeNamedDBAdapter.open();
                if (makeNamedDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#B18912"));
                } else if (makeExoticDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#fe6e0e"));
                } else if (makeItems.get(position).getGear()) {
                    txtName.setTextColor(Color.parseColor("#2BBE2B"));
                } else {
                    txtName.setTextColor(Color.parseColor("#AAAAAA"));
                }
                makeNamedDBAdapter.close();
                makeExoticDBAdapter.close();

                btnMake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        listSheld.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                String[] material_limit;
                if (isWeapon(makeItems.get(position).getType())) {
                    material_limit = new String[]{"총몸부품", "강철", "탄소섬유"};
                } else if (isSheldAType(makeItems.get(position).getType())) {
                    material_limit = new String[]{"보호용 옷감", "세라믹", "전자부품"};
                } else {
                    material_limit = new String[]{"보호용 옷감", "폴리카보네이트", "티타늄"};
                }
                int[] need_material = {60, 36, 36};

                txtName.setText(makeItems.get(position).getName());
                txtType.setText(makeItems.get(position).getType());
                if (isTypeWeapon(makeItems.get(position).getType())) {
                    imgType.setImageResource(setWeaponImageResource(makeItems.get(position).getType()));
                } else {
                    imgType.setImageResource(setSheldImageResource(makeItems.get(position).getType()));
                }

                txtNormal.setText(material_limit[0]);
                txtRare.setText(material_limit[1]);
                txtEpic.setText(material_limit[2]);
                materialDbAdapter.open();
                txtNowNormal.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[0])));
                txtNowRare.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[1])));
                txtNowEpic.setText(Integer.toString(materialDbAdapter.getMaterial(material_limit[2])));
                materialDbAdapter.close();
                txtNeedNormal.setText(Integer.toString(need_material[0]));
                txtNeedRare.setText(Integer.toString(need_material[1]));
                txtNeedEpic.setText(Integer.toString(need_material[2]));

                makeExoticDBAdapter.open();
                makeNamedDBAdapter.open();
                if (makeNamedDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#B18912"));
                } else if (makeExoticDBAdapter.haveItem(makeItems.get(position).getName())) {
                    txtName.setTextColor(Color.parseColor("#fe6e0e"));
                } else if (makeItems.get(position).getGear()) {
                    txtName.setTextColor(Color.parseColor("#2BBE2B"));
                } else {
                    txtName.setTextColor(Color.parseColor("#AAAAAA"));
                }
                makeNamedDBAdapter.close();
                makeExoticDBAdapter.close();

                btnMake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        listExotic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                String[] material_limit;
                if (isWeapon(makeItems.get(position).getType())) {
                    material_limit = new String[]{"총몸부품", "강철", "탄소섬유", "특급 부품"};
                } else if (isSheldAType(makeItems.get(position).getType())) {
                    material_limit = new String[]{"보호용 옷감", "세라믹", "전자부품", "특급 부품"};
                } else {
                    material_limit = new String[]{"보호용 옷감", "폴리카보네이트", "티타늄", "특급 부품"};
                }
                int[] need_material = {60, 36, 36, 2};

                txtName.setText(makeItems.get(position).getName());
                txtType.setText(makeItems.get(position).getType());
                if (isTypeWeapon(makeItems.get(position).getType())) {
                    imgType.setImageResource(setWeaponImageResource(makeItems.get(position).getType()));
                } else {
                    imgType.setImageResource(setSheldImageResource(makeItems.get(position).getType()));
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
                    txtName.setTextColor(Color.parseColor("#AAAAAA"));
                }
                makeNamedDBAdapter.close();
                makeExoticDBAdapter.close();

                btnMake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        return root;
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
            MakeItem item = new MakeItem(name, sheldtypes[0]);
            makeItems.add(item);
            cursor.moveToNext();
        }
        makeSheldDBAdapter.open();
        cursor = makeSheldDBAdapter.fetchTypeData(sheldtypes[0]);
        makeSheldDBAdapter.close();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            int gear = cursor.getInt(3);
            boolean isGear = false;
            if (gear == 1) isGear = true;
            MakeItem item = new MakeItem(name, sheldtypes[0]);
            item.setGear(isGear);
            makeItems.add(item);
            cursor.moveToNext();
        }
        makeAdapter = new MakeAdapter(getActivity(), makeItems, false);
        listSheld.setAdapter(makeAdapter);
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
}