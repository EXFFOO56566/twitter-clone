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
import com.scappy.twlight.ActivitiesSystems.CommentSystemActivityPage;
import com.scappy.twlight.ActivitiesSystems.ViewMyProfileContentActivityPage;
import com.scappy.twlight.ActivitiesSystems.SearchPostSystemActivityPage;
import com.scappy.twlight.ActivitiesSystems.ContentUserProfileActivityPage;
import com.scappy.twlight.SystemModels.SystemModelForPosts;
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
public class AdapterforAdminPostSystem extends RecyclerView.Adapter<AdapterforAdminPostSystem.MyHolder> {

    Context context;
    final List<SystemModelForPosts> postList;
    String liked="no";
    String reTweetedString="no";

    String pId;

    String id;
    String name;
    String reTweet;

    private RequestQueue requestQueue;
    private boolean notify = false;

    public AdapterforAdminPostSystem(Context context, List<SystemModelForPosts> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_view_ui, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

         id = postList.get(position).getId();
        String pText = postList.get(position).getText();
        String pType = postList.get(position).getType();
         pId = postList.get(position).getpId();
         reTweet = postList.get(position).getReTweet();
        String reId = postList.get(position).getReId();

        //PostDetails
        String pView = postList.get(position).getpViews();
        String video = postList.get(position).getVideo();
        String image = postList.get(position).getImage();
        String pComment = postList.get(position).getpComments();
        String privacy = postList.get(position).getPrivacy();
        String pTime = postList.get(position).getpTime();



        if (reTweet.isEmpty()) {

            FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.child(pId).exists()){
                            holder.view.setText(String.valueOf(snapshot.child(pId).getChildrenCount()));
                        }
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
                    String userId = snapshot.child("id").getValue().toString();
                     name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                    String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                    String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                    holder.tv_name.setText(name);
                    holder.tv_username.setText(username);

                    if (!dp.isEmpty()) {
                        Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.mDp);
                    }

