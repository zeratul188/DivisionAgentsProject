package com.example.divisionsimulation.ui.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private RadioGroup rgWeapon;
    private RadioButton[] rdoWeapon = new RadioButton[8];
    private ListView listView;
    private Button btnNamed, btnTalent;

    private WeaponAdapter weaponAdapter;
    private ArrayList<WeaponItem> weaponItems;
    private WeaponDbAdapter weaponDbAdapter;
    private WeaponExoticDbAdapter weaponExoticDbAdapter;

    private String[] types = {"돌격소총", "소총", "기관단총", "경기관총", "지정사수소총", "산탄총", "권총"};
    private Cursor cursor = null;
    private boolean exoticed = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        rgWeapon = root.findViewById(R.id.rgWeapon);
        listView = root.findViewById(R.id.listView);
        btnNamed = root.findViewById(R.id.btnNamed);
        btnTalent = root.findViewById(R.id.btnTalent);

        int resource;
        for (int i = 0; i < rdoWeapon.length; i++) {
            resource = root.getResources().getIdentifier("rdoWeapon"+(i+1), "id", getActivity().getPackageName());
            rdoWeapon[i] = root.findViewById(resource);
        }

        weaponItems = new ArrayList<WeaponItem>();
        weaponDbAdapter = new WeaponDbAdapter(getActivity());
        weaponExoticDbAdapter = new WeaponExoticDbAdapter(getActivity());

        weaponDbAdapter.open();
        cursor = weaponDbAdapter.fetchTypeWeapon(types[0]);
        weaponDbAdapter.close();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            String type = cursor.getString(9);
            WeaponItem item = new WeaponItem(name, type);
            weaponItems.add(item);
            cursor.moveToNext();
        }
        weaponAdapter = new WeaponAdapter(getActivity(), weaponItems, false);
        listView.setAdapter(weaponAdapter);

        rgWeapon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                weaponItems.clear();
                for (int i = 0; i < rdoWeapon.length; i++) {
                    if (rdoWeapon[i].isChecked()) {
                        rdoWeapon[i].setTextColor(Color.parseColor("#FF6337"));
                        if (i == 7) {
                            exoticed = true;
                            weaponExoticDbAdapter.open();
                            cursor = weaponExoticDbAdapter.fetchAllWeapon();
                            if (cursor != null) cursor.moveToFirst();
                            weaponExoticDbAdapter.close();
                            while (!cursor.isAfterLast()) {
                                String name = cursor.getString(1);
                                String type = cursor.getString(12);
                                WeaponItem item = new WeaponItem(name, type);
                                weaponItems.add(item);
                                cursor.moveToNext();
                            }
                            weaponAdapter = new WeaponAdapter(getActivity(), weaponItems, true);
                            listView.setAdapter(weaponAdapter);
                        } else {
                            exoticed = false;
                            weaponDbAdapter.open();
                            cursor = weaponDbAdapter.fetchTypeWeapon(types[i]);
                            weaponDbAdapter.close();
                            while (!cursor.isAfterLast()) {
                                String name = cursor.getString(1);
                                String type = cursor.getString(9);
                                WeaponItem item = new WeaponItem(name, type);
                                weaponItems.add(item);
                                cursor.moveToNext();
                            }
                            weaponAdapter = new WeaponAdapter(getActivity(), weaponItems, false);
                            listView.setAdapter(weaponAdapter);
                        }
                    } else {
                        rdoWeapon[i].setTextColor(Color.parseColor("#F0F0F0"));
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                if (exoticed) {
                    intent = new Intent(getActivity(), WeaponExoticListActivity.class); //intent에 현재 화면에 Weapon1Activity를 새로운 화면에 출력시키는 변수다.
                } else {
                    intent = new Intent(getActivity(), WeaponListActivity.class); //intent에 현재 화면에 Weapon1Activity를 새로운 화면에 출력시키는 변수다.
                }
                intent.putExtra("Name", weaponItems.get(position).getName());
                startActivity(intent); //intent 액티비티를 시작시킨다.
            }
        });

        btnNamed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Weapon9Activity.class);
                startActivity(intent);
            }
        });

        btnTalent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Weapon10Activity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}