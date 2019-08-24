package com.liming.batteryinfo.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.broadcast.BatteryInfoBroadcastReceiver;
import com.liming.batteryinfo.entity.TabEntity;
import com.liming.batteryinfo.service.BatteryInfoService;
import com.liming.batteryinfo.utils.BatteryInfo;
import com.liming.batteryinfo.utils.ShellUtils;
import com.liming.batteryinfo.utils.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private List<TabEntity> tabList = new ArrayList<>();


    @ViewInject(R.id.bottomLayout)
    LinearLayout bottomLayout;


    BatteryInfo batteryInfo;



    private static Handler mTimeHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        batteryInfo = BatteryInfo.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(new BatteryInfoBroadcastReceiver(), intentFilter);
        Intent intent = new Intent(this,BatteryInfoService.class);
        startService(intent);



        initFragment();

        initHandler();

        mTimeHandler.sendEmptyMessage( 0);

    }


    /**
     * 初始化Tab
     */
    private void initFragment(){
        tabList.add(new TabEntity(new MainFragment(),(LinearLayout) findViewById(R.id.id_tab1_layout), (ImageView) findViewById(R.id.id_tab1_imageview),(TextView) findViewById(R.id.id_tab1_text),R.drawable.main,R.drawable.main_not_select,Color.parseColor("#2BBDF3")));
        tabList.add(new TabEntity(new ChargeFragment(),(LinearLayout) findViewById(R.id.id_tab2_layout),(ImageView) findViewById(R.id.id_tab2_imageview),(TextView) findViewById(R.id.id_tab2_text),R.drawable.charge,R.drawable.charge_not_select,Color.parseColor("#9FD661")));
        tabList.add(new TabEntity(new SettingsFragment(),(LinearLayout) findViewById(R.id.id_tab3_layout),(ImageView) findViewById(R.id.id_tab3_imageview),(TextView) findViewById(R.id.id_tab3_text),R.drawable.settings,R.drawable.settings_not_select,Color.parseColor("#49505A")));

        for (TabEntity tabEntity : tabList) {
            tabEntity.getTabLayout().setOnClickListener(this);
        }

        tabList.get(0).getImage().setImageResource(tabList.get(0).getSelectImage());
        tabList.get(0).getText().setTextColor(tabList.get(0).getSelectColor());
        this.getFragmentManager().beginTransaction().replace(R.id.id_framelayout,tabList.get(0).getView()).commit();

    }



    /**
     * 初始化Handler
     */
    public void initHandler() {
        mTimeHandler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 0) {
                    stopCharge();
                    notifCharge();
                    mTimeHandler.sendEmptyMessageDelayed(0, 1000);
                }

            }
        };
    }

    /**
     * 充电提醒
     */
    boolean isOpenNotif = false;
    private void notifCharge() {

        int notifnum = (int)getParam("notifnum", 0);
        if (notifnum == 0){
            isOpenNotif = false;
            return;
        }


        if (batteryInfo.isCharging() && batteryInfo.getQuantity() >= notifnum){
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
            rt.play();
            if (!isOpenNotif){
                isOpenNotif = true;
                startActivity(new Intent(this, NotifChargeActivity.class));
            }


        }

    }

    /**
     * 停止充电
     */
    Thread thread;
    boolean dostart = false;
    private void stopCharge() {

        if (thread != null && thread.isAlive()){
            return;
        }

            thread = new Thread(new Runnable() {
            @Override
            public void run() {

                int stopnum = (int)getParam("stopnum", 0);
                if (stopnum == 0){
                    return;
                }

                Log.d("cd",batteryInfo.isCharging()+"");

                if (batteryInfo.isCharging() && batteryInfo.getQuantity() >= stopnum){
                    //停止充电
                    ShellUtils.execCmd("if [ -f '/sys/class/power_supply/battery/battery_charging_enabled' ]; \nthen \necho 0 > /sys/class/power_supply/battery/battery_charging_enabled; \nelse \necho 1 > /sys/class/power_supply/battery/input_suspend; \nfi;\n",true);
                    dostart = false;
                }else if (!dostart && batteryInfo.getQuantity() < 60){
                    //恢复充电
                    ShellUtils.CommandResult commandResult = ShellUtils.execCmd("if [ -f '/sys/class/power_supply/battery/battery_charging_enabled' ]; \nthen \necho 1 > /sys/class/power_supply/battery/battery_charging_enabled; \nelse \necho 0 > /sys/class/power_supply/battery/input_suspend; \nfi;\n", true);
                    if (commandResult.result != -1){
                        dostart = true;
                    }
                }
            }
        },"STOPCHARGE");

        thread.start();

    }




    /**
     * tab按钮点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.id_tab2_layout){
            bottomLayout.setBackgroundResource(R.drawable.background_gradient);
        }else {
            bottomLayout.setBackgroundResource(R.color.config_color_white);
        }

        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        for (TabEntity tabEntity : tabList) {
            if (tabEntity.getTabLayout().getId() == view.getId()){
                tabEntity.getImage().setImageResource(tabEntity.getSelectImage());
                tabEntity.getText().setTextColor(tabEntity.getSelectColor());
                transaction.replace(R.id.id_framelayout, tabEntity.getView());
            }else {
                tabEntity.getImage().setImageResource(tabEntity.getNotSelectImage());
                tabEntity.getText().setTextColor(getResources().getColor(R.color.config_color_gray_6));
            }
        }
        transaction.commit();
    }



}
