package com.example.divisionsimulation.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

import java.io.Serializable;

public class SimulActivity extends Activity implements Serializable {

    public static TextView txtSheld, txtHealth, txtNowDemage, txtAmmo, txtAllAmmo, txtTime, txtAdddemage, txtStatue;
    private TextView txtNickname;

    public static ProgressBar progressSheld, progressHealth, progressAmmo;

    private DemageSimulThread dst = null;
    private CluchThread ct = null;
    private TimeThread tt = null;
    private ReloadThread rt = null;

    private LinearLayout layoutQuickhand;
    public static TextView txtQuickhand;

    private Handler handler;

    private static boolean exit = false;

    public static Button btnExit;

    private boolean quick = false;

    private static int health;
    private static ImageView imgBoom, imgCritical, imgHeadshot;

    public static synchronized void setHealth(int hp) { health = hp; }
    public static synchronized int getHealth() { return health; }
    public static void setExit(boolean et) { exit = et; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simullayout);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("Is on?", "Turning immersive mode mode off. ");
        } else {
            Log.i("Is on?", "Turning immersive mode mode on.");
        }
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

        setTitle("디비전2 시뮬레이션");

        exit = false;
        handler = new Handler();

        txtSheld = findViewById(R.id.txtSheld);
        txtHealth = findViewById(R.id.txtHealth);
        txtNowDemage = findViewById(R.id.txtNowDemage);
        txtStatue = findViewById(R.id.txtStatue);
        txtAmmo = findViewById(R.id.txtAmmo);
        txtAllAmmo = findViewById(R.id.txtAllAmmo);
        txtTime = findViewById(R.id.txtTime);
        txtAdddemage = findViewById(R.id.txtAdddemage);
        txtNickname = findViewById(R.id.txtNickname);

        progressSheld = findViewById(R.id.progressSheld);
        progressHealth = findViewById(R.id.progressHealth);
        progressAmmo = findViewById(R.id.progressAmmo);

        layoutQuickhand = findViewById(R.id.layoutQuickhand);
        txtQuickhand = findViewById(R.id.txtQuickhand);

        btnExit = findViewById(R.id.btnExit);

        imgHeadshot = findViewById(R.id.imgHeadshot);
        imgBoom = findViewById(R.id.imgBoom);
        imgCritical = findViewById(R.id.imgCritical);

        //progressSheld.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);

        progressSheld.setMax(10000);
        progressHealth.setMax(10000);
        progressAmmo.setMax(10000);

        progressHealth.setProgress(10000);
        progressAmmo.setProgress(10000);

        //TimeThread tt = new TimeThread();
        //tt.start();

        tt = (TimeThread) getIntent().getSerializableExtra("timethread");
        tt.setHandler(handler);
        tt.start();

        rt = new ReloadThread(handler);

        String nickname = getIntent().getStringExtra("nickname");
        if (!nickname.equals("")) txtNickname.setText(nickname);
        else txtNickname.setText("표적");

        boolean elite_true = Boolean.parseBoolean(getIntent().getStringExtra("elite"));
        if (elite_true) progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health_elite));
        else progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health));

        quick = Boolean.parseBoolean(getIntent().getStringExtra("quickhand"));
        if (quick) layoutQuickhand.setVisibility(View.VISIBLE);
        else layoutQuickhand.setVisibility(View.GONE);

        dst = (DemageSimulThread) getIntent().getSerializableExtra("thread");
        ct = (CluchThread) getIntent().getSerializableExtra("cluchthread");
        if (rt != null) {
            dst.setReloadThread(rt);
        }
        dst.setHandler(handler);
        if (ct != null) {
            ct.setHandler(handler);
            dst.setCluchThread(ct);
        }
        dst.setTimeThread(tt);
        dst.setActivity(this);
        if (dst.getSheld() != 0) progressSheld.setProgress(10000);
        else progressSheld.setProgress(0);
        dst.start();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!exit) {
                    exit = true;
                    dst.setEnd(true);
                    dst.setSheld(0);
                    dst.setHealth(0);
                    progressHealth.setProgress(0);
                    progressSheld.setProgress(0);
                    txtSheld.setText("0");
                    txtHealth.setText("0");
                    rt.stopThread();
                    Toast.makeText(getApplicationContext(), "강제 종료되었습니다.\n뒤로가기 키 또는 뒤로 가기 버튼을 눌러 메인으로 돌아가십시오.", Toast.LENGTH_LONG).show();
                    btnExit.setText("뒤로 가기");
                } else {
                    finish();
                }
            }
        });
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                dst.interrupt();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }*/

    public static void hitCritical() {
        txtNowDemage.setTextColor(Color.parseColor("#FF6600"));
    }
    public static void hitBoom() {
        txtNowDemage.setTextColor(Color.parseColor("#C7A900"));
    }
    public static void hitHeadshot() {
        txtNowDemage.setTextColor(Color.parseColor("#FF0000"));
    }
    public static void defaultColor() { txtNowDemage.setTextColor(Color.parseColor("#F0F0F0")); }
    public static void shelddefaultColor() {
        txtNowDemage.setTextColor(Color.parseColor("#5555FF"));
    }

    public static synchronized void changeCritical(boolean change) {
        if (change) imgCritical.setVisibility(View.VISIBLE);
        else imgCritical.setVisibility(View.INVISIBLE);
    }
    public static synchronized void changeHeadshot(boolean change) {
        if (change) imgHeadshot.setVisibility(View.VISIBLE);
        else imgHeadshot.setVisibility(View.INVISIBLE);
    }
    public static synchronized void changeBoom(boolean change) {
        if (change) imgBoom.setVisibility(View.VISIBLE);
        else imgBoom.setVisibility(View.INVISIBLE);
    }
}
