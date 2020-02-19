package com.example.divisionsimulation.ui.send;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class KeenerActivity extends AppCompatActivity {

    private boolean started = false;
    private ImageView[] btnKeener = new ImageView[8];
    private MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keenerlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("아론 키너 음성 기록");

        Intent intent = getIntent();

        int temp;
        for (int i = 0; i < btnKeener.length; i++) {
            temp = getResources().getIdentifier("btnKeener"+(i+1), "id", getPackageName());
            btnKeener[i] = findViewById(temp);
            btnKeener[i].setAdjustViewBounds(true);
        }

        btnKeener[0].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound1);
                    mp.start();
                    started = true;
                } else {
                    mp.stop();
                    mp.reset();
                    started = false;
                }
            }
        });
        btnKeener[1].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound2);
                    mp.start();
                    started = true;
                } else {
                    mp.stop();
                    mp.reset();
                    started = false;
                }
            }
        });
        btnKeener[2].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound3);
                    mp.start();
                    started = true;
                } else {
                    mp.stop();
                    mp.reset();
                    started = false;
                }
            }
        });
        btnKeener[3].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound4);
                    mp.start();
                    started = true;
                } else {
                    mp.stop();
                    mp.reset();
                    started = false;
                }
            }
        });
        btnKeener[4].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound5);
                    mp.start();
                    started = true;
                } else {
                    mp.stop();
                    mp.reset();
                    started = false;
                }
            }
        });
        btnKeener[5].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound6);
                    mp.start();
                    started = true;
                } else {
                    mp.stop();
                    mp.reset();
                    started = false;
                }
            }
        });
        btnKeener[6].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound7);
                    mp.start();
                    started = true;
                } else {
                    mp.stop();
                    mp.reset();
                    started = false;
                }
            }
        });
        btnKeener[7].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound8);
                    mp.start();
                    started = true;
                } else {
                    mp.stop();
                    mp.reset();
                    started = false;
                }
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
