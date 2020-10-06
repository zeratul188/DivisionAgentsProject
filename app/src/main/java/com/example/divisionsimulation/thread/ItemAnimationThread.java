package com.example.divisionsimulation.thread;

import android.widget.ProgressBar;

import android.os.Handler;

public class ItemAnimationThread extends Thread {
    private ProgressBar progressBar;
    private double max, now = 0;
    private Handler handler = null;

    public ItemAnimationThread(ProgressBar progressBar, double max, Handler handler) {
        this.progressBar = progressBar;
        this.max = max;
        this.handler = handler;
    }

    public void run() {
        while (now < max) {
            try {
                this.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            now += max/100;
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress((int)(now*100));
                    }
                });
            }
        }
    }
}
