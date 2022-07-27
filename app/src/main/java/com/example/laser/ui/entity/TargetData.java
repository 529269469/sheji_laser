package com.example.laser.ui.entity;


import java.util.ArrayList;

/**
 * Created by  on 2021/5/30.
 * 打靶成绩 实体类
 * 打靶人员一次的所有数据
 */

public class TargetData {
    private String name; // 打靶人员名称
    private int roundId; // 局id
    private float allRing; // 总环数
    private ArrayList<GradesData> gradesData;

    public TargetData() {
    }

    public TargetData(String name, int roundId, float allRing, ArrayList<GradesData> gradesData) {
        this.name = name;
        this.roundId = roundId;
        this.allRing = allRing;
        this.gradesData = gradesData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public float getAllRing() {
        return allRing;
    }

    public void setAllRing(float allRing) {
        this.allRing = allRing;
    }

    public ArrayList<GradesData> getGradesData() {
        return gradesData;
    }

    public void setGradesData(ArrayList<GradesData> gradesData) {
        this.gradesData = gradesData;
    }
}
