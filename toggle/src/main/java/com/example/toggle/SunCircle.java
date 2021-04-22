package com.example.toggle;

public class SunCircle {
    private float x;
    private float y;
    private float r;
    private int alpha;
    private Long createdTime;
    private boolean isFirstTime = true;


    public SunCircle(float x, float y, float r, int alpha,  Long createdTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.alpha = alpha;
        this.createdTime = createdTime;
    }


    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setR(float r) {
        this.r = r;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

    public int getAlpha() {
        return alpha;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

}
