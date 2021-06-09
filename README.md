# 목차
## 1. 앱 소개<br>
+ 주제 선정이유<br> 
+ 차별점<br>
## 2. 기능구현
### 2-1 파이어베이스<br>
+ 소개<br>
+ 로그인<br>
+ 회원가입<br>
+ 메인화면<br>
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

    + 로그인<br>
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
fAuth.signInWithEmailAndPassword(email,password) 코드를 통해 이메일과 패스워드를 인증합니다 <br>

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

<br> 로그인 화면에서는 사용자가 비밀번호를 분실했을때 비밀번호를 다시 세팅 할 수 있는 서비스도 제공합니다 <br>
AlertDialog 기능을 사용해 사용자에 아아디, 즉 이메일 정보를 받아 해당 이메일에 패스워드 리셋 링크를 전송합니다 <br>
이때 전송에 실패 하거나 등록되지 않은 이메일을 작성했으면 Failure 메시지를 확인 할 수 있게 구현했습니다<br>
<pre><code>

       forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });
</code></pre>

마지막으로 로그인 페이지에서 회원가입(register) 로 넘어가기위한 버튼 입니다
<pre><code>
 mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });
</code></pre>

+ 회원가입<br>

<br> 회원가입 페이지에서는 사용자에게 5가지 종류의 데이터를 작성받습니다. 이름, 이메일, 비밀번호, 전화번호, 국적 입니다 <br>
앱은 사용자의 정보를 받아 로그인 인증, 앱 내 서비스 이용에 해당 유저의 데이터를 활용합니다 <br>

<br>아래는 사용자의 데이터를 인식해 파이어베이스에 저장을 하기 위한 코드 입니다

<pre><code>
  Toast.maeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",fullName);
                            user.put("email",email);
                            user.put("phone",phone);
                            user.put("country",country);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
  fDatabase = FirebaseDatabase.getInstance();

                            DatabaseReference reference = fDatabase.getReference();

                            Map<String, Object> childUpdates=new HashMap<>();

                            Map<String, Object> postValues=null;

                            com.androidapp.youjigom.FirebasePost post=new com.androidapp.youjigom.FirebasePost(image, fullName, country,senderName);
                            postValues=post.toMap();

                            setSenderName(fullName);

                            childUpdates.put("/users/"+fullName,postValues);
                            reference.updateChildren(childUpdates);

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


</pre></code>

<br> 로그인 화면에서와 마찬가지로 사용자가 잘못된 정보를 기입할 수도 있기 때문에 잘못된 정보 작성에 대한 정보 또한 제공합니다 <br>
<pre><code>
 mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                final String phone    = mPhone.getText().toString();
                final String country = mCountry.getText().toString().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                if(TextUtils.isEmpty(country)){
                    mCountry.setError("Country information is required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
</pre></code>

<br> 사용자가 정보를 작성 할 때, 국적(country) 값은 정해진 답변에서만 선택 해야 합니다 <br>
주어진 옵션에서만 선택 할 수 있도록 AlertDialog 시스템을 구현헀습니다<br>

<pre><code>
 final int[] selectedItem = {0};

        choose = (Button) findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{
                        "미국",//0
                        "영국",//1
                        "프랑스",//2
                        "남아프리카공화국",//3
                        "이탈리아",//4
                        "사우디아라비아",//5
                        "인도",//6
                        "러시아",//7
                        "멕시코",//8
                        "브라질",//9
                        "아르헨티나",//10
                        "태국",//11
                        "중국",//12
                        "일본",//13
                        "대한민국"};//14
                AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
                dialog.setTitle("Choose your country")
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedItem[0] = which;
                                mCountry.setText(items[selectedItem[0]]);

                            }
                        })
                        .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(Register.this
                                        ,items[selectedItem[0]]
                                        ,Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(Register.this
                                        ,"Canceled"
                                        ,Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.create();
                dialog.show();

            }
        });
</pre></code>

<br> 마지막으로 로그인 화면에 다시 돌아갈 수 있도록 해주는 버튼 입니다 <br>
<pre><code>
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

</pre></code>

+ 메인화면<br>

<br> 메인화면은 사용자가 최종적으로 개인정보 등을 다 입력해 앱 사용을 시작하는 화면입니다
<br> 메인 화면에서는 사용자가 기입한 정보 프로필, 아이디 등등 변경이 가능합니다 <br>
위를 구현하기 위해서는 우선 파이어베이스에서 정보를 불러 올 수 있는 코드들이 필요합니다 <br>
이번 프로젝트에서 활용하지는 않았지만 서버에 저장된 이미지를 불러오는 기능인 picasso 또한 코드로 작성했습니다
<pre><code>
 fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
</pre></code>

<br> 메인 화면에서 가장 중요한 기능중 하나는 이메일 인증입니다. <br>
사용자는 로그인에 사용되는 이메일을 통해 본인이 맞는지 인증 하는 메일을 받고 링크를 실행하여야 합니다 <br>
<pre><code>
if (!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }
</pre></code>

<br> 회원가입에서 기입한 사용자 정보들을 불러오기위한 코드들 입니다 <br>
<pre><code>
 DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    phone.setText(documentSnapshot.getString("phone"));
                    fullName.setText(documentSnapshot.getString("fName"));
                  email.setText(documentSnapshot.getString("email"));
                  country.setText(documentSnapshot.getString("country"));

                } else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
</pre></code>

<br>  패스워드 변경기능입니다 <br>
<br> 패스워드를 이메일 인증을 통해 완전히 바꾸는것과는 다른 패스워드를 변경하는 기능입니다 <br>
<br> 사용자가 패스워드 규격에 맞지 않는 정보를 입력하면 에러 메시지를 보여줍니다 <br>
<pre><code>
 resetPassLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter New Password > 8 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });

                passwordResetDialog.create().show();

            }
        });
       </pre></code>

<br> 마지막으로 메인 액티비티는 다른 화면으로 넘어 갈 수 있도록 하는 각종 버튼들이 존재합니다
<br> 그러한 버튼들을 이용할 수 있게 해주는 코드들입니다

앨범
<pre><code>
 ImgList = findViewById(R.id.btnImgList);
        ImgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),imgList.class));
            }
        });
 </pre></code>
 로그아웃
 <pre><code>
public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
</pre></code>
옵션메뉴
<pre><code>
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
</pre></code>

    
    

  + 회원가입<br>

## 2-2 리얼타임 데이터베이스
+ 소개<br>
Realtime Database는 Firebase에서 제공하는 실시간 데이터베이스로 데이터 저장 및 동기화가 실시간으로 가능한 데이터베이스입니다. 이 앱에서는 회원가입한 사용자의 일부 정보를 저장하고, 전송된 이미지를 스트링 값으로 변환하여 저장하는 곳에 사용했습니다.<br><br>
+ 데이터 저장<br>
1. 회원가입한 사용자의 정보 중 이름과 국가 정보 저장<br>
각 데이터를 저장하기 위해서 FirebasePost 자바 파일을 만들어 그 안에 필요한 변수들과 함수를 작성해줍니다.<br>

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
       
toMap 함수는 Realtime Database에 값을 저장하기 위해 HashMap 형식으로 데이터를 넣어줄 함수입니다.<br> 
처음 회원가입시에는 이미지 전송을 하지 않은 상태이기 때문에 fullName과 country정보만 데이터베이스에 저장되게 됩니다.<br>

<pre><code>
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("image",image);
        result.put("fullName",fullName);
        result.put("country",country);
        result.put("senderName",senderName);

        return result;
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
