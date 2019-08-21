package com.liming.batteryinfo.ui;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.service.CurrentWindowService;
import com.liming.batteryinfo.utils.AnnotateUtils;
import com.liming.batteryinfo.utils.BatteryInfo;
import com.liming.batteryinfo.utils.ShellUtils;
import com.liming.batteryinfo.utils.ViewInject;
import com.liming.batteryinfo.view.BatteryView;
import com.liming.batteryinfo.view.DynamicWave;


public class MainFragment extends BaseFragment implements View.OnClickListener {


    private static final String TAG = "MainFragment";


    private View view;

    BatteryInfo batteryInfo;

    @ViewInject(R.id.batteryview)
    private BatteryView batteryView;


    @ViewInject(R.id.dynamicwave)
    private DynamicWave dynamicWave;


    @ViewInject(R.id.battery_current)
    private TextView batteryCurrent;

    @ViewInject(R.id.battery_currenttip)
    private TextView batteryCurrenttip;

    @ViewInject(R.id.device_name)
    private TextView deviceName;

    @ViewInject(R.id.sjbattery)
    private TextView sjbattery;

    @ViewInject(R.id.xdbattery)
    private TextView xdbattery;

    @ViewInject(R.id.device_nametip)
    private TextView deviceNametip;

    @ViewInject(R.id.batteryviewtip)
    private TextView batteryviewtip;

    @ViewInject(R.id.temp)
    private TextView temp;

    @ViewInject(R.id.voltage)
    private TextView voltage;

    @ViewInject(R.id.tv_technology)
    private TextView tvTechnology;//电池工艺

    @ViewInject(R.id.tv_charge_count)
    private TextView tvChargeCount;//充电循环次数

    @ViewInject(R.id.tv_health)
    private TextView tvHealth;//健康状况


    /**
     * 工具箱
     */
    @ViewInject(R.id.batteryview)
    private BatteryView verticalBattery;//悬浮窗

    @ViewInject(R.id.rl_current_windown)
    RelativeLayout rlCurrentWindown;


    @ViewInject(R.id.rl_health_item)
    RelativeLayout rlHealthItem;//电池健康程度

    @ViewInject(R.id.tv_health_num)
    TextView tvHealthNum;

    @ViewInject(R.id.battery_change_item)
    RelativeLayout batteryChangeItem;//改变电量

    @ViewInject(R.id.tv_battery_change_num)
    TextView tvBatteryChangeNum;


    ValueAnimator valueAnimator;



    private static int SETVIEWDATA = 1;//设置视图数据

    Handler mTimeHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == SETVIEWDATA) {
                setViewData();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        batteryInfo = BatteryInfo.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        AnnotateUtils.bindView(view);

        initView();
        return view;
    }


    /**
     * 初始化视图数据
     */
    public void initView() {
        deviceName.setText(Build.BRAND + " " + Build.MODEL);
        deviceNametip.setText("Android" + Build.VERSION.RELEASE + " SDK" + Build.VERSION.SDK_INT);
        rlCurrentWindown.setOnClickListener(this);
        rlHealthItem.setOnClickListener(this);
        batteryChangeItem.setOnClickListener(this);

        setFonts();
        setViewData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser);
    }


    @Override
    public void onResume() {//和activity的onResume绑定，Fragment初始化的时候必调用，但切换fragment的hide和visible的时候可能不会调用！
        super.onResume();
        if (isAdded() && !isHidden()) {//用isVisible此时为false，因为mView.getWindowToken为null
            Log.d(TAG, "主界面进入可见状态: ");
            String theme = (String) getParam("theme", "0");
            if (theme.equals("0")) {

                if (valueAnimator != null) {
                    valueAnimator.removeAllUpdateListeners();
                }

                dynamicWave.clearAnimation();
                valueAnimator = startAnimation(dynamicWave);
            } else {
                dynamicWave.clearAnimation();
                dynamicWave.setBackgroundColor(Color.parseColor(theme));
            }

        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {//默认fragment创建的时候是可见的，但是不会调用该方法！切换可见状态的时候会调用，但是调用onResume，onPause的时候却不会调用
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d(TAG, "onHiddenChanged: " + hidden);
        } else {
            Log.d(TAG, "onHiddenChanged: " + hidden);
        }
    }

    /**
     * 设置视图数据
     */
    public void setViewData() {

        //上部分
        batteryCurrent.setText(batteryInfo.getCurrent() + "mA");//当前电流
        batteryCurrenttip.setText(batteryInfo.isCharging() ? "正在充电" : "正在放电");
        sjbattery.setText("设计容量：" + batteryInfo.getBatteryCapacity(getActivity()) + " mAh");
        xdbattery.setText("实际容量：" + (batteryInfo.getChargeFull() / 1000) + " mAh");

        //中间部分
        if (batteryInfo.getChargeFull() != 0 && batteryInfo.getChargeCounter() != 0) {
            batteryviewtip.setText(batteryInfo.getQuantity() + "%");//电量百分比
        } else {

        }

        batteryView.setPower(batteryInfo.getQuantity());

        temp.setText((batteryInfo.getTemperature() / 10d) + "℃");
        voltage.setText((batteryInfo.getVoltage() / 1000d) + "V");

        tvTechnology.setText(batteryInfo.getTechnology());//电池工艺
        tvChargeCount.setText(batteryInfo.getCycleCount() + " 次");
        tvHealth.setText(batteryInfo.getHealth());

        //工具部分
        tvHealthNum.setText(((batteryInfo.getChargeFull() / 10) / batteryInfo.getBatteryCapacity(getActivity())) + "%");
        tvBatteryChangeNum.setText(batteryInfo.getQuantity() + "%");

        //一秒更新一次
        mTimeHandler.sendEmptyMessageDelayed(SETVIEWDATA, 1000);

    }


    /**
     * 设置字体
     */
    private void setFonts() {
        Typeface mtypeface = Typeface.createFromFile("/system/fonts/Roboto-Thin.ttf");
        batteryviewtip.setTypeface(mtypeface);
        temp.setTypeface(mtypeface);
        voltage.setTypeface(mtypeface);
        tvHealthNum.setTypeface(mtypeface);
        tvBatteryChangeNum.setTypeface(mtypeface);

    }


    /**
     * 显示悬浮窗
     */
    private void showCurrentWindown() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
            //若没有权限，提示获取.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Toast.makeText(getActivity(), "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show();
            startActivity(intent);

        } else {
            Intent intent = new Intent(getActivity(), CurrentWindowService.class);
            Toast.makeText(getActivity(), "已开启电流悬浮窗", Toast.LENGTH_SHORT).show();
            getActivity().startService(intent);
        }
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_current_windown:
                showCurrentWindown();
                break;
            case R.id.rl_health_item:
                new AlertDialog.Builder(getActivity()).setTitle("温馨提示")//设置对话框标题
                        .setMessage("确定要清除电池信息吗？清除成功后会自动重启！")//设置显示的内容
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                if (ShellUtils.execCmd("rm -f /data/system/batterystats-checkin.bin;rm -f /data/system/batterystats-daily.xml;rm -f /data/system/batterystats.bin;reboot;\n", true).result != -1) {
                                    Toast.makeText(getActivity(), "清空电池信息成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "清空电池信息失败", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).setNegativeButton("取消", null).show();//在按键响应事件中显示此对话框

                break;

            case R.id.battery_change_item:

                Intent intent = new Intent(getActivity(), BatterySettingActivity.class);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }

}
