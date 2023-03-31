package com.scappy.twlight.WelcomeUsersSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.scappy.twlight.GetCheckContentSystemActivityPage;
import com.scappy.twlight.R;

@SuppressWarnings("deprecation")
public class StartSplashScreenContentActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_page);

        //Timer to Intent
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), GetCheckContentSystemActivityPage.class);
            startActivity(intent);
            finish();
        },3000);

    }
}