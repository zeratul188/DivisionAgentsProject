package com.example.divisionsimulation.ui.share;

public class NamedItem {
    private String name, talent, type, brand, asp;
    private boolean noTalent = false;

    public NamedItem(String name, String talent, String type, String brand, String asp) {
        this.name = name;
        this.talent = talent;
        this.type = type;
        this.brand = brand;
        this.asp = asp;
    }

    public String getAsp() {
        return asp;
    }

    public void setAsp(String asp) {
        this.asp = asp;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setNoTalent(int index) {
        if (index == 1) noTalent = true;
        else noTalent = false;
    }

    public boolean getNoTalent() {
        return noTalent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTalent() {
        return talent;
    }

    public void setTalent(String talent) {
        this.talent = talent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
