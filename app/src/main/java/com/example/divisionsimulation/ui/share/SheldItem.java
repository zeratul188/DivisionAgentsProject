package com.example.divisionsimulation.ui.share;

public class SheldItem {
    private String name, type, asp, vest, backpack;

    public SheldItem(String name, String type, String asp, String vest, String backpack) {
        this.name = name;
        this.type = type;
        this.asp = asp;
        this.vest = vest;
        this.backpack = backpack;
    }

    public String getVest() {
        return vest;
    }

    public void setVest(String vest) {
        this.vest = vest;
    }

    public String getBackpack() {
        return backpack;
    }

    public void setBackpack(String backpack) {
        this.backpack = backpack;
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

    public String getAsp() {
        return asp;
    }

    public void setAsp(String asp) {
        this.asp = asp;
    }
}
