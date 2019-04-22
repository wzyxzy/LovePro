package com.wzy.lamanpro.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

public class UsbUtils {

    private static final String ACTION_DEVICE_PERMISSION = "ACTION_DEVICE_PERMISSION";
    //设备列表
    private static HashMap<String, UsbDevice> deviceList;
    //USB管理器:负责管理USB设备的类
    private static UsbManager manager;
    //找到的USB设备
    private static UsbDevice mUsbDevice;
    //代表USB设备的一个接口
    private static UsbInterface mInterface;
    private static UsbDeviceConnection mDeviceConnection;
    //代表一个接口的某个节点的类:写数据节点
    private static UsbEndpoint usbEpOut;
    //代表一个接口的某个节点的类:读数据节点
    private static UsbEndpoint usbEpIn;
    //要发送信息字节
    private static byte[] sendbytes;
    //接收到的信息字节
    private static byte[] receiveytes;
    private static Context context;

    public static boolean initUsbData(Context context) {
        UsbUtils.context = context;
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(mUsbReceiver, usbFilter);


        // 获取USB设备
        manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //获取到设备列表
        deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            Log.e("ldm", "vid=" + deviceIterator.next().getVendorId() + "---pid=" + deviceIterator.next().getProductId());
            //if (mVendorID == usb.getVendorId() && mProductID == usb.getProductId()) {//找到指定设备
            mUsbDevice = deviceIterator.next();
        }
        if (mUsbDevice == null || mUsbDevice.getInterfaceCount() == 0) {
            showTmsg("请使用usb连接设备");
            return false;
        }
        //获取设备接口
        for (int i = 0; i < mUsbDevice.getInterfaceCount(); ) {
            // 一般来说一个设备都是一个接口，你可以通过getInterfaceCount()查看接口的个数
            // 这个接口上有两个端点，分别对应OUT 和 IN
            UsbInterface usbInterface = mUsbDevice.getInterface(i);
            mInterface = usbInterface;
            break;
        }
        //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
        if (mInterface.getEndpoint(1) != null) {
            usbEpOut = mInterface.getEndpoint(1);
        }
        if (mInterface.getEndpoint(0) != null) {
            usbEpIn = mInterface.getEndpoint(0);
        }
        if (mInterface != null) {
            // 判断是否有权限
            if (manager.hasPermission(mUsbDevice)) {
                // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                mDeviceConnection = manager.openDevice(mUsbDevice);
                if (mDeviceConnection == null) {
                    return false;
                }
                if (mDeviceConnection.claimInterface(mInterface, true)) {
                    showTmsg("找到设备接口");
                    return true;

                } else {
                    mDeviceConnection.close();
                    return false;
                }
            } else {
                UsbPermissionReceiver usbPermissionReceiver = new UsbPermissionReceiver();
                Intent intent = new Intent(ACTION_DEVICE_PERMISSION);
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                IntentFilter permissionFilter = new IntentFilter(ACTION_DEVICE_PERMISSION);
                context.registerReceiver(usbPermissionReceiver, permissionFilter);
                manager.requestPermission(mUsbDevice, mPermissionIntent);

                showTmsg("没有权限，请授权！");
                return false;
            }
        } else {
            showTmsg("没有找到设备接口！");
            return false;
        }


    }

    private static class UsbPermissionReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DEVICE_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device.getDeviceName().equals(mUsbDevice.getDeviceName())) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            //授权成功,在这里进行打开设备操作
                            initUsbData(context);
                        } else {
                            initUsbData(context);
                            //授权失败
                        }
                    }
                }
            }
        }
    }

    /**
     * 用于检测usb插入状态的BroadcasReceiver
     */
    private final static BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                //设备插入
                initUsbData(context);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                //设备移除
            }
        }
    };


    public static void sendToUsb(byte[] content) {
        sendbytes = content;
        int ret = -1;
        // 发送准备命令
        ret = mDeviceConnection.bulkTransfer(usbEpOut, sendbytes, sendbytes.length, 5000);
        showTmsg("指令已经发送！");
        // 接收发送成功信息(相当于读取设备数据)
        receiveytes = new byte[128];   //根据设备实际情况写数据大小
        ret = mDeviceConnection.bulkTransfer(usbEpIn, receiveytes, receiveytes.length, 10000);
//        result_tv.setText(String.valueOf(ret));
        Toast.makeText(context, String.valueOf(ret), Toast.LENGTH_SHORT).show();
    }

    public static byte[] readFromUsb() {
        //读取数据2
        int outMax = usbEpOut.getMaxPacketSize();
        int inMax = usbEpIn.getMaxPacketSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(inMax);
        UsbRequest usbRequest = new UsbRequest();
        usbRequest.initialize(mDeviceConnection, usbEpIn);
        usbRequest.queue(byteBuffer, inMax);
        if (mDeviceConnection.requestWait() == usbRequest) {
            byte[] retData = byteBuffer.array();
            try {
                showTmsg("收到数据：" + new String(retData, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return retData;
        }
        return null;
    }


    private static void showTmsg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] hexToByteArray(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }


    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }

    public static String repeat(int count) {
        return repeat(count, " ");
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param bytes 要转换的字节数组
     * @return 转换后的结果
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
