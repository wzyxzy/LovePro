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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.UsersDao;
import com.wzy.lamanpro.common.LaManApplication;
import com.wzy.lamanpro.dao.UserDaoUtils;
import com.wzy.lamanpro.ui.CommonDialog;
import com.wzy.lamanpro.utils.SPUtility;
import com.wzy.lamanpro.utils.UsbUtils;

import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
    private LineChart lineChart;
    private Button button_start;
    private TextView text_report;
    private FloatingActionButton fab;
    private NavigationView nav_view;
    private DrawerLayout drawer_layout;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(Main2Activity.this, msg.obj + "", Toast.LENGTH_SHORT).show();
                    UsbUtils.sendToUsb(UsbUtils.hexToByteArray("094F657002000000"));
                    break;
                case 1:
                    Toast.makeText(Main2Activity.this, "请输入合理范围内的时间", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

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
//            tvInfo.append("BroadcastReceiver in\n");

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Toast.makeText(Main2Activity.this, "USB设备已连接！", Toast.LENGTH_SHORT).show();
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

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
//                canUseUsb = UsbUtils.initUsbData(this);
                if (LaManApplication.canUseUsb) {
                    final int[] count = {0};
                    final Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            switch (count[0]) {
                                case 0:
                                    try {
                                        int time = Integer.valueOf(SPUtility.getSPString(Main2Activity.this, "time"));
                                        String s = Integer.toHexString(time * 1000);
                                        StringBuilder value = new StringBuilder("094F6974");
                                        for (int i = s.length() - 2; i >= -1; i -= 2) {
                                            if (i == -1) {
                                                value.append("0").append(s);
                                                break;
                                            }
                                            value.append(s.substring(i));
                                            s = s.substring(0, i);

                                        }
                                        if (value.length() < 16) {

                                            value.append(UsbUtils.repeat(16 - value.length(), "0"));
                                        }
                                        UsbUtils.sendToUsb(UsbUtils.hexToByteArray(value.toString()));
                                    } catch (NumberFormatException e) {
                                        handler.sendEmptyMessage(1);

                                    }


                                    break;
                                case 1:
                                    UsbUtils.sendToUsb(UsbUtils.hexToByteArray("094F707700000000E803000042000000"));
                                    break;
                                case 2:
                                    UsbUtils.sendToUsb(UsbUtils.hexToByteArray("094F657000000000"));
                                    break;
                                case 3:
                                    UsbUtils.sendToUsb(UsbUtils.hexToByteArray("094F534F"));
                                    break;
                                case 4:
                                    byte[] bytes = UsbUtils.readFromUsb();
                                    Message message = new Message();
                                    message.what = 0;
                                    message.obj = UsbUtils.bytesToHexString(bytes != null ? bytes : new byte[0]);
                                    handler.sendMessage(message);
                                    break;
                                case 5:
                                    timer.cancel();
                                    break;

                            }
                            count[0]++;

                        }
                    };
                    timer.schedule(timerTask, 0, 50);

                } else {
                    Toast.makeText(Main2Activity.this, "请先连接usb设备！", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
