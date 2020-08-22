package com.example.divisionsimulation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.dbdatas.InventoryDBAdapter;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.dbdatas.TalentFMDBAdapter;
import com.example.divisionsimulation.ui.tools.LibraryDBAdapter;
import com.example.divisionsimulation.ui.tools.TalentLibraryDBAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class SettingActivity extends AppCompatActivity {
    private Button btnAllReset, btnLibraryReset, btnLibraryMax, btnLevelReset, btnInventoryClear, btnInventorySave, btnInventoryInput;
    private Button btnDeveloper, btnPower;

    private LibraryDBAdapter libraryDBAdapter;
    private SHDDBAdapter shddbAdapter;
    private InventoryDBAdapter inventoryDBAdapter;
    private TalentLibraryDBAdapter talentLibraryDBAdapter;
    private MaxOptionsFMDBAdapter maxOptionsDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;

    private Cursor cursor;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnAllReset = findViewById(R.id.btnAllReset);
        btnLibraryReset = findViewById(R.id.btnLibraryReset);
        btnLibraryMax = findViewById(R.id.btnLibraryMax);
        btnLevelReset = findViewById(R.id.btnLevelReset);
        btnInventoryClear = findViewById(R.id.btnInventoryClear);
        btnInventorySave = findViewById(R.id.btnInventorySave);
        btnInventoryInput = findViewById(R.id.btnInventoryInput);
        btnDeveloper = findViewById(R.id.btnDeveloper);
        btnPower = findViewById(R.id.btnPower);

        libraryDBAdapter = new LibraryDBAdapter(this);
        shddbAdapter = new SHDDBAdapter(this);
        inventoryDBAdapter = new InventoryDBAdapter(this);
        talentLibraryDBAdapter = new TalentLibraryDBAdapter(this);
        maxOptionsDBAdapter = new MaxOptionsFMDBAdapter(this);
        talentDBAdapter = new TalentFMDBAdapter(this);

        btnAllReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libraryReset();
                shdReset();
                inventoryClear();
            }
        });

        btnLibraryReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libraryReset();
            }
        });

        btnLibraryMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libraryMax();
            }
        });

        btnLevelReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shdReset();
            }
        });

        btnInventoryClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventoryClear();
            }
        });

        btnInventorySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importInventory();
            }
        });

        btnInventoryInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportInventory();
            }
        });

        btnDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("차후 개발될 예정", false);
            }
        });

        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermissions()) {
                    toast("이미 허용된 권한입니다.", false);
                } else {
                    requestPerms();
                }
            }
        });

    }

    private void inventoryClear() {
        builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
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
                Toast.makeText(getApplicationContext(), "인벤토리가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void exportInventory() {
        builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("인벤토리 불러오기");
        builder.setMessage("인벤토리를 불러오시겠습니까?");
        builder.setPositiveButton("불러오기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String databaseName = inventoryDBAdapter.getDatabaseName();
                String backupDirectoryName = "Inventory";
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();
                    if (sd.canWrite()) {
                        String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName+".db";
                        String backupDBPath = "inventory_savefile.db";
                        File backupDB = new File(data, currentDBPath);
                        File currentDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();

                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();

                        toast("인벤토리를 불러왔습니다.", false);
                    } else {
                        toast("권한이 없습니다.", false);
                    }
                } catch (Exception e) {
                    toast("Export Failed!!", false);
                }
            }
        });
        builder.setNegativeButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void importInventory() {
        builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("인벤토리 저장");
        builder.setMessage("인벤토리를 저장하시겠습니까?");
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String databaseName = inventoryDBAdapter.getDatabaseName();
                String backupDirectoryName = "Inventory";
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();
                    if (sd.canWrite()) {
                        File backupDir = new File(sd, backupDirectoryName);
                        backupDir.mkdir();
                        String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName+".db";
                        String backupDBPath = "inventory_savefile.db";
                        File currentDB = new File(data, currentDBPath);
                        File backupDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();

                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();

                        toast("현재 인벤토리를 저장하였습니다.", false);
                    } else {
                        toast("권한이 없습니다.", false);
                    }
                } catch (Exception e) {
                    toast("Import Failed!!", false);
                }
            }
        });
        builder.setNegativeButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void toast(String message, boolean longer) {
        int length;
        if (longer) length = Toast.LENGTH_LONG;
        else length = Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    private void shdReset() {
        builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("SHD 초기화");
        builder.setMessage("SHD 레벨과 포인트 모두 초기화됩니다.");
        builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shddbAdapter.open();
                shddbAdapter.resetSHD();
                shddbAdapter.close();
                toast("SHD 레벨이 초기화되었습니다.", false);
            }
        });
        builder.setNegativeButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void libraryMax() {
        builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
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
                Toast.makeText(getApplicationContext(), "모든 보정 옵션을 최대치로 설정하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void libraryReset() {
        builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("보정 라이브러리 초기화");
        builder.setMessage("보정 라이브러리를 모두 초기화하시겠습니까?");
        builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetData();
                Toast.makeText(getApplicationContext(), "모든 보정 라이브러리가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void resetData() {
        libraryDBAdapter.open();
        libraryDBAdapter.resetAllData();
        libraryDBAdapter.close();
        talentLibraryDBAdapter.open();
        talentLibraryDBAdapter.databaseReset();
        talentLibraryDBAdapter.close();
    }

    private boolean hasPermissions() {
        int res = 0;
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allowed = true;

        switch (requestCode) {
            case 0:
                for (int res : grantResults) {
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                allowed = false;
                break;
        }

        if (!allowed) {
            Toast.makeText(getApplicationContext(), "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
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
}
