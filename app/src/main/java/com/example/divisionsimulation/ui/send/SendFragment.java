package com.example.divisionsimulation.ui.send;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;

    private ListView listWeapon, listSheld, listExotic;
    private RadioGroup rgWeapon, rgSheld, rgType;
    //private RadioButton rdoWeaponType, rdoSheldType, rdoExoticType;
    private RadioButton[] rdoWeapon = new RadioButton[7];
    private RadioButton[] rdoSheld = new RadioButton[6];
    private LinearLayout layoutWeapon, layoutSheld, layoutExotic;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        /*final TextView textView = root.findViewById(R.id.text_send);
        sendViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        listWeapon = root.findViewById(R.id.listWeapon);
        listSheld = root.findViewById(R.id.listSheld);
        listExotic = root.findViewById(R.id.listExotic);
        rgType = root.findViewById(R.id.rgType);
        rgWeapon = root.findViewById(R.id.rgWeapon);
        rgSheld = root.findViewById(R.id.rgSheld);
        layoutWeapon = root.findViewById(R.id.layoutWeapon);
        layoutSheld = root.findViewById(R.id.layoutSheld);
        layoutExotic = root.findViewById(R.id.layoutExotic);

        int resource;
        for (int i = 0; i < rdoWeapon.length; i++) {
            resource = root.getResources().getIdentifier("rdoWeapon"+(i+1), "id", getActivity().getPackageName());
            rdoWeapon[i] = root.findViewById(resource);
        }
        for (int i = 0; i < rdoSheld.length; i++) {
            resource = root.getResources().getIdentifier("rdoSheld"+(i+1), "id", getActivity().getPackageName());
            rdoSheld[i] = root.findViewById(resource);
        }

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdoWeaponType:
                        layoutWeapon.setVisibility(View.VISIBLE);
                        layoutSheld.setVisibility(View.GONE);
                        layoutExotic.setVisibility(View.GONE);
                        break;
                    case R.id.rdoSheldType:
                        layoutWeapon.setVisibility(View.GONE);
                        layoutSheld.setVisibility(View.VISIBLE);
                        layoutExotic.setVisibility(View.GONE);
                        break;
                    case R.id.rdoExoticType:
                        layoutWeapon.setVisibility(View.GONE);
                        layoutSheld.setVisibility(View.GONE);
                        layoutExotic.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        rgWeapon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < rdoWeapon.length; i++) {
                    if (rdoWeapon[i].isChecked()) rdoWeapon[i].setTextColor(Color.parseColor("#FF6337"));
                    else rdoWeapon[i].setTextColor(Color.parseColor("#F0F0F0"));
                }
            }
        });

        rgSheld.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < rdoSheld.length; i++) {
                    if (rdoSheld[i].isChecked()) rdoSheld[i].setTextColor(Color.parseColor("#FF6337"));
                    else rdoSheld[i].setTextColor(Color.parseColor("#F0F0F0"));
                }
            }
        });

        return root;
    }
}