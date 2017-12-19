package com.example.kendalsasus.finalproject_cst2335.nutrition;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kendalsasus.finalproject_cst2335.R;
import com.example.kendalsasus.finalproject_cst2335.nutrition.NutritionFragment;

/**
 * Created by Chris on 2017-12-09.
 */

public class AddNutritionEntry extends AppCompatActivity {

    private final static String ACTIVITY_NAME = "AddNutritionEntryEntry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nutrition_entry);
        Log.i(ACTIVITY_NAME, "Device is phone");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        NutritionFragment nutritionFragment = new NutritionFragment();
        Bundle info = new Bundle();
        info.putInt("processCode", 10);
        nutritionFragment.setArguments(info);
        nutritionFragment.setTablet(false);
        transaction.replace(R.id.add_nutrition_frame_layout, nutritionFragment);
        transaction.commit();
    }
}
