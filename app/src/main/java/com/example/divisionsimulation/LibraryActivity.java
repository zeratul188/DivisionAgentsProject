package com.example.divisionsimulation;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.librarydatas.PTLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.PTTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.RFLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.RFTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.SGLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.SGTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.SRLibraryDBAdapter;
import com.example.divisionsimulation.dbdatas.TalentFMDBAdapter;
import com.example.divisionsimulation.librarydatas.SRTalentDBAdapter;
import com.example.divisionsimulation.librarydatas.VestLibraryDBAdapter;
import com.example.divisionsimulation.librarydatas.VestTalentDBAdapter;
import com.example.divisionsimulation.ui.tools.LibraryDBAdapter;
import com.example.divisionsimulation.ui.tools.TalentLibraryDBAdapter;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {
    private Cursor cursor;
    private ListView listView;
    private RadioGroup rgType, rgWeapon, rgSheld;
    private RadioButton[] rdoType = new RadioButton[8];
    private RadioButton[] rdoWeapon = new RadioButton[7];
    private RadioButton[] rdoSheld = new RadioButton[6];
    private LinearLayout layoutCount;
    private TextView txtCount, txtMaxCount;

    private String[] weapon_types = {"돌격소총", "기관단총", "경기관총", "소총", "지정사수소총", "산탄총", "권총"};
    private String[] sheld_types = {"마스크", "조끼", "권총집", "백팩", "장갑", "무릎보호대"};

    private ArrayList<LibraryItem> libraryItems;
    private ArrayList<String> talentItems;
    private LibraryAdapter libraryAdapter;

    private LibraryDBAdapter libraryDBAdapter;
    private TalentLibraryDBAdapter talentLibraryDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private MaxOptionsFMDBAdapter maxOptionsDBAdapter;

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

        listView = findViewById(R.id.listView);
        rgType = findViewById(R.id.rgType);
        rgWeapon = findViewById(R.id.rgWeapon);
        rgSheld = findViewById(R.id.rgSheld);

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
        for (int i = 0; i < rdoSheld.length; i++) {
            resource = getResources().getIdentifier("rdoSheld"+(i+1), "id", getPackageName());
            rdoSheld[i] = findViewById(resource);
        }

        libraryItems = new ArrayList<LibraryItem>();
        talentItems = new ArrayList<String>();

        arLibraryDBAdapter.open();
        rfLibraryDBAdapter.open();
        brLibraryDBAdapter.open();
        srLibraryDBAdapter.open();
        mmrLibraryDBAdapter.open();
        sgLibraryDBAdapter.open();
        ptLibraryDBAdapter.open();
        cursor = arLibraryDBAdapter.fetchTypeData("무기");
        LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
        libraryItems.add(item);
        cursor = rfLibraryDBAdapter.fetchTypeData("무기");
        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
        libraryItems.add(item);
        cursor = brLibraryDBAdapter.fetchTypeData("무기");
        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
        libraryItems.add(item);
        cursor = srLibraryDBAdapter.fetchTypeData("무기");
        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
        libraryItems.add(item);
        cursor = mmrLibraryDBAdapter.fetchTypeData("무기");
        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
        libraryItems.add(item);
        cursor = sgLibraryDBAdapter.fetchTypeData("무기");
        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
        libraryItems.add(item);
        cursor = ptLibraryDBAdapter.fetchTypeData("무기");
        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
        libraryItems.add(item);
        arLibraryDBAdapter.close();
        rfLibraryDBAdapter.close();
        brLibraryDBAdapter.close();
        srLibraryDBAdapter.close();
        mmrLibraryDBAdapter.close();
        sgLibraryDBAdapter.close();
        ptLibraryDBAdapter.close();

        libraryAdapter = new LibraryAdapter(this, libraryItems, null, false, "weapon_core1", true);
        listView.setAdapter(libraryAdapter);

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LibraryItem item = null;
                libraryItems.clear();
                talentItems.clear();
                talentLibraryDBAdapter.open();
                rgWeapon.setVisibility(View.GONE);
                rgSheld.setVisibility(View.GONE);
                layoutCount.setVisibility(View.GONE);
                switch (checkedId) {
                    case R.id.rdoType1:
                        arLibraryDBAdapter.open();
                        rfLibraryDBAdapter.open();
                        brLibraryDBAdapter.open();
                        srLibraryDBAdapter.open();
                        mmrLibraryDBAdapter.open();
                        sgLibraryDBAdapter.open();
                        ptLibraryDBAdapter.open();
                        cursor = arLibraryDBAdapter.fetchTypeData("무기");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        cursor = rfLibraryDBAdapter.fetchTypeData("무기");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        cursor = brLibraryDBAdapter.fetchTypeData("무기");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        cursor = srLibraryDBAdapter.fetchTypeData("무기");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        cursor = mmrLibraryDBAdapter.fetchTypeData("무기");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        cursor = sgLibraryDBAdapter.fetchTypeData("무기");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        cursor = ptLibraryDBAdapter.fetchTypeData("무기");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "weapon_core1", true);
                        for (int i = 0; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) rdoType[i].setTextColor(Color.parseColor("#FF6337"));
                            else rdoType[i].setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        arLibraryDBAdapter.close();
                        rfLibraryDBAdapter.close();
                        brLibraryDBAdapter.close();
                        srLibraryDBAdapter.close();
                        mmrLibraryDBAdapter.close();
                        sgLibraryDBAdapter.close();
                        ptLibraryDBAdapter.close();
                        break;
                    case R.id.rdoType2:
                        arLibraryDBAdapter.open();
                        rfLibraryDBAdapter.open();
                        brLibraryDBAdapter.open();
                        srLibraryDBAdapter.open();
                        mmrLibraryDBAdapter.open();
                        sgLibraryDBAdapter.open();
                        ptLibraryDBAdapter.open();
                        cursor = arLibraryDBAdapter.fetchTypeData("돌격소총");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        item.setWeaponType("돌격소총");
                        libraryItems.add(item);
                        cursor = rfLibraryDBAdapter.fetchTypeData("소총");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        item.setWeaponType("소총");
                        libraryItems.add(item);
                        cursor = brLibraryDBAdapter.fetchTypeData("경기관총");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        item.setWeaponType("경기관총");
                        libraryItems.add(item);
                        cursor = srLibraryDBAdapter.fetchTypeData("기관단총");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        item.setWeaponType("기관단총");
                        libraryItems.add(item);
                        cursor = mmrLibraryDBAdapter.fetchTypeData("지정사수소총");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        item.setWeaponType("지정사수소총");
                        libraryItems.add(item);
                        cursor = sgLibraryDBAdapter.fetchTypeData("산탄총");
                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        item.setWeaponType("산탄총");
                        libraryItems.add(item);
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "weapon_core2", true);
                        for (int i = 0; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) rdoType[i].setTextColor(Color.parseColor("#FF6337"));
                            else rdoType[i].setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        arLibraryDBAdapter.close();
                        rfLibraryDBAdapter.close();
                        brLibraryDBAdapter.close();
                        srLibraryDBAdapter.close();
                        mmrLibraryDBAdapter.close();
                        sgLibraryDBAdapter.close();
                        ptLibraryDBAdapter.close();
                        break;
                    case R.id.rdoType3:
                        rgWeapon.setVisibility(View.VISIBLE);
                        cursor = null;
                        for (int i = 0; i < rdoWeapon.length; i++) {
                            if (rdoWeapon[i].isChecked()) {
                                switch (weapon_types[i]) {
                                    case "돌격소총":
                                        arLibraryDBAdapter.open();
                                        cursor = arLibraryDBAdapter.fetchSubAllData();
                                        arLibraryDBAdapter.close();
                                        break;
                                    case "소총":
                                        rfLibraryDBAdapter.open();
                                        cursor = rfLibraryDBAdapter.fetchSubAllData();
                                        rfLibraryDBAdapter.close();
                                        break;
                                    case "지정사수소총":
                                        mmrLibraryDBAdapter.open();
                                        cursor = mmrLibraryDBAdapter.fetchSubAllData();
                                        mmrLibraryDBAdapter.close();
                                        break;
                                    case "산탄총":
                                        sgLibraryDBAdapter.open();
                                        cursor = sgLibraryDBAdapter.fetchSubAllData();
                                        sgLibraryDBAdapter.close();
                                        break;
                                    case "기관단총":
                                        srLibraryDBAdapter.open();
                                        cursor = srLibraryDBAdapter.fetchSubAllData();
                                        srLibraryDBAdapter.close();
                                        break;
                                    case "경기관총":
                                        brLibraryDBAdapter.open();
                                        cursor = brLibraryDBAdapter.fetchSubAllData();
                                        brLibraryDBAdapter.close();
                                        break;
                                    case "권총":
                                        ptLibraryDBAdapter.open();
                                        cursor = ptLibraryDBAdapter.fetchSubAllData();
                                        ptLibraryDBAdapter.close();
                                        break;
                                }
                                if (cursor != null) {
                                    while (!cursor.isAfterLast()) {
                                        item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                                        libraryItems.add(item);
                                        cursor.moveToNext();
                                    }
                                }
                            }
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "weapon_sub", false);
                        for (int i = 0; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) rdoType[i].setTextColor(Color.parseColor("#FF6337"));
                            else rdoType[i].setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        break;
                    case R.id.rdoType4:
                        rgSheld.setVisibility(View.VISIBLE);
                        rdoSheld[0].setChecked(true);
                        for (int i = 0; i < sheld_types.length; i++) {
                            if (rdoSheld[i].isChecked()) {
                                switch (sheld_types[i]) {
                                    case "마스크":
                                        maskLibraryDBAdapter.open();
                                        cursor = maskLibraryDBAdapter.fetchSheldCoreAllData();
                                        maskLibraryDBAdapter.close();
                                        break;
                                    case "조끼":
                                        vestLibraryDBAdapter.open();
                                        cursor = vestLibraryDBAdapter.fetchSheldCoreAllData();
                                        vestLibraryDBAdapter.close();
                                        break;
                                    case "권총집":
                                        holsterLibraryDBAdapter.open();
                                        cursor = holsterLibraryDBAdapter.fetchSheldCoreAllData();
                                        holsterLibraryDBAdapter.close();
                                        break;
                                    case "백팩":
                                        backpackLibraryDBAdapter.open();
                                        cursor = backpackLibraryDBAdapter.fetchSheldCoreAllData();
                                        backpackLibraryDBAdapter.close();
                                        break;
                                    case "장갑":
                                        gloveLibraryDBAdapter.open();
                                        cursor = gloveLibraryDBAdapter.fetchSheldCoreAllData();
                                        gloveLibraryDBAdapter.close();
                                        break;
                                    case "무릎보호대":
                                        kneepedLibraryDBAdapter.open();
                                        cursor = kneepedLibraryDBAdapter.fetchSheldCoreAllData();
                                        kneepedLibraryDBAdapter.close();
                                        break;
                                }
                            }
                        }
                        while (!cursor.isAfterLast()) {
                            item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                            libraryItems.add(item);
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "sheld_core", true);
                        for (int i = 0; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) rdoType[i].setTextColor(Color.parseColor("#FF6337"));
                            else rdoType[i].setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        break;
                    case R.id.rdoType5:
                        rgSheld.setVisibility(View.VISIBLE);
                        rdoSheld[0].setChecked(true);
                        for (int i = 0; i < sheld_types.length; i++) {
                            if (rdoSheld[i].isChecked()) {
                                switch (sheld_types[i]) {
                                    case "마스크":
                                        maskLibraryDBAdapter.open();
                                        cursor = maskLibraryDBAdapter.fetchSheldSubAllData();
                                        maskLibraryDBAdapter.close();
                                        break;
                                    case "조끼":
                                        vestLibraryDBAdapter.open();
                                        cursor = vestLibraryDBAdapter.fetchSheldSubAllData();
                                        vestLibraryDBAdapter.close();
                                        break;
                                    case "권총집":
                                        holsterLibraryDBAdapter.open();
                                        cursor = holsterLibraryDBAdapter.fetchSheldSubAllData();
                                        holsterLibraryDBAdapter.close();
                                        break;
                                    case "백팩":
                                        backpackLibraryDBAdapter.open();
                                        cursor = backpackLibraryDBAdapter.fetchSheldSubAllData();
                                        backpackLibraryDBAdapter.close();
                                        break;
                                    case "장갑":
                                        gloveLibraryDBAdapter.open();
                                        cursor = gloveLibraryDBAdapter.fetchSheldSubAllData();
                                        gloveLibraryDBAdapter.close();
                                        break;
                                    case "무릎보호대":
                                        kneepedLibraryDBAdapter.open();
                                        cursor = kneepedLibraryDBAdapter.fetchSheldSubAllData();
                                        kneepedLibraryDBAdapter.close();
                                        break;
                                }
                            }
                        }
                        while (!cursor.isAfterLast()) {
                            item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                            libraryItems.add(item);
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "sheld_sub", false);
                        for (int i = 0; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) rdoType[i].setTextColor(Color.parseColor("#FF6337"));
                            else rdoType[i].setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        break;
                    case R.id.rdoType6:
                        rgWeapon.setVisibility(View.VISIBLE);
                        layoutCount.setVisibility(View.VISIBLE);
                        talentLibraryDBAdapter.close();
                        rdoWeapon[0].setChecked(true);
                        talentItems.clear();
                        arTalentDBAdapter.open();
                        cursor = arTalentDBAdapter.fetchHaveData();
                        while (!cursor.isAfterLast()) {
                            talentItems.add(cursor.getString(1));
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, null, talentItems, true, "");
                        txtCount.setText(Integer.toString(arTalentDBAdapter.getHaveCount()));
                        arTalentDBAdapter.close();
                        talentDBAdapter.open();
                        txtMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount(weapon_types[0])));
                        talentDBAdapter.close();
                        for (int i = 0; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) rdoType[i].setTextColor(Color.parseColor("#FF6337"));
                            else rdoType[i].setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        break;
                    case R.id.rdoType7:
                        layoutCount.setVisibility(View.VISIBLE);
                        vestTalentDBAdapter.open();
                        cursor = vestTalentDBAdapter.fetchHaveData();
                        while (!cursor.isAfterLast()) {
                            talentItems.add(cursor.getString(1));
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, null, talentItems, true, "");
                        txtCount.setText(Integer.toString(vestTalentDBAdapter.getHaveCount()));
                        vestTalentDBAdapter.close();
                        talentDBAdapter.open();
                        txtMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount("조끼")));
                        talentDBAdapter.close();
                        for (int i = 0; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) rdoType[i].setTextColor(Color.parseColor("#FF6337"));
                            else rdoType[i].setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        break;
                    case R.id.rdoType8:
                        layoutCount.setVisibility(View.VISIBLE);
                        backpackTalentDBAdapter.open();
                        cursor = backpackTalentDBAdapter.fetchHaveData();
                        while (!cursor.isAfterLast()) {
                            talentItems.add(cursor.getString(1));
                            cursor.moveToNext();
                        }
                        libraryAdapter = new LibraryAdapter(LibraryActivity.this, null, talentItems, true, "");
                        txtCount.setText(Integer.toString(backpackTalentDBAdapter.getHaveCount()));
                        backpackTalentDBAdapter.close();
                        talentDBAdapter.open();
                        txtMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount("백팩")));
                        talentDBAdapter.close();
                        for (int i = 0; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) rdoType[i].setTextColor(Color.parseColor("#FF6337"));
                            else rdoType[i].setTextColor(Color.parseColor("#F0F0F0"));
                        }
                        break;
                }
                listView.setAdapter(libraryAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 5; i < rdoType.length; i++) {
                            if (rdoType[i].isChecked()) {
                                View talentview = getLayoutInflater().inflate(R.layout.talentdialog, null);

                                TextView txtName = talentview.findViewById(R.id.txtName);
                                TextView txtContent = talentview.findViewById(R.id.txtContent);
                                Button btnOK = talentview.findViewById(R.id.btnOK);

                                talentDBAdapter.open();
                                String content = talentDBAdapter.findContent(talentItems.get(position));
                                talentDBAdapter.close();

                                txtName.setText(talentItems.get(position));
                                txtContent.setText(transformString(content));

                                btnOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                AlertDialog.Builder dialog_builder = new AlertDialog.Builder(LibraryActivity.this);
                                dialog_builder.setView(talentview);

                                alertDialog = dialog_builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertDialog.show();
                            }
                        }
                    }
                });
                libraryAdapter.notifyDataSetChanged();
            }
        });

        rgSheld.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                libraryItems.clear();
                for (int i = 0; i < rdoSheld.length; i++) {
                    if (rdoSheld[i].isChecked()) rdoSheld[i].setTextColor(Color.parseColor("#FF6337"));
                    else rdoSheld[i].setTextColor(Color.parseColor("#F0F0F0"));
                }
                if (rdoType[3].isChecked()) {
                    for (int i = 0; i < sheld_types.length; i++) {
                        if (rdoSheld[i].isChecked()) {
                            switch (sheld_types[i]) {
                                case "마스크":
                                    maskLibraryDBAdapter.open();
                                    cursor = maskLibraryDBAdapter.fetchSheldCoreAllData();
                                    maskLibraryDBAdapter.close();
                                    break;
                                case "조끼":
                                    vestLibraryDBAdapter.open();
                                    cursor = vestLibraryDBAdapter.fetchSheldCoreAllData();
                                    vestLibraryDBAdapter.close();
                                    break;
                                case "권총집":
                                    holsterLibraryDBAdapter.open();
                                    cursor = holsterLibraryDBAdapter.fetchSheldCoreAllData();
                                    holsterLibraryDBAdapter.close();
                                    break;
                                case "백팩":
                                    backpackLibraryDBAdapter.open();
                                    cursor = backpackLibraryDBAdapter.fetchSheldCoreAllData();
                                    backpackLibraryDBAdapter.close();
                                    break;
                                case "장갑":
                                    gloveLibraryDBAdapter.open();
                                    cursor = gloveLibraryDBAdapter.fetchSheldCoreAllData();
                                    gloveLibraryDBAdapter.close();
                                    break;
                                case "무릎보호대":
                                    kneepedLibraryDBAdapter.open();
                                    cursor = kneepedLibraryDBAdapter.fetchSheldCoreAllData();
                                    kneepedLibraryDBAdapter.close();
                                    break;
                            }
                        }
                    }
                    while (!cursor.isAfterLast()) {
                        LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        cursor.moveToNext();
                    }
                    libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "sheld_core", true);
                    listView.setAdapter(libraryAdapter);
                    listView.setOnItemClickListener(null);
                    libraryAdapter.notifyDataSetChanged();
                } else if (rdoType[4].isChecked()) {
                    for (int i = 0; i < sheld_types.length; i++) {
                        if (rdoSheld[i].isChecked()) {
                            switch (sheld_types[i]) {
                                case "마스크":
                                    maskLibraryDBAdapter.open();
                                    cursor = maskLibraryDBAdapter.fetchSheldSubAllData();
                                    maskLibraryDBAdapter.close();
                                    break;
                                case "조끼":
                                    vestLibraryDBAdapter.open();
                                    cursor = vestLibraryDBAdapter.fetchSheldSubAllData();
                                    vestLibraryDBAdapter.close();
                                    break;
                                case "권총집":
                                    holsterLibraryDBAdapter.open();
                                    cursor = holsterLibraryDBAdapter.fetchSheldSubAllData();
                                    holsterLibraryDBAdapter.close();
                                    break;
                                case "백팩":
                                    backpackLibraryDBAdapter.open();
                                    cursor = backpackLibraryDBAdapter.fetchSheldSubAllData();
                                    backpackLibraryDBAdapter.close();
                                    break;
                                case "장갑":
                                    gloveLibraryDBAdapter.open();
                                    cursor = gloveLibraryDBAdapter.fetchSheldSubAllData();
                                    gloveLibraryDBAdapter.close();
                                    break;
                                case "무릎보호대":
                                    kneepedLibraryDBAdapter.open();
                                    cursor = kneepedLibraryDBAdapter.fetchSheldSubAllData();
                                    kneepedLibraryDBAdapter.close();
                                    break;
                            }
                        }
                    }
                    while (!cursor.isAfterLast()) {
                        LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                        libraryItems.add(item);
                        cursor.moveToNext();
                    }
                    libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "sheld_sub", false);
                    listView.setAdapter(libraryAdapter);
                    listView.setOnItemClickListener(null);
                    libraryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rgWeapon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < rdoWeapon.length; i++) {
                    if (rdoWeapon[i].isChecked()) rdoWeapon[i].setTextColor(Color.parseColor("#FF6337"));
                    else rdoWeapon[i].setTextColor(Color.parseColor("#F0F0F0"));
                }
                if (rdoType[2].isChecked()) {
                    libraryItems.clear();
                    for (int i = 0; i < rdoWeapon.length; i++) {
                        if (rdoWeapon[i].isChecked()) {
                            switch (weapon_types[i]) {
                                case "돌격소총":
                                    arLibraryDBAdapter.open();
                                    cursor = arLibraryDBAdapter.fetchSubAllData();
                                    arLibraryDBAdapter.close();
                                    break;
                                case "소총":
                                    rfLibraryDBAdapter.open();
                                    cursor = rfLibraryDBAdapter.fetchSubAllData();
                                    rfLibraryDBAdapter.close();
                                    break;
                                case "지정사수소총":
                                    mmrLibraryDBAdapter.open();
                                    cursor = mmrLibraryDBAdapter.fetchSubAllData();
                                    mmrLibraryDBAdapter.close();
                                    break;
                                case "산탄총":
                                    sgLibraryDBAdapter.open();
                                    cursor = sgLibraryDBAdapter.fetchSubAllData();
                                    sgLibraryDBAdapter.close();
                                    break;
                                case "기관단총":
                                    srLibraryDBAdapter.open();
                                    cursor = srLibraryDBAdapter.fetchSubAllData();
                                    srLibraryDBAdapter.close();
                                    break;
                                case "경기관총":
                                    brLibraryDBAdapter.open();
                                    cursor = brLibraryDBAdapter.fetchSubAllData();
                                    brLibraryDBAdapter.close();
                                    break;
                                case "권총":
                                    ptLibraryDBAdapter.open();
                                    cursor = ptLibraryDBAdapter.fetchSubAllData();
                                    ptLibraryDBAdapter.close();
                                    break;
                            }
                            if (cursor != null) {
                                while (!cursor.isAfterLast()) {
                                    LibraryItem item = new LibraryItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                                    libraryItems.add(item);
                                    cursor.moveToNext();
                                }
                            }
                        }
                    }
                    libraryAdapter = new LibraryAdapter(LibraryActivity.this, libraryItems, null, false, "weapon_sub", false);
                    listView.setAdapter(libraryAdapter);
                    listView.setOnItemClickListener(null);
                    libraryAdapter.notifyDataSetChanged();
                } else if (rdoType[5].isChecked()) {
                    talentItems.clear();
                    for (int i = 0; i < rdoWeapon.length; i++) {
                        if (rdoWeapon[i].isChecked()) {
                            switch (weapon_types[i]) {
                                case "돌격소총":
                                    arTalentDBAdapter.open();
                                    cursor = arTalentDBAdapter.fetchHaveData();
                                    txtCount.setText(Integer.toString(arTalentDBAdapter.getHaveCount()));
                                    arTalentDBAdapter.close();
                                    break;
                                case "소총":
                                    rfTalentDBAdapter.open();
                                    cursor = rfTalentDBAdapter.fetchHaveData();
                                    txtCount.setText(Integer.toString(rfTalentDBAdapter.getHaveCount()));
                                    rfTalentDBAdapter.close();
                                    break;
                                case "지정사수소총":
                                    mmrTalentDBAdapter.open();
                                    cursor = mmrTalentDBAdapter.fetchHaveData();
                                    txtCount.setText(Integer.toString(mmrTalentDBAdapter.getHaveCount()));
                                    mmrTalentDBAdapter.close();
                                    break;
                                case "산탄총":
                                    sgTalentDBAdapter.open();
                                    cursor = sgTalentDBAdapter.fetchHaveData();
                                    txtCount.setText(Integer.toString(sgTalentDBAdapter.getHaveCount()));
                                    sgTalentDBAdapter.close();
                                    break;
                                case "기관단총":
                                    srTalentDBAdapter.open();
                                    cursor = srTalentDBAdapter.fetchHaveData();
                                    txtCount.setText(Integer.toString(srTalentDBAdapter.getHaveCount()));
                                    srTalentDBAdapter.close();
                                    break;
                                case "경기관총":
                                    brTalentDBAdapter.open();
                                    cursor = brTalentDBAdapter.fetchHaveData();
                                    txtCount.setText(Integer.toString(brTalentDBAdapter.getHaveCount()));
                                    brTalentDBAdapter.close();
                                    break;
                                case "권총":
                                    ptTalentDBAdapter.open();
                                    cursor = ptTalentDBAdapter.fetchHaveData();
                                    txtCount.setText(Integer.toString(ptTalentDBAdapter.getHaveCount()));
                                    ptTalentDBAdapter.close();
                                    break;
                            }
                            talentDBAdapter.open();
                            txtMaxCount.setText(Integer.toString(talentDBAdapter.getTypeCount(weapon_types[i])));
                            talentDBAdapter.close();
                        }
                        if (rdoWeapon[i].isChecked()) rdoWeapon[i].setTextColor(Color.parseColor("#FF6337"));
                        else rdoWeapon[i].setTextColor(Color.parseColor("#F0F0F0"));
                    }
                    while (!cursor.isAfterLast()) {
                        talentItems.add(cursor.getString(1));
                        cursor.moveToNext();
                    }
                    libraryAdapter = new LibraryAdapter(LibraryActivity.this, null, talentItems, true, "");
                    listView.setAdapter(libraryAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            View talentview = getLayoutInflater().inflate(R.layout.talentdialog, null);

                            TextView txtName = talentview.findViewById(R.id.txtName);
                            TextView txtContent = talentview.findViewById(R.id.txtContent);
                            Button btnOK = talentview.findViewById(R.id.btnOK);

                            talentDBAdapter.open();
                            String content = talentDBAdapter.findContent(talentItems.get(position));
                            talentDBAdapter.close();

                            txtName.setText(talentItems.get(position));
                            txtContent.setText(transformString(content));

                            btnOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });

                            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(LibraryActivity.this);
                            dialog_builder.setView(talentview);

                            alertDialog = dialog_builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertDialog.show();
                        }
                    });
                    libraryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
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
