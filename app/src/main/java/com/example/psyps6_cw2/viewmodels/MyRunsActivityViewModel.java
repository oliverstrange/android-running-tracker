package com.example.psyps6_cw2.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.psyps6_cw2.database.Repository;
import com.example.psyps6_cw2.database.Run;

import java.util.List;

/*
Viewmodel for past runs activity storing all runs to be observed by activity / adapter for showing all runs
 */
public class MyRunsActivityViewModel extends AndroidViewModel {

    private Repository repo;
    LiveData<List<Run>> runs;

    public MyRunsActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new Repository(application);
    }

    public LiveData<List<Run>> getAllRuns(){
        runs = repo.getAllRuns();
        return runs;
    }
}
