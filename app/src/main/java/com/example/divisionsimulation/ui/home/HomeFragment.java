package com.example.divisionsimulation.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.divisionsimulation.R;
import com.google.android.material.chip.ChipGroup;

import java.io.Serializable;

public class HomeFragment extends Fragment implements Serializable {

    private AlertDialog.Builder builder = null;
    private AlertDialog alertDialog = null;
    private View dialogView = null;
    /*
    다이얼로그를 띄우기 위한 객체들이다.
     */

    private HomeViewModel homeViewModel;

    private Button btnDemageSimul, btnDPS; //데미지 시뮬, 초기화 버튼이다.

    private CluchThread ct = null; //클러치 스레드이다.

    private RadioGroup rgCrazy, rgPush; //광분, 중압감 라디오 그룹이다.
    private RadioButton[] rdoCrazy = new RadioButton[6]; //광분 라디오 버튼 배열이다.
    private RadioButton[] rdoPush = new RadioButton[11]; //중압감 라디오 버튼 배열이다.
    private CheckBox[] chkCamelOption = new CheckBox[3]; //카멜레온 옵션 체크 박스 배열이다.
    private CheckBox chkSeeker, chkCrazy, chkBoom, chkPush, chkEagle, chkQuickhand, chkBumerang, chkCamel, chkFire, chkFront; //기타 체크 박스이다. (집념, 무자비 등)

    private LinearLayout layoutCamel; //카멜레온 여부 체크 시 기타 버프 옵션이 나타나기 위한 레이아웃이다.

    private boolean boom = false, quick_hand = false, bumerang_true = false, fire = false; //무자비 폭발탄, 빠른 손, 부메랑, 불꽃 여부

    private int crazy_dmg, seeker_dmg, push_dmg, eagle_dmg, front_dmg; //광분 데미지, 감시병 데미지, 중압감 치명타 데미지, 집념 데미지, 완벽한 근접전의 대가 데미지를 스레드로 보내기 위해 임시 저장하는 변수이다.

    private AlertDialog.Builder builder_error = null;
    private AlertDialog alertDialog_error = null;
    private View dialogView_error = null;
    /*
    위와 동일하게 다이얼 로그를 띄우기 위한 객체이다.
     */

    private int reset_count = 0; //초기화 카운터이다.
    private boolean btnEnd = false; //초기화 카운터가 1500(3초)가 되면 초기화되도록 해주는 변수이다.

    private CircleProgressBar progressReset = null; //초기화 진행 중 나타나는 원형 프로그레스 바이다.

