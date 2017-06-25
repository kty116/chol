package com.sgmcommunity.chlo24.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.sgmcommunity.chlo24.CoordinatesLoder;
import com.sgmcommunity.chlo24.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends AppCompatActivity implements View.OnClickListener {

    private int orient;
    private CameraView mCameraView;
    private View mCaptureButton;
    private OrientationEventListener eventListener;
    private File mFolder;
    private String mFileName;
    private FrameLayout mParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mParent = (FrameLayout) findViewById(R.id.parent);
        mCameraView = (CameraView) findViewById(R.id.camera_view);
        mCaptureButton = findViewById(R.id.capture_button);
        mCaptureButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
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
        eventListener.enable();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
        eventListener.disable();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture_button:
                mCameraView.setCameraListener(new CameraListener() {
                    @Override
                    public void onPictureTaken(byte[] picture) {
                        super.onPictureTaken(picture);

//                        Log.d("onPictureTaken: ", String.valueOf(mCameraView.getRotation()));

                        // Create a bitmap

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize=2;
                        Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length,options);

                        Bitmap bitmap = imgRotate(result, orient);

                        saveBitmapToJpeg("Chol24", bitmap);
                        if (checkLocationServicesStatus()) {  //gps모드 활성화
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    CoordinatesLoder coordinatesLoder = new CoordinatesLoder(getBaseContext(), mFolder + "/" + mFileName, true);
                                    coordinatesLoder.buildGoogleApiClient();
                                }
                            }).start();
                        }
                    }
                });

                mCameraView.captureImage();
        }
    }

    public static Bitmap imgRotate(Bitmap bmp, int orientation) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        Bitmap copyBitmap = resizedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copyBitmap);
        canvas.drawBitmap(bmp, 5.0f, 0.0f, null);

        return resizedBitmap;
    }

    /**
     * 비트맵 이미지 jpg로 디바이스에 저장
     *
     * @param albumFileName
     * @param bitmapImage
     */
    public void saveBitmapToJpeg(String albumFileName, Bitmap bitmapImage) {
        //외부스토리지에 저장하면 앱이 지워져도 사진은 그대로

        mFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), albumFileName);

        mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";

        if (!mFolder.isDirectory()) {
            mFolder.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mFolder + "/" + mFileName);
//            Log.d("----------", "getAlbumStorageDir: " + mFolder + "/" + mFileName);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //파일 저장 후 미디어 스캐닝
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + mFolder + "/" + mFileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "사진을 저장했습니다.", Toast.LENGTH_SHORT).show();

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
