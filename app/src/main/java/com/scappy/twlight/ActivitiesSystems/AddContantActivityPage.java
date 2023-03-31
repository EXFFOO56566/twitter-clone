package com.scappy.twlight.ActivitiesSystems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("ALL")
public class AddContantActivityPage extends AppCompatActivity {

    ImageView delete;
    ImageView postImage;

    String myUid;
    String storyId;
    long timeend;

    DatabaseReference reference;
    private Uri image_uri;

    ProgressBar pb;

    private String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
    };

    private static final int PERMISSION_REQ_CODE = 1 << 4;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int CAMERA_PIC_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_page_appui);
        ImageView back = findViewById(R.id.imageView);
        back.setOnClickListener(v -> onBackPressed());
        ImageView uImage = findViewById(R.id.uImage);
        delete = (ImageView) findViewById(R.id.delete);
        postImage = findViewById(R.id.imageView8);
        pb = findViewById(R.id.pb);
        FloatingActionButton post = findViewById(R.id.button2);

        myUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        reference = FirebaseDatabase.getInstance().getReference("Story").child(myUid);
        storyId = reference.push().getKey();
        timeend = System.currentTimeMillis()+86400000;

        uImage.setOnClickListener(v -> {
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
        });
        delete.setOnClickListener(v -> {
            postImage.setImageURI(null);
            delete.setVisibility(View.GONE);
        });

        ImageView camera = findViewById(R.id.camera);
        camera.setOnClickListener(v -> check());

        post.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            if (image_uri == null){

                new StyleableToast
                        .Builder(getApplicationContext())
                        .gravity(0)
                        .text("Upload a Image")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                pb.setVisibility(View.GONE);

            }else {
                        String image = image_uri.toString();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Story/" + "Story_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(image)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageUri", downloadUri);
                hashMap.put("timestart", ServerValue.TIMESTAMP);
                hashMap.put("timeend", timeend);
                hashMap.put("storyid", storyId);
                hashMap.put("userid", myUid);

                reference.child(storyId).setValue(hashMap);

                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Story uploaded")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                pb.setVisibility(View.GONE);

            }
        });
            }
        });

    }

    //ImagePick
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
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
                        .gravity(0)
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
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_PIC_REQUEST);
            } else {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Camera Permission is Required")
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
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE   && data != null && data.getData() != null){
            image_uri = data.getData();
            postImage.setImageURI(image_uri);
            postImage.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }
        if (resultCode == RESULT_OK && requestCode == CAMERA_PIC_REQUEST ) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),photo, "val",null);
            image_uri = Uri.parse(path);
            postImage.setImageURI(image_uri);
            postImage.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }

    }

    private void check() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }

        if (granted) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,CAMERA_PIC_REQUEST);
        } else {
            requestPermissions();
        }
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ_CODE);
    }

}