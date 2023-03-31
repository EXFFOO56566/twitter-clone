package com.scappy.twlight.EmailAuthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.MainActivity;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.Policy;
import com.scappy.twlight.ActivitiesSystems.Terms;

import java.util.Objects;

public class GetPasswordContentActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_page);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Progress
        ProgressBar progressBar = findViewById(R.id.pb);

        //Forgot
        TextView forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(v -> {
            Intent intent = new Intent(GetPasswordContentActivityPage.this, ForgotPasswordSystemActivityPage.class);
            startActivity(intent);
        });

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

        //Back
        ImageView back = findViewById(R.id.imageView);
        back.setOnClickListener(v -> onBackPressed());

        //Get Email
        String email = getIntent().getStringExtra("email").trim();

        //text
        TextView textView = findViewById(R.id.textView);
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){

                    String name = Objects.requireNonNull(ds.child("name").getValue()).toString().trim();
                    textView.setText("Hi "+name);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();

            }
        });

        //Button & EditText
        EditText password = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            String mPassword = password.getText().toString().trim();
            progressBar.setVisibility(View.VISIBLE);
            if (mPassword.isEmpty()){
                progressBar.setVisibility(View.GONE);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter Your Password")
                        .gravity(0)
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();

            }else {

                mAuth.signInWithEmailAndPassword(email,mPassword).addOnCompleteListener(GetPasswordContentActivityPage.this, task -> {

                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(GetPasswordContentActivityPage.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }else {
                        progressBar.setVisibility(View.GONE);
                        String msg = Objects.requireNonNull(task.getException()).getMessage();
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text(msg)
                                .textColor(Color.WHITE)
                                .textBold()
                                .gravity(0)
                                .length(2000)
                                .solidBackground()
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();

                    }
                });

            }
        });

    }
}