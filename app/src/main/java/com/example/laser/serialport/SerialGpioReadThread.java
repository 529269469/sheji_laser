
package com.example.laser.serialport;

import static com.example.laser.serialport.DataUtils.toHexString;

import android.os.SystemClock;

import com.example.laser.message.RockerMessage;
import com.vondear.rxtool.RxLogTool;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by  on 2021/1/28 17:06.
 */
public class SerialGpioReadThread extends Thread {
    private static final String TAG = "SerialGpioReadThread";
    private BufferedInputStream mInputStream;
    private String type = "G01";

    public SerialGpioReadThread(InputStream is) {
        mInputStream = new BufferedInputStream(is);
    }

    @Override
    public void run() {
        // 定义一个包的最大长度
        int maxLength = 200;
        // 所有数据的buffer
        byte[] buffer = new byte[maxLength];

        // 每次收到实际长度
        int available = 0;

        // 当前已经收到包的总长度
        int currentLength = 0;
        // 协议头长度4个字节（开始符1，类型1，长度2）
        int headerLength = 2;

        // 定义帧长度
        int mframeSize = 12;

        RxLogTool.e(TAG,"开始读线程");

        while (!isInterrupted()) {

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {

                available = mInputStream.available();
                if (available > 0) {

                    mInputStream.read(buffer, currentLength, available);
                    currentLength += available;
//                    RxLogTool.e("currentLength++++gun", currentLength);
                    if (currentLength >= mframeSize) {
                        onDataReceived(buffer, 0, currentLength);
                        currentLength = 0;
                    }
                } else {
                    // 暂停一点时间，免得一直循环造成CPU占用率过高
                    SystemClock.sleep(1);
                }
            } catch (IOException e) {
                RxLogTool.e(TAG,"读取数据失败", e);
            }
            //Thread.yield();
        }

        RxLogTool.e(TAG,"结束读进程");
    }


    private void onDataReceived(final byte[] buffer, final int index, final int packlen) {
        byte[] buf = new byte[packlen];
        System.arraycopy(buffer, index, buf, 0, packlen);
        String s = toHexString(buf, packlen).toUpperCase();
        String substring = s.substring(0, 2);
        String substring1 = s.substring(2,24);

        RxLogTool.e(TAG,substring1);
        if (substring.equals("C1")) {
            RockerMessage rockerMessage = new RockerMessage(type, s.substring(2));
            EventBus.getDefault().post(rockerMessage);
        }
    }


    /**
     * 停止读线程1
     */
    public void close() {

        try {
            mInputStream.close();
        } catch (IOException e) {
            RxLogTool.e("异常", e);
        } finally {
            super.interrupt();
        }
    }
}
