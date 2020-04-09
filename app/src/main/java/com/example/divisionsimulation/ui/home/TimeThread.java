package com.example.divisionsimulation.ui.home;


import android.os.Handler;

import java.io.Serializable;

class TimeThread extends Thread implements Serializable {
    private int seconds = 0, hours = 0, minutes = 0; //초, 분, 시간을 저장하는 변수
    private boolean stop = false; //타임 스레드 종료 변수

    private Handler handler = null; //UI변경시 사용할 핸들러

    private SimulActivity sa = null; //액티비티

    public void setSimulActivity(SimulActivity sa) { this.sa = sa; } //액티비티 설정

    public void setHandler(Handler handler) { this.handler = handler; } //핸들러 가져오는 메소드

    public void setStop(boolean stop) { //스레드 종료 메소드
        if (stop) this.stop = true; //true가 들어오게 되면 종료되도록 종료 변수를 참으로 변경해준다.
        else this.stop = false; //위와 반대로 종료되지 않게 해준다.
    }

    public void run() {
        while (!stop) { //stop이 참이 될 경우 스레드가 종료되도록 한다. 참이 되지 않으면 무한 반복한다.
            try {
                Thread.sleep(1000); //1초동안 딜레이시킨다.
            } catch (InterruptedException e) {
                e.printStackTrace(); //sleep 과정에서 오류가 발생시 콘솔에 오류 메시지를 출력한다.
            }
            if (stop) break; //stop이 참이 되면 중간에 종료시킨다.
            seconds++; //1초를 늘려준다.
            if (seconds == 60) { //초가 60이 되면 작동한다.
                minutes++; //1분 늘려준다.
                seconds -= 60; //1분 늘어났으므로 초는 60을 감소시켜 0으로 초기화시켜준다.
            }
            if (minutes == 60) { //분이 60이 되면 작동한다.
                hours++; //1시간 늘려준다.
                minutes -= 60; //1시간 늘어났으므로 분은 60을 감소시켜 0으로 초기화시켜준다.
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (minutes == 0 && hours == 0) { //분, 시간이 0이면 출력할 필요없으므로 초단위만 보여준다.
                        //SimulActivity.txtTime.setText(seconds+"초");
                        sa.setTxtTime(seconds+"초"); //초 단위만 보여준다.
                    } else if (hours == 0) { //시간만 0이면 시간만 빼고 보여준다.
                        //SimulActivity.txtTime.setText(minutes+"분 "+seconds+"초");
                        sa.setTxtTime(minutes+"분 "+seconds+"초"); //시간 없이 분, 초만 보여준다.
                    } else { //시간, 분, 초 모두 1이상일 경우
                        //SimulActivity.txtTime.setText(hours+"시간 "+minutes+"분 "+seconds+"초");
                        sa.setTxtTime(hours+"시간 "+minutes+"분 "+seconds+"초"); //시간, 분, 초 모두 화면에 보여준다.
                    }
                }
            });

        }

        System.out.println("(TimeThread) 정상적으로 종료됨"); //콘솔에 타임 스레드가 종료되었음을 출력한다.
    }
}
