package com.androidapp.youjigom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class imgList extends AppCompatActivity {
    FirebaseDatabase fDatabase;
    CustomAdapter adapter;
    String user;
    String image;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_list);

        ListView listView = findViewById(R.id.listView); //받은 사진 목록을 보여주는 용도로 listView를 선언해줍니다.
        ImageView imgMsg = findViewById(R.id.imgMsg);

        ArrayList<String> items = new ArrayList<>(); //사진을 보낸 사람의 이름을 저장할 items라는 ArrayList를 만들어줍니다.

        adapter = new CustomAdapter(this, 0, items);

        fDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = fDatabase.getReference();

        reference.child("users/abc").addValueEventListener(new ValueEventListener() {
            //지정된 경로에 이미지가 저장되면 이벤트리스너가 실행됩니다.
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals("fullName")){
                        //로그인한 사용자의 이름을 뽑아 user 변수에 저장 후, items에 추가해줍니다.
                        user = dataSnapshot.getValue().toString();
                        items.add(user);
                        Log.d("TAG","items: "+user);
                        //listView에 adapter를 연결해줍니다.
                        listView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        //listView를 클릭하면 일어나는 이벤트 처리입니다.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String data = (String)adapterView.getItemAtPosition(position);
                Log.d("TAG","text: "+data);
                setContentView(R.layout.image);

                TextView txtNameGet = findViewById(R.id.txtNameGet);
                ImageView imgGet = findViewById(R.id.imgGet);

                reference.child("users/abc").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.getKey().equals("image")){
                                //스트링 값으로 저장된 이미지를 가져와 image변수에 저장합니다.
                                image = dataSnapshot.getValue().toString();

                                byte[] b = Base64.decode(image, Base64.DEFAULT);

                                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                                Log.d("TAG","bitmap: "+bitmap);
                                //image변수에 저장된 스트링 값을 바이트 배열로 바꾸고 비트맵으로 변환하여 이미지뷰에 띄워줍니다.
                                imgGet.setImageBitmap(bitmap);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
                //받는 사람이름을 텍스트뷰에 띄워줍니다.
                txtNameGet.setText(user);
            }
        });
    }

    public class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects){
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;
            if (v == null){
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.item, null);
            }

            TextView txtName = (TextView)v.findViewById(R.id.txtName);

            if (user.equals(items.get(position))) {
                txtName.setText(items.get(position));
            }

            return v;
        }
    }
}