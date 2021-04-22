package com.example.toggle;

public class Circle {
    private float x;
    private float y;
    private float r;
    private int alpha;
    private float theta;
    private Long createdTime;
    private Long lifeTime;


    public Circle(float x, float y, float r, int alpha, float theta, Long createdTime, Long lifeTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.alpha = alpha;
        this.theta = theta;
        this.createdTime = createdTime;
        this.lifeTime = lifeTime;
    }

    public float getTheta() {
        return theta;
    }

    public void setTheta(float theta) {
        this.theta = theta;
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

    public Long getLifeTime() {
        return lifeTime;
    }
}
