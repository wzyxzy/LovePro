package com.wzy.lamanpro.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.HisDataAdapter;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.bean.ListBean;
import com.wzy.lamanpro.dao.HisDaoUtils;
import com.wzy.lamanpro.utils.ChartUtil;
import com.wzy.lamanpro.utils.PdfManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HisDetails extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private TextView title_name;
    private ListView allData;
    private Long id;
    private HisData hisData;
    private HisDataAdapter hisDataAdapter;
    private List<ListBean> listBeans;
    private LineChart lineChart;
    List<String> xDataList = new ArrayList<>();// x轴数据源
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源
    private ConstraintLayout view_pdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_details);
        initView();
        initData();
    }

    private void initData() {
        id = getIntent().getLongExtra("id", 0);
        hisData = new HisDaoUtils(this).queryUser(id);
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
//        allData.setOnClickListener(this);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        lineChart.setOnClickListener(this);
        view_pdf = (ConstraintLayout) findViewById(R.id.view_pdf);
        view_pdf.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "您确定要生成测试报告吗？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    String path = Environment.getExternalStorageDirectory() + File.separator + "拉曼测试报告-" + new Date().getTime() + ".pdf";
                                    PdfManager.makeViewEveryPdf(new View[]{view_pdf}, path);
                                    Toast.makeText(HisDetails.this, "文件已经保存在:" + path, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                break;
        }
    }
}
