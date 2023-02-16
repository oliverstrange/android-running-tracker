package com.example.psyps6_cw2.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
Room database storing details on completed runs
 */

@Database(entities = {Run.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MainDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;

    private static volatile MainDatabase instance;
    public abstract RunDao runDao();
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    //get reference to singleton database
    public static MainDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (MainDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    MainDatabase.class, "runs_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(createCallback)
                            .build();
                }
            }
        }
        return instance;
    }

    //callback - ran when the database is created
    private static RoomDatabase.Callback createCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("ME", "on create db");
            databaseWriteExecutor.execute(() -> {
                //insert function
            });
        }
    };

}