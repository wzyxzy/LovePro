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
import android.os.Handler;
import android.os.Message;
import android.text.BoringLayout;
import android.util.Log;
import android.widget.Toast;

import com.wzy.lamanpro.common.LaManApplication;

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

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    showTmsg("指令已经发送！ret为" + msg.arg1 + msg.obj);
                    break;
                case 1:
                    showTmsg("收到数据：" + bytesToHexString((byte[]) msg.obj));
                    break;
            }
        }
    };

    public static boolean initUsbData(Context context) {
        UsbUtils.context = context;

        // 获取USB设备
        manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //获取到设备列表
        deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
//            Log.e("ldm", "vid=" + deviceIterator.next().getVendorId() + "---pid=" + deviceIterator.next().getProductId());
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
        // 设置端点
        for (int i = 0; i < mInterface.getEndpointCount(); i++) {
            //if (mInterface.getEndpoint(1) != null)
            // 端点地址,最高位表示方向
            if ((mInterface.getEndpoint(i).getAddress() & 0x80) == 0x00) {

//                Log.e("usbEpOut", "EpOut = " + String.format("%02X ", usbEpOut.getAddress()) + "i=" + i);
            } else {
//                usbEpIn = mInterface.getEndpoint(i);
//                Log.e("usbEpOut", "EpIn = " + String.format("%02X ", usbEpIn.getAddress()) + "i=" + i);
            }
            Log.e("usbEpOut", "getAddress = " + mInterface.getEndpoint(i).getAddress() + ",getDirection = " + mInterface.getEndpoint(i).getDirection() + ",getAttributes = " + mInterface.getEndpoint(i).getAttributes() + ",getEndpointNumber = " + mInterface.getEndpoint(i).getEndpointNumber() + ",getInterval = " + mInterface.getEndpoint(i).getInterval() + ",getMaxPacketSize = " + mInterface.getEndpoint(i).getMaxPacketSize() + ",getType = " + mInterface.getEndpoint(i).getType());
        }
        usbEpOut = mInterface.getEndpoint(3);
        usbEpIn = mInterface.getEndpoint(2);
        //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
//        if (mInterface.getEndpoint(1) != null) {
//            usbEpOut = mInterface.getEndpoint(1);
//        }
//        if (mInterface.getEndpoint(1) != null) {
//            usbEpIn = mInterface.getEndpoint(1);
//        }
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
                            showTmsg("已授权！");
                            LaManApplication.canUseUsb = true;
                            //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
                            initUsbData(context);
//                            context.unregisterReceiver();
                        } else {
                            showTmsg("授权失败！");
                            //授权失败
                            LaManApplication.canUseUsb = false;
                        }
                    }
                }
            }
        }
    }

    public static void sendToUsb(byte[] content) {
        sendToUsb(content, false);
    }

    public static void sendToUsb(byte[] content, boolean isLast) {
        sendbytes = content;
        int ret = -1;
        // 发送准备命令
        ret = mDeviceConnection.bulkTransfer(usbEpOut, sendbytes, sendbytes.length, 5000);
//        showTmsg("指令已经发送！");

        Message message1 = new Message();
        message1.what = 0;
        message1.arg1 = ret;
        message1.obj = bytesToHexString(content);
        handler.sendMessage(message1);
        if (isLast) {
            // 接收发送成功信息(相当于读取设备数据)
            receiveytes = new byte[8192];   //根据设备实际情况写数据大小
            ret = mDeviceConnection.bulkTransfer(usbEpIn, receiveytes, receiveytes.length, 10000);
            Log.e("receive", bytesToHexString(receiveytes));
            Message message2 = new Message();
            message2.what = 0;
            message2.arg1 = ret;
            message2.obj = bytesToHexString(receiveytes);
            handler.sendMessage(message2);
        }

//        Message message = new Message();
//        message.what = 0;
//        message.arg1 = ret;
//        message.obj = bytesToHexString(receiveytes);
//        handler.sendMessage(message);
//        message.what = 0;
//        message.arg1 = ret;
//        handler.sendMessage(message);
//        result_tv.setText(String.valueOf(ret));
//        Toast.makeText(context, String.valueOf(ret), Toast.LENGTH_SHORT).show();
    }

    public static byte[] readFromUsb() {
//        //读取数据2
//        int inMax = usbEpIn.getMaxPacketSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
        UsbRequest usbRequest = new UsbRequest();
        usbRequest.initialize(mDeviceConnection, usbEpIn);
        usbRequest.queue(byteBuffer, 8192);
        if (mDeviceConnection.requestWait() == usbRequest) {
            byte[] retData = byteBuffer.array();
            Log.e("receive",bytesToHexString(retData));
            Message message = new Message();
            message.what = 1;
            message.obj = retData;
            handler.sendMessage(message);
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
