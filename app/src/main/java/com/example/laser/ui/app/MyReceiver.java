package com.example.laser.ui.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vondear.rxtool.RxLogTool;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SHUTDOWN.equals(action)) {
            RxLogTool.d("Android操作系统关机了.......");
        }
    }
}