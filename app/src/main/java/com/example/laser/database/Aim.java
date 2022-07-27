package com.example.laser.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by  on 2021/5/31.
 *  每打一次 记录当前所有坐标
 */
@Entity
public class Aim {
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "TargetId")
    private Long TargetId;  //对应Target表id

    @Property(nameInDb = "aimCount")
    private int aimCount;   //轨迹序号（从1开始）

    @Property(nameInDb = "aimTime")
    private String aimTime; //轨迹瞄准时间

    @Property(nameInDb = "aim_X")
    private double aim_X;   //X坐标

    @Property(nameInDb = "aim_Y")
    private double aim_Y;   //Y坐标

    @Property(nameInDb = "aim_RingNumber")
    private double aim_RingNumber;   //瞄准/击中环数

    @Property(nameInDb = "X_RingNumber")
    private double X_RingNumber;

    @Property(nameInDb = "Y_RingNumber")
    private double Y_RingNumber;

    @Property(nameInDb = "aim_Direction")
    private int aim_Direction;      //瞄准/击中方位

    @Property(nameInDb = "aim_ShotNum")
    private int aim_ShotNum;        //击中序号,仅瞄准未击发(-1)，击发(1~10)

    @Property(nameInDb = "HoldingGunt")
    private int HoldingGunt;

    @Property(nameInDb = "AimFractions")
    private int AimFractions;

    @Property(nameInDb = "ShotFractions")
    private int ShotFractions;

    @Property(nameInDb = "AchieveFractions")
    private int AchieveFractions;

    @Property(nameInDb = "TotalFractions")
    private int TotalFractions;

    @Generated(hash = 2077774140)
    public Aim(Long id, Long TargetId, int aimCount, String aimTime, double aim_X,
            double aim_Y, double aim_RingNumber, double X_RingNumber,
            double Y_RingNumber, int aim_Direction, int aim_ShotNum,
            int HoldingGunt, int AimFractions, int ShotFractions,
            int AchieveFractions, int TotalFractions) {
        this.id = id;
        this.TargetId = TargetId;
        this.aimCount = aimCount;
        this.aimTime = aimTime;
        this.aim_X = aim_X;
        this.aim_Y = aim_Y;
        this.aim_RingNumber = aim_RingNumber;
        this.X_RingNumber = X_RingNumber;
        this.Y_RingNumber = Y_RingNumber;
        this.aim_Direction = aim_Direction;
        this.aim_ShotNum = aim_ShotNum;
        this.HoldingGunt = HoldingGunt;
        this.AimFractions = AimFractions;
        this.ShotFractions = ShotFractions;
        this.AchieveFractions = AchieveFractions;
        this.TotalFractions = TotalFractions;
    }

    @Generated(hash = 1115992614)
    public Aim() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetId() {
        return this.TargetId;
    }

    public void setTargetId(Long TargetId) {
        this.TargetId = TargetId;
    }

    public int getAimCount() {
        return this.aimCount;
    }

    public void setAimCount(int aimCount) {
        this.aimCount = aimCount;
    }

    public String getAimTime() {
        return this.aimTime;
    }

    public void setAimTime(String aimTime) {
        this.aimTime = aimTime;
    }

    public double getAim_X() {
        return this.aim_X;
    }

    public void setAim_X(double aim_X) {
        this.aim_X = aim_X;
    }

    public double getAim_Y() {
        return this.aim_Y;
    }

    public void setAim_Y(double aim_Y) {
        this.aim_Y = aim_Y;
    }

    public double getAim_RingNumber() {
        return this.aim_RingNumber;
    }

    public void setAim_RingNumber(double aim_RingNumber) {
        this.aim_RingNumber = aim_RingNumber;
    }

    public double getX_RingNumber() {
        return this.X_RingNumber;
    }

    public void setX_RingNumber(double X_RingNumber) {
        this.X_RingNumber = X_RingNumber;
    }

    public double getY_RingNumber() {
        return this.Y_RingNumber;
    }

    public void setY_RingNumber(double Y_RingNumber) {
        this.Y_RingNumber = Y_RingNumber;
    }

    public int getAim_Direction() {
        return this.aim_Direction;
    }

    public void setAim_Direction(int aim_Direction) {
        this.aim_Direction = aim_Direction;
    }

    public int getAim_ShotNum() {
        return this.aim_ShotNum;
    }

    public void setAim_ShotNum(int aim_ShotNum) {
        this.aim_ShotNum = aim_ShotNum;
    }

    public int getHoldingGunt() {
        return this.HoldingGunt;
    }

    public void setHoldingGunt(int HoldingGunt) {
        this.HoldingGunt = HoldingGunt;
    }

    public int getAimFractions() {
        return this.AimFractions;
    }

    public void setAimFractions(int AimFractions) {
        this.AimFractions = AimFractions;
    }

    public int getShotFractions() {
        return this.ShotFractions;
    }

    public void setShotFractions(int ShotFractions) {
        this.ShotFractions = ShotFractions;
    }

    public int getAchieveFractions() {
        return this.AchieveFractions;
    }

    public void setAchieveFractions(int AchieveFractions) {
        this.AchieveFractions = AchieveFractions;
    }

    public int getTotalFractions() {
        return this.TotalFractions;
    }

    public void setTotalFractions(int TotalFractions) {
        this.TotalFractions = TotalFractions;
    }

}
