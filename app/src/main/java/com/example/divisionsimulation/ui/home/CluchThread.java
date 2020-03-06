package com.example.divisionsimulation.ui.home;

import android.os.Handler;

import java.io.Serializable;

/*
클러치 스레드는 적이 클러치 사용한 상태일 때 상대방 체력을 회복시키는 스레드다.
단, 방어도는 회복하지 않는다.
 */

class CluchThread extends Thread implements Serializable {
    private int temp_health, first_health; //현재 남은 체력, 최대 체력
    private int rpm, ammo; //RPM, 탄약 수
    private double reload, critical, aiming; //재장전 시간, 크리티컬 확률, 명중률
    private boolean stop = false; //스레드 종료 여부
    private double dec_health; //프로그래스바 진행도

    private Handler handler; //UI 변경시 필요한 핸들러
    private SimulActivity sa = null;

    public void setHandler(Handler handler) { this.handler = handler; } //외부 액티비티의 UI를 변경할 수 있게 해주는 핸들러를 가져온다.

    public CluchThread(int rpm, int ammo, double reload, double critical, double aiming) { //클러치 스레드에 변수에 대한 데이터를 스레드를 생성하면서 가져온다.
        this.rpm = rpm;
        this.ammo = ammo;
        this.reload = reload;
        this.critical = critical;
        this.aiming = aiming;
    }

    public void setSimulActivity(SimulActivity sa) { this.sa = sa; }

    public void reload() { //재장전 메소드다.
        int time = (int)(reload * 1000); //재장전하는 시간을 나타낸다.
        try {
            Thread.sleep(time); //재장전 시간만큼 스레드를 일시 정지시킨다.
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void setFirst_health(int first_health) { this.first_health = first_health; } //최대 체력을 저장시키는 메소드다.

    public void setStop(boolean stop) { //스레드를 정지시키는 스레드다.
        if (stop) this.stop = true;
        else this.stop = false;
    }

    public void run() {
        //System.out.println("Health : "+first_health+"\nRPM : "+rpm+"\ncritical : "+critical+"\nAmmo : "+ammo+"\nReload time : "+reload);
        int time = (60 * 1000) / rpm; // RPM으로 인해 1발간격당 딜레이를 나타낸다.
        int now_ammo = ammo; //현재 남은 탄약수를 나타내는 변수이다.
        int temp_critical, random, per; //기타 스레드에 필요한 임시 변수들이다.
        if (aiming == 0) aiming = 50; //명중률이 0%이면 자동으로 50%로 올려준다.
        while (!stop) { //stop이 true가 되면 스레드가 종료된다는 의미이다.
            //System.out.println("play Cluch + "+time);
           try {
               if (SimulActivity.getHealth() <= 0) break; //체력이 0 이하가 되면 사망한 것이므로 스레드도 당연 중지된다.
               temp_critical = (int)(critical * 10); //치명타 확률은 소수점 1자리까지 되므로 1000까지 올려준다.
               random = (int)(Math.random()*1234567)%1000+1; //random 변수에는 1~1000까지 임의의 숫자를 저장하게 해서 치명타가 확률대로 터지게 만들어준다.
               if (temp_critical >= random) { //예를 들어 치명타 확률이 40.5퍼면 램덤수가 405 이하가 될 경우 작동하게 하여 치명타 확률이 정확하게 되도록 해준다.
                   temp_health = SimulActivity.getHealth(); //현재 남은 체력은 체력을 담당하는 시뮬액티비티에서 가져온다.
                   if (now_ammo == 0) { //남은 탄약 수가 없을 경우 재장전한다.
                       now_ammo = ammo; //탄약수는 다 채워진다.
                       reload(); //재장전동안 스레드를 일시 정지시키는 메소드이다.
                   }
                   else {
                       now_ammo--; //사격했으므로 탄약수 1씩 줄여준다.
                       per = (int)(Math.random()*1234567)%1000+1;
                       if (aiming*10 >= per) { //명중률에 해당하여 타격할 경우에 실행된다.
                           temp_health += (first_health - temp_health) / 4; //현재 까인 체력만큼의 25%만큼 회복하므로 전체 체력에서 남은 체력을 뺀 후 25%만큼을 늘려준다.
                           dec_health = ((double)temp_health / (double)first_health) * 10000; //진행도를 다시 조정한다.
                           SimulActivity.setHealth(temp_health); //클러치로 인해 체력이 증가한 것을 다시 갱신시켜준다.
                           handler.post(new Runnable() {
                               @Override
                               public void run() {
                                   //SimulActivity.txtHealth.setText(Integer.toString(temp_health)+"/"+first_health);
                                   sa.setTxtHealth(Integer.toString(temp_health)+"/"+first_health); //체력을 올려준만큼 액티비티 화면에도 갱신시킨다.
                                   //SimulActivity.progressHealth.setProgress((int)dec_health);
                                   sa.setProgressHealth((int)dec_health); //위와 동일
                               }
                           });
                       }
                       try {
                           this.sleep(time); //1발당 딜레이만큼 일시 정지시켜준다.
                       } catch (Exception e) {
                           System.err.println(e);
                       }
                   }
               }
           } catch (Exception e) {
               System.err.println(e);
               stop = true;
           }
        }

        System.out.println("(CluchThread) 정상적으로 종료됨"); //스레드가 정상적으로 종료됐다는 것을 콘솔에 출력으로 알려준다.
    }
}
