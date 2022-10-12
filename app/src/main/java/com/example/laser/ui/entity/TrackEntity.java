package com.example.laser.ui.entity;


/**
 * Created by  on 2022/3/4.
 */

public class TrackEntity {


    private String no;
    private String personName;
    private String aimTime;
    private String ip;
    private double aim_X;
    private double aim_Y;
    private String ringNumber;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getAimTime() {
        return aimTime;
    }

    public void setAimTime(String aimTime) {
        this.aimTime = aimTime;
    }

    public double getAim_X() {
        return aim_X;
    }

    public void setAim_X(double aim_X) {
        this.aim_X = aim_X;
    }

    public double getAim_Y() {
        return aim_Y;
    }

    public void setAim_Y(double aim_Y) {
        this.aim_Y = aim_Y;
    }

    public String getRingNumber() {
        return ringNumber;
    }

    public void setRingNumber(String ringNumber) {
        this.ringNumber = ringNumber;
    }
}
