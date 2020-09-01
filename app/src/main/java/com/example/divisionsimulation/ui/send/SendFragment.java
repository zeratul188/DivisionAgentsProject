package com.example.divisionsimulation.ui.send;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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

    private ArrayList<MakeItem> makeItems;
    private MakeAdapter makeAdapter;
    private Cursor cursor;
    private String[] weapontypes = {"돌격소총", "소총", "기관단총", "경기관총", "지정사수소총", "산탄총", "권총"};
    private String[] sheldtypes = {"마스크", "조끼", "권총집", "백팩", "장갑", "무릎보호대"};

    private MakeExoticDBAdapter makeExoticDBAdapter;
    private MakeNamedDBAdapter makeNamedDBAdapter;
    private MakeSheldDBAdapter makeSheldDBAdapter;
    private MakeWeaponDBAdapter makeWeaponDBAdapter;

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

        return root;
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