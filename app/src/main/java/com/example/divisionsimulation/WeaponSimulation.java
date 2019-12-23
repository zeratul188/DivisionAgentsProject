package com.example.divisionsimulation;

class WeaponSimulation {
    private double weapondemage, rpm, critical, criticaldemage, headshot, headshotdemage, elitedemage, shelddemage, healthdemage, reloadtime, ammo;
    private int health;

    public WeaponSimulation(double weapondemage, double rpm, double critical, double criticaldemage, double headshot, double headshotdemage, double elitedemage, double shelddemage, double healthdemage, double reloadtime, double ammo) {
        this.weapondemage = weapondemage;
        this.rpm = rpm;
        this.critical = critical;
        this.criticaldemage = criticaldemage;
        this.headshot = headshot;
        this.headshotdemage = headshotdemage;
        this.elitedemage = elitedemage;
        this.shelddemage = shelddemage;
        this.healthdemage = healthdemage;
        this.reloadtime = reloadtime;
        this.ammo = ammo;
    }

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
        int result, ransu;
        double temp = 0;
        for (int i = 0; i < rps; i++) {
            temp = weapondemage*((healthdemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
        }
        result = (int) temp;
        return Integer.toString(result);
    }

    public String getdpm_health() {
        int result, ransu;
        double temp = 0;
        for (int i = 0; i < rpm; i++) {
            temp = weapondemage*((healthdemage/100)+1);
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= critical) temp *= (criticaldemage/100)+1;
            ransu = (int)(Math.random()*123456)%101;
            if (ransu <= headshot) temp *= (headshotdemage/100)+1;
        }
        result = (int) temp;
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

}
