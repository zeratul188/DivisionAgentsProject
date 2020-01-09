package com.example.divisionsimulation.ui.home;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class SimulActivity extends AppCompatActivity {

    public static TextView txtSheld, txtHealth, txtNowDemage, txtStatue, txtAmmo, txtAllAmmo, txtTime;

    public static ProgressBar progressSheld, progressHealth;

    private DemageSimulThread dst = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simullayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("디비전2 시뮬레이션");

        txtSheld = findViewById(R.id.txtSheld);
        txtHealth = findViewById(R.id.txtHealth);
        txtNowDemage = findViewById(R.id.txtNowDemage);
        txtStatue = findViewById(R.id.txtStatue);
        txtAmmo = findViewById(R.id.txtAmmo);
        txtAllAmmo = findViewById(R.id.txtAllAmmo);
        txtTime = findViewById(R.id.txtTime);

        progressSheld = findViewById(R.id.progressSheld);
        progressHealth = findViewById(R.id.progressHealth);

        progressSheld.setMax(100);
        progressHealth.setMax(100);

        progressSheld.setProgress(100);
        progressHealth.setProgress(100);

        TimeThread tt = new TimeThread();
        tt.start();

        dst = (DemageSimulThread) getIntent().getSerializableExtra("thread");
        dst.setTimeThread(tt);
        dst.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                dst.interrupt();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
