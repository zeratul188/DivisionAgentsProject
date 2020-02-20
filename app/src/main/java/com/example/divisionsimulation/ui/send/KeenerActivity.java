package com.example.divisionsimulation.ui.send;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class KeenerActivity extends AppCompatActivity {

    private ImageView[] btnKeener = new ImageView[8];
    private MediaPlayer mp = null;

    private AlertDialog.Builder builder = null;
    private AlertDialog alertDialog = null;
    private View dialogView = null;

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
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound1);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'간단한 계산' 음성 기록 재생 중...").setMessage("중간에 종료하실려면 종료 버튼을 누르시거나 바깥부분을 누르십시오.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'간단한 계산' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btnKeener[1].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound2);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'경고' 음성 기록 재생 중...").setMessage("중간에 종료하실려면 종료 버튼을 누르시거나 바깥부분을 누르십시오.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'경고' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btnKeener[2].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound3);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'고향' 음성 기록 재생 중...").setMessage("중간에 종료하실려면 종료 버튼을 누르시거나 바깥부분을 누르십시오.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'고향' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btnKeener[3].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound4);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'그린 플루' 음성 기록 재생 중...").setMessage("중간에 종료하실려면 종료 버튼을 누르시거나 바깥부분을 누르십시오.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'그린 플루' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btnKeener[4].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound5);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'기록' 음성 기록 재생 중...").setMessage("중간에 종료하실려면 종료 버튼을 누르시거나 바깥부분을 누르십시오.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'기록' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btnKeener[5].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound6);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'비탈리' 음성 기록 재생 중...").setMessage("중간에 종료하실려면 종료 버튼을 누르시거나 바깥부분을 누르십시오.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'비탈리' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btnKeener[6].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound7);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'인내' 음성 기록 재생 중...").setMessage("중간에 종료하실려면 종료 버튼을 누르시거나 바깥부분을 누르십시오.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'인내' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btnKeener[7].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound8);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'출입구 열기' 음성 기록 재생 중...").setMessage("중간에 종료하실려면 종료 버튼을 누르시거나 바깥부분을 누르십시오.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'출입구 열기' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
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
