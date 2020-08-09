package com.example.divisionsimulation.ui.share;

public class ItemData {
    private String core1_name, core2_name, sub1_name, sub2_name;
    private double core1, core2, sub1, sub2;

    public void setWeaponData(String core1_name, String core2_name, String sub1_name, double core1, double core2, double sub1) {
        this.core1_name = core1_name;
        this.core2_name = core2_name;
        this.sub1_name = sub1_name;
        this.core1 = core1;
        this.core2 = core2;
        this.sub1 = sub1;
    }

    public void setSheldData(String core1_name, String sub1_name, String sub2_name, double core1, double sub1, double sub2) {
        this.core1_name = core1_name;
        this.sub1_name = sub1_name;
        this.sub2_name = sub2_name;
        this.core1 = core1;
        this.sub1 = sub1;
        this.sub2 = sub2;
    }

    public String getCore1_name() {
        return core1_name;
    }

    public void setCore1_name(String core1_name) {
        this.core1_name = core1_name;
    }

    public String getCore2_name() {
        return core2_name;
    }

    public void setCore2_name(String core2_name) {
        this.core2_name = core2_name;
    }

    public String getSub1_name() {
        return sub1_name;
    }

    public void setSub1_name(String sub1_name) {
        this.sub1_name = sub1_name;
    }

    public String getSub2_name() {
        return sub2_name;
    }

    public void setSub2_name(String sub2_name) {
        this.sub2_name = sub2_name;
    }

    public double getCore1() {
        return core1;
    }

    public void setCore1(double core1) {
        this.core1 = core1;
    }

    public double getCore2() {
        return core2;
    }

    public void setCore2(double core2) {
        this.core2 = core2;
    }

    public double getSub1() {
        return sub1;
    }

    public void setSub1(double sub1) {
        this.sub1 = sub1;
    }

    public double getSub2() {
        return sub2;
    }

    public void setSub2(double sub2) {
        this.sub2 = sub2;
    }
}
