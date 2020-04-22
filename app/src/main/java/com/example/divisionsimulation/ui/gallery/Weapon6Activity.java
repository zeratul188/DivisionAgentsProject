package com.example.divisionsimulation.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class Weapon6Activity extends AppCompatActivity {

    //private Button[] btnWP = new Button[9];
    private LinearLayout[] btnWP = new LinearLayout[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weapon6layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("산탄총");

        int temp;
        for (int i = 0; i < btnWP.length; i++) {
            temp = getResources().getIdentifier("btnWP"+(i+1), "id", getPackageName());
            btnWP[i] = findViewById(temp);
        }

        btnWP[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List1Activity.class);
                startActivity(intent);
            }
        });
        btnWP[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List2Activity.class);
                startActivity(intent);
            }
        });
        btnWP[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List3Activity.class);
                startActivity(intent);
            }
        });
        btnWP[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List4Activity.class);
                startActivity(intent);
            }
        });
        btnWP[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List5Activity.class);
                startActivity(intent);
            }
        });
        btnWP[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List6Activity.class);
                startActivity(intent);
            }
        });
        btnWP[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List7Activity.class);
                startActivity(intent);
            }
        });
        btnWP[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List8Activity.class);
                startActivity(intent);
            }
        });
        btnWP[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weapon6List9Activity.class);
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
