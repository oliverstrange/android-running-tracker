package com.example.psyps6_cw2.database;

import android.location.Location;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/*
Typeconverter for storing Location Arraylists to be used by the map
 */
public class Converters {
    @TypeConverter
    public ArrayList<Location> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Location>>() {}.getType();
        Gson gson = new Gson();
        ArrayList<Location> arrayList = gson.fromJson(value, listType);
        return arrayList;
    }

    @TypeConverter
    public String fromArrayList(ArrayList<Location> list) {
        Type listType = new TypeToken<ArrayList<Location>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(list, listType);
        return json;
    }
}
