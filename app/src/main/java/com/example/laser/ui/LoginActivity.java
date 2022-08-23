package com.example.laser.ui;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laser.R;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.RxLogTool;
import com.vondear.rxtool.RxPermissionsTool;
import com.vondear.rxtool.RxTool;

import java.util.List;

/**
 * Created by  on 2021/8/23.
 */

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setNavigationBarVisible(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RxTool.delayToDo(2000, () -> RxActivityTool.skipActivityAndFinish(LoginActivity.this,MainActivity.class));
//        PermissionX.init(this)
//                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                 )
//                .request((allGranted, grantedList, deniedList) -> {
//                    if (allGranted) {
////                        RxTool.delayToDo(2000, () -> RxActivityTool.skipActivityAndFinish(LoginActivity.this, FaceLoginActivity.class));
//                         RxTool.delayToDo(2000, () -> RxActivityTool.skipActivityAndFinish(LoginActivity.this,MainActivity.class));
//
//                    } else {
//
//                    }
//                });

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
