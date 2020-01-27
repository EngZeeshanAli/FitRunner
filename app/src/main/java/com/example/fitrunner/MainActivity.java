package com.example.fitrunner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fitrunner.Authentications.Login;
import com.example.fitrunner.Authentications.Register;
import com.example.fitrunner.MusicPlayer.MusicPlayer;
import com.example.fitrunner.UiControllers.Ui;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView demoText;
    Button signIn, signUp,musicPlayer;
    FrameLayout fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGui();

    }

    void initGui() {
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(this);
        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(this);
        musicPlayer=findViewById(R.id.goto_music_player);
        musicPlayer.setOnClickListener(this);
        demoText = findViewById(R.id.demo_text);
        fragments=findViewById(R.id.login_register);

    }


    public void slidleft(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.login_register, fragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }


    void getFormData() {
//        if (TextUtils.isEmpty()) {
//      }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signIn:
                slidleft(new Login());
                signIn.setVisibility(View.GONE);
                signUp.setVisibility(View.GONE);
                demoText.setVisibility(View.GONE);
                musicPlayer.setVisibility(View.GONE);
                fragments.setVisibility(View.VISIBLE);
                break;
            case R.id.signUp:
                slidleft(new Register());
                signIn.setVisibility(View.GONE);
                signUp.setVisibility(View.GONE);
                demoText.setVisibility(View.GONE);
                musicPlayer.setVisibility(View.GONE);
                fragments.setVisibility(View.VISIBLE);
                break;
            case R.id.goto_music_player:
                startActivity(new Intent(MainActivity.this, MusicPlayer.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (signIn.getVisibility() == View.GONE) {
            signIn.setVisibility(View.VISIBLE);
            signUp.setVisibility(View.VISIBLE);
            demoText.setVisibility(View.VISIBLE);
            musicPlayer.setVisibility(View.VISIBLE);
        }
        if (fragments.getVisibility()==View.VISIBLE){
            fragments.setVisibility(View.GONE);
        }
        super.onBackPressed();

    }
}

