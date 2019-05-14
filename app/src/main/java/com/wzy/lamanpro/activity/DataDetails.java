package com.wzy.lamanpro.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.DataDetailAdapter;
import com.wzy.lamanpro.adapter.HisDataAdapter;
import com.wzy.lamanpro.bean.ListBean;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.dao.DataDaoUtils;
import com.wzy.lamanpro.dao.HisDaoUtils;
import com.wzy.lamanpro.dao.UserDaoUtils;
import com.wzy.lamanpro.utils.ChartUtil;

import java.util.ArrayList;
import java.util.List;

public class DataDetails extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private TextView title_name;
    private LineChart lineChart;
    private ListView allData;
    private Long id;
    private String results;
    private ProductData productData;
    private DataDetailAdapter dataDetailAdapter;
    private List<ListBean> listBeans;
    List<String> xDataList = new ArrayList<>();// x轴数据源
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_details);
        initView();
        initData();
    }

    private void initData() {
        id = getIntent().getLongExtra("id", -1);
        if (id == -2) {
            results = getIntent().getStringExtra("results");
            title_name.setText("建库");
            productData = new ProductData("", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        } else {
            productData = new DataDaoUtils(this).queryUser(Long.valueOf(id));
            title_name.setText(productData.getProName());
            results = productData.getData();
        }

        listBeans = new ArrayList<>();
        listBeans.add(new ListBean("样品名称:", productData.getProName()));
        listBeans.add(new ListBean("用户名:", productData.getUserName()));
        listBeans.add(new ListBean("公司:", productData.getUserCompany()));
        listBeans.add(new ListBean("HS码:", productData.getProHSCode()));
        listBeans.add(new ListBean("CAS码:", productData.getProCASCode()));
        listBeans.add(new ListBean("NFPA704标志:", productData.getProNFPA704Code()));
        listBeans.add(new ListBean("危险等级:", productData.getProDangerLevel()));
        listBeans.add(new ListBean("危险性符号:", productData.getProDangerClass()));
        listBeans.add(new ListBean("危险运输编码:", productData.getProDangerTransportCode()));
        listBeans.add(new ListBean("MDL号:", productData.getProMDLNumber()));
        listBeans.add(new ListBean("EINECS号:", productData.getProEINECSNumber()));
        listBeans.add(new ListBean("RTECS号:", productData.getProRTECSNumber()));
        listBeans.add(new ListBean("BRN号:", productData.getProBRNNumber()));
        listBeans.add(new ListBean("样品信息:", productData.getProDetail()));
        dataDetailAdapter = new DataDetailAdapter(listBeans, this, R.layout.item_datas);
        allData.setAdapter(dataDetailAdapter);
        String[] strings = results.split(",");
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
        lineChart = (LineChart) findViewById(R.id.lineChart);
        lineChart.setOnClickListener(this);
        allData = (ListView) findViewById(R.id.allData);
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("您要保存修改的数据吗？").setTitle("特别提示").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (id == -2) {
                    new DataDaoUtils(DataDetails.this).insertProductList(productData);
                } else {
                    new DataDaoUtils(DataDetails.this).updateData(productData);
                }
                finish();
            }
        }).setNegativeButton("取消", null).create().show();
    }
}
