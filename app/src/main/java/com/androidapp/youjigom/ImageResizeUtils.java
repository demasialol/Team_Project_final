package com.androidapp.youjigom;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageResizeUtils {

    //이미지의 너비를 변경한다.
    public static void resizeFile(File file, File newFile, int newWidth, Boolean isCamera) {
        String TAG = "KNLBDC";
        Bitmap originalBm = null;
        Bitmap resizedBitmap = null;
        try {
            //비트맵 객체 생성
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inDither = true;
            //파일 경로를 비트 맵으로 디코딩합니다.
            originalBm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            // 카메라인 경우 이미지를 상황에 맞게 회전시킨다
            if(isCamera) {
                try {
                    //핸드폰 내부의 카메라를 사용하여 촬영을 하고 사진정보를 받아와 인텐트에 보여줄 때
                    //보여주고자 하는 사진이 회전되어 의도와는 다르게 표시되는 오류를 정정
                    ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    Log.d(TAG,"exifDegree : " + exifDegree);

                    originalBm = rotate(originalBm, exifDegree);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //오류처리
            if(originalBm == null) {
                Log.e(TAG,("파일 에러"));
                return;
            }
            int width = originalBm.getWidth();
            int height = originalBm.getHeight();

            //클래스 선언 때 받아온 int newWidth을 기반으로 width와 height를 조정하기 위한 코드
            float aspect, scaleWidth, scaleHeight;
            if(width > height) {
                if(width <= newWidth) return;
                aspect = (float) width / height;
                scaleWidth = newWidth;
                scaleHeight = scaleWidth / aspect;
            } else {
                if(height <= newWidth) return;
                aspect = (float) height / width;
                scaleHeight = newWidth;
                scaleWidth = scaleHeight / aspect;

            }

            // 비트맵 조정을 위해서 사용될 matrix 선언
            Matrix matrix = new Matrix();

            // 비트맵 크기 재조정
            matrix.postScale(scaleWidth / width, scaleHeight / height);

            // 새로운 비트맵을 생성해서 바꾼 값 넣어주기
            resizedBitmap = Bitmap.createBitmap(originalBm, 0, 0, width, height, matrix, true);

            //안드로이드의 sdk버전과 코드 버전에 따라서 재조정되는 확장자가 틀려짐
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                resizedBitmap.compress(CompressFormat.JPEG, 80, new FileOutputStream(newFile));
            } else {
                resizedBitmap.compress(CompressFormat.PNG, 80, new FileOutputStream(newFile));
            }

            //오류처리 및 예외처리
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(originalBm != null){
                originalBm.recycle();
            }
            if (resizedBitmap != null){
                resizedBitmap.recycle();
            }
        }
    }


    //EXIF 정보를 회전각도로 변환하는 메서드

    public static int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    //이미지를 회전시킵니다.
    public static Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);
            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
}