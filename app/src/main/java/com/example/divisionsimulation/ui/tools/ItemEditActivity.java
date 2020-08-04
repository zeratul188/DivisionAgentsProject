package com.example.divisionsimulation.ui.tools;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;

import java.util.ArrayList;

public class ItemEditActivity extends AppCompatActivity {

    private LinearLayout layoutWeapon, layoutSheld;
    private TextView txtWeaponOption, txtSheldOption;
    private ProgressBar progressWeaponOption, progressSheldOption;
    private ImageView imgSheldOption;
    private ListView listView;
    private Button btnRessetting, btnCancel;

    private EditAdapter editAdapter;
    private boolean exoticed = false;
    private String name, type, option_type;
    private double value;
    private long rowID;
    private Cursor cursor;
    private ArrayList<EditItem> editItems;

    private MaxOptionsFMDBAdapter maxDBAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemeditlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutWeapon = findViewById(R.id.layoutWeapon);
        layoutSheld = findViewById(R.id.layoutSheld);
        txtWeaponOption = findViewById(R.id.txtWeaponOption);
        txtSheldOption = findViewById(R.id.txtSheldOption);
        progressWeaponOption = findViewById(R.id.progressWeaponOption);
        progressSheldOption = findViewById(R.id.progressSheldOption);
        imgSheldOption = findViewById(R.id.imgSheldOption);
        btnRessetting = findViewById(R.id.btnResetting);
        btnCancel = findViewById(R.id.btnCancel);
        listView = findViewById(R.id.listView);

        exoticed = getIntent().getBooleanExtra("exoticed", false);
        name = getIntent().getStringExtra("name");
        value = getIntent().getDoubleExtra("value", 0);
        type = getIntent().getStringExtra("type");
        option_type = getIntent().getStringExtra("option_type");
        rowID = getIntent().getLongExtra("itemID", 9999);

        maxDBAdapter = new MaxOptionsFMDBAdapter(this);

        if (exoticed) {
            listView.setVisibility(View.INVISIBLE);
            btnRessetting.setVisibility(View.VISIBLE);
        } else {
            maxDBAdapter.open();
            switch (option_type) {
                case "weapon_core1":
                    cursor = maxDBAdapter.fetchTypeData("무기");
                    break;
                case "weapon_core2":
                    cursor = maxDBAdapter.fetchTypeData(type);
                    break;
                case "weapon_sub":
                    cursor = maxDBAdapter.fetchTypeData("무기 부속성");
                    break;
                case "sheld_core":
                    cursor = maxDBAdapter.fetchTypeData("보호장구 핵심속성");
                    break;
                case "sheld_sub1":
                case "sheld_sub2":
                    cursor = maxDBAdapter.fetchTypeData("보호장구 부속성");
                    break;
            }
            while (!cursor.isAfterLast()) {
                EditItem item = new EditItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                editItems.add(item);
                cursor.moveToNext();
            }
            maxDBAdapter.close();
            editAdapter = new EditAdapter(this, editItems);
            listView.setAdapter(editAdapter);
        }
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
