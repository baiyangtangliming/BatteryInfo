package com.liming.batteryinfo.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.AnnotateUtils;
import com.liming.batteryinfo.utils.BatteryInfo;
import com.liming.batteryinfo.utils.ViewInject;
import com.liming.batteryinfo.view.BatteryWaveView;
import com.liming.batteryinfo.view.DynamicWave;


public class ChargeFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ChargeFragment";


    BatteryInfo batteryInfo;

    private View view;


    @ViewInject(R.id.dynamicwave)
    private DynamicWave dynamicWave;

    @ViewInject(R.id.batterywaveview)
    BatteryWaveView batteryWaveView;

    @ViewInject(R.id.tv_voltage)
    TextView tvVoltage;

    @ViewInject(R.id.tv_temp)
    TextView tvTemp;


    @ViewInject(R.id.rl_stop_charge_item)
    RelativeLayout rlStopChargeItem;


    private static int SETVIEWDATA = 1;//设置视图数据

    Handler mTimeHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == SETVIEWDATA) {
                setViewData();
            }
        }
    };

    /**
     * 设置视图数据
     */
    private void setViewData() {

        batteryWaveView.setProgress(batteryInfo.getQuantity());
        batteryWaveView.setmCurrentText(batteryInfo.getCurrent() + "mA");
        batteryWaveView.setmPowerText(String.format("%.1f", (batteryInfo.getCurrent() * batteryInfo.getVoltage() / 1000000f)) + "W");


        tvTemp.setText(("电池温度：" + batteryInfo.getTemperature() / 10d) + "℃");
        tvVoltage.setText(("电池电压：" + batteryInfo.getVoltage() / 1000d) + "V");


        //一秒更新一次
        mTimeHandler.sendEmptyMessageDelayed(SETVIEWDATA, 1000);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        batteryInfo = BatteryInfo.getInstance(getActivity());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_charge, container, false);
        AnnotateUtils.bindView(view);

        initView();
        return view;
    }


    @Override
    public void onResume() {//和activity的onResume绑定，Fragment初始化的时候必调用，但切换fragment的hide和visible的时候可能不会调用！
        super.onResume();
        if (isAdded() && !isHidden()) {//用isVisible此时为false，因为mView.getWindowToken为null
            Log.d(TAG, "充电界面进入可见状态: ");
            String theme = (String) getParam("theme", "0");
            if (theme.equals("0")) {

                if (dynamicWave.animate() == null){
                    dynamicWave.clearAnimation();
                    startAnimation(dynamicWave);
                }

            } else {
                dynamicWave.clearAnimation();
                dynamicWave.setBackgroundColor(Color.parseColor(theme));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "充电界面进入停止状态: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "充电界面进入暂停状态: ");

    }

    /**
     * 初始化视图数据
     */
    public void initView() {
        rlStopChargeItem.setOnClickListener(this);
        setViewData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {
            case R.id.rl_stop_charge_item:
                intent  = new Intent(getActivity(),TimeChargeActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }
}
