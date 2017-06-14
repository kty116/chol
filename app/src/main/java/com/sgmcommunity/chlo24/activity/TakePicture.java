package com.sgmcommunity.chlo24.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import com.sgmcommunity.chlo24.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePicture extends CustomActivity {

    private ImageView mPictureImage;
    private File extraFile;
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        mPictureImage = (ImageView) findViewById(R.id.picture_image);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri());
        startActivityForResult(intent, 1);

    }

    private Uri getFileUri() {
        File dir = new File(getFilesDir(), "img");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        extraFile = new File(dir, System.currentTimeMillis() + ".jpg");
        imgPath = extraFile.getAbsolutePath();
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", extraFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                    mPictureImage.setImageBitmap(bitmap);

                    saveBitmapToJpeg("Chol24", bitmap);
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri());
//                    startActivityForResult(intent, 1);

                    break;

                //사진 찍은거 있으면 사진 찍기랑 신고하러 가기 버튼해서 신고 페이지로 바로 이동
            }
        } else {
            //해당 액티비티 종료
            finish();
        }
    }

    public void saveBitmapToJpeg(String albumFileName, Bitmap bitmapImage) {
        //외부스토리지에 저장하면 앱이 지워져도 사진은 그대로

        File albumFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumFileName);

        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";

        if (!albumFile.isDirectory()) {
            albumFile.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(albumFile.getAbsolutePath() + "/" + imageName);
            Log.d("----------", "getAlbumStorageDir: " + albumFile.getAbsolutePath() + "/" + imageName);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
