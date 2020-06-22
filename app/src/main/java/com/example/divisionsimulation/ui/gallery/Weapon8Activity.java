package com.example.divisionsimulation.ui.gallery;

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

public class Weapon8Activity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private WeaponExoticDbAdapter exoticAdapter;

    private int[] img_resource = {R.drawable.wp1custom, R.drawable.wp2custom, R.drawable.wp3custom, R.drawable.wp4custom, R.drawable.wp5custom, R.drawable.wp6custom, R.drawable.wp7custom};
    private String[] types = {"돌격소총", "소총", "지정사수소총", "기관단총", "경기관총", "산탄총", "권총"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weapon8layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("엑조틱 목록");
        
        mainLayout = findViewById(R.id.mainLayout);
        
        exoticAdapter = new WeaponExoticDbAdapter(this);
        copyExcelDataToDatabase();

        exoticAdapter.open();

        Cursor cursor;
        View view;
        cursor = exoticAdapter.fetchNeedWeapon();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            String type = cursor.getString(1);
            cursor.moveToNext();

            view = getLayoutInflater().inflate(R.layout.weaponexoticitem, null);
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
                    Intent intent = new Intent(mContext, WeaponExoticListActivity.class); //intent에 현재 화면에 Weapon1Activity를 새로운 화면에 출력시키는 변수다.
                    intent.putExtra("Name", String.valueOf(txtName.getText()));
                    startActivity(intent); //intent 액티비티를 시작시킨다.
                }
            });

            mainLayout.addView(view);
        }

        exoticAdapter.close();

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

    private int setImageResource(String type) {
        for (int i = 0; i < types.length; i++) if (type.equals(types[i])) return img_resource[i];
        return img_resource[0];
    }

    private void copyExcelDataToDatabase() {
        Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

        Workbook workbook = null;
        Sheet sheet = null;

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("weaponexotic.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = 12;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(1).length - 1;

                    exoticAdapter.open();
                    exoticAdapter.databaseReset();

                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String option = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String rpm = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                        String reloadtime = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                        String mag = sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                        String firemethod = sheet.getCell(nColumnStartIndex+5, nRow).getContents();
                        String item = sheet.getCell(nColumnStartIndex+6, nRow).getContents();
                        String talent = sheet.getCell(nColumnStartIndex+7, nRow).getContents();
                        String talentcontent = sheet.getCell(nColumnStartIndex+8, nRow).getContents();
                        String droped = sheet.getCell(nColumnStartIndex+9, nRow).getContents();
                        String content = sheet.getCell(nColumnStartIndex+10, nRow).getContents();
                        String type = sheet.getCell(nColumnStartIndex+11, nRow).getContents();

                        exoticAdapter.createWeapon(name, option, rpm, reloadtime, mag, firemethod, item, talent, talentcontent, droped, content, type);
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
