package com.example.divisionsimulation.ui.home;

import android.os.Handler;

import java.io.Serializable;

class ReloadThread extends Thread implements Serializable {
    private int time, progress_time = 0;
    private double progress_parcent;
    private Handler handler;
    private SimulActivity sa = null;
    private boolean stop = false, pause = true;

    public ReloadThread(Handler handler, SimulActivity sa) {
        this.handler = handler;
        this.sa = sa;
    }

    public void setTime(int time) { this.time = time; }
    public void stopThread() { stop = true; }
    public void pause(boolean pause) { this.pause = pause; }

    public void run() {
        while (!stop) {
            if (!pause) {
                progress_parcent = ((double)progress_time / (double)time) * 10000;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //SimulActivity.progressAmmo.setProgress((int)progress_parcent);
                        sa.setProgressAmmo((int)progress_parcent);
                    }
                });
                if (progress_parcent >= 10000) break;
                progress_time += 20;
                try {
                    this.sleep(20);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                progress_time = 0;
                Thread.yield();
            }
        }
    }
}
