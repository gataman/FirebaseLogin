package com.gurcanataman.denemeproje;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    EditText commentText;
    ImageView imageView;
    Uri selected;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        commentText =findViewById(R.id.commentText);
        imageView =(ImageView) findViewById(R.id.resim);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        myAuth = FirebaseAuth.getInstance();

        // Resim, dosya, media için firebasete referans oluşturuldu.
        mStorageRef = FirebaseStorage.getInstance().getReference();


    }

    public void upload(View view){


        // Random bir UUID adı oluşturuluyor...
        UUID uuıdImage = UUID.randomUUID();
        String imageName = "images/"+uuıdImage+".jpg";


        StorageReference storageReference = mStorageRef.child(imageName);
        storageReference.putFile(selected)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadURL = taskSnapshot.getDownloadUrl().toString();

                        // Kullanıcı epostaya erişelim

                        FirebaseUser user = myAuth.getCurrentUser();
                        String userEmail = user.getEmail().toString();
                        String userComment = commentText.getText().toString();
                        String uuidString = UUID.randomUUID().toString();

                        myRef.child("Posts").child(uuidString).child("user_email").setValue(userEmail);
                        myRef.child("Posts").child(uuidString).child("user_comment").setValue(userComment);
                        myRef.child("Posts").child(uuidString).child("download_url").setValue(downloadURL);

                        Toast.makeText(getApplicationContext(), "Gönderildi.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                        startActivity(intent);

                    }

                    })

                    .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e != null){
                            Toast.makeText(getApplicationContext(), "Yükleme başarısız:"+e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });



    }

    public void chooseImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);




/*        PackageManager mPackageManager = getApplicationContext().getPackageManager();
        int hasPermStorage = mPackageManager.checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, getApplicationContext().getPackageName());


        if (hasPermStorage != PackageManager.PERMISSION_GRANTED) {
            // do stuff
            Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG).show();


        } else if (hasPermStorage == PackageManager.PERMISSION_GRANTED) {



        }else{
            Toast.makeText(getApplicationContext(), "Has permission", Toast.LENGTH_LONG).show();

        }*/



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1 && resultCode == RESULT_OK && data!=null){
            selected = data.getData();
            try {
                Bitmap bitmap =MediaStore.Images.Media.getBitmap(this.getContentResolver(),selected);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Hatatattttt", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
