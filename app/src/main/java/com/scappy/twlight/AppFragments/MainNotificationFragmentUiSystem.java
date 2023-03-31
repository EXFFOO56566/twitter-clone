package com.scappy.twlight.AppFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scappy.twlight.Adpref;
import com.scappy.twlight.R;
import com.scappy.twlight.SystemModels.SystemModelForNotifications;
import com.scappy.twlight.adapter.AdapterForNotifctionsSystem;

import java.util.ArrayList;
import java.util.Objects;

public class MainNotificationFragmentUiSystem extends Fragment {

    String userId;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private ArrayList<SystemModelForNotifications> notifications;
    private AdapterForNotifctionsSystem adapterNotify;
    ProgressBar pb;
    TextView found;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this AppFragments
        View view = inflater.inflate(R.layout.fragment_notificationpage, container, false);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        recyclerView = view.findViewById(R.id.chatList);
        pb = view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();

        found = view.findViewById(R.id.found);

        MobileAds.initialize(getContext(), initializationStatus -> {
        });
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Adpref adpref;
        adpref = new Adpref(Objects.requireNonNull(getContext()));
        if (adpref.loadAdsModeState()){
            mAdView.setVisibility(View.VISIBLE);
        }

        getAllNotifications();
        return view;
    }

    private void getAllNotifications() {
        notifications = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifications.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            SystemModelForNotifications systemModelForNotifications = ds.getValue(SystemModelForNotifications.class);
                            notifications.add(systemModelForNotifications);
                            pb.setVisibility(View.GONE);
                        }
                        if (snapshot.getChildrenCount() == 0){
                            found.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);
                        }
                        adapterNotify = new AdapterForNotifctionsSystem(getActivity(), notifications);
                        recyclerView.setAdapter(adapterNotify);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}