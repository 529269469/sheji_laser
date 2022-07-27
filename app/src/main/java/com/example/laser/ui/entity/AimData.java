package com.example.laser.ui.entity;

/**
 * Created by  on 2021/5/31.
 *  每打一次 记录当前所有坐标
 */

public class AimData {
    private double x;
    private double y;

    public AimData() {
    }

    public AimData(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
