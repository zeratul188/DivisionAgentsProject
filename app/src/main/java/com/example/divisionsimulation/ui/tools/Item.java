package com.example.divisionsimulation.ui.tools;

public class Item implements Comparable<Item> {
    private long rowId;
    private String name, type, core1, core2, sub1, sub2, talent;
    private double core1_value, core2_value, sub1_value, sub2_value;
    private boolean edit1, edit2, edit3, talentedit;
    private int favorite;

    public Item(long rowId, String name, String type) {
        this.rowId = rowId;
        this.name = name;
        this.type = type;
    }

    public boolean isEditOR() {
        if (edit1) return true;
        if (edit2) return true;
        if (edit3) return true;
        if (talentedit) return true;
        return false;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public boolean isTalentedit() {
        return talentedit;
    }

    public void setTalentedit(boolean talentedit) {
        this.talentedit = talentedit;
    }

    public boolean isEdit1() {
        return edit1;
    }

    public void setEdit1(boolean edit1) {
        this.edit1 = edit1;
    }

    public boolean isEdit2() {
        return edit2;
    }

    public void setEdit2(boolean edit2) {
        this.edit2 = edit2;
    }

    public boolean isEdit3() {
        return edit3;
    }

    public void setEdit3(boolean edit3) {
        this.edit3 = edit3;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
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

    public String getCore1() {
        return core1;
    }

    public void setCore1(String core1) {
        this.core1 = core1;
    }

    public String getCore2() {
        return core2;
    }

    public void setCore2(String core2) {
        this.core2 = core2;
    }

    public String getSub1() {
        return sub1;
    }

    public void setSub1(String sub1) {
        this.sub1 = sub1;
    }

    public String getSub2() {
        return sub2;
    }

    public void setSub2(String sub2) {
        this.sub2 = sub2;
    }

    public String getTalent() {
        return talent;
    }

    public void setTalent(String talent) {
        this.talent = talent;
    }

    public double getCore1_value() {
        return core1_value;
    }

    public void setCore1_value(double core1_value) {
        this.core1_value = core1_value;
    }

    public double getCore2_value() {
        return core2_value;
    }

    public void setCore2_value(double core2_value) {
        this.core2_value = core2_value;
    }

    public double getSub1_value() {
        return sub1_value;
    }

    public void setSub1_value(double sub1_value) {
        this.sub1_value = sub1_value;
    }

    public double getSub2_value() {
        return sub2_value;
    }

    public void setSub2_value(double sub2_value) {
        this.sub2_value = sub2_value;
    }

    @Override
    public int compareTo(Item item) {
        return this.name.compareTo(item.getName());
    }
}
