package com.sgmcommunity.chlo24.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.library.StringFilter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class SignUp extends CustomActivity implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = SignUp.class.getSimpleName();
    private EditText mUserId;
    private EditText mUserPassWord1;
    private EditText mUserPassword2;
    private EditText mUserName;
    private EditText mUserNic;
    private EditText mUserPH;
    private CheckBox mCheck;
    private TextView mClause;
    private ScrollView mClauseScrollView;
    private ScrollView mSignUpParent;
    private ImageView mClauseButton;
    private boolean mClauseVisible = true;
    private AsyncHttpClient mhttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sign_up);

        mUserId = (EditText) findViewById(R.id.user_id); //입력한 아이디
        mUserPassWord1 = (EditText) findViewById(R.id.user_password1);  //입력한 비밀번호
        mUserPassword2 = (EditText) findViewById(R.id.user_password2);  //비밀헌호 재입력
        mUserName = (EditText) findViewById(R.id.user_name);
        mUserNic = (EditText) findViewById(R.id.user_nic_name);
        mUserPH = (EditText) findViewById(R.id.user_phone_num);
        mCheck = (CheckBox) findViewById(R.id.check);
        mClause = (TextView) findViewById(R.id.clause_text);
        mClauseScrollView = (ScrollView) findViewById(R.id.clause_scroll);
        mSignUpParent = (ScrollView) findViewById(R.id.sign_up_parent);
        mClauseButton = (ImageView) findViewById(R.id.clause_button);
        mClauseScrollView.setOnTouchListener(this);
        mClauseButton.setOnClickListener(this);

        StringFilter stringFilter = new StringFilter(this);

        InputFilter[] allowAlphanumeric = new InputFilter[1]; //영문자랑 숫자 허용
        allowAlphanumeric[0] = stringFilter.allowAlphanumeric;

        InputFilter[] allowAlphanumericHangul = new InputFilter[1]; //영문자 숫자 한글 허용
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

        mUserId.setFilters(allowAlphanumeric);
        mUserName.setFilters(allowAlphanumericHangul);
        mUserNic.setFilters(allowAlphanumericHangul);

        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.sign_up_button).setOnClickListener(this);
        findViewById(R.id.mobil_vs_button).setOnClickListener(this);

        mhttpClient = new AsyncHttpClient();

        new Clausephp().execute("http://chol24.com/app/clause.php");
//        receiveClauseData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                finish();
                break;

            case R.id.sign_up_button:
                String userId = mUserId.getText().toString().trim();
                String userPassword1 = mUserPassWord1.getText().toString().trim();
                String userPassword2 = mUserPassword2.getText().toString().trim();
                String userName = mUserName.getText().toString().trim();
                String userPH = mUserPH.getText().toString().trim();
                String userNick = mUserNic.getText().toString().trim();

                if (userId.getBytes().length < 6) {

                    Toast.makeText(this, "아이디는 6~12자로 구성되어야합니다.", Toast.LENGTH_SHORT).show();
                } else if (userPassword1.getBytes().length < 6) {
                    Toast.makeText(this, "비밀번호는 6자 이상이어야합니다.", Toast.LENGTH_SHORT).show();
                } else if (!userPassword1.equals(userPassword2)) {
                    Toast.makeText(this, "비밀번호와 비밀헌호 재입력이 같지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userPH)) {
                    Toast.makeText(this, "핸드폰 번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (userNick.getBytes().length < 4) {
                    Toast.makeText(this, "닉네임은 2자 이상 입력하셔야합니다.", Toast.LENGTH_SHORT).show();
                } else if (!mCheck.isChecked()) {
                    Toast.makeText(this, "서비스 약관에 동의하셔야 가입이 가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    sendUserInfo(userId, userPassword1,userName, userPH, userNick);
                }
                break;

            case R.id.mobil_vs_button:
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(("http://www.chol24.com")));
                startActivity(intent2);
                break;

            case R.id.clause_button:

                if (mClauseVisible == false) { //
                    mClauseScrollView.setVisibility(View.GONE);
                    mClauseVisible = true;
                    mClauseButton.setImageResource(R.drawable.sign_up_ic_expand_more_black);
                } else {
                    mClauseScrollView.setVisibility(View.VISIBLE);
                    mClauseVisible = false;
                    mClauseButton.setImageResource(R.drawable.sign_up_ic_expand_less_black);
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mSignUpParent.requestDisallowInterceptTouchEvent(true);

        return false;
    }

    public void sendUserInfo(String id, String password, String name, String hp, String nick) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("id", id);
        requestParams.put("password", password);
        requestParams.put("name", name);
        requestParams.put("hp", hp);
        requestParams.put("nick", nick);

        mhttpClient.post("http://www.chol24.com/android_api/register.php", requestParams, new JsonHttpResponseHandler() {

            private String error;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    error = response.getString("error");
                    if (error.contains("false")) { //에러 없음
                        Toast.makeText(SignUp.this, "성공적으로 가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        if (error.contains("missing")) {
                            Toast.makeText(SignUp.this, "값이 전달되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        } else if (error.contains("existed")) {
                            Toast.makeText(SignUp.this, "아이디가 존재합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUp.this, "서버 오류 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                    throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "onFailure: 서버와 통신 실패");
            }
        });
    }

//    public void receiveClauseData() {
//
//        mhttpClient.post("http://chol24.com/app/clause.php", new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//                InputStream is = conn.getInputStream(); //input스트림 개방
////
//                StringBuilder builder = new StringBuilder(); //문자열을 담기 위한 객체
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); //문자열 셋 세팅
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    builder.append(line + "\n");
//                }
//
//                conn.disconnect();
//
//                return builder.toString();
//
//                mClause.setText(responseBody.);
//                Log.d(TAG, "-------------------" + responseBody.toString());
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                mClause.setText("약관내용을 가져오는데 실패했습니다");
//                Log.d(TAG, "-------------------에러");
//            }
//        });
//    }

    private class Clausephp extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // URL을 연결한 객체 생성.
                conn.setRequestMethod("POST"); // get방식 통신
//                conn.setDoOutput(true); // 쓰기모드 지정
                conn.setDoInput(true); // 읽기모드 지정
                conn.setUseCaches(false); // 캐싱데이터를 받을지 안받을지
                conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정

                InputStream is = conn.getInputStream(); //input스트림 개방

                StringBuilder builder = new StringBuilder(); //문자열을 담기 위한 객체
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); //문자열 셋 세팅
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                conn.disconnect();

                return builder.toString();
            } catch (IOException io) {
                io.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mClause.setText(Html.fromHtml(result));
//            Log.d(TAG, "onPostExecute: "+result);
        }
    }

}



