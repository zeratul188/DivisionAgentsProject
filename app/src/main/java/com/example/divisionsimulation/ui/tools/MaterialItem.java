package com.example.divisionsimulation.ui.tools;

public class MaterialItem {
    private String name;
    private int count, max;

    public MaterialItem(String name, int count, int max) {
        this.name = name;
        this.count = count;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
