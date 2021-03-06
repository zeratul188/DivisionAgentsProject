package com.example.divisionsimulation.ui.gallery;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

public class Weapon9Activity extends AppCompatActivity {
    
    private NamedWeaponDbAdapter adapterDB;
    private LinearLayout mainLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weapon9layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("네임드 무기 정보");
        
        mainLayout = findViewById(R.id.mainLayout);
        
        this.adapterDB = new NamedWeaponDbAdapter(this);
        copyExcelDataToDatabase();

        try {
            adapterDB.open();

            Cursor cursor;
            View view;
            cursor = adapterDB.fetchAllWeapon();
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String name = cursor.getString(1);
                String weapon = cursor.getString(2);
                String talent = cursor.getString(3);
                String location = cursor.getString(4);
                String content = cursor.getString(5);
                cursor.moveToNext();

                view = getLayoutInflater().inflate(R.layout.nameditem, null);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param.bottomMargin = 50;
                view.setLayoutParams(param);

                final TextView txtName = view.findViewById(R.id.txtName);
                final TextView txtWeapon = view.findViewById(R.id.txtWeapon);
                final TextView txtLocation = view.findViewById(R.id.txtLocation);
                final TextView txtTalent = view.findViewById(R.id.txtTalent);
                final TextView txtContent = view.findViewById(R.id.txtContent);

                txtName.setText(name);
                txtWeapon.setText(weapon);
                txtLocation.setText(location);
                txtTalent.setText(talent);
                if (content.equals("-")) txtContent.setVisibility(View.GONE);
                else txtContent.setText(content);

                mainLayout.addView(view);
            }

            adapterDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            InputStream is = getBaseContext().getResources().getAssets().open("namedweapon.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = 5;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(5).length - 1;

                    adapterDB.open();
                    adapterDB.databaseReset();

                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String weapon = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String talent = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                        String location = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                        String content = sheet.getCell(nColumnStartIndex+4, nRow).getContents();

                        adapterDB.createWeapon(name, weapon, talent, location, content);
                    }

                    adapterDB.close();
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
