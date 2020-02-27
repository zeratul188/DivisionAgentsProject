package com.example.divisionsimulation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
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

    private AlertDialog.Builder builder = null;
    private AlertDialog alertDialog = null;
    private View dialogView = null;

    public static AlertDialog.Builder builder_timer = null;
    public static AlertDialog alertDialog_timer = null;
    public static View dialogView_timer = null;
    public static TextView txtTimer = null;

    public static ProgressBar progressTimer;

    public static TextView txtInfo = null;

    private Handler handler;

    private int hour, minute, second;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final View viewt = getWindow().getDecorView();

        handler = new Handler();

        ActionBarDrawerToggle DrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** drawer가 닫혔을 때, 호출된다. */
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

            /** drawer가 열렸을 때, 호출된다. */
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

        drawer.setDrawerListener(DrawerToggle);

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

        txtInfo = dialogView.findViewById(R.id.txtInfo);

        builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et.stopThread(true);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                et.stopThread(true);
            }
        });
        alertDialog = builder.create();
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
                builder = new AlertDialog.Builder(this);
                builder.setTitle("버젼 확인").setMessage("Version 1.7.1\n마지막 수정 일자 : 2020년 2월 27일 13시 34분\n\n변경 사항 : \n- 파밍 시뮬레이션 : 이송 시스템 로그로 인한 탈취확률 조정, 즉시 이송과 이송 지점 벗어나는 기능 추가\n- 대부분 다이얼로그 바깥영역을 누르면 취소되는 것을 방지");
                builder.setPositiveButton("확인", null);
                alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.menu2:
                dialogView = getLayoutInflater().inflate(R.layout.helplayout1, null);
                builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);
                builder.setTitle("무기 시뮬레이션 도움말");
                builder.setPositiveButton("확인", null);
                alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.menu3:
                dialogView = getLayoutInflater().inflate(R.layout.helplayout2, null);
                builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);
                builder.setTitle("무기 정보 도움말");
                builder.setPositiveButton("확인", null);
                alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.menu4:
                dialogView = getLayoutInflater().inflate(R.layout.helplayout3, null);
                builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);
                builder.setTitle("보호장구(방어구) 정보 도움말");
                builder.setPositiveButton("확인", null);
                alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.menu5:
                dialogView = getLayoutInflater().inflate(R.layout.helplayout4, null);
                builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);
                builder.setTitle("공략 및 정보 도움말");
                builder.setPositiveButton("확인", null);
                alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.menu6:
                dialogView = getLayoutInflater().inflate(R.layout.helplayout5, null);
                builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);
                builder.setTitle("파밍 시뮬레이션 도움말");
                builder.setPositiveButton("확인", null);
                alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.menu7:
                dialogView = getLayoutInflater().inflate(R.layout.timerlayout, null);

                final TextView edtTarget = dialogView.findViewById(R.id.edtTarget);
                final EditText edtHour = dialogView.findViewById(R.id.edtHour);
                final EditText edtMinute = dialogView.findViewById(R.id.edtMinute);
                final EditText edtSecond = dialogView.findViewById(R.id.edtSecond);
                final Activity activity = this;

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
                builder.setTitle("목표 타이머");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

                            if (edtTarget.getText() != null && !String.valueOf(edtTarget.getText()).equals("")) txtResultTarget.setText(String.valueOf(edtTarget.getText())+"까지 남은 시간");
                            else txtResultTarget.setText("'목표'까지 남은 시간");
                            final TimerThread tt = new TimerThread(hour, minute, second, handler, activity);

                            progressTimer.setMax(10000);
                            progressTimer.setProgress(0);

                            builder_timer = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
                            builder_timer.setView(dialogView_timer);
                            builder_timer.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(activity, "타이머가 종료됩니다.", Toast.LENGTH_SHORT).show();
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
