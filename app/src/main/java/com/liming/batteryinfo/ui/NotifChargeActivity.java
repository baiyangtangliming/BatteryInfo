package com.liming.batteryinfo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.ShellUtils;
import com.liming.batteryinfo.utils.ViewInject;

public class NotifChargeActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.battery_text)
    EditText battery_text;

    @ViewInject(R.id.btn_resert)
    Button btn_reset;
    @ViewInject(R.id.btn_submit)
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_charge);
        this.btn_reset.setOnClickListener(this);
        this.btn_submit.setOnClickListener(this);

        int notifnum = (int)getParam("notifnum", 0);

        battery_text.setText(String.valueOf(notifnum));
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_resert:
                setParam("notifnum",  0);
                this.finish();
                break;
            case R.id.btn_submit:
                String obj = this.battery_text.getText().toString();
                if (obj.isEmpty()) {
                    obj = "0";
                }

                setParam("notifnum",  Integer.valueOf(obj));
                this.finish();
                break;
            default:
                return;
        }
    }
}
