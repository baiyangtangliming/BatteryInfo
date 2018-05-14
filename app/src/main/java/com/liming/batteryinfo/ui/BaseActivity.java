package com.liming.batteryinfo.ui;

import android.app.Activity;
import android.view.View;

import com.liming.batteryinfo.utils.AnnotateUtils;
import com.liming.batteryinfo.utils.QMUIStatusBarHelper;


public class BaseActivity extends Activity {
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        AnnotateUtils.injectViews(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        AnnotateUtils.injectViews(this);
    }
}
