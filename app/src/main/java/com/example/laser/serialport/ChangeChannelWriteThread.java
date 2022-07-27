package com.example.laser.serialport;

import android.content.Context;

import com.example.laser.R;

/**
 * Created by  on 2021/7/21 17:31.
 */
public class ChangeChannelWriteThread extends Thread{
    private final Object lock = new Object();
    private boolean pause = false;
    private int changechannel;
    private Context context;
    private String[] schannel;

    public ChangeChannelWriteThread(Context context, int changechannel){
        this.context = context;
        this.changechannel = changechannel;
        schannel = context.getResources().getStringArray(R.array.schannel);
    }
    /**
     * 调用这个方法实现暂停线程
     */
    void pauseThread() {
        pause = true;
    }

    /**
     *          * 调用这个方法实现恢复线程的运行
     *          
     */
    void resumeThread() {
        pause = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     *          * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
     *          
     */
    void onPause() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        //GPIO操作即串口配置

    }

}
