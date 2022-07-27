package com.example.laser.ui.adapter;

import android.content.res.Resources;
import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.laser.R;
import com.example.laser.ui.data.AimCalcData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by  on 2021/5/30.
 */

public class MainAdapter extends BaseQuickAdapter<AimCalcData, BaseViewHolder> {

    private int SelectPosition = -1;
    private Date StartDate = new Date();
    private Date LastDate = new Date();
    private int ReportType = 0;

    public Date getLastDate() {
        return LastDate;
    }

    public SimpleDateFormat DateTimeFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    public SimpleDateFormat DateSFormat = new SimpleDateFormat("mm:ss.SSS");

    public MainAdapter(int layoutResId, @Nullable List<AimCalcData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AimCalcData aimCalcData) {
        baseViewHolder.setText(R.id.main_list_item_number, String.valueOf(baseViewHolder.getAdapterPosition() + 1))
                .setText(R.id.main_list_item_currentTime, DateTimeFormat.format(aimCalcData.getTime()));
        if(ReportType == 0){
            baseViewHolder.setText(R.id.main_list_item_currentRing, String.valueOf(aimCalcData.getRingNumber()));
        }
        else{
            if(aimCalcData.getRingNumber() >= 10.0){
                baseViewHolder.setText(R.id.main_list_item_currentRing, "10");
            }
            else if(aimCalcData.getRingNumber() >= 9.0){
                baseViewHolder.setText(R.id.main_list_item_currentRing, "9");
            }
            else if(aimCalcData.getRingNumber() >= 8.0){
                baseViewHolder.setText(R.id.main_list_item_currentRing, "8");
            }
            else if(aimCalcData.getRingNumber() >= 7.0){
                baseViewHolder.setText(R.id.main_list_item_currentRing, "7");
            }
            else if(aimCalcData.getRingNumber() >= 6.0){
                baseViewHolder.setText(R.id.main_list_item_currentRing, "6");
            }
            else if(aimCalcData.getRingNumber() >= 5.0){
                baseViewHolder.setText(R.id.main_list_item_currentRing, "5");
            }
            else if(aimCalcData.getRingNumber() >= 4.0){
                baseViewHolder.setText(R.id.main_list_item_currentRing, "4");
            }
            else{
                baseViewHolder.setText(R.id.main_list_item_currentRing, "0");
            }
        }
        int direction = aimCalcData.getDirection();
        switch (direction){
            case 1 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection1);
                break;
            case 2 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection2);
                break;
            case 3 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection3);
                break;
            case 4 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection4);
                break;
            case 5 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection5);
                break;
            case 6 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection6);
                break;
            case 7 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection7);
                break;
            case 8 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection8);
                break;
            case 9 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection9);
                break;
            case 10 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection10);
                break;
            case 11 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection11);
                break;
            case 12 :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection12);
                break;
            default :
                baseViewHolder.setImageResource(R.id.main_list_item_direction, R.mipmap.shootdirection13);
                break;
        }

        if(baseViewHolder.getAdapterPosition() == 0){
            baseViewHolder.setText(R.id.main_list_item_useTime,
                    DateSFormat.format(new Date(aimCalcData.getTime().getTime() - StartDate.getTime())));
            LastDate = aimCalcData.getTime();
        }
        else {
            baseViewHolder.setText(R.id.main_list_item_useTime,
                    DateSFormat.format(new Date(
                            aimCalcData.getTime().getTime() - LastDate.getTime()
                    )));
            LastDate = aimCalcData.getTime();
        }

        if(baseViewHolder.getAdapterPosition() == SelectPosition){
            baseViewHolder.setBackgroundResource(R.id.main_list_item_main, R.drawable.data);
            baseViewHolder.setImageResource(R.id.main_list_item_replay, R.mipmap.trackplayback);
        }
        else{
            baseViewHolder.setBackgroundColor(R.id.main_list_item_main, Color.TRANSPARENT);
            baseViewHolder.setImageResource(R.id.main_list_item_replay, R.mipmap.trackplaybacknull);
        }
    }

    public void setSelectPosition(int selectPosition) {
        SelectPosition = selectPosition;
    }
    public int getSelectPosition(){
        return SelectPosition;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public int getReportType() {
        return ReportType;
    }

    public void setReportType(String selectreportType, Resources resources) {
        String[] ReportTypeArray = resources.getStringArray(R.array.TargetRecordTpye);
        for (int i = 0; i < ReportTypeArray.length; i++) {
            if(ReportTypeArray[i].equals(selectreportType)){
                ReportType = i;
                notifyDataSetChanged();
            }
        }
    }

}
