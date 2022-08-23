package com.example.laser.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by  on 2022/2/17.
 */

public class TcpClient {
    /**
     * single instance TcpClient
     */
    private static TcpClient mSocketClient = null;

    private TcpClient() {
    }

    public static TcpClient getInstance() {
        if (mSocketClient == null) {
            synchronized (TcpClient.class) {
                mSocketClient = new TcpClient();
            }
        }
        return mSocketClient;
    }


    String TAG_log = "Socket";
    private Socket mSocket;

    private OutputStream mOutputStream;
    private BufferedWriter writer;
    private InputStream mInputStream;

    private SocketThread mSocketThread;
    private boolean isStop = false;//thread flag




    /**
     * - 数据按照最长接收，一次性
     */
    private class SocketThread extends Thread {

        private String ip;
        private int port;

        public SocketThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            Log.e(TAG_log, "SocketThread start ");
            super.run();
            try {
                if (mSocket != null) {
                    mSocket.close();
                    mSocket = null;
                }

                InetAddress ipAddress = InetAddress.getByName(ip);
                try {
                    mSocket = new Socket(ip, port);
                    //阻塞停止，表示连接成功
                    Log.e(TAG_log, "连接成功");
                } catch (Exception e) {
                    Log.e(TAG_log, "连接服务器时异常");
                    e.printStackTrace();
                    return;
                }

                if (isConnect()) {
                    Log.e(TAG_log, "SocketThread connect 连接成功");
                    mOutputStream = mSocket.getOutputStream();
                    mInputStream = mSocket.getInputStream();
                    writer = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                    isStop = false;

                    uiHandler.sendEmptyMessage(1);
                }
                /* 此处这样做没什么意义不大，真正的socket未连接还是靠心跳发送，等待服务端回应比较好，一段时间内未回应，则socket未连接成功 */
                else {
                    uiHandler.sendEmptyMessage(-1);
                    Log.e(TAG_log, "SocketThread connect fail");
                    return;
                }

            } catch (IOException e) {
                uiHandler.sendEmptyMessage(-1);
                Log.e(TAG_log, "SocketThread connect io exception = " + e.getMessage());
                e.printStackTrace();
                return;
            }
            Log.e(TAG_log, "SocketThread connect over ");

            //read ...
//            while (isConnect() && !isStop && !isInterrupted()) {
//
//                int size;
//                try {
//                    byte[] buffer = new byte[1024];
//                    if (mInputStream == null) return;
//                    size = mInputStream.read(buffer);//null data -1 , zrd serial rule size default 10
//                    if (size > 0) {
//                        Message msg = new Message();
//                        msg.what = 100;
//                        Bundle bundle = new Bundle();
//                        bundle.putByteArray("data", buffer);
//                        bundle.putInt("size", size);
//                        bundle.putInt("requestCode", requestCode);
//                        msg.setData(bundle);
//                        uiHandler.sendMessage(msg);
//                    }
////                    Log.e(TAG_log, "SocketThread read listening");
//                    //Thread.sleep(100);//log eof
//                } catch (IOException e) {
//                    uiHandler.sendEmptyMessage(-1);
//                    Log.e(TAG_log, "SocketThread read io exception = " + e.getMessage());
//                    e.printStackTrace();
//                    return;
//                }
//            }
        }
    }


    //==============================socket connect============================


    /**
     * 发送消息
     */
    public void sendStrSocket(final String senddata) {
        new Thread(() -> {
            try {
                Log.e(TAG_log, "sendStrSocket:" + senddata);
                writer.write(senddata);//"utf-8"
                writer.flush();
            } catch (Exception e) {
                Log.e(TAG_log, "sendStrSocket:" + e.getMessage());
            }
        }).start();

    }


    /**
     * connect socket in thread
     * Exception : android.os.NetworkOnMainThreadException
     */
    public void connect(String ip, int port) {
        mSocketThread = new SocketThread(ip, port);
        mSocketThread.start();
    }

    /**
     * socket is connect
     */
    public boolean isConnect() {
        boolean flag = false;
        if (mSocket != null) {
            flag = mSocket.isConnected();
        }
        return flag;
    }

    /**
     * socket disconnect
     */
    public void disconnect() {
        isStop = true;
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }

            if (mInputStream != null) {
                mInputStream.close();
            }

            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mSocketThread != null) {
            mSocketThread.interrupt();//not intime destory thread,so need a flag
        }
    }


    /**
     * send byte[] cmd
     * Exception : android.os.NetworkOnMainThreadException
     */
    public void sendByteCmd(final byte[] mBuffer, int requestCode) {
        this.requestCode = requestCode;

        new Thread(() -> {
            try {
                if (mOutputStream != null) {
                    mOutputStream.write(mBuffer);
                    mOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }


    /**
     * send string cmd to serial
     */
    public void sendStrCmds(String cmd, int requestCode) {
        byte[] mBuffer = cmd.getBytes();
        sendByteCmd(mBuffer, requestCode);
    }


    /**
     * send prt content cmd to serial
     */
    public void sendChsPrtCmds(String content, int requestCode) {
        try {
            byte[] mBuffer = content.getBytes("GB2312");
            sendByteCmd(mBuffer, requestCode);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }


    Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //connect error
                case -1:
                    if (null != onDataReceiveListener) {
                        onDataReceiveListener.onConnectFail();
                        disconnect();
                    }
                    break;

                //connect success
                case 1:
                    if (null != onDataReceiveListener) {
                        onDataReceiveListener.onConnectSuccess();
                    }
                    break;

                //receive data
                case 100:
                    Bundle bundle = msg.getData();
                    byte[] buffer = bundle.getByteArray("data");
                    int size = bundle.getInt("size");
                    int mequestCode = bundle.getInt("requestCode");
                    if (null != onDataReceiveListener) {
                        onDataReceiveListener.onDataReceive(buffer, size, mequestCode);
                    }
                    break;
            }
            return false;
        }
    });


    /**
     * socket response data listener
     */
    private OnDataReceiveListener onDataReceiveListener = null;
    private int requestCode = -1;

    public interface OnDataReceiveListener {
        public void onConnectSuccess();

        public void onConnectFail();

        public void onDataReceive(byte[] buffer, int size, int requestCode);
    }

    public void setOnDataReceiveListener(
            OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }



}