    private EditText edtWeaponDemage, edtRPM, edtCritical, edtCriticalDemage, edtHeadshot, edtHeadshotDemage, edtEliteDemage, edtSheldDemage, edtHealthDemage, edtReload, edtAmmo, edtNickname, edtAiming; //레이아웃에서 입력받은 값들을 저장시키는 변수이다.

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //메시지를 받아 UI 관련 작업을 실행한다.
            if (reset_count >= 1500) btnEnd = true; //초기화 카운터가 1500(3초)보다 크거나 같아지면 3초가 지난 것이므로 초기화를 진행하도록 참으로 바꿔준다.
            if (btnEnd) { //btnEnd가 참이 될 경우 초기화를 진행한다.
                alertDialog.dismiss(); //3초가 지나 초기화를 진행하므로 다이얼로그는 닫아준다.

                edtNickname.setText("");
                edtWeaponDemage.setText("");
                edtRPM.setText("");
                edtCritical.setText("");
                edtCriticalDemage.setText("");
                edtHeadshot.setText("");
                edtHeadshotDemage.setText("");
                edtEliteDemage.setText("");
                edtSheldDemage.setText("");
                edtHealthDemage.setText("");
                edtReload.setText("");
                edtAmmo.setText("");
                edtAiming.setText("");
                /*
                입력된 값들을 비워준다.
                 */

                if (chkEagle.isChecked()) chkEagle.toggle();
                if (chkBoom.isChecked()) chkBoom.toggle();
                if (chkSeeker.isChecked()) chkSeeker.toggle();
                if (chkBumerang.isChecked()) chkBumerang.toggle();
                if (chkCrazy.isChecked()) chkCrazy.toggle();
                if (chkPush.isChecked()) chkPush.toggle();
                if (chkQuickhand.isChecked()) chkQuickhand.toggle();
                if (chkCamel.isChecked()) chkCamel.toggle();
                if (chkFire.isChecked()) chkFire.toggle();
                /*
                체크된 것들을 모두 체크 해제된다.
                체크 해제 되면서 체크로 인해 비활성화, 펼쳐진 레이아웃은 자동으로 해제된다.
                 */

                chkPush.setTextColor(Color.parseColor("#aaaaaa"));
                chkPush.setEnabled(true);
                chkEagle.setTextColor(Color.parseColor("#aaaaaa"));
                chkEagle.setEnabled(true);
                chkBumerang.setTextColor(Color.parseColor("#aaaaaa"));
                chkBumerang.setEnabled(true);
                chkBoom.setTextColor(Color.parseColor("#aaaaaa"));
                chkBoom.setEnabled(true);
                chkCrazy.setTextColor(Color.parseColor("#aaaaaa"));
                chkCrazy.setEnabled(true);
                chkQuickhand.setTextColor(Color.parseColor("#aaaaaa"));
                chkQuickhand.setEnabled(true);
                chkCamel.setTextColor(Color.parseColor("#aaaaaa"));
                chkCamel.setEnabled(true);
                /*
                비활성화로 인한 글자 색상이 변한 것들을 원래대로 되돌려놓는다.
                 */

                rgPush.clearCheck();
                rgCrazy.clearCheck();
                /*
                광분, 중압감 라디오 그룹에 있는 것들도 초기화 시켜준다.
                 */

                btnEnd = false; //초기화 되었으므로 다시 거짓으로 바꿔 다시 초기화 진행 과정을 밟을 수 있도록 한다.
                Toast.makeText(getActivity(), "입력 값들이 모두 초기화 되었습니다.", Toast.LENGTH_SHORT).show(); //토스트로 초기화 되었음을 알려준다.
                mHandler.removeMessages(0); //반복되는 핸들러 진행 과정을 제거하여 멈추게 한다.
            } else {
                reset_count += 10; //3초가 지나지 않았으므로 10씩 증가한다. 0.02초마다 반복되므로 3초까지는 1500이기 때문에 10씩 올린다.
                progressReset.setProgress(reset_count); //원형 프로그레스바도 초기화 카운터로 잡아 진행도도 설정한다.
            }
            Log.v("LC버튼", "Long클릭" + ct); //임시 로그 메시지 남김
            mHandler.sendEmptyMessageDelayed(0, 20); //0.02초마다 딜레이를 두어 핸들러를 반복시켜 준다.
        }
    };

    /*public void mOnClick (View v){
        reset_count = 0;
        progressReset.setProgress(0);
        alertDialog.dismiss();
        reset_count = 0;
        Log.v("OC버튼", "On클릭:"+ ct);
        mHandler.removeMessages(0); //롱클릭리스너에서 동작이 넘어오면 remove시켜준다.
    };*/


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        edtWeaponDemage = root.findViewById(R.id.edtWeaponDemage);
        edtRPM = root.findViewById(R.id.edtRPM);
        edtCritical = root.findViewById(R.id.edtCritical);
        edtCriticalDemage = root.findViewById(R.id.edtCriticalDemage);
        edtHeadshot = root.findViewById(R.id.edtHeadshot);
        edtHeadshotDemage = root.findViewById(R.id.edtHeadshotDemage);
        edtEliteDemage = root.findViewById(R.id.edtEliteDemage);
        edtSheldDemage = root.findViewById(R.id.edtSheldDemage);
        edtHealthDemage = root.findViewById(R.id.edtHealthDemage);
        edtReload = root.findViewById(R.id.edtReload);
        edtAmmo = root.findViewById(R.id.edtAmmo);
        edtNickname = root.findViewById(R.id.edtNickname);
        edtAiming = root.findViewById(R.id.edtAiming);

        btnDPS = root.findViewById(R.id.btnDPS);
        btnDemageSimul = root.findViewById(R.id.btnDemageSimul);

        chkBoom = root.findViewById(R.id.chkBoom);
        chkQuickhand = root.findViewById(R.id.chkQuickhand);
        chkCrazy = root.findViewById(R.id.chkCrazy);
        chkBumerang = root.findViewById(R.id.chkBumerang);
        chkFire = root.findViewById(R.id.chkFire);
        chkCamel = root.findViewById(R.id.chkCamel);
        chkFront = root.findViewById(R.id.chkFront);

        rgCrazy = root.findViewById(R.id.rgCrazy);
        /*
        레이아웃에 있는 것들을 아이디를 받아온다.
         */
        int temp;
        for (int i = 0; i < rdoCrazy.length; i++) {
            temp = root.getResources().getIdentifier("rdoCrazy"+(i+1), "id", getActivity().getPackageName());
            rdoCrazy[i] = (RadioButton) root.findViewById(temp);
        }
        /*
        rdoCrazy1, rdoCrazy2 등 rdoCrazy는 고정이고 뒤에 숫자만 반복된다.
        그러므로 뒤에 숫자만 반복되면 되므로 rdoCrazy 변수는 배열로 받고
        위처럼 getResouces()를 이용하여 반복되는 것들을 반복문으로 가져온다.
         */

        chkPush = root.findViewById(R.id.chkPush);
        rgPush = root.findViewById(R.id.rgPush);
        layoutCamel = root.findViewById(R.id.layoutCamel);
        for (int i = 0; i < rdoPush.length; i++) {
            temp = root.getResources().getIdentifier("rdoPush"+(i+1), "id", getActivity().getPackageName());
            rdoPush[i] = (RadioButton) root.findViewById(temp);
        }
        for (int i = 0; i < chkCamelOption.length; i++) {
            temp = root.getResources().getIdentifier("chkCamelOption"+(i+1), "id", getActivity().getPackageName());
            chkCamelOption[i] = (CheckBox) root.findViewById(temp);
        }

        chkSeeker = root.findViewById(R.id.chkSeeker);
        chkEagle = root.findViewById(R.id.chkEagle);
        /*
        위와 동일한 방식
         */

        chkCrazy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //광분 체크 박스를 체크하거나 체크 해제할 때마다 작동한다.
                if (isChecked) rgCrazy.setVisibility(View.VISIBLE); //체크되었다면 광분 라디오 그룹을 보이도록 한다.
                else { //체크 해제되었다면 작동한다.
                    rgCrazy.clearCheck(); //광분 라디오 그룹이 다시 화면에 사라지면서 초기화를 해줘야 하기 때문에 전부 체크를 풀어준다.
                    rgCrazy.setVisibility(View.GONE); //광분 라디오 그룹이 화면에 안 보이도록 설정한다.
                }
            }
        });
        chkPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //위와 동일한 방식
                if (isChecked) {
                    rgPush.setVisibility(View.VISIBLE); //중압감 라디오 그룹을 보여준다.
                    chkQuickhand.setTextColor(Color.parseColor("#666666"));
                    chkQuickhand.setEnabled(false);
                    chkEagle.setTextColor(Color.parseColor("#666666"));
                    chkEagle.setEnabled(false);
                    chkBumerang.setTextColor(Color.parseColor("#666666"));
                    chkBumerang.setEnabled(false);
                    chkCamel.setTextColor(Color.parseColor("#666666"));
                    chkCamel.setEnabled(false);
                    chkFront.setTextColor(Color.parseColor("#666666"));
                    chkFront.setEnabled(false);
                    /*
                    중압감으로 인해 비활성화 되는 체크박스는 비활성화하고 색도 회색으로 변하게 함
                     */
                }
                else {
                    rgPush.clearCheck();
                    rgPush.setVisibility(View.GONE);
                    chkQuickhand.setTextColor(Color.parseColor("#aaaaaa"));
                    chkQuickhand.setEnabled(true);
                    chkBumerang.setTextColor(Color.parseColor("#aaaaaa"));
                    chkBumerang.setEnabled(true);
                    chkFront.setTextColor(Color.parseColor("#aaaaaa"));
                    chkFront.setEnabled(true);
                    if (!chkBoom.isChecked() && !chkQuickhand.isChecked() && !chkBumerang.isChecked() && !chkCamel.isChecked() && !chkEagle.isChecked() && !chkFront.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#aaaaaa"));
                        chkEagle.setEnabled(true);
                        chkCamel.setTextColor(Color.parseColor("#aaaaaa"));
                        chkCamel.setEnabled(true);
                    }
                    /*
                    중압감 체크 해제로 인해 비활성화, 색이 변경된 것들을 다시 원래대로 되돌려놓는다.
                     */
                }
            }
        });
        chkBumerang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkQuickhand.setTextColor(Color.parseColor("#666666"));
                    chkQuickhand.setEnabled(false);
                    chkPush.setTextColor(Color.parseColor("#666666"));
                    chkPush.setEnabled(false);
                    chkEagle.setTextColor(Color.parseColor("#666666"));
                    chkEagle.setEnabled(false);
                    chkCamel.setTextColor(Color.parseColor("#666666"));
                    chkCamel.setEnabled(false);
                    chkFront.setTextColor(Color.parseColor("#666666"));
                    chkFront.setEnabled(false);
                } else {
                    chkPush.setTextColor(Color.parseColor("#aaaaaa"));
                    chkPush.setEnabled(true);
                    chkQuickhand.setTextColor(Color.parseColor("#aaaaaa"));
                    chkQuickhand.setEnabled(true);
                    chkFront.setTextColor(Color.parseColor("#aaaaaa"));
                    chkFront.setEnabled(true);
                    if (!chkBoom.isChecked() && !chkPush.isChecked() && !chkQuickhand.isChecked() && !chkCamel.isChecked() && !chkEagle.isChecked() && !chkFront.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#aaaaaa"));
                        chkEagle.setEnabled(true);
                        chkCamel.setTextColor(Color.parseColor("#aaaaaa"));
                        chkCamel.setEnabled(true);
                    }
                }
            }
        });
        chkFront.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkQuickhand.setTextColor(Color.parseColor("#666666"));
                    chkQuickhand.setEnabled(false);
                    chkPush.setTextColor(Color.parseColor("#666666"));
                    chkPush.setEnabled(false);
                    chkEagle.setTextColor(Color.parseColor("#666666"));
                    chkEagle.setEnabled(false);
                    chkCamel.setTextColor(Color.parseColor("#666666"));
                    chkCamel.setEnabled(false);
                    chkBumerang.setTextColor(Color.parseColor("#666666"));
                    chkBumerang.setEnabled(false);
                } else {
                    chkPush.setTextColor(Color.parseColor("#aaaaaa"));
                    chkPush.setEnabled(true);
                    chkQuickhand.setTextColor(Color.parseColor("#aaaaaa"));
                    chkQuickhand.setEnabled(true);
                    chkBumerang.setTextColor(Color.parseColor("#aaaaaa"));
                    chkBumerang.setEnabled(true);
                    if (!chkBoom.isChecked() && !chkPush.isChecked() && !chkQuickhand.isChecked() && !chkCamel.isChecked() && !chkEagle.isChecked() && !chkBumerang.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#aaaaaa"));
                        chkEagle.setEnabled(true);
                        chkCamel.setTextColor(Color.parseColor("#aaaaaa"));
                        chkCamel.setEnabled(true);
                    }
                }
            }
        });
        chkEagle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkBoom.setTextColor(Color.parseColor("#666666"));
                    chkBoom.setEnabled(false);
                    chkQuickhand.setTextColor(Color.parseColor("#666666"));
                    chkQuickhand.setEnabled(false);
                    chkPush.setTextColor(Color.parseColor("#666666"));
                    chkPush.setEnabled(false);
                    chkBumerang.setTextColor(Color.parseColor("#666666"));
                    chkBumerang.setEnabled(false);
                    chkCamel.setTextColor(Color.parseColor("#666666"));
                    chkCamel.setEnabled(false);
                    chkFront.setTextColor(Color.parseColor("#666666"));
                    chkFront.setEnabled(false);
                } else {
                    chkBoom.setTextColor(Color.parseColor("#aaaaaa"));
                    chkBoom.setEnabled(true);
                    chkQuickhand.setTextColor(Color.parseColor("#aaaaaa"));
                    chkQuickhand.setEnabled(true);
                    chkPush.setTextColor(Color.parseColor("#aaaaaa"));
                    chkPush.setEnabled(true);
                    chkBumerang.setTextColor(Color.parseColor("#aaaaaa"));
                    chkBumerang.setEnabled(true);
                    chkCamel.setTextColor(Color.parseColor("#aaaaaa"));
                    chkCamel.setEnabled(true);
                    chkFront.setTextColor(Color.parseColor("#aaaaaa"));
                    chkFront.setEnabled(true);
                }
            }
        });
        chkCamel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkBoom.setTextColor(Color.parseColor("#666666"));
                    chkBoom.setEnabled(false);
                    chkQuickhand.setTextColor(Color.parseColor("#666666"));
                    chkQuickhand.setEnabled(false);
                    chkPush.setTextColor(Color.parseColor("#666666"));
                    chkPush.setEnabled(false);
                    chkBumerang.setTextColor(Color.parseColor("#666666"));
                    chkBumerang.setEnabled(false);
                    chkEagle.setTextColor(Color.parseColor("#666666"));
                    chkEagle.setEnabled(false);
                    chkFront.setTextColor(Color.parseColor("#666666"));
                    chkFront.setEnabled(false);
                    layoutCamel.setVisibility(View.VISIBLE);
                } else {
                    chkBoom.setTextColor(Color.parseColor("#aaaaaa"));
                    chkBoom.setEnabled(true);
                    chkQuickhand.setTextColor(Color.parseColor("#aaaaaa"));
                    chkQuickhand.setEnabled(true);
                    chkPush.setTextColor(Color.parseColor("#aaaaaa"));
                    chkPush.setEnabled(true);
                    chkBumerang.setTextColor(Color.parseColor("#aaaaaa"));
                    chkBumerang.setEnabled(true);
                    chkEagle.setTextColor(Color.parseColor("#aaaaaa"));
                    chkEagle.setEnabled(true);
                    chkFront.setTextColor(Color.parseColor("#aaaaaa"));
                    chkFront.setEnabled(true);
                    for (int i = 0; i < chkCamelOption.length; i++) if (chkCamelOption[i].isChecked()) chkCamelOption[i].toggle();
                    layoutCamel.setVisibility(View.GONE);
                }
            }
        });
        chkBoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkEagle.setTextColor(Color.parseColor("#666666"));
                    chkEagle.setEnabled(false);
                    chkCamel.setTextColor(Color.parseColor("#666666"));
                    chkCamel.setEnabled(false);
                } else {
                    if (!chkPush.isChecked() && !chkQuickhand.isChecked() && !chkBumerang.isChecked() && !chkEagle.isChecked() && !chkCamel.isChecked() && !chkFront.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#aaaaaa"));
                        chkEagle.setEnabled(true);
                        chkCamel.setTextColor(Color.parseColor("#aaaaaa"));
                        chkCamel.setEnabled(true);
                    }
                }
            }
        });
        chkQuickhand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkPush.setTextColor(Color.parseColor("#666666"));
                    chkPush.setEnabled(false);
                    chkEagle.setTextColor(Color.parseColor("#666666"));
                    chkEagle.setEnabled(false);
                    chkBumerang.setTextColor(Color.parseColor("#666666"));
                    chkBumerang.setEnabled(false);
                    chkCamel.setTextColor(Color.parseColor("#666666"));
                    chkCamel.setEnabled(false);
                    chkFront.setTextColor(Color.parseColor("#666666"));
                    chkFront.setEnabled(false);
                } else {
                    chkPush.setTextColor(Color.parseColor("#aaaaaa"));
                    chkPush.setEnabled(true);
                    chkBumerang.setTextColor(Color.parseColor("#aaaaaa"));
                    chkBumerang.setEnabled(true);
                    chkFront.setTextColor(Color.parseColor("#aaaaaa"));
                    chkFront.setEnabled(true);
                    if (!chkBoom.isChecked() && !chkPush.isChecked() && !chkBumerang.isChecked() && !chkEagle.isChecked() && !chkCamel.isChecked() && !chkFront.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#aaaaaa"));
                        chkEagle.setEnabled(true);
                        chkCamel.setTextColor(Color.parseColor("#aaaaaa"));
                        chkCamel.setEnabled(true);
                    }
                }
            }
        });
        /*
        위와 동일한 방식
         */

        edtCritical.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //치명타 확률이 입력받을 때마다 작동한다.
                String temp = String.valueOf(edtCritical.getText()); //임시 문자열 배열에 치명타 확률에 입력된 값을 가져온다.
                int index = temp.indexOf("."); //'.'의 위치를 받는다.
                String result = "", end_result = ""; //이후 사용될 변수를 만들어 놓는다.
                if (index != -1) result = temp.substring(0, index); //'.'가 있는 위치 전까지 값들을 받아 넣는다. 예를 들어 44.5면 44만 받게 된다.
                else result = temp; //index가 -1이면 '.'이 없다는 것이므로 전체 값들을 받아 넣는다.
                if (!result.equals("")) { //결과값이 비어있지 않다면 작동한다.
                    if (index != -1) end_result = temp.substring(index+1, temp.length()); //소수점 뒤에 있는 숫자도 받는다.
                    if (Integer.parseInt(result) == 60 && !end_result.equals("")) { //예를 들어 60.2도 60이 넘어가기 때문에 60이며 소수점 뒤가 0보다 크게 되면 작동하게 한다.
                        if (Integer.parseInt(end_result) > 0) { //소수점 뒤 숫자가 0보다 크면 작동한다.
                            Toast.makeText(getActivity(), "'치명타 확률'은 60 이하이여야 합니다.", Toast.LENGTH_SHORT).show(); //토스트로 60보다 커졌다는 것을 알려준다.
                            edtCritical.setText("60"); //자동으로 최대치 60으로 맞춰준다.
                        }
                    }
                    if (Integer.parseInt(result) < 0 || Integer.parseInt(result) > 60) { //0보다 작아지거나 60보다 커지게 되면 작동한다.
                        Toast.makeText(getActivity(), "'치명타 확률'은 0 이상, 60 이하이여야 합니다.", Toast.LENGTH_SHORT).show(); //토스트로 문제를 알려준다.
                        edtCritical.setText("0"); //자동으로 최소치로 맞춰준다.
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtAiming.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = String.valueOf(edtAiming.getText());
                int index = temp.indexOf(".");
                String result = "", end_result = "";
                if (index != -1) result = temp.substring(0, index);
                else result = temp;
                if (!result.equals("")) {
                    if (index != -1) end_result = temp.substring(index+1, temp.length());
                    if (Integer.parseInt(result) == 100 && !end_result.equals("")) {
                        if (Integer.parseInt(end_result) > 0) {
                            Toast.makeText(getActivity(), "'명중률'은 100 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                            edtAiming.setText("100");
                        }
                    }
                    if (Integer.parseInt(result) < 0 || Integer.parseInt(result) > 100) {
                        Toast.makeText(getActivity(), "'헤드샷 확률'은 0 이상, 100 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                        edtAiming.setText("0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtHeadshot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = String.valueOf(edtHeadshot.getText());
                int index = temp.indexOf(".");
                String result = "", end_result = "";
                if (index != -1) result = temp.substring(0, index);
                else result = temp;
                if (!result.equals("")) {
                    if (index != -1) end_result = temp.substring(index+1, temp.length());
                    if (Integer.parseInt(result) == 100 && !end_result.equals("")) {
                        if (Integer.parseInt(end_result) > 0) {
                            Toast.makeText(getActivity(), "'헤드샷 확률'은 100 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                            edtHeadshot.setText("100");
                        }
                    }
                    if (Integer.parseInt(result) < 0 || Integer.parseInt(result) > 100) {
                        Toast.makeText(getActivity(), "'헤드샷 확률'은 0 이상, 100 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                        edtHeadshot.setText("0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtReload.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(edtReload.getText()).equals("")) {
                    double input = Double.parseDouble(String.valueOf(edtReload.getText()));
                    if (input < 0 || input > 600) {
                        Toast.makeText(getActivity(), "'재장전 시간'은 600초(10분)을 넘겨선 안됩니다.", Toast.LENGTH_SHORT).show();
                        edtReload.setText("0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnDPS.setOnLongClickListener(new Button.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                dialogView = getLayoutInflater().inflate(R.layout.resetlayout, null);
                progressReset = dialogView.findViewById(R.id.progressReset);
                progressReset.setMax(1500);
                progressReset.setProgress(0);
                reset_count = 0;

                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);
                builder.setTitle("초기화까지");
                alertDialog = builder.create();
                alertDialog.show();

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        reset_count = 0;
                        progressReset.setProgress(0);
                        alertDialog.dismiss();
                        reset_count = 0;
                        mHandler.removeMessages(0);
                    }
                });

                mHandler.sendEmptyMessageDelayed(0, 20);

                return false;
            }
        });
        /*
        위와 동일한 방식
         */
        btnDPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //초기화 버튼이 한번 눌렸을 경우 작동한다.
                Toast.makeText(getActivity(), "길게 누르십시오.", Toast.LENGTH_SHORT).show(); //길게 눌러야 작동되는 초기화 버튼이므로 길게 누르라고 토스트로 알려준다.
            }
        });

        /*btnDPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtNickname.setText("");
                edtWeaponDemage.setText("");
                edtRPM.setText("");
                edtCritical.setText("");
                edtCriticalDemage.setText("");
                edtHeadshot.setText("");
                edtHeadshotDemage.setText("");
                edtEliteDemage.setText("");
                edtSheldDemage.setText("");
                edtHealthDemage.setText("");
                edtReload.setText("");
                edtAmmo.setText("");
                edtAiming.setText("");

                if (chkEagle.isChecked()) chkEagle.toggle();
                if (chkBoom.isChecked()) chkBoom.toggle();
                if (chkSeeker.isChecked()) chkSeeker.toggle();
                if (chkBumerang.isChecked()) chkBumerang.toggle();
                if (chkCrazy.isChecked()) chkCrazy.toggle();
                if (chkPush.isChecked()) chkPush.toggle();
                if (chkQuickhand.isChecked()) chkQuickhand.toggle();
                if (chkCamel.isChecked()) chkCamel.toggle();
                if (chkFire.isChecked()) chkFire.toggle();

                chkPush.setTextColor(Color.parseColor("#000000"));
                chkPush.setEnabled(true);
                chkEagle.setTextColor(Color.parseColor("#000000"));
                chkEagle.setEnabled(true);
                chkBumerang.setTextColor(Color.parseColor("#000000"));
                chkBumerang.setEnabled(true);
                chkBoom.setTextColor(Color.parseColor("#000000"));
                chkBoom.setEnabled(true);
                chkCrazy.setTextColor(Color.parseColor("#000000"));
                chkCrazy.setEnabled(true);
                chkQuickhand.setTextColor(Color.parseColor("#000000"));
                chkQuickhand.setEnabled(true);
                chkCamel.setTextColor(Color.parseColor("#000000"));
                chkCamel.setEnabled(true);

                rgPush.clearCheck();
                rgCrazy.clearCheck();

                for (int i = 0; i < chkCamelOption.length; i++) if (chkCamelOption[i].isChecked()) chkCamelOption[i].toggle();

                Toast.makeText(getActivity(), "입력값들이 모두 초기화 되었습니다.", Toast.LENGTH_SHORT).show();

                if (String.valueOf(edtWeaponDemage.getText()).equals("") || String.valueOf(edtRPM.getText()).equals("") || String.valueOf(edtAmmo.getText()).equals("")) {
                    Toast.makeText(getActivity(), "무기 데미지, RPM, 탄창이 입력해야합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    int temp_demage = Integer.parseInt(String.valueOf(edtWeaponDemage.getText()));
                    int temp_rpm = Integer.parseInt(String.valueOf(edtRPM.getText()));
                    int temp_ammo = Integer.parseInt(String.valueOf(edtAmmo.getText()));
                    if (temp_demage <= 0 || temp_rpm <= 0 || temp_ammo <= 0) {
                        Toast.makeText(getActivity(), "무기 데미지, RPM, 탄창을 최소 0 이상 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        WeaponSimulation ws = new WeaponSimulation();
                        try {
                            ws.setWeapondemage(Double.parseDouble(String.valueOf(edtWeaponDemage.getText())));
                            ws.setRPM(Double.parseDouble(String.valueOf(edtRPM.getText())));
                            if (!String.valueOf(edtCritical.getText()).equals("")) ws.setCritical(Double.parseDouble(String.valueOf(edtCritical.getText())));
                            else ws.setCritical(0);
                            if (!String.valueOf(edtCriticalDemage.getText()).equals("")) ws.setCriticaldemage(Double.parseDouble(String.valueOf(edtCriticalDemage.getText())));
                            else ws.setCriticaldemage(0);
                            if (!String.valueOf(edtHeadshot.getText()).equals("")) ws.setHeadshot(Double.parseDouble(String.valueOf(edtHeadshot.getText())));
                            else ws.setHeadshot(0);
                            if (!String.valueOf(edtHeadshotDemage.getText()).equals("")) ws.setHeadshotdemage(Double.parseDouble(String.valueOf(edtHeadshotDemage.getText())));
                            else ws.setHealthdemage(0);
                            if (!String.valueOf(edtEliteDemage.getText()).equals("")) ws.setElitedemage(Double.parseDouble(String.valueOf(edtEliteDemage.getText())));
                            else ws.setElitedemage(0);
                            if (!String.valueOf(edtSheldDemage.getText()).equals("")) ws.setShelddemage(Double.parseDouble(String.valueOf(edtSheldDemage.getText())));
                            else ws.setShelddemage(0);
                            if (!String.valueOf(edtHealthDemage.getText()).equals("")) ws.setHealthdemage(Double.parseDouble(String.valueOf(edtHealthDemage.getText())));
                            else ws.setHealthdemage(0);
                            if (!String.valueOf(edtReload.getText()).equals("")) ws.setReloadtime(Double.parseDouble(String.valueOf(edtReload.getText())));
                            else ws.setReloadtime(0);
                            ws.setAmmo(Double.parseDouble(String.valueOf(edtAmmo.getText())));
                        } catch (Exception e) {
                            builder_error = new AlertDialog.Builder(getActivity());
                            builder_error.setTitle("오류").setMessage("Error\n"+e.getStackTrace());
                            builder_error.setPositiveButton("확인", null);
                            alertDialog_error = builder_error.create();
                            alertDialog_error.show();
                        }

                        Intent intent = new Intent(getActivity(), DemageSimulationActivity.class);

                        try {
                            intent.putExtra("bodyhealth",ws.getbody_health());
                            intent.putExtra("bodycriticalhealth", ws.getbody_critical_health());
                            intent.putExtra("headshothealth", ws.getheadshot_health());
                            intent.putExtra("headshotcriticalhealth", ws.getheadshot_critical_health());
                            intent.putExtra("healthDPS", ws.getdps_health());
                            intent.putExtra("healthDPM", ws.getdpm_health());

                            intent.putExtra("bodysheld", ws.getbody_sheld());
                            intent.putExtra("bodycriticalsheld", ws.getbody_critical_sheld());
                            intent.putExtra("headshotsheld", ws.getheadshot_sheld());
                            intent.putExtra("headshotcriticalsheld", ws.getheadshot_critical_sheld());
                            intent.putExtra("sheldDPS", ws.getdps_sheld());
                            intent.putExtra("sheldDPM", ws.getdpm_sheld());

                            intent.putExtra("bodyhealthelite", ws.getelite_body_health());
                            intent.putExtra("bodycriticalhealthelite", ws.getelite_body_critical_health());
                            intent.putExtra("headshothealthelite", ws.getelite_headshot_health());
                            intent.putExtra("headshotcriticalhealthelite", ws.getelite_headshot_critical_health());
                            intent.putExtra("elitehealthDPS", ws.getdps_elite_health());
                            intent.putExtra("elitehealthDPM", ws.getdpm_elite_health());

                            intent.putExtra("bodysheldelite", ws.getelite_body_sheld());
                            intent.putExtra("bodycriticalsheldelite", ws.getelite_body_critical_sheld());
                            intent.putExtra("headshotsheldelite", ws.getelite_headshot_sheld());
                            intent.putExtra("headshotcriticalsheldelite", ws.getelite_headshot_critical_sheld());
                            intent.putExtra("elitesheldDPS", ws.getdps_elite_sheld());
                            intent.putExtra("elitesheldDPM", ws.getdpm_elite_sheld());

                            startActivity(intent);
                        } catch (Exception e) {
                            builder_error = new AlertDialog.Builder(getActivity());
                            builder_error.setTitle("오류").setMessage("Error\n"+e.getStackTrace());
                            builder_error.setPositiveButton("확인", null);
                            alertDialog_error = builder_error.create();
                            alertDialog_error.show();
                        }
                    }
                }
            }
        });*/

        btnDemageSimul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(edtWeaponDemage.getText()).equals("") || String.valueOf(edtRPM.getText()).equals("") || String.valueOf(edtAmmo.getText()).equals("") || String.valueOf(edtAiming.getText()).equals("")) { //무기데미지, RPM, 탄약 수, 명중률 등 필수 항목이 비었을 경우 작동한다.
                    Toast.makeText(getActivity(), "무기 데미지, RPM, 탄창, 명중률이 입력해야합니다.", Toast.LENGTH_SHORT).show(); //필수 항목은 꼭 입력해야 하니 꼭 입력해야 한다고 알려주는 토스트를 보여준다.
                } else {
                    int temp_demage = Integer.parseInt(String.valueOf(edtWeaponDemage.getText())); //무기 데미지를 임시로 저장하는 변수를 생성한다.
                    int temp_rpm = Integer.parseInt(String.valueOf(edtRPM.getText())); //위와 동일한 방식
                    int temp_ammo = Integer.parseInt(String.valueOf(edtAmmo.getText())); //위와 동일한 방식
                    /*String temp_critical, temp_criticaldemage, temp_headshot, temp_headshotdemage, temp_elitedemage, temp_shelddemage, temp_healthdemage, temp_reload, temp_aiming;
                    temp_critical = String.valueOf(edtCritical.getText());
                    temp_criticaldemage = String.valueOf(edtCriticalDemage.getText());
                    temp_headshot = String.valueOf(edtHeadshot.getText());
                    temp_headshotdemage = String.valueOf(edtHealthDemage.getText());
                    System.out.println("headshot demage : "+temp_headshotdemage);
                    temp_elitedemage = String.valueOf(edtEliteDemage.getText());
                    temp_shelddemage = String.valueOf(edtSheldDemage.getText());
                    temp_healthdemage = String.valueOf(edtHealthDemage.getText());
                    temp_reload = String.valueOf(edtReload.getText());
                    temp_aiming = String.valueOf(edtAiming.getText());*/
                    if (temp_demage <= 0 || temp_rpm <= 0 || temp_ammo <= 0) { //무기데미지, RPM, 탄약 수가 0보다 작거나 같을 때 작동한다. 단, 1개라도 해당하면 작동한다.
                        Toast.makeText(getActivity(), "무기 데미지, RPM, 탄창을 최소 0 이상 입력해야 합니다.", Toast.LENGTH_SHORT).show(); //0이 되면 안 되는 필수 항목이므로 안내 메시지를 보여주는 토스트를 생성한다.
                    } /*else if ((temp_critical.indexOf(".") == temp_critical.length()-1 && !temp_critical.equals("")) ||
                            (temp_criticaldemage.indexOf(".") == temp_criticaldemage.length()-1 && !temp_criticaldemage.equals("")) ||
                            (temp_headshot.indexOf(".") == temp_headshot.length()-1 && !temp_headshot.equals("")) ||
                            (temp_headshotdemage.indexOf(".") == temp_headshotdemage.length()-1 && !temp_headshotdemage.equals("")) ||
                            (temp_elitedemage.indexOf(".") == temp_elitedemage.length()-1 && !temp_elitedemage.equals("")) ||
                            (temp_shelddemage.indexOf(".") == temp_shelddemage.length()-1 && !temp_shelddemage.equals("")) ||
                            (temp_healthdemage.indexOf(".") == temp_healthdemage.length()-1 && !temp_healthdemage.equals("")) ||
                            (temp_reload.indexOf(".") == temp_reload.length()-1 && !temp_reload.equals("")) ||
                            (temp_aiming.indexOf(".") == temp_aiming.length()-1 && !temp_aiming.equals(""))) {
                        Toast.makeText(getActivity(), "입력했던 값들 중 끝에 .으로 끝나는 입력값이 있습니다. 확인 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }*/ else { //필수 항목이 올바르게 입력되었을 경우 작동한다.
                        switch (rgCrazy.getCheckedRadioButtonId()) {
                            case R.id.rdoCrazy1:
                                crazy_dmg = 0; break; //방어도가 80~100일 경우
                            case R.id.rdoCrazy2:
                                crazy_dmg = 10; break; //방어도가 60~80일 경우
                            case R.id.rdoCrazy3:
                                crazy_dmg = 20; break; //방어도가 40~60일 경우
                            case R.id.rdoCrazy4:
                                crazy_dmg = 30; break; //방어도가 20~40일 경우
                            case R.id.rdoCrazy5:
                                crazy_dmg = 40; break; //방어도가 0~20일 경우
                            case R.id.rdoCrazy6:
                                crazy_dmg = 50; break; //방어도가 전부 소진되었을 경우
                            default:
                                Toast.makeText(getActivity(), "광분 여부가 체크가 안 되어 있으므로 광분 없는 것으로 설정합니다.", Toast.LENGTH_SHORT).show(); //아무것도 체크되지 않았을 경우에 토스트로 알려준다.
                                crazy_dmg = 0; //아무것도 체크되지 않았으므로 없는것으로 설정하기 때문에 0으로 한다.
                        }

                        switch (rgPush.getCheckedRadioButtonId()) {
                            case R.id.rdoPush1:
                                push_dmg = 0; break; //방어도가 90~100일 경우
                            case R.id.rdoPush2:
                                push_dmg = 5; break; //방어도가 80~90일 경우
                            case R.id.rdoPush3:
                                push_dmg = 10; break; //방어도가 70~80일 경우
                            case R.id.rdoPush4:
                                push_dmg = 15; break; //방어도가 60~70일 경우
                            case R.id.rdoPush5:
                                push_dmg = 20; break; //방어도가 50~60일 경우
                            case R.id.rdoPush6:
                                push_dmg = 25; break; //방어도가 40~50일 경우
                            case R.id.rdoPush7:
                                push_dmg = 30; break; //방어도가 30~40일 경우
                            case R.id.rdoPush8:
                                push_dmg = 35; break; //방어도가 20~30일 경우
                            case R.id.rdoPush9:
                                push_dmg = 40; break; //방어도가 10~20일 경우
                            case R.id.rdoPush10:
                                push_dmg = 45; break; //방어도가 0~10일 경우
                            case R.id.rdoPush11:
                                push_dmg = 50; break; //방어도가 전부 소진되었을 경우
                            default:
                                Toast.makeText(getActivity(), "중압감 여부가 체크가 안 되어 있으므로 중압감 없는 것으로 설정합니다.", Toast.LENGTH_SHORT).show();
                                push_dmg = 0;
                                /*
                                위와 동일한 방식
                                 */
                        }

                        final boolean[] options = { false, false, false }; //카멜레온 헤드샷, 몸샷, 레그샷(다리) 버프 여부를 저장한다.

                        for (int i = 0; i < chkCamelOption.length; i++) if (chkCamelOption[i].isChecked()) options[i] = true; //버프 여부 체크박스가 체크되었을 경우 참으로 바꾼다.

                        if (chkSeeker.isChecked()) seeker_dmg = 20; //감시병 여부가 적용되었으면 20% 데미지로 저장한다.
                        else seeker_dmg = 0; //감시병 여부가 적용되어 있지 않다면 0%로 저장시킨다.

                        if (chkBoom.isChecked()) boom = true;
                        else boom = false;
                        //위와 동일한 방식

                        if (chkEagle.isChecked()) eagle_dmg = 35;
                        else eagle_dmg = 0;
                        //위와 동일한 방식

                        if (chkFire.isChecked()) fire = true;
                        else fire = false;
                        //위와 동일한 방식

                        if (chkFront.isChecked()) front_dmg = 50;
                        else front_dmg = 0;
                        //위와 동일한 방식

                        View dialogView = getLayoutInflater().inflate(R.layout.dialoglayout, null);//다이얼 로그에 사용할 VIEW를 생성한다. dialoglayout.xml을 불러온다. 다이얼로그를 생성할 때 화면에 dialoglayout이 나타나게 된다.
                        final EditText edtSheld = dialogView.findViewById(R.id.edtSheld);
                        final EditText edtHealth = dialogView.findViewById(R.id.edtHealth);
                        final CheckBox chkElite = dialogView.findViewById(R.id.chkElite);
                        final CheckBox chkPVP = dialogView.findViewById(R.id.chkPVP);
                        final CheckBox chkCluch = dialogView.findViewById(R.id.chkCluch);
                        final LinearLayout layoutCluch = dialogView.findViewById(R.id.layoutCluch);

                        final EditText edtCluchRPM = dialogView.findViewById(R.id.edtCluchRPM);
                        final EditText edtCluchAmmo = dialogView.findViewById(R.id.edtCluchAmmo);
                        final EditText edtCluchReload = dialogView.findViewById(R.id.edtCluchReload);
                        final EditText edtCluchCritical = dialogView.findViewById(R.id.edtCluchCritical);
                        final EditText edtCluchAiming = dialogView.findViewById(R.id.edtCluchAiming);

                        final LinearLayout layoutPVP = dialogView.findViewById(R.id.layoutPVP);
                        final RadioGroup rgPVP = dialogView.findViewById(R.id.rgPVP);
                        final RadioButton[] rdoPVP = new RadioButton[7];

                        final Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                        final Button btnPlay = dialogView.findViewById(R.id.btnPlay);
                        //레이아웃에 있는 view들을 가져온다.

                        int temp_index; //배열로 불러오는 view들의 주소를 임시로 담아두는 변수를 생성한다.
                        for (int i = 0; i < rdoPVP.length; i++) {
                            temp_index = dialogView.getResources().getIdentifier("rdoPVP"+(i+1), "id", getActivity().getPackageName()); //규칙적으로 rdoPVP1, rdoPVP2 등 숫자만 바뀌므로 배열을 사용하여 view를 가져와 임시 변수에 주소를 넣는다.
                            rdoPVP[i] = dialogView.findViewById(temp_index); //임시로 담은 변수에 있는 주소를 통해 view에 저장한다.
                        }

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //다이얼로그를 띄우는 객체이다.
                        builder.setView(dialogView); //다이얼로그 빌더에 dialogView를 넣어 다이얼로그를 보여줄 때 해당 뷰를 보여주게 해준다.

                        edtCluchCritical.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) { //치명타 확률이 입력되었을 때마다 작동한다.
                                String temp = String.valueOf(edtCluchCritical.getText());
                                int index = temp.indexOf(".");
                                String result = "";
                                if (index != -1) result = temp.substring(0, index);
                                else result = temp;
                                if (!result.equals("")) {
                                    if (Integer.parseInt(result) < 0 || Integer.parseInt(result) > 100) {
                                        Toast.makeText(getActivity(), "'치명타 확률'은 0 이상, 100 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                                        edtCluchCritical.setText("0");
                                    }
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        }); //상단 치명타 확률, 헤드샷 확률 등 입력할 때마다 작동하는 방식과 동일하다.
                        chkCluch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) layoutCluch.setVisibility(View.VISIBLE);
                                else layoutCluch.setVisibility(View.GONE);
                            }
                        });

                        chkPVP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (chkPVP.isChecked()) {
                                    chkElite.setTextColor(Color.parseColor("#bbbbbb"));
                                    chkElite.setEnabled(false);
                                    chkCluch.setEnabled(true);
                                    layoutPVP.setVisibility(View.VISIBLE);
                                } else {
                                    chkElite.setTextColor(Color.parseColor("#000000"));
                                    chkElite.setEnabled(true);
                                    layoutPVP.setVisibility(View.GONE);
                                    rdoPVP[0].setChecked(true);
                                    if (chkCluch.isChecked()) {
                                        layoutCluch.setVisibility(View.GONE);
                                        chkCluch.toggle();
                                    }
                                    chkCluch.setEnabled(false);
                                }
                            }
                        });

                        chkElite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    chkPVP.setTextColor(Color.parseColor("#bbbbbb"));
                                    chkPVP.setEnabled(false);
                                } else {
                                    chkPVP.setTextColor(Color.parseColor("#000000"));
                                    chkPVP.setEnabled(true);
                                }
                            }
                        });
                        //위와 동일한 방식
                        final AlertDialog alertDialog = builder.create(); //다이얼로그를 생성한다. 빌더에 저장된 정보들을 통해 만들어진다.
                        btnPlay.setOnClickListener(new View.OnClickListener() { //실행 버튼을 누를 경우 작동한다.
                            @Override
                            public void onClick(View v) {
                                boolean elite_true, pvp_true, cluch_true; //체크 박스로 인해 작동 여부를 저장하는 변수이다.
                                double coefficient = 1; //기본 계수를 결정한다. 1로 초기화한다.
                                if (chkPVP.isChecked()) { //PVP 여부를 적용시키면 작동한다.
                                    if (rdoPVP[0].isChecked()) coefficient = 0.68;
                                    else if (rdoPVP[1].isChecked()) coefficient = 0.66;
                                    else if (rdoPVP[2].isChecked() || rdoPVP[3].isChecked() || rdoPVP[5].isChecked() || rdoPVP[6].isChecked()) coefficient = 0.55;
                                    else coefficient = 0.6;
                                }
                                //각 무기의 종류에 맞게 계수를 적용시킨다.
                                if (chkElite.isChecked() && !chkPVP.isChecked()) elite_true = true; //정예 대상이 적용되어 있다면 작동한다.
                                else elite_true = false; //정예 대상이 적용되어 있지 않다면 작동한다.
                                if (chkPVP.isChecked()) pvp_true = true;
                                else pvp_true = false;
                                if (chkQuickhand.isChecked()) quick_hand = true;
                                else quick_hand = false;
                                if (chkCluch.isChecked()) cluch_true = true;
                                else cluch_true = false;
                                if (chkBumerang.isChecked()) bumerang_true = true;
                                else bumerang_true = false;
                                //위와 동일한 방식

                                if (String.valueOf(edtHealth.getText()).equals("")) { //생명력이 입력되지 않았을 경우 작동한다.
                                    Toast.makeText(getActivity(), "체력은 입력해야 합니다.", Toast.LENGTH_SHORT).show(); //생명력을 입력하라는 메시지를 토스트를 통해 전달한다.
                                } else {
                                    int temp_health = Integer.parseInt(String.valueOf(edtHealth.getText())); //입력받은 생명력을 int타입으로 변환하여 임시 변수에 저장한다.
                                    if (temp_health <= 0) { //생명력이 0 이하일 경우 작동한다.
                                        Toast.makeText(getActivity(), "체력은 최소 0을 초과해야만 합니다.", Toast.LENGTH_SHORT).show(); //생명력은 최소 0보다 커야 하므로 0보다 크게 입력하라고 토스트를 통해 전달한다.
                                    } else {
                                        DemageSimulThread ws = new DemageSimulThread(); //데미지 스레드를 생성한다.

                                        try {
                                            ws.setWeapondemage(Double.parseDouble(String.valueOf(edtWeaponDemage.getText()))); //데미지 스레드에 입력한 무기 데미지를 저장한다.
                                            ws.setRPM(Double.parseDouble(String.valueOf(edtRPM.getText()))); //데미지 스레드에 입력한 RPM을 저장한다.
                                            if (!String.valueOf(edtCritical.getText()).equals("")) ws.setCritical(Double.parseDouble(String.valueOf(edtCritical.getText()))); //치명타 확률이 입력되어 있다면 데미지 스레드에 치명타 확률을 저장한다.
                                            else ws.setCritical(0); //치명타 확률이 아무것도 입력되어 있지 않다면 0으로 자동으로 저장시킨다.
                                            if (String.valueOf(edtCriticalDemage.getText()).equals("") || Integer.parseInt(String.valueOf(edtCriticalDemage.getText())) < 25) {
                                                Toast.makeText(getActivity(), "치명타 데미지 최솟값이 25%이므로 25%로 자동 변경됩니다.", Toast.LENGTH_SHORT).show();
                                                ws.setCriticaldemage(25);
                                            } else if (!String.valueOf(edtCriticalDemage.getText()).equals("")) ws.setCriticaldemage(Double.parseDouble(String.valueOf(edtCriticalDemage.getText())));
                                            else ws.setCriticaldemage(25);
                                            if (!String.valueOf(edtHeadshot.getText()).equals("")) ws.setHeadshot(Double.parseDouble(String.valueOf(edtHeadshot.getText())));
                                            else ws.setHeadshot(0);
                                            if (String.valueOf(edtHeadshotDemage.getText()).equals("") || Integer.parseInt(String.valueOf(edtHeadshotDemage.getText())) < 50) {
                                                Toast.makeText(getActivity(), "헤드샷 데미지 최솟값이 50%이므로 50%로 자동 변경됩니다.", Toast.LENGTH_SHORT).show();
                                                ws.setHeadshotdemage(50);
                                            } else if (!String.valueOf(edtHeadshotDemage.getText()).equals("")) ws.setHeadshotdemage(Double.parseDouble(String.valueOf(edtHeadshotDemage.getText())));
                                            else ws.setHeadshotdemage(50);
                                            if (!String.valueOf(edtEliteDemage.getText()).equals("")) ws.setElitedemage(Double.parseDouble(String.valueOf(edtEliteDemage.getText())));
                                            else ws.setElitedemage(0);
                                            if (!String.valueOf(edtSheldDemage.getText()).equals("")) ws.setShelddemage(Double.parseDouble(String.valueOf(edtSheldDemage.getText())));
                                            else ws.setShelddemage(0);
                                            if (!String.valueOf(edtHealthDemage.getText()).equals("")) ws.setHealthdemage(Double.parseDouble(String.valueOf(edtHealthDemage.getText())));
                                            else ws.setHealthdemage(0);
                                            if (!String.valueOf(edtReload.getText()).equals("")) ws.setReloadtime(Double.parseDouble(String.valueOf(edtReload.getText())));
                                            else ws.setReloadtime(0);
                                            ws.setAmmo(Double.parseDouble(String.valueOf(edtAmmo.getText())));
                                            if (!String.valueOf(edtSheld.getText()).equals("")) ws.setSheld(Integer.parseInt(String.valueOf(edtSheld.getText())));
                                            else ws.setSheld(0);
                                            if (!String.valueOf(edtAiming.getText()).equals("") && !String.valueOf(edtAiming.getText()).equals("0")) ws.setAiming(Double.parseDouble(String.valueOf(edtAiming.getText())));
                                            else ws.setAiming(50);
                                            //위와 동일한 방식
                                            ws.setHealth(Integer.parseInt(String.valueOf(edtHealth.getText()))); //데미지 스레드의 생명력은 입력한 생명력으로 저장시킨다.
                                            ws.setPVP_true(pvp_true); //PVP 여부를 저장한다.
                                            ws.setElite_true(elite_true); //정예 대상 여부를 저장한다.
                                            ws.setCrazy_dmg(crazy_dmg); //광분, 방어도에 따른 데미지 수치를 저장한다. 0보다 크면 적용된 것으로 간주한다.
                                            ws.setSeeker_dmg(seeker_dmg); //감시병, 0보다 크면 적용된 것으로 간주한다.
                                            ws.setPush_critical_dmg(push_dmg); //중압감, 방어도에 따른 데미지 수치를 저장한다. 0보다 크면 적용된 것으로 간주한다.
                                            ws.setBoom(boom); //무자비 수납 여부를 저장한다.
                                            ws.setEagle_dmg(eagle_dmg); //집념, 집념 데미지 수치를 저장하며 0보다 크면 적용된 것으로 간주한다.
                                            ws.setQuick_hand(quick_hand); //빠른 손 여부를 저장한다.
                                            ws.setCluch_true(cluch_true); //클러치 여부를 저장한다.
                                            ws.setBumerang_true(bumerang_true); //부메랑 여부를 저장한다.
                                            ws.setOptions(options); //카멜레온이 적용되어 있지 않다면 전부 false이다. 카멜레온 각각 옵션의 여부가 담긴 배열을 저장한다.
                                            ws.setFire(fire); //불꽃 여부를 저장한다.
                                            if (chkPVP.isChecked()) ws.setCoefficient(coefficient); //PVP 여부가 적용되어 있다면 무기의 종류에 따른 무기 계수를 저장시킨다.
                                            if (chkFront.isChecked()) ws.setFront_dmg(front_dmg); //근접전의 대가가 적용되어 있다면 급접전의 대가의 데미지 수치를 저장시킨다.
                                            else ws.setFront_dmg(0); //근접전의 대가가 적용되어 있지 않다면 0으로 초기화한다.

                                            String elite = Boolean.toString(elite_true); //정예 여부를 문자열로 변환하여 임시 변수에 저장한다. 이후 다음 액티비티로 넘길 때 사용한다.

                                            //System.out.println(cluch_true);

                                            if (cluch_true && (String.valueOf(edtCluchReload.getText()).equals("") || String.valueOf(edtCluchAmmo.getText()).equals("") || String.valueOf(edtCluchRPM.getText()).equals("") || String.valueOf(edtCluchCritical.getText()).equals("") || String.valueOf(edtCluchAiming.getText()).equals(""))) {
                                                Toast.makeText(getActivity(), "재장전 시간, 탄약수, RPM, 치명타 확률, 명중률 모두 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                                                //클러치가 적용되어 있으면 재장전 시간, 탄약수 등 필수 항목이 모두 입력되어 있어야 한다고 토스트를 통해 전달한다.
                                            } else {
                                                if (cluch_true) { //클러치가 적용되어 있다면 작동한다.
                                                    ct = new CluchThread(Integer.parseInt(String.valueOf(edtCluchRPM.getText())), Integer.parseInt(String.valueOf(edtCluchAmmo.getText())), Double.parseDouble(String.valueOf(edtCluchReload.getText())), Double.parseDouble(String.valueOf(edtCluchCritical.getText())), Double.parseDouble(String.valueOf(edtCluchAiming.getText())));
                                                    //ws.setCluchThread(ct);
                                                    //클러치 스레드를 생성한다. 입력한 값들도 자동으로 생성과 동시에 저장된다.
                                                }

                                                TimeThread tt = new TimeThread(); //시뮬 동안 타이머를 작동할 타이머 스레드를 생성한다.

                                                Intent intent = new Intent(getActivity(), SimulActivity.class); //SimulActivity를 화면에 띄울 intent를 생성한다.

                                                intent.putExtra("thread", ws); //데미지 스레드를 다음 액티비티로 넘긴다.
                                                intent.putExtra("cluchthread", ct); //클러치 스레드를 다음 액티비티로 넘긴다.
                                                intent.putExtra("timethread", tt); //타이머 스레드를 다음 액티비티로 넘긴다.
                                                intent.putExtra("nickname", String.valueOf(edtNickname.getText())); //입력한 닉네임을 다음 액티비티로 넘긴다.
                                                intent.putExtra("elite", elite); //정예 여부를 다음 액티비티로 넘긴다.
                                                intent.putExtra("quickhand", Boolean.toString(quick_hand)); //빠른 손 여부를 문자열로 변환하여 다음 액티비티로 넘긴다.

                                                startActivity(intent); //액티비티를 실행한다.
                                                alertDialog.dismiss(); //할 일을 마친 다이얼로그는 닫는다.
                                            }
                                        } catch (Exception e) {
                                            builder_error = new AlertDialog.Builder(getActivity());
                                            builder_error.setTitle("오류").setMessage("Error\n"+e.getMessage());
                                            builder_error.setPositiveButton("확인", null);
                                            alertDialog_error = builder_error.create();
                                            alertDialog_error.show();
                                            //오류 발생 시 오류 메시지를 띄워줄 다이얼로그를 생성한다.
                                            System.err.println(e); //시스템에도 메시지를 띄운다.
                                        }
                                    }
                                }
                            }
                        });
                        btnCancel.setOnClickListener(new View.OnClickListener() { //취소 버튼을 눌렀을 경우 작동한다.
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss(); //다이얼로그를 닫는다.
                            }
                        });
                        alertDialog.setCancelable(false); //다이얼로그 바깥 영역, 뒤로 가기를 눌러도 닫히지 않게 한다.
                        alertDialog.show(); //다이얼로그를 화면에 보여준다.
                    }
                }
            }
        });

        return root;
    }
}