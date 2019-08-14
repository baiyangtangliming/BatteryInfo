package com.liming.batteryinfo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.liming.batteryinfo.utils.BatteryInfo;
import com.liming.batteryinfo.utils.ShellUtils;

import java.util.HashMap;
import java.util.Map;

public class BatteryInfoService extends Service {

    private static final String TAG = "BatteryInfoService";

    private static final String[] BATTERY_PATH = {
            "/sys/class/power_supply/bms/uevent",
            "/sys/class/power_supply/Battery/uevent"
    };//电池信息路径


    //不与Activity进行绑定.
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MainService Created");
        mTimeHandler.sendEmptyMessage(1);
    }


    Handler mTimeHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        catBmsInfo();
                        catChargeCurrentMax();
                    }
                }).start();
                mTimeHandler.sendEmptyMessageDelayed(1, 1000);
            }


        }

    };


    /**
     * 获取最大充电电流
     */
    private void catChargeCurrentMax() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd("cat /sys/class/power_supply/battery/constant_charge_current_max", false);
        //Log.d(TAG, "catChargeCurrentMax:============================》\n "+commandResult.successMsg);
        if (TextUtils.isEmpty(commandResult.successMsg)) {
            return;
        }
        BatteryInfo.setChargeCurrentMax(commandResult.successMsg);
    }


    /**
     * 读取电池信息
     * sagit:/ $ cat /sys/class/power_supply/bms/uevent
     * POWER_SUPPLY_NAME=bms
     * POWER_SUPPLY_CAPACITY=70
     * POWER_SUPPLY_TEMP=360
     * POWER_SUPPLY_VOLTAGE_NOW=4137440
     * POWER_SUPPLY_VOLTAGE_OCV=4010748
     * POWER_SUPPLY_CURRENT_NOW=-1202147
     * POWER_SUPPLY_RESISTANCE_ID=67000
     * POWER_SUPPLY_RESISTANCE=145263
     * POWER_SUPPLY_BATTERY_TYPE=c1_atl
     * POWER_SUPPLY_CHARGE_FULL_DESIGN=3350000
     * POWER_SUPPLY_VOLTAGE_MAX_DESIGN=4400000
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

    private void catBmsInfo() {

        String uevent = null;

        //获取到event的信息
        for (String path : BATTERY_PATH) {
            ShellUtils.CommandResult commandResult = ShellUtils.execCmd("cat " + path, false);
            if (commandResult.result != -1 && !TextUtils.isEmpty(commandResult.successMsg)) {
                uevent = commandResult.successMsg;
                break;
            }
        }

        Map<String, String> ueventMap = new HashMap<>();

        if (TextUtils.isEmpty(uevent)) {
            return;
        }
        //Log.d(TAG, "catBmsInfo:============================》\n "+uevent);
        String[] arr = uevent.split("\n");
        for (String ueventLine : arr) {
            if (TextUtils.isEmpty(ueventLine) || !ueventLine.contains("=")) {
                continue;
            }

            String[] key = ueventLine.split("=");
            if (key!=null && key.length > 1){
                ueventMap.put(key[0], key[1]);
            }

        }


        BatteryInfo.setCatInfo(ueventMap);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
