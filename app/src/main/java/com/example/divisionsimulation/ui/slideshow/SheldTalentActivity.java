package com.example.divisionsimulation.ui.slideshow;

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

public class SheldTalentActivity extends AppCompatActivity {
    
    private LinearLayout mainLayout;
    private SheldTalentDbAdapter talentAdapter;

    private int[] arrItem = {R.drawable.vests, R.drawable.backpack};
    private String[] arrNames = {"조끼", "백팩"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheldoptionlayout2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("특수 효과");

        mainLayout = findViewById(R.id.mainLayout);

        this.talentAdapter = new SheldTalentDbAdapter(this);
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
                String type = cursor.getString(3);
                cursor.moveToNext();

                view = getLayoutInflater().inflate(R.layout.sheldtalentitem, null);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param.bottomMargin = 50;
                view.setLayoutParams(param);

                final ImageView imgType = view.findViewById(R.id.imgType);
                final TextView txtName = view.findViewById(R.id.txtName);
                final TextView txtContent = view.findViewById(R.id.txtContent);

                txtName.setText(name);
                txtContent.setText(content);
                imgType.setImageResource(setImageResource(type));

                mainLayout.addView(view);
            }

            talentAdapter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            InputStream is = getBaseContext().getResources().getAssets().open("sheldtalent.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = 3;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(1).length - 1;

                    talentAdapter.open();
                    talentAdapter.databaseReset();

                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String content = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String type = sheet.getCell(nColumnStartIndex+2, nRow).getContents();

                        talentAdapter.createWeapon(name, content, type);
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
