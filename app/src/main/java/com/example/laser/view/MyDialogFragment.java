package com.example.laser.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ToastUtils;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/05/18
 *    desc   : 项目中的 Dialog 基类
 */
public final class MyDialogFragment {

    public static class Builder<B extends MyDialogFragment.Builder>
            extends BaseDialogFragment.Builder<B> {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public B setContentView(@NonNull View view) {
            return super.setContentView(view);
        }

        /**
         * 显示吐司
         */
        public void toast(CharSequence text) {
            ToastUtils.showShort(text);
        }

        public void toast(@StringRes int id) {
            ToastUtils.showShort(id);
        }

        public void toast(Object object) {
            ToastUtils.showShort(String.valueOf(object));
        }
    }
}