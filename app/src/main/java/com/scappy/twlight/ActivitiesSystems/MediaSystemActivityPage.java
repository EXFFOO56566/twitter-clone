package com.scappy.twlight.ActivitiesSystems;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.scappy.twlight.R;

@SuppressWarnings("deprecation")
public class MediaSystemActivityPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_page);
        PhotoView photo_view = findViewById(R.id.photo_view);
        VideoView video_view = findViewById(R.id.video_view);

        String type = getIntent().getStringExtra("type");
        String link = getIntent().getStringExtra("uri");

        if (type.equals("image")){
            Glide.with(getApplicationContext()).load(link).into(photo_view);
            photo_view.setVisibility(View.VISIBLE);
        }else if (type.equals("video")){
            Uri uri = Uri.parse(link);
            video_view.setVideoURI(uri);
            video_view.setVisibility(View.VISIBLE);
            video_view.start();
            MediaController mediaController = new MediaController(MediaSystemActivityPage.this);
            mediaController.setAnchorView(video_view);
            video_view.setMediaController(mediaController);
        }

    }
}