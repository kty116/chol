package com.sgmcommunity.chlo24.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.sgmcommunity.chlo24.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Video extends AppCompatActivity implements View.OnClickListener {

    private CameraView mVideoView;
    private boolean isRecoding;
    private Button mRecodingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mVideoView = (CameraView) findViewById(R.id.video_view);
        mRecodingButton = (Button) findViewById(R.id.recoding_button);
        mRecodingButton.setOnClickListener(this);

        mVideoView.setMethod(CameraKit.Constants.METHOD_STANDARD);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onPause() {
        mVideoView.stopRecordingVideo();
        mVideoView.stop();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recoding_button:
                if (isRecoding == false) {
                    mVideoView.setCameraListener(new CameraListener() {
                        @Override
                        public void onVideoTaken(File video) {
                            super.onVideoTaken(video);
                            //동영상 파일 처리

                            saveVideo("Chol24", video);
                        }
                    });

                    mVideoView.startRecordingVideo();
                    isRecoding = true;
                } else {
                    Log.d("onClick: ","isRecoding: "+isRecoding);
                    mVideoView.stopRecordingVideo();
                    isRecoding = false;
                }

                break;
        }

    }

    public void saveVideo(String albumFileName, File video) {
        //외부스토리지에 저장하면 앱이 지워져도 사진은 그대로

        File Folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), albumFileName);

        String file = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";

        if (!Folder.isDirectory()) {
            Folder.mkdirs();
        }
        FileInputStream input = null;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Folder + "/" + file);
            input = new FileInputStream(video);

                byte[] buffer = new byte[file.length()];
                int readCount = 0;

                while ((readCount = input.read(buffer)) != -1) {
                    out.write(buffer);
                }
            out.close();
            //파일 저장 후 미디어 스캐닝
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + Folder + "/" + file)));
//            setMetaData(Folder + "/" + file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "동영상을 저장했습니다.", Toast.LENGTH_SHORT).show();
    }
}
