package com.example.psyps6_cw2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.psyps6_cw2.database.Repository;
import com.example.psyps6_cw2.database.Run;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/*
Service to track location, time, speed
Stores arraylist of visited locations for a map view of the route
 */
public class TrackingService extends Service {

    private static final String CHANNEL_ID = "trackingChannel";
    private static final String ACTION_STOP_LISTEN = "action_stop_listen";

    private double distance = 0;
    private long time = 0;
    private double speed = 0;

    private MutableLiveData<String> displayTime = new MutableLiveData<>();
    private MutableLiveData<String> displayDistance = new MutableLiveData<>();
    private MutableLiveData<String> displaySpeed = new MutableLiveData<>();

    private long recentTime = -1;   //initialised to -1 for location loop to reset

    private ArrayList<Location> route = new ArrayList<>();       //all visited locations for map
    private Location[] recentLocations = new Location[2];   //recent locations to work out distance travelled

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest= new
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build();

    private Repository repo;
    private Run run;

    private final IBinder binder = new MyBinder();


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("me", "SERVICE STARTING");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //callback when location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    Log.d("me", "location " + location.toString());
                    Log.d("me", "dist " + distance);
                    Log.d("me", "time" + time);

                    if(recentLocations[0] == null && recentLocations[1] == null) {
                        recentLocations[0] = recentLocations[1] = location;
                        recentTime = System.currentTimeMillis();    //initialise locations and timer variables
                    }
                    else {
                        recentLocations[0] = recentLocations[1];
                        recentLocations[1] = location;
                    }
                    route.add(location);
                    distance += recentLocations[0].distanceTo(recentLocations[1]);
                    time += System.currentTimeMillis() - recentTime;
                    recentTime = System.currentTimeMillis();            //fix
                    speed = (distance / time) * 3600;

                    displayTime.setValue(String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(time),
                            TimeUnit.MILLISECONDS.toMinutes(time) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                            TimeUnit.MILLISECONDS.toSeconds(time) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))));
                    displayDistance.setValue(String.format("%.2f",distance/1000)+"km");
                    displaySpeed.setValue(String.format("%.2f",speed)+"km/h");
                }
            }
        };
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());    //start tracking location by looping the callback
        } catch(SecurityException e) {
            // lacking permission to access location
        }

        //create a notification channel
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);

        //when notification clicked go back to the tracking activity
        Intent notificationIntent = new Intent(this, RunActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(TrackingService.this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Run Tracker")
                .setContentText("Tracking your run!")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        repo = new Repository(this.getApplication());
    }

    private int saveRun() {
        run = new Run(repo.getCurrentDate(), time, distance, speed);
        run.setLocations(route);
        return repo.insert(run);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {

        public int finishRun() {
            int id = TrackingService.this.saveRun();
            TrackingService.this.fusedLocationClient.removeLocationUpdates(TrackingService.this.locationCallback);
            TrackingService.this.stopForeground(true);
            TrackingService.this.stopSelf();
            return id;
        }

        public void pauseRun() {
            TrackingService.this.fusedLocationClient.removeLocationUpdates(TrackingService.this.locationCallback);
        }

        public void resumeRun() {
            try {
                TrackingService.this.recentTime = System.currentTimeMillis();   //reset time to resume counter
                TrackingService.this.fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback,
                        Looper.getMainLooper());    //start tracking location by looping the callback
            } catch(SecurityException e) {
                // lacking permission to access location
            }
        }

        public LiveData<String> getTime() {
            if (TrackingService.this.displayTime == null)
                TrackingService.this.displayTime = new MutableLiveData<>("00:00:00");
            return TrackingService.this.displayTime;
        }

        public LiveData<String> getDistance() {
            if (TrackingService.this.displayDistance == null)
                TrackingService.this.displayDistance = new MutableLiveData<>("0.00km");
            return TrackingService.this.displayDistance;
        }

        public LiveData<String> getSpeed() {
            if (TrackingService.this.displaySpeed == null)
                TrackingService.this.displaySpeed = new MutableLiveData<>("0.00km/h");
            return TrackingService.this.displaySpeed;
        }

    }

}
