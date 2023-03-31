package com.scappy.twlight.EmailAuthentication;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.Policy;
import com.scappy.twlight.ActivitiesSystems.Terms;

public class GetEmailContentActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_page);

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
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();

            }else {

                //Check if email exist
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Query query = ref.orderByChild("email").equalTo(email);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){

                                        progressBar.setVisibility(View.GONE);
                                        //Email exist
                                        Intent intent = new Intent(getApplicationContext(), GetPasswordContentActivityPage.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);

                                    }else {

                                        progressBar.setVisibility(View.GONE);
                                        //Email not exist
                                        Intent intent = new Intent(getApplicationContext(), SignUpContentSystemActivityPage.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);

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
                                            .length(2000)
                                            .solidBackground()
                                            .gravity(0)
                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                            .show();
                                }
                            });
                        }else {

                            progressBar.setVisibility(View.GONE);
                            //Email not exist
                            Intent intent = new Intent(getApplicationContext(), SignUpContentSystemActivityPage.class);
                            intent.putExtra("email", email);
                            startActivity(intent);

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