package com.example.laser.serialport;


import android.content.Context;

import com.example.laser.R;
import com.vondear.rxtool.RxLogTool;
import com.vondear.rxtool.RxSPTool;

/**
 * Created by  on 2019/9/25.
 */
public class MainGunWriteThread extends Thread {
    private final Object lock = new Object();
    private boolean pause = false;
    private Context context;
    private String[] schannel;


    public MainGunWriteThread(Context context){
        this.context = context;
        schannel = context.getResources().getStringArray(R.array.schannel);
    }

    /**
     * 调用这个方法实现暂停线程
     */
    public void pauseThread() {
        pause = true;
    }

    /**
     *          * 调用这个方法实现恢复线程的运行
     *          
     */
    public void resumeThread() {
        pause = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     *          * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
     *          
     */
    public void onPause() {
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
        //打开串口
        // 发送A01;
        while (true) {
            while (pause) {
                onPause();
            }
            try {
                // 每次发送时间
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            int NumberOfBulletsint = RxSPTool.getInt(context,"NumberOfBullets");
            String NumberOfBullets = ByteUtil.decimal2fitHex(NumberOfBulletsint);
            int Channelint = RxSPTool.getInt(context, "Channel");
            String Channel = ByteUtil.decimal2fitHex(Channelint);
            String A07 = "AA55E1" + NumberOfBullets + Channel + "55";
            byte[] bytes = DataUtils.HexToByteArr(A07);
            String crcDate = ByteUtil.decimal2fitHex(565 + NumberOfBulletsint + Channelint, 4);
//            String crcDate = DataUtils.ByteArrToHex(CrcUtilsReverse.CRC16_Ccitt_Reverse(bytes, bytes.length));
            SerialGunPortManager.instance().sendCommand( "FFFF" + schannel[Channelint-1] + A07 + crcDate);
//            SerialGunPortManager.instance().sendCommand( "0004" + "1E" + A07 + crcDate);
            RxLogTool.e("Send", "0004" + "1E" + A07 + crcDate);
        }
    }
}
