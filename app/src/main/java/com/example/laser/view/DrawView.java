package com.example.laser.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.laser.R;
import com.example.laser.database.Aim;
import com.example.laser.database.AimDao;
import com.example.laser.database.ShotNums;
import com.example.laser.database.ShotNumsDao;
import com.example.laser.database.Target;
import com.example.laser.database.TargetDao;
import com.example.laser.database.dao.DaoManager;
import com.example.laser.ui.data.AimCalcData;
import com.example.laser.ui.data.AnalysisAchievement;
import com.example.laser.ui.data.ImgXYCalc;
import com.github.mikephil.charting.data.Entry;
import com.vondear.rxtool.RxLogTool;
import com.vondear.rxtool.RxSPTool;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by  on 2021/6/14.
 * 绘制弹孔 和 折线
 */

public class DrawView extends AppCompatImageView {
    private Paint paint;
    private Rect mSrcRect, mDestRect;
    private int EndShootCount = 5;
    private Bitmap bitmap;
    private Bitmap bitmap1;
    private Path path;
    private int Width = 0;
    private int height = 0;
    private ImgXYCalc imgXYCalc;
    private int currentIndex = 0;
    private Bitmap bitmapp;
    private Canvas imgcanvas;
    private int SelectLastIndex = -1;
    private int SelectIndex = -1;
    private boolean PlayBack = false;
    private boolean PlayAllBack = false;
    private int PlayBackStartPosition = -1;
    private int PlayBackEndPosition = -1;
    private int PlayBackShootPostion = -1;
    private int PlayBackPosition = -1;
    private int PlayBackImgIndex = -1;
    private int PlayBackChartIndex = -1;
    private int Bureau = 1;
    private int GunType = 0;
    private int TotalShootNumbers = 0;
    private Date StartDate = new Date();
    private String PersonName = "";
    private String[] TargetType;
    private int SelectTargetType;
    private SimpleDateFormat DateTimeFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
    private SimpleDateFormat DateSFormat = new SimpleDateFormat("mm:ss.SSS");
    private ArrayList<Entry> lineChartList = new ArrayList<>();
    private ArrayList<AimCalcData> shootLineDates = new ArrayList<>();
    private ArrayList<AimCalcData> shootLineDates_ = new ArrayList<>();
    private ArrayList<AimCalcData> shootDates = new ArrayList<>();

    private Canvas PlayBackCanvas;
    private Bitmap PlayBackbitmap;

    private TargetDao targetDao = DaoManager.getTargetDao();
    private AimDao aimDao = DaoManager.getAimDao();

    private ShotNumsDao shotNumsDao = DaoManager.getShotNumsDao();

    private boolean isShot = false;
    private boolean isReplay = false;


    private String TAG = "DrawView";

    public DrawView(Context context) {
        super(context);
        paint = new Paint();
        path = new Path();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d1);
        bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d2);
        bitmapp = Bitmap.createBitmap(532, 532, Bitmap.Config.ARGB_8888);
        imgcanvas = new Canvas(bitmapp);
        PlayBackbitmap = Bitmap.createBitmap(532, 532, Bitmap.Config.ARGB_8888);
        PlayBackCanvas = new Canvas(PlayBackbitmap);
        TargetType = getResources().getStringArray(R.array.TargetType);
        SelectTargetType = RxSPTool.getInt(getContext(), "SelectTargetType");
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d1);
        bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d2);
        bitmapp = Bitmap.createBitmap(532, 532, Bitmap.Config.ARGB_8888);
        imgcanvas = new Canvas(bitmapp);
        PlayBackbitmap = Bitmap.createBitmap(532, 532, Bitmap.Config.ARGB_8888);
        PlayBackCanvas = new Canvas(PlayBackbitmap);
        TargetType = getResources().getStringArray(R.array.TargetType);
        SelectTargetType = RxSPTool.getInt(getContext(), "SelectTargetType");
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        path = new Path();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d1);
        bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d2);
        bitmapp = Bitmap.createBitmap(532, 532, Bitmap.Config.ARGB_8888);
        imgcanvas = new Canvas(bitmapp);
        PlayBackbitmap = Bitmap.createBitmap(532, 532, Bitmap.Config.ARGB_8888);
        PlayBackCanvas = new Canvas(PlayBackbitmap);
        TargetType = getResources().getStringArray(R.array.TargetType);
        SelectTargetType = RxSPTool.getInt(getContext(), "SelectTargetType");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Width = MeasureSpec.getSize(widthMeasureSpec);
//        height = MeasureSpec.getSize(heightMeasureSpec);
        height = 532;
        Width = 532;
        imgXYCalc = new ImgXYCalc(Width, height, getResources());
