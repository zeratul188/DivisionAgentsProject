package com.example.divisionsimulation.ui.share;

public class OptionItem {
    private String content, reter, option;
    private Double value;

    public OptionItem(String content, Double value, String option, String reter) {
        this.content = content;
        this.value = value;
        this.option = option;
        this.reter = reter;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReter() {
        return reter;
    }

    public void setReter(String reter) {
        this.reter = reter;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