                    //ClickToPro
                    holder.tv_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", userId);
                                context.startActivity(intent);
                            }
                        }
                    });
                    holder.tv_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", userId);
                                context.startActivity(intent);
                            }
                        }
                    });
                    holder.mDp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", userId);
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
                            .textColor(Color.WHITE)
                            .textBold() .gravity(0)
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

            //Like
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liked.equals("no")) {
                        liked = "yes";
                        holder.liked.setVisibility(View.VISIBLE);
                        holder.like.setVisibility(View.GONE);
                        addToHisNotification("Liked your post",id);
                        notify = true;
                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                                if (notify){
                                    sendNotification(id, user.getName(), name + " Liked your post");

                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    }

                }
            });

            //UnLike
            holder.liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liked.equals("yes")) {
                        liked = "no";
                        holder.liked.setVisibility(View.GONE);
                        holder.like.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    }
                }
            });

            //ReTweet
            holder.reTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reTweetedString.equals("no")){
                        reTweetedString = "yes";
                        holder.reTweet.setVisibility(View.GONE);
                        holder.reTweeted.setVisibility(View.VISIBLE);

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("pId", timeStamp);
                        hashMap.put("text", pText);
                        hashMap.put("pViews", pView);
                        hashMap.put("type", pType);
                        hashMap.put("video", video);
                        hashMap.put("image", image);
                        hashMap.put("pComments", pComment);
                        hashMap.put("reTweet", id);
                        hashMap.put("reId", pId);
                        hashMap.put("privacy", ""+privacy);
                        hashMap.put("pTime", pTime);

                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                        dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                addToHisNotification("Retweeted your post",id);
                                notify = true;
                                           DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                                if (notify){
                                    sendNotification(id, user.getName(), name + " Retweeted your post");

                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

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
            holder.reTweeted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reTweetedString.equals("yes")) {
                        holder.reTweeted.setVisibility(View.GONE);
                        holder.reTweet.setVisibility(View.VISIBLE);


                    Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(pId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                String userId = ds.child("id").getValue().toString();
                                if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    ds.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("ReTweet").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    reTweetedString = "no";
                                    new StyleableToast
                                            .Builder(context)
                                            .text("ReTweet Removed")
                                            .textColor(Color.WHITE)
                                            .textBold()
                                            .length(2000) .gravity(0)
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
            checkLike(holder, pId);

            //LikeNumber
            numberLike(holder, pId);

            //CheckReTweet
            checkReTweet(holder, pId);

            //ReTweetNumber
            numberReTweet(holder, pId);

            //Comment
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pType.equals("video")){
                        FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                   if (snapshot.child(pId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                       Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                       intent.putExtra("id", pId);
                                       context.startActivity(intent);
                                   }else {
                                       FirebaseDatabase.getInstance().getReference().child("Views").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                       Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                       intent.putExtra("id", pId);
                                       context.startActivity(intent);
                                   }
                                }else {
                                    FirebaseDatabase.getInstance().getReference().child("Views").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                    intent.putExtra("id", pId);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else {
                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                        intent.putExtra("id", pId);
                        context.startActivity(intent);
                    }
                }
            });

            holder.media_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pType.equals("video")){
                        FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (snapshot.child(pId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                        intent.putExtra("id", pId);
                                        context.startActivity(intent);
                                    }else {
                                        FirebaseDatabase.getInstance().getReference().child("Views").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                        intent.putExtra("id", pId);
                                        context.startActivity(intent);
                                    }
                                }else {
                                    FirebaseDatabase.getInstance().getReference().child("Views").child(pId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                    intent.putExtra("id", pId);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else {
                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                        intent.putExtra("id", pId);
                        context.startActivity(intent);
                    }
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                    intent.putExtra("id", pId);
                    context.startActivity(intent);
                }
            });

            holder.commented.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                    intent.putExtra("id", pId);
                    context.startActivity(intent);
                }
            });

            holder.commentTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                    intent.putExtra("id", pId);
                    context.startActivity(intent);
                }
            });

            //More
            holder.more.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.more, Gravity.END);
                        popupMenu.getMenu().add(Menu.NONE,1,0, "Delete");
                    popupMenu.getMenu().add(Menu.NONE,2,0, "Delete from report");

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            if (id == 1){
                                String postId = postList.get(position).getpId();
                                deletePost(postId,pType,image,video);
                                FirebaseDatabase.getInstance().getReference().child("Report").child(postId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }   else  if (id == 2){
                                String postId = postList.get(position).getpId();
                                FirebaseDatabase.getInstance().getReference().child("Report").child(postId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();
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

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }


                            return false;
                        }
                    });
                    popupMenu.show();
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
                            holder.comment.setVisibility(View.GONE);
                            holder.commented.setVisibility(View.VISIBLE);
                        }
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


            //CommentCount
            FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() == 0){
                        holder.commentTv.setText("");
                    }else {
                        holder.commentTv.setText(String.valueOf(snapshot.getChildrenCount()));
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


        } else {

                   FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.child(reId).exists()){
                            holder.view.setText(String.valueOf(snapshot.child(reId).getChildrenCount()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //ReTweetDetails
            FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId = snapshot.child("id").getValue().toString();
                    String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                    holder.tv_reTweet.setText("ReTweeted by "+ username);

                    holder.tv_reTweet.setVisibility(View.VISIBLE);

                    holder.tv_reTweet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", userId);
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
                            .textColor(Color.WHITE)
                            .textBold() .gravity(0)
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

            //User details
            FirebaseDatabase.getInstance().getReference().child("Users").child(reTweet).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId = snapshot.child("id").getValue().toString();
                    String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                    String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                    String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                    holder.tv_name.setText(name);
                    holder.tv_username.setText(username);

                    if (!dp.isEmpty()) {
                        Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.mDp);
                    }

                    holder.tv_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (reTweet.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", userId);
                                context.startActivity(intent);
                            }
                        }
                    });

                    holder.tv_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (reTweet.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", userId);
                                context.startActivity(intent);
                            }
                        }
                    });

                    holder.mDp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (reTweet.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                                intent.putExtra("id", userId);
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
                            .textColor(Color.WHITE)
                            .textBold() .gravity(0)
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

            //Like
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liked.equals("no")) {
                        liked = "yes";
                        holder.liked.setVisibility(View.VISIBLE);
                        holder.like.setVisibility(View.GONE);
                        addToHisNotification("Liked your post",reTweet);
                        notify = true;
                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                                if (notify){
                                    sendNotification(reTweet, user.getName(), name + " Liked your post");

                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    }

                }
            });

            //UnLike
            holder.liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liked.equals("yes")) {
                        liked = "no";
                        holder.liked.setVisibility(View.GONE);
                        holder.like.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    }
                }
            });

            //ReTweet
            holder.reTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reTweetedString.equals("no")){
                        reTweetedString = "yes";
                        holder.reTweet.setVisibility(View.GONE);
                        holder.reTweeted.setVisibility(View.VISIBLE);

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("pId", timeStamp);
                        hashMap.put("text", pText);
                        hashMap.put("pViews", pView);
                        hashMap.put("type", pType);
                        hashMap.put("video", video);
                        hashMap.put("image", image);
                        hashMap.put("pComments", pComment);
                        hashMap.put("reTweet", reTweet);
                        hashMap.put("reId", reId);
                        hashMap.put("privacy", ""+privacy);
                        hashMap.put("pTime", pTime);

                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                        dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                addToHisNotification("Retweeted your post",reTweet);
                                notify = true;
                                           DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                                if (notify){
                                    sendNotification(reTweet, user.getName(), name + " Retweeted your post");

                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

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
            holder.reTweeted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reTweetedString.equals("yes")) {
                        holder.reTweeted.setVisibility(View.GONE);
                        holder.reTweet.setVisibility(View.VISIBLE);

                        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(reId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    String userId = ds.child("id").getValue().toString();
                                    if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        ds.getRef().removeValue();
                                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        reTweetedString = "no";
                                        new StyleableToast
                                                .Builder(context)
                                                .text("ReTweet Removed")
                                                .textColor(Color.WHITE)
                                                .textBold() .gravity(0)
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
                                        .textBold() .gravity(0)
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
            checkLike(holder, reId);

            //LikeNumber
            numberLike(holder, reId);

            //CheckReTweet
            checkReTweet(holder, reId);

            //ReTweetNumber
            numberReTweet(holder, reId);

            //Comment
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pType.equals("video")){
                        FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (snapshot.child(reId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                        intent.putExtra("id", reId);
                                        context.startActivity(intent);
                                    }else {
                                        FirebaseDatabase.getInstance().getReference().child("Views").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                        intent.putExtra("id", reId);
                                        context.startActivity(intent);
                                    }
                                }else {
                                    FirebaseDatabase.getInstance().getReference().child("Views").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                    intent.putExtra("id", reId);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else {
                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                        intent.putExtra("id", reId);
                        context.startActivity(intent);
                    }
                }
            });

            holder.media_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pType.equals("video")){
                        FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (snapshot.child(reId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                        intent.putExtra("id", reId);
                                        context.startActivity(intent);
                                    }else {
                                        FirebaseDatabase.getInstance().getReference().child("Views").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                        intent.putExtra("id", reId);
                                        context.startActivity(intent);
                                    }
                                }else {
                                    FirebaseDatabase.getInstance().getReference().child("Views").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                                    intent.putExtra("id", reId);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else {
                        Intent intent = new Intent(context, CommentSystemActivityPage.class);
                        intent.putExtra("id", reId);
                        context.startActivity(intent);
                    }
                }
            });


            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                    intent.putExtra("id", reId);
                    context.startActivity(intent);
                }
            });

            holder.commented.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                    intent.putExtra("id", reId);
                    context.startActivity(intent);
                }
            });

            holder.commentTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentSystemActivityPage.class);
                    intent.putExtra("id", reId);
                    context.startActivity(intent);
                }
            });

            //More
            holder.more.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.more, Gravity.END);
                    popupMenu.getMenu().add(Menu.NONE,1,0, "Delete Retweet");
                    popupMenu.getMenu().add(Menu.NONE,2,0, "Remove from report");

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            if (id == 1){
                                String rePost = postList.get(position).getReId();
                                FirebaseDatabase.getInstance().getReference().child("Report").child(rePost).removeValue();
                                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(rePost);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                           String mId = postList.get(position).getId();
                                            String userId = ds.child("id").getValue().toString();
                                            if (userId.equals(mId)){
                                                ds.getRef().removeValue();
                                                String idP = postList.get(position).getId();
                                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(rePost).child(idP).removeValue();
                                                String re = postList.get(position).getpId();
                                                FirebaseDatabase.getInstance().getReference().child("Report").child(re).removeValue();
                                                reTweetedString = "no";
                                                new StyleableToast
                                                        .Builder(context)
                                                        .text("ReTweet Removed")
                                                        .textColor(Color.WHITE)
                                                        .textBold()
                                                        .length(2000) .gravity(0)
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
                                                .text(databaseError.getMessage()) .gravity(0)
                                                .textColor(Color.WHITE)
                                                .textBold()
                                                .length(2000)
                                                .solidBackground()
                                                .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                                .show();
                                    }
                                });

                            }
                            else  if (id == 2){
                                String rePost = postList.get(position).getpId();
                                FirebaseDatabase.getInstance().getReference().child("Report").child(rePost).removeValue();
                                new StyleableToast
                                        .Builder(context)
                                        .text("ReTweet Removed")
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .solidBackground() .gravity(0)
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }


                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            //CheckCommented
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(reId).child("Comments");
            Query query = ref.orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.child("id").getValue().toString();
                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            holder.comment.setVisibility(View.GONE);
                            holder.commented.setVisibility(View.VISIBLE);
                        }
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

            //CommentCount
            FirebaseDatabase.getInstance().getReference("Posts").child(reId).child("Comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() == 0){
                        holder.commentTv.setText("");
                    }else {
                        holder.commentTv.setText(String.valueOf(snapshot.getChildrenCount()));
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

        //Post details
        holder.tv_tweet_text.setLinkText(pText);
        holder.tv_tweet_text.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
            @Override
            public void onLinkClicked(int i, String s) {
                if (i == 1){
                    Intent intent = new Intent(context, SearchPostSystemActivityPage.class);
                    intent.putExtra("tag", s);
                    context.startActivity(intent);
                }else
                    if (i == 2){
                        String username = s.substring(1);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = ref.orderByChild("username").equalTo(username);
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
                                        .textColor(Color.WHITE) .gravity(0)
                                        .textBold()
                                        .length(2000)
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
        if (pType.equals("image")){
            String imgURi = postList.get(position).getImage();
            Glide.with(context).asBitmap().load(imgURi).into(holder.media);
            holder.media_layout.setVisibility(View.VISIBLE);
        }else
        if (pType.equals("video")){
            String vidURi = postList.get(position).getVideo();
            holder.play.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().load(vidURi).into(holder.media);
            holder.media_layout.setVisibility(View.VISIBLE);
            holder.views.setVisibility(View.VISIBLE);
            holder.view.setVisibility(View.VISIBLE);
        }


        //Share
        holder.tweet_action_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (pType) {
                    case "image": {

                        //Share Image
                        String imgURi = postList.get(position).getImage();
                        Uri uri = Uri.parse(imgURi);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.putExtra(Intent.EXTRA_TEXT, pText);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                        intent.setType("image/*");
                        context.startActivity(Intent.createChooser(intent, "Share Via"));

                        break;
                    }
                    case "video": {

                        //Share Video
                        String vidURi = postList.get(position).getVideo();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/*");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                        intent.putExtra(Intent.EXTRA_TEXT, pText + " Link: " + vidURi);
                        context.startActivity(Intent.createChooser(intent, "Share Via"));

                        break;
                    }
                    case "text": {

                        //Share Text
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/*");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                        intent.putExtra(Intent.EXTRA_TEXT, pText);
                        context.startActivity(Intent.createChooser(intent, "Share Via"));

                        break;
                    }
                }

            }
        });


    }

    //Download
    private void download(String pId, String image, String video) {
        if (!image.isEmpty()){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                downloadImage(context, "Image", ".png", DIRECTORY_DOWNLOADS, url);

            });

        }if (!video.isEmpty()){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(video);
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

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new StyleableToast
                                    .Builder(context)
                                    .text(databaseError.getMessage())
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .solidBackground()
                                    .gravity(0)
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
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
                                                .Builder(context)
                                                .text(databaseError.getMessage())
                                                .textColor(Color.WHITE)
                                                .gravity(0)
                                                .textBold()
                                                .length(2000)
                                                .solidBackground()
                                                .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                                .show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new StyleableToast
                                    .Builder(context)
                                    .text(databaseError.getMessage())
                                    .textColor(Color.WHITE)
                                    .gravity(0)
                                    .textBold()
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
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
                                        .Builder(context)
                                        .text(databaseError.getMessage())
                                        .textColor(Color.WHITE)
                                        .gravity(0)
                                        .textBold()
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    new StyleableToast
                            .Builder(context)
                            .text(databaseError.getMessage())
                            .textColor(Color.WHITE)
                            .gravity(0)
                            .textBold()
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            });

        }

    }

    //NumberReTweet
    private void numberReTweet(MyHolder holder, String postId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ReTweet")){
                    if (snapshot.child("ReTweet").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).addValueEventListener(new ValueEventListener() {
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
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .gravity(0)
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
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    //CheckReTweet
    private void checkReTweet(MyHolder holder, String postId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ReTweet")){
                    if (snapshot.child("ReTweet").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).addValueEventListener(new ValueEventListener() {
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
                        .length(2000)
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    //CheckLike
    private void checkLike(MyHolder holder, String postId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Likes")){
                    if (snapshot.child("Likes").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
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
                                        .length(2000)
                                        .solidBackground()
                                        .gravity(0)
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
                        .gravity(0)
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });

    }

    //NumberLike
    private void numberLike(MyHolder holder, String postId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Likes")){
                    if (snapshot.child("Likes").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
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
                                        .length(2000)
                                        .gravity(0)
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
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
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
        ImageView like,reTweet,comment,tweet_action_edit,more,views;
        ImageView liked,reTweeted,commented;

        //Text
        TextView likeTv,reTweetTv,commentTv,view;

        //RelativeLayout
        RelativeLayout media_layout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //User
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_username = itemView.findViewById(R.id.tv_username);
            mDp = itemView.findViewById(R.id.profile_photo);
            tv_reTweet = itemView.findViewById(R.id.tv_reTweet);

            //Post
            tv_tweet_text = itemView.findViewById(R.id.tv_tweet_text);
            media = itemView.findViewById(R.id.media);
            play = itemView.findViewById(R.id.play);

            //Buttons
            like = itemView.findViewById(R.id.like);
            reTweet = itemView.findViewById(R.id.reTweet);
            comment = itemView.findViewById(R.id.comment);
            tweet_action_edit = itemView.findViewById(R.id.tweet_action_edit);
            more = itemView.findViewById(R.id.more);

            //Buttons
            views = itemView.findViewById(R.id.views);
            liked = itemView.findViewById(R.id.liked);
            reTweeted = itemView.findViewById(R.id.reTweeted);
            commented = itemView.findViewById(R.id.commented);

            //Text
            likeTv = itemView.findViewById(R.id.likeTv);
            reTweetTv = itemView.findViewById(R.id.reTweetTv);
            commentTv = itemView.findViewById(R.id.commentTv);

            //RelativeLayout
            media_layout = itemView.findViewById(R.id.media_layout);
            view = itemView.findViewById(R.id.view);

        }
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

