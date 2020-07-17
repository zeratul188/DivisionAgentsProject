package com.example.divisionsimulation.ui.share;

public class WeaponItem {
    private String name, type, option;

    public WeaponItem(String name, String type, String option) {
        this.name = name;
        this.type = type;
        this.option = option;
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

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
