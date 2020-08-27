package com.example.divisionsimulation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    private Button btnDeveloper, btnMaterialReset, btnMaterialMax, btnLibrarySave, btnLibraryLoad, btnSHDSave, btnSHDLoad, btnMaterialSave, btnMaterialLoad;
    private Button btnAllSave, btnAllLoad;
    private TextView txtWriteRead;

    private LibraryDBAdapter libraryDBAdapter;
    private SHDDBAdapter shddbAdapter;
    private InventoryDBAdapter inventoryDBAdapter;
    private TalentLibraryDBAdapter talentLibraryDBAdapter;
    private MaxOptionsFMDBAdapter maxOptionsDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private MaterialDbAdapter materialDbAdapter;

    private Cursor cursor;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("설정");

        btnAllReset = findViewById(R.id.btnAllReset);
        btnLibraryReset = findViewById(R.id.btnLibraryReset);
        btnLibraryMax = findViewById(R.id.btnLibraryMax);
        btnLevelReset = findViewById(R.id.btnLevelReset);
        btnInventoryClear = findViewById(R.id.btnInventoryClear);
        btnInventorySave = findViewById(R.id.btnInventorySave);
        btnInventoryInput = findViewById(R.id.btnInventoryInput);
        btnDeveloper = findViewById(R.id.btnDeveloper);
        btnMaterialReset = findViewById(R.id.btnMaterialReset);
        btnMaterialMax = findViewById(R.id.btnMaterialMax);
        btnLibrarySave = findViewById(R.id.btnLibrarySave);
        btnLibraryLoad = findViewById(R.id.btnLibraryLoad);
        btnSHDSave = findViewById(R.id.btnSHDSave);
        btnSHDLoad = findViewById(R.id.btnSHDLoad);
        btnMaterialSave = findViewById(R.id.btnMaterialSave);
        btnMaterialLoad = findViewById(R.id.btnMaterialLoad);
        btnAllSave = findViewById(R.id.btnAllSave);
        btnAllLoad = findViewById(R.id.btnAllLoad);
        txtWriteRead = findViewById(R.id.txtWriteRead);

        updatePermissionsUI();

        libraryDBAdapter = new LibraryDBAdapter(this);
        shddbAdapter = new SHDDBAdapter(this);
        inventoryDBAdapter = new InventoryDBAdapter(this);
        talentLibraryDBAdapter = new TalentLibraryDBAdapter(this);
        maxOptionsDBAdapter = new MaxOptionsFMDBAdapter(this);
        talentDBAdapter = new TalentFMDBAdapter(this);
        materialDbAdapter = new MaterialDbAdapter(this);

        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            toast("외부 저장 매체 사용 불가능", false);
        }

        btnAllReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("모두 초기화");
                builder.setMessage("보정 라이브러리, 인벤토리, SHD, 재료를 모두 초기화하시겠습니까?");
                builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetData();
                        materialDbAdapter.open();
                        final int[] material = new int[materialDbAdapter.getCount()];
                        final String[] material_name = new String[materialDbAdapter.getCount()];
                        final int[] material_max = new int[materialDbAdapter.getCount()];
                        cursor = materialDbAdapter.fetchAllMaterial();
                        cursor.moveToFirst();
                        int count = 0;
                        while (!cursor.isAfterLast()) {
                            material_name[count] = cursor.getString(1);
                            material[count] = cursor.getInt(2);
                            material_max[count] = cursor.getInt(3);
                            cursor.moveToNext();
                            count++;
                        }
                        for (int i = 0; i < material.length; i++) {
                            material[i] = 0;
                            materialDbAdapter.updateMaterial(material_name[i], material[i]);
                        }
                        materialDbAdapter.close();
                        inventoryDBAdapter.open();
                        inventoryDBAdapter.deleteAllData();
                        inventoryDBAdapter.close();
                        shddbAdapter.open();
                        shddbAdapter.resetSHD();
                        shddbAdapter.close();
                        Toast.makeText(getApplicationContext(), "모든 데이터가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
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

        btnMaterialReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialReset();
            }
        });

        btnMaterialMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialMax();
            }
        });

        btnLibrarySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importLibrary();
            }
        });

        btnLibraryLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportLibrary();
            }
        });

        btnSHDSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importSHD();
            }
        });

        btnSHDLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportSHD();
            }
        });

        btnMaterialSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importMaterial();
            }
        });

        btnMaterialLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportMaterial();
            }
        });

        btnAllSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importMaterial();
                importSHD();
                importLibrary();
                importInventory();
            }
        });

        btnAllLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportMaterial();
                exportSHD();
                exportInventory();
                exportLibrary();
            }
        });

    }

    private void materialMax() {
        materialDbAdapter.open();
        final int[] material = new int[materialDbAdapter.getCount()];
        final String[] material_name = new String[materialDbAdapter.getCount()];
        final int[] material_max = new int[materialDbAdapter.getCount()];
        cursor = materialDbAdapter.fetchAllMaterial();
        cursor.moveToFirst();
        int count = 0;
        while (!cursor.isAfterLast()) {
            material_name[count] = cursor.getString(1);
            material[count] = cursor.getInt(2);
            material_max[count] = cursor.getInt(3);
            cursor.moveToNext();
            count++;
        }
        materialDbAdapter.close();
        builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("재료 최대치");
        builder.setMessage("재료를 최대치로 채우시겠습니까?");

        builder.setPositiveButton("모든 재료 채우기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                materialDbAdapter.open();
                for (int i = 0; i < material.length; i++) {
                    material[i] = material_max[i];
                    materialDbAdapter.updateMaterial(material_name[i], material[i]);
                }
                materialDbAdapter.close();
            }
        });
        builder.setNegativeButton("특급 부품만 채우기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                materialDbAdapter.open();
                material[9] = material_max[9];
                materialDbAdapter.updateMaterial(material_name[9], material[9]);
                materialDbAdapter.close();
            }
        });
        builder.setNeutralButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void materialReset() {
        materialDbAdapter.open();
        final int[] material = new int[materialDbAdapter.getCount()];
        final String[] material_name = new String[materialDbAdapter.getCount()];
        final int[] material_max = new int[materialDbAdapter.getCount()];
        cursor = materialDbAdapter.fetchAllMaterial();
        cursor.moveToFirst();
        int count = 0;
        while (!cursor.isAfterLast()) {
            material_name[count] = cursor.getString(1);
            material[count] = cursor.getInt(2);
            material_max[count] = cursor.getInt(3);
            cursor.moveToNext();
            count++;
        }
        materialDbAdapter.close();
        builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("재료 초기화");
        builder.setMessage("모든 재료를 초기화하시겠습니까?");
        builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                materialDbAdapter.open();
                for (int i = 0; i < material.length; i++) {
                    material[i] = 0;
                    materialDbAdapter.updateMaterial(material_name[i], material[i]);
                }
                materialDbAdapter.close();
                Toast.makeText(getApplicationContext(), "모든 재료를 초기화하였습니다.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", null);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
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
        if (hasPermissions()) {
            builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("인벤토리 불러오기");
            builder.setMessage("인벤토리를 불러오시겠습니까?");
            builder.setPositiveButton("불러오기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String databaseName = inventoryDBAdapter.getDatabaseName();
                    String backupDirectoryName = "Division2Databases";
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
                            String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            String backupDBPath = "inventory_savefile";
                            File backupDB = new File(data, currentDBPath);
                            File currentDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            toast("인벤토리를 불러왔습니다.", false);
                        } else {
                            toast("권한 오류", false);
                        }
                    } catch (Exception e) {
                        toast("저장된 파일이 없습니다.", false);
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("취소", null);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void importInventory() {
        if (hasPermissions()) {
            builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("인벤토리 저장");
            builder.setMessage("인벤토리를 저장하시겠습니까?");
            builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String databaseName = inventoryDBAdapter.getDatabaseName();
                    String backupDirectoryName = "Division2Databases";
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
                            File backupDir = new File(sd, backupDirectoryName);
                            if (!backupDir.exists()) backupDir.mkdir();
                            String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            String backupDBPath = "inventory_savefile";
                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            toast("현재 인벤토리를 저장하였습니다.", false);
                        } else {
                            toast("권한 오류", false);
                        }
                    } catch (Exception e) {
                        toast("Import Failed!!", false);
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("취소", null);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void exportSHD() {
        if (hasPermissions()) {
            builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("SHD 불러오기");
            builder.setMessage("SHD를 불러오시겠습니까?");
            builder.setPositiveButton("불러오기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String databaseName = shddbAdapter.getDatabaseName();
                    String backupDirectoryName = "Division2Databases";
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
                            String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            String backupDBPath = "shd_savefile";
                            File backupDB = new File(data, currentDBPath);
                            File currentDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            toast("SHD를 불러왔습니다.", false);
                        } else {
                            toast("권한 오류", false);
                        }
                    } catch (Exception e) {
                        toast("저장된 파일이 없습니다.", false);
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("취소", null);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void importSHD() {
        if (hasPermissions()) {
            builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("SHD 저장");
            builder.setMessage("SHD를 저장하시겠습니까?");
            builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String databaseName = shddbAdapter.getDatabaseName();
                    String backupDirectoryName = "Division2Databases";
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
                            File backupDir = new File(sd, backupDirectoryName);
                            if (!backupDir.exists()) backupDir.mkdir();
                            String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            String backupDBPath = "shd_savefile";
                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            toast("현재 SHD를 저장하였습니다.", false);
                        } else {
                            toast("권한 오류", false);
                        }
                    } catch (Exception e) {
                        toast("Import Failed!!", false);
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("취소", null);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void exportMaterial() {
        if (hasPermissions()) {
            builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("재료 불러오기");
            builder.setMessage("재료를 불러오시겠습니까?");
            builder.setPositiveButton("불러오기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String databaseName = materialDbAdapter.getDatabaseName();
                    String backupDirectoryName = "Division2Databases";
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
                            String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            String backupDBPath = "material_savefile";
                            File backupDB = new File(data, currentDBPath);
                            File currentDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            toast("재료를 불러왔습니다.", false);
                        } else {
                            toast("권한 오류", false);
                        }
                    } catch (Exception e) {
                        toast("저장된 파일이 없습니다.", false);
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("취소", null);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void importMaterial() {
        if (hasPermissions()) {
            builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("재료 저장");
            builder.setMessage("재료를 저장하시겠습니까?");
            builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String databaseName = materialDbAdapter.getDatabaseName();
                    String backupDirectoryName = "Division2Databases";
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
                            File backupDir = new File(sd, backupDirectoryName);
                            if (!backupDir.exists()) backupDir.mkdir();
                            String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            String backupDBPath = "material_savefile";
                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            toast("현재 재료를 저장하였습니다.", false);
                        } else {
                            toast("권한 오류", false);
                        }
                    } catch (Exception e) {
                        toast("Import Failed!!", false);
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("취소", null);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void exportLibrary() {
        if (hasPermissions()) {
            builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("보정 라이브러리 불러오기");
            builder.setMessage("보정 라이브러리를 불러오시겠습니까?");
            builder.setPositiveButton("불러오기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String databaseName = libraryDBAdapter.getDatabaseName();
                    String backupDirectoryName = "Division2Databases";
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
                            String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            String backupDBPath = "library_savefile";
                            File backupDB = new File(data, currentDBPath);
                            File currentDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            databaseName = talentLibraryDBAdapter.getDatabaseName();

                            currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            backupDBPath = "talent_library_savefile";
                            backupDB = new File(data, currentDBPath);
                            currentDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            src = new FileInputStream(currentDB).getChannel();
                            dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            toast("보정 라이브러리를 불러왔습니다.", false);
                        } else {
                            toast("권한 오류", false);
                        }
                    } catch (Exception e) {
                        toast("저장된 파일이 없습니다.", false);
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("취소", null);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void importLibrary() {
        if (hasPermissions()) {
            builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("보정 라이브러리 저장");
            builder.setMessage("보정 라이브러리를 저장하시겠습니까?");
            builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String databaseName = libraryDBAdapter.getDatabaseName();
                    String backupDirectoryName = "Division2Databases";
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
                            File backupDir = new File(sd, backupDirectoryName);
                            if (!backupDir.exists()) backupDir.mkdir();
                            String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            String backupDBPath = "library_savefile";
                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            databaseName = talentLibraryDBAdapter.getDatabaseName();

                            currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                            backupDBPath = "talent_library_savefile";
                            currentDB = new File(data, currentDBPath);
                            backupDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                            src = new FileInputStream(currentDB).getChannel();
                            dst = new FileOutputStream(backupDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            toast("현재 보정 라이브러리를 저장하였습니다.", false);
                        } else {
                            toast("권한 오류", false);
                        }
                    } catch (Exception e) {
                        toast("Import Failed!!", false);
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("취소", null);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            requestPerms();
        }
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
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 0);
        }
    }

    private void updatePermissionsUI() {
        if (hasPermissions()) {
            txtWriteRead.setText("허용됨");
            txtWriteRead.setTextColor(Color.parseColor("#00FF00"));
        } else {
            txtWriteRead.setText("거부됨");
            txtWriteRead.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionsUI();
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

        updatePermissionsUI();
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
