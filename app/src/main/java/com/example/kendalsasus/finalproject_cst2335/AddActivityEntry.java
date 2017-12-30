package com.example.kendalsasus.finalproject_cst2335;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Melissa Rajala on 2017-12-30.
 */

public class AddActivityEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity_entry);

        Bundle info = getIntent().getExtras();

        //start Transaction to insert fragment in screen:
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ActivityFragment af = new ActivityFragment();

        af.setArguments(info);
        ft.add(R.id.activity_frame_layout, af);
        ft.commit();
    }

}
