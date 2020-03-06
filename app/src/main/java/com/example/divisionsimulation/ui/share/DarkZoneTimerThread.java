package com.example.divisionsimulation.ui.share;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.divisionsimulation.MainActivity;

class DarkZoneTimerThread extends Thread {
    private int minute = 0, second = 0;
    private int sum_second = 0, now_sum_second = 0;
    private boolean stop = false, outputing = false, rogue = false, input_rogue = false;
    private double process;
    private Handler handler = null;
    private Activity activity = null;
    private ShareFragment sf = null;
    private int rogue_percent = 0;

    public DarkZoneTimerThread(Handler handler, Activity activity, ShareFragment sf) {
        this.handler = handler;
        this.activity = activity;
        this.sf = sf;
    }
    public void stopThread() { stop = true; }
    public void setMinute(int minute) { this.minute = minute; }
    public void setSecond(int second) { this.second = second; }
    public void setOutputing(boolean outputing) { this.outputing = outputing; }
    public void setInput_rogue(boolean input_rogue) { this.input_rogue = input_rogue; }
    public boolean getInput_rogue() { return input_rogue; }
    public void setRogue(boolean rogue) { this.rogue = rogue; }
    public void setRoguePercent(int rogue_percent) { this.rogue_percent = rogue_percent; }

    public int randomRogue(int min, int length) {
        return (int)(Math.random()*1234567)%length+min;
    };

    public void run() {
        sum_second = (minute*60)+second;
        now_sum_second = (minute*60)+second-1;
        while (((minute != 0 || second != 0) || now_sum_second != -1) && !stop && !rogue) {
            process = ((double)now_sum_second/(double)sum_second)*10000;

            if (minute != 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //ShareFragment.txtTimer.setText(minute+"분 "+second+"초");
                        sf.setTxtTimer(minute+"분 "+second+"초");
                        sf.setProgressTimer(10000-(int)process);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //ShareFragment.txtTimer.setText(second+"초");
                        sf.setTxtTimer(second+"초");
                        sf.setProgressTimer(10000-(int)process);
                    }
                });
            }
            /*handler.post(new Runnable() {
                @Override
                public void run() {
                    ShareFragment.progressTimer.setProgress(10000-(int)process);
                }
            });*/

            now_sum_second --;
            second --;

            if (second < 0) {
                if (minute == 0) {
                    break;
                } else {
                    minute --;
                    second = 59;
                }
            }

            if (input_rogue && randomRogue(1, 100) < 10) {
                sf.deleteDZitem();
                rogue = true;
            }
            if ((randomRogue(1, 100) < rogue_percent) && !input_rogue) {
                input_rogue = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "주위에 로그 요원이 있습니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            if (rogue) break;

            if (now_sum_second != -1) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (!rogue) {
            if (!stop) {
                if (!outputing) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "헬기가 도착했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "이송을 완료했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "목표를 정지합니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (!outputing) sf.playOutputDZ();
            if (outputing) sf.dialogOpen();
        }
    }
}
