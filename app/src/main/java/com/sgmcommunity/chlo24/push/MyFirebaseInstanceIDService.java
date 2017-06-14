package com.sgmcommunity.chlo24.push;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by hunter on 2017-05-18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
//        Log.d(TAG, "onTokenRefresh: -------------------------------------------");
//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "---------------------- "+token);

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
//        sendRegistrationToServer(token);
//        postData(token);
    }
}
