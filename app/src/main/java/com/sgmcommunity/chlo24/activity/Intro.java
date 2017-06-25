package com.sgmcommunity.chlo24.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.sgmcommunity.chlo24.R;

public class Intro extends CustomActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_chol24_intro);


        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
