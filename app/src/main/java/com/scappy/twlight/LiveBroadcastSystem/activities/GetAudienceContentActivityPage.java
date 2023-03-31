package com.scappy.twlight.LiveBroadcastSystem.activities;

import android.content.Intent;
import android.os.Bundle;

import io.agora.rtc.Constants;

@SuppressWarnings("SameParameterValue")
public class GetAudienceContentActivityPage extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gotoLiveActivity(Constants.CLIENT_ROLE_AUDIENCE);

    }
    private void gotoLiveActivity(int role) {
        Intent intent = new Intent(getIntent());
        intent.putExtra(com.scappy.twlight.LiveBroadcastSystem.Constants.KEY_CLIENT_ROLE, role);
        intent.setClass(getApplicationContext(), GetLiveBroadcastContentActivityPage.class);
        startActivity(intent);
        finish();
    }
}