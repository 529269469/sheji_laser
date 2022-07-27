package com.example.laser.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by  on 2021/5/4.
 */
@Entity
public class Target {
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "targetDate")
    private String targetDate; // 打靶日期   历史记录有需要根据日期查询

    @Property(nameInDb = "targetDate_Year")
    private String targetDate_Year;     //打靶年份

    @Property(nameInDb = "targetDate_Month")
    private String targetDate_Month;     //打靶月份

    @Property(nameInDb = "targetDate_Day")
    private String targetDate_Day;     //打靶日

    @Property(nameInDb = "targetPerson")
    private String targetPerson; // 打靶人员

    @Property(nameInDb = "TargetType")
    private String TargetType;  //靶型

    @Property(nameInDb = "GunType")
    private int GunType;            //枪型 0:92    1:95

    @Property(nameInDb = "targetAllRing")
    private String targetAllRing; // 打靶总环数

    @Property(nameInDb = "targetBureauId")
    private String targetBureauId; // 打靶局ID

    @Generated(hash = 1321585081)
    public Target(Long id, String targetDate, String targetDate_Year,
            String targetDate_Month, String targetDate_Day, String targetPerson,
            String TargetType, int GunType, String targetAllRing,
            String targetBureauId) {
        this.id = id;
        this.targetDate = targetDate;
        this.targetDate_Year = targetDate_Year;
        this.targetDate_Month = targetDate_Month;
        this.targetDate_Day = targetDate_Day;
        this.targetPerson = targetPerson;
        this.TargetType = TargetType;
        this.GunType = GunType;
        this.targetAllRing = targetAllRing;
        this.targetBureauId = targetBureauId;
    }

    @Generated(hash = 231566653)
    public Target() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetDate() {
        return this.targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getTargetDate_Year() {
        return this.targetDate_Year;
    }

    public void setTargetDate_Year(String targetDate_Year) {
        this.targetDate_Year = targetDate_Year;
    }

    public String getTargetDate_Month() {
        return this.targetDate_Month;
    }

    public void setTargetDate_Month(String targetDate_Month) {
        this.targetDate_Month = targetDate_Month;
    }

    public String getTargetDate_Day() {
        return this.targetDate_Day;
    }

    public void setTargetDate_Day(String targetDate_Day) {
        this.targetDate_Day = targetDate_Day;
    }

    public String getTargetPerson() {
        return this.targetPerson;
    }

    public void setTargetPerson(String targetPerson) {
        this.targetPerson = targetPerson;
    }

    public String getTargetType() {
        return this.TargetType;
    }

    public void setTargetType(String TargetType) {
        this.TargetType = TargetType;
    }

    public int getGunType() {
        return this.GunType;
    }

    public void setGunType(int GunType) {
        this.GunType = GunType;
    }

    public String getTargetAllRing() {
        return this.targetAllRing;
    }

    public void setTargetAllRing(String targetAllRing) {
        this.targetAllRing = targetAllRing;
    }

    public String getTargetBureauId() {
        return this.targetBureauId;
    }

    public void setTargetBureauId(String targetBureauId) {
        this.targetBureauId = targetBureauId;
    }

}
