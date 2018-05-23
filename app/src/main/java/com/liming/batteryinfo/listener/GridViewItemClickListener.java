package com.liming.batteryinfo.listener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.liming.batteryinfo.ui.BatterySettingActivity;
import com.liming.batteryinfo.ui.MainActivity;
import com.liming.batteryinfo.ui.MaxCurrentSettingActivity;
import com.liming.batteryinfo.ui.TimeChargeActivity;
import com.liming.batteryinfo.utils.RootCmd;
import com.liming.batteryinfo.utils.SystemInfo;

public class GridViewItemClickListener implements AdapterView.OnItemClickListener {
    MainActivity mainActivity;

    public GridViewItemClickListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (SystemInfo.getQuantity(mainActivity) == 100) {
                    new AlertDialog.Builder(mainActivity).setTitle("温馨提示")//设置对话框标题
                            .setMessage("确定要清除电池信息吗？清除成功后会自动重启！")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    if (RootCmd.execRootCmdSilent("rm -f /data/system/batterystats-checkin.bin;rm -f /data/system/batterystats-daily.xml;rm -f /data/system/batterystats.bin;reboot;\n") != -1) {
                                        Toast.makeText(mainActivity, "清空电池信息成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mainActivity, "清空电池信息失败", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }).setNegativeButton("取消", null).show();//在按键响应事件中显示此对话框
                } else {
                    Toast.makeText(mainActivity, "请将电量充满再执行操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                Intent intent = new Intent(mainActivity, BatterySettingActivity.class);
                mainActivity.startActivity(intent);
                break;
            case 2:
                mainActivity.startActivity(new Intent(mainActivity, MaxCurrentSettingActivity.class));
                break;
            case 3:
                mainActivity.startActivityForResult(new Intent(mainActivity, TimeChargeActivity.class).putExtra("stopnum", mainActivity.stopnum), 1);
                break;
            default:
                Toast.makeText(mainActivity, mainActivity.listItemArrayList.get(position).getItemTool().toString() + "功能加紧开发中", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
