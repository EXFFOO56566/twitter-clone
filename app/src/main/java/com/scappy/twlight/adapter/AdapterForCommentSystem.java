package com.scappy.twlight.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.scappy.twlight.ActivitiesSystems.GetCommentLikedByActivity;
import com.scappy.twlight.ActivitiesSystems.GetCommentRetweetedByActivityPage;
import com.scappy.twlight.ActivitiesSystems.MediaSystemActivityPage;
import com.scappy.twlight.ActivitiesSystems.ViewMyProfileContentActivityPage;
import com.scappy.twlight.ActivitiesSystems.SearchPostSystemActivityPage;
import com.scappy.twlight.ActivitiesSystems.ContentUserProfileActivityPage;
import com.scappy.twlight.SystemModels.SystemModelForComments;
import com.scappy.twlight.SystemModels.SystemModelForUsers;
import com.scappy.twlight.NotificationSystem.Data;
import com.scappy.twlight.NotificationSystem.Sender;
import com.scappy.twlight.NotificationSystem.Token;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@SuppressWarnings("ALL")
public class AdapterForCommentSystem extends RecyclerView.Adapter<AdapterForCommentSystem.MyHolder>{

    Context context;
    final List<SystemModelForComments> commentList;
    String liked="no";
    String reTweetedString="no";

    private RequestQueue requestQueue;
    private boolean notify = false;
    String name;
    String postId;

    String comment;

