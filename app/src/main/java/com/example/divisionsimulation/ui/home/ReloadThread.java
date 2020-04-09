package com.example.divisionsimulation.ui.home;

import android.os.Handler;

import java.io.Serializable;

/*
스레드에서 UI를 변경할 때 단순히 .setText만 사용하게 되면 되기는 하나 오류가 뜨는 경우가 다수 있다.
스레드에서 UI를 수정할 경우 handler를 사용하여 변경하는 것이 좋다. 오류도 거의 없어진다.
 */

class ReloadThread extends Thread implements Serializable {
    private int time, progress_time = 0; //탄약 1발간 딜레이 시간을 저장하는 변수, 프로그레스 진행도를 저장하는 변수
    private double progress_parcent; // 프로그레스 진행도를 설정하는 변수
    private Handler handler; //스레드에서 UI변경시 사용하는 변수
    private SimulActivity sa = null; //시뮬 액티비티 객체를 가져와 저장할 객체이다.
    private boolean stop = false, pause = true; //재장전 스레드를 종료, 일시정지하는 변수이다.

    public ReloadThread(Handler handler, SimulActivity sa) { //재장전 스레드 생성자
        this.handler = handler;
        this.sa = sa;
    }

    public void setTime(int time) { this.time = time; } //탄약 1발간 딜레이 시간을 저장한다.
    public void stopThread() { stop = true; } //스레드를 정지시킨다.
    public void pause(boolean pause) { this.pause = pause; } //스레드를 다시 시작하거나 일시 정지한다. true = 일시정지, false = 다시시작

    public void run() {
        while (!stop) { //stop 변수가 true가 되면 재장전 스레드는 종료된다.
            if (!pause) { //pause가 true(일시정지)일 경우 작동하지 않아 일시정지상태가 된다. (차후 wait() 함수를 사용하여 일시정지하는 방법을 알아보고 있음)
                progress_parcent = ((double)progress_time / (double)time) * 10000; //프로그레스바의 최대수치가 10000이므로 10000을 기준으로 잡아 백분율로 만든다.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //SimulActivity.progressAmmo.setProgress((int)progress_parcent);
                        sa.setProgressAmmo((int)progress_parcent); //프로그레스 진행도를 설정한다.
                    }
                });
                if (progress_parcent >= 10000) break; //프로그레스가 10000이 넘어가면 이후 작업을 생략시킨다.
                progress_time += 20; //프로그레스 진행 변수를 20씩 증가시킨다.
                try {
                    this.sleep(20); //0.02초만큼 딜레이 시킨다.
                } catch (Exception e) { //오류 발생시 실행
                    // TODO Auto-generated catch block
                    e.printStackTrace(); //콘솔에 오류 메시지 출력
                }
            } else {
                progress_time = 0; //일시 정지되면 진행도를 초기화한다.
                Thread.yield(); //다른 스레드에게 우선순위를 양보한다.
            }
        }
    }
}
