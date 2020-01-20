package com.example.divisionsimulation.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

    private EditText edtWeaponDemage, edtRPM, edtCritical, edtCriticalDemage, edtHeadshot, edtHeadshotDemage, edtEliteDemage, edtSheldDemage, edtHealthDemage, edtReload, edtAmmo;

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

        btnDPS = root.findViewById(R.id.btnDPS);
        btnDemageSimul = root.findViewById(R.id.btnDemageSimul);

        btnDPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeaponSimulation ws = new WeaponSimulation();
                ws.setWeapondemage(Double.parseDouble(String.valueOf(edtWeaponDemage.getText())));
                ws.setRPM(Double.parseDouble(String.valueOf(edtRPM.getText())));
                ws.setCritical(Double.parseDouble(String.valueOf(edtCritical.getText())));
                ws.setCriticaldemage(Double.parseDouble(String.valueOf(edtCriticalDemage.getText())));
                ws.setHeadshot(Double.parseDouble(String.valueOf(edtHeadshot.getText())));
                ws.setHeadshotdemage(Double.parseDouble(String.valueOf(edtHeadshotDemage.getText())));
                ws.setElitedemage(Double.parseDouble(String.valueOf(edtEliteDemage.getText())));
                ws.setShelddemage(Double.parseDouble(String.valueOf(edtSheldDemage.getText())));
                ws.setHealthdemage(Double.parseDouble(String.valueOf(edtHealthDemage.getText())));
                ws.setReloadtime(Double.parseDouble(String.valueOf(edtReload.getText())));
                ws.setAmmo(Double.parseDouble(String.valueOf(edtAmmo.getText())));

                Intent intent = new Intent(getActivity(), DemageSimulationActivity.class);

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
            }
        });

        btnDemageSimul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (String.valueOf(edtWeaponDemage.getText()).equals("") || String.valueOf(edtRPM.getText()).equals("") || String.valueOf(edtAmmo.getText()).equals("")) {
                    Toast.makeText(getActivity(), "무기 데미지, RPM, 탄창이 입력해야합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    int temp_demage = Integer.parseInt(String.valueOf(edtWeaponDemage.getText()));
                    int temp_rpm = Integer.parseInt(String.valueOf(edtRPM.getText()));
                    int temp_ammo = Integer.parseInt(String.valueOf(edtAmmo.getText()));
                    if (temp_demage <= 0 || temp_rpm <= 0 || temp_ammo <= 0) {
                        Toast.makeText(getActivity(), "무기 데미지, RPM, 탄창을 최소 0 이상 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        View dialogView = getLayoutInflater().inflate(R.layout.dialoglayout, null);
                        final EditText edtSheld = dialogView.findViewById(R.id.edtSheld);
                        final EditText edtHealth = dialogView.findViewById(R.id.edtHealth);
                        final CheckBox chkElite = dialogView.findViewById(R.id.chkElite);
                        final CheckBox chkPVP = dialogView.findViewById(R.id.chkPVP);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setView(dialogView);

                        chkPVP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (chkPVP.isChecked()) chkElite.setEnabled(false);
                                else chkElite.setEnabled(true);
                            }
                        });

                        builder.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean elite_true, pvp_true;
                                if (chkElite.isChecked() && !chkPVP.isChecked()) elite_true = true;
                                else elite_true = false;
                                if (chkPVP.isChecked()) pvp_true = true;
                                else pvp_true = false;

                                if (String.valueOf(edtSheld.getText()).equals("") || String.valueOf(edtHealth.getText()).equals("")) {
                                    Toast.makeText(getActivity(), "방어구, 체력 모두 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    int temp_sheld = Integer.parseInt(String.valueOf(edtSheld.getText()));
                                    int temp_health = Integer.parseInt(String.valueOf(edtHealth.getText()));
                                    if (temp_sheld <= 0 || temp_health <= 0) {
                                        Toast.makeText(getActivity(), "방어구, 체력이 최소 0 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        DemageSimulThread ws = new DemageSimulThread();
                                        ws.setWeapondemage(Double.parseDouble(String.valueOf(edtWeaponDemage.getText())));
                                        ws.setRPM(Double.parseDouble(String.valueOf(edtRPM.getText())));
                                        if (!String.valueOf(edtCritical.getText()).equals("")) ws.setCritical(Double.parseDouble(String.valueOf(edtCritical.getText())));
                                        else ws.setCritical(0);
                                        if (!String.valueOf(edtCriticalDemage.getText()).equals("")) ws.setCriticaldemage(Double.parseDouble(String.valueOf(edtCriticalDemage.getText())));
                                        else ws.setCriticaldemage(0);
                                        if (!String.valueOf(edtHeadshot.getText()).equals("")) ws.setHeadshot(Double.parseDouble(String.valueOf(edtHeadshot.getText())));
                                        else ws.setHeadshot(0);
                                        if (!String.valueOf(edtHeadshotDemage.getText()).equals("")) ws.setHeadshotdemage(Double.parseDouble(String.valueOf(edtHeadshotDemage.getText())));
                                        else ws.setHeadshotdemage(0);
                                        if (!String.valueOf(edtEliteDemage.getText()).equals("")) ws.setElitedemage(Double.parseDouble(String.valueOf(edtEliteDemage.getText())));
                                        else ws.setElitedemage(0);
                                        if (!String.valueOf(edtSheldDemage.getText()).equals("")) ws.setShelddemage(Double.parseDouble(String.valueOf(edtSheldDemage.getText())));
                                        else ws.setShelddemage(0);
                                        if (!String.valueOf(edtHeadshotDemage.getText()).equals("")) ws.setHealthdemage(Double.parseDouble(String.valueOf(edtHealthDemage.getText())));
                                        else ws.setHeadshotdemage(0);
                                        if (!String.valueOf(edtReload.getText()).equals("")) ws.setReloadtime(Double.parseDouble(String.valueOf(edtReload.getText())));
                                        else ws.setReloadtime(0);
                                        ws.setAmmo(Double.parseDouble(String.valueOf(edtAmmo.getText())));
                                        ws.setSheld(Integer.parseInt(String.valueOf(edtSheld.getText())));
                                        ws.setHealth(Integer.parseInt(String.valueOf(edtHealth.getText())));
                                        ws.setPVP_true(pvp_true);
                                        ws.setElite_true(elite_true);

                                        Intent intent = new Intent(getActivity(), SimulActivity.class);

                                        intent.putExtra("thread", ws);

                                        startActivity(intent);
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