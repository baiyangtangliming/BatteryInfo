package com.liming.batteryinfo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.liming.batteryinfo.utils.BatteryInfo;

public class BatteryInfoBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //获取当前电量
        BatteryInfo.setBroadcastInfo(intent);
    }

}

