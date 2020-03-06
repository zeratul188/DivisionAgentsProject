package com.example.divisionsimulation.ui.home;


import android.os.Handler;

import java.io.Serializable;

class TimeThread extends Thread implements Serializable {
    private int seconds = 0, hours = 0, minutes = 0;
    private boolean stop = false;

    private Handler handler = null;

    private SimulActivity sa = null;

    public void setSimulActivity(SimulActivity sa) { this.sa = sa; }

    public void setHandler(Handler handler) { this.handler = handler; }

    public void setStop(boolean stop) {
        if (stop) this.stop = true;
        else this.stop = false;
    }

    public void run() {
        while (!stop) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (stop) break;
            seconds++;
            if (seconds == 60) {
                minutes++;
                seconds -= 60;
            }
            if (minutes == 60) {
                hours++;
                minutes -= 60;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (minutes == 0 && hours == 0) {
                        //SimulActivity.txtTime.setText(seconds+"초");
                        sa.setTxtTime(seconds+"초");
                    } else if (hours == 0) {
                        //SimulActivity.txtTime.setText(minutes+"분 "+seconds+"초");
                        sa.setTxtTime(minutes+"분 "+seconds+"초");
                    } else {
                        //SimulActivity.txtTime.setText(hours+"시간 "+minutes+"분 "+seconds+"초");
                        sa.setTxtTime(hours+"시간 "+minutes+"분 "+seconds+"초");
                    }
                }
            });

        }

        System.out.println("(TimeThread) 정상적으로 종료됨");
    }
}
