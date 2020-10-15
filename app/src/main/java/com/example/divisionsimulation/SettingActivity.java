package com.example.divisionsimulation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.divisionsimulation.librarydatas.ARLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.ARTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.BRLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.BRTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.BackpackLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.BackpackTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.GloveLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.HolsterLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.KneepedLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.MMRLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.MMRTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.MaskLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.PTLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.PTTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.RFLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.RFTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.SGLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.SGTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.SRLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.SRTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.VestLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.VestTalentDBAdapter;
import com.example.divisionsimulation.ui.home.LoadoutDBAdapter;
import com.example.divisionsimulation.ui.tools.LibraryDBAdapter;
import com.example.divisionsimulation.ui.tools.TalentLibraryDBAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class SettingActivity extends AppCompatActivity {
    private Button btnAllReset, btnLibraryReset, btnLibraryMax, btnLevelReset, btnInventoryClear, btnInventorySave, btnInventoryInput;
    private Button btnMaterialReset, btnMaterialMax, btnLibrarySave, btnLibraryLoad, btnSHDSave, btnSHDLoad, btnMaterialSave, btnMaterialLoad;
    private Button btnAllSave, btnAllLoad, btnLoadoutDelete, btnLoadoutSave, btnLoadoutLoad;
    private TextView txtWriteRead;

    private LibraryDBAdapter libraryDBAdapter;
    private SHDDBAdapter shddbAdapter;
    private InventoryDBAdapter inventoryDBAdapter;
    private TalentLibraryDBAdapter talentLibraryDBAdapter;
    private MaxOptionsFMDBAdapter maxOptionsDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private MaterialDbAdapter materialDbAdapter;
    private LoadoutDBAdapter loadoutDBAdapter;

    private ARLibraryDBAdapter arLibraryDBAdapter;
    private BRLibraryDBAdapter brLibraryDBAdapter;
    private MMRLibraryDBAdapter mmrLibraryDBAdapter;
    private PTLibraryDBAdapter ptLibraryDBAdapter;
    private RFLibraryDBAdapter rfLibraryDBAdapter;
    private SGLibraryDBAdapter sgLibraryDBAdapter;
    private SRLibraryDBAdapter srLibraryDBAdapter;

    private MaskLibraryDBAdapter maskLibraryDBAdapter;
    private VestLibraryDBAdapter vestLibraryDBAdapter;
    private HolsterLibraryDBAdapter holsterLibraryDBAdapter;
    private BackpackLibraryDBAdapter backpackLibraryDBAdapter;
    private GloveLibraryDBAdapter gloveLibraryDBAdapter;
    private KneepedLibraryDBAdapter kneepedLibraryDBAdapter;

    private ARTalentDBAdapter arTalentDBAdapter;
    private BRTalentDBAdapter brTalentDBAdapter;
    private MMRTalentDBAdapter mmrTalentDBAdapter;
    private PTTalentDBAdapter ptTalentDBAdapter;
    private RFTalentDBAdapter rfTalentDBAdapter;
    private SGTalentDBAdapter sgTalentDBAdapter;
    private SRTalentDBAdapter srTalentDBAdapter;
    private VestTalentDBAdapter vestTalentDBAdapter;
    private BackpackTalentDBAdapter backpackTalentDBAdapter;

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
        btnLoadoutDelete = findViewById(R.id.btnLoadoutDelete);
        btnLoadoutSave = findViewById(R.id.btnLoadoutSave);
        btnLoadoutLoad = findViewById(R.id.btnLoadoutLoad);

        updatePermissionsUI();

        libraryDBAdapter = new LibraryDBAdapter(this);
        shddbAdapter = new SHDDBAdapter(this);
        inventoryDBAdapter = new InventoryDBAdapter(this);
        talentLibraryDBAdapter = new TalentLibraryDBAdapter(this);
        maxOptionsDBAdapter = new MaxOptionsFMDBAdapter(this);
        talentDBAdapter = new TalentFMDBAdapter(this);
        materialDbAdapter = new MaterialDbAdapter(this);
        loadoutDBAdapter = new LoadoutDBAdapter(this);

        arLibraryDBAdapter = new ARLibraryDBAdapter(this);
        brLibraryDBAdapter = new BRLibraryDBAdapter(this);
        mmrLibraryDBAdapter = new MMRLibraryDBAdapter(this);
        ptLibraryDBAdapter = new PTLibraryDBAdapter(this);
        rfLibraryDBAdapter = new RFLibraryDBAdapter(this);
        sgLibraryDBAdapter = new SGLibraryDBAdapter(this);
        srLibraryDBAdapter = new SRLibraryDBAdapter(this);

        maskLibraryDBAdapter = new MaskLibraryDBAdapter(this);
        vestLibraryDBAdapter = new VestLibraryDBAdapter(this);
        holsterLibraryDBAdapter = new HolsterLibraryDBAdapter(this);
        backpackLibraryDBAdapter = new BackpackLibraryDBAdapter(this);
        gloveLibraryDBAdapter = new GloveLibraryDBAdapter(this);
        kneepedLibraryDBAdapter = new KneepedLibraryDBAdapter(this);

        arTalentDBAdapter = new ARTalentDBAdapter(this);
        brTalentDBAdapter = new BRTalentDBAdapter(this);
        mmrTalentDBAdapter = new MMRTalentDBAdapter(this);
        ptTalentDBAdapter = new PTTalentDBAdapter(this);
        rfTalentDBAdapter = new RFTalentDBAdapter(this);
        sgTalentDBAdapter = new SGTalentDBAdapter(this);
        srTalentDBAdapter = new SRTalentDBAdapter(this);
        vestTalentDBAdapter = new VestTalentDBAdapter(this);
        backpackTalentDBAdapter = new BackpackTalentDBAdapter(this);

        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            toast("외부 저장 매체 사용 불가능", false);
        }

        btnAllReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                TextView txtContent = view.findViewById(R.id.txtContent);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                Button btnOK = view.findViewById(R.id.btnOK);

                btnOK.setText("초기화");
                txtContent.setText("보정 라이브러리, 인벤토리, SHD, 재료, 로드아웃을 모두 초기화하시겠습니까?");

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
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
                        loadoutDBAdapter.open();
                        loadoutDBAdapter.deleteAllData();
                        loadoutDBAdapter.close();
                        Toast.makeText(getApplicationContext(), "모든 데이터가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setView(view);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

        btnLoadoutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLoadout();
            }
        });

        btnLoadoutLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLoadout();
            }
        });

        btnLoadoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLoadout();
            }
        });

        btnAllSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermissions()) {
                    View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                    TextView txtContent = view.findViewById(R.id.txtContent);
                    Button btnCancel = view.findViewById(R.id.btnCancel);
                    Button btnOK = view.findViewById(R.id.btnOK);

                    btnOK.setText("저장");
                    txtContent.setText("보정 라이브러리, 인벤토리, SHD, 재료를 모두 저장하시겠습니까?");

                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            importMaterialData();
                            importSHDData();
                            importLibraryData();
                            importInventoryData();
                            saveLoadoutData();
                            Toast.makeText(getApplicationContext(), "모든 데이터를 저장하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setView(view);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                } else {
                    requestPerms();
                }
            }
        });

        btnAllLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermissions()) {
                    View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                    TextView txtContent = view.findViewById(R.id.txtContent);
                    Button btnCancel = view.findViewById(R.id.btnCancel);
                    Button btnOK = view.findViewById(R.id.btnOK);

                    btnOK.setText("불러오기");
                    txtContent.setText("보정 라이브러리, 인벤토리, SHD, 재료를 모두 불러오시겠습니까?");

                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            exportMaterialData();
                            exportSHDData();
                            exportInventoryData();
                            exportLibraryData();
                            loadLoadoutData();
                            Toast.makeText(getApplicationContext(), "모든 데이터를 불러왔습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setView(view);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                } else {
                    requestPerms();
                }
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

        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

        TextView txtContent = view.findViewById(R.id.txtContent);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnOK = view.findViewById(R.id.btnOK);

        btnOK.setText("채우기");
        txtContent.setText("재료를 최대치로 채우시겠습니까?");

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                materialDbAdapter.open();
                for (int i = 0; i < material.length; i++) {
                    material[i] = material_max[i];
                    materialDbAdapter.updateMaterial(material_name[i], material[i]);
                }
                materialDbAdapter.close();
                Toast.makeText(getApplicationContext(), "모든 재료를 채웠습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

        TextView txtContent = view.findViewById(R.id.txtContent);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnOK = view.findViewById(R.id.btnOK);

        btnOK.setText("초기화");
        txtContent.setText("모든 재료를 초기화하시겠습니까?");

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                materialDbAdapter.open();
                for (int i = 0; i < material.length; i++) {
                    material[i] = 0;
                    materialDbAdapter.updateMaterial(material_name[i], material[i]);
                }
                materialDbAdapter.close();
                Toast.makeText(getApplicationContext(), "모든 재료를 초기화하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void inventoryClear() {
        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

        TextView txtContent = view.findViewById(R.id.txtContent);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnOK = view.findViewById(R.id.btnOK);

        btnOK.setText("초기화");
        inventoryDBAdapter.open();
        txtContent.setText("인벤토리를 초기화하시겠습니까? ("+inventoryDBAdapter.getCount()+"/300)");
        inventoryDBAdapter.close();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                inventoryDBAdapter.open();
                inventoryDBAdapter.deleteAllData();
                inventoryDBAdapter.close();
                Toast.makeText(getApplicationContext(), "인벤토리가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void exportInventoryData() {
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
            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("저장된 파일이 없습니다.", false);
            e.printStackTrace();
        }
    }

    private void exportInventory() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("불러오기");
            txtContent.setText("인벤토리를 불러오시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    exportInventoryData();
                    toast("인벤토리를 불러왔습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void importInventoryData() {
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

            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("Import Failed!!", false);
            e.printStackTrace();
        }
    }

    private void importInventory() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("저장");
            txtContent.setText("인벤토리를 저장하시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    importInventoryData();
                    toast("현재 인벤토리를 저장하였습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void exportSHDData() {
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

            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("저장된 파일이 없습니다.", false);
            e.printStackTrace();
        }
    }

    private void exportSHD() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("불러오기");
            txtContent.setText("SHD를 불러오시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    exportSHDData();
                    toast("SHD를 불러왔습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void importSHDData() {
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

            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("Import Failed!!", false);
            e.printStackTrace();
        }
    }

    private void importSHD() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("저장");
            txtContent.setText("SHD를 저장하시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    importSHDData();
                    toast("현재 SHD를 저장하였습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void exportMaterialData() {
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

            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("저장된 파일이 없습니다.", false);
            e.printStackTrace();
        }
    }

    private void exportMaterial() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("불러오기");
            txtContent.setText("재료를 불러오시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    exportMaterialData();
                    toast("재료를 불러왔습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void importMaterialData() {
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

            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("Import Failed!!", false);
            e.printStackTrace();
        }
    }

    private void importMaterial() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("저장");
            txtContent.setText("재료를 저장하시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    importMaterialData();
                    toast("현재 재료를 저장하였습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void exportLibraryData() {
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

            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("저장된 파일이 없습니다.", false);
            e.printStackTrace();
        }
    }

    private void exportLibrary() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("불러오기");
            txtContent.setText("보정 라이브러리를 불러오시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    exportLibraryData();
                    toast("보정 라이브러리를 불러왔습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void importLibraryData() {
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

            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("Import Failed!!", false);
            e.printStackTrace();
        }
    }

    private void importLibrary() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("저장");
            txtContent.setText("보정 라이브러리를 저장하시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    importLibraryData();
                    toast("현재 보정 라이브러리를 저장하였습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void deleteLoadout() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("삭제");
            txtContent.setText("로드아웃을 모두 삭제하시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    loadoutDBAdapter.open();
                    loadoutDBAdapter.deleteAllData();
                    loadoutDBAdapter.close();
                    toast("모든 로드아웃을 삭제하였습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void saveLoadout() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("저장");
            txtContent.setText("로드아웃을 외부에 저장하시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    saveLoadoutData();
                    toast("로드아웃을 저장하였습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void saveLoadoutData() {
        String databaseName = loadoutDBAdapter.getDatabaseName();
        String backupDirectoryName = "Division2Databases";
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                File backupDir = new File(sd, backupDirectoryName);
                if (!backupDir.exists()) backupDir.mkdir();
                String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                String backupDBPath = "loadout_savefile";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();

                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("Import Failed!!", false);
            e.printStackTrace();
        }
    }

    private void loadLoadout() {
        if (hasPermissions()) {
            View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

            TextView txtContent = view.findViewById(R.id.txtContent);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            Button btnOK = view.findViewById(R.id.btnOK);

            btnOK.setText("불러오기");
            txtContent.setText("로드아웃을 외부에서 불러오시겠습니까?");

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    loadLoadoutData();
                    toast("로드아웃을 불러왔습니다.", false);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            requestPerms();
        }
    }

    private void loadLoadoutData() {
        String databaseName = loadoutDBAdapter.getDatabaseName();
        String backupDirectoryName = "Division2Databases";
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getPackageName()+ "//databases//" + databaseName;
                String backupDBPath = "loadout_savefile";
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDirectoryName+"/"+backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();

                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            } else {
                toast("권한 오류", false);
            }
        } catch (Exception e) {
            toast("저장된 파일이 없습니다.", false);
            e.printStackTrace();
        }
    }

    private void toast(String message, boolean longer) {
        int length;
        if (longer) length = Toast.LENGTH_LONG;
        else length = Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    private void shdReset() {
        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

        TextView txtContent = view.findViewById(R.id.txtContent);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnOK = view.findViewById(R.id.btnOK);

        btnOK.setText("초기화");
        txtContent.setText("SHD 레벨과 포인트 모두 초기화됩니다.");

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                shddbAdapter.open();
                shddbAdapter.resetSHD();
                shddbAdapter.close();
                toast("SHD 레벨이 초기화되었습니다.", false);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void libraryMax() {
        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

        TextView txtContent = view.findViewById(R.id.txtContent);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnOK = view.findViewById(R.id.btnOK);

        btnOK.setText("설정");
        txtContent.setText("보정 라이브러리 옵션을 모두 최대치로 설정합니까?");

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void libraryReset() {
        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

        TextView txtContent = view.findViewById(R.id.txtContent);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnOK = view.findViewById(R.id.btnOK);

        btnOK.setText("초기화");
        txtContent.setText("보정 라이브러리를 모두 초기화하시겠습니까?");

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                resetData();
                Toast.makeText(getApplicationContext(), "모든 보정 라이브러리가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void resetData() {
        arLibraryDBAdapter.open();
        arLibraryDBAdapter.resetAllData();
        arLibraryDBAdapter.close();
        brLibraryDBAdapter.open();
        brLibraryDBAdapter.resetAllData();
        brLibraryDBAdapter.close();
        mmrLibraryDBAdapter.open();
        mmrLibraryDBAdapter.resetAllData();
        mmrLibraryDBAdapter.close();
        ptLibraryDBAdapter.open();
        ptLibraryDBAdapter.resetAllData();
        ptLibraryDBAdapter.close();
        rfLibraryDBAdapter.open();
        rfLibraryDBAdapter.resetAllData();
        rfLibraryDBAdapter.close();
        sgLibraryDBAdapter.open();
        sgLibraryDBAdapter.resetAllData();
        sgLibraryDBAdapter.close();
        srLibraryDBAdapter.open();
        srLibraryDBAdapter.resetAllData();
        srLibraryDBAdapter.close();
        maskLibraryDBAdapter.open();
        maskLibraryDBAdapter.resetAllData();
        maskLibraryDBAdapter.close();
        vestLibraryDBAdapter.open();
        vestLibraryDBAdapter.resetAllData();
        vestLibraryDBAdapter.close();
        holsterLibraryDBAdapter.open();
        holsterLibraryDBAdapter.resetAllData();
        holsterLibraryDBAdapter.close();
        backpackLibraryDBAdapter.open();
        backpackLibraryDBAdapter.resetAllData();
        backpackLibraryDBAdapter.close();
        gloveLibraryDBAdapter.open();
        gloveLibraryDBAdapter.resetAllData();
        gloveLibraryDBAdapter.close();
        kneepedLibraryDBAdapter.open();
        kneepedLibraryDBAdapter.resetAllData();
        kneepedLibraryDBAdapter.close();
        arTalentDBAdapter.open();
        arTalentDBAdapter.resetAllData();
        arTalentDBAdapter.close();
        brTalentDBAdapter.open();
        brTalentDBAdapter.resetAllData();
        brTalentDBAdapter.close();
        mmrTalentDBAdapter.open();
        mmrTalentDBAdapter.resetAllData();
        mmrTalentDBAdapter.close();
        ptTalentDBAdapter.open();
        ptTalentDBAdapter.resetAllData();
        ptTalentDBAdapter.close();
        rfTalentDBAdapter.open();
        rfTalentDBAdapter.resetAllData();
        rfTalentDBAdapter.close();
        sgTalentDBAdapter.open();
        sgTalentDBAdapter.resetAllData();
        sgTalentDBAdapter.close();
        srTalentDBAdapter.open();
        srTalentDBAdapter.resetAllData();
        srTalentDBAdapter.close();
        vestTalentDBAdapter.open();
        vestTalentDBAdapter.resetAllData();
        vestTalentDBAdapter.close();
        backpackTalentDBAdapter.open();
        backpackTalentDBAdapter.resetAllData();
        backpackTalentDBAdapter.close();
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
