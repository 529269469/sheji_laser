package com.example.laser.database;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ShotNums {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "targetPerson")
    private String targetPerson; // 打靶人员

    @Property(nameInDb = "targetNums")
    private String targetNums; // 打靶总数

    @Generated(hash = 1662977520)
    public ShotNums(Long id, String targetPerson, String targetNums) {
        this.id = id;
        this.targetPerson = targetPerson;
        this.targetNums = targetNums;
    }

    @Generated(hash = 1166123591)
    public ShotNums() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetPerson() {
        return this.targetPerson;
    }

    public void setTargetPerson(String targetPerson) {
        this.targetPerson = targetPerson;
    }

    public String getTargetNums() {
        return this.targetNums;
    }

    public void setTargetNums(String targetNums) {
        this.targetNums = targetNums;
    }

}
