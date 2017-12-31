package com.example.kendalsasus.finalproject_cst2335;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kendalsasus.finalproject_cst2335.nutrition.NutritionTracker;

import java.util.ArrayList;


public class ActivityTracker extends MainActivity {

    ListView activityList;
    LinearLayout buttonBar;
    ImageButton addBtn;
    ImageButton deleteBtn;
    ImageButton editBtn;
    ArrayList<Activity> activityArray = new ArrayList<>(); //arraylist of Activities
    DatabaseSetup ds;
    ActivityAdapter adapter;


    //activity adaptor to display activities in list view
    private class ActivityAdapter extends ArrayAdapter<Activity> {
        public ActivityAdapter (Context ctx){
            super(ctx, 0);
        }

        //returns number of lines in chat listview
        public int getCount(){
            return activityArray.size();
        }

        public Activity getItem(int position){
            return activityArray.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = ActivityTracker.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.list_row, null);
            TextView activityEntry = result.findViewById(R.id.row_entry);
            activityEntry.setText(getItem(position).getType());
            return result;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        activityList = findViewById(R.id.activity_list);
        buttonBar = findViewById(R.id.activity_button_bar);
        addBtn = findViewById(R.id.activity_button_add);
        deleteBtn = findViewById(R.id.activity_button_delete);
        editBtn = findViewById(R.id.activity_button_edit);

        //working with the databse - view activities existing in the database within the ListView
        ds = new DatabaseSetup();
        ds.execute("1");

        //in this case, “this” is the ActivityList, which is-A Context object
        adapter = new ActivityAdapter(this);
        activityList.setAdapter(adapter);


        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //start the MessageDetails class
                Intent intent = new Intent(ActivityTracker.this, AddActivityEntry.class);
                startActivityForResult(intent, 10);

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //adds to database

        adapter.notifyDataSetChanged();
        try {
            DatabaseSetup conn = new DatabaseSetup();
            conn.setBundle(data.getExtras());
            conn.execute(String.valueOf(resultCode));
            adapter.notifyDataSetChanged();
        }catch (NullPointerException n){

        }
    }


    //clicking the help menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        super.onOptionsItemSelected(menuItem);

        if (menuItem.getItemId() == R.id.mi_help){

            Log.i("ActivityTracker", "Help menu option selected");

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
                    //TO DO: Finish instructions

            builder.create();
            builder.show();
        }
        return true;
    }

    // Connecting to Database
    public class DatabaseSetup extends AsyncTask<String, Integer, String>
    {
        Bundle bundle;
        SQLiteDatabase db;
        DatabaseHelper dh;
        Cursor results;
        int caseNumber;

        String activity;
        String duration;
        String comment;
        String date;
        int intDuration;

        @Override
        protected String doInBackground(String... args)
        {
            Log.i("ActivityTracker", "In doInBackground");
            dh = new DatabaseHelper(ActivityTracker.this);
            //create a local variable for the SQLite database
            db = dh.getWritableDatabase();

            //query for results from db (ID, Type, Duration, Date, Comment)
            results = activityQuery();

            //caseNumber = resultCode
            caseNumber = 0;

            try {
                caseNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                //do nothing
            }

            switch (caseNumber) {
                case 1:
                    Log.i("ActivityTracker", "In doInBackground CASE 1");
                    //resets the iteration of results
                    results.moveToFirst();

                    //How many rows in the results:
                    int numResults = results.getCount();

                    for (int i= 0; i < numResults; i++){
                        activityArray.add(new Activity(
                                results.getString(results.getColumnIndex(DatabaseHelper.ACTIVITY_TYPE)),
                                results.getInt(results.getColumnIndex(DatabaseHelper.ACTIVITY_DURATION)),
                                results.getString(results.getColumnIndex(DatabaseHelper.ACTIVITY_DATE)),
                                results.getString(results.getColumnIndex(DatabaseHelper.ACTIVITY_COMMENT))
                        ));
                        results.moveToNext();
                    }
                    break;
                case 10:
                    Log.i("ActivityTracker", "In doInBackground CASE 10");
                    activity = bundle.getString("activityType");
                    duration = bundle.getString("activityDuration");
                    try {
                        intDuration = Integer.parseInt(duration);
                    } catch (NumberFormatException nfe){
                        intDuration = 1;
                    }
                    comment = bundle.getString("activityComment");
                    date = bundle.getString("activityDate");

                    ds.updateActivityDB(activity, intDuration, comment, date);
                    Log.i("ActivityTracker", "Got new Activity data from bundle");
                    results.moveToLast();

                    activityArray.add(new Activity(activity, intDuration, date, comment));
                    break;
                }
            return "";
        }


        @Override
        protected void onProgressUpdate(Integer... args)
        {
            //fill this out
        }

        @Override
        protected void onPostExecute(String result)
        {
            adapter.notifyDataSetChanged();
            activityList.setVisibility(View.VISIBLE);
        }

        // get results for content of Activity Databse
        private Cursor activityQuery() {
            return db.query(DatabaseHelper.ACTIVITY_TABLE,
                    new String[]{DatabaseHelper.ID, DatabaseHelper.ACTIVITY_TYPE, DatabaseHelper.ACTIVITY_DURATION, DatabaseHelper.ACTIVITY_DATE, DatabaseHelper.ACTIVITY_COMMENT},
                    null, null, null, null, DatabaseHelper.ACTIVITY_DATE + " DESC");
        }

        public void updateActivityDB(String type, int duration, String date, String comment){
            //Create new row data
            ContentValues newData = new ContentValues();
            newData.put(DatabaseHelper.ACTIVITY_TYPE, type);
            newData.put(DatabaseHelper.ACTIVITY_DURATION, duration);
            newData.put(DatabaseHelper.ACTIVITY_DATE, date);
            newData.put(DatabaseHelper.ACTIVITY_COMMENT, comment);

            //Then insert
            db.insert(DatabaseHelper.ACTIVITY_TABLE, "" , newData);
        }

        public void setBundle(Bundle bundle) {
            this.bundle = bundle;
        }

    }

    //Data class for activities in database
    public class Activity {

        private String type;
        private int duration;
        private String date;
        private String comment;

        public Activity (){
            type = "";
            duration = 0;
            date = "";
            comment = "";
        }

        public Activity (String type, int duration, String date, String comment){
            this.type = type;
            this.duration = duration;
            this.date = date;
            this.comment = comment;
        }

        public String getType(){
            return type;
        }

        public void setType(String type){
            this.type = type;
        }

        public int getDuration(){
            return duration;
        }

        public void setDuration(int duration){
            this.duration = duration;
        }

        public String getDate(){
            return date;
        }

        public void setDate(String date){
            this.date = date;
        }

        public String getComment(){
            return comment;
        }

        public void setComment(String comment){
            this.comment = comment;
        }

    }

}
