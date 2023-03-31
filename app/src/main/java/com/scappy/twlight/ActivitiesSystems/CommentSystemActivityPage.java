package com.scappy.twlight.ActivitiesSystems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.joooonho.SelectableRoundedImageView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.adapter.AdapterForCommentSystem;
import com.scappy.twlight.SystemModels.SystemModelForComments;
import com.scappy.twlight.SystemModels.SystemModelForUsers;
import com.scappy.twlight.NotificationSystem.Data;
import com.scappy.twlight.NotificationSystem.Sender;
import com.scappy.twlight.NotificationSystem.Token;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@SuppressWarnings("ALL")
public class CommentSystemActivityPage extends AppCompatActivity implements View.OnClickListener {

    String pId;

    //User
    TextView tv_name,tv_username,tv_reTweet;
    CircleImageView mDp;

    //Post
    SocialTextView tv_tweet_text;
    SelectableRoundedImageView media;
    VideoView video;

    //Buttons
    ImageView like,reTweet,comment,tweet_action_edit,more;
    ImageView liked,reTweeted,commented;

    //Text
    TextView likeTv,reTweetTv,commentTv;

    //RelativeLayout
    RelativeLayout media_layout;

    //String
    String likeString = "no";
    String reTweetString = "no";
    String hisId;
    String myName;

    //Media
    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    RecyclerView recycler_view;
    List<SystemModelForComments> commentsList;
    AdapterForCommentSystem adapterComments;


    private RequestQueue requestQueue;
    private boolean notify = false;

    ProgressBar progressBar;

