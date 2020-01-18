package com.example.divisionsimulation.ui.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;

class DemageSimulThread extends Thread implements Serializable, Runnable  {
    private double weapondemage, rpm, critical, criticaldemage, headshot, headshotdemage, elitedemage, shelddemage, healthdemage, reloadtime, ammo;
    private int health, sheld, all_ammo = 0;
    private boolean elite_true = false;
    private int first_health, first_sheld;
    private double dec_health, dec_sheld, dec_ammo;
    private TimeThread tt;
    private Context context;

    private boolean headshot_enable = false;
    private boolean critical_enable = false;

    /*final Handler headshot_handle = new Handler(){

        public void handleMessage(Message msg){
            // 원래 하려던 동작 (UI변경 작업 등)
            if (headshot_enable) SimulActivity.changeHeadshot(true);
            else SimulActivity.changeHeadshot(false);
        }
    };

    final Handler critical_handle = new Handler() {

        public void handleMessage(Message msg) {
            if (critical_enable) SimulActivity.changeCritical(true);
            else SimulActivity.changeCritical(false);
        }
    };*/

    public void setTimeThread(TimeThread tt) {
        this.tt = tt;
    }

    public void setWeapondemage(double weapondemage) { this.weapondemage = weapondemage; }
    public void setRPM(double rpm) { this.rpm = rpm; }
    public void setCritical(double critical) { this.critical = critical; }
    public void setCriticaldemage(double criticaldemage) { this.criticaldemage = criticaldemage; }
    public void setHeadshot(double headshot) { this.headshot = headshot; }
    public void setHeadshotdemage(double headshotdemage) { this.headshotdemage = headshotdemage; }
    public void setElitedemage(double elitedemage) { this.elitedemage = elitedemage; }
    public void setShelddemage(double shelddemage) { this.shelddemage = shelddemage; }
    public void setHealthdemage(double healthdemage) { this.healthdemage = healthdemage; }
    public void setReloadtime(double reloadtime) { this.reloadtime = reloadtime; }
    public void setAmmo(double ammo) { this.ammo = ammo; }
    public void setHealth(int health) { this.health = health; }
    public void setSheld(int sheld) { this.sheld = sheld; }
    public void setElite_true(boolean elite_true) { this.elite_true = elite_true; }

    private void reload() {
        int time = (int)(reloadtime*1000);
        SimulActivity.progressAmmo.setIndeterminate(true);
        SimulActivity.txtNowDemage.setText("재장전 중...");
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SimulActivity.progressAmmo.setIndeterminate(false);
    }

    private int demage() {
        int diff_demage = (int)(weapondemage*0.1);
        int ransu = (int)(Math.random()*123456)%(diff_demage*2)-diff_demage;
        int real_demage = (int)weapondemage + ransu;
        return real_demage;
    }

    public void run() {
        first_health = health;
        first_sheld = sheld;
        int time = (60 * 1000) / (int) rpm;
        int now_ammo = (int) ammo;
        int headshot_ransu, critical_ransu, real_demage;
        int all_dmg = 0;
        double now_demage;
        System.out.println("Start HP : " + health);
        String log, statue_log = "", ammo_log = "";
        double per;
        SimulActivity.txtSheld.setText(Integer.toString(sheld));
        SimulActivity.txtHealth.setText(Integer.toString(health));
        while (sheld > 0 && !Thread.interrupted()) {
            statue_log = "";
            ammo_log = "";
            now_demage = demage();
            critical_ransu = (int) (Math.random() * 123456) % 1001;
            headshot_ransu = (int) (Math.random() * 123456) % 1001;
            if (critical_ransu <= critical*10) {
                per = criticaldemage / 100;
                now_demage *= 1 + per;
            }
            if (headshot_ransu <= headshot*10) {
                per = headshotdemage / 100;
                now_demage *= 1 + per;
            }
            per = shelddemage/100;
            now_demage *= 1+per;
            if (elite_true == true) {
                per = elitedemage/100;
                now_demage *= 1+per;
            }
            real_demage = (int) now_demage;
            sheld -= real_demage;
            all_dmg += real_demage;
            if (sheld < 0) sheld = 0;
            now_ammo--;
            all_ammo++;
            log = "-" + real_demage;
            ammo_log = "현재 탄수 : "+now_ammo;
            if (critical_ransu <= (int) critical*10) statue_log += "(치명타!!)";
            if (headshot_ransu <= (int) headshot*10) statue_log += "(헤드샷!!)";
            SimulActivity.txtSheld.setText(Integer.toString(sheld));
            SimulActivity.txtNowDemage.setText(log);
            SimulActivity.txtAmmo.setText(ammo_log);
            SimulActivity.txtStatue.setText(statue_log);
            SimulActivity.txtAllAmmo.setText(Integer.toString(all_ammo));
            SimulActivity.txtAdddemage.setText(Integer.toString(all_dmg));
            dec_sheld = ((double)sheld / (double)first_sheld) * 10000;
            SimulActivity.progressSheld.setProgress((int)dec_sheld);
            dec_ammo = ((double)now_ammo / (double)ammo) * 10000;
            SimulActivity.progressAmmo.setProgress((int)dec_ammo);
            if (now_ammo == 0) {
                reload();
                now_ammo += (int) ammo;
            } else {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        while (health > 0 && !Thread.interrupted()) {
            statue_log = "";
            ammo_log = "";
            now_demage = demage();
            critical_ransu = (int) (Math.random() * 123456) % 1001;
            headshot_ransu = (int) (Math.random() * 123456) % 1001;
            if (critical_ransu <= critical*10) {
                per = criticaldemage / 100;
                now_demage *= 1 + per;
            }
            if (headshot_ransu <= headshot*10) {
                per = headshotdemage / 100;
                now_demage *= 1 + per;
            }
            per = healthdemage/100;
            now_demage *= 1+per;
            if (elite_true == true) {
                per = elitedemage/100;
                now_demage *= 1+per;
            }
            real_demage = (int) now_demage;
            health -= real_demage;
            all_dmg += real_demage;
            if (health < 0) health = 0;
            now_ammo--;
            all_ammo++;
            log = "-" + real_demage;
            ammo_log = "현재 탄수 : "+now_ammo;
            if (critical_ransu <= (int) critical*10) statue_log += "(치명타!!)";
            if (headshot_ransu <= (int) headshot*10) statue_log += "(헤드샷!!)";
            SimulActivity.txtHealth.setText(Integer.toString(health));
            SimulActivity.txtNowDemage.setText(log);
            SimulActivity.txtAmmo.setText(ammo_log);
            SimulActivity.txtStatue.setText(statue_log);
            SimulActivity.txtAllAmmo.setText(Integer.toString(all_ammo));
            SimulActivity.txtAdddemage.setText(Integer.toString(all_dmg));
            dec_health = ((double)health / (double)first_health) * 10000;
            SimulActivity.progressHealth.setProgress((int)dec_health);
            dec_ammo = ((double)now_ammo / (double)ammo) * 10000;
            SimulActivity.progressAmmo.setProgress((int)dec_ammo);
            if (now_ammo == 0) {
                reload();
                now_ammo += (int) ammo;
            } else {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        tt.setStop(true);
        System.out.println("(DemageSimulThread) 정상적으로 종료됨");
    }
}
