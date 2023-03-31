package com.scappy.twlight.ActivitiesSystems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class EditProfileSystemActivityPage extends AppCompatActivity  implements View.OnClickListener{

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int COVER_PICK_CODE = 1002;
    private static final int PERMISSION_CODE = 1001;
    private Uri image_uri,cover_uri;
    CircleImageView dp;
    ProgressBar pb;
    ImageView cover;

    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    ConstraintLayout constraintLayout5,deleteC;
    BottomSheetDialog bottomSheetDialogCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);

        //Id
        cover = findViewById(R.id.imageView2);
        ImageView back = findViewById(R.id.back);
        dp = findViewById(R.id.circleImageView);
        EditText name = findViewById(R.id.name);
        EditText username = findViewById(R.id.username);
        EditText bio = findViewById(R.id.bio);
        EditText link = findViewById(R.id.link);
        EditText location = findViewById(R.id.location);
        Button save = findViewById(R.id.save);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);

        back.setOnClickListener(v -> onBackPressed());

        //SetDetails
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pb.setVisibility(View.GONE);
                String mName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String mUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String mBio = Objects.requireNonNull(snapshot.child("bio").getValue()).toString();
                String mLink = Objects.requireNonNull(snapshot.child("link").getValue()).toString();
                String mLocation = Objects.requireNonNull(snapshot.child("location").getValue()).toString();
                String mCover = Objects.requireNonNull(snapshot.child("cover").getValue()).toString();
                String mDp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();

                name.setText(mName);
                username.setText(mUsername);
                bio.setText(mBio);
                link.setText(mLink);
                location.setText(mLocation);

                if (!mDp.isEmpty()){
                    Picasso.get().load(mDp).placeholder(R.drawable.avatar).into(dp);
                }

                if (mCover.isEmpty()){
                    Picasso.get().load(R.drawable.cover).into(cover);
                }else {
                    Picasso.get().load(mCover).placeholder(R.drawable.cover).into(cover);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pb.setVisibility(View.GONE);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter Your Username")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();

            }
        });

        //SaveClick
        save.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            String mName = name.getText().toString().trim();
            String mUsername = username.getText().toString().trim();
            String mBio = bio.getText().toString().trim();
            String mLink = link.getText().toString().trim();
            String mLocation = location.getText().toString().trim();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", mName);
            hashMap.put("username", mUsername);
            hashMap.put("bio", mBio);
            hashMap.put("location",mLocation);
            hashMap.put("link",mLink);

            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);

            pb.setVisibility(View.GONE);
            new StyleableToast
                    .Builder(getApplicationContext())
                    .text("Profile Details Updated")
                    .textColor(Color.WHITE)
                    .textBold()
                    .length(2000)
                    .solidBackground()
                    .gravity(0)
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .show();

        });

        //ProfilePhotoChange
        dp.setOnClickListener(v -> bottomSheetDialog.show());

        //CoverPhotoChange
        cover.setOnClickListener(v -> bottomSheetDialogCover.show());

        createBottomSheetDialog();
        createBottomSheetDialogCover();

    }

    private void createBottomSheetDialogCover() {
        if (bottomSheetDialogCover == null){
            View view = LayoutInflater.from(this).inflate(R.layout.edit_cover_ui, null);
            constraintLayout5 = view.findViewById(R.id.constraintLayout5);
            deleteC = view.findViewById(R.id.deleteC);
            constraintLayout5.setOnClickListener(this);
            deleteC.setOnClickListener(this);
            bottomSheetDialogCover = new BottomSheetDialog(this);
            bottomSheetDialogCover.setContentView(view);
        }
    }

    //ImagePick
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //CoverPick
    private void pickCoverFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, COVER_PICK_CODE);
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
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        }
    }

    //callBack
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null && data.getData() != null) {
            image_uri = data.getData();
            dp.setImageURI(image_uri);
            imageUpload();
            pb.setVisibility(View.VISIBLE);
            new StyleableToast
                    .Builder(getApplicationContext())
                    .text("Please wait updating...")
                    .textColor(Color.WHITE)
                    .textBold()
                    .gravity(0)
                    .length(2000)
                    .solidBackground()
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .show();
        }else
        if (resultCode == RESULT_OK && requestCode == COVER_PICK_CODE && data != null && data.getData() != null) {
            cover_uri = data.getData();
            cover.setImageURI(image_uri);
            coverUpload();
            pb.setVisibility(View.VISIBLE);
            new StyleableToast
                    .Builder(getApplicationContext())
                    .text("Please wait updating...")
                    .textColor(Color.WHITE)
                    .textBold()
                    .length(2000)
                    .gravity(0)
                    .solidBackground()
                    .gravity(0)
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .show();
        }
    }

    private void imageUpload() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathName = "Dp/" + "DP_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathName);
        ref.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){
                HashMap<String , Object> hashMap = new HashMap<>();
                hashMap.put("photo", downloadUri);
                FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                    pb.setVisibility(View.GONE);
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Profile Photo Changed")
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
    }

    private void coverUpload() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathName = "Cover/" + "Cover_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathName);
        ref.putFile(cover_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cover", downloadUri);
                FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                    pb.setVisibility(View.GONE);
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Cover Photo Changed")
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


    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            View view = LayoutInflater.from(this).inflate(R.layout.edit_profile_ui, null);
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
            case R.id.delete:
                //Delete
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("photo", "");
                FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap).addOnCompleteListener(task -> new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Profile Photo Deleted")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show());
                break;
            case R.id.constraintLayout5:

                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permissions2 = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions2, PERMISSION_CODE);
                    } else {
                        pickCoverFromGallery();
                    }
                } else {
                    pickCoverFromGallery();
                }

                break;
            case R.id.deleteC:
                //Delete
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("cover", "");
                FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap2).addOnCompleteListener(task -> new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Cover Photo Deleted")
                        .textColor(Color.WHITE)
                        .gravity(0)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show());
                break;
        }

    }
}