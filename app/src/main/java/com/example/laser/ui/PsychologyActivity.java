package com.example.laser.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.laser.R;
import com.example.laser.databinding.ActivityPsychologyBinding;

/**
 * 心理管理
 */
public class PsychologyActivity extends AppCompatActivity {

    private ActivityPsychologyBinding activityPsychologyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setNavigationBarVisible(this, true);
        super.onCreate(savedInstanceState);
        activityPsychologyBinding = DataBindingUtil.setContentView(this, R.layout.activity_psychology);
        activityPsychologyBinding.setPsychology(this);


    }



    /**
     * 隐藏或显示 导航栏
     *
     * @param activity
     */
    public static void setNavigationBarVisible(Activity activity, boolean isHide) {
        if (isHide) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        } else {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}