package com.sgmcommunity.chlo24.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.push.MyFirebaseMessagingService;

public class PushMessageWebview extends CustomActivity implements View.OnClickListener {
//    private static final String TAG = WebviewActivity.class.getSimpleName();
    private WebView mWebView;
    private TextView mTitleBarText;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mPref;
    private String webAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_push_message_webview);

        Intent intent = getIntent();
        webAddress = intent.getStringExtra("web_address");
//        Log.d(TAG, "onCreate: " + webAddress);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mPref.getInt("push_count", 0) > 0) { //푸시카운트 0 이상이면
            MyFirebaseMessagingService.setBadge(getApplicationContext(), 0);
            SharedPreferences.Editor editor = mPref.edit();
            editor.putInt("push_count", 0); //푸시카운트 0으로 저장
            editor.commit();
        }

        mTitleBarText = (TextView) findViewById(R.id.title_bar_text);
        mWebView = (WebView) findViewById(R.id.web_view);
        findViewById(R.id.close_button).setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("잠시만 기다려주세요.");
        //스크롤바 안보이게
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressDialog.dismiss();
                CookieSyncManager.getInstance().sync();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

//        WebSettings webSettings = mWebView.getSettings();
        mWebView.loadUrl(webAddress);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {

        finish();

    }
}
