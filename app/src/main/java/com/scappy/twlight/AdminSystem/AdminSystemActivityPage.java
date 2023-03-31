package com.scappy.twlight.AdminSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.scappy.twlight.R;

public class AdminSystemActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page__appui);
        TextView online_text = findViewById(R.id.online_text);
        TextView users_text = findViewById(R.id.users_text);
        TextView posts_text = findViewById(R.id.posts_text);
        TextView msg_text = findViewById(R.id.msg_text);

        ImageView menu3 = findViewById(R.id.menu3);
        menu3.setOnClickListener(v -> onBackPressed());

        //Numbers
        Query queryOnline = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("status").equalTo("online");
        queryOnline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    online_text.setText(String.valueOf(ds.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users_text.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts_text.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msg_text.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Click
        ConstraintLayout usersManage = findViewById(R.id.usersManage);
        usersManage.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), GetUserListContentActivityPage.class);
            startActivity(intent4);
        });

        ConstraintLayout postsManage = findViewById(R.id.postsManage);
        postsManage.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), GetContentPostListActivityPage.class);
            startActivity(intent4);
        });

        ConstraintLayout verificationManage = findViewById(R.id.verificationManage);
        verificationManage.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), MangePendingVerificationActivity.class);
            startActivity(intent4);
        });

        ConstraintLayout reportedUsers = findViewById(R.id.reportedUsers);
        reportedUsers.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), GetUserReportListContentActivityPage.class);
            startActivity(intent4);
        });

        ConstraintLayout reportedPosts = findViewById(R.id.reportedPosts);
        reportedPosts.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), GetPostReportListConentActivityPage.class);
            startActivity(intent4);
        });


    }
}