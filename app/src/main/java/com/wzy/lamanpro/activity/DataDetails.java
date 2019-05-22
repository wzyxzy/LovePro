package com.wzy.lamanpro.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.dao.DataDaoUtils;
import com.wzy.lamanpro.utils.ChartUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataDetails extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private TextView title_name;
    private LineChart lineChart;
    //    private ListView allData;
    private Long id;
    private String results;
    private ProductData productData;
    //    private DataDetailAdapter dataDetailAdapter;
//    private List<ListBean> listBeans;
    List<String> xDataList = new ArrayList<>();// x轴数据源
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源
    private EditText product_name;
    private EditText user_account;
    private EditText user_company;
    private EditText product_hs;
    private EditText product_cas;
    private EditText product_nfpa704;
    private EditText dangerous_level;
    private EditText dangerous_sign;
    private EditText dangerous_transport;
    private EditText product_mdl;
    private EditText product_einecs;
    private EditText product_rtecs;
    private EditText product_brn;
    private EditText product_detail;
    private boolean canEdit = false;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_details);
        initView();
        initData();
    }


    private void initData() {
        id = getIntent().getLongExtra("id", -1);
        if (id == -1) {
            results = getIntent().getStringExtra("results");
            title_name.setText("建库");
            productData = new ProductData("", "", format.format(new Date()), "", "", "", "", "", "", "", "", "", "", "", "", "");
            canEdit = true;
            checkEnabled();
        } else {
            productData = new DataDaoUtils(this).queryUser(Long.valueOf(id));
            title_name.setText(productData.getProName());
            results = productData.getData();
            product_name.setText(productData.getProName());
            user_account.setText(productData.getUserName());
            user_company.setText(productData.getUserCompany());
            product_hs.setText(productData.getProHSCode());
            product_cas.setText(productData.getProCASCode());
            product_nfpa704.setText(productData.getProNFPA704Code());
            dangerous_level.setText(productData.getProDangerLevel());
            dangerous_sign.setText(productData.getProDangerClass());
            dangerous_transport.setText(productData.getProDangerTransportCode());
            product_mdl.setText(productData.getProMDLNumber());
            product_einecs.setText(productData.getProEINECSNumber());
            product_rtecs.setText(productData.getProRTECSNumber());
            product_brn.setText(productData.getProBRNNumber());
            product_detail.setText(productData.getProDetail());
            canEdit = false;
            checkEnabled();

        }

        String[] strings = results.split(",");
        for (int i = 0; i < strings.length; i++) {
            xDataList.add(String.valueOf(i));
            yDataList.add(new Entry(Float.valueOf(strings[i]), i));
        }
        ChartUtil.showChart(this, lineChart, xDataList, yDataList, "波普图", "波长/时间", "mm");
    }

    private void checkEnabled() {
        product_name.setEnabled(canEdit);
        user_account.setEnabled(canEdit);
        user_company.setEnabled(canEdit);
        product_hs.setEnabled(canEdit);
        product_cas.setEnabled(canEdit);
        product_nfpa704.setEnabled(canEdit);
        dangerous_level.setEnabled(canEdit);
        dangerous_sign.setEnabled(canEdit);
        dangerous_transport.setEnabled(canEdit);
        product_mdl.setEnabled(canEdit);
        product_einecs.setEnabled(canEdit);
        product_rtecs.setEnabled(canEdit);
        product_brn.setEnabled(canEdit);
        product_detail.setEnabled(canEdit);

    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setOnClickListener(this);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        lineChart.setOnClickListener(this);
        product_name = (EditText) findViewById(R.id.product_name);
        product_name.setOnClickListener(this);
        user_account = (EditText) findViewById(R.id.user_account);
        user_account.setOnClickListener(this);
        user_company = (EditText) findViewById(R.id.user_company);
        user_company.setOnClickListener(this);
        product_hs = (EditText) findViewById(R.id.product_hs);
        product_hs.setOnClickListener(this);
        product_cas = (EditText) findViewById(R.id.product_cas);
        product_cas.setOnClickListener(this);
        product_nfpa704 = (EditText) findViewById(R.id.product_nfpa704);
        product_nfpa704.setOnClickListener(this);
        dangerous_level = (EditText) findViewById(R.id.dangerous_level);
        dangerous_level.setOnClickListener(this);
        dangerous_sign = (EditText) findViewById(R.id.dangerous_sign);
        dangerous_sign.setOnClickListener(this);
        dangerous_transport = (EditText) findViewById(R.id.dangerous_transport);
        dangerous_transport.setOnClickListener(this);
        product_mdl = (EditText) findViewById(R.id.product_mdl);
        product_mdl.setOnClickListener(this);
        product_einecs = (EditText) findViewById(R.id.product_einecs);
        product_einecs.setOnClickListener(this);
        product_rtecs = (EditText) findViewById(R.id.product_rtecs);
        product_rtecs.setOnClickListener(this);
        product_brn = (EditText) findViewById(R.id.product_brn);
        product_brn.setOnClickListener(this);
        product_detail = (EditText) findViewById(R.id.product_detail);
        product_detail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "点击可以对库数据进行修改，确认要修改吗", Snackbar.LENGTH_LONG)
                        .setAction("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                canEdit = true;
                                checkEnabled();
                            }
                        }).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (canEdit) {
            new AlertDialog.Builder(this).setMessage("您要保存修改的数据吗？").setTitle("特别提示").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (submit()) {
                        if (id == -1) {
                            productData.setData(results);
                            new DataDaoUtils(DataDetails.this).insertProductList(productData);
                        } else {
                            new DataDaoUtils(DataDetails.this).updateData(productData);
                        }
                        finish();
                    }

                }
            }).setNegativeButton("取消", null).create().show();
        } else {
            super.onBackPressed();
        }

    }

    private boolean submit() {
        // validate
        String name = product_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "样品名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        productData.setProName(name);
        productData.setUserName(user_account.getText().toString().trim());
//        if (TextUtils.isEmpty(account)) {
//            Toast.makeText(this, "用户名", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setUserCompany(user_company.getText().toString().trim());
//        if (TextUtils.isEmpty(company)) {
//            Toast.makeText(this, "公司", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProHSCode(product_hs.getText().toString().trim());
//        if (TextUtils.isEmpty(hs)) {
//            Toast.makeText(this, "HS码", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProCASCode(product_cas.getText().toString().trim());
//        if (TextUtils.isEmpty(cas)) {
//            Toast.makeText(this, "CAS码", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProNFPA704Code(product_nfpa704.getText().toString().trim());
//        if (TextUtils.isEmpty(nfpa704)) {
//            Toast.makeText(this, "NFPA704标志", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProDangerLevel(dangerous_level.getText().toString().trim());
//        if (TextUtils.isEmpty(level)) {
//            Toast.makeText(this, "危险等级", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProDangerClass(dangerous_sign.getText().toString().trim());
//        if (TextUtils.isEmpty(sign)) {
//            Toast.makeText(this, "危险性符号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProDangerTransportCode(dangerous_transport.getText().toString().trim());
//        if (TextUtils.isEmpty(transport)) {
//            Toast.makeText(this, "危险运输编码", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProMDLNumber(product_mdl.getText().toString().trim());
//        if (TextUtils.isEmpty(mdl)) {
//            Toast.makeText(this, "MDL号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProEINECSNumber(product_einecs.getText().toString().trim());
//        if (TextUtils.isEmpty(einecs)) {
//            Toast.makeText(this, "EINECS号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProRTECSNumber(product_rtecs.getText().toString().trim());
//        if (TextUtils.isEmpty(rtecs)) {
//            Toast.makeText(this, "RTECS号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProBRNNumber(product_brn.getText().toString().trim());
//        if (TextUtils.isEmpty(brn)) {
//            Toast.makeText(this, "BRN号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProDetail(product_detail.getText().toString().trim());
//        if (TextUtils.isEmpty(detail)) {
//            Toast.makeText(this, "样品信息", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // TODO validate success, do something
        return true;

    }
}
