package com.example.fitrunner.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitrunner.Authentications.User;
import com.example.fitrunner.DashBoard;
import com.example.fitrunner.MapWay;
import com.example.fitrunner.R;
import com.example.fitrunner.UiControllers.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFrag extends Fragment implements View.OnClickListener, OnMapReadyCallback {
    Button startRunning;
    GoogleMap map;
    TextView name, email;
    CircleImageView img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        init(view);
        return view;
    }

    void init(View v) {
        startRunning = v.findViewById(R.id.start_runiing_home);
        startRunning.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_home);
        mapFragment.getMapAsync(HomeFrag.this);
        img = v.findViewById(R.id.img_home_frag);
        name = v.findViewById(R.id.name_home_frag);
        email = v.findViewById(R.id.email_home_frag);
        userDetail();
    }

    public boolean checkGpsStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_runiing_home:
                getActivity().startActivity(new Intent(getActivity(),MapWay.class));
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        if (checkGpsStatus() == false) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage("Turn On GPS To Use Location Service....");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "Go",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gps);
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        if (checkGpsStatus()) {
            map.setMyLocationEnabled(true);
            if (map != null) {
                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 18));
                    }
                });
            }
        }
    }

    void userDetail() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USER_TABLE).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                email.setText(user.getEmail());
                if (getActivity() != null && !getActivity().isFinishing()) {
                    Glide.with(getContext()).load(user.getImg()).placeholder(R.drawable.home).into(img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
