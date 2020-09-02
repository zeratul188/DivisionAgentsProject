package com.example.divisionsimulation.ui.send;

public class MakeItem {
    private String name, type, asp = "-";
    private Boolean gear = false;

    public MakeItem(String name, String type) {
        this.name = name;
        this.type = type;
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

    public Boolean getGear() {
        return gear;
    }

    public void setGear(Boolean gear) {
        this.gear = gear;
    }

    public String getAsp() {
        return asp;
    }

    public void setAsp(String asp) {
        this.asp = asp;
    }
}
