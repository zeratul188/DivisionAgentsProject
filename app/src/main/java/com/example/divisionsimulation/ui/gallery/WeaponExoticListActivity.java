package com.example.divisionsimulation.ui.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class WeaponExoticListActivity extends AppCompatActivity {
    private WeaponExoticDbAdapter exoticAdapter;
    private String name, option, rpm, reloadtime, mag, firemethod, item, talent, talentcontent, droped, content, type;

    private int[] img_resource = {R.drawable.wp1custom, R.drawable.wp2custom, R.drawable.wp3custom, R.drawable.wp4custom, R.drawable.wp5custom, R.drawable.wp6custom, R.drawable.wp7custom};
    private String[] types = {"돌격소총", "소총", "지정사수소총", "기관단총", "경기관총", "산탄총", "권총"};

    private ImageView imgWeaponType;
    private TextView txtName, txtOption, txtRPM, txtReloadTime, txtMAG, txtFireMethod, txtItem, txtTalent, txtTalentContent, txtDroped, txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weaponexoticlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgWeaponType = findViewById(R.id.imgWeaponType);
        txtName = findViewById(R.id.txtName);
        txtRPM = findViewById(R.id.txtRPM);
        txtMAG = findViewById(R.id.txtMAG);
        txtReloadTime = findViewById(R.id.txtReloadTime);
        txtFireMethod = findViewById(R.id.txtFireMethod);
        txtContent = findViewById(R.id.txtContent);
        txtOption = findViewById(R.id.txtOption);
        txtItem = findViewById(R.id.txtItem);
        txtTalent = findViewById(R.id.txtTalent);
        txtTalentContent = findViewById(R.id.txtTalentContent);
        txtDroped = findViewById(R.id.txtDroped);

        Intent intent = getIntent();
        name = intent.getStringExtra("Name");

        exoticAdapter = new WeaponExoticDbAdapter(this);

        setTitle(name);

        exoticAdapter.open();

        Cursor cursor = exoticAdapter.fetchNameWeapon(name);
        option =cursor.getString(2);
        rpm = cursor.getString(3);
        reloadtime = cursor.getString(4);
        mag = cursor.getString(5);
        firemethod = cursor.getString(6);
        item = cursor.getString(7);
        talent = cursor.getString(8);
        talentcontent = cursor.getString(9);
        droped = cursor.getString(10);
        content = cursor.getString(11);
        type = cursor.getString(12);

        imgWeaponType.setImageResource(setImageResource(type));

        txtName.setText(name);
        txtOption.setText(option);
        txtRPM.setText(rpm);
        txtReloadTime.setText(reloadtime);
        txtMAG.setText(mag);
        txtFireMethod.setText(firemethod);
        txtItem.setText(item);
        txtTalent.setText(talent);
        txtTalentContent.setText(talentcontent);
        txtDroped.setText(droped);
        txtContent.setText(content);

        exoticAdapter.close();
    }

    private int setImageResource(String type) {
        for (int i = 0; i < types.length; i++) if (type.equals(types[i])) return img_resource[i];
        return img_resource[0];
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
