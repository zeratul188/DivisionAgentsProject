package com.example.divisionsimulation.ui.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class SimulActivity extends AppCompatActivity {

    public static TextView txtSheld, txtHealth, txtNowDemage, txtStatue, txtAmmo;

    private DemageSimulThread dst = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simullayout);

        txtSheld = findViewById(R.id.txtSheld);
        txtHealth = findViewById(R.id.txtHealth);
        txtNowDemage = findViewById(R.id.txtNowDemage);
        txtStatue = findViewById(R.id.txtStatue);
        txtAmmo = findViewById(R.id.txtAmmo);

        dst = (DemageSimulThread) getIntent().getSerializableExtra("thread");
        dst.start();
    }
}
