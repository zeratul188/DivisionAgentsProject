package com.example.divisionsimulation.ui.home;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.divisionsimulation.R;

import java.io.Serializable;

public class SimulActivity extends Activity implements Serializable {

    private TextView txtSheld, txtHealth, txtNowDemage, txtAmmo, txtAllAmmo, txtTime, txtAdddemage, txtStatue; //방어도, 생명력, 현재 데미지, 탄약 수, 누적 탄약 수, 시뮬 시간, 추가 데미지, 상태메시지 변수
    private TextView txtNickname; //표적 이름, "생명력 : " 부분 텍스트뷰
    private Boolean named = false, hitted = false;

    private TextView[] txtListDemage = new TextView[11]; //데미지 목록 배열
    private ImageView[] imgtake = new ImageView[4]; //타격 이미지뷰
    private ImageView imgAim, imgHittedSheld; //에임 이미지뷰

    private ProgressBar progressSheld, progressHealth, progressAmmo; //방어도, 생명력, 탄약 진행바

    private DemageSimulThread dst = null; //전투 시뮬레이션 스레드 객체
    private CluchThread ct = null; //클러치 스레드 객체
    private TimeThread tt = null; //타이머 스레드 객체
    private ReloadThread rt = null; //재장전 스레드 객체

    private LinearLayout layoutQuickhand; //빠른손 레이아웃
    private TextView txtQuickhand; //빠른손 히트수 텍스트뷰

    private Handler handler; //UI변경시 사용하는 핸들러

    private boolean exit = false; //종료 변수

    private Button btnExit; //종료 버튼 객체

    private boolean quick = false; //빠른 손 여부

    private int type = 0;

    private static int health; //생명력 변수
    private static ImageView imgBoom, imgCritical, imgHeadshot; //치명타, 헤드샷, 무자비 폭발탄 이미지뷰

    public static synchronized void setHealth(int hp) { health = hp; } //생명력을 저장한다.
    public static synchronized int getHealth() { return health; } //생명력을 외부에서 가져갈 때 사용하는 메소드
    public void setExit(boolean et) { exit = et; } //종료시키는 변수 설정 true : 종료, false : 진행

    public void setProgressSheld(int progress) { progressSheld.setProgress(progress); } //방어도 진행도를 설정한다.
    public void setProgressHealth(int progress) { progressHealth.setProgress(progress); } //생명력 진행도를 설정한다.
    public void setProgressAmmo(int progress) { progressAmmo.setProgress(progress); } //탄약 진행도를 설정한다.
    public void settingIndeterminate_Ammo(boolean flag) { progressAmmo.setIndeterminate(flag); } //탄약 무한 로딩을 설정한다. true : 무한 로딩 활성화, false : 무한 로딩 비활성화

    public void setTxtSheld(String message) { txtSheld.setText(message); } //현재 방어도를 UI에 적용시킨다.
    public void setTxtHealth(String message) { txtHealth.setText(message); } //위와 동일한 방식
    public void setTxtNowDemage(String message) { txtNowDemage.setText(message); } //위와 동일한 방식
    public void setTxtAmmo(String message) { txtAmmo.setText(message); } //위와 동일한 방식
    public void setTxtAllAmmo(String message) { txtAllAmmo.setText(message); } //위와 동일한 방식
    public void setTxtTime(String message) { txtTime.setText(message); } //위와 동일한 방식
    public void setTxtAdddemage(String message) { txtAdddemage.setText(message); } //위와 동일한 방식
    public void setTxtStatue(String message) { txtStatue.setText(message); } //위와 동일한 방식
    public void setTxtQuickhand(String message) { txtQuickhand.setText(message); } //위와 동일한 방식
    public void setBtnExitText(String message) { btnExit.setText(message); } //위와 동일한 방식
    public void setHitted(boolean hitted) {
        if (hitted) imgHittedSheld.setVisibility(View.VISIBLE);
        else imgHittedSheld.setVisibility(View.INVISIBLE);
    }

