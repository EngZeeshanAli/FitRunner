package com.example.fitrunner.MusicPlayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitrunner.R;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    static MediaPlayer mp = new MediaPlayer();

    static int position;
    SeekBar sb;
    ArrayList<Music> mySongs;

    Button pause, next, previous;
    TextView songNameText;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_ui);
        init();

    }

    void init() {
        mySongs = new ArrayList<>();
        Intent i = getIntent();
        String[] mySongsJson = i.getStringArrayExtra("sl");
        position = Integer.parseInt(i.getStringExtra("pos"));
        getMusicObjects(mySongsJson);

        songNameText = findViewById(R.id.txtSongLabel);
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(this);
        previous = findViewById(R.id.previous);
        previous.setOnClickListener(this);
        next = findViewById(R.id.next);
        next.setOnClickListener(this);
        sb = findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setNextSong();
            }
        });

        playMusic();

    }


    void playMusic() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
            mp = new MediaPlayer();
        }

        Music music = mySongs.get(position);
        songNameText.setText(music.getTitle());
        songNameText.setSelected(true);

        try {
            mp.setDataSource(music.getUri());
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int totalDuration = mp.getDuration();
        sb.setMax(totalDuration);
        setUpdateSeekBar(totalDuration);

    }

    void setUpdateSeekBar(final int total){
        final int current=mp.getCurrentPosition();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (current<total){
                    sb.setProgress(current);
                    setUpdateSeekBar(total);
                }else{
                    return;
                }

            }
        },1000);
    }

    void setNextSong() {
        if (position < mySongs.size()-1) {
            position = position + 1;
        } else {
            if (position >= mySongs.size()-1) {
                position = 0;
            }
        }

        playMusic();
    }

    void setPreviousSong() {
        if (position <=0 ) {
            position = mySongs.size()-1;
        } else {
            if (position > 0) {
                position = position-1;
            }
        }

        playMusic();
    }


    void getMusicObjects(String[] items) {
        Music music = new Music();
        for (int i = 0; i < items.length - 1; i++) {
            Music mus = music.getMusicObject(items[i]);
            mySongs.add(mus);
        }
    }

    void pp() {
        if (mp.isPlaying()) {
            pause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            mp.pause();

        } else {
            pause.setBackgroundResource(R.drawable.pause);
            mp.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pause:
                pp();
                break;
            case R.id.next:
                setNextSong();
                break;
            case R.id.previous:
                setPreviousSong();
                break;
        }
    }
}