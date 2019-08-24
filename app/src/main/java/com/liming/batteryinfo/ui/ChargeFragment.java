package com.liming.batteryinfo.ui;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.AnnotateUtils;
import com.liming.batteryinfo.utils.BatteryInfo;
import com.liming.batteryinfo.utils.ViewInject;
import com.liming.batteryinfo.view.BatteryWaveView;


public class ChargeFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ChargeFragment";


    BatteryInfo batteryInfo;

    private View view;

    @ViewInject(R.id.batterywaveview)
    BatteryWaveView batteryWaveView;

    @ViewInject(R.id.tv_voltage)
    TextView tvVoltage;

    @ViewInject(R.id.tv_temp)
    TextView tvTemp;

    @ViewInject(R.id.tv_capacity_now)
    TextView tvCapacityNow;

    @ViewInject(R.id.tv_capacity_full)
    TextView tvCapacityFull;


    @ViewInject(R.id.ll_charging_reminder)
    LinearLayout llChargingReminder;

    @ViewInject(R.id.ll_electricity)
    LinearLayout llElectricity;

    @ViewInject(R.id.ll_max_current)
    LinearLayout llMaxCurrent;

    @ViewInject(R.id.ll_other_settings)
    LinearLayout llOtherSettnigs;

    @ViewInject(R.id.ll_quantitative_stop)
    LinearLayout llQuantitativeStop;

    @ViewInject(R.id.ll_smart_charging)
    LinearLayout llSmartCharging;


    @ViewInject(R.id.tv_charging_reminder)
    TextView tvChargingReminder;

    @ViewInject(R.id.tv_electricity)
    TextView tvElectricity;


    @ViewInject(R.id.tv_electricity_num)
    TextView tvElectricityNum;

    @ViewInject(R.id.tv_max_current)
    TextView tvMaxCurrent;

    @ViewInject(R.id.tv_quantitative_stop)
    TextView tvQuantitativeStop;

    @ViewInject(R.id.tv_smart_charging)
    TextView tvSmartCharging;


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

        if (!isAdded() || isHidden()) {
            return;
        }

        batteryWaveView.setProgress(batteryInfo.getQuantity());
        batteryWaveView.setmCurrentText(batteryInfo.getCurrent() + "mA");
        batteryWaveView.setmPowerText(String.format("%.1f", (batteryInfo.getCurrent() * batteryInfo.getVoltage() / 1000000f)) + "W");


        tvTemp.setText((batteryInfo.getTemperature() / 10d) + "℃");
        tvVoltage.setText(String.format("%.2f", (batteryInfo.getVoltage() / 1000d)) + "V");
        tvCapacityNow.setText((batteryInfo.getChargeCounter() / 1000) + "mA");
        Integer capacityFull = (Integer) getParam("capacityFull", 0);
        tvCapacityFull.setText(capacityFull == 0 ? "请满充电" : (capacityFull + "mA"));


        if (batteryInfo.getQuantity() == 100) {
            setParam("capacityFull", (batteryInfo.getChargeCounter() / 1000));
        }


        tvElectricityNum.setText(batteryInfo.getQuantity()+"%");

        if (capacityFull > 0 && batteryInfo.isCharging()){
            tvElectricity.setText("预计"+((capacityFull*1000 - batteryInfo.getChargeCounter())/batteryInfo.getCurrent()/60)+"分钟后充满");
        }else if (capacityFull == 0 && batteryInfo.isCharging()){
            tvElectricity.setText("循环充电一次后可可计算充电速度");
        }else {
            tvElectricity.setText("预计可使用"+(batteryInfo.getChargeCounter()/batteryInfo.getCurrent()/60)+"分钟");
        }



        tvMaxCurrent.setText("最大充电电流"+(batteryInfo.getChargeCurrentMax() / 1000)+"mA");

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

            setViewData();

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
        llChargingReminder.setOnClickListener(this);
        llElectricity.setOnClickListener(this);
        llMaxCurrent.setOnClickListener(this);
        llOtherSettnigs.setOnClickListener(this);
        llQuantitativeStop.setOnClickListener(this);
        llSmartCharging.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {
            case R.id.ll_charging_reminder:
                startActivity(new Intent(getActivity(), NotifChargeActivity.class));
                break;
            case R.id.ll_electricity:
                break;
            case R.id.ll_max_current:
                startActivity(new Intent(getActivity(), MaxCurrentSettingActivity.class));
                break;
            case R.id.ll_other_settings:
                break;
            case R.id.ll_quantitative_stop:
                startActivity(new Intent(getActivity(), TimeChargeActivity.class));
                break;
            case R.id.ll_smart_charging:
                break;
            default:
        }
    }
}
