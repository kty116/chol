package com.sgmcommunity.chlo24.activity;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sgmcommunity.chlo24.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private Button mButton;
    private SurfaceView mSurface;
    private Camera cam;
    private SurfaceHolder sv;
    private MediaRecorder mediaRecorder;
    private boolean recording = false;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        mParent = (FrameLayout) findViewById(R.id.parent);
        mSurface = (SurfaceView) findViewById(R.id.surface_view);
        mButton = (Button) findViewById(R.id.take_button);
        mButton.setOnClickListener(this);
        mediaRecorder = new MediaRecorder();
        cam = android.hardware.Camera.open();
        cam.setDisplayOrientation(90);
        sv = mSurface.getHolder();
        sv.addCallback(this);
        sv.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        List psizes = cam.getParameters().getSupportedPreviewSizes();
        psizes.size();

//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        width = size.x;
//        height = size.y;


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (cam == null) {

                cam.setPreviewDisplay(holder);
//                cam.startPreview();
            }

        } catch (IOException e) {
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (sv.getSurface() == null) {
            // preview surface does not exist

            return;
        }
        // stop preview before making changes
        try {
            cam.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(cam);
        try {
            cam.setPreviewDisplay(sv);
            cam.startPreview();
        } catch (Exception e) {
        }


//        refreshCamera(cam);


//    public void refreshCamera(Camera camera) {

    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        cam = camera;
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        mediaRecorder = null;
        cam.stopPreview();
        cam.release();
        cam = null;

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            cam.lock();
            recording = false;
        }
    }

    @Override
    public void onClick(View v) {

        if (recording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            cam.lock();
            recording = false;

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Main2Activity.this, "succeed", Toast.LENGTH_LONG).show();
                    try {

                        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                        cam.unlock();
                        mediaRecorder.setCamera(cam);
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                        mediaRecorder.setVideoFrameRate(15);
                        mediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
                        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
//
//                        mediaRecorder.setProfile(profile);
//                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);


                        mediaRecorder.setOrientationHint(90);
                        mediaRecorder.setOutputFile(new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DCIM), "Chol24") + "/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4");

                        mediaRecorder.setPreviewDisplay(sv.getSurface());
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        recording = true;
                        Toast.makeText(Main2Activity.this, "start", Toast.LENGTH_LONG).show();
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                        mediaRecorder.release();
                        return;

                        // Log.i("---","Exception in thread");
                    }
                }
            });

//        if (cameraFragment != null) {
//            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultListener() {
//                @Override
//                public void onVideoRecorded(String filePath) {
//                    Log.d("onVideoRecorded: ",filePath);
//
//                }
//
//                @Override
//                public void onPhotoTaken(byte[] bytes, String filePath) {
//                    Log.d("onPhotoTaken: ", filePath);
//
//                }
//            },new File(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DCIM), "Chol24") + "",
//                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
//        }


        }
    }
}
