package com.example.laser.ui.entity;

import java.util.ArrayList;

/**
 * Created by  on 2021/5/30.
 *  每一发子弹 的所有数据
 */

public class GradesData {
    private int number; // 打靶序号
    private float currentRing; // 当前环数
    private int direction; //  方向
    private String useTime; // 用时
    private String currentTime; // 当前时间
    private ArrayList<AimData> aimData;

    public GradesData() {
    }

    public GradesData(int number, float currentRing, int direction, String useTime, String currentTime, ArrayList<AimData> aimData) {
        this.number = number;
        this.currentRing = currentRing;
        this.direction = direction;
        this.useTime = useTime;
        this.currentTime = currentTime;
        this.aimData = aimData;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getCurrentRing() {
        return currentRing;
    }

    public void setCurrentRing(float currentRing) {
        this.currentRing = currentRing;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public ArrayList<AimData> getAimData() {
        return aimData;
    }

    public void setAimData(ArrayList<AimData> aimData) {
        this.aimData = aimData;
    }
}