    public void setTxtListDemage(int index, String message) { txtListDemage[index].setText(message); } //index에 해당하는 데미지 목록을 UI에 적용시킨다.
    public void setImgTake(int index) { //타격 이미지를 적용시킨다.
        for (int i = 0; i < imgtake.length; i++) { //타격이미지 갯수만큼 반복한다. 길이는 10개
            if (index == i) imgtake[i].setVisibility(View.VISIBLE); //인덱스에 해당하는 타격 이미지를 보여준다.
            else imgtake[i].setVisibility(View.INVISIBLE); //인덱스에 해당하지 않는 타격 이미지들은 화면에서 보이지 않게 한다. gone으로 하게 되면 자리마저 사라진다.
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

        getWindow().getDecorView().setSystemUiVisibility( //상단바, 소프트키를 보이지 않게 한다.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );


        setTitle("디비전2 시뮬레이션"); //타이틀을 설정한다.

        exit = false; //종료 변수를 초기화한다.
        handler = new Handler(); //핸들러 객체를 생성한다.

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
        imgAim = findViewById(R.id.imgAim);
        imgHittedSheld = findViewById(R.id.imgHittedSheld);

        layoutQuickhand = findViewById(R.id.layoutQuickhand);
        txtQuickhand = findViewById(R.id.txtQuickhand);

        btnExit = findViewById(R.id.btnExit);

        imgHeadshot = findViewById(R.id.imgHeadshot);
        imgBoom = findViewById(R.id.imgBoom);
        imgCritical = findViewById(R.id.imgCritical);
        /*
        위 과정들은 모두 아이디를 불러오는 과정이다.
         */

        int temp;
        for (int i = 0; i < txtListDemage.length; i++) {
            temp = getResources().getIdentifier("txtListDemage"+(i+1), "id", getPackageName());
            txtListDemage[i] = findViewById(temp);
        }
        for (int i = 0; i < imgtake.length; i++) {
            temp = getResources().getIdentifier("imgtake"+(i+1), "id", getPackageName());
            imgtake[i]  = findViewById(temp);
        }
        /*
        매열 변수를 활용하여 반복되는 아이디를 반복문으로 불러온다.
         */

        //progressSheld.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);

        txtNowDemage.setShadowLayer(1, 2, 2, Color.parseColor("#bbe3e3e3")); //현재 데미지에 그림자를 추가한다.
        txtAmmo.setShadowLayer(1, 2, 2, Color.parseColor("#bb222222")); //위와 동일한 방식
        txtSheld.setShadowLayer(1, 2, 2, Color.parseColor("#bbe3e3e3")); //위와 동일한 방식
        txtHealth.setShadowLayer(1, 2, 2, Color.parseColor("#bbe3e3e3")); //위와 동일한 방식

        progressSheld.setMax(10000); //방어도 진행도의 최대치를 10000으로 잡는다.
        progressHealth.setMax(10000); //위와 동일한 방식
        progressAmmo.setMax(10000); //위와 동일한 방식

        progressHealth.setProgress(10000); //위와 동일한 방식
        progressAmmo.setProgress(10000); //위와 동일한 방식

        //TimeThread tt = new TimeThread();
        //tt.start();

        named = getIntent().getBooleanExtra("named", false);
        if (named) txtNickname.setVisibility(View.VISIBLE);
        else txtNickname.setVisibility(View.INVISIBLE);

        hitted = getIntent().getBooleanExtra("hitted", false);
        if (hitted) progressSheld.setVisibility(View.INVISIBLE);
        else progressSheld.setVisibility(View.VISIBLE);
        imgHittedSheld.setVisibility(View.INVISIBLE);

        type = getIntent().getIntExtra("type", 0);

        tt = (TimeThread) getIntent().getSerializableExtra("timethread"); //전 액티비티에서 생성했던 타이머 스레드를 가져와 현재 타이머 스레드 객체에 저장한다.
        tt.setSimulActivity(this); //시뮬액티비티를 현재 액티비티로 설정한다.
        tt.setHandler(handler); //핸들러를 설정한다.
        tt.start(); //타이머 스레드를 실행한다.

        rt = new ReloadThread(handler, this); //재장전 스레드를 생성한다.

        String nickname = getIntent().getStringExtra("nickname"); //전 액티비티에서 닉네임을 가져와 저장한다.
        if (!nickname.equals("")) txtNickname.setText(nickname); //닉네임이 비어있지 않다면 닉네임으로 설정한다.
        else txtNickname.setText("표적"); //닉네임이 비었다면 기본값인 "표적"으로 설정한다.

        boolean elite_true = Boolean.parseBoolean(getIntent().getStringExtra("elite")); //정예 여부를 전 액티비티 정예 여부 체크여부를 통해 저장한다.
        /*if (elite_true) { //표적이 정예대상이라면 적용된다. 정예 여부를 체크하게 되면 작동한다.
            progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health_elite)); //생명력 진행도 이미지를 변경 (색상만 노란색으로 변경됨)
            txtHealth.setTextColor(Color.parseColor("#bdc900")); //생명력 텍스트 색상 변경
            txtHealthInfo.setTextColor(Color.parseColor("#bdc900")); //위와 동일한 방식
        }
        else progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health)); //생명력 진행도 이미지를 원래대로 되돌린다.*/

        if (type == 3) {
            progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health_elite)); //생명력 진행도 이미지를 변경 (색상만 노란색으로 변경됨)
        } else if (type == 2) {
            progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health_veterang)); //생명력 진행도 이미지를 변경 (색상만 노란색으로 변경됨)
        } else {
            progressHealth.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_health)); //생명력 진행도 이미지를 변경 (색상만 노란색으로 변경됨)
        }

        quick = Boolean.parseBoolean(getIntent().getStringExtra("quickhand")); //전 액티비티에서 빠른손 여부를 가져온다.
        if (quick) layoutQuickhand.setVisibility(View.VISIBLE); //빠른손이 적용되어 있으면 빠른손 UI를 보여준다.
        else layoutQuickhand.setVisibility(View.GONE); //빠른손이 적용되지 않으면 빠른 손 UI를 숨긴다.

        dst = (DemageSimulThread) getIntent().getSerializableExtra("thread"); //전 액티비티에서 전투 시뮬레이션 스레드를 가져온다.
        dst.setSimulActivity(this); //현재 액티비티를 저장한다.
        ct = (CluchThread) getIntent().getSerializableExtra("cluchthread"); //전 액티비티에서 클러치 스레드를 가져온다.
        if (rt != null) { //재장전 스레드가 비어있지 않으면 작동한다.
            dst.setReloadThread(rt); //전투 시뮬레이션 스레드에 재장전 스레드를 넣는다.
        }
        dst.setHandler(handler); //현재 액티비티에 해당하는 핸들러를 전투 시뮬레이션 스레드에 넣는다.
        if (ct != null) { //클러치 스레드가 비어있지 않다면 작동한다.
            ct.setSimulActivity(this); //현재 액티비티를 넣는다.
            ct.setHandler(handler); //현재 액티비티에 해당하는 핸들러를 클러치 스레드에 넣는다.
            dst.setCluchThread(ct); //전투 시뮬레이션 스레드에 현재 클러치 스레드를 넣는다.
        }
        dst.setTimeThread(tt); //전투 시뮬레이션 스레드에 타이머 스레드를 넣는다.
        dst.setActivity(this); //전투 시뮬레이션 스레드에 현재 액티비티를 넣는다.
        if (dst.getSheld() != 0) progressSheld.setProgress(10000); //방어도가 0이 아니면 진행도를 최대치로 설정한다.
        else progressSheld.setProgress(0); //방어도가 0이면 진행도를 0으로 설정한다.
        dst.start(); //전투 시뮬레이션 스레드를 실행한다.

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //"강제종료" 버튼을 한번 누르면 작동한다.
                if (!exit) { //exit가 false 즉, 종료되지 않았으면 작동한다. 초기값이 false이다.
                    exit = true; //exit를 참으로 변경한다.
                    dst.setEnd(true); //전투 시뮬레이션 스레드를 종료한다.
                    dst.setSheld(0); //방어도를 0으로 초기화한다.
                    dst.setHealth(0); //생명력을 0으로 초기화한다.
                    progressHealth.setProgress(0); //생명력 진행도를 0으로 설정한다.
                    progressSheld.setProgress(0); //방어도 진행도를 0으로 설정한다.
                    txtSheld.setText("0"); //생명력 텍스트를 0으로 설정한다.
                    txtHealth.setText("0"); //방어도 텍스트를 0으로 설정한다.
                    rt.stopThread(); //재장전 스레드를 중지시킨다.
                    Toast.makeText(getApplicationContext(), "강제 종료되었습니다.\n뒤로가기 키 또는 뒤로 가기 버튼을 눌러 메인으로 돌아가십시오.", Toast.LENGTH_LONG).show(); //토스트로 종료되었음을 알려준다.
                    btnExit.setText("뒤로 가기"); //버튼의 텍스트를 "뒤로가기"로 변경한다. 다시 누르게 되면 액티비티가 꺼지게 만들어준다.
                } else {
                    finish(); //액티비티를 종료시킨다.
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
    } //텍스트 색상을 변경한다.
    public void hitBoom() {
        txtNowDemage.setTextColor(Color.parseColor("#C7A900"));
    } //위와 동일한 방식
    public void hitHeadshot() {
        txtNowDemage.setTextColor(Color.parseColor("#FF0000"));
    } //위와 동일한 방식
    public void defaultColor() { txtNowDemage.setTextColor(Color.parseColor("#F0F0F0")); } //위와 동일한 방식
    public void shelddefaultColor() {
        txtNowDemage.setTextColor(Color.parseColor("#A4A4FF"));
    } //위와 동일한 방식

    public void hitCritical_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#FF6600")); } //위와 동일한 방식
    public void hitboom_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#C7A900")); } //위와 동일한 방식
    public void hitHeadshot_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#FF0000")); } //위와 동일한 방식
    public void defaultColor_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#F0F0F0")); } //위와 동일한 방식
    public void shelddefaultColor_list(int index) { txtListDemage[index].setTextColor(Color.parseColor("#A4A4FF")); } //위와 동일한 방식

    public synchronized void changeCritical(boolean change) { //치명타 적용되면 액티비티에 치명타 이미지를 보여주는 메소드이다. change가 true = 치명타 적용됨, false = 치명타 적용 안됨
        if (change) imgCritical.setVisibility(View.VISIBLE); //적용되면 화면에 보여준다.
        else imgCritical.setVisibility(View.INVISIBLE); //적용되지 않으면 화면에서 숨긴다.
    }
    public synchronized void changeHeadshot(boolean change) { //위와 동일한 방식
        if (change) imgHeadshot.setVisibility(View.VISIBLE); //위와 동일한 방식
        else imgHeadshot.setVisibility(View.INVISIBLE); //위와 동일한 방식
    }
    public synchronized void changeBoom(boolean change) { //위와 동일한 방식
        if (change) imgBoom.setVisibility(View.VISIBLE); //위와 동일한 방식
        else imgBoom.setVisibility(View.INVISIBLE); //위와 동일한 방식
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility( //상단바, 소프트키를 화면에 안 보이게 해준다. 스와이프로 다시 일시적으로 나타난다.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
