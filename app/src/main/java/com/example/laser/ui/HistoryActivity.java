package com.example.laser.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.laser.R;
import com.example.laser.database.Aim;
import com.example.laser.database.AimDao;
import com.example.laser.database.Target;
import com.example.laser.database.TargetDao;
import com.example.laser.database.dao.DaoManager;
import com.example.laser.databinding.ActivityHistoryBinding;
import com.example.laser.message.HistoryMessage;
import com.example.laser.ui.adapter.HistoryCountAdapter;
import com.example.laser.ui.adapter.HistoryDataAdapter;
import com.example.laser.ui.adapter.HistoryPersonAdapter;
import com.example.laser.ui.entity.tree.BureauNode;
import com.example.laser.ui.entity.tree.FifthNode;
import com.example.laser.ui.entity.tree.FirstNode;
import com.example.laser.ui.entity.tree.FourthNode;
import com.example.laser.ui.entity.tree.PersonNode;
import com.example.laser.ui.entity.tree.SecondNode;
import com.example.laser.ui.entity.tree.ThirdNode;
import com.example.laser.utils.ExcelUtil;
import com.example.laser.utils.PrintAchievement;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.RxLogTool;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by  on 2021/5/4.
 */

public class HistoryActivity extends AppCompatActivity {
    private HistoryDataAdapter historyDataAdapter = new HistoryDataAdapter();
    private HistoryPersonAdapter historyPersonAdapter = new HistoryPersonAdapter();
    private HistoryCountAdapter historyCountAdapter;
    private ActivityHistoryBinding activityHistoryBinding;
    private PrintAchievement printAchievement;
    private TargetDao targetDao;
    private AimDao aimDao;
    private Target CurrentTarget = null;
    private List<Aim> CurrentAims;
    private List<Target> bureaus;
    private String DateAndPerson = "日期";
    private String PersonName = "";
    private String Bureaus = "";

