package com.scappy.twlight.ActivitiesSystems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.adapter.AdapterForPostsSystem;
import com.scappy.twlight.SystemModels.SystemModelForPosts;
import com.scappy.twlight.SystemModels.SystemModelForUsers;
import com.scappy.twlight.NotificationSystem.Data;
import com.scappy.twlight.NotificationSystem.Sender;
import com.scappy.twlight.NotificationSystem.Token;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class ContentUserProfileActivityPage extends AppCompatActivity {

    ProgressBar pb;
    String hisId;
    Button edit;
    Button unFollow;

    RecyclerView post;
    List<SystemModelForPosts> postList;
    AdapterForPostsSystem adapterPost;
    TextView found_txt;
    ImageView found;

    String myId;
    FirebaseAuth mAuth;
    String mName;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrentPage = 1;

    private RequestQueue requestQueue;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        found = findViewById(R.id.found);
        found_txt = findViewById(R.id.found_txt);

        //UserId
        mAuth = FirebaseAuth.getInstance();
        myId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        hisId = getIntent().getStringExtra("id");

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        ImageView message = findViewById(R.id.message);
        message.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChatActivityPage.class);
            intent.putExtra("id", hisId);
            startActivity(intent);
        });

        TextView followersNo = findViewById(R.id.followersNo);
        TextView followingNo = findViewById(R.id.followingNo);
        TextView followers = findViewById(R.id.followers);
        TextView following = findViewById(R.id.following);

        followersNo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowersActivityPage.class);
            intent.putExtra("id", hisId);
            startActivity(intent);
        });
        followingNo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowingActivityPage.class);
            intent.putExtra("id", hisId);
            startActivity(intent);
        });
        followers.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowersActivityPage.class);
            intent.putExtra("id", hisId);
            startActivity(intent);
        });
        following.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentFollowingActivityPage.class);
            intent.putExtra("id", hisId);
            startActivity(intent);
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisId).child("Followers");
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
                .child("Follow").child(hisId).child("Following");
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
        edit = findViewById(R.id.button3);
        unFollow = findViewById(R.id.unFollow);
        post = findViewById(R.id.post);
        pb = findViewById(R.id.pb);

        ImageView more = findViewById(R.id.more);
        more.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), more, Gravity.END);

            popupMenu.getMenu().add(Menu.NONE,1,0, "Report");

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == 1) {
                    FirebaseDatabase.getInstance().getReference().child("userReport").child(hisId).setValue(true);
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Reported")
                            .textColor(Color.WHITE)
                            .gravity(0)
                            .textBold()
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                }

                return false;
            });
            popupMenu.show();
        });

        pb.setVisibility(View.VISIBLE);

        postList= new ArrayList<>();
        loadPost();

        post.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrentPage++;
                    loadPost();
                }
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        edit.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("Following").child(hisId).setValue(true);
            FirebaseDatabase.getInstance().getReference().child("Follow").child(hisId)
                    .child("Followers").child(firebaseUser.getUid()).setValue(true);
            unFollow.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            addToHisNotification(""+hisId);
            notify = true;
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                    if (notify){
                        sendNotification(hisId, user.getName(), mName + " Started following you");

                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        unFollow.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("Following").child(hisId).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Follow").child(hisId)
                    .child("Followers").child(firebaseUser.getUid()).removeValue();
            unFollow.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
        });

        //Profile Details
        FirebaseDatabase.getInstance().getReference().child("Users").child(hisId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pb.setVisibility(View.GONE);
                 mName = snapshot.child("name").getValue().toString();
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

        isFollowing();

    }

    private void isFollowing() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(hisId).exists()){
                    edit.setVisibility(View.GONE);
                    unFollow.setVisibility(View.VISIBLE);
                }else {
                    edit.setVisibility(View.VISIBLE);
                    unFollow.setVisibility(View.GONE);
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
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    private void loadPost() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        post.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(hisId).limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    SystemModelForPosts systemModelForPosts = ds.getValue(SystemModelForPosts.class);
                    postList.add(systemModelForPosts);
                    adapterPost = new AdapterForPostsSystem(getApplicationContext(), postList);
                    post.setAdapter(adapterPost);
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

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), name + " : " + message, "New Commment", hisId, R.drawable.ic_logo);
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Log.d("JSON_RESPONSE", "onResponse" + response.toString()), error -> Log.d("JSON_RESPONSE", "onResponse" + error.toString())){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAA0uZNv7o:APA91bHuCIlfdRCrtHTAnDts-krSPky-_w9MhhW10BBVqVMX5LB9U7NIS7BnUc9ttavO-xPULcGkfz6SCqlD1yd7s-KktXBJCilBabWHXv_BSgQZIFuRhWE84Giozg1xscDtuEqFlxFs ");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addToHisNotification(String hisUid){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", "Started following you");
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap);

    }


}