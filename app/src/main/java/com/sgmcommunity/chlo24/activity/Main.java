package com.sgmcommunity.chlo24.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sgmcommunity.chlo24.BuildConfig;
import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.dto.MemberDTO;
import com.sgmcommunity.chlo24.push.MyFirebaseMessagingService;

public class Main extends CustomActivity implements View.OnClickListener {

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private SharedPreferences mPref;
    private ImageView mSettingButton;
    private DrawerLayout mMainDrawer;
    private LinearLayout mNavMenu;
    public static boolean isForeGround;
    private MemberDTO memberDTO;
    private ImageView mLoginButton;
    private Class mActivity;
    private TextView mVersionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        findViewById(R.id.look_button).setOnClickListener(this);
        findViewById(R.id.participation_button).setOnClickListener(this);
        findViewById(R.id.video_button).setOnClickListener(this);
        findViewById(R.id.mobil_vs_button).setOnClickListener(this);
        findViewById(R.id.menu_inquiry_button).setOnClickListener(this);
        findViewById(R.id.menu_morgue_button).setOnClickListener(this);
        findViewById(R.id.menu_noti_button).setOnClickListener(this);
        findViewById(R.id.camera_button).setOnClickListener(this);
        findViewById(R.id.intro_button).setOnClickListener(this);
        mSettingButton = (ImageView) findViewById(R.id.setting_button);
        mLoginButton = (ImageView) findViewById(R.id.logout_button);
        mNavMenu = (LinearLayout) findViewById(R.id.nav_menu);
        mMainDrawer = (DrawerLayout) findViewById(R.id.main_drawer);
        mVersionText = (TextView) findViewById(R.id.version_text);
        String LVersionName = BuildConfig.VERSION_NAME;
        String CVersionName = null;
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            CVersionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mVersionText.setText("최신버전                    " + LVersionName + "\n현재버전                    " + CVersionName);

        mSettingButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        memberDTO = new MemberDTO();
        memberDTO.setUserId(mPref.getString("id", "null"));
        memberDTO.setUserPw(mPref.getString("password", "null"));

        if (!(memberDTO.getUserId().equals("null") && memberDTO.getUserPw().equals("null"))) {
            mLoginButton.setImageResource(R.drawable.main_ic_logout_white);
        } else {
            mLoginButton.setImageResource(R.drawable.main_ic_login_white);
        }

        //추가한 라인
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        if (mPref.getInt("push_count", 0) > 0) { //저장된 푸시카운트가 0이상이면 푸시메세지 띄우기
            Intent intent = new Intent(this, PushMessageWebview.class);
            intent.putExtra("web_address", "http://www.chol24.com" + MyFirebaseMessagingService.contents);
            startActivity(intent);
        }

        //메인 액티비티의 포,백 상태확인
        isForeGround = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //액션바

            case R.id.setting_button:
                mMainDrawer.openDrawer(mNavMenu);
                break;

