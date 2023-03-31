package com.scappy.twlight.ActivitiesSystems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hendraanggrian.appcompat.widget.SocialEditText;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.PrivacyPick;
import com.scappy.twlight.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings({"ALL", "unused"})
public class CreatePostContentActivity extends AppCompatActivity implements PrivacyPick.SingleChoiceListener {

    private Uri image_uri, video_uri;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int PICK_VIDEO_REQUEST = 1;
    String type = "none";
    String privacyType;
    MediaController mediaController;

    ImageView postImage;
    VideoView postVideo;
    ImageView delete;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_page__appui);

        //Id
        ImageView back  = findViewById(R.id.imageView);
        CircleImageView dp = findViewById(R.id.circleImageView3);
        SocialEditText editText = findViewById(R.id.postText);
        Button postButton = findViewById(R.id.button2);
        ImageView uImage = findViewById(R.id.uImage);
        ImageView uVideo = findViewById(R.id.uVideo);
        ImageView uPrivacy = findViewById(R.id.privacy);
        delete = findViewById(R.id.delete);
        postImage = findViewById(R.id.image);
        postVideo = findViewById(R.id.video);
        pb = findViewById(R.id.pb);

        //HideControls
        mediaController = new MediaController(this);
        postVideo.setMediaController(mediaController);
        mediaController.setAnchorView(postVideo);
        MediaController ctrl = new MediaController(CreatePostContentActivity.this);
        ctrl.setVisibility(View.GONE);
        postVideo.setMediaController(ctrl);
        postVideo.setOnPreparedListener(mp -> mp.setLooping(true));

        //Post
        postButton.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            String postText = Objects.requireNonNull(editText.getText()).toString().trim();
            if (postText.isEmpty()){
                pb.setVisibility(View.GONE);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter Text")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }else {
                switch (type) {
                    case "none": {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("pId", timeStamp);
                        hashMap.put("text", postText);
                        hashMap.put("pViews", "0");
                        hashMap.put("type", "text");
                        hashMap.put("video", "no");
                        hashMap.put("image", "no");
                        hashMap.put("pComments", "0");
                        hashMap.put("reTweet", "");
                        hashMap.put("reId", "");
                        hashMap.put("privacy", "" + privacyType);
                        hashMap.put("pTime", timeStamp);
                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                        dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(aVoid -> {
                            editText.setText("");
                            pb.setVisibility(View.GONE);
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .gravity(0)
                                    .text("Tweet Posted")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        });
                        break;
                    }
                    case "image": {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "Post/" + "Post_" + timeStamp;
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putFile(image_uri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                    if (uriTask.isSuccessful()) {
                                        String timeStamp12 = String.valueOf(System.currentTimeMillis());
                                        HashMap<Object, String> hashMap = new HashMap<>();
                                        hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        hashMap.put("pId", timeStamp12);
                                        hashMap.put("text", postText);
                                        hashMap.put("pViews", "0");
                                        hashMap.put("type", "image");
                                        hashMap.put("video", "no");
                                        hashMap.put("reTweet", "");
                                        hashMap.put("reId", "");
                                        hashMap.put("image", downloadUri);
                                        hashMap.put("pComments", "0");
                                        hashMap.put("privacy", "" + privacyType);
                                        hashMap.put("pTime", timeStamp12);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                        dRef.child(timeStamp12).setValue(hashMap).addOnSuccessListener(aVoid -> {
                                            editText.setText("");
                                            pb.setVisibility(View.GONE);
                                            postImage.setImageURI(null);
                                            type = "none";
                                            delete.setVisibility(View.GONE);
                                            new StyleableToast
                                                    .Builder(getApplicationContext())
                                                    .text("Tweet Posted")
                                                    .textColor(Color.WHITE)
                                                    .textBold()
                                                    .gravity(0)
                                                    .length(2000)
                                                    .solidBackground()
                                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                    .show();
                                        });
                                    }
                                });

                        break;
                    }
                    case "video": {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "Post/" + "Post_" + timeStamp;
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putFile(video_uri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                    if (uriTask.isSuccessful()) {
                                        String timeStamp1 = String.valueOf(System.currentTimeMillis());
                                        HashMap<Object, String> hashMap = new HashMap<>();
                                        hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        hashMap.put("pId", timeStamp1);
                                        hashMap.put("text", postText);
                                        hashMap.put("pViews", "0");
                                        hashMap.put("type", "video");
                                        hashMap.put("video", downloadUri);
                                        hashMap.put("image", "no");
                                        hashMap.put("reTweet", "");
                                        hashMap.put("reId", "");
                                        hashMap.put("pComments", "0");
                                        hashMap.put("privacy", "" + privacyType);
                                        hashMap.put("pTime", timeStamp1);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                        dRef.child(timeStamp1).setValue(hashMap).addOnSuccessListener(aVoid -> {
                                            editText.setText("");
                                            postVideo.setVisibility(View.GONE);
                                            type = "none";
                                            pb.setVisibility(View.GONE);
                                            delete.setVisibility(View.GONE);
                                            new StyleableToast
                                                    .Builder(getApplicationContext())
                                                    .text("Tweet Posted")
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
                        break;
                    }
                }
            }
        });

        //OnClick
        back.setOnClickListener(v -> onBackPressed());
        uImage.setOnClickListener(v -> {
            switch (type) {
                case "none":
                    //Check Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_CODE);
                        } else {
                            pickImageFromGallery();
                        }
                    } else {
                        pickImageFromGallery();
                    }
                    break;
                case "image":
                    postImage.setImageURI(null);
                    delete.setVisibility(View.GONE);
                    //Check Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_CODE);
                        } else {
                            pickImageFromGallery();
                        }
                    } else {
                        pickImageFromGallery();
                    }
                    break;
                case "video":
                    postVideo.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    //Check Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_CODE);
                        } else {
                            pickImageFromGallery();
                        }
                    } else {
                        pickImageFromGallery();
                    }
                    break;
            }
        });
        uVideo.setOnClickListener(v -> {
            switch (type) {
                case "none":
                    //Check Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_CODE);
                        } else {
                            chooseVideo();
                        }
                    } else {
                        chooseVideo();
                    }
                    break;
                case "image":
                    postImage.setImageURI(null);
                    delete.setVisibility(View.GONE);
                    type = "none";
                    //Check Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_CODE);
                        } else {
                            chooseVideo();
                        }
                    } else {
                        chooseVideo();
                    }
                    break;
                case "video":
                    postVideo.setVisibility(View.GONE);
                    type = "none";
                    delete.setVisibility(View.GONE);
                    //Check Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_CODE);
                        } else {
                            chooseVideo();
                        }
                    } else {
                        chooseVideo();
                    }
                    break;
            }

        });
        uPrivacy.setOnClickListener(v -> {
            DialogFragment dialogFragment = new PrivacyPick();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "Single Choice Dialog");
        });
        delete.setOnClickListener(v -> {
            if (type.equals("image")){
                postImage.setImageURI(null);
                type = "none";
                delete.setVisibility(View.GONE);
            } else if (type.equals("video")) {
                postVideo.setVisibility(View.GONE);
                type = "none";
                delete.setVisibility(View.GONE);
            }
        });

        //SetDp
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mDp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();
                if (!mDp.isEmpty()){
                    Picasso.get().load(mDp).placeholder(R.drawable.avatar).into(dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(error.getMessage())
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

    //ImagePick
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //VideoPick
    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    //Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Storage Permission Allowed")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            } else {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Storage Permission is Required")
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

    //callBack
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE   && data != null && data.getData() != null){
            image_uri = data.getData();
            postImage.setImageURI(image_uri);
            type = "image";
            postImage.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            video_uri = data.getData();
            postVideo.setVideoURI(video_uri);
            postVideo.start();
            type = "video";
            postVideo.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        privacyType = list[position];
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}