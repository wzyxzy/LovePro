package com.wzy.lamanpro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.DataAdapter;
import com.wzy.lamanpro.adapter.UserAdapter;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.bean.Users;
import com.wzy.lamanpro.dao.DataDaoUtils;
import com.wzy.lamanpro.dao.UserDaoUtils;

import java.util.List;

public class ManageData extends AppCompatActivity {

    private ListView dataList;
    private List<ProductData> productData;
    private DataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data);
        initView();
        initData();
    }

    private void initData() {
        productData = new DataDaoUtils(this).queryAllData();
        dataAdapter = new DataAdapter(productData, this, R.layout.item_data);
        dataList.setAdapter(dataAdapter);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManageData.this, UserDetails.class);
                intent.putExtra("id", productData.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dataList = (ListView) findViewById(R.id.dataList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        productData = new DataDaoUtils(this).queryAllData();
        dataAdapter.updateRes(productData);
    }

}
