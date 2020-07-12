package com.example.divisionsimulation;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SHDActivity extends AppCompatActivity {
    private SHDDBAdapter shdAdapter;

    private static final int ARRAY_LENGTH = 4;

    private TextView txtSHDLevel, txtEXP, txtNextAttribute;
    private ProgressBar progressEXP;

    private LinearLayout[] layoutAttack = new LinearLayout[4];
    private LinearLayout[] layoutSheld = new LinearLayout[4];
    private LinearLayout[] layoutPower = new LinearLayout[4];
    private LinearLayout[] layoutAnother = new LinearLayout[4];
    private TextView[] txtAttack = new TextView[4];
    private TextView[] txtSheld = new TextView[4];
    private TextView[] txtPower = new TextView[4];
    private TextView[] txtAnother = new TextView[4];
    private TextView[] txtAttackTitle = new TextView[4];
    private TextView[] txtSheldTitle = new TextView[4];
    private TextView[] txtPowerTitle = new TextView[4];
    private TextView[] txtAnotherTitle = new TextView[4];
    private ProgressBar[] progressAttack = new ProgressBar[4];
    private ProgressBar[] progressSheld = new ProgressBar[4];
    private ProgressBar[] progressPower = new ProgressBar[4];
    private ProgressBar[] progressAnother = new ProgressBar[4];
    private TextView[] txtPoint = new TextView[4];

    private Button btnLevelUp, btnReset;

    private int[] attack = new int[4];
    private int[] sheld = new int[4];
    private int[] power = new int[4];
    private int[] another = new int[4];
    private int level;
    private int exp;
    private int[] point = new int[4];
    private String nextOption = "null";

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shdlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("SHD 레벨");

        shdAdapter = new SHDDBAdapter(this);

        txtSHDLevel = findViewById(R.id.txtSHDLevel);
        txtEXP = findViewById(R.id.txtEXP);
        txtNextAttribute = findViewById(R.id.txtNextAttribute);
        progressEXP = findViewById(R.id.progressEXP);
        progressEXP.setMax(700000);

        btnLevelUp = findViewById(R.id.btnLevelUP);
        btnLevelUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(SHDActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("SHD 레벨 업");
                builder.setMessage("레벨 1 상승합니다.");
                builder.setPositiveButton("레벨 업", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shdAdapter.open();
                        shdAdapter.addEXP(700000);
                        shdAdapter.levelUp();
                        shdAdapter.close();
                        toast("레벨업하였습니다.", false);
                        refreshData();
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(SHDActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("SHD 초기화");
                builder.setMessage("SHD 레벨과 포인트 모두 초기화됩니다.");
                builder.setPositiveButton("레벨 업", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shdAdapter.open();
                        shdAdapter.resetSHD();
                        shdAdapter.close();
                        toast("SHD 레벨이 초기화되었습니다.", false);
                        refreshData();
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        int resource;
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            resource = getResources().getIdentifier("layoutAttack"+(i+1), "id", getPackageName());
            layoutAttack[i] = findViewById(resource);
            resource = getResources().getIdentifier("layoutSheld"+(i+1), "id", getPackageName());
            layoutSheld[i] = findViewById(resource);
            resource = getResources().getIdentifier("layoutPower"+(i+1), "id", getPackageName());
            layoutPower[i] = findViewById(resource);
            resource = getResources().getIdentifier("layoutAnother"+(i+1), "id", getPackageName());
            layoutAnother[i] = findViewById(resource);
            resource = getResources().getIdentifier("txtAttack"+(i+1), "id", getPackageName());
            txtAttack[i] = findViewById(resource);
            resource = getResources().getIdentifier("txtSheld"+(i+1), "id", getPackageName());
            txtSheld[i] = findViewById(resource);
            resource = getResources().getIdentifier("txtPower"+(i+1), "id", getPackageName());
            txtPower[i] = findViewById(resource);
            resource = getResources().getIdentifier("txtAnother"+(i+1), "id", getPackageName());
            txtAnother[i] = findViewById(resource);
            resource = getResources().getIdentifier("progressAttack"+(i+1), "id", getPackageName());
            progressAttack[i] = findViewById(resource);
            progressAttack[i].setMax(50);
            resource = getResources().getIdentifier("progressSheld"+(i+1), "id", getPackageName());
            progressSheld[i] = findViewById(resource);
            progressSheld[i].setMax(50);
            resource = getResources().getIdentifier("progressPower"+(i+1), "id", getPackageName());
            progressPower[i] = findViewById(resource);
            progressPower[i].setMax(50);
            resource = getResources().getIdentifier("progressAnother"+(i+1), "id", getPackageName());
            progressAnother[i] = findViewById(resource);
            progressAnother[i].setMax(50);
            resource = getResources().getIdentifier("txtPoint"+(i+1), "id", getPackageName());
            txtPoint[i] = findViewById(resource);
            resource = getResources().getIdentifier("txtAttackTitle"+(i+1), "id", getPackageName());
            txtAttackTitle[i] = findViewById(resource);
            resource = getResources().getIdentifier("txtSheldTitle"+(i+1), "id", getPackageName());
            txtSheldTitle[i] = findViewById(resource);
            resource = getResources().getIdentifier("txtPowerTitle"+(i+1), "id", getPackageName());
            txtPowerTitle[i] = findViewById(resource);
            resource = getResources().getIdentifier("txtAnotherTitle"+(i+1), "id", getPackageName());
            txtAnotherTitle[i] = findViewById(resource);

            final int index = i;

            layoutAttack[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(SHDActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(String.valueOf(txtAttackTitle[index].getText()));
                    builder.setMessage("스킬 포인트를 사용하시겠습니까? ("+attack[index]+"/50)");
                    builder.setPositiveButton("사용", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shdAdapter.open();
                            Cursor cursor = shdAdapter.fetchSHD("공격");
                            int count = cursor.getInt(2);
                            if (count > 0) {
                                if (shdAdapter.increaseSHD(String.valueOf(txtAttackTitle[index].getText()))) shdAdapter.usePoint("공격");
                                else toast("최대치까지 포인트를 사용하였습니다.", false);
                                refreshData();
                            } else toast("사용가능한 포인트가 없습니다.", false);
                            shdAdapter.close();
                        }
                    });
                    builder.setNegativeButton("취소", null);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });
            layoutSheld[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(SHDActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(String.valueOf(txtSheldTitle[index].getText()));
                    builder.setMessage("스킬 포인트를 사용하시겠습니까? ("+sheld[index]+"/50)");
                    builder.setPositiveButton("사용", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shdAdapter.open();
                            Cursor cursor = shdAdapter.fetchSHD("방어");
                            int count = cursor.getInt(2);
                            if (count > 0) {
                                if (shdAdapter.increaseSHD(String.valueOf(txtSheldTitle[index].getText()))) shdAdapter.usePoint("방어");
                                else toast("최대치까지 포인트를 사용하였습니다.", false);
                                refreshData();
                            } else toast("사용가능한 포인트가 없습니다.", false);
                            shdAdapter.close();
                        }
                    });
                    builder.setNegativeButton("취소", null);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });
            layoutPower[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(SHDActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(String.valueOf(txtPowerTitle[index].getText()));
                    builder.setMessage("스킬 포인트를 사용하시겠습니까? ("+power[index]+"/50)");
                    builder.setPositiveButton("사용", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shdAdapter.open();
                            Cursor cursor = shdAdapter.fetchSHD("다용도");
                            int count = cursor.getInt(2);
                            if (count > 0) {
                                if (shdAdapter.increaseSHD(String.valueOf(txtPowerTitle[index].getText()))) shdAdapter.usePoint("다용도");
                                else toast("최대치까지 포인트를 사용하였습니다.", false);
                                refreshData();
                            } else toast("사용가능한 포인트가 없습니다.", false);
                            shdAdapter.close();
                        }
                    });
                    builder.setNegativeButton("취소", null);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });
            layoutAnother[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(SHDActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(String.valueOf(txtAnotherTitle[index].getText()));
                    builder.setMessage("스킬 포인트를 사용하시겠습니까? ("+another[index]+"/50)");
                    builder.setPositiveButton("사용", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shdAdapter.open();
                            Cursor cursor = shdAdapter.fetchSHD("기타");
                            int count = cursor.getInt(2);
                            if (count > 0) {
                                if (shdAdapter.increaseSHD(String.valueOf(txtAnotherTitle[index].getText()))) shdAdapter.usePoint("기타");
                                else toast("최대치까지 포인트를 사용하였습니다.", false);
                                refreshData();
                            } else toast("사용가능한 포인트가 없습니다.", false);
                            shdAdapter.close();
                        }
                    });
                    builder.setNegativeButton("취소", null);

                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });
        }

        refreshData();
    }

    private void toast(String message, boolean longer) {
        int length;
        if (longer) length = Toast.LENGTH_LONG;
        else length = Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    private void refreshData() {
        shdAdapter.open();
        Cursor cursor = shdAdapter.fetchAllSHD();
        cursor.moveToFirst();
        int cnt = 1;
        while (!cursor.isAfterLast()) {
            if (cnt == 1) level = cursor.getInt(2);
            else if (cnt == 2) exp = cursor.getInt(2);
            else if (cnt >= 3 && cnt < 7) attack[cnt-3] = cursor.getInt(2);
            else if (cnt >= 7 && cnt < 11) sheld[cnt-7] = cursor.getInt(2);
            else if (cnt >= 11 && cnt < 15) power[cnt-11] = cursor.getInt(2);
            else if (cnt >= 15 && cnt < 19) another[cnt-15] = cursor.getInt(2);
            else if (cnt >= 19 && cnt < 23) point[cnt-19] = cursor.getInt(2);
            else if (cnt == 23) {
                if (cursor.getInt(2) == 0) nextOption = "공격";
                else if (cursor.getInt(2) == 1) nextOption = "방어";
                else if (cursor.getInt(2) == 2) nextOption = "다용도";
                else nextOption = "기타";
            }
            cnt++;
            cursor.moveToNext();
        }
        txtSHDLevel.setText(Integer.toString(level));
        txtEXP.setText(exp+"/700000");
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            txtAttack[i].setText(attack[i]+"/50");
            progressAttack[i].setProgress(attack[i]);
            txtSheld[i].setText(sheld[i]+"/50");
            progressSheld[i].setProgress(sheld[i]);
            txtPower[i].setText(power[i]+"/50");
            progressPower[i].setProgress(power[i]);
            txtAnother[i].setText(another[i]+"/50");
            progressAnother[i].setProgress(another[i]);
            txtPoint[i].setText(Integer.toString(point[i]));
        }
        txtNextAttribute.setText(nextOption);
        progressEXP.setProgress(exp);
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
