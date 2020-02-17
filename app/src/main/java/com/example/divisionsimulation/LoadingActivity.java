package com.example.divisionsimulation;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.MediaController;
import android.widget.VideoView;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends Activity {

    private VideoView aviLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadinglayout);

        aviLoading = (MyVideoView)findViewById(R.id.aviLoading);

        /*MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(aviLoading);*/

        Uri video = Uri.parse("android.resource://"+getPackageName()+"/raw/loding");
        aviLoading.setMediaController(null);
        aviLoading.setVideoURI(video);
        aviLoading.requestFocus();

        aviLoading.start();

        startLoading();
    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
    public void onBackPressed() {

    }
}
