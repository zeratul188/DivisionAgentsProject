package com.example.divisionsimulation;

public class LibraryItem {
    private String name, type, weapon_type;
    private double max;

    public LibraryItem(String name, String type, double max) {
        this.name = name;
        this.type = type;
        this.max = max;
        weapon_type = "";
    }

    public String getWeaponType() {
        return weapon_type;
    }

    public void setWeaponType(String weapon_type) {
        this.weapon_type = weapon_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
