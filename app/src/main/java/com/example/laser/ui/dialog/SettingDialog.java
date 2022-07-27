package com.example.laser.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.laser.R;
import com.example.laser.ui.fragment.SystemFragment;
import com.example.laser.view.NoScrollViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * Created by  on 2021/5/26.
 */

public class SettingDialog extends DialogFragment {
    private ArrayList<Fragment> fragments;
    private String[] titles = {"系统设置"};
    private SystemFragment systemFragment;


    private OnListener mListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NO_FRAME, R.style.BaseDialogStyle);
        View view = inflater.inflate(R.layout.dialog_setting, container, false);
        fragments = new ArrayList<>();
        TabLayout tabLayout = view.findViewById(R.id.dialog_setting_tabLayout);
        NoScrollViewPager viewPager = view.findViewById(R.id.dialog_setting_viewPager);
        systemFragment = new SystemFragment();
        fragments.add(systemFragment);
        systemFragment.setListener(new SystemFragment.OnListener() {
            @Override
            public void SelectTargetMian(SystemFragment systemFragment, int position) {
                mListener.SelectTargetMian(SettingDialog.this, position);
            }

            @Override
            public void SelectTargetRecordType(SystemFragment systemFragment, String SelectedTargetRecordType) {
                mListener.SelectTargetRecordType(SettingDialog.this, SelectedTargetRecordType);
            }

            @Override
            public void SelectShotNum(SystemFragment systemFragment, int shotnums) {
                mListener.SelectShotNum(SettingDialog.this, shotnums);
            }

            @Override
            public void SelectGunVoice(SystemFragment systemFragment, boolean isTrue) {
                mListener.SelectGunVoice(SettingDialog.this, isTrue);
            }

            @Override
            public void SelectShotVoice(SystemFragment systemFragment, boolean isTrue) {
                mListener.SelectShotVoice(SettingDialog.this, isTrue);
            }

            @Override
            public void SelectAutoPrint(SystemFragment systemFragment, boolean isAuto) {
                mListener.SelectAutoPrint(SettingDialog.this, isAuto);
            }
        });
//        Window window = getDialog().getWindow();
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        layoutParams.alpha = 0.1f;
//        window.setAttributes(layoutParams);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//        tabLayout.setTabTextColors(Color.GRAY, Color.WHITE);
        return view;
    }

    public void setListener(OnListener mListener) {
        this.mListener = mListener;
    }

    public interface OnListener {
        void SelectTargetMian(SettingDialog settingDialog, int position);
        void SelectTargetRecordType(SettingDialog settingDialog, String SelectedTargetRecordType);
        void SelectShotNum(SettingDialog settingDialog, int shotnums);
        void SelectGunVoice(SettingDialog settingDialog, boolean isTrue);
        void SelectShotVoice(SettingDialog settingDialog, boolean isTrue);
        void SelectAutoPrint(SettingDialog settingDialog, boolean isAuto);
    }
}
