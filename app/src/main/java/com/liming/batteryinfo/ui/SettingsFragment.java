package com.liming.batteryinfo.ui;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.batteryinfo.BuildConfig;
import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.AnnotateUtils;
import com.liming.batteryinfo.utils.ViewInject;


public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.tx_version)
    TextView versoin;

    @ViewInject(R.id.setting_switch)
    Switch splashcheckbox;


    @ViewInject(R.id.update_item)
    RelativeLayout updateItem;

    @ViewInject(R.id.about_item)
    RelativeLayout aboutItem;

    @ViewInject(R.id.donate_item)
    RelativeLayout donateItem;

    @ViewInject(R.id.feedback_item)
    RelativeLayout feedbackItem;

    @ViewInject(R.id.setting_item)
    RelativeLayout settingItem;

    @ViewInject(R.id.thank_item)
    RelativeLayout thankItem;

    @ViewInject(R.id.theme_item)
    RelativeLayout themeItem;


    private View view ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        AnnotateUtils.bindView(view);
        versoin.setText("电箱 V"+ BuildConfig.VERSION_NAME);
        initView();
        return view;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        updateItem.setOnClickListener(this);
        aboutItem.setOnClickListener(this);
        donateItem.setOnClickListener(this);
        feedbackItem.setOnClickListener(this);
        settingItem.setOnClickListener(this);
        thankItem.setOnClickListener(this);
        themeItem.setOnClickListener(this);
        splashcheckbox.setOnClickListener(this);
        //设置开关
        splashcheckbox.setChecked((Boolean) getParam("splash",true));
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.update_item:
             Toast.makeText(getActivity(),R.string.update,Toast.LENGTH_SHORT).show();
             break;

            case R.id.about_item:
                Toast.makeText(getActivity(),"白羊唐黎明 出品",Toast.LENGTH_SHORT).show();
             break;

            case R.id.theme_item:
                Toast.makeText(getActivity(),"尽请期待",Toast.LENGTH_SHORT).show();
             break;

            case R.id.donate_item:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + "https://qr.alipay.com/aex02181nvk2ld7ktftkj6d" + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis())));
                }catch (Exception e){
                    Toast.makeText(view.getContext(),"启动支付宝失败",Toast.LENGTH_SHORT).show();
                }
             break;

            case R.id.feedback_item:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=2862102898")));
                }catch (Exception e){
                    Toast.makeText(view.getContext(),"启动QQ失败",Toast.LENGTH_SHORT).show();}
             break;
            case R.id.setting_switch:
            case R.id.setting_item:
                boolean checked=!(Boolean) getParam("splash",true);
                setParam("splash",checked);
                splashcheckbox.setChecked(checked);
                Toast.makeText(getActivity(),"开机动画："+(checked?"开":"关"),Toast.LENGTH_SHORT).show();
             break;

            case R.id.thank_item:
                Toast.makeText(getActivity(),"感谢酷安全体小伙伴支持",Toast.LENGTH_SHORT).show();
             break;
        }
    }
}
