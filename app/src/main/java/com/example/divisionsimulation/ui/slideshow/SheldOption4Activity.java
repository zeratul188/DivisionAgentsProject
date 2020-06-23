package com.example.divisionsimulation.ui.slideshow;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

public class SheldOption4Activity extends AppCompatActivity {

    private ExoticSheldDbAdapter exoticAdapter;
    private LinearLayout mainLayout;

    private int[] arrItem = {R.drawable.mask, R.drawable.vests, R.drawable.holsters, R.drawable.backpack, R.drawable.gloves, R.drawable.kneepeds};
    private String[] arrNames = {"마스크", "조끼", "권총집", "백팩", "장갑", "무릎 보호대"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheldoptionlayout4);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("특수 장비");

        mainLayout = findViewById(R.id.mainLayout);

        this.exoticAdapter = new ExoticSheldDbAdapter(this);
        copyExcelDataToDatabase();

        exoticAdapter.open();

        Cursor cursor;
        View view;
        cursor = exoticAdapter.fetchAllWeapon();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            final long number = cursor.getLong(0);
            String name = cursor.getString(1);
            String type = cursor.getString(10);
            cursor.moveToNext();

            view = getLayoutInflater().inflate(R.layout.sheldexoticitem, null);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 160);
            param.bottomMargin = 20;
            view.setLayoutParams(param);

            final TextView txtName = view.findViewById(R.id.txtName);
            final ImageView imgWeaponType = view.findViewById(R.id.imgWeaponType);

            imgWeaponType.setImageResource(setImageResource(type));
            txtName.setText(name);

            final Context mContext = this;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SheldExoticListActivity.class); //intent에 현재 화면에 Weapon1Activity를 새로운 화면에 출력시키는 변수다.
                    intent.putExtra("number", number);
                    startActivity(intent); //intent 액티비티를 시작시킨다.
                }
            });

            mainLayout.addView(view);
        }

        exoticAdapter.close();
    }
    
    private int setImageResource(String type) {
        for (int i = 0; i < arrNames.length; i++) if (type.equals(arrNames[i])) return arrItem[i];
        return arrItem[0];
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyExcelDataToDatabase() {
        Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

        Workbook workbook = null;
        Sheet sheet = null;

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("exoticsheld.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = 10;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(1).length - 1;

                    exoticAdapter.open();
                    exoticAdapter.databaseReset();

                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String sheldth = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String first = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                        String second = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                        String third = sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                        String talent = sheet.getCell(nColumnStartIndex+5, nRow).getContents();
                        String talent_content = sheet.getCell(nColumnStartIndex+6, nRow).getContents();
                        String location = sheet.getCell(nColumnStartIndex+7, nRow).getContents();
                        String content = sheet.getCell(nColumnStartIndex+8, nRow).getContents();
                        String type = sheet.getCell(nColumnStartIndex+9, nRow).getContents();

                        exoticAdapter.createWeapon(name, sheldth, first, second, third, talent, talent_content, location, content, type);
                    }

                    exoticAdapter.close();
                    //Toast.makeText(getApplicationContext(), "불러오기 성공", Toast.LENGTH_SHORT).show();
                } else System.out.println("Sheet is null!!!");
            } else System.out.println("WorkBook is null!!!");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "불러오기 오류", Toast.LENGTH_SHORT).show();
        } finally {
            if (workbook != null) workbook.close();
        }
    }
}
