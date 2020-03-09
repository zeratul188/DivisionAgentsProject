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

    private TextView txtSheld, txtHealth, txtNowDemage, txtAmmo, txtAllAmmo, txtTime, txtAdddemage, txtStatue;
    private TextView txtNickname, txtHealthInfo;

    private TextView[] txtListDemage = new TextView[11];
    private ImageView[] imgtake = new ImageView[4];
    private ImageView imgAim;

    private ProgressBar progressSheld, progressHealth, progressAmmo;

    private DemageSimulThread dst = null;
    private CluchThread ct = null;
    private TimeThread tt = null;
    private ReloadThread rt = null;

    private LinearLayout layoutQuickhand;
    private TextView txtQuickhand;

    private Handler handler;

    private boolean exit = false;

    private Button btnExit;

    private boolean quick = false;

    private static int health;
    private static ImageView imgBoom, imgCritical, imgHeadshot;

    public static synchronized void setHealth(int hp) { health = hp; }
    public static synchronized int getHealth() { return health; }
    public void setExit(boolean et) { exit = et; }

    public void setProgressSheld(int progress) { progressSheld.setProgress(progress); }
    public void setProgressHealth(int progress) { progressHealth.setProgress(progress); }
    public void setProgressAmmo(int progress) { progressAmmo.setProgress(progress); }
    public void settingIndeterminate_Ammo(boolean flag) { progressAmmo.setIndeterminate(flag); }

    public void setTxtSheld(String message) { txtSheld.setText(message); }
    public void setTxtHealth(String message) { txtHealth.setText(message); }
    public void setTxtNowDemage(String message) { txtNowDemage.setText(message); }
    public void setTxtAmmo(String message) { txtAmmo.setText(message); }
    public void setTxtAllAmmo(String message) { txtAllAmmo.setText(message); }
    public void setTxtTime(String message) { txtTime.setText(message); }
    public void setTxtAdddemage(String message) { txtAdddemage.setText(message); }
    public void setTxtStatue(String message) { txtStatue.setText(message); }
    public void setTxtQuickhand(String message) { txtQuickhand.setText(message); }
    public void setBtnExitText(String message) { btnExit.setText(message); }

    public void setTxtListDemage(int index, String message) { txtListDemage[index].setText(message); }
    public void setImgTake(int index) {
        for (int i = 0; i < imgtake.length; i++) {
            if (index == i) imgtake[i].setVisibility(View.VISIBLE);
            else imgtake[i].setVisibility(View.INVISIBLE);
        }
    }
    /*
    0~1 : 몸샷
    2~3 : 헤드샷
    4 : 빗나감
     */
    public void setImgAim(int index, boolean taked) {
        if (taked) { //index : 1~10
            switch (index) {
                case 1:
                    imgAim.setImageResource(R.drawable.aim1);
                    break;
                case 2:
                    imgAim.setImageResource(R.drawable.aim2);
                    break;
                case 3:
                    imgAim.setImageResource(R.drawable.aim3);
                    break;
                case 4:
                    imgAim.setImageResource(R.drawable.aim4);
                    break;
                case 5:
                    imgAim.setImageResource(R.drawable.aim5);
                    break;
                case 6:
                    imgAim.setImageResource(R.drawable.aim6);
                    break;
                case 7:
                    imgAim.setImageResource(R.drawable.aim7);
                    break;
                case 8:
                    imgAim.setImageResource(R.drawable.aim8);
                    break;
                case 9:
                    imgAim.setImageResource(R.drawable.aim9);
                    break;
                case 10:
                    imgAim.setImageResource(R.drawable.aim10);
                    break;
            }
        } else { //index : 1~5
            switch (index) {
                case 1:
                    imgAim.setImageResource(R.drawable.out_aim1);
                    break;
                case 2:
                    imgAim.setImageResource(R.drawable.out_aim2);
                    break;
                case 3:
                    imgAim.setImageResource(R.drawable.out_aim3);
                    break;
                case 4:
                    imgAim.setImageResource(R.drawable.out_aim4);
                    break;
                case 5:
                    imgAim.setImageResource(R.drawable.out_aim5);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simullayout);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
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
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);*/

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );


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
        txtHealthInfo = findViewById(R.id.txtHealthInfo);

        progressSheld = findViewById(R.id.progressSheld);
        progressHealth = findViewById(R.id.progressHealth);
        progressAmmo = findViewById(R.id.progressAmmo);
        imgAim = findViewById(R.id.imgAim);

        layoutQuickhand = findViewById(R.id.layoutQuickhand);
        txtQuickhand = findViewById(R.id.txtQuickhand);

        btnExit = findViewById(R.id.btnExit);

        imgHeadshot = findViewById(R.id.imgHeadshot);
        imgBoom = findViewById(R.id.imgBoom);
        imgCritical = findViewById(R.id.imgCritical);

        int temp;
        for (int i = 0; i < txtListDemage.length; i++) {
            temp = getResources().getIdentifier("txtListDemage"+(i+1), "id", getPackageName());
            txtListDemage[i] = findViewById(temp);
        }
        for (int i = 0; i < imgtake.length; i++) {
            temp = getResources().getIdentifier("imgtake"+(i+1), "id", getPackageName());
            imgtake[i]  = findViewById(temp);
        }

        //progressSheld.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);

        txtNowDemage.setShadowLayer(1, 5, 5, Color.parseColor("#bbe3e3e3"));
        txtAmmo.setShadowLayer(1, 5, 5, Color.parseColor("#bb222222"));
        txtSheld.setShadowLayer(1, 3, 3, Color.parseColor("#bbe3e3e3"));
        txtHealth.setShadowLayer(1, 3, 3, Color.parseColor("#bbe3e3e3"));

        progressSheld.setMax(10000);
        progressHealth.setMax(10000);
        progressAmmo.setMax(10000);

        progressHealth.setProgress(10000);
        progressAmmo.setProgress(10000);

        //TimeThread tt = new TimeThread();
        //tt.start();

        tt = (TimeThread) getIntent().getSerializableExtra("timethread");
        tt.setSimulActivity(this);
        tt.setHandler(handler);
        tt.start();

        rt = new ReloadThread(handler, this);

        String nickname = getIntent().getStringExtra("nickname");
        if (!nickname.equals("")) txtNickname.setText(nickname);
        else txtNickname.setText("표적");

        boolean elite_true = Boolean.parseBoolean(getIntent().getStringExtra("elite"));
        if (elite_true) {
            progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health_elite));
            txtHealth.setTextColor(Color.parseColor("#bdc900"));
            txtHealthInfo.setTextColor(Color.parseColor("#bdc900"));
        }
        else progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health));

        quick = Boolean.parseBoolean(getIntent().getStringExtra("quickhand"));
        if (quick) layoutQuickhand.setVisibility(View.VISIBLE);
        else layoutQuickhand.setVisibility(View.GONE);

        dst = (DemageSimulThread) getIntent().getSerializableExtra("thread");
        dst.setSimulActivity(this);
        ct = (CluchThread) getIntent().getSerializableExtra("cluchthread");
        if (rt != null) {
            dst.setReloadThread(rt);
        }
        dst.setHandler(handler);
        if (ct != null) {
            ct.setSimulActivity(this);
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

    public void hitCritical() {
        txtNowDemage.setTextColor(Color.parseColor("#FF6600"));
    }
    public void hitBoom() {
        txtNowDemage.setTextColor(Color.parseColor("#C7A900"));
    }
    public void hitHeadshot() {
        txtNowDemage.setTextColor(Color.parseColor("#FF0000"));
    }
    public void defaultColor() { txtNowDemage.setTextColor(Color.parseColor("#F0F0F0")); }
    public void shelddefaultColor() {
        txtNowDemage.setTextColor(Color.parseColor("#A4A4FF"));
    }

    public void hitCritical_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#FF6600")); }
    public void hitboom_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#C7A900")); }
    public void hitHeadshot_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#FF0000")); }
    public void defaultColor_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#F0F0F0")); }
    public void shelddefaultColor_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#A4A4FF")); }

    public synchronized void changeCritical(boolean change) {
        if (change) imgCritical.setVisibility(View.VISIBLE);
        else imgCritical.setVisibility(View.INVISIBLE);
    }
    public synchronized void changeHeadshot(boolean change) {
        if (change) imgHeadshot.setVisibility(View.VISIBLE);
        else imgHeadshot.setVisibility(View.INVISIBLE);
    }
    public synchronized void changeBoom(boolean change) {
        if (change) imgBoom.setVisibility(View.VISIBLE);
        else imgBoom.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
