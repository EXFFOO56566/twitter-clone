package com.scappy.twlight.PhoneAuthenticationSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.MainActivity;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.Policy;
import com.scappy.twlight.ActivitiesSystems.Terms;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("ALL")
public class PhoneSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sign_up_page);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //SignUp
        EditText name = findViewById(R.id.editText);
        EditText username = findViewById(R.id.username);
        Button button = findViewById(R.id.next);

        //Progress
        ProgressBar progressBar = findViewById(R.id.pb);

        //Back
        ImageView back = findViewById(R.id.imageView);
        back.setOnClickListener(v -> onBackPressed());

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

        //Get phone no.
        String phone = getIntent().getStringExtra("phone").trim();

        //Process
        button.setOnClickListener(v -> {
            String mName = name.getText().toString().trim();
            String mUsername = username.getText().toString().trim();
            progressBar.setVisibility(View.VISIBLE);
            if (mName.isEmpty()){
                progressBar.setVisibility(View.GONE);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .gravity(0)
                        .text("Enter Your Name")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();

            }else if (mUsername.isEmpty()){
                progressBar.setVisibility(View.GONE);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter Your Username")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();

            }else{


                //Check if username exist
                FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(mUsername);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getChildrenCount()>0){
                                        progressBar.setVisibility(View.GONE);
                                        new StyleableToast
                                                .Builder(getApplicationContext())
                                                .text("Username already exist, try another username")
                                                .textColor(Color.WHITE)
                                                .textBold()
                                                .length(2000)
                                                .solidBackground()
                                                .gravity(0)
                                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                .show();

                                    }else {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        String userId = Objects.requireNonNull(firebaseUser).getUid();

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("id", userId);
                                        hashMap.put("name", mName);
                                        hashMap.put("email", "");
                                        hashMap.put("username", mUsername);
                                        hashMap.put("bio", "");
                                        hashMap.put("verified","");
                                        hashMap.put("location","");
                                        hashMap.put("status","");
                                        hashMap.put("phone",phone);
                                        hashMap.put("typingTo","noOne");
                                        hashMap.put("link","");
                                        hashMap.put("cover","");
                                        hashMap.put("photo", "");

                                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {

                                            if (task1.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(PhoneSignUpActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();

                                            }

                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressBar.setVisibility(View.GONE);
                                    new StyleableToast
                                            .Builder(getApplicationContext())
                                            .text(error.getMessage())
                                            .textColor(Color.WHITE)
                                            .textBold()
                                            .gravity(0)
                                            .length(2000)
                                            .solidBackground()
                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                            .show();
                                }
                            });
                        }else {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userId = Objects.requireNonNull(firebaseUser).getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("name", mName);
                            hashMap.put("email", "");
                            hashMap.put("username", mUsername);
                            hashMap.put("bio", "");
                            hashMap.put("verified","");
                            hashMap.put("location","");
                            hashMap.put("status","");
                            hashMap.put("phone",phone);
                            hashMap.put("typingTo","noOne");
                            hashMap.put("link","");
                            hashMap.put("cover","");
                            hashMap.put("photo", "");

                            reference.setValue(hashMap).addOnCompleteListener(task1 -> {

                                if (task1.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(PhoneSignUpActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                }

                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text(error.getMessage())
                                .gravity(0)
                                .textColor(Color.WHITE)
                                .textBold()
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