package com.liming.batteryinfo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.ShellUtils;
import com.liming.batteryinfo.utils.ViewInject;

public class TimeChargeActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.battery_text)
    EditText battery_text;

    @ViewInject(R.id.btn_resert)
    Button btn_reset;
    @ViewInject(R.id.btn_submit)
    Button btn_submit;
    int result;
    @ViewInject(R.id.tip)
    TextView tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_charge);
        this.btn_reset.setOnClickListener(this);
        this.btn_submit.setOnClickListener(this);

        int stopnum = (int)getParam("stopnum", 0);

        battery_text.setText(String.valueOf(stopnum));
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_resert:
                if (ShellUtils.execCmd("if [ -f '/sys/class/power_supply/battery/battery_charging_enabled' ]; then echo 1 > /sys/class/power_supply/battery/battery_charging_enabled; else echo 0 > /sys/class/power_supply/battery/input_suspend; fi;\n",true).result != -1) {
                    setParam("stopnum", 0);
                    Toast.makeText(TimeChargeActivity.this, "恢复充电成功", Toast.LENGTH_SHORT).show();
                    this.finish();
                } else {
                    Toast.makeText(TimeChargeActivity.this, "恢复充电失败，请尝试重启！", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_submit:
                String obj = this.battery_text.getText().toString();
                if (obj.isEmpty()) {
                    obj = "0";
                }

                if (Integer.valueOf(obj) < 60){
                    Toast.makeText(TimeChargeActivity.this, "暂时只支持60%以上停止充电", Toast.LENGTH_LONG).show();
                    return;
                }

                setParam("stopnum",  Integer.valueOf(obj));
                this.finish();
                break;
            default:
                return;
        }
    }
}
