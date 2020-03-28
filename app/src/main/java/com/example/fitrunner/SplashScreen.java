package com.example.fitrunner;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitrunner.MusicPlayer.MusicPlayer;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {
    private static final int AUTO_DELAY_MILLIS = 300;
    ProgressBar connectionProgress;
    Button musicPlayer;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
    }

    void init() {
        connectionProgress = findViewById(R.id.connectionProgress);
        musicPlayer = findViewById(R.id.goto_music_player_splash);
        musicPlayer.setOnClickListener(this);
        status = findViewById(R.id.status);
        setConnectionProgress(connectionProgress);


    }

    void setConnectionProgress(final ProgressBar bar) {
        if (bar.getProgress() < AUTO_DELAY_MILLIS) {
            bar.setProgress(bar.getProgress() + 20);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setConnectionProgress(bar);
                }
            }, 100);
        } else {
            if (isOnline() == false) {
                status.setText("No Internet Access !");
                musicPlayer.setVisibility(View.VISIBLE);
            }
            if (isOnline() == true) {
                status.setText("Connected.....");
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_music_player_splash:
                finish();
                startActivity(new Intent(SplashScreen.this, MusicPlayer.class));
                break;
        }
    }
}
