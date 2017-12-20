package com.example.kendalsasus.finalproject_cst2335;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AddGasEntry extends AppCompatActivity {
    Automobile auto = new Automobile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gas_entry);


        Log.i("Phone", "This is a phone");
        Bundle info = getIntent().getExtras();
        FragmentTransaction ft = getFragmentManager().beginTransaction().addToBackStack(null);
        GasFragment gasFragment = new GasFragment(auto);
        gasFragment.setArguments(info);
        ft.add(R.id.addFrameLayout, gasFragment);
        ft.commit();

    }

}
