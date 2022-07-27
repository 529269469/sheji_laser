package com.example.laser.ui.data;

import java.util.Date;

/**
 * Created by  on 2021/6/30 17:35.
 */
public class AimCalcData {
    private int x;
    private int y;
    private int Width;
    private int Height;
    private double ringNumber;
    private int direction;
    private Date time;
    private double aim_x;
    private double aim_y;
    private double XringNumber;
    private double YringNumber;
    private double XShit;
    private double YShit;
    private double Shift_XY;
    private double CenDis;
    private int HoldingGunt;
    private int AimFractions;
    private int ShotFractions;
    private int AchieveFractions;
    private int TotalFractions;

    public AimCalcData(){

    }


    public AimCalcData(Date Time, int X, int Y, double RingNumber, double xringNumber,
                       double yringNumber,int Direction, int width, int height,
                       double aimX, double aimY, double xShit, double yShit, double shift_XY, double cenDis){
        x = X;
        y = Y;
        ringNumber = RingNumber;
        XringNumber = xringNumber;
        YringNumber = yringNumber;
        direction = Direction;
        time = Time;
        Width = width;
        Height = height;
        aim_x = aimX;
        aim_y = aimY;
        XShit = xShit;
        YShit = yShit;
        Shift_XY = shift_XY;
        CenDis = cenDis;
        HoldingGunt = -1;
        AimFractions = -1;
        ShotFractions = -1;
        AchieveFractions = -1;
        TotalFractions = -1;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getRingNumber() {
        return ringNumber;
    }

    public void setRingNumber(double ringNumber) {
        this.ringNumber = ringNumber;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        this.Height = height;
    }

    public double getAim_x() {
        return aim_x;
    }

    public void setAim_x(double aim_x) {
        this.aim_x = aim_x;
    }

    public double getAim_y() {
        return aim_y;
    }

    public void setAim_y(double aim_y) {
        this.aim_y = aim_y;
    }

    public double getXringNumber() {
        return XringNumber;
    }

    public void setXringNumber(double xringNumber) {
        XringNumber = xringNumber;
    }

    public double getYringNumber() {
        return YringNumber;
    }

    public void setYringNumber(double yringNumber) {
        YringNumber = yringNumber;
    }

    public double getXShit() {
        return XShit;
    }

    public double getYShit() {
        return YShit;
    }

    public double getShift_XY() {
        return Shift_XY;
    }

    public double getCenDis() {
        return CenDis;
    }

    public int getHoldingGunt() {
        return HoldingGunt;
    }

    public void setHoldingGunt(int holdingGunt) {
        HoldingGunt = holdingGunt;
    }

    public int getAimFractions() {
        return AimFractions;
    }

    public void setAimFractions(int aimFractions) {
        AimFractions = aimFractions;
    }

    public int getShotFractions() {
        return ShotFractions;
    }

    public void setShotFractions(int shotFractions) {
        ShotFractions = shotFractions;
    }

    public int getAchieveFractions() {
        return AchieveFractions;
    }

    public void setAchieveFractions(int achieveFractions) {
        AchieveFractions = achieveFractions;
    }

    public int getTotalFractions() {
        return TotalFractions;
    }

    public void setTotalFractions(int totalFractions) {
        TotalFractions = totalFractions;
    }
}
