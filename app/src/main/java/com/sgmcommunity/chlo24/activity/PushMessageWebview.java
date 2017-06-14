package com.sgmcommunity.chlo24.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.push.MyFirebaseMessagingService;

public class PushMessageWebview extends CustomActivity implements View.OnClickListener {
    private static final String TAG = WebviewActivity.class.getSimpleName();
    private WebView mWebView;
    private TextView mTitleBarText;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mPref;
    private ValueCallback<Uri> filePathCallbackNormal;
    private ValueCallback<Uri[]> filePathCallbackLollipop;
    private Uri mCapturedImageURI;
    private final static int FILECHOOSER_NORMAL_REQ_CODE = 1;
    private final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2;
    private String mIntentAction;
    private String webAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_push_message_webview);

        Intent intent = getIntent();
        webAddress = intent.getStringExtra("web_address");
        Log.d(TAG, "onCreate: " + webAddress);

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

        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
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
//
//    private class Php extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            try {
//                URL url = new URL(params[0]);
//                String postData = "id=" + "ddd033" + "&" + "password=" + "d026dd";
//                Log.i("PHPRequest", postData);
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                if (conn != null) {
//                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                    conn.setRequestMethod("POST");
//                    conn.setConnectTimeout(5000);
//                    conn.setDoOutput(true);
//                    conn.setDoInput(true);
//
//                    //서버에 전송
//                    OutputStream outputStream = conn.getOutputStream();
//                    outputStream.write(postData.getBytes("UTF-8"));
//                    outputStream.flush();
//                    outputStream.close();
//
//
//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line).append('\n');
//                    }
//                    conn.disconnect();
//
//                    return sb.toString();
//                }
//            } catch (Exception e) {
//                Log.i("PHPRequest", "통신 실패");
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            Log.i("PHPRequest", result);
//
//        }
//
//
//    }
}
