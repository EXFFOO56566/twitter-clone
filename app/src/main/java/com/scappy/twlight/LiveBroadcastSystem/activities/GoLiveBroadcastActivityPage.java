package com.scappy.twlight.LiveBroadcastSystem.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoast.StyleableToast;
import com.scappy.twlight.R;

import java.util.HashMap;

@SuppressWarnings("ALL")
public class GoLiveBroadcastActivityPage extends BaseActivity {

    // Permission request code of any integer value
    private static final int PERMISSION_REQ_CODE = 1 << 4;

    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check();
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
            resetLayoutAndForward();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {
                resetLayoutAndForward();
            } else {
                toastNeedPermissions();
            }
        }
    }

    private void resetLayoutAndForward() {
        gotoRoleActivity();
    }

    private void toastNeedPermissions() {
        new StyleableToast
                .Builder(getApplicationContext())
                .text("Permissions needed")
                .textColor(Color.WHITE)
                .gravity(0)
                .textBold()
                .length(2000)
                .solidBackground()
                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    public void gotoRoleActivity() {
        String room = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("room", room);
        hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Live");
        reference.child(room).setValue(hashMap).addOnCompleteListener(task -> {

            Intent intent = new Intent(GoLiveBroadcastActivityPage.this, BroadcasterActivity.class);
            config().setChannelName(room);
            startActivity(intent);
            finish();


            return;
        });

    }
}