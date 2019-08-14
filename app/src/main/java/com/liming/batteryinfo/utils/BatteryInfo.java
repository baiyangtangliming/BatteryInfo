package com.liming.batteryinfo.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;

import java.util.Map;

import static android.content.Context.BATTERY_SERVICE;
import static android.os.BatteryManager.*;

public class BatteryInfo {

    private static BatteryInfo batteryInfo;

    private BatteryManager batteryManager;


    private static int status;
    private static int health;//获得健康
    private static boolean present;
    private static int level;//电量
    private static int scale;
    private static int plugged;//
    private static int voltage;//电压
    private static int temperature; // 温度的单位是10℃
    private static String technology;//电池工艺


    /**
     * 单例模式
     *
     * @param context
     * @return
     */
    public static BatteryInfo getInstance(Context context) {

        if (batteryInfo == null) {
            batteryInfo = new BatteryInfo();
            batteryInfo.batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        }

        return batteryInfo;
    }

    /**
     * 设置电池信息
     *
     * @param intent
     */
    public static void setBroadcastInfo(Intent intent) {
        status = intent.getIntExtra("status", 0);
        health = intent.getIntExtra("health", 1);
        present = intent.getBooleanExtra("present", false);
        level = intent.getIntExtra("level", 0);
        scale = intent.getIntExtra("scale", 0);
        plugged = intent.getIntExtra("plugged", 0);
        voltage = intent.getIntExtra("voltage", 0);
        temperature = intent.getIntExtra("temperature", 0); // 温度的单位是10℃
        technology = intent.getStringExtra("technology");
    }
    /**
     * 设置电池信息
     * sagit:/ $ cat /sys/class/power_supply/bms/uevent
     * POWER_SUPPLY_NAME=bms
     * POWER_SUPPLY_CAPACITY=70//电量
     * POWER_SUPPLY_TEMP=360//温度
     * POWER_SUPPLY_VOLTAGE_NOW=4137440//现在的电压
     * POWER_SUPPLY_VOLTAGE_OCV=4010748//开路电压
     * POWER_SUPPLY_CURRENT_NOW=-1202147//电流
     * POWER_SUPPLY_RESISTANCE_ID=67000
     * POWER_SUPPLY_RESISTANCE=145263
     * POWER_SUPPLY_BATTERY_TYPE=c1_atl
     * POWER_SUPPLY_CHARGE_FULL_DESIGN=3350000
     * POWER_SUPPLY_VOLTAGE_MAX_DESIGN=4400000//设计最大充电电压
     * POWER_SUPPLY_CYCLE_COUNT=151
     * POWER_SUPPLY_CYCLE_COUNT_ID=1
     * POWER_SUPPLY_CHARGE_NOW_RAW=-1190400
     * POWER_SUPPLY_CHARGE_NOW=317457
     * POWER_SUPPLY_CHARGE_FULL=2187885
     * POWER_SUPPLY_CHARGE_COUNTER=1673935
     * POWER_SUPPLY_TIME_TO_FULL_AVG=1867
     * POWER_SUPPLY_TIME_TO_EMPTY_AVG=43983
     * POWER_SUPPLY_SOC_REPORTING_READY=1
     * POWER_SUPPLY_DEBUG_BATTERY=0
     * POWER_SUPPLY_CONSTANT_CHARGE_VOLTAGE=4389899
     * POWER_SUPPLY_CC_STEP=0
     * POWER_SUPPLY_CC_STEP_SEL=0
     */


    private static int POWER_SUPPLY_CAPACITY;//电量
    private static int POWER_SUPPLY_CHARGE_FULL;//剩余容量
    private static int POWER_SUPPLY_CHARGE_FULL_DESIGN;//设计容量
    private static int POWER_SUPPLY_TEMP;//温度
    private static int POWER_SUPPLY_CURRENT_NOW;//电流
    private static int POWER_SUPPLY_VOLTAGE_NOW;//电压
    private static int POWER_SUPPLY_CYCLE_COUNT;//充电循环次数

    private static int constant_charge_current_max;//最大充电电流

