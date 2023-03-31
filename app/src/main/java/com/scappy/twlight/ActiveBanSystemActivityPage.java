package com.scappy.twlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.scappy.twlight.WelcomeUsersSystem.GetWelcomeContentActivityPage;

public class ActiveBanSystemActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_page_appui);
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent4 = new Intent(getApplicationContext(), GetWelcomeContentActivityPage.class);
            startActivity(intent4);
            finish();
        });
    }
}