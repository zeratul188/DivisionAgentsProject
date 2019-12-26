package com.example.divisionsimulation.ui.home;

import android.text.Editable;

class WeaponSimulation {
    private double weapondemage, rpm, critical, criticaldemage, headshot, headshotdemage, elitedemage, shelddemage, healthdemage, reloadtime, ammo;

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

    public String getbody_health() {
        int result = (int)(weapondemage * ((healthdemage/100)+1));
        return Integer.toString(result);
    }

    public String getbody_critical_health() {
        int result = (int)(weapondemage*((healthdemage/100)+1)*((criticaldemage/100)+1));
        return Integer.toString(result);
    }

    public String getheadshot_health() {
        int result = (int)(weapondemage*((healthdemage/100)+1)*((headshotdemage/100)+1));
        return Integer.toString(result);
    }

    public String getheadshot_critical_health() {
        int result = (int)(weapondemage*((healthdemage/100)+1)*((criticaldemage/100)+1)*((headshotdemage/100)+1));
        return Integer.toString(result);
    }

    public String getdps_health() {
        int rps = (int)(rpm / 60);
        int result = 0, ransu;
        double temp = 0;
        for (int i = 0; i < rps; i++) {
            temp = weapondemage*((healthdemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
            result += (int) temp;
        }
        return Integer.toString(result);
    }

    public String getdpm_health() {
        int result = 0, ransu;
        double temp = 0;
        for (int i = 0; i < rpm; i++) {
            if (0 == (i%(int)ammo)) {
                i += (int) reloadtime-1;
                continue;
            }
            temp = weapondemage*((healthdemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
            result += (int) temp;
        }
        return Integer.toString(result);
    }

    public String getbody_sheld() {
        int result = (int)(weapondemage*((shelddemage/100)+1));
        return Integer.toString(result);
    }

    public String getbody_critical_sheld() {
        int result = (int)(weapondemage*((shelddemage/100)+1)*((criticaldemage/100)+1));
        return Integer.toString(result);
    }

    public String getheadshot_sheld() {
        int result = (int)(weapondemage*((shelddemage/100)+1)*((headshotdemage/100)+1));
        return Integer.toString(result);
    }

    public String getheadshot_critical_sheld() {
        int result = (int)(weapondemage*((shelddemage/100)+1)*((criticaldemage/100)+1)*((headshotdemage/100)+1));
        return Integer.toString(result);
    }

    public String getdps_sheld() {
        int rps = (int)(rpm / 60);
        int result = 0, ransu;
        double temp = 0;
        for (int i = 0; i < rps; i++) {
            temp = weapondemage*((shelddemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
            result += (int) temp;
        }
        return Integer.toString(result);
    }

    public String getdpm_sheld() {
        int result = 0, ransu;
        double temp = 0;
        for (int i = 0; i < rpm; i++) {
            if (0 == (i%(int)ammo)) {
                i += (int) reloadtime-1;
                continue;
            }
            temp = weapondemage*((shelddemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
            result += (int) temp;
        }
        return Integer.toString(result);
    }

    public String getelite_body_health() {
        int result = (int)(weapondemage*((elitedemage/100)+1)*((healthdemage/100)+1));
        return Integer.toString(result);
    }

    public String getelite_body_critical_health() {
        int result = (int)(weapondemage*((elitedemage/100)+1)*((criticaldemage/100)+1)*((healthdemage/100)+1));
        return Integer.toString(result);
    }

    public String getelite_headshot_health() {
        int result = (int)(weapondemage*((elitedemage/100)+1)*((headshotdemage/100)+1)*((healthdemage/100)+1));
        return Integer.toString(result);
    }

    public String getelite_headshot_critical_health() {
        int result = (int)(weapondemage*((elitedemage/100)+1)*((headshotdemage/100)+1)*((criticaldemage/100)+1)*((healthdemage/100)+1));
        return Integer.toString(result);
    }

    public String getdps_elite_health() {
        int rps = (int)(rpm / 60);
        int result = 0, ransu;
        double temp = 0;
        for (int i = 0; i < rps; i++) {
            temp = weapondemage*((elitedemage/100)+1)*((healthdemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
            result += (int) temp;
        }
        return Integer.toString(result);
    }

    public String getdpm_elite_health() {
        int result = 0, ransu;
        double temp = 0;
        for (int i = 0; i < rpm; i++) {
            if (0 == (i%(int)ammo)) {
                i += (int) reloadtime-1;
                continue;
            }
            temp = weapondemage*((elitedemage/100)+1)*((healthdemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
            result += (int) temp;
        }
        return Integer.toString(result);
    }

    public String getelite_body_sheld() {
        int result = (int)(weapondemage*((elitedemage/100)+1)*((shelddemage/100)+1));
        return Integer.toString(result);
    }

    public String getelite_body_critical_sheld() {
        int result = (int)(weapondemage*((elitedemage/100)+1)*((criticaldemage/100)+1)*((shelddemage/100)+1));
        return Integer.toString(result);
    }

    public String getelite_headshot_sheld() {
        int result = (int)(weapondemage*((elitedemage/100)+1)*((headshotdemage/100)+1)*((shelddemage/100)+1));
        return Integer.toString(result);
    }

    public String getelite_headshot_critical_sheld() {
        int result = (int)(weapondemage*((elitedemage/100)+1)*((headshotdemage/100)+1)*((criticaldemage/100)+1)*((shelddemage/100)+1));
        return Integer.toString(result);
    }

    public String getdps_elite_sheld() {
        int rps = (int)(rpm / 60);
        int result = 0, ransu;
        double temp = 0;
        for (int i = 0; i < rps; i++) {
            temp = weapondemage*((elitedemage/100)+1)*((shelddemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
            result += (int) temp;
        }
        return Integer.toString(result);
    }

    public String getdpm_elite_sheld() {
        int result = 0, ransu;
        double temp = 0;
        for (int i = 0; i < rpm; i++) {
            if (0 == (i%(int)ammo)) {
                i += (int) reloadtime-1;
                continue;
            }
            temp = weapondemage*((elitedemage/100)+1)*((shelddemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
            result += (int) temp;
        }
        return Integer.toString(result);
    }
}
