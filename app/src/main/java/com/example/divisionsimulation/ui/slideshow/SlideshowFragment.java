package com.example.divisionsimulation.ui.slideshow;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.ui.gallery.WeaponListActivity;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

public class SlideshowFragment extends Fragment {
    
    private SheldDbAdapter sheldAdapter;
    private LinearLayout mainLayout;
    
    private int[] images = new int[28];

    private SlideshowViewModel slideshowViewModel;
    private Button[] btnSheldoption = new Button[2];
    private int temp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        
        mainLayout = root.findViewById(R.id.mainLayout);

        for (int i = 0; i < images.length; i++) images[i] = getActivity().getResources().getIdentifier("eq"+(i+1), "drawable", getActivity().getPackageName());
        
        sheldAdapter = new SheldDbAdapter(getActivity());
        copyExcelDataToDatabase(images);

        sheldAdapter.open();

        Cursor cursor;
        View view;
        cursor = sheldAdapter.fetchAllWeapon();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            String first = cursor.getString(2);
            String second = cursor.getString(3);
            String third = cursor.getString(4);
            String core = cursor.getString(5);
            String sub = cursor.getString(6);
            String type = cursor.getString(7);
            String vest = cursor.getString(8);
            String backpack = cursor.getString(9);
            int image = Integer.parseInt(cursor.getString(10));
            cursor.moveToNext();

            view = getLayoutInflater().inflate(R.layout.shelditem, null);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.bottomMargin = 50;
            view.setLayoutParams(param);

            final TextView txtName = view.findViewById(R.id.txtName);
            final View viewLine = view.findViewById(R.id.viewLine);
            final ImageView imgIcon = view.findViewById(R.id.imgIcon);
            final TextView txtFirstInfo = view.findViewById(R.id.txtFirstInfo);
            final TextView txtSecondInfo = view.findViewById(R.id.txtSecondInfo);
            final TextView txtThirdInfo = view.findViewById(R.id.txtThirdInfo);
            final TextView txtFirst = view.findViewById(R.id.txtFirst);
            final TextView txtSecond = view.findViewById(R.id.txtSecond);
            final TextView txtThird = view.findViewById(R.id.txtThird);
            final TextView txtCore = view.findViewById(R.id.txtCore);
            final TextView txtSub = view.findViewById(R.id.txtSub);
            final LinearLayout layoutGearOptions = view.findViewById(R.id.layoutGearOptions);

            if (type.equals("1")) {
                viewLine.setBackgroundColor(Color.parseColor("#04ff8c"));

                txtName.setTextColor(Color.parseColor("#04ff8c"));

                final TextView txtVest = view.findViewById(R.id.txtVest);
                final TextView txtBackpack = view.findViewById(R.id.txtBackpack);

                txtFirstInfo.setText("2 세트 +:");
                txtSecondInfo.setText("3 세트 +:");
                txtThirdInfo.setText("4 세트 +:");

                layoutGearOptions.setVisibility(View.VISIBLE);

                txtVest.setText(vest);
                txtBackpack.setText(backpack);
            } else {
                viewLine.setBackgroundColor(Color.parseColor("#fdae13"));

                txtName.setTextColor(Color.parseColor("#fdae13"));

                layoutGearOptions.setVisibility(View.GONE);

                txtFirstInfo.setText("1 세트 +:");
                txtSecondInfo.setText("2 세트 +:");
                txtThirdInfo.setText("3 세트 +:");
            }

            imgIcon.setImageResource(image);
            txtFirst.setText(first);
            txtSecond.setText(second);
            txtThird.setText(third);
            txtCore.setText(core);
            txtSub.setText(sub);
            txtName.setText(name);

            mainLayout.addView(view);
        }

        sheldAdapter.close();
        
        for (int i = 0; i < btnSheldoption.length; i++) {
            temp = getResources().getIdentifier("btnSheldoption"+(i+1), "id", getActivity().getPackageName());
            btnSheldoption[i] = root.findViewById(temp);
        }
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

        return root;
    }

    private void copyExcelDataToDatabase(int[] images) {
        Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

        Workbook workbook = null;
        Sheet sheet = null;

        try {
            InputStream is = getActivity().getBaseContext().getResources().getAssets().open("sheld.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = 9;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(1).length - 1;

                    sheldAdapter.open();
                    sheldAdapter.databaseReset();

                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String first = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String second = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                        String third = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                        String core = sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                        String sub = sheet.getCell(nColumnStartIndex+5, nRow).getContents();
                        String type = sheet.getCell(nColumnStartIndex+6, nRow).getContents();
                        String vest = sheet.getCell(nColumnStartIndex+7, nRow).getContents();
                        String backpack = sheet.getCell(nColumnStartIndex+8, nRow).getContents();
                        String image = Integer.toString(images[nRow]);

                        sheldAdapter.createWeapon(name, first, second, third, core, sub, type, vest, backpack, image);
                    }

                    sheldAdapter.close();
                    //Toast.makeText(getApplicationContext(), "불러오기 성공", Toast.LENGTH_SHORT).show();
                } else System.out.println("Sheet is null!!!");
            } else System.out.println("WorkBook is null!!!");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "불러오기 오류", Toast.LENGTH_SHORT).show();
        } finally {
            if (workbook != null) workbook.close();
        }
    }
}