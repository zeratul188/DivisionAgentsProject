package com.example.divisionsimulation.ui.home;

import java.io.Serializable;

class DemageSimulThread extends Thread implements Serializable {
    private double weapondemage, rpm, critical, criticaldemage, headshot, headshotdemage, elitedemage, shelddemage, healthdemage, reloadtime, ammo;
    private int health, sheld;
    private boolean elite_true = false;

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
        int time = (int)reloadtime*1000;
        SimulActivity.txtStatue.setText("재장전 중...");
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int demage() {
        int diff_demage = (int)(weapondemage*0.1);
        int ransu = (int)(Math.random()*123456)%(diff_demage*2)-diff_demage;
        int real_demage = (int)weapondemage + ransu;
        return real_demage;
    }

    public void run() {
        int time = (60 * 1000) / (int) rpm;
        int now_ammo = (int) ammo;
        int headshot_ransu, critical_ransu, real_demage;
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
            critical_ransu = (int) (Math.random() * 123456) % 100 + 1;
            headshot_ransu = (int) (Math.random() * 123456) % 100 + 1;
            if (critical_ransu <= critical) {
                per = criticaldemage / 100;
                now_demage *= 1 + per;
            }
            if (headshot_ransu <= 30) {
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
            if (sheld < 0) sheld = 0;
            now_ammo--;
            log = "-" + real_demage;
            ammo_log = "현재 탄수 : "+now_ammo;
            if (critical_ransu <= (int) critical) statue_log += "(크리티컬!!)";
            if (headshot_ransu <= (int) headshot) statue_log += "(헤드샷!!)";
            SimulActivity.txtSheld.setText(Integer.toString(sheld));
            SimulActivity.txtNowDemage.setText(log);
            SimulActivity.txtStatue.setText(statue_log);
            SimulActivity.txtAmmo.setText(ammo_log);
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
            critical_ransu = (int) (Math.random() * 123456) % 100 + 1;
            headshot_ransu = (int) (Math.random() * 123456) % 100 + 1;
            if (critical_ransu <= critical) {
                per = criticaldemage / 100;
                now_demage *= 1 + per;
            }
            if (headshot_ransu <= 30) {
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
            if (health < 0) health = 0;
            now_ammo--;
            log = "-" + real_demage;
            ammo_log = "현재 탄수 : "+now_ammo;
            if (critical_ransu <= (int) critical) statue_log += "(크리티컬!!)";
            if (headshot_ransu <= (int) headshot) statue_log += "(헤드샷!!)";
            SimulActivity.txtHealth.setText(Integer.toString(health));
            SimulActivity.txtNowDemage.setText(log);
            SimulActivity.txtStatue.setText(statue_log);
            SimulActivity.txtAmmo.setText(ammo_log);
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
    }
}
