package com.liming.batteryinfo.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.BatteryUtil;
import com.liming.batteryinfo.utils.RootCmd;
import com.liming.batteryinfo.utils.SystemInfo;
import com.liming.batteryinfo.utils.ViewInject;

public class MaxCurrentSettingActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.battery_text)
    EditText battery_text;
    @ViewInject(R.id.btn_resert)
    Button btn_reset;
    @ViewInject(R.id.btn_submit)
    Button btn_submit;
    int result;
    @ViewInject(R.id.tip)
    TextView tip;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_resert:
                        finish();
                        break;
            case R.id.btn_submit:
                String obj = this.battery_text.getText().toString().trim();
                if (obj.isEmpty()) {
                    Toast.makeText(getBaseContext(), "请输入电流值",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer shell=new StringBuffer();
                if (BatteryUtil.setChargeCurrentMax(Integer.valueOf(obj))) {
                    Toast.makeText(getBaseContext(), "成功修改充电电流为" +
                                    obj + "mA",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getBaseContext(), "修改充电电流失败",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return;
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_current_setting);
        this.btn_reset.setOnClickListener(this);
        this.btn_submit.setOnClickListener(this);
        battery_text.setText(String.valueOf(SystemInfo.getConstant_charge_current_max()));
    }

}