package com.example.psyps6_cw2.viewmodels;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.psyps6_cw2.Status;
import com.example.psyps6_cw2.TrackingService;

/*
Veiwmodel for tracking a run
Handles the service and state
Contains text to be observed by activity UI
 */
public class RunActivityViewModel extends AndroidViewModel {

    TrackingService.MyBinder trackingService = null;

    //livedata to be accessed by run activity
    private MutableLiveData<String> buttonText;
    private MutableLiveData<String> timeText;
    private MutableLiveData<String> distanceText;
    private MutableLiveData<String> speedText;

    private final Application application;
    private Status status;

    //observers to get livedata from service
    final Observer<String> timeObserver = newTime -> timeText.setValue(newTime);
    final Observer<String> distanceObserver = newDistance -> distanceText.setValue(newDistance);
    final Observer<String> speedObserver = newSpeed -> speedText.setValue(newSpeed);

    public RunActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        status = Status.STOPPED;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("me", "connecting service");
            trackingService = (TrackingService.MyBinder) service;
            trackingService.getTime().observeForever(timeObserver);
            trackingService.getDistance().observeForever(distanceObserver);
            trackingService.getSpeed().observeForever(speedObserver);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("me", "disconnecting service");
            trackingService.getTime().removeObserver(timeObserver);
            trackingService.getDistance().removeObserver(distanceObserver);
            trackingService.getSpeed().removeObserver(speedObserver);
            trackingService = null;
            timeText.setValue("00:00:00");
            distanceText.setValue("0.00km");
            speedText.setValue("0.00km/h");
        }
    };

    public LiveData<String> getButtonText() {
        if (buttonText == null)
            buttonText = new MutableLiveData<>("Start Run");
        return buttonText;
    }

    public LiveData<String> getTimeText() {
        if (timeText == null)
            timeText = new MutableLiveData<>("00:00:00");
        return timeText;
    }

    public LiveData<String> getDistanceText() {
        if (distanceText == null)
            distanceText = new MutableLiveData<>("0.00km");
        return distanceText;
    }

    public LiveData<String> getSpeedText() {
        if (speedText == null)
            speedText = new MutableLiveData<>("0.00km/h");
        return speedText;
    }

    public Status getStatus() {
        return status;
    }

    public void startTracking() {
        Intent intent = new Intent(application.getApplicationContext(), TrackingService.class);
        application.getApplicationContext().startForegroundService(intent);
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        buttonText.setValue("Pause");
        status = Status.TRACKING;
    }

    public void pauseTracking() {
        trackingService.pauseRun();
        buttonText.setValue("Resume");
        status = Status.PAUSED;
    }

    public void resumeTracking() {
        trackingService.resumeRun();
        buttonText.setValue("Pause");
        status = Status.TRACKING;
    }

    public int finishTracking() {
        int id = trackingService.finishRun();
        if (serviceConnection != null) {
            application.unbindService(serviceConnection);
        }
        buttonText.setValue("Start Run");
        trackingService.getTime().removeObserver(timeObserver);
        trackingService.getDistance().removeObserver(distanceObserver);
        trackingService.getSpeed().removeObserver(speedObserver);
        timeText.setValue("00:00:00");
        distanceText.setValue("0.00km");
        speedText.setValue("0.00km/h");
        status = Status.STOPPED;

        return id;
    }
}