package com.example.fitrunner.fragment;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitrunner.MapWay;
import com.example.fitrunner.R;

public class HomeFrag extends Fragment implements View.OnClickListener {
    Button startRunning;

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
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_runiing_home:
                startActivity(new Intent(getActivity(), MapWay.class));
                break;
        }
    }
}
