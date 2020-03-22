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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitrunner.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class HomeFrag extends Fragment implements View.OnClickListener, OnMapReadyCallback {
    Button startRunning;
    GoogleMap map;

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

    @Override
    public void onResume() {
        super.onResume();
    }
}