    ImageView verified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_page_appui);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Back
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //User
        tv_name = findViewById(R.id.tv_name);
        tv_username = findViewById(R.id.tv_username);
        mDp = findViewById(R.id.profile_photo);
        tv_reTweet = findViewById(R.id.tv_reTweet);
        verified = findViewById(R.id.verify);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //Post
        tv_tweet_text = findViewById(R.id.tv_tweet_text);
        media = findViewById(R.id.media);
        video = findViewById(R.id.video);

        //Buttons
        like = findViewById(R.id.like);
        reTweet = findViewById(R.id.reTweet);
        comment = findViewById(R.id.comment);
        tweet_action_edit = findViewById(R.id.tweet_action_edit);
        more = findViewById(R.id.more);

        //Buttons
        liked = findViewById(R.id.liked);
        reTweeted = findViewById(R.id.reTweeted);
        commented = findViewById(R.id.commented);

        //Text
        likeTv = findViewById(R.id.likeTv);
        reTweetTv = findViewById(R.id.reTweetTv);
        commentTv = findViewById(R.id.commentTv);

        //RelativeLayout
        media_layout = findViewById(R.id.media_layout);

        //RecyclerView
        recycler_view = findViewById(R.id.recycler_view);


        //Get Post Id
        pId = getIntent().getStringExtra("id");


        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myName = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

        //PostDetails
        FirebaseDatabase.getInstance().getReference().child("Posts").child(pId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hisId = snapshot.child("id").getValue().toString();
                String pText = snapshot.child("text").getValue().toString();
                String pType = snapshot.child("type").getValue().toString();

                progressBar.setVisibility(View.GONE);


                tv_tweet_text.setLinkText(pText);
                tv_tweet_text.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                    @Override
                    public void onLinkClicked(int i, String s) {
                        if (i == 1){
                            Intent intent = new Intent(getApplicationContext(), SearchPostSystemActivityPage.class);
                            intent.putExtra("tag", s);
                            startActivity(intent);
                        }else
                        if (i == 2){
                            String username = s.replaceFirst("@","");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            Query query = ref.orderByChild("username").equalTo(username.trim());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            String id = ds.child("id").getValue().toString();
                                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
                                                startActivity(intent);
                                            }else {
                                                Intent intent = new Intent(getApplicationContext(), ContentUserProfileActivityPage.class);
                                                intent.putExtra("id", id);
                                                startActivity(intent);
                                            }
                                        }
                                    }else {
                                        new StyleableToast
                                                .Builder(getApplicationContext())
                                                .text("Invalid username, can't find user with this username")
                                                .textColor(Color.WHITE)
                                                .textBold()
                                                .gravity(0)
                                                .length(2000)
                                                .solidBackground()
                                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                .show();
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
                        }
                        else if (i == 16){
                            String url = s;
                            if (!url.startsWith("https://") && !url.startsWith("http://")){
                                url = "http://" + url;
                            }
                            Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(openUrlIntent);
                        }else if (i == 4){
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                            startActivity(intent);
                        }else if (i == 8){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, s);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                startActivity(intent);

                        }

                    }
                });
                if (pType.equals("image")){
                    String imgURi = snapshot.child("image").getValue().toString();
                    Glide.with(getApplicationContext()).asBitmap().centerCrop().load(imgURi).into(media);
                    media_layout.setVisibility(View.VISIBLE);
                    media.setVisibility(View.VISIBLE);
                }else
                if (pType.equals("video")){
                    String vidURi = snapshot.child("video").getValue().toString();
                    Uri vineUri = Uri.parse(vidURi);
                    video.setVideoURI(vineUri);

                    video.start();

                    MediaController mediaController = new MediaController(CommentSystemActivityPage.this);
                    mediaController.setAnchorView(video);
                    video.setMediaController(mediaController);
                    media_layout.setVisibility(View.VISIBLE);
                    video.setVisibility(View.VISIBLE);

                }

                media_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (pType.equals("image")){
                            String imgURi = snapshot.child("image").getValue().toString();
                            Intent intent = new Intent(getApplicationContext(), MediaSystemActivityPage.class);
                            intent.putExtra("type", pType);
                            intent.putExtra("uri", imgURi);
                            startActivity(intent);
                        }else
                        if (pType.equals("video")){
                            String vidURi = snapshot.child("video").getValue().toString();
                            Intent intent = new Intent(getApplicationContext(), MediaSystemActivityPage.class);
                            intent.putExtra("type", pType);
                            intent.putExtra("uri", vidURi);
                            startActivity(intent);
                        }
                    }
                });

                String pView = snapshot.child("pViews").getValue().toString();
                String imgURi = snapshot.child("image").getValue().toString();
                String vidURi = snapshot.child("video").getValue().toString();
                String pComment = snapshot.child("pComments").getValue().toString();
                String privacy = snapshot.child("privacy").getValue().toString();
                String pTime = snapshot.child("pTime").getValue().toString();
                //ReTweet
                reTweet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (reTweetString.equals("no")){
                            reTweetString = "yes";
                            reTweet.setVisibility(View.GONE);
                            reTweeted.setVisibility(View.VISIBLE);

                            String timeStamp = String.valueOf(System.currentTimeMillis());
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap.put("pId", timeStamp);
                            hashMap.put("text", pText);
                            hashMap.put("pViews", pView);
                            hashMap.put("type", pType);
                            hashMap.put("video", vidURi);
                            hashMap.put("image", imgURi);
                            hashMap.put("pComments", pComment);
                            hashMap.put("reTweet", hisId);
                            hashMap.put("reId", pId);
                            hashMap.put("privacy", ""+privacy);
                            hashMap.put("pTime", pTime);

                            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                            dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    FirebaseDatabase.getInstance().getReference().child("ReTweet").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                    notify = true;
                                     DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                            if (notify){
                                sendNotification(hisId, user.getName(), myName + " Retweeted your post");

                                addToHisNotification("Retweeted your post", hisId);

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                                    new StyleableToast
                                            .Builder(getApplicationContext())
                                            .text("ReTweeted")
                                            .textColor(Color.WHITE)
                                            .textBold()
                                            .gravity(0)
                                            .length(2000)
                                            .solidBackground()
                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                            .show();
                                }
                            });

                        }
                    }
                });



                //UserDetails
                userDetails(hisId);

                //CheckLike
                checkLike(pId);

                //LikeNumber
                numberLike(pId);

                //CheckReTweet
                checkReTweet(pId);

                //ReTweetNumber
                numberReTweet(pId);

                //Share
                tweet_action_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        switch (pType) {
                            case "image": {

                                //Share Image
                                String imgURi = snapshot.child("image").getValue().toString();
                                Uri uri = Uri.parse(imgURi);
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                intent.putExtra(Intent.EXTRA_TEXT, pText);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                                intent.setType("image/*");
                                startActivity(Intent.createChooser(intent, "Share Via"));

                                break;
                            }
                            case "video": {

                                //Share Video
                                String vidURi = snapshot.child("video").getValue().toString();
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/*");
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                                intent.putExtra(Intent.EXTRA_TEXT, pText + " Link: " + vidURi);
                                startActivity(Intent.createChooser(intent, "Share Via"));

                                break;
                            }
                            case "text": {

                                //Share Text
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/*");
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                                intent.putExtra(Intent.EXTRA_TEXT, pText);
                                startActivity(Intent.createChooser(intent, "Share Via"));

                                break;
                            }
                        }

                    }
                });

                //More
                more.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), more, Gravity.END);
                        if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            popupMenu.getMenu().add(Menu.NONE,0,0, "Edit");
                            popupMenu.getMenu().add(Menu.NONE,1,0, "Delete");
                        }
                        if (!hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            popupMenu.getMenu().add(Menu.NONE,2,0, "Report");
                        }
                        popupMenu.getMenu().add(Menu.NONE,3,0, "Liked By");
                        popupMenu.getMenu().add(Menu.NONE,4,0, "ReTweeted By");
                        FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()){
                                    popupMenu.getMenu().add(Menu.NONE,5,0, "Save Tweet");

                                }else if (snapshot.exists()) {
                                    popupMenu.getMenu().add(Menu.NONE,8,0, "UnSave Tweet");
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
                                        .solidBackground() .gravity(0)
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });

                        String imgURi = snapshot.child("image").getValue().toString();
                        String vidURi = snapshot.child("video").getValue().toString();
                        if (!imgURi.isEmpty() || !vidURi.isEmpty()){
                            popupMenu.getMenu().add(Menu.NONE,6,0, "Download");
                        }
                        if (!pType.equals("text")){
                            popupMenu.getMenu().add(Menu.NONE,7,0, "Fullscreen");
                        }

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();

                                if (id == 0){
                                    Intent intent = new Intent(getApplicationContext(), EditPostSystemActivityPage.class);
                                    intent.putExtra("id", pId);
                                    startActivity(intent);
                                }

                                if (id == 1){
                                    deletePost(pId ,pType,imgURi,vidURi);
                                }

                                if (id == 2){
                                    reportPost(pId);
                                }


                                if (id == 3){
                                    Intent intent = new Intent(getApplicationContext(), GetLikedByActivityPage.class);
                                    intent.putExtra("postId", pId);
                                    startActivity(intent);
                                }

                                if (id == 4){
                                    Intent intent = new Intent(getApplicationContext(), GetRetweetedByContentActivityPage.class);
                                    intent.putExtra("postId", pId);
                                    startActivity(intent);
                                }

                                if (id == 5){
                                    new StyleableToast
                                            .Builder(getApplicationContext())
                                            .text("Saved")
                                            .textColor(Color.WHITE)
                                            .textBold()
                                            .length(2000) .gravity(0)
                                            .solidBackground()
                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                            .show();
                                    FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pId).setValue(true);
                                }


                                if (id == 6){
                                    download(pId,imgURi,vidURi);
                                }

                                if (id == 7){
                                    if (pType.equals("image")){
                                        Intent intent = new Intent(getApplicationContext(), MediaSystemActivityPage.class);
                                        intent.putExtra("type", pType);
                                        intent.putExtra("uri", imgURi);
                                        startActivity(intent);
                                    }else
                                        if (pType.equals("video")){
                                        Intent intent = new Intent(getApplicationContext(), MediaSystemActivityPage.class);
                                        intent.putExtra("type", pType);
                                        intent.putExtra("uri", vidURi);
                                        startActivity(intent);
                                    }


                                }

                                if (id == 8){
                                    new StyleableToast
                                            .Builder(getApplicationContext())
                                            .text("unSaved")
                                            .textColor(Color.WHITE)
                                            .textBold()
                                            .length(2000)
                                            .solidBackground() .gravity(0)
                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                            .show();
                                    FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pId).removeValue();
                                }

                                return false;
                            }



                        });
                        popupMenu.show();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000) .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });



        //Comment
        EditText postComment = findViewById(R.id.postText);
        ImageView addMedia = findViewById(R.id.add);
        ImageView send = findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = postComment.getText().toString();
                if (comment.isEmpty()){
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Type a comment")
                            .textColor(Color.WHITE)
                            .textBold()
                            .length(2000) .gravity(0)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                }else {

                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments");

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("cId", timeStamp);
                    hashMap.put("comment", comment);
                    hashMap.put("reTweet", "");
                    hashMap.put("reId", "");
                    hashMap.put("timestamp", timeStamp);
                    hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("type", "text");
                    hashMap.put("pId", pId);

                    ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            addToHisNotification("Commented on your post", hisId);
                            notify = true;
                                                                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                            if (notify){
                                sendNotification(hisId, user.getName(), comment);

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                            postComment.setText("");
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Comment Posted")
                                    .textColor(Color.WHITE) .gravity(0)
                                    .textBold()
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                    });

                }
            }
        });

        addMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        createBottomSheetDialog();

        //CommentCount
        FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0){
                    commentTv.setText("");
                }else {
                    commentTv.setText(String.valueOf(snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000) .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });

        //CheckCommented
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments");
        Query query = ref.orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String id = ds.child("id").getValue().toString();
                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        comment.setVisibility(View.GONE);
                        commented.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE) .gravity(0)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });


        loadComments();

        //Like
        //Like
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeString.equals("no")) {
                    likeString = "yes";
                    liked.setVisibility(View.VISIBLE);
                    like.setVisibility(View.GONE);
                    addToHisNotification("Liked your post",hisId);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    notify = true;
                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                            if (notify){
                                sendNotification(hisId, user.getName(), myName + " Liked your post");

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

        //UnLike
        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeString.equals("yes")) {
                    likeString = "no";
                    liked.setVisibility(View.GONE);
                    like.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                }
            }
        });



        //UnReTweet
        reTweeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reTweetString.equals("yes")) {
                    reTweeted.setVisibility(View.GONE);
                    reTweet.setVisibility(View.VISIBLE);


                    Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(pId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                String userId = ds.child("id").getValue().toString();
                                if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    ds.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("ReTweet").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    reTweetString = "no";
                                    new StyleableToast
                                            .Builder(getApplicationContext())
                                            .text("ReTweet Removed")
                                            .textColor(Color.WHITE) .gravity(0)
                                            .textBold()
                                            .length(2000)
                                            .solidBackground()
                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                            .show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text(databaseError.getMessage())
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000) .gravity(0)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                    });



                }
            }
        });


    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);
        commentsList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    SystemModelForComments systemModelComments = ds.getValue(SystemModelForComments.class);
                    commentsList.add(systemModelComments);
                    adapterComments = new AdapterForCommentSystem(getApplicationContext(), commentsList);
                    recycler_view.setAdapter(adapterComments);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            View view = LayoutInflater.from(this).inflate(R.layout.addmedia_ui, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayout3);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }

    private void download(String pId, String image, String video) {
        if (!image.isEmpty()){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                downloadImage(getApplicationContext(), "Image", ".png", DIRECTORY_DOWNLOADS, url);

            });

        }if (!video.isEmpty()){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(video);
            picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                downloadVideo(getApplicationContext(), "Video", ".mp4", DIRECTORY_DOWNLOADS, url);

            });

        }
    }

    private void downloadVideo(Context context, @SuppressWarnings("SameParameterValue") String video, @SuppressWarnings("SameParameterValue") String s, String directoryDownloads, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, directoryDownloads, video + s);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }

    public void downloadImage(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }


    private void reportPost(String pId) {
        FirebaseDatabase.getInstance().getReference().child("Report").child(pId).setValue(true);
        new StyleableToast
                .Builder(getApplicationContext())
                .text("Reported")
                .textColor(Color.WHITE)
                .textBold()
                .length(2000) .gravity(0)
                .solidBackground()
                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    //Delete Post
    private void deletePost(String postId, String pType, String image, String video) {

        if (pType.equals("image")){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ds.getRef().removeValue();
                                Query query1 = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postId);
                                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            ds.getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        new StyleableToast
                                                .Builder(getApplicationContext())
                                                .text(databaseError.getMessage())
                                                .textColor(Color.WHITE) .gravity(0)
                                                .textBold()
                                                .length(2000)
                                                .solidBackground()
                                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                .show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text(databaseError.getMessage()) .gravity(0)
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

        }if (pType.equals("video")){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(video);
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ds.getRef().removeValue();
                                Query query1 = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postId);
                                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            ds.getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        new StyleableToast
                                                .Builder(getApplicationContext())
                                                .text(databaseError.getMessage())
                                                .textColor(Color.WHITE)
                                                .textBold()
                                                .length(2000) .gravity(0)
                                                .solidBackground()
                                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                .show();
                                    }
                                });
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
                                    .solidBackground() .gravity(0)
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                    });
                }
            });

        }else{

            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();

                        Query query1 = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postId);
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text(databaseError.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000) .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text(databaseError.getMessage())
                            .textColor(Color.WHITE)
                            .textBold()
                            .length(2000) .gravity(0)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

        }

    }

    private void numberReTweet(String pId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ReTweet")){
                    if (snapshot.child("ReTweet").hasChild(pId)){
                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(pId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() == 0){
                                    reTweetTv.setText("");
                                }else {
                                    reTweetTv.setText(String.valueOf(snapshot.getChildrenCount()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text(error.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000) .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000) .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    private void checkReTweet(String pId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ReTweet")){
                    if (snapshot.child("ReTweet").hasChild(pId)){
                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(pId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    reTweetString="yes";
                                    reTweeted.setVisibility(View.VISIBLE);
                                    reTweet.setVisibility(View.GONE);
                                }else {
                                    reTweetString="no";
                                    reTweeted.setVisibility(View.GONE);
                                    reTweet.setVisibility(View.VISIBLE);
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
                                        .solidBackground() .gravity(0)
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000) .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    private void numberLike(String pId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Likes")){
                    if (snapshot.child("Likes").hasChild(pId)){
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(pId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() == 0){
                                    likeTv.setText("");
                                }else {
                                    likeTv.setText(String.valueOf(snapshot.getChildrenCount()));
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
                                        .solidBackground() .gravity(0)
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold() .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    private void checkLike(String pId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Likes")){
                    if (snapshot.child("Likes").hasChild(pId)){
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(pId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    likeString="yes";
                                    liked.setVisibility(View.VISIBLE);
                                    like.setVisibility(View.GONE);
                                }else {
                                    likeString="no";
                                    liked.setVisibility(View.GONE);
                                    like.setVisibility(View.VISIBLE);
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
                                        .solidBackground() .gravity(0)
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold() .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    private void userDetails(String hisId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(hisId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String username = snapshot.child("username").getValue().toString();
                String dp = snapshot.child("photo").getValue().toString();
                String mVerified = snapshot.child("verified").getValue().toString();

                if (mVerified.isEmpty()){
                    verified.setVisibility(View.GONE);
                }else {
                    verified.setVisibility(View.VISIBLE);
                }

                tv_name.setText(name);
                tv_username.setText(username);
                if (!dp.isEmpty()) {
                    Picasso.get().load(dp).placeholder(R.drawable.avatar).into(mDp);
                }
                tv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(getApplicationContext(), ContentUserProfileActivityPage.class);
                            intent.putExtra("id", hisId);
                            startActivity(intent);
                        }

                    }
                });
                tv_username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(getApplicationContext(), ContentUserProfileActivityPage.class);
                            intent.putExtra("id", hisId);
                            startActivity(intent);
                        }
                    }
                });
                mDp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(getApplicationContext(), ContentUserProfileActivityPage.class);
                            intent.putExtra("id", hisId);
                            startActivity(intent);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE) .gravity(0)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.constraintLayout3:
                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickImageFromGallery();
                    }
                }
                else {
                    pickImageFromGallery();
                }

                break;
            case R.id.delete:
                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        chooseVideo();
                    }
                }
                else {
                    chooseVideo();
                }
                break;
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Storage permission allowed")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000) .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            } else {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Storage permission is required")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground() .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bottomSheetDialog.cancel();
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri image_uri = Objects.requireNonNull(data).getData();
            sendImage(image_uri);
            new StyleableToast
                    .Builder(getApplicationContext())
                    .text("Please wait...")
                    .textColor(Color.WHITE)
                    .textBold() .gravity(0)
                    .length(2000)
                    .solidBackground()
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .show();
            progressBar.setVisibility(View.VISIBLE);
            bottomSheetDialog.cancel();
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri video_uri = data.getData();
            sendVideo(video_uri);
            new StyleableToast
                    .Builder(getApplicationContext())
                    .text("Please wait...")
                    .textColor(Color.WHITE)
                    .textBold()
                    .length(2000) .gravity(0)
                    .solidBackground()
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .show();
            progressBar.setVisibility(View.VISIBLE);
            bottomSheetDialog.cancel();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImage(Uri image_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cId", timeStamp);
                hashMap.put("comment", downloadUri);
                hashMap.put("timestamp", timeStamp);
                hashMap.put("reTweet", "");
                hashMap.put("reId", "");
                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("type", "image");
                hashMap.put("pId", pId);

                ref1.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addToHisNotification("Commented a image",hisId);
                        progressBar.setVisibility(View.GONE);
                        notify = true;
                                           DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                            if (notify){
                                sendNotification(hisId, user.getName(), "Commented a Image");

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("Comment Posted")
                                .textColor(Color.WHITE)
                                .textBold()
                                .length(2000)
                                .solidBackground() .gravity(0)
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                    }
                });


            }
        });
    }

    private void sendVideo(Uri video_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(video_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cId", timeStamp);
                hashMap.put("comment", downloadUri);
                hashMap.put("timestamp", timeStamp);
                hashMap.put("reTweet", "");
                hashMap.put("reId", "");
                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("type", "video");
                hashMap.put("pId", pId);

                ref1.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                addToHisNotification("Commented a video",hisId);

                                progressBar.setVisibility(View.GONE);
                                notify = true;
                                           DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                            if (notify){
                                sendNotification(hisId, user.getName(), "Commented a video");

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text("Comment Posted")
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000) .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
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
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), name + " : " + message, "New Notification", hisId, R.drawable.ic_logo);
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse" + response.toString());

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse" + error.toString());
                            }
                        }){
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

    private void addToHisNotification(String msg,String hisUid){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", msg);
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap);

    }



}