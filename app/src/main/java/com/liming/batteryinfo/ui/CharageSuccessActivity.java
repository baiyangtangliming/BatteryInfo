package com.liming.batteryinfo.ui;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.liming.batteryinfo.R;

public class CharageSuccessActivity extends BaseActivity {

    boolean isPlay = true;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                if (isPlay){
                    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
                    rt.play();
                    handler.sendEmptyMessageDelayed(1,1000);
                }else {
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charage_success);
        handler.sendEmptyMessage(1);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_resert:
                setParam("notifnum",  0);
                isPlay = false;
                break;
            default:
                return;
        }
    }
}
