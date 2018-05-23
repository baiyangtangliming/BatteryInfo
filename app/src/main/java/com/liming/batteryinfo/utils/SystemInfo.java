package com.liming.batteryinfo.utils;

/**
 * Created by tlm on 2018/5/5.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.BATTERY_SERVICE;

final public class SystemInfo {

    private static long lastreadtime;
    private static Map<String, String> keys = new HashMap<String, String>() {
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
    private static Map<String, String> values = new HashMap<String, String>() {
        {
            put("charge_full", "0");
            put("charge_full_design", "0");
            put("temp", "0");//温度
            put("current_now", "0");//电流
            put("voltage_now", "0");//电压
            put("cycle_count", "0");//充电循环次数
            put("constant_charge_current_max", "0");//充电限制电流
        }
    };
    private static final String[] BATTERY_PATH = {
            "/sys/class/power_supply/bms/",
            "/sys/class/power_supply/battery/"
    };//电池信息路径

    /**
     * 设备厂商
     *
     * @return 手机制造厂商
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 机型名称
     *
     * @return 机型名称
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 设备名称
     *
     * @return concro
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 系统版本
     *
     * @return 系统版本
     */
    public static String getRelease() {
        return VERSION.RELEASE;
    }

    /**
     * SDK版本
     *
     * @return 实际容量
     */
    public static String getSdk() {
        return VERSION.SDK;
    }

    /**
     * 实际容量
     *
     * @return 实际容量
     */
    public static int getCharge_full() {
        return parseInt(getPro("charge_full")) / 1000;
    }

    public static String getPro(String str) {
        String path = new String();
        String content = new String();
        for (String s : BATTERY_PATH) {
            path = s + str;
            if (new File(path).exists()) {
                content = readFile(path);
                if (!content.isEmpty() && !content.startsWith("0")) {
                    return content;
                }
            }
        }

        if (content.isEmpty() || content.startsWith("0") && keys.containsKey(str)) {
            if ((System.currentTimeMillis() - lastreadtime) >= 1000) {
                getBattery();
            }
            content = values.get(str);
        }
        return content;
    }

    /**
     * 设计容量
     *
     * @return
     */
    public static int getCharge_full_design() {
        return (parseInt(getPro("charge_full_design")) / 1000);
    }

    /**
     * 充电限制电流
     *
     * @return
     */
    public static int getConstant_charge_current_max() {
        return parseInt(getPro("constant_charge_current_max")) / 1000;
    }


    /**
     * 充电次数
     *
     * @return
     */
    public static int getCycle_count() {
        return parseInt(getPro("cycle_count"));
    }

    /**
     * 电流
     *
     * @return
     */
    public static int getCurrent() {
        String current_now = getPro("current_now");
        return current_now.contains("-") ? parseInt(current_now) / 1000 : 0 - parseInt(current_now) / 1000;
    }

    /**
     * 获取电池信息
     */
    private static String getBattery() {
        String batteryinfos = new String();
        if (new File(BATTERY_PATH[0] + "uevent").exists()) {
            batteryinfos = RootCmd.getProp(BATTERY_PATH[0] + "uevent");
            //判断得到的信息不为空
            if (batteryinfos.isEmpty()) {
                return "0";
            }
        } else {
            batteryinfos = RootCmd.getProp(BATTERY_PATH[1] + "uevent");
            //判断得到的信息不为空
            if (batteryinfos.isEmpty()) {
                return "0";
            }
        }
        String[] arr = batteryinfos.split("\n");
        for (int i = 0; i < arr.length; i++) {
            for (String key : keys.keySet()) {
                if (arr[i].startsWith(keys.get(key))) {
                    values.put(key, arr[i].split("=")[1]);
                }
            }
            lastreadtime = System.currentTimeMillis();

        }
        return "0";
    }

    /**
     * 电量
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getQuantity(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    /**
     * 温度
     *
     * @return
     */
    public static Double getTemp() {
        return (Double.parseDouble(getPro("temp")) / 10);
    }


    /**
     * 电压
     *
     * @return
     */
    public static int getVoltage() {
        return parseInt(getPro("voltage_now")) / 1000;
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
     *
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
        } catch (Exception e) {
        }
        if (new File(path).exists() && content.isEmpty()) {
            content = RootCmd.getProp(path, false);
        }
        return content.isEmpty() ? "0" : content;
    }

}
