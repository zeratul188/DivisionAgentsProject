package com.example.divisionsimulation;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable {

    private AppBarConfiguration mAppBarConfiguration;
    private long backKeyPressedTime = 0;
    private Toast toast;

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

        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }
    public void showGuide() {
        toast = Toast.makeText(getApplicationContext(), "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
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
        AlertDialog.Builder builder = null;
        AlertDialog alertDialog = null;
        View dialogView = null;
        switch(item.getItemId())
        {
            case R.id.menu1:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("버젼 확인").setMessage("Version 1.5.6\n마지막 수정 일자 : 2020년 2월 14일 16시 32분\n\n변경 사항 : \n- 무기 시뮬레이션 : 카멜레온 탤런트 추가\n- 무기 시뮬레이션 : 데미지, 체력 등 잘 보이도록 하얀 그림자 추가\n- 무기 시뮬레이션 : 체력바 디자인 수정");
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
