package com.example.psyps6_cw2.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

public class Repository {

    private long newID;

    private LiveData<List<Run>> runs;

    private RunDao dao;

    public Repository(Application application) {
        MainDatabase db = MainDatabase.getDatabase(application);
        dao = db.runDao();
    }

    public int insert(Run run) {
        MainDatabase.databaseWriteExecutor.execute(() -> {
            this.newID = dao.insert(run);
            //return (int) newID;
        });
        return (int)newID;
    }

    public void update(Run run) {
        MainDatabase.databaseWriteExecutor.execute(() -> {
            dao.update(run); });
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String date = String.format("%s/%s/%s", calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.YEAR));
        return date;
    }

    public long getNewId() {
        return newID;
    }

    public LiveData<List<Run>> getAllRuns() {
        runs = dao.getAllRuns();
        return runs;
    }

    public LiveData<Run> getHighestDistance() {
        return dao.getHighestDistance();
    }

    public LiveData<Run> getHighestSpeed() {
        return dao.getHighestPace();
    }

    public LiveData<Run> getHighestDistanceToday() {
        return dao.getHighestDistanceToday(getCurrentDate());
    }


    public LiveData<Run> getHighestSpeedToday() {
        return dao.getHighestPaceToday(getCurrentDate());
    }

    public LiveData<Run> getRunById(int id) {
        return dao.getRunById(id);
    }

    public void clearTable() {
        dao.clearTable();
    }
}
