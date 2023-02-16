package com.example.psyps6_cw2.viewmodels;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.psyps6_cw2.database.Repository;
import com.example.psyps6_cw2.database.Run;

import java.util.ArrayList;

/*
Viewmodel for editing / viewing a run storing the run's details to be observed by activity
 */
public class EditRunActivityViewModel extends AndroidViewModel {

    private Repository repo;
    private int id;

    public EditRunActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new Repository(application);
    }

    public LiveData<Run> getRun(int id) {
        this.id = id;
        return repo.getRunById(id);
    }

    public void updateRun(Run run) {
        repo.update(run);
    }
}
