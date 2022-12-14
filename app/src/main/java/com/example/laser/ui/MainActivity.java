package com.example.laser.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.laser.R;
import com.example.laser.api.api;
import com.example.laser.database.TargetDao;
import com.example.laser.database.dao.DaoManager;
import com.example.laser.databinding.ActivityMainBinding;
import com.example.laser.message.RockerMessage;
import com.example.laser.serialport.ChangeChannelWriteThread;
import com.example.laser.serialport.CrcUtilsReverse;
import com.example.laser.serialport.DataUtils;
import com.example.laser.serialport.GunDetectThread;
import com.example.laser.serialport.MainGunWriteThread;
import com.example.laser.serialport.SerialGpioK7Manager;
import com.example.laser.serialport.SerialGpioManager;
import com.example.laser.serialport.SerialGunPortManager;
import com.example.laser.serialport.SerialTargetPortManager;
import com.example.laser.ui.adapter.DemoAdapter;
import com.example.laser.ui.adapter.MainAdapter;
import com.example.laser.ui.data.AimCalcData;
import com.example.laser.ui.dialog.SettingDialog;
import com.example.laser.ui.entity.InfoEntity;
import com.example.laser.ui.entity.ServerReceiveEntity;
import com.example.laser.ui.entity.ShotEntity;
import com.example.laser.ui.entity.TrackEntity;
import com.example.laser.utils.PrintAchievement;
import com.example.laser.utils.SpeechUtils;
import com.example.laser.utils.TcpClient;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lztek.toolkit.Lztek;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.RxLogTool;
import com.vondear.rxtool.RxSPTool;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding activityMainBinding;

    private SettingDialog settingDialog;

    private int testIndex = 1;
    private int testmode = 0;
    private Handler handler;
    //    private SimpleDateFormat DateTimeFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
    private Date TaskStartDateTime;
    private MainAdapter targetAdapter;

    private boolean isTestline = true;
    private boolean isShottingFirst = false;
    private LineData lineData;
    private LineDataSet lineDataSet;
    public static boolean Shootting = false;
    private boolean isPlayBacking = false;
    private boolean ChannelChangeEnable = false;
    private int channel = 1;
    private int changechannel = 1;
    private boolean GunVoiceEnable;
    private float volume95 = 0.3f;
    private String[] TargetType;

    private int ReportType = 0;


    private TargetDao targetDao;
    private MainGunWriteThread mainGunWriteThread;
    private String printPersonName;
    private String printBureau;

    private SoundPool sp;                   // ??????SoundPool?????????
    private HashMap<Integer, Integer> hm;   // ????????????HashMap?????????????????????
    private String[] TargetMian;

    private boolean isAutoPrint = false;
    private boolean isShotVoice = true;

    private PrintAchievement printAchievement;

    private ArrayList<String> strings = new ArrayList<>();
    private DemoAdapter demoAdapter;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat DateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static final String D01 = "D01";
    public static final String F01 = "F01";
    public static final String Demo = "Demo";
    private TcpClient tcpClient; // ???????????????
    private String ipAddress;

    private String curMode = "0"; // ???????????????????????? 1 ?????? 2 ??????
    boolean isSame = false; // ??????????????????
    private String reMode = "0";

    private boolean isRevice = false;
    private BatteryReceiver mBatteryReceiver;

    public static boolean isGpioK7Read = true;  // k7 ???????????????????????????????????? ???????????????false
    public static boolean isGpioK8Read = true;  // k8 ???????????????????????????????????? ???????????????false
    private String sendGpioData;
    private Lztek lztek;

    private String numberNo;

    private int freeBullet = 5;
    private GunDetectThread gunDetectThread;
    private AlertDialog alertDialog;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setNavigationBarVisible(this, true);
        super.onCreate(savedInstanceState);
//        BusUtils.register(this);

//        GpioTest gpioTest = new GpioTest(this);
//        int gpioTestNum = gpioTest.gpioGetNumber();
//        RxLogTool.e("gpioTestNum:"+gpioTestNum);

        initDao();
        RxActivityTool.addActivity(this);
        printAchievement = new PrintAchievement(getResources(), this);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setMainModel(this);
        activityMainBinding.mainEditPersonName.clearFocus();
//        settingDialog = new SettingDialog();
        TargetType = getResources().getStringArray(R.array.img_x);
        initSoundPool();
        initVoice();
        //TODO ?????????????????????????????????????????????
        initSerial();


        getInfo();
        channel = RxSPTool.getInt(this, "Channel");
        GunVoiceEnable = RxSPTool.getBoolean(this, "GunVoice");
        numberNo = RxSPTool.getString(this, api.number);

        TaskStartDateTime = new Date();
        int[] value = activityMainBinding.mainImage.setPersonName(activityMainBinding.mainEditPersonName.getText().toString());
        activityMainBinding.mainTextJuId.setText(String.valueOf(value[0]));
        SpanUtils.with(activityMainBinding.mainTextAllNum).append("????????? ").setFontSize(14, true)
                .append(String.valueOf(value[1])).setFontSize(20, true).setForegroundColor(getColor(R.color.textColor)).create();
//        activityMainBinding.mainTextAllNum.setText(String.valueOf(value[1]));
//        initSerial();
        initRecycler();// ?????????recycler
        initLineChart();
        initView();
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getWindow().getDecorView().getHeight() == 0) {
                    return;
                }
//                if (!Shootting) {
                setNavigationBarVisible(MainActivity.this, true);
//                RxLogTool.e("setNavigationBarVisible");
//                }
//                RxLogTool.e("?????????");
            }
        });


        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
