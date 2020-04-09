package com.example.divisionsimulation.ui.share;

import com.dinuscxj.progressbar.CircleProgressBar;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.MainActivity;
import com.example.divisionsimulation.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;
    public static Context context = null; //프래그먼트 context를 저장할 객체
    private String NOTIFICATION_ID = ""; //상단에 알림 올 때 사용하는 채널 아이디

    private int reset_count = 0; //초기화 진행도 변수
    private boolean btnEnd = false; //초기화 완료 변수

    private AlertDialog.Builder buildera = null;
    private AlertDialog alertDialog = null;
    private View dialogViewa = null;
    /*
    다이얼로그 생성시 필요한 객체 생성
     */

    public static AlertDialog.Builder builder_timer = null;
    public static AlertDialog alertDialog_timer = null;
    public static View dialogView_timer = null;
    /*
    위와 동일
     */

    public AlertDialog.Builder builder_list = null;
    public AlertDialog alertDialog_list = null;
    public View dialogView_list = null;
     /*
    위와 동일
     */

    private LinearLayout layoutItemList; //아이템 목록 레이아웃 객체 생성
    private TextView view; //목록 아이템 목록 1줄당 나타낼 뷰 생성

    private Button btnInput = null; //다크존 가방 담는 버튼 생성

    final private int BIG = 1234567; //램덤 함수에 쓰일 고정형 변수

    private Button btnLitezone, btnDarkzone, btnRaid, btnRaidbox, btnReset, btnOutput, btnTruesun, btnDragov, btnNewYork, btnLastBoss, btnBox, btnItemList; //라이트존, 다크존, 칠흑의 시간 레이드, 레이드 박스, 초기화, 트루썬 막보, 드래고프, 뉴욕 필드보스, 전설난이도 막보, 세션 박스, 아이템 목록 버튼 객체 생성
    private TextView txtSpecial, txtNamed, txtGear, txtBrand, txtAll; //특급, 네임드, 기어, 일반, 전체 갯수 텍스트뷰 생성

    private CircleProgressBar progressSpecial, progressNamed, progressGear, progressBrand; //특급, 네임드, 기어, 브랜드의 백분율을 나타낼 진행바 객체 생성

    private int special = 0, named = 0, gear = 0, brand = 0, darkitem = 0, all = 0, temp; //특급, 네임드, 기어, 브랜드, 다크존 가방 아이템 갯수, 전체 갯수를 저장할 변수 생성

    private int[] typet = new int[13]; //돌격소총, 소총 등 드랍된 아이템 갯수를 저장할 배열 변수 생성

    private boolean one_time = false; //현재 사용하지 않는 변수

    private Handler handler; //UI 변경시 사용할 핸들러
    private NotificationChannel channel = null; //알림때 필요한 채널

    private CircleProgressBar progressReset = null; //초기화 진행바 객체 생성

    private NotificationManager notificationManager = null; //알림 매니저 생성

    private DarkZoneTimerThread coming_dz = null; //헬기 올 때까지 사용할 다크존 이송 스레드 생성
    private DarkZoneTimerThread output_dz = null; //헬기 도착 후 사용할 다크존 이송 스레드 생성

    private TextView[] txtTypelist = new TextView[13]; //장비 종류에 따른 갯수를 표현할 텍스트뷰
    private ProgressBar[] progressType = new ProgressBar[13]; //장비 종류에 따른 진행바

    private AlertDialog dialog_dark = null; //다크존 전용 다이얼로그 생성

    private TextView txtInfo = null; //이송 상태를 알려준다.
    private ProgressBar progressTimer = null; //이송 진행률을 진행바로 보여준다.
    private Button btnNowOutput = null; //가방 이송헬기에 걸 때 사용하는 버튼
    private TextView txtTimer = null; //이송 진행률을 숫자로 표현해준다.

    private RadioGroup rgDifficulty; //난이도 그룹
    private RadioButton[] rdoDiff = new RadioButton[4]; //난이도 버튼 목록 (스토리/보통, 어려움, 매우어려움, 영웅)
    private int bonus = 0, option_bonus = 0; //난이도별 추가 드랍률, 난이도별 장비 추가 보너스 옵션

    private String[] item_name = new String[50]; //얻었던 아이템을 50개까지 저장
    private String[] item_type = new String[50]; //얻었던 아이템 종류를 50개까지 저장
    private int index = 0; //아이템 목록에서 새로운 아이템을 추가할 때 추가한 배열 다음에 공간을 지정해주는 역할을 한다.

    private boolean openWeapon = false; //드랍된 장비가 무기일 때 사용
    private boolean openSheld = false; //드랍된 장비가 보호장구일 때 사용

    public void inputData(String name, String type) { //드랍되고 아이템 목록에 아이템이 추가 될 경우 사용
        if (index >= 50) { //아이템 목록이 최대 50개이므로 50개가 넘어갈 경우 작동된다.
            for (int i = 0; i < 49; i++) { //첫번째 아이템 정보는 사라지고 나머지는 앞으로 1칸씩 땡겨준다.
                item_name[i] = item_name[i+1]; //아이템 이름을 뒤에서 앞으로 1칸씩 땡긴다.
                item_type[i] = item_type[i+1]; //아이템 종류를 뒤에서 앞으로 1칸씩 땡긴다.
            }
            item_name[49] = name; //마지막 장비 이름에 새로운 장비 이름을 추가한다.
            item_type[49] = type; //마지막 장비 종류에 새로운 장비 종류를 추가한다.
        } else { //얻었던 장비가 총 50개 미만일 경우 작동한다.
            item_name[index] = name; //현재 index 공간에 새로운 장비 이름을 추가한다.
            item_type[index] = type; //현재 index 공간에 새로운 장비 종류를 추가한다.
        }
        index++; //장비가 목록에 추가될때마다 인덱스를 1씩 늘려주어 다음 배열 공간에 추가할 수 있도록 한다.
    }

    //public void setTxtInfo(String message) { txtInfo.setText(message); }
    public void setProgressTimer(int progress) { progressTimer.setProgress(progress); } //이송 진행률을 설정한다.
    public void setTxtTimer(String message) { txtTimer.setText(message); } //이송 진행 상태(초단위로 보여주는 TextView)를 설정한다.

    public synchronized void playOutputDZ() {
        notificationManager.cancelAll(); //현재 앱에서 사용된 알림을 모두 제거한다.
        NotificationCompat.Builder buildert = new NotificationCompat.Builder(context, NOTIFICATION_ID) //알림 상태를 설정한다.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                .setContentTitle("이송 진행 중...") //타이틀 TEXT
                .setContentText("이송 지점에서 이송 중...") //서브 타이틀 TEXT
                .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                .setSound(null)
                .setOngoing(true) // 사용자가 직접 못지우게 계속 실행하기.
        ;

        notificationManager.notify(0, buildert.build()); //위 알림 상태를 설정한 후 실행한다.
        handler.post(new Runnable() {
            @Override
            public void run() {
                btnNowOutput.setEnabled(true); //이송이 도착한 후엔 다크존 가방을 이송 헬기에 걸 수 있도록 해준다.
                txtInfo.setText("이송 완료까지 남은 시간"); //이송 상태를 업데이트한다.
            }
        });
        output_dz.setInput_rogue(coming_dz.getInput_rogue()); //이송 헬기 도착전 로그요원이 등장했으면 이후 스레드도 로그 등장 상태를 유지시킨다.
        output_dz.start(); //이송헬기 도착 후 스레드를 진행한다.
    }

    public void dialogOpen() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog_timer.dismiss(); //이송 중 다이얼로그를 닫는다.
                AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
                tbuilder.setMessage("이송 완료");
                tbuilder.setPositiveButton("확인", null);
                AlertDialog talertDialog = tbuilder.create();
                talertDialog.show();
                /*
                이송 완료를 다이얼로그로 알려준다.
                 */
            }
        });
        notificationManager.cancelAll(); //알림을 모두 제거한다.
        NotificationCompat.Builder buildert = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                .setContentTitle("이송 완료") //타이틀 TEXT
                .setContentText("이송이 끝났습니다.") //서브 타이틀 TEXT
                .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                .setSound(null)
                .setOngoing(false) // 사용자가 직접 못지우게 계속 실행하기.
        ;

        notificationManager.notify(0, buildert.build());
        /*
        위와 동일한 방식
         */
    }

    public void deleteDZitem() { //로그 요원에게 이송물을 탈취당하거나 이송을 완료하거나 초기화하였을 경우 작동한다. 다크존 아이템을 초기화시킬 때 사용한다.
        darkitem = 0; //현재 저장된 다크존 아이템 갯수를 초기화한다.
        btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
        btnOutput.setText("이송하기 ("+darkitem+"/10)");
        /*
        버튼에 있는 다크존 아이템 목록을 초기화한 상태로 업데이트한다.
         */
        alertDialog_timer.dismiss(); //이송 다이얼로그를 닫는다.
        Looper.prepare();
        //Toast.makeText(getActivity(), "로그 요원에게 이송물을 빼앗겼습니다.", Toast.LENGTH_SHORT).show();
        alertDialog_timer.dismiss();
        AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
        tbuilder.setMessage("로그 요원에게 이송물을 빼앗겼습니다.");
        tbuilder.setPositiveButton("확인", null);
        AlertDialog talertDialog = tbuilder.create();
        talertDialog.show();
        Looper.loop();
        /*
        로그 요원에게 이송물을 탈취되었다는 메시지를 올린다.
         */
    }

    public void addTextView(int number, String name, String type, LinearLayout layout) { //아이템 목록을 열었을 때 보여줄 택스브튜를 생성한다.
        String result = number+". "+name+" ("+type+")"; //텍스트 뷰에 메시지를 올릴 문자열을 적는다. 아이템 이름, 종류를 출력한다.

        SpannableString spannableString = new SpannableString(result); //텍스트뷰 메시지 일부 글자만 색을 바꿀 때 사용한다.

        /*
        특급 색 : 오렌지 비슷한 색상
        네임드 색 : 노란색
        기어 색 : 초록색
        기타 장비 : 기본 색인 하얀색
         */

        String word; //찾을 문자열
        int start, end; //시작번호, 끝번호
        Itemlist il = new Itemlist(); //모든 아이템 정보가 들어있는 객체 생성
        for (int i = 0; i < il.getNewSpecialweapon_Length(); i++) { //뉴욕의 지배자 확장팩 출시 후 등장한 엑조틱 장비들을 특급 색으로 변경해준다.
            word = il.getNewSpecialweapon(i); //찾을 문자열에 새로운 특급 장비 이름을 넣는다. 반복문으로 모든 엑조틱과 비교가 된다.
            start = result.indexOf(word); //찾을 문자열과 같은 문자열을 찾게되면 시작 번호를 알려줘 start 변수에 대입한다.
            end = start + word.length(); //시작번호로부터 찾을 문자열의 길이를 추가해 끝번호를 찾는다.
            if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c00")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //start가 -1이면 찾을 문자열이 없다는 뜻이므로 실행을 하지 않고 -1보다 크게 되면 찾았다는 뜻이므로 그 특정 부분만 특급 색으로 변경한다.
        }
        for (int i = 0; i < il.getSpecialweapon_raid_Length(); i++) {
            word = il.getSpecialweapon_raid(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c00")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        /*
        위와 동일한 방식
        뉴욕의 지배자 출시 전 엑조틱들을 특급 색으로 변경
         */
        for (int i = 0; i < il.getNamedweapon_lite_Length(); i++) {
            word = il.getNamedweapon_lite(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#c99700")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        /*
        위와 동일한 방식
        라이트존에서 등장하는 네임드 무기들을 네임드 색으로 변경한다.
         */
        for (int i = 0; i < il.getNamedweapon_dark_Length(); i++) {
            word = il.getNamedweapon_dark(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#c99700")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        /*
        위와 동일한 방식
        다크존에서 등장하는 네임드 무기들을 네임드 색으로 변경한다.
         */
        for (int i = 0; i < il.getNamedsheld_lite_Length(); i++) {
            word = il.getNamedsheld_lite(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#c99700")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        /*
        위와 동일한 방식
        라이트존에서 등장하는 네임드 보호장구들을 네임드 색으로 변경한다.
         */
        for (int i = 0; i < il.getNamedsheld_dark_Length(); i++) {
            word = il.getNamedsheld_dark(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#c99700")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        /*
        위와 동일한 방식
        다크존에서 등장하는 네임드 보호장구들을 네임드 색으로 변경한다.
         */
        for (int i = 0; i < il.getSheldgear_Length(); i++) {
            word = il.getSheldgear(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#009900")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        /*
        위와 동일한 방식
        기어 장비들을 기어 색으로 변경한다.
         */

        view = new TextView(getActivity()); //현재 액티비티 (다이얼로그)에 추가할 텍스트뷰를 생성
        view.setText(spannableString); //위에 색까지 적용한 텍스트를 저장한다.
        view.setTextSize(20); //텍스트 사이즈를 20sp로 설정한다.
        view.setTextColor(Color.parseColor("#aaaaaa")); //텍스트 색을 설정한다. 위에 색을 적용한 부분은 바뀌지 않는다.

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //레이아웃
        lp.gravity = Gravity.LEFT; //왼쪽 정렬시킨다.
        view.setLayoutParams(lp);

        layout.addView(view); //레이아웃에 텍스트뷰를 추가한다.
    }
    public void removeTextView() { //텍스트뷰 제거 메소드 현재 사용하지 않는다.
        if (view.getParent() != null) { //뷰의 부모가 비어있지 않다면 작동
            ((ViewGroup)view.getParent()).removeView(view);
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (reset_count >= 1500) btnEnd = true; //리셋카운트가 1500 이상이 되면 작동 (3초 이후)
            if (btnEnd) { //btnEnd가 참이 될 경우 작동한다.
                index = 0; //인덱스를 초기화한다.
                for (int i = 0; i < item_name.length; i++) { //아이템 목록의 갯수(50개)만큼 반복한다.
                    item_name[i] = null; //아이템 이름을 비운다.
                    item_type[i] = null; //아이템 종류를 비운다.
                }
                alertDialog.dismiss(); //다이얼로그를 닫는다.
                special = 0; //특급 갯수를 초기화한다.
                named = 0; //네임드 갯수를 초기화한다.
                gear = 0; //기어 갯수를 초기화한다.
                brand = 0; //브랜드 갯수를 초기화한다.
                txtSpecial.setText("0"); //특급 텍스트 뷰 내용 초기화
                txtNamed.setText("0"); //위와 동일한 방식
                txtGear.setText("0"); //위와 동일한 방식
                txtBrand.setText("0"); //위와 동일한 방식
                darkitem = 0; //다크존 아이템 갯수 초기화
                all = 0; //전체 아이템 갯수 초기화
                txtAll.setText("0"); //위와 동일한 방식
                progressBrand.setProgress(0); //브랜드 진행바 초기화
                progressGear.setProgress(0); //기어 진행바 초기화
                progressNamed.setProgress(0); //네임드 진행바 초기화
                progressSpecial.setProgress(0); //특급 진행바 초기화
                for (int i = 0; i < txtTypelist.length; i++) { //장비 종류 갯수만큼 반복
                    txtTypelist[i].setText("0"); //현재 종류 텍스트 뷰 내용 초기화
                    progressType[i].setProgress(0); //현재 종류 진행도 초기화
                    typet[i] = 0; //현재 종류 갯수 초기화
                }
                for (int i = 0; i < progressType.length; i++) progressType[i].setMax(20); //모든 종류 진행도 최대치 20으로 설정
                btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)"); //다크존 이송 갯수 초기화
                btnOutput.setText("이송하기 ("+darkitem+"/10)"); //위와 동일한 방식
                btnEnd = false; //초기화 완료 조건 초기화
                Toast.makeText(getActivity(), "모두 초기화 되었습니다.", Toast.LENGTH_SHORT).show(); //초기화가 되었다며 토스트를 통해 전달한다.
                mHandler.removeMessages(0); //현재 핸들러를 종료시킨다.
            } else { //아직 리셋카운트로 인해 btnEnd가 참이 되지 않았을 경우 작동
                reset_count += 10; //10을 늘려준다. (1500까지 3초 걸린다.)
                progressReset.setProgress(reset_count); //리셋 카운트만큼 진행도를 설정한다.
            }
            Log.v("LC버튼", "Long클릭"); //로그를 남긴다.
            mHandler.sendEmptyMessageDelayed(0, 20); //핸들러를 0.02초만큼 반복시킨다. (다시 핸들러를 불러오는 방식으로 반복시키는 것이다.)
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        /*final TextView textView = root.findViewById(R.id.text_share);
        shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        handler = new Handler(); //핸들러 객체를 생성한다.

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE); //현재 context에서 가져온다.

        NOTIFICATION_ID = "10001"; //알림 아이디 설정
        String NOTIFICATION_NAME = "동기화"; //알림 이름 설정
        int IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;

//채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //현재버전이 안드로이드 버전 오레오(O)보다 크면 작동한다.
            channel = new NotificationChannel(NOTIFICATION_ID, NOTIFICATION_NAME, IMPORTANCE); //알림 아이디, 이름 등을 토대로 채널을 생성한다.
            notificationManager.createNotificationChannel(channel);
        }

        btnLitezone = root.findViewById(R.id.btnLitezone);
        btnDarkzone = root.findViewById(R.id.btnDarkzone);
        btnRaid = root.findViewById(R.id.btnRaid);
        btnRaidbox = root.findViewById(R.id.btnRaidbox);
        btnReset = root.findViewById(R.id.btnReset);
        btnOutput = root.findViewById(R.id.btnOutput);

        btnTruesun = root.findViewById(R.id.btnTruesun);
        btnLastBoss = root.findViewById(R.id.btnLastBoss);
        btnDragov = root.findViewById(R.id.btnDragov);
        btnNewYork = root.findViewById(R.id.btnNewYork);
        btnBox = root.findViewById(R.id.btnBox);

        txtSpecial = root.findViewById(R.id.txtSpecial);
        txtNamed = root.findViewById(R.id.txtNamed);
        txtGear = root.findViewById(R.id.txtGear);
        txtBrand = root.findViewById(R.id.txtBrand);

        btnItemList = root.findViewById(R.id.btnItemList);

        txtAll = root.findViewById(R.id.txtAll);

        progressSpecial = root.findViewById(R.id.progressSpecial);
        progressNamed = root.findViewById(R.id.progressNamed);
        progressGear = root.findViewById(R.id.progressGear);
        progressBrand = root.findViewById(R.id.progressBrand);
        /*
        아이디를 통해 UI 객체에 대입
         */

        for (int i = 0; i < typet.length; i++) typet[i] = 0; //종류 갯수 초기화 과정

        int temp;
        for (int i = 0; i < txtTypelist.length; i++) {
            temp = root.getResources().getIdentifier("txtType"+(i+1), "id", getActivity().getPackageName());
            txtTypelist[i] = root.findViewById(temp);
            temp = root.getResources().getIdentifier("progressType"+(i+1), "id", getActivity().getPackageName());
            progressType[i] = root.findViewById(temp);
            progressType[i].setMax(20);
            progressType[i].setProgress(0);
        }
        /*
        반복되는 것으로 배열을 사용하여 아이디를 대입한다.
        진행도도 아이디 대입한 후 진행도를 0으로 맞추고 최대치도 20으로 설정한다.
         */

        progressSpecial.setProgressStartColor(Color.parseColor("#fe6e0e"));
        progressSpecial.setProgressEndColor(Color.parseColor("#fe6e0e"));
        progressSpecial.setProgressBackgroundColor(Color.parseColor("#888888"));
        progressSpecial.setLineWidth(40);
        progressNamed.setProgressStartColor(Color.parseColor("#fe6e0e"));
        progressNamed.setProgressEndColor(Color.parseColor("#fe6e0e"));
        progressNamed.setProgressBackgroundColor(Color.parseColor("#888888"));
        progressNamed.setLineWidth(40);
        progressGear.setProgressStartColor(Color.parseColor("#fe6e0e"));
        progressGear.setProgressEndColor(Color.parseColor("#fe6e0e"));
        progressGear.setProgressBackgroundColor(Color.parseColor("#888888"));
        progressGear.setLineWidth(40);
        progressBrand.setProgressStartColor(Color.parseColor("#fe6e0e"));
        progressBrand.setProgressEndColor(Color.parseColor("#fe6e0e"));
        progressBrand.setProgressBackgroundColor(Color.parseColor("#888888"));
        progressBrand.setLineWidth(40);
        /*
        특급, 네임드, 기어, 브랜드 진행도의 색을 찾우고 선 두께도 설정한다.
         */

        progressSpecial.setMax(10000);
        progressNamed.setMax(10000);
        progressGear.setMax(10000);
        progressBrand.setMax(10000);
        /*
        특급, 네임드, 기어, 브랜드 진행도의 최대치를 설정한다.
         */

        rgDifficulty = root.findViewById(R.id.rgDifficulty);
        int id_number;
        for (int i = 0; i < rdoDiff.length; i++) {
            id_number = root.getResources().getIdentifier("rdoDif"+(i+1), "id", getActivity().getPackageName());
            rdoDiff[i] = (RadioButton) root.findViewById(id_number);
        }
        /*
        난이도 UI 객체에 아이디를 찾아 대입
         */

        rgDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) { //난이도를 선택할 때마다 작동
                switch (checkedId) {
                    case R.id.rdoDif1: //스토리/보통
                        bonus = 0; //보너스 드랍 확률
                        option_bonus = 0; //추가 옵션
                        break;
                    case R.id.rdoDif2: //어려움
                        bonus = 5;
                        option_bonus = 30;
                        break;
                    case R.id.rdoDif3: //매우어려움
                        bonus = 10;
                        option_bonus = 50;
                        break;
                    case R.id.rdoDif4: //영웅
                        bonus = 15;
                        option_bonus = 70;
                        break;
                }
            }
        });

        final Itemlist il = new Itemlist(); //모든 아이템 정보가 들어있다.

        final View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null); //아이템 드랍할때마다 보여줄 뷰이다.

        final TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
        final TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
        final Button btnChange = dialogView.findViewById(R.id.btnChange); //특급, 네임드일 경우 내용을 바로 보여주지 않고 이 버튼으로 누르면 보여주도록 해준다.
        final TableLayout tableMain = dialogView.findViewById(R.id.tableMain); //내용이 들어있는 테이블 레이아웃
        //final ImageView[] imgOption = new ImageView[3];
        //final TableRow trOption = dialogView.findViewById(R.id.trOption);
        final Button btnExit = dialogView.findViewById(R.id.btnExit); //닫기 버튼

        final TextView txtWMain1 = dialogView.findViewById(R.id.txtWMain1); //첫번째 무기 핵심속성
        final TextView txtWMain2 = dialogView.findViewById(R.id.txtWMain2); //두번째 무기 핵심속성
        final TextView txtWSub = dialogView.findViewById(R.id.txtWSub); //무기 속성
        final ProgressBar progressWMain1 = dialogView.findViewById(R.id.progressWMain1); //첫번째 무기 핵심속성 진행도
        final ProgressBar progressWMain2 = dialogView.findViewById(R.id.progressWMain2); //두번재 무기 핵심속성 진행도
        final ProgressBar progressWSub = dialogView.findViewById(R.id.progressWSub); //무기 속성 진행도
        final TextView txtWTalent = dialogView.findViewById(R.id.txtWTalent); //무기 탤런트

        final TextView txtSMain = dialogView.findViewById(R.id.txtSMain); //보호장구 핵심속성
        final TextView txtSSub1 = dialogView.findViewById(R.id.txtSSub1); //첫번째 보호장구 속성
        final TextView txtSSub2 = dialogView.findViewById(R.id.txtSSub2); //두번째 보호장구 속성
        final ProgressBar progressSMain = dialogView.findViewById(R.id.progressSMain); //보호장구 핵심속성 진행도
        final ProgressBar progressSSub1 = dialogView.findViewById(R.id.progressSSub1); //첫번째 보호장구 속성 진행도
        final ProgressBar progressSSub2 = dialogView.findViewById(R.id.progressSSub2); //두번째 보호장구 속성 진행도
        final ImageView imgSMain = dialogView.findViewById(R.id.imgSMain); //보호장구 핵심속성 타입 이미지
        final ImageView imgSSub1 = dialogView.findViewById(R.id.imgSSub1); //첫번재 보호장구 속성 타입 이미지
        final ImageView imgSSub2 = dialogView.findViewById(R.id.imgSSub2); //두번째 보호장구 속성 타입 이미지

        final LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
        final LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
        /*
        위들과 동일한 방식이지만 프래그먼트에서 추가하는 것이 아닌 위 다이얼로그뷰에서 추가한다.
         */


        /*for (int i = 0; i < imgOption.length; i++) {
            temp = dialogView.getResources().getIdentifier("imgOption"+(i+1), "id", getActivity().getPackageName());
            imgOption[i] = dialogView.findViewById(temp);
        }*/

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View dark_dialogView = getLayoutInflater().inflate(R.layout.itemlayout_dark, null);

        final TextView txtName2 = dark_dialogView.findViewById(R.id.txtName);
        final TextView txtType2 = dark_dialogView.findViewById(R.id.txtType);
        final Button btnChange2 = dark_dialogView.findViewById(R.id.btnChange);
        final TableLayout tableMain2 = dark_dialogView.findViewById(R.id.tableMain);
        final Button btnExit2 = dark_dialogView.findViewById(R.id.btnExit);

        final TextView txtWMain1_dark = dark_dialogView.findViewById(R.id.txtWMain1);
        final TextView txtWMain2_dark = dark_dialogView.findViewById(R.id.txtWMain2);
        final TextView txtWSub_dark = dark_dialogView.findViewById(R.id.txtWSub);
        final ProgressBar progressWMain1_dark = dark_dialogView.findViewById(R.id.progressWMain1);
        final ProgressBar progressWMain2_dark = dark_dialogView.findViewById(R.id.progressWMain2);
        final ProgressBar progressWSub_dark = dark_dialogView.findViewById(R.id.progressWSub);
        final TextView txtWTalent_dark = dark_dialogView.findViewById(R.id.txtWTalent);

        final TextView txtSMain_dark = dark_dialogView.findViewById(R.id.txtSMain);
        final TextView txtSSub1_dark = dark_dialogView.findViewById(R.id.txtSSub1);
        final TextView txtSSub2_dark = dark_dialogView.findViewById(R.id.txtSSub2);
        final ProgressBar progressSMain_dark = dark_dialogView.findViewById(R.id.progressSMain);
        final ProgressBar progressSSub1_dark = dark_dialogView.findViewById(R.id.progressSSub1);
        final ProgressBar progressSSub2_dark = dark_dialogView.findViewById(R.id.progressSSub2);
        final ImageView imgSMain_dark = dark_dialogView.findViewById(R.id.imgSMain);
        final ImageView imgSSub1_dark = dark_dialogView.findViewById(R.id.imgSSub1);
        final ImageView imgSSub2_dark = dark_dialogView.findViewById(R.id.imgSSub2);

        final LinearLayout layoutWeapon_dark = dark_dialogView.findViewById(R.id.layoutWeapon);
        final LinearLayout layoutSheld_dark = dark_dialogView.findViewById(R.id.layoutSheld);
        /*
        다크존전용 다이얼로그 생성
         */

        btnInput = dark_dialogView.findViewById(R.id.btnInput); //다크존 아이템을 다크존 가방에 담는 버튼이다.

        btnExit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_dark.dismiss();
            }
        }); //닫기 버튼을 누르게 되면 다크존 다이얼로그가 닫히게 된다.

        final AlertDialog.Builder builder_dark = new AlertDialog.Builder((getActivity()));

        btnItemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //아이템 목록 보기 버튼
                dialogView_list = getLayoutInflater().inflate(R.layout.itemlistlayout, null); //다이얼로그에 추가할 뷰 생성
                layoutItemList = dialogView_list.findViewById(R.id.layoutItemList); //아이템을 추가할 레이아웃
                Button btnExit = dialogView_list.findViewById(R.id.btnExit); //팝업창을 닫을 버튼

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //버튼을 누르면 작동
                        alertDialog_list.dismiss(); //다이얼로그를 닫는다.
                    }
                });

                for (int i = 0; i < item_name.length; i++) { //아이템 목록의 최대치(50)만큼 반복한다.
                    if (item_name[i] != null) addTextView(i+1, item_name[i], item_type[i], layoutItemList); //아이템 이름이 비어있지 않다면 그 아이템 정보를 통해 텍스트뷰를 생성한다. (위쪽에 메소드 존재)
                }

                builder_list = new AlertDialog.Builder(getActivity());
                builder_list.setView(dialogView_list);

                alertDialog_list = builder_list.create();
                alertDialog_list.setCancelable(false);
                alertDialog_list.show();
                /*
                다이얼로그 생성 및 띄우기
                 */
            }
        });

        btnReset.setOnLongClickListener(new Button.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) { //초기화 버튼, 길게 눌러야 작동된다.
                dialogViewa = getLayoutInflater().inflate(R.layout.resetlayout, null); //다이얼로그에 추가할 뷰 생성
                progressReset = dialogViewa.findViewById(R.id.progressReset);
                progressReset.setMax(1500);
                progressReset.setProgress(0);
                reset_count = 0;
                /*
                프로그레스바를 불러오고 최대치, 처음 진행도, 카운트를 설정한다.
                리셋 카운트는 리셋 카운트가 1500이 되게 되면 초기화해주는 역할을 수행한다.
                 */

                Button btnExit = dialogViewa.findViewById(R.id.btnExit); //다이얼로그를 닫는 버튼을 불러온다.
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss(); //다이얼로그를 닫아준다.
                    }
                });

                buildera = new AlertDialog.Builder(getActivity());
                buildera.setView(dialogViewa);

                alertDialog = buildera.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                /*
                다이얼로그 생성 및 지우기
                 */

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        reset_count = 0;
                        progressReset.setProgress(0);
                        alertDialog.dismiss();
                        mHandler.removeMessages(0);
                        /*
                        초기화 작업
                        핸들러 메시지를 지움으로써 핸들러가 무한 반복되는 것을 종료한다.
                         */
                    }
                });

                mHandler.sendEmptyMessageDelayed(0, 20); //0.02초 딜레이를 주고 핸들러 메시지를 보내 작업한다.

                return false;
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "길게 누르십시오.", Toast.LENGTH_SHORT).show(); //리셋버튼을 한번만 누르게 되면 길게 누르라며 토스트를 통해 전달한다.
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //네임드, 특급 전용 버튼 누르면 내용이 공개된다.
                if (openWeapon) layoutWeapon.setVisibility(View.VISIBLE); //드랍된 장비가 무기일 경우 무기레이아웃을 보여준다.
                else layoutWeapon.setVisibility(View.GONE); //드랍된 장비가 무기가 아닐 경우 무기레이아웃을 숨긴다.
                if (openSheld) layoutSheld.setVisibility(View.VISIBLE); //드랍된 장비가 보호장구일 경우 보호장구 레이아웃을 보여준다.
                else layoutSheld.setVisibility(View.GONE); //드랍된 장비가 보호장구가 아닐 경우 보호장구 레이아웃을 숨긴다.
                tableMain.setVisibility(View.VISIBLE); //아이템 정보를 공개한다.
                btnChange.setVisibility(View.GONE); //버튼은 보이지 않게 숨긴다.
            }
        });

        btnChange2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일 (다크존 전용)
                if (openWeapon) layoutWeapon_dark.setVisibility(View.VISIBLE);
                else layoutWeapon_dark.setVisibility(View.GONE);
                if (openSheld) layoutSheld_dark.setVisibility(View.VISIBLE);
                else layoutSheld_dark.setVisibility(View.GONE);
                tableMain2.setVisibility(View.VISIBLE);
                btnChange2.setVisibility(View.GONE);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() { //닫기 버튼 누를 경우 작동
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); //닫기 버튼을 누르면 다이얼로그가 닫힌다.
            }
        });

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //이송 물품 다크존 가방에 담는 버튼
                if (darkitem < 10) { //다크존 가방에 있는 아이템 갯수가 10개 미만일 경우
                    darkitem++; //다크존 가방 아이템 수를 1 늘려준다.
                    btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)"); //버튼 텍스트를 업데이트한다.
                    btnOutput.setText("이송하기 ("+darkitem+"/10)"); //위와 동일
                    dialog_dark.dismiss(); //다크존 다이얼로그를 닫는다.
                } else { //다크존 가방에 있는 아이템 갯수가 10개 이상을 경우
                    AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
                    tbuilder.setMessage("다크존 가방이 가득찼습니다.");
                    tbuilder.setPositiveButton("확인", null);
                    AlertDialog talertDialog = tbuilder.create();
                    talertDialog.show();
                    /*
                    다크존 가방이 가득찼다고 알려준다.
                     */
                }
            }
        });

        btnOutput.setOnClickListener(new View.OnClickListener() { //이송하는 버튼
            @Override
            public void onClick(View v) {
                if (darkitem != 0) { //다크존 아이템이 하나도 없을 경우
                    notificationManager.cancelAll(); //현재 앱에 관련된 모든 알림 제거

                    NotificationCompat.Builder buildert = new NotificationCompat.Builder(context, NOTIFICATION_ID) //알림을 설정한다.
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                            .setContentTitle("이송 진행 중...") //타이틀 TEXT
                            .setContentText("이송 지점에서 이송 헬기를 대기 중...") //서브 타이틀 TEXT
                            .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                            .setSound(null)
                            .setOngoing(true) // 사용자가 직접 못지우게 계속 실행하기.
                            ;

                    notificationManager.notify(0, buildert.build()); //알림 띄우기


                    dialogView_timer = getLayoutInflater().inflate(R.layout.timercominglayout, null); //타이머 뷰 생성

                    txtInfo = dialogView_timer.findViewById(R.id.txtInfo); //현재 이송 상태를 알려준다.
                    progressTimer = dialogView_timer.findViewById(R.id.progressTimer); //타이머 진행도를 알려준다.
                    btnNowOutput = dialogView_timer.findViewById(R.id.btnNowOutput); //이송헬기에 가방을 걸 버튼이다.
                    txtTimer = dialogView_timer.findViewById(R.id.txtTimer); //남은 시간을 나타낸다.

                    btnNowOutput.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { //가방을 거는 버튼을 눌렀을 경우
                            darkitem = 0; //다크존 아이템을 초기화한다.
                            btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
                            btnOutput.setText("이송하기 ("+darkitem+"/10)");
                            /*
                            기타 버튼들에게도 업데이트한다.
                             */
                            Toast.makeText(getActivity(), "헬기에 이송물을 걸었습니다.", Toast.LENGTH_SHORT).show(); //토스트를 통해 이송물을 걸었다는 것을 알려준다.
                            btnNowOutput.setEnabled(false); //이송물 걸기 버튼을 비활성화시킨다.
                        }
                    });

                    coming_dz = new DarkZoneTimerThread(handler, getActivity(), ShareFragment.this); //헬기 오기 전 타이머 스레드
                    output_dz = new DarkZoneTimerThread(handler, getActivity(), ShareFragment.this); //헬기 도착 후 타이머 스레드

                    progressTimer.setMax(10000); //타이머의 최대치를 설정
                    progressTimer.setProgress(0); //타이머의 진행도를 초기화

                    builder_timer = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle); //타이머 빌더를 생성
                    builder_timer.setView(dialogView_timer);
                    builder_timer.setPositiveButton("즉시 이송", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //즉시 이송을 누르면 작동
                            darkitem = 0; //다크존 아이템을 0으로 초기화
                            btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)"); //위와 동일한 방식
                            btnOutput.setText("이송하기 ("+darkitem+"/10)"); //위와 동일한 방식
                            Toast.makeText(getActivity(), "즉시 이송시켰습니다.", Toast.LENGTH_SHORT).show(); //즉시 이송 완료 메시지를 토스트로 통해 알려준다.
                            coming_dz.stopThread(); //헬기 오기 전 스레드를 종료
                            output_dz.stopThread(); //헬기 도착 후 스레드를 종료
                            output_dz.setRogue(true); //로그 세팅
                            coming_dz.setRogue(true); //위와 동일
                            notificationManager.cancelAll(); //모든 알림 제거
                            NotificationCompat.Builder buildert = new NotificationCompat.Builder(context, NOTIFICATION_ID) //새로운 알림 설정
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                                    .setContentTitle("이송 완료") //타이틀 TEXT
                                    .setContentText("이송이 끝났습니다.") //서브 타이틀 TEXT
                                    .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                                    .setSound(null)
                                    .setOngoing(false) // 사용자가 직접 못지우게 계속 실행하기.
                            ;

                            notificationManager.notify(0, buildert.build()); //새로운 알림을 띄운다.
                        }
                    });
                    builder_timer.setNeutralButton("이송지점 벗어나기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //이송지점을 벗어날 경우
                            if (darkitem > 0) { //다크존 아이템이 0보다 크면 작동한다. 즉, 이송물을 헬기에 걸기 전을 나타낸다.
                                Toast.makeText(getActivity(), "이송하지 않고 이송지점에서 벗어났습니다.", Toast.LENGTH_SHORT).show(); //이송지점에서 벗어낫다는 메시지를 토스트로 전달
                                coming_dz.stopThread();
                                output_dz.stopThread();
                                output_dz.setRogue(true);
                                coming_dz.setRogue(true);
                                /*
                                스레드들을 종료
                                 */
                            } else {
                                if (percent(1, 4) == 1) Toast.makeText(getActivity(), "이송물은 버려둔 채로 이송지점에서 벗어났지만 이송에 성공하였습니다.", Toast.LENGTH_SHORT).show(); //25% 확률로 자동으로 이송 성공
                                else Toast.makeText(getActivity(), "이송물은 버려둔 채로 이송지점에서 벗어났으나 로그 요원에게 이송물을 탈취당했습니다.", Toast.LENGTH_SHORT).show(); //75% 확률로 로그요원들에게 탈취
                                coming_dz.stopThread();
                                output_dz.stopThread();
                                output_dz.setRogue(true);
                                coming_dz.setRogue(true);
                                /*
                                스레드들을 종료
                                 */
                            }
                            notificationManager.cancelAll(); //모든 알림 제거
                            NotificationCompat.Builder buildert = new NotificationCompat.Builder(context, NOTIFICATION_ID) //새로운 알림 생성
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                                    .setContentTitle("이송 완료") //타이틀 TEXT
                                    .setContentText("이송이 끝났습니다.") //서브 타이틀 TEXT
                                    .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                                    .setSound(null)
                                    .setOngoing(false) // 사용자가 직접 못지우게 계속 실행하기.
                            ;

                            notificationManager.notify(0, buildert.build()); //새로운 알림을 띄운다.
                        }
                    });
                    builder_timer.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) { //다이얼로그가 꺼지게 될 경우
                            coming_dz.stopThread();
                            output_dz.stopThread();
                            output_dz.setRogue(true);
                            coming_dz.setRogue(true);
                            /*
                            스레드들을 종료
                             */
                        }
                    });
                    alertDialog_timer = builder_timer.create(); //다이얼로그 객체 생성
                    alertDialog_timer.setCancelable(false); //바깥영역 또는 뒤로가기 버튼을 눌러도 꺼지지 않게 해준다.
                    alertDialog_timer.show(); //다이얼로그를 화면에 띄운다.

                    coming_dz.setMinute(0); //헬기 도착 전 스레드 분을 0으로 설정
                    coming_dz.setSecond(40); //헬기 도착 전 스레드 초를 40초로 설정

                    output_dz.setMinute(1);
                    output_dz.setSecond(0);
                    //1분 타이머를 설정

                    coming_dz.setRoguePercent(1);
                    output_dz.setRoguePercent(2);
                    /*
                    헬기 도착 전 스레드에서 로그 등장 빈도를 1%로 설정 (1초마다)
                    헬기 도착 후 스레드에서 로그 등장 빈도를 2%로 설정
                     */

                    coming_dz.setOutputing(false);
                    output_dz.setOutputing(true);
                    //헬기 도착 여부 설정 true : 도착함, false : 도착하지 않음

                    coming_dz.start(); //헬기 도착 전 스레드를 실행

                } else {
                    AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
                    tbuilder.setMessage("이송할 아이템이 없습니다.");
                    tbuilder.setPositiveButton("확인", null);
                    AlertDialog talertDialog = tbuilder.create();
                    talertDialog.show();
                    //이송하기 전 이송할 아이템이 없다면 이송할 아이템이 없다고 다이얼로그를 통해 전달한다.
                }
            }
        });

        btnTruesun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //트루썬 마지막 보스 처치할 경우
                int pick, temp_percent; //램덤 난수가 저장될 변수
                double now_option; //임시로 저장될 옵션 수치
                int type = 0; // 1:attack, 2:sheld, 3:power 옵션 종류 (화기, 방어, 전력)
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 10+(bonus*4)) { //특급 장비
                    if (percent(0, 2) == 1) { //50% 확률로 작동된다.
                        txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                        special++; //특급 장비 갯수를 1개 늘린다.
                        all++; //총 아이템 갯수를 1개 늘린다.
                        setInterface(); //UI에 변경된 내용을 업데이트한다.
                        txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                        tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                        btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                        btnChange.setText("특급"); //버튼의 이름을 "특급"으로 바꾼다.
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                        txtName.setText("\"타디그레이드\" 방탄복 시스템"); //아이템 이름을 바꾼다.
                        txtType.setText("조끼"); //아이템 종류르 바꾼다.

                        type = 2; //""타디그레이드" 방탄복 시스템"은 핵심 속성이 방어구가 기본으로 170000 극옵션을 가지고 있다.
                        openSheld = true; //보호장구 여부를 체크한다.
                        temp_option = il.getSheldMainOption(1); //아이템 객체에서 엑조틱 이름("타디그레이드" 방탄복 시스템)을 가져온다.
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10); //핵심속성 최대치를 설정
                        now_option = 170000; //핵심속성 수치를 설정 (보호장구 엑조틱은 핵심속성이 최대치로 고정)
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00")); //핵심속성 수치가 최대치보다 크거나 같을 때 글자색을 주황색으로 변경
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa")); //핵심속성 수치가 최대치보다 작을 때 글자색을 기본색으로 변경한다.
                        progressSMain.setProgress((int)(now_option*10)); //핵심속성 수치 진행도를 설정한다.
                        txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option); //텍스트뷰에 핵심속성 수치와 이름을 넣는다.
                        switch (type) {
                            case 1: //옵션 종류가 화기일 경우
                                imgSMain.setImageResource(R.drawable.attack); //화기 아이콘으로 변경한다.
                                break;
                            case 2: //옵션 종류가 방어일 경우
                                imgSMain.setImageResource(R.drawable.sheld); //방어 아이콘으로 변경한다.
                                break;
                            case 3: //옵션 종류가 전력일 경우
                                imgSMain.setImageResource(R.drawable.power); //전력 아이콘으로 변경한다.
                                break;
                                default: //그 외 다른 것이 들어올 경우 (오류)
                                    imgSMain.setImageResource(R.drawable.critical); //크리티컬 아이콘으로 변경하여 잘못되었다는 것을 보여준다. 실제로는 일어날 일은 거의 없을 것이다.
                        }
                        type = 2; //방어구 옵션
                        switch (type) {
                            case 1: //옵션 종류가 화기일 경우
                                imgSSub1.setImageResource(R.drawable.attack); //속성1 아이콘을 화기로 변경
                                temp_option = il.getSheldSubWeaponOption(); //화기 속성 중 램덤으로 하나를 선정하여 가져온다.
                                pick = percent(1, 100); //1~100까지의 무작위 난수 저장
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00")); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa")); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10); //속성1의 최대치 설정
                                progressSSub1.setProgress((int)(now_option*10)); //속성1의 진행도 설정
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option); //결과를 텍스트뷰에 설정
                                break;
                            case 2: //옵션 종류가 방어일 경우, 위와 동일한 방식
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3: //옵션 종류가 전력일 경우, 위와 동일한 방식
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = 2; //방어구 옵션
                        switch (type) { //위와 동일한 방식
                            case 1: //화기
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2: //방어
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3: //전력
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                    } else { //"타디그레이드" 방탄복 시스템이 아닐 경우
                        txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                        special++; //특급 갯수를 1개 늘린다.
                        all++; //총 갯수를 1개 늘린다.
                        setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                        txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                        tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                        btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                        btnChange.setText("특급"); //버튼 텍스트를 바꾼다.
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial)); //버튼 배경을 주황색 계열로 바꾼다.
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                        pick = percent(0, il.getSpecialweapon_Length()); //0~특급 아이템 갯수 중 임의의 숫자를 가져온다.
                        txtName.setText(il.getSpecialweapon(pick)); //임의의 숫자에 해당하는 자리의 배열 값(장비이름)을 가져온다.
                        txtType.setText(il.getSpecialweapon_type(pick)); //위와 동일한 방식

                        switch (il.getSpecialweapon_type(pick)) { //임의의 숫자에 해당하는 자리의 배열 값(장비 종류)을 비교
                            case "소총": case "산탄총": case "지정사수소총": case "권총": case "돌격소총": case "기관단총": //가져온 값이 왼쪽에 있는 문자열과 같을 경우 작동
                                openWeapon = true; //무기 옵션 레이아웃을 보이게 할 값을 참으로 바꾼다.
                                temp_option = String.valueOf(txtType.getText()); //장비 종류에 있는 텍스트를 가져온다.
                                progressWMain1.setMax(150); //핵심속성1 최대치를 설정
                                pick = percent(1, 100); //1~100 중 임의의 숫자를 저장
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 높은 옵션을 부여
                                else temp_percent = percent(1, 20) + option_bonus; //80% 확률로 일반적인 옵션을 부여
                                now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0; //옵션 수치를 설정
                                if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00")); //핵심속성1이 가지는 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else txtWMain1.setTextColor(Color.parseColor("#aaaaaa")); //핵심속성1이 가지는 수치가 최대치보다 작을 경우 기본색으로 변경한다.
                                progressWMain1.setProgress((int)(now_option*10)); //핵심속성1의 진행도를 설정
                                txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지"); //핵심속성1 텍스트뷰에 결과를 넣는다.

                                temp_option = il.getWeaponMainOption(temp_option);
                                progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain2.setProgress((int)(now_option*10));
                                txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);
                                //위와 동일한 방식으로 핵심속성2를 정한다.

                                temp_option = il.getWeaponSubOption();
                                progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWSub.setProgress((int)(now_option*10));
                                txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);
                                //위와 동일한 방식으로 속성을 정한다.

                                txtWTalent.setText(il.getSpecialTalent(String.valueOf(txtName.getText()))); //무기 탤런트는 종류에 해당하는 탤런트를 임의로 하나를 가져온다.
                                break;
                            case "장갑": //위와 동일한 방식
                                type = 3;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(2);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 1;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;
                            case "무릎 보호대": //위와 동일한 방식
                                type = 2;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(1);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 170000;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;

                        }
                    }
                } else if (percent(1, 1000) <= 50+(bonus*4)) { //네임드 장비 확률 : 5%(시스템 : 50)
                    named++; //네임드의 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //변경된 데이터를 UI에 업데이트한다.
                    txtAll.setText(Integer.toString(all)); //변경된 총 갯수를 UI에 업데이트한다.
                    txtNamed.setText(Integer.toString(named)); //위와 동일한 방식
                    txtName.setTextColor(Color.parseColor("#c99700")); //장비 이름의 색을 어두운 노란색으로 변경한다.
                    tableMain.setVisibility(View.GONE); //장비 정보 레이아웃을 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //장비 공개 버튼을 보여준다.
                    btnChange.setText("네임드"); //버튼의 이름을 "네임드"로 변경한다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomnamed)); //버튼의 배경을 어두운 노란색 계열로 변경한다.
                    if (percent(1, 2) == 1) { //weapon, 위 특급과 비슷한 방식이다. 특급 장비들 대신 네임드 장비로 비교하면 된다. 이하 조석 설명 생략함.
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNamedWeaponLiteTalent(String.valueOf(txtName.getText())));

                    } else { //sheld
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        /*switch (il.getNamedsheld_lite_type(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }*/
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));

                        type = percent(1, 3);
                        openSheld = true;
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++; //브랜드 갯수에 1개를 늘려준다.
                        all++; //총 갯수를 1개 늘려준다.
                        setInterface(); //변경된 데이터 값들을 UI에 업데이트한다.
                        txtAll.setText(Integer.toString(all)); //총 갯수를 업데이트한다.
                        txtBrand.setText(Integer.toString(brand)); //위와 동일한 방식
                        pick = percent(0, il.getWeapontype_Length()); //0~무기 종류 갯수 중 임의의 숫자를 가져온다.
                        int temp; //이후에 사용될 변수
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length()); //0~돌격소총 목록에 장비 갯수 중 임의의 숫자를 가져온다.
                                txtName.setText(il.getWeaponlist1(temp)); //임의의 숫자에 해당하는 배열 값(무기 이름)을 장비 이름에 업데이트한다.
                                txtType.setText(il.getWeapontype(pick)); //위와 동일한 방식
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length()); //위와 동일한 방식
                                txtName.setText(il.getWeaponlist2(temp)); //위와 동일한 방식
                                txtType.setText(il.getWeapontype(pick)); //위와 동일한 방식
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length()); //위와 동일한 방식
                                txtName.setText(il.getWeaponlist3(temp)); //위와 동일한 방식
                                txtType.setText(il.getWeapontype(pick)); //위와 동일한 방식
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length()); //위와 동일한 방식
                                txtName.setText(il.getWeaponlist4(temp)); //위와 동일한 방식
                                txtType.setText(il.getWeapontype(pick)); //위와 동일한 방식
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length()); //위와 동일한 방식
                                txtName.setText(il.getWeaponlist5(temp)); //위와 동일한 방식
                                txtType.setText(il.getWeapontype(pick)); //위와 동일한 방식
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length()); //위와 동일한 방식
                                txtName.setText(il.getWeaponlist6(temp)); //위와 동일한 방식
                                txtType.setText(il.getWeapontype(pick)); //위와 동일한 방식
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length()); //위와 동일한 방식
                                txtName.setText(il.getWeaponlist7(temp)); //위와 동일한 방식
                                txtType.setText(il.getWeapontype(pick)); //위와 동일한 방식
                                break;
                            default:
                                txtName.setText("Error"); //위에 해당하는 것이 없을 경우 장비 이름에 에러를 올린다.
                                txtType.setText("Error"); //위와 동일한 방식
                        }

                        /*
                        아래 부분도 윗 부분에서 설명했듯이 비슷한 방식이다.
                        아래 부분도 주석 설명 생략함.
                         */

                        openWeapon = true;
                        layoutWeapon.setVisibility(View.VISIBLE);
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getWeaponTalent(String.valueOf(txtType.getText())));

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length()); //위와 동일한 방식
                        txtType.setText(il.getSheldtype(pick)); //위와 동일한 방식
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        switch (il.getSheldtype(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(1, 100); //위와 동일한 방식
                        if (pick <= 20) { //gear
                            gear++; //기어 갯수를 1개 늘려준다.
                            all++; //위와 동일한 방식
                            setInterface(); //위와 동일한 방식
                            txtAll.setText(Integer.toString(all)); //위와 동일한 방식
                            txtGear.setText(Integer.toString(gear)); //위와 동일한 방식
                            txtName.setTextColor(Color.parseColor("#009900")); //위와 동일한 방식
                            pick = percent(0, il.getSheldgear_Length()); //위와 동일한 방식
                            txtName.setText(il.getSheldgear(pick)); //위와 동일한 방식
                        } else { //brand
                            brand++; //위와 동일한 방식
                            all++; //위와 동일한 방식
                            setInterface(); //위와 동일한 방식
                            txtAll.setText(Integer.toString(all)); //위와 동일한 방식
                            txtBrand.setText(Integer.toString(brand)); //위와 동일한 방식
                            pick = percent(0, il.getSheldbrand_Length()); //위와 동일한 방식
                            /*switch (il.getSheldbrand(pick)) {
                                case "알프스 정상 군수산업":
                                case "아이랄디 홀딩":
                                    switch (String.valueOf(txtType.getText())) {
                                        case "백팩":
                                        case "조끼":
                                            imgOption[2].setVisibility(View.GONE);
                                            break;
                                        default:
                                            for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);

                                    }
                            }*/
                            txtName.setText(il.getSheldbrand(pick)); //위와 동일한 방식
                        }

                        //위와 동일한 방식
                        type = percent(1, 3);
                        openSheld = true;
                        layoutSheld.setVisibility(View.VISIBLE);
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));

                setSemiInterface(String.valueOf(txtType.getText()));

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //세션 박스를 열었을 경우, 위와 내용이 비슷하므로 설명 생략
                int pick, temp_percent;
                double now_option;
                int type = 0; // 1:attack, 2:sheld, 3:power
                String temp_option;
                openSheld = false;
                openWeapon = false;
                layoutSheld.setVisibility(View.GONE);
                layoutWeapon.setVisibility(View.GONE);

                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                //trOption.setVisibility(View.GONE);
                txtName.setTextColor(Color.parseColor("#aaaaaa"));

                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 10+bonus) { //특급 장비
                    if (percent(0, 2) == 1) {
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtSpecial.setText(Integer.toString(special));
                        tableMain.setVisibility(View.GONE);
                        btnChange.setVisibility(View.VISIBLE);
                        btnChange.setText("특급");
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                        txtName.setText("아코스타의 비상가방");
                        txtType.setText("백팩");
                        //trOption.setVisibility(View.VISIBLE);
                        int ransu;
                        /*for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/

                        type = 2;
                        openSheld = true;
                        temp_option = il.getSheldMainOption(1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        now_option = 170000;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = 2;
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = 2;
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    } else {
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtSpecial.setText(Integer.toString(special));
                        tableMain.setVisibility(View.GONE);
                        btnChange.setVisibility(View.VISIBLE);
                        btnChange.setText("특급");
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                        pick = percent(0, il.getSpecialweapon_Length());
                        txtName.setText(il.getSpecialweapon(pick));
                        txtType.setText(il.getSpecialweapon_type(pick));

                        switch (il.getSpecialweapon_type(pick)) {
                            case "소총": case "산탄총": case "지정사수소총": case "권총": case "돌격소총": case "기관단총":
                                openWeapon = true;
                                temp_option = String.valueOf(txtType.getText());
                                progressWMain1.setMax(150);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain1.setProgress((int)(now_option*10));
                                txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                                temp_option = il.getWeaponMainOption(temp_option);
                                progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain2.setProgress((int)(now_option*10));
                                txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                                temp_option = il.getWeaponSubOption();
                                progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWSub.setProgress((int)(now_option*10));
                                txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                                txtWTalent.setText(il.getSpecialTalent(String.valueOf(txtName.getText())));
                                break;
                            case "장갑":
                                type = 3;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(2);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 1;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;
                            case "무릎 보호대":
                                type = 2;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(1);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 170000;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;

                        }

                    }
                } else if (percent(1, 1000) <= 50+(bonus*4)) { //네임드 장비 확률 : 5%(시스템 : 50)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("네임드");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomnamed));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNamedWeaponLiteTalent(String.valueOf(txtName.getText())));

                    } else { //sheld
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        /*switch (il.getNamedsheld_lite_type(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }*/
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));

                        type = percent(1, 3);
                        openSheld = true;
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName.setText(il.getWeaponlist1(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                        openWeapon = true;
                        layoutWeapon.setVisibility(View.VISIBLE);
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getWeaponTalent(String.valueOf(txtType.getText())));

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        switch (il.getSheldtype(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(1, 100);
                        if (pick <= 20) { //gear
                            gear++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtGear.setText(Integer.toString(gear));
                            txtName.setTextColor(Color.parseColor("#009900"));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            /*switch (il.getSheldbrand(pick)) {
                                case "알프스 정상 군수산업":
                                case "아이랄디 홀딩":
                                    switch (String.valueOf(txtType.getText())) {
                                        case "백팩":
                                        case "조끼":
                                            imgOption[2].setVisibility(View.GONE);
                                            break;
                                        default:
                                            for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);

                                    }
                            }*/
                            txtName.setText(il.getSheldbrand(pick));
                        }

                        type = percent(1, 3);
                        openSheld = true;
                        layoutSheld.setVisibility(View.VISIBLE);
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                setSemiInterface(String.valueOf(txtType.getText()));
                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnLastBoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //전설난이도에서 마지막 보스를 잡았을 경우, 위와 내용이 비슷하므로 설명 생략
                int pick, temp_percent;
                double now_option;
                int type = 0; // 1:attack, 2:sheld, 3:power
                String temp_option;
                openSheld = false;
                openWeapon = false;
                layoutSheld.setVisibility(View.GONE);
                layoutWeapon.setVisibility(View.GONE);

                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                //trOption.setVisibility(View.GONE);
                txtName.setTextColor(Color.parseColor("#aaaaaa"));
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 10+(bonus*4)) { //특급 장비
                    if (percent(0, 2) == 1) {
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtSpecial.setText(Integer.toString(special));
                        tableMain.setVisibility(View.GONE);
                        btnChange.setVisibility(View.VISIBLE);
                        btnChange.setText("특급");
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                        txtName.setText("빅혼");
                        txtType.setText("돌격소총");

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNewSpecialWeaponTalent(String.valueOf(txtName.getText())));

                    } else {
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtSpecial.setText(Integer.toString(special));
                        tableMain.setVisibility(View.GONE);
                        btnChange.setVisibility(View.VISIBLE);
                        btnChange.setText("특급");
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                        pick = percent(0, il.getSpecialweapon_Length());
                        txtName.setText(il.getSpecialweapon(pick));
                        txtType.setText(il.getSpecialweapon_type(pick));

                        switch (il.getSpecialweapon_type(pick)) {
                            case "소총": case "산탄총": case "지정사수소총": case "권총": case "돌격소총": case "기관단총":
                                openWeapon = true;
                                temp_option = String.valueOf(txtType.getText());
                                progressWMain1.setMax(150);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain1.setProgress((int)(now_option*10));
                                txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                                temp_option = il.getWeaponMainOption(temp_option);
                                progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain2.setProgress((int)(now_option*10));
                                txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                                temp_option = il.getWeaponSubOption();
                                progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWSub.setProgress((int)(now_option*10));
                                txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                                txtWTalent.setText(il.getSpecialTalent(String.valueOf(txtName.getText())));
                                break;
                            case "장갑":
                                type = 3;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(2);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 1;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;
                            case "무릎 보호대":
                                type = 2;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(1);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 170000;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;

                        }

                    }
                } else if (percent(1, 1000) <= 50+(bonus*4)) { //네임드 장비 확률 : 5%(시스템 : 50)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("네임드");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomnamed));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNamedWeaponLiteTalent(String.valueOf(txtName.getText())));
                        

                    } else { //sheld
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        /*switch (il.getNamedsheld_lite_type(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }*/
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));

                        type = percent(1, 3);
                        openSheld = true;
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName.setText(il.getWeaponlist1(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                        openWeapon = true;
                        layoutWeapon.setVisibility(View.VISIBLE);
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getWeaponTalent(String.valueOf(txtType.getText())));

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        switch (il.getSheldtype(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(1, 100);
                        if (pick <= 20) { //gear
                            gear++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtGear.setText(Integer.toString(gear));
                            txtName.setTextColor(Color.parseColor("#009900"));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            /*switch (il.getSheldbrand(pick)) {
                                case "알프스 정상 군수산업":
                                case "아이랄디 홀딩":
                                    switch (String.valueOf(txtType.getText())) {
                                        case "백팩":
                                        case "조끼":
                                            imgOption[2].setVisibility(View.GONE);
                                            break;
                                        default:
                                            for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);

                                    }
                            }*/
                            txtName.setText(il.getSheldbrand(pick));
                        }

                        type = percent(1, 3);
                        openSheld = true;
                        layoutSheld.setVisibility(View.VISIBLE);
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                setSemiInterface(String.valueOf(txtType.getText()));
                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnDragov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //월 스트리트 미션에서 마지막 보스 제임스 드래고프를 처치했을 경우, 위와 내용이 비슷하므로 설명 생략
                int pick, temp_percent;
                double now_option;
                int type = 0; // 1:attack, 2:sheld, 3:power
                String temp_option;
                openSheld = false;
                openWeapon = false;
                layoutSheld.setVisibility(View.GONE);
                layoutWeapon.setVisibility(View.GONE);

                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                //trOption.setVisibility(View.GONE);
                txtName.setTextColor(Color.parseColor("#aaaaaa"));
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 10+(bonus*4)) { //특급 장비
                    if (percent(0, 2) == 1) {
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtSpecial.setText(Integer.toString(special));
                        tableMain.setVisibility(View.GONE);
                        btnChange.setVisibility(View.VISIBLE);
                        btnChange.setText("특급");
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                        txtName.setText("탄환 제왕");
                        txtType.setText("경기관총");


                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNewSpecialWeaponTalent(String.valueOf(txtName.getText())));

                    } else {
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtSpecial.setText(Integer.toString(special));
                        tableMain.setVisibility(View.GONE);
                        btnChange.setVisibility(View.VISIBLE);
                        btnChange.setText("특급");
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                        pick = percent(0, il.getSpecialweapon_Length());
                        txtName.setText(il.getSpecialweapon(pick));
                        txtType.setText(il.getSpecialweapon_type(pick));

                        switch (il.getSpecialweapon_type(pick)) {
                            case "소총": case "산탄총": case "지정사수소총": case "권총": case "돌격소총": case "기관단총":
                                openWeapon = true;
                                temp_option = String.valueOf(txtType.getText());
                                progressWMain1.setMax(150);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain1.setProgress((int)(now_option*10));
                                txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                                temp_option = il.getWeaponMainOption(temp_option);
                                progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain2.setProgress((int)(now_option*10));
                                txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                                temp_option = il.getWeaponSubOption();
                                progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWSub.setProgress((int)(now_option*10));
                                txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                                txtWTalent.setText(il.getSpecialTalent(String.valueOf(txtName.getText())));
                                break;
                            case "장갑":
                                type = 3;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(2);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 1;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;
                            case "무릎 보호대":
                                type = 2;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(1);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 170000;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;

                        }

                    }
                } else if (percent(1, 1000) <= 50+(bonus*4)) { //네임드 장비 확률 : 5%(시스템 : 50)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("네임드");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomnamed));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNamedWeaponLiteTalent(String.valueOf(txtName.getText())));

                    } else { //sheld
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        /*switch (il.getNamedsheld_lite_type(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }*/
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));

                        type = percent(1, 3);
                        openSheld = true;
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName.setText(il.getWeaponlist1(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                        openWeapon = true;
                        layoutWeapon.setVisibility(View.VISIBLE);
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getWeaponTalent(String.valueOf(txtType.getText())));

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        switch (il.getSheldtype(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(1, 100);
                        if (pick <= 20) { //gear
                            gear++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtGear.setText(Integer.toString(gear));
                            txtName.setTextColor(Color.parseColor("#009900"));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            /*switch (il.getSheldbrand(pick)) {
                                case "알프스 정상 군수산업":
                                case "아이랄디 홀딩":
                                    switch (String.valueOf(txtType.getText())) {
                                        case "백팩":
                                        case "조끼":
                                            imgOption[2].setVisibility(View.GONE);
                                            break;
                                        default:
                                            for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);

                                    }
                            }*/
                            txtName.setText(il.getSheldbrand(pick));
                        }

                        type = percent(1, 3);
                        openSheld = true;
                        layoutSheld.setVisibility(View.VISIBLE);
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);

                                System.out.println("temp_option : "+temp_option);
                                System.out.println("now_option : "+now_option);
                                System.out.println("temp_percent : "+temp_percent);
                                System.out.println("option_bonus : "+option_bonus);
                                System.out.println("il.getMaxSheldSubWeaponOption(temp_option) : "+il.getMaxSheldSubWeaponOption(temp_option));
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);

                                System.out.println("temp_option : "+temp_option);
                                System.out.println("now_option : "+now_option);
                                System.out.println("temp_percent : "+temp_percent);
                                System.out.println("option_bonus : "+option_bonus);
                                System.out.println("il.getMaxSheldSubSheldOption(temp_option) : "+il.getMaxSheldSubSheldOption(temp_option));
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);

                                System.out.println("temp_option : "+temp_option);
                                System.out.println("now_option : "+now_option);
                                System.out.println("temp_percent : "+temp_percent);
                                System.out.println("option_bonus : "+option_bonus);
                                System.out.println("il.getMaxSheldSubPowerOption(temp_option) : "+il.getMaxSheldSubPowerOption(temp_option));
                                break;
                        }

                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                setSemiInterface(String.valueOf(txtType.getText()));
                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnNewYork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //뉴욕에서 필드 보스를 잡았을 경우, 위와 내용이 비슷하므로 설명 생략
                int pick, temp_percent;
                double now_option;
                int type = 0; // 1:attack, 2:sheld, 3:power
                String temp_option;
                openSheld = false;
                openWeapon = false;
                layoutSheld.setVisibility(View.GONE);
                layoutWeapon.setVisibility(View.GONE);

                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                //trOption.setVisibility(View.GONE);
                txtName.setTextColor(Color.parseColor("#aaaaaa"));
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 10+(bonus*4)) { //특급 장비
                    if (percent(0, 2) == 1) {
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtSpecial.setText(Integer.toString(special));
                        tableMain.setVisibility(View.GONE);
                        btnChange.setVisibility(View.VISIBLE);
                        btnChange.setText("특급");
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                        txtName.setText("죽음의 귀부인");
                        txtType.setText("기관단총");

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNewSpecialWeaponTalent(String.valueOf(txtName.getText())));

                    } else {
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtSpecial.setText(Integer.toString(special));
                        tableMain.setVisibility(View.GONE);
                        btnChange.setVisibility(View.VISIBLE);
                        btnChange.setText("특급");
                        btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                        //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                        pick = percent(0, il.getSpecialweapon_Length());
                        txtName.setText(il.getSpecialweapon(pick));
                        txtType.setText(il.getSpecialweapon_type(pick));

                        switch (il.getSpecialweapon_type(pick)) {
                            case "소총": case "산탄총": case "지정사수소총": case "권총": case "돌격소총": case "기관단총":
                                openWeapon = true;
                                temp_option = String.valueOf(txtType.getText());
                                progressWMain1.setMax(150);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain1.setProgress((int)(now_option*10));
                                txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                                temp_option = il.getWeaponMainOption(temp_option);
                                progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWMain2.setProgress((int)(now_option*10));
                                txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                                temp_option = il.getWeaponSubOption();
                                progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                                progressWSub.setProgress((int)(now_option*10));
                                txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                                txtWTalent.setText(il.getSpecialTalent(String.valueOf(txtName.getText())));
                                break;
                            case "장갑":
                                type = 3;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(2);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 1;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 3;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;
                            case "무릎 보호대":
                                type = 2;
                                openSheld = true;
                                temp_option = il.getSheldMainOption(1);
                                progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                                now_option = 170000;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSMain.setProgress((int)(now_option*10));
                                txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                                switch (type) {
                                    case 1:
                                        imgSMain.setImageResource(R.drawable.attack);
                                        break;
                                    case 2:
                                        imgSMain.setImageResource(R.drawable.sheld);
                                        break;
                                    case 3:
                                        imgSMain.setImageResource(R.drawable.power);
                                        break;
                                    default:
                                        imgSMain.setImageResource(R.drawable.critical);
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub1.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub1.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub1.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub1.setProgress((int)(now_option*10));
                                        txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                type = 2;
                                switch (type) {
                                    case 1:
                                        imgSSub2.setImageResource(R.drawable.attack);
                                        temp_option = il.getSheldSubWeaponOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 2:
                                        imgSSub2.setImageResource(R.drawable.sheld);
                                        temp_option = il.getSheldSubSheldOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                    case 3:
                                        imgSSub2.setImageResource(R.drawable.power);
                                        temp_option = il.getSheldSubPowerOption();
                                        pick = percent(1, 100);
                                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                        else temp_percent = percent(1, 20) + option_bonus;
                                        now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                        if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                        else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                        progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                        progressSSub2.setProgress((int)(now_option*10));
                                        txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                        break;
                                }
                                break;

                        }

                    }
                } else if (percent(1, 1000) <= 50+(bonus*4)) { //네임드 장비 확률 : 5%(시스템 : 50)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("네임드");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomnamed));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNamedWeaponLiteTalent(String.valueOf(txtName.getText())));

                    } else { //sheld
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        /*switch (il.getNamedsheld_lite_type(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }*/
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));

                        type = percent(1, 3);
                        openSheld = true;
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName.setText(il.getWeaponlist1(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                        openWeapon = true;
                        layoutWeapon.setVisibility(View.VISIBLE);
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getWeaponTalent(String.valueOf(txtType.getText())));

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        switch (il.getSheldtype(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(1, 100);
                        if (pick <= 20) { //gear
                            gear++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtGear.setText(Integer.toString(gear));
                            txtName.setTextColor(Color.parseColor("#009900"));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            /*switch (il.getSheldbrand(pick)) {
                                case "알프스 정상 군수산업":
                                case "아이랄디 홀딩":
                                    switch (String.valueOf(txtType.getText())) {
                                        case "백팩":
                                        case "조끼":
                                            imgOption[2].setVisibility(View.GONE);
                                            break;
                                        default:
                                            for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);

                                    }
                            }*/
                            txtName.setText(il.getSheldbrand(pick));
                        }

                        type = percent(1, 3);
                        openSheld = true;
                        layoutSheld.setVisibility(View.VISIBLE);
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);



                setSemiInterface(String.valueOf(txtType.getText()));
                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnLitezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //라이트존에서 적을 죽였을 경우, 위와 내용이 비슷하므로 설명 생략
                int pick, temp_percent;
                double now_option;
                int type = 0; // 1:attack, 2:sheld, 3:power
                String temp_option;
                openSheld = false;
                openWeapon = false;
                layoutSheld.setVisibility(View.GONE);
                layoutWeapon.setVisibility(View.GONE);

                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                //trOption.setVisibility(View.GONE);
                txtName.setTextColor(Color.parseColor("#aaaaaa"));
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 10+bonus) { //특급 장비
                    txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    all++;
                    setInterface();
                    txtSpecial.setText(Integer.toString(special));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("특급");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                    //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                    pick = percent(0, il.getSpecialweapon_Length());
                    txtName.setText(il.getSpecialweapon(pick));
                    txtType.setText(il.getSpecialweapon_type(pick));

                    switch (il.getSpecialweapon_type(pick)) {
                        case "소총": case "산탄총": case "지정사수소총": case "권총": case "돌격소총": case "기관단총":
                            openWeapon = true;
                            temp_option = String.valueOf(txtType.getText());
                            progressWMain1.setMax(150);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWMain1.setProgress((int)(now_option*10));
                            txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                            temp_option = il.getWeaponMainOption(temp_option);
                            progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWMain2.setProgress((int)(now_option*10));
                            txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                            temp_option = il.getWeaponSubOption();
                            progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWSub.setProgress((int)(now_option*10));
                            txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                            txtWTalent.setText(il.getSpecialTalent(String.valueOf(txtName.getText())));
                            break;
                        case "장갑":
                            type = 3;
                            openSheld = true;
                            temp_option = il.getSheldMainOption(2);
                            progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                            now_option = 1;
                            if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                            else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                            progressSMain.setProgress((int)(now_option*10));
                            txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                            switch (type) {
                                case 1:
                                    imgSMain.setImageResource(R.drawable.attack);
                                    break;
                                case 2:
                                    imgSMain.setImageResource(R.drawable.sheld);
                                    break;
                                case 3:
                                    imgSMain.setImageResource(R.drawable.power);
                                    break;
                                default:
                                    imgSMain.setImageResource(R.drawable.critical);
                            }
                            type = 3;
                            switch (type) {
                                case 1:
                                    imgSSub1.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub1.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub1.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            type = 3;
                            switch (type) {
                                case 1:
                                    imgSSub2.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub2.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub2.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            break;
                        case "무릎 보호대":
                            type = 2;
                            openSheld = true;
                            temp_option = il.getSheldMainOption(1);
                            progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                            now_option = 170000;
                            if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                            else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                            progressSMain.setProgress((int)(now_option*10));
                            txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                            switch (type) {
                                case 1:
                                    imgSMain.setImageResource(R.drawable.attack);
                                    break;
                                case 2:
                                    imgSMain.setImageResource(R.drawable.sheld);
                                    break;
                                case 3:
                                    imgSMain.setImageResource(R.drawable.power);
                                    break;
                                default:
                                    imgSMain.setImageResource(R.drawable.critical);
                            }
                            type = 2;
                            switch (type) {
                                case 1:
                                    imgSSub1.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub1.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub1.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            type = 2;
                            switch (type) {
                                case 1:
                                    imgSSub2.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub2.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub2.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            break;

                    }

                } else if (percent(1, 1000) <= 20+(bonus*2)) { //네임드 장비
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("네임드");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomnamed));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNamedWeaponLiteTalent(String.valueOf(txtName.getText())));

                    } else { //sheld
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        /*switch (il.getNamedsheld_lite_type(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }*/
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));

                        type = percent(1, 3);
                        openSheld = true;
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName.setText(il.getWeaponlist1(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                        openWeapon = true;
                        layoutWeapon.setVisibility(View.VISIBLE);
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getWeaponTalent(String.valueOf(txtType.getText())));

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        switch (il.getSheldtype(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(1, 100);
                        if (pick <= 20) { //gear
                            gear++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtGear.setText(Integer.toString(gear));
                            txtName.setTextColor(Color.parseColor("#009900"));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            /*switch (il.getSheldbrand(pick)) {
                                case "알프스 정상 군수산업":
                                case "아이랄디 홀딩":
                                    switch (String.valueOf(txtType.getText())) {
                                        case "백팩":
                                        case "조끼":
                                            imgOption[2].setVisibility(View.GONE);
                                            break;
                                        default:
                                            for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);

                                    }
                            }*/
                            txtName.setText(il.getSheldbrand(pick));
                        }

                        type = percent(1, 3);
                        openSheld = true;
                        layoutSheld.setVisibility(View.VISIBLE);
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                setSemiInterface(String.valueOf(txtType.getText()));
                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnDarkzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //다크존에서 적을 죽였을 경우, 위와 내용이 비슷하므로 설명 생략
                int pick, temp_percent;
                double now_option;
                int type = 0; // 1:attack, 2:sheld, 3:power
                String temp_option;
                openSheld = false;
                openWeapon = false;
                layoutSheld_dark.setVisibility(View.GONE);
                layoutWeapon_dark.setVisibility(View.GONE);

                if (!rdoDiff[2].isChecked()) rdoDiff[2].toggle();
                tableMain2.setVisibility(View.VISIBLE);
                btnChange2.setVisibility(View.GONE);
                txtName2.setTextColor(Color.parseColor("#aaaaaa"));
                //trOption2.setVisibility(View.GONE);
                //for (int i = 0; i < 3; i++) imgOption2[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 10+bonus) { //특급 장비
                    btnChange2.setText("특급");
                    btnChange2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                    tableMain2.setVisibility(View.GONE);
                    btnChange2.setVisibility(View.VISIBLE);
                    txtName2.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtSpecial.setText(Integer.toString(special));
                    pick = percent(2, 10);
                    if (pick > 2) {
                        txtName2.setText("역병");
                        txtType2.setText("경기관총");
                    } else {
                        pick = percent(0, il.getSpecialweapon_Length());
                        txtName2.setText(il.getSpecialweapon(pick));
                        txtType2.setText(il.getSpecialweapon_type(pick));
                    }

                    switch (String.valueOf(txtType2.getText())) {
                        case "소총": case "산탄총": case "지정사수소총": case "권총": case "돌격소총": case "기관단총": case "경기관총":
                            openWeapon = true;
                            temp_option = String.valueOf(txtType2.getText());
                            progressWMain1_dark.setMax(150);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                            now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= 15) txtWMain1_dark.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWMain1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWMain1_dark.setProgress((int)(now_option*10));
                            txtWMain1_dark.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                            temp_option = il.getWeaponMainOption(temp_option);
                            progressWMain2_dark.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                            now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2_dark.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWMain2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWMain2_dark.setProgress((int)(now_option*10));
                            txtWMain2_dark.setText("+"+Double.toString(now_option)+"% "+temp_option);

                            temp_option = il.getWeaponSubOption();
                            progressWSub_dark.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                            now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2_dark.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWSub_dark.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWSub_dark.setProgress((int)(now_option*10));
                            txtWSub_dark.setText("+"+Double.toString(now_option)+"% "+temp_option);

                            if (!String.valueOf(txtName2.getText()).equals("역병")) txtWTalent_dark.setText(il.getSpecialTalent(String.valueOf(txtName2.getText())));
                            else txtWTalent_dark.setText(il.getNewSpecialWeaponTalent(String.valueOf(txtName2.getText())));
                            break;
                        case "장갑":
                            type = 3;
                            openSheld = true;
                            temp_option = il.getSheldMainOption(2);
                            progressSMain_dark.setMax(il.getMaxSheldMainOption(temp_option)*10);
                            now_option = 1;
                            if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain_dark.setTextColor(Color.parseColor("#ff3c00"));
                            else txtSMain_dark.setTextColor(Color.parseColor("#aaaaaa"));
                            progressSMain_dark.setProgress((int)(now_option*10));
                            txtSMain_dark.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                            switch (type) {
                                case 1:
                                    imgSMain_dark.setImageResource(R.drawable.attack);
                                    break;
                                case 2:
                                    imgSMain_dark.setImageResource(R.drawable.sheld);
                                    break;
                                case 3:
                                    imgSMain_dark.setImageResource(R.drawable.power);
                                    break;
                                default:
                                    imgSMain_dark.setImageResource(R.drawable.critical);
                            }
                            type = 3;
                            switch (type) {
                                case 1:
                                    imgSSub1_dark.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1_dark.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub1_dark.setProgress((int)(now_option*10));
                                    txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub1_dark.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1_dark.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub1_dark.setProgress((int)(now_option*10));
                                    txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub1_dark.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1_dark.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub1_dark.setProgress((int)(now_option*10));
                                    txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            type = 3;
                            switch (type) {
                                case 1:
                                    imgSSub2_dark.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2_dark.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub2_dark.setProgress((int)(now_option*10));
                                    txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub2_dark.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2_dark.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub2_dark.setProgress((int)(now_option*10));
                                    txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub2_dark.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2_dark.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub2_dark.setProgress((int)(now_option*10));
                                    txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            break;
                        case "무릎 보호대":
                            type = 2;
                            openSheld = true;
                            temp_option = il.getSheldMainOption(1);
                            progressSMain_dark.setMax(il.getMaxSheldMainOption(temp_option)*10);
                            now_option = 170000;
                            if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain_dark.setTextColor(Color.parseColor("#ff3c00"));
                            else txtSMain_dark.setTextColor(Color.parseColor("#aaaaaa"));
                            progressSMain_dark.setProgress((int)(now_option*10));
                            txtSMain_dark.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                            switch (type) {
                                case 1:
                                    imgSMain_dark.setImageResource(R.drawable.attack);
                                    break;
                                case 2:
                                    imgSMain_dark.setImageResource(R.drawable.sheld);
                                    break;
                                case 3:
                                    imgSMain_dark.setImageResource(R.drawable.power);
                                    break;
                                default:
                                    imgSMain_dark.setImageResource(R.drawable.critical);
                            }
                            type = 2;
                            switch (type) {
                                case 1:
                                    imgSSub1_dark.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1_dark.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub1_dark.setProgress((int)(now_option*10));
                                    txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub1_dark.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1_dark.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub1_dark.setProgress((int)(now_option*10));
                                    txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub1_dark.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1_dark.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub1_dark.setProgress((int)(now_option*10));
                                    txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            type = 2;
                            switch (type) {
                                case 1:
                                    imgSSub2_dark.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2_dark.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub2_dark.setProgress((int)(now_option*10));
                                    txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub2_dark.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2_dark.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub2_dark.setProgress((int)(now_option*10));
                                    txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub2_dark.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2_dark.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub2_dark.setProgress((int)(now_option*10));
                                    txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            break;

                    }

                } else if (percent(1, 1000) <= 20+(bonus*2)) { //네임드 장비
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName2.setTextColor(Color.parseColor("#c99700"));
                    tableMain2.setVisibility(View.GONE);
                    btnChange2.setVisibility(View.VISIBLE);
                    btnChange2.setText("네임드");
                    btnChange2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomnamed));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_dark_Length());
                        txtName2.setText(il.getNamedweapon_dark(pick));
                        txtType2.setText(il.getNamedweapon_dark_type(pick));

                        openWeapon = true;
                        temp_option = String.valueOf(txtType2.getText());
                        progressWMain1_dark.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1_dark.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1_dark.setProgress((int)(now_option*10));
                        txtWMain1_dark.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2_dark.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2_dark.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2_dark.setProgress((int)(now_option*10));
                        txtWMain2_dark.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub_dark.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2_dark.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub_dark.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub_dark.setProgress((int)(now_option*10));
                        txtWSub_dark.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent_dark.setText(il.getNamedWeaponDarkTalent(String.valueOf(txtName2.getText())));

                    } else { //sheld
                        /*trOption2.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption2.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption2[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption2[i].setImageResource(R.drawable.sheld);
                            else imgOption2[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(0, il.getNamedsheld_dark_Length());
                        /*switch (il.getNamedsheld_dark_type(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption2[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption2[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption2[i].setVisibility(View.GONE);
                                break;
                        }*/
                        txtName2.setText(il.getNamedsheld_dark(pick));
                        txtType2.setText(il.getNamedsheld_dark_type(pick));

                        type = percent(1, 3);
                        openSheld = true;
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain_dark.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                        else temp_percent = percent(1, 30) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain_dark.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain_dark.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain_dark.setProgress((int)(now_option*10));
                        txtSMain_dark.setText("+"+Double.toString(now_option)+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain_dark.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain_dark.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain_dark.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain_dark.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1_dark.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1_dark.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1_dark.setProgress((int)(now_option*10));
                                txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1_dark.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1_dark.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1_dark.setProgress((int)(now_option*10));
                                txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1_dark.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1_dark.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1_dark.setProgress((int)(now_option*10));
                                txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2_dark.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2_dark.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2_dark.setProgress((int)(now_option*10));
                                txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2_dark.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2_dark.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2_dark.setProgress((int)(now_option*10));
                                txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2_dark.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2_dark.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2_dark.setProgress((int)(now_option*10));
                                txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName2.setText(il.getWeaponlist1(temp));
                                txtType2.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName2.setText(il.getWeaponlist2(temp));
                                txtType2.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName2.setText(il.getWeaponlist3(temp));
                                txtType2.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName2.setText(il.getWeaponlist4(temp));
                                txtType2.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName2.setText(il.getWeaponlist5(temp));
                                txtType2.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName2.setText(il.getWeaponlist6(temp));
                                txtType2.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName2.setText(il.getWeaponlist7(temp));
                                txtType2.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName2.setText("Error");
                                txtType2.setText("Error");
                        }

                        openWeapon = true;
                        layoutWeapon_dark.setVisibility(View.VISIBLE);
                        temp_option = String.valueOf(txtType2.getText());
                        progressWMain1_dark.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1_dark.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1_dark.setProgress((int)(now_option*10));
                        txtWMain1_dark.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2_dark.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2_dark.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2_dark.setProgress((int)(now_option*10));
                        txtWMain2_dark.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub_dark.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2_dark.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub_dark.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub_dark.setProgress((int)(now_option*10));
                        txtWSub_dark.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent_dark.setText(il.getWeaponTalent(String.valueOf(txtType2.getText())));

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType2.setText(il.getSheldtype(pick));
                        int option;
                        /*switch (il.getSheldtype(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption2[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption2[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption2[i].setVisibility(View.GONE);
                                break;
                        }*/
                        pick = percent(1, 100);
                        /*trOption2.setVisibility(View.VISIBLE);
                        int ransu;
                        for (int i = 0; i < imgOption2.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption2[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption2[i].setImageResource(R.drawable.sheld);
                            else imgOption2[i].setImageResource(R.drawable.power);
                        }*/
                        if (pick <= 20) { //gear
                            gear++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtGear.setText(Integer.toString(gear));
                            txtName2.setTextColor(Color.parseColor("#009900"));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName2.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            /*switch (il.getSheldbrand(pick)) {
                                case "알프스 정상 군수산업":
                                case "아이랄디 홀딩":
                                    switch (String.valueOf(txtType2.getText())) {
                                        case "백팩":
                                        case "조끼":
                                            imgOption2[2].setVisibility(View.GONE);
                                            break;
                                        default:
                                            for (int i = 1; i < 3; i++) imgOption2[i].setVisibility(View.GONE);

                                    }
                            }*/
                            txtName2.setText(il.getSheldbrand(pick));
                        }

                        type = percent(1, 3);
                        openSheld = true;
                        layoutSheld_dark.setVisibility(View.VISIBLE);
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain_dark.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain_dark.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain_dark.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain_dark.setProgress((int)(now_option*10));
                        txtSMain_dark.setText("+"+Double.toString(now_option)+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain_dark.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain_dark.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain_dark.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain_dark.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1_dark.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1_dark.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1_dark.setProgress((int)(now_option*10));
                                txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1_dark.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1_dark.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1_dark.setProgress((int)(now_option*10));
                                txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1_dark.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1_dark.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1_dark.setProgress((int)(now_option*10));
                                txtSSub1_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2_dark.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2_dark.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2_dark.setProgress((int)(now_option*10));
                                txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2_dark.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2_dark.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2_dark.setProgress((int)(now_option*10));
                                txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2_dark.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(31, 20) + option_bonus;
                            else temp_percent = percent(1, 30) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2_dark.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2_dark.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2_dark.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2_dark.setProgress((int)(now_option*10));
                                txtSSub2_dark.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                    }
                }

                if (dark_dialogView.getParent() != null)
                    ((ViewGroup) dark_dialogView.getParent()).removeView(dark_dialogView);
                builder_dark.setView(dark_dialogView);

                setSemiInterface(String.valueOf(txtType2.getText()));
                inputData(String.valueOf(txtName2.getText()), String.valueOf(txtType2.getText()));

                dialog_dark = builder_dark.create();
                dialog_dark.setCancelable(false);
                dialog_dark.show();
            }
        });

        btnRaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //칠흑의 시간 레이드에서 네임드 보스를 죽였을 경우, 위와 내용이 비슷하므로 설명 생략
                int pick, temp_percent;
                double now_option;
                if (!rdoDiff[3].isChecked()) rdoDiff[3].toggle();
                int type = 0; // 1:attack, 2:sheld, 3:power
                String temp_option;
                openSheld = false;
                openWeapon = false;
                layoutSheld.setVisibility(View.GONE);
                layoutWeapon.setVisibility(View.GONE);

                txtName.setTextColor(Color.parseColor("#aaaaaa"));
                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                /*trOption.setVisibility(View.GONE);
                for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);*/
                if (percent(1, 1000) <= 10) { //특급 장비
                    txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtSpecial.setText(Integer.toString(special));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("특급");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                    pick = percent(0, il.getSpecialweapon_raid_Length());
                    txtName.setText(il.getSpecialweapon_raid(pick));
                    txtType.setText(il.getSpecialweapon_raid_type(pick));

                    switch (il.getSpecialweapon_type(pick)) {
                        case "소총": case "산탄총": case "지정사수소총": case "권총": case "돌격소총": case "기관단총":
                            openWeapon = true;
                            temp_option = String.valueOf(txtType.getText());
                            progressWMain1.setMax(150);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWMain1.setProgress((int)(now_option*10));
                            txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                            temp_option = il.getWeaponMainOption(temp_option);
                            progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWMain2.setProgress((int)(now_option*10));
                            txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                            temp_option = il.getWeaponSubOption();
                            progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                            pick = percent(1, 100);
                            if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                            else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                            progressWSub.setProgress((int)(now_option*10));
                            txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                            txtWTalent.setText(il.getSpecialTalent(String.valueOf(txtName.getText())));
                            break;
                        case "장갑":
                            type = 3;
                            openSheld = true;
                            temp_option = il.getSheldMainOption(2);
                            progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                            now_option = 1;
                            if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                            else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                            progressSMain.setProgress((int)(now_option*10));
                            txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                            switch (type) {
                                case 1:
                                    imgSMain.setImageResource(R.drawable.attack);
                                    break;
                                case 2:
                                    imgSMain.setImageResource(R.drawable.sheld);
                                    break;
                                case 3:
                                    imgSMain.setImageResource(R.drawable.power);
                                    break;
                                default:
                                    imgSMain.setImageResource(R.drawable.critical);
                            }
                            type = 3;
                            switch (type) {
                                case 1:
                                    imgSSub1.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub1.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub1.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            type = 3;
                            switch (type) {
                                case 1:
                                    imgSSub2.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub2.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub2.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            break;
                        case "무릎 보호대":
                            type = 2;
                            openSheld = true;
                            temp_option = il.getSheldMainOption(1);
                            progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                            now_option = 170000;
                            if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                            else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                            progressSMain.setProgress((int)(now_option*10));
                            txtSMain.setText("+"+il.getMaxSheldMainOption(temp_option)+temp_option);
                            switch (type) {
                                case 1:
                                    imgSMain.setImageResource(R.drawable.attack);
                                    break;
                                case 2:
                                    imgSMain.setImageResource(R.drawable.sheld);
                                    break;
                                case 3:
                                    imgSMain.setImageResource(R.drawable.power);
                                    break;
                                default:
                                    imgSMain.setImageResource(R.drawable.critical);
                            }
                            type = 2;
                            switch (type) {
                                case 1:
                                    imgSSub1.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub1.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub1.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub1.setProgress((int)(now_option*10));
                                    txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            type = 2;
                            switch (type) {
                                case 1:
                                    imgSSub2.setImageResource(R.drawable.attack);
                                    temp_option = il.getSheldSubWeaponOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 2:
                                    imgSSub2.setImageResource(R.drawable.sheld);
                                    temp_option = il.getSheldSubSheldOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                                case 3:
                                    imgSSub2.setImageResource(R.drawable.power);
                                    temp_option = il.getSheldSubPowerOption();
                                    pick = percent(1, 100);
                                    if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                    else temp_percent = percent(1, 20) + option_bonus;
                                    now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                    if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                    else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                    progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                    progressSSub2.setProgress((int)(now_option*10));
                                    txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                    break;
                            }
                            break;

                    }

                } else if (percent(1, 1000) <= 30+(bonus*2)) { //네임드 장비
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("네임드");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomnamed));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));

                        openWeapon = true;
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getNamedWeaponLiteTalent(String.valueOf(txtName.getText())));

                    } else { //sheld
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        /*switch (il.getNamedsheld_lite_type(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }*/
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));

                        type = percent(1, 3);
                        openSheld = true;
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName.setText(il.getWeaponlist1(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                        openWeapon = true;
                        layoutWeapon.setVisibility(View.VISIBLE);
                        temp_option = String.valueOf(txtType.getText());
                        progressWMain1.setMax(150);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((15.0*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= 15) txtWMain1.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain1.setProgress((int)(now_option*10));
                        txtWMain1.setText("+"+Double.toString(now_option)+"% "+temp_option+" 데미지");

                        temp_option = il.getWeaponMainOption(temp_option);
                        progressWMain2.setMax(il.getMaxWeaponMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponMainOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWMain2.setProgress((int)(now_option*10));
                        txtWMain2.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        temp_option = il.getWeaponSubOption();
                        progressWSub.setMax(il.getMaxWeaponSubOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        now_option = Math.round((il.getMaxWeaponSubOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(now_option) >= il.getMaxWeaponSubOption(temp_option)) txtWMain2.setTextColor(Color.parseColor("#ff3c00"));
                        else txtWSub.setTextColor(Color.parseColor("#aaaaaa"));
                        progressWSub.setProgress((int)(now_option*10));
                        txtWSub.setText("+"+Double.toString(now_option)+"% "+temp_option);

                        txtWTalent.setText(il.getWeaponTalent(String.valueOf(txtType.getText())));

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        /*trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        switch (il.getSheldtype(pick)) {
                            case "마스크":
                            case "장갑":
                            case "권총집":
                                option = percent(1, 100);
                                if (option <= 80) imgOption[2].setVisibility(View.GONE);
                                else for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                            case "무릎 보호대":
                                for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                                break;
                        }
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }*/
                        pick = percent(1, 100);
                        if (pick <= 20) { //gear
                            gear++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtGear.setText(Integer.toString(gear));
                            txtName.setTextColor(Color.parseColor("#009900"));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            /*switch (il.getSheldbrand(pick)) {
                                case "알프스 정상 군수산업":
                                case "아이랄디 홀딩":
                                    switch (String.valueOf(txtType.getText())) {
                                        case "백팩":
                                        case "조끼":
                                            imgOption[2].setVisibility(View.GONE);
                                            break;
                                        default:
                                            for (int i = 1; i < 3; i++) imgOption[i].setVisibility(View.GONE);

                                    }
                            }*/
                            txtName.setText(il.getSheldbrand(pick));
                        }

                        type = percent(1, 3);
                        openSheld = true;
                        layoutSheld.setVisibility(View.VISIBLE);
                        temp_option = il.getSheldMainOption(type-1);
                        progressSMain.setMax(il.getMaxSheldMainOption(temp_option)*10);
                        pick = percent(1, 100);
                        if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (type != 3) now_option = Math.round((il.getMaxSheldMainOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                        else now_option = 1;
                        if ((int)Math.floor(now_option) >= il.getMaxSheldMainOption(temp_option)) txtSMain.setTextColor(Color.parseColor("#ff3c00"));
                        else txtSMain.setTextColor(Color.parseColor("#aaaaaa"));
                        progressSMain.setProgress((int)(now_option*10));
                        txtSMain.setText("+"+now_option+temp_option);
                        switch (type) {
                            case 1:
                                imgSMain.setImageResource(R.drawable.attack);
                                break;
                            case 2:
                                imgSMain.setImageResource(R.drawable.sheld);
                                break;
                            case 3:
                                imgSMain.setImageResource(R.drawable.power);
                                break;
                            default:
                                imgSMain.setImageResource(R.drawable.critical);
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub1.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub1.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub1.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub1.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub1.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub1.setProgress((int)(now_option*10));
                                txtSSub1.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }
                        type = percent(1, 3);
                        switch (type) {
                            case 1:
                                imgSSub2.setImageResource(R.drawable.attack);
                                temp_option = il.getSheldSubWeaponOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubWeaponOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubWeaponOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubWeaponOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 2:
                                imgSSub2.setImageResource(R.drawable.sheld);
                                temp_option = il.getSheldSubSheldOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubSheldOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubSheldOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubSheldOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                            case 3:
                                imgSSub2.setImageResource(R.drawable.power);
                                temp_option = il.getSheldSubPowerOption();
                                pick = percent(1, 100);
                                if (pick <= 20) temp_percent = percent(21, 10) + option_bonus;
                                else temp_percent = percent(1, 20) + option_bonus;
                                now_option = Math.round(((double)il.getMaxSheldSubPowerOption(temp_option)*(double)((double)temp_percent/100))*10.0)/10.0;
                                if ((int)Math.floor(now_option) >= il.getMaxSheldSubPowerOption(temp_option)) txtSSub2.setTextColor(Color.parseColor("#ff3c00"));
                                else txtSSub2.setTextColor(Color.parseColor("#aaaaaa"));
                                progressSSub2.setMax(il.getMaxSheldSubPowerOption(temp_option)*10);
                                progressSSub2.setProgress((int)(now_option*10));
                                txtSSub2.setText("+"+Double.toString(now_option)+temp_option);
                                break;
                        }

                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                setSemiInterface(String.valueOf(txtType.getText()));
                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnRaidbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //칠흑의 시간 레이드에서 마지막 보스 처치 후 상자 개봉할 경우, 위와 내용이 비슷하므로 설명 생략
                int pick;
                int start, end;
                layoutSheld.setVisibility(View.GONE);
                layoutWeapon.setVisibility(View.GONE);
                if (!rdoDiff[3].isChecked()) rdoDiff[3].toggle();
                txtName.setTextColor(Color.parseColor("#aaaaaa"));
                String name = "", type = "";
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                if (percent(1, 100) <= 10+bonus) {
                    //txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtSpecial.setText(Integer.toString(special));
                    name += "독수리를 거느린 자\n";
                    type += "돌격소총\n";
                    inputData("독수리를 거느린 자", "돌격소총");
                    setSemiInterface("돌격소총");
                }
                for (int i = 0; i < 5; i++) {
                    if (percent(1, 1000) <= 10+bonus) { //특급 장비
                        //txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtSpecial.setText(Integer.toString(special));
                        pick = percent(0, il.getSpecialweapon_Length());
                        if (i != 4) {
                            name += il.getSpecialweapon(pick)+"\n";
                            type += il.getSpecialweapon_type(pick)+"\n";
                        } else {
                            name += il.getSpecialweapon(pick);
                            type += il.getSpecialweapon_type(pick);
                        }
                        inputData(il.getSpecialweapon(pick), il.getSpecialweapon_type(pick));
                        setSemiInterface(il.getSpecialweapon_type(pick));
                        //txtName.setText(il.getSpecialweapon(pick));
                        //txtType.setText(il.getSpecialweapon_type(pick));
                    } else if (percent(1, 1000) <= 20+(bonus*2)) { //네임드 장비
                        named++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtNamed.setText(Integer.toString(named));
                        //txtName.setTextColor(Color.parseColor("#c99700"));
                        if (percent(1, 2) == 1) { //weapon
                            pick = percent(0, il.getNamedweapon_lite_Length());
                            if (i != 4) {
                                name += il.getNamedweapon_lite(pick)+"\n";
                                type += il.getNamedweapon_lite_type(pick)+"\n";
                            } else {
                                name += il.getNamedweapon_lite(pick);
                                type += il.getNamedweapon_lite_type(pick);
                            }
                            setSemiInterface(il.getNamedweapon_lite_type(pick));
                            inputData(il.getNamedweapon_lite(pick), il.getNamedweapon_lite_type(pick));
                            //txtName.setText(il.getNamedweapon_lite(pick));
                            //txtType.setText(il.getNamedweapon_lite_type(pick));
                        } else { //sheld
                            pick = percent(0, il.getNamedsheld_lite_Length());
                            if (i != 4) {
                                name += il.getNamedsheld_lite(pick)+"\n";
                                type += il.getNamedsheld_lite_type(pick)+"\n";
                            } else {
                                name += il.getNamedsheld_lite(pick);
                                type += il.getNamedsheld_lite_type(pick);
                            }
                            setSemiInterface(il.getNamedsheld_lite_type(pick));
                            inputData(il.getNamedsheld_lite(pick), il.getNamedsheld_lite_type(pick));
                            //txtName.setText(il.getNamedsheld_lite(pick));
                            //txtType.setText(il.getNamedsheld_lite_type(pick));
                        }
                    } else { //기타 장비
                        if (percent(1,2) == 1) { //weapon
                            brand++;
                            all++;
                            setInterface();
                            txtAll.setText(Integer.toString(all));
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getWeapontype_Length());
                            int temp;
                            switch (pick) {
                                case 0: //돌격소총
                                    temp = percent(0, il.getWeaponlist1_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist1(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist1(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    inputData(il.getWeaponlist1(temp), il.getWeapontype(pick));
                                    //txtName.setText(il.getWeaponlist1(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 1: //소총
                                    temp = percent(0, il.getWeaponlist2_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist2(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist2(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    inputData(il.getWeaponlist2(temp), il.getWeapontype(pick));
                                    //txtName.setText(il.getWeaponlist2(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 2: //지정사수소총
                                    temp = percent(0, il.getWeaponlist3_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist3(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist3(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    inputData(il.getWeaponlist3(temp), il.getWeapontype(pick));
                                    //txtName.setText(il.getWeaponlist3(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 3: //기관단총
                                    temp = percent(0, il.getWeaponlist4_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist4(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist4(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    inputData(il.getWeaponlist4(temp), il.getWeapontype(pick));
                                    //txtName.setText(il.getWeaponlist4(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 4: //경기관총
                                    temp = percent(0, il.getWeaponlist5_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist5(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist5(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    inputData(il.getWeaponlist5(temp), il.getWeapontype(pick));
                                    //txtName.setText(il.getWeaponlist5(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 5: //산탄총
                                    temp = percent(0, il.getWeaponlist6_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist6(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist6(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    inputData(il.getWeaponlist6(temp), il.getWeapontype(pick));
                                    //txtName.setText(il.getWeaponlist6(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 6: //권총
                                    temp = percent(0, il.getWeaponlist7_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist7(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist7(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    inputData(il.getWeaponlist7(temp), il.getWeapontype(pick));
                                    //txtName.setText(il.getWeaponlist7(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                default:
                                    txtName.setText("Error");
                                    txtType.setText("Error");
                            }
                            setSemiInterface(il.getWeapontype(pick));
                        } else { //sheld
                            int temp_pick;
                            pick = percent(0, il.getSheldtype_Length());
                            if (i != 4) type += il.getSheldtype(pick)+"\n";
                            else type += il.getSheldtype(pick);
                            temp_pick = pick;
                            setSemiInterface(il.getSheldtype(pick));
                            //txtType.setText(il.getSheldtype(pick));
                            pick = percent(1, 100);
                            if (pick <= 20) { //gear
                                gear++;
                                all++;
                                setInterface();
                                txtAll.setText(Integer.toString(all));
                                txtGear.setText(Integer.toString(gear));
                                pick = percent(0, il.getSheldgear_Length());
                                if (i != 4) name += il.getSheldgear(pick)+"\n";
                                else name += il.getSheldgear(pick);
                                //txtName.setText(il.getSheldgear(pick));
                                inputData(il.getSheldgear(pick), il.getSheldtype(pick));
                            } else { //brand
                                brand++;
                                all++;
                                setInterface();
                                txtAll.setText(Integer.toString(all));
                                txtBrand.setText(Integer.toString(brand));
                                pick = percent(0, il.getSheldbrand_Length());
                                if (i != 4) name += il.getSheldbrand(pick)+"\n";
                                else name += il.getSheldbrand(pick);
                                txtName.setText(il.getSheldbrand(pick));
                                inputData(il.getSheldbrand(pick), il.getSheldtype(temp_pick));
                            }
                        }
                    }
                }


                SpannableString spannableString = new SpannableString(name);

                String word;
                for (int i = 0; i < il.getSpecialweapon_raid_Length(); i++) {
                    word = il.getSpecialweapon_raid(i);
                    start = name.indexOf(word);
                    end = start + word.length();
                    if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c00")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                for (int i = 0; i < il.getNamedweapon_lite_Length(); i++) {
                    word = il.getNamedweapon_lite(i);
                    start = name.indexOf(word);
                    end = start + word.length();
                    if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#c99700")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                for (int i = 0; i < il.getNamedsheld_lite_Length(); i++) {
                    word = il.getNamedsheld_lite(i);
                    start = name.indexOf(word);
                    end = start + word.length();
                    if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#c99700")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                for (int i = 0; i < il.getSheldgear_Length(); i++) {
                    word = il.getSheldgear(i);
                    start = name.indexOf(word);
                    end = start + word.length();
                    if (start != -1) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#009900")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                txtName.setText(spannableString);
                txtType.setText(type);

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        return root;
    }

    public int percent(int min, int length) {
        return (int)(Math.random()*BIG)%length + min;
    }

    public void setInterface() {
        progressBrand.setProgress((int)(((double)brand/(double)all)*10000));
        progressSpecial.setProgress((int)(((double)special/(double)all)*10000));
        progressNamed.setProgress((int)(((double)named/(double)all)*10000));
        progressGear.setProgress((int)(((double)gear/(double)all)*10000));
        txtAll.setText(Integer.toString(all));
    }

    public void setSemiInterface(String type_name) {
        switch (type_name) {
            case "돌격소총":
                typet[0]++;
                txtTypelist[0].setText(Integer.toString(typet[0]));
                break;
            case "소총":
                typet[1]++;
                txtTypelist[1].setText(Integer.toString(typet[1]));
                break;
            case "지정사수소총":
                typet[2]++;
                txtTypelist[2].setText(Integer.toString(typet[2]));
                break;
            case "기관단총":
                typet[3]++;
                txtTypelist[3].setText(Integer.toString(typet[3]));
                break;
            case "경기관총":
                typet[4]++;
                txtTypelist[4].setText(Integer.toString(typet[4]));
                break;
            case "산탄총":
                typet[5]++;
                txtTypelist[5].setText(Integer.toString(typet[5]));
                break;
            case "권총":
                typet[6]++;
                txtTypelist[6].setText(Integer.toString(typet[6]));
                break;
            case "마스크":
                typet[7]++;
                txtTypelist[7].setText(Integer.toString(typet[7]));
                break;
            case "백팩":
                typet[8]++;
                txtTypelist[8].setText(Integer.toString(typet[8]));
                break;
            case "조끼":
                typet[9]++;
                txtTypelist[9].setText(Integer.toString(typet[9]));
                break;
            case "장갑":
                typet[10]++;
                txtTypelist[10].setText(Integer.toString(typet[10]));
                break;
            case "권총집":
                typet[11]++;
                txtTypelist[11].setText(Integer.toString(typet[11]));
                break;
            case "무릎 보호대":
                typet[12]++;
                txtTypelist[12].setText(Integer.toString(typet[12]));
                break;
        }
        switch (progressType[0].getMax()) {
            case 20:
                for (int i = 0; i < typet.length; i++) {
                    if (typet[i] > 20){
                        for (int j = 0; j < progressType.length; j++) progressType[j].setMax(40);
                    }
                }
                break;
            case 40:
                for (int i = 0; i < typet.length; i++) {
                    if (typet[i] > 40){
                        for (int j = 0; j < progressType.length; j++) progressType[j].setMax(60);
                    }
                }
                break;
            case 60:
                for (int i = 0; i < typet.length; i++) {
                    if (typet[i] > 60){
                        for (int j = 0; j < progressType.length; j++) progressType[j].setMax(80);
                    }
                }
                break;
            case 80:
                for (int i = 0; i < typet.length; i++) {
                    if (typet[i] > 80){
                        for (int j = 0; j < progressType.length; j++) progressType[j].setMax(100);
                    }
                }
                break;
        }
        for (int i = 0; i < progressType.length; i++) progressType[i].setProgress(typet[i]);
    }
}