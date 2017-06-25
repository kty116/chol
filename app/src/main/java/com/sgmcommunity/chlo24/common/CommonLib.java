package com.sgmcommunity.chlo24.common;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.protocol.HttpContext;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by hunter on 2017-05-29.
 */

public class CommonLib{

    public static void cookieMaker(AsyncHttpClient mhttpClient, Context mContext) {
        //롤리팝 이하 버전 cookiesyncmanager로 사용

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(mContext);
        }

        HttpContext httpContext = mhttpClient.getHttpContext();
        CookieStore cookieStore = (CookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
        List<Cookie> cookies = cookieStore.getCookies();
//        Log.d(TAG, "cookieMaker: "+cookies.toString());

        CookieManager cookieManager = CookieManager.getInstance();

        for (int i = 0; i < cookies.size(); i++) {
            Cookie eachCookie = cookies.get(i);
            String cookieString = eachCookie.getName() + "=" + eachCookie.getValue();
            cookieManager.setCookie("http://www.chol24.com", cookieString);
//            Log.i(">>>>>", "cookie : " + cookieString);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        }else {
            CookieManager.getInstance().flush();
        }
    }

}

