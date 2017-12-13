package com.example.kendalsasus.finalproject_cst2335;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.kendalsasus.finalproject_cst2335.R;

/**
 * Created by Chris on 2017-11-08.
 */

public abstract class MainActivity extends AppCompatActivity {

    public void setToolbar() {
        setActionBar((Toolbar) findViewById(R.id.toolbar));
        getActionBar().setTitle("App");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String tag = "Click on";
        switch (item.getItemId()) {
            case R.id.mi_activityTracker:
                Log.i(tag, "Activity Tracker icon");
                startActivity(new Intent(getApplicationContext(), ActivityTracker.class));
                break;
            case R.id.mi_nutritionTracker:
                Log.i(tag, "Nutrition Tracker icon");
                startActivity(new Intent(getApplicationContext(), NutritionTracker.class));
                break;
            case R.id.mi_thermostat:
                Log.i(tag, "Thermostat icon");
                startActivity(new Intent(getApplicationContext(), Thermostat.class));
                break;
            case R.id.mi_automobile:
                Log.i(tag, "Automobile icon");
                startActivity(new Intent(getApplicationContext(), Automobile.class));
                break;
        }
        return true;
    }
}
