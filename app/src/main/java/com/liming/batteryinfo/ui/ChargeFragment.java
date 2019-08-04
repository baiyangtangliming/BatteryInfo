package com.liming.batteryinfo.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.view.DynamicWave;
import com.liming.batteryinfo.view.WaterWaveView;


public class ChargeFragment extends BaseFragment {



    private View view;


    private DynamicWave dynamicWave;

    private WaterWaveView waterWaveView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_charge, container, false);

        dynamicWave = view.findViewById(R.id.dynamicwave);
        waterWaveView = view.findViewById(R.id.waterwaveview);
        startAnimation(dynamicWave);


        waterWaveView.setmWaterLevel(0.5F);
        waterWaveView.startWave();

        return view;
    }

    @Override
    public void onDestroy() {
        waterWaveView.stopWave();
        super.onDestroy();
    }



}