    public static void setCatInfo(Map<String, String> ueventMap) {

        if (ueventMap.containsKey("POWER_SUPPLY_CAPACITY"))POWER_SUPPLY_CAPACITY = Integer.parseInt(ueventMap.get("POWER_SUPPLY_CAPACITY"));

        if (ueventMap.containsKey("POWER_SUPPLY_CHARGE_FULL"))POWER_SUPPLY_CHARGE_FULL = Integer.parseInt(ueventMap.get("POWER_SUPPLY_CHARGE_FULL"));

        if (ueventMap.containsKey("POWER_SUPPLY_CHARGE_FULL_DESIGN"))POWER_SUPPLY_CHARGE_FULL_DESIGN = Integer.parseInt(ueventMap.get("POWER_SUPPLY_CHARGE_FULL_DESIGN"));

        if (ueventMap.containsKey("POWER_SUPPLY_TEMP"))POWER_SUPPLY_TEMP = Integer.parseInt(ueventMap.get("POWER_SUPPLY_TEMP"));

        if (ueventMap.containsKey("POWER_SUPPLY_CURRENT_NOW"))POWER_SUPPLY_CURRENT_NOW = Integer.parseInt(ueventMap.get("POWER_SUPPLY_CURRENT_NOW"));

        if (ueventMap.containsKey("POWER_SUPPLY_VOLTAGE_NOW"))POWER_SUPPLY_VOLTAGE_NOW = Integer.parseInt(ueventMap.get("POWER_SUPPLY_VOLTAGE_NOW"));

        if (ueventMap.containsKey("POWER_SUPPLY_CYCLE_COUNT"))POWER_SUPPLY_CYCLE_COUNT = Integer.parseInt(ueventMap.get("POWER_SUPPLY_CYCLE_COUNT"));

    }

    public  String getHealth() {

        String healthName = "未知状态";

        switch (health){
            case BATTERY_HEALTH_COLD:
                healthName = "温度过低";
                break;
            case BATTERY_HEALTH_DEAD:
                healthName = "报废状态";
                break;
            case BATTERY_HEALTH_GOOD:
                healthName = "电池良好";
                break;
            case BATTERY_HEALTH_OVERHEAT:
                healthName = "电池过热";
                break;
            case BATTERY_HEALTH_OVER_VOLTAGE:
                healthName = "电压过高";
                break;
        }

        return healthName;
    }

    /**
     * 获得充电电流
     */
    public  int getChargeCurrentMax( ) {
        return constant_charge_current_max;
    }

    /**
     * 获得电池工艺
     */
    public  String getTechnology( ) {
        return technology;
    }
    /**
     * 设置充电电流
     * @param successMsg
     */
    public static void setChargeCurrentMax(String successMsg) {
        if (!TextUtils.isEmpty(successMsg)){
            constant_charge_current_max = Integer.parseInt(successMsg);
        }
    }


    /**
     * 剩余容量
     *
     * @return
     */
    public int getChargeFull() {
        return POWER_SUPPLY_CHARGE_FULL;
    }

    /**
     * 充电循环次数
     *
     * @return
     */
    public int getCycleCount() {
        return POWER_SUPPLY_CYCLE_COUNT;
    }

    /**
     * 电量
     *
     * @return
     */
    public int getQuantity() {

        int intProperty = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        if (intProperty == 0 ){
            intProperty = level;
        }

        if (intProperty == 0){
            intProperty = POWER_SUPPLY_CAPACITY;
        }

        return intProperty;
    }

    /**
     * 电流 毫安
     *
     * @return
     */
    public int getCurrent() {

        if (POWER_SUPPLY_CURRENT_NOW == 0){
            POWER_SUPPLY_CURRENT_NOW = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        }

        return (0 - POWER_SUPPLY_CURRENT_NOW) / 1000;
    }

    /**
     * 剩余容量 微安时
     *
     * @return
     */
    public int getChargeCounter() {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
    }

    /**
     * 是否在充电
     *
     * @return
     */
    public boolean isCharging() {
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    /**
     * 电压
     *
     * @return
     */
    public int getVoltage() {
        if (voltage == 0){
            voltage = POWER_SUPPLY_VOLTAGE_NOW;
        }
        return voltage;
    }

    /**
     * 电池转态
     *
     * @return
     */
    public int getStatus() {
        return status;
    }
    /**
     * 电池温度
     *
     * @return
     */
    public int getTemperature() {
        return temperature;
    }


    /**
     * 获取电池容量 mAh
     * <p>
     * 源头文件:frameworks/base/core/res\res/xml/power_profile.xml
     * <p>
     * Java 反射文件：frameworks\base\core\java\com\android\internal\os\PowerProfile.java
     */
    public int getBatteryCapacity(Context context) {

        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }



        if ((int)batteryCapacity == 0){
            return (POWER_SUPPLY_CHARGE_FULL_DESIGN/1000);
        }


        return (int)batteryCapacity;
    }



}
