package com.example.laser.database.dao;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.laser.database.AimDao;
import com.example.laser.database.DaoMaster;
import com.example.laser.database.DaoSession;
import com.example.laser.database.ShotNumsDao;
import com.example.laser.database.TargetDao;
import com.example.laser.ui.app.MyApplication;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  on 2021/7/9 15:27.
 */
public class DaoManager {
    private static DaoManager daoManager;
    private DaoMaster daoMaster;
    private DaoSession daoSession = null;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private static String SQL_DISTINCT_PERSON = "SELECT DISTINCT " + TargetDao.Properties.TargetPerson.columnName + " FROM " + TargetDao.TABLENAME;
    private static String SQL_DISTINCT_YEAR = "SELECT DISTINCT " + TargetDao.Properties.TargetDate_Year.columnName + " FROM " + TargetDao.TABLENAME;
    private static String SQL_DISTINCT_MONTH = "SELECT DISTINCT " + TargetDao.Properties.TargetDate_Month.columnName + " FROM " + TargetDao.TABLENAME;
    private static String SQL_DISTINCT_DAY = "SELECT DISTINCT " + TargetDao.Properties.TargetDate_Day.columnName + " FROM " + TargetDao.TABLENAME;
//    private static String SQL_DISTINCT_BUREAU = "SELECT DISTINCT " + TargetDao.Properties.TargetBureauId.columnName + " FROM " + TargetDao.TABLENAME;

    private DaoManager() {
    }

    @NonNull
    public static DaoManager getInstance() {
        if (daoManager == null) {
            daoManager = new DaoManager();
        }
        return daoManager;
    }

    @NonNull
    public DaoMaster getDaoManager(Context context) {
        if (daoMaster == null) {
            devOpenHelper = new DaoMaster.DevOpenHelper(context, "data_Dao", null);
            daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        }
        return daoMaster;
    }

    @NonNull
    public DaoSession getDaoSession() {
        if (daoSession == null) {
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 关闭DaoSession
     */
    public void closeDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    /**
     * 关闭Helper
     */
    public void closeHelper() {
        if (devOpenHelper != null) {
            devOpenHelper.close();
            devOpenHelper = null;
        }
    }

    /**
     * 关闭所有的操作
     */
    public void closeConnection() {
        closeDaoSession();
        closeHelper();
    }

    /**
     * 清除数据库所有数据
     */
    public static void deleteAllSql() {
        Database database = com.example.laser.database.dao.DaoManager.getInstance().getDaoManager(MyApplication.getApplication()).getDatabase();
        DaoMaster.dropAllTables(database, true);
        DaoMaster.createAllTables(database, true);
    }

    public static TargetDao getTargetDao() {
        return DaoManager.getInstance().getDaoManager(MyApplication.getApplication()).newSession().getTargetDao();
    }

    public static AimDao getAimDao() {
        return DaoManager.getInstance().getDaoManager(MyApplication.getApplication()).newSession().getAimDao();
    }

    public static ShotNumsDao getShotNumsDao() {
        return DaoManager.getInstance().getDaoManager(MyApplication.getApplication()).newSession().getShotNumsDao();
    }

    public static List<String> listPerson() {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = DaoManager.getInstance().getDaoManager(MyApplication.getApplication()).newSession().getDatabase().rawQuery(SQL_DISTINCT_PERSON, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    public static List<String> listTargetYear() {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = DaoManager.getInstance().getDaoManager(MyApplication.getApplication()).newSession().getDatabase().rawQuery(SQL_DISTINCT_YEAR, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    public static List<String> listTargetYearToMonth(String year) {
        ArrayList<String> result = new ArrayList<String>();
        String string = " WHERE " + TargetDao.Properties.TargetDate_Year.columnName + " = " + year;
        Cursor c = DaoManager.getInstance().getDaoManager(MyApplication.getApplication())
                .newSession().getDatabase().rawQuery(SQL_DISTINCT_MONTH + string, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    public static List<String> listTargetYearToMonthToDay(String year, String month) {
        ArrayList<String> result = new ArrayList<String>();
        String string = " WHERE " + TargetDao.Properties.TargetDate_Year.columnName + " = " + year +
                " AND " + TargetDao.Properties.TargetDate_Month.columnName + " = " + month;
        Cursor c = DaoManager.getInstance().getDaoManager(MyApplication.getApplication())
                .newSession().getDatabase().rawQuery(SQL_DISTINCT_DAY + string, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    public static List<String> listTargetYearToMonthToDayToPerson(String year, String month, String day) {
        ArrayList<String> result = new ArrayList<String>();
        String string = " WHERE " + TargetDao.Properties.TargetDate_Year.columnName + " = " + year +
                " AND " + TargetDao.Properties.TargetDate_Month.columnName + " = " + month +
                " AND " + TargetDao.Properties.TargetDate_Day.columnName + " = " + day;
        Cursor c = DaoManager.getInstance().getDaoManager(MyApplication.getApplication())
                .newSession().getDatabase().rawQuery(SQL_DISTINCT_PERSON + string, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

}
