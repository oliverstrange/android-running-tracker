package com.example.psyps6_cw2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.psyps6_cw2.database.Repository;
import com.example.psyps6_cw2.viewmodels.MainActivityViewModel;
import com.example.psyps6_cw2.viewmodels.RunActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/*
Home screen activity. Displays averages over previous runs
 */

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel vm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.new_run:
                        startActivity(new Intent(getApplicationContext(),RunActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.home:
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

        //request missing permissions
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        //set observers for ui
        vm = new ViewModelProvider(this).get(MainActivityViewModel.class);

        vm.getHighestDistance().observe(this, run -> {
            if (run != null){
                TextView distance = findViewById(R.id.allTimeDistanceText);
                distance.setText(String.format("%.2f",run.getDistance()/1000)+"km");
            }
        });

        vm.getHighestSpeed().observe(this, run -> {
            if (run != null){
                TextView distance = findViewById(R.id.allTimeSpeedText);
                distance.setText(String.format("%.2f",run.getSpeed())+"km/h");
            }
        });

        vm.getHighestDistance().observe(this, run -> {
            if (run != null){
                TextView distance = findViewById(R.id.allTimeDistanceText);
                distance.setText(String.format("%.2f",run.getDistance()/1000)+"km");
            }
        });

        vm.getHighestDistanceToday().observe(this, run -> {
            if (run != null){
                TextView distance = findViewById(R.id.todayDistanceText);
                distance.setText(String.format("%.2f",run.getDistance()/1000)+"km");
            }
        });

        vm.getHighestSpeedToday().observe(this, run -> {
            if (run != null){
                TextView distance = findViewById(R.id.todaySpeedText);
                distance.setText(String.format("%.2f",run.getSpeed())+"km/h");
            }
        });




    }
}