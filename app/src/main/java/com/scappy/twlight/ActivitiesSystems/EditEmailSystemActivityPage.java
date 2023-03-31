package com.scappy.twlight.ActivitiesSystems;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;

import java.util.HashMap;
import java.util.Objects;

public class EditEmailSystemActivityPage extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email_page__appui);

         mAuth = FirebaseAuth.getInstance();

        //Progress
         progressBar = findViewById(R.id.pb);

        //Back
        ImageView back = findViewById(R.id.imageView);
        back.setOnClickListener(v -> onBackPressed());

        //SignUp
        EditText name = findViewById(R.id.editText);
        EditText username = findViewById(R.id.username);

        Button button = findViewById(R.id.signUp);

        button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String newE = name.getText().toString().trim();
            String newP = username.getText().toString().trim();
            if (TextUtils.isEmpty(newE)){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter your new Email")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(newP)){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter your password")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(newE);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount()>0){

                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("Email already exist")
                                .textColor(Color.WHITE)
                                .textBold()
                                .length(2000)
                                .solidBackground()
                                .gravity(0)
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    updateEmail(newE,newP);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private void updateEmail(String newE, String newP) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), newP);
        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(aVoid -> {
            firebaseUser.updateEmail(newE);
            FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("email", newE);
                    progressBar.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

}