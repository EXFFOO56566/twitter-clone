package com.scappy.twlight.ActivitiesSystems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.scappy.twlight.adapter.AdapterforChatSystem;
import com.scappy.twlight.SystemModels.SystemModelForChat;
import com.scappy.twlight.SystemModels.SystemModelForUsers;
import com.scappy.twlight.NotificationSystem.Data;
import com.scappy.twlight.NotificationSystem.Sender;
import com.scappy.twlight.NotificationSystem.Token;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class ChatActivityPage extends AppCompatActivity implements View.OnClickListener {

    String hisId;
    RecyclerView post;

    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    List<SystemModelForChat> nChat;
    AdapterforChatSystem adapterChat;


    private RequestQueue requestQueue;
    private boolean notify = false;

    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    ImageView more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page_appui);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());


        more = findViewById(R.id.more);
        more.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), more, Gravity.END);
            FirebaseDatabase.getInstance().getReference().child("userReport").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(hisId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        popupMenu.getMenu().add(Menu.NONE,0,0, "Report");
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
            popupMenu.getMenu().add(Menu.NONE,1,0, "Profile");
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == 0) {
                    FirebaseDatabase.getInstance().getReference().child("userReport").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(hisId).setValue(true);
                }

                if (id == 1) {
                    if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Intent intent = new Intent(getApplicationContext(), ViewMyProfileContentActivityPage.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ContentUserProfileActivityPage.class);
                        intent.putExtra("id", hisId);
                        startActivity(intent);
                    }
                }

                return false;
            });
            popupMenu.show();
        });

        CircleImageView mDp = findViewById(R.id.circleImageView4);
        TextView mName = findViewById(R.id.name);
        post = findViewById(R.id.post);
        post.setHasFixedSize(true);
         linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        post.setLayoutManager(linearLayoutManager);
        hisId = getIntent().getStringExtra("id");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ImageView online = findViewById(R.id.online);

        AVLoadingIndicatorView typingIndicator = findViewById(R.id.typing);

        mDp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContentUserProfileActivityPage.class);
            intent.putExtra("id", hisId);
            startActivity(intent);
        });


             mName.setOnClickListener(v -> {
                 Intent intent = new Intent(getApplicationContext(), ContentUserProfileActivityPage.class);
                 intent.putExtra("id", hisId);
                 startActivity(intent);
             });

             ImageView verified = findViewById(R.id.verified);

        FirebaseDatabase.getInstance().getReference().child("Users").child(hisId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dp = snapshot.child("photo").getValue().toString();
                if (!dp.isEmpty()){
                    Picasso.get().load(dp).placeholder(R.drawable.avatar).into(mDp);
                }
                String mVerified = snapshot.child("verified").getValue().toString();
               String name = snapshot.child("name").getValue().toString();
                mName.setText(name);
                String status = snapshot.child("status").getValue().toString();
                if (status.equals("online")){
                    online.setVisibility(View.VISIBLE);
                }
                String typing = snapshot.child("typingTo").getValue().toString();
                if (typing.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    typingIndicator.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.GONE);
                }else {
                    typingIndicator.setVisibility(View.GONE);
                    mName.setVisibility(View.VISIBLE);
                }
                if (mVerified.isEmpty()){
                    verified.setVisibility(View.GONE);
                }else {
                    verified.setVisibility(View.VISIBLE);

                    if (typing.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        verified.setVisibility(View.GONE);
                    }else {
                        verified.setVisibility(View.VISIBLE);
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

        EditText postText = findViewById(R.id.postText);
        ImageView send = findViewById(R.id.send);
        send.setOnClickListener(v -> {
            String msg = postText.getText().toString();
            if (msg.isEmpty()){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Type a message")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }else {
                String timeStamp = ""+System.currentTimeMillis();
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("receiver", hisId);
                hashMap.put("msg", msg);
                hashMap.put("isSeen", false);
                hashMap.put("type", "text");
                hashMap.put("timeStamp", timeStamp);
                databaseReference1.child("Chats").push().setValue(hashMap);

                postText.setText("");
                notify = true;
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                        if (notify){
                            sendNotification(hisId, user.getName(), msg);

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0){
                    checkTypingStatus("noOne");
                }else {
                    checkTypingStatus(hisId);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageView add = findViewById(R.id.add);
        add.setOnClickListener(v -> bottomSheetDialog.show());

        createBottomSheetDialog();
        seenMessage();
        readMessage();

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(hisId);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
                        .textColor(Color.WHITE)
                        .gravity(0)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef2.child("id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });



    }

    private void seenMessage(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SystemModelForChat systemModelForChat = snapshot.getValue(SystemModelForChat.class);
                    if (Objects.requireNonNull(systemModelForChat).getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && systemModelForChat.getSender().equals(hisId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
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
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });



    }

    private void readMessage(){
        nChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SystemModelForChat chat = snapshot.getValue(SystemModelForChat.class);
                    if (Objects.requireNonNull(chat).getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && chat.getSender().equals(hisId) ||
                            chat.getReceiver().equals(hisId) && chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        nChat.add(chat);
                    }
                    progressBar.setVisibility(View.GONE);
                    adapterChat = new AdapterforChatSystem(ChatActivityPage.this, nChat);
                    post.setAdapter(adapterChat);
                    post.smoothScrollToPosition(post.getAdapter().getItemCount());
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

    private void checkOnlineStatus(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }
    private void checkTypingStatus(String typing){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
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
                        .gravity(0)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            } else {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Storage permission is required")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
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
                    .text("Please wait, Sending...")
                    .textColor(Color.WHITE)
                    .textBold()
                    .length(2000)
                    .solidBackground()
                    .gravity(0)
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
                    .text("Please wait, Sending...")
                    .textColor(Color.WHITE)
                    .textBold()
                    .length(2000)
                    .solidBackground()
                    .gravity(0)
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .show();
            progressBar.setVisibility(View.VISIBLE);
            bottomSheetDialog.cancel();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImage(Uri image_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "ChatImages/"+"chat_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("receiver", hisId);
                hashMap.put("msg", downloadUri);
                hashMap.put("isSeen", false);
                hashMap.put("type", "image");
                hashMap.put("timeStamp", timeStamp);
                databaseReference1.child("Chats").push().setValue(hashMap).addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    notify = true;
                                       DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                        if (notify){
                            sendNotification(hisId, user.getName(), "Sent a image");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Image Sent")
                            .textColor(Color.WHITE)
                            .textBold()
                            .length(2000)
                            .gravity(0)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                });


            }
        });
    }

    private void sendVideo(Uri video_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "ChatImages/"+"chat_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(video_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("receiver", hisId);
                hashMap.put("msg", downloadUri);
                hashMap.put("isSeen", false);
                hashMap.put("type", "video");
                hashMap.put("timeStamp", timeStamp);
                databaseReference1.child("Chats").push().setValue(hashMap).addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    notify = true;
                                       DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SystemModelForUsers user = dataSnapshot.getValue(SystemModelForUsers.class);
                        if (notify){
                            sendNotification(hisId, user.getName(), "Sent a video");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Video Sent")
                            .textColor(Color.WHITE)
                            .gravity(0)
                            .textBold()
                            .length(2000)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
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
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), name + " : " + message, "New Message", hisId, R.drawable.ic_logo);
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


}