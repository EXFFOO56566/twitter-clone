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

@SuppressWarnings("StatementWithEmptyBody")
public class EditPostSystemActivityPage extends AppCompatActivity implements PrivacyPick.SingleChoiceListener {

    private Uri image_uri, video_uri;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int PICK_VIDEO_REQUEST = 1;
    String type = "none";
    String privacyType;
    MediaController mediaController;
    String updateType = "none";
    ImageView postImage;
    VideoView postVideo;
    ImageView delete;
    ProgressBar pb;
    String postId;
    String pVideo;
    String pImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_page);

        postId = getIntent().getStringExtra("id");

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
        MediaController ctrl = new MediaController(EditPostSystemActivityPage.this);
        ctrl.setVisibility(View.GONE);
        postVideo.setMediaController(ctrl);
        postVideo.setOnPreparedListener(mp -> mp.setLooping(true));

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


        //OnClick
        back.setOnClickListener(v -> onBackPressed());

        //PostDetails
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pText = Objects.requireNonNull(snapshot.child("text").getValue()).toString();
                String pType = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                 pVideo = Objects.requireNonNull(snapshot.child("video").getValue()).toString();
                 pImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                editText.setText(pText);
                type = pType;
                if (pType.equals("image")){
                    postImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(pImage).into(postImage);
                }else  if (pType.equals("video")){
                    postVideo.setVisibility(View.VISIBLE);
                    Uri vineUri = Uri.parse(pVideo);
                    postVideo.setVideoURI(vineUri);
                    postVideo.start();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postButton.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            String postText = Objects.requireNonNull(editText.getText()).toString().trim();
            if (postText.isEmpty()){
                pb.setVisibility(View.GONE);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter Text")
                        .gravity(0)
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }else {
                if (updateType.equals("none") && type.equals("text")){
                  //T2T
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("text", postText);
                    hashMap.put("type", "text");
                    hashMap.put("video", "no");
                    hashMap.put("image", "no");
                    hashMap.put("privacy", ""+privacyType);
                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                    dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                        editText.setText("");
                        pb.setVisibility(View.GONE);
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("Tweet Updated")
                                .textColor(Color.WHITE)
                                .textBold()
                                .length(2000)
                                .solidBackground()
                                .gravity(0)
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                    });
               } else
                if (updateType.equals("none") && type.equals("image")){
                  //I2T
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
                    picRef.delete().addOnSuccessListener(aVoid -> {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("text", postText);
                        hashMap.put("type", "text");
                        hashMap.put("video", "no");
                        hashMap.put("image", "no");
                        hashMap.put("privacy", "" + privacyType);
                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                        dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid16 -> {
                            editText.setText("");
                            pb.setVisibility(View.GONE);
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Tweet Updated")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .gravity(0)
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        });
                    });
               }
               else
                if (updateType.equals("none") && type.equals("video")){
                  //V2T
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pVideo);
                    picRef.delete().addOnSuccessListener(aVoid -> {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("text", postText);
                        hashMap.put("type", "text");
                        hashMap.put("video", "no");
                        hashMap.put("image", "no");
                        hashMap.put("privacy", "" + privacyType);
                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                        dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid15 -> {
                            editText.setText("");
                            pb.setVisibility(View.GONE);
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Tweet Updated")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .gravity(0)
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        });
                    });
               }
               else
                if (updateType.equals("image") && type.equals("text")){
                  //T2I
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    String filePathAndName = "Post/" + "Post_" + timeStamp;
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                    ref.putFile(image_uri)
                            .addOnSuccessListener(taskSnapshot -> {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                if (uriTask.isSuccessful()) {
                                    String timeStamp16 = String.valueOf(System.currentTimeMillis());
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("text", postText);
                                    hashMap.put("type", "image");
                                    hashMap.put("video", "no");
                                    hashMap.put("image", downloadUri);
                                    hashMap.put("privacy", "" + privacyType);
                                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                    dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                                        editText.setText("");
                                        pb.setVisibility(View.GONE);
                                        postImage.setImageURI(null);
                                        updateType = "none";
                                        delete.setVisibility(View.GONE);
                                        new StyleableToast
                                                .Builder(getApplicationContext())
                                                .text("Tweet Updated")
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

                }else
                if (updateType.equals("image") && type.equals("image")){
                    //I2I
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
                    picRef.delete().addOnSuccessListener(aVoid -> {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "Post/" + "Post_" + timeStamp;
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putFile(image_uri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                    if (uriTask.isSuccessful()) {
                                        String timeStamp15 = String.valueOf(System.currentTimeMillis());
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("text", postText);
                                        hashMap.put("type", "image");
                                        hashMap.put("video", "no");
                                        hashMap.put("image", downloadUri);
                                        hashMap.put("privacy", "" + privacyType);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                        dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid14 -> {
                                            editText.setText("");
                                            pb.setVisibility(View.GONE);
                                            postImage.setImageURI(null);
                                            updateType = "none";
                                            delete.setVisibility(View.GONE);
                                            new StyleableToast
                                                    .Builder(getApplicationContext())
                                                    .text("Tweet Updated")
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
                    });
                }
                else
                if (updateType.equals("image") && type.equals("video")){
                    //V2I
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pVideo);
                    picRef.delete().addOnSuccessListener(aVoid -> {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "Post/" + "Post_" + timeStamp;
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putFile(image_uri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                    if (uriTask.isSuccessful()) {
                                        String timeStamp14 = String.valueOf(System.currentTimeMillis());
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("text", postText);
                                        hashMap.put("type", "image");
                                        hashMap.put("video", "no");
                                        hashMap.put("image", downloadUri);
                                        hashMap.put("privacy", "" + privacyType);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                        dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid13 -> {
                                            editText.setText("");
                                            pb.setVisibility(View.GONE);
                                            postImage.setImageURI(null);
                                            updateType = "none";
                                            delete.setVisibility(View.GONE);
                                            new StyleableToast
                                                    .Builder(getApplicationContext())
                                                    .text("Tweet Updated")
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
                    });
                }
                else
                if (updateType.equals("video") && type.equals("text")){
                    //T2V
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    String filePathAndName = "Post/" + "Post_" + timeStamp;
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                    ref.putFile(video_uri)
                            .addOnSuccessListener(taskSnapshot -> {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                if (uriTask.isSuccessful()) {
                                    String timeStamp13 = String.valueOf(System.currentTimeMillis());
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("text", postText);
                                    hashMap.put("type", "video");
                                    hashMap.put("video", downloadUri);
                                    hashMap.put("image", "no");
                                    hashMap.put("privacy", "" + privacyType);
                                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                    dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                                        editText.setText("");
                                        pb.setVisibility(View.GONE);
                                        postImage.setImageURI(null);
                                        updateType = "none";
                                        delete.setVisibility(View.GONE);
                                        new StyleableToast
                                                .Builder(getApplicationContext())
                                                .text("Tweet Updated")
                                                .textColor(Color.WHITE)
                                                .textBold()
                                                .length(2000)
                                                .solidBackground()
                                                .gravity(0)
                                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                .show();
                                    });
                                }
                            });
                }
                else
                if (updateType.equals("video") && type.equals("image")){
                    //I2V
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
                    picRef.delete().addOnSuccessListener(aVoid -> {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "Post/" + "Post_" + timeStamp;
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putFile(video_uri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                    if (uriTask.isSuccessful()) {
                                        String timeStamp12 = String.valueOf(System.currentTimeMillis());
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("text", postText);
                                        hashMap.put("type", "video");
                                        hashMap.put("video", downloadUri);
                                        hashMap.put("image", "no");
                                        hashMap.put("privacy", "" + privacyType);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                        dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid12 -> {
                                            editText.setText("");
                                            pb.setVisibility(View.GONE);
                                            postImage.setImageURI(null);
                                            updateType = "none";
                                            delete.setVisibility(View.GONE);
                                            new StyleableToast
                                                    .Builder(getApplicationContext())
                                                    .text("Tweet Updated")
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
                    });
                }
                else
                if (updateType.equals("video") && type.equals("video")){
                    //V2V
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pVideo);
                    picRef.delete().addOnSuccessListener(aVoid -> {
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
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("text", postText);
                                        hashMap.put("type", "video");
                                        hashMap.put("video", downloadUri);
                                        hashMap.put("image", "no");
                                        hashMap.put("privacy", "" + privacyType);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                        dRef.child(postId).updateChildren(hashMap).addOnSuccessListener(aVoid1 -> {
                                            editText.setText("");
                                            pb.setVisibility(View.GONE);
                                            postImage.setImageURI(null);
                                            updateType = "none";
                                            delete.setVisibility(View.GONE);
                                            new StyleableToast
                                                    .Builder(getApplicationContext())
                                                    .text("Tweet Updated")
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
                    });
                }
            }
        });

        uImage.setOnClickListener(v -> {
            switch (updateType) {
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
            switch (updateType) {
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
                    updateType = "none";
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
                    updateType = "none";
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
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            } else {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Storage Permission is Required")
                        .textColor(Color.WHITE)
                        .gravity(0)
                        .textBold()
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
            updateType = "image";
            postImage.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            video_uri = data.getData();
            postVideo.setVideoURI(video_uri);
            postVideo.start();
            updateType = "video";
            postVideo.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        privacyType = list[position];
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}