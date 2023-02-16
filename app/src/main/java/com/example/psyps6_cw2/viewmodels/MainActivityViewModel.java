package com.example.psyps6_cw2.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.psyps6_cw2.database.Repository;
import com.example.psyps6_cw2.database.Run;

import java.util.List;

/*
Main activity viewmodel storing runs to be observed my activity for stats
 */
public class MainActivityViewModel extends AndroidViewModel {

    private Repository repo;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new Repository(application);
    }

    public LiveData<Run> getHighestDistance() {
        return repo.getHighestDistance();
    }

    public LiveData<Run> getHighestSpeed() {
        return repo.getHighestSpeed();
    }

    public LiveData<Run> getHighestDistanceToday() {
        return repo.getHighestDistanceToday();
    }

    public LiveData<Run> getHighestSpeedToday() {
        return repo.getHighestSpeedToday();
    }


}
