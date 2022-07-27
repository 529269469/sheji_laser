package com.example.laser.serialport;

import com.blankj.utilcode.util.BusUtils;
import com.example.laser.ui.MainActivity;

/**
 * Created by  on 2022/1/11.
 */

public class DemoThread extends Thread {

    @Override
    public void run() {
        super.run();
        BusUtils.post(MainActivity.Demo,"123453");
    }
}