//                    case 1 :{
//                        if(isShottingFirst){
//                            targetAdapter.setStartDate(new Date());
//                            RefushViewData();
//                            activityMainBinding.mainImage.ClearData();
//                            activityMainBinding.mainImage.setStartDate(targetAdapter.getStartDate());
//                            targetAdapter.setSelectPosition(-1);
//                            targetAdapter.notifyDataSetChanged();
//                            RefushLineChart();
//                            isShottingFirst = false;
//                            Shootting = true;
//                            activityMainBinding.mainEditPersonName.setEnabled(false);
//                        }
//                        if(isTestline){
//                            testDrawLine(testIndex, testmode);
////                            testDrawLine1(testIndex, testmode);
//                            if(testIndex < 100){
//                                if(testmode < 3){
//                                    testmode++;
//                                }
//                                else{
//                                    testmode = 0;
//                                    testIndex++;
//                                }
//                                handler.sendEmptyMessageDelayed(1, 100);
//                            }
//                            else{
//                                testIndex = 1;
//                                testmode = 0;
//                                isTestline = true;
//                                Shootting = false;
//                                activityMainBinding.mainEditPersonName.setEnabled(true);
//                                isShottingFirst = true;
////                                activityMainBinding.mainImage.SaveData();
////                                handler.sendEmptyMessageDelayed(1, 20000);
//                            }
//                        }
//                        break;
//                    }
                    case 2: {
                        if (!activityMainBinding.mainImage.RunPlayBack()) {
                            RefushLineChart();
                            handler.sendEmptyMessageDelayed(2, 100);
                        } else {
                            isPlayBacking = false;
                        }
                        break;
                    }
                    case 6: {
                        Shootting = false;
//                        printPersonName = activityMainBinding.mainEditPersonName.getText().toString();
//                        printBureau = activityMainBinding.mainTextJuId.getText().toString();
//                        activityMainBinding.mainEditPersonName.setEnabled(true);
//                        activityMainBinding.mainImage.SaveData();
//                        if (isAutoPrint) {
//                            printAchievement.PrintImg(printPersonName, printBureau);
//                        }
                        break;
                    }
                    case 7: {
                        RxLogTool.e("ChangeChannel");
//                        ChangeChannel();
//                        RxSPTool.putInt(MainActivity.this,"Channel", changechannel);
//                        channel = changechannel;
//                        ChannelChangeEnable = false;
                        break;
                    }

                    case 8: {
                        int[] value = activityMainBinding.mainImage.setPersonName(activityMainBinding.mainEditPersonName.getText().toString());
                        activityMainBinding.mainTextJuId.setText(String.valueOf(value[0]));
                        SpanUtils.with(activityMainBinding.mainTextAllNum).append("????????? ").setFontSize(14, true)
                                .append(String.valueOf(value[1])).setFontSize(20, true).setForegroundColor(getColor(R.color.textColor)).create();
                        targetAdapter.setStartDate(new Date());
                        RefushViewData();
                        activityMainBinding.mainImage.ClearData();
                        activityMainBinding.mainImage.setStartDate(targetAdapter.getStartDate());
                        targetAdapter.setSelectPosition(-1);
                        targetAdapter.notifyDataSetChanged();
                        RefushLineChart();
                        Shootting = true;
                        CurrentShotIndex = 0;
                        activityMainBinding.mainEditPersonName.setEnabled(false);
//                        activityMainBinding.mainImage.setShot(false);
                        break;
                    }

                    case 10: {
                        SpeechUtils.SpeakVoice(msg.getData().getString("shotvoice"));
                        break;
                    }
                    case 50:
                        activityMainBinding.mainSocket.setText("?????????");
                        break;

                    case 51:
                        if (tcpClient != null && tcpClient.isConnect()) {
                            ipAddress = NetworkUtils.getIpAddressByWifi();
                            AimCalcData trackValue = activityMainBinding.mainImage.getTrackValue();
                            TrackEntity trackEntity = new TrackEntity();
                            trackEntity.setNo(numberNo);
                            trackEntity.setAim_X(trackValue.getAim_x());
                            trackEntity.setAim_Y(trackValue.getAim_y());
                            trackEntity.setIp(ipAddress);
                            trackEntity.setAimTime(DateTimeFormat.format(trackValue.getTime()));
                            trackEntity.setPersonName(activityMainBinding.mainEditPersonName.getText().toString());
                            trackEntity.setRingNumber(String.valueOf(trackValue.getRingNumber()));
                            try {
                                //TODO Socket??????
                                RxLogTool.e("trackEntity", GsonUtils.toJson(trackEntity));
                                tcpClient.sendStrSocket(GsonUtils.toJson(trackEntity));
//                                tcpClient.sendByteCmd(GsonUtils.toJson(trackEntity).getBytes("UTF-8"), 2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.sendEmptyMessage(50);
                        }
                        break;

                    case 52:
//                        if (tcpClient != null && tcpClient.isConnect()) {
                        ArrayList<AimCalcData> shootDates = activityMainBinding.mainImage.getShootDates();
                        AimCalcData aimCalcData = activityMainBinding.mainImage.getShootDates().get(shootDates.size() - 1);
                        ShotEntity shotEntity = new ShotEntity();
                        shotEntity.setNo(numberNo);
                        shotEntity.setPersonName(activityMainBinding.mainEditPersonName.getText().toString());
                        shotEntity.setAchieveFractions(String.valueOf(aimCalcData.getAchieveFractions()));

                        shotEntity.setAim_date(DateTimeFormat.format(aimCalcData.getTime()));
                        shotEntity.setAim_Direction(String.valueOf(aimCalcData.getDirection()));
                        shotEntity.setAim_RingNumber(String.valueOf(aimCalcData.getRingNumber()));
                        shotEntity.setAim_ShotNum(String.valueOf(shootDates.size() + 1));
                        if (shootDates.size() == 1) {

                            shotEntity.setAim_timecost(targetAdapter.DateSFormat.format(new Date(aimCalcData.getTime().getTime()
                                    - targetAdapter.getStartDate().getTime())));
                        } else {
                            shotEntity.setAim_timecost(targetAdapter.DateSFormat.format(new Date(
                                    aimCalcData.getTime().getTime() - targetAdapter.getLastDate().getTime()
                            )));
                        }
                        shotEntity.setAimFractions(String.valueOf(aimCalcData.getAimFractions()));
                        shotEntity.setHoldingGunt(String.valueOf(aimCalcData.getHoldingGunt()));
                        shotEntity.setShotFractions(String.valueOf(aimCalcData.getShotFractions()));

                        String allNums = String.valueOf((Integer.parseInt(activityMainBinding.mainTextAllNum.getText().toString().
                                replace("????????? ", ""))));

                        String gunType = String.valueOf((Integer.parseInt(activityMainBinding.mainTextGunType.getText().toString().
                                replace("?????? ", ""))));

                        String bulletNum = String.valueOf((Integer.parseInt(activityMainBinding.mainTextSurplusNum.getText().toString().
                                replace("????????? ", ""))));
                        shotEntity.setTargetAllNums(allNums);
                        shotEntity.setTargetAllRing(activityMainBinding.maintextAllJuNum.getText().toString());
                        shotEntity.setTargetCurrentRing(activityMainBinding.mainTextCurrentRingnum.getText().toString());
                        shotEntity.setTotalFractions(String.valueOf(aimCalcData.getTotalFractions()));
                        shotEntity.setX_RingNumber(String.valueOf(aimCalcData.getAim_x()));
                        shotEntity.setY_RingNumber(String.valueOf(aimCalcData.getAim_y()));
                        shotEntity.setBullets(bulletNum);
                        shotEntity.setGunType(gunType);
                        shotEntity.setTargetBureauId(activityMainBinding.mainTextJuId.getText().toString());
                        ipAddress = NetworkUtils.getIpAddressByWifi();
                        shotEntity.setIp(ipAddress);
                        String s = GsonUtils.toJson(shotEntity);
                        RxLogTool.e("sss", s);
                        try {
                            tcpClient.sendStrSocket(s);
//                            tcpClient.sendByteCmd(GsonUtils.toJson(shotEntity).getBytes("UTF-8"), 2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        } else {
//                            handler.sendEmptyMessage(50);
//                        }
                        break;
                }
            }
        };
