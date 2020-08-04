package com.example.divisionsimulation.ui.tools;

public class EditItem {
    private String name, type;
    private double max;

    public EditItem(String name, String type, double max) {
        this.name = name;
        this.type = type;
        this.max = max;
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
