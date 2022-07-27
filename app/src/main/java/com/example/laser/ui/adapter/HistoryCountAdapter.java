package com.example.laser.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.laser.R;
import com.example.laser.database.Aim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by  on 2021/6/19.
 * 打靶次数
 */

public class HistoryCountAdapter extends BaseQuickAdapter<Aim, BaseViewHolder> {

    private int SelectIndex = -1;
    private int MaxDataIndex = 10;

    public HistoryCountAdapter(int layoutResId, @Nullable List<Aim> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Aim s) {
        baseViewHolder.setText(R.id.history_list_item_number, String.valueOf(s.getAim_ShotNum()));

        if(SelectIndex == baseViewHolder.getAdapterPosition()){
            baseViewHolder.setBackgroundResource(R.id.history_list_item_number, R.drawable.data);
        }
        else{
            baseViewHolder.setBackgroundResource(R.id.history_list_item_number, R.drawable.charts);
        }

    }


    public int getSelectIndex() {
        return SelectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        SelectIndex = selectIndex;
        notifyDataSetChanged();
    }

}
