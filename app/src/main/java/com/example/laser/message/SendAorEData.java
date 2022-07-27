package com.example.laser.message;

import com.example.laser.serialport.CrcUtilsReverse;
import com.example.laser.serialport.DataUtils;

/**
 * Created by  on 2021/5/31.
 * 发送协议 A01 B01 E01
 */

public class SendAorEData {

    /**
     * A01 发送 显控---> 底板 询问帧
     *
     * @param bulletNumber 子弹数量 默认0A
     * @param channel      设置枪信道
     */
    public static String SendA01(String bulletNumber, String channel) {
        String A01 = "AA55E1" + bulletNumber + channel + 55;
        byte[] bytes = CrcUtilsReverse.CRC16_Ccitt_Reverse(DataUtils.HexToByteArr(A01), DataUtils.HexToByteArr(A01).length);
        return A01 + DataUtils.ByteArrToHex(bytes);
    }


    /**
     * A02  发送  ---> 底板参数设置帧
     *
     * @param channel 00:为查询不设置，其他设置对应信道
     * @return 返回A02数据
     */
    public static String SendA02(String channel) {
        String A02 = "AA55E2" + channel;
        byte[] bytes = CrcUtilsReverse.CRC16_Ccitt_Reverse(DataUtils.HexToByteArr(A02), DataUtils.HexToByteArr(A02).length);
        return A02 + DataUtils.ByteArrToHex(bytes);
    }

    /**
     * 字节形式：大端在前；上电开始发送，当收到返回后停止发送，
     * 【当参数配置时收到数据后立即发送，接收到数据对应则停止发送，
     * 自身收到频率、信道对应则停止发送】
     *
     * @param frequency B00:5Hz，B01:10Hz，B10:15Hz，B11:20Hz
     * @param channel   靶信道
     * @return 返回E01数据
     */
    public static String SendE01(String frequency, String channel) {
        String A02 = "AA55E5" + frequency;
        byte[] bytes = CrcUtilsReverse.CRC16_Ccitt_Reverse(DataUtils.HexToByteArr(A02), DataUtils.HexToByteArr(A02).length);
        return A02 + DataUtils.ByteArrToHex(bytes);
    }
}
