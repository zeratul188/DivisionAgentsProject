package com.example.divisionsimulation.ui.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

import java.io.Serializable;

public class WeaponListActivity extends AppCompatActivity {
    private WeaponDbAdapter weaponAdapter;
    private String name, demage, rpm, mag, reload_time, fire_method, mode, variation, type, content;

    private int[] img_resource = {R.drawable.wp1custom, R.drawable.wp2custom, R.drawable.wp3custom, R.drawable.wp4custom, R.drawable.wp5custom, R.drawable.wp6custom, R.drawable.wp7custom};
    private String[] types = {"돌격소총", "소총", "지정사수소총", "기관단총", "경기관총", "산탄총", "권총"};

    private ImageView imgWeaponType;
    private TextView txtName, txtDemage, txtRPM, txtMAG, txtReloadTime, txtFireMethod, txtMode, txtVariation, txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weaponlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgWeaponType = findViewById(R.id.imgWeaponType);
        txtName = findViewById(R.id.txtName);
        txtDemage = findViewById(R.id.txtDemage);
        txtRPM = findViewById(R.id.txtRPM);
        txtMAG = findViewById(R.id.txtMAG);
        txtReloadTime = findViewById(R.id.txtReloadTime);
        txtFireMethod = findViewById(R.id.txtFireMethod);
        txtMode = findViewById(R.id.txtMode);
        txtVariation = findViewById(R.id.txtVariation);
        txtContent = findViewById(R.id.txtContent);

        Intent intent = getIntent();
        name = intent.getStringExtra("Name");

        weaponAdapter = new WeaponDbAdapter(this);

        setTitle(name);

        weaponAdapter.open();

        Cursor cursor = weaponAdapter.fetchNameWeapon(name);
        demage =cursor.getString(2);
        rpm = cursor.getString(3);
        mag = cursor.getString(4);
        reload_time = cursor.getString(5);
        fire_method = cursor.getString(6);
        mode = cursor.getString(7);
        variation = cursor.getString(8);
        type = cursor.getString(9);
        content = cursor.getString(10);

        imgWeaponType.setImageResource(setImageResource(type));

        txtName.setText(name);
        txtDemage.setText(demage);
        txtRPM.setText(rpm);
        txtMAG.setText(mag);
        txtReloadTime.setText(reload_time);
        txtFireMethod.setText(fire_method);
        txtMode.setText(mode);
        txtVariation.setText(variation);
        txtContent.setText(content);

        weaponAdapter.close();
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
