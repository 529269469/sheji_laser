package com.example.laser.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.laser.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by  on 2021/9/23.
 */

public class DemoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    private int a = 0;

    public DemoAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {
        a++;
        baseViewHolder.setText(R.id.main_demo_item, s + "," + a);
    }
}
