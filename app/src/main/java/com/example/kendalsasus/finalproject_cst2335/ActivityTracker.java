package com.example.kendalsasus.finalproject_cst2335;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ActivityTracker extends MainActivity {

    ListView activityList;
    LinearLayout buttonBar;
    ImageButton addBtn;
    ImageButton deleteBtn;
    ImageButton editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        activityList = findViewById(R.id.activity_list);
        buttonBar = findViewById(R.id.activity_button_bar);
        addBtn = findViewById(R.id.activity_button_add);
        deleteBtn = findViewById(R.id.activity_button_delete);
        editBtn = findViewById(R.id.activity_button_edit);

    }

    //enter activities:
    // Activity label - pull down with 5 options (Running, Walking, Biking, Swimming, Skating)
    // Time label - enter amount of time using textview (x2) - have selection for minutes and hours
    // Comment label - textview to enter comment
    // current time attribute - enter into dB too

    //have a listview to show activities previously entered (my activity history) - use fragment - selecting item by date shows details in fragment
    // should be able to delete or edit past entries and commit back to the dB
    // monthly statistics

    // *progress bar, button, edittext, toast, snackbar, dialog, help menu item, support one other language (French - will make another strings.xml at the end)
}
