package com.example.divisionsimulation.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class DemageSimulationActivity extends AppCompatActivity {

    private TextView txtBodyHealth, txtBodyCriticalHealth, txtHeadshotHealth, txtHeadshotCriticalHealth, txtDPSHealth, txtDPMHealth;
    private TextView txtBodySheld, txtBodyCriticalSheld, txtHeadshotSheld, txtHeadshotCriticalSheld, txtDPSSheld, txtDPMSheld;
    private TextView txtBodyHealthElite, txtBodyCriticalHealthElite, txtHeadshotHealthElite, txtHeadshotCriticalHealthElite, txtDPSHealthElite, txtDPMHealthElite;
    private TextView txtBodySheldElite, txtBodyCriticalSheldElite, txtHeadshotSheldElite, txtHeadshotCriticalSheldElite, txtDPSSheldElite, txtDPMSheldElite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demagesimulationlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("DPS 정보");

        Intent intent = getIntent();

        txtBodyHealth = findViewById(R.id.txtBodyHealth);
        txtBodyCriticalHealth = findViewById(R.id.txtBodyCriticalHealth);
        txtHeadshotHealth = findViewById(R.id.txtHeadshotHealth);
        txtHeadshotCriticalHealth = findViewById(R.id.txtHeadshotCriticalHealth);
        txtDPSHealth = findViewById(R.id.txtBodyDPSHealth);
        txtDPMHealth = findViewById(R.id.txtBodyDPMHealth);

        txtBodyHealth.setText(intent.getExtras().getString("bodyhealth"));
        txtBodyCriticalHealth.setText(intent.getExtras().getString("bodycriticalhealth"));
        txtHeadshotHealth.setText(intent.getExtras().getString("headshothealth"));
        txtHeadshotCriticalHealth.setText(intent.getExtras().getString("headshotcriticalhealth"));
        txtDPSHealth.setText(intent.getExtras().getString("healthDPS"));
        txtDPMHealth.setText(intent.getExtras().getString("healthDPM"));

        txtBodySheld = findViewById(R.id.txtBodySheld);
        txtBodyCriticalSheld = findViewById(R.id.txtBodyCriticalSheld);
        txtHeadshotSheld = findViewById(R.id.txtHeadshotSheld);
        txtHeadshotCriticalSheld = findViewById(R.id.txtHeadshotCriticalSheld);
        txtDPSSheld = findViewById(R.id.txtBodyDPSSheld);
        txtDPMSheld = findViewById(R.id.txtBodyDPMSheld);

        txtBodySheld.setText(intent.getExtras().getString("bodysheld"));
        txtBodyCriticalSheld.setText(intent.getExtras().getString("bodycriticalsheld"));
        txtHeadshotSheld.setText(intent.getExtras().getString("headshotsheld"));
        txtHeadshotCriticalSheld.setText(intent.getExtras().getString("headshotcriticalsheld"));
        txtDPSSheld.setText(intent.getExtras().getString("sheldDPS"));
        txtDPMSheld.setText(intent.getExtras().getString("sheldDPM"));

        txtBodyHealthElite = findViewById(R.id.txtBodyHealthElite);
        txtBodyCriticalHealthElite = findViewById(R.id.txtBodyCriticalHealthElite);
        txtHeadshotHealthElite = findViewById(R.id.txtHeadshotHealthElite);
        txtHeadshotCriticalHealthElite = findViewById(R.id.txtHeadshotCriticalHealthElite);
        txtDPSHealthElite = findViewById(R.id.txtBodyDPSHealthElite);
        txtDPMHealthElite = findViewById(R.id.txtBodyDPMHealthElite);

        txtBodyHealthElite.setText(intent.getExtras().getString("bodyhealthelite"));
        txtBodyCriticalHealthElite.setText(intent.getExtras().getString("bodycriticalhealthelite"));
        txtHeadshotHealthElite.setText(intent.getExtras().getString("headshothealthelite"));
        txtHeadshotCriticalHealthElite.setText(intent.getExtras().getString("headshotcriticalhealthelite"));
        txtDPSHealthElite.setText(intent.getExtras().getString("elitehealthDPS"));
        txtDPMHealthElite.setText(intent.getExtras().getString("elitehealthDPM"));

        txtBodySheldElite = findViewById(R.id.txtBodySheldElite);
        txtBodyCriticalSheldElite = findViewById(R.id.txtBodyCriticalSheldElite);
        txtHeadshotSheldElite = findViewById(R.id.txtHeadshotSheldElite);
        txtHeadshotCriticalSheldElite = findViewById(R.id.txtHeadshotCriticalSheldElite);
        txtDPSSheldElite = findViewById(R.id.txtBodyDPSSheldElite);
        txtDPMSheldElite = findViewById(R.id.txtBodyDPMSheldElite);

        txtBodySheldElite.setText(intent.getExtras().getString("bodysheldelite"));
        txtBodyCriticalSheldElite.setText(intent.getExtras().getString("bodycriticalsheldelite"));
        txtHeadshotSheldElite.setText(intent.getExtras().getString("headshotsheldelite"));
        txtHeadshotCriticalSheldElite.setText(intent.getExtras().getString("headshotcriticalsheldelite"));
        txtDPSSheldElite.setText(intent.getExtras().getString("elitesheldDPS"));
        txtDPMSheldElite.setText(intent.getExtras().getString("elitesheldDPM"));

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
