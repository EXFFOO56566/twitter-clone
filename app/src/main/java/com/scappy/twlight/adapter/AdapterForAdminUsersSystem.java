package com.scappy.twlight.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.scappy.twlight.SystemModels.SystemModelForUsers;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;


@SuppressWarnings("Convert2Lambda")
public class AdapterForAdminUsersSystem extends RecyclerView.Adapter<AdapterForAdminUsersSystem.MyHolder>{

    final Context context;
    final List<SystemModelForUsers> userList;

    public AdapterForAdminUsersSystem(Context context, List<SystemModelForUsers> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.userview_ui, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        final String userName = userList.get(position).getName();
        String userUsername = userList.get(position).getUsername();
        String mVerified = userList.get(position).getVerified();

        holder.mName.setText(userName);
        holder.mUsername.setText(userUsername);


        if (!userImage.isEmpty()){
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.avatar);
        }

        if (mVerified.isEmpty()){
            holder.verified.setVisibility(View.GONE);
        }else {
            holder.verified.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(v -> {
            if (hisUID.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                Intent intent = new Intent(context, ViewMyProfileContentActivityPage.class);
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, ContentUserProfileActivityPage.class);
                intent.putExtra("id", hisUID);
                context.startActivity(intent);
            }

        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.itemView, Gravity.END);
                FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            popupMenu.getMenu().add(Menu.NONE,3,0, "Remove Ban");
                        }else {
                            popupMenu.getMenu().add(Menu.NONE,1,0, "Ban user");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                popupMenu.getMenu().add(Menu.NONE,2,0, "Delete from report");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == 1){
                            FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).setValue(true);
                            new StyleableToast
                                    .Builder(context)
                                    .text("User Banned")
                                    .gravity(0)
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }   else  if (id == 2){
                            FirebaseDatabase.getInstance().getReference().child("userReport").child(hisUID).removeValue();
                            new StyleableToast
                                    .Builder(context)
                                    .text("User Removed")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .gravity(0)
                                    .solidBackground()
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                    .show();


                        }else if (id == 3 ){
                            FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).removeValue();
                            new StyleableToast
                                    .Builder(context)
                                    .text("User Banned")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .gravity(0)
                                    .solidBackground()
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }


                        return false;
                    }
                });
                popupMenu.show();

            }
        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final ImageView verified;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.circleImageView2);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);
            verified = itemView.findViewById(R.id.verified);

        }

    }
}
