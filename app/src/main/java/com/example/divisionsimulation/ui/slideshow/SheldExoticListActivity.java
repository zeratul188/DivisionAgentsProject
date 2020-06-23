package com.example.divisionsimulation.ui.slideshow;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class SheldExoticListActivity extends AppCompatActivity {

    private int[] arrItem = {R.drawable.mask, R.drawable.vests, R.drawable.holsters, R.drawable.backpack, R.drawable.gloves, R.drawable.kneepeds};
    private String[] arrNames = {"마스크", "조끼", "권총집", "백팩", "장갑", "무릎 보호대"};

    private ImageView imgType;
    private TextView txtName, txtSheld, txtCore, txtSub1, txtSub2, txtTalent, txtTalentContent, txtLocation, txtContent;

    private ExoticSheldDbAdapter exoticAdapter;
    private long number = -1;

    private String name, sheldth, core, sub1, sub2, talent, talent_content, location, content, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheldexoticlistlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgType = findViewById(R.id.imgType);
        txtName = findViewById(R.id.txtName);
        txtSheld = findViewById(R.id.txtSheld);
        txtCore = findViewById(R.id.txtCore);
        txtSub1 = findViewById(R.id.txtSub1);
        txtSub2 = findViewById(R.id.txtSub2);
        txtTalent = findViewById(R.id.txtTalent);
        txtTalentContent = findViewById(R.id.txtTalentContent);
        txtLocation = findViewById(R.id.txtLocation);
        txtContent = findViewById(R.id.txtContent);

        this.exoticAdapter = new ExoticSheldDbAdapter(this);

        Intent intent = getIntent();
        number = intent.getLongExtra("number", -1);

        exoticAdapter.open();

        Cursor cursor = exoticAdapter.fetchWeapon(number);
        name = cursor.getString(1);
        sheldth = cursor.getString(2);
        core = cursor.getString(3);
        sub1 = cursor.getString(4);
        sub2 = cursor.getString(5);
        talent = cursor.getString(6);
        talent_content = cursor.getString(7);
        location = cursor.getString(8);
        content = cursor.getString(9);
        type = cursor.getString(10);

        imgType.setImageResource(setImageResource(type));

        txtName.setText(name);
        txtSheld.setText(sheldth);
        txtCore.setText(core);
        txtSub1.setText(sub1);
        txtSub2.setText(sub2);
        txtTalent.setText(talent);
        txtTalentContent.setText(talent_content);
        txtLocation.setText(location);
        txtContent.setText(content);

        setTitle(name);

        exoticAdapter.close();
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

    private int setImageResource(String type) {
        for (int i = 0; i < arrNames.length; i++) if (type.equals(arrNames[i])) return arrItem[i];
        return arrItem[0];
    }
}
