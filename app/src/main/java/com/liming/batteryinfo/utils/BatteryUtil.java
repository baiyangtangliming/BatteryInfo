package com.liming.batteryinfo.utils;


public class BatteryUtil {
    /**
     * 修改充电速度限制
     *
     * @param limit
     * @return
     */
    public static boolean setChargeCurrentMax(int limit) {
        String cmd =
                "echo 0 > /sys/class/power_supply/battery/restricted_charging;" +
                        "echo 0 > /sys/class/power_supply/battery/safety_timer_enabled;" +
                        "chmod 644 /sys/class/power_supply/bms/temp_warm;" +
                        "echo 480 > /sys/class/power_supply/bms/temp_warm;" +
                        "chmod 644 /sys/class/power_supply/battery/constant_charge_current_max;" +
                        "echo " + limit + "000 > /sys/class/power_supply/battery/constant_charge_current_max;";
        return ShellUtils.execCmd(cmd,true).result != -1;
    }

    /**
     * 禁用充电
     *
     * @return
     */
    public static boolean disbleCharge() {
        String cmd = "if [ -f '/sys/class/power_supply/battery/battery_charging_enabled' ]; then echo 0 > /sys/class/power_supply/battery/battery_charging_enabled; else echo 1 > /sys/class/power_supply/battery/input_suspend; fi;setprop vtools.bp 1;\n";
        return ShellUtils.execCmd(cmd,true).result != -1;
    }

    /**
     * 恢复充电
     *
     * @return
     */
    public static boolean resumeCharge() {
        String cmd = "if [ -f '/sys/class/power_supply/battery/battery_charging_enabled' ]; then echo 1 > /sys/class/power_supply/battery/battery_charging_enabled; else echo 0 > /sys/class/power_supply/battery/input_suspend; fi;setprop vtools.bp 0;\n";
        return ShellUtils.execCmd(cmd,true).result != -1;
    }
}
