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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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
import com.wzy.lamanpro.utils.ChartUtil;
import com.wzy.lamanpro.utils.SPUtility;
import com.wzy.lamanpro.utils.UsbUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.wzy.lamanpro.utils.UsbUtils.readFromUsb;

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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    UsbUtils.sendToUsb(CLOSE_PORT);
                    for (int i = 0; i < finalsResults.length; i++) {
                        xDataList.add(String.valueOf(i));
                        yDataList.add(new Entry(finalsResults[i], i));
                    }
                    stateText.append("获取波形完成\n");
                    handler.sendEmptyMessage(2);
                    ChartUtil.showChart(AddLibrary.this, lineChart, xDataList, yDataList, "波普图", "波长/时间", "mm");
                    break;
                case 1:
                    Toast.makeText(AddLibrary.this, "请输入合理范围内的设置", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    state.setText(stateText.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_library);
        initView();
    }

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
                LaManApplication.canUseUsb = UsbUtils.initUsbData(AddLibrary.this);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(AddLibrary.this, "USB设备已移除！", Toast.LENGTH_SHORT).show();
                LaManApplication.canUseUsb = false;
            }
        }
    };

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
        state.setMovementMethod(ScrollingMovementMethod.getInstance());
        LaManApplication.canUseUsb = UsbUtils.initUsbData(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        button_start.setOnClickListener(this);
        fab.setOnClickListener(this);
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
                    final int[] count = {0, 0};
                    final int once = TextUtils.isEmpty(SPUtility.getSPString(AddLibrary.this, "once")) ? 10 : Integer.valueOf(SPUtility.getSPString(AddLibrary.this, "once"));
                    final int time = TextUtils.isEmpty(SPUtility.getSPString(AddLibrary.this, "time")) ? 500 : Integer.valueOf(SPUtility.getSPString(AddLibrary.this, "time"));
                    final int power = TextUtils.isEmpty(SPUtility.getSPString(AddLibrary.this, "power")) ? 66 : Integer.valueOf(SPUtility.getSPString(AddLibrary.this, "power"));
                    results = new byte[once][4200];
                    finalsResults = new float[2100];
                    final Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            switch (count[0]) {
                                case 0:
                                    try {
                                        UsbUtils.sendToUsb(UsbUtils.addBytes(SET_INIT_TIME, UsbUtils.intTobyteLH(time * 1000)));
                                        stateText.append("第" + (count[1] + 1) + "次积分：\n积分时间设置完毕，积分时间为" + time + "毫秒。\n");
//                                        stateText.append("积分时间设置完毕，积分时间为" + time + "毫秒，发送的内容是：" + Arrays.toString(UsbUtils.addBytes(SET_INIT_TIME, UsbUtils.intTobyteLH(time * 1000))) + "\n");
                                        handler.sendEmptyMessage(2);
                                    } catch (NumberFormatException e) {
                                        handler.sendEmptyMessage(1);
                                    }
                                    break;
                                case 1:
                                    try {
                                        UsbUtils.sendToUsb(UsbUtils.addBytes(SET_POWER, UsbUtils.intTobyteLH(power)));
                                        stateText.append("功率设置发送完毕，功率设置为：" + power + "。\n");
//                                    stateText.append("功率设置发送完毕，发送的内容是：" + Arrays.toString(SET_POWER) + "\n");
                                        handler.sendEmptyMessage(2);
                                    } catch (NumberFormatException e) {
                                        handler.sendEmptyMessage(1);
                                    }
                                    break;
                                case 2:
                                    UsbUtils.sendToUsb(OPEN_PORT);
                                    stateText.append("打开激光发送完毕。\n");
//                                    stateText.append("打开激光发送完毕，发送的内容是：" + Arrays.toString(OPEN_PORT) + "\n");
                                    handler.sendEmptyMessage(2);
                                    break;
                                case 3:
                                    UsbUtils.sendToUsb(GET_DATA);
                                    stateText.append("获取波形发送完毕。\n");
//                                    stateText.append("获取波形发送完毕，发送的内容是：" + Arrays.toString(GET_DATA) + "\n");
                                    handler.sendEmptyMessage(2);
                                    break;
                                case 4:
                                    results[count[1]++] = readFromUsb();
                                    stateText.append("返回结果完毕并存储。\n");
//                                    stateText.append("返回的内容是：" + Arrays.toString(results) + "\n");
                                    break;
                                case 5:
                                    for (int i = 0; i < finalsResults.length; i++) {
                                        finalsResults[i] = UsbUtils.twoByteToUnsignedInt(results[0][2 * i + 1], results[0][2 * i]);
                                    }
                                    label1:
                                    for (int i = 0; i < results.length; i++) {
                                        for (int j = 0; j < finalsResults.length; j++) {
                                            if (i == 0) {
                                                continue label1;
                                            } else {
                                                finalsResults[j] = (finalsResults[j] + UsbUtils.twoByteToUnsignedInt(results[i][2 * j + 1], results[i][2 * j])) / 2;
                                            }
                                        }
                                    }
                                    handler.sendEmptyMessage(0);
                                    timer.cancel();
                                    break;
                            }
                            count[0]++;
                            if (once > count[1] + 1)
                                count[0] %= 5;
                        }
                    };
                    timer.schedule(timerTask, 50, 50);
                } else {
                    Toast.makeText(AddLibrary.this, "请先连接usb设备！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fab:
                Snackbar.make(v, "点击即可将此库建立并保存，确定要建库吗？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AddLibrary.this, UserDetails.class);
                                intent.putExtra("account", "");
                                startActivity(intent);
                            }
                        }).show();
                break;
        }
    }
}
