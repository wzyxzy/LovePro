package com.wzy.lamanpro.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.wzy.lamanpro.dao.HisDaoUtils;
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
                Intent intent = new Intent(ManageData.this, DataDetails.class);
                intent.putExtra("id", productData.get(position).getId());
                startActivity(intent);
            }
        });
        dataList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(ManageData.this)
                        .setTitle("温馨提示")
                        .setMessage("确定要删除吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DataDaoUtils(ManageData.this).deleteUser(productData.get(position).getId());
                                productData = new DataDaoUtils(ManageData.this).queryAllData();
                                dataAdapter.updateRes(productData);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
                return true;
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
