package com.example.laser.serialport;

import android.os.SystemClock;

import com.example.laser.message.RockerMessage;
import com.example.laser.ui.MainActivity;
import com.vondear.rxtool.RxLogTool;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import static com.example.laser.serialport.DataUtils.toHexString;


/**
 * 读串口线程
 */
public class SerialTargetReadThread extends Thread {

    private static final String TAG = "SerialTargetReadThread";
    private BufferedInputStream mInputStream;
    private String type = "F01";
    public SerialTargetReadThread(InputStream is) {
        mInputStream = new BufferedInputStream(is);
    }

    @Override
    public void run() {
        // 定义一个包的最大长度
        int maxLength = 4096;
        // 所有数据的buffer
        byte[] buffer = new byte[maxLength];

        // 每次收到实际长度
        int available = 0;

        // 当前已经收到包的总长度
        int currentLength = 0;
        // 协议头长度4个字节（开始符1，类型1，长度2）
        int headerLength = 4;

        // 定义帧长度
        int mframeSize = 16;

        RxLogTool.e(TAG+"开始读线程");

        while (!isInterrupted()) {

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {

                available = mInputStream.available();
                if (available > 0) {

                    mInputStream.read(buffer, currentLength, available);
                    currentLength += available;
//                    RxLogTool.e(TAG+"currentLength++++target", currentLength);
                } else {
                    // 暂停一点时间，免得一直循环造成CPU占用率过高
                    SystemClock.sleep(1);
                }
            } catch (IOException e) {
                RxLogTool.e("读取数据失败", e);
            }

            // 如果当前收到包大于头的长度，则解析当前包
            while (currentLength >= mframeSize) {
                int cursor = 0;
                String s;
                while (currentLength >= headerLength) {

                    if (cursor == 0) {
                        s = ByteUtil.bytes2HexStr(buffer, 0, 1);
                    } else {
                        s = ByteUtil.bytes2HexStr(buffer, cursor, 1);
                    }
                    String s1 = ByteUtil.bytes2HexStr(buffer, cursor + 1, 1);
                    String s2 = ByteUtil.bytes2HexStr(buffer, cursor + 2, 1);
                    // 取到头部第一个字节
                    if (!Objects.equals(s, "55") && !Objects.equals(s, "AA")) {
                        --currentLength;
                        ++cursor;
//                        RxLogTool.e("======4=====");
                        continue;
                    } else {
                        if(Objects.equals(s, "AA")){
                            if (!Objects.equals(s1, "55")) {
                                --currentLength;
                                ++cursor;
                                ++cursor;
//                                RxLogTool.e("======5=====");
                                continue;
                            }
                        }
                        else{
                            if (!Objects.equals(s1, "AA")) {
                                --currentLength;
                                ++cursor;
                                ++cursor;
//                                RxLogTool.e("======5=====");
                                continue;
                            }
                        }
                    }
//                    RxLogTool.e("s2", s2);
                    if(s2.equals("E5") || s2.equals("e5")){
                        mframeSize = 18;
                        type = "F01";
                    }

                    if (currentLength < mframeSize) {
                        break;
                    }
//                    // buffer 的长度
                    // 如果内容包的长度大于最大内容长度或者小于等于0，则说明这个包有问题，丢弃
                    if (currentLength > maxLength) {
//                        currentLength = 0;
                        break;
                    }
                    // 如果当前获取到长度小于整个包的长度，则跳出循环等待继续接收数据
                    int factPackLen = mframeSize;
                    if (currentLength < factPackLen) {
                        break;
                    }
//                     一个完整包即产生
                    onDataReceived(buffer, cursor, factPackLen);
//                    RxLogTool.e("发下的数据", ByteUtil.bytes2HexStr(buffer, cursor, factPackLen));
                    currentLength -= factPackLen;
                    cursor += factPackLen;
                }
//                 残留字节移到缓冲区首
                if (currentLength > 0) {
//                        RxLogTool.e("======10=====");
                    byte[] bytes = new byte[maxLength];
                    System.arraycopy(buffer, cursor, bytes, 0, currentLength);
                    Arrays.fill(buffer, (byte) 0);
                    System.arraycopy(bytes, 0, buffer, 0, currentLength);
                }
            }

            //Thread.yield();
        }

        RxLogTool.e("结束读进程");
    }


    private void onDataReceived(final byte[] buffer, final int index, final int packlen) {
        byte[] buf = new byte[packlen];
        System.arraycopy(buffer, index, buf, 0, packlen);
        String s = toHexString(buf, packlen);
//        RxLogTool.e(TAG+"待校验代码", s);
        // 加入校验 先计算校验码 在对比校验
        String crcString = DataUtils.ByteArrToHex(CrcUtilsReverse.CRC16_Ccitt_Reverse(buffer, packlen - 2));
//        String crcString = CrcUtilsReverse.CRC2ByteCusumStr(buffer, packlen - 2);
        String crcCode = ByteUtil.bytes2HexStr(buffer, buf.length - 2, 2);
        // 如果校验正确发送 不正确丢弃 不做处理
        if (crcString.equals(crcCode)) {
            RockerMessage rockerMessage = new RockerMessage(type, s);
//            RxLogTool.e("完整数据+target", s);
            if (MainActivity.Shootting) {
//                RxLogTool.e("接收到初始数据时间1",System.currentTimeMillis());
                EventBus.getDefault().post(rockerMessage);
            }
//            BusUtils.post(MainActivity.F01,s);
        }
    }


    /**
     * 停止读线程
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
