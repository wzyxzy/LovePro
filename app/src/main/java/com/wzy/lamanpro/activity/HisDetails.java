package com.wzy.lamanpro.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.HisDataAdapter;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.bean.ListBean;
import com.wzy.lamanpro.dao.HisDaoUtils;
import com.wzy.lamanpro.utils.ChartUtil;

import java.util.ArrayList;
import java.util.List;

public class HisDetails extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private TextView title_name;
    private ListView allData;
    private String id;
    private HisData hisData;
    private HisDataAdapter hisDataAdapter;
    private List<ListBean> listBeans;
    private LineChart lineChart;
    List<String> xDataList = new ArrayList<>();// x轴数据源
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_details);
        initView();
        initData();
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        hisData = new HisDaoUtils(this).queryUser(Long.valueOf(id));
        title_name.setText(hisData.getName());
        listBeans = new ArrayList<>();
        listBeans.add(new ListBean("名字:", hisData.getName()));
        listBeans.add(new ListBean("日期:", hisData.getDate()));
        listBeans.add(new ListBean("测试者姓名:", hisData.getTestName()));
        listBeans.add(new ListBean("测试者账号:", hisData.getTestAccount()));
        listBeans.add(new ListBean("测试地点:", hisData.getTestLocal()));
        listBeans.add(new ListBean("测试积分时间:", hisData.getTestTime()));
        listBeans.add(new ListBean("测试积分功率:", hisData.getTestPower()));
        hisDataAdapter = new HisDataAdapter(listBeans, this, R.layout.item_his);
        allData.setAdapter(hisDataAdapter);
        String[] strings = hisData.getData().split(",");
        for (int i = 0; i < strings.length; i++) {
            xDataList.add(String.valueOf(i));
            yDataList.add(new Entry(Float.valueOf(strings[i]), i));
        }
        ChartUtil.showChart(this, lineChart, xDataList, yDataList, "波普图", "波长/时间", "mm");
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setOnClickListener(this);
        allData = (ListView) findViewById(R.id.allData);
        allData.setOnClickListener(this);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        lineChart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }
}
