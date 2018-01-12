package com.zyl.mp3cutter.skin;

public class ThemeColor {

    int drawableResId;
    String name;
    boolean isChosen = false;

    public ThemeColor(int drawableResId, String name) {
        this.drawableResId = drawableResId;
        this.name = name;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public String getName() {
        return name;
    }

    public ThemeColor setName(String name) {
        this.name = name;
        return this;
    }
}
