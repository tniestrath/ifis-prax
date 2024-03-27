package com.analysetool.util;

public class Pair {
    private String text;
    private int count;

    public Pair(String text, int count) {
        this.text = text;
        this.count=count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
