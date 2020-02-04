package com.example.divisionsimulation.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

import java.io.Serializable;

public class SimulActivity extends AppCompatActivity implements Serializable {

    public static TextView txtSheld, txtHealth, txtNowDemage, txtAmmo, txtAllAmmo, txtTime, txtAdddemage, txtStatue;
    private TextView txtNickname;

    public static ProgressBar progressSheld, progressHealth, progressAmmo;

    private DemageSimulThread dst = null;

    private LinearLayout layoutQuickhand;
    public static TextView txtQuickhand;

    private boolean exit = false;

    private Button btnExit;

    private boolean quick = false;

    private static int health;

    public static synchronized void setHealth(int hp) { health = hp; }
    public static synchronized int getHealth() { return health; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simullayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("디비전2 시뮬레이션");

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

        //progressSheld.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);

        progressSheld.setMax(10000);
        progressHealth.setMax(10000);
        progressAmmo.setMax(10000);

        progressHealth.setProgress(10000);
        progressAmmo.setProgress(10000);

        TimeThread tt = new TimeThread();
        tt.start();

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
        //dst = (DemageSimulThread) getIntent().getParcelableExtra("thread");
        dst.setTimeThread(tt);
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
                    Toast.makeText(getApplicationContext(), "강제 종료되었습니다.\n뒤로가기 또는 상단의 화살표 버튼을 눌러 메인으로 돌아가십시오.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "이미 종료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                dst.interrupt();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static void hitCritical() {
        txtNowDemage.setTextColor(Color.parseColor("#FF6600"));
    }
    public static void hitBoom() {
        txtNowDemage.setTextColor(Color.parseColor("#C7A900"));
    }
    public static void hitHeadshot() {
        txtNowDemage.setTextColor(Color.parseColor("#FF0000"));
    }
    public static void defaultColor() {
        txtNowDemage.setTextColor(Color.parseColor("#000000"));
    }
}
