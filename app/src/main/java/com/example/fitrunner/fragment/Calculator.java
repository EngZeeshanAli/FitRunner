package com.example.fitrunner.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitrunner.R;

public class Calculator extends Fragment implements View.OnClickListener {
    EditText kilometer, hh, mm, ss;
    TextView pace, speed;
    LinearLayout paceL, speedL;
    Button calculate, reset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculator_layout, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        reset = view.findViewById(R.id.reset);
        reset.setOnClickListener(this);
        calculate = view.findViewById(R.id.calculat);
        calculate.setOnClickListener(this);
        pace = view.findViewById(R.id.calculated_pace_tv);
        speed = view.findViewById(R.id.calculated_speed_tv);
        kilometer = view.findViewById(R.id.kilometer_ed);
        hh = view.findViewById(R.id.hh_ed);
        mm = view.findViewById(R.id.mm_ed);
        ss = view.findViewById(R.id.ss_ed);
        paceL = view.findViewById(R.id.paceL);
        speedL = view.findViewById(R.id.speedL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                setReset();
                break;
            case R.id.calculat:
                setCalculate();
                break;
        }
    }

    private void setCalculate() {
        float hhF, mmF, ssF, disF;

        if (TextUtils.isEmpty(hh.getText().toString())) {
            hhF = 0;
        } else {
            hhF = Float.parseFloat(hh.getText().toString());
        }

        if (TextUtils.isEmpty(mm.getText().toString())) {
            mmF = 0;
        } else {
            mmF = Float.parseFloat(mm.getText().toString());
        }

        if (TextUtils.isEmpty(ss.getText().toString())) {
            ssF = 0;
        } else {
            ssF = Float.parseFloat(ss.getText().toString());
        }

        if (TextUtils.isEmpty(kilometer.getText().toString())) {
            disF = 0;
        } else {
            disF = Float.parseFloat(kilometer.getText().toString());
        }

        //calculate speed
        float speed_temp = disF / (((((hhF * 60) + mmF) * 60) + ssF) / 3600);
        String finalSpeed = String.valueOf(Math.round(speed_temp * 1000.0) / 1000.0);
        speed.setText(finalSpeed);


        //calculate pace
        float time = (((((hhF * 60) + mmF) * 60) + ssF) / 3600);
        float tem_pace = time / disF;
        pace.setText(String.valueOf(tem_pace));
        paceL.setVisibility(View.VISIBLE);
        speedL.setVisibility(View.VISIBLE);
    }

    private void setReset() {
        kilometer.setText("");
        hh.setText("");
        mm.setText("");
        ss.setText("");
        paceL.setVisibility(View.GONE);
        speedL.setVisibility(View.GONE);
    }
}
