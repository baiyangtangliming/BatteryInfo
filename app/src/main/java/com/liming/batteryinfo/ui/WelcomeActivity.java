package com.liming.batteryinfo.ui;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.ViewInject;
import com.liming.batteryinfo.view.BatteryView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class WelcomeActivity extends BaseActivity {
    @ViewInject(R.id.layout_welcome)
    FrameLayout r1_splash;
    @ViewInject(R.id.batteryview)
    BatteryView batteryView;
    private static int power;
    Handler mTimeHandler=new Handler(){
        public void handleMessage(Message message) {
            if (message.what == 1) {
                startAnimation(r1_splash,R.color.app_color_theme_1, R.color.app_color_theme_5);
            }
            if (message.what==2){
                if (power>=100){
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    batteryView.setPower(power+=1);
                    sendEmptyMessageDelayed(2,  30);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        //设置为无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置为全屏模式
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        int perm = this.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!(perm == PackageManager.PERMISSION_GRANTED)){
            if (Build.VERSION.SDK_INT >=23) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        r1_splash.setBackgroundColor(getResources().getColor(R.color.config_color_white));

        //this.mTimeHandler.sendEmptyMessageDelayed(1,  3000);
        this.mTimeHandler.sendEmptyMessage(2);








/*        //创建Timer对象
        Timer timer = new Timer();
        //创建TimerTask对象
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);


                finish();
            }
        };
        //使用timer.schedule（）方法调用timerTask，定时3秒后执行run
        timer.schedule(timerTask, 600);*/
    }
    public void startAnimation(View view, int color1, int color2) {
        final View animationView = view;
        //创建动画,这里的关键就是使用ArgbEvaluator, 后面2个参数就是 开始的颜色,和结束的颜色.
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(color1),  getResources().getColor(color2));
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();//之后就可以得到动画的颜色了
                animationView.setBackgroundColor(color);//设置一下, 就可以看到效果.
            }
        });
        colorAnimator.setDuration(2000);
        colorAnimator.start();
    }
}
