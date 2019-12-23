package com.example.divisionsimulation.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

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

        WeaponSimulation ws = new WeaponSimulation();
        ws.setWeapondemage(Double.parseDouble(String.valueOf(edtWeaponDemage.getText())));
        ws.setRPM(Double.parseDouble(String.valueOf(edtRPM.getText())));
        ws.setCritical(Double.parseDouble(String.valueOf(edtCritical.getText())));
        

        return root;
    }
}