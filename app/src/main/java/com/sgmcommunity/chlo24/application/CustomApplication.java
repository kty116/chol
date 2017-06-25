package com.sgmcommunity.chlo24.application;

import android.app.Application;

//import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by hunter on 2017-05-29.
 */

public class CustomApplication extends Application{
//        implements IAdobeAuthClientCredentials {
//
//    private static final String CREATIVE_SDK_CLIENT_ID      = "805eab314c7c43fda1c56a2638ec47e3";
//    private static final String CREATIVE_SDK_CLIENT_SECRET  = "9dcd171a-af56-484d-85fe-f83f4269f1c5";
//    private static final String CREATIVE_SDK_REDIRECT_URI   = "ams+859ab3715b90b91ec8ef7a9947a98ed06fe1f52f://adobeid/805eab314c7c43fda1c56a2638ec47e3";
//    private static final String[] CREATIVE_SDK_SCOPES       = {"email", "profile", "address"};
//
//    private AppStatus mAppStatus = AppStatus.FOREGROUND;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
//    }
//    @Override
//    public String getClientID() {
//        return CREATIVE_SDK_CLIENT_ID;
//    }
//
//    @Override
//    public String getClientSecret() {
//        return CREATIVE_SDK_CLIENT_SECRET;
//    }
//
//    @Override
//    public String[] getAdditionalScopesList() {
//        return CREATIVE_SDK_SCOPES;
//    }
//
//    @Override
//    public String getRedirectURI() {
//        return CREATIVE_SDK_REDIRECT_URI;
//    }
//
//    public CustomApplication get(Context context) {
//        return (CustomApplication) context.getApplicationContext();
//    }
//
//    public AppStatus getAppStatus() {
//        return mAppStatus;
//    }
//
//    // check if app is foreground
//    public boolean isForeground() {
//        return mAppStatus.ordinal() > AppStatus.BACKGROUND.ordinal();
//    }
//
//    public enum AppStatus {
//        BACKGROUND, // app is background
//        RETURNED_TO_FOREGROUND, // app returned to foreground (or first launch)
//        FOREGROUND; // app is foreground
//    }
//
//    public class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
//
//        // running activity count
//        private int running = 0;
//
//        @Override
//        public void onActivityCreated(Activity activity, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onActivityStarted(Activity activity) {
//            if (++running == 1) {
//                // running activity is 1
//                // app must be returned from background just now (or first launch)
//                mAppStatus = AppStatus.RETURNED_TO_FOREGROUND;
//                Log.d("-----------", "onActivityStopped: "+mAppStatus);
//            } else if (running > 1) {
//                // 2 or more running activities,
//                // should be foreground already.
//                mAppStatus = AppStatus.FOREGROUND;
//                Log.d("-----------", "onActivityStopped: "+mAppStatus);
//            }
//        }
//
//        @Override
//        public void onActivityResumed(Activity activity) {
//        }
//
//        @Override
//        public void onActivityPaused(Activity activity) {
//        }
//
//        @Override
//        public void onActivityStopped(Activity activity) {
//            if (--running == 0) {
//                // no active activity
//                // app goes to background
//                mAppStatus = AppStatus.BACKGROUND;
//                Log.d("-----------", "onActivityStopped: "+mAppStatus);
//            }
//        }
//
//        @Override
//        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
//        }
//
//        @Override
//        public void onActivityDestroyed(Activity activity) {
//            Log.d("-----------", "onActivityStopped: "+mAppStatus);
//
//        }
//    }
}
