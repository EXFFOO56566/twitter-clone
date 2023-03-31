package com.scappy.twlight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.ActivitiesSystems.CreateChatContentActivity;
import com.scappy.twlight.ActivitiesSystems.CreatePostContentActivity;
import com.scappy.twlight.ActivitiesSystems.EditProfileSystemActivityPage;
import com.scappy.twlight.ActivitiesSystems.ContentFollowersActivityPage;
import com.scappy.twlight.ActivitiesSystems.ContentFollowingActivityPage;
import com.scappy.twlight.ActivitiesSystems.ViewMyProfileContentActivityPage;
import com.scappy.twlight.ActivitiesSystems.Policy;
import com.scappy.twlight.ActivitiesSystems.SavedPostContentActivityPage;
import com.scappy.twlight.ActivitiesSystems.SettingsSystemActivityPage;

import com.scappy.twlight.AppFragments.MainHomeFragmentUiSystem;
import com.scappy.twlight.AppFragments.MainMessageFragmentUiSystem;
import com.scappy.twlight.AppFragments.MainNotificationFragmentUiSystem;
import com.scappy.twlight.AppFragments.MainSearchFragmentUiSystem;
import com.scappy.twlight.LiveBroadcastSystem.activities.GoLiveBroadcastActivityPage;
import com.scappy.twlight.NotificationSystem.Token;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    ConstraintLayout home,message,notification;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page_appui);

        //CircularImageView
        ImageView live = findViewById(R.id.dp);
        CircleImageView dp4 = findViewById(R.id.dp4);

        FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();
                if (!dp.isEmpty()){
                    Picasso.get().load(dp).placeholder(R.drawable.avatar).into(dp4);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

        live.setOnClickListener(v -> {
            Query query = FirebaseDatabase.getInstance().getReference().child("Live").orderByChild("userid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        snapshot.getRef().removeValue();
                        Intent intent = new Intent(getApplicationContext(), GoLiveBroadcastActivityPage.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(getApplicationContext(), GoLiveBroadcastActivityPage.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        dp4.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
            startActivity(intent);
        });

        ImageView addChat = findViewById(R.id.addChat);
        addChat.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CreateChatContentActivity.class);
            startActivity(intent);
        });

        Button tweet = findViewById(R.id.tweet);
        tweet.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CreatePostContentActivity.class);
            startActivity(intent);
        });

        home = findViewById(R.id.constraintLayout99);
        message = findViewById(R.id.constraintLayout2);
        notification = findViewById(R.id.constraintLayout3);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        ImageView menu = findViewById(R.id.menu);
        ImageView menu2 = findViewById(R.id.menu3);
        ImageView menu3 = findViewById(R.id.menu4);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_ui);
        CircleImageView profileDp = headerView.findViewById(R.id.profileDp);
        TextView name = headerView.findViewById(R.id.name);
        TextView username = headerView.findViewById(R.id.username);
        TextView followersNo = headerView.findViewById(R.id.followersNo);
        TextView followingNo = headerView.findViewById(R.id.followingNo);
        TextView followers = headerView.findViewById(R.id.followers);
        TextView following = headerView.findViewById(R.id.following);
        ImageView verified = headerView.findViewById(R.id.verified);

        //ProfileDetails
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mDp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();
                String mUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String mName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String mVerified = Objects.requireNonNull(snapshot.child("verified").getValue()).toString();
                username.setText(mUsername);
                name.setText(mName);
                if (!mDp.isEmpty()){
                    Picasso.get().load(mDp).placeholder(R.drawable.avatar).into(profileDp);
                }
                if (mVerified.isEmpty()){
                    verified.setVisibility(View.GONE);
                }else {
                    verified.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingNo.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileDp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        name.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        username.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        followersNo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowersActivityPage.class);
            intent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        followingNo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowingActivityPage.class);
            intent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        followers.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowersActivityPage.class);
            intent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        following.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowingActivityPage.class);
            intent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        menu2.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        menu3.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id){
                case R.id.profile:
                    Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
                    startActivity(intent);
                    break;
                case R.id.edit:
                    Intent intent2 = new Intent(getApplicationContext(), EditProfileSystemActivityPage.class);
                    startActivity(intent2);
                    break;
                case R.id.live:
                    Query query = FirebaseDatabase.getInstance().getReference().child("Live").orderByChild("userid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                snapshot.getRef().removeValue();
                                Intent intent = new Intent(getApplicationContext(), GoLiveBroadcastActivityPage.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(getApplicationContext(), GoLiveBroadcastActivityPage.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    break;
                case R.id.save:
                    Intent intent3 = new Intent(getApplicationContext(), SavedPostContentActivityPage.class);
                    startActivity(intent3);
                    break;
                case R.id.settings:
                    Intent intent5 = new Intent(getApplicationContext(), SettingsSystemActivityPage.class);
                    startActivity(intent5);
                    break;

                case R.id.privacy:
                    Intent intent8 = new Intent(getApplicationContext(), Policy.class);
                    startActivity(intent8);
                    break;
            }
            return true;

        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationSelected);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MainHomeFragmentUiSystem()).commit();

        FloatingActionButton createPost = findViewById(R.id.createPost);
        createPost.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CreatePostContentActivity.class);
            startActivity(intent);
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationSelected =
            item -> {
                switch (item.getItemId()){
                    case R.id.home:
                        selectedFragment = new MainHomeFragmentUiSystem();
                        home.setVisibility(View.VISIBLE);
                        message.setVisibility(View.GONE);
                        notification.setVisibility(View.GONE);
                        break;
                    case R.id.search:
                        selectedFragment = new MainSearchFragmentUiSystem();
                        home.setVisibility(View.GONE);
                        message.setVisibility(View.GONE);
                        notification.setVisibility(View.GONE);
                        break;
                    case R.id.notification:
                        selectedFragment = new MainNotificationFragmentUiSystem();
                        home.setVisibility(View.GONE);
                        message.setVisibility(View.GONE);
                        notification.setVisibility(View.VISIBLE);
                        break;
                    case R.id.messages:
                        selectedFragment = new MainMessageFragmentUiSystem();
                        home.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        notification.setVisibility(View.GONE);
                        break;
                }
                if (selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            };

    private void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(mToken);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("Ban").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Intent intent = new Intent(MainActivity.this, ActiveBanSystemActivityPage.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}