package com.example.divisionsimulation.ui.tools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.InventoryDBAdapter;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    private InventoryDBAdapter inventoryDBAdapter;

    private TextView txtInventory;
    private Button btnWeapon, btnSubWeapon, btnMask, btnBackpack, btnVest, btnGlove, btnHolster, btnKneeped, btnReset;

    private int weapons = 0;

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
        btnReset = root.findViewById(R.id.btnReset);

        inventoryDBAdapter = new InventoryDBAdapter(getActivity());

        refresh();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setTitle("인벤토리 초기화");
                inventoryDBAdapter.open();
                builder.setMessage("인벤토리를 초기화하시겠습니까? ("+inventoryDBAdapter.getCount()+"/300)");
                inventoryDBAdapter.close();
                builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inventoryDBAdapter.open();
                        inventoryDBAdapter.deleteAllData();
                        inventoryDBAdapter.close();
                        Toast.makeText(getActivity(), "인벤토리가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                });
                builder.setNegativeButton("취소", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
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
        txtInventory.setText("("+inventoryDBAdapter.getCount()+"/300)");
        weapons += inventoryDBAdapter.getTypeCount("돌격소총");
        weapons += inventoryDBAdapter.getTypeCount("소총");
        weapons += inventoryDBAdapter.getTypeCount("경기관총");
        weapons += inventoryDBAdapter.getTypeCount("기관단총");
        weapons += inventoryDBAdapter.getTypeCount("산탄총");
        weapons += inventoryDBAdapter.getTypeCount("지정사수소총");
        btnWeapon.setText("무기 ("+weapons+")");
        btnSubWeapon.setText("보조 무기 ("+inventoryDBAdapter.getTypeCount("권총")+")");
        btnMask.setText("마스크 ("+inventoryDBAdapter.getTypeCount("마스크")+")");
        btnBackpack.setText("백팩 ("+inventoryDBAdapter.getTypeCount("백팩")+")");
        btnVest.setText("조끼 ("+inventoryDBAdapter.getTypeCount("조끼")+")");
        btnGlove.setText("장갑 ("+inventoryDBAdapter.getTypeCount("장갑")+")");
        btnHolster.setText("권총집 ("+inventoryDBAdapter.getTypeCount("권총집")+")");
        btnKneeped.setText("무릎보호대 ("+inventoryDBAdapter.getTypeCount("무릎보호대")+")");
        inventoryDBAdapter.close();
    }
}