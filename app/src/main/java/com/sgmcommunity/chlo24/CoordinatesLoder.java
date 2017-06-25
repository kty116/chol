package com.sgmcommunity.chlo24;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.sgmcommunity.chlo24.EventBus.CompliteCoordinatesEvent;
import com.sgmcommunity.chlo24.common.GPS;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
/**
 * Created by hunter on 2017-06-21.
 */

public class CoordinatesLoder implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient googleApiClient;
    private String fileName;
    private Location mLastLocation;
    private boolean isImageFile;
    private MediaRecorder mediaRecorder;

    public CoordinatesLoder(Context context, String fileName, boolean isImageFile, MediaRecorder mediaRecorder) {
        this.context = context;
        this.fileName = fileName;
        this.isImageFile = isImageFile;
        this.mediaRecorder = mediaRecorder;
    }

    public CoordinatesLoder(Context context, String fileName,boolean isImageFile) {
        this.context = context;
        this.fileName = fileName;
        this.isImageFile = isImageFile;
    }

    public synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//연결됐을때

//        Toast.makeText(context, "구글 클라이언트 연결 성공", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

//            String lat = String.valueOf(mLastLocation.getLatitude());
//            String lon = String.valueOf(mLastLocation.getLongitude());

//            Toast.makeText(context, mLastLocation.getLatitude() + "" + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//            Log.d("위도경도 : ", mLastLocation.getLatitude() + "" + mLastLocation.getLongitude());
            if (mLastLocation != null) {
                //위도값이 없을때 메타데이터에 값 넣지 않음

                if(isImageFile == true) {

                    try {
                        setMetaData(fileName, mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                        getMetaData(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
//                    Toast.makeText(context, "메타데이터 오류", Toast.LENGTH_SHORT).show();
                    }
                }else {

                    EventBus.getDefault().post(new CompliteCoordinatesEvent(mLastLocation.getLatitude(),mLastLocation.getLongitude()));


//                    mediaRecorder.setLocation(Float.valueOf(String.valueOf(mLastLocation.getLatitude())),Float.valueOf(String.valueOf(mLastLocation.getLongitude())));
//                    ContentValues values = new ContentValues();

//                    values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo");
//                    values.put(MediaStore.Audio.Media.ALBUM, "Video Album");
//                    values.put(MediaStore.Video.Media.);
//                    values.put(MediaStore.Video.Media.LATITUDE, mLastLocation.getLatitude());
//                    values.put(MediaStore.Video.Media.LONGITUDE, mLastLocation.getLongitude());
//                    values.put(MediaStore.Audio.Media.ARTIST, "Mike");
//                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Video");
//                    values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
//                    values.put(MediaStore.MediaColumns.MIME_TYPE, "video/");
//                    values.put(MediaStore.MediaColumns.);
//                    values.put(MediaStore.Audio.Media.DATA, filename);

//                    Uri videoUri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//                    if (videoUri == null) {
//                        Log.d("SampleVideoRecorder", "Video insert failed.");
//                        return;
//                    }else {


//                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                        Uri uri = Uri.parse("file://"+fileName);
////                        Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,);
//                        retriever.setDataSource(context,uri);
//
//                        Toast.makeText(context, retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION), Toast.LENGTH_SHORT).show();
                    }

//                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileName));
//                }
            }else {
//                Toast.makeText(context, "값 없음", Toast.LENGTH_SHORT).show();
            }
            if (googleApiClient != null && googleApiClient.isConnected()) {

                mLastLocation = null;
                googleApiClient.disconnect();
                googleApiClient = null;
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //연결에 실패했을때
//        Toast.makeText(context, "구글 클라이언트 연결 실패", Toast.LENGTH_SHORT).show();

//        Log.d("onConnectionFailed: ", "위치정보 가져올 수 없음");

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

//            Log.d("getMetaData ", aft_latitude + " " + aft_longtude);
//
//            Toast.makeText(context, getCurrentAddress(new LatLng(aft_latitude, aft_longtude)), Toast.LENGTH_LONG
//            ).show();

            return new LatLng(aft_latitude, aft_longtude);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

//    public set

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


//    public boolean checkLocationServicesStatus() {
//        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
//
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }

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

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1);
        } catch (IOException ioException) {
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }
}