    private LineData lineRTData, lineRxy_XData, lineRxy_YData;
    private LineDataSet lineRTDataSet, lineRxy_XDataSet, lineRxy_YDataSet;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setNavigationBarVisible(this, true);
        super.onCreate(savedInstanceState);
        RxActivityTool.addActivity(this);
        activityHistoryBinding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        CurrentAims = new ArrayList<>();
        targetDao = DaoManager.getTargetDao();
        aimDao = DaoManager.getAimDao();
        printAchievement = new PrintAchievement(getResources(), this);
        bureaus = new ArrayList<>();
        inDateRecycler();
        initCountRecycler();
        initView();
        int[] intstest = new int[5];
        intstest[0] = 0;
        intstest[1] = 0;
        intstest[2] = 0;
        intstest[3] = 0;
        intstest[4] = 0;
        RefushProgress(intstest);
        initLineChart();
    }


    /**
     * 测试成绩分析
     */
    private void RefushProgress(int[] source) {
        activityHistoryBinding.historyProgressHoldingGuntBar.setAnimProgress(source[0], "据枪\n");
//        activityHistoryBinding.historyProgressHoldingGuntBar.setmText( + activityHistoryBinding.historyProgressHoldingGuntBar.getmProgress() + "%");
//        activityHistoryBinding.historyProgressHoldingGuntBar.postInvalidate();

        activityHistoryBinding.historyProgressAimBar.setAnimProgress(source[1], "瞄准\n");
//        activityHistoryBinding.historyProgressAimBar.setmText("瞄准\n" + activityHistoryBinding.historyProgressAimBar.getmProgress() + "%");
//        activityHistoryBinding.historyProgressAimBar.postInvalidate();

        activityHistoryBinding.historyProgressFiringBar.setAnimProgress(source[2], "击发\n");
//        activityHistoryBinding.historyProgressFiringBar.setmText("击发\n" + activityHistoryBinding.historyProgressFiringBar.getmProgress() + "%");
//        activityHistoryBinding.historyProgressFiringBar.postInvalidate();

        activityHistoryBinding.historyProgressAchievementGuntBar.setAnimProgress(source[3], "成绩\n");
//        activityHistoryBinding.historyProgressAchievementGuntBar.setmText("成绩\n" + activityHistoryBinding.historyProgressAchievementGuntBar.getmProgress() + "%");
//        activityHistoryBinding.historyProgressAchievementGuntBar.postInvalidate();

        activityHistoryBinding.historyProgressTotalityBar.setAnimProgress(source[4], "总体\n");
//        activityHistoryBinding.historyProgressTotalityBar.setmText("总体\n" + activityHistoryBinding.historyProgressTotalityBar.getmProgress() + "%");
//        activityHistoryBinding.historyProgressTotalityBar.postInvalidate();
    }


    /**
     * 初始化显示及按键监听
     */
    private void initView() {
        activityHistoryBinding.historyRtChart.setVisibility(View.GONE);
        activityHistoryBinding.historyRxyLinear.setVisibility(View.GONE);
        activityHistoryBinding.historyDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitDate();
            }
        });
        activityHistoryBinding.historyPersonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitPerson();
            }
        });
        activityHistoryBinding.historyLastShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LastData();
            }
        });
        activityHistoryBinding.historyNextShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextData();
            }
        });
        activityHistoryBinding.historyMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityHistoryBinding.historyImage.setVisibility(View.VISIBLE);
                activityHistoryBinding.historyRtChart.setVisibility(View.GONE);
                activityHistoryBinding.historyRxyLinear.setVisibility(View.GONE);
                activityHistoryBinding.historyMove.setBackground(getDrawable(R.drawable.data));
                activityHistoryBinding.historyRt.setBackground(getDrawable(R.drawable.charts));
                activityHistoryBinding.historyRxy.setBackground(getDrawable(R.drawable.charts));
                activityHistoryBinding.historyMoveImage.setImageResource(R.mipmap.icon_move);
                activityHistoryBinding.historyRtImage.setImageResource(R.mipmap.icon_rt_s);
                activityHistoryBinding.historyRxyImage.setImageResource(R.mipmap.icon_txy_s);
            }
        });
        activityHistoryBinding.historyRt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityHistoryBinding.historyImage.setVisibility(View.GONE);
                activityHistoryBinding.historyRtChart.setVisibility(View.VISIBLE);
                activityHistoryBinding.historyRxyLinear.setVisibility(View.GONE);
                activityHistoryBinding.historyMove.setBackground(getDrawable(R.drawable.charts));
                activityHistoryBinding.historyRt.setBackground(getDrawable(R.drawable.data));
                activityHistoryBinding.historyRxy.setBackground(getDrawable(R.drawable.charts));
                activityHistoryBinding.historyMoveImage.setImageResource(R.mipmap.icon_move_s);
                activityHistoryBinding.historyRtImage.setImageResource(R.mipmap.icon_rt);
                activityHistoryBinding.historyRxyImage.setImageResource(R.mipmap.icon_txy_s);
            }
        });
        activityHistoryBinding.historyRxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityHistoryBinding.historyImage.setVisibility(View.GONE);
                activityHistoryBinding.historyRtChart.setVisibility(View.GONE);
                activityHistoryBinding.historyRxyLinear.setVisibility(View.VISIBLE);
                activityHistoryBinding.historyMove.setBackground(getDrawable(R.drawable.charts));
                activityHistoryBinding.historyRt.setBackground(getDrawable(R.drawable.charts));
                activityHistoryBinding.historyRxy.setBackground(getDrawable(R.drawable.data));
                activityHistoryBinding.historyMoveImage.setImageResource(R.mipmap.icon_move_s);
                activityHistoryBinding.historyRtImage.setImageResource(R.mipmap.icon_rt_s);
                activityHistoryBinding.historyRxyImage.setImageResource(R.mipmap.icon_txy);
            }
        });
        activityHistoryBinding.historyLastBureaus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PersonName.equals("") && !Bureaus.equals("")) {
                    InitPerson();
                    historyPersonAdapter.LastBureaus(PersonName, Bureaus);
                }
            }
        });
        activityHistoryBinding.historyNextBureaus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PersonName.equals("") && !Bureaus.equals("")) {
                    InitPerson();
                    historyPersonAdapter.NextBureaus(PersonName, Bureaus);
                }
            }
        });
        activityHistoryBinding.historyReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                RxActivityTool.skipActivity(HistoryActivity.this, MainActivity.class);
            }
        });

        activityHistoryBinding.historyPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (historyCountAdapter.getData().size() > 0) {
                    printAchievement.PrintImg(PersonName, Bureaus);
                }
            }
        });
    }

    /**
     * 点击日期条目时 刷新该 adapter
     */
    private void initCountRecycler() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        activityHistoryBinding.historyCount.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        historyCountAdapter = new HistoryCountAdapter(R.layout.history_list_item, activityHistoryBinding.historyImage.getAims());
        activityHistoryBinding.historyCount.setAdapter(historyCountAdapter);
        historyCountAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (historyCountAdapter.getSelectIndex() != position) {
                    historyCountAdapter.setSelectIndex(position);
                    historyCountAdapter.notifyDataSetChanged();
                    SelectData(position);
                }
            }
        });
    }

    /**
     * 初始化折线图
     */
    private void initLineChart() {
        lineRTDataSet = new LineDataSet(activityHistoryBinding.historyImage.getRTchartEntrys(), "实际中靶瞄准环数");
        lineRTDataSet.setCircleColor(Color.parseColor("#67BCFF"));    //设置点的颜色
        lineRTDataSet.setColor(Color.WHITE);          //设置线的颜色
        lineRTDataSet.setDrawFilled(true);
        lineRTDataSet.setDrawValues(false);
        lineRTDataSet.setDrawCircles(false);                                     //设置是否绘制点，默认是true
        lineRTDataSet.setMode(LineDataSet.Mode.LINEAR);                          //设置Mode为直线，这也是默认的Mode
        lineRTData = new LineData(lineRTDataSet);
        activityHistoryBinding.historyRtChart.setData(lineRTData);
        activityHistoryBinding.historyRtChart.setDrawBorders(false);
        activityHistoryBinding.historyRtChart.getDescription().setEnabled(true);
        activityHistoryBinding.historyRtChart.getDescription().setText("RT折线图");
        activityHistoryBinding.historyRtChart.getDescription().setTextColor(R.color.white);
        activityHistoryBinding.historyRtChart.getAxisRight().setEnabled(false);
        activityHistoryBinding.historyRtChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        activityHistoryBinding.historyRtChart.postInvalidate();

        lineRxy_XDataSet = new LineDataSet(activityHistoryBinding.historyImage.getRXYchartXEntrys(), "X轴瞄准对应环数");
        lineRxy_XDataSet.setCircleColor(Color.parseColor("#67BCFF"));    //设置点的颜色
        lineRxy_XDataSet.setColor(Color.WHITE);          //设置线的颜色
        lineRxy_XDataSet.setDrawFilled(true);
        lineRxy_XDataSet.setDrawValues(false);
        lineRxy_XDataSet.setDrawCircles(false);                                     //设置是否绘制点，默认是true
        lineRxy_XDataSet.setMode(LineDataSet.Mode.LINEAR);                          //设置Mode为直线，这也是默认的Mode
        lineRxy_XData = new LineData(lineRxy_XDataSet);
        activityHistoryBinding.historyRxyChartX.setData(lineRxy_XData);
        activityHistoryBinding.historyRxyChartX.setDrawBorders(false);
        activityHistoryBinding.historyRxyChartX.getDescription().setEnabled(true);
        activityHistoryBinding.historyRxyChartX.getDescription().setText("TXY-X折线图");
        activityHistoryBinding.historyRxyChartX.getDescription().setTextColor(R.color.white);
        activityHistoryBinding.historyRxyChartX.getAxisRight().setEnabled(false);
        activityHistoryBinding.historyRxyChartX.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        activityHistoryBinding.historyRxyChartX.postInvalidate();

        lineRxy_YDataSet = new LineDataSet(activityHistoryBinding.historyImage.getRXYchartYEntrys(), "Y轴瞄准对应环数");
        lineRxy_YDataSet.setCircleColor(Color.parseColor("#67BCFF"));    //设置点的颜色
        lineRxy_YDataSet.setColor(Color.WHITE);          //设置线的颜色
        lineRxy_YDataSet.setDrawFilled(true);
        lineRxy_YDataSet.setDrawValues(false);
        lineRxy_YDataSet.setDrawCircles(false);                                     //设置是否绘制点，默认是true
        lineRxy_YDataSet.setMode(LineDataSet.Mode.LINEAR);                          //设置Mode为直线，这也是默认的Mode
        lineRxy_YData = new LineData(lineRxy_YDataSet);
        activityHistoryBinding.historyRxyChartY.setData(lineRxy_YData);
        activityHistoryBinding.historyRxyChartY.setDrawBorders(false);
        activityHistoryBinding.historyRxyChartY.getDescription().setEnabled(true);
        activityHistoryBinding.historyRxyChartY.getDescription().setText("TXY-Y折线图");
        activityHistoryBinding.historyRxyChartY.getDescription().setTextColor(R.color.white);
        activityHistoryBinding.historyRxyChartY.getAxisRight().setEnabled(false);
        activityHistoryBinding.historyRxyChartY.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        activityHistoryBinding.historyRxyChartY.postInvalidate();
    }

    /**
     * 刷新所有折线图
     */
    private void RefushLineChart() {
        lineRTDataSet.notifyDataSetChanged();
        lineRTData.notifyDataChanged();
        activityHistoryBinding.historyRtChart.setData(lineRTData);
        activityHistoryBinding.historyRtChart.postInvalidate();

        lineRxy_XDataSet.notifyDataSetChanged();
        lineRxy_XData.notifyDataChanged();
        activityHistoryBinding.historyRxyChartX.setData(lineRxy_XData);
        activityHistoryBinding.historyRxyChartX.postInvalidate();

        lineRxy_YDataSet.notifyDataSetChanged();
        lineRxy_YData.notifyDataChanged();
        activityHistoryBinding.historyRxyChartY.setData(lineRxy_YData);
        activityHistoryBinding.historyRxyChartY.postInvalidate();
    }

    private void InitDate() {
        RxLogTool.e("date");
        activityHistoryBinding.historyDateText.setBackground(getDrawable(R.drawable.data));
        activityHistoryBinding.historyPersonText.setBackground(getDrawable(R.drawable.charts));
        DateAndPerson = "日期";
        inDateRecycler();
    }

    private void InitPerson() {
        RxLogTool.e("person");
        activityHistoryBinding.historyDateText.setBackground(getDrawable(R.drawable.charts));
        activityHistoryBinding.historyPersonText.setBackground(getDrawable(R.drawable.data));
        DateAndPerson = "人员";
        inPersonRecycler();
    }

    /**
     * 打靶日期记录 初始化
     */
    private void inDateRecycler() {
        activityHistoryBinding.historyData.setLayoutManager(new LinearLayoutManager(this));
        activityHistoryBinding.historyData.setAdapter(historyDataAdapter);
        historyDataAdapter.setList(getDateEntity());
    }

    /**
     * 打靶人员记录 初始化
     */
    private void inPersonRecycler() {
        activityHistoryBinding.historyData.setLayoutManager(new LinearLayoutManager(this));
        activityHistoryBinding.historyData.setAdapter(historyPersonAdapter);
        historyPersonAdapter.setList(getPersonEntity());
    }

    /**
     * EventBus接收数据
     *
     * @param historyMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(HistoryMessage historyMessage) {
        CurrentTarget = null;
        String personname = historyMessage.getPerson();
        String bureausstr = historyMessage.getBureau();
        PersonName = personname;
        Bureaus = bureausstr;
        activityHistoryBinding.historyPersonName.setText(personname);
        activityHistoryBinding.historyBureaus.setText(bureausstr + "局");
        CurrentTarget = targetDao.queryBuilder().where(TargetDao.Properties.TargetPerson.eq(personname),
                TargetDao.Properties.TargetBureauId.eq(bureausstr)).build().unique();
        if (CurrentTarget != null) {
            activityHistoryBinding.historyHitTime.setText("打靶时间: " + CurrentTarget.getTargetDate());
            activityHistoryBinding.historyTotalRingNum.setText("总环数: " + String.format("%.1f", Double.parseDouble(CurrentTarget.getTargetAllRing())));
            RxLogTool.e("总环数: ", CurrentTarget.getTargetAllRing());
            initData(CurrentTarget.getId());
        }
    }

    /**
     * 选择局后初始化数据
     *
     * @param id Target表ID
     */
    private void initData(Long id) {
        int[] ints = activityHistoryBinding.historyImage.SetData(id);
        activityHistoryBinding.historyImage.startAnim();
        activityHistoryBinding.historyCurrentRingNum.setText("当前环数: " + activityHistoryBinding.historyImage.getRingNumber());
        RefushProgress(ints);
        historyCountAdapter.setSelectIndex(0);
        RefushLineChart();
    }

    /**
     * 按照日期查询数据库生成5级列表
     *
     * @return 返回日期5级列表
     */
    private List<BaseNode> getDateEntity() {
        List<BaseNode> listyear = new ArrayList<>();
        List<String> targets = DaoManager.listTargetYear();
        Collections.sort(targets, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i = 0;
                if (Integer.parseInt(o1) > Integer.parseInt(o2)) {
                    i = -1;
                }
                if (Integer.parseInt(o1) < Integer.parseInt(o2)) {
                    i = 1;
                }
                return i;
            }
        });
        for (int i = 0; i < targets.size(); i++) {
            List<BaseNode> secondMonthList = new ArrayList<>();
            List<String> Months = DaoManager.listTargetYearToMonth(targets.get(i));
            Collections.sort(Months, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int i = 0;
                    if (Integer.parseInt(o1) > Integer.parseInt(o2)) {
                        i = 1;
                    }
                    if (Integer.parseInt(o1) < Integer.parseInt(o2)) {
                        i = -1;
                    }
                    return i;
                }
            });
            for (int j = 0; j < Months.size(); j++) {
                List<BaseNode> thirdDayList = new ArrayList<>();
                List<String> Days = DaoManager.listTargetYearToMonthToDay(targets.get(i), Months.get(j));
                Collections.sort(Days, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int i = 0;
                        if (Integer.parseInt(o1) > Integer.parseInt(o2)) {
                            i = 1;
                        }
                        if (Integer.parseInt(o1) < Integer.parseInt(o2)) {
                            i = -1;
                        }
                        return i;
                    }
                });
                for (int k = 0; k < Days.size(); k++) {
                    List<BaseNode> fourthperson = new ArrayList<>();
                    List<String> persons = DaoManager.listTargetYearToMonthToDayToPerson(targets.get(i), Months.get(j), Days.get(k));
                    for (int l = 0; l < persons.size(); l++) {
                        List<BaseNode> fifthBureaus = new ArrayList<>();
                        bureaus.clear();
                        bureaus = targetDao.queryBuilder().where(
                                TargetDao.Properties.TargetDate_Year.eq(targets.get(i)),
                                TargetDao.Properties.TargetDate_Month.eq(Months.get(j)),
                                TargetDao.Properties.TargetDate_Day.eq(Days.get(k)),
                                TargetDao.Properties.TargetPerson.eq(persons.get(l))
                        ).build().list();
                        Collections.sort(bureaus, new Comparator<Target>() {
                            @Override
                            public int compare(Target o1, Target o2) {
                                int i = 0;
                                if (Integer.parseInt(o1.getTargetBureauId()) > Integer.parseInt(o2.getTargetBureauId())) {
                                    i = 1;
                                }
                                if (Integer.parseInt(o1.getTargetBureauId()) < Integer.parseInt(o2.getTargetBureauId())) {
                                    i = -1;
                                }
                                return i;
                            }
                        });
                        for (int m = 0; m < bureaus.size(); m++) {
                            FifthNode fifthbureaunode = new FifthNode(bureaus.get(m).getTargetBureauId(), persons.get(l));
                            fifthBureaus.add(fifthbureaunode);
                        }
                        FourthNode fourthpersonnode = new FourthNode(fifthBureaus, persons.get(l));
                        fourthperson.add(fourthpersonnode);
                    }
                    ThirdNode thirdday = new ThirdNode(fourthperson, Days.get(k) + "日");
                    thirdDayList.add(thirdday);
                }
                SecondNode secondmonth = new SecondNode(thirdDayList, Months.get(j) + "月");
                secondMonthList.add(secondmonth);
            }
            FirstNode entity = new FirstNode(secondMonthList, targets.get(i) + "年");
            listyear.add(entity);
        }
        return listyear;
    }

    /**
     * 按照人员查询数据库生成2级列表
     *
     * @return 返回人员2级列表
     */
    private List<BaseNode> getPersonEntity() {
        List<BaseNode> listperson = new ArrayList<>();
        List<String> persons = DaoManager.listPerson();
        for (int i = 0; i < persons.size(); i++) {
            List<BaseNode> listbureaus = new ArrayList<>();
            List<Target> bureaus = targetDao.queryBuilder().where(
                    TargetDao.Properties.TargetPerson.eq(persons.get(i))
            ).build().list();
            Collections.sort(bureaus, new Comparator<Target>() {
                @Override
                public int compare(Target o1, Target o2) {
                    int i = 0;
                    if (Integer.parseInt(o1.getTargetBureauId()) > Integer.parseInt(o2.getTargetBureauId())) {
                        i = 1;
                    }
                    if (Integer.parseInt(o1.getTargetBureauId()) < Integer.parseInt(o2.getTargetBureauId())) {
                        i = -1;
                    }
                    return i;
                }
            });
            for (int j = 0; j < bureaus.size(); j++) {
                BureauNode bureauNode = new BureauNode(bureaus.get(j).getTargetBureauId(), persons.get(i));
                listbureaus.add(bureauNode);
            }
            PersonNode personNode = new PersonNode(listbureaus, persons.get(i));
            listperson.add(personNode);
        }
        return listperson;
    }

    /**
     * 下一发
     */
    private void NextData() {
        int[] source = activityHistoryBinding.historyImage.NextData();
        historyCountAdapter.setSelectIndex(source[5]);
        activityHistoryBinding.historyCurrentRingNum.setText("当前环数: " + activityHistoryBinding.historyImage.getRingNumber());
        RefushLineChart();
        RefushProgress(source);
        activityHistoryBinding.historyImage.startAnim();
    }

    /**
     * 上一发
     */
    private void LastData() {
        int[] source = activityHistoryBinding.historyImage.LastData();
        historyCountAdapter.setSelectIndex(source[5]);
        activityHistoryBinding.historyCurrentRingNum.setText("当前环数: " + activityHistoryBinding.historyImage.getRingNumber());
        RefushLineChart();
        RefushProgress(source);
        activityHistoryBinding.historyImage.startAnim();
    }

    /**
     * 选择对应发数
     */
    private void SelectData(int select) {
        int[] source = activityHistoryBinding.historyImage.SelectData(select);
        historyCountAdapter.setSelectIndex(source[5]);
        activityHistoryBinding.historyCurrentRingNum.setText("当前环数: " + activityHistoryBinding.historyImage.getRingNumber());
        RefushLineChart();
        RefushProgress(source);
    }

    /**
     * 隐藏或显示 导航栏
     *
     * @param activity
     */
    public static void setNavigationBarVisible(Activity activity, boolean isHide) {
        if (isHide) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))//加上判断
            EventBus.getDefault().unregister(this);
    }


    /**
     * 导出excel
     */
    public void exportExcel(View view) {

        if (CurrentTarget == null) {
            return;
        }

        File file = new File(ExcelUtil.getSDPath() + "/excel");
        ExcelUtil.makeDir(file);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());//Calendar.getInstance().toString();

        String fileName = file.toString() + "/" + CurrentTarget.getTargetPerson() + CurrentTarget.getTargetDate() + "-" + time + ".xls";

