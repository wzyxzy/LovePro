package com.wzy.lamanpro.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.GFs;
import com.wzy.lamanpro.dao.MainDaoUtils;

import java.util.List;

public class AddLibrary extends AppCompatActivity implements View.OnClickListener {

    private Button button_start;
    private TextView state;
    private FloatingActionButton fab;
    private List<GFs> gFsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_library);
        initView();
        initData();
    }

    private void initData() {
        gFsList = new MainDaoUtils(this).queryAllGFs();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(AddLibrary.this, SettingTest.class));
                return true;
            case R.id.use_info:
                new AlertDialog.Builder(AddLibrary.this)
                        .setMessage("内容正在完善中。。。")
                        .setTitle("使用说明:")
                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
//            case R.id.action_report:
//
//                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_start = (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(this);
        state = (TextView) findViewById(R.id.state);
//        state.setOnClickListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                final int random = (int) (Math.random() * gFsList.size());
                state.setText("您本次的选秀结果是：" + gFsList.get(random).getName() + "，点击可查看详情！");
                state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddLibrary.this, GFsDetails.class);
                        intent.putExtra("id", gFsList.get(random).getId());
                        startActivity(intent);
                    }
                });
                break;
            case R.id.fab:

                break;

        }
    }
}
