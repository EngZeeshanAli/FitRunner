package com.example.fitrunner.MusicPlayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitrunner.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class MusicPlayer extends AppCompatActivity {
    ArrayList<Music> list;
    ListView songList;
    String[] songNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionDenied();
        } else {
            init();
        }

    }


    void init() {
        list = new ArrayList<>();
        musicList();
        songList = findViewById(R.id.listView);
        ArrayAdapter<String> adp = new
                ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songNames);
        songList.setAdapter(adp);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startPlayer = new Intent(MusicPlayer.this, PlayerActivity.class);
                startPlayer.putExtra("pos", String.valueOf(position));
                startPlayer.putExtra("sl", toJsonMusic());
                startActivity(startPlayer);
            }
        });
        player();
    }

    void requestPermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(MusicPlayer.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }

    private void permissionDenied() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Stroage permission is neccesary.\n Go to setting to give permission? ");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Go",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void musicList() {
        ContentResolver resolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(songUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int location = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String songTitle = cursor.getString(title);
                String songLocaion = cursor.getString(location);
                Music music = new Music(songTitle, songLocaion);
                list.add(music);
            } while (cursor.moveToNext());

            songNames = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Music music = list.get(i);
                songNames[i] = music.getTitle();
            }
        }
    }

    String[] toJsonMusic() {
        Music music = new Music();
        String[] items = new String[list.size()];
        for (int o = 0; o < list.size(); o++) {
            Music m = list.get(o);
            items[o] = music.getJsonObject(m);
        }
        return items;
    }

    void player() {
        PlayerActivity playerActivity = new PlayerActivity();
        final MediaPlayer player = playerActivity.getPlayer();
        if (player != null && player.getCurrentPosition() > 1) {
            SeekBar bar = findViewById(R.id.seekBar_main);
            bar.setMax(player.getDuration());
            bar.setProgress(player.getCurrentPosition());
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    seekBar.setProgress(seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    player.seekTo(seekBar.getProgress());
                }
            });
        }
    }
}