    public AdapterForCommentSystem(Context context, List<SystemModelForComments> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_view_ui, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

         comment = commentList.get(position).getComment();
        String id = commentList.get(position).getId();
         postId = commentList.get(position).getpId();
        String type = commentList.get(position).getType();

        if (commentList.get(position).getReTweet().isEmpty()) {

            FirebaseDatabase.getInstance().getReference().child("Ban").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        holder.itemView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //User details
            FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                     name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                    String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                    String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                    holder.tv_name.setText(name);
                    holder.tv_username.setText(username);

                    String mVerified = snapshot.child("verified").getValue().toString();

                    if (mVerified.isEmpty()){
                        holder.verified.setVisibility(View.GONE);
                    }else {
                        holder.verified.setVisibility(View.VISIBLE);
                    }

                    if (!dp.isEmpty()) {
                        Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.mDp);
                    }

                    //ClickToPro
                    //noinspection Convert2Lambda
                    holder.tv_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", id);
                                context.startActivity(intent);
                            }
                        }
                    });
                    //noinspection Convert2Lambda
                    holder.tv_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", id);
                                context.startActivity(intent);
                            }
                        }
                    });
                    //noinspection Convert2Lambda
                    holder.mDp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", id);
                                context.startActivity(intent);
                            }
                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    new StyleableToast
                            .Builder(context)
                            .text(error.getMessage()) .gravity(0)
                            .textColor(Color.WHITE)
                            .textBold()
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

            //Like
            //noinspection Convert2Lambda
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liked.equals("no")) {
                        liked = "yes";
                        holder.liked.setVisibility(View.VISIBLE);
                        holder.like.setVisibility(View.GONE);
                        addToHisNotification("Liked your comment",id);
                        notify = true;
                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                                if (notify){
                                    sendNotification(id, user.getName(), name + " Liked your comment");

                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("CommentLikes").child(commentList.get(position).getcId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    }

                }
            });

            //UnLike
            //noinspection Convert2Lambda
            holder.liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liked.equals("yes")) {
                        liked = "no";
                        holder.liked.setVisibility(View.GONE);
                        holder.like.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference().child("CommentLikes").child(commentList.get(position).getcId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    }
                }
            });

            //ReTweet
            //noinspection Convert2Lambda
            holder.reTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reTweetedString.equals("no")){
                        reTweetedString = "yes";
                        holder.reTweet.setVisibility(View.GONE);
                        holder.reTweeted.setVisibility(View.VISIBLE);

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("cId", timeStamp);
                        hashMap.put("comment", comment);
                        hashMap.put("reTweet", commentList.get(position).getId());
                        hashMap.put("reId", commentList.get(position).getcId());
                        hashMap.put("timestamp", timeStamp);
                        hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("type", type);
                        hashMap.put("pId", postId);

                        //noinspection Convert2Lambda
                        FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").
                        child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addToHisNotification("Retweeted your comment",id);
                                notify = true;
                                          DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                                if (notify){
                                    sendNotification(id, user.getName(), name + " Retweeted your comment");

                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                                FirebaseDatabase.getInstance().getReference().child("CommentReTweet").child(commentList.get(position).getcId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                new StyleableToast
                                        .Builder(context)
                                        .text("ReTweeted") .gravity(0)
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });

                    }
                }
            });

            //UnReTweet
            //noinspection Convert2Lambda
            holder.reTweeted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reTweetedString.equals("yes")) {
                        holder.reTweeted.setVisibility(View.GONE);
                        holder.reTweet.setVisibility(View.VISIBLE);


                    Query query = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").orderByChild("reId").equalTo(commentList.get(position).getcId());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                String userId = ds.child("id").getValue().toString();
                                if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    ds.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("CommentReTweet").child(commentList.get(position).getcId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    reTweetedString = "no";
                                    new StyleableToast
                                            .Builder(context)
                                            .text("ReTweet Removed")
                                            .textColor(Color.WHITE) .gravity(0)
                                            .textBold()
                                            .length(2000)
                                            .solidBackground()
                                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                            .show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new StyleableToast
                                    .Builder(context)
                                    .text(databaseError.getMessage())
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000) .gravity(0)
                                    .solidBackground()
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                    });



                    }
                }
            });



            //CheckLike
            checkLike(holder, commentList.get(position).getcId());

            //LikeNumber
            numberLike(holder, commentList.get(position).getcId());

            //CheckReTweet
            checkReTweet(holder, commentList.get(position).getcId());

            //ReTweetNumber
            numberReTweet(holder, commentList.get(position).getcId());

            //More
            //noinspection Convert2Lambda
            holder.more.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.more, Gravity.END);
                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        popupMenu.getMenu().add(Menu.NONE,1,0, "Delete");
                    }
                    popupMenu.getMenu().add(Menu.NONE,3,0, "Liked By");
                    popupMenu.getMenu().add(Menu.NONE,4,0, "ReTweeted By");
                    if (!type.equals("image") || !type.equals("video")){
                        popupMenu.getMenu().add(Menu.NONE,6,0, "Download");
                    }
                    if (!type.equals("text")){
                        popupMenu.getMenu().add(Menu.NONE,7,0, "Fullscreen");
                    }
                    //noinspection Convert2Lambda
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            if (id == 1){
                                deletePost(commentList.get(position).getcId(),type,postId);
                            }


                            if (id == 3){
                                Intent intent = new Intent(context, GetCommentLikedByActivity.class);
                                intent.putExtra("cId", commentList.get(position).getcId());
                                intent.putExtra("pId", postId);
                                context.startActivity(intent);
                            }

                            if (id == 4){
                                Intent intent = new Intent(context, GetCommentRetweetedByActivityPage.class);
                                intent.putExtra("postId", commentList.get(position).getcId());
                                context.startActivity(intent);
                            }


                            if (id == 7){
                                if (type.equals("image")){
                                    Intent intent = new Intent(context, MediaSystemActivityPage.class);
                                    intent.putExtra("type", type);
                                    intent.putExtra("uri", comment);
                                    context.startActivity(intent);
                                }else
                                if (type.equals("video")){
                                    Intent intent = new Intent(context, MediaSystemActivityPage.class);
                                    intent.putExtra("type", type);
                                    intent.putExtra("uri", comment);
                                    context.startActivity(intent);
                                }


                            }


                            if (id == 6){
                                download(commentList.get(position).getcId(),type);
                            }


                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });


        } else {

            FirebaseDatabase.getInstance().getReference().child("Ban").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        holder.itemView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Ban").child(commentList.get(position).getReId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        holder.itemView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //ReTweetDetails
            FirebaseDatabase.getInstance().getReference().child("Users").child(commentList.get(position).getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                    holder.tv_reTweet.setText("ReTweeted by "+ username);

                    holder.tv_reTweet.setVisibility(View.VISIBLE);

                    //noinspection Convert2Lambda
                    holder.tv_reTweet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", id);
                                context.startActivity(intent);
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    new StyleableToast
                            .Builder(context)
                            .text(error.getMessage()) .gravity(0)
                            .textColor(Color.WHITE)
                            .textBold()
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

            //User details
            FirebaseDatabase.getInstance().getReference().child("Users").child(commentList.get(position).getReTweet()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                    String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                    String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                    holder.tv_name.setText(name);
                    holder.tv_username.setText(username);

                    String mVerified = snapshot.child("verified").getValue().toString();

                    if (mVerified.isEmpty()){
                        holder.verified.setVisibility(View.GONE);
                    }else {
                        holder.verified.setVisibility(View.VISIBLE);
                    }

                    if (!dp.isEmpty()) {
                        Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.mDp);
                    }

                    //noinspection Convert2Lambda
                    holder.tv_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (commentList.get(position).getReId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", commentList.get(position).getReId());
                                context.startActivity(intent);
                            }
                        }
                    });

                    //noinspection Convert2Lambda
                    holder.tv_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (commentList.get(position).getReId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", commentList.get(position).getReId());
                                context.startActivity(intent);
                            }
                        }
                    });

                    //noinspection Convert2Lambda
                    holder.mDp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (commentList.get(position).getReId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", commentList.get(position).getReId());
                                context.startActivity(intent);
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    new StyleableToast
                            .Builder(context)
                            .text(error.getMessage())
                            .textColor(Color.WHITE) .gravity(0)
                            .textBold()
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

            //Like
            //noinspection Convert2Lambda
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liked.equals("no")) {
                        liked = "yes";
                        holder.liked.setVisibility(View.VISIBLE);
                        holder.like.setVisibility(View.GONE);
                        addToHisNotification("Liked your comment",commentList.get(position).getReId());
                        notify = true;
                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                                if (notify){
                                    sendNotification( commentList.get(position).getReId(), user.getName(), name + " Liked your comment");

                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("CommentLikes").child(commentList.get(position).getReId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    }

                }
            });

            //UnLike
            //noinspection Convert2Lambda
            holder.liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liked.equals("yes")) {
                        liked = "no";
                        holder.liked.setVisibility(View.GONE);
                        holder.like.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference().child("CommentLikes").child(commentList.get(position).getReId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    }
                }
            });

            //ReTweet
            //noinspection Convert2Lambda
            holder.reTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reTweetedString.equals("no")){
                        reTweetedString = "yes";
                        holder.reTweet.setVisibility(View.GONE);
                        holder.reTweeted.setVisibility(View.VISIBLE);

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("cId", timeStamp);
                        hashMap.put("comment", comment);
                        hashMap.put("reTweet", commentList.get(position).getReTweet());
                        hashMap.put("reId", commentList.get(position).getReId());
                        hashMap.put("timestamp", timeStamp);
                        hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("type", type);
                        hashMap.put("pId", postId);

                        addToHisNotification("Retweeted your comment",commentList.get(position).getReId());

                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                        //noinspection Convert2Lambda
                        dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notify = true;
                                          DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                                if (notify){
                                    sendNotification(commentList.get(position).getReId(), user.getName(), name + " Retweeted your comment");

                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                                FirebaseDatabase.getInstance().getReference().child("CommentReTweet").child(commentList.get(position).getReId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                new StyleableToast
                                        .Builder(context)
                                        .text("ReTweeted") .gravity(0)
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });

                    }
                }
            });


            //UnReTweet
            //noinspection Convert2Lambda
            holder.reTweeted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reTweetedString.equals("yes")) {
                        holder.reTweeted.setVisibility(View.GONE);
                        holder.reTweet.setVisibility(View.VISIBLE);


                        Query query = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").orderByChild("reId").equalTo(commentList.get(position).getReId());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    String userId = ds.child("id").getValue().toString();
                                    if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        ds.getRef().removeValue();
                                        FirebaseDatabase.getInstance().getReference().child("CommentReTweet").child(commentList.get(position).getReId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        reTweetedString = "no";
                                        new StyleableToast
                                                .Builder(context)
                                                .text("ReTweet Removed") .gravity(0)
                                                .textColor(Color.WHITE)
                                                .textBold()
                                                .length(2000)
                                                .solidBackground()
                                                .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                                .show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                new StyleableToast
                                        .Builder(context) .gravity(0)
                                        .text(databaseError.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });



                    }
                }
            });

            //CheckLike
            checkLike(holder, commentList.get(position).getReId());

            //LikeNumber
            numberLike(holder, commentList.get(position).getReId());

            //CheckReTweet
            checkReTweet(holder, commentList.get(position).getReId());

            //ReTweetNumber
            numberReTweet(holder, commentList.get(position).getReId());

            //More
            //noinspection Convert2Lambda
            holder.more.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.more, Gravity.END);
                    popupMenu.getMenu().add(Menu.NONE,3,0, "Liked By");
                    popupMenu.getMenu().add(Menu.NONE,4,0, "ReTweeted By");
                    if (!type.equals("image") || !type.equals("video")){
                        popupMenu.getMenu().add(Menu.NONE,6,0, "Download");
                    }
                    if (!type.equals("text")){
                        popupMenu.getMenu().add(Menu.NONE,7,0, "Fullscreen");
                    }

                    //noinspection Convert2Lambda
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();


                            if (id == 3){
                                Intent intent = new Intent(context, GetCommentLikedByActivity.class);
                                intent.putExtra("cId", commentList.get(position).getReId());
                                intent.putExtra("pId", postId);
                                context.startActivity(intent);

                            }

                            if (id == 4){
                                Intent intent = new Intent(context, GetCommentRetweetedByActivityPage.class);
                                intent.putExtra("postId", commentList.get(position).getReId());
                                context.startActivity(intent);
                            }

                            if (id == 7){
                                if (type.equals("image")){
                                    Intent intent = new Intent(context, MediaSystemActivityPage.class);
                                    intent.putExtra("type", type);
                                    intent.putExtra("uri", comment);
                                    context.startActivity(intent);
                                }else
                                if (type.equals("video")){
                                    Intent intent = new Intent(context, MediaSystemActivityPage.class);
                                    intent.putExtra("type", type);
                                    intent.putExtra("uri", comment);
                                    context.startActivity(intent);
                                }


                            }

                            if (id == 6){
                                download(commentList.get(position).getReId(),type);
                            }


                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });


        }

        //Post details
        if (type.equals("image")){
            Glide.with(context).asBitmap().load(comment).centerCrop().into(holder.media);
            holder.media_layout.setVisibility(View.VISIBLE);
            holder.play.setVisibility(View.GONE);
        }else
        if (type.equals("video")){
            holder.play.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().load(comment).centerCrop().into(holder.media);
            holder.media_layout.setVisibility(View.VISIBLE);
        }else {
            holder.tv_tweet_text.setLinkText(comment);
            //noinspection Convert2Lambda
            holder.tv_tweet_text.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                @Override
                public void onLinkClicked(int i, String s) {
                    if (i == 1){
                        Intent intent = new Intent(context, SearchPostSystemActivityPage.class);
                        intent.putExtra("tag", s);
                        context.startActivity(intent);
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
                                            Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                            context.startActivity(intent);
                                        }else {
                                            Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                            intent.putExtra("id", id);
                                            context.startActivity(intent);
                                        }
                                    }
                                }else {
                                    new StyleableToast
                                            .Builder(context)
                                            .text("Invalid username, can't find user with this username")
                                            .textColor(Color.WHITE)
                                            .textBold() .gravity(0)
                                            .length(2000)
                                            .solidBackground()
                                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                            .show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new StyleableToast
                                        .Builder(context)
                                        .text(error.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000) .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
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
                        context.startActivity(openUrlIntent);
                        }else if (i == 4){
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                        context.startActivity(intent);
                        }else if (i == 8){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, s);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "");

                                context.startActivity(intent);

                        }
                }
            });
        }

        //noinspection Convert2Lambda
        holder.media_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("image")){
                    Intent intent = new Intent(context, MediaSystemActivityPage.class);
                    intent.putExtra("type", type);
                    intent.putExtra("uri", comment);
                    context.startActivity(intent);
                }else
                if (type.equals("video")){
                    Intent intent = new Intent(context, MediaSystemActivityPage.class);
                    intent.putExtra("type", type);
                    intent.putExtra("uri", comment);
                    context.startActivity(intent);
                }
            }
        });

        //noinspection Convert2Lambda
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("image")){
                    Intent intent = new Intent(context, MediaSystemActivityPage.class);
                    intent.putExtra("type", type);
                    intent.putExtra("uri", comment);
                    context.startActivity(intent);
                }else
                if (type.equals("video")){
                    Intent intent = new Intent(context, MediaSystemActivityPage.class);
                    intent.putExtra("type", type);
                    intent.putExtra("uri", comment);
                    context.startActivity(intent);
                }
            }
        });

        //Share
        //noinspection Convert2Lambda
        holder.tweet_action_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (type) {
                    case "image": {

                        //Share Image
                        Uri uri = Uri.parse(comment);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                        intent.setType("image/*");
                        context.startActivity(Intent.createChooser(intent, "Share Via"));

                        break;
                    }
                    case "video": {

                        //Share Video
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/*");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                        intent.putExtra(Intent.EXTRA_TEXT, " Link: " + comment);
                        context.startActivity(Intent.createChooser(intent, "Share Via"));

                        break;
                    }
                    case "text": {

                        //Share Text
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/*");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                        intent.putExtra(Intent.EXTRA_TEXT, comment);
                        context.startActivity(Intent.createChooser(intent, "Share Via"));

                        break;
                    }
                }

            }
        });


    }

    private void numberLike(MyHolder holder, String commentId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("CommentLikes")){
                    if (snapshot.child("CommentLikes").hasChild(commentId)){
                        FirebaseDatabase.getInstance().getReference().child("CommentLikes").child(commentId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() == 0){
                                    holder.likeTv.setText("");
                                }else {
                                    holder.likeTv.setText(String.valueOf(snapshot.getChildrenCount()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new StyleableToast
                                        .Builder(context)
                                        .text(error.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000) .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(context)
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000) .gravity(0)
                        .solidBackground()
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    private void checkLike(MyHolder holder, String commentId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("CommentLikes")){
                    if (snapshot.child("CommentLikes").hasChild(commentId)){
                        FirebaseDatabase.getInstance().getReference().child("CommentLikes").child(commentId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    liked="yes";
                                    holder.liked.setVisibility(View.VISIBLE);
                                    holder.like.setVisibility(View.GONE);
                                }else {
                                    liked="no";
                                    holder.liked.setVisibility(View.GONE);
                                    holder.like.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new StyleableToast
                                        .Builder(context)
                                        .text(error.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000) .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(context)
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold() .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });

    }

    private void checkReTweet(MyHolder holder, String commentId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("CommentReTweet")){
                    if (snapshot.child("CommentReTweet").hasChild(commentId)){
                        FirebaseDatabase.getInstance().getReference().child("CommentReTweet").child(commentId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    reTweetedString="yes";
                                    holder.reTweeted.setVisibility(View.VISIBLE);
                                    holder.reTweet.setVisibility(View.GONE);
                                }else {
                                    reTweetedString="no";
                                    holder.reTweeted.setVisibility(View.GONE);
                                    holder.reTweet.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new StyleableToast
                                        .Builder(context)
                                        .text(error.getMessage())
                                        .textColor(Color.WHITE) .gravity(0)
                                        .textBold()
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(context)
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground() .gravity(0)
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    private void numberReTweet(MyHolder holder, String commentId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("CommentReTweet")){
                    if (snapshot.child("CommentReTweet").hasChild(commentId)){
                        FirebaseDatabase.getInstance().getReference().child("CommentReTweet").child(commentId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() == 0){
                                    holder.reTweetTv.setText("");
                                }else {
                                    holder.reTweetTv.setText(String.valueOf(snapshot.getChildrenCount()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new StyleableToast
                                        .Builder(context)
                                        .text(error.getMessage())
                                        .textColor(Color.WHITE) .gravity(0)
                                        .textBold()
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(context)
                        .text(error.getMessage()) .gravity(0)
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    private void deletePost(String commentId, String type, String postId) {
        if (type.equals("image")){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(comment);
            //noinspection Convert2Lambda
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Query query = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").orderByChild("cId").equalTo(commentId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ds.getRef().removeValue();
                                new StyleableToast
                                        .Builder(context)
                                        .text("Comment deleted") .gravity(0)
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new StyleableToast
                                    .Builder(context)
                                    .text(databaseError.getMessage())
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000) .gravity(0)
                                    .solidBackground()
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                    });
                }
            });

        }if (type.equals("video")){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(comment);
            //noinspection Convert2Lambda
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Query query = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").orderByChild("cId").equalTo(commentId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ds.getRef().removeValue();
                                new StyleableToast
                                        .Builder(context)
                                        .text("Comment deleted")
                                        .textColor(Color.WHITE)
                                        .textBold() .gravity(0)
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new StyleableToast
                                    .Builder(context)
                                    .text(databaseError.getMessage())
                                    .textColor(Color.WHITE)
                                    .textBold() .gravity(0)
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                    });
                }
            });

        }else {

            Query query = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").orderByChild("cId").equalTo(commentId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                        new StyleableToast
                                .Builder(context)
                                .text("Comment deleted")
                                .textColor(Color.WHITE)
                                .textBold()
                                .length(2000) .gravity(0)
                                .solidBackground()
                                .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    new StyleableToast
                            .Builder(context)
                            .text(databaseError.getMessage())
                            .textColor(Color.WHITE) .gravity(0)
                            .textBold()
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

        }
    }



    private void download(String commentId, String type) {
        if (type.equals("image")){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(comment);
            picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                downloadImage(context, "Image", ".png", DIRECTORY_DOWNLOADS, url);

            });

        }if (type.equals("video")){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(comment);
            picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                downloadVideo(context, "Video", ".mp4", DIRECTORY_DOWNLOADS, url);

            });

        }

    }

    //DownloadVideo
    private void downloadVideo(Context context, String video, String s, String directoryDownloads, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, directoryDownloads, video + s);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }

    //DownloadImage
    public void downloadImage(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        //User
        TextView tv_name,tv_username,tv_reTweet;
        CircleImageView mDp;

        //Post
        SocialTextView tv_tweet_text;
        ImageView play;
        SelectableRoundedImageView media;

        //Buttons
        ImageView like,reTweet,tweet_action_edit,more;
        ImageView liked,reTweeted;

        //Text
        TextView likeTv,reTweetTv;

        //RelativeLayout
        RelativeLayout media_layout;

        ImageView verified;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //User
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_username = itemView.findViewById(R.id.tv_username);
            mDp = itemView.findViewById(R.id.profile_photo);
            tv_reTweet = itemView.findViewById(R.id.tv_reTweet);
            verified = itemView.findViewById(R.id.verified);

            //Post
            tv_tweet_text = itemView.findViewById(R.id.tv_tweet_text);
            media = itemView.findViewById(R.id.media);
            play = itemView.findViewById(R.id.play);

            //Buttons
            like = itemView.findViewById(R.id.like);
            reTweet = itemView.findViewById(R.id.reTweet);
            tweet_action_edit = itemView.findViewById(R.id.tweet_action_edit);
            more = itemView.findViewById(R.id.more);

            //Buttons
            liked = itemView.findViewById(R.id.liked);
            reTweeted = itemView.findViewById(R.id.reTweeted);

            //Text
            likeTv = itemView.findViewById(R.id.likeTv);
            reTweetTv = itemView.findViewById(R.id.reTweetTv);

            //RelativeLayout
            media_layout = itemView.findViewById(R.id.media_layout);

        }
    }

    private void sendNotification(final String hisId, final String name,final String message){
        requestQueue = Volley.newRequestQueue(context);
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
                        @SuppressWarnings("Convert2Lambda") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addToHisNotification(String msg,String hisUid){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", postId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", msg);
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap);

    }

}
