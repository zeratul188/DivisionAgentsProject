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

public class SheldOption2Activity extends AppCompatActivity {
    
    private LinearLayout mainLayout;
    private NamedSheldDbAdapter namedAdapter;

    private int[] arrItem = {R.drawable.mask, R.drawable.vests, R.drawable.holsters, R.drawable.backpack, R.drawable.gloves, R.drawable.kneepeds};
    // 0 : 마스크
    // 1 : 조끼
    // 2 : 권총집
    // 3 : 백팩
    // 4 : 장갑
    // 5 : 무릎 보호대

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheldoptionlayout2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("네임드 보호장구");

        mainLayout = findViewById(R.id.mainLayout);

        this.namedAdapter = new NamedSheldDbAdapter(this);
        copyExcelDataToDatabase();

        try {
            namedAdapter.open();

            Cursor cursor;
            View view;
            cursor = namedAdapter.fetchAllWeapon();
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String name = cursor.getString(1);
                String brand = cursor.getString(2);
                String talent = cursor.getString(3);
                String content = cursor.getString(4);
                String location = cursor.getString(5);
                String type = cursor.getString(6);
                cursor.moveToNext();

                view = getLayoutInflater().inflate(R.layout.namedshelditem, null);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param.bottomMargin = 50;
                view.setLayoutParams(param);

                final ImageView imgType = view.findViewById(R.id.imgType);
                final TextView txtName = view.findViewById(R.id.txtName);
                final TextView txtBrand = view.findViewById(R.id.txtBrand);
                final TextView txtLocation = view.findViewById(R.id.txtLocation);
                final TextView txtTalent = view.findViewById(R.id.txtTalent);
                final TextView txtContent = view.findViewById(R.id.txtContent);

                switch (type) {
                    case "마스크":
                        imgType.setImageResource(arrItem[0]);
                        break;
                    case "조끼":
                        imgType.setImageResource(arrItem[1]);
                        break;
                    case "권총집":
                        imgType.setImageResource(arrItem[2]);
                        break;
                    case "백팩":
                        imgType.setImageResource(arrItem[3]);
                        break;
                    case "장갑":
                        imgType.setImageResource(arrItem[4]);
                        break;
                    case "무릎 보호대":
                        imgType.setImageResource(arrItem[5]);
                        break;
                    default :
                        imgType.setImageResource(arrItem[0]);
                }

                txtName.setText(name);
                txtBrand.setText(brand);
                txtLocation.setText(location);
                txtTalent.setText(talent);
                if (content.equals("-")) txtContent.setVisibility(View.GONE);
                else txtContent.setText(content);

                mainLayout.addView(view);
            }

            namedAdapter.close();
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
            InputStream is = getBaseContext().getResources().getAssets().open("namedsheld.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = 6;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(1).length - 1;

                    namedAdapter.open();
                    namedAdapter.databaseReset();

                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String brand = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String talent = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                        String content = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                        String location = sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                        String type = sheet.getCell(nColumnStartIndex+5, nRow).getContents();

                        namedAdapter.createWeapon(name, brand, talent, content, location, type);
                    }

                    namedAdapter.close();
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
