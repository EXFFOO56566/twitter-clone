package com.scappy.twlight.ActivitiesSystems;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;

import java.util.Objects;

public class EditPasswordSystemActivityPage extends AppCompatActivity {

    EditText pass,name;
    ImageView imageView3;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password_page);
        progressBar = findViewById(R.id.pb);
        pass = findViewById(R.id.username);
        name = findViewById(R.id.editText);
        mAuth = FirebaseAuth.getInstance();
        imageView3 = findViewById(R.id.imageView);
        imageView3.setOnClickListener(v -> onBackPressed());
        Button button = findViewById(R.id.signUp);
        button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String oldP = name.getText().toString().trim();
            String newP = pass.getText().toString().trim();
            if (TextUtils.isEmpty(oldP)){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter your current password")
                        .textColor(Color.WHITE)
                        .gravity(0)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                progressBar.setVisibility(View.GONE);
                return;
            }else if (TextUtils.isEmpty(newP)){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter your new password")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                progressBar.setVisibility(View.GONE);
                return;
            }else if (newP.length()<6){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Password should have minimum 6 characters")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            updatePassword(oldP,newP);
        });
    }

    private void updatePassword(String oldP, String newP) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), oldP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> {
                    firebaseUser.updatePassword(newP);
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Password updated")
                            .textColor(Color.WHITE)
                            .textBold()
                            .gravity(0)
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                    progressBar.setVisibility(View.GONE);
                });
    }
}