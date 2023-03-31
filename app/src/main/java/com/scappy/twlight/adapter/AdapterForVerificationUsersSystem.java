package com.scappy.twlight.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.ViewMyProfileContentActivityPage;
import com.scappy.twlight.ActivitiesSystems.ContentUserProfileActivityPage;
import com.scappy.twlight.SystemModels.SystemModelForVerification;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


@SuppressWarnings("ALL")
public class AdapterForVerificationUsersSystem extends RecyclerView.Adapter<AdapterForVerificationUsersSystem.MyHolder>{

    final Context context;
    final List<SystemModelForVerification> userList;

    public AdapterForVerificationUsersSystem(Context context, List<SystemModelForVerification> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.verification_view_ui, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String hisUID = userList.get(position).getId();
        String gId = userList.get(position).getgId();
        String type = userList.get(position).getType();
        String refOne = userList.get(position).getRefOne();
        String refTwo = userList.get(position).getRefTwo();


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

        holder.id.setText(gId);
        holder.type.setText(type);
        holder.rOne.setText(refOne);
        holder.rTwo.setText(refTwo);

        holder.submit.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("Users").child(hisUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("verified","yes");
                FirebaseDatabase.getInstance().getReference().child("Users").child(hisUID).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                    FirebaseDatabase.getInstance().getReference().child("Verification").child(hisUID).removeValue();
                    new StyleableToast
                            .Builder(context)
                            .text("Request Accepted")
                            .textColor(Color.WHITE)
                            .textBold()
                            .gravity(0)
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
        holder.reject.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Verification").child(hisUID).removeValue();
            new StyleableToast
                    .Builder(context)
                    .text("Request reject")
                    .textColor(Color.WHITE)
                    .gravity(0)
                    .textBold()
                    .length(2000)
                    .solidBackground()
                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                    .show();
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(hisUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mName = snapshot.child("name").getValue().toString();
                String mUsername = snapshot.child("name").getValue().toString();
                String userImage = snapshot.child("name").getValue().toString();

                holder.name.setText(mName);
                holder.username.setText(mUsername);


                if (!userImage.isEmpty()){
                    Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.circleImageView2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnClickListener(v -> {
            if (hisUID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                intent.putExtra("id", hisUID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        @SuppressWarnings("CanBeFinal")
        TextView name, username,type,id,rOne,rTwo;
        Button submit,reject;
        CircleImageView circleImageView2;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            type = itemView.findViewById(R.id.type);
            id = itemView.findViewById(R.id.id);
            rOne = itemView.findViewById(R.id.rTwo);
            submit = itemView.findViewById(R.id.submit);
            circleImageView2 = itemView.findViewById(R.id.circleImageView2);
            rTwo = itemView.findViewById(R.id.rTwo);
            reject = itemView.findViewById(R.id.reject);

        }

    }
}
