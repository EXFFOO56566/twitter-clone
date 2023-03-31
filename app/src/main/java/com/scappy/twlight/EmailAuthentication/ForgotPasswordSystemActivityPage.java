package com.scappy.twlight.EmailAuthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.Policy;
import com.scappy.twlight.ActivitiesSystems.Terms;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class ForgotPasswordSystemActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Progress
        ProgressBar progressBar = findViewById(R.id.pb);

        //Back
        ImageView back = findViewById(R.id.imageView);
        back.setOnClickListener(v -> onBackPressed());

        //EditText
        EditText mEmail = findViewById(R.id.editText);

                        TextView privacy = findViewById(R.id.privacy);
        TextView terms = findViewById(R.id.terms);

        privacy.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Policy.class);
            startActivity(intent);
        });
        terms.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Terms.class);
            startActivity(intent);
        });

        //Continue Button
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            progressBar.setVisibility(View.VISIBLE);
            if (email.isEmpty()){
                progressBar.setVisibility(View.GONE);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter Your Email Address")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();

            }else{

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("Reset link is sent on your email address")
                                .textColor(Color.WHITE)
                                .textBold()
                                .length(2000)
                                .gravity(0)
                                .solidBackground()
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();


                        new Handler().postDelayed(() -> {

                            Intent intent = new Intent(getApplicationContext(), GetEmailContentActivityPage.class);
                            startActivity(intent);
                            finish();

                        }, 2000);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        String msg = Objects.requireNonNull(task.getException()).getMessage();
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text(msg)
                                .textColor(Color.WHITE)
                                .textBold()
                                .length(2000)
                                .gravity(0)
                                .solidBackground()
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();

                    }
                });
            }
        });
    }
}