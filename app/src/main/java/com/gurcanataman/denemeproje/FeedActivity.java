package com.gurcanataman.denemeproje;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedActivity extends AppCompatActivity {

    ArrayList<String> userEmailFromFB;
    ArrayList<String> userCommentFromFB;
    ArrayList<String> userImageUrlFromFB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    PostClass adapter;
    ListView listView;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_post,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_post) {
            Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        userEmailFromFB = new ArrayList<String>();
        userCommentFromFB = new ArrayList<String>();
        userImageUrlFromFB = new ArrayList<String>();



        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        adapter = new PostClass(userEmailFromFB,userImageUrlFromFB,userCommentFromFB,this);
        listView = findViewById(R.id.gelenViews);

        listView.setAdapter(adapter);

        getDataFromFirebas();
    }

    protected void getDataFromFirebas(){
        DatabaseReference newRef = firebaseDatabase.getReference("Posts");
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    userEmailFromFB.add(hashMap.get("user_email"));
                    userCommentFromFB.add(hashMap.get("user_comment"));
                    userImageUrlFromFB.add((hashMap.get("download_url")));
                    adapter.notifyDataSetChanged();

                    Toast.makeText(FeedActivity.this, hashMap.get("user_email").toString(), Toast.LENGTH_SHORT).show();

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