            case R.id.logout_button:
                if (!(memberDTO.getUserId().equals("null") && memberDTO.getUserPw().equals("null"))) {
                    //값 있을때
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Main.this);
                    alert_confirm.setMessage("로그아웃 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences.Editor editor = mPref.edit();
                                    editor.clear();
                                    editor.commit();
                                    memberDTO.setUserId(mPref.getString("id", "null"));
                                    memberDTO.setUserPw(mPref.getString("password", "null"));
                                    CookieManager.getInstance().removeSessionCookie();
                                    CookieManager.getInstance().removeAllCookie();
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                        CookieSyncManager.getInstance().sync();
                                    } else {
                                        CookieManager.getInstance().flush();
                                    }
                                    Toast.makeText(Main.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
//                                    Log.d("------", "onClick: " + mPref.getString("token", "null"));
//                                    Intent intent3 = new Intent(Main.this, Login.class);
//                                    startActivity(intent3);
//                                    finish();
                                }
                            }).setNegativeButton("취소", null);
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                    break;
                } else {
                    //값 없을때
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Main.this);
                    alert_confirm.setMessage("로그인 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent3 = new Intent(Main.this, Login.class);
                                    startActivity(intent3);
                                }
                            }).setNegativeButton("취소", null);
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                    break;

                }

                // 메인 아이콘

            case R.id.look_button:
                if (!(memberDTO.getUserId().equals("null") && memberDTO.getUserPw().equals("null"))) {
                    Intent intent = new Intent(this, WebviewActivity.class);
                    intent.putExtra("web_address", "http://chol24.com/app_list.php");
                    intent.putExtra("title_text", "캠페인 보기");
                    startActivity(intent);
                } else {
                    setLoginDialog();
                }
                break;

            case R.id.participation_button:
                if (!(memberDTO.getUserId().equals("null") && memberDTO.getUserPw().equals("null"))) {
                    Intent intent1 = new Intent(this, WebviewActivity.class);
                    intent1.putExtra("web_address", "http://www.chol24.com/bbs/app_write.php?bo_table=with");
                    intent1.putExtra("title_text", "캠페인 참여");
                    startActivity(intent1);
                } else {
                    setLoginDialog();
                }
                break;

            case R.id.camera_button:
                if (!checkLocationServicesStatus()) { //gps모드 비 활성화시
                    showDialogForLocationServiceSetting(Camera.class);
                    mActivity = Camera.class;
                } else {
                    Intent intent4 = new Intent(this, Camera.class);
                    startActivity(intent4);
                }
                mMainDrawer.closeDrawer(mNavMenu);
                break;

            case R.id.video_button:
                if (!checkLocationServicesStatus()) { //gps모드 비 활성화시
                    showDialogForLocationServiceSetting(Main2Activity.class);
                    mActivity = Main2Activity.class;
                } else {
                    Intent intent7 = new Intent(this, Main2Activity.class);
                    startActivity(intent7);
                }
                mMainDrawer.closeDrawer(mNavMenu);
                break;

            case R.id.mobil_vs_button:
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(("http://www.chol24.com")));
                startActivity(intent2);
                break;

            //사이드 메뉴

            case R.id.intro_button:
                Intent intent3 = new Intent(this, Intro.class);
                startActivity(intent3);
                mMainDrawer.closeDrawer(mNavMenu);
                break;

            case R.id.menu_inquiry_button:
                if (!(memberDTO.getUserId().equals("null") && memberDTO.getUserPw().equals("null"))) {
                    Intent intent6 = new Intent(this, WebviewActivity.class);
                    intent6.putExtra("web_address", "http://www.chol24.com/app_contact.php");
                    intent6.putExtra("title_text", "문의하기");
                    startActivity(intent6);
                    mMainDrawer.closeDrawer(mNavMenu);
                } else {
                    setLoginDialog();
                }
                break;

            case R.id.menu_morgue_button:
                if (!(memberDTO.getUserId().equals("null") && memberDTO.getUserPw().equals("null"))) {
                    Intent intent8 = new Intent(this, WebviewActivity.class);
                    intent8.putExtra("web_address", "http://www.chol24.com/app_pds.php");
                    intent8.putExtra("title_text", "자료실");
                    startActivity(intent8);
                    mMainDrawer.closeDrawer(mNavMenu);
                } else {
                    setLoginDialog();
                }
                break;

            case R.id.menu_noti_button:
                if (!(memberDTO.getUserId().equals("null") && memberDTO.getUserPw().equals("null"))) {
                    Intent intent5 = new Intent(this, WebviewActivity.class);
                    intent5.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent5.putExtra("web_address", "http://www.chol24.com/app_notice.php");
                    intent5.putExtra("title_text", "공지사항");
                    startActivity(intent5);
                    mMainDrawer.closeDrawer(mNavMenu);
                } else {
                    setLoginDialog();
                }
                break;
        }
    }

    public void setLoginDialog() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Main.this);
        alert_confirm.setMessage("로그인이 필요한 서비스 입니다.\n로그인 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences.Editor editor = mPref.edit();
//                        editor.clear();
//                        editor.commit();
//                        Log.d("------", "onClick: " + mPref.getString("token", "null"));
                        Intent intent = new Intent(Main.this, Login.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("취소", null);
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    private void showDialogForLocationServiceSetting(final Class activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("사진에 위치 정보를 저장하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 서비스 활성화를 원하시면 설정을 누르세요.");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);

            }
        });
//        builder.setNegativeButton("취소", null);
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                builder.setMessage("위치 서비스 비활성화로 사진에 위치 정보가 저장 되지 않습니다.");
                builder.setCancelable(true);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Main.this, activity);
                        startActivity(intent);
                    }
                });
                builder.create().show();


            }
        });
        builder.create().show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                Intent intent = new Intent(this, mActivity);
                startActivity(intent);

                //설정 들어간뒤의 화면
                //여기서는 화면 처음에 위치서비스 활성화 안됏을때 물어보는 곳
                //아무 설정안하고 사진찍을때 다시 체크한뒤 쓰레드 타게
                break;
        }
    }

    public boolean checkLocationServicesStatus() {

        String gpsEnabled = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        return gpsEnabled.matches(".*gps.*");
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (mMainDrawer.isDrawerOpen(mNavMenu)) {
            mMainDrawer.closeDrawer(mNavMenu);
        } else if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isForeGround = false;
    }
}
