package com.example.fitrunner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fitrunner.MusicPlayer.MusicPlayer;
import com.example.fitrunner.fragment.Calculator;
import com.example.fitrunner.fragment.HomeFrag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    private static final int TIME_INTERVAL = 1000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        initUi();
        if (savedInstanceState == null) {
            permissions();
        }

    }


    void initUi() {
        Toolbar toolbar = findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView view = findViewById(R.id.bottomNavigationView);
        view.setOnNavigationItemSelectedListener(this);
        NavigationView navigationView = findViewById(R.id.nav_view_draw);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_draw, R.string.close_draw);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            /** Controlling drawer navigation items*/

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFrag()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.music_player:
                startActivity(new Intent(DashBoard.this, MusicPlayer.class));
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.profile:
                startActivity(new Intent(DashBoard.this, Profile.class));
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;

            /** Controlling bottom navigation items*/
            case R.id.home_bt:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFrag()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.reports:
                Toast.makeText(this, "repots", Toast.LENGTH_SHORT).show();
                //getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFrag()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.calculator:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new Calculator()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;


        }

        return true;
    }


    void permissions() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (isFirstTimeAskingPermission(this)) {
                askGpsPermissions();
            } else {
                if (!isFirstTimeAskingPermission(this)) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        permissionDenied();
                    }
                }
            }
        } else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFrag()).commit();
            }
        }
    }


    private void permissionDenied() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Location  permission is neccesary.\n Go to setting to give permission? ");
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
                        Toast.makeText(DashBoard.this, "Permission are Required", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    void askGpsPermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        firstTimeAskingPermission(DashBoard.this);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFrag()).commit();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        permissionDenied();
                        firstTimeAskingPermission(DashBoard.this);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }

    public static void firstTimeAskingPermission(Context context) {
        SharedPreferences sharedPreference = context.getSharedPreferences("permission", MODE_PRIVATE);
        sharedPreference.edit().putBoolean("per", false).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context) {
        return context.getSharedPreferences("permission", MODE_PRIVATE).getBoolean("per", true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFrag()).commit();
        }
    }
}
