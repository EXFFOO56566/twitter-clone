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

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import com.google.firebase.database.annotations.NotNull;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.MainActivity;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.Policy;
import com.scappy.twlight.ActivitiesSystems.Terms;

public class VerifyOTPActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    String verificationId;
    EditText editText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        //Firebase
         mAuth = FirebaseAuth.getInstance();

        //Id
        ImageView imageView = findViewById(R.id.imageView);
         editText = findViewById(R.id.editText);
        Button next = findViewById(R.id.button);
         progressBar = findViewById(R.id.pb);
        TextView forgot = findViewById(R.id.forgot);

        //Forgot
        forgot.setOnClickListener(v -> onBackPressed());

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
        imageView.setOnClickListener(v -> onBackPressed());

        //Get no.
        String phone = getIntent().getStringExtra("phone");

        sendVerificationCode(phone);

        //Process
        next.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String code = editText.getText().toString().trim();

            if (code.isEmpty() || code.length() < 6){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter Verification OTP")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                progressBar.setVisibility(View.INVISIBLE);
            }else {
                verifyCode(code);
            }
        });
    }

    private  void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyOTPActivity.this, task -> {
                    if (task.isSuccessful()) {
                        final String phone = getIntent().getStringExtra("phone");
                        Query userQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone").equalTo(phone);
                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount()>0){
                                    Intent intent = new Intent(VerifyOTPActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }else {
                                    Intent intent = new Intent(VerifyOTPActivity.this, PhoneSignUpActivity.class);
                                    intent.putExtra("phone", phone);
                                    startActivity(intent);
                                    finish();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text(databaseError.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });


                    } else {
                        // Sign in failed, display a message and update the UI
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
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
    }
    private void sendVerificationCode(String phonenumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                editText.setText(code);
                verifyCode(code);
                progressBar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            new StyleableToast
                    .Builder(getApplicationContext())
                    .text(e.getMessage())
                    .gravity(0)
                    .textColor(Color.WHITE)
                    .textBold()
                    .length(2000)
                    .solidBackground()
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .show();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

}