//        handler.sendEmptyMessageDelayed(1, 100);

        // ??????????????????
        mBatteryReceiver = new BatteryReceiver();
        registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        upLoad();
    }

    /**
     * ??????????????? ??????????????????
     */
    private void upLoad() {
        // ???????????????Ip

        ipAddress = NetworkUtils.getIpAddressByWifi();
        RxLogTool.e("upLoad", ipAddress);
        Thread thread = new Thread(() -> {
            boolean isSend = true;
            while (isSend) {
                if (tcpClient != null && tcpClient.isConnect()) {
                    sendSocketBasic();
                    isSend = false;

                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }


    /**
     * ???????????????
     */
    private void initView() {
        activityMainBinding.mainEditPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int[] value = activityMainBinding.mainImage.setPersonName(activityMainBinding.mainEditPersonName.getText().toString());
                activityMainBinding.mainTextJuId.setText(String.valueOf(value[0]));
                SpanUtils.with(activityMainBinding.mainTextAllNum).append("????????? ").setFontSize(14, true)
                        .append(String.valueOf(value[1])).setFontSize(20, true).setForegroundColor(getColor(R.color.textColor)).create();
                activityMainBinding.mainImage.ClearData();
                activityMainBinding.mainImage.setStartDate(targetAdapter.getStartDate());
                targetAdapter.setSelectPosition(-1);
                targetAdapter.notifyDataSetChanged();
                RefushLineChart();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        activityMainBinding.mainTextShootNum.setText("1");
        activityMainBinding.mainTextCurrentRingnum.setText("0.0");
        activityMainBinding.maintextAllJuNum.setText("0.0");
        int num = RxSPTool.getInt(this, "NumberOfBullets");
        SpanUtils.with(activityMainBinding.mainTextSurplusNum).append("????????? ").setFontSize(14, true)
                .append(String.valueOf(num)).setFontSize(20, true).setForegroundColor(getColor(R.color.textColor)).create();

        SpanUtils.with(activityMainBinding.mainTextGunType).append("?????? ").setFontSize(14, true)
                .append(String.valueOf(95)).setFontSize(20, true).setForegroundColor(getColor(R.color.textColor)).create();


        activityMainBinding.mainProgressHoldingGuntBar.setAnimProgress(0, "??????\n");
//        activityMainBinding.mainProgressHoldingGuntBar.setmText("??????\n" + activityMainBinding.mainProgressHoldingGuntBar.getmProgress() + "%");

        activityMainBinding.mainProgressAimBar.setAnimProgress(0, "??????\n");
//        activityMainBinding.mainProgressAimBar.setmText("??????\n" + activityMainBinding.mainProgressAimBar.getmProgress() + "%");

        activityMainBinding.mainProgressFiringBar.setAnimProgress(0, "??????\n");
//        activityMainBinding.mainProgressFiringBar.setmText("??????\n" + activityMainBinding.mainProgressFiringBar.getmProgress() + "%");

        activityMainBinding.mainProgressAchievementGuntBar.setAnimProgress(0, "??????\n");
//        activityMainBinding.mainProgressAchievementGuntBar.setmText("??????\n" + activityMainBinding.mainProgressAchievementGuntBar.getmProgress() + "%");

        activityMainBinding.mainProgressTotalityBar.setAnimProgress(0, "??????\n");
//        activityMainBinding.mainProgressTotalityBar.setmText("??????\n" + activityMainBinding.mainProgressTotalityBar.getmProgress() + "%");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        activityMainBinding.mainDemo.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        demoAdapter = new DemoAdapter(R.layout.demo_list_item, strings);
        activityMainBinding.mainDemo.setAdapter(demoAdapter);


        activityMainBinding.mainLo.setText(numberNo);


        // ???????????????????????????

        gunDetectThread = GunDetectThread.instance();
        gunDetectThread.start();
    }

    //  ????????? ?????????????????????
    private void getInfo() {


        /**
         *  ?????????Socket ??????????????????????????????
         *
         *  ?????????????????????????????????
         */
        //??????????????????ip
        String ip = RxSPTool.getString(this, api.ip);
        RxLogTool.e(ip);
        if (ip == null || ip.equals("")) {
            ip = "192.168.1.1";
        }
        int port = RxSPTool.getInt(this, api.port);
        if (port <= 0) {
            port = 9291;
        }
        // ??????????????????

        tcpClient = TcpClient.getInstance();
        tcpClient.connect(ip, port);


        tcpClient.setOnDataReceiveListener(new TcpClient.OnDataReceiveListener() {
            @Override
            public void onConnectSuccess() {
                RxToast.showToast("????????????");
                activityMainBinding.mainSocket.setText("?????????");

                sendSocketBasic();
            }

            @Override
            public void onConnectFail() {
                RxToast.showToast("????????????");
                activityMainBinding.mainSocket.setText("?????????");
            }

            @Override
            public void onDataReceive(byte[] buffer, int size, int requestCode) {
                String s = byteToString(buffer);
                RxLogTool.e("requestCode", s + "    " + requestCode + "    " + size + "     " + buffer.length);

                ServerReceiveEntity serverReceiveEntity = GsonUtils.fromJson(s, ServerReceiveEntity.class);

                /**
                 *  ?????????????????????????????? ????????????  ??????????????????
                 *  ???????????????????????????????????????????????????
                 *  ??????????????????
                 *  ????????????????????????
                 *   ??????????????????
                 */

                String mode;
                String sendCode;
                if (serverReceiveEntity.getMode().equals("2")) {
                    reMode = "2";
                    activityMainBinding.mainType.setText("????????????");
                    if (serverReceiveEntity.getIsStarted().equals("0")) {
                        mode = "80";
                        activityMainBinding.mainIsStart.setText("???????????????");
                    } else {
                        mode = "C0";
                        activityMainBinding.mainIsStart.setText("???????????????");
                    }
                    sendCode = "AA55" + mode + DataUtils.Completion1(DataUtils.IntToHex(Integer.parseInt(serverReceiveEntity.getBullets())));
                } else {
                    reMode = "1";
                    mode = "00";
                    activityMainBinding.mainType.setText("????????????");
                    RxLogTool.e("sendData", freeBullet + "");
                    sendCode = "AA55" + mode + DataUtils.Completion1(DataUtils.IntToHex(freeBullet));
                }


                byte[] bytes = DataUtils.HexToByteArr(sendCode);
                String crcString = DataUtils.ByteArrToHex(CrcUtilsReverse.CRC16_Ccitt_Reverse(bytes, bytes.length));
//            RxLogTool.e("Send",crcString);
                new Thread(() -> {
                    while (true) {
                        String string = RxSPTool.getString(MainActivity.this, api.channel);
                        SerialGunPortManager.instance().sendCommand("0004" + string + sendCode + crcString);
                        RxLogTool.e("Send", string + sendCode + crcString);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (isRevice) {
                            isRevice = false;

                            Shootting = false;
                            isShottingFirst = false;
                            break;
                        }
                    }
                }).start();

            }
        });


        activityMainBinding.mainSocketCon.setOnClickListener(view -> {
            //??????????????????ip
            String string = RxSPTool.getString(this, api.ip);
            RxLogTool.e(string);
            if (string == null || string.equals("")) {
                string = "192.168.1.1";
            }
            int anInt = RxSPTool.getInt(this, api.port);
            if (anInt <= 0) {
                anInt = 9291;
            }
            // ??????????????????

//            tcpClient = TcpClient.getInstance();
            if (tcpClient.isConnect()) {
                tcpClient.disconnect();
            }
            tcpClient.connect(string, anInt);



        });


        activityMainBinding.mainSocketStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reMode = "2";

                String sendCode = "AA55" + "C0" + "06";

                byte[] bytes = DataUtils.HexToByteArr(sendCode);
                String crcString = DataUtils.ByteArrToHex(CrcUtilsReverse.CRC16_Ccitt_Reverse(bytes, bytes.length));
//            RxLogTool.e("Send",crcString);
                new Thread(() -> {

                    while (true) {
                        String string = RxSPTool.getString(MainActivity.this, api.channel);
                        SerialGunPortManager.instance().sendCommand("0004" + DataUtils.Completion1(DataUtils.IntToHex(Integer.parseInt(string))) + sendCode + crcString);
                        RxLogTool.e("Send", sendCode + crcString);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (isRevice) {
                            isRevice = false;
                            Shootting = false;
                            isShottingFirst = false;
                            break;
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * ???????????????????????????
     */
    public void sendSocketBasic(){
        ipAddress = NetworkUtils.getIpAddressByWifi();
        InfoEntity infoEntity = new InfoEntity();
        infoEntity.setMainTextJuId(activityMainBinding.mainTextJuId.getText().toString());
        infoEntity.setNo(numberNo);
        infoEntity.setIp(ipAddress);
        infoEntity.setBullets("10");
        infoEntity.setPersonName(activityMainBinding.mainEditPersonName.getText().toString());
        infoEntity.setType("1");
        infoEntity.setMain_text_all_num(activityMainBinding.mainTextAllNum.getText().toString());
        infoEntity.setMain_text_gun_type(activityMainBinding.mainTextGunType.getText().toString());
        infoEntity.setMain_text_surplus_num(activityMainBinding.mainTextSurplusNum.getText().toString());
        infoEntity.setIsStart(false);
        try {
            //TODO socket??????
            tcpClient.sendStrSocket(GsonUtils.toJson(infoEntity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String byteToString(byte[] data) {
        int index = data.length;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                index = i;
                break;
            }
        }
        byte[] temp = new byte[index];
        Arrays.fill(temp, (byte) 0);
        System.arraycopy(data, 0, temp, 0, index);
        String str;
        try {
            str = new String(temp, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
        return str;
    }


    /**
     * ?????????????????????
     *
     * @param source
     */
    private void SetProgress(int[] source) {

        RxLogTool.e("??????", source[0]);
        RxLogTool.e("??????", source[1]);
        RxLogTool.e("??????", source[2]);
        RxLogTool.e("??????", source[3]);
        RxLogTool.e("??????", source[4]);
        activityMainBinding.mainProgressHoldingGuntBar.setAnimProgress(source[0], "??????\n");
//        activityMainBinding.mainProgressHoldingGuntBar.postInvalidate();

        activityMainBinding.mainProgressAimBar.setAnimProgress(source[1], "??????\n");
//        activityMainBinding.mainProgressAimBar.setmText("??????\n" + activityMainBinding.mainProgressAimBar.getmProgress() + "%");
//        activityMainBinding.mainProgressAimBar.postInvalidate();

        activityMainBinding.mainProgressFiringBar.setAnimProgress(source[2], "??????\n");
//        activityMainBinding.mainProgressFiringBar.setmText("??????\n" + activityMainBinding.mainProgressFiringBar.getmProgress() + "%");
//        activityMainBinding.mainProgressFiringBar.postInvalidate();

        activityMainBinding.mainProgressAchievementGuntBar.setAnimProgress(source[3], "??????\n");
//        activityMainBinding.mainProgressAchievementGuntBar.setmText("??????\n" + activityMainBinding.mainProgressAchievementGuntBar.getmProgress() + "%");
//        activityMainBinding.mainProgressAchievementGuntBar.postInvalidate();

        activityMainBinding.mainProgressTotalityBar.setAnimProgress(source[4], "??????\n");
//        activityMainBinding.mainProgressTotalityBar.setmText("??????\n" + activityMainBinding.mainProgressTotalityBar.getmProgress() + "%");
//        activityMainBinding.mainProgressTotalityBar.postInvalidate();
    }

    /**
     * ??????????????????  ??? Sp ??????
     */
    private void initDao() {
        targetDao = DaoManager.getTargetDao();
        TargetMian = getResources().getStringArray(R.array.TargetType);
        isAutoPrint = RxSPTool.getBoolean(this, "AutoPrint");
        String str = RxSPTool.getString(this, "TargetRecordType");
        String[] ReportTypeArray = getResources().getStringArray(R.array.TargetRecordTpye);
        for (int i = 0; i < ReportTypeArray.length; i++) {
            if (ReportTypeArray[i].equals(str)) {
                ReportType = i;
            }
        }
        isShotVoice = RxSPTool.getBoolean(this, "ShotVoice");
    }

    private int IncreaseShotnum() {
        int num = Integer.parseInt(activityMainBinding.mainTextShootNum.getText().toString()) + 1;
        if (num <= 10) {
            activityMainBinding.mainTextShootNum.setText(String.valueOf(num));
        }
        return num;
    }

    private void ViewCalcRingNumber() {
        activityMainBinding.mainTextCurrentRingnum.setText(String.valueOf(activityMainBinding.mainImage.getCurrentRingNum()));
        activityMainBinding.maintextAllJuNum.setText(activityMainBinding.mainImage.getTotalRingNum());
        String allNums = String.valueOf((Integer.parseInt(activityMainBinding.mainTextAllNum.getText().toString().
                replace("????????? ", "")) + 1));
        SpanUtils.with(activityMainBinding.mainTextAllNum).append("????????? ").setFontSize(14, true)
                .append(allNums).setFontSize(20, true).
                setForegroundColor(getColor(R.color.textColor)).create();

    }

    /**
     * ???????????????
     */
    private void RefushViewData() {
        activityMainBinding.mainTextShootNum.setText("1");
        activityMainBinding.mainTextCurrentRingnum.setText("0.0");
        activityMainBinding.maintextAllJuNum.setText("0.0");
    }

    /**
     * ???????????????
     */
    private void initSerial() {
        //        /dev/ttyS2
        boolean TargetisOpen = SerialTargetPortManager.instance().open(api.channel2, api.baudrate115200) != null; //UART12
        if (!TargetisOpen) {
            ToastUtils.showLong("?????????????????????");
        } else {
            RxLogTool.e("???????????????");
        }

        // Com 2 ?????? S2      /dev/ttyS1
        boolean GunisOpen = SerialGunPortManager.instance().open(api.channel1, api.baudrate115200) != null;        //UART14
        if (!GunisOpen) {
            ToastUtils.showLong("?????????????????????");
        } else {
            RxLogTool.e("???????????????");
        }

        // ??????lo??????
        lztek = Lztek.create(MainActivity.this);

        boolean b = lztek.gpioEnable(api.k8);
        boolean b1 = lztek.gpioEnable(api.k7);

        if (!b || !b1) {
            RxToast.showToast("????????????????????????");
            return;
        }
        lztek.setGpioOutputMode(api.k7);
        lztek.setGpioOutputMode(api.k8);

        lztek.setGpioValue(api.k7, 0);
        lztek.setGpioValue(api.k8, 0);
    }


    public void setLoPort(View view) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_loport, null);
        AppCompatSpinner groupNo = inflate.findViewById(R.id.dialog_loport_group_no);
        AppCompatSpinner gunNo = inflate.findViewById(R.id.dialog_loport_gun_no);
        AppCompatButton dimiss = inflate.findViewById(R.id.dialog_loport_dimiss);
        AppCompatButton ok = inflate.findViewById(R.id.dialog_loport_ok);

        int group1 = (DataUtils.HexToInt(RxSPTool.getString(this, api.channel)) - Integer.parseInt(RxSPTool.getString(this, api.number))) / 10;

        groupNo.setSelection(group1 - 1);
        gunNo.setSelection(Integer.parseInt(RxSPTool.getString(this, api.number)) - 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflate).setTitle("??????????????????");
        alertDialog = builder.create();
        alertDialog.show();
        dimiss.setOnClickListener(view1 -> alertDialog.dismiss());

        ok.setOnClickListener(view12 -> {
            int group = groupNo.getSelectedItemPosition() + 1;
            int gun = gunNo.getSelectedItemPosition() + 1;

            // ????????????
            SerialTargetPortManager.instance().close();
            SerialGunPortManager.instance().close();

            // ???????????????
            boolean b = lztek.gpioEnable(api.k8);
            boolean b1 = lztek.gpioEnable(api.k7);
            if (!b || !b1) {
                alertDialog.dismiss();
                RxToast.showToast("????????????????????????");
                return;
            }
            lztek.setGpioOutputMode(api.k7);
            lztek.setGpioOutputMode(api.k8);

            lztek.setGpioValue(api.k7, 1);
            lztek.setGpioValue(api.k8, 1);

            // ??????9600 ?????????????????????

            boolean TargetisOpen = SerialGpioManager.instance().open(api.channel2, api.baudrate9600) != null;
            boolean GunisOpen = SerialGpioK7Manager.instance().open(api.channel1, api.baudrate9600) != null;

            if (!TargetisOpen || !GunisOpen) {
                alertDialog.dismiss();
                RxToast.showToast("????????????????????????");
                return;
            }

            RxLogTool.e("sendData", group * 10 + gun + "");
            //???????????????
            String sendDataTop = "C0";
            sendGpioData = "0009000108E600" + DataUtils.Completion1(DataUtils.IntToHex(group * 10 + gun)) + "430000";


            new Thread(() -> {
                while (isGpioK8Read) {
                    SerialGpioManager.instance().sendCommand(sendDataTop + sendGpioData);
                    RxLogTool.e("sendDatak8", sendDataTop + sendGpioData);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(() -> {
                while (isGpioK7Read) {
                    SerialGpioK7Manager.instance().sendCommand(sendDataTop + sendGpioData);
                    RxLogTool.e("sendDatak7", sendDataTop + sendGpioData);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(() -> {
                while (true) {
                    if (!isGpioK7Read && !isGpioK8Read) {
                        isGpioK7Read = true;
                        isGpioK8Read = true;
                        activityMainBinding.mainLo.setText(gunNo.getSelectedItem().toString());
                        RxSPTool.putString(MainActivity.this, api.number, gunNo.getSelectedItem().toString());
                        RxSPTool.putString(MainActivity.this, api.channel, DataUtils.Completion1(DataUtils.IntToHex(group * 10 + gun)));
                        numberNo = RxSPTool.getString(this, api.number);

                        alertDialog.dismiss();
                        break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        });

    }

    /**
     * ???????????????
     */
    private void initSoundPool() {
        sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);    // ??????SoundPool??????
        hm = new HashMap<Integer, Integer>();                                        // ??????HashMap??????
        try {
            hm.put(1, sp.load(getAssets().openFd("open92.mp3"), 1));
            hm.put(2, sp.load(getAssets().openFd("open95.mp3"), 1));
            hm.put(3, sp.load(getAssets().openFd("shot92.mp3"), 1));
            hm.put(4, sp.load(getAssets().openFd("shot95.mp3"), 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     */
    private void playSound(int sound, int loop, float volume) {
        Log.e("TAG", "playSound: ");
        sp.play(hm.get(sound), volume, volume, 1, loop, 1.0f);
    }

    /**
     * ???????????? ???????????????
     * D01 ?????????
     * F01 ?????????
     *
     * @param string rocker ??????
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RockerMessage string) {
        RxLogTool.e("RockerMessage--", string.getName()+string.getMessage());
        if (string.getName().equals("D01")) {
//            RxLogTool.e("D01--", string.getMessage());
            receiveD01(string.getMessage());
            gunDetectThread.setD();
        } else if (string.getName().equals("F01")) {
//            RxLogTool.e("F01--", string.getMessage());
            receiveF01(string.getMessage());
        } else if (string.getName().equals("G01")) {
            String message = string.getMessage();
//            RxLogTool.e("G01", message);
            if (message.equals(sendGpioData.toUpperCase())) {
                isGpioK8Read = false;
                SerialGpioManager.instance().close();
                lztek.setGpioValue(api.k8, 0);
            }
        } else if (string.getName().equals("G02")) {
            String message = string.getMessage();
//            RxLogTool.e("G02", message);
            if (Objects.equals(message, sendGpioData.toUpperCase())) {
                isGpioK7Read = false;
                SerialGpioK7Manager.instance().close();
                lztek.setGpioValue(api.k7, 0);
            }
        } else if (string.getName().equals("GD01")) {
            RxLogTool.e(ATAG, "????????????100000S");
            if (isShottingFirst) {
//                    handler.sendEmptyMessageDelayed(6, 1000);    //????????????1s??????
                handler.postDelayed(runnable, 1000);    //????????????1s??????
                isShottingFirst = false;
//                    activityMainBinding.mainImage.setShot(false);
                RxLogTool.e(ATAG, "????????????1S");

            }
        }
    }


    private int CurrentShotIndex = 0;
    private ArrayList<Integer> ShotPoints = new ArrayList<>();

    /**
     * ?????????D01??????  ????????????????????????????????????????????????????????????????????????
     */

    String ATAG = "2021???12???18???";

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void receiveD01(String string) {
        runOnUiThread(() -> {
            if (strings.size() > 50) {
                strings.clear();
            }
            strings.add(string);
            activityMainBinding.mainDemo.scrollToPosition(demoAdapter.getItemCount() - 1);
            demoAdapter.notifyDataSetChanged();
        });
        int state = Integer.parseInt(string.substring(4, 6), 16);
        int mGun = Integer.parseInt(string.substring(6, 8), 16);
        int gun = mGun & 0x7F;

        int bulletNum = Integer.parseInt(string.substring(8, 10), 16);

        if (Integer.parseInt(activityMainBinding.mainTextGunType.getText().toString().replace("?????? ", "")) != gun) {
            SpanUtils.with(activityMainBinding.mainTextGunType).append("?????? ").setFontSize(14, true)
                    .append(String.valueOf(gun)).setFontSize(20, true).setForegroundColor(getColor(R.color.textColor)).create();
//            activityMainBinding.mainTextGunType.setText("?????? " + gun);
            activityMainBinding.mainGun.setImageResource(R.mipmap.q92);
        }

        SpanUtils.with(activityMainBinding.mainTextSurplusNum).append("????????? ").setFontSize(14, true)
                .append(String.valueOf(bulletNum)).setFontSize(20, true).setForegroundColor(getColor(R.color.textColor)).create();

        activityMainBinding.mainImage.setShot(false);
        if (state == 128 || state == 192) {
            if (!Shootting) {
                isShottingFirst = true;
                if (isShottingFirst) {        //????????????
                    if (GunVoiceEnable) {
                        if (gun == 92) {
                            playSound(1, 0, 1.0f);
                        } else if (gun == 95) {
                            playSound(2, 0, 1.0f);
                        }
                    }
                    handler.sendEmptyMessage(8);
                }
            } else {
                if (CurrentShotIndex != 0 && state == 128) {
                    handler.sendEmptyMessage(8);
                }
            }
        } else if (state == 64) {
            curMode = "2";
            if (reMode.equals(curMode)) {
                isRevice = true;
                curMode = "0";
            }
            RxLogTool.e("curMode", "curMode");

        } else if (state == 0) {
            // ==1 ???????????????????????????
            curMode = "1";
            if (reMode.equals(curMode)) {
                isRevice = true;
                curMode = "0";
            }
        }

//        String s2 = DataUtils.hexString2binaryString(DataUtils.IntToHex(state));

        if (Shootting) {
//            RxLogTool.e("S1S1", state & 0x03F);
            String s1 = DataUtils.hexString2binaryString(DataUtils.IntToHex(state));

            if (s1.charAt(1) == '0') {
                curMode = "1";
//                RxLogTool.e("S1S1", s1 + "   " + s1.charAt(1));
                if (reMode.equals(curMode)) {
                    isRevice = true;
                    curMode = "0";
                }
            } else {
                curMode = "2";
//                RxLogTool.e("S1S1", s1 + "   " + s1.charAt(1) + "  " + curMode + "  " + reMode);
                if (reMode.equals(curMode)) {
                    if ((mGun & 0x80) == 1) {
                        isRevice = true;
                        curMode = "0";
                    }
                }
            }
        }
        if (curMode.equals("1")) {
            freeBullet = bulletNum;
        }
        if (Shootting && CurrentShotIndex < (state & 0x03F)
//                && state != 128&&state!=192&&state!=64
        ) {            //??????


            activityMainBinding.mainImage.setShot(true); // ????????????true
            if (GunVoiceEnable) {
                if (gun == 92) {
                    playSound(3, 0, 1.0f);
                } else if (gun == 95) {
                    playSound(4, 0, volume95);
                }
            }
            SetProgress(activityMainBinding.mainImage.setDate(ShotPoints, true));
            setVoice();
            targetAdapter.setSelectPosition(activityMainBinding.mainImage.getShootDates().size() - 1);
            targetAdapter.notifyDataSetChanged();
            activityMainBinding.mainRecycler.smoothScrollToPosition(targetAdapter.getItemCount() - 1);
            ViewCalcRingNumber();
            activityMainBinding.mainImage.postInvalidate();
            RefushLineChart();
            CurrentShotIndex = (state & 0x03F);
            activityMainBinding.mainTextShootNum.setText(String.valueOf(CurrentShotIndex));

            /**
             *  ???????????????????????? ??????????????????
             */

            handler.sendEmptyMessage(52);

            // ?????????????????????????????? ???????????? ????????????????????????false

            if ((state & 0x03F) == bulletNum) {
                RxLogTool.e(ATAG, "????????????");
                if (isShottingFirst) {
//                    handler.sendEmptyMessageDelayed(6, 1000);    //????????????1s??????
                    handler.postDelayed(runnable, 1000);    //????????????1s??????
                    isShottingFirst = false;
//                    activityMainBinding.mainImage.setShot(false);
                    RxLogTool.e(ATAG, "????????????1S");

                }
            } else {
                RxLogTool.e("??????????????????");
                activityMainBinding.mainImage.clear();
            }
            RxLogTool.e(ATAG, "??????");
        }
    }

    /**
     * ??????????????????  ????????????
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Shootting = false;
            printPersonName = activityMainBinding.mainEditPersonName.getText().toString();
            printBureau = activityMainBinding.mainTextJuId.getText().toString();
            activityMainBinding.mainEditPersonName.setEnabled(true);

            String allNums = String.valueOf((Integer.parseInt(activityMainBinding.mainTextAllNum.getText().toString().
                    replace("????????? ", ""))));

            RxLogTool.e("?????????", allNums);
            activityMainBinding.mainImage.SaveData(allNums);
            if (isAutoPrint) {
                printAchievement.PrintImg(printPersonName, printBureau);
            }
            handler.removeCallbacks(this);
        }
    };


    private int a = 0;

    private long mLastReceiveTime;

    /**
     * ?????????F01??????   ?????????????????????????????????????????????????????????
     */
    private void receiveF01(String string) {
        if (Shootting) {
            ShotPoints.clear();
            activityMainBinding.mainLight.setText("");
//            Log.e("receiveF01", "receiveF01: "+string);
            int Ligtnum1 = Integer.parseInt(string.substring(4, 8), 16);
//            int Ligtnum1 = Integer.parseInt("00EE", 16);
            int Ligtnum2 = Integer.parseInt(string.substring(8, 12), 16);
            int Ligtnum3 = Integer.parseInt(string.substring(12, 16), 16);
            int Ligtnum4 = Integer.parseInt(string.substring(16, 20), 16);
            int Ligtnum5 = Integer.parseInt(string.substring(20, 24), 16);
            int Ligtnum6 = Integer.parseInt(string.substring(24, 28), 16);
            a++;
            activityMainBinding.mainLight.setText(Ligtnum1 + "," + Ligtnum2 + "," + Ligtnum3 + "," + Ligtnum4 + "," + Ligtnum5 + "," + Ligtnum6 + ">>>>" + a);
            /**
             *  ?????????6???????????? ???????????????????????????0 ?????????????????????
             *  ??? ?????????????????? ?????????????????? ??????????????? ????????? ?????????????????? ??????????????????500ms
             *  ???500ms ?????????????????? ?????????100ms?????????????????? ???
             *  ???????????? ?????????????????????  ???????????? ????????????????????????
             *
             *  ???????????????2022.3.1???
             */

            int mLightSum = Ligtnum1 + Ligtnum2 + Ligtnum3 + Ligtnum4 + Ligtnum5 + Ligtnum6;
            if (mLightSum > 0) {
                if (Ligtnum1 != 0) {
                    ShotPoints.add(Ligtnum1);
                }
                if (Ligtnum2 != 0) {
                    ShotPoints.add(Ligtnum2);
                }
                if (Ligtnum3 != 0) {
                    ShotPoints.add(Ligtnum3);
                }
                if (Ligtnum4 != 0) {
                    ShotPoints.add(Ligtnum4);
                }
                if (Ligtnum5 != 0) {
                    ShotPoints.add(Ligtnum5);
                }
                if (Ligtnum6 != 0) {
                    ShotPoints.add(Ligtnum6);
                }
            } else {
//                RxLogTool.e("??????");
                if ((System.currentTimeMillis() - mLastReceiveTime) > api.mTime) {
                    activityMainBinding.mainImage.setDate(ShotPoints, false);
                    activityMainBinding.mainImage.postInvalidate();
                    RefushLineChart();


                    /**
                     *   ?????????????????????
                     */

                    handler.sendEmptyMessage(51);


                } else {
                    mLastReceiveTime = System.currentTimeMillis();
                }
            }

            if (ShotPoints.size() > 0) {
                activityMainBinding.mainImage.setDate(ShotPoints, false);
//                RxLogTool.e("???????????????????????????2", System.currentTimeMillis());
                activityMainBinding.mainImage.postInvalidate();
//                RxLogTool.e("???????????????????????????", System.currentTimeMillis());
                RefushLineChart();

                /**
                 *   ?????????????????????
                 */

                handler.sendEmptyMessage(51);

            }


        }
        handler.removeMessages(7);
        if (ChannelChangeEnable) {
            handler.sendEmptyMessageDelayed(7, 1000);
        }
    }

    /**
     * ??????GPIO???????????????
     */
    private void ChangeChannel() {
        ChangeChannelWriteThread changeChannelWriteThread = new ChangeChannelWriteThread(this, changechannel);
        changeChannelWriteThread.start();
    }

    /**
     * ?????????recycler
     */
    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        activityMainBinding.mainRecycler.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        targetAdapter = new MainAdapter(R.layout.main_list_item, activityMainBinding.mainImage.getShootDates());
        activityMainBinding.mainRecycler.setAdapter(targetAdapter);
        targetAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (!Shootting && !isPlayBacking) {
                    if (targetAdapter.getSelectPosition() != position) {
                        targetAdapter.setSelectPosition(position);
                        targetAdapter.notifyDataSetChanged();
                        SetProgress(activityMainBinding.mainImage.getSource(position));
                        if (activityMainBinding.mainImage.isPlayBack()) {
                            activityMainBinding.mainImage.PlayBackTrack(position);
                        } else {
                            activityMainBinding.mainImage.PlayAllTrack(position);
                        }
                    } else {
                        targetAdapter.setSelectPosition(-1);
                        targetAdapter.notifyDataSetChanged();
                        activityMainBinding.mainImage.PlayAllTrack(-1);
                    }
                    RefushLineChart();
                }
            }
        });
        targetAdapter.addChildClickViewIds(R.id.main_list_item_replay);
        targetAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.main_list_item_replay &&
                    targetAdapter.getSelectPosition() == position && !isPlayBacking && !Shootting) {
                activityMainBinding.mainImage.PlayBackTrack(position);
                handler.sendEmptyMessageDelayed(2, 100);
                isPlayBacking = true;
            }
        });
        targetAdapter.setReportType(RxSPTool.getString(this, "TargetRecordType"), getResources());
    }

    double x = 0.1;
    double y = 0.1;

    /**
     * ??????????????????
     */
    private void initLineChart() {
        lineDataSet = new LineDataSet(activityMainBinding.mainImage.getLineChartList(), "??????");
        lineDataSet.setCircleColor(Color.parseColor("#67BCFF"));    //??????????????????
        lineDataSet.setColor(Color.WHITE);          //??????????????????
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);                                     //?????????????????????????????????true
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);                          //??????Mode??????????????????????????????Mode
        lineData = new LineData(lineDataSet);
        activityMainBinding.mainChart.setData(lineData);
        activityMainBinding.mainChart.setDrawBorders(false);
        activityMainBinding.mainChart.getDescription().setEnabled(true);
        activityMainBinding.mainChart.getDescription().setText("???????????????");
        activityMainBinding.mainChart.getDescription().setTextColor(R.color.white);
        activityMainBinding.mainChart.getAxisRight().setEnabled(false);
        activityMainBinding.mainChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        activityMainBinding.mainChart.postInvalidate();
        //?????????????????????
        activityMainBinding.mainChart.setOnClickListener(v -> {
            //??????
//            SpeechUtils.getInstance(this).SpeakVoice2("10???");
//            startActivity(new Intent(MainActivity.this, WitnessCheckActivity.class));
//            startActivity(new Intent(MainActivity.this,PsychologyActivity.class));


            x = x + 0.1;
            y = y + 0.1;
            tcpClient.sendStrSocket("{\n" +
                    "  \"aimTime\": \"2022-08-10 10:48:28.718\",\n" +
                    "  \"aim_X\": "+x+",\n" +
                    "  \"aim_Y\": "+y+",\n" +
                    "  \"no\": \"5\",\n" +
                    "  \"personName\": \"??????10\",\n" +
                    "  \"ringNumber\": \"0.0\",\n" +
                    "  \"ip\": \"192.168.31.154\",\n" +
                    "}");

        });
    }

    /**
     * ???????????????
     */
    private void RefushLineChart() {
        lineDataSet.notifyDataSetChanged();
        lineData.notifyDataChanged();
        activityMainBinding.mainChart.setData(lineData);
        activityMainBinding.mainChart.postInvalidate();
    }

    /**
     * ??????????????????
     */

    private void initVoice() {
        if (!SpeechUtils.IsInit()) {
            SpeechUtils.getInstance(MainActivity.this);
        }
    }

    private void setVoice() {
        if (isShotVoice) {
            if (SpeechUtils.IsInit()) {
                double ringNum = activityMainBinding.mainImage.getCurrentRingNum();
                int direction = activityMainBinding.mainImage.getCurrentDirection();
                String str;
                ReportType = targetAdapter.getReportType();
                if (ReportType == 0) {
                    if (ringNum == 0.0) {
                        str = "??????";
                    } else {
                        str = ringNum + "???";
                    }
                } else {
                    if (ringNum >= 10.0) {
                        str = "10" + "???";
                    } else if (ringNum >= 9.0) {
                        str = "9" + "???";
                    } else if (ringNum >= 8.0) {
                        str = "8" + "???";
                    } else if (ringNum >= 7.0) {
                        str = "7" + "???";
                    } else if (ringNum >= 6.0) {
                        str = "6" + "???";
                    } else if (ringNum >= 5.0) {
                        str = "5" + "???";
                    } else if (ringNum >= 4.0) {
                        str = "4" + "???";
                    } else {
                        str = "??????";
                    }
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("shotvoice", str);
                msg.setData(bundle);
                msg.what = 10;
                handler.sendMessageDelayed(msg, 500);
            }
        }
    }

    /**
     * ???????????????????????????
     *
     * @param view ??????view
     */
    public void onImageHistoryClick(View view) {
        if (Shootting) {
            ToastUtils.showShort("????????????,???????????????");
        } else {
            RxActivityTool.skipActivity(this, HistoryActivity.class);
        }
    }

    /**
     * ??????setting Dialog
     *
     * @param view ??????view
     */
    public void onImageSettingClick(View view) {
        settingDialog = new SettingDialog();
        settingDialog.show(getSupportFragmentManager(), "tag");
        settingDialog.setListener(new SettingDialog.OnListener() {
            @Override
            public void SelectTargetMian(SettingDialog settingDialog, int position) {
                activityMainBinding.mainImage.setSelectTargetType(position);
                activityMainBinding.mainTargetMian.setText(TargetMian[position]);
            }

            @Override
            public void SelectTargetRecordType(SettingDialog settingDialog, String SelectedTargetRecordType) {
                targetAdapter.setReportType(SelectedTargetRecordType, getResources());
            }

            @Override
            public void SelectShotNum(SettingDialog settingDialog, int shotnums) {

            }

            @Override
            public void SelectGunVoice(SettingDialog settingDialog, boolean isTrue) {
                GunVoiceEnable = isTrue;
            }

            @Override
            public void SelectShotVoice(SettingDialog settingDialog, boolean isTrue) {
                isShotVoice = isTrue;
            }

            @Override
            public void SelectAutoPrint(SettingDialog settingDialog, boolean isAuto) {
                isAutoPrint = isAuto;
            }
        });
    }

    //????????????long???????????????????????????????????????????????????????????????
    private long mExitTime;

    /**
     * ????????????
     *
     * @param view ??????view
     */
    public void onImageCloseClick(View view) {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            //??????2000ms??????????????????????????????Toast????????????
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            //???????????????????????????????????????????????????????????????????????????
            mExitTime = System.currentTimeMillis();
        } else {
            //??????2000ms??????????????????????????????????????????-??????System.exit()??????????????????
            RxActivityTool.AppExit(this);
        }

    }


    //????????????long???????????????????????????????????????????????????????????????
    private long mShutDownTime;

    /**
     * ??????
     *
     * @param view ??????view
     */
    public void onImageShutDownClick(View view) {
        if ((System.currentTimeMillis() - mShutDownTime) > 2000) {
            //??????2000ms??????????????????????????????Toast????????????
            Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
            //???????????????????????????????????????????????????????????????????????????
            mShutDownTime = System.currentTimeMillis();
        } else {

            try {
                Runtime.getRuntime().exec("reboot -p"); //??????
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ??????????????? ?????????
     *
     * @param activity
     */
    public static void setNavigationBarVisible(Activity activity, boolean isHide) {
        if (isHide) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        } else {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {//????????????
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onDestroy() {
        // ???????????? : ?????????????????????
        if (mBatteryReceiver != null) {
            unregisterReceiver(mBatteryReceiver);
        }
        super.onDestroy();
        SpeechUtils.stop();
        if (mainGunWriteThread != null) {
            mainGunWriteThread.interrupt();
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        hm.clear();
        sp.release();
        if (EventBus.getDefault().isRegistered(this))//????????????
            EventBus.getDefault().unregister(this);
//        BusUtils.unregister(this);
    }

    /**
     * ???????????????????????????
     */
    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // ????????????
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            // ????????????
            activityMainBinding.tvHomeBattery.setText(level + "%");
        }
    }
}