package com.example.divisionsimulation.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;

    private Button[] btnFaction = new Button[4];
    private Button[] btnMenu = new Button[1];

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

        int temp;
        for (int i = 0; i < btnFaction.length; i++) {
            temp = root.getResources().getIdentifier("btnFaction"+(i+1), "id", getActivity().getPackageName());
            btnFaction[i] = root.findViewById(temp);
        }
        for (int i = 0; i < btnMenu.length; i++) {
            temp = root.getResources().getIdentifier("btnMenu"+(i+1), "id", getActivity().getPackageName());
            btnMenu[i] = root.findViewById(temp);
        }

        btnFaction[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BlacktuskListActivity.class);
                startActivity(intent);
            }
        });
        btnFaction[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HyenasListActivity.class);
                startActivity(intent);
            }
        });
        btnFaction[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TruesonsListActivity.class);
                startActivity(intent);
            }
        });
        btnFaction[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OutcastListActivity.class);
                startActivity(intent);
            }
        });

        btnMenu[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), KeenerActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}