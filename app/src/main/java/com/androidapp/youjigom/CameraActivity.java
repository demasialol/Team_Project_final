package com.androidapp.youjigom;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.androidapp.youjigom.UserInfo_number.UserInfo_0;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "KNLBDC";
    private static String StringImage;

    private Boolean isPermission = true;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private Boolean isCamera = false;
    private File tempFile;

    Button upload, btnCamera, btnGallery;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image);

        //activity_get_image 레이아웃에서 사용되는 버튼 선언
        upload = findViewById(R.id.upload);
        btnCamera= findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        upload.setVisibility(View.INVISIBLE);


        tedPermission();

        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 권한을 허용했을 경우 goToAlbum을 실행합니다.
                // 권한 허용에 동의하지 않았을 경우 토스트를 띄웁니다.
                if(isPermission) goToAlbum();
                else Toast.makeText(view.getContext(), getResources().
                        getString(R.string.permission_2), Toast.LENGTH_LONG).show();

            }
        });
        findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 권한을 허용했을 경우 takePhoto()를 실행합니다
                // 권한 허용에 동의하지 않았을 경우 토스트를 띄웁니다.
                if(isPermission)  takePhoto();
                else Toast.makeText(view.getContext(), getResources().
                        getString(R.string.permission_2), Toast.LENGTH_LONG).show();
            }
        });

    }

    //카메라 액티비티에서 권한을 허용한 뒤 선택 분기에 대한 코드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //선택 분기에서 앨범이나 카메라에서 이미지 선택을 취소했을 경우
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            //취소를 하기 전에 만들어진 tempFile이 있으면 삭제 시켜주는 구문
            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        // 앨범 버튼을 클릭하는 선택을 했을 경우
        if (requestCode == PICK_FROM_ALBUM) {
            //이미지 uri 얻어옴
            Uri photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

            Cursor cursor = null;
            try {
                //Uri 스키마를 content:/// 에서 file:/// 로  변경한다.
                //String 배열에 이미지의 projection값
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUri != null;
                //contentresolver는 contentprovider를 이용하여 데이터에 접근해 결과를 가져온다
                //cursor의 형태에 맞게 파일의 uri와 projection 값을 가져온다
                cursor = getContentResolver().query(photoUri, proj, null, null, null);
                assert cursor != null;
                //int column_index에 해당 필드 컬럼을 얻어옴
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // Cursor를 제일 첫번째 행(Row)으로 이동 시킨다.
                cursor.moveToFirst();
                //tempfile에 앨범에서 선택한 사진의 데이터값을 넣어준다
                tempFile = new File(cursor.getString(column_index));
                Log.d(TAG, "tempFile Uri : " + Uri.fromFile(tempFile));
            } finally {
                if (cursor != null) {
                    //이거 안해주면 액티비티가 끝나도 cursor가 작동하는 경우가 있기 때문에
                    //통상적으로 cursor 종료시 넣어줍니다.
                    cursor.close();
                }
            }
            setImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        }
    }


    //앨범에서 이미지 가져오기
    private void goToAlbum() {
        isCamera = false;

        //안드로이드 스튜디오 내부의 앨범 기능으로 Intent를 전환
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }



    //카메라를 이미지 가져오기
    private void takePhoto() {
        isCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            //이미지가 담길 임시 파일을 생성하는 함수 선언
            tempFile = createImageFile();
        } catch (IOException e) {
            // 오류처리
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        //파일이 정상적으로 생성되었으면 진행
        if (tempFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ////URI 가져오기
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.androidapp.youjigom", tempFile);
                //인텐트에 이미지가 저장될 URI담기
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                //인텐트 실행
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {
                //tempfile이 null일 때 다시 카메라에서 사진을 촬영
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }


    //폴더 및 파일 만들기
    //사진을 찍거나 앨범에서 가져올 때 이미지가 저장될 파일 생성
    private File createImageFile() throws IOException {
        // 이미지 파일 이름 사진 찍을 때 시간으로 지정
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "KNLBDC_" + timeStamp + "_";
        // 이미지가 저장될 파일 주소
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/KNLBDC/");
        if (!storageDir.exists()) storageDir.mkdirs();
        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());
        return image;
    }

    //tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
    private void setImage() {
        imageView = findViewById(R.id.imageView);
        //tempfile의 크기를 재조정
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        //비트맵으로 파일을 변환하기 위해 필요
        BitmapFactory.Options options = new BitmapFactory.Options();
        //파일을 비트맵으로 변환
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

        //이미지 뷰에 변환한 비트맵 사진 표시
        imageView.setImageBitmap(originalBm);
        //비트맵 이미지를 스트링 값으로 전환 나중에 다른 클래스에서 String 전송 예정
        StringImage=BitmapToString(originalBm);
        //getter / setter로 stringimage를 사용 가능하게함
        setoriginalBm(StringImage);


        upload.setVisibility(View.VISIBLE);
        btnCamera.setVisibility(View.INVISIBLE);
        btnGallery.setVisibility(View.INVISIBLE);


        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //업로드 버튼 클릭 시 MapsActivity로 화면 전환
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });
    }
    //originalbm의 값을 호출해 주기 위해 사용한 getter setter
    public static String getoriginalBm() {
        return StringImage;
    }
    public void setoriginalBm(String StringImage) {
        this.StringImage = StringImage;
    }

    //비트맵을 스트링으로 전환하는 코드
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }

    /**
     *  권한 설정
     */
    //권한을 불러오는데 필요한 코드
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;

            }
        };

        //권한을 설정할때 나오는 텍스트 설정
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

}
