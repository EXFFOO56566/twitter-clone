package com.scappy.twlight.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.ChatActivityPage;
import com.scappy.twlight.SystemModels.SystemModelForUsers;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterForChatListSystem extends RecyclerView.Adapter<AdapterForChatListSystem.MyHolder> {

    final Context context;
    final List<SystemModelForUsers> userList;
    private final HashMap<String, String> lastMessageMap;

    public AdapterForChatListSystem(Context context, List<SystemModelForUsers> userList) {
        this.context = context;
        this.userList = userList;
     lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.userview_ui, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String hisUid = userList.get(position).getId();
        String dp = userList.get(position).getPhoto();
        String name = userList.get(position).getName();

        FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUid).addValueEventListener(new ValueEventListener() {
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

        FirebaseDatabase.getInstance().getReference().child("Users").child(hisUid).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String lastMessage = lastMessageMap.get(hisUid);

                    holder.mName.setText(name);

                    if (lastMessage == null || lastMessage.equals("default")){
                        holder.message.setText("No messages");
                    }else {
                        holder.message.setText(lastMessage);
                    }

                    try{
                        Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.mDp);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.avatar).into(holder.mDp);
                    }

                    String mVerified = Objects.requireNonNull(snapshot.child("verified").getValue()).toString();

                    if (mVerified.isEmpty()){
                        holder.verified.setVisibility(View.GONE);
                    }else {
                        holder.verified.setVisibility(View.VISIBLE);
                    }


                    if (userList.get(position).getStatus().equals("online")){
                        holder.status.setVisibility(View.VISIBLE);
                    }else {
                        holder.status.setVisibility(View.GONE);
                    }

                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ChatActivityPage.class);
                        intent.putExtra("id", hisUid);
                        context.startActivity(intent);
                    });
                }else {
                    holder.itemView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView mDp;
        final RelativeLayout status;
        final TextView mName;
        final TextView message;
        final ImageView verified;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.circleImageView2);
            status = itemView.findViewById(R.id.relativeLayout);
            mName = itemView.findViewById(R.id.name);
            verified = itemView.findViewById(R.id.verified);
            message = itemView.findViewById(R.id.username);
        }
    }

}
