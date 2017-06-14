package com.sgmcommunity.chlo24.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.activity.Main;
import com.sgmcommunity.chlo24.activity.PushMessageWebview;
import com.sgmcommunity.chlo24.activity.Splash;
import com.sgmcommunity.chlo24.application.CustomApplication;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by hunter on 2017-05-18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String contents = null;
    private Intent intent;
    private String appStatus = "BACKGROUND";
    private SharedPreferences mPref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //포그라운드에서만 작동

        String rr = remoteMessage.getData().toString();

        //데이터 추출
        String[] ss = rr.split(", ");
        contents = ss[0].substring(10);
        String body = ss[1].substring(5);
        int ds = ss[2].lastIndexOf("}");
        String title = ss[2].substring(6, ds);
        appStatus = ((CustomApplication) getApplication()).getAppStatus().toString();
        sendNotification(title, body);

        Log.d(TAG, "onMessageReceived:-------------- "+contents);

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int pushCount = mPref.getInt("push_count",0);


        //푸시카운트 가져와서

        ++pushCount; //푸시카운트 ++하고
        setBadge(this, pushCount); //아이콘 벳지 셋팅 //숫자 바뀔때마다 셋팅 새로 해줘야 바뀜


        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("push_count", pushCount);
        editor.commit();
        //푸시 카운트 저장

        Log.d("-----------", "onMessageReceived: " + pushCount);

        Log.d("-------0", "onCreate: " + ((CustomApplication) getApplication()).getAppStatus());

    }

    private void sendNotification(String title, String text) {

        if(Main.isForeGround) {
            intent = new Intent(this, PushMessageWebview.class);
        }else {
//            백그라운드 일때
            intent = new Intent(this, Splash.class); //서비스에서 메인액티비티로 가는 인텐트
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0/* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //intent를 스플래쉬로 주고 스플래쉬 후 해당 노티내용에 맞는 내용 액티비티 띄우고 난 후에 뒤로 버튼 누르면 메인 액티비티

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)  //알림 텍스트의 첫번째 줄
                .setContentText(text) //알림 텍스트의 2번째 줄
                .setAutoCancel(true) //사용자가 노티를 클릭했을때 자동으로 없어짐
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent); //노티 클릭될때 인텐트 보내는걸 제공한다


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * 아이콘 위에 숫자 표시 메소드
     *
     * @param context
     * @param count
     */
    public static void setBadge(final Context context, final int count) {
        final String launcherClassName = getLauncherClassName(context);

        if (launcherClassName == null) {
            return;
        }

        final Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count > 0 ? count : null);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);

        context.sendBroadcast(intent);
    }

    private static String getLauncherClassName(Context context) {
        final PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (final ResolveInfo resolveInfo : resolveInfos) {
            final String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }

        return null;
    }
}
