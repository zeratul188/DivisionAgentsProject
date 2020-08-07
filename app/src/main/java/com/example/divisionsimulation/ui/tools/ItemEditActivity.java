package com.example.divisionsimulation.ui.tools;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;
import com.example.divisionsimulation.dbdatas.ExoticFMDBAdapter;
import com.example.divisionsimulation.dbdatas.InventoryDBAdapter;
import com.example.divisionsimulation.dbdatas.MaxOptionsFMDBAdapter;
import com.example.divisionsimulation.dbdatas.TalentFMDBAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ItemEditActivity extends AppCompatActivity {

    private LinearLayout layoutWeapon, layoutSheld;
    private TextView txtWeaponOption, txtSheldOption, txtTalent;
    private ProgressBar progressWeaponOption, progressSheldOption;
    private ImageView imgSheldOption;
    private ListView listView;
    private Button btnRessetting, btnCancel;

    private EditAdapter editAdapter;
    private boolean exoticed = false, talented = false;
    private String name, type, option_type;
    private double value;
    private long rowID;
    private Cursor cursor;
    private ArrayList<EditItem> editItems;
    private ArrayList<String> talentItems;

    private MaxOptionsFMDBAdapter maxDBAdapter;
    private TalentFMDBAdapter talentDBAdapter;
    private InventoryDBAdapter inventoryDBAdapter;
    private ExoticFMDBAdapter exoticDBAdapter;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private String[] weaponList = {"돌격소총", "소총", "지정사수소총", "산탄총", "기관단총", "경기관총", "권총"};
    private boolean weaponed = false;

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
        listView = findViewById(R.id.listView);
        txtTalent = findViewById(R.id.txtTalent);

        exoticed = getIntent().getBooleanExtra("exoticed", false);
        talented = getIntent().getBooleanExtra("talented", false);
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        rowID = getIntent().getLongExtra("itemID", 9999);
        if (!talented) {
            value = getIntent().getDoubleExtra("value", 0);
            option_type = getIntent().getStringExtra("option_type");
        }

        editItems = new ArrayList<EditItem>();
        talentItems = new ArrayList<String>();
        maxDBAdapter = new MaxOptionsFMDBAdapter(this);
        talentDBAdapter = new TalentFMDBAdapter(this);
        inventoryDBAdapter = new InventoryDBAdapter(this);
        exoticDBAdapter = new ExoticFMDBAdapter(this);

        if (exoticed) {
            listView.setVisibility(View.INVISIBLE);
            btnRessetting.setVisibility(View.VISIBLE);
        } else if (talented) {
            txtTalent.setVisibility(View.VISIBLE);
            txtTalent.setText(name);
            listView.setVisibility(View.VISIBLE);
            talentDBAdapter.open();
            cursor = talentDBAdapter.fetchTypeData(type);
            talentDBAdapter.close();
            while (!cursor.isAfterLast()) {
                talentItems.add(cursor.getString(1));
                cursor.moveToNext();
            }
            editAdapter = new EditAdapter(this, null, talentItems, true);
            listView.setAdapter(editAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    builder = new AlertDialog.Builder(ItemEditActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(talentItems.get(position));
                    talentDBAdapter.open();
                    cursor = talentDBAdapter.fetchData(talentItems.get(position));
                    String content = cursor.getString(11);
                    talentDBAdapter.close();
                    builder.setMessage(content);
                    System.out.println(rowID);
                    final int index = position;
                    builder.setPositiveButton("보정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), name+"에서 "+talentItems.get(index)+"로 보정되었습니다.", Toast.LENGTH_SHORT).show();
                            inventoryDBAdapter.open();
                            inventoryDBAdapter.updateEditData(rowID, false, false, false, true);
                            inventoryDBAdapter.updateTalentData(rowID, talentItems.get(index));
                            inventoryDBAdapter.close();
                            alertDialog.dismiss();
                            finish();
                        }
                    });
                    builder.setNegativeButton("취소", null);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
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
            maxDBAdapter.open();
            switch (option_type) {
                case "weapon_core1":
                    cursor = maxDBAdapter.fetchTypeData("무기");
                    break;
                case "weapon_core2":
                    cursor = maxDBAdapter.fetchTypeData(type);
                    break;
                case "weapon_sub":
                    cursor = maxDBAdapter.fetchTypeData("무기 부속성");
                    break;
                case "sheld_core":
                    cursor = maxDBAdapter.fetchTypeData("보호장구 핵심속성");
                    break;
                case "sheld_sub1":
                case "sheld_sub2":
                    cursor = maxDBAdapter.fetchTypeData("보호장구 부속성");
                    break;
            }
            while (!cursor.isAfterLast()) {
                EditItem item = new EditItem(cursor.getString(1), cursor.getString(4), cursor.getDouble(2));
                editItems.add(item);
                cursor.moveToNext();
            }
            maxDBAdapter.close();
            editAdapter = new EditAdapter(this, editItems, null, false);
            listView.setAdapter(editAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View seek_view = getLayoutInflater().inflate(R.layout.editseeker, null);

                    TextView txtName = seek_view.findViewById(R.id.txtName);
                    final TextView txtValue = seek_view.findViewById(R.id.txtValue);
                    final SeekBar seekBar = seek_view.findViewById(R.id.seekBar);
                    TextView txtEnd = seek_view.findViewById(R.id.txtEnd);

                    maxDBAdapter.open();
                    cursor = maxDBAdapter.fetchData(editItems.get(position).getName());
                    String end = cursor.getString(5);
                    if (end.equals("-")) end = "";
                    maxDBAdapter.close();

                    txtName.setText(editItems.get(position).getName());
                    txtValue.setText(formatD(editItems.get(position).getMax()));
                    seekBar.setMax((int)(editItems.get(position).getMax()*10));
                    seekBar.setProgress((int)(editItems.get(position).getMax()*10));
                    txtEnd.setText(end);

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

                    builder = new AlertDialog.Builder(ItemEditActivity.this, R.style.MyAlertDialogStyle);
                    builder.setView(seek_view);
                    final int index = position;
                    builder.setPositiveButton("보정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                    builder.setNegativeButton("취소", null);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
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

        btnRessetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(ItemEditActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("특급 재조정");
                builder.setMessage("재조정 하시겠습니까?");
                builder.setPositiveButton("재조정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                            maxDBAdapter.open();
                            cursor = maxDBAdapter.fetchTypeData(weapon_type);
                            max = cursor.getDouble(2);
                            maxDBAdapter.close();
                            pick = percent(1, 100);
                            if (pick <= 30) temp_percent = 100;
                            else if (pick <= 60) temp_percent = percent(21, 10) + 70; //20% 확률로 좋은 옵션이 나온다. (보너스를 제외한 21~30%)
                            else temp_percent = percent(1, 20) + 50; //80%확률로 일반적인 옵션이 나온다. (보너스를 제외한 1~20%)
                            core2_value = Math.floor(((double)max*((double)temp_percent/100))*10.0)/10.0; //현재 옵션 수치를 설정
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
                            inventoryDBAdapter.updateCore2Data(rowID, core2, core2_value);
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
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
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

    private int percent(int min, int length) {
        return (int)(Math.random()*12345678)%length + min;
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
