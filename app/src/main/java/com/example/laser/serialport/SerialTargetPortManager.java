package com.example.laser.serialport;

import android.os.HandlerThread;
import android.serialport.SerialPort;

import com.vondear.rxtool.RxLogTool;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/three/28 0028.
 */
public class SerialTargetPortManager {

    private static final String TAG = "SerialTargetPortManager";

    private SerialTargetReadThread mReadThread;
    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;
    private Scheduler mSendScheduler;

    private static class InstanceHolder {

        public static SerialTargetPortManager sManager = new SerialTargetPortManager();
    }

    public static SerialTargetPortManager instance() {
        return InstanceHolder.sManager;
    }

    private SerialPort mSerialPort;

    private SerialTargetPortManager() {
    }

    /**
     * 打开串口
     *
     * @param device
     * @return
     */
    public SerialPort open(Device device) {
        return open(device.getPath(), device.getBaudrate());
    }

    /**
     * 打开串口
     *
     * @param devicePath
     * @param baudrateString
     * @return
     */
    public SerialPort open(String devicePath, String baudrateString) {
        if (mSerialPort != null) {
            close();
        }

        try {
            File device = new File(devicePath);

//            String cmd = "adb shell";
//
//            String s = "/n";
//            try {
//                Process p = Runtime.getRuntime().exec(cmd);
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(p.getInputStream()));
//                String line = null;
//                while ((line = in.readLine()) != null) {
//                    s += line + "/n";
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                Process su = Runtime.getRuntime().exec("su");
//                su.getOutputStream().write(cmd.getBytes());
//                su.getOutputStream().write("chmod 777 /dev/ttyS1".getBytes());
//                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
//                    RxLogTool.e(TAG, "erro");
//                } else {
//                    RxLogTool.e(TAG, "ok");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            int baurate = Integer.parseInt(baudrateString);
            mSerialPort = new SerialPort(device, baurate, 0);

            mReadThread = new SerialTargetReadThread(mSerialPort.getInputStream());
            mReadThread.start();

            mOutputStream = mSerialPort.getOutputStream();

            mWriteThread = new HandlerThread("write-thread");
            mWriteThread.start();
            mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());

            return mSerialPort;
        } catch (Throwable tr) {
            RxLogTool.e(TAG, "打开串口失败", tr);
            close();
            return null;
        }
    }

    /**
     * 关闭串口
     */
    public void close() {
        if (mReadThread != null) {
            mReadThread.close();
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mWriteThread != null) {
            mWriteThread.quit();
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    /**
     * 发送数据
     *
     * @param datas
     * @return
     */
    public void sendData(String datas) {
        try {
            byte[] bytes = datas.getBytes();
            mOutputStream.write(bytes);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData01(byte[] datas) throws IOException {
        mOutputStream.write(datas);
    }

    /**
     * (rx包裹)发送数据
     *
     * @param datas
     * @return
     */
    private Observable<Object> rxSendData(final byte[] datas) {

        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    sendData01(datas);
                    emitter.onNext(new Object());
                } catch (Exception e) {

                    RxLogTool.e("发送：" + ByteUtil.bytes2HexStr(datas) + " 失败", e);

                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                        return;
                    }
                }
                emitter.onComplete();
            }
        });
    }

    /**
     * 发送命令包
     */
    public void sendCommand(final String command) {


//        RxLogTool.i("发送命令：" + command);

        byte[] bytes = ByteUtil.hexStr2bytes(command);
        rxSendData(bytes).subscribeOn(mSendScheduler).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable e) {
                RxLogTool.e("发送失败", e);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
