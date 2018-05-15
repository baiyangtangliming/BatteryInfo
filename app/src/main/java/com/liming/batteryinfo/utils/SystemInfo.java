package com.liming.batteryinfo.utils;

/**
 * Created by tlm on 2018/5/5.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.BATTERY_SERVICE;

final public class SystemInfo {

    private static Map<String,String> keys=new HashMap<String, String>(){
        {
            put("charge_full", "POWER_SUPPLY_CHARGE_FULL");
            put("charge_full_design", "POWER_SUPPLY_CHARGE_FULL_DESIGN");
            put("temp", "POWER_SUPPLY_TEMP");//温度
            put("current_now", "POWER_SUPPLY_CURRENT_NOW");//电流
            put("voltage_now", "POWER_SUPPLY_VOLTAGE_NOW");//电压
            put("cycle_count", "POWER_SUPPLY_CYCLE_COUNT");//充电循环次数
            put("constant_charge_current_max", "无");//充电限制电流
    }
};
    private static final String [] BATTERY_PATH ={
            "/sys/class/power_supply/bms/",
            "/sys/class/power_supply/battery/"
            } ;//电池信息路径

    private static final String ERROR_MAC_STR = "02:00:00:00:00:00";//获取失败默认返回值

    private static WifiManager mWifiManager;// Wifi 管理器

    /**
     * 默认构造方法
     */
    public SystemInfo() {
    }

    /**
     * 实例化WifiManager对象
     * @param context 当前上下文对象
     * @return
     */
    private static WifiManager getInstant(Context context) {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        return mWifiManager;
    }

    /**
     * 获取手机IMEI
     *
     * @param context
     * @return
     */
    public static final String getIMEI(Context context) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
          @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 开启wifi
     */
    @SuppressLint("MissingPermission")
    public static void getStartWifiEnabled() {
        // 判断当前wifi状态是否为开启状态
        if (!mWifiManager.isWifiEnabled()) {
            // 打开wifi 有些设备需要授权
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 获取手机设备MAC地址
     * MAC地址：物理地址、硬件地址，用来定义网络设备的位置
     * modify by heliquan at 2018年1月17日
     *
     * @param context
     * @return
     */
    public static String getMobileMAC(Context context) {
        mWifiManager = getInstant(context);
        // 如果当前设备系统大于等于6.0 使用下面的方法
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getAndroidHighVersionMac();
        } else { // 当前设备在6.0以下
            return getAndroidLowVersionMac(mWifiManager);
        }
    }

    /**
     * Android 6.0 设备兼容获取mac
     * 兼容原因：从Android 6.0之后，Android 移除了通过WiFi和蓝牙API来在应用程序中可编程的访问本地硬件标示符。
     * 现在WifiInfo.getMacAddress()和BluetoothAdapter.getAddress()方法都将返回：02:00:00:00:00:00
     *
     * @return
     */
    public static String getAndroidHighVersionMac() {
        String str = "";
        String macSerial = "";
        try {
            // 由于Android底层基于Linux系统 可以根据shell获取
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
                macSerial = getAndroidVersion7MAC();
            }
        }
        return macSerial;
    }

    /**
     * Android 6.0 以下设备获取mac地址 获取失败默认返回：02:00:00:00:00:00
     *
     * @param wifiManager
     * @return
     */
    private static String getAndroidLowVersionMac(WifiManager wifiManager) {
        try {
            @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String mac = wifiInfo.getMacAddress();
            if (TextUtils.isEmpty(mac)) {
                return ERROR_MAC_STR;
            } else {
                return mac;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("mac", "get android low version mac error:" + e.getMessage());
            return ERROR_MAC_STR;
        }
    }

    /**
     * 兼容7.0获取不到的问题
     *
     * @return
     */
    public static String getAndroidVersion7MAC() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            Log.e("mac", "get android version 7.0 mac error:" + e.getMessage());
        }
        return ERROR_MAC_STR;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 设备厂商
     * @return 手机制造厂商
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 机型名称
     * @return 机型名称
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 设备名称
     * @return concro
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 基带版本
     * @return 基带版本
     */
    public static String getIncremental() {
        return VERSION.INCREMENTAL;
    }

    /**
     * 系统版本
     * @return 系统版本
     */
    public static String getRelease() {
        return VERSION.RELEASE;
    }

    /**
     * SDK版本
     * @return 实际容量
     */
    public static String getSdk() {
        return VERSION.SDK;
    }

    /**
     * 实际容量
     * @return 实际容量
     */
    public static int getCharge_full() {
        return parseInt(getPro("charge_full")) / 1000;
    }

    public static String getPro(String str){
        String path=new String();
        String content=new String();
        for (String s : BATTERY_PATH) {
            path=s+str;
            if (new File(path).exists()) {
                content= readFile(path);
            }
        }
        if (content.isEmpty()||content.equals("0")&&keys.containsKey(str)  ){
            content=getBattery(str);
        }
        return content;
    }

    /**
     * 设计容量
     * @return
     */
    public static int getCharge_full_design() {
        return (parseInt(getPro("charge_full_design")) / 1000);
    }

    /**
     * 充电限制电流
     * @return
     */
    public static int getConstant_charge_current_max() {
        return parseInt(getPro("constant_charge_current_max")) / 1000;
    }


    /**
     * 充电次数
     * @return
     */
    public static int getCycle_count() {
        return parseInt(getPro("cycle_count"));
    }


    /**
     * 电池健康程度
     * @return
     */
    public static int getHealthy() {
        return getCharge_full()==0?0:100*getCharge_full()/getCharge_full_design();
    }


    /**
     * 电流
     * @return
     */
    public static int getCurrent() {
        String current_now=getPro("current_now");
        return current_now.contains("-")?parseInt(current_now)/1000:0-parseInt(current_now)/1000;
    }

    /**
     * 获取电池信息
     *
     POWER_SUPPLY_NAME=bms
     POWER_SUPPLY_CAPACITY=30
     POWER_SUPPLY_CAPACITY_RAW=77
     POWER_SUPPLY_TEMP=320
     POWER_SUPPLY_VOLTAGE_NOW=3697500
     POWER_SUPPLY_VOLTAGE_OCV=3777837
     POWER_SUPPLY_CURRENT_NOW=440917
     POWER_SUPPLY_RESISTANCE_ID=58000
     POWER_SUPPLY_RESISTANCE=200195
     POWER_SUPPLY_BATTERY_TYPE=sagit_atl
     POWER_SUPPLY_CHARGE_FULL_DESIGN=3349000
     POWER_SUPPLY_VOLTAGE_MAX_DESIGN=4400000
     POWER_SUPPLY_CYCLE_COUNT=69
     POWER_SUPPLY_CYCLE_COUNT_ID=1
     POWER_SUPPLY_CHARGE_NOW_RAW=1244723
     POWER_SUPPLY_CHARGE_NOW=0
     POWER_SUPPLY_CHARGE_FULL=3272000
     POWER_SUPPLY_CHARGE_COUNTER=1036650
     POWER_SUPPLY_TIME_TO_FULL_AVG=26438
     POWER_SUPPLY_TIME_TO_EMPTY_AVG=30561
     POWER_SUPPLY_SOC_REPORTING_READY=1
     POWER_SUPPLY_DEBUG_BATTERY=0
     POWER_SUPPLY_CONSTANT_CHARGE_VOLTAGE=4389899
     POWER_SUPPLY_CC_STEP=0
     POWER_SUPPLY_CC_STEP_SEL=0
     */
    private static String getBattery(String key) {
        String batteryinfos=new String();
            if (new File(BATTERY_PATH[0]+"uevent").exists()) {
                batteryinfos=readFile(BATTERY_PATH[0]+"uevent");
                //判断得到的信息不为空
                if (String.valueOf(batteryinfos).isEmpty()){
                    return "0";
                }
            }else {
                if (new File(BATTERY_PATH[1]+"uevent").exists()) {
                    batteryinfos=readFile(BATTERY_PATH[1]+"uevent");
                    //判断得到的信息不为空
                    if (String.valueOf(batteryinfos).isEmpty()) {
                        return "0";
                    }
                }
            }
        Log.d(TAG, "getBattery: "+batteryinfos.toString() );
        String[] arr = batteryinfos.split("\n");
        Log.e(TAG, "getBattery: key"+key );
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].startsWith(keys.get(key))) {
                    String batteryinfo = arr[i];
                    batteryinfo = batteryinfo.substring(key.length()+1);
                    return batteryinfo;
                }
        }
        return "0";
    }
    /**
     * 电量
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getQuantity(Context context) {
        BatteryManager batteryManager = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
        return  batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    /**
     * 温度
     * @return
     */
    public static Double getTemp() {
        return (Double.parseDouble(getPro("temp")) / 10);
    }


    /**
     * 电压
     * @return
     */
    public static int getVoltage() {
        return parseInt(getPro("voltage_now")) / 1000;
    }

    /**
     * 检查是否有网络
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /**
     *  检查是否是WIFI
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }
        return false;
    }

    /**
     * 检查是否是移动网络
     */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private static NetworkInfo getNetworkInfo(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
    /** 检查SD卡是否存在 */
    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    public static int parseInt(String str) {
        int intValue;
        if (str != null) {
            intValue = Integer.valueOf(str.replaceAll("\\D", "")).intValue();
        } else {
            intValue = 0;
        }
        return intValue;
    }
    /**
     * 读取文件信息
     * @param path
     * @return
     */
    private static String readFile(String path) {
        String content = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(path));
            byte[] bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr);
            fileInputStream.close();
            content = new String(bArr, "UTF-8");
        } catch (IOException e) {
            if (new File(path).exists()&&content.isEmpty()){
                content=RootCmd.execRootCmd("cat "+path,false);
                if (content.isEmpty()){
                    content=RootCmd.execRootCmd("cat "+path,true);
                }
            }
            System.out.println("此设备不支持"+path+"信息读取");}
        return content.isEmpty()?"0":content;
    }

}
