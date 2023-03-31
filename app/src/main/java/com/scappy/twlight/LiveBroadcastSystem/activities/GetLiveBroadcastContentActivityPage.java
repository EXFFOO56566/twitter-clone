package com.scappy.twlight.LiveBroadcastSystem.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.MainActivity;
import com.scappy.twlight.R;
import com.scappy.twlight.adapter.AdapterForLiveChatSystem;
import com.scappy.twlight.LiveBroadcastSystem.stats.LocalStatsData;
import com.scappy.twlight.LiveBroadcastSystem.stats.RemoteStatsData;
import com.scappy.twlight.LiveBroadcastSystem.stats.StatsData;
import com.scappy.twlight.LiveBroadcastSystem.ui.VideoGridContainer;
import com.scappy.twlight.SystemModels.SystemModelForLiveChat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;

@SuppressWarnings({"ALL", "CodeBlock2Expr"})
public class GetLiveBroadcastContentActivityPage extends RtcBaseActivity {
    private static final String TAG = GetLiveBroadcastContentActivityPage.class.getSimpleName();
    private VideoGridContainer mVideoGridContainer;
    private ImageView mMuteAudioBtn;
    private ImageView mMuteVideoBtn;
    int role;

    EditText sendMessage;
    ImageView send;
    RecyclerView chat_rv;

    //Post
    AdapterForLiveChatSystem liveChat;
    List<SystemModelForLiveChat> modelLives;

    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room_page__appui);
        initUI();
        initData();

        //UserInfo
        CircleImageView mDp = findViewById(R.id.mDp);
        TextView username = findViewById(R.id.username);
        ImageView verify = findViewById(R.id.verify);

        sendMessage = findViewById(R.id.sendMessage);
        send = findViewById(R.id.imageView2);
        chat_rv = findViewById(R.id.chat_rv);

        new Handler().postDelayed(() -> {
            FirebaseDatabase.getInstance().getReference().child("Live").child(config().getChannelName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId = snapshot.child("userid").getValue().toString();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String dp = snapshot.child("photo").getValue().toString();
                            String mUsername = snapshot.child("username").getValue().toString();
                            String mVerify = snapshot.child("verified").getValue().toString();
                            if (!dp.isEmpty()){
                                Picasso.get().load(dp).into(mDp);
                            }
                            username.setText(mUsername);
                            if (!mVerify.isEmpty()){
                                verify.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        },2000);

        if (role ==  Constants.CLIENT_ROLE_AUDIENCE){
            new Handler().postDelayed(() -> {
                FirebaseDatabase.getInstance().getReference().child("Live").child(config().getChannelName()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            Intent intent6 = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent6);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            },2000);
        }

        TextView number = findViewById(R.id.number);

        new Handler().postDelayed(() -> {
            FirebaseDatabase.getInstance().getReference().child("Live").child(config().getChannelName()).child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    number.setText(String.valueOf(snapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
         },2000);

        send.setOnClickListener(v -> {
            String msg = sendMessage.getText().toString();
            if (msg.isEmpty()){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Type something")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }else {

                String timeStamp = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("ChatId", timeStamp);
                hashMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("msg", msg);
                FirebaseDatabase.getInstance().getReference().child("Live").child(config().getChannelName()).child("Chats").child(timeStamp).setValue(hashMap);

                sendMessage.setText("");

            }
        });

        modelLives = new ArrayList<>();

        chat_rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chat_rv.setLayoutManager(linearLayoutManager);

        readMessage();

    }

    private void readMessage() {
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("Live").child(config().getChannelName()).child("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelLives.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SystemModelForLiveChat systemModelForLiveChat = ds.getValue(SystemModelForLiveChat.class);
                    modelLives.add(systemModelForLiveChat);
                }
                liveChat = new AdapterForLiveChatSystem(getApplicationContext(), modelLives);
                chat_rv.setAdapter(liveChat);
                liveChat.notifyDataSetChanged();
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

    private void initUI() {

         role = getIntent().getIntExtra(
                com.scappy.twlight.LiveBroadcastSystem.Constants.KEY_CLIENT_ROLE,
                Constants.CLIENT_ROLE_AUDIENCE);
        boolean isBroadcaster =  (role == Constants.CLIENT_ROLE_BROADCASTER);

        mMuteVideoBtn = findViewById(R.id.live_btn_mute_video);
        mMuteVideoBtn.setActivated(isBroadcaster);

        mMuteAudioBtn = findViewById(R.id.live_btn_mute_audio);
        mMuteAudioBtn.setActivated(isBroadcaster);

        ImageView beautyBtn = findViewById(R.id.live_btn_beautification);
        beautyBtn.setActivated(true);
        rtcEngine().setBeautyEffectOptions(beautyBtn.isActivated(),
                com.scappy.twlight.LiveBroadcastSystem.Constants.DEFAULT_BEAUTY_OPTIONS);

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());

        rtcEngine().setClientRole(role);
        if (isBroadcaster) startBroadcast();


    }


    private void initData() {
        mVideoDimension = com.scappy.twlight.LiveBroadcastSystem.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }



    private void startBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);
        mMuteAudioBtn.setActivated(true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
        mMuteAudioBtn.setActivated(false);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment


    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(() -> removeRemoteUser(uid));
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(() -> renderRemoteUser(uid));
    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }


    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (role == Constants.CLIENT_ROLE_BROADCASTER){
            FirebaseDatabase.getInstance().getReference().child("Live").child(config().getChannelName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().removeValue();
                    Intent intent6 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent6);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Intent intent6 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent6);
            finish();

        }
    }

    public void onLeaveClicked(View view) {

        if (role == Constants.CLIENT_ROLE_BROADCASTER){
            FirebaseDatabase.getInstance().getReference().child("Live").child(config().getChannelName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().removeValue();
                    Intent intent6 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent6);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Intent intent6 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent6);
            finish();

        }

    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine().switchCamera();
    }

    public void onBeautyClicked(View view) {
        view.setActivated(!view.isActivated());
        rtcEngine().setBeautyEffectOptions(view.isActivated(),
                com.scappy.twlight.LiveBroadcastSystem.Constants.DEFAULT_BEAUTY_OPTIONS);
    }

    public void onMuteAudioClicked(View view) {
        if (!mMuteVideoBtn.isActivated()) return;

        rtcEngine().muteLocalAudioStream(view.isActivated());
        view.setActivated(!view.isActivated());
    }

    public void onMuteVideoClicked(View view) {
        if (view.isActivated()) {
            stopBroadcast();
        } else {
            startBroadcast();
        }
        view.setActivated(!view.isActivated());
    }


}
