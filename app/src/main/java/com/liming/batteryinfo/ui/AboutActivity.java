package com.liming.batteryinfo.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.SystemInfo;
import com.liming.batteryinfo.utils.ViewInject;

public class AboutActivity extends BaseActivity {

    @ViewInject(R.id.linearLayout)
    LinearLayout linearLayout;
    @ViewInject(R.id.versoin)
    TextView versoin;
    public static int color;
    private int []colors={
            R.color.app_color_theme_4,
            R.color.app_color_theme_2,
            R.color.app_color_theme_9,
            R.color.app_color_theme_1,
            R.color.app_color_theme_6,
            R.color.app_color_theme_5,
            R.color.app_color_theme_8,
            R.color.app_color_theme_7
    };
    Handler mTimeHandler=new Handler(){
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 1) {
                if (color>=colors.length-1){
                    startAnimation(linearLayout,colors[colors.length-1],colors[0]);
                    color=0;
                }else {
                    startAnimation(linearLayout,colors[color],colors[++color]);
                }
                sendEmptyMessageDelayed(1,3000);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mTimeHandler.sendEmptyMessage(1);
        versoin.setText(String.format(getResources().getString(R.string.versoin), SystemInfo.getVersionName(this)));
    }
    public void startAnimation(final View view, int color1, int color2) {
        //创建动画,这里的关键就是使用ArgbEvaluator, 后面2个参数就是 开始的颜色,和结束的颜色.
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(color1),  getResources().getColor(color2));
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((int)animation.getAnimatedValue());//设置一下, 就可以看到效果.
            }
        });
        colorAnimator.setDuration(3000);
        colorAnimator.start();
    }
}
