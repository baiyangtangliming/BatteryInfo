package com.liming.batteryinfo.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.adapter.ToolAdapter;
import com.liming.batteryinfo.entity.ToolBean;
import com.liming.batteryinfo.listener.GridViewItemClickListener;
import com.liming.batteryinfo.utils.BatteryUtil;
import com.liming.batteryinfo.utils.RootCmd;
import com.liming.batteryinfo.utils.SystemInfo;
import com.liming.batteryinfo.utils.ViewInject;
import com.liming.batteryinfo.view.BatteryView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    @ViewInject(R.id.linearLayout)
    LinearLayout linearLayout;
    @ViewInject(R.id.battery_current)
    TextView battery_current;
    @ViewInject(R.id.battery_currenttip)
    TextView battery_currenttip;
    @ViewInject(R.id.device_name)
    TextView device_name;
    @ViewInject(R.id.sjbattery)
    TextView sjbattery;
    @ViewInject(R.id.xdbattery)
    TextView xdbattery;
    @ViewInject(R.id.device_nametip)
    TextView device_nametip;
    @ViewInject(R.id.batteryviewtip)
    TextView batteryviewtip;
    @ViewInject(R.id.temp)
    TextView temp;
    @ViewInject(R.id.voltage)
    TextView voltage;
    @ViewInject(R.id.gridview)
    GridView gridView;
    @ViewInject(R.id.batteryview)
    private BatteryView verticalBattery;
    @ViewInject(R.id.abouttip)
    TextView abouttip;
    ToolAdapter toolAdapter;
    static int charge_current_max;
    static int quantity;
    static int health;
    public static int color;
    public static int donext = 0;
    private static int[] colors = {
            R.color.app_color_theme_1,
            R.color.app_color_theme_2,
            R.color.app_color_theme_3,
            R.color.app_color_theme_4,
            R.color.app_color_theme_5,
            R.color.app_color_theme_6,
            R.color.app_color_theme_7,
            R.color.app_color_theme_8,
            R.color.app_color_theme_9
    };
    public static int stopnum = 101;
    static int stopdo = 0;
    public final ArrayList<ToolBean> listItemArrayList = new ArrayList<ToolBean>();
    Handler mTimeHandler = new Handler() {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 0 && donext == 0) {
                initViews();
                sendEmptyMessageDelayed(0, 1000);
            }
            if (message.what == 1) {
                if (color >= colors.length - 1) {
                    startAnimation(linearLayout, colors[colors.length - 1], colors[0]);
                    color = 0;
                } else {
                    startAnimation(linearLayout, colors[color], colors[++color]);
                }
                sendEmptyMessageDelayed(1, 3000);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            stopnum = data.getIntExtra("stopnum", 101);
            stopdo = data.getIntExtra("stopdo", 1);
            listItemArrayList.get(3).setItemTip(data.getStringExtra("memo"));
            listItemArrayList.get(3).setItemNum(String.valueOf(stopnum) + "%");
            toolAdapter.notifyDataSetChanged();
        }
    }

    private void initListItem() {
        listItemArrayList.add(
                new ToolBean("电池健康程度（仅供参考）",
                        "数据不准确？点击进行电量校准",
                        health + "%"));

        listItemArrayList.add(
                new ToolBean("电池电量百分比",
                        "点击可修改电量百分比及充电状态",
                        quantity + "%"));
        listItemArrayList.add(
                new ToolBean("电池充电最大电流",
                        "点击可突破限制",
                        charge_current_max + "mA"));
        listItemArrayList.add(
                new ToolBean("定量停冲(实验性)",
                        "电量冲至指定电量禁止充电",
                        stopnum + "%"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimeHandler.sendEmptyMessage(1);
        mTimeHandler.sendEmptyMessage(0);
        Typeface mtypeface = Typeface.createFromFile("/system/fonts/Roboto-Thin.ttf");
        batteryviewtip.setTypeface(mtypeface);
        temp.setTypeface(mtypeface);
        voltage.setTypeface(mtypeface);
        abouttip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });
        initListItem();
        //生成适配器的ImageItem 与动态数组的元素相对应
        toolAdapter = new ToolAdapter(this, listItemArrayList);
        //添加并且显示
        gridView.setAdapter(toolAdapter);
        //添加消息处理
        gridView.setOnItemClickListener(new GridViewItemClickListener(this));

    }

    private static int power;

    private void initViews() {

        device_name.setText(SystemInfo.getBrand() + " " + SystemInfo.getModel());
        device_nametip.setText(SystemInfo.getDevice() + " Android" + SystemInfo.getRelease() + " SDK" + SystemInfo.getSdk());
        new Thread(new Runnable() {
            @Override
            public void run() {
                donext = 1;
                final long start = System.currentTimeMillis();
                final int current = SystemInfo.getCurrent();
                final Double tempstr = SystemInfo.getTemp();
                final int voltagenum = SystemInfo.getVoltage();
                final int charge_full_design = SystemInfo.getCharge_full_design();
                final int charge_full = SystemInfo.getCharge_full();
                final int cycle_count = SystemInfo.getCycle_count();
                final int charge_current_max = SystemInfo.getConstant_charge_current_max();
                final int quantity = SystemInfo.getQuantity(getBaseContext());
                final int health = (charge_full == 0 ? 0 : 100 * charge_full / charge_full_design);
                //向Handler发送处理操作
                mTimeHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setView(charge_full_design, charge_full, tempstr, voltagenum, current, cycle_count, charge_current_max, quantity, health);
                    }
                });
            }
        }).start();
    }

    public void setView(int charge_full_design, int charge_full, Double tempstr, int voltagenum, int current, int cycle_count, int charge_current_max, int quantity, int health) {
        try {
            sjbattery.setText("设计容量" + charge_full_design + "mA");
            xdbattery.setText("实际容量" + charge_full + "mA");
            temp.setText(tempstr + "℃");
            voltage.setText(voltagenum + "mV");
            batteryviewtip.setText(quantity + "%");
            battery_current.setText(current + ("mA"));
            battery_currenttip.setText(((current > 0) ? "正在充电" : "正在放电") + " 循环充电" + cycle_count + "次");
            if (quantity != 100 && current > 0) {
                if (power >= 100) {
                    power = 0;
                }
                verticalBattery.setPower(power += 5);
            } else {
                verticalBattery.setPower(quantity);
            }
            if (MainActivity.health != health || MainActivity.charge_current_max != charge_current_max || MainActivity.quantity != quantity) {
                MainActivity.health = health;
                MainActivity.charge_current_max = charge_current_max;
                MainActivity.quantity = quantity;
                listItemArrayList.get(0).setItemNum(health + "%");
                listItemArrayList.get(1).setItemNum(quantity + "%");
                listItemArrayList.get(2).setItemNum(charge_current_max + "mA");
                listItemArrayList.get(3).setItemNum(stopnum + "%");
                toolAdapter.notifyDataSetChanged();
            }
            if (quantity >= stopnum) {
                boolean bool = BatteryUtil.disbleCharge();
                if (bool && stopdo == 0) {
                    System.exit(1);
                } else if (bool && stopdo == 1) {
                    RootCmd.execRootCmdSilent("reboot -p\n");
                } else if (bool && stopdo == 2) {
                    RootCmd.execRootCmdSilent("reboot\n");
                }
            }
            donext = 0;
        } catch (Exception e) {
        }
    }

}
