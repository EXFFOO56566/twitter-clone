package com.scappy.twlight.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.AddContantActivityPage;
import com.scappy.twlight.ActivitiesSystems.StoryViewSystemActivityPage;
import com.scappy.twlight.SystemModels.SystemModelForStory;
import com.scappy.twlight.SystemModels.SystemModelForUsers;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class AdapterForStoriesSystem extends RecyclerView.Adapter<AdapterForStoriesSystem.ViewHolder> {

    private final Context context;
    private final List<SystemModelForStory>storyList;

    public AdapterForStoriesSystem(Context context, List<SystemModelForStory> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
     if (i == 0){
         View view = LayoutInflater.from(context).inflate(R.layout.add_story, parent, false);
         return new ViewHolder(view);
     }else {
         View view = LayoutInflater.from(context).inflate(R.layout.story_box_ui, parent, false);
         return new ViewHolder(view);
     }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        SystemModelForStory story = storyList.get(position);
        userInfo(viewHolder, story.getUserid(), position);
        if (viewHolder.getAdapterPosition() != 0){
            seenStory(viewHolder, story.getUserid());

        }
        if (viewHolder.getAdapterPosition() == 0){
            myStory(viewHolder.addstory_text, viewHolder.story_plus, false);

        }

        viewHolder.itemView.setOnClickListener(v -> {
            if (viewHolder.getAdapterPosition() == 0){
                myStory(viewHolder.addstory_text, viewHolder.story_plus, true);

            }else {
                Intent intent = new Intent(context, StoryViewSystemActivityPage.class);
                intent.putExtra("userid", story.getUserid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

       SelectableRoundedImageView story_photo;
        public final ImageView story_plus;
        public final SelectableRoundedImageView story_photo_seen;
        public final TextView story_username;
        public final TextView addstory_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.addstory_text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }
        return 1;
    }

    private void userInfo (ViewHolder viewHolder, String userId, int pos){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SystemModelForUsers systemModelForUsers = snapshot.getValue(SystemModelForUsers.class);
                if (!systemModelForUsers.getPhoto().isEmpty()){
                    Glide.with(context).asBitmap().load(Objects.requireNonNull(systemModelForUsers).getPhoto()).centerCrop().into(viewHolder.story_photo);
                }else {
                    Glide.with(context).asBitmap().load(R.drawable.avatar).centerCrop().into(viewHolder.story_photo);
                }
                if (pos != 0){
                    if (!systemModelForUsers.getPhoto().isEmpty()){
                        Glide.with(context).asBitmap().load(systemModelForUsers.getPhoto()).centerCrop().into(viewHolder.story_photo_seen);
                    }else {
                        Glide.with(context).asBitmap().load(R.drawable.avatar).centerCrop().into(viewHolder.story_photo_seen);
                    }
                    viewHolder.story_username.setText(systemModelForUsers.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void myStory(TextView textView, ImageView imageView, boolean click){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    SystemModelForStory story = snapshot1.getValue(SystemModelForStory.class);
                    if (timecurrent > Objects.requireNonNull(story).getTimestart() && timecurrent < story.getTimeend()){
                        count++;
                    }
                }

                if (click){
                    if (count > 0){
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View story", (dialog, which) -> {
                            Intent intent = new Intent(context, StoryViewSystemActivityPage.class);
                            intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            context.startActivity(intent);
                            dialog.dismiss();
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add story", (dialog, which) -> {
                            Intent intent = new Intent(context , AddContantActivityPage.class);
                            context.startActivity(intent);
                            dialog.dismiss();
                        });
                        alertDialog.show();
                    }else {
                        Intent intent = new Intent(context , AddContantActivityPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }else {
                 if (count > 0){
                     textView.setText("My Story");
                     imageView.setVisibility(View.GONE);
                 }else {
                     textView.setText("Add");
                     imageView.setVisibility(View.VISIBLE);
                 }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void seenStory(ViewHolder viewHolder, String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
           if (!snapshot1.child("views").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists() && System.currentTimeMillis() < Objects.requireNonNull(snapshot1.getValue(SystemModelForStory.class)).getTimeend()){
               i++;
           }
                }
                if (i > 0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
