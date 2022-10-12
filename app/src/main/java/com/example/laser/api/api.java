package com.example.laser.api;

public class api {

    public static String channel1 = "/dev/ttyS1"; //   /dev/ttyS1  对应枪
    public static String channel2 = "/dev/ttyS2"; //   /dev/ttyS2  对应靶

    public static String baudrate115200 = "115200";
    public static String baudrate9600 = "9600";

    public static int k8 = 230;   //K8  对应靶
    public static int k7 = 229;  //K7  对应枪

    // 上传灯号为0的间隔时间
    public static int mTime = 500;

    public static String ip = "ip";  // 后端ip

    public static String port = "port";  // 端口号

    public static String number = "number";  //枪号

    public static String channel = "channel"; //组号

    public static int detect = 3000;

}
