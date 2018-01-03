package com.example.kendalsasus.finalproject_cst2335;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kendalsasus.finalproject_cst2335.nutrition.NutritionTracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ActivityTracker extends MainActivity {

    public static String ACTIVITY_NAME = "ActivityTracker";
    public ListView activityList;
    public LinearLayout buttonBar;
    public LinearLayout detailsBar;
    public LinearLayout monthlyBar;
    public ImageButton addBtn;
    public ImageButton deleteBtn;
    public ImageButton editBtn;
    public ArrayList<Activity> activityArray = new ArrayList<>(); //arraylist of Activities
    public DatabaseSetup ds;
    public ActivityAdapter adapter;
    public int requestCode;
    public ProgressBar progress;
    public TextView details;
    public TextView monthly;

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
        detailsBar = findViewById(R.id.activity_details_bar);
        monthlyBar = findViewById(R.id.activity_monthly_bar);


        progress = (ProgressBar) findViewById(R.id.activityProgress);
        progress.setVisibility(View.VISIBLE);

        //working with the databse - view activities existing in the database within the ListView
        ds = new DatabaseSetup();
        ds.execute("1");

        //in this case, “this” is the ActivityList, which is-A Context object
        adapter = new ActivityAdapter(this);
        activityList.setAdapter(adapter);


        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //start the AddActivityEntry class
                Intent intent = new Intent(ActivityTracker.this, AddActivityEntry.class);
                startActivityForResult(intent, 10);

            }
        });

        // Response to clicking listview item - get position
        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //show details of selected activity
                getActivityDetails(activityArray.get(position));
