package com.sgmcommunity.chlo24.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.push.MyFirebaseMessagingService;

public class Main extends CustomActivity implements View.OnClickListener {

    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private SharedPreferences mPref;
    private FrameLayout mMainContainer;
    private ImageView mSettingButton;
    private DrawerLayout mMainDrawer;
    private LinearLayout mNavMenu;
    public static boolean isForeGround;

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
        mSettingButton = (ImageView) findViewById(R.id.setting_button);
//        findViewById(R.id.my_page_button).setOnClickListener(this);
        findViewById(R.id.logout_button).setOnClickListener(this);
        mSettingButton.setOnClickListener(this);

        mNavMenu = (LinearLayout) findViewById(R.id.nav_menu);
        mMainContainer = (FrameLayout) findViewById(R.id.main_container);
        mMainDrawer = (DrawerLayout) findViewById(R.id.main_drawer);
        findViewById(R.id.intro_button).setOnClickListener(this);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

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
            case R.id.look_button:
                Intent intent = new Intent(this, WebviewActivity.class);
                intent.putExtra("web_address", "http://chol24.com/app_list.php");
                intent.putExtra("title_text", "캠페인 보기");
                startActivity(intent);
                break;

            case R.id.participation_button:
                Intent intent1 = new Intent(this, WebviewActivity.class);
                intent1.putExtra("web_address", "http://www.chol24.com/bbs/app_write.php?bo_table=with");
                intent1.putExtra("title_text", "캠페인 참여");
                startActivity(intent1);
                break;

            case R.id.mobil_vs_button:
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(("http://www.chol24.com")));
                startActivity(intent2);
                break;

            case R.id.setting_button:

                mMainDrawer.openDrawer(mNavMenu);

                break;

            case R.id.logout_button:

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Main.this);
                alert_confirm.setMessage("로그아웃 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = mPref.edit();
                                editor.clear();
                                editor.commit();
                                Log.d("------", "onClick: " + mPref.getString("token", "null"));
                                Intent intent3 = new Intent(Main.this, Login.class);
                                startActivity(intent3);
                                finish();
                            }
                        }).setNegativeButton("취소", null);
                AlertDialog alert = alert_confirm.create();
                alert.show();

                break;

            case R.id.intro_button:

                Intent intent3 = new Intent(this, Intro.class);
                startActivity(intent3);
                mMainDrawer.closeDrawer(mNavMenu);
                break;

            case R.id.video_button:
                Intent intent4 = new Intent(this, Camera.class);
                startActivity(intent4);


                break;

            case R.id.menu_inquiry_button:

                Intent intent6 = new Intent(this, Main2Activity.class);
                startActivity(intent6);

//                Intent intent1 = new Intent(this, WebviewActivity.class);
//                intent1.putExtra("web_address", "http://www.chol24.com/bbs/app_write.php?bo_table=with");
//                intent1.putExtra("title_text", "캠페인 참여");
//                startActivity(intent1);

//                // GPS 사용유무 가져오기
//                if (gps.isGetLocation()) {
//
//                    double latitude = gps.getLatitude();
//                    double longitude = gps.getLongitude();
//
////                    gps.stopUsingGPS();
//
//                    Log.d("----------", "onClick: " + "위도: " + latitude + " 경도: " + longitude);
//                } else {
//                    // GPS 를 사용할수 없으므로
//                    gps.showSettingsAlert();
//                }
                break;

            case R.id.menu_morgue_button:
                // TODO: 2017-06-01  폴더에 있는 데이터 가져와서 뿌리기 구현

//                Intent intent8 = new Intent(this,Maps.class);
//                startActivity(intent8);

                break;

            case R.id.menu_noti_button:
                Intent intent5 = new Intent(this, WebviewActivity.class);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent5.putExtra("web_address", "http://www.chol24.com/bbs/app_notice.php");
                intent5.putExtra("title_text", "공지사항");
                startActivity(intent5);
                mMainDrawer.closeDrawer(mNavMenu);
                break;
        }
    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            Log.d("======", "onLocationChanged: 위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : " + accuracy);
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
// end of class


@Override
public void onBackPressed(){
        long tempTime=System.currentTimeMillis();
        long intervalTime=tempTime-backPressedTime;

        if(mMainDrawer.isDrawerOpen(mNavMenu)){
        mMainDrawer.closeDrawer(mNavMenu);
        }else if(0<=intervalTime&&FINSH_INTERVAL_TIME>=intervalTime){
        super.onBackPressed();
        }else{
        backPressedTime=tempTime;
        Toast.makeText(getApplicationContext(),"'뒤로' 버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
        }

@Override
protected void onDestroy(){
        super.onDestroy();
        isForeGround =false;

        }
        }
