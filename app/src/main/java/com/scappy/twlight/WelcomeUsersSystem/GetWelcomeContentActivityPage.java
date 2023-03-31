package com.scappy.twlight.WelcomeUsersSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.scappy.twlight.R;
import com.scappy.twlight.EmailAuthentication.GetEmailContentActivityPage;
import com.scappy.twlight.PhoneAuthenticationSystem.OTPActivity;

public class GetWelcomeContentActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        //Id
        Button email = findViewById(R.id.email);
        Button phone = findViewById(R.id.phone);

        //OnClick
        email.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GetEmailContentActivityPage.class);
            startActivity(intent);
        });

        phone.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
            startActivity(intent);
        });

    }
}