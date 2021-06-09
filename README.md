# 목차
## 1. 앱 소개<br>
+ 주제 선정이유<br> 
+ 차별점<br>
## 2. 기능구현
### 2-1 파이어베이스<br>
+ 소개<br>
+ 회원가입<br>
### 2-2 리얼타임 데이터베이스<br>
+ 소개<br>
+ 데이터 저장<br>
+ 데이터 불러오기<br>
### 2-3 구글맵<br>
+ 소개<br>
+ 마커 찍기<br>
+ 마커 클릭<br>
### 2-4 카메라 실행<br>
+ 카메라, 앨범 실행<br>
+ 이미지 불러오기<br>
### 2-5 이미지 저장 및 전송<br>
+ 이미지값 변환<br>
+ 이미지값 데이터 베이스에 저장<br>
### 2-6 환경설정<br>
+ 소개<br>
+ 시스템 설정으로 이동<br>
## 3. 동작영상<br><br><br>
  
# 1. 앱 소개<br>
+ 주제 선정이유<br> 
+ 차별점<br>

# 2. 기능구현<br>
## 2-1 파이어베이스<br>
  + 소개<br>
  + 회원가입<br>

## 2-2 리얼타임 데이터베이스
+ 소개<br>
+ 데이터 저장<br>
+ 데이터 불러오기<br>

## 2-3 구글맵<br>
+ 소개<br>
+ 마커 찍기<br>
+ 마커 클릭<br>

## 2-4 카메라 실행<br>
+ 카메라, 앨범 실행<br><br>
TedPermision()을 이용하여 CameraActivity 실행시 카메라 사용과 저장소 접근에 대한 서용 권한 체크 창을 띄우고 Gallery 버튼과 Camera 버튼 클릭 시 권한이 허락 되어 있다면 각 버튼의 이름에 맞는 함수를 호출하도록 했습니다.
  <pre><code>
      @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image);

        upload = findViewById(R.id.upload);
        btnCamera= findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        upload.setVisibility(View.INVISIBLE);

        tedPermission();

        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermission) goToAlbum();
                else Toast.makeText(view.getContext(), getResources().
                        getString(R.string.permission_2), Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermission)  takePhoto();
                else Toast.makeText(view.getContext(), getResources().
                        getString(R.string.permission_2), Toast.LENGTH_LONG).show();
            }
        });
    }
</code></pre><br>
앨범 버튼 클릭 시 goToAlbum()이라는 함수를 수행하게 되며 함수에서는 이미지의 선택 방법을 확인 할 수 있는 isCamera를 false로 선언해 주고 안드로이드 내에서 제공 하는 앨범의 사진 선택 화면으로 Intent를 전환해 줍니다. 이미지를 선택한 후 선택 분기를 결정하는 인텐트로 전환해 줍니다.
  <pre><code>
  private void goToAlbum() {
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
  </pre></code><br>
카메라 버튼 클릭 시 takephoto() 라는 함수를 수행하게 되며 isCamera를 true로 선언하여 Camera로 이미지를 선택했음을 알려주고 안드로이드 에서 제공하는 사진 촬영 인텐트로 전환합니다. 그 다음 createImageFile() 함수를 수행하여 이미지가 담길 임시 파일인 tempfile 만든 다음 FileProvider를 이용하여 tempfile의 uri값을 얻어오고 선택 분기를 결정하는 인텐트로 전환해 줍니다. 
   <pre><code>
   private void takePhoto() {
        isCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.androidapp.youjigom", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } else {
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }
   </pre></code><br>
onActivityResult를 이용하여 선택 분기에 따른 이벤트를 처리해 줍니다. 카메라 혹은 앨범에서 사진 선택 과정중 활동을 취소했을 경우 Toast메시지를 송출하고 tempfile을 삭제 시켜 줍니다.<br><<Br>
앨범에서 사진을 선택했을 경우는 우선 이미지의 URI를 얻어온 뒤 Cursor를 이용하여 URI 스키마를 Content:// 에서 File://로 변경한 뒤  이미지를 띄우는 Setimage()함수를 선언합니다.<br><br>
카메라에서 찍은 사진을 선택했을 경우에는 SetImage()함수를 선언합니다.
   <pre><code>
   if (requestCode == PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                tempFile = new File(cursor.getString(column_index));
                Log.d(TAG, "tempFile Uri : " + Uri.fromFile(tempFile));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        }
    }
  </pre></code>
createImageFile을 통해서 선택한 이미지가 담길 임시 파일을 생성합니다 파일의 이름은 중복을 피하기 위해서 해당 활동을 시작한 시간으로 지정합니다.
  <pre><code>
  private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "KNLBDC_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/KNLBDC/");
        if (!storageDir.exists()) storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());
        return image;
    }
  </pre></code><br><br>
  
+ 이미지 불러오기<br>
tempfile을 사진을 조정하는 ImageResizeUtils 클래스의 resizeFile 함수를 이용하여 크기를 조정하고 비트맵 형식으로 변환하여 ImageView에 표시해 줍니다. 업로드 버튼 클릭시 이미지를 전송하는 Activity로 인텐트를 전환합니다.
<pre><code>
private void setImage() {
        imageView = findViewById(R.id.imageView);
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

        imageView.setImageBitmap(originalBm);
        StringImage=BitmapToString(originalBm);
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
</pre></code>

## 2-5 이미지 저장 및 전송<br>
+ 이미지값 변환<br>
+ 이미지값 데이터 베이스에 저장<br>

## 2-6 환경설정<br>
+ 소개<br>
+ 시스템 설정으로 이동<br>

# 3. 동작영상<br>
