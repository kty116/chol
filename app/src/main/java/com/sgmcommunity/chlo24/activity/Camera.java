package com.sgmcommunity.chlo24.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.flurgle.camerakit.DisplayOrientationDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.sgmcommunity.chlo24.R;
import com.sgmcommunity.chlo24.common.GPS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Camera extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private boolean isLand;
    private int orient;
    private boolean isGpsMode;
    private CameraView mCameraView;
    private View mCaptureButton;
    private GoogleApiClient mGoogleApiClient;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private String fileName;
    private android.hardware.Camera camera;
    private Location mLastLocation;
    private DisplayOrientationDetector mDisplayOrientationDetector;
    private FrameLayout mParent;
    private Display display;
    private OrientationEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mParent = (FrameLayout) findViewById(R.id.parent);
        mCameraView = (CameraView) findViewById(R.id.camera_view);
        mCaptureButton = findViewById(R.id.capture_button);
        mCaptureButton.setOnClickListener(this);

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        eventListener = new OrientationEventListener(getBaseContext(), SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {

                if(orientation > 45 && orientation <= 135){
//                    Log.d("onOrientationChanged: ", "거꾸로 가로");
                    orient = 180;

                }else if (orientation > 135 && orientation <= 225){
//                    Log.d("onOrientationChanged: ", "거꾸로 세로");
                    orient = 270;

                }else if (orientation > 225 && orientation <=315){
//                    Log.d("onOrientationChanged: ", "가로");
                    orient = 0;

                }else {
//                    Log.d("onOrientationChanged: ", "세로");
                    orient = 90;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
        eventListener.enable();
//        if (!checkLocationServicesStatus()) { //gps모드 비 활성화시
//            showDialogForLocationServiceSetting();
//        }
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

                        Log.d("onPictureTaken: ", String.valueOf(mCameraView.getRotation()));

                        // Create a bitmap
                        Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                        Bitmap bitmap = imgRotate(result, orient);

                        fileName = saveBitmapToJpeg("Chol24", bitmap);
                        if (checkLocationServicesStatus()) {  //gps모드 활성화
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    buildGoogleApiClient();

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
    public String saveBitmapToJpeg(String albumFileName, Bitmap bitmapImage) {
        //외부스토리지에 저장하면 앱이 지워져도 사진은 그대로

        File Folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), albumFileName);

        String file = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";

        if (!Folder.isDirectory()) {
            Folder.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Folder + "/" + file);
            Log.d("----------", "getAlbumStorageDir: " + Folder + "/" + file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //파일 저장 후 미디어 스캐닝
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + Folder + "/" + file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "사진을 저장했습니다.", Toast.LENGTH_SHORT).show();

        return Folder + "/" + file;
    }

    public void setMetaData(String fileName, double latitude, double longitude) throws IOException {

        ExifInterface mexif = new ExifInterface(fileName);
        mexif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(latitude));
        mexif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(longitude));
        mexif.saveAttributes();

    }

    private String getTagString(String tag, ExifInterface exif) {
        return exif.getAttribute(tag);
    }

    public LatLng getMetaData(String fileName) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileName);

            String bef_latitude = getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
            String bef_longtude = getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);

            double aft_latitude = changeCoordinates(bef_latitude);
            double aft_longtude = changeCoordinates(bef_longtude);

            Log.d("getMetaData ", aft_latitude + " " + aft_longtude);

            Toast.makeText(this, getCurrentAddress(new LatLng(aft_latitude, aft_longtude)), Toast.LENGTH_LONG
            ).show();

            return new LatLng(aft_latitude, aft_longtude);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 좌표 변환
     *
     * @param coordinate
     * @return
     */
    public double changeCoordinates(String coordinate) {
        String[] s = coordinate.split(",");
        String[] head = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            head[i] = s[i].substring(0, s[i].lastIndexOf("/"));
        }

        int degree = Integer.parseInt(head[0]);
        double minute = Double.parseDouble(head[1]);
        String sSecond = head[2];
        String h = sSecond.substring(0, 2);
        String t = sSecond.substring(2);
        double second = Double.parseDouble(h + "." + t);

        return degree + minute / 60 + second / 3600;
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//연결됐을때
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d("위도경도 : ", mLastLocation.getLatitude() + "" + mLastLocation.getLongitude());
            if (mLastLocation.getLatitude() != 0) {
                //위도값이 없을때 메타데이터에 값 넣지 않음
                try {
                    setMetaData(fileName, mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    getMetaData(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

                mLastLocation = null;
                mGoogleApiClient.disconnect();
                mGoogleApiClient = null;
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //연결에 실패했을때

        Log.d("onConnectionFailed: ", "위치정보 가져올 수 없음");

    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("사진에 위치 정보를 저장하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 서비스를 활성화를 원하시면 설정을 누르세요.");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //여기서는 화면 처음에 위치서비스 활성화 안됏을때 물어보는 곳
                //아무 설정안하고 사진찍을때 다시 체크한뒤 쓰레드 타게
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

//    private boolean checkCameraHardware() {
//
//        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//
//            // 카메라가 최소한 한개 있는 경우 처리
//
//            Log.i("dddd", "Number of available camera : " + camera.getNumberOfCameras());
//
//            return true;
//
//        } else {
//
//            // 카메라가 전혀 없는 경우
//
//            Toast.makeText(this, "No camera found!", Toast.LENGTH_SHORT).show();
//
//            return false;
//
//        }
//
//    }

    public String getCurrentAddress(LatLng latLng) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1);
        } catch (IOException ioException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }
}
