package com.liming.batteryinfo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.FrameLayout;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.ShellUtils;
import com.liming.batteryinfo.utils.ViewInject;
import com.liming.batteryinfo.view.BatteryView;

public class WelcomeActivity extends BaseActivity {
    @ViewInject(R.id.layout_welcome)
    FrameLayout r1_splash;
    @ViewInject(R.id.batteryview)
    BatteryView batteryView;
    private static int power;
    Handler mTimeHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                if (power >= 100) {
                    startMain();
                } else {
                    batteryView.setPower(power += 1);
                    sendEmptyMessageDelayed(2, 30);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        r1_splash.setBackgroundColor(getResources().getColor(R.color.config_color_white));
        if ((Boolean) getParam("splash", false)) {
            this.mTimeHandler.sendEmptyMessage(2);
        } else {
            startMain();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                ShellUtils.haveRoot();
            }
        }).start();
    }

    public void startMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
