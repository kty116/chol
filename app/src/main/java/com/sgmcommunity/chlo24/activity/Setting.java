package com.sgmcommunity.chlo24.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.common.CommonLib;
import com.sgmcommunity.chlo24.dto.SettingDTO;

import static com.sgmcommunity.chlo24.common.CommonLib.checkLocationServicesStatus;

public class Setting extends CustomActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private Switch mAutoUpdateSwitch;
    private Switch mPushSwitch;
    private Switch mAutoLoginSwitch;
    private SharedPreferences mPref;
    private SettingDTO mSettingDTO;
    private boolean gps_check;
    private SharedPreferences.Editor mEditor;
    private AlertDialog showBuilder;
    private Switch mGPSSwitch;

    //설정값 0이면 설정 값 비었을때
    //1은 true  2는 false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setSettingDTO();
        init();
        setSettingView();

    }

    private void init() {
        findViewById(R.id.home_button).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
        mGPSSwitch = (Switch) findViewById(R.id.gps_switch);
        mAutoUpdateSwitch = (Switch) findViewById(R.id.auto_update_switch);
        mPushSwitch = (Switch) findViewById(R.id.push_switch);
        mAutoLoginSwitch = (Switch) findViewById(R.id.auto_login_switch);

    }

    private void setSettingDTO() {
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (mPref.getInt("update", 0) == 0) { //설정창에 처음 들어왔을때
            //값 비었을때 기본값으로 넣고
            mEditor = mPref.edit();
            mEditor.putInt("update", 1);
            mEditor.putInt("push", 1);
            mEditor.putInt("login", 1);
            mEditor.commit();
        } else {
            //값 있을때
            mPref.getInt("update", 2);
            mPref.getInt("push", 2);
            mPref.getInt("login", 2);
        }

        mSettingDTO = new SettingDTO();
        mSettingDTO.setUpdate(mPref.getInt("update", 0));
        mSettingDTO.setPush(mPref.getInt("push", 0));
        mSettingDTO.setLogin(mPref.getInt("login", 0));

    }

    private void setSettingView() {
        gps_check = checkLocationServicesStatus(this);

        if (gps_check == true) {
            mGPSSwitch.setChecked(true);
        } else {
            mGPSSwitch.setChecked(false);
        }

        mAutoUpdateSwitch.setChecked(mSettingDTO.getUpdate() == 1 ? true : false);
        mPushSwitch.setChecked(mSettingDTO.getPush() == 1 ? true : false);
        mAutoLoginSwitch.setChecked(mSettingDTO.getLogin() == 1 ? true : false);

        mGPSSwitch.setOnCheckedChangeListener(this);
        mAutoUpdateSwitch.setOnCheckedChangeListener(this);
        mPushSwitch.setOnCheckedChangeListener(this);
        mAutoLoginSwitch.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        finish();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.gps_switch:

                //현재상태 먼저 확인후
                if (checkLocationServicesStatus(this)) {
                    mGPSSwitch.setChecked(true);
                } else {
                    mGPSSwitch.setChecked(false);
                }
                // TODO: 2017-06-28 gps 설정 화면 띄우기
                if (isChecked) {
                    showDialogForLocationServiceSetting();

                } else {
                    showDialogForLocationServiceSetting();
                }
                break;

            case R.id.auto_update_switch:
                actionTrueOrFalse(isChecked, "update");
                break;

            case R.id.push_switch:
                actionTrueOrFalse(isChecked, "push");
                break;

            case R.id.auto_login_switch:
                actionTrueOrFalse(isChecked, "login");
                break;
        }
    }

    private void actionTrueOrFalse(boolean isChecked, String key) {
        mEditor = mPref.edit();
        if (isChecked) {
            mEditor.putInt(key, 1);
        } else {
            mEditor.putInt(key, 2);
        }
        mEditor.commit();
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS 설정");
        builder.setMessage("GPS 설정 화면으로 이동합니다.");
        builder.setCancelable(true);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", null);
        builder.create();
        showBuilder = builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                boolean gpsStatu = CommonLib.checkLocationServicesStatus(this);
                if (gpsStatu) {
                    mGPSSwitch.setChecked(true);
                } else {
                    mGPSSwitch.setChecked(false);
                }

                showBuilder.dismiss();
                break;

        }
    }


}
