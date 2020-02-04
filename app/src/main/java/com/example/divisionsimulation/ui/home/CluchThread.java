package com.example.divisionsimulation.ui.home;

import java.io.Serializable;

class CluchThread extends Thread implements Serializable, Runnable {
    private int temp_health, first_health;
    private int rpm, ammo;
    private double reload, critical;
    private boolean stop = false;

    public CluchThread(int rpm, int ammo, double reload, double critical) {
        this.rpm = rpm;
        this.ammo = ammo;
        this.reload = reload;
        this.critical = critical;
    }

    public void reload() {
        int time = (int)(reload * 1000);
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void setFirst_health(int first_health) { this.first_health = first_health; }

    public void setStop(boolean stop) {
        if (stop) this.stop = true;
        else this.stop = false;
    }

    public void run() {
        System.out.println("Health : "+first_health+"\nRPM : "+rpm+"\ncritical : "+critical+"\nAmmo : "+ammo+"\nReload time : "+reload);
        int time = (60 * 1000) / rpm;
        int now_ammo = ammo;
        int temp_critical, random;
        double dec_health;
        while (!stop) {
            System.out.println("play Cluch + "+time);
            temp_critical = (int)(critical * 10);
            random = (int)(Math.random()*1234567)%1000+1;
            if (temp_critical >= random) {
                temp_health = SimulActivity.getHealth();
                if (now_ammo == 0) {
                    now_ammo = ammo;
                    reload();
                }
                else {
                    temp_health += (first_health - temp_health) / 4;
                    SimulActivity.setHealth(temp_health);
                    now_ammo--;
                    SimulActivity.txtHealth.setText(Integer.toString(temp_health)+"/"+first_health);
                    dec_health = ((double)temp_health / (double)first_health) * 10000;
                    SimulActivity.progressHealth.setProgress((int)dec_health);
                    try {
                        this.sleep(time);
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
            }
        }

        System.out.println("(CluchThread) 정상적으로 종료됨");
    }
}
