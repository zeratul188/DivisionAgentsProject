package com.example.divisionsimulation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.divisionsimulation.ui.share.ShareFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable {

    private AppBarConfiguration mAppBarConfiguration;
    private long backKeyPressedTime = 0;
    private Toast toast;

    private boolean managered = false;
    private int manager_access_count = 0;

    private AlertDialog.Builder builder = null;
    private AlertDialog alertDialog = null;
    private View dialogView = null;

    private AlertDialog.Builder builder_timer = null;
    private AlertDialog alertDialog_timer = null;
    private View dialogView_timer = null;

    private TextView txtTimer = null;

    private ProgressBar progressTimer;

    private TextView txtInfo = null;

    private String NOTIFICATION_ID = "";

    private Handler handler;

    private NotificationManager notificationManager = null;
    private NotificationChannel channel = null;

    private int hour, minute, second;

    public void setTxtInfo(String message) { txtInfo.setText(message); }

    public void setTimerText(String message) {
        txtTimer.setText(message);
    }

    public void setTimerProgress(int progress) {
        progressTimer.setProgress(progress);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#00222222"));
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Drawable draw = getResources().getDrawable(R.drawable.redwall4);
        //getSupportActionBar().setBackgroundDrawable(draw);

        ShareFragment.context = this;

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final View viewt = getWindow().getDecorView();

        handler = new Handler();
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        NOTIFICATION_ID = "10001";
        String NOTIFICATION_NAME = "동기화";
        int IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(NOTIFICATION_ID, NOTIFICATION_NAME, IMPORTANCE);
            notificationManager.createNotificationChannel(channel);
        }

        /*ActionBarDrawerToggle DrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            // drawer가 닫혔을 때, 호출된다.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (viewt != null) {
                        viewt.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "안드로이드 버젼이 마시멜로(6.0)보다 낮아 상태바 아이콘 색상 변경이 불가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // drawer가 열렸을 때, 호출된다.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (viewt != null) {
                        viewt.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        Toast.makeText(getApplicationContext(), "안드로이드 버젼이 마시멜로(6.0)보다 낮아 상태바 아이콘 색상 변경이 불가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        drawer.setDrawerListener(DrawerToggle);*/

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        getSupportActionBar().setElevation(0);

        /*View view = getWindow().getDecorView();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (view != null) {
                    view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (view != null) {
                    view.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
                }
            }
        }*/

        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

    }
    public void showGuide() {
        toast = Toast.makeText(getApplicationContext(), "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onBackPressed() {
        /*if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }*/
        dialogView = getLayoutInflater().inflate(R.layout.exitdialoglayout, null);

        final ExitThread et = new ExitThread(this);

        final Button btnPlay = dialogView.findViewById(R.id.btnPlay);
        final Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        txtInfo = dialogView.findViewById(R.id.txtInfo);

        builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.stopThread(true);
                alertDialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                et.stopThread(true);
            }
        });
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        et.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        /*AlertDialog.Builder builder = null;
        AlertDialog alertDialog = null;
        View dialogView = null;*/
        switch(item.getItemId())
        {
            case R.id.menu1:
                /*builder = new AlertDialog.Builder(this);
                builder.setTitle("버젼 확인").setMessage("Version 2.2.3\n마지막 수정 일자 : 2020년 4월 3일 16시 46분\n\n변경 사항 : \n- 파밍 시뮬레이션 : 다크존 수치가 이상하게 나왔던 버그 수정\n- 파밍 시뮬레이션 : 아이템 목록에 다크존 네임드 아이템이 색이 적용안되던 버그 수정\n- 파밍 시뮬레이션 : 네임드, 특급 아이템 탤런트 이름 추가");
                builder.setPositiveButton("확인", null);
                alertDialog = builder.create();
                alertDialog.show();*/
                dialogView = getLayoutInflater().inflate(R.layout.helplayout0, null);
                final Button btnExit = dialogView.findViewById(R.id.btnExit);
                final TextView txtVersion = dialogView.findViewById(R.id.txtVersion);
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                txtVersion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = "";
                        manager_access_count++;
                        if (manager_access_count >= 5) {
                            if (!managered) {
                                str = "관리자 모드에 진입하셨습니다.";
                                managered = true;
                            } else str = "이미 관리자 모드입니다.";
                            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                        } else {
                            str = "관리자 모드까지 "+(5-manager_access_count)+"단계 남았습니다.";
                            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                break;
            case R.id.menu7:
                dialogView = getLayoutInflater().inflate(R.layout.timerlayout, null);

                final TextView edtTarget = dialogView.findViewById(R.id.edtTarget);
                final EditText edtHour = dialogView.findViewById(R.id.edtHour);
                final EditText edtMinute = dialogView.findViewById(R.id.edtMinute);
                final EditText edtSecond = dialogView.findViewById(R.id.edtSecond);
                final Activity activity = this;

                final Button btnTimerPlay = dialogView.findViewById(R.id.btnTimerPlay);
                final Button btnTImerCancel = dialogView.findViewById(R.id.btnTimerCancel);

                edtMinute.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String temp = String.valueOf(edtMinute.getText());
                        int index = temp.indexOf(".");
                        String result = "", end_result = "";
                        if (index != -1) result = temp.substring(0, index);
                        else result = temp;
                        if (!result.equals("")) {
                            if (index != -1) end_result = temp.substring(index+1, temp.length());
                            if (Integer.parseInt(result) == 59 && !end_result.equals("")) {
                                if (Integer.parseInt(end_result) > 0) {
                                    Toast.makeText(activity, "'분'은 59 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                                    edtMinute.setText("60");
                                }
                            }
                            if (Integer.parseInt(result) < 0 || Integer.parseInt(result) > 60) {
                                Toast.makeText(activity, "'분'은 0 이상, 59 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                                edtMinute.setText("0");
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                edtSecond.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String temp = String.valueOf(edtSecond.getText());
                        int index = temp.indexOf(".");
                        String result = "", end_result = "";
                        if (index != -1) result = temp.substring(0, index);
                        else result = temp;
                        if (!result.equals("")) {
                            if (index != -1) end_result = temp.substring(index+1, temp.length());
                            if (Integer.parseInt(result) == 59 && !end_result.equals("")) {
                                if (Integer.parseInt(end_result) > 0) {
                                    Toast.makeText(activity, "'분'은 59 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                                    edtSecond.setText("60");
                                }
                            }
                            if (Integer.parseInt(result) < 0 || Integer.parseInt(result) > 60) {
                                Toast.makeText(activity, "'분'은 0 이상, 59 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                                edtSecond.setText("0");
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);

                btnTimerPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (String.valueOf(edtHour.getText()).equals("") && String.valueOf(edtMinute.getText()).equals("") && String.valueOf(edtSecond.getText()).equals("")) {
                            Toast.makeText(activity, "'시간', '분', '초' 중에서 1개 이상은 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogView_timer = getLayoutInflater().inflate(R.layout.timerstartlayout, null);

                            TextView txtResultTarget = dialogView_timer.findViewById(R.id.txtResultTarget);
                            progressTimer = dialogView_timer.findViewById(R.id.progressTimer);
                            txtTimer = dialogView_timer.findViewById(R.id.txtTimer);

                            if (String.valueOf(edtHour.getText()).equals("")) hour = 0;
                            else hour = Integer.parseInt(String.valueOf(edtHour.getText()));
                            if (String.valueOf(edtMinute.getText()).equals("")) minute = 0;
                            else minute = Integer.parseInt(String.valueOf(edtMinute.getText()));
                            if (String.valueOf(edtSecond.getText()).equals("")) second = 0;
                            else second = Integer.parseInt(String.valueOf(edtSecond.getText()));

                            notificationManager.cancelAll();

                            if (edtTarget.getText() != null && !String.valueOf(edtTarget.getText()).equals("")) {
                                String message = String.valueOf(edtTarget.getText())+"까지 진행 중...";
                                System.out.println(message);
                                NotificationCompat.Builder buildert = new NotificationCompat.Builder(MainActivity.this, NOTIFICATION_ID)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                                        .setContentTitle("타이머 진행 중...") //타이틀 TEXT
                                        .setContentText(message) //서브 타이틀 TEXT
                                        .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                                        .setSound(null)
                                        .setOngoing(true) // 사용자가 직접 못지우게 계속 실행하기.
                                        ;

                                notificationManager.notify(0, buildert.build());
                                txtResultTarget.setText(String.valueOf(edtTarget.getText())+"까지 남은 시간");
                            }
                            else {
                                NotificationCompat.Builder buildert = new NotificationCompat.Builder(MainActivity.this, NOTIFICATION_ID)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                                        .setContentTitle("타이머 진행 중...") //타이틀 TEXT
                                        .setContentText("'목표'까지 진행 중...") //서브 타이틀 TEXT
                                        .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                                        .setSound(null)
                                        .setOngoing(true) // 사용자가 직접 못지우게 계속 실행하기.
                                        ;

                                notificationManager.notify(0, buildert.build());
                                txtResultTarget.setText("'목표'까지 남은 시간");
                            }
                            final TimerThread tt = new TimerThread(hour, minute, second, handler, activity, notificationManager, MainActivity.this, MainActivity.this);

                            progressTimer.setMax(10000);
                            progressTimer.setProgress(0);

                            builder_timer = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
                            builder_timer.setView(dialogView_timer);
                            builder_timer.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tt.stopThread();
                                }
                            });
                            builder_timer.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    Toast.makeText(activity, "타이머가 종료됩니다.", Toast.LENGTH_SHORT).show();
                                    tt.stopThread();
                                }
                            });
                            alertDialog_timer = builder_timer.create();
                            alertDialog_timer.setCancelable(false);
                            alertDialog_timer.show();

                            tt.start();
                        }
                    }
                });

                btnTImerCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
