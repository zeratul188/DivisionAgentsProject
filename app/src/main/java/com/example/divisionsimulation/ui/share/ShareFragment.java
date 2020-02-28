package com.example.divisionsimulation.ui.share;

import com.dinuscxj.progressbar.CircleProgressBar;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;

    public static AlertDialog.Builder builder_timer = null;
    public static AlertDialog alertDialog_timer = null;
    public static View dialogView_timer = null;

    Button btnInput = null;

    final private int BIG = 1234567;

    private Button btnLitezone, btnDarkzone, btnRaid, btnRaidbox, btnReset, btnOutput;
    private TextView txtSpecial, txtNamed, txtGear, txtBrand, txtAll;

    private CircleProgressBar progressSpecial, progressNamed, progressGear, progressBrand;

    private int special = 0, named = 0, gear = 0, brand = 0, darkitem = 0, all = 0, temp;

    private int[] typet = new int[13];

    private Handler handler;

    public static DarkZoneTimerThread coming_dz = null;
    public static DarkZoneTimerThread output_dz = null;

    private TextView[] txtTypelist = new TextView[13];
    private ProgressBar[] progressType = new ProgressBar[13];

    private AlertDialog dialog_dark = null;

    public static TextView txtInfo = null;
    public static ProgressBar progressTimer = null;
    public static Button btnNowOutput = null;
    public static TextView txtTimer = null;

    public synchronized void playOutputDZ() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                btnNowOutput.setEnabled(true);
                txtInfo.setText("이송 완료까지 남은 시간");
            }
        });
        output_dz.setInput_rogue(coming_dz.getInput_rogue());
        output_dz.start();
    }

    public void dialogOpen() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog_timer.dismiss();
                AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
                tbuilder.setMessage("이송 완료");
                tbuilder.setPositiveButton("확인", null);
                AlertDialog talertDialog = tbuilder.create();
                talertDialog.show();
            }
        });
    }

    public void deleteDZitem() {
        darkitem = 0;
        btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
        btnOutput.setText("이송하기 ("+darkitem+"/10)");
        alertDialog_timer.dismiss();
        Looper.prepare();
        //Toast.makeText(getActivity(), "로그 요원에게 이송물을 빼앗겼습니다.", Toast.LENGTH_SHORT).show();
        alertDialog_timer.dismiss();
        AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
        tbuilder.setMessage("로그 요원에게 이송물을 빼앗겼습니다.");
        tbuilder.setPositiveButton("확인", null);
        AlertDialog talertDialog = tbuilder.create();
        talertDialog.show();
        Looper.loop();
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

        handler = new Handler();

        btnLitezone = root.findViewById(R.id.btnLitezone);
        btnDarkzone = root.findViewById(R.id.btnDarkzone);
        btnRaid = root.findViewById(R.id.btnRaid);
        btnRaidbox = root.findViewById(R.id.btnRaidbox);
        btnReset = root.findViewById(R.id.btnReset);
        btnOutput = root.findViewById(R.id.btnOutput);

        txtSpecial = root.findViewById(R.id.txtSpecial);
        txtNamed = root.findViewById(R.id.txtNamed);
        txtGear = root.findViewById(R.id.txtGear);
        txtBrand = root.findViewById(R.id.txtBrand);

        txtAll = root.findViewById(R.id.txtAll);

        progressSpecial = root.findViewById(R.id.progressSpecial);
        progressNamed = root.findViewById(R.id.progressNamed);
        progressGear = root.findViewById(R.id.progressGear);
        progressBrand = root.findViewById(R.id.progressBrand);

        for (int i = 0; i < typet.length; i++) typet[i] = 0;

        int temp;
        for (int i = 0; i < txtTypelist.length; i++) {
            temp = root.getResources().getIdentifier("txtType"+(i+1), "id", getActivity().getPackageName());
            txtTypelist[i] = root.findViewById(temp);
            temp = root.getResources().getIdentifier("progressType"+(i+1), "id", getActivity().getPackageName());
            progressType[i] = root.findViewById(temp);
            progressType[i].setMax(20);
            progressType[i].setProgress(0);
        }

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

        progressSpecial.setMax(10000);
        progressNamed.setMax(10000);
        progressGear.setMax(10000);
        progressBrand.setMax(10000);

        final Itemlist il = new Itemlist();

        final View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null);

        final TextView txtName = dialogView.findViewById(R.id.txtName);
        final TextView txtType = dialogView.findViewById(R.id.txtType);
        final Button btnChange = dialogView.findViewById(R.id.btnChange);
        final TableLayout tableMain = dialogView.findViewById(R.id.tableMain);
        final ImageView[] imgOption = new ImageView[3];
        final TableRow trOption = dialogView.findViewById(R.id.trOption);


        for (int i = 0; i < imgOption.length; i++) {
            temp = dialogView.getResources().getIdentifier("imgOption"+(i+1), "id", getActivity().getPackageName());
            imgOption[i] = dialogView.findViewById(temp);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View dark_dialogView = getLayoutInflater().inflate(R.layout.itemlayout_dark, null);

        final TextView txtName2 = dark_dialogView.findViewById(R.id.txtName);
        final TextView txtType2 = dark_dialogView.findViewById(R.id.txtType);
        final Button btnChange2 = dark_dialogView.findViewById(R.id.btnChange);
        final TableLayout tableMain2 = dark_dialogView.findViewById(R.id.tableMain);
        btnInput = dark_dialogView.findViewById(R.id.btnInput);
        final ImageView[] imgOption2 = new ImageView[3];
        final TableRow trOption2 = dark_dialogView.findViewById(R.id.trOption);

        for (int i = 0; i < imgOption2.length; i++) {
            temp = dark_dialogView.getResources().getIdentifier("imgOption"+(i+1), "id", getActivity().getPackageName());
            imgOption2[i] =dark_dialogView.findViewById(temp);
        }

        final AlertDialog.Builder builder_dark = new AlertDialog.Builder((getActivity()));

        /*
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        */

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                special = 0;
                named = 0;
                gear = 0;
                brand = 0;
                txtSpecial.setText("0");
                txtNamed.setText("0");
                txtGear.setText("0");
                txtBrand.setText("0");
                darkitem = 0;
                all = 0;
                txtAll.setText("0");
                progressBrand.setProgress(0);
                progressGear.setProgress(0);
                progressNamed.setProgress(0);
                progressSpecial.setProgress(0);
                for (int i = 0; i < txtTypelist.length; i++) {
                    txtTypelist[i].setText("0");
                    progressType[i].setProgress(0);
                    typet[i] = 0;
                }
                for (int i = 0; i < progressType.length; i++) progressType[i].setMax(20);
                btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
                btnOutput.setText("이송하기 ("+darkitem+"/10)");
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
            }
        });

        btnChange2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableMain2.setVisibility(View.VISIBLE);
                btnChange2.setVisibility(View.GONE);
            }
        });

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (darkitem < 10) {
                    darkitem++;
                    btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
                    btnOutput.setText("이송하기 ("+darkitem+"/10)");
                    dialog_dark.dismiss();
                } else {
                    AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
                    tbuilder.setMessage("다크존 가방이 가득찼습니다.");
                    tbuilder.setPositiveButton("확인", null);
                    AlertDialog talertDialog = tbuilder.create();
                    talertDialog.show();
                }
            }
        });

        btnOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (darkitem != 0) {
                    dialogView_timer = getLayoutInflater().inflate(R.layout.timercominglayout, null);

                    txtInfo = dialogView_timer.findViewById(R.id.txtInfo);
                    progressTimer = dialogView_timer.findViewById(R.id.progressTimer);
                    btnNowOutput = dialogView_timer.findViewById(R.id.btnNowOutput);
                    txtTimer = dialogView_timer.findViewById(R.id.txtTimer);

                    btnNowOutput.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            darkitem = 0;
                            btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
                            btnOutput.setText("이송하기 ("+darkitem+"/10)");
                            Toast.makeText(getActivity(), "헬기에 이송물을 걸었습니다.", Toast.LENGTH_SHORT).show();
                            btnNowOutput.setEnabled(false);
                        }
                    });

                    coming_dz = new DarkZoneTimerThread(handler, getActivity(), ShareFragment.this);
                    output_dz = new DarkZoneTimerThread(handler, getActivity(), ShareFragment.this);

                    progressTimer.setMax(10000);
                    progressTimer.setProgress(0);

                    builder_timer = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                    builder_timer.setView(dialogView_timer);
                    builder_timer.setPositiveButton("바로 이송", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            darkitem = 0;
                            btnInput.setText("다크존 가방에 담기 ("+darkitem+"/10)");
                            btnOutput.setText("이송하기 ("+darkitem+"/10)");
                            Toast.makeText(getActivity(), "즉시 이송시켰습니다.", Toast.LENGTH_SHORT).show();
                            coming_dz.stopThread();
                            output_dz.stopThread();
                            output_dz.setRogue(true);
                            coming_dz.setRogue(true);
                        }
                    });
                    builder_timer.setNeutralButton("이송지점 벗어나기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (darkitem > 0) {
                                Toast.makeText(getActivity(), "이송하지 않고 이송지점에서 벗어났습니다.", Toast.LENGTH_SHORT).show();
                                coming_dz.stopThread();
                                output_dz.stopThread();
                                output_dz.setRogue(true);
                                coming_dz.setRogue(true);
                            } else {
                                if (percent(1, 4) == 1) Toast.makeText(getActivity(), "이송물은 버려둔 채로 이송지점에서 벗어났지만 이송에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                else Toast.makeText(getActivity(), "이송물은 버려둔 채로 이송지점에서 벗어났으나 로그 요원에게 이송물을 탈취당했습니다.", Toast.LENGTH_SHORT).show();
                                coming_dz.stopThread();
                                output_dz.stopThread();
                                output_dz.setRogue(true);
                                coming_dz.setRogue(true);
                            }
                        }
                    });
                    builder_timer.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            coming_dz.stopThread();
                            output_dz.stopThread();
                            output_dz.setRogue(true);
                            coming_dz.setRogue(true);
                        }
                    });
                    alertDialog_timer = builder_timer.create();
                    alertDialog_timer.setCancelable(false);
                    alertDialog_timer.show();

                    coming_dz.setMinute(0);
                    coming_dz.setSecond(40);

                    output_dz.setMinute(1);
                    output_dz.setSecond(0);

                    coming_dz.setRoguePercent(1);
                    output_dz.setRoguePercent(2);

                    coming_dz.setOutputing(false);
                    output_dz.setOutputing(true);

                    coming_dz.start();

                    /*if (percent(1, 100) > 20) {
                        AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
                        tbuilder.setMessage("이송 완료");
                        tbuilder.setPositiveButton("확인", null);
                        AlertDialog talertDialog = tbuilder.create();
                        talertDialog.show();
                    } else {
                        AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
                        tbuilder.setMessage("이송 탈취당하셨습니다.\n아이템이 사라집니다.");
                        tbuilder.setPositiveButton("확인", null);
                        AlertDialog talertDialog = tbuilder.create();
                        talertDialog.show();
                    }*/
                } else {
                    AlertDialog.Builder tbuilder = new AlertDialog.Builder(getActivity());
                    tbuilder.setMessage("이송할 아이템이 없습니다.");
                    tbuilder.setPositiveButton("확인", null);
                    AlertDialog talertDialog = tbuilder.create();
                    talertDialog.show();
                }
            }
        });

        btnLitezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pick;
                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                trOption.setVisibility(View.GONE);
                txtName.setTextColor(Color.parseColor("#000000"));
                for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 10) { //특급 장비
                    txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    all++;
                    setInterface();
                    txtSpecial.setText(Integer.toString(special));
                    tableMain.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                    btnChange.setText("특급");
                    btnChange.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttoncustomspecial));
                    for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                    pick = percent(0, il.getSpecialweapon_Length());
                    txtName.setText(il.getSpecialweapon(pick));
                    txtType.setText(il.getSpecialweapon_type(pick));
                } else if (percent(1, 1000) <= 30) { //네임드 장비
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
                    } else { //sheld
                        trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        switch (il.getNamedsheld_lite_type(pick)) {
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
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));
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

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        trOption.setVisibility(View.VISIBLE);
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
                        }
                        pick = percent(1, 100);
                        if (pick <= 10) { //gear
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
                            switch (il.getSheldbrand(pick)) {
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
                            }
                            txtName.setText(il.getSheldbrand(pick));
                        }
                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                builder.setPositiveButton("확인", null);

                setSemiInterface(String.valueOf(txtType.getText()));

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnDarkzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pick;
                tableMain2.setVisibility(View.VISIBLE);
                btnChange2.setVisibility(View.GONE);
                txtName2.setTextColor(Color.parseColor("#000000"));
                trOption2.setVisibility(View.GONE);
                for (int i = 0; i < 3; i++) imgOption2[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 15) { //특급 장비
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
                } else if (percent(1, 1000) <= 30) { //네임드 장비
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
                    } else { //sheld
                        trOption2.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption2.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption2[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption2[i].setImageResource(R.drawable.sheld);
                            else imgOption2[i].setImageResource(R.drawable.power);
                        }
                        pick = percent(0, il.getNamedsheld_dark_Length());
                        switch (il.getNamedsheld_dark_type(pick)) {
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
                        }
                        txtName2.setText(il.getNamedsheld_dark(pick));
                        txtType2.setText(il.getNamedsheld_dark_type(pick));
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

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType2.setText(il.getSheldtype(pick));
                        int option;
                        switch (il.getSheldtype(pick)) {
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
                        }
                        pick = percent(1, 100);
                        trOption2.setVisibility(View.VISIBLE);
                        int ransu;
                        for (int i = 0; i < imgOption2.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption2[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption2[i].setImageResource(R.drawable.sheld);
                            else imgOption2[i].setImageResource(R.drawable.power);
                        }
                        if (pick <= 10) { //gear
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
                            switch (il.getSheldbrand(pick)) {
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
                            }
                            txtName2.setText(il.getSheldbrand(pick));
                        }
                    }
                }

                if (dark_dialogView.getParent() != null)
                    ((ViewGroup) dark_dialogView.getParent()).removeView(dark_dialogView);
                builder_dark.setView(dark_dialogView);

                builder_dark.setPositiveButton("확인", null);

                setSemiInterface(String.valueOf(txtType2.getText()));

                dialog_dark = builder_dark.create();
                dialog_dark.show();
            }
        });

        btnRaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pick;
                txtName.setTextColor(Color.parseColor("#000000"));
                tableMain.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                trOption.setVisibility(View.GONE);
                for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.VISIBLE);
                if (percent(1, 1000) <= 15) { //특급 장비
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
                } else if (percent(1, 1000) <= 40) { //네임드 장비
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
                    } else { //sheld
                        trOption.setVisibility(View.VISIBLE);
                        int ransu, option;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        switch (il.getNamedsheld_lite_type(pick)) {
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
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));
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

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        int option;
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
                        pick = percent(1, 100);
                        trOption.setVisibility(View.VISIBLE);
                        int ransu;
                        for (int i = 0; i < imgOption.length; i++) {
                            ransu = percent(1, 3);
                            if (ransu == 1) imgOption[i].setImageResource(R.drawable.attack);
                            else if (ransu == 2) imgOption[i].setImageResource(R.drawable.sheld);
                            else imgOption[i].setImageResource(R.drawable.power);
                        }
                        if (pick <= 10) { //gear
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
                            switch (il.getSheldbrand(pick)) {
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
                            }
                            txtName.setText(il.getSheldbrand(pick));
                        }
                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                builder.setPositiveButton("확인", null);

                setSemiInterface(String.valueOf(txtType.getText()));

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnRaidbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pick;
                int start, end;
                txtName.setTextColor(Color.parseColor("#000000"));
                String name = "", type = "";
                for (int i = 0; i < 3; i++) imgOption[i].setVisibility(View.GONE);
                if (percent(1, 100) <= 10) {
                    //txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    all++;
                    setInterface();
                    txtAll.setText(Integer.toString(all));
                    txtSpecial.setText(Integer.toString(special));
                    name += "독수리를 거느린 자\n";
                    type += "돌격소총\n";
                    setSemiInterface("돌격소총");
                }
                for (int i = 0; i < 5; i++) {
                    if (percent(1, 1000) <= 10) { //특급 장비
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
                        setSemiInterface(il.getSpecialweapon_type(pick));
                        //txtName.setText(il.getSpecialweapon(pick));
                        //txtType.setText(il.getSpecialweapon_type(pick));
                    } else if (percent(1, 1000) <= 30) { //네임드 장비
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
                                    //txtName.setText(il.getWeaponlist7(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                default:
                                    txtName.setText("Error");
                                    txtType.setText("Error");
                            }
                            setSemiInterface(il.getWeapontype(pick));
                        } else { //sheld
                            pick = percent(0, il.getSheldtype_Length());
                            if (i != 4) type += il.getSheldtype(pick)+"\n";
                            else type += il.getSheldtype(pick);
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

                builder.setPositiveButton("확인", null);

                AlertDialog alertDialog = builder.create();
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