package com.example.kendalsasus.finalproject_cst2335;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/** @author Steven Adema
 *  dateCreated: 28-Dec-2017
 */
public class ThermostatAddEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat_add_entry);
        Bundle info = getIntent().getExtras();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ThermostatFragment tf = new ThermostatFragment();
        tf.setArguments(info);
        ft.add(R.id.thermostatFrameLayout, tf);
        ft.commit();
    }

}
