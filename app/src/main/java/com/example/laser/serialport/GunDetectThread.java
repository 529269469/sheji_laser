package com.example.laser.serialport;


import android.util.Log;

import com.example.laser.api.api;
import com.example.laser.message.RockerMessage;
import com.vondear.rxtool.RxLogTool;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by  on 2019/9/25.
 */
public class GunDetectThread extends Thread {


    private String TAG = "GunDetectThread";
    // 设置一个输入时间

    public long enterTime = 0L;
    public String type = "GD01";

    private boolean isD = true;

    public void setEnterTime() {
        enterTime = System.currentTimeMillis();
    }

    // 是否掉线
    public void setD() {
        isD = false;
    }



    private static class InstanceHolder {
        public static GunDetectThread sManager = new GunDetectThread();
    }
    public static GunDetectThread instance() {
        return GunDetectThread.InstanceHolder.sManager;
    }

    @Override
    public void run() {
        super.run();
//        RxLogTool.e(TAG, "run");
        // 循环检测枪是否掉线
        while (true) {

//            RxLogTool.e(TAG, "sleep");
            try {
                // 每次发送时间
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (enterTime == 0) {
                continue;
            }
//            RxLogTool.e(TAG, "while");
//            RxLogTool.e(TAG, enterTime);
            // 如果时间大于 设定的指 代表枪掉线
            if ((System.currentTimeMillis() - enterTime) > api.detect && !isD) {
                RockerMessage rockerMessage = new RockerMessage(type, "0");
                EventBus.getDefault().post(rockerMessage);
                isD = true;
                RxLogTool.e(TAG, "枪掉线");

            }

        }
    }
}
