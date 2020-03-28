package com.example.fitrunner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitrunner.UiControllers.Constants;
import com.example.fitrunner.objects.RunningLog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapWay extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mMap;
    private List<LatLng> latlngs = new ArrayList<>();
    private ArrayList<Float> speedList = new ArrayList();
    private Button end;
    private FirebaseUser user;
    private long startTime;
    private long currentTime;
    private long finalTime;
    TextView timer;
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_way);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
    }

    void init() {
        timer = findViewById(R.id.timer);
        end = findViewById(R.id.end);
        end.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        startTime = System.currentTimeMillis();
        timer();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        if (mMap != null) {
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 18));
                    latlngs.add(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
                    drawPath(latlngs);
                    float speed = (arg0.getSpeed() * 3600) / 1000;
                    speedList.add(speed);
                }
            });
        }
    }

    void drawPath(List listLatLng) {
        PolylineOptions rectOptions = new PolylineOptions().addAll(listLatLng);
        Polyline path = mMap.addPolyline(rectOptions);
        // Style the polyline
        path.setWidth(10);
        path.setColor(Color.parseColor("#FF0000"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.end:
                t.interrupt();
                setEnd();
                break;
        }
    }


    private void setEnd() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do you want to save your log?\n" +
                "Speed = " + calculateSpeed() + " KM/H" + "\n" +
                "Pace = " + calculatePace(calculateDistance(), finalTime) + "hr/km " + "\n" +
                "Time = " + timer.getText().toString() + "\n" +
                "Distance = " + calculateDistance() + " KM");

        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        final ProgressDialog process = new ProgressDialog(MapWay.this);
                        process.setMessage("Saving....");
                        process.show();
                        RunningLog log = new RunningLog(String.valueOf(calculateSpeed()), String.valueOf(calculatePace(calculateDistance(), finalTime)), timer.getText().toString(), String.valueOf(calculateDistance()), String.valueOf(System.currentTimeMillis()));
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.LOGS_TABLE).child(user.getUid()).push().setValue(log).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                process.dismiss();
                                finish();
                                Toast.makeText(MapWay.this, "Saved Succeefully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void timer() {
        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentTime = System.currentTimeMillis();
                                finalTime = currentTime - startTime;
                                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(finalTime),
                                        TimeUnit.MILLISECONDS.toMinutes(finalTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(finalTime)),
                                        TimeUnit.MILLISECONDS.toSeconds(finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime)));
                                timer.setText(hms);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }


    private float calculateDistance() {
        LatLng current = latlngs.get(latlngs.size() - 1);
        LatLng start = latlngs.get(0);
        float distance = 0;
        Location crntLocation = new Location("crntlocation");
        crntLocation.setLatitude(current.latitude);
        crntLocation.setLongitude(current.longitude);
        Location newLocation = new Location("newlocation");
        newLocation.setLatitude(start.latitude);
        newLocation.setLongitude(start.longitude);
        distance = crntLocation.distanceTo(newLocation) / 1000; // in km
        return distance;
    }

    private String calculatePace(float dis, long time) {
        long pace = 0;
        pace = (long) (time / dis);
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(pace),
                TimeUnit.MILLISECONDS.toMinutes(pace) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(pace)),
                TimeUnit.MILLISECONDS.toSeconds(pace) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(pace)));

    }

    private float calculateSpeed() {
        float averageSpeed = 0;
        for (float speed : speedList) {
            averageSpeed = speed + averageSpeed;
        }
        averageSpeed = (averageSpeed / speedList.size());
        return averageSpeed;
    }


}
