package com.example.divisionsimulation.ui.tools;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.MaterialDbAdapter;
import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.ExoticFMDBAdapter;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ItemEditActivity extends AppCompatActivity {

    private LinearLayout layoutWeapon, layoutSheld, layoutTalentCount, layoutExotic;
    private TextView txtWeaponOption, txtSheldOption, txtTalent, txtLibraryTalentCount, txtTalentMaxCount, txtName, txtType, txtMenu;
    private ProgressBar progressWeaponOption, progressSheldOption;
    private ImageView imgSheldOption, imgType;
    private ListView listView;
    private Button btnRessetting, btnCancel, btnEdit;

    private EditAdapter editAdapter;
    private boolean exoticed = false, talented = false, edit_possible = false, sheld_sub = false, darked = false;
    private String name, type, option_type, other_name = "", exoticname = "";
    private double value;
    private long rowID;
    private Cursor cursor;
    private ArrayList<EditItem> editItems;
    private ArrayList<String> talentItems;

    private MaxOptionsFMDBAdapter maxDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private InventoryDBAdapter inventoryDBAdapter;
    private ExoticFMDBAdapter exoticDBAdapter;
    private MaterialDbAdapter materialDbAdapter;
    private LibraryDBAdapter libraryDBAdapter;
    private TalentLibraryDBAdapter talentLibraryDBAdapter;
    
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

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private String[] weaponList = {"돌격소총", "소총", "지정사수소총", "산탄총", "기관단총", "경기관총", "권총"};
    private boolean weaponed = false, core = false;
    private String remove_option = "";

    private ArrayList<MaterialItem> materialList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemeditlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("보정 라이브러리");

        layoutWeapon = findViewById(R.id.layoutWeapon);
        layoutSheld = findViewById(R.id.layoutSheld);
        txtWeaponOption = findViewById(R.id.txtWeaponOption);
        txtSheldOption = findViewById(R.id.txtSheldOption);
        progressWeaponOption = findViewById(R.id.progressWeaponOption);
        progressSheldOption = findViewById(R.id.progressSheldOption);
        imgSheldOption = findViewById(R.id.imgSheldOption);
        btnRessetting = findViewById(R.id.btnResetting);
        btnCancel = findViewById(R.id.btnCancel);
        btnEdit = findViewById(R.id.btnEdit);
        listView = findViewById(R.id.listView);
        txtTalent = findViewById(R.id.txtTalent);
        layoutTalentCount = findViewById(R.id.layoutTalentCount);
        txtLibraryTalentCount = findViewById(R.id.txtLibraryTalentCount);
        txtTalentMaxCount = findViewById(R.id.txtTalentMaxCount);

        layoutExotic = findViewById(R.id.layoutExotic);
        txtName = findViewById(R.id.txtName);
        txtType = findViewById(R.id.txtType);
        txtMenu = findViewById(R.id.txtMenu);
        imgType = findViewById(R.id.imgType);

        exoticed = getIntent().getBooleanExtra("exoticed", false);
        talented = getIntent().getBooleanExtra("talented", false);
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        rowID = getIntent().getLongExtra("itemID", 9999);
        sheld_sub = getIntent().getBooleanExtra("sheld_sub", false);
        darked = getIntent().getBooleanExtra("darked", false);
        exoticname = getIntent().getStringExtra("exoticname");

        if (!talented) {
            value = getIntent().getDoubleExtra("value", 0);
            option_type = getIntent().getStringExtra("option_type");
            if (sheld_sub) {
                other_name = getIntent().getStringExtra("other_name");
            }
        }

        editItems = new ArrayList<EditItem>();
        talentItems = new ArrayList<String>();
        maxDBAdapter = new MaxOptionsFMDBAdapter(this);
        talentDBAdapter = new TalentFMDBAdapter(this);
        inventoryDBAdapter = new InventoryDBAdapter(this);
        exoticDBAdapter = new ExoticFMDBAdapter(this);
        materialDbAdapter = new MaterialDbAdapter(this);
        libraryDBAdapter = new LibraryDBAdapter(this);
        talentLibraryDBAdapter = new TalentLibraryDBAdapter(this);

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

        materialList = new ArrayList<MaterialItem>();

        materialDbAdapter.open();
        cursor = materialDbAdapter.fetchAllMaterial();
        cursor.moveToFirst();
        String material_name;
        int material, material_max;
        while (!cursor.isAfterLast()) {
            material_name = cursor.getString(1);
            material = cursor.getInt(2);
            material_max = cursor.getInt(3);
            MaterialItem item = new MaterialItem(material_name, material, material_max);
            materialList.add(item);
            cursor.moveToNext();
        }
        materialDbAdapter.close();

        if (exoticed) {
            txtMenu.setText("특급 아이템 정보");
            listView.setVisibility(View.INVISIBLE);
            btnRessetting.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            layoutExotic.setVisibility(View.VISIBLE);
            txtName.setText(exoticname);
            txtType.setText(type);
            switch (type) {
                case "돌격소총":
                    imgType.setImageResource(R.drawable.wp1custom);
                    break;
                case "소총":
                    imgType.setImageResource(R.drawable.wp2custom);
                    break;
                case "지정사수소총":
                    imgType.setImageResource(R.drawable.wp3custom);
                    break;
                case "기관단총":
                    imgType.setImageResource(R.drawable.wp4custom);
                    break;
                case "경기관총":
                    imgType.setImageResource(R.drawable.wp5custom);
                    break;
                case "산탄총":
                    imgType.setImageResource(R.drawable.wp6custom);
                    break;
                case "권총":
                    imgType.setImageResource(R.drawable.wp7custom);
                    break;
                case "마스크":
                    imgType.setImageResource(R.drawable.sd1custom);
                    break;
                case "조끼":
                    imgType.setImageResource(R.drawable.sd2custom);
                    break;
                case "권총집":
                    imgType.setImageResource(R.drawable.sd3custom);
                    break;
                case "백팩":
                    imgType.setImageResource(R.drawable.sd4custom);
                    break;
                case "장갑":
                    imgType.setImageResource(R.drawable.sd5custom);
                    break;
                case "무릎보호대":
                    imgType.setImageResource(R.drawable.sd6custom);
                    break;
            }
        } else if (talented) {
            layoutTalentCount.setVisibility(View.VISIBLE);
            txtTalent.setVisibility(View.VISIBLE);
            txtTalent.setText(name);
            listView.setVisibility(View.VISIBLE);
            switch (type) {
                case "돌격소총":
                    arTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(arTalentDBAdapter.getHaveCount()));
                    cursor = arTalentDBAdapter.fetchHaveData();
                    arTalentDBAdapter.close();
                    break;
                case "기관단총":
                    srTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(srTalentDBAdapter.getHaveCount()));
                    cursor = srTalentDBAdapter.fetchHaveData();
                    srTalentDBAdapter.close();
                    break;
                case "경기관총":
                    brTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(brTalentDBAdapter.getHaveCount()));
                    cursor = brTalentDBAdapter.fetchHaveData();
                    brTalentDBAdapter.close();
                    break;
                case "지정사수소총":
                    mmrTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(mmrTalentDBAdapter.getHaveCount()));
                    cursor = mmrTalentDBAdapter.fetchHaveData();
                    mmrTalentDBAdapter.close();
                    break;
                case "소총":
                    rfTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(rfTalentDBAdapter.getHaveCount()));
                    cursor = rfTalentDBAdapter.fetchHaveData();
                    rfTalentDBAdapter.close();
                    break;
                case "산탄총":
                    sgTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(sgTalentDBAdapter.getHaveCount()));
                    cursor = sgTalentDBAdapter.fetchHaveData();
                    sgTalentDBAdapter.close();
                    break;
                case "권총":
                    ptTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(ptTalentDBAdapter.getHaveCount()));
                    cursor = ptTalentDBAdapter.fetchHaveData();
                    ptTalentDBAdapter.close();
                    break;
                case "조끼":
                    vestTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(vestTalentDBAdapter.getHaveCount()));
                    cursor = vestTalentDBAdapter.fetchHaveData();
                    vestTalentDBAdapter.close();
                    break;
                case "백팩":
                    backpackTalentDBAdapter.open();
                    txtLibraryTalentCount.setText(Integer.toString(backpackTalentDBAdapter.getHaveCount()));
                    cursor = backpackTalentDBAdapter.fetchHaveData();
                    backpackTalentDBAdapter.close();
                    break;
            }
            talentDBAdapter.open();
            txtTalentMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount(type)));
            talentDBAdapter.close();
            while (!cursor.isAfterLast()) {
                talentItems.add(cursor.getString(1));
                cursor.moveToNext();
            }
            editAdapter = new EditAdapter(this, null, talentItems, true, option_type, type);
            listView.setAdapter(editAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final View talent_view = getLayoutInflater().inflate(R.layout.edittalent, null);
                    TextView[] txtMaterialName = new TextView[3];
                    TextView[] txtMaterial = new TextView[3];
                    TextView txtName = talent_view.findViewById(R.id.txtName);
                    TextView txtContent = talent_view.findViewById(R.id.txtContent);
                    Button btnOK = talent_view.findViewById(R.id.btnOK);
                    Button btnExit = talent_view.findViewById(R.id.btnExit);
                    TextView txtUndo = talent_view.findViewById(R.id.txtUndo);
                    TextView txtNext = talent_view.findViewById(R.id.txtNext);

                    LinearLayout layoutDark = talent_view.findViewById(R.id.layoutDark);
                    TextView txtDarkMaterial = talent_view.findViewById(R.id.txtDarkMaterial);

                    txtName.setText(talentItems.get(position));
                    txtUndo.setText(name);
                    txtNext.setText(talentItems.get(position));

                    talentDBAdapter.open();
                    txtContent.setText(transformString(talentDBAdapter.findContent(talentItems.get(position))));
                    talentDBAdapter.close();

                    int resource;
                    for (int i = 0; i < txtMaterialName.length; i++) {
                        resource = talent_view.getResources().getIdentifier("txtMaterialName"+(i+1), "id", getPackageName());
                        txtMaterialName[i] = talent_view.findViewById(resource);
                        resource = talent_view.getResources().getIdentifier("txtMaterial"+(i+1), "id", getPackageName());
                        txtMaterial[i] = talent_view.findViewById(resource);
                    }

                    // first : 85, second : 61, third : 41
                    String[] material_limit;
                    if (isWeapon(type)) {
                        material_limit = new String[]{"총몸부품", "강철", "탄소섬유"};
                    } else if (isSheldAType(type)) {
                        material_limit = new String[]{"보호용 옷감", "세라믹", "전자부품"};
                    } else {
                        material_limit = new String[]{"보호용 옷감", "폴리카보네이트", "티타늄"};
                    }

                    edit_possible = true;
                    int[] count = new int[3];
                    materialDbAdapter.open();
                    for (int i = 0; i < material_limit.length; i++) {
                        cursor = materialDbAdapter.fetchMaterial(material_limit[i]);
                        count[i] = cursor.getInt(2);
                        txtMaterialName[i].setText(material_limit[i]);
                        txtMaterial[i].setText(Integer.toString(count[i]));
                    }
                    int dark_count = 0;
                    if (darked) {
                        layoutDark.setVisibility(View.VISIBLE);
                        cursor = materialDbAdapter.fetchMaterial("다크존 자원");
                        dark_count = cursor.getInt(2);
                        txtDarkMaterial.setText(Integer.toString(dark_count));
                    }
                    materialDbAdapter.close();

                    if (count[0] < 85) {
                        txtMaterial[0].setTextColor(Color.parseColor("#f04d52"));
                        edit_possible = false;
                    }
                    if (count[1] < 61) {
                        txtMaterial[1].setTextColor(Color.parseColor("#f04d52"));
                        edit_possible = false;
                    }
                    if (count[2] < 41) {
                        txtMaterial[2].setTextColor(Color.parseColor("#f04d52"));
                        edit_possible = false;
                    }
                    if (darked && dark_count < 5) {
                        txtDarkMaterial.setTextColor(Color.parseColor("#f04d52"));
                        edit_possible = false;
                    }

                    final String[] final_material_limit = material_limit;
                    final int[] final_count = count;
                    final int[] dark_cnt = {dark_count};
                    final int index = position;

                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edit_possible) {
                                if (name.equals(talentItems.get(position))) {
                                    Toast.makeText(getApplicationContext(), "바꾸실 특수효과와 변경전 특수효과와 동일합니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    alertDialog.dismiss();
                                    inventoryDBAdapter.open();
                                    if (inventoryDBAdapter.isFavorite(rowID)) {
                                        View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                                        TextView txtContent = view.findViewById(R.id.txtContent);
                                        Button btnCancel = view.findViewById(R.id.btnCancel);
                                        Button btnOK = view.findViewById(R.id.btnOK);

                                        btnOK.setText("보정");
                                        txtContent.setText("즐겨찾기로 지정된 아이템입니다. 정말로 보정하시겠습니까?");

                                        btnOK.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                materialDbAdapter.open();
                                                final_count[0] -= 85;
                                                final_count[1] -= 61;
                                                final_count[2] -= 41;
                                                if (darked) {
                                                    dark_cnt[0] -= 20;
                                                    materialDbAdapter.updateMaterial("다크존 자원", dark_cnt[0]);
                                                }
                                                for (int i = 0; i < final_material_limit.length; i++) {
                                                    materialDbAdapter.updateMaterial(final_material_limit[i], final_count[i]);
                                                }
                                                materialDbAdapter.close();
                                                Toast.makeText(getApplicationContext(), name+"에서 "+talentItems.get(index)+"로 보정되었습니다.", Toast.LENGTH_SHORT).show();
                                                inventoryDBAdapter.open();
                                                inventoryDBAdapter.updateEditData(rowID, false, false, false, true);
                                                inventoryDBAdapter.updateTalentData(rowID, talentItems.get(index));
                                                inventoryDBAdapter.close();
                                                alertDialog.dismiss();
                                                finish();
                                            }
                                        });

                                        btnCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.dismiss();
                                            }
                                        });

                                        builder = new AlertDialog.Builder(ItemEditActivity.this);
                                        builder.setView(view);

                                        alertDialog = builder.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertDialog.show();
                                    } else {
                                        materialDbAdapter.open();
                                        final_count[0] -= 85;
                                        final_count[1] -= 61;
                                        final_count[2] -= 41;
                                        if (darked) {
                                            dark_cnt[0] -= 20;
                                            materialDbAdapter.updateMaterial("다크존 자원", dark_cnt[0]);
                                        }
                                        for (int i = 0; i < final_material_limit.length; i++) {
                                            materialDbAdapter.updateMaterial(final_material_limit[i], final_count[i]);
                                        }
                                        materialDbAdapter.close();
                                        Toast.makeText(getApplicationContext(), name+"에서 "+talentItems.get(index)+"로 보정되었습니다.", Toast.LENGTH_SHORT).show();
                                        inventoryDBAdapter.open();
                                        inventoryDBAdapter.updateEditData(rowID, false, false, false, true);
                                        inventoryDBAdapter.updateTalentData(rowID, talentItems.get(index));
                                        inventoryDBAdapter.close();
                                        finish();
                                    }
                                    inventoryDBAdapter.close();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "재료가 부족합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnExit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    builder = new AlertDialog.Builder(ItemEditActivity.this);
                    builder.setView(talent_view);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    alertDialog.show();
                }
            });
        } else {
            for (int i = 0; i < weaponList.length; i++) {
                if (weaponList[i].equals(type)) weaponed = true;
            }
            String end = "";
            double max = 0;
            maxDBAdapter.open();
            if (maxDBAdapter.notWeaponCore(name)) {
                switch (option_type) {
                    case "weapon_core1":
                        cursor = maxDBAdapter.fetchTypeData("무기");
                        break;
                    case "weapon_core2":
                        cursor = maxDBAdapter.fetchTypeData(type);
                        break;
                    case "weapon_sub":
                        cursor = maxDBAdapter.fetchSubData(name);
                        break;
                    case "sheld_core":
                        cursor = maxDBAdapter.fetchSheldCoreData(name);
                        break;
                    case "sheld_sub1":
                    case "sheld_sub2":
                        cursor = maxDBAdapter.fetchSheldSubData(name);
                        break;
                    default:
                        cursor = maxDBAdapter.fetchTypeData("무기");
                }
                end = cursor.getString(5);
                if (end.equals("-")) end = "";
                max = cursor.getDouble(2);
            } else {
                end = "%";
                max = 15;
            }
            maxDBAdapter.close();
            if (weaponed) {
                layoutWeapon.setVisibility(View.VISIBLE);
                txtWeaponOption.setText("+"+formatD(value)+end+" "+name);

                progressWeaponOption.setMax((int)(max*10));
                progressWeaponOption.setProgress((int)(value*10));
            } else {
                layoutSheld.setVisibility(View.VISIBLE);
                changeImage(imgSheldOption, name, progressSheldOption);
                txtSheldOption.setText("+"+formatD(value)+end+" "+name);
                progressSheldOption.setMax((int)(max*10));
                progressSheldOption.setProgress((int)(value*10));
            }
            listView.setVisibility(View.VISIBLE);
            switch (type) {
                case "돌격소총":
                    arLibraryDBAdapter.open();
                    switch (option_type) {
                        case "weapon_core1":
                            cursor = arLibraryDBAdapter.fetchTypeData("무기");
                            core = true;
                            break;
                        case "weapon_core2":
                            cursor = arLibraryDBAdapter.fetchTypeData(type);
                            core = true;
                            break;
                        case "weapon_sub":
                            cursor = arLibraryDBAdapter.fetchTypeData(type);
                            core = false;
                            cursor = arLibraryDBAdapter.fetchTypeData("무기 부속성");
                            break;
                    }
                    arLibraryDBAdapter.close();
                    break;
                case "기관단총":
                    srLibraryDBAdapter.open();
                    switch (option_type) {
                        case "weapon_core1":
                            cursor = srLibraryDBAdapter.fetchTypeData("무기");
                            core = true;
                            break;
                        case "weapon_core2":
                            cursor = srLibraryDBAdapter.fetchTypeData(type);
                            core = true;
                            break;
                        case "weapon_sub":
                            cursor = srLibraryDBAdapter.fetchTypeData(type);
                            core = false;
                            cursor = srLibraryDBAdapter.fetchTypeData("무기 부속성");
                            break;
                    }
                    srLibraryDBAdapter.close();
                    break;
                case "경기관총":
                    brLibraryDBAdapter.open();
                    switch (option_type) {
                        case "weapon_core1":
                            cursor = brLibraryDBAdapter.fetchTypeData("무기");
                            core = true;
                            break;
                        case "weapon_core2":
                            cursor = brLibraryDBAdapter.fetchTypeData(type);
                            core = true;
                            break;
                        case "weapon_sub":
                            cursor = brLibraryDBAdapter.fetchTypeData(type);
                            core = false;
                            cursor = brLibraryDBAdapter.fetchTypeData("무기 부속성");
                            break;
                    }
                    brLibraryDBAdapter.close();
                    break;
                case "지정사수소총":
                    mmrLibraryDBAdapter.open();
                    switch (option_type) {
                        case "weapon_core1":
                            cursor = mmrLibraryDBAdapter.fetchTypeData("무기");
                            core = true;
                            break;
                        case "weapon_core2":
                            cursor = mmrLibraryDBAdapter.fetchTypeData(type);
                            core = true;
                            break;
                        case "weapon_sub":
                            cursor = mmrLibraryDBAdapter.fetchTypeData(type);
                            core = false;
                            cursor = mmrLibraryDBAdapter.fetchTypeData("무기 부속성");
                            break;
                    }
                    mmrLibraryDBAdapter.close();
                    break;
                case "소총":
                    rfLibraryDBAdapter.open();
                    switch (option_type) {
                        case "weapon_core1":
                            cursor = rfLibraryDBAdapter.fetchTypeData("무기");
                            core = true;
                            break;
                        case "weapon_core2":
                            cursor = rfLibraryDBAdapter.fetchTypeData(type);
                            core = true;
                            break;
                        case "weapon_sub":
                            cursor = rfLibraryDBAdapter.fetchTypeData(type);
                            core = false;
                            cursor = rfLibraryDBAdapter.fetchTypeData("무기 부속성");
                            break;
                    }
                    rfLibraryDBAdapter.close();
                    break;
                case "권총":
                    ptLibraryDBAdapter.open();
                    switch (option_type) {
                        case "weapon_core1":
                            cursor = ptLibraryDBAdapter.fetchTypeData("무기");
                            core = true;
                            break;
                        case "weapon_sub":
                            cursor = ptLibraryDBAdapter.fetchTypeData(type);
                            core = false;
                            cursor = ptLibraryDBAdapter.fetchTypeData("무기 부속성");
                            break;
                    }
                    ptLibraryDBAdapter.close();
                    break;
                case "산탄총":
                    sgLibraryDBAdapter.open();
                    switch (option_type) {
                        case "weapon_core1":
                            cursor = sgLibraryDBAdapter.fetchTypeData("무기");
                            core = true;
                            break;
                        case "weapon_core2":
                            cursor = sgLibraryDBAdapter.fetchTypeData(type);
                            core = true;
                            break;
                        case "weapon_sub":
                            cursor = sgLibraryDBAdapter.fetchTypeData(type);
                            core = false;
                            cursor = sgLibraryDBAdapter.fetchTypeData("무기 부속성");
                            break;
                    }
                    sgLibraryDBAdapter.close();
                    break;
                case "조끼":
                    vestLibraryDBAdapter.open();
                    switch (option_type) {
                        case "sheld_core":
                            cursor = vestLibraryDBAdapter.fetchTypeData("보호장구 핵심속성");
                            core = true;
                            break;
                        case "sheld_sub1":
                        case "sheld_sub2":
                            remove_option = other_name;
                            cursor = vestLibraryDBAdapter.fetchTypeData("보호장구 부속성");
                            core = false;
                            break;
                    }
                    vestLibraryDBAdapter.close();
                    break;
                case "권총집":
                    holsterLibraryDBAdapter.open();
                    switch (option_type) {
                        case "sheld_core":
                            cursor = holsterLibraryDBAdapter.fetchTypeData("보호장구 핵심속성");
                            core = true;
                            break;
                        case "sheld_sub1":
                        case "sheld_sub2":
                            remove_option = other_name;
                            cursor = holsterLibraryDBAdapter.fetchTypeData("보호장구 부속성");
                            core = false;
                            break;
                    }
                    holsterLibraryDBAdapter.close();
                    break;
                case "마스크":
                    maskLibraryDBAdapter.open();
                    switch (option_type) {
                        case "sheld_core":
                            cursor = maskLibraryDBAdapter.fetchTypeData("보호장구 핵심속성");
                            core = true;
                            break;
                        case "sheld_sub1":
                        case "sheld_sub2":
                            remove_option = other_name;
                            cursor = maskLibraryDBAdapter.fetchTypeData("보호장구 부속성");
                            core = false;
                            break;
                    }
                    maskLibraryDBAdapter.close();
                    break;
                case "장갑":
                    gloveLibraryDBAdapter.open();
                    switch (option_type) {
                        case "sheld_core":
                            cursor = gloveLibraryDBAdapter.fetchTypeData("보호장구 핵심속성");
                            core = true;
                            break;
                        case "sheld_sub1":
                        case "sheld_sub2":
                            remove_option = other_name;
                            cursor = gloveLibraryDBAdapter.fetchTypeData("보호장구 부속성");
                            core = false;
                            break;
                    }
                    gloveLibraryDBAdapter.close();
                    break;
                case "무릎보호대":
                    kneepedLibraryDBAdapter.open();
                    switch (option_type) {
                        case "sheld_core":
                            cursor = kneepedLibraryDBAdapter.fetchTypeData("보호장구 핵심속성");
                            core = true;
                            break;
                        case "sheld_sub1":
                        case "sheld_sub2":
                            remove_option = other_name;
                            cursor = kneepedLibraryDBAdapter.fetchTypeData("보호장구 부속성");
                            core = false;
                            break;
                    }
                    kneepedLibraryDBAdapter.close();
                    break;
                case "백팩":
                    backpackLibraryDBAdapter.open();
                    switch (option_type) {
                        case "sheld_core":
                            cursor = backpackLibraryDBAdapter.fetchTypeData("보호장구 핵심속성");
                            core = true;
                            break;
                        case "sheld_sub1":
                        case "sheld_sub2":
                            remove_option = other_name;
                            cursor = backpackLibraryDBAdapter.fetchTypeData("보호장구 부속성");
                            core = false;
                            break;
                    }
                    backpackLibraryDBAdapter.close();
                    break;
            }
            /*switch (option_type) {
                case "weapon_core1":
                    cursor = arLibraryDBAdapter.fetchTypeData("무기");
                    core = true;
                    break;
                case "weapon_core2":
                    cursor = arLibraryDBAdapter.fetchTypeData(type);
                    core = true;
                    break;
                case "weapon_sub":
                    if (!type.equals("권총")) {
                        cursor = arLibraryDBAdapter.fetchTypeData(type);
                        remove_option = cursor.getString(1);
                    } else {
                        remove_option = "";
                    }
                    core = false;
                    cursor = arLibraryDBAdapter.fetchTypeData("무기 부속성");
                    break;
                case "sheld_core":
                    cursor = arLibraryDBAdapter.fetchTypeData("보호장구 핵심속성");
                    core = true;
                    break;
                case "sheld_sub1":
                case "sheld_sub2":
                    remove_option = other_name;
                    cursor = arLibraryDBAdapter.fetchTypeData("보호장구 부속성");
                    core = false;
                    break;
            }*/
            while (!cursor.isAfterLast()) {
                if (!cursor.getString(1).equals(remove_option)) {
                    EditItem item = new EditItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                    editItems.add(item);
                }
                cursor.moveToNext();
            }
            editAdapter = new EditAdapter(this, editItems, null, false, option_type, type, core);
            listView.setAdapter(editAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final View seek_view = getLayoutInflater().inflate(R.layout.editseeker, null);

                    TextView txtName = seek_view.findViewById(R.id.txtName);
                    final TextView txtValue = seek_view.findViewById(R.id.txtValue);
                    final SeekBar seekBar = seek_view.findViewById(R.id.seekBar);
                    TextView txtEnd = seek_view.findViewById(R.id.txtEnd);
                    Button btnOK = seek_view.findViewById(R.id.btnOK);
                    Button btnExit = seek_view.findViewById(R.id.btnExit);
                    TextView txtUndo = seek_view.findViewById(R.id.txtUndo);
                    TextView txtNext = seek_view.findViewById(R.id.txtNext);

                    TextView[] txtMaterialName = new TextView[3];
                    TextView[] txtMaterial = new TextView[3];

                    LinearLayout layoutDark = seek_view.findViewById(R.id.layoutDark);
                    TextView txtDarkMaterial = seek_view.findViewById(R.id.txtDarkMaterial);

                    int resource;
                    for (int i = 0; i < txtMaterialName.length; i++) {
                        resource = seek_view.getResources().getIdentifier("txtMaterialName"+(i+1), "id", getPackageName());
                        txtMaterialName[i] = seek_view.findViewById(resource);
                        resource = seek_view.getResources().getIdentifier("txtMaterial"+(i+1), "id", getPackageName());
                        txtMaterial[i] = seek_view.findViewById(resource);
                    }
                    // first : 85, second : 61, third : 41
                    String[] material_limit;
                    if (isWeapon(type)) {
                        material_limit = new String[]{"총몸부품", "강철", "탄소섬유"};
                    } else if (isSheldAType(type)) {
                        material_limit = new String[]{"보호용 옷감", "세라믹", "전자부품"};
                    } else {
                        material_limit = new String[]{"보호용 옷감", "폴리카보네이트", "티타늄"};
                    }

                    edit_possible = true;
                    int[] count = new int[3];
                    materialDbAdapter.open();
                    for (int i = 0; i < material_limit.length; i++) {
                        cursor = materialDbAdapter.fetchMaterial(material_limit[i]);
                        count[i] = cursor.getInt(2);
                        txtMaterialName[i].setText(material_limit[i]);
                        txtMaterial[i].setText(Integer.toString(count[i]));
                    }
                    int dark_count = 0;
                    if (darked) {
                        layoutDark.setVisibility(View.VISIBLE);
                        cursor = materialDbAdapter.fetchMaterial("다크존 자원");
                        dark_count = cursor.getInt(2);
                        txtDarkMaterial.setText(Integer.toString(dark_count));
                    }
                    materialDbAdapter.close();

                    if (count[0] < 85) {
                        txtMaterial[0].setTextColor(Color.parseColor("#f04d52"));
                        edit_possible = false;
                    }
                    if (count[1] < 61) {
                        txtMaterial[1].setTextColor(Color.parseColor("#f04d52"));
                        edit_possible = false;
                    }
                    if (count[2] < 41) {
                        txtMaterial[2].setTextColor(Color.parseColor("#f04d52"));
                        edit_possible = false;
                    }
                    if (darked && dark_count < 5) {
                        txtDarkMaterial.setTextColor(Color.parseColor("#f04d52"));
                        edit_possible = false;
                    }

                    final String[] final_material_limit = material_limit;
                    final int[] final_count = count;
                    final int[] dark_cnt = {dark_count};

                    maxDBAdapter.open();
                    String str;
                    if (option_type.equals("weapon_core1")) cursor = maxDBAdapter.fetchData("무기군 기본 데미지");
                    else cursor = maxDBAdapter.fetchData(editItems.get(position).getName());
                    String end = cursor.getString(5);
                    if (end.equals("-")) end = "";
                    maxDBAdapter.close();

                    txtName.setText(editItems.get(position).getName());
                    txtValue.setText(formatD(editItems.get(position).getMax()));
                    seekBar.setMax((int)(editItems.get(position).getMax()*10));
                    seekBar.setProgress((int)(editItems.get(position).getMax()*10));
                    if (editItems.get(position).getName().equals("스킬 등급")) {
                        seekBar.setMax(10);
                        seekBar.setMin(10);
                        seekBar.setProgress(10);
                    }
                    txtEnd.setText(end);

                    txtUndo.setText(name);
                    txtNext.setText(editItems.get(position).getName());

                    changeThumb(seekBar, editItems.get(position).getName());

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            txtValue.setText(formatD((double)progress/10));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    final int index = position;

                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edit_possible) {
                                alertDialog.dismiss();
                                inventoryDBAdapter.open();
                                if (inventoryDBAdapter.isFavorite(rowID)) {
                                    View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                                    TextView txtContent = view.findViewById(R.id.txtContent);
                                    Button btnCancel = view.findViewById(R.id.btnCancel);
                                    Button btnOK = view.findViewById(R.id.btnOK);

                                    txtContent.setText("즐겨찾기로 지정된 아이템입니다. 정말로 보정하시겠습니까?");
                                    btnOK.setText("보정");

                                    btnOK.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (seekBar.getProgress() == 0) {
                                                Toast.makeText(getApplicationContext(), "보정할 수치가 0입니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                materialDbAdapter.open();
                                                final_count[0] -= 85;
                                                final_count[1] -= 61;
                                                final_count[2] -= 41;
                                                if (darked) {
                                                    dark_cnt[0] -= 20;
                                                    materialDbAdapter.updateMaterial("다크존 자원", dark_cnt[0]);
                                                }
                                                for (int i = 0; i < final_material_limit.length; i++) {
                                                    materialDbAdapter.updateMaterial(final_material_limit[i], final_count[i]);
                                                }
                                                materialDbAdapter.close();
                                            }

                                            Toast.makeText(getApplicationContext(), name+"에서 "+editItems.get(index).getName()+"으로 보정되었습니다.", Toast.LENGTH_SHORT).show();
                                            inventoryDBAdapter.open();
                                            switch (option_type) {
                                                case "weapon_core1":
                                                    inventoryDBAdapter.updateEditData(rowID, true, false, false, false);
                                                    inventoryDBAdapter.updateCore1Data(rowID, type+" 데미지", (double)seekBar.getProgress()/10);
                                                    break;
                                                case "sheld_core":
                                                    inventoryDBAdapter.updateEditData(rowID, true, false, false, false);
                                                    inventoryDBAdapter.updateCore1Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                                    break;
                                                case "weapon_core2":
                                                    inventoryDBAdapter.updateEditData(rowID, false, true, false, false);
                                                    inventoryDBAdapter.updateCore2Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                                    break;
                                                case "weapon_sub":
                                                    inventoryDBAdapter.updateEditData(rowID, false, false, true, false);
                                                    inventoryDBAdapter.updateSub1Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                                    break;
                                                case "sheld_sub1":
                                                    inventoryDBAdapter.updateEditData(rowID, false, true, false, false);
                                                    inventoryDBAdapter.updateSub1Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                                    break;
                                                case "sheld_sub2":
                                                    inventoryDBAdapter.updateEditData(rowID, false, false, true, false);
                                                    inventoryDBAdapter.updateSub2Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                                    break;
                                            }

                                            inventoryDBAdapter.close();
                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    });

                                    btnCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    builder = new AlertDialog.Builder(ItemEditActivity.this);
                                    builder.setView(view);

                                    alertDialog = builder.create();
                                    alertDialog.setCancelable(false);
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    alertDialog.show();
                                } else {
                                    if (seekBar.getProgress() == 0) {
                                        Toast.makeText(getApplicationContext(), "보정할 수치가 0입니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        materialDbAdapter.open();
                                        final_count[0] -= 85;
                                        final_count[1] -= 61;
                                        final_count[2] -= 41;
                                        if (darked) {
                                            dark_cnt[0] -= 20;
                                            materialDbAdapter.updateMaterial("다크존 자원", dark_cnt[0]);
                                        }
                                        for (int i = 0; i < final_material_limit.length; i++) {
                                            materialDbAdapter.updateMaterial(final_material_limit[i], final_count[i]);
                                        }
                                        materialDbAdapter.close();
                                    }

                                    Toast.makeText(getApplicationContext(), name+"에서 "+editItems.get(index).getName()+"으로 보정되었습니다.", Toast.LENGTH_SHORT).show();
                                    inventoryDBAdapter.open();
                                    switch (option_type) {
                                        case "weapon_core1":
                                            inventoryDBAdapter.updateEditData(rowID, true, false, false, false);
                                            inventoryDBAdapter.updateCore1Data(rowID, type+" 데미지", (double)seekBar.getProgress()/10);
                                            break;
                                        case "sheld_core":
                                            inventoryDBAdapter.updateEditData(rowID, true, false, false, false);
                                            inventoryDBAdapter.updateCore1Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                            break;
                                        case "weapon_core2":
                                            inventoryDBAdapter.updateEditData(rowID, false, true, false, false);
                                            inventoryDBAdapter.updateCore2Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                            break;
                                        case "weapon_sub":
                                            inventoryDBAdapter.updateEditData(rowID, false, false, true, false);
                                            inventoryDBAdapter.updateSub1Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                            break;
                                        case "sheld_sub1":
                                            inventoryDBAdapter.updateEditData(rowID, false, true, false, false);
                                            inventoryDBAdapter.updateSub1Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                            break;
                                        case "sheld_sub2":
                                            inventoryDBAdapter.updateEditData(rowID, false, false, true, false);
                                            inventoryDBAdapter.updateSub2Data(rowID, editItems.get(index).getName(), (double)seekBar.getProgress()/10);
                                            break;
                                    }

                                    inventoryDBAdapter.close();
                                    finish();
                                }
                                inventoryDBAdapter.close();
                            } else {
                                Toast.makeText(getApplicationContext(), "재료가 부족합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnExit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    builder = new AlertDialog.Builder(ItemEditActivity.this);
                    builder.setView(seek_view);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                }
            });
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.builderdialoglayout, null);

                TextView txtContent = view.findViewById(R.id.txtContent);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                Button btnOK = view.findViewById(R.id.btnOK);

                btnOK.setText("추가");
                txtContent.setText(name+"을(를) 보정 라이브러리에 추가합니까?");

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (!talented) {
                            double max;
                            switch (type) {
                                case "돌격소총":
                                    arLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "weapon_core1":
                                            cursor = arLibraryDBAdapter.fetchTypeData("무기");
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                arLibraryDBAdapter.updateTypeData("무기", "돌격소총 데미지", Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_core2":
                                            cursor = arLibraryDBAdapter.fetchTypeData(type);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                arLibraryDBAdapter.updateTypeData(type, name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_sub":
                                            cursor = arLibraryDBAdapter.fetchSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                arLibraryDBAdapter.updateSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    arLibraryDBAdapter.close();
                                    break;
                                case "기관단총":
                                    srLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "weapon_core1":
                                            cursor = srLibraryDBAdapter.fetchTypeData("무기");
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                srLibraryDBAdapter.updateTypeData("무기", "기관단총 데미지", Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_core2":
                                            cursor = srLibraryDBAdapter.fetchTypeData(type);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                srLibraryDBAdapter.updateTypeData(type, name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_sub":
                                            cursor = srLibraryDBAdapter.fetchSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                srLibraryDBAdapter.updateSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    srLibraryDBAdapter.close();
                                    break;
                                case "경기관총":
                                    brLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "weapon_core1":
                                            cursor = brLibraryDBAdapter.fetchTypeData("무기");
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                brLibraryDBAdapter.updateTypeData("무기", "경기관총 데미지", Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_core2":
                                            cursor = brLibraryDBAdapter.fetchTypeData(type);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                brLibraryDBAdapter.updateTypeData(type, name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_sub":
                                            cursor = brLibraryDBAdapter.fetchSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                brLibraryDBAdapter.updateSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    brLibraryDBAdapter.close();
                                    break;
                                case "지정사수소총":
                                    mmrLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "weapon_core1":
                                            cursor = mmrLibraryDBAdapter.fetchTypeData("무기");
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                mmrLibraryDBAdapter.updateTypeData("무기", "지정사수소총 데미지", Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_core2":
                                            cursor = mmrLibraryDBAdapter.fetchTypeData(type);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                mmrLibraryDBAdapter.updateTypeData(type, name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_sub":
                                            cursor = mmrLibraryDBAdapter.fetchSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                mmrLibraryDBAdapter.updateSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    mmrLibraryDBAdapter.close();
                                    break;
                                case "소총":
                                    rfLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "weapon_core1":
                                            cursor = rfLibraryDBAdapter.fetchTypeData("무기");
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                rfLibraryDBAdapter.updateTypeData("무기", "소총 데미지", Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_core2":
                                            cursor = rfLibraryDBAdapter.fetchTypeData(type);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                rfLibraryDBAdapter.updateTypeData(type, name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_sub":
                                            cursor = rfLibraryDBAdapter.fetchSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                rfLibraryDBAdapter.updateSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    rfLibraryDBAdapter.close();
                                    break;
                                case "권총":
                                    ptLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "weapon_core1":
                                            cursor = ptLibraryDBAdapter.fetchTypeData("무기");
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                ptLibraryDBAdapter.updateTypeData("무기", "권총 데미지", Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_sub":
                                            cursor = ptLibraryDBAdapter.fetchSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                ptLibraryDBAdapter.updateSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    ptLibraryDBAdapter.close();
                                    break;
                                case "산탄총":
                                    sgLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "weapon_core1":
                                            cursor = sgLibraryDBAdapter.fetchTypeData("무기");
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                sgLibraryDBAdapter.updateTypeData("무기", "산탄총 데미지", Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_core2":
                                            cursor = sgLibraryDBAdapter.fetchTypeData(type);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                sgLibraryDBAdapter.updateTypeData(type, name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "weapon_sub":
                                            cursor = sgLibraryDBAdapter.fetchSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max)
                                                sgLibraryDBAdapter.updateSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    sgLibraryDBAdapter.close();
                                    break;
                                case "조끼":
                                    vestLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "sheld_core":
                                            cursor = vestLibraryDBAdapter.fetchSheldCoreData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) vestLibraryDBAdapter.updateSheldCoreData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "sheld_sub1":
                                        case "sheld_sub2":
                                            cursor = vestLibraryDBAdapter.fetchSheldSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) vestLibraryDBAdapter.updateSheldSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    vestLibraryDBAdapter.close();
                                    break;
                                case "권총집":
                                    holsterLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "sheld_core":
                                            cursor = holsterLibraryDBAdapter.fetchSheldCoreData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) holsterLibraryDBAdapter.updateSheldCoreData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "sheld_sub1":
                                        case "sheld_sub2":
                                            cursor = holsterLibraryDBAdapter.fetchSheldSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) holsterLibraryDBAdapter.updateSheldSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    holsterLibraryDBAdapter.close();
                                    break;
                                case "마스크":
                                    maskLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "sheld_core":
                                            cursor = maskLibraryDBAdapter.fetchSheldCoreData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) maskLibraryDBAdapter.updateSheldCoreData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "sheld_sub1":
                                        case "sheld_sub2":
                                            cursor = maskLibraryDBAdapter.fetchSheldSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) maskLibraryDBAdapter.updateSheldSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    maskLibraryDBAdapter.close();
                                    break;
                                case "장갑":
                                    gloveLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "sheld_core":
                                            cursor = gloveLibraryDBAdapter.fetchSheldCoreData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) gloveLibraryDBAdapter.updateSheldCoreData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "sheld_sub1":
                                        case "sheld_sub2":
                                            cursor = gloveLibraryDBAdapter.fetchSheldSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) gloveLibraryDBAdapter.updateSheldSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    gloveLibraryDBAdapter.close();
                                    break;
                                case "무릎보호대":
                                    kneepedLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "sheld_core":
                                            cursor = kneepedLibraryDBAdapter.fetchSheldCoreData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) kneepedLibraryDBAdapter.updateSheldCoreData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "sheld_sub1":
                                        case "sheld_sub2":
                                            cursor = kneepedLibraryDBAdapter.fetchSheldSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) kneepedLibraryDBAdapter.updateSheldSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    kneepedLibraryDBAdapter.close();
                                    break;
                                case "백팩":
                                    backpackLibraryDBAdapter.open();
                                    switch (option_type) {
                                        case "sheld_core":
                                            cursor = backpackLibraryDBAdapter.fetchSheldCoreData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) backpackLibraryDBAdapter.updateSheldCoreData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                        case "sheld_sub1":
                                        case "sheld_sub2":
                                            cursor = backpackLibraryDBAdapter.fetchSheldSubData(name);
                                            max = Double.parseDouble(cursor.getString(2));
                                            if (value > max) backpackLibraryDBAdapter.updateSheldSubData(name, Double.toString(value));
                                            else {
                                                Toast.makeText(getApplicationContext(), "이미 더 높은 옵션으로 저장되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            break;
                                    }
                                    backpackLibraryDBAdapter.close();
                                    break;
                            }
                        } else {
                            switch (type) {
                                case "돌격소총":
                                    arTalentDBAdapter.open();
                                    if (arTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        arTalentDBAdapter.saveTalent(name);
                                    }
                                    arTalentDBAdapter.close();
                                    break;
                                case "기관단총":
                                    srTalentDBAdapter.open();
                                    if (srTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        srTalentDBAdapter.saveTalent(name);
                                    }
                                    srTalentDBAdapter.close();
                                    break;
                                case "경기관총":
                                    brTalentDBAdapter.open();
                                    if (brTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        brTalentDBAdapter.saveTalent(name);
                                    }
                                    brTalentDBAdapter.close();
                                    break;
                                case "지정사수소총":
                                    mmrTalentDBAdapter.open();
                                    if (mmrTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        mmrTalentDBAdapter.saveTalent(name);
                                    }
                                    mmrTalentDBAdapter.close();
                                    break;
                                case "소총":
                                    rfTalentDBAdapter.open();
                                    if (rfTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        rfTalentDBAdapter.saveTalent(name);
                                    }
                                    rfTalentDBAdapter.close();
                                    break;
                                case "권총":
                                    ptTalentDBAdapter.open();
                                    if (ptTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        ptTalentDBAdapter.saveTalent(name);
                                    }
                                    ptTalentDBAdapter.close();
                                    break;
                                case "산탄총":
                                    sgTalentDBAdapter.open();
                                    if (sgTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        sgTalentDBAdapter.saveTalent(name);
                                    }
                                    sgTalentDBAdapter.close();
                                    break;
                                case "조끼":
                                    vestTalentDBAdapter.open();
                                    if (vestTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        vestTalentDBAdapter.saveTalent(name);
                                    }
                                    vestTalentDBAdapter.close();
                                    break;
                                case "백팩":
                                    backpackTalentDBAdapter.open();
                                    if (backpackTalentDBAdapter.haveTalent(name)) {
                                        Toast.makeText(getApplicationContext(), "이미 라이브러리에 존재합니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        backpackTalentDBAdapter.saveTalent(name);
                                    }
                                    backpackTalentDBAdapter.close();
                                    break;
                            }
                        }
                        inventoryDBAdapter.open();
                        inventoryDBAdapter.deleteData(rowID);
                        inventoryDBAdapter.close();
                        Toast.makeText(getApplicationContext(), name+"을(를) 보정 라이브러리에 추가하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                builder = new AlertDialog.Builder(ItemEditActivity.this);
                builder.setView(view);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        btnRessetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.exoticbuilddialog, null);
                final MaterialItem exoticItem = materialList.get(9);

                Button btnCancel = view.findViewById(R.id.btnExit);
                Button btnBuild = view.findViewById(R.id.btnBuild);
                ImageView imgType = view.findViewById(R.id.imgType);
                TextView txtName = view.findViewById(R.id.txtName);
                TextView txtType = view.findViewById(R.id.txtType);
                TextView txtNowExotic = view.findViewById(R.id.txtNowExotic);

                txtName.setText(exoticname);
                txtType.setText(type);
                txtNowExotic.setText(Integer.toString(exoticItem.getCount()));
                if (exoticItem.getCount() < 1) txtNowExotic.setTextColor(Color.parseColor("#FF0000"));
                else txtNowExotic.setTextColor(Color.parseColor("#F0F0F0"));

                switch (type) {
                    case "돌격소총":
                        imgType.setImageResource(R.drawable.wp1custom);
                        break;
                    case "소총":
                        imgType.setImageResource(R.drawable.wp2custom);
                        break;
                    case "지정사수소총":
                        imgType.setImageResource(R.drawable.wp3custom);
                        break;
                    case "기관단총":
                        imgType.setImageResource(R.drawable.wp4custom);
                        break;
                    case "경기관총":
                        imgType.setImageResource(R.drawable.wp5custom);
                        break;
                    case "산탄총":
                        imgType.setImageResource(R.drawable.wp6custom);
                        break;
                    case "권총":
                        imgType.setImageResource(R.drawable.wp7custom);
                        break;
                    case "마스크":
                        imgType.setImageResource(R.drawable.sd1custom);
                        break;
                    case "조끼":
                        imgType.setImageResource(R.drawable.sd2custom);
                        break;
                    case "권총집":
                        imgType.setImageResource(R.drawable.sd3custom);
                        break;
                    case "백팩":
                        imgType.setImageResource(R.drawable.sd4custom);
                        break;
                    case "장갑":
                        imgType.setImageResource(R.drawable.sd5custom);
                        break;
                    case "무릎보호대":
                        imgType.setImageResource(R.drawable.sd6custom);
                        break;
                }

                btnBuild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (exoticItem.getCount() <= 0) {
                            Toast.makeText(getApplicationContext(), "특급 부품이 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            int count = exoticItem.getCount();
                            count--;
                            exoticItem.setCount(count);
                            materialDbAdapter.open();
                            materialDbAdapter.updateMaterial(exoticItem.getName(), exoticItem.getCount());
                            materialDbAdapter.close();
                        }

                        double max;
                        String core1, core2, sub1, sub2, temp, weapon_type;
                        double core1_value, core2_value, sub1_value, sub2_value;
                        boolean weapon_t = false;
                        int pick;
                        double temp_percent;

                        inventoryDBAdapter.open();
                        cursor = inventoryDBAdapter.fetchIDData(rowID);
                        weapon_type = cursor.getString(2);
                        inventoryDBAdapter.close();
                        for (int i = 0; i < weaponList.length; i++) {
                            if (weaponList[i].equals(weapon_type)) weapon_t = true;
                        }
                        if (weapon_t) {
                            inventoryDBAdapter.open();
                            cursor = inventoryDBAdapter.fetchIDData(rowID);
                            core1 = cursor.getString(3);
                            core2 = cursor.getString(4);
                            sub1 = cursor.getString(5);
                            inventoryDBAdapter.close();
                            maxDBAdapter.open();
                            cursor = maxDBAdapter.fetchTypeData("무기");
                            max = cursor.getDouble(2);
                            maxDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 30) temp_percent = 100;
                            else if (pick <= 60) temp_percent = percent(21, 10) + 70; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + 50; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core1_value = Math.floor(((double)max*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            if (!weapon_type.equals("권총")) {
                                maxDBAdapter.open();
                                cursor = maxDBAdapter.fetchTypeData(weapon_type);
                                max = cursor.getDouble(2);
                                maxDBAdapter.close();
                                pick = percent(1, 100);
                                if (pick <= 30) temp_percent = 100;
                                else if (pick <= 60) temp_percent = percent(21, 10) + 70; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                                else temp_percent = percent(1, 20) + 50; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                                core2_value = Math.floor(((double)max*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            } else {
                                core2_value = 0;
                            }
                            maxDBAdapter.open();
                            cursor = maxDBAdapter.fetchSubData(sub1);
                            max = cursor.getDouble(2);
                            maxDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 30) temp_percent = 100;
                            else if (pick <= 60) temp_percent = percent(21, 10) + 70; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + 50; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1_value = Math.floor(((double)max*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            inventoryDBAdapter.open();
                            inventoryDBAdapter.updateCore1Data(rowID, core1, core1_value);
                            if (!weapon_type.equals("권총")) inventoryDBAdapter.updateCore2Data(rowID, core2, core2_value);
                            inventoryDBAdapter.updateSub1Data(rowID, sub1, sub1_value);
                            inventoryDBAdapter.close();
                        } else {
                            inventoryDBAdapter.open();
                            cursor = inventoryDBAdapter.fetchIDData(rowID);
                            sub1 = cursor.getString(5);
                            sub2 = cursor.getString(6);
                            inventoryDBAdapter.close();
                            maxDBAdapter.open();
                            cursor = maxDBAdapter.fetchSheldSubData(sub1);
                            max = cursor.getDouble(2);
                            maxDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 30) temp_percent = 100;
                            else if (pick <= 60) temp_percent = percent(21, 10) + 70; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + 50; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub1_value = Math.floor(((double)max*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            maxDBAdapter.open();
                            cursor = maxDBAdapter.fetchSheldSubData(sub2);
                            max = cursor.getDouble(2);
                            maxDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 30) temp_percent = 100;
                            else if (pick <= 60) temp_percent = percent(21, 10) + 70; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + 50; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            sub2_value = Math.floor(((double)max*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
                            inventoryDBAdapter.open();
                            inventoryDBAdapter.updateSub1Data(rowID, sub1, sub1_value);
                            inventoryDBAdapter.updateSub2Data(rowID, sub2, sub2_value);
                            inventoryDBAdapter.close();
                        }
                        Toast.makeText(getApplicationContext(), "보정되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                builder = new AlertDialog.Builder(ItemEditActivity.this);
                builder.setView(view);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });
    }

    private void changeThumb(SeekBar seekBar, String name) {
        String str;
        maxDBAdapter.open();
        if (maxDBAdapter.isSheldCoreData(name)) {
            cursor = maxDBAdapter.fetchSheldCoreData(name);
            str = cursor.getString(4);
            switch (str) {
                case "공격":
                    seekBar.setThumb(getResources().getDrawable(R.drawable.attackthumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.attackseeker));
                    break;
                case "방어":
                    seekBar.setThumb(getResources().getDrawable(R.drawable.sheldthumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.sheldseeker));
                    break;
                case "다용도":
                    seekBar.setThumb(getResources().getDrawable(R.drawable.powerthumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.powerseeker));
                    break;
                default:
                    seekBar.setThumb(getResources().getDrawable(R.drawable.attackthumb));
            }
        } else if (maxDBAdapter.isSheldSubData(name)) {
            cursor = maxDBAdapter.fetchSheldSubData(name);
            str = cursor.getString(4);
            switch (str) {
                case "공격":
                    seekBar.setThumb(getResources().getDrawable(R.drawable.attackthumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.attackseeker));
                    break;
                case "방어":
                    seekBar.setThumb(getResources().getDrawable(R.drawable.sheldthumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.sheldseeker));
                    break;
                case "다용도":
                    seekBar.setThumb(getResources().getDrawable(R.drawable.powerthumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.powerseeker));
                    break;
                default:
                    seekBar.setThumb(getResources().getDrawable(R.drawable.attackthumb));
            }
        } else {
            seekBar.setThumb(getResources().getDrawable(R.drawable.attackthumb));
            seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.attackseeker));
        }
        maxDBAdapter.close();
    }

    private void changeImage(ImageView imgView, String name, ProgressBar progressBar) {
        String str;
        maxDBAdapter.open();
        if (maxDBAdapter.isSheldCoreData(name)) {
            cursor = maxDBAdapter.fetchSheldCoreData(name);
            str = cursor.getString(4);
            switch (str) {
                case "공격":
                    imgView.setImageResource(R.drawable.attack);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.attack_progress));
                    break;
                case "방어":
                    imgView.setImageResource(R.drawable.sheld);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.sheld_progress));
                    break;
                case "다용도":
                    imgView.setImageResource(R.drawable.power);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.power_progress));
                    break;
                default:
                    imgView.setImageResource(R.drawable.weaponicon);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.attack_progress));
            }
        } else if (maxDBAdapter.isSheldSubData(name)) {
            cursor = maxDBAdapter.fetchSheldSubData(name);
            str = cursor.getString(4);
            switch (str) {
                case "공격":
                    imgView.setImageResource(R.drawable.attack);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.attack_progress));
                    break;
                case "방어":
                    imgView.setImageResource(R.drawable.sheld);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.sheld_progress));
                    break;
                case "다용도":
                    imgView.setImageResource(R.drawable.power);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.power_progress));
                    break;
                default:
                    imgView.setImageResource(R.drawable.weaponicon);
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.attack_progress));
            }
        } else {
            imgView.setImageResource(R.drawable.weaponicon);
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_progressbar_gage));
        }
        maxDBAdapter.close();
    }

    private String formatD(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }

    private boolean isWeapon(String type) {
        switch (type) {
            case "돌격소총":
            case "소총":
            case "지정사수소총":
            case "산탄총":
            case "기관단총":
            case "경기관총":
            case "권총":
                return true;
        }
        return false;
    }

    private boolean isSheldAType(String type) {
        switch (type) {
            case "마스크":
            case "조끼":
            case "백팩":
                return true;
        }
        return false;
    }

    private boolean isSheldBType(String type) {
        switch (type) {
            case "권총집":
            case "장갑":
            case "무릎보호대":
                return true;
        }
        return false;
    }

    private int percent(int min, int length) {
        return (int)(Math.random()*12345678)%length + min;
    }

    private SpannableString transformString(String content) {
        SpannableString spannableString = new SpannableString(content);
        String word;
        int start, end;
        int find_index = 0;
        String[] changes = {"+", "-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "%", "m", "초", "번", "개", "명", "배", "배율", "발", "."};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        for (int i = 0; i < changes.length; i++) { //뉴욕의 지배자 확장팩 출시 후 등장한 엑조틱 장비들을 특급 색으로 변경해준다.
            find_index = 0;
            while(true) {
                word = changes[i]; //찾을 문자열에 새로운 특급 장비 이름을 넣는다. 반복문으로 모든 엑조틱과 비교가 된다.
                start = content.indexOf(word, find_index); //찾을 문자열과 같은 문자열을 찾게되면 시작 번호를 알려줘 start 변수에 대입한다.
                find_index = start+1;
                end = start + word.length(); //시작번호로부터 찾을 문자열의 길이를 추가해 끝번호를 찾는다.
                if (start > 0) {
                    if ((isFrontNumber(content, start) && changes[i].equals("초")) ||
                            (!changes[i].equals("초") && !changes[i].equals("번") && !changes[i].equals("개") && !changes[i].equals("명") && !changes[i].equals("배") && !changes[i].equals("발") && !changes[i].equals(".")) ||
                            (isFrontNumber(content, start) && changes[i].equals("번")) ||
                            (isFrontNumber(content, start) && changes[i].equals("개")) ||
                            (isFrontNumber(content, start) && changes[i].equals("배")) ||
                            (isFrontNumber(content, start) && changes[i].equals("발")) ||
                            (isFrontNumber(content, start) && changes[i].equals(".")) ||
                            (isFrontNumber(content, start) && changes[i].equals("명"))) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#B18912")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    break;
                }
            }
        }
        for (int i = 0; i < numbers.length; i++) {
            word = numbers[i]; //찾을 문자열에 새로운 특급 장비 이름을 넣는다. 반복문으로 모든 엑조틱과 비교가 된다.
            start = content.indexOf(word);
            end = start + word.length();
            if (start == 0) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#B18912")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    private boolean isFrontNumber(String content, int index) {
        String result = "";
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        if (index > 0) result = content.substring(index-1, index);
        else return false;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i].equals(result)) return true;
        }
        return false;
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
