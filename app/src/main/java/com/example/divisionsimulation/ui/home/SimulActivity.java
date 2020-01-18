package com.example.divisionsimulation.ui.home;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

import java.io.Serializable;

public class SimulActivity extends AppCompatActivity implements Serializable {

    public static TextView txtSheld, txtHealth, txtNowDemage, txtAmmo, txtAllAmmo, txtTime, txtAdddemage, txtStatue;

    public static ProgressBar progressSheld, progressHealth, progressAmmo;

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
        txtAdddemage = findViewById(R.id.txtAdddemage);

        progressSheld = findViewById(R.id.progressSheld);
        progressHealth = findViewById(R.id.progressHealth);
        progressAmmo = findViewById(R.id.progressAmmo);

        //progressSheld.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        //progressHealth.getProgressDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);

        progressSheld.setMax(10000);
        progressHealth.setMax(10000);
        progressAmmo.setMax(10000);

        progressSheld.setProgress(10000);
        progressHealth.setProgress(10000);
        progressAmmo.setProgress(10000);

        TimeThread tt = new TimeThread();
        tt.start();

        dst = (DemageSimulThread) getIntent().getSerializableExtra("thread");
        //dst = (DemageSimulThread) getIntent().getParcelableExtra("thread") ;
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
