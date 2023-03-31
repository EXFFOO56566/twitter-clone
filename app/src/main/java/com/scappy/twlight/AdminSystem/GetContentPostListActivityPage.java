package com.scappy.twlight.AdminSystem;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.adapter.AdapterForPostsSystem;
import com.scappy.twlight.SystemModels.SystemModelForPosts;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetContentPostListActivityPage extends AppCompatActivity {

    //Post
    AdapterForPostsSystem adapterPost;
    List<SystemModelForPosts> postList;

    RecyclerView trendingRv;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrentPage = 1;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist_page);
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
                    filterPost(s.toString());
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    getAllTrend();
                    progressBar.setVisibility(View.VISIBLE);
                }

            }
        });

        trendingRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrentPage++;
                    getAllTrend();
                }
            }
        });
        //Post
        postList = new ArrayList<>();
        getAllTrend();



    }
    private void filterPost(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    SystemModelForPosts systemModelForPosts = ds.getValue(SystemModelForPosts.class);
                    if (Objects.requireNonNull(systemModelForPosts).getText().toLowerCase().contains(query.toLowerCase()) || systemModelForPosts.getType().contains(query.toLowerCase())){
                        postList.add(systemModelForPosts);
                        progressBar.setVisibility(View.GONE);
                    }
                    adapterPost = new AdapterForPostsSystem(getApplicationContext(), postList);
                    trendingRv.setAdapter(adapterPost);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getAllTrend() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        trendingRv.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SystemModelForPosts systemModelForPosts = ds.getValue(SystemModelForPosts.class);
                    postList.add(systemModelForPosts);
                    progressBar.setVisibility(View.GONE);
                }
                adapterPost = new AdapterForPostsSystem(getApplicationContext(), postList);
                trendingRv.setAdapter(adapterPost);
                adapterPost.notifyDataSetChanged();
                layoutManager.scrollToPosition(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(databaseError.getMessage())
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


}