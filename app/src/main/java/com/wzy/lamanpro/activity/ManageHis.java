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
import com.wzy.lamanpro.adapter.HisAdapter;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.dao.HisDaoUtils;

import java.util.List;

public class ManageHis extends AppCompatActivity {

    private ListView hisList;
    private List<HisData> hisData;
    private HisAdapter hisAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_his);
        initView();
        initData();
    }

    private void initData() {
        hisData = new HisDaoUtils(this).queryAllData();
        hisAdapter = new HisAdapter(hisData, this, R.layout.item_his);
        hisList.setAdapter(hisAdapter);
        hisList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManageHis.this, HisDetails.class);
                intent.putExtra("id", hisData.get(position).getId());
                startActivity(intent);
            }
        });
        hisList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(ManageHis.this)
                        .setTitle("温馨提示")
                        .setMessage("确定要删除吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new HisDaoUtils(ManageHis.this).deleteUser(hisData.get(position).getId());
                                hisData = new HisDaoUtils(ManageHis.this).queryAllData();
                                hisAdapter.updateRes(hisData);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
                return true;
            }
        });

    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hisList = (ListView) findViewById(R.id.hisList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hisData = new HisDaoUtils(this).queryAllData();
        hisAdapter.updateRes(hisData);
    }
}
