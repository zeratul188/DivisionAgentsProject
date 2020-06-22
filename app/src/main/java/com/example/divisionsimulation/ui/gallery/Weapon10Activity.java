package com.example.divisionsimulation.ui.gallery;

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

public class Weapon10Activity extends AppCompatActivity {
    
    private LinearLayout mainLayout;
    private WeaponTalentDbAdapter talentAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weapon10layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("특수 효과 정보");

        mainLayout = findViewById(R.id.mainLayout);

        this.talentAdapter = new WeaponTalentDbAdapter(this);
        copyExcelDataToDatabase();

        try {
            talentAdapter.open();

            Cursor cursor;
            View view;
            cursor = talentAdapter.fetchAllWeapon();
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String name = cursor.getString(1);
                String content = cursor.getString(2);
                String[] types = new String[7];
                for (int i = 0; i < types.length; i++) {
                    types[i] = cursor.getString(i+3);
                }
                cursor.moveToNext();

                view = getLayoutInflater().inflate(R.layout.weapontalentitem, null);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param.bottomMargin = 50;
                view.setLayoutParams(param);

                final TextView txtName = view.findViewById(R.id.txtName);
                final TextView txtContent = view.findViewById(R.id.txtContent);
                final ImageView[] imgType = new ImageView[7];

                int address;
                for (int i = 0; i < imgType.length; i++) {
                    address = view.getResources().getIdentifier("imgType"+(i+1), "id", this.getPackageName());
                    imgType[i] = view.findViewById(address);
                    if (!types[i].equals("1")) imgType[i].setVisibility(View.INVISIBLE);
                    else imgType[i].setVisibility(View.VISIBLE);
                }

                txtName.setText(name);
                txtContent.setText(content);

                mainLayout.addView(view);
            }

            talentAdapter.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "불러오는데 오류 발생", Toast.LENGTH_SHORT).show();
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
            InputStream is = getBaseContext().getResources().getAssets().open("weapontalent.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = 9;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(9).length - 1;

                    talentAdapter.open();
                    talentAdapter.databaseReset();

                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String content = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String ar = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                        String sr = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                        String br = sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                        String rf = sheet.getCell(nColumnStartIndex+5, nRow).getContents();
                        String mmg = sheet.getCell(nColumnStartIndex+6, nRow).getContents();
                        String sg = sheet.getCell(nColumnStartIndex+7, nRow).getContents();
                        String pt = sheet.getCell(nColumnStartIndex+8, nRow).getContents();

                        talentAdapter.createWeapon(name, content, ar, sr, br, rf, mmg, sg, pt);
                    }

                    talentAdapter.close();
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
