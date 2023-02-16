package com.example.psyps6_cw2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.psyps6_cw2.viewmodels.MyRunsActivityViewModel;
import com.example.psyps6_cw2.viewmodels.RunActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/*
Activity displaying previous runs. Runs can be clicked on for more information / annotation.
 */

public class MyRunsActivity extends AppCompatActivity {

    private MyRunsActivityViewModel vm;
    RunAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_runs);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.my_runs);
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
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.my_runs:
                        return true;
                }
                return false;
            }
        });

        //set recyclerview and adapter to show all runs
        vm = new ViewModelProvider(this).get(MyRunsActivityViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RunAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vm.getAllRuns().observe(this, runs ->
                adapter.setData(runs));

        //set runs in view to open EditRunActivity once clicked on
        adapter.setClickListener(run -> {
            Intent intent = new Intent(this, EditRunActivity.class);
            intent.putExtra("id", run.getId());
            startActivity(intent);
        });
    }
}