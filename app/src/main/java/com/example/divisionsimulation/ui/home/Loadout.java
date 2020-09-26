package com.example.divisionsimulation.ui.home;

public class Loadout {
    private double weapondemage, rpm = 0, critical = 0, critical_demage = 0, headshot = 0, headshot_demage = 0, nohide = 0, armor = 0, health = 0, reload = 0, ammo, aim;
    private long rowID;
    private String make;

    public Loadout(long rowID, double weapondemage, double rpm, double ammo, double aim, String make) {
        this.rowID = rowID;
        this.weapondemage = weapondemage;
        this.rpm = rpm;
        this.ammo = ammo;
        this.aim = aim;
        this.make = make;
    }

    public long getRowID() {
        return rowID;
    }

    public void setRowID(long rowID) {
        this.rowID = rowID;
    }

    public double getWeapondemage() {
        return weapondemage;
    }

    public void setWeapondemage(double weapondemage) {
        this.weapondemage = weapondemage;
    }

    public double getRpm() {
        return rpm;
    }

    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    public double getCritical() {
        return critical;
    }

    public void setCritical(double critical) {
        this.critical = critical;
    }

    public double getCritical_demage() {
        return critical_demage;
    }

    public void setCritical_demage(double critical_demage) {
        this.critical_demage = critical_demage;
    }

    public double getHeadshot() {
        return headshot;
    }

    public void setHeadshot(double headshot) {
        this.headshot = headshot;
    }

    public double getHeadshot_demage() {
        return headshot_demage;
    }

    public void setHeadshot_demage(double headshot_demage) {
        this.headshot_demage = headshot_demage;
    }

    public double getNohide() {
        return nohide;
    }

    public void setNohide(double nohide) {
        this.nohide = nohide;
    }

    public double getArmor() {
        return armor;
    }

    public void setArmor(double armor) {
        this.armor = armor;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getReload() {
        return reload;
    }

    public void setReload(double reload) {
        this.reload = reload;
    }

    public double getAmmo() {
        return ammo;
    }

    public void setAmmo(double ammo) {
        this.ammo = ammo;
    }

    public double getAim() {
        return aim;
    }

    public void setAim(double aim) {
        this.aim = aim;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }
}
