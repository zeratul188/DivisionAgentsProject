package com.example.divisionsimulation.ui.tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.ui.gallery.Weapon1Activity;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    private Button[] btnList = new Button[8];

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        //final TextView textView = root.findViewById(R.id.text_tools);
        /*toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        int temp;
        for (int i = 0; i < btnList.length; i++) {
            temp = root.getResources().getIdentifier("btnList"+(i+1), "id", getActivity().getPackageName());
            btnList[i] = (Button)root.findViewById(temp);
        }

        btnList[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), List1Activity.class);
                startActivity(intent);
            }
        });
        btnList[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), List2Activity.class);
                startActivity(intent);
            }
        });
        btnList[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), List3Activity.class);
                startActivity(intent);
            }
        });
        btnList[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), List4Activity.class);
                startActivity(intent);
            }
        });
        btnList[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), List5Activity.class);
                startActivity(intent);
            }
        });
        btnList[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), List6Activity.class);
                startActivity(intent);
            }
        });
        btnList[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), List7Activity.class);
                startActivity(intent);
            }
        });
        btnList[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), List8Activity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}