package com.sgmcommunity.chlo24.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.common.CommonLib;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Login extends CustomActivity implements View.OnClickListener {

    private static final String TAG = Login.class.getSimpleName();
    private EditText mUserPassWord;
    private EditText mUserId;
    private SharedPreferences mPref;
    private ProgressDialog mProgressDialog;
    private CookieManager cookieManager;
    private String mToken;
    private AsyncHttpClient mhttpClient;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        mUserId = (EditText) findViewById(R.id.user_id); //입력한 아이디
        mUserPassWord = (EditText) findViewById(R.id.user_password);  //입력한 비밀번호
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.sign_up_button).setOnClickListener(this);
        findViewById(R.id.mobil_vs_button).setOnClickListener(this);
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mProgressDialog = new ProgressDialog(Login.this);
        mProgressDialog.setMessage("로그인 요청중입니다.");

//        mToken = FirebaseInstanceId.getInstance().getToken();

//        token = FirebaseInstanceId.getInstance().getToken();
//
//        Log.d(TAG, "onCreate: ---------------"+token.toString());
//        String userid = mPref.getString("id", "null");
//        if (!(userid.equals("null"))) {
//            mUserId.setText(userid);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                //로그인값 넘겨주기

                String userid = mUserId.getText().toString().trim();
                String userpassword = mUserPassWord.getText().toString().trim();
                if (!(TextUtils.isEmpty(userid) || TextUtils.isEmpty(userpassword))) {
                    mProgressDialog.show();
                    sendToLogin(userid, userpassword);
                } else if (TextUtils.isEmpty(userid)) {
                    Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userpassword)) {
                    Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.sign_up_button:
                //회원가입 화면으로 이동

                Intent intent1 = new Intent(this, SignUp.class);
                startActivity(intent1);

                break;

            case R.id.mobil_vs_button:
                //모바일 버전으로 이동
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(("http://www.chol24.com")));
                startActivity(intent2);
                break;
        }
    }

    /**
     * 로그인 연동
     *
     * @param userId
     * @param userPassword
     */
    public void sendToLogin(final String userId, final String userPassword) {
        mhttpClient = new AsyncHttpClient();

        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getApplicationContext());
        mhttpClient.setCookieStore(myCookieStore);
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", userId);
        requestParams.put("password", userPassword);
        if (mPref.getString("token", "null").equals("null")) {
            token = FirebaseInstanceId.getInstance().getToken();
            requestParams.put("Token", token);
        } else {
            requestParams.put("Token", mPref.getString("token", "null"));
        }

        mhttpClient.post("http://www.chol24.com/android_api/login.php", requestParams, new JsonHttpResponseHandler() {

            private String error;

            @Override
            public void onStart() {
                super.onStart();
                mProgressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {

                    error = response.getString("error");

                    if (error.contains("false")) {
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putString("id", userId);
                        editor.putString("password", userPassword);
                        if(mPref.getString("token","null").equals("null")) {
                            editor.putString("token", token);
                        }
                        editor.commit();

                        CommonLib.cookieMaker(mhttpClient,getBaseContext());

                        mainIntent();
                    } else {
                        Toast.makeText(Login.this, "아이디 또는 패스워드가 틀립니다.", Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onSuccess: " + error);
                } catch (JSONException e) {
                    Log.d(TAG, "onSuccess: 응답 메세지 없음");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
//                Log.d(TAG, "onFailure: 서버와 통신 실패");
                Toast.makeText(Login.this, "서버와 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mProgressDialog.cancel();
            }
        });

    }


    private void mainIntent() {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }
}
