package com.example.firebasedatabaseexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadActivity extends AppCompatActivity {

    private Button mUploadBtn;
    private StorageReference mStorage;
    private  static final int GALLERY_INTENT =1;
    private ImageView mImageView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mStorage = FirebaseStorage.getInstance().getReference();
        mUploadBtn =(Button) findViewById(R.id.btnSubir);
        mImageView = (ImageView) findViewById(R.id.SubirImagen);
        mProgressDialog = new ProgressDialog(this);

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent intent = new Intent(Intent.ACTION_PICK);
                                              intent.setType("image/*");
                                              startActivityForResult(intent,GALLERY_INTENT);


                                          }
                                      }


        );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            mProgressDialog.setMessage("Subiendo Imagen");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            Uri uri = data.getData();
            StorageReference filePath = mStorage.child("images").child(uri.getLastPathSegment());


            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    Glide.with(UploadActivity.this)
                            .load(downloadUrl)
                            .into(mImageView);



                    Toast.makeText(UploadActivity.this,"Se subio correctamente la imagen", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}