package com.scappy.twlight.ActivitiesSystems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class ViewMyProfileContentActivityPage extends AppCompatActivity {

    ProgressBar pb;

    String myId;
    FirebaseAuth mAuth;

    RecyclerView recyclerView;
    List<SystemModelForPosts> postList;
    AdapterForPostsSystem adapterPost;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrentPage = 1;

    TextView found_txt;
    ImageView found;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        found = findViewById(R.id.found);
        found_txt = findViewById(R.id.found_txt);
        //UserId
        mAuth = FirebaseAuth.getInstance();
        myId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        ImageView settings = findViewById(R.id.settings);
        settings.setOnClickListener(v -> {
            Intent intent3 = new Intent(getApplicationContext(), SettingsSystemActivityPage.class);
            startActivity(intent3);
        });
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        TextView followersNo = findViewById(R.id.followersNo);
        TextView followingNo = findViewById(R.id.followingNo);
        TextView followers = findViewById(R.id.followers);
        TextView following = findViewById(R.id.following);

        followersNo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowersActivityPage.class);
            intent.putExtra("id", myId);
            startActivity(intent);
        });
        followingNo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowingActivityPage.class);
            intent.putExtra("id", myId);
            startActivity(intent);
        });
        followers.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowersActivityPage.class);
            intent.putExtra("id", myId);
            startActivity(intent);
        });
        following.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowingActivityPage.class);
            intent.putExtra("id", myId);
            startActivity(intent);
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersNo.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingNo.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Id
        TextView name = findViewById(R.id.name);
        TextView username = findViewById(R.id.username);
        TextView bio = findViewById(R.id.bio);
        TextView location = findViewById(R.id.location);
        TextView link = findViewById(R.id.link);
        CircleImageView dp = findViewById(R.id.circleImageView);
        ImageView verified = findViewById(R.id.verified);
        ImageView cover = findViewById(R.id.imageView2);
        Button edit = findViewById(R.id.button3);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.post);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrentPage++;
                    loadPost();
                }
            }
        });

        postList= new ArrayList<>();
        loadPost();


        edit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditProfileSystemActivityPage.class);
            startActivity(intent);
        });

        //Profile Details
        FirebaseDatabase.getInstance().getReference().child("Users").child(myId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pb.setVisibility(View.GONE);
                String mName = snapshot.child("name").getValue().toString();
                String mUsername = snapshot.child("username").getValue().toString();
                String mBio = snapshot.child("bio").getValue().toString();
                String mLocation = snapshot.child("location").getValue().toString();
                String mLink = snapshot.child("link").getValue().toString();
                String mDp = snapshot.child("photo").getValue().toString();
                String mCover = snapshot.child("cover").getValue().toString();
                String mVerified = snapshot.child("verified").getValue().toString();

                //Set Text
                name.setText(mName);
                username.setText(mUsername);
                bio.setText(mBio);
                location.setText(mLocation);
                link.setText(mLink);

                if (!mDp.isEmpty()){
                    Picasso.get().load(mDp).placeholder(R.drawable.avatar).into(dp);
                }

                if (mVerified.isEmpty()){
                    verified.setVisibility(View.GONE);
                }else {
                    verified.setVisibility(View.VISIBLE);
                }

                if (mCover.isEmpty()){
                    Picasso.get().load(R.drawable.cover).into(cover);
                }else {
                    Picasso.get().load(mCover).placeholder(R.drawable.cover).into(cover);
                }

                //HideShow

                String bio_l = bio.getText().toString().trim();

                if(bio_l.length() > 0)
                {
                    bio.setVisibility(View.VISIBLE);

                }
                else
                {
                    bio.setVisibility(View.GONE);
                }

                String location_l = location.getText().toString().trim();

                if(location_l.length() > 0)
                {
                    location.setVisibility(View.VISIBLE);

                }
                else
                {
                    location.setVisibility(View.GONE);
                }

                String link_l = link.getText().toString().trim();

                if(link_l.length() > 0)
                {
                    link.setVisibility(View.VISIBLE);

                }
                else
                {
                    link.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

    private void loadPost() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(myId).limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    SystemModelForPosts systemModelForPosts = ds.getValue(SystemModelForPosts.class);
                    postList.add(systemModelForPosts);
                    adapterPost = new AdapterForPostsSystem(getApplicationContext(), postList);
                    recyclerView.setAdapter(adapterPost);
                    layoutManager.scrollToPosition(0);
                }
                if (dataSnapshot.getChildrenCount() == 0){
                    found.setVisibility(View.VISIBLE);
                    found_txt.setVisibility(View.VISIBLE);
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