//        Log.e(TAG, "onMeasure: height"+height );
//        Log.e(TAG, "onMeasure: Width"+Width );
        bitmapp.setHeight(height);
        bitmapp.setWidth(Width);
        PlayBackbitmap.setWidth(Width);
        PlayBackbitmap.setHeight(height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        // 绘制 弹孔
//        RxLogTool.e("onDraw");
        paint.setStrokeWidth(3.0f);
        paint.setAntiAlias(true);
//        if (isShot||isReplay) {
//            paint.setColor(Color.RED);
//        } else {
        paint.setColor(Color.BLUE);
//        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
//        CornerPathEffect cornerPathEffect = new CornerPathEffect(200);
//        paint.setPathEffect(cornerPathEffect);
        int width = this.bitmap.getWidth() / 2;
        int height = this.bitmap.getHeight() / 2;
        int width1 = this.bitmap1.getWidth() / 2;
        int height1 = this.bitmap1.getHeight() / 2;

        //260  310为10环正中心

//        imgcanvas.drawBitmap(bitmap1, 260, 310, paint);
//        RxLogTool.e(TAG,PlayBack+"    "+PlayAllBack);
        if (!PlayBack) {
            if (!PlayAllBack) {
//                if (!shootLineDates.isEmpty()) {
//                    for (int i = currentIndex; i < shootLineDates.size(); i++) {
//                        int x = (int) shootLineDates.get(i).getX();
//                        int y = (int) shootLineDates.get(i).getY();
//                        if (i == 0) {
//                            path.moveTo(x, y);
//                        } else {
//                            if (shootLineDates.get(i - 1).getX() == -1) {
//                                if (shootLineDates.get(i).getX() != -1) {
//                                    path.moveTo(x, y);
//                                }
//                            } else {
//                                if (shootLineDates.get(i).getX() != -1) {
//                                    path.lineTo(x, y);
//                                }
//                            }
//                        }
//                    }
//                    currentIndex = shootLineDates.size();
//                    imgcanvas.drawPath(path, paint);
//                    path.reset();
//                    path.moveTo((int) shootLineDates.get(shootLineDates.size() - 1).getX(),
//                            (int) shootLineDates.get(shootLineDates.size() - 1).getY());
//                }

                if (!shootLineDates_.isEmpty()) {
                    for (int i = currentIndex; i < shootLineDates_.size(); i++) {
//                        Log.e(TAG, "onDraw: "+   shootLineDates_.get(i).getTime() );

                        int x = (int) shootLineDates_.get(i).getX();
                        int y = (int) shootLineDates_.get(i).getY();
                        if (i == 0) {
                            path.moveTo(x, y);
                        } else {
                            if (shootLineDates_.get(i - 1).getX() == -1) {
                                if (shootLineDates_.get(i).getX() != -1) {
                                    path.moveTo(x, y);
                                }
                            } else {
                                if (shootLineDates_.get(i).getX() != -1) {
//                                    Random random = new Random();
//                                    int top_X = shootLineDates_.get(i - 1).getX();
//                                    int top_y = shootLineDates_.get(i - 1).getY();
//                                    List<Integer> xxList = new ArrayList<>();
//                                    List<Integer> yyList = new Ar//                                    int xxx = x;rayList<>();
//                                    int yyy = y;
//                                    if (x > top_X) {
//                                        xxx = (x - top_X) / 3 + top_X;
//                                        for (int j = 0; j < 3; j++) {
//                                            int min = xxx - 2;
//                                            int max = xxx + 2;
//                                            int num = random.nextInt(max) % (max - min + 1) + min;
//                                            xxList.add(num);
//                                        }
//
//                                        if (y > top_y) {
//                                            yyy = (y - top_y) / 3 + top_y;
//                                            for (int j = 0; j < 3; j++) {
//                                                int min = yyy - 2;
//                                                int max = yyy + 2;
//                                                int num = random.nextInt(max) % (max - min + 1) + min;
//                                                yyList.add(num);
//                                            }
//                                        } else {
//                                            yyy = y - (top_y - y) / 3;
//                                            for (int j = 0; j < 3; j++) {
//                                                int min = yyy - 2;
//                                                int max = yyy + 2;
//                                                int num = random.nextInt(max) % (max - min + 1) + min;
//                                                yyList.add(num);
//                                            }
//                                        }
//                                    } else {
//                                        xxx = top_X - (top_X - x) / 3;
//                                        for (int j = 0; j < 3; j++) {
//                                            int min = xxx - 2;
//                                            int max = xxx + 2;
//                                            int num = random.nextInt(max) % (max - min + 1) + min;
//                                            xxList.add(num);
//                                        }
//                                        if (y > top_y) {
//                                            yyy = (y - top_y) / 3 + top_y;
//                                            for (int j = 0; j < 3; j++) {
//                                                int min = yyy - 2;
//                                                int max = yyy + 2;
//                                                int num = random.nextInt(max) % (max - min + 1) + min;
//                                                yyList.add(num);
//                                            }
//                                        } else {
//                                            yyy = y - (top_y - y) / 3;
//                                            for (int j = 0; j < 3; j++) {
//                                                int min = yyy - 2;
//                                                int max = yyy + 2;
//                                                int num = random.nextInt(max) % (max - min + 1) + min;
//                                                yyList.add(num);
//                                            }
//                                        }
//                                    }
//
//                                    for (int j = 0; j < xxList.size(); j++) {
//                                        path.lineTo(xxList.get(j), yyList.get(j));
//                                    }

                                    path.lineTo(x, y);

                                }
                            }
                        }
                    }
                    currentIndex = shootLineDates_.size();
                    imgcanvas.drawPath(path, paint);
                    path.reset();
                    path.moveTo((int) shootLineDates_.get(shootLineDates_.size() - 1).getX(),
                            (int) shootLineDates_.get(shootLineDates_.size() - 1).getY());
                }
                if (!shootDates.isEmpty()) {
//                    RxLogTool.e(TAG, SelectIndex);
                    if (SelectIndex == -1) {
                        if (shootDates.size() < 2) {
                            int xx = (int) shootDates.get(shootDates.size() - 1).getX();
                            int yy = (int) shootDates.get(shootDates.size() - 1).getY();
                            if (xx != -1) {
                                imgcanvas.drawBitmap(bitmap1, xx - width1, yy - height1, paint);
                            }
                            if (SelectLastIndex != -1) {
                                int xxx = (int) shootDates.get(SelectLastIndex).getX();
                                int yyy = (int) shootDates.get(SelectLastIndex).getY();
                                if (xxx != -1) {
                                    imgcanvas.drawBitmap(bitmap, xxx - width1, yyy - height1, paint);
                                }
                                SelectLastIndex = -1;
                            }
                        } else {

//                            RxLogTool.e(TAG, "总打靶数据" + shootDates.size());
                            for (int i = shootDates.size() - 1; i >= 0; i--) {
//                                RxLogTool.e(TAG, "循环的i" + i);
                                int x = (int) shootDates.get(i).getX();
                                int y = ((int) shootDates.get(i).getY());
                                if (x != -1) {
                                    if (i == shootDates.size() - 1) {
//                                        RxLogTool.e(TAG, "相同 花红色");
                                        imgcanvas.drawBitmap(bitmap1, x - width1, y - height1, paint);
                                    } else {
                                        imgcanvas.drawBitmap(bitmap, x - width, y - height, paint);
                                    }
                                }
                            }


//                            int x = (int) shootDates.get(shootDates.size() - 2).getX();
//                            int y = ((int) shootDates.get(shootDates.size() - 2).getY());
//                            int xx = (int) shootDates.get(shootDates.size() - 1).getX();
//                            int yy = ((int) shootDates.get(shootDates.size() - 1).getY());
//                            if (x != -1) {
//                                imgcanvas.drawBitmap(bitmap, x - width, y - height, paint);
//                            }
//                            if (xx != -1) {
//                                imgcanvas.drawBitmap(bitmap1, xx - width1, yy - height1, paint);
//                            }
                            if (SelectLastIndex != -1) {
                                int xxx = (int) shootDates.get(SelectLastIndex).getX();
                                int yyy = (int) shootDates.get(SelectLastIndex).getY();
                                if (xxx != -1) {
                                    imgcanvas.drawBitmap(bitmap, xxx - width1, yyy - height1, paint);
                                }
                                SelectLastIndex = -1;
                            }
                        }
                    } else {
                        if (shootDates.size() < 2) {
                            int xx = (int) shootDates.get(SelectIndex).getX();
                            int yy = ((int) shootDates.get(SelectIndex).getY());
                            if (xx != -1) {
                                imgcanvas.drawBitmap(bitmap1, xx - width1, yy - height1, paint);
                            }
                            SelectLastIndex = SelectIndex;
                        } else {
                            int x = (int) shootDates.get(shootDates.size() - 2).getX();
                            int y = (int) shootDates.get(shootDates.size() - 2).getY();
                            int xx = (int) shootDates.get(shootDates.size() - 1).getX();
                            int yy = ((int) shootDates.get(shootDates.size() - 1).getY());
                            if (x != -1) {
                                imgcanvas.drawBitmap(bitmap, x - width, y - height, paint);
                            }
                            if (xx != -1) {
                                imgcanvas.drawBitmap(bitmap, xx - width1, yy - height1, paint);
                            }
                            int xxx = (int) shootDates.get(SelectIndex).getX();
                            int yyy = (int) shootDates.get(SelectIndex).getY();
                            if (xxx != -1) {
                                imgcanvas.drawBitmap(bitmap1, xxx - width1, yyy - height1, paint);
                            }
                            if (SelectLastIndex != -1 && SelectLastIndex != SelectIndex) {
                                int xxxx = (int) shootDates.get(SelectLastIndex).getX();
                                int yyyy = (int) shootDates.get(SelectLastIndex).getY();
                                if (xxxx != -1) {
                                    imgcanvas.drawBitmap(bitmap, xxxx - width1, yyyy - height1, paint);
                                }
                            }
                            SelectLastIndex = SelectIndex;
                        }
                    }
                }
                canvas.drawBitmap(bitmapp, 0, 0, paint);
            } else {
                for (int i = 0; i < shootDates.size(); i++) {
                    int xx = (int) shootDates.get(i).getX();
                    int yy = (int) shootDates.get(i).getY();
                    if (xx != -1) {
                        imgcanvas.drawBitmap(bitmap, xx - width, yy - height, paint);
                    }
                }
                canvas.drawBitmap(bitmapp, 0, 0, paint);
                PlayAllBack = false;
                RxLogTool.e("playAllback");
                if (SelectIndex != -1) {
                    postInvalidate();
                }
            }
        } else {
            if (PlayBackImgIndex < (PlayBackEndPosition - 1)) {
                if (!shootLineDates.isEmpty()) {
                    int x = (int) shootLineDates.get(PlayBackImgIndex).getX();
                    int y = ((int) shootLineDates.get(PlayBackImgIndex).getY());
                    if (PlayBackImgIndex > 0) {
                        if (((int) shootLineDates.get(PlayBackImgIndex - 1).getX()) != -1) {
                            if (x != -1) {
                                path.lineTo(x, y);
                                PlayBackCanvas.drawPath(path, paint);
                            }
                        } else {
                            if (x != -1) {
                                path.moveTo(x, y);
                            }
                        }
                    } else {
                        if (x != -1) {
                            path.moveTo(x, y);
                        }
                    }
                }
                if (PlayBackImgIndex >= PlayBackPosition) {
                    int x = (int) shootLineDates.get(PlayBackPosition).getX();
                    int y = (int) shootLineDates.get(PlayBackPosition).getY();
                    if (x != -1) {
                        PlayBackCanvas.drawBitmap(bitmap1, x - width1, y - height1, paint);
                    }
                }
                canvas.drawBitmap(PlayBackbitmap, 0, 0, paint);
                PlayBackImgIndex++;
            } else {
                if (!shootLineDates.isEmpty()) {
                    int x = (int) shootLineDates.get(PlayBackImgIndex).getX();
                    int y = ((int) shootLineDates.get(PlayBackImgIndex).getY());
                    if (PlayBackImgIndex > 0) {
                        if (((int) shootLineDates.get(PlayBackImgIndex - 1).getX()) != -1) {
                            if (x != -1) {
                                path.lineTo(x, y);
                                PlayBackCanvas.drawPath(path, paint);
                            }
                        } else {
                            if (x != -1) {
                                path.moveTo(x, y);
                            }
                        }
                    } else {
                        if (x != -1) {
                            path.moveTo(x, y);
                        }
                    }
                }
                if (PlayBackImgIndex >= PlayBackPosition) {
                    int x = (int) shootLineDates.get(PlayBackPosition).getX();
                    int y = (int) shootLineDates.get(PlayBackPosition).getY();
                    if (x != -1) {
                        PlayBackCanvas.drawBitmap(bitmap1, x - width1, y - height1, paint);
                    }
                }
                canvas.drawBitmap(PlayBackbitmap, 0, 0, paint);
                PlayBackImgIndex++;
                PlayBack = false;
            }
        }
    }

    public void setShot(boolean b) {
        isShot = b;
    }

    public int[] setDate(ArrayList<Integer> shootingDates, boolean isHit) {
        int[] ints = new int[5];
        ints[0] = -1;
        ints[1] = -1;
        ints[2] = -1;
        ints[3] = -1;
        ints[4] = -1;
        if (shootLineDates.size() > 0) {
            shootLineDates.add(imgXYCalc.getXYimg(shootingDates, Width, height,
                    shootLineDates.get(shootLineDates.size() - 1).getX(),
                    shootLineDates.get(shootLineDates.size() - 1).getY()));
        } else {
            shootLineDates.add(imgXYCalc.getXYimg(shootingDates, Width, height,
                    -1,
                    -1));
        }
        //TODO 每次清空轨迹所以添加一个集合
//        if (isHit) {
//            shootLineDates_.clear();
//        }
        if (shootLineDates_.size() > 0) {
            shootLineDates_.add(imgXYCalc.getXYimg(shootingDates, Width, height,
                    shootLineDates_.get(shootLineDates_.size() - 1).getX(),
                    shootLineDates_.get(shootLineDates_.size() - 1).getY()));
        } else {
            shootLineDates_.add(imgXYCalc.getXYimg(shootingDates, Width, height,
                    -1,
                    -1));
        }

        Date date = new Date(shootLineDates.get(shootLineDates.size() - 1).getTime().getTime() - StartDate.getTime());
        String sdata = DateSFormat.format(date);
        float fdata = (float) (Integer.parseInt(sdata.substring(0, 2)) * 60 + Integer.parseInt(sdata.substring(3, 5)))
                + (((float) Integer.parseInt(sdata.substring(6))) / 1000.0f);
        if (fdata > 0) {
            lineChartList.add(new Entry(
                    fdata,
                    (float) shootLineDates.get(shootLineDates.size() - 1).getRingNumber()
            ));
        }

        if (isHit) {
            int AnaStartIndex = shootLineDates.size() - 1;
            shootDates.add(shootLineDates.get(shootLineDates.size() - 1));
            ArrayList<AimCalcData> analysis = new ArrayList<>();
            if (shootDates.size() > 1) {
                for (int i = shootLineDates.size() - 1; i >= 0; i--) {
                    if (shootLineDates.get(i).getTime().equals(shootDates.get(shootDates.size() - 2).getTime())) {
                        AnaStartIndex = i;
                    }
                }
                if (shootLineDates.size() - 1 - AnaStartIndex >= 10) {
                    AnaStartIndex = shootLineDates.size() - 11;
                } else {
                    AnaStartIndex = AnaStartIndex + 1;
                }
                for (int i = AnaStartIndex; i < shootLineDates.size(); i++) {
                    analysis.add(shootLineDates.get(i));
                }
                ints = AnalysisAchievement.AnalysisAchieve(analysis, shootDates,
                        imgXYCalc.getCentreX(), imgXYCalc.getCentreY(), imgXYCalc.getEveryPxEqMillimeter());

                shootDates.get(shootDates.size() - 1).setHoldingGunt(ints[0]);
                shootDates.get(shootDates.size() - 1).setAimFractions(ints[1]);
                shootDates.get(shootDates.size() - 1).setShotFractions(ints[2]);
                shootDates.get(shootDates.size() - 1).setAchieveFractions(ints[3]);
                shootDates.get(shootDates.size() - 1).setTotalFractions(ints[4]);
            } else {
                if (shootLineDates.size() >= 10) {
                    for (int i = shootLineDates.size() - 10; i < shootLineDates.size(); i++) {
                        analysis.add(shootLineDates.get(i));
                    }
                } else {
                    analysis.addAll(shootLineDates);
                }
                ints = AnalysisAchievement.AnalysisAchieve(analysis, shootDates,
                        imgXYCalc.getCentreX(), imgXYCalc.getCentreY(), imgXYCalc.getEveryPxEqMillimeter());

                shootDates.get(shootDates.size() - 1).setHoldingGunt(ints[0]);
                shootDates.get(shootDates.size() - 1).setAimFractions(ints[1]);
                shootDates.get(shootDates.size() - 1).setShotFractions(ints[2]);
                shootDates.get(shootDates.size() - 1).setAchieveFractions(ints[3]);
                shootDates.get(shootDates.size() - 1).setTotalFractions(ints[4]);
            }
            SelectIndex = -1;
        }
        return ints;
    }

    public void clear() {
        imgcanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        shootLineDates_.clear();
        currentIndex = 0;
        path.rewind();
        postInvalidate();
    }

    public void PlayBackTrack(int position) {
        PlayBack = true;
        PlayBackShootPostion = position;
        if (position > 0) {
            for (int i = 0; i < shootLineDates.size(); i++) {
                if (shootDates.get(position - 1).getTime().equals(shootLineDates.get(i).getTime())) {
                    PlayBackStartPosition = i + 1;
                }
                if (shootDates.get(position).getTime().equals(shootLineDates.get(i).getTime())) {
                    PlayBackPosition = i;
                    if ((i + 5) < shootLineDates.size()) {
                        PlayBackEndPosition = i + 6;
                    } else {
                        PlayBackEndPosition = shootLineDates.size();
                    }
                }
            }
        } else {
            PlayBackStartPosition = 0;
            for (int i = 0; i < shootLineDates.size(); i++) {
                if (shootDates.get(position).getTime().equals(shootLineDates.get(i).getTime())) {
                    PlayBackPosition = i;
                    if ((i + 5) < shootLineDates.size()) {
                        PlayBackEndPosition = i + 6;
                    } else {
                        PlayBackEndPosition = shootLineDates.size();
                    }
                }
            }
        }
        PlayBackImgIndex = PlayBackStartPosition;
        PlayBackChartIndex = PlayBackStartPosition;
        lineChartList.clear();
        PlayBackCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        path.reset();
        path.moveTo((int) shootLineDates.get(PlayBackStartPosition).getX(),
                (int) shootLineDates.get(PlayBackStartPosition).getY());
    }

    public boolean RunPlayBack() {
        boolean istrue = false;
        if (PlayBackChartIndex < PlayBackPosition) {
            Date date = new Date(shootLineDates.get(PlayBackPosition).getTime().getTime() - shootLineDates.get(PlayBackChartIndex).getTime().getTime());
            String sdata = DateSFormat.format(date);
            float fdata = (float) (Integer.parseInt(sdata.substring(0, 2)) * 60 + Integer.parseInt(sdata.substring(3, 5)))
                    + (((float) Integer.parseInt(sdata.substring(6))) / 1000.0f);
            lineChartList.add(new Entry(
                    -fdata,
                    (float) shootLineDates.get(PlayBackChartIndex).getRingNumber()
            ));
        } else if (PlayBackChartIndex == PlayBackPosition) {
            lineChartList.add(new Entry(
                    0.0f,
                    (float) shootLineDates.get(PlayBackChartIndex).getRingNumber()
            ));
        } else {
            Date date = new Date(shootLineDates.get(PlayBackChartIndex).getTime().getTime() - shootLineDates.get(PlayBackPosition).getTime().getTime());
            String sdata = DateSFormat.format(date);
            float fdata = (float) (Integer.parseInt(sdata.substring(0, 2)) * 60 + Integer.parseInt(sdata.substring(3, 5)))
                    + (((float) Integer.parseInt(sdata.substring(6))) / 1000.0f);
            lineChartList.add(new Entry(
                    fdata,
                    (float) shootLineDates.get(PlayBackChartIndex).getRingNumber()
            ));
        }

        postInvalidate();
        PlayBackChartIndex++;
        if (PlayBackChartIndex >= PlayBackEndPosition) {
            istrue = true;
        }
        return istrue;
    }

    public void PlayAllTrack(int position) {
        SelectIndex = position;
        PlayBack = false;
        PlayAllBack = true;
        PlayBackStartPosition = -1;
        PlayBackEndPosition = -1;
        PlayBackShootPostion = -1;
        PlayBackImgIndex = -1;
        PlayBackChartIndex = -1;
        int start = -1;
        int end = -1;
        int selectnum = -1;
        lineChartList.clear();
        if (SelectIndex == -1) {
            for (int i = 0; i < shootLineDates.size(); i++) {
                String sdata = DateSFormat.format(new Date(
                        shootLineDates.get(i).getTime().getTime() - StartDate.getTime()
                ));
                float fdata = (float) (Integer.parseInt(sdata.substring(0, 2)) * 60 + Integer.parseInt(sdata.substring(3, 5)))
                        + (((float) Integer.parseInt(sdata.substring(6))) / 1000.0f);
                lineChartList.add(new Entry(
                        fdata,
                        (float) shootLineDates.get(i).getRingNumber()
                ));
            }
        } else {
            if (shootDates.size() > 0) {
                if (SelectIndex != 0) {     //非中弹列表首个
                    if ((shootDates.size() - SelectIndex) > 1) {  //非中弹列表末尾
                        for (int i = 0; i < shootLineDates.size(); i++) {
                            if (shootLineDates.get(i).getTime().equals(shootDates.get(SelectIndex - 1).getTime())) {
                                start = i + 1;
                            }
                            if (shootLineDates.get(i).getTime().equals(shootDates.get(SelectIndex).getTime())) {
                                selectnum = i;
                                if ((i + 5) < shootLineDates.size()) {
                                    end = i + 5;
                                } else {
                                    end = shootLineDates.size();
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < shootLineDates.size(); i++) {
                            if (shootLineDates.get(i).getTime().equals(shootDates.get(SelectIndex - 1).getTime())) {
                                start = i + 1;
                            }
                            if (shootLineDates.get(i).getTime().equals(shootDates.get(SelectIndex).getTime())) {
                                selectnum = i;
                            }
                        }
                        end = shootLineDates.size();
                    }
                } else {
                    start = 0;
                    for (int i = 0; i < shootLineDates.size(); i++) {
                        if (shootLineDates.get(i).getTime().equals(shootDates.get(SelectIndex).getTime())) {
                            selectnum = i;
                            if ((i + 5) < shootLineDates.size()) {
                                end = i + 5;
                            } else {
                                end = shootLineDates.size();
                            }
                        }
                    }
                }
            }
            if (start != -1 && end != -1 && selectnum != -1) {
                Date date0 = shootDates.get(SelectIndex).getTime();
                for (int i = start; i < end; i++) {
                    if (i < selectnum) {      //-轴
                        String sdata = DateSFormat.format(new Date(
                                date0.getTime() - shootLineDates.get(i).getTime().getTime()
                        ));
                        float fdata = (float) (Integer.parseInt(sdata.substring(0, 2)) * 60 + Integer.parseInt(sdata.substring(3, 5)))
                                + (((float) Integer.parseInt(sdata.substring(6))) / 1000.0f);
                        fdata = 0.0f - fdata;
                        lineChartList.add(new Entry(
                                fdata,
                                (float) shootLineDates.get(i).getRingNumber()
                        ));
                    } else if (i > selectnum) {
                        String sdata = DateSFormat.format(new Date(
                                shootLineDates.get(i).getTime().getTime() - date0.getTime()
                        ));
                        float fdata = (float) (Integer.parseInt(sdata.substring(0, 2)) * 60 + Integer.parseInt(sdata.substring(3, 5)))
                                + (((float) Integer.parseInt(sdata.substring(6))) / 1000.0f);
                        lineChartList.add(new Entry(
                                fdata,
                                (float) shootLineDates.get(i).getRingNumber()
                        ));
                    } else {
                        lineChartList.add(new Entry(
                                0.0f,
                                (float) shootLineDates.get(i).getRingNumber()
                        ));
                    }
                }
            }
        }
        postInvalidate();
    }

    public void ClearData() {
        shootLineDates.clear();
        shootLineDates_.clear();
        shootDates.clear();
        lineChartList.clear();
        currentIndex = 0;
        SelectLastIndex = -1;
        SelectIndex = -1;
        bitmapp = Bitmap.createBitmap(Width, height, Bitmap.Config.ARGB_8888);
        imgcanvas = new Canvas(bitmapp);
        PlayBack = false;
        PlayBackStartPosition = -1;
        PlayBackEndPosition = -1;
        PlayBackShootPostion = -1;
        PlayBackImgIndex = -1;
        PlayBackChartIndex = -1;
        PlayBackCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        imgcanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        path.reset();
        postInvalidate();
    }

    public ArrayList<AimCalcData> getShootDates() {
        return shootDates;
    }

    public int getImgWidth() {
        return Width;
    }

    public int getImgHeight() {
        return height;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public boolean isPlayBack() {
        return PlayBack;
    }

    public int[] getSource(int select) {
        int[] source = new int[5];
        source[0] = shootDates.get(select).getHoldingGunt();
        source[1] = shootDates.get(select).getAimFractions();
        source[2] = shootDates.get(select).getShotFractions();
        source[3] = shootDates.get(select).getAchieveFractions();
        source[4] = shootDates.get(select).getTotalFractions();
        return source;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public ArrayList<Entry> getLineChartList() {
        return lineChartList;
    }

    public int[] setPersonName(String personName) {
        PersonName = personName;
        List<Target> list = targetDao.queryBuilder().where(TargetDao.Properties.TargetPerson.eq(PersonName)).build().list();
        Bureau = list.size() + 1;
        TotalShootNumbers = 0;
        ShotNums unique = shotNumsDao.queryBuilder().where(ShotNumsDao.Properties.TargetPerson.eq(PersonName)).build().unique();
        if (unique != null) {
            TotalShootNumbers = Integer.parseInt(unique.getTargetNums());
//            RxLogTool.e("当前总发数", unique.getTargetNums());
        }
//        for (int i = 0; i < list.size(); i++) {
//            TotalShootNumbers = TotalShootNumbers + aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(list.get(i).getId()),
//                    AimDao.Properties.Aim_ShotNum.notEq(-1)).build().list().size();
//        }
//        RxLogTool.e("当前总发数1", TotalShootNumbers);
        int[] val = new int[2];
        val[0] = Bureau;
        val[1] = TotalShootNumbers;
        return val;
    }

    public void SaveData(String s) {
        String Savedate = DateTimeFormat.format(StartDate);
        String Savedate_year = String.valueOf(Integer.parseInt(Savedate.substring(0, 4)));
        String Savedate_month = String.valueOf(Integer.parseInt(Savedate.substring(5, 7)));
        String Savedate_day = String.valueOf(Integer.parseInt(Savedate.substring(8, 10)));
        Target target = new Target(
                null,
                Savedate,
                Savedate_year,
                Savedate_month,
                Savedate_day,
                PersonName,
                TargetType[SelectTargetType],
                GunType,
                getTotalRingNum(),
                String.valueOf(Bureau)
        );
        targetDao.insert(target);
        // 先查询是否有相同名称的人
        ShotNums unique = shotNumsDao.queryBuilder().where(ShotNumsDao.Properties.TargetPerson.eq(PersonName)).build().unique();
        if (unique == null) {
            ShotNums shotNums = new ShotNums(null, PersonName, s);
            shotNumsDao.insert(shotNums);
        } else {
            unique.setTargetNums(s);
            shotNumsDao.update(unique);
        }

//        RxLogTool.e("打印当前时间", System.currentTimeMillis());
        targetDao.getDatabase().beginTransaction();// 手动设置transaction

        try {
            int shootIndexCount = 0;
            for (int i = 0; i < shootLineDates.size(); i++) {
                if (shootDates.size() <= 0) {
                    return;
                }
                if (shootDates.get(shootIndexCount).getTime().equals(shootLineDates.get(i).getTime())) {
                    Aim aim = new Aim(
                            null,
                            target.getId(),
                            i + 1,
                            DateTimeFormat.format(shootLineDates.get(i).getTime()),
                            shootLineDates.get(i).getAim_x(),
                            shootLineDates.get(i).getAim_y(),
                            shootLineDates.get(i).getRingNumber(),
                            shootLineDates.get(i).getXringNumber(),
                            shootLineDates.get(i).getYringNumber(),
                            shootLineDates.get(i).getDirection(),
                            shootIndexCount + 1,
                            shootDates.get(shootIndexCount).getHoldingGunt(),
                            shootDates.get(shootIndexCount).getAimFractions(),
                            shootDates.get(shootIndexCount).getShotFractions(),
                            shootDates.get(shootIndexCount).getAchieveFractions(),
                            shootDates.get(shootIndexCount).getTotalFractions()
                    );
                    aimDao.insert(aim);
                    if (shootIndexCount < shootDates.size() - 1) {
                        shootIndexCount++;
                    }
                } else {
                    Aim aim = new Aim(
                            null,
                            target.getId(),
                            i + 1,
                            DateTimeFormat.format(shootLineDates.get(i).getTime()),
                            shootLineDates.get(i).getAim_x(),
                            shootLineDates.get(i).getAim_y(),
                            shootLineDates.get(i).getRingNumber(),
                            shootLineDates.get(i).getXringNumber(),
                            shootLineDates.get(i).getYringNumber(),
                            shootLineDates.get(i).getDirection(),
                            -1,
                            0,
                            0,
                            0,
                            0,
                            0
                    );
                    aimDao.insert(aim);
                }
            }
            targetDao.getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            RxLogTool.e("数据库事务", Objects.requireNonNull(e.getMessage()));
            targetDao.getDatabase().endTransaction();
        }
        targetDao.getDatabase().endTransaction();
//        RxLogTool.e("打印当前时间结束", System.currentTimeMillis());
    }

    public String getPersonName() {
        return PersonName;
    }

    public Double getCurrentRingNum() {
        return shootDates.get(shootDates.size() - 1).getRingNumber();
    }

    public int getCurrentDirection() {
        return shootDates.get(shootDates.size() - 1).getDirection();
    }

    public String getTotalRingNum() {
        Double total = 0.0;
        for (int i = 0; i < shootDates.size(); i++) {
            total = total + shootDates.get(i).getRingNumber();
        }
        DecimalFormat df = new DecimalFormat("0.0");//设置保留位数
        String format = df.format(total);
        return format;
    }

    public int getGunType() {
        return GunType;
    }

    public void setGunType(int gunType) {
        GunType = gunType;
    }

    public int getSelectTargetType() {
        return SelectTargetType;
    }

    public void setSelectTargetType(int selectTargetType) {
        SelectTargetType = selectTargetType;
    }


    /**
     * 获取当前绘制轨迹的值
     *
     * @return
     */
    public AimCalcData getTrackValue() {
        AimCalcData aimCalcData = shootLineDates.get(shootLineDates.size() - 1);
        return aimCalcData;
    }
}
