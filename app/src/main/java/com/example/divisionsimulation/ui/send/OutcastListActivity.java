package com.example.divisionsimulation.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class OutcastListActivity extends AppCompatActivity {

    private Button[] btnWP = new Button[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outcastlistlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("아웃캐스트");

        int temp;
        for (int i = 0; i < btnWP.length; i++) {
            temp = getResources().getIdentifier("btnWP"+(i+1), "id", getPackageName());
            btnWP[i] = findViewById(temp);
        }

        btnWP[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList1Activity.class);
                startActivity(intent);
            }
        });
        btnWP[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList2Activity.class);
                startActivity(intent);
            }
        });
        btnWP[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList3Activity.class);
                startActivity(intent);
            }
        });
        btnWP[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList4Activity.class);
                startActivity(intent);
            }
        });
        btnWP[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList5Activity.class);
                startActivity(intent);
            }
        });
        btnWP[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList6Activity.class);
                startActivity(intent);
            }
        });
        btnWP[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList7Activity.class);
                startActivity(intent);
            }
        });
        btnWP[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList8Activity.class);
                startActivity(intent);
            }
        });
        btnWP[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OutcastList9Activity.class);
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
