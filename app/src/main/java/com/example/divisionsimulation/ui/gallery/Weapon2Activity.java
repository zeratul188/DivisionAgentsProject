package com.example.divisionsimulation.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class Weapon2Activity extends AppCompatActivity {

    //private Button[] btnWP = new Button[12];
    private LinearLayout[] btnWP = new LinearLayout[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weapon2layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("소총");

        int temp;
        for (int i = 0; i < btnWP.length; i++) {
            temp = getResources().getIdentifier("btnWP"+(i+1), "id", getPackageName());
            btnWP[i] = findViewById(temp);
        }

        btnWP[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List1Activity.class);
                startActivity(intent);
            }
        });
        btnWP[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List2Activity.class);
                startActivity(intent);
            }
        });
        btnWP[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List3Activity.class);
                startActivity(intent);
            }
        });
        btnWP[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List4Activity.class);
                startActivity(intent);
            }
        });
        btnWP[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List5Activity.class);
                startActivity(intent);
            }
        });
        btnWP[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List6Activity.class);
                startActivity(intent);
            }
        });
        btnWP[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List7Activity.class);
                startActivity(intent);
            }
        });
        btnWP[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List8Activity.class);
                startActivity(intent);
            }
        });
        btnWP[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List9Activity.class);
                startActivity(intent);
            }
        });
        btnWP[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List10Activity.class);
                startActivity(intent);
            }
        });
        btnWP[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List11Activity.class);
                startActivity(intent);
            }
        });
        btnWP[11].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon2List12Activity.class);
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
