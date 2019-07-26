package com.wzy.lamanpro.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bin.david.form.core.SmartTable;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.GFs;
import com.wzy.lamanpro.dao.MainDaoUtils;

import java.util.List;

public class ManageData extends AppCompatActivity implements View.OnClickListener {


    private SmartTable dataList;
    private FloatingActionButton menu_search;
    private List<GFs> gFsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data);
        initView();
        initData();
    }

    private void initData() {
        gFsList = new MainDaoUtils(this).queryAllGFs();
        dataList.setData(gFsList);
    }


    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dataList = (SmartTable) findViewById(R.id.dataList);
        dataList.setOnClickListener(this);
        menu_search = (FloatingActionButton) findViewById(R.id.menu_search);
        menu_search.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.menu_search:
                break;
        }
    }
}