//                detailsBar.setVisibility(View.VISIBLE);

                //check for monthly activites
                getMonthlyMinutes();

                final String activity = activityArray.get(position).getType().toString();
                final String comment = activityArray.get(position).getComment().toString();
                final String duration = String.valueOf(activityArray.get(position).getDuration());
                final long currentID = id;


                // delete selected entry from list
                deleteBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.i(ACTIVITY_NAME, "Delete button selected");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTracker.this);
                        builder.setTitle("Delete");
                        builder.setMessage("Do you want to delete the selected activity?");
                        // Add the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                ds.deleteActivity(position);
                                Snackbar.make(findViewById(R.id.activity_list), "You deleted " + activity,
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                                //clear the details
                                details = (TextView) findViewById(R.id.details);
                                details.setText("");
                                if (position == 0){
                                    monthly = (TextView) findViewById(R.id.monthly);
                                    monthly.setText("Total duration for January is 0 minutes.");
                                }
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                //do nothing but go back to activity list
                            }
                        });
                        // Create the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });

                // edit selected entry from list
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //go back to the add fragment and use result code to modify the current list item
                        //start the AddActivityEntry class
                        requestCode = 20;
                        Bundle activityBundle = new Bundle();
                        activityBundle.putLong("ID", currentID);
                        activityBundle.putString("Type", activity);
                        activityBundle.putString("Duration", duration);
                        activityBundle.putString("Comment", comment);
                        activityBundle.putInt("RequestCode", requestCode);

                        Intent intent = new Intent(ActivityTracker.this, AddActivityEntry.class);
                        intent.putExtras(activityBundle);
                        startActivityForResult(intent, requestCode);
                    }
                });
            }
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            Toast t = Toast.makeText(ActivityTracker.this, R.string.activity_exit_help, Toast.LENGTH_LONG);
                            t.show();
                        }
                    });

                    // Add content for activity help
                    author.setText(R.string.activity_author);
                    version.setText(R.string.activity_version);
                    instructions.setText(
                            "1. Add a new Activity Entry \n" +
                                    "Click the '+' button at the bottom of the application to add a new activity. You will be prompted for the activity type." +
                                    "You may select from 5 different activities: Running, Walking, Biking, Swimming, and Skating." +
                                    "Enter the duration of the activity (in minutes) as well as any comments related to the activity." +
                                    "Once a new activity has been submitted, it is visible in the main screen of the Activity application." +
                                    "Click on the activities to view the details. \n" +
                                    "2. Edit an Existing Activity \n" +
                                            "Select an activity to edit the details. Click the edit button at the bottom of the application to bring up" +
                                    "the edit dialog. \n" +
                                    "3. Delete an Existing Activity \n" +
                                    "Click the trash button at the bottom of the application to delete the selected activity."
                    );


            builder.create();
            builder.show();
        }
        return true;
    }

    //get details and show under list of activities
    public void getActivityDetails(Activity activity){
        details = (TextView) findViewById(R.id.details);

        String date = activity.getDate();
        String type = activity.getType();
        String comment = activity.getComment();
        int duration = activity.getDuration();

        details.setText("Activity: " + type + "\n Duration: " + duration + " minutes \n Comments: " + comment + "\n Date: " + date);

    }

    //get monthly stats
    public void getMonthlyMinutes(){
        monthly = (TextView) findViewById(R.id.monthly);
        int minutes = 0;
//        Calendar date = Calendar.getInstance();
//        int month = date.get(Calendar.MONTH);

        for (int i = 0; i < activityArray.size(); i++){
            minutes += activityArray.get(i).getDuration();
        }
        monthly.setText("Total duration for January: " + minutes + " minutes.");
    }


    // Connecting to Database
    public class DatabaseSetup extends AsyncTask<String, Integer, String> {
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

        long id;


        @Override
        protected String doInBackground(String... args) {
            Log.i(ACTIVITY_NAME, "In doInBackground");
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
                    Log.i(ACTIVITY_NAME, "In doInBackground Case 1");
                    updateListView();
                    break;
                case 10:
                    //add entry
                    Log.i(ACTIVITY_NAME, "In doInBackground Case 10");
                    // sleep for progress to be visible
                    try {
                        Thread.sleep(1500, 2000);
                    } catch (InterruptedException e) {
                        Log.e(ACTIVITY_NAME, "InteruptedException", e);
                    }
                    progress.setProgress(70);
                    activity = bundle.getString("activityType");
                    duration = bundle.getString("activityDuration");
                    try {
                        intDuration = Integer.parseInt(duration);
                    } catch (NumberFormatException nfe){
                        intDuration = 0;
                    }
                    comment = bundle.getString("activityComment");
                    date = bundle.getString("activityDate");

                    ds.addActivityDB(activity, intDuration, date, comment);
                    Log.i("ActivityTracker", "Got new Activity data from bundle");
                    results.moveToLast();

                    activityArray.add(new Activity(activity, intDuration, date, comment));

                    break;
                case 20:
                    Log.i(ACTIVITY_NAME, "In doInBackground Case 20");
                    //update existing entry
                    progress.setProgress(70);
                    id = bundle.getLong("activityID");
                    activity = bundle.getString("activityType");
                    duration = bundle.getString("activityDuration");
                    try {
                        intDuration = Integer.parseInt(duration);
                    } catch (NumberFormatException nfe){
                        intDuration = 1;
                    }
                    comment = bundle.getString("activityComment");
                    date = bundle.getString("activityDate");
                    ds.updateExistingActivity(id, activity, intDuration, date, comment);

                    break;
                }
            return "";
        }


        @Override
        protected void onProgressUpdate(Integer... value) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(value[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            adapter.notifyDataSetChanged();
            activityList.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }

        // get results for content of Activity Databse
        private Cursor activityQuery() {
            return db.query(DatabaseHelper.ACTIVITY_TABLE,
                    new String[]{DatabaseHelper.ID, DatabaseHelper.ACTIVITY_TYPE, DatabaseHelper.ACTIVITY_DURATION, DatabaseHelper.ACTIVITY_DATE, DatabaseHelper.ACTIVITY_COMMENT},
                    null, null, null, null, DatabaseHelper.ACTIVITY_DATE + " DESC");
        }

        //add new activity
        public void addActivityDB(String type, int duration, String date, String comment){
            //Create new row data
            ContentValues newData = new ContentValues();
            newData.put(DatabaseHelper.ACTIVITY_TYPE, type);
            newData.put(DatabaseHelper.ACTIVITY_DURATION, duration);
            newData.put(DatabaseHelper.ACTIVITY_DATE, date);
            newData.put(DatabaseHelper.ACTIVITY_COMMENT, comment);

            //Then insert
            db.insert(DatabaseHelper.ACTIVITY_TABLE, "" , newData);
        }

        public void updateExistingActivity(long id, String type, int duration, String date, String comment){
            String updateSQL = "UPDATE " + DatabaseHelper.ACTIVITY_TABLE +
                    " SET " +
                    DatabaseHelper.ACTIVITY_TYPE + " = '" + type + "', " +
                    DatabaseHelper.ACTIVITY_DURATION + " = " + duration + ", " +
                    DatabaseHelper.ACTIVITY_DATE + " = '" + date + "', " +
                    DatabaseHelper.ACTIVITY_COMMENT + " = '" + comment +
                    "' WHERE " +
                    DatabaseHelper.ID + " = '" + id + "'";
            db.execSQL(updateSQL);
            //update array list too..
            int integerId = (int) id;
            activityArray.set(integerId, new Activity(type, duration, date, comment));
        }


        public void updateListView(){
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
        }

        public void deleteActivity(int position){
            db.delete(DatabaseHelper.ACTIVITY_TABLE, DatabaseHelper.ACTIVITY_DATE + "= '" + activityArray.get(position).getDate() + "'" , null);
            activityArray.remove(position);
            adapter.notifyDataSetChanged();
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
