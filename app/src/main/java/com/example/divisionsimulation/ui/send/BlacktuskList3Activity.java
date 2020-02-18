package com.example.divisionsimulation.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class BlacktuskList3Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blacktusklayout3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("블랙터스크 조종사");

        Intent intent = getIntent();
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
