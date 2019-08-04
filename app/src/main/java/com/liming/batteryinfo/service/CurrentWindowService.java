package com.liming.batteryinfo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.BatteryInfo;

public class CurrentWindowService extends Service {

    private static final String TAG = "MainService";

    LinearLayout toucherLayout;
    WindowManager.LayoutParams params;
    WindowManager windowManager;

    BatteryInfo batteryInfo;

    TextView tvCurrent;

    //状态栏高度.
    int statusBarHeight = -1;

    //不与Activity进行绑定.
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MainService Created");
        createToucher();
        batteryInfo = BatteryInfo.getInstance(getApplicationContext());
        mTimeHandler.sendEmptyMessage(1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        //Android8.0行为变更，对8.0进行适配https://developer.android.google.cn/about/versions/oreo/android-8.0-changes#o-apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }


        params.gravity = Gravity.LEFT | Gravity.TOP;


        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


        //设置悬浮窗口长宽数据.
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (LinearLayout) inflater.inflate(R.layout.layout_current_window, null);
        //添加toucherlayout
        windowManager.addView(toucherLayout, params);

        Log.i(TAG, "toucherlayout-->left:" + toucherLayout.getLeft());
        Log.i(TAG, "toucherlayout-->right:" + toucherLayout.getRight());
        Log.i(TAG, "toucherlayout-->top:" + toucherLayout.getTop());
        Log.i(TAG, "toucherlayout-->bottom:" + toucherLayout.getBottom());

        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "状态栏高度为:" + statusBarHeight);



        //设置窗口初始停靠位置.
        params.x = 0;
        params.y = statusBarHeight;

        //浮动窗口按钮.
        tvCurrent = (TextView) toucherLayout.findViewById(R.id.tv_current);

        tvCurrent.setOnClickListener(new View.OnClickListener() {
            long[] hints = new long[2];

            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了");
                System.arraycopy(hints, 1, hints, 0, hints.length - 1);
                hints[hints.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - hints[0] >= 700) {
                    Log.i(TAG, "要执行");
                    Toast.makeText(CurrentWindowService.this, "连续点击两次以退出", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "即将关闭");
                    stopSelf();
                }
            }
        });

        tvCurrent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                params.x = (int) event.getRawX();
                params.y = (int) event.getRawY() - statusBarHeight;
                windowManager.updateViewLayout(toucherLayout, params);
                return false;
            }
        });
    }


    Handler mTimeHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1){
                //+String.format("%.1f",(batteryInfo.getTemperature()/10d))+"℃"
                tvCurrent.setText(batteryInfo.getCurrent() + "mA");//当前电流
                mTimeHandler.sendEmptyMessageDelayed(1,1000);
            }


        }
    };



    @Override
    public void onDestroy() {
        if (tvCurrent != null) {
            windowManager.removeView(toucherLayout);
        }
        super.onDestroy();
    }
}
