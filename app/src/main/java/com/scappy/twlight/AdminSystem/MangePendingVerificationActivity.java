package com.scappy.twlight.AdminSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.adapter.AdapterForVerificationUsersSystem;
import com.scappy.twlight.SystemModels.SystemModelForVerification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MangePendingVerificationActivity extends AppCompatActivity {

    //User
    AdapterForVerificationUsersSystem adapterUsers;
    List<SystemModelForVerification> userList;


    ProgressBar progressBar;
    RecyclerView trendingRv;

    TextView found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verificattion_page);
        ImageView imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());
        EditText editText = findViewById(R.id.editText);
        trendingRv = findViewById(R.id.trendingRv);
        progressBar = findViewById(R.id.pb);
        progressBar.setVisibility(View.VISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    filterUser(s.toString());
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    getAllUsers();
                    progressBar.setVisibility(View.VISIBLE);
                }

            }
        });

         found = findViewById(R.id.found);


        //User
        userList = new ArrayList<>();
        getAllUsers();
    }


    private void filterUser(String query) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Verification");
       databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    SystemModelForVerification modelUser = ds.getValue(SystemModelForVerification.class);
                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        if (modelUser.getType().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getRefOne().toLowerCase().contains(query.toLowerCase()) ||    modelUser.getRefTwo().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    adapterUsers = new AdapterForVerificationUsersSystem(getApplicationContext(), userList);
                    adapterUsers.notifyDataSetChanged();
                    trendingRv.setAdapter(adapterUsers);
                }
                if (dataSnapshot.getChildrenCount() == 0){
                    found.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(databaseError.getMessage())
                        .textColor(Color.WHITE)
                        .gravity(0)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });

    }


    private void getAllUsers() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        trendingRv.setLayoutManager(layoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Verification");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    SystemModelForVerification modelUser = ds.getValue(SystemModelForVerification.class);
                    userList.add(modelUser);
                    progressBar.setVisibility(View.GONE);
                    adapterUsers = new AdapterForVerificationUsersSystem(getApplicationContext(), userList);
                    trendingRv.setAdapter(adapterUsers);
                    adapterUsers.notifyDataSetChanged();
                }
                if (dataSnapshot.getChildrenCount() == 0){
                    found.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
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
            }
        });
    }
}