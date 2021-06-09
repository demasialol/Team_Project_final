# 목차
## 1. 앱 소개<br>
+ 주제 선정이유<br> 
+ 차별점<br>
## 2. 기능구현
### 2-1 파이어베이스<br>
+ 소개<br>
+ 회원가입<br>
+ 로그인<br>
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
  
Firebase 에서 제공하는 서비스를 이용해 사용자를 식별할수 있는 ID / Passwrod 생성과 앱 활용에 필요한
각종 데이터들을 저장하기 위한 기능을 하는 파트 입니다<br> 
사용자는 로그인 / 레지스터 / 메인 화면 3 가지 화면을 활용 합니다 <br>


<br> 먼저 로그인 화면입니다 <br> 사용자는 로그인 화면에서 토스트 메시지를 참고해 규격에 맞지 않는 
ID / PW 작성을 인지할수 있습니다<br>
<pre><code> 
  mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim(); // 사용자 이메일 인식
                String password = mPassword.getText().toString().trim();// 사용자 패스워드 인식

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required."); //이메일 작성칸에 이메일을 적지 않았을시 나오는 메시지
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required."); //패스워드 작성칸에 패스워드를 적지 않았을시 나오는 메시지
                    return;
                }

                if(password.length() < 8){
                    mPassword.setError("Password Must be >= 8 Characters"); //패스워드가 8자리 이상이 아닐시 나오는 메시지
                    return;
                }
    </code></pre> 
    
<br> 아래는 로그인 화면에서 가장 중요한 파이어 베이스에서 정보를 불러오는 코드입니다 <br>


<pre><code> 
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        }else {
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
               </code></pre> 
    
    

  + 회원가입<br>

## 2-2 리얼타임 데이터베이스
+ 소개<br>
Realtime Database는 Firebase에서 제공하는 실시간 데이터베이스로 데이터 저장 및 동기화가 실시간으로 가능한 데이터베이스입니다. 이 앱에서는 회원가입한 사용자의 일부 정보를 저장하고, 전송된 이미지를 스트링 값으로 변환하여 저장하는 곳에 사용했습니다.<br><br>
+ 데이터 저장<br>
1. 회원가입한 사용자의 정보 중 이름과 국가 정보 저장<br>
  <pre><code>
  public String image;
    public String fullName;
    public String country;
    public String senderName;

    public FirebasePost() {
    }

    public FirebasePost(String image, String fullName, String country, String senderName) {
        this.image = image;
        this.fullName = fullName;
        this.country = country;
        this.senderName = senderName;
    }
    </code></pre>
2. 이미지 전송시, 이미지를 스트링 값으로 변환하여 저장<br>
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
</code></pre>
앨범 버튼 클릭 시 goToAlbum()이라는 함수를 수행하게 되며 함수에서는 이미지의 선택 방법을 확인 할 수 있는 isCamera를 false로 선언해 주고 안드로이드 내에서 제공 하는 앨범의 사진 선택 화면으로 Intent를 전환해 줍니다. 이미지를 선택한 후 선택 분기를 결정하는 인텐트로 전환해 줍니다.
  <pre><code>
  private void goToAlbum() {
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
  </pre></code>
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
   </pre></code>
onActivityResult를 이용하여 선택 분기에 따른 이벤트를 처리해 줍니다. 카메라 혹은 앨범에서 사진 선택 과정중 활동을 취소했을 경우 Toast메시지를 송출하고 tempfile을 삭제 시켜 줍니다.<br><br>
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
  </pre></code>
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
+ 이미지값 변환<br><br>
BitmaptoString이라는 함수를 이용해서 비트맵 형식의 파일을 String값으로 전환합니다. 먼저 비트맵 파일을 Byte형식의 행렬에 저장한  그것을 String 값으로 전환해 줍니다.
<pre><code>
public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }
</pre></code>

+ 이미지값 데이터 베이스에 저장<br>

## 2-6 환경설정<br>
+ 소개<br>
+ 시스템 설정으로 이동<br>

# 3. 동작영상<br>
