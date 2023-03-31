package com.scappy.twlight.adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.joooonho.SelectableRoundedImageView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.ActivitiesSystems.MediaSystemActivityPage;
import com.scappy.twlight.ActivitiesSystems.ViewMyProfileContentActivityPage;
import com.scappy.twlight.ActivitiesSystems.SearchPostSystemActivityPage;
import com.scappy.twlight.ActivitiesSystems.ContentStoryChatViewSystemActivityPage;
import com.scappy.twlight.ActivitiesSystems.ContentUserProfileActivityPage;
import com.scappy.twlight.SystemModels.SystemModelForChat;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@SuppressWarnings({"SameParameterValue", "Convert2Lambda", "DuplicateBranchesInSwitch"})
public class AdapterforChatSystem extends RecyclerView.Adapter<AdapterforChatSystem.MyHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context context;
    private final List<SystemModelForChat> systemModelForChats;

    FirebaseUser firebaseUser;

    public AdapterforChatSystem(Context context, List<SystemModelForChat> systemModelForChats) {
        this.context = context;
        this.systemModelForChats = systemModelForChats;
    }

    @NonNull
    @Override
    public AdapterforChatSystem.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)  {
       if (viewType == MSG_TYPE_RIGHT){
           View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);

           return new MyHolder(view);
       }
        View view = LayoutInflater.from(context).inflate(R.layout.rowchat_l_ui, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterforChatSystem.MyHolder holder, int position) {

        SystemModelForChat mChat = systemModelForChats.get(position);

        String msg = systemModelForChats.get(position).getMsg();
        String type = systemModelForChats.get(position).getType();
         String hisId = systemModelForChats.get(position).getReceiver();

        switch (type) {
            case "text":
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setLinkText(msg);
                holder.media.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);

                holder.text.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
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
                                            String id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                            if (id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
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
                                                .textBold()
                                                .length(2000)
                                                .solidBackground()
                                                .gravity(0)
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
                                            .gravity(0)
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

                break;
            case "image":
                holder.text.setVisibility(View.GONE);
                holder.media.setVisibility(View.VISIBLE);
                holder.play.setVisibility(View.GONE);
                Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.media);
                break;
            case "video":
                holder.text.setVisibility(View.GONE);
                holder.play.setVisibility(View.VISIBLE);
                holder.media.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.media);
                break;
            case "story":
                holder.text.setVisibility(View.VISIBLE);
                holder.play.setVisibility(View.GONE);
                holder.text.setLinkText(msg);

                holder.text.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
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
                                            String id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                            if (id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
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
                                                .textBold()
                                                .length(2000)
                                                .gravity(0)
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
                                            .gravity(0)
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

                holder.media.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(systemModelForChats.get(position).getTimeStamp()).into(holder.media);
                break;
        }

        if (position == systemModelForChats.size()-1){
            if (mChat.isIsSeen()) {
               holder.seen.setText("Seen");
            }else {
                holder.seen.setText("Delivered");
            }
        }else {
            holder.seen.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(systemModelForChats.get(position).getSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mDP = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();
                if (!mDP.isEmpty()){
                    Picasso.get().load(mDP).placeholder(R.drawable.avatar).into(holder.dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(context)
                        .text("Video Sent")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.itemView, Gravity.END);
                    popupMenu.getMenu().add(Menu.NONE,0,0, "Delete");

                popupMenu.getMenu().add(Menu.NONE,1,0, "Report");
                if (type.equals("text")){
                    popupMenu.getMenu().add(Menu.NONE,2,0, "Copy");
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == 0){

                            String cId = systemModelForChats.get(position).getTimeStamp();
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
                            Query query = dbRef.orderByChild("timeStamp").equalTo(cId);
                            query.addValueEventListener(new ValueEventListener() {
                                @SuppressWarnings("DuplicateBranchesInSwitch")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        if (Objects.requireNonNull(ds.child("sender").getValue()).equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                            switch (type) {
                                                case "text":
                                                    ds.getRef().removeValue();
                                                    break;
                                                case "video": {
                                                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(msg);
                                                    picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            ds.getRef().removeValue();
                                                        }
                                                    });

                                                    break;
                                                }
                                                case "image": {
                                                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(msg);
                                                    picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            ds.getRef().removeValue();
                                                        }
                                                    });
                                                    break;
                                                }
                                            }

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

                        if (id == 1){
                            String cId = systemModelForChats.get(position).getTimeStamp();
                            FirebaseDatabase.getInstance().getReference().child("ChatReport").child(cId).setValue(true);
                            new StyleableToast
                                    .Builder(context)
                                    .text("Reported")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .solidBackground()
                                    .gravity(0)
                                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }else if (id == 2){
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip =  ClipData.newPlainText("text", msg);
                            assert clipboard != null;
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context, "Message Copied",Toast.LENGTH_SHORT).show();
                        }

                        return false;
                    }
                });
                popupMenu.show();

                return false;
            }
        });

        holder.media.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("DuplicateBranchesInSwitch")
            @Override
            public void onClick(View v) {
                switch (type) {
                    case "image": {
                        Intent intent = new Intent(context, MediaSystemActivityPage.class);
                        intent.putExtra("type", type);
                        intent.putExtra("uri", msg);
                        context.startActivity(intent);
                        break;
                    }
                    case "video": {
                        Intent intent = new Intent(context, MediaSystemActivityPage.class);
                        intent.putExtra("type", type);
                        intent.putExtra("uri", msg);
                        context.startActivity(intent);
                        break;
                    }
                    case "story": {
                        Intent intent = new Intent(context, ContentStoryChatViewSystemActivityPage.class);
                        intent.putExtra("userid", hisId);
                        context.startActivity(intent);
                        break;
                    }
                }
            }
        });

        holder.media.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (type.equals("image")){
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(msg);
                    picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = uri.toString();
                        downloadImage(context, "Image", ".png", DIRECTORY_DOWNLOADS, url);
                    });
                }else
                if (type.equals("video")){
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(msg);
                    picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = uri.toString();
                        downloadVideo(context, "Video", ".mp4", DIRECTORY_DOWNLOADS, url);
                    });
                }
                return false;
            }
        });

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
        return systemModelForChats.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        public final SocialTextView text;
        public final TextView seen;
        public final ImageView play;
        public final SelectableRoundedImageView media;
        public final CircleImageView dp;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            seen = itemView.findViewById(R.id.seen);
            play = itemView.findViewById(R.id.play);
            text = itemView.findViewById(R.id.textView4);
            media = itemView.findViewById(R.id.media);
            dp = itemView.findViewById(R.id.circleImageView5);
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (systemModelForChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}

