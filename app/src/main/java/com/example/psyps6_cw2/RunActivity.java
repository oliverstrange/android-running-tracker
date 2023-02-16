package com.example.psyps6_cw2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.psyps6_cw2.viewmodels.RunActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/*
Activity for logging a new run
 */

public class RunActivity extends AppCompatActivity {

    private RunActivityViewModel vm;

    private TextView timeView;
    private TextView distanceView;
    private TextView speedView;
    private Button button;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.new_run);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.new_run:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.my_runs:
                        startActivity(new Intent(getApplicationContext(),MyRunsActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        //observe viewmodel data on current run
        timeView = findViewById(R.id.timeTextView);
        distanceView = findViewById(R.id.distanceTextView);
        speedView = findViewById(R.id.speedTextView);
        button = findViewById(R.id.startButton);

        vm = new ViewModelProvider(this).get(RunActivityViewModel.class);

        final Observer<String> buttonTextObserver = newButtonText -> button.setText(newButtonText);
        final Observer<String> timeTextObserver = newTimeText -> timeView.setText(newTimeText);
        final Observer<String> distanceTextObserver = newDistanceText -> distanceView.setText(newDistanceText);
        final Observer<String> speedTextObserver = newSpeedText -> speedView.setText(newSpeedText);

        vm.getButtonText().observe(this, buttonTextObserver);
        vm.getTimeText().observe(this, timeTextObserver);
        vm.getDistanceText().observe(this, distanceTextObserver);
        vm.getSpeedText().observe(this, speedTextObserver);

        setNavVisibility();
    }

    public void onStartPressed(View v) {
        if (vm.getStatus() == Status.STOPPED) {
            vm.startTracking();
        }
        else if (vm.getStatus() == Status.PAUSED) {
            vm.resumeTracking();
        }
        else if (vm.getStatus() == Status.TRACKING) {
            vm.pauseTracking();
        }
        setNavVisibility();
    }

    public void onStopPressed(View v) {
        if (vm.getStatus() != Status.STOPPED) {
            int id = vm.finishTracking();
            Log.d("ME", "STOPPING ID" + id);
        }
        setNavVisibility();
    }

    //hide navigation bar if currently running
    private void setNavVisibility() {
        if(vm.getStatus() != Status.STOPPED) {
            bottomNavigationView.setVisibility(View.INVISIBLE);
        }
        else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}