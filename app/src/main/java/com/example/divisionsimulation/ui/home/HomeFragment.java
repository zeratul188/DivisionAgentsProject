package com.example.divisionsimulation.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import com.example.divisionsimulation.R;
import com.google.android.material.chip.ChipGroup;

import java.io.Serializable;

public class HomeFragment extends Fragment implements Serializable {

    private HomeViewModel homeViewModel;

    private Button btnDemageSimul, btnDPS;

    private CluchThread ct = null;

    private RadioGroup rgCrazy, rgPush;
    private RadioButton[] rdoCrazy = new RadioButton[6];
    private RadioButton[] rdoPush = new RadioButton[11];
    private CheckBox chkSeeker, chkCrazy, chkBoom, chkPush, chkEagle, chkQuickhand, chkBumerang;

    private boolean boom = false, quick_hand = false, bumerang_true = false;

    private int crazy_dmg, seeker_dmg, push_dmg, eagle_dmg;

    private AlertDialog.Builder builder_error = null;
    private AlertDialog alertDialog_error = null;
    private View dialogView_error = null;

    private EditText edtWeaponDemage, edtRPM, edtCritical, edtCriticalDemage, edtHeadshot, edtHeadshotDemage, edtEliteDemage, edtSheldDemage, edtHealthDemage, edtReload, edtAmmo, edtNickname, edtAiming;

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
        rgCrazy = root.findViewById(R.id.rgCrazy);
        int temp;
        for (int i = 0; i < rdoCrazy.length; i++) {
            temp = root.getResources().getIdentifier("rdoCrazy"+(i+1), "id", getActivity().getPackageName());
            rdoCrazy[i] = (RadioButton) root.findViewById(temp);
        }

        chkPush = root.findViewById(R.id.chkPush);
        rgPush = root.findViewById(R.id.rgPush);
        for (int i = 0; i < rdoPush.length; i++) {
            temp = root.getResources().getIdentifier("rdoPush"+(i+1), "id", getActivity().getPackageName());
            rdoPush[i] = (RadioButton) root.findViewById(temp);
        }

        chkSeeker = root.findViewById(R.id.chkSeeker);
        chkEagle = root.findViewById(R.id.chkEagle);

