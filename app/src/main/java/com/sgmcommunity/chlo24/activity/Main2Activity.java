package com.sgmcommunity.chlo24.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sgmcommunity.chlo24.CoordinatesLoder;
import com.sgmcommunity.chlo24.EventBus.CompliteCoordinatesEvent;
import com.sgmcommunity.chlo24.EventBus.MessageEvent;
import com.sgmcommunity.chlo24.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private SurfaceView mSurface;
    private Camera cam;
    private SurfaceHolder sv;
    private MediaRecorder mediaRecorder;
    private boolean recording = false;
    private ImageView mCapterButton;
    private OrientationEventListener eventListener;
    private int orient;
    private String mFileName;
    private File mFolder;
    private TextView mTimeText;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mSurface = (SurfaceView) findViewById(R.id.surface_view);
        mCapterButton = (ImageView) findViewById(R.id.take_button);
        mCapterButton.setOnClickListener(this);
        mTimeText = (TextView) findViewById(R.id.time_text);

//        cam = android.hardware.Camera.open();
//        cam.setDisplayOrientation(90);
        sv = mSurface.getHolder();
        sv.addCallback(this);
        sv.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        eventListener = new OrientationEventListener(getBaseContext(), SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {

                if (orientation > 45 && orientation <= 135) {
//                    Log.d("onOrientationChanged: ", "거꾸로 가로");
                    orient = 180;

                } else if (orientation > 135 && orientation <= 225) {
//                    Log.d("onOrientationChanged: ", "거꾸로 세로");
                    orient = 270;

                } else if (orientation > 225 && orientation <= 315) {
//                    Log.d("onOrientationChanged: ", "가로");
                    orient = 0;

                } else {
//                    Log.d("onOrientationChanged: ", "세로");
                    orient = 90;
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventListener.enable();
//        if (!checkLocationServicesStatus()) { //gps모드 비 활성화시
//            showDialogForLocationServiceSetting();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventListener.disable();

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cam = Camera.open();
        cam.setDisplayOrientation(90);
        try {
            cam.setPreviewDisplay(holder);
        } catch (IOException exception) {
            cam.release();
            cam = null;

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        try {
            cam.stopPreview();
        } catch (Exception e) {
        }
        Camera.Parameters parameters = cam.getParameters();
        try {
            cam.setPreviewDisplay(sv);
//
//            String focusmode = enabled ? ENABLEDAUTOFOCUSSETTING : DISABLEDAUTOFOCUSSETTING;
//            if(parameters.getFocusMode() != focusmode && isSupported(focusmode, this.mParameters.getSupportedFocusModes())){
//                parameters.setFocusMode(focusmode);
//            }
//            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            cam.setParameters(parameters);
            cam.startPreview();
        } catch (Exception e) {
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (cam != null) {
            cam.stopPreview();
            cam.release();
            cam = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event instanceof CompliteCoordinatesEvent) {
            CompliteCoordinatesEvent comEvent = (CompliteCoordinatesEvent) event;
            runMediaRecorder(comEvent.getLat(), comEvent.getLon());
//            setCurrentTime();
        }

    }

    @Override
    public void onClick(View v) {

        if (recording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            recording = false;
            time = 0;
            mCapterButton.setImageResource(R.drawable.ic_camera_white);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + mFolder + "/" + mFileName)));
            cam.lock();

            Toast.makeText(this, "동영상을 저장했습니다.", Toast.LENGTH_SHORT).show();
//
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            Uri uri = Uri.parse("file://" + mFolder + "/" + mFileName);
//            retriever.setDataSource(this, uri);

//            Toast.makeText(this, retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION), Toast.LENGTH_SHORT).show();
//            Log.d("onClick: ",retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION));

        } else {
            //녹화 시작

//            if (checkLocationServicesStatus()) {  //gps모드 활성화
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("run: ","ddddddddddddddddddd");
                        CoordinatesLoder coordinatesLoder = new CoordinatesLoder(getBaseContext(), mFolder + "/" + mFileName, false, mediaRecorder);
                        coordinatesLoder.buildGoogleApiClient();
                    }
                }).start();
//            }

            mCapterButton.setImageResource(R.drawable.ic_camera_blue);
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

    public void runMediaRecorder(final double lat, final double lon) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {

                    mediaRecorder = new MediaRecorder();
                    cam.unlock();
                    mediaRecorder.setCamera(cam);
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                    mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

                    mediaRecorder.setOrientationHint(orient);
                    mediaRecorder.setMaxDuration(50000);

                    mFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Chol24");
                    mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";

                    mediaRecorder.setOutputFile(mFolder + "/" + mFileName);
                    if (lat != 0.0) {
                        mediaRecorder.setLocation(Float.valueOf(String.valueOf(lat)), Float.valueOf(String.valueOf(lon)));
                    }

                    mediaRecorder.setPreviewDisplay(sv.getSurface());
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    recording = true;

                    Thread.sleep(1000);
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    mediaRecorder.release();
                    return;

                }
            }
        });

    }

    public void setCurrentTime() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                while (recording) {

                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time += 1000;
//                    mTimeText.setText(getTime(time));
                    Log.d("run: ", getTime(time));

                }
            }
        });
    }

    private String getTime(long average) {
        // 초,분,시간 단위로 보여주기 위해 "0시 0분 0초" type으로 변경해서 return
        StringBuffer sb = new StringBuffer();
        long second = (average / 1000) % 60;
        long minute = (average / (1000 * 60)) % 60;
        long hour = (average / (1000 * 60 * 60)) % 24;
        if (hour > 0) {
            sb.append((int) hour + "시 ");
        }
        if (minute > 0) {
            sb.append((int) minute + "분 ");
        }
        if (second > 0) {
            sb.append((int) second + "초 ");
        }
        if (sb.toString().length() == 0) {
            if (average > 0) {
                sb.append("0초 미만 (" + average + ")");
            } else {
                sb.append("0초 미만 ");
            }
        }
        return sb.toString();
    }
}


