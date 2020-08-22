package com.example.divisionsimulation;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.dbdatas.TalentFMDBAdapter;
import com.example.divisionsimulation.ui.tools.LibraryDBAdapter;
import com.example.divisionsimulation.ui.tools.TalentLibraryDBAdapter;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {
    private Cursor cursor;
    private ListView listView;
    private RadioGroup rgType, rgWeapon;
    private RadioButton[] rdoType = new RadioButton[8];
    private RadioButton[] rdoWeapon = new RadioButton[6];
    private LinearLayout layoutCount;
    private TextView txtCount, txtMaxCount;

    private String[] weapon_types = {"돌격소총", "기관단총", "경기관총", "소총", "지정사수소총", "산탄총"};

    private ArrayList<LibraryItem> libraryItems;
    private ArrayList<String> talentItems;
    private LibraryAdapter libraryAdapter;

    private LibraryDBAdapter libraryDBAdapter;
    private TalentLibraryDBAdapter talentLibraryDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private MaxOptionsFMDBAdapter maxOptionsDBAdapter;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.librarysettinglayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("보정 라이브러리");

        libraryDBAdapter = new LibraryDBAdapter(this);
        talentLibraryDBAdapter = new TalentLibraryDBAdapter(this);
        talentDBAdapter = new TalentFMDBAdapter(this);
        maxOptionsDBAdapter = new MaxOptionsFMDBAdapter(this);

        listView = findViewById(R.id.listView);
        rgType = findViewById(R.id.rgType);
        rgWeapon = findViewById(R.id.rgWeapon);

        layoutCount = findViewById(R.id.layoutCount);
        txtCount = findViewById(R.id.txtCount);
        txtMaxCount = findViewById(R.id.txtMaxCount);

        int resource;
        for (int i = 0; i < rdoType.length; i++) {
            resource = getResources().getIdentifier("rdoType"+(i+1), "id", getPackageName());
            rdoType[i] = findViewById(resource);
        }
        for (int i = 0; i < rdoWeapon.length; i++) {
            resource = getResources().getIdentifier("rdoWeapon"+(i+1), "id", getPackageName());
            rdoWeapon[i] = findViewById(resource);
        }

        libraryItems = new ArrayList<LibraryItem>();
        talentItems = new ArrayList<String>();

        libraryDBAdapter.open();
        cursor = libraryDBAdapter.fetchTypeData("무기");
        while (!cursor.isAfterLast()) {
            LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
            libraryItems.add(item);
            cursor.moveToNext();
        }
        libraryDBAdapter.close();

        libraryAdapter = new LibraryAdapter(this, libraryItems, null, false, "weapon_core1");
        listView.setAdapter(libraryAdapter);

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                libraryItems.clear();
                talentItems.clear();
                libraryDBAdapter.open();
                talentLibraryDBAdapter.open();
                rgWeapon.setVisibility(View.GONE);
                layoutCount.setVisibility(View.GONE);
                switch (checkedId) {
                    case R.id.rdoType1:
                        cursor = libraryDBAdapter.fetchTypeData("무기");
                        while (!cursor.isAfterLast()) {
                            LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                            libraryItems.add(item);
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "weapon_core1");
                        break;
                    case R.id.rdoType2:
                        for (int i = 0; i < weapon_types.length; i++) {
                            cursor = libraryDBAdapter.fetchTypeData(weapon_types[i]);
                            LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                            item.setWeaponType(weapon_types[i]);
                            libraryItems.add(item);
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "weapon_core2");
                        break;
                    case R.id.rdoType3:
                        cursor = libraryDBAdapter.fetchSubAllData();
                        while (!cursor.isAfterLast()) {
                            LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                            libraryItems.add(item);
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "weapon_sub");
                        break;
                    case R.id.rdoType4:
                        cursor = libraryDBAdapter.fetchSheldCoreAllData();
                        while (!cursor.isAfterLast()) {
                            LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                            libraryItems.add(item);
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "sheld_core");
                        break;
                    case R.id.rdoType5:
                        cursor = libraryDBAdapter.fetchSheldSubAllData();
                        while (!cursor.isAfterLast()) {
                            LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                            libraryItems.add(item);
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "sheld_sub");
                        break;
                    case R.id.rdoType6:
                        rgWeapon.setVisibility(View.VISIBLE);
                        layoutCount.setVisibility(View.VISIBLE);
                        talentLibraryDBAdapter.close();
                        rdoWeapon[0].setChecked(true);
                        talentItems.clear();
                        talentLibraryDBAdapter.open();
                        cursor = talentLibraryDBAdapter.fetchTypeData(weapon_types[0]);
                        while (!cursor.isAfterLast()) {
                            talentItems.add(cursor.getString(1));
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, null, talentItems, true, "");
                        txtCount.setText(Integer.toString(talentLibraryDBAdapter.getTypeCount(weapon_types[0])));
                        talentDBAdapter.open();
                        txtMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount(weapon_types[0])));
                        talentDBAdapter.close();
                        break;
                    case R.id.rdoType7:
                        layoutCount.setVisibility(View.VISIBLE);
                        cursor = talentLibraryDBAdapter.fetchTypeData("조끼");
                        while (!cursor.isAfterLast()) {
                            talentItems.add(cursor.getString(1));
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, null, talentItems, true, "");
                        txtCount.setText(Integer.toString(talentLibraryDBAdapter.getTypeCount("조끼")));
                        talentDBAdapter.open();
                        txtMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount("조끼")));
                        talentDBAdapter.close();
                        break;
                    case R.id.rdoType8:
                        layoutCount.setVisibility(View.VISIBLE);
                        cursor = talentLibraryDBAdapter.fetchTypeData("백팩");
                        while (!cursor.isAfterLast()) {
                            talentItems.add(cursor.getString(1));
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, null, talentItems, true, "");
                        txtCount.setText(Integer.toString(talentLibraryDBAdapter.getTypeCount("백팩")));
                        talentDBAdapter.open();
                        txtMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount("백팩")));
                        talentDBAdapter.close();
                        break;
                }
                listView.setAdapter(libraryAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 5; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) {
                                builder = new AlertDialog.Builder(LibraryActivity.this, R.style.MyAlertDialogStyle);
                                builder.setTitle(talentItems.get(position));
                                talentDBAdapter.open();
                                String content = talentDBAdapter.findContent(talentItems.get(position));
                                talentDBAdapter.close();
                                builder.setMessage(content);
                                builder.setPositiveButton("확인", null);

                                alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();
                            }
                        }
                    }
                });
                libraryAdapter.notifyDataSetChanged();
                talentLibraryDBAdapter.close();
                libraryDBAdapter.close();
            }
        });

        rgWeapon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                talentItems.clear();
                talentLibraryDBAdapter.open();
                for (int i = 0; i < rdoWeapon.length; i++) {
                    if (rdoWeapon[i].isChecked()) {
                        cursor = talentLibraryDBAdapter.fetchTypeData(weapon_types[i]);
                        txtCount.setText(Integer.toString(talentLibraryDBAdapter.getTypeCount(weapon_types[i])));
                        talentDBAdapter.open();
                        txtMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount(weapon_types[i])));
                        talentDBAdapter.close();
                    }
                }
                while (!cursor.isAfterLast()) {
                    talentItems.add(cursor.getString(1));
                    cursor.moveToNext();
                }
                talentLibraryDBAdapter.close();
                libraryAdapter = new LibraryAdapter(LibraryActivity.this, null, talentItems, true, "");
                listView.setAdapter(libraryAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        builder = new AlertDialog.Builder(LibraryActivity.this, R.style.MyAlertDialogStyle);
                        builder.setTitle(talentItems.get(position));
                        talentDBAdapter.open();
                        String content = talentDBAdapter.findContent(talentItems.get(position));
                        talentDBAdapter.close();
                        builder.setMessage(content);
                        builder.setPositiveButton("확인", null);

                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }
                });
                libraryAdapter.notifyDataSetChanged();
            }
        });

        /*btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(LibraryActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("보정 라이브러리 초기화");
                builder.setMessage("보정 라이브러리를 모두 초기화하시겠습니까?");
                builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetData();
                        rgRefresh();
                        libraryAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "모든 보정 라이브러리가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                        txtCount.setText("0");
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(LibraryActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("보정 라이브러리 최대치 설정");
                builder.setMessage("보정 라이브러리 옵션을 모두 최대치로 설정합니까?");
                builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetData();
                        maxOptionsDBAdapter.open();
                        libraryDBAdapter.open();
                        cursor = maxOptionsDBAdapter.fetchAllData();
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            long rowID = cursor.getLong(0);
                            String max = cursor.getString(2);
                            libraryDBAdapter.updateIDData(rowID, max);
                            cursor.moveToNext();
                        }
                        libraryDBAdapter.close();
                        maxOptionsDBAdapter.close();
                        talentDBAdapter.open();
                        talentLibraryDBAdapter.open();
                        cursor = talentDBAdapter.fetchAllData();
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            String name = cursor.getString(1);
                            int ar = cursor.getInt(2);
                            int sr = cursor.getInt(3);
                            int br = cursor.getInt(4);
                            int rf = cursor.getInt(5);
                            int mmr = cursor.getInt(6);
                            int sg = cursor.getInt(7);
                            int pt = cursor.getInt(8);
                            int vest = cursor.getInt(9);
                            int backpack = cursor.getInt(10);
                            talentLibraryDBAdapter.insertData(name, ar, sr, br, rf, mmr, sg, pt, vest, backpack);
                            cursor.moveToNext();
                        }
                        talentLibraryDBAdapter.close();
                        talentDBAdapter.close();
                        rgRefresh();
                        libraryAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "모든 보정 옵션을 최대치로 설정하였습니다.", Toast.LENGTH_SHORT).show();
                        txtCount.setText(txtMaxCount.getText());
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });*/
    }

    /*private void rgRefresh() {
        libraryItems.clear();
        talentItems.clear();
        libraryDBAdapter.open();
        talentLibraryDBAdapter.open();
        switch (rgType.getCheckedRadioButtonId()) {
            case R.id.rdoType1:
                cursor = libraryDBAdapter.fetchTypeData("무기");
                while (!cursor.isAfterLast()) {
                    LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                    libraryItems.add(item);
                    cursor.moveToNext();
                }
                break;
            case R.id.rdoType2:
                for (int i = 0; i < weapon_types.length; i++) {
                    cursor = libraryDBAdapter.fetchTypeData(weapon_types[i]);
                    LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                    item.setWeaponType(weapon_types[i]);
                    libraryItems.add(item);
                }
                break;
            case R.id.rdoType3:
                cursor = libraryDBAdapter.fetchSubAllData();
                while (!cursor.isAfterLast()) {
                    LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                    libraryItems.add(item);
                    cursor.moveToNext();
                }
                break;
            case R.id.rdoType4:
                cursor = libraryDBAdapter.fetchSheldCoreAllData();
                while (!cursor.isAfterLast()) {
                    LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                    libraryItems.add(item);
                    cursor.moveToNext();
                }
                break;
            case R.id.rdoType5:
                cursor = libraryDBAdapter.fetchSheldSubAllData();
                while (!cursor.isAfterLast()) {
                    LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                    libraryItems.add(item);
                    cursor.moveToNext();
                }
                break;
            case R.id.rdoType6:
                rgWeapon.setVisibility(View.VISIBLE);
                talentLibraryDBAdapter.close();
                rdoWeapon[0].setChecked(true);
                talentItems.clear();
                talentLibraryDBAdapter.open();
                cursor = talentLibraryDBAdapter.fetchTypeData(weapon_types[0]);
                while (!cursor.isAfterLast()) {
                    talentItems.add(cursor.getString(1));
                    cursor.moveToNext();
                }
                break;
            case R.id.rdoType7:
                cursor = talentLibraryDBAdapter.fetchTypeData("조끼");
                while (!cursor.isAfterLast()) {
                    talentItems.add(cursor.getString(1));
                    cursor.moveToNext();
                }
                break;
            case R.id.rdoType8:
                cursor = talentLibraryDBAdapter.fetchTypeData("백팩");
                while (!cursor.isAfterLast()) {
                    talentItems.add(cursor.getString(1));
                    cursor.moveToNext();
                }
                break;
        }
        libraryDBAdapter.close();
        talentItems.clear();
        for (int i = 0; i < rdoWeapon.length; i++) {
            if (rdoWeapon[i].isChecked()) {
                cursor = talentLibraryDBAdapter.fetchTypeData(weapon_types[i]);
            }
        }
        while (!cursor.isAfterLast()) {
            talentItems.add(cursor.getString(1));
            cursor.moveToNext();
        }
        talentLibraryDBAdapter.close();
    }*/

    /*private void resetData() {
        libraryDBAdapter.open();
        libraryDBAdapter.resetAllData();
        libraryDBAdapter.close();
        talentLibraryDBAdapter.open();
        talentLibraryDBAdapter.databaseReset();
        talentLibraryDBAdapter.close();
    }*/

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
}
