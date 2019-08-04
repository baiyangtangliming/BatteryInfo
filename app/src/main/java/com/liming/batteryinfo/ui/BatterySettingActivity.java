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
import com.liming.batteryinfo.utils.ShellUtils;
import com.liming.batteryinfo.utils.ViewInject;

public class BatterySettingActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    @ViewInject(R.id.battery_text)
    EditText battery_text;
    @ViewInject(R.id.batteryType)
    Spinner battery_type;
    @ViewInject(R.id.btn_resert)
    Button btn_reset;
    @ViewInject(R.id.btn_submit)
    Button btn_submit;
    int result;
    @ViewInject(R.id.tip)
    TextView tip;

    public void afterTextChanged(Editable editable) {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_resert:
                ShellUtils.execCmd("dumpsys battery reset\ndumpsys batterymanager reset\n",true);
                if (this.result != -1) {
                    Toast.makeText(this, "重置成功", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this, "重置失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            case R.id.btn_submit:
                String obj = this.battery_text.getText().toString();
                StringBuffer shell=new StringBuffer();
                switch (this.battery_type.getSelectedItemPosition()) {
                    case 1:
                        shell.append("dumpsys battery set ac 1\n");
                        shell.append("dumpsys battery set usb 0\n");
                        shell.append("dumpsys battery set wireless 0\n");
                        shell.append("dumpsys battery set status 2\n");
                        break;
                    case 2:
                        shell.append("dumpsys battery set ac 0\n");
                        shell.append("dumpsys battery set usb 1\n");
                        shell.append("dumpsys battery set wireless 0\n");
                        shell.append("dumpsys battery set status 2\n");
                        break;
                    case 3:
                        shell.append("dumpsys battery set ac 0\n");
                        shell.append("dumpsys battery set usb 0\n");
                        shell.append("dumpsys battery set wireless 1\n");
                        shell.append("dumpsys battery set status 2\n");
                        break;
                    default:
                        shell.append("dumpsys battery set ac 0\n");
                        shell.append("dumpsys battery set usb 0\n");
                        shell.append("dumpsys battery set wireless 0\n");
                        shell.append("dumpsys battery set status 3\n");
                        break;
                }

                if (!obj.isEmpty()) {
                    shell.append("dumpsys battery set level " + Integer.parseInt(obj) + "\n");
                    shell.append("dumpsys batterymanager set level " + Integer.parseInt(obj) + "\n");
                }
                this.result = ShellUtils.execCmd(shell.toString(),true).result;
                if (this.result != -1) {
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            default:
                return;
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_battery_setting);
        this.btn_reset.setOnClickListener(this);
        this.btn_submit.setOnClickListener(this);
        this.battery_type.setAdapter(new ArrayAdapter(this, R.layout.simple_spinner_item, new String[]{"未在充电", "交流充电", "USB充电", "无线充电"}));
        if (Integer.valueOf(registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("level", -1)).intValue() < 10) {
            this.tip.setText("电量少于10%，谨慎操作");
            this.tip.setVisibility(View.VISIBLE);
        }
    }


    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (charSequence.toString().isEmpty()) {
            this.tip.setVisibility(View.GONE);
        } else if (Integer.parseInt(charSequence.toString()) == 0) {
            this.tip.setText("电量设置成零，可能会立即关机");
            this.tip.setVisibility(View.VISIBLE);
        } else {
            this.tip.setVisibility(View.GONE);
        }
    }
}