package com.example.divisionsimulation.ui.slideshow;

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

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    //private Button[] btnSheldlist = new Button[6];
    private Button[] btnSheldoption = new Button[2];
    private int temp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        /*final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        /*for (int i = 0; i < btnSheldlist.length; i++) {
            temp = getResources().getIdentifier("btnSheldlist"+(i+1), "id", getActivity().getPackageName());
            btnSheldlist[i] = root.findViewById(temp);
        }*/
        for (int i = 0; i < btnSheldoption.length; i++) {
            temp = getResources().getIdentifier("btnSheldoption"+(i+1), "id", getActivity().getPackageName());
            btnSheldoption[i] = root.findViewById(temp);
        }

        /*btnSheldlist[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldList1Activity.class);
                startActivity(intent);
            }
        });
        btnSheldlist[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldList2Activity.class);
                startActivity(intent);
            }
        });
        btnSheldlist[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldList3Activity.class);
                startActivity(intent);
            }
        });
        btnSheldlist[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldList4Activity.class);
                startActivity(intent);
            }
        });
        btnSheldlist[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldList5Activity.class);
                startActivity(intent);
            }
        });
        btnSheldlist[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldList6Activity.class);
                startActivity(intent);
            }
        });*/
        btnSheldoption[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldOption2Activity.class);
                startActivity(intent);
            }
        });
        btnSheldoption[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldOption4Activity.class);
                startActivity(intent);
            }
        });
        /*btnSheldoption[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldOption3Activity.class);
                startActivity(intent);
            }
        });
        btnSheldoption[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldOption4Activity.class);
                startActivity(intent);
            }
        });
        btnSheldoption[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SheldOption5Activity.class);
                startActivity(intent);
            }
        });*/

        return root;
    }
}