package com.wzy.lamanpro.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.common.LaManApplication;
import com.wzy.lamanpro.dao.UserDaoUtils;
import com.wzy.lamanpro.ui.CommonDialog;
import com.wzy.lamanpro.utils.ChartUtil;
import com.wzy.lamanpro.utils.SPUtility;
import com.wzy.lamanpro.utils.UsbUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.wzy.lamanpro.utils.UsbUtils.readFromUsb;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final byte[] SET_INIT_TIME = {0x09, 0x4F, 0x69, 0x74};//设置积分时间
    private static final byte[] SET_POWER = {0x09, 0x4F, 0x70, 0x77, 0x00, 0x00, 0x00, 0x00, (byte) 0xE8, 0x03, 0x00, 0x00, 0x42, 0x00, 0x00, 0x00};//设置能量大小
    private static final byte[] OPEN_PORT = {0x09, 0x4f, 0x65, 0x70, 0x02, 0x00, 0x00, 0x00};//打开激光
    private static final byte[] GET_DATA = {0x09, 0x4F, 0x53, 0x4F};//获取波形
    private static final byte[] CLOSE_PORT = {0x09, 0x4f, 0x65, 0x70, 0x00, 0x00, 0x00, 0x00};//关闭激光
    private static byte[] results;
    private LineChart lineChart;
    private Button button_start;
    private TextView text_report;
    private StringBuffer stateText;
    List<String> xDataList = new ArrayList<>();// x轴数据源
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    Toast.makeText(Main2Activity.this, msg.obj + "", Toast.LENGTH_SHORT).show();
                    UsbUtils.sendToUsb(CLOSE_PORT);

                    for (int i = 0; i < results.length; i += 2) {
                        xDataList.add(String.valueOf(i / 2));
                        yDataList.add(new Entry(UsbUtils.twoByteToUnsignedInt(results[i + 1], results[i]), i / 2));
                    }
                    Log.e("y", yDataList.toString());
                    stateText.append("获取波形完成\n");
                    handler.sendEmptyMessage(2);

                    ChartUtil.showChart(Main2Activity.this, lineChart, xDataList, yDataList, "波普图", "波长/时间", "mm");

                    break;
                case 1:
                    Toast.makeText(Main2Activity.this, "请输入合理范围内的时间", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    state.setText(stateText.toString());
                    break;
            }
        }
    };
    private Button button_start1;
    private Button button_start2;
    private Button button_start3;
    private Button button_start4;
    private Toolbar toolbar;
    private TextView state;

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                LaManApplication.canUseUsb = UsbUtils.initUsbData(Main2Activity.this);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(Main2Activity.this, "USB设备已移除！", Toast.LENGTH_SHORT).show();
                LaManApplication.canUseUsb = false;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LaManApplication.canUseUsb = UsbUtils.initUsbData(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.textView);
        textView.setText(new UserDaoUtils(this).queryUserName(SPUtility.getUserId(this)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                startActivity(new Intent(Main2Activity.this, SettingTest.class));
                return true;
            case R.id.action_report:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_camera:
                break;
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                startActivity(new Intent(Main2Activity.this, ManageUsers.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(Main2Activity.this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                showStyleDialog();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showStyleDialog() {
        CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setTitle("温 馨 提 示 :");
        commonDialog.setMessage("您确定注销吗？");
        commonDialog.setRightButtonClickListener(new CommonDialog.RightButtonClickListener() {
            @Override
            public void onRightButtonClick() {
                SPUtility.putSPBoolean(Main2Activity.this, "isAutoLogin", false);
                finish();
                Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        commonDialog.show();
    }

    private void initView() {
        lineChart = findViewById(R.id.lineChart);
        button_start = findViewById(R.id.button_start);
        text_report = findViewById(R.id.text_report);
        button_start.setOnClickListener(this);
        button_start1 = (Button) findViewById(R.id.button_start1);
        button_start1.setOnClickListener(this);
        button_start2 = (Button) findViewById(R.id.button_start2);
        button_start2.setOnClickListener(this);
        button_start3 = (Button) findViewById(R.id.button_start3);
        button_start3.setOnClickListener(this);
        button_start4 = (Button) findViewById(R.id.button_start4);
        button_start4.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(this);
        state = (TextView) findViewById(R.id.state);
        state.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:

                if (LaManApplication.canUseUsb) {
                    lineChart.clear();
                    xDataList.clear();
                    yDataList.clear();
                    stateText = new StringBuffer();
                    stateText.append("开始测试。。。\n");
                    handler.sendEmptyMessage(2);
                    final int[] count = {0};
                    final Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            switch (count[0]) {
                                case 0:
                                    try {
                                        int time = Integer.valueOf(SPUtility.getSPString(Main2Activity.this, "time"));
                                        UsbUtils.sendToUsb(UsbUtils.addBytes(SET_INIT_TIME, UsbUtils.intTobyteLH(time * 1000)));
                                        stateText.append("积分时间设置完毕，积分时间为" + time + "毫秒，发送的内容是：" + Arrays.toString(UsbUtils.addBytes(SET_INIT_TIME, UsbUtils.intTobyteLH(time * 1000))) + "\n");
                                        handler.sendEmptyMessage(2);
                                    } catch (NumberFormatException e) {
                                        handler.sendEmptyMessage(1);
                                    }
                                    break;
                                case 1:
                                    UsbUtils.sendToUsb(SET_POWER);
                                    stateText.append("功率设置发送完毕，发送的内容是：" + Arrays.toString(SET_POWER) + "\n");
                                    handler.sendEmptyMessage(2);
                                    break;
                                case 2:

                                    UsbUtils.sendToUsb(OPEN_PORT);
                                    stateText.append("打开激光发送完毕，发送的内容是：" + Arrays.toString(OPEN_PORT) + "\n");
                                    handler.sendEmptyMessage(2);

                                    break;
                                case 3:
                                    UsbUtils.sendToUsb(GET_DATA);
                                    stateText.append("获取波形发送完毕，发送的内容是：" + Arrays.toString(GET_DATA) + "\n");
                                    handler.sendEmptyMessage(2);

                                    break;
                                case 4:
                                    results = readFromUsb();
                                    stateText.append("返回的内容是：" + Arrays.toString(results) + "\n");
                                    handler.sendEmptyMessage(0);
                                    break;
                                case 5:

                                    timer.cancel();
                                    break;
                            }
                            count[0]++;
                        }
                    };
                    timer.schedule(timerTask, 50, 50);
                } else {
                    Toast.makeText(Main2Activity.this, "请先连接usb设备！", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_start1:
                if (!LaManApplication.canUseUsb)
                    return;
                final Timer timer1 = new Timer();
                final int[] count = {0};
                TimerTask timerTask1 = new TimerTask() {
                    @Override
                    public void run() {
                        count[0]++;
                        switch (count[0]) {
                            case 1:
                                UsbUtils.sendToUsb(SET_INIT_TIME);
                                break;
                            case 2:
                                UsbUtils.sendToUsb(SET_POWER);
                                break;
                            case 3:
                                UsbUtils.sendToUsb(OPEN_PORT);
                                break;
                            case 4:
                                UsbUtils.sendToUsb(GET_DATA);
                                break;
                            case 5:
                                UsbUtils.readFromUsb();
                                break;
                        }
                        if (count[0] == 5) {
                            timer1.cancel();
                        }
                    }
                };
                timer1.schedule(timerTask1, 50, 50);
                break;
            case R.id.button_start2:
                if (LaManApplication.canUseUsb)
                    UsbUtils.sendToUsb(CLOSE_PORT);
                else
                    UsbUtils.showTmsg(UsbUtils.twoByteToSignedInt((byte) 0x03, (byte) 0x08) + "");
                break;
            case R.id.button_start3:
                if (LaManApplication.canUseUsb)
                    UsbUtils.sendToUsb(OPEN_PORT);
                else
                    UsbUtils.showTmsg(UsbUtils.twoByteToUnsignedInt((byte) 0x03, (byte) 0x08) + "");
                break;
            case R.id.button_start4:
                if (LaManApplication.canUseUsb) {
                    lineChart.clear();
                    xDataList.clear();
                    yDataList.clear();
                    stateText = new StringBuffer();
                    stateText.append("开始测试。。。\n");
                    UsbUtils.sendToUsb(GET_DATA);
                    results = readFromUsb();
                    stateText.append("返回的内容是：" + Arrays.toString(results) + "\n");
                    handler.sendEmptyMessage(2);

                    handler.sendEmptyMessage(0);
                }

                break;

        }
    }
}
