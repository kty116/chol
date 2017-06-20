package com.sgmcommunity.chlo24.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sgmcommunity.chlo24.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mSurface = (SurfaceView) findViewById(R.id.surface_view);
        mCapterButton = (ImageView) findViewById(R.id.take_button);
        mCapterButton.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {

        if (recording) {

            mediaRecorder.stop();
            mediaRecorder.release();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + mFolder + "/" + mFileName)));
            cam.lock();
            recording = false;


        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(Main2Activity.this, "succeed", Toast.LENGTH_LONG).show();
                    try {

                        mediaRecorder = new MediaRecorder();
//                        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                        cam.unlock();
                        mediaRecorder.setCamera(cam);
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

//                        mediaRecorder.setVideoFrameRate(15);
//                        mediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
                        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

                        mediaRecorder.setOrientationHint(orient);

                        mFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Chol24");
                        mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";

                        mediaRecorder.setOutputFile(mFolder + "/" + mFileName);

                        mediaRecorder.setPreviewDisplay(sv.getSurface());
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        recording = true;
                        Toast.makeText(Main2Activity.this, "start", Toast.LENGTH_LONG).show();
                        Thread.sleep(1000);
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                        mediaRecorder.release();
                        return;

                    }
                }
            });
        }
    }
}
