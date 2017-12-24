package com.example.kendalsasus.finalproject_cst2335;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        super.onOptionsItemSelected(menuItem);

        if (menuItem.getItemId() == R.id.mi_help){

            Log.d("Toolbar", "Option 3 selected");

            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTracker.this);
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();
            final LinearLayout rootTag = (LinearLayout)inflater.inflate(R.layout.dialog_box, null);
            final TextView author = (TextView)rootTag.findViewById(R.id.author);
            final TextView version = (TextView)rootTag.findViewById(R.id.versionNum);
            final TextView instructions = (TextView)rootTag.findViewById(R.id.instructions);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_box, null))

                    //set view as root tag
                    .setView(rootTag)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do nothing - except exit dialog
                            Toast t = Toast.makeText(ActivityTracker.this, "Exited Help", Toast.LENGTH_LONG);
                            t.show();
                        }
                    });

                    // Add content for activity help
                    author.setText("Author: Melissa Rajala");
                    version.setText("Version: 1.0");
                    instructions.setText("1.");



            builder.create();
            builder.show();

        }

        return true;

    }

}
