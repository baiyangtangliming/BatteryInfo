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
import com.liming.batteryinfo.utils.RootCmd;
import com.liming.batteryinfo.utils.ViewInject;

public class TimeChargeActivity extends BaseActivity implements View.OnClickListener{
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
    String []dothings=new String[]{"充电停止后关闭软件","充电停止后关闭手机","充电停止后重启手机", "充电停止后不做操作"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_charge);
        this.btn_reset.setOnClickListener(this);
        this.btn_submit.setOnClickListener(this);
        this.battery_type.setAdapter(new ArrayAdapter(this, R.layout.simple_spinner_item,dothings ));
        Intent intent=getIntent();
        battery_text.setText(String.valueOf(intent.getIntExtra("stopnum",101)));
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_resert:
                if (RootCmd.execRootCmdSilent("if [ -f '/sys/class/power_supply/battery/battery_charging_enabled' ]; then echo 1 > /sys/class/power_supply/battery/battery_charging_enabled; else echo 0 > /sys/class/power_supply/battery/input_suspend; fi;setprop vtools.bp 0;\n",true)!=-1){
                    intent.putExtra("stopnum",101);
                    intent.putExtra("stopdo",0);
                    intent.putExtra("memo",dothings[dothings.length-1]);
                    setResult(1, intent);
                    Toast.makeText(TimeChargeActivity.this,"恢复充电成功",Toast.LENGTH_SHORT).show();
                    this.finish();
                }else {
                    Toast.makeText(TimeChargeActivity.this,"恢复充电失败，请尝试重启！",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_submit:
                String obj = this.battery_text.getText().toString();
                if (obj.isEmpty()){
                    obj="101";
                }
                intent.putExtra("stopnum",Integer.valueOf(obj));
                intent.putExtra("stopdo",this.battery_type.getSelectedItemPosition());
                intent.putExtra("memo",dothings[battery_type.getSelectedItemPosition()]);
                setResult(1, intent);
                this.finish();
                break;
            default:
                return;
        }
    }
}
