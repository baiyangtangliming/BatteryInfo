package com.liming.batteryinfo.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.SystemInfo;
import com.liming.batteryinfo.utils.ViewInject;

public class AboutActivity extends BaseActivity {

    @ViewInject(R.id.linearLayout)
    LinearLayout linearLayout;
    @ViewInject(R.id.versoin)
    TextView versoin;
    @ViewInject(R.id.splashcheckbox)
    CheckBox splashcheckbox;
    public static int color;
    private int send;
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
                if (send==0){
                    sendEmptyMessageDelayed(1,3100);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mTimeHandler.sendEmptyMessage(1);
        versoin.setText(String.format(getResources().getString(R.string.versoin), SystemInfo.getVersionName(this)));
        splashcheckbox.setChecked((Boolean) getParam("splash",false));
    }
    /**
     * 加qq
     * @param view
     */
    public void joinQQ(View view){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=2862102898")));
        }catch (Exception e){
            Toast.makeText(getBaseContext(),"启动QQ失败",Toast.LENGTH_SHORT).show();}

    }

    /**
     * 支付宝捐赠
     * @param view
     */
    public void alipayDonate(View view){

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + "https://qr.alipay.com/aex02181nvk2ld7ktftkj6d" + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis())));
        }catch (Exception e){
            Toast.makeText(getBaseContext(),"启动支付宝失败",Toast.LENGTH_SHORT).show();}
    }
    public void showSplash(View view){
        boolean checked=(Boolean) getParam("splash",false);
        setParam("splash",!checked);
        splashcheckbox.setChecked(!checked);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        linearLayout.clearAnimation();
        send=1;
    }
}
