package com.scappy.twlight.PhoneAuthenticationSystem;

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

import com.hbb20.CountryCodePicker;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.Policy;
import com.scappy.twlight.ActivitiesSystems.Terms;

public class OTPActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_o_t_p_page);

        //Id
        ImageView imageView = findViewById(R.id.imageView);
        EditText editText = findViewById(R.id.editText);
        Button next = findViewById(R.id.next);
        ProgressBar progressBar = findViewById(R.id.pb);

        //Back
        imageView.setOnClickListener(v -> onBackPressed());

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

        //CCP
        CountryCodePicker ccp = findViewById(R.id.ccp);
        String code = ccp.getSelectedCountryCode();
        editText.setText("+"+code);

        //Process
        next.setOnClickListener(v -> {
            String no = editText.getText().toString().trim();
            progressBar.setVisibility(View.VISIBLE);
            if (no.isEmpty()){
                progressBar.setVisibility(View.GONE);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter Your Phone no.")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }else {
                Intent intent = new Intent(OTPActivity.this, VerifyOTPActivity.class);
                intent.putExtra("phone", no);
                startActivity(intent);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }
}