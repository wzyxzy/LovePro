package com.wzy.lamanpro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.wzy.lamanpro.R;

import java.util.ArrayList;
import java.util.List;

public class AddLibrary extends AppCompatActivity implements View.OnClickListener {

    private LineChart lineChart;
    private Button button_start;
    private TextView state;
    private FloatingActionButton fab;
    private static final byte[] SET_INIT_TIME = {0x09, 0x4F, 0x69, 0x74};//设置积分时间
    private static final byte[] SET_POWER = {0x09, 0x4F, 0x70, 0x77, 0x00, 0x00, 0x00, 0x00, (byte) 0xE8, 0x03, 0x00, 0x00};//设置能量大小
    private static final byte[] OPEN_PORT = {0x09, 0x4f, 0x65, 0x70, 0x02, 0x00, 0x00, 0x00};//打开激光
    private static final byte[] GET_DATA = {0x09, 0x4F, 0x53, 0x4F};//获取波形
    private static final byte[] CLOSE_PORT = {0x09, 0x4f, 0x65, 0x70, 0x00, 0x00, 0x00, 0x00};//关闭激光
    private static byte[][] results;
    private static float[] finalsResults;
    private StringBuffer stateText;
    List<String> xDataList = new ArrayList<>();// x轴数据源
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_library);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
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
            case R.id.action_report:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        button_start = (Button) findViewById(R.id.button_start);
        state = (TextView) findViewById(R.id.state);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        button_start.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:

                break;
            case R.id.fab:

                break;
        }
    }
}
