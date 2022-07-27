package com.example.laser.ui.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import androidx.multidex.MultiDex;

import com.blankj.utilcode.util.Utils;
import com.example.laser.R;
import com.example.laser.api.api;
import com.example.laser.database.DaoMaster;
import com.example.laser.database.DaoSession;
import com.vondear.rxtool.RxLogTool;
import com.vondear.rxtool.RxSPTool;
import com.vondear.rxtool.RxTool;

/**
 * Created by  on 2021/7/9 15:20.
 */
public class MyApplication extends Application {
    private static Application myApplication = null;

    public static Application getApplication() {
        return myApplication;
    }

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        RxTool.init(this);
        Utils.init(this);
        myApplication = this;
        initFirstData();
        setDatabase();
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "shop-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    private void initFirstData() {
        SharedPreferences preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
        if (preferences.getBoolean("firststart", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firststart", false);
            editor.commit();
            String[] TargetRecordTypeStrs = getResources().getStringArray(R.array.TargetRecordTpye);
            RxSPTool.putString(this, "TargetRecordType", TargetRecordTypeStrs[0]);
            RxSPTool.putBoolean(this, "AutoPrint", false);
            RxSPTool.putBoolean(this, "GunVoice", true);
            RxSPTool.putInt(this, "NumberOfBullets", 10);
            RxSPTool.putInt(this, "Channel", 1);
            RxSPTool.putInt(this, "SelectTargetType", 0);
            RxSPTool.putBoolean(this, "ShotVoice", true);

            RxSPTool.putString(this, api.ip, "192.168.1.1");
            RxSPTool.putInt(this, api.port, 9291);
            RxSPTool.putString(this, api.number, "1");
            RxSPTool.putString(this, api.channel, "0B");
            RxLogTool.e("write", "ok");
        }
    }

}
