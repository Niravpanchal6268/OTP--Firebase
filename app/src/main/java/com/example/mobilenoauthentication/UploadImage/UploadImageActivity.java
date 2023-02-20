package com.example.mobilenoauthentication.UploadImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mobilenoauthentication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadImageActivity extends AppCompatActivity {

    ViewPager viewPager;
    Button selectimgBTN,uploadBTN;
    private final  int Request_Permission_Code=35;
    private final  int Pick_Image_Code=39;
    private ArrayList<Uri> imagesUri;
    private ProgressDialog progressDialog;
    EditText nameinput;
    String name;
    private  int  count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        viewPager=findViewById(R.id.viewpager_id);
        selectimgBTN=findViewById(R.id.select_image_btn);
        nameinput=findViewById(R.id.name_id);
        progressDialog=new ProgressDialog(this);
        imagesUri=new ArrayList<>();

        selectimgBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name= nameinput.getText().toString();
                CheckUserPermission();
            }
        });

        uploadBTN=findViewById(R.id.upload_btn_id);
        uploadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressImages();
            }
        });

    }

    private void CheckUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Request_Permission_Code);
        }
        else
        {
            pickimage();
        }


    }

    private void pickimage() {
        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,Pick_Image_Code);
        if (imagesUri!=null)
        {
            imagesUri.clear();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==Request_Permission_Code)
        {
            if (grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                pickimage();
            }
            else
            {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Pick_Image_Code && resultCode==RESULT_OK)
        {
            if (data!=null)
            {
                if (data.getClipData()!=null)
                {
                    int count=data.getClipData().getItemCount();
                    for (int i=0;i<count;i++)
                    {
                        imagesUri.add(data.getClipData().getItemAt(i).getUri());
                    }
                }
                else {
                    imagesUri.add(data.getData());
                }
                setAdapter();
            }

        }
    }

    private void setAdapter() {
        ImageAdapterClass imageAdapterClass=new ImageAdapterClass(this,imagesUri);
        viewPager.setAdapter(imageAdapterClass);

    }

    private void compressImages()
    {
        progressDialog.setMessage("Images upload");
        progressDialog.show();
        for (int i=0;i<imagesUri.size();i++)
        {
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),imagesUri.get(i));
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,60,stream);
                byte[] bytes =stream.toByteArray();
                upoadImage(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }
    private  void upoadImage(byte[] bytes)
    {
        StorageReference storageReference= FirebaseStorage.getInstance().getReference()
                .child("Images")
                .child("Images"+System.currentTimeMillis()+".jpg");


        storageReference.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uploadtofirebase(String.valueOf(uri),"name");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(UploadImageActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadImageActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void Uploadtofirebase(String uri,String name){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance("https://mobile-no-authentication-f9c21-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("images_uri");

        HashMap hashMap=new HashMap();
        hashMap.put("imageuri",uri);
        hashMap.put("name",name);
         String key =databaseReference.push().getKey();
         databaseReference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void unused) {
                 count +=1;//
                 System.out.println(count);
                 if (count==imagesUri.size())
                 {
                     progressDialog.dismiss();
                     Toast.makeText(UploadImageActivity.this, "images upload", Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {

                 progressDialog.dismiss();
             }
         });


    }

}