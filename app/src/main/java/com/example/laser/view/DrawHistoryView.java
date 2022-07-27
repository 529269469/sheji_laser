package com.example.laser.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.laser.R;
import com.example.laser.database.Aim;
import com.example.laser.database.AimDao;
import com.example.laser.database.dao.DaoManager;
import com.example.laser.ui.data.ImgXYCalc;
import com.github.mikephil.charting.data.Entry;
import com.vondear.rxtool.RxLogTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by  on 2021/7/14 16:38.
 */
public class DrawHistoryView extends AppCompatImageView {
    private Paint paint;
    private Bitmap bitmap;
    private Path path;
    private int Width = 0;
    private int height = 0;
    private ImgXYCalc imgXYCalc;
    private AimDao aimDao = DaoManager.getAimDao();
    private List<Aim> aims = new ArrayList<>();
    private List<Aim> drawaims = new ArrayList<>();
    private ArrayList<Entry> RTchartEntrys = new ArrayList<>();
    private ArrayList<Entry> RXYchartXEntrys = new ArrayList<>();
    private ArrayList<Entry> RXYchartYEntrys = new ArrayList<>();

    private int SelectIndex = -1;

    private int i = 0;

    // TODO 记录当前绘制的线条时间
    private String mTime;
    //TODO 是否绘制过点
    private boolean isPoint = false;

    public DrawHistoryView(Context context) {
        super(context);
        paint = new Paint();
        path = new Path();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d2);
    }

    public DrawHistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d2);
    }

    public DrawHistoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        path = new Path();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.d2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        imgXYCalc = new ImgXYCalc(Width, height, getResources());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(2.0f);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        int width = this.bitmap.getWidth() / 2;
        int height = this.bitmap.getHeight() / 2;
//        path.reset();

        if (drawaims.size() <= 0) {
            return;
        }

//        for (int i = 0; i < drawaims.size(); i++) {
        if (i == 0) {
            path.moveTo(imgXYCalc.getCalcX(drawaims.get(i).getAim_X()),
                    imgXYCalc.getCalcY(drawaims.get(i).getAim_Y()));
            mTime = drawaims.get(i).getAimTime();
        } else {
            if (i >= drawaims.size()) {
                return;
            }
            if (drawaims.get(i).getAim_X() != -1.0) {
                if (drawaims.get(i - 1).getAim_X() != -1.0) {
                    path.lineTo(imgXYCalc.getCalcX(drawaims.get(i).getAim_X()),
                            imgXYCalc.getCalcY(drawaims.get(i).getAim_Y()));
                } else {
                    path.moveTo(imgXYCalc.getCalcX(drawaims.get(i).getAim_X()),
                            imgXYCalc.getCalcY(drawaims.get(i).getAim_Y()));
                }
                mTime = drawaims.get(i).getAimTime();
            }
//            }
            if (drawaims.size() > 0) {
                canvas.drawPath(path, paint);
            }

            if (SelectIndex != -1) {
                if (!isPoint) {
                    String aimTime = aims.get(SelectIndex).getAimTime();
                    canvas.drawBitmap(bitmap,
                            imgXYCalc.getCalcX(aims.get(SelectIndex).getAim_X()) - width,
                            imgXYCalc.getCalcY(aims.get(SelectIndex).getAim_Y()) - height,
                            paint);
                }
            }
        }
    }


    public int[] SetData(Long id) {
        path.reset();
        int[] ints = new int[5];
        aims.clear();
        List<Aim> list = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(id),
                AimDao.Properties.Aim_ShotNum.notEq(-1)).build().list();
        Collections.sort(list, new Comparator<Aim>() {
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
        aims.addAll(list);
        SelectIndex = 0;
        drawaims.clear();
        drawaims = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(id),
                AimDao.Properties.AimCount.gt(0),
                AimDao.Properties.AimCount.le(aims.get(0).getAimCount() + 5)).build().list();
        Collections.sort(drawaims, new Comparator<Aim>() {
            @Override
            public int compare(Aim o1, Aim o2) {
                int i = 0;
                if (o1.getAimCount() > o2.getAimCount()) {
                    i = 1;
                }
                if (o1.getAimCount() < o2.getAimCount()) {
                    i = -1;
                }
                return i;
            }
        });
        RTchartEntrys.clear();
        RXYchartXEntrys.clear();
        RXYchartYEntrys.clear();
        for (int i = 0; i < drawaims.size(); i++) {
            float time = ((float) (drawaims.get(i).getAimCount() - aims.get(SelectIndex).getAimCount()) * 0.1f);
            RTchartEntrys.add(new Entry(
                    time,
                    (float) drawaims.get(i).getAim_RingNumber()
            ));
            RXYchartXEntrys.add(new Entry(
                    time,
                    (float) drawaims.get(i).getX_RingNumber()
            ));
            RXYchartYEntrys.add(new Entry(
                    time,
                    (float) drawaims.get(i).getY_RingNumber()
            ));
        }
        postInvalidate();
        ints[0] = aims.get(0).getHoldingGunt();
        ints[1] = aims.get(0).getAimFractions();
        ints[2] = aims.get(0).getShotFractions();
        ints[3] = aims.get(0).getAchieveFractions();
        ints[4] = aims.get(0).getTotalFractions();
        return ints;
    }


    public List<Aim> getAims() {
        return aims;
    }

    public int[] NextData() {
        path.reset();
        int[] source = new int[6];
        if (SelectIndex < (aims.size() - 1)) {
            SelectIndex++;
            drawaims.clear();
            drawaims = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(aims.get(SelectIndex).getTargetId()),
                    AimDao.Properties.AimCount.gt(aims.get(SelectIndex - 1).getAimCount()),
                    AimDao.Properties.AimCount.le(aims.get(SelectIndex).getAimCount() + 5)).build().list();
            Collections.sort(drawaims, new Comparator<Aim>() {
                @Override
                public int compare(Aim o1, Aim o2) {
                    int i = 0;
                    if (o1.getAimCount() > o2.getAimCount()) {
                        i = 1;
                    }
                    if (o1.getAimCount() < o2.getAimCount()) {
                        i = -1;
                    }
                    return i;
                }
            });

            RTchartEntrys.clear();
            RXYchartXEntrys.clear();
            RXYchartYEntrys.clear();
            for (int i = 0; i < drawaims.size(); i++) {
                float time = ((float) (drawaims.get(i).getAimCount() - aims.get(SelectIndex).getAimCount()) * 0.1f);
                RTchartEntrys.add(new Entry(
                        time,
                        (float) drawaims.get(i).getAim_RingNumber()
                ));
                RXYchartXEntrys.add(new Entry(
                        time,
                        (float) drawaims.get(i).getX_RingNumber()
                ));
                RXYchartYEntrys.add(new Entry(
                        time,
                        (float) drawaims.get(i).getY_RingNumber()
                ));
            }

            postInvalidate();
        }
        source[0] = aims.get(SelectIndex).getHoldingGunt();
        source[1] = aims.get(SelectIndex).getAimFractions();
        source[2] = aims.get(SelectIndex).getShotFractions();
        source[3] = aims.get(SelectIndex).getAchieveFractions();
        source[4] = aims.get(SelectIndex).getTotalFractions();
        source[5] = SelectIndex;

        return source;
    }

    public int[] LastData() {
        path.reset();
        int[] source = new int[6];
        if (SelectIndex > 0) {
            SelectIndex--;
            drawaims.clear();
            if (SelectIndex > 0) {
                drawaims = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(aims.get(SelectIndex).getTargetId()),
                        AimDao.Properties.AimCount.gt(aims.get(SelectIndex - 1).getAimCount()),
                        AimDao.Properties.AimCount.le(aims.get(SelectIndex).getAimCount() + 5)).build().list();
            } else {
                drawaims = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(aims.get(SelectIndex).getTargetId()),
                        AimDao.Properties.AimCount.ge(0),
                        AimDao.Properties.AimCount.le(aims.get(SelectIndex).getAimCount() + 5)).build().list();
            }
            Collections.sort(drawaims, new Comparator<Aim>() {
                @Override
                public int compare(Aim o1, Aim o2) {
                    int i = 0;
                    if (o1.getAimCount() > o2.getAimCount()) {
                        i = 1;
                    }
                    if (o1.getAimCount() < o2.getAimCount()) {
                        i = -1;
                    }
                    return i;
                }
            });

            RTchartEntrys.clear();
            RXYchartXEntrys.clear();
            RXYchartYEntrys.clear();
            for (int i = 0; i < drawaims.size(); i++) {
                float time = ((float) (drawaims.get(i).getAimCount() - aims.get(SelectIndex).getAimCount()) * 0.1f);
                RTchartEntrys.add(new Entry(
                        time,
                        (float) drawaims.get(i).getAim_RingNumber()
                ));
                RXYchartXEntrys.add(new Entry(
                        time,
                        (float) drawaims.get(i).getX_RingNumber()
                ));
                RXYchartYEntrys.add(new Entry(
                        time,
                        (float) drawaims.get(i).getY_RingNumber()
                ));
            }

            postInvalidate();
        }
        source[0] = aims.get(SelectIndex).getHoldingGunt();
        source[1] = aims.get(SelectIndex).getAimFractions();
        source[2] = aims.get(SelectIndex).getShotFractions();
        source[3] = aims.get(SelectIndex).getAchieveFractions();
        source[4] = aims.get(SelectIndex).getTotalFractions();
        source[5] = SelectIndex;
        return source;
    }

    public int[] SelectData(int select) {
        int[] source = new int[6];
        SelectIndex = select;
        drawaims.clear();
        if (SelectIndex > 0) {
            drawaims = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(aims.get(SelectIndex).getTargetId()),
                    AimDao.Properties.AimCount.gt(aims.get(SelectIndex - 1).getAimCount()),
                    AimDao.Properties.AimCount.le(aims.get(SelectIndex).getAimCount() + 5)).build().list();
        } else {
            drawaims = aimDao.queryBuilder().where(AimDao.Properties.TargetId.eq(aims.get(SelectIndex).getTargetId()),
                    AimDao.Properties.AimCount.ge(0),
                    AimDao.Properties.AimCount.le(aims.get(SelectIndex).getAimCount() + 5)).build().list();
        }
        Collections.sort(drawaims, new Comparator<Aim>() {
            @Override
            public int compare(Aim o1, Aim o2) {
                int i = 0;
                if (o1.getAimCount() > o2.getAimCount()) {
                    i = 1;
                }
                if (o1.getAimCount() < o2.getAimCount()) {
                    i = -1;
                }
                return i;
            }
        });

        RTchartEntrys.clear();
        RXYchartXEntrys.clear();
        RXYchartYEntrys.clear();
        for (int i = 0; i < drawaims.size(); i++) {
            float time = ((float) (drawaims.get(i).getAimCount() - aims.get(SelectIndex).getAimCount()) * 0.1f);
            RTchartEntrys.add(new Entry(
                    time,
                    (float) drawaims.get(i).getAim_RingNumber()
            ));
            RXYchartXEntrys.add(new Entry(
                    time,
                    (float) drawaims.get(i).getX_RingNumber()
            ));
            RXYchartYEntrys.add(new Entry(
                    time,
                    (float) drawaims.get(i).getY_RingNumber()
            ));
        }

        postInvalidate();
        source[0] = aims.get(SelectIndex).getHoldingGunt();
        source[1] = aims.get(SelectIndex).getAimFractions();
        source[2] = aims.get(SelectIndex).getShotFractions();
        source[3] = aims.get(SelectIndex).getAchieveFractions();
        source[4] = aims.get(SelectIndex).getTotalFractions();
        source[5] = SelectIndex;
        return source;
    }

    public double getRingNumber() {
        return aims.get(SelectIndex).getAim_RingNumber();
    }

    public int getSelectIndex() {
        return SelectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        SelectIndex = selectIndex;
    }

    public ArrayList<Entry> getRTchartEntrys() {
        return RTchartEntrys;
    }

    public ArrayList<Entry> getRXYchartXEntrys() {
        return RXYchartXEntrys;
    }

    public ArrayList<Entry> getRXYchartYEntrys() {
        return RXYchartYEntrys;
    }


    public void startAnim() {
        RxLogTool.e("drawaims", drawaims.size());
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, drawaims.size());
        //动画间隔(帧率)
        valueAnimator.setDuration(1000L);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //更新绘制下一帧
                int distance = (int) animation.getAnimatedValue();
                if (distance != drawaims.size()) {
                    i = distance;
                }
                invalidate();
            }
        });
        valueAnimator.start();
    }
}
