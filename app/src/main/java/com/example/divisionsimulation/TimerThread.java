package com.example.divisionsimulation;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

class TimerThread extends Thread {
    private int hour = 0, minute = 0, second = 0;
    private int sum_second = 0, now_sum_second = 0;
    private boolean stop = false;
    private double process;
    private Handler handler;
    private Activity activity;
    private NotificationManager nm = null;
    private Context context = null;
    private String NOTIFICATION_ID = "10001";
    private MainActivity ma = null;
    private String time_text;
    private TextView txtTImer;
    private ProgressBar progressTImer;

    public TimerThread(int hour, int minute, int second, Handler handler, Activity activity, NotificationManager nm, Context context, TextView txtTimer, ProgressBar progressTimer) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.handler = handler;
        this.activity = activity;
        this.nm = nm;
        this.context = context;
        this.txtTImer = txtTimer;
        this.progressTImer = progressTimer;
    }
    public void stopThread() { stop = true; }

    public void run() {
        sum_second = (hour*60*60)+(minute*60)+second;
        now_sum_second = (hour*60*60)+(minute*60)+second-1;
        while (((hour != 0 || minute != 0 || second != 0) || now_sum_second != -1) && !stop) {
            process = ((double)now_sum_second/(double)sum_second)*10000;

            if (hour != 0) {
                if (second != 0) time_text = hour+"시간 "+minute+"분 "+(second-1)+"초";
                else time_text = hour+"시간 "+minute+"분 0초";
            }else if (minute != 0) {
                if (second != 0) time_text = minute+"분 "+(second-1)+"초";
                else time_text = minute+"분 0초";
            } else {
                if (second != 0) time_text = (second-1)+"초";
                else time_text = "0초";
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //MainActivity.txtTimer.setText(second+"초");
                    txtTImer.setText(time_text);
                    progressTImer.setProgress(10000-(int)process);
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

            if (now_sum_second != -1) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (!stop) {
            nm.cancelAll();
            NotificationCompat.Builder buildert = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                    .setContentTitle("목표 달성") //타이틀 TEXT
                    .setContentText("'목표를 달성했습니다.") //서브 타이틀 TEXT
                    .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                    .setSound(null)
                    .setOngoing(false) // 사용자가 직접 못지우게 계속 실행하기.
            ;

            nm.notify(0, buildert.build());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "목표를 달성했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            nm.cancelAll();
            NotificationCompat.Builder buildert = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_division2_logo)) //BitMap 이미지 요구
                    .setContentTitle("타이머 종료") //타이틀 TEXT
                    .setContentText("타이머를 종료합니다.") //서브 타이틀 TEXT
                    .setSmallIcon (R.drawable.ic_division2_logo) //필수 (안해주면 에러)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT) //중요도 기본
                    .setSound(null)
                    .setOngoing(false) // 사용자가 직접 못지우게 계속 실행하기.
            ;

            nm.notify(0, buildert.build());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "목표를 정지합니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
