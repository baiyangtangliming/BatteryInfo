package com.liming.batteryinfo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.batteryinfo.BuildConfig;
import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.AnnotateUtils;
import com.liming.batteryinfo.utils.ViewInject;


public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";

    @ViewInject(R.id.tx_version)
    TextView versoin;

    @ViewInject(R.id.setting_switch)
    Switch splashSwitch;


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

    @ViewInject(R.id.theme_view)
    View themeView;


    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        AnnotateUtils.bindView(view);
        versoin.setText("电箱 V" + BuildConfig.VERSION_NAME);
        initView();
        return view;
    }




    @Override
    public void onResume() {//和activity的onResume绑定，Fragment初始化的时候必调用，但切换fragment的hide和visible的时候可能不会调用！
        super.onResume();
        if (isAdded() && !isHidden()) {//用isVisible此时为false，因为mView.getWindowToken为null
            Log.d(TAG, "关于面进入可见状态: ");
            String theme = (String) getParam("theme", "0");
            if (theme.equals("0")) {
                themeView.setBackgroundResource(R.drawable.view_select_many_color);
            } else {
                themeView.setBackgroundColor(Color.parseColor(theme));
            }
        }
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
        splashSwitch.setOnClickListener(this);


        //设置开关
        splashSwitch.setChecked((Boolean) getParam("splash", true));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_item:
                Toast.makeText(getActivity(), R.string.update, Toast.LENGTH_SHORT).show();
                break;

            case R.id.about_item:
                Toast.makeText(getActivity(), "白羊唐黎明 出品", Toast.LENGTH_SHORT).show();
                break;

            case R.id.theme_item:
                chooseTheme();
                break;

            case R.id.donate_item:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + "https://qr.alipay.com/aex02181nvk2ld7ktftkj6d" + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis())));
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "启动支付宝失败", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.feedback_item:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=2862102898")));
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "启动QQ失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.setting_switch:
            case R.id.setting_item:
                boolean checked = !(Boolean) getParam("splash", true);
                setParam("splash", checked);
                splashSwitch.setChecked(checked);
                Toast.makeText(getActivity(), "开机动画：" + (checked ? "开" : "关"), Toast.LENGTH_SHORT).show();
                break;

            case R.id.thank_item:
                Toast.makeText(getActivity(), "感谢酷安全体小伙伴支持", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 选择主题
     */
    private void chooseTheme() {


        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String color = (String) view.getTag();

                setParam("theme",color);
                themeView.setBackground(view.getBackground());
                dialog.dismiss();
            }
        };

        final View v = getActivity().getLayoutInflater().inflate(R.layout.layout_color_select, null);
        View theme1 = v.findViewById(R.id.app_color_theme_1);
        View theme2 = v.findViewById(R.id.app_color_theme_2);
        View theme3 = v.findViewById(R.id.app_color_theme_3);
        View theme4 = v.findViewById(R.id.app_color_theme_4);
        View theme5 = v.findViewById(R.id.app_color_theme_5);
        View theme6 = v.findViewById(R.id.app_color_theme_6);
        View theme7 = v.findViewById(R.id.app_color_theme_7);
        View theme8 = v.findViewById(R.id.app_color_theme_8);
        View theme9 = v.findViewById(R.id.app_color_theme_9);
        View manyColor = v.findViewById(R.id.app_color_many_color);

        theme1.setOnClickListener(onClickListener);
        theme2.setOnClickListener(onClickListener);
        theme3.setOnClickListener(onClickListener);
        theme4.setOnClickListener(onClickListener);
        theme5.setOnClickListener(onClickListener);
        theme6.setOnClickListener(onClickListener);
        theme7.setOnClickListener(onClickListener);
        theme8.setOnClickListener(onClickListener);
        theme9.setOnClickListener(onClickListener);
        manyColor.setOnClickListener(onClickListener);

        dialog.setView(v);
        dialog.show();

    }
}
