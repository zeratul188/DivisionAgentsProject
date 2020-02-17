package com.example.divisionsimulation;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

class TimerThread extends Thread {
    private int hour = 0, minute = 0, second = 0;
    private int sum_second = 0, now_sum_second = 0;
    private boolean stop = false;
    private double process;
    private Handler handler;
    private Activity activity;

    public TimerThread(int hour, int minute, int second, Handler handler, Activity activity) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.handler = handler;
        this.activity = activity;
    }
    public void stopThread() { stop = true; }


    public void run() {
        sum_second = (hour*60*60)+(minute*60)+second;
        now_sum_second = (hour*60*60)+(minute*60)+second-1;
        while ((hour != 0 || minute != 0 || second != 0) && !stop) {
            process = ((double)now_sum_second/(double)sum_second)*10000;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (hour != 0) MainActivity.txtTimer.setText(hour+"시간 "+minute+"분 "+second+"초");
                    else if (minute != 0) MainActivity.txtTimer.setText(minute+"분 "+second+"초");
                    else MainActivity.txtTimer.setText(second+"초");
                    MainActivity.progressTimer.setProgress(10000-(int)process);
                }
            });

            now_sum_second --;
            second --;

            if (second < 0) {
                if (minute == 0) {
                    if (hour != 0) {
                        hour--;
                        minute = 59;
                        second = 59;
                    } else break;
                } else {
                    minute --;
                    second = 59;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        if (!stop) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "타이머가 종료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "타이머가 강제 종료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
