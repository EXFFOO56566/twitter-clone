package com.scappy.twlight.ActivitiesSystems;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;

import java.util.HashMap;
import java.util.Objects;

public class VerificationSystemActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_page);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());
        Button submit = findViewById(R.id.submit);
        EditText rTwo = findViewById(R.id.rTwo);
        EditText rOne = findViewById(R.id.rOne);
        EditText id = findViewById(R.id.id);
        EditText type = findViewById(R.id.type);
        submit.setOnClickListener(v -> {
            String refOne = rOne.getText().toString();
            String refTwo = rTwo.getText().toString();
            String gId = id.getText().toString();
            String Type = type.getText().toString();
            if (refOne.isEmpty() && refTwo.isEmpty() && gId.isEmpty() && Type.isEmpty()){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter every details")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }else {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                hashMap.put("type", Type);
                hashMap.put("gId",gId);
                hashMap.put("refOne",refOne);
                hashMap.put("refTwo",refTwo);

                FirebaseDatabase.getInstance().getReference().child("Verification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(hashMap).addOnSuccessListener(aVoid -> {
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Request sent")
                            .textColor(Color.WHITE)
                            .textBold()
                            .gravity(0)
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                    rOne.setText("");
                    rTwo.setText("");
                    type.setText("");
                    id.setText("");
                });

            }
        });
    }
}