package com.example.psyps6_cw2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.psyps6_cw2.database.Run;
import com.example.psyps6_cw2.viewmodels.EditRunActivityViewModel;
import com.example.psyps6_cw2.viewmodels.RunActivityViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
Activity for viewing / editing a run
 */
public class EditRunActivity extends AppCompatActivity implements OnMapReadyCallback{

    private TextView timeView;
    private TextView distanceView;
    private TextView speedView;
    private TextView dateView;
    private EditText notes;
    private EditText rating;

    private EditRunActivityViewModel vm;

    private Run thisRun;

    private GoogleMap map;
    private ArrayList<Location> locations = new ArrayList<>();

    //initialise text and observe the selected run through viewmodel
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_run);

        Bundle extras = getIntent().getExtras();
        int runID = extras.getInt("id");

        timeView = findViewById(R.id.runTime);
        distanceView = findViewById(R.id.runDistance);
        speedView = findViewById(R.id.runSpeed);
        dateView = findViewById(R.id.runDate);
        notes = findViewById(R.id.notes);
        rating = findViewById(R.id.editTextNumber);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        vm = new ViewModelProvider(this).get(EditRunActivityViewModel.class);

        vm.getRun(runID).observe(this, run -> {
            if (run != null) {
                thisRun = run;
                locations = run.getLocations();
                for (int i=0; i<locations.size(); i++) {
                    Log.d("add", "point" + locations.get(i).getLatitude() + locations.get(i).getLongitude());
                }
                long time = run.getDuration();
                timeView.setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(time),
                        TimeUnit.MILLISECONDS.toMinutes(time) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                        TimeUnit.MILLISECONDS.toSeconds(time) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))));
                distanceView.setText(String.format("%.2f", run.getDistance() / 1000) + "km");
                speedView.setText(String.format("%.2f", run.getSpeed()) + "km/h");
                dateView.setText(run.getDate());
                if (run.getNotes() != null) {
                    notes.setText(run.getNotes());
                }
                if (run.getRating() != null) {
                    rating.setText(run.getRating());
                }
                Log.d("me", "RUN GOT");
                drawRoute();
            }
        });
    }

    //save notes and rating if entered by user
    public void onSaveClicked(View view) {
        String newNote = notes.getText().toString();
        String newRating = rating.getText().toString();
        if (!(newNote.equals("")))
            thisRun.setNotes(newNote);
        if (!(newRating.equals(""))) {
            int intRating = Integer.parseInt(newRating);    //set rating to 10 (max) if user enters a higher number
            if (intRating > 10)
                intRating = 10;
            thisRun.setRating(String.valueOf(intRating));
        }
        vm.updateRun(thisRun);
        for (int i=0; i<locations.size(); i++) {
            Log.d("add", "point" + locations.get(i).getLatitude() + locations.get(i).getLongitude());
        }
        finish();
    }

    //draw route on the map fragment
    //not currently working due to bug with accessing locations through room and typeconverter
    private void drawRoute() {
        PolylineOptions options = new PolylineOptions();

        LatLng start = new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());

        ArrayList<LatLng> points = new ArrayList<>();

        for (int i=0; i<locations.size(); i++) {
            points.add(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()));
            Log.d("add", "point" + locations.get(i).getLatitude() + locations.get(i).getLongitude());
        }
        options.addAll(points);
        Polyline line = map.addPolyline(options);
        line.setColor(Color.RED);
        line.setWidth(20);
        line.setJointType(JointType.ROUND);
        map.addMarker(new MarkerOptions().position(start).title("Start"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 1));
    }

    //map callback
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        Log.d("map", "ready");
    }
}