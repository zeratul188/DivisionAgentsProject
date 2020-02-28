package com.example.divisionsimulation.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.ui.home.DemageSimulationActivity;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private Button[] btnWeapon = new Button[10]; //각 메뉴들의 버튼 배열로 10개 정리

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        int temp;
        for (int i = 0; i < btnWeapon.length; i++) {
            temp = root.getResources().getIdentifier("btnWeapon"+(i+1), "id", getActivity().getPackageName()); //btnWeapon? 아이디를 temp 변수에 저장
            btnWeapon[i] = (Button)root.findViewById(temp); //btnWeapon에 배열마다 아이디가 temp인 뷰를 찾아서 넣는다.
        }

        btnWeapon[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //각 버튼마다 다른 화면을 출력하기 위해서 이벤트 처리
                Intent intent = new Intent(getActivity(), Weapon1Activity.class); //intent에 현재 화면에 Weapon1Activity를 새로운 화면에 출력시키는 변수다.
                startActivity(intent); //intent 액티비티를 시작시킨다.
            }
        });
        btnWeapon[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon2Activity.class);
                startActivity(intent);
            }
        });
        btnWeapon[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon3Activity.class);
                startActivity(intent);
            }
        });
        btnWeapon[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon4Activity.class);
                startActivity(intent);
            }
        });
        btnWeapon[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon5Activity.class);
                startActivity(intent);
            }
        });
        btnWeapon[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon6Activity.class);
                startActivity(intent);
            }
        });
        btnWeapon[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon7Activity.class);
                startActivity(intent);
            }
        });
        btnWeapon[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon8Activity.class);
                startActivity(intent);
            }
        });
        btnWeapon[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon9Activity.class);
                startActivity(intent);
            }
        });
        btnWeapon[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일
                Intent intent = new Intent(getActivity(), Weapon10Activity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}