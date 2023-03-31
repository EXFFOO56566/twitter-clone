package com.scappy.twlight.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;
import com.scappy.twlight.R;
import com.scappy.twlight.LiveBroadcastSystem.activities.GoAudienceActivityPage;
import com.scappy.twlight.SystemModels.SystemModelForLiveBroadcast;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ALL")
public class AdapterForLiveBrodcastSystem extends RecyclerView.Adapter<AdapterForLiveBrodcastSystem.MyHolder>{

    final Context context;
    final List<SystemModelForLiveBroadcast> systemModelForLiveBroadcasts;
    String mUsername;

    public AdapterForLiveBrodcastSystem(Context context, List<SystemModelForLiveBroadcast> systemModelForLiveBroadcasts) {
        this.context = context;
        this.systemModelForLiveBroadcasts = systemModelForLiveBroadcasts;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.live_box_ui, parent, false);
        return new MyHolder(view);
    }

    @SuppressWarnings("Convert2Lambda")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String hisUID = systemModelForLiveBroadcasts.get(position).getUserid();
        String room = systemModelForLiveBroadcasts.get(position).getRoom();

        FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).addValueEventListener(new ValueEventListener() {
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

        FirebaseDatabase.getInstance().getReference().child("Users").child(hisUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 mUsername = snapshot.child("username").getValue().toString();
                String dp = snapshot.child("photo").getValue().toString();
                if (!dp.isEmpty()){
                    Glide.with(context).asBitmap().load(dp).centerCrop().into(holder.live_photo);
                }else {
                    Glide.with(context).asBitmap().load(R.drawable.avatar).centerCrop().into(holder.live_photo);
                }
                holder.story_username.setText(mUsername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Live").child(room).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                String timeStamp = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("ChatId", timeStamp);
                hashMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("msg", mUsername + " has joined");
                FirebaseDatabase.getInstance().getReference().child("Live").child(room).child("Chats").child(timeStamp).setValue(hashMap);
                Intent intent = new Intent(context, GoAudienceActivityPage.class);
                intent.putExtra("room", room);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return systemModelForLiveBroadcasts.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        SelectableRoundedImageView live_photo;
        TextView story_username;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            live_photo = itemView.findViewById(R.id.live_photo);
            story_username = itemView.findViewById(R.id.story_username);
        }
    }
}
