package com.example.divisionsimulation.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.ui.gallery.Weapon1List10Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List11Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List12Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List13Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List1Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List2Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List3Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List4Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List5Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List6Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List7Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List8Activity;
import com.example.divisionsimulation.ui.gallery.Weapon1List9Activity;

public class BlacktuskListActivity extends AppCompatActivity {

    private Button[] btnWP = new Button[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blacktusklistlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("블랙터스크");

        int temp;
        for (int i = 0; i < btnWP.length; i++) {
            temp = getResources().getIdentifier("btnWP"+(i+1), "id", getPackageName());
            btnWP[i] = findViewById(temp);
        }

        btnWP[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlacktuskList1Activity.class);
                startActivity(intent);
            }
        });
        btnWP[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlacktuskList2Activity.class);
                startActivity(intent);
            }
        });
        btnWP[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlacktuskList3Activity.class);
                startActivity(intent);
            }
        });
        btnWP[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlacktuskList4Activity.class);
                startActivity(intent);
            }
        });
        btnWP[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlacktuskList5Activity.class);
                startActivity(intent);
            }
        });
        btnWP[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlacktuskList6Activity.class);
                startActivity(intent);
            }
        });
        btnWP[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlacktuskList7Activity.class);
                startActivity(intent);
            }
        });
        btnWP[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlacktuskList8Activity.class);
                startActivity(intent);
            }
        });
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
