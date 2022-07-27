package com.example.laser.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;

import androidx.print.PrintHelper;

import com.example.laser.R;
import com.example.laser.database.Aim;
import com.example.laser.database.AimDao;
import com.example.laser.database.Target;
import com.example.laser.database.TargetDao;
import com.example.laser.database.dao.DaoManager;
import com.example.laser.ui.data.ImgXYCalc;
import com.vondear.rxtool.RxLogTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by  on 2021/7/27 18:33.
 */
public class PrintAchievement {
    private TargetDao targetDao;
    private AimDao aimDao;
    private Resources resources;
    private Context context;

    private int width = 300;
    private int height = 300;

    public PrintAchievement(Resources resources, Context context) {
        targetDao = DaoManager.getTargetDao();
        aimDao = DaoManager.getAimDao();
        this.resources = resources;
        this.context = context;
    }


    public void PrintImg(String PersonName, String Bureaus) {
        Target target = targetDao.queryBuilder().where(TargetDao.Properties.TargetPerson.eq(PersonName),
                TargetDao.Properties.TargetBureauId.eq(Bureaus)).build().unique();
        Bitmap bitmap = Bitmap.createBitmap(1200, 1697, Bitmap.Config.ARGB_8888);
        Canvas Bitmap_canvas = new Canvas(bitmap);
        Bitmap AchievementBitmap = Bitmap.createBitmap(590, 300, Bitmap.Config.ARGB_8888);
        Canvas AchievementBitmap_Canvas = new Canvas(AchievementBitmap);
        Bitmap AchievementTargetBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        RxLogTool.e(AchievementTargetBitmap.getWidth());
        RxLogTool.e(AchievementTargetBitmap.getHeight());

        Canvas AchievementTargetBitmap_Canvas = new Canvas(AchievementTargetBitmap);
        List<Aim> aimList = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(target.getId())).build().list();
        Collections.sort(aimList, new Comparator<Aim>() {
            @Override
            public int compare(Aim o1, Aim o2) {
                int i = 0;
                if (o1.getAim_ShotNum() > o2.getAim_ShotNum()) {
                    i = 1;
                }
                if (o1.getAim_ShotNum() < o2.getAim_ShotNum()) {
                    i = -1;
                }
                return i;
            }
        });
        List<Aim> shotlist = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(target.getId()),
                AimDao.Properties.Aim_ShotNum.notEq(-1)).build().list();
        Collections.sort(shotlist, new Comparator<Aim>() {
            @Override
            public int compare(Aim o1, Aim o2) {
                int i = 0;
                if (o1.getAim_ShotNum() > o2.getAim_ShotNum()) {
                    i = 1;
                }
                if (o1.getAim_ShotNum() < o2.getAim_ShotNum()) {
                    i = -1;
                }
                return i;
            }
        });
        ImgXYCalc imgXYCalc = new ImgXYCalc(300, 300, resources);
        Bitmap ShotBitmap = BitmapFactory.decodeStream(resources.openRawResource(R.raw.d3));
        Bitmap TargetBitmap = BitmapFactory.decodeStream(resources.openRawResource(R.raw.target300));
        if (TargetBitmap != null) {
            RxLogTool.e("TargetBitmap", TargetBitmap.getWidth());
            RxLogTool.e("TargetBitmap——", ShotBitmap.getWidth());
        }
        RxLogTool.e("TargetBitmap.w", TargetBitmap.getWidth());
        RxLogTool.e("TargetBitmap.h", TargetBitmap.getHeight());
        SimpleDateFormat DateSFormat = new SimpleDateFormat("mm:ss.SSS");
        SimpleDateFormat DateTimeFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
        Date StartDate = null;
        Date LastDate = null;
        Date CurrentDate = null;
        try {
            StartDate = DateTimeFormat.parse(target.getTargetDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int width = ShotBitmap.getWidth() / 2;
        int height = ShotBitmap.getHeight() / 2;
        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setAntiAlias(true);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        TextPaint painttext = new TextPaint();
        painttext.setTextSize(20);
        painttext.setColor(Color.BLACK);
        for (int i = 0; i < shotlist.size(); i++) {
            RxLogTool.e("ring" + i + 1, shotlist.get(i).getAim_RingNumber());
            AchievementBitmap = Bitmap.createBitmap(590, 300, Bitmap.Config.ARGB_8888);
            AchievementBitmap_Canvas = new Canvas(AchievementBitmap);
            AchievementTargetBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            AchievementTargetBitmap_Canvas = new Canvas(AchievementTargetBitmap);
            AchievementTargetBitmap_Canvas.drawBitmap(TargetBitmap, 0, 0, paint);
            path.reset();

            if (i == 0) {
                if (shotlist.get(1).getAimCount() - shotlist.get(0).getAimCount() < 10) {
                    for (int j = 0; j < shotlist.get(1).getAimCount() - 1; j++) {
                        if (j == 0) {
                            path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                    imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                        } else {
                            if (aimList.get(j).getAim_X() != -1.0) {
                                if (aimList.get(j - 1).getAim_X() != -1.0) {
                                    path.lineTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                            imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                                } else {
                                    path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                            imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                                }
                            }
                        }
                    }
                    AchievementTargetBitmap_Canvas.drawPath(path, paint);
                    if (shotlist.get(0).getAim_X() != -1) {
                        AchievementTargetBitmap_Canvas.drawBitmap(ShotBitmap,
                                imgXYCalc.getCalcX(shotlist.get(0).getAim_X()) - width,
                                imgXYCalc.getCalcY(shotlist.get(0).getAim_Y()) - height,
                                paint);
                    }
                } else {
                    for (int j = 0; j < shotlist.get(0).getAimCount() + 10; j++) {
                        if (j == i) {
                            path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                    imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                        } else {
                            if (aimList.get(j).getAim_X() != -1.0) {
                                if (aimList.get(j - 1).getAim_X() != -1.0) {
                                    path.lineTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                            imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                                } else {
                                    path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                            imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                                }
                            }
                        }
                    }
                    AchievementTargetBitmap_Canvas.drawPath(path, paint);
                    if (shotlist.get(0).getAim_X() != -1) {
                        AchievementTargetBitmap_Canvas.drawBitmap(ShotBitmap,
                                imgXYCalc.getCalcX(shotlist.get(0).getAim_X()) - width,
                                imgXYCalc.getCalcY(shotlist.get(0).getAim_Y()) - height,
                                paint);
                    }
                }
            } else if (i < shotlist.size() - 1) {
                if (shotlist.get(i + 1).getAimCount() - shotlist.get(i).getAimCount() < 10) {
                    for (int j = shotlist.get(i - 1).getAimCount(); j < shotlist.get(i + 1).getAimCount() - 1; j++) {
                        if (j == shotlist.get(i - 1).getAimCount()) {
                            path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                    imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                        } else {
                            if (aimList.get(j).getAim_X() != -1.0) {
                                if (aimList.get(j - 1).getAim_X() != -1.0) {
                                    path.lineTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                            imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                                } else {
                                    path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                            imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                                }
                            }
                        }
                    }
                    AchievementTargetBitmap_Canvas.drawPath(path, paint);
                    if (shotlist.get(i).getAim_X() != -1) {
                        AchievementTargetBitmap_Canvas.drawBitmap(ShotBitmap,
                                imgXYCalc.getCalcX(shotlist.get(i).getAim_X()) - width,
                                imgXYCalc.getCalcY(shotlist.get(i).getAim_Y()) - height,
                                paint);
                    }
                } else {
                    for (int j = shotlist.get(i - 1).getAimCount(); j < shotlist.get(i).getAimCount() + 10; j++) {
                        if (j == shotlist.get(i - 1).getAimCount()) {
                            path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                    imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                        } else {
                            if (aimList.get(j).getAim_X() != -1.0) {
                                if (aimList.get(j - 1).getAim_X() != -1.0) {
                                    path.lineTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                            imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                                } else {
                                    path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                            imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                                }
                            }
                        }
                    }
                    AchievementTargetBitmap_Canvas.drawPath(path, paint);
                    if (shotlist.get(i).getAim_X() != -1) {
                        AchievementTargetBitmap_Canvas.drawBitmap(ShotBitmap,
                                imgXYCalc.getCalcX(shotlist.get(i).getAim_X()) - width,
                                imgXYCalc.getCalcY(shotlist.get(i).getAim_Y()) - height,
                                paint);
                    }
                }
            } else {
                for (int j = shotlist.get(i - 1).getAimCount(); j < shotlist.size(); j++) {
                    if (j == shotlist.get(i - 1).getAimCount()) {
                        path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                    } else {
                        if (aimList.get(j).getAim_X() != -1.0) {
                            if (aimList.get(j - 1).getAim_X() != -1.0) {
                                path.lineTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                        imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                            } else {
                                path.moveTo(imgXYCalc.getCalcX(aimList.get(j).getAim_X()),
                                        imgXYCalc.getCalcY(aimList.get(j).getAim_Y()));
                            }
                        }
                    }
                }
                AchievementTargetBitmap_Canvas.drawPath(path, paint);
                if (shotlist.get(i).getAim_X() != -1) {
                    AchievementTargetBitmap_Canvas.drawBitmap(ShotBitmap,
                            imgXYCalc.getCalcX(shotlist.get(i).getAim_X()) - width,
                            imgXYCalc.getCalcY(shotlist.get(i).getAim_Y()) - height,
                            paint);
                }
            }
            AchievementBitmap_Canvas.drawBitmap(AchievementTargetBitmap, 0, 0, paint);
            ///////此处处理成绩文字信息/////////////////////
            AchievementBitmap_Canvas.drawText("第" + (i + 1) + "发" + "  " + shotlist.get(i).getAim_RingNumber() + "环",
                    360, 30, painttext);
            AchievementBitmap_Canvas.drawText("据枪: " + shotlist.get(i).getHoldingGunt() + "%", 380, 70, painttext);
            AchievementBitmap_Canvas.drawText("瞄准: " + shotlist.get(i).getAimFractions() + "%", 380, 110, painttext);
            AchievementBitmap_Canvas.drawText("击发: " + shotlist.get(i).getShotFractions() + "%", 380, 150, painttext);
            AchievementBitmap_Canvas.drawText("成绩: " + shotlist.get(i).getAchieveFractions() + "%", 380, 190, painttext);
            AchievementBitmap_Canvas.drawText("总体: " + shotlist.get(i).getTotalFractions() + "%", 380, 230, painttext);
            String times = "";
            CurrentDate = null;
            if (i == 0) {
                try {
                    CurrentDate = DateTimeFormat.parse(shotlist.get(i).getAimTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (CurrentDate != null && StartDate != null) {
                    times = DateSFormat.format(new Date(
                            CurrentDate.getTime() - StartDate.getTime()
                    ));
                    LastDate = CurrentDate;
                }
            } else {
                try {
                    CurrentDate = DateTimeFormat.parse(shotlist.get(i).getAimTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (CurrentDate != null && LastDate != null) {
                    times = DateSFormat.format(new Date(
                            CurrentDate.getTime() - LastDate.getTime()
                    ));
                    LastDate = CurrentDate;
                }
            }
            AchievementBitmap_Canvas.drawText("用时(分:秒:毫秒) " + times, 320, 270, painttext);

            //////////////////////////////////////////////
            switch (i) {
                case 0: //1
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 0, 0, paint);
                    break;
                case 1: //2
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 610, 0, paint);
                    break;
                case 2: //3
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 0, 320, paint);
                    break;
                case 3: //4
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 610, 320, paint);
                    break;
                case 4: //5
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 0, 640, paint);
                    break;
                case 5: //6
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 610, 640, paint);
                    break;
                case 6: //7
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 0, 960, paint);
                    break;
                case 7:
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 610, 960, paint);
                    break;
                case 8:
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 0, 1280, paint);
                    break;
                case 9:
                    Bitmap_canvas.drawBitmap(AchievementBitmap, 610, 1280, paint);
                    break;
                default:
                    break;
            }

        }
        doPrintPictures(bitmap);
    }

    private void doPrintPictures(Bitmap bitmap) {
        RxLogTool.e("doPrintPictures");
        PrintHelper photoPrinter = new PrintHelper(context);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FILL);
        photoPrinter.setOrientation(PrintHelper.ORIENTATION_PORTRAIT);
//        Bitmap bitmapp = BitmapFactory.decodeResource(getResources(), R.mipmap.xhhdtarget_di);              //本地图片
        String fileName = "printimg" + ".jpg";
        photoPrinter.printBitmap(fileName, bitmap, null);
    }
}
