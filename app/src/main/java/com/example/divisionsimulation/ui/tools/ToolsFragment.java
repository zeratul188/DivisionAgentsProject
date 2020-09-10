package com.example.divisionsimulation.ui.tools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.MaterialDbAdapter;
import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.InventoryDBAdapter;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    private InventoryDBAdapter inventoryDBAdapter;
    private MaterialDbAdapter materialDbAdapter;

    private TextView txtInventory;
    private Button btnWeapon, btnSubWeapon, btnMask, btnBackpack, btnVest, btnGlove, btnHolster, btnKneeped, btnMaterialList;

    private Intent intent;
    private int weapons = 0;

    private AlertDialog alertDialog;

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

        txtInventory = root.findViewById(R.id.txtInventory);
        btnWeapon = root.findViewById(R.id.btnWeapon);
        btnSubWeapon = root.findViewById(R.id.btnSubWeapon);
        btnMask = root.findViewById(R.id.btnMask);
        btnBackpack = root.findViewById(R.id.btnBackpack);
        btnVest = root.findViewById(R.id.btnVest);
        btnGlove = root.findViewById(R.id.btnGlove);
        btnHolster = root.findViewById(R.id.btnHolster);
        btnKneeped = root.findViewById(R.id.btnKneeped);
        btnMaterialList = root.findViewById(R.id.btnMaterialList);

        inventoryDBAdapter = new InventoryDBAdapter(getActivity());
        materialDbAdapter = new MaterialDbAdapter(getActivity());

        refresh();

        intent = new Intent(getActivity(), InventoryActivity.class);

        btnWeapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "무기");
                startActivity(intent);
            }
        });

        btnSubWeapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "권총");
                startActivity(intent);
            }
        });

        btnMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "마스크");
                startActivity(intent);
            }
        });

        btnBackpack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "백팩");
                startActivity(intent);
            }
        });

        btnVest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "조끼");
                startActivity(intent);
            }
        });

        btnGlove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "장갑");
                startActivity(intent);
            }
        });

        btnHolster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "권총집");
                startActivity(intent);
            }
        });

        btnKneeped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "무릎보호대");
                startActivity(intent);
            }
        });
        
        btnMaterialList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.materialtoolslayout, null); //다이얼로그에 추가할 뷰 생성
                Button btnMaterialExit = dialogView.findViewById(R.id.btnMaterialExit); //팝업창을 닫을 버튼

                btnMaterialExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //버튼을 누르면 작동
                        alertDialog.dismiss(); //다이얼로그를 닫는다.
                    }
                });

                Cursor cursor;
                materialDbAdapter.open();
                final int[] material = new int[materialDbAdapter.getCount()];
                final String[] material_name = new String[materialDbAdapter.getCount()];
                final int[] material_max = new int[materialDbAdapter.getCount()];
                cursor = materialDbAdapter.fetchAllMaterial();
                cursor.moveToFirst();
                int count = 0;
                while (!cursor.isAfterLast()) {
                    material_name[count] = cursor.getString(1);
                    material[count] = cursor.getInt(2);
                    material_max[count] = cursor.getInt(3);
                    cursor.moveToNext();
                    count++;
                }
                materialDbAdapter.close();

                TextView[] txtNormal = new TextView[2];
                TextView[] txtRare = new TextView[3];
                TextView[] txtEpic = new TextView[3];
                TextView txtDark = dialogView.findViewById(R.id.txtDark);
                ProgressBar progressDark = dialogView.findViewById(R.id.progressDark);
                TextView txtExotic = dialogView.findViewById(R.id.txtExotic);
                ProgressBar progressExotic = dialogView.findViewById(R.id.progressExotic);

                ProgressBar[] progressNormal = new ProgressBar[2];
                ProgressBar[] progressRare = new ProgressBar[3];
                ProgressBar[] progressEpic = new ProgressBar[3];

                int resource;
                for (int i = 0; i < txtNormal.length; i++) {
                    resource = dialogView.getResources().getIdentifier("txtNormal"+(i+1), "id", getActivity().getPackageName());
                    txtNormal[i] = dialogView.findViewById(resource);
                    resource = dialogView.getResources().getIdentifier("progressNormal"+(i+1), "id", getActivity().getPackageName());
                    progressNormal[i] = dialogView.findViewById(resource);
                    progressNormal[i].setMax(2000);
                    progressNormal[i].setProgress(material[i]);
                    txtNormal[i].setText(Integer.toString(material[i]));
                }
                for (int i = 0; i < txtRare.length; i++) {
                    resource = dialogView.getResources().getIdentifier("txtRare"+(i+1), "id", getActivity().getPackageName());
                    txtRare[i] = dialogView.findViewById(resource);
                    resource = dialogView.getResources().getIdentifier("txtEpic"+(i+1), "id", getActivity().getPackageName());
                    txtEpic[i] = dialogView.findViewById(resource);
                    resource = dialogView.getResources().getIdentifier("progressRare"+(i+1), "id", getActivity().getPackageName());
                    progressRare[i] = dialogView.findViewById(resource);
                    resource = dialogView.getResources().getIdentifier("progressEpic"+(i+1), "id", getActivity().getPackageName());
                    progressEpic[i] = dialogView.findViewById(resource);
                    progressRare[i].setMax(1500);
                    progressEpic[i].setMax(1500);
                    progressRare[i].setProgress(material[i+2]);
                    progressEpic[i].setProgress(material[i+5]);
                    txtRare[i].setText(Integer.toString(material[i+2]));
                    txtEpic[i].setText(Integer.toString(material[i+5]));
                }
                txtDark.setText(Integer.toString(material[8]));
                progressDark.setMax(300);
                progressDark.setProgress(material[8]);

                txtExotic.setText(Integer.toString(material[9]));
                progressExotic.setMax(20);
                progressExotic.setProgress(material[9]);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private void refresh() {
        weapons = 0;
        inventoryDBAdapter.open();
        txtInventory.setText(inventoryDBAdapter.getCount()+"/300");
        weapons += inventoryDBAdapter.getTypeCount("돌격소총");
        weapons += inventoryDBAdapter.getTypeCount("소총");
        weapons += inventoryDBAdapter.getTypeCount("경기관총");
        weapons += inventoryDBAdapter.getTypeCount("기관단총");
        weapons += inventoryDBAdapter.getTypeCount("산탄총");
        weapons += inventoryDBAdapter.getTypeCount("지정사수소총");
        btnWeapon.setText("무기 \n("+weapons+")");
        btnSubWeapon.setText("보조 무기 \n("+inventoryDBAdapter.getTypeCount("권총")+")");
        btnMask.setText("마스크 \n("+inventoryDBAdapter.getTypeCount("마스크")+")");
        btnBackpack.setText("백팩 \n("+inventoryDBAdapter.getTypeCount("백팩")+")");
        btnVest.setText("조끼 \n("+inventoryDBAdapter.getTypeCount("조끼")+")");
        btnGlove.setText("장갑 \n("+inventoryDBAdapter.getTypeCount("장갑")+")");
        btnHolster.setText("권총집 \n("+inventoryDBAdapter.getTypeCount("권총집")+")");
        btnKneeped.setText("무릎보호대 \n("+inventoryDBAdapter.getTypeCount("무릎보호대")+")");
        inventoryDBAdapter.close();
    }
}