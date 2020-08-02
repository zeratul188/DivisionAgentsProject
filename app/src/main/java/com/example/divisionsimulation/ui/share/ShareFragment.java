package com.example.divisionsimulation.ui.share;

import com.dinuscxj.progressbar.CircleProgressBar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.MainActivity;
import com.example.divisionsimulation.MaterialDbAdapter;
import com.example.divisionsimulation.R;
import com.example.divisionsimulation.SHDDBAdapter;
import com.example.divisionsimulation.dbdatas.ExoticFMDBAdapter;
import com.example.divisionsimulation.dbdatas.InventoryDBAdapter;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.dbdatas.NamedFMDBAdapter;
import com.example.divisionsimulation.dbdatas.SheldFMDBAdapter;
import com.example.divisionsimulation.dbdatas.TalentFMDBAdapter;
import com.example.divisionsimulation.dbdatas.WeaponFMDBAdapter;

import java.util.ArrayList;

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
    private Boolean darked = false, first_darked = true, exotic = false;

    private Button btnInput = null; //다크존 가방 담는 버튼 생성

    final private int BIG = 1234567; //램덤 함수에 쓰일 고정형 변수

    private Button btnLitezone, btnDarkzone, btnRaid, btnRaidbox, btnReset, btnOutput, btnTruesun, btnDragov, btnNewYork, btnLastBoss, btnBox, btnItemList, btnMaterialList, btnDropBox; //라이트존, 다크존, 칠흑의 시간 레이드, 레이드 박스, 초기화, 트루썬 막보, 드래고프, 뉴욕 필드보스, 전설난이도 막보, 세션 박스, 아이템 목록 버튼 객체 생성
    private Button btnIronHorse, btnIronHorseBox, btnKoyotae, btnCleaners;
    private TextView txtSpecial, txtNamed, txtGear, txtBrand, txtAll; //특급, 네임드, 기어, 일반, 전체 갯수 텍스트뷰 생성
    private Button btnMission;

    private CircleProgressBar progressSpecial, progressNamed, progressGear, progressBrand; //특급, 네임드, 기어, 브랜드의 백분율을 나타낼 진행바 객체 생성

    private int special = 0, named = 0, gear = 0, brand = 0, darkitem = 0, all = 0, temp; //특급, 네임드, 기어, 브랜드, 다크존 가방 아이템 갯수, 전체 갯수를 저장할 변수 생성
    private int max = 0;

    private int[] material = new int[10];
    private String[] material_name = {"총몸부품", "보호용 옷감", "강철", "세라믹", "폴리카보네이트", "탄소섬유", "전자부품", "티타늄", "다크존 자원", "특급 부품"};
    private String[] sheld_type = {"마스크", "백팩", "조끼", "장갑", "권총집", "무릎보호대"};
    private MaterialDbAdapter materialDbAdapter;
    private SHDDBAdapter shdAdapter;

    private int[] typet = new int[13]; //돌격소총, 소총 등 드랍된 아이템 갯수를 저장할 배열 변수 생성
    private ArrayList<Item> dark_items;

    private boolean isBtnDown;

    private Handler handler; //UI 변경시 사용할 핸들러
    private NotificationChannel channel = null; //알림때 필요한 채널

    private CircleProgressBar progressReset = null; //초기화 진행바 객체 생성

    private NotificationManager notificationManager = null; //알림 매니저 생성

    private DarkZoneTimerThread coming_dz = null; //헬기 올 때까지 사용할 다크존 이송 스레드 생성
    private DarkZoneTimerThread output_dz = null; //헬기 도착 후 사용할 다크존 이송 스레드 생성

    private TextView[] txtTypelist = new TextView[13]; //장비 종류에 따른 갯수를 표현할 텍스트뷰
    private ProgressBar[] progressType = new ProgressBar[13]; //장비 종류에 따른 진행바

    //private AlertDialog dialog_dark = null; //다크존 전용 다이얼로그 생성

    private TextView txtInfo = null; //이송 상태를 알려준다.
    private ProgressBar progressTimer = null; //이송 진행률을 진행바로 보여준다.
    //private Button btnNowOutput = null; //가방 이송헬기에 걸 때 사용하는 버튼
    private ProgressBar processOutput = null;
    private TextView txtTimer = null; //이송 진행률을 숫자로 표현해준다.

    private RadioGroup rgDifficulty; //난이도 그룹
    private RadioButton[] rdoDiff = new RadioButton[5]; //난이도 버튼 목록 (스토리/보통, 어려움, 매우어려움, 영웅)
    private int bonus = 0, option_bonus = 0; //난이도별 추가 드랍률, 난이도별 장비 추가 보너스 옵션

    private String[] item_name = new String[50]; //얻었던 아이템을 50개까지 저장
    private String[] item_type = new String[50]; //얻었던 아이템 종류를 50개까지 저장
    private int index = 0; //아이템 목록에서 새로운 아이템을 추가할 때 추가한 배열 다음에 공간을 지정해주는 역할을 한다.

    private boolean openWeapon = false; //드랍된 장비가 무기일 때 사용
    private boolean openSheld = false; //드랍된 장비가 보호장구일 때 사용
    private boolean taked = false;

    private ImageView imgType, imgType2;

    private ExoticFMDBAdapter exoticDBAdpater;
    private MaxOptionsFMDBAdapter maxoptionDBAdapter;
    private NamedFMDBAdapter namedDBAdapter;
    private SheldFMDBAdapter sheldDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private WeaponFMDBAdapter weaponDBAdpater;
    private InventoryDBAdapter inventoryDBAdapter;

    private Item item;

    public void inputItem(Item item) {
        inventoryDBAdapter.open();
        if (inventoryDBAdapter.getCount() < 300) {
            switch (item.getType()) {
                case "돌격소총":
                case "소총":
                case "지정사수소총":
                case "기관단총":
                case "산탄총":
                case "경기관총":
                case "권총":
                    inventoryDBAdapter.insertWeaponData(item.getName(), item.getType(), item.getCore1(), item.getCore2(), item.getSub1(), item.getCore1_value(), item.getCore2_value(), item.getSub1_value(), item.getTalent());
                    break;
                case "마스크":
                case "백팩":
                case "조끼":
                case "장갑":
                case "권총집":
                case "무릎보호대":
                    inventoryDBAdapter.insertSheldData(item.getName(), item.getType(), item.getCore1(), item.getSub1(), item.getSub2(), item.getCore1_value(), item.getSub1_value(), item.getSub2_value(), item.getTalent());
                    break;
            }
            Toast.makeText(getActivity(), item.getName()+"("+item.getType()+")을 인벤토리에 추가하였습니다.", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        } else Toast.makeText(getActivity(), "인벤토리가 가득찼습니다.", Toast.LENGTH_SHORT).show();
        inventoryDBAdapter.close();
    }

    public void darkInputItems(Item item) {
        inventoryDBAdapter.open();
        if (inventoryDBAdapter.getCount() < 300) {
            switch (item.getType()) {
                case "돌격소총":
                case "소총":
                case "지정사수소총":
                case "기관단총":
                case "산탄총":
                case "경기관총":
                case "권총":
                    inventoryDBAdapter.insertWeaponData(item.getName(), item.getType(), item.getCore1(), item.getCore2(), item.getSub1(), item.getCore1_value(), item.getCore2_value(), item.getSub1_value(), item.getTalent());
                    break;
                case "마스크":
                case "백팩":
                case "조끼":
                case "장갑":
                case "권총집":
                case "무릎보호대":
                    inventoryDBAdapter.insertSheldData(item.getName(), item.getType(), item.getCore1(), item.getSub1(), item.getSub2(), item.getCore1_value(), item.getSub1_value(), item.getSub2_value(), item.getTalent());
                    break;
            }
        }
        inventoryDBAdapter.close();
    }

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
    //public void setProgressTimer(int progress) { progressTimer.setProgress(progress); } //이송 진행률을 설정한다.
    //public void setTxtTimer(String message) { txtTimer.setText(message); } //이송 진행 상태(초단위로 보여주는 TextView)를 설정한다.

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
                //btnNowOutput.setEnabled(true); //이송이 도착한 후엔 다크존 가방을 이송 헬기에 걸 수 있도록 해준다.
                activate = true;
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
                AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
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

        success_extract();
        taked = false;
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
        notificationManager.cancelAll();
        failed_extract();
        taked = false;
        /*
        로그 요원에게 이송물을 탈취되었다는 메시지를 올린다.
         */
    }

    public void addTextView(int number, String name, String type, LinearLayout layout) { //아이템 목록을 열었을 때 보여줄 택스브튜를 생성한다.
        //String result = number+". "+name+" ("+type+")"; //텍스트 뷰에 메시지를 올릴 문자열을 적는다. 아이템 이름, 종류를 출력한다.
        String result = name;

        SpannableString spannableString = new SpannableString(result); //텍스트뷰 메시지 일부 글자만 색을 바꿀 때 사용한다.

        /*
        특급 색 : 오렌지 비슷한 색상
        네임드 색 : 노란색
        기어 색 : 초록색
        기타 장비 : 기본 색인 하얀색
         */

        LinearLayout itemLayout = new LinearLayout(getActivity());
        itemLayout.setBackgroundResource(R.drawable.rareitem);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(10, 10, 10, 10);
        itemLayout.setGravity(Gravity.CENTER);

        ImageView imgView = new ImageView(getActivity());
        LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        imgParam.weight = 1;
        imgView.setLayoutParams(imgParam);
        imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        switch (type) {
            case "돌격소총":
                imgView.setImageResource(R.drawable.wp1custom);
                break;
            case "소총":
                imgView.setImageResource(R.drawable.wp2custom);
                break;
            case "지정사수소총":
                imgView.setImageResource(R.drawable.wp3custom);
                break;
            case "기관단총":
                imgView.setImageResource(R.drawable.wp4custom);
                break;
            case "경기관총":
                imgView.setImageResource(R.drawable.wp5custom);
                break;
            case "산탄총":
                imgView.setImageResource(R.drawable.wp6custom);
                break;
            case "권총":
                imgView.setImageResource(R.drawable.wp7custom);
                break;
            case "마스크":
                imgView.setImageResource(R.drawable.sd1custom);
                break;
            case "백팩":
                imgView.setImageResource(R.drawable.sd4custom);
                break;
            case "조끼":
                imgView.setImageResource(R.drawable.sd2custom);
                break;
            case "장갑":
                imgView.setImageResource(R.drawable.sd5custom);
                break;
            case "권총집":
                imgView.setImageResource(R.drawable.sd3custom);
                break;
            case "무릎보호대":
                imgView.setImageResource(R.drawable.sd6custom);
                break;
        }

        String word; //찾을 문자열
        int start, end; //시작번호, 끝번호

        ArrayList<String> exoticList, namedList, gearList;
        exoticDBAdpater.open();
        exoticList = exoticDBAdpater.arrayAllData();
        exoticDBAdpater.close();
        namedDBAdapter.open();
        namedList = namedDBAdapter.arrayAllData();
        namedDBAdapter.close();
        sheldDBAdapter.open();
        gearList = sheldDBAdapter.arrayGearData("기어세트");
        sheldDBAdapter.close();

        for (int i = 0; i < exoticList.size(); i++) { //뉴욕의 지배자 확장팩 출시 후 등장한 엑조틱 장비들을 특급 색으로 변경해준다.
            word = exoticList.get(i); //찾을 문자열에 새로운 특급 장비 이름을 넣는다. 반복문으로 모든 엑조틱과 비교가 된다.
            start = result.indexOf(word); //찾을 문자열과 같은 문자열을 찾게되면 시작 번호를 알려줘 start 변수에 대입한다.
            end = start + word.length(); //시작번호로부터 찾을 문자열의 길이를 추가해 끝번호를 찾는다.
            if (start != -1) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c00")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //start가 -1이면 찾을 문자열이 없다는 뜻이므로 실행을 하지 않고 -1보다 크게 되면 찾았다는 뜻이므로 그 특정 부분만 특급 색으로 변경한다.
                itemLayout.setBackgroundResource(R.drawable.exoticitem);
            }
        }

        for (int i = 0; i < namedList.size(); i++) {
            word = namedList.get(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#c99700")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                itemLayout.setBackgroundResource(R.drawable.rareitem);
            }
        }

        for (int i = 0; i < gearList.size(); i++) {
            word = gearList.get(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#009900")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                itemLayout.setBackgroundResource(R.drawable.gearitem);
            }
        }

        /*for (int i = 0; i < il.getNewSpecialweapon_Length(); i++) { //뉴욕의 지배자 확장팩 출시 후 등장한 엑조틱 장비들을 특급 색으로 변경해준다.
            word = il.getNewSpecialweapon(i); //찾을 문자열에 새로운 특급 장비 이름을 넣는다. 반복문으로 모든 엑조틱과 비교가 된다.
            start = result.indexOf(word); //찾을 문자열과 같은 문자열을 찾게되면 시작 번호를 알려줘 start 변수에 대입한다.
            end = start + word.length(); //시작번호로부터 찾을 문자열의 길이를 추가해 끝번호를 찾는다.
            if (start != -1) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c00")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //start가 -1이면 찾을 문자열이 없다는 뜻이므로 실행을 하지 않고 -1보다 크게 되면 찾았다는 뜻이므로 그 특정 부분만 특급 색으로 변경한다.
                itemLayout.setBackgroundResource(R.drawable.exoticitem);
            }
        }
        for (int i = 0; i < il.getNamedweapon_lite_Length(); i++) {
            word = il.getNamedweapon_lite(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#c99700")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                itemLayout.setBackgroundResource(R.drawable.rareitem);
            }
        }
        for (int i = 0; i < il.getSheldgear_Length(); i++) {
            word = il.getSheldgear(i);
            start = result.indexOf(word);
            end = start + word.length();
            if (start != -1) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#009900")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                itemLayout.setBackgroundResource(R.drawable.gearitem);
            }
        }*/

        imgView.setPadding(30, 30, 30, 30);

        LinearLayout infoLayout = new LinearLayout(getActivity());
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setPadding(5, 0, 0, 0);
        LinearLayout.LayoutParams infoParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoParam.weight = 4;
        infoLayout.setLayoutParams(infoParam);

        TextView nameView = new TextView(getActivity());
        /*LinearLayout.LayoutParams nameParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nameView.setLayoutParams(nameParam);*/
        nameView.setText(spannableString);
        nameView.setTextSize(20);
        nameView.setTextColor(Color.parseColor("#f0f0f0"));

        TextView typeView = new TextView(getActivity());
        /*LinearLayout.LayoutParams typeParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        typeView.setLayoutParams(typeParam);*/
        typeView.setText(type);
        typeView.setTextSize(14);
        typeView.setTextColor(Color.parseColor("#888888"));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        //layoutParams.height = nameParam.height+typeParam.height;
        layoutParams.height = 200;
        layoutParams.bottomMargin = 15;
        itemLayout.setLayoutParams(layoutParams);

        infoLayout.addView(nameView);
        infoLayout.addView(typeView);

        itemLayout.addView(imgView);
        itemLayout.addView(infoLayout);

        /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //레이아웃
        lp.gravity = Gravity.CENTER; //왼쪽 정렬시킨다.
        view.setLayoutParams(lp);*/

        layout.addView(itemLayout); //레이아웃에 텍스트뷰를 추가한다.
    }
    public void removeTextView() { //텍스트뷰 제거 메소드 현재 사용하지 않는다.
        if (view.getParent() != null) { //뷰의 부모가 비어있지 않다면 작동
            ((ViewGroup)view.getParent()).removeView(view);
        }
    }

    private boolean material_reset = false;

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
                if (material_reset) {
                    materialDbAdapter.open();
                    for (int i = 0; i < material.length; i++) {
                        material[i] = 0;
                        materialDbAdapter.updateMaterial(material_name[i], material[i]);
                    }
                    materialDbAdapter.close();
                }
                Toast.makeText(getActivity(), "모두 초기화 되었습니다.", Toast.LENGTH_SHORT).show(); //초기화가 되었다며 토스트를 통해 전달한다.
                editor.clear();
                editor.commit();
                mHandler.removeMessages(0); //현재 핸들러를 종료시킨다.
            } else { //아직 리셋카운트로 인해 btnEnd가 참이 되지 않았을 경우 작동
                reset_count += 10; //10을 늘려준다. (1500까지 3초 걸린다.)
                progressReset.setProgress(reset_count); //리셋 카운트만큼 진행도를 설정한다.
            }
            Log.v("LC버튼", "Long클릭"); //로그를 남긴다.
            mHandler.sendEmptyMessageDelayed(0, 20); //핸들러를 0.02초만큼 반복시킨다. (다시 핸들러를 불러오는 방식으로 반복시키는 것이다.)
        }
    };

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private boolean activate = false;

    public Handler touchHandler = new Handler() {
        public void handlerMessage(Message msg) {
            Log.d("ShareFragment", "click");
        }
    };

    private class TouchThread extends Thread {

        int process = 0;

        @Override
        public void run() {
            super.run();
            while(isBtnDown) {
                touchHandler.sendEmptyMessage(9876);

                if (process >= 5000) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            output();
                        }
                    });
                    break;
                }

                process += 10;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        processOutput.setProgress(process);
                    }
                });

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    processOutput.setProgress(0);
                }
            });
        }
    }

    public void onBtnDown() {
        TouchThread tt = new TouchThread();
        tt.start();
    }

    public void output() {
        activate = false;
        first_darked = true;
        taked = true;
        darkitem = 0; //다크존 아이템을 초기화한다.
        btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
        btnOutput.setText("이송하기 ("+darkitem+"/10)");
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.mainActivity(), "헬기에 이송물을 걸었습니다.", Toast.LENGTH_SHORT).show(); //토스트를 통해 이송물을 걸었다는 것을 알려준다.
            }
        });
    }

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

        pref = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        //pref = MainActivity.mainActivity().getPreferences(Activity.MODE_PRIVATE);
        editor = pref.edit();

        dark_items = new ArrayList<Item>();

        materialDbAdapter = new MaterialDbAdapter(getActivity());
        shdAdapter = new SHDDBAdapter(getActivity());

        exoticDBAdpater = new ExoticFMDBAdapter(getActivity());
        maxoptionDBAdapter = new MaxOptionsFMDBAdapter(getActivity());
        namedDBAdapter = new NamedFMDBAdapter(getActivity());
        sheldDBAdapter = new SheldFMDBAdapter(getActivity());
        talentDBAdapter = new TalentFMDBAdapter(getActivity());
        weaponDBAdpater = new WeaponFMDBAdapter(getActivity());
        inventoryDBAdapter = new InventoryDBAdapter(getActivity());

        //editor.clear();
        //editor.commit();

        for (int i = 0; i < material.length; i++) material[i] = pref.getInt("material"+(i+1), 0);

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
        btnDropBox = root.findViewById(R.id.btnDropBox);

        btnTruesun = root.findViewById(R.id.btnTruesun);
        btnLastBoss = root.findViewById(R.id.btnLastBoss);
        btnDragov = root.findViewById(R.id.btnDragov);
        btnNewYork = root.findViewById(R.id.btnNewYork);
        btnBox = root.findViewById(R.id.btnBox);

        btnIronHorse = root.findViewById(R.id.btnIronHorse);
        btnIronHorseBox = root.findViewById(R.id.btnIronHorseBox);
        btnKoyotae = root.findViewById(R.id.btnKoyotae);
        btnCleaners = root.findViewById(R.id.btnCleaners);

        btnMission = root.findViewById(R.id.btnMission);
        btnMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExp(102453, 213265, 402154, 675426, 1012645);
                Toast.makeText(getActivity(), "임무 완수", Toast.LENGTH_LONG).show();
            }
        });

        txtSpecial = root.findViewById(R.id.txtSpecial);
        txtNamed = root.findViewById(R.id.txtNamed);
        txtGear = root.findViewById(R.id.txtGear);
        txtBrand = root.findViewById(R.id.txtBrand);

        btnItemList = root.findViewById(R.id.btnItemList);
        btnMaterialList = root.findViewById(R.id.btnMaterialList);

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

        btnLastBoss.setEnabled(false);

        rgDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) { //난이도를 선택할 때마다 작동
                switch (checkedId) {
                    case R.id.rdoDif1: //스토리/보통
                        bonus = 0; //보너스 드랍 확률
                        option_bonus = 0; //추가 옵션
                        max = 0;
                        break;
                    case R.id.rdoDif2: //어려움
                        bonus = 5;
                        option_bonus = 30;
                        max = 1;
                        break;
                    case R.id.rdoDif3: //매우어려움
                        bonus = 10;
                        option_bonus = 50;
                        max = 3;
                        break;
                    case R.id.rdoDif4: //영웅
                    case R.id.rdoDif5: //전설
                        bonus = 15;
                        option_bonus = 70;
                        max = 5;
                        break;
                }
                switch (checkedId) {
                    case R.id.rdoDif1:
                    case R.id.rdoDif2:
                    case R.id.rdoDif3:
                    case R.id.rdoDif4:
                        btnLastBoss.setEnabled(false);
                        btnDarkzone.setEnabled(true);
                        btnRaid.setEnabled(true);
                        btnRaidbox.setEnabled(true);
                        btnTruesun.setEnabled(true);
                        btnDragov.setEnabled(true);
                        btnBox.setEnabled(true);
                        btnNewYork.setEnabled(true);
                        btnDropBox.setEnabled(true);
                        btnIronHorse.setEnabled(true);
                        btnIronHorseBox.setEnabled(true);
                        btnKoyotae.setEnabled(true);
                        btnCleaners.setEnabled(true);
                        break;
                    case R.id.rdoDif5:
                        btnLastBoss.setEnabled(true);
                        btnDarkzone.setEnabled(false);
                        btnRaid.setEnabled(false);
                        btnRaidbox.setEnabled(false);
                        btnTruesun.setEnabled(false);
                        btnDragov.setEnabled(false);
                        btnBox.setEnabled(false);
                        btnNewYork.setEnabled(false);
                        btnDropBox.setEnabled(false);
                        btnIronHorse.setEnabled(false);
                        btnIronHorseBox.setEnabled(false);
                        btnKoyotae.setEnabled(false);
                        btnCleaners.setEnabled(false);
                        break;
                }
            }
        });

        if (pref.getBoolean("Saved", false)) startInterface();

        final View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null); //아이템 드랍할때마다 보여줄 뷰이다.

        final TextView txtName = dialogView.findViewById(R.id.txtName); //장비 이름
        final TextView txtType = dialogView.findViewById(R.id.txtType); //장비 종류
        final Button btnChange = dialogView.findViewById(R.id.btnChange); //특급, 네임드일 경우 내용을 바로 보여주지 않고 이 버튼으로 누르면 보여주도록 해준다.
        final LinearLayout tableMain = dialogView.findViewById(R.id.tableMain); //내용이 들어있는 테이블 레이아웃
        //final ImageView[] imgOption = new ImageView[3];
        //final TableRow trOption = dialogView.findViewById(R.id.trOption);
        final Button btnExit = dialogView.findViewById(R.id.btnExit); //닫기 버튼\
        final Button btnDestroy = dialogView.findViewById(R.id.btnDestroy);
        final Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        imgType = dialogView.findViewById(R.id.imgType);

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
        final LinearLayout layoutTalent = dialogView.findViewById(R.id.layoutTalent);
        final LinearLayout layoutTalentButton = dialogView.findViewById(R.id.layoutTalentButton);

        final LinearLayout layoutWeapon = dialogView.findViewById(R.id.layoutWeapon); //무기 속성 레이아웃
        final LinearLayout layoutSheld = dialogView.findViewById(R.id.layoutSheld); //보호장구 속성 레이아웃
        final LinearLayout layoutSSub2 = dialogView.findViewById(R.id.layoutSSub2);
        
        final TextView txtInventory = dialogView.findViewById(R.id.txtInventory);
        final LinearLayout layoutInventory = dialogView.findViewById(R.id.layoutInventory);

        final TextView txtWTalentContent = dialogView.findViewById(R.id.txtWTalentContent);

        final LinearLayout layoutWeaponMain1 = dialogView.findViewById(R.id.layoutWeaponMain1);
        final LinearLayout layoutWeaponMain2 = dialogView.findViewById(R.id.layoutWeaponMain2);
        final LinearLayout layoutWeaponSub = dialogView.findViewById(R.id.layoutWeaponSub);
        final LinearLayout layoutSheldMain = dialogView.findViewById(R.id.layoutSheldMain);
        final LinearLayout layoutSheldSub1 = dialogView.findViewById(R.id.layoutSheldSub1);
        final LinearLayout layoutSheldSub2 = dialogView.findViewById(R.id.layoutSheldSub2);

        btnInput = dialogView.findViewById(R.id.btnInput);
        /*
        위들과 동일한 방식이지만 프래그먼트에서 추가하는 것이 아닌 위 다이얼로그뷰에서 추가한다.
         */


        /*for (int i = 0; i < imgOption.length; i++) {
            temp = dialogView.getResources().getIdentifier("imgOption"+(i+1), "id", getActivity().getPackageName());
            imgOption[i] = dialogView.findViewById(temp);
        }*/

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /*final View dark_dialogView = getLayoutInflater().inflate(R.layout.itemlayout_dark, null);

        final TextView txtName2 = dark_dialogView.findViewById(R.id.txtName);
        final TextView txtType2 = dark_dialogView.findViewById(R.id.txtType);
        final Button btnChange2 = dark_dialogView.findViewById(R.id.btnChange);
        final LinearLayout tableMain2 = dark_dialogView.findViewById(R.id.tableMain);
        imgType2 = dark_dialogView.findViewById(R.id.imgType);
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

        //btnInput = dark_dialogView.findViewById(R.id.btnInput); //다크존 아이템을 다크존 가방에 담는 버튼이다.

        /*btnExit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_dark.dismiss();
            }
        });*/ //닫기 버튼을 누르게 되면 다크존 다이얼로그가 닫히게 된다.

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
                    if (item_name[i] != null && !item_name[i].equals("")) addTextView(i+1, item_name[i], item_type[i], layoutItemList); //아이템 이름이 비어있지 않다면 그 아이템 정보를 통해 텍스트뷰를 생성한다. (위쪽에 메소드 존재)
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

        btnMaterialList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView_list = getLayoutInflater().inflate(R.layout.materiallayout, null); //다이얼로그에 추가할 뷰 생성
                Button btnMaterialExit = dialogView_list.findViewById(R.id.btnMaterialExit); //팝업창을 닫을 버튼

                btnMaterialExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //버튼을 누르면 작동
                        alertDialog_list.dismiss(); //다이얼로그를 닫는다.
                    }
                });

                Cursor cursor;
                materialDbAdapter.open();
                cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material[count] = cursor.getInt(2);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();

                TextView[] txtNormal = new TextView[2];
                TextView[] txtRare = new TextView[3];
                TextView[] txtEpic = new TextView[3];
                TextView txtDark = dialogView_list.findViewById(R.id.txtDark);
                ProgressBar progressDark = dialogView_list.findViewById(R.id.progressDark);
                TextView txtExotic = dialogView_list.findViewById(R.id.txtExotic);
                ProgressBar progressExotic = dialogView_list.findViewById(R.id.progressExotic);

                ProgressBar[] progressNormal = new ProgressBar[2];
                ProgressBar[] progressRare = new ProgressBar[3];
                ProgressBar[] progressEpic = new ProgressBar[3];

                int resource;
                for (int i = 0; i < txtNormal.length; i++) {
                    resource = dialogView_list.getResources().getIdentifier("txtNormal"+(i+1), "id", getActivity().getPackageName());
                    txtNormal[i] = dialogView_list.findViewById(resource);
                    resource = dialogView_list.getResources().getIdentifier("progressNormal"+(i+1), "id", getActivity().getPackageName());
                    progressNormal[i] = dialogView_list.findViewById(resource);
                    progressNormal[i].setMax(2000);
                    progressNormal[i].setProgress(material[i]);
                    txtNormal[i].setText(Integer.toString(material[i]));
                }
                for (int i = 0; i < txtRare.length; i++) {
                    resource = dialogView_list.getResources().getIdentifier("txtRare"+(i+1), "id", getActivity().getPackageName());
                    txtRare[i] = dialogView_list.findViewById(resource);
                    resource = dialogView_list.getResources().getIdentifier("txtEpic"+(i+1), "id", getActivity().getPackageName());
                    txtEpic[i] = dialogView_list.findViewById(resource);
                    resource = dialogView_list.getResources().getIdentifier("progressRare"+(i+1), "id", getActivity().getPackageName());
                    progressRare[i] = dialogView_list.findViewById(resource);
                    resource = dialogView_list.getResources().getIdentifier("progressEpic"+(i+1), "id", getActivity().getPackageName());
                    progressEpic[i] = dialogView_list.findViewById(resource);
                    progressRare[i].setMax(1500);
                    progressEpic[i].setMax(1500);
                    progressRare[i].setProgress(material[i+2]);
                    progressEpic[i].setProgress(material[i+5]);
                    txtRare[i].setText(Integer.toString(material[i+2]));
                    txtEpic[i].setText(Integer.toString(material[i+5]));
                }
                txtDark.setText(Integer.toString(material[8]));
                progressDark.setMax(300);
                progressDark.setProgress(material[8]);

                txtExotic.setText(Integer.toString(material[9]));
                progressExotic.setMax(20);
                progressExotic.setProgress(material[9]);

                builder_list = new AlertDialog.Builder(getActivity());
                builder_list.setView(dialogView_list);

                alertDialog_list = builder_list.create();
                alertDialog_list.setCancelable(false);
                alertDialog_list.show();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setTitle("초기화");
                builder.setMessage("재료 모두 초기화 하시겠습니까?");

                builder.setPositiveButton("모두 초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        material_reset = true;
                        resetDialog();
                    }
                });
                builder.setNegativeButton("재료 제외 초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        material_reset = false;
                        resetDialog();
                    }
                });
                builder.setNeutralButton("취소", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
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
                layoutTalentButton.setVisibility(View.VISIBLE);
            }
        });

        /*btnChange2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일 (다크존 전용)
                if (openWeapon) layoutWeapon_dark.setVisibility(View.VISIBLE);
                else layoutWeapon_dark.setVisibility(View.GONE);
                if (openSheld) layoutSheld_dark.setVisibility(View.VISIBLE);
                else layoutSheld_dark.setVisibility(View.GONE);
                tableMain2.setVisibility(View.VISIBLE);
                btnChange2.setVisibility(View.GONE);
            }
        });*/

        btnExit.setOnClickListener(new View.OnClickListener() { //닫기 버튼 누를 경우 작동
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); //닫기 버튼을 누르면 다이얼로그가 닫힌다.
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputItem(item);
            }
        });

        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = String.valueOf(txtType.getText());
                String normal_str = "", rare_str = "", epic_str = "";
                int normal = 0, rare = 0, epic = 0;
                int random_select;
                if (darked) {
                    if (first_darked) {
                        if (darkitem < 10) {
                            darkitem++; //다크존 가방 아이템 수를 1 늘려준다.
                            btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)"); //버튼 텍스트를 업데이트한다.
                            btnOutput.setText("이송하기 ("+darkitem+"/10)"); //위와 동일
                            first_darked = false;
                        } else {
                            Toast.makeText(getActivity(), "다크존 가방이 가득찼습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    material[8]++;
                    if (material[8] >= 300) material[8] = 300;
                    materialDbAdapter.open();
                    materialDbAdapter.updateMaterial(material_name[8], material[8]);
                    materialDbAdapter.close();
                    Toast.makeText(getActivity(), "다크존 자원을 획득하였습니다.", Toast.LENGTH_SHORT).show();
                } else if (exotic) {
                    material[9]++;
                    if (material[9] >= 20) material[8] = 20;
                    materialDbAdapter.open();
                    materialDbAdapter.updateMaterial(material_name[9], material[9]);
                    materialDbAdapter.close();
                    Toast.makeText(getActivity(), "특급 부품을 획득하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    switch (str) {
                        case "돌격소총": case "소총": case "지정사수소총": case "기관단총": case "경기관총": case "산탄총": case "권총":
                            normal = percent(10, 12);
                            if (material[0] < 2000) material[0] += normal;
                            if (material[0] >= 2000) material[0] = 2000;
                            normal_str = material_name[0];
                            break;
                        case "마스크": case "조끼": case "백팩": case "장갑": case "권총집": case "무릎보호대":
                            normal = percent(10, 12);
                            if (material[1] < 2000) material[1] += normal;
                            if (material[1] >= 2000) material[1] = 2000;
                            normal_str = material_name[1];
                            break;
                    }
                    random_select = percent(2, 3);
                    rare = percent(7, 6);
                    material[random_select] += rare;
                    if (material[random_select] >= 1500) material[random_select] = 1500;
                    rare_str = material_name[random_select];
                    random_select = percent(5, 3);
                    epic = percent(3, 5);
                    material[random_select] += epic;
                    if (material[random_select] >= 1500) material[random_select] = 1500;
                    epic_str = material_name[random_select];
                    materialDbAdapter.open();
                    for (int i = 0; i < material.length; i++) {
                        materialDbAdapter.updateMaterial(material_name[i], material[i]);
                    }
                    materialDbAdapter.close();
                    Toast.makeText(getActivity(), normal_str+" +"+normal+", "+rare_str+" +"+rare+", "+epic_str+" +"+epic, Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //이송 물품 다크존 가방에 담는 버튼
                if (darkitem < 10) { //다크존 가방에 있는 아이템 갯수가 10개 미만일 경우
                    dark_items.add(item);
                    darkitem++; //다크존 가방 아이템 수를 1 늘려준다.
                    btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)"); //버튼 텍스트를 업데이트한다.
                    btnOutput.setText("이송하기 ("+darkitem+"/10)"); //위와 동일
                    alertDialog.dismiss(); //다크존 다이얼로그를 닫는다.
                } else { //다크존 가방에 있는 아이템 갯수가 10개 이상을 경우
                    AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
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

        btnOutput.setOnClickListener(new View.OnClickListener() {
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
                    //btnNowOutput = dialogView_timer.findViewById(R.id.btnNowOutput); //이송헬기에 가방을 걸 버튼이다.
                    processOutput = dialogView_timer.findViewById(R.id.progressOutput);
                    txtTimer = dialogView_timer.findViewById(R.id.txtTimer); //남은 시간을 나타낸다.
            /*btnNowOutput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //가방을 거는 버튼을 눌렀을 경우
                    darkitem = 0; //다크존 아이템을 초기화한다.
                    btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
                    btnOutput.setText("이송하기 ("+darkitem+"/10)");
                    Toast.makeText(getActivity(), "헬기에 이송물을 걸었습니다.", Toast.LENGTH_SHORT).show(); //토스트를 통해 이송물을 걸었다는 것을 알려준다.
                    btnNowOutput.setEnabled(false); //이송물 걸기 버튼을 비활성화시킨다.
                }
            });*/
                    processOutput.setMax(5000);
                    processOutput.setProgress(0);
                    processOutput.setClickable(true);

                    processOutput.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    if (activate) {
                                        isBtnDown = true;
                                        onBtnDown();
                                    } else Toast.makeText(MainActivity.mainActivity(), "이송물을 부착하였거나 이송헬기가 도착하지 않았습니다.", Toast.LENGTH_SHORT).show();
                                    break;
                                case MotionEvent.ACTION_UP:
                                    isBtnDown = false;
                                    processOutput.setProgress(0);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });

                    coming_dz = new DarkZoneTimerThread(handler, getActivity(), ShareFragment.this, txtTimer, progressTimer); //헬기 오기 전 타이머 스레드
                    output_dz = new DarkZoneTimerThread(handler, getActivity(), ShareFragment.this, txtTimer, progressTimer); //헬기 도착 후 타이머 스레드

                    progressTimer.setMax(10000); //타이머의 최대치를 설정
                    progressTimer.setProgress(0); //타이머의 진행도를 초기화

                    builder_timer = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle); //타이머 빌더를 생성
                    builder_timer.setView(dialogView_timer);
                    builder_timer.setPositiveButton("즉시 이송", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //즉시 이송을 누르면 작동
                            taked = false;
                            for (int i = 0; i < dark_items.size(); i++) {
                                darkInputItems(dark_items.get(i));
                            }
                            dark_items.clear();
                            darkitem = 0; //다크존 아이템을 0으로 초기화
                            btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)"); //위와 동일한 방식
                            btnOutput.setText("이송하기 ("+darkitem+"/10)"); //위와 동일한 방식
                            Toast.makeText(getActivity(), "즉시 이송시켰습니다.", Toast.LENGTH_SHORT).show(); //즉시 이송 완료 메시지를 토스트로 통해 알려준다.
                            coming_dz.stopThread(); //헬기 오기 전 스레드를 종료
                            output_dz.stopThread(); //헬기 도착 후 스레드를 종료
                            output_dz.setRogue(true); //로그 세팅
                            coming_dz.setRogue(true); //위와 동일
                            notificationManager.cancelAll(); //모든 알림 제거
                            first_darked = true;
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
                            taked = false;
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
                                if (percent(1, 4) == 1) {
                                    Toast.makeText(getActivity(), "이송물은 버려둔 채로 이송지점에서 벗어났지만 이송에 성공하였습니다.", Toast.LENGTH_SHORT).show(); //25% 확률로 자동으로 이송 성공
                                    for (int i = 0; i < dark_items.size(); i++) {
                                        darkInputItems(dark_items.get(i));
                                    }
                                    dark_items.clear();
                                } else {
                                    Toast.makeText(getActivity(), "이송물은 버려둔 채로 이송지점에서 벗어났으나 로그 요원에게 이송물을 탈취당했습니다.", Toast.LENGTH_SHORT).show(); //75% 확률로 로그요원들에게 탈취
                                    dark_items.clear();
                                }
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
                            isBtnDown = false;
                            activate = false;
                            /*
                            스레드들을 종료
                             */
                        }
                    });
                    alertDialog_timer = builder_timer.create(); //다이얼로그 객체 생성
                    alertDialog_timer.setCancelable(false); //바깥영역 또는 뒤로가기 버튼을 눌러도 꺼지지 않게 해준다.
                    alertDialog_timer.show(); //다이얼로그를 화면에 띄운다.

                    coming_dz.setMinute(0); //헬기 도착 전 스레드 분을 0으로 설정
                    coming_dz.setSecond(40); //헬기 도착 전 스레드 초를 40초로 설정 //40

                    output_dz.setMinute(1); //1
                    output_dz.setSecond(0); //0
                    //1분 타이머를 설정

                    coming_dz.setRoguePercent(3);
                    output_dz.setRoguePercent(5);
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
                setExp(25846, 40326, 85542, 101141, 0);
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 10+(bonus*4)) {
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                     //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "\"타디그레이드\" 방탄복 시스템";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_core1 = cursor.getString(3);
                    item_sub1 = cursor.getString(4);
                    item_sub2 = cursor.getString(5);
                    item_core1_type = cursor.getString(6);
                    item_sub1_type = cursor.getString(7);
                    item_sub2_type = cursor.getString(8);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openSheld = true;
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchData(item_core1);
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    if (tail_core1.equals("-")) tail_core1 = "";
                    cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    System.out.println(item_sub2);
                    cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub2);
                    max_sub2 = Double.parseDouble(cursor.getString(2));
                    tail_sub2 = cursor.getString(5);
                    if (tail_sub2.equals("-")) tail_sub2 = "";
                    maxoptionDBAdapter.close();
                    progressSMain.setMax((int)(max_core1*10));
                    core1 = max_core1;
                    if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSMain.setProgress((int)(core1*10));
                    txtSMain.setText("+"+(int)core1+tail_core1+" "+item_core1);
                    changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub1.setMax((int)(max_sub1*10));
                    progressSSub1.setProgress((int)(sub1*10)); //속성1의 진행도 설정
                    txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub2.setMax((int)(max_sub2*10));
                    progressSSub2.setProgress((int)(sub2*10)); //속성1의 진행도 설정
                    txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    txtWTalent.setText(item_talent);
                    System.out.println("SMain Max : "+progressSMain.getMax()+"\nSMain Progress : "+progressSMain.getProgress()+"\nSSub1 Max : "+progressSSub1.getMax()+"\nSSub1 Progress : "+progressSSub1.getProgress()+"\nSSub2 Max : "+progressSSub2.getMax()+"\nSSub2 Progress"+progressSSub2.getProgress());
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 2) { //2
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                     //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnKoyotae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //트루썬 마지막 보스 처치할 경우
                setExp(25846, 40326, 85542, 101141, 0);
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 10+(bonus*4)) {
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                    //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "코요테의 마스크";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_core1 = cursor.getString(3);
                    item_sub1 = cursor.getString(4);
                    item_sub2 = cursor.getString(5);
                    item_core1_type = cursor.getString(6);
                    item_sub1_type = cursor.getString(7);
                    item_sub2_type = cursor.getString(8);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openSheld = true;
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchData(item_core1);
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    if (tail_core1.equals("-")) tail_core1 = "";
                    cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    System.out.println(item_sub2);
                    cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub2);
                    max_sub2 = Double.parseDouble(cursor.getString(2));
                    tail_sub2 = cursor.getString(5);
                    if (tail_sub2.equals("-")) tail_sub2 = "";
                    maxoptionDBAdapter.close();
                    progressSMain.setMax((int)(max_core1*10));
                    core1 = max_core1;
                    if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSMain.setProgress((int)(core1*10));
                    txtSMain.setText("+"+(int)core1+tail_core1+" "+item_core1);
                    changeImageType(item_core1_type, imgSMain, progressSMain);
                    changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                    changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub1.setMax((int)(max_sub1*10));
                    progressSSub1.setProgress((int)(sub1*10)); //속성1의 진행도 설정
                    txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub2.setMax((int)(max_sub2*10));
                    progressSSub2.setProgress((int)(sub2*10)); //속성1의 진행도 설정
                    txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    txtWTalent.setText(item_talent);
                    System.out.println("SMain Max : "+progressSMain.getMax()+"\nSMain Progress : "+progressSMain.getProgress()+"\nSSub1 Max : "+progressSSub1.getMax()+"\nSSub1 Progress : "+progressSSub1.getProgress()+"\nSSub2 Max : "+progressSSub2.getMax()+"\nSSub2 Progress"+progressSSub2.getProgress());
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 2) { //2
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                    //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub1.setImageResource(R.drawable.attack);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub1.setImageResource(R.drawable.sheld);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub1.setImageResource(R.drawable.power);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnCleaners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //트루썬 마지막 보스 처치할 경우
                setExp(25846, 40326, 85542, 101141, 0);
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 10+(bonus*4)) {
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                    //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "제국의 왕가";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_core1 = cursor.getString(3);
                    item_sub1 = cursor.getString(4);
                    item_sub2 = cursor.getString(5);
                    item_core1_type = cursor.getString(6);
                    item_sub1_type = cursor.getString(7);
                    item_sub2_type = cursor.getString(8);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openSheld = true;
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchData(item_core1);
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    if (tail_core1.equals("-")) tail_core1 = "";
                    cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    System.out.println(item_sub2);
                    cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub2);
                    max_sub2 = Double.parseDouble(cursor.getString(2));
                    tail_sub2 = cursor.getString(5);
                    if (tail_sub2.equals("-")) tail_sub2 = "";
                    maxoptionDBAdapter.close();
                    progressSMain.setMax((int)(max_core1*10));
                    core1 = max_core1;
                    if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSMain.setProgress((int)(core1*10));
                    txtSMain.setText("+"+(int)core1+tail_core1+" "+item_core1);
                    changeImageType(item_core1_type, imgSMain, progressSMain);
                    changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                    changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub1.setMax((int)(max_sub1*10));
                    progressSSub1.setProgress((int)(sub1*10)); //속성1의 진행도 설정
                    txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub2.setMax((int)(max_sub2*10));
                    progressSSub2.setProgress((int)(sub2*10)); //속성1의 진행도 설정
                    txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    txtWTalent.setText(item_talent);
                    System.out.println("SMain Max : "+progressSMain.getMax()+"\nSMain Progress : "+progressSMain.getProgress()+"\nSSub1 Max : "+progressSSub1.getMax()+"\nSSub1 Progress : "+progressSSub1.getProgress()+"\nSSub2 Max : "+progressSSub2.getMax()+"\nSSub2 Progress"+progressSSub2.getProgress());
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 2) { //2
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                    //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub1.setImageResource(R.drawable.attack);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub1.setImageResource(R.drawable.sheld);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub1.setImageResource(R.drawable.power);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //세션 박스를 열었을 경우, 위와 내용이 비슷하므로 설명 생략
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 30) {
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                     //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "아코스타의 비상 가방";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_core1 = cursor.getString(3);
                    item_sub1 = cursor.getString(4);
                    item_sub2 = cursor.getString(5);
                    item_core1_type = cursor.getString(6);
                    item_sub1_type = cursor.getString(7);
                    item_sub2_type = cursor.getString(8);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openSheld = true;
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchData(item_core1);
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    if (tail_core1.equals("-")) tail_core1 = "";
                    cursor = maxoptionDBAdapter.fetchData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    System.out.println(item_sub2);
                    cursor = maxoptionDBAdapter.fetchData(item_sub2);
                    max_sub2 = Double.parseDouble(cursor.getString(2));
                    tail_sub2 = cursor.getString(5);
                    if (tail_sub2.equals("-")) tail_sub2 = "";
                    maxoptionDBAdapter.close();
                    progressSMain.setMax((int)(max_core1*10));
                    core1 = max_core1;
                    if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSMain.setProgress((int)(core1*10));
                    txtSMain.setText("+"+(int)core1+tail_core1+" "+item_core1);
                    changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub1.setMax((int)(max_sub1*10));
                    progressSSub1.setProgress((int)(sub1*10)); //속성1의 진행도 설정
                    txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub2.setMax((int)(max_sub2*10));
                    progressSSub2.setProgress((int)(sub2*10)); //속성1의 진행도 설정
                    txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    txtWTalent.setText(item_talent);
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnLastBoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //전설난이도에서 마지막 보스를 잡았을 경우, 위와 내용이 비슷하므로 설명 생략
                setExp(0, 0, 0, 0, 250000);
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 50) { //50
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                     //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "빅혼";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_sub1 = cursor.getString(4);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_core1 = item_type+" 데미지";
                    openWeapon = true;
                    txtWTalent.setText(item_talent);
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchTypeData("무기");
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 4+max) temp_percent = 100;
                    else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (!item_type.equals("권총")) {
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                        max_core2 = Double.parseDouble(cursor.getString(2));
                        tail_core2 = cursor.getString(5);
                        item_core2 = cursor.getString(1);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        if (tail_core2.equals("-")) tail_core2 = "";
                        txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                        progressWMain2.setMax((int)(max_core2*10));
                        progressWMain2.setProgress((int)(core2*10));
                    } else {
                        layoutWeaponMain2.setVisibility(View.GONE);
                    }
                    maxoptionDBAdapter.open();
                    System.out.println(item_sub1);
                    cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 4+max) temp_percent = 100;
                    else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (tail_core1.equals("-")) tail_core1 = "";
                    txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                    progressWMain1.setMax((int)(max_core1*10));
                    progressWMain1.setProgress((int)(core1*10));
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    progressWSub.setMax((int)(max_sub1*10));
                    progressWSub.setProgress((int)(sub1*10));
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 2) { //2
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                     //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 4+max) temp_percent = 100;
                            else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 4+max) temp_percent = 100;
                            else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 4+max) temp_percent = 100;
                            else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 4+max) temp_percent = 100;
                        else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 4+max) temp_percent = 100;
                            else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 4+max) temp_percent = 100;
                                else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 4+max) temp_percent = 100;
                            else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 4+max) temp_percent = 100;
                            else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 4+max) temp_percent = 100;
                            else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 4+max) temp_percent = 100;
                            else if (pick <= 50) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnDragov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //월 스트리트 미션에서 마지막 보스 제임스 드래고프를 처치했을 경우, 위와 내용이 비슷하므로 설명 생략
                setExp(25846, 40326, 85542, 101141, 0);
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 20+(bonus*4)) { //20+(bonus*4)
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                     //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "탄환 제왕";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_sub1 = cursor.getString(4);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    item_core1 = item_type+" 데미지";
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openWeapon = true;
                    txtWTalent.setText(item_talent);
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchTypeData("무기");
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (!item_type.equals("권총")) {
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                        max_core2 = Double.parseDouble(cursor.getString(2));
                        tail_core2 = cursor.getString(5);
                        item_core2 = cursor.getString(1);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        if (tail_core2.equals("-")) tail_core2 = "";
                        txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                        progressWMain2.setMax((int)(max_core2*10));
                        progressWMain2.setProgress((int)(core2*10));
                    } else {
                        layoutWeaponMain2.setVisibility(View.GONE);
                    }
                    maxoptionDBAdapter.open();
                    System.out.println(item_sub1);
                    cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (tail_core1.equals("-")) tail_core1 = "";
                    txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                    progressWMain1.setMax((int)(max_core1*10));
                    progressWMain1.setProgress((int)(core1*10));
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    progressWSub.setMax((int)(max_sub1*10));
                    progressWSub.setProgress((int)(sub1*10));
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 2) { //2
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                     //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnNewYork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //뉴욕에서 필드 보스를 잡았을 경우, 위와 내용이 비슷하므로 설명 생략
                setExp(17846, 34326, 67542, 81141, 0);
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 30) { //30
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                     //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "죽음의 귀부인";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_sub1 = cursor.getString(4);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openWeapon = true;
                    txtWTalent.setText(item_talent);
                    item_core1 = item_type+" 데미지";
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchTypeData("무기");
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (!item_type.equals("권총")) {
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                        max_core2 = Double.parseDouble(cursor.getString(2));
                        tail_core2 = cursor.getString(5);
                        item_core2 = cursor.getString(1);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        if (tail_core2.equals("-")) tail_core2 = "";
                        txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                        progressWMain2.setMax((int)(max_core2*10));
                        progressWMain2.setProgress((int)(core2*10));
                    } else {
                        layoutWeaponMain2.setVisibility(View.GONE);
                    }
                    maxoptionDBAdapter.open();
                    System.out.println(item_sub1);
                    cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (tail_core1.equals("-")) tail_core1 = "";
                    txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                    progressWMain1.setMax((int)(max_core1*10));
                    progressWMain1.setProgress((int)(core1*10));
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    progressWSub.setMax((int)(max_sub1*10));
                    progressWSub.setProgress((int)(sub1*10));
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 2) { //2
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                     //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnLitezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //라이트존에서 적을 죽였을 경우, 위와 내용이 비슷하므로 설명 생략
                setExp(946, 1926, 4542, 8141, 10114);
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 1) { //2
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                     //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                            System.out.println("Talent : "+item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                            System.out.println("Talent : "+item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnDarkzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //다크존에서 적을 죽였을 경우, 위와 내용이 비슷하므로 설명 생략
                setExp(0, 0, 7441, 0, 0);
                if (!rdoDiff[2].isChecked()) rdoDiff[2].toggle();
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = true;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                layoutInventory.setVisibility(View.GONE);
                btnAdd.setVisibility(View.GONE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.VISIBLE);

                if (percent(1, 1000) <= 30) { //30
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                     //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "역병";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_sub1 = cursor.getString(4);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_core1 = item_type+" 데미지";
                    openWeapon = true;
                    txtWTalent.setText(item_talent);
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchTypeData("무기");
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (!item_type.equals("권총")) {
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                        max_core2 = Double.parseDouble(cursor.getString(2));
                        tail_core2 = cursor.getString(5);
                        item_core2 = cursor.getString(1);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        if (tail_core2.equals("-")) tail_core2 = "";
                        txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                        progressWMain2.setMax((int)(max_core2*10));
                        progressWMain2.setProgress((int)(core2*10));
                    } else {
                        layoutWeaponMain2.setVisibility(View.GONE);
                    }
                    maxoptionDBAdapter.open();
                    System.out.println(item_sub1);
                    cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (tail_core1.equals("-")) tail_core1 = "";
                    txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                    progressWMain1.setMax((int)(max_core1*10));
                    progressWMain1.setProgress((int)(core1*10));
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    progressWSub.setMax((int)(max_sub1*10));
                    progressWSub.setProgress((int)(sub1*10));
                } else if (percent(1, 100) <= 1) { //1
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                     //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchDarkData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchDarkData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        btnDropBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExp(0, 0, 15542, 0, 0);
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = true;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                layoutInventory.setVisibility(View.GONE);
                btnAdd.setVisibility(View.GONE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.VISIBLE);

                if (percent(1, 1000) <= 30) { //30
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                     //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "닌자바이크 메신저 무릎 보호대";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_core1 = cursor.getString(3);
                    item_sub1 = cursor.getString(4);
                    item_sub2 = cursor.getString(5);
                    item_core1_type = cursor.getString(6);
                    item_sub1_type = cursor.getString(7);
                    item_sub2_type = cursor.getString(8);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openSheld = true;
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchData(item_core1);
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    if (tail_core1.equals("-")) tail_core1 = "";
                    cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    System.out.println(item_sub2);
                    cursor = maxoptionDBAdapter.fetchSheldSubData(item_sub2);
                    max_sub2 = Double.parseDouble(cursor.getString(2));
                    tail_sub2 = cursor.getString(5);
                    if (tail_sub2.equals("-")) tail_sub2 = "";
                    maxoptionDBAdapter.close();
                    progressSMain.setMax((int)(max_core1*10));
                    core1 = max_core1;
                    if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSMain.setProgress((int)(core1*10));
                    txtSMain.setText("+"+(int)core1+tail_core1+" "+item_core1);
                    changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub1.setMax((int)(max_sub1*10));
                    progressSSub1.setProgress((int)(sub1*10)); //속성1의 진행도 설정
                    txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    progressSSub2.setMax((int)(max_sub2*10));
                    progressSSub2.setProgress((int)(sub2*10)); //속성1의 진행도 설정
                    txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    txtWTalent.setText(item_talent);
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 2) { //2
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                     //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchDarkData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchDarkData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnRaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //칠흑의 시간 레이드에서 네임드 보스를 죽였을 경우, 위와 내용이 비슷하므로 설명 생략
                setExp(0, 0, 0, 121141, 0);
                if (!rdoDiff[3].isChecked()) rdoDiff[3].toggle();
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 5) { //20+(bonus*4)
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                     //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "독수리를 거느린 자";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_sub1 = cursor.getString(4);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openWeapon = true;
                    item_core1 = item_type+" 데미지";
                    txtWTalent.setText(item_talent);
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchTypeData("무기");
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (!item_type.equals("권총")) {
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                        max_core2 = Double.parseDouble(cursor.getString(2));
                        tail_core2 = cursor.getString(5);
                        item_core2 = cursor.getString(1);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        if (tail_core2.equals("-")) tail_core2 = "";
                        txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                        progressWMain2.setMax((int)(max_core2*10));
                        progressWMain2.setProgress((int)(core2*10));
                    } else {
                        layoutWeaponMain2.setVisibility(View.GONE);
                    }
                    maxoptionDBAdapter.open();
                    System.out.println(item_sub1);
                    cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (tail_core1.equals("-")) tail_core1 = "";
                    txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                    progressWMain1.setMax((int)(max_core1*10));
                    progressWMain1.setProgress((int)(core1*10));
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    progressWSub.setMax((int)(max_sub1*10));
                    progressWSub.setProgress((int)(sub1*10));
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 1) { //1
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                     //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                            }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
                            
                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
                            
                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
                            
                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnRaidbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //칠흑의 시간 레이드에서 마지막 보스 처치 후 상자 개봉할 경우, 위와 내용은 비슷하나 박스에서는 5개의 아이템이 나온다. 이 부분만 설명함.
                setExp(0, 0, 0, 121141, 0);
                if (!rdoDiff[3].isChecked()) rdoDiff[3].toggle();
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 100) { //20+(bonus*4)
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                    //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "독수리를 거느린 자";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_sub1 = cursor.getString(4);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openWeapon = true;
                    item_core1 = item_type+" 데미지";
                    txtWTalent.setText(item_talent);
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchTypeData("무기");
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (!item_type.equals("권총")) {
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                        max_core2 = Double.parseDouble(cursor.getString(2));
                        tail_core2 = cursor.getString(5);
                        item_core2 = cursor.getString(1);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        if (tail_core2.equals("-")) tail_core2 = "";
                        txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                        progressWMain2.setMax((int)(max_core2*10));
                        progressWMain2.setProgress((int)(core2*10));
                    } else {
                        layoutWeaponMain2.setVisibility(View.GONE);
                    }
                    maxoptionDBAdapter.open();
                    System.out.println(item_sub1);
                    cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (tail_core1.equals("-")) tail_core1 = "";
                    txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                    progressWMain1.setMax((int)(max_core1*10));
                    progressWMain1.setProgress((int)(core1*10));
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    progressWSub.setMax((int)(max_sub1*10));
                    progressWSub.setProgress((int)(sub1*10));
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub1.setImageResource(R.drawable.attack);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub1.setImageResource(R.drawable.sheld);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub1.setImageResource(R.drawable.power);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        btnIronHorse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //칠흑의 시간 레이드에서 네임드 보스를 죽였을 경우, 위와 내용이 비슷하므로 설명 생략
                setExp(0, 0, 0, 121141, 0);
                if (!rdoDiff[3].isChecked()) rdoDiff[3].toggle();
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 5) { //20+(bonus*4)
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                    //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "탐식자";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_sub1 = cursor.getString(4);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openWeapon = true;
                    item_core1 = item_type+" 데미지";
                    txtWTalent.setText(item_talent);
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchTypeData("무기");
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (!item_type.equals("권총")) {
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                        max_core2 = Double.parseDouble(cursor.getString(2));
                        tail_core2 = cursor.getString(5);
                        item_core2 = cursor.getString(1);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        if (tail_core2.equals("-")) tail_core2 = "";
                        txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                        progressWMain2.setMax((int)(max_core2*10));
                        progressWMain2.setProgress((int)(core2*10));
                    } else {
                        layoutWeaponMain2.setVisibility(View.GONE);
                    }
                    maxoptionDBAdapter.open();
                    System.out.println(item_sub1);
                    cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (tail_core1.equals("-")) tail_core1 = "";
                    txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                    progressWMain1.setMax((int)(max_core1*10));
                    progressWMain1.setProgress((int)(core1*10));
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    progressWSub.setMax((int)(max_sub1*10));
                    progressWSub.setProgress((int)(sub1*10));
                } else if ((rdoDiff[3].isChecked() || rdoDiff[4].isChecked()) && percent(1, 100) <= 1) { //1
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름이 들어가는 텍스트뷰 글자 색상을 특급(주황색)색으로 변경한다.
                    special++; //특급 갯수를 1개 늘린다.
                    all++; //총 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 데이터값을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //내용을 숨긴다.
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE); //특급, 네임드 버튼을 보이게 한다.
                    //버튼 텍스트를 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼 배경을 주황색 계열로 바꾼다.
                    exoticDBAdpater.open();
                    long id = exoticDBAdpater.rowidDroped();
                    cursor = exoticDBAdpater.fetchIDData(id);
                    String ws = cursor.getString(11);
                    item_name = cursor.getString(1);
                    item_type = cursor.getString(2);
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    txtWTalent.setText(item_talent);
                    if (ws.equals("무기")) {
                        item_core1 = item_type+" 데미지";
                        item_sub1 = cursor.getString(4);
                        openWeapon = true;
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (tail_core2.equals("-")) tail_core2 = "";
                            txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                            progressWMain2.setMax((int)(max_core2*10));
                            progressWMain2.setProgress((int)(core2*10));
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        System.out.println(item_sub1);
                        cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                        progressWMain1.setMax((int)(max_core1*10));
                        progressWMain1.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        item_core1 = cursor.getString(3);
                        item_sub1 = cursor.getString(4);
                        item_sub2 = cursor.getString(5);
                        item_core1_type = cursor.getString(6);
                        item_sub1_type = cursor.getString(7);
                        item_sub2_type = cursor.getString(8);
                        changeImageType(item_core1_type, imgSMain, progressSMain);
                        changeImageType(item_sub1_type, imgSSub1, progressSSub1);
                        changeImageType(item_sub2_type, imgSSub2, progressSSub2);
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_core1);
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        core1 = max_core1; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchData(item_sub2);
                        max_sub2 = Double.parseDouble(cursor.getString(2));
                        tail_sub2 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                    }
                    exoticDBAdpater.close();
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub1.setImageResource(R.drawable.attack);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub1.setImageResource(R.drawable.sheld);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub1.setImageResource(R.drawable.power);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                //다이얼로그를 화면에 띄움
            }
        });

        btnIronHorseBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //칠흑의 시간 레이드에서 마지막 보스 처치 후 상자 개봉할 경우, 위와 내용은 비슷하나 박스에서는 5개의 아이템이 나온다. 이 부분만 설명함.
                setExp(0, 0, 0, 121141, 0);
                if (!rdoDiff[3].isChecked()) rdoDiff[3].toggle();
                String item_name, item_type, item_talent = "";
                String item_core1 = "", item_core2 = "", item_sub1 = "", item_sub2 = "", tail_core1 = "", tail_core2 = "", tail_sub1 = "", tail_sub2 = "";
                String item_core1_type, item_core2_type, item_sub1_type, item_sub2_type;
                darked = false;
                exotic = false;
                boolean weaponed = true;
                double core1 = 0, core2 = 0, sub1 = 0, sub2 = 0;
                double max_core1, max_core2, max_sub1, max_sub2;
                inventoryDBAdapter.open();
                txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
                inventoryDBAdapter.close();
                layoutInventory.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                Cursor cursor;
                int pick, temp_percent; //램덤 난수가 저장될 변수
                tableMain.setBackgroundResource(R.drawable.rareitem);
                String temp_option; //옵션 이름
                tableMain.setVisibility(View.VISIBLE); //옵션 내용은 보이게 한다.
                btnChange.setVisibility(View.GONE); //특급, 네임드일 경우 나타나는 버튼은 숨긴다.
                openSheld = false; //드랍된 장비가 보호장구일 경우 true가 된다.
                openWeapon = false; //드랍된 장비가 무기였을 경우 true가 된다.
                layoutSheld.setVisibility(View.GONE); //보호장구 옵션 레이아웃을 숨긴다.
                layoutWeapon.setVisibility(View.GONE); //무기 옵션 레이아웃을 숨긴다.
                txtName.setTextColor(Color.parseColor("#aaaaaa")); //장비이름의 색을 흰색으로 바꾼다. (완전 흰색이 아닌 조금 어두운 흰색)
                //for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                txtWTalent.setTextColor(Color.parseColor("#aaaaaa"));
                layoutSheldSub2.setVisibility(View.VISIBLE);
                btnInput.setVisibility(View.GONE);

                if (percent(1, 1000) <= 100) { //20+(bonus*4)
                    tableMain.setBackgroundResource(R.drawable.exoticitem);
                    exotic = true;
                    layoutTalent.setVisibility(View.VISIBLE);
                    txtName.setTextColor(Color.parseColor("#ff3c00")); //장비 이름의 색을 특급색(주황색)으로 바꾼다.
                    special++; //특급 장비 갯수를 1개 늘린다.
                    all++; //총 아이템 갯수를 1개 늘린다.
                    setInterface(); //UI에 변경된 내용을 업데이트한다.
                    txtSpecial.setText(Integer.toString(special)); //특급 갯수 텍스트뷰에 변경된 특급 갯수를 업데이트한다.
                    tableMain.setVisibility(View.GONE); //아이템 내용 레이아웃은 숨긴다.
                    btnChange.setVisibility(View.VISIBLE); //아이템 보기 버튼을 보이게 한다.
                    layoutTalentButton.setVisibility(View.GONE);
                    //버튼의 이름을 "특급"으로 바꾼다.
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.exoticdrop)); //버튼의 배경을 바꾼다. 주황색 계열로 바꾸게 된다.
                    item_name = "탐식자";
                    exoticDBAdpater.open();
                    cursor = exoticDBAdpater.fetchData(item_name);
                    item_type = cursor.getString(2);
                    item_sub1 = cursor.getString(4);
                    item_talent = cursor.getString(9);
                    txtWTalentContent.setText(cursor.getString(12));
                    exoticDBAdpater.close();
                    txtName.setText(item_name);
                    txtType.setText(item_type);
                    openWeapon = true;
                    item_core1 = item_type+" 데미지";
                    txtWTalent.setText(item_talent);
                    maxoptionDBAdapter.open();
                    cursor = maxoptionDBAdapter.fetchTypeData("무기");
                    max_core1 = Double.parseDouble(cursor.getString(2));
                    tail_core1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (!item_type.equals("권총")) {
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                        max_core2 = Double.parseDouble(cursor.getString(2));
                        tail_core2 = cursor.getString(5);
                        item_core2 = cursor.getString(1);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        layoutWeaponMain2.setVisibility(View.VISIBLE);
                        if (tail_core2.equals("-")) tail_core2 = "";
                        txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                        progressWMain2.setMax((int)(max_core2*10));
                        progressWMain2.setProgress((int)(core2*10));
                    } else {
                        layoutWeaponMain2.setVisibility(View.GONE);
                    }
                    maxoptionDBAdapter.open();
                    System.out.println(item_sub1);
                    cursor = maxoptionDBAdapter.fetchExoticWeaponData(item_sub1);
                    max_sub1 = Double.parseDouble(cursor.getString(2));
                    tail_sub1 = cursor.getString(5);
                    maxoptionDBAdapter.close();
                    pick = percent(1, 100);
                    if (pick <= 2+max) temp_percent = 100;
                    else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                    else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                    sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                    if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                    else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                    if (tail_core1.equals("-")) tail_core1 = "";
                    txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                    progressWMain1.setMax((int)(max_core1*10));
                    progressWMain1.setProgress((int)(core1*10));
                    if (tail_sub1.equals("-")) tail_sub1 = "";
                    txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                    progressWSub.setMax((int)(max_sub1*10));
                    progressWSub.setProgress((int)(sub1*10));
                } else if (percent(1, 1000) <= 20+(bonus*4)) { //Named Items 네임드 아이템 20+(bonus*4)
                    named++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    tableMain.setVisibility(View.GONE);
                    layoutTalentButton.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);

                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.namedlogo));
                    if (percent(1, 2) == 1) { //weapon
                        openWeapon = true;
                        layoutTalent.setVisibility(View.VISIBLE);
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("무기");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType());

                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        if (!item.getNoTalent()) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else {
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchTypeData("무기");
                        item_core1 = item.getType()+" 데미지";
                        max_core1 = Double.parseDouble(cursor.getString(2));
                        tail_core1 = cursor.getString(5);
                        OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                        item_sub1 = option_item.getContent();
                        max_sub1 = option_item.getValue();
                        tail_sub1 = option_item.getReter();
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (!item_type.equals("권총")) {
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                            item_core2 = cursor.getString(1);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                            max_core2 = Double.parseDouble(cursor.getString(2));
                            tail_core2 = cursor.getString(5);
                            item_core2 = cursor.getString(1);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            layoutWeaponMain2.setVisibility(View.VISIBLE);
                            if (item.getName().equals("하얀 사신")) {
                                txtWMain2.setTextColor(Color.parseColor("#c99700"));
                                txtWMain2.setText(item.getTalent());
                                progressWMain2.setMax(100);
                                progressWMain2.setProgress(100);
                                layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground);
                            } else {
                                txtWMain2.setTextColor(Color.parseColor("#aaaaaa"));
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            }
                        } else {
                            layoutWeaponMain2.setVisibility(View.GONE);
                        }
                        maxoptionDBAdapter.open();
                        cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                        max_sub1 = Double.parseDouble(cursor.getString(2));
                        tail_sub1 = cursor.getString(5);
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                        else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                        if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                        else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                        if (item.getName().equals("보조 붐스틱")) {
                            txtWMain1.setTextColor(Color.parseColor("#c99700"));
                            txtWMain1.setText(item.getTalent());
                            progressWMain1.setMax(100);
                            progressWMain1.setProgress(100);
                            layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtWMain1.setTextColor(Color.parseColor("#aaaaaa"));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                        }
                        txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        progressWSub.setMax((int)(max_sub1*10));
                        progressWSub.setProgress((int)(sub1*10));
                    } else {
                        openSheld = true;
                        namedDBAdapter.open();
                        NamedItem item = namedDBAdapter.fetchLiteData_Random("보호장구");
                        namedDBAdapter.close();
                        item_name = item.getName();
                        item_type = item.getType();
                        txtName.setText(item_name);
                        txtType.setText(item_type);

                        System.out.println("Name : "+item.getName()+"\nType : "+item.getType()+"\nBrand : "+item.getBrand());

                        if (sheldTalent(item_type)) {
                            txtWTalent.setTextColor(Color.parseColor("#c99700"));
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getTalent();
                            txtWTalent.setText(item_talent);
                            txtWTalentContent.setText(item.getTalentcontent());
                        } else layoutTalent.setVisibility(View.GONE);
                        sheldDBAdapter.open();
                        cursor = sheldDBAdapter.fetchData(item.getBrand());
                        String brandset = cursor.getString(3);
                        sheldDBAdapter.close();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        if (item.getNoTalent()) {
                            txtSSub1.setTextColor(Color.parseColor("#c99700"));
                            txtSSub1.setText(item.getTalent());
                            progressSSub1.setMax(100);
                            progressSSub1.setProgress(100);
                            if (item.getAsp().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (item.getAsp().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        } else {
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        }
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    }
                } else {
                    if (percent(1, 100) <= 7) {
                        openSheld = true;
                        tableMain.setBackgroundResource(R.drawable.gearitem);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutSheldSub2.setVisibility(View.GONE);
                        gear++;
                        all++;
                        setInterface();
                        txtAll.setText(Integer.toString(all));
                        txtGear.setText(Integer.toString(gear));
                        txtName.setTextColor(Color.parseColor("#009900"));
                        sheldDBAdapter.open();
                        SheldItem item = sheldDBAdapter.fetchRandomData("기어세트");
                        sheldDBAdapter.close();
                        item_name = item.getName();
                        pick = percent(0, sheld_type.length);
                        item_type = sheld_type[pick];
                        if (item_type.equals("백팩")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getBackpack();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else if (item_type.equals("조끼")) {
                            layoutTalent.setVisibility(View.VISIBLE);
                            item_talent = item.getVest();
                            txtWTalent.setText(item_talent);
                            talentDBAdapter.open();
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                        } else {
                            layoutTalent.setVisibility(View.GONE);
                        }
                        txtName.setText(item_name);
                        txtType.setText(item_type);
                        String brandset = item.getAsp();
                        maxoptionDBAdapter.open();
                        if (brandset.equals("공격")) {
                            cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                            item_core1 = "무기 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.attack);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (brandset.equals("방어")) {
                            cursor = maxoptionDBAdapter.fetchData("방어도");
                            item_core1 = "방어도";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.sheld);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                            item_core1 = "스킬 등급";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            imgSMain.setImageResource(R.drawable.power);
                            progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        maxoptionDBAdapter.close();
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                        else core1 = max_core1;
                        if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSMain.setMax((int)(max_core1*10));
                        progressSMain.setProgress((int)(core1*10));
                        if (tail_core1.equals("-")) tail_core1 = "";
                        txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                        txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                        maxoptionDBAdapter.open();
                        OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub1 = optionItem.getContent();
                        max_sub1 = optionItem.getValue();
                        tail_sub1 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub1.setImageResource(R.drawable.attack);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub1.setImageResource(R.drawable.sheld);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub1.setImageResource(R.drawable.power);
                            progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub1.setMax((int)(max_sub1*10));
                        progressSSub1.setProgress((int)(sub1*10));
                        if (tail_sub1.equals("-")) tail_sub1 = "";
                        txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                        maxoptionDBAdapter.open();
                        optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                        maxoptionDBAdapter.close();
                        item_sub2 = optionItem.getContent();
                        max_sub2 = optionItem.getValue();
                        tail_sub2 = optionItem.getReter();
                        if (optionItem.getOption().equals("공격")) {
                            imgSSub2.setImageResource(R.drawable.attack);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                        } else if (optionItem.getOption().equals("방어")) {
                            imgSSub2.setImageResource(R.drawable.sheld);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                        } else {
                            imgSSub2.setImageResource(R.drawable.power);
                            progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                        }
                        pick = percent(1, 100);
                        if (pick <= 2+max) temp_percent = 100;
                        else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                        else temp_percent = percent(1, 20) + option_bonus;
                        sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                        if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                        else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                        progressSSub2.setMax((int)(max_sub2*10));
                        progressSSub2.setProgress((int)(sub2*10));
                        if (tail_sub2.equals("-")) tail_sub2 = "";
                        txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                    } else {
                        brand++;
                        all++;
                        setInterface();
                        if (percent(1, 2) == 1) { //weapon
                            openWeapon = true;
                            layoutTalent.setVisibility(View.VISIBLE);
                            layoutWeapon.setVisibility(View.VISIBLE);
                            weaponDBAdpater.open();
                            WeaponItem item = weaponDBAdpater.fetchRandomData();
                            weaponDBAdpater.close();
                            item_name = item.getName();
                            item_type = item.getType();
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            talentDBAdapter.open();
                            item_talent = talentDBAdapter.fetchRandomData(item_type);
                            txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                            talentDBAdapter.close();
                            txtWTalent.setText(item_talent);
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchTypeData("무기");
                            item_core1 = item.getType()+" 데미지";
                            max_core1 = Double.parseDouble(cursor.getString(2));
                            tail_core1 = cursor.getString(5);
                            OptionItem option_item = maxoptionDBAdapter.fetchRandomData("무기 부속성");
                            item_sub1 = option_item.getContent();
                            max_sub1 = option_item.getValue();
                            tail_sub1 = option_item.getReter();
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(core1) >= max_core1) layoutWeaponMain1.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponMain1.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (!item_type.equals("권총")) {
                                maxoptionDBAdapter.open();
                                cursor = maxoptionDBAdapter.fetchTypeData(item.getType());
                                item_core2 = cursor.getString(1);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                cursor = maxoptionDBAdapter.fetchTypeData(item_type);
                                max_core2 = Double.parseDouble(cursor.getString(2));
                                tail_core2 = cursor.getString(5);
                                item_core2 = cursor.getString(1);
                                maxoptionDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 2+max) temp_percent = 100;
                                else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2 = Math.floor(((double)max_core2*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                                if ((int)Math.floor(core2) >= max_core2) layoutWeaponMain2.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                                else layoutWeaponMain2.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                                layoutWeaponMain2.setVisibility(View.VISIBLE);
                                if (tail_core2.equals("-")) tail_core2 = "";
                                txtWMain2.setText("+"+core2+tail_core2+" "+item_core2);
                                progressWMain2.setMax((int)(max_core2*10));
                                progressWMain2.setProgress((int)(core2*10));
                            } else {
                                layoutWeaponMain2.setVisibility(View.GONE);
                            }
                            maxoptionDBAdapter.open();
                            cursor = maxoptionDBAdapter.fetchSubData(item_sub1);
                            max_sub1 = Double.parseDouble(cursor.getString(2));
                            tail_sub1 = cursor.getString(5);
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + option_bonus; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if ((int)Math.floor(sub1) >= max_sub1) layoutWeaponSub.setBackgroundResource(R.drawable.maxbackground); //옵션 수치가 최대치보다 크거나 같을 경우 글자색을 주황색으로 변경한다.
                            else layoutWeaponSub.setBackgroundResource(R.drawable.notmaxbackground); //옵션 수치가 최대치보다 작을 경우 글자색을 기본색(흰색)으로 변경한다.
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtWMain1.setText("+"+core1+tail_core1+" "+item_type+" 데미지");
                            progressWMain1.setMax((int)(max_core1*10));
                            progressWMain1.setProgress((int)(core1*10));
                            txtWSub.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            progressWSub.setMax((int)(max_sub1*10));
                            progressWSub.setProgress((int)(sub1*10));
                        } else { //sheld
                            openSheld = true;
                            layoutSheld.setVisibility(View.VISIBLE);
                            sheldDBAdapter.open();
                            SheldItem item = sheldDBAdapter.fetchRandomData("브랜드");
                            sheldDBAdapter.close();
                            item_name = item.getName();
                            pick = percent(0, sheld_type.length);
                            item_type = sheld_type[pick];
                            txtName.setText(item_name);
                            txtType.setText(item_type);
                            if (sheldTalent(item_type)) {
                                layoutTalent.setVisibility(View.VISIBLE);
                                talentDBAdapter.open();
                                item_talent = talentDBAdapter.fetchRandomData(item_type);
                                txtWTalentContent.setText(talentDBAdapter.findContent(item_talent));
                                talentDBAdapter.close();
                                txtWTalent.setText(item_talent);
                            } else layoutTalent.setVisibility(View.GONE);
                            String brandset = item.getAsp();
                            maxoptionDBAdapter.open();
                            if (brandset.equals("공격")) {
                                cursor = maxoptionDBAdapter.fetchData("무기 데미지");
                                item_core1 = "무기 데미지";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.attack);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (brandset.equals("방어")) {
                                cursor = maxoptionDBAdapter.fetchData("방어도");
                                item_core1 = "방어도";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.sheld);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                cursor = maxoptionDBAdapter.fetchData("스킬 등급");
                                item_core1 = "스킬 등급";
                                max_core1 = Double.parseDouble(cursor.getString(2));
                                tail_core1 = cursor.getString(5);
                                imgSMain.setImageResource(R.drawable.power);
                                progressSMain.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            maxoptionDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            if (!brandset.equals("다용도")) core1 = Math.floor(((double)max_core1*((double)temp_percent/100))*10.0)/10.0;
                            else core1 = max_core1;
                            if ((int)Math.floor(core1) >= max_core1) layoutSheldMain.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldMain.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSMain.setMax((int)(max_core1*10));
                            progressSMain.setProgress((int)(core1*10));
                            if (tail_core1.equals("-")) tail_core1 = "";
                            txtSMain.setText("+"+core1+tail_core1+" "+item_core1);
                            txtSSub1.setTextColor(Color.parseColor("#aaaaaa"));
                            maxoptionDBAdapter.open();
                            OptionItem optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub1 = optionItem.getContent();
                            max_sub1 = optionItem.getValue();
                            tail_sub1 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub1.setImageResource(R.drawable.attack);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub1.setImageResource(R.drawable.sheld);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub1.setImageResource(R.drawable.power);
                                progressSSub1.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub1 = Math.floor(((double)max_sub1*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub1) >= max_sub1) layoutSheldSub1.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub1.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub1.setMax((int)(max_sub1*10));
                            progressSSub1.setProgress((int)(sub1*10));
                            if (tail_sub1.equals("-")) tail_sub1 = "";
                            txtSSub1.setText("+"+sub1+tail_sub1+" "+item_sub1);
                            maxoptionDBAdapter.open();
                            optionItem = maxoptionDBAdapter.fetchRandomData("보호장구 부속성");
                            maxoptionDBAdapter.close();
                            item_sub2 = optionItem.getContent();
                            max_sub2 = optionItem.getValue();
                            tail_sub2 = optionItem.getReter();
                            if (optionItem.getOption().equals("공격")) {
                                imgSSub2.setImageResource(R.drawable.attack);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));

                            } else if (optionItem.getOption().equals("방어")) {
                                imgSSub2.setImageResource(R.drawable.sheld);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));

                            } else {
                                imgSSub2.setImageResource(R.drawable.power);
                                progressSSub2.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));

                            }
                            pick = percent(1, 100);
                            if (pick <= 2+max) temp_percent = 100;
                            else if (pick <= 30) temp_percent = percent(21, 10) + option_bonus;
                            else temp_percent = percent(1, 20) + option_bonus;
                            sub2 = Math.floor(((double)max_sub2*((double)temp_percent/100))*10.0)/10.0;
                            if ((int)Math.floor(sub2) >= max_sub2) layoutSheldSub2.setBackgroundResource(R.drawable.maxbackground);
                            else layoutSheldSub2.setBackgroundResource(R.drawable.notmaxbackground);
                            progressSSub2.setMax((int)(max_sub2*10));
                            progressSSub2.setProgress((int)(sub2*10));
                            if (tail_sub2.equals("-")) tail_sub2 = "";
                            txtSSub2.setText("+"+sub2+tail_sub2+" "+item_sub2);
                            System.out.println("Main1 : "+core1+"\nSub1 : "+sub1+"\nSub2 : "+sub2);
                        }
                    }
                }

                if (dialogView.getParent() != null) //다이얼로그에 들어가는 뷰의 부모가 비어있지 않다면 작동
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView); //다이얼뷰의 부모의 그룹에서 다이얼뷰를 제거한다.
                //(!!!매우 중요!!!)위 작업을 하지 않는다면 다이얼로그를 띄우고 한번 더 띄울 때 에러가 생기게 된다. 그러므로 다시 동일한 뷰를 띄울 때는 제거하고 다시 생성해서 올리는 방식으로 사용해야 한다.
                builder.setView(dialogView); //빌더에 다이얼 뷰를 설정

                inputData(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item = new Item(String.valueOf(txtName.getText()), String.valueOf(txtType.getText()));
                item.setCore1(item_core1);
                item.setCore2(item_core2);
                item.setSub1(item_sub1);
                item.setSub2(item_sub2);
                item.setCore1_value(core1);
                item.setCore2_value(core2);
                item.setSub1_value(sub1);
                item.setSub2_value(sub2);
                item.setTalent(item_talent);

                setSemiInterface(String.valueOf(txtType.getText()), imgType);
                updateData();

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        return root;
    }



    public int percent(int min, int length) {
        return (int)(Math.random()*BIG)%length + min;
    } //min~length까지 임의의 숫자를 반환한다.

    public void setInterface() { //특급, 네임드, 기어, 브랜드 갯수가 전체에서 몇 %인지 진행도를 통해 보여주므로 진행도를 설정한다.
        progressBrand.setProgress((int)(((double)brand/(double)all)*10000));
        progressSpecial.setProgress((int)(((double)special/(double)all)*10000));
        progressNamed.setProgress((int)(((double)named/(double)all)*10000));
        progressGear.setProgress((int)(((double)gear/(double)all)*10000));
        txtAll.setText(Integer.toString(all));
    }

    private void updateData() {
        txtSpecial.setText(Integer.toString(special));
        txtNamed.setText(Integer.toString(named));
        txtGear.setText(Integer.toString(gear));
        txtBrand.setText(Integer.toString(brand));
        all = special + named + gear + brand;
        txtAll.setText(Integer.toString(all));
    }

    public void startInterface() {
        special = pref.getInt("Special", 0);
        named = pref.getInt("Named", 0);
        gear = pref.getInt("Gear", 0);
        brand = pref.getInt("Brand", 0);
        darkitem = pref.getInt("DarkItem", 0);
        all = pref.getInt("All", 0);
        index = pref.getInt("Index", 0);
        for (int i = 0; i < typet.length; i++) typet[i] = pref.getInt("typet"+(i+1), 0);
        for (int i = 0; i < item_name.length; i++) {
            item_name[i] = pref.getString("ItemName"+(i+1), "");
            item_type[i] = pref.getString("ItemType"+(i+1), "");
        }
        setInterface();
        txtSpecial.setText(Integer.toString(special));
        txtNamed.setText(Integer.toString(named));
        txtGear.setText(Integer.toString(gear));
        txtBrand.setText(Integer.toString(brand));
        for (int i = 0; i < txtTypelist.length; i++) txtTypelist[i].setText(Integer.toString(pref.getInt("typet"+(i+1), 0)));
        switch (pref.getInt("ProgessMax", 20)) {
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
                default:
                    for (int j = 0; j < progressType.length; j++) progressType[j].setMax(20);
        }
        for (int i = 0; i < progressType.length; i++) progressType[i].setProgress(typet[i]);
    }

    public void setEditor() {
        editor.putInt("Special", special);
        editor.putInt("Named", named);
        editor.putInt("Gear", gear);
        editor.putInt("Brand", brand);
        editor.putInt("Darkitem", darkitem);
        editor.putInt("All", all);
        for (int i = 0; i < typet.length; i++) editor.putInt("typet"+(i+1), typet[i]);
        for (int i = 0; i < item_name.length; i++) {
            editor.putString("ItemName"+(i+1), item_name[i]);
            editor.putString("ItemType"+(i+1), item_type[i]);
        }
        editor.putInt("Index", index);
        editor.putInt("ProgressMax", progressType[0].getMax());
        editor.putBoolean("Saved", true);
        editor.commit();
    }

    public void setSemiInterface(String type_name, ImageView view) { //무기 종류에 따라 갯수를 표시한다. 진행도 또한 설정한다.
        ImageView temp = view;
        switch (type_name) {
            case "돌격소총":
                typet[0]++;
                txtTypelist[0].setText(Integer.toString(typet[0]));
                temp.setImageResource(R.drawable.wp1custom);
                break;
            case "소총":
                typet[1]++;
                txtTypelist[1].setText(Integer.toString(typet[1]));
                temp.setImageResource(R.drawable.wp2custom);
                break;
            case "지정사수소총":
                typet[2]++;
                txtTypelist[2].setText(Integer.toString(typet[2]));
                temp.setImageResource(R.drawable.wp3custom);
                break;
            case "기관단총":
                typet[3]++;
                txtTypelist[3].setText(Integer.toString(typet[3]));
                temp.setImageResource(R.drawable.wp4custom);
                break;
            case "경기관총":
                typet[4]++;
                txtTypelist[4].setText(Integer.toString(typet[4]));
                temp.setImageResource(R.drawable.wp5custom);
                break;
            case "산탄총":
                typet[5]++;
                txtTypelist[5].setText(Integer.toString(typet[5]));
                temp.setImageResource(R.drawable.wp6custom);
                break;
            case "권총":
                typet[6]++;
                txtTypelist[6].setText(Integer.toString(typet[6]));
                temp.setImageResource(R.drawable.wp7custom);
                break;
            case "마스크":
                typet[7]++;
                txtTypelist[7].setText(Integer.toString(typet[7]));
                temp.setImageResource(R.drawable.sd1custom);
                break;
            case "백팩":
                typet[8]++;
                txtTypelist[8].setText(Integer.toString(typet[8]));
                temp.setImageResource(R.drawable.sd4custom);
                break;
            case "조끼":
                typet[9]++;
                txtTypelist[9].setText(Integer.toString(typet[9]));
                temp.setImageResource(R.drawable.sd2custom);
                break;
            case "장갑":
                typet[10]++;
                txtTypelist[10].setText(Integer.toString(typet[10]));
                temp.setImageResource(R.drawable.sd5custom);
                break;
            case "권총집":
                typet[11]++;
                txtTypelist[11].setText(Integer.toString(typet[11]));
                temp.setImageResource(R.drawable.sd3custom);
                break;
            case "무릎보호대":
                typet[12]++;
                txtTypelist[12].setText(Integer.toString(typet[12]));
                temp.setImageResource(R.drawable.sd6custom);
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
        setEditor();
    }

    private void addExp(int exp) {
        shdAdapter.open();
        shdAdapter.addEXP(exp);
        shdAdapter.levelUp();
        shdAdapter.close();
    }

    private void setExp(int normal, int hard, int very_hard, int hero, int legend) {
        switch (rgDifficulty.getCheckedRadioButtonId()) {
            case R.id.rdoDif1:
                addExp(normal); break;
            case R.id.rdoDif2:
                addExp(hard); break;
            case R.id.rdoDif3:
                addExp(very_hard); break;
            case R.id.rdoDif4:
                addExp(hero); break;
            case R.id.rdoDif5:
                addExp(legend); break;
        }
    }

    private void resetDialog() {
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
    }

    private void changeImageType(String type, ImageView view, ProgressBar progress) {
        if (type.equals("공격")) {
            view.setImageResource(R.drawable.attack);
            progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.attack_progress));
        } else if (type.equals("방어")) {
            view.setImageResource(R.drawable.sheld);
            progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.sheld_progress));
        } else {
            view.setImageResource(R.drawable.power);
            progress.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.power_progress));
        }
    }

    public void success_extract() {
        if (taked) {
            for (int i = 0; i < dark_items.size(); i++) {
                darkInputItems(dark_items.get(i));
            }
            dark_items.clear();
        }
    }

    public void failed_extract() {
        dark_items.clear();
    }

    private boolean sheldTalent(String type) {
        switch (type) {
            case "마스크": case "장갑": case "권총집": case "무릎보호대":
                return false;
            case "조끼": case "백팩":
                return true;
        }
        return true;
    }
}