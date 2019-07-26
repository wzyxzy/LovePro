package com.wzy.lamanpro.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.HisDataAdapter;
import com.wzy.lamanpro.bean.GFs;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.bean.ListBean;
import com.wzy.lamanpro.dao.HisDaoUtils;
import com.wzy.lamanpro.dao.MainDaoUtils;
import com.wzy.lamanpro.utils.ChartUtil;
import com.wzy.lamanpro.utils.PdfManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GFsDetails extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private TextView title_name;
    private ListView allData;
    private Long id;
    private GFs gFs;
    private HisDataAdapter hisDataAdapter;
    private List<ListBean> listBeans;

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    title_name.setText(gFs.getName());

                    listBeans.add(new ListBean("姓名:", gFs.getName()));
                    listBeans.add(new ListBean("生日:", gFs.getBirth()));
                    listBeans.add(new ListBean("身高:", gFs.getHeight() + ""));
                    listBeans.add(new ListBean("体重:", gFs.getWeight() + ""));
                    listBeans.add(new ListBean("颜值:", gFs.getYanzhi() + ""));
                    listBeans.add(new ListBean("出生地:", gFs.getDirct()));
                    listBeans.add(new ListBean("学历:", gFs.getSt()));
                    listBeans.add(new ListBean("工作:", gFs.getWork()));
                    listBeans.add(new ListBean("月收入:", gFs.getIns() + ""));
                    listBeans.add(new ListBean("认识时间:", gFs.getRec()));
                    listBeans.add(new ListBean("喜欢我的程度:", gFs.getLoveme() + ""));
                    listBeans.add(new ListBean("我喜欢的程度:", gFs.getIlove() + ""));
                    listBeans.add(new ListBean("目前关系:", gFs.getRelate() + ""));
                    listBeans.add(new ListBean("是否为处:", gFs.getOthers() + ""));
                    listBeans.add(new ListBean("将来在京概率:", gFs.getFutureinbj() + ""));
                    listBeans.add(new ListBean("备注:", gFs.getMarks()));
                    listBeans.add(new ListBean("排序:", gFs.getSort() + ""));
                    listBeans.add(new ListBean("分值:", gFs.getPoint() + ""));
                    listBeans.add(new ListBean("分值变化:", gFs.getPoint_now() + ""));
                    hisDataAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gfs_details);
        initView();
        initData();
    }

    private void initData() {
        id = getIntent().getLongExtra("id", -1);

        OkGo.<String>get("https://cnxa.top:8443/api/gfs/findOne").params("id", id).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                gFs = new Gson().fromJson(response.body(), GFs.class);

                handler.sendEmptyMessage(0);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);

            }

            @Override
            public void onFinish() {
                super.onFinish();

            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                super.onCacheSuccess(response);
                Logger.d(response);
                if (response.code() == 200) {
                    gFs = new Gson().fromJson(response.body(), GFs.class);
                } else {
                    gFs = new MainDaoUtils(GFsDetails.this).queryGF(id);
                }
                handler.sendEmptyMessage(0);


            }
        });



    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setOnClickListener(this);
        allData = (ListView) findViewById(R.id.allData);
        listBeans = new ArrayList<>();
        hisDataAdapter = new HisDataAdapter(listBeans, GFsDetails.this, R.layout.item_his);
        allData.setAdapter(hisDataAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "您确定要改变分值吗？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                break;
        }
    }
}