        chkCrazy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) rgCrazy.setVisibility(View.VISIBLE);
                else {
                    rgCrazy.clearCheck();
                    rgCrazy.setVisibility(View.GONE);
                }
            }
        });
        chkPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rgPush.setVisibility(View.VISIBLE);
                    chkQuickhand.setTextColor(Color.parseColor("#bbbbbb"));
                    chkQuickhand.setEnabled(false);
                    chkEagle.setTextColor(Color.parseColor("#bbbbbb"));
                    chkEagle.setEnabled(false);
                    chkBumerang.setTextColor(Color.parseColor("#bbbbbb"));
                    chkBumerang.setEnabled(false);
                }
                else {
                    rgPush.clearCheck();
                    rgPush.setVisibility(View.GONE);
                    chkQuickhand.setTextColor(Color.parseColor("#000000"));
                    chkQuickhand.setEnabled(true);
                    chkBumerang.setTextColor(Color.parseColor("#000000"));
                    chkBumerang.setEnabled(true);
                    if (!chkBoom.isChecked() && !chkQuickhand.isChecked() && !chkBumerang.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#000000"));
                        chkEagle.setEnabled(true);
                    }
                }
            }
        });
        chkBumerang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkQuickhand.setTextColor(Color.parseColor("#bbbbbb"));
                    chkQuickhand.setEnabled(false);
                    chkPush.setTextColor(Color.parseColor("#bbbbbb"));
                    chkPush.setEnabled(false);
                    chkEagle.setTextColor(Color.parseColor("#bbbbbb"));
                    chkEagle.setEnabled(false);
                } else {
                    chkPush.setTextColor(Color.parseColor("#000000"));
                    chkPush.setEnabled(true);
                    chkQuickhand.setTextColor(Color.parseColor("#000000"));
                    chkQuickhand.setEnabled(true);
                    if (!chkBoom.isChecked() && !chkPush.isChecked() && !chkQuickhand.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#000000"));
                        chkEagle.setEnabled(true);
                    }
                }
            }
        });
        chkEagle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkBoom.setTextColor(Color.parseColor("#bbbbbb"));
                    chkBoom.setEnabled(false);
                    chkQuickhand.setTextColor(Color.parseColor("#bbbbbb"));
                    chkQuickhand.setEnabled(false);
                    chkPush.setTextColor(Color.parseColor("#bbbbbb"));
                    chkPush.setEnabled(false);
                    chkBumerang.setTextColor(Color.parseColor("#bbbbbb"));
                    chkBumerang.setEnabled(false);
                } else {
                    chkBoom.setTextColor(Color.parseColor("#000000"));
                    chkBoom.setEnabled(true);
                    chkQuickhand.setTextColor(Color.parseColor("#000000"));
                    chkQuickhand.setEnabled(true);
                    chkPush.setTextColor(Color.parseColor("#000000"));
                    chkPush.setEnabled(true);
                    chkBumerang.setTextColor(Color.parseColor("#000000"));
                    chkBumerang.setEnabled(true);
                }
            }
        });
        chkBoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkEagle.setTextColor(Color.parseColor("#bbbbbb"));
                    chkEagle.setEnabled(false);
                } else {
                    if (!chkPush.isChecked() && !chkQuickhand.isChecked() && !chkBumerang.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#000000"));
                        chkEagle.setEnabled(true);
                    }
                }
            }
        });
        chkQuickhand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkPush.setTextColor(Color.parseColor("#bbbbbb"));
                    chkPush.setEnabled(false);
                    chkEagle.setTextColor(Color.parseColor("#bbbbbb"));
                    chkEagle.setEnabled(false);
                    chkBumerang.setTextColor(Color.parseColor("#bbbbbb"));
                    chkBumerang.setEnabled(false);
                } else {
                    chkPush.setTextColor(Color.parseColor("#000000"));
                    chkPush.setEnabled(true);
                    chkBumerang.setTextColor(Color.parseColor("#000000"));
                    chkBumerang.setEnabled(true);
                    if (!chkBoom.isChecked() && !chkPush.isChecked() && !chkBumerang.isChecked()) {
                        chkEagle.setTextColor(Color.parseColor("#000000"));
                        chkEagle.setEnabled(true);
                    }
                }
            }
        });

        edtCritical.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = String.valueOf(edtCritical.getText());
                int index = temp.indexOf(".");
                String result = "", end_result = "";
                if (index != -1) result = temp.substring(0, index);
                else result = temp;
                if (!result.equals("")) {
                    if (index != -1) end_result = temp.substring(index+1, temp.length());
                    if (Integer.parseInt(result) == 60 && !end_result.equals("")) {
                        if (Integer.parseInt(end_result) > 0) {
                            Toast.makeText(getActivity(), "'치명타 확률'은 60 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                            edtCritical.setText("60");
                        }
                    }
                    if (Integer.parseInt(result) < 0 || Integer.parseInt(result) > 60) {
                        Toast.makeText(getActivity(), "'치명타 확률'은 0 이상, 60 이하이여야 합니다.", Toast.LENGTH_SHORT).show();
                        edtCritical.setText("0");
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

        btnDPS.setOnClickListener(new View.OnClickListener() {
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

                rgPush.clearCheck();
                rgCrazy.clearCheck();

                Toast.makeText(getActivity(), "입력값들이 모두 초기화 되었습니다.", Toast.LENGTH_SHORT).show();

                /*if (String.valueOf(edtWeaponDemage.getText()).equals("") || String.valueOf(edtRPM.getText()).equals("") || String.valueOf(edtAmmo.getText()).equals("")) {
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
                }*/
            }
        });

        btnDemageSimul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(edtWeaponDemage.getText()).equals("") || String.valueOf(edtRPM.getText()).equals("") || String.valueOf(edtAmmo.getText()).equals("") || String.valueOf(edtAiming.getText()).equals("")) {
                    Toast.makeText(getActivity(), "무기 데미지, RPM, 탄창, 명중률이 입력해야합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    int temp_demage = Integer.parseInt(String.valueOf(edtWeaponDemage.getText()));
                    int temp_rpm = Integer.parseInt(String.valueOf(edtRPM.getText()));
                    int temp_ammo = Integer.parseInt(String.valueOf(edtAmmo.getText()));
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
                    if (temp_demage <= 0 || temp_rpm <= 0 || temp_ammo <= 0) {
                        Toast.makeText(getActivity(), "무기 데미지, RPM, 탄창을 최소 0 이상 입력해야 합니다.", Toast.LENGTH_SHORT).show();
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
                    }*/ else {
                        switch (rgCrazy.getCheckedRadioButtonId()) {
                            case R.id.rdoCrazy1:
                                crazy_dmg = 0; break;
                            case R.id.rdoCrazy2:
                                crazy_dmg = 10; break;
                            case R.id.rdoCrazy3:
                                crazy_dmg = 20; break;
                            case R.id.rdoCrazy4:
                                crazy_dmg = 30; break;
                            case R.id.rdoCrazy5:
                                crazy_dmg = 40; break;
                            case R.id.rdoCrazy6:
                                crazy_dmg = 50; break;
                            default:
                                Toast.makeText(getActivity(), "광분 여부가 체크가 안 되어 있으므로 광분 없는 것으로 설정합니다.", Toast.LENGTH_SHORT).show();
                                crazy_dmg = 0;
                        }

                        switch (rgPush.getCheckedRadioButtonId()) {
                            case R.id.rdoPush1:
                                push_dmg = 0; break;
                            case R.id.rdoPush2:
                                push_dmg = 5; break;
                            case R.id.rdoPush3:
                                push_dmg = 10; break;
                            case R.id.rdoPush4:
                                push_dmg = 15; break;
                            case R.id.rdoPush5:
                                push_dmg = 20; break;
                            case R.id.rdoPush6:
                                push_dmg = 25; break;
                            case R.id.rdoPush7:
                                push_dmg = 30; break;
                            case R.id.rdoPush8:
                                push_dmg = 35; break;
                            case R.id.rdoPush9:
                                push_dmg = 40; break;
                            case R.id.rdoPush10:
                                push_dmg = 45; break;
                            case R.id.rdoPush11:
                                push_dmg = 50; break;
                            default:
                                Toast.makeText(getActivity(), "중압감 여부가 체크가 안 되어 있으므로 중압감 없는 것으로 설정합니다.", Toast.LENGTH_SHORT).show();
                                crazy_dmg = 0;
                        }

                        if (chkSeeker.isChecked()) seeker_dmg = 20;
                        else seeker_dmg = 0;

                        if (chkBoom.isChecked()) boom = true;
                        else boom = false;

                        if (chkEagle.isChecked()) eagle_dmg = 25;
                        else eagle_dmg = 0;

                        View dialogView = getLayoutInflater().inflate(R.layout.dialoglayout, null);
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

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setView(dialogView);

                        edtCluchCritical.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                        });
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
                                } else {
                                    chkElite.setTextColor(Color.parseColor("#000000"));
                                    chkElite.setEnabled(true);
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

                        builder.setPositiveButton("실행", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean elite_true, pvp_true, cluch_true;
                                if (chkElite.isChecked() && !chkPVP.isChecked()) elite_true = true;
                                else elite_true = false;
                                if (chkPVP.isChecked()) pvp_true = true;
                                else pvp_true = false;
                                if (chkQuickhand.isChecked()) quick_hand = true;
                                else quick_hand = false;
                                if (chkCluch.isChecked()) cluch_true = true;
                                else cluch_true = false;
                                if (chkBumerang.isChecked()) bumerang_true = true;
                                else bumerang_true = false;

                                if (String.valueOf(edtHealth.getText()).equals("")) {
                                    Toast.makeText(getActivity(), "체력은 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    int temp_health = Integer.parseInt(String.valueOf(edtHealth.getText()));
                                    if (temp_health <= 0) {
                                        Toast.makeText(getActivity(), "체력은 최소 0을 초과해야만 합니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        DemageSimulThread ws = new DemageSimulThread();

                                        try {
                                            ws.setWeapondemage(Double.parseDouble(String.valueOf(edtWeaponDemage.getText())));
                                            ws.setRPM(Double.parseDouble(String.valueOf(edtRPM.getText())));
                                            if (!String.valueOf(edtCritical.getText()).equals("")) ws.setCritical(Double.parseDouble(String.valueOf(edtCritical.getText())));
                                            else ws.setCritical(0);
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
                                            else ws.setHeadshotdemage(0);
                                            if (!String.valueOf(edtReload.getText()).equals("")) ws.setReloadtime(Double.parseDouble(String.valueOf(edtReload.getText())));
                                            else ws.setReloadtime(0);
                                            ws.setAmmo(Double.parseDouble(String.valueOf(edtAmmo.getText())));
                                            if (!String.valueOf(edtSheld.getText()).equals("")) ws.setSheld(Integer.parseInt(String.valueOf(edtSheld.getText())));
                                            else ws.setSheld(0);
                                            if (!String.valueOf(edtAiming.getText()).equals("") && !String.valueOf(edtAiming.getText()).equals("0")) ws.setAiming(Double.parseDouble(String.valueOf(edtAiming.getText())));
                                            else ws.setAiming(50);
                                            ws.setHealth(Integer.parseInt(String.valueOf(edtHealth.getText())));
                                            ws.setPVP_true(pvp_true);
                                            ws.setElite_true(elite_true);
                                            ws.setCrazy_dmg(crazy_dmg);
                                            ws.setSeeker_dmg(seeker_dmg);
                                            ws.setPush_critical_dmg(push_dmg);
                                            ws.setBoom(boom);
                                            ws.setEagle_dmg(eagle_dmg);
                                            ws.setQuick_hand(quick_hand);
                                            ws.setCluch_true(cluch_true);
                                            ws.setBumerang_true(bumerang_true);

                                            String elite = Boolean.toString(elite_true);

                                            System.out.println(cluch_true);

                                            if (cluch_true && (String.valueOf(edtCluchReload.getText()).equals("") || String.valueOf(edtCluchAmmo.getText()).equals("") || String.valueOf(edtCluchRPM.getText()).equals("") || String.valueOf(edtCluchCritical.getText()).equals("") || String.valueOf(edtCluchAiming.getText()).equals(""))) {
                                                Toast.makeText(getActivity(), "재장전 시간, 탄약수, RPM, 치명타 확률, 명중률 모두 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (cluch_true) {
                                                    ct = new CluchThread(Integer.parseInt(String.valueOf(edtCluchRPM.getText())), Integer.parseInt(String.valueOf(edtCluchAmmo.getText())), Double.parseDouble(String.valueOf(edtCluchReload.getText())), Double.parseDouble(String.valueOf(edtCluchCritical.getText())), Double.parseDouble(String.valueOf(edtCluchAiming.getText())));
                                                    //ws.setCluchThread(ct);
                                                }

                                                TimeThread tt = new TimeThread();

                                                Intent intent = new Intent(getActivity(), SimulActivity.class);

                                                intent.putExtra("thread", ws);
                                                intent.putExtra("cluchthread", ct);
                                                intent.putExtra("timethread", tt);
                                                intent.putExtra("nickname", String.valueOf(edtNickname.getText()));
                                                intent.putExtra("elite", elite);
                                                intent.putExtra("quickhand", Boolean.toString(quick_hand));

                                                startActivity(intent);
                                            }
                                        } catch (Exception e) {
                                            builder_error = new AlertDialog.Builder(getActivity());
                                            builder_error.setTitle("오류").setMessage("Error\n"+e.getMessage());
                                            builder_error.setPositiveButton("확인", null);
                                            alertDialog_error = builder_error.create();
                                            alertDialog_error.show();
                                            System.err.println(e);
                                        }
                                    }
                                }
                            }
                        });
                        builder.setNegativeButton("취소", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });

        return root;
    }
}