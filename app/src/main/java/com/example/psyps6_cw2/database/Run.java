package com.example.psyps6_cw2.database;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
Table for logging runs
 */

@Entity(tableName = "run_table")
public class Run {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @NonNull
    @ColumnInfo(name = "duration")
    private long duration;

    @NonNull
    @ColumnInfo(name = "distance")
    private double distance;

    @NonNull
    @ColumnInfo(name = "speed")
    private double speed;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "locations")
    private ArrayList<Location> locations;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "rating")
    private String rating;


    public Run(String date, long duration, Double distance, Double speed) {
        this.date = date;
        this.duration = duration;
        this.distance = distance;
        this.speed = speed;
    }

    public int getId() {
        return id;
    }
    public String getDate() {
        return date;
    }
    public long getDuration() {
        return duration;
    }
    public double getDistance() {
        return distance;
    }
    public double getSpeed() {
        return speed;
    }
    public String getNotes() {
        return notes;
    }
    public String getRating() {
        return rating;
    }
    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setDate(@NonNull String date) {
        this.date = date;
    }
    public void setDuration(@NonNull long duration) {
        this.duration = duration;
    }
    public void setDistance(@NonNull Double distance) {
        this.distance = distance;
    }
    public void setSpeed(@NonNull Double speed) {
        this.speed = speed;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

}