//        Log.e("TAG", ExcelUtil.getSDPath() + "/excel");
//        Log.e("TAG1", fileName);

        ExcelUtil.initExcel(fileName, getTitles());
        ExcelUtil.writeObjListToExcel(getRecordData(), fileName, this);
    }

    /**
     * @return 获取到excel的头
     */
    private String[] getTitles() {
        String[] title = {"姓名", "日期", "打靶局ID", "子弹序号",
                "据枪", "瞄准", "击发", "成绩", "总体"};
        return title;
    }


    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     *
     * @return
     */
    private ArrayList<ArrayList<String>> getRecordData() {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        List<Aim> aims = activityHistoryBinding.historyImage.getAims();
        float holding_gun = 0f;
        float aim_f = 0f;
        float shot_f = 0f;
        float ach = 0f;
        float total = 0f;
        for (int i = 0; i < aims.size(); i++) {
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(CurrentTarget.getTargetPerson());
            beanList.add(aims.get(i).getAimTime());
            beanList.add(CurrentTarget.getTargetBureauId());
            beanList.add(String.valueOf(aims.get(i).getAim_ShotNum()));
            beanList.add(String.valueOf(aims.get(i).getHoldingGunt()));
            beanList.add(String.valueOf(aims.get(i).getAimFractions()));
            beanList.add(String.valueOf(aims.get(i).getShotFractions()));
            beanList.add(String.valueOf(aims.get(i).getAchieveFractions()));
            beanList.add(String.valueOf(aims.get(i).getTotalFractions()));

            holding_gun += aims.get(i).getHoldingGunt();
            aim_f += aims.get(i).getAimFractions();
            shot_f += aims.get(i).getShotFractions();
            ach += aims.get(i).getAchieveFractions();
            total += aims.get(i).getTotalFractions();
            recordList.add(beanList);
        }
        ArrayList<String> beanList1 = new ArrayList<String>();
        beanList1.add("平均成绩");
        beanList1.add("");
        beanList1.add("");
        beanList1.add("");
        beanList1.add(String.valueOf(holding_gun / aims.size()));
        beanList1.add(String.valueOf(aim_f / aims.size()));
        beanList1.add(String.valueOf(shot_f / aims.size()));
        beanList1.add(String.valueOf(ach / aims.size()));
        beanList1.add(String.valueOf(total / aims.size()));

        ArrayList<String> beanList2 = new ArrayList<String>();
        String s = dataCha(aims.get(0).getAimTime(), aims.get(aims.size() - 1).getAimTime());
        beanList2.add("总体用时");
        beanList2.add(s);
        recordList.add(beanList1);
        recordList.add(beanList2);
        return recordList;
    }


    private String dataCha(String data1, String data2) {

        long time = 0L;
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            Date parse = dateFormat.parse(data1);
            Date parse1 = dateFormat.parse(data2);

            time = Math.abs(parse1.getTime() - parse.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffSeconds = time / 1000 % 60;
        long diffMinutes = time / (60 * 1000) % 60;
        long diffHours = time / (60 * 60 * 1000) % 24;
        return diffHours+"时"+diffMinutes+"分"+diffSeconds+"秒";
    }
}
