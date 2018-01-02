package com.example.kendalsasus.finalproject_cst2335;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* @author Steven Adema
 * dateCreated: 28-Dec-2017 */

public class Thermostat extends MainActivity {

    public ListView thermostatLV;
    public ImageButton addBTN;
    public ImageButton deleteBTN;
    public ImageButton editBTN;
    public List<ThermostatSetting> thermostatArrayList = new ArrayList<>();
    public ThermostatAdapter thermostatAdapter;
    public ThermostatDBConn thermostatDBC;
    public int requestCode;
    public int currentTemp;
    public String ACTIVITY_NAME = "Thermostat";


    //====================== Thermostat Adapter Class Start =================================//

    private class ThermostatAdapter extends ArrayAdapter<ThermostatSetting> {

        public ThermostatAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount() {
            return thermostatArrayList.size();
        }

        public ThermostatSetting getItem(int position){
            return thermostatArrayList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = Thermostat.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.list_row, null);
            TextView thermostatEntered = result.findViewById(R.id.row_entry);
            thermostatEntered.setText(getItem(position).getDay() + " @ " + getItem(position).getHour() + " --> " + getItem(position).getTemp() + "°C");
            return result;
        }
        //return ID of selected ThermostatSetting
        public long getItemId(int position){ return thermostatDBC.idbyPos(position); }
    }

    //====================== Thermostat Adapter Class End ==================================//

    //====================== Thermostat Class Continue ====================================//

    //default constructor
    public Thermostat(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        // intialize vars from layout file
        thermostatLV = findViewById(R.id.thermostat_list);
        addBTN = findViewById(R.id.activity_button_add);
        deleteBTN = findViewById(R.id.activity_button_delete);
        editBTN = findViewById(R.id.activity_button_edit);

        // create database connection
        thermostatDBC = new ThermostatDBConn();
        thermostatDBC.execute("1");

        // display array list of thermostatsetting objects
        thermostatAdapter = new ThermostatAdapter(this);
        thermostatLV.setAdapter(thermostatAdapter);

        // GUI to add new thermostat setting
        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                requestCode = 1;
                Intent intent = new Intent( Thermostat.this, ThermostatAddEntry.class);
                intent.putExtra("requestCode", requestCode);
                startActivityForResult(intent, requestCode);
            }
        });

        // Get current position of selected listview item
        thermostatLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final long currentItem = id;
                final ThermostatSetting ts = thermostatArrayList.get(position); //selected listview item

                // once an item is selected...

                //Show detailed info at bottom of the screen
                setFooterText(ts);

                // can delete
                deleteBTN.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Thermostat.this).create();
                        alertDialog.setTitle("Deletion Warning");
                        alertDialog.setMessage("Are you sure you wish to Delete?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                thermostatDBC.deleteDB(ts);
                                thermostatAdapter.notifyDataSetChanged();
                                Snackbar kitkat = Snackbar.make(deleteBTN, "Thermostat Setting Deleted", Snackbar.LENGTH_SHORT);
                                View snackBarView = kitkat.getView();
                                snackBarView.setBackgroundColor(-16776961);
                                kitkat.show();
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Snackbar kitkat = Snackbar.make(deleteBTN, "Thermostat Setting Unchanged", Snackbar.LENGTH_SHORT);
                                View snackBarView = kitkat.getView();
                                snackBarView.setBackgroundColor(-16776961);
                                kitkat.show();
                            }
                        });

                        alertDialog.show();
                    }
                });

                // can edit
                editBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCode = 2;


                        // current ThermostatSetting is used to populate a new entry in AsyncTask database
                        ThermostatSetting ts = thermostatDBC.selectById((int) currentItem);
                        Intent intent = new Intent(Thermostat.this, ThermostatAddEntry.class);
                        intent.putExtra("requestCode", requestCode);
                        intent.putExtra("day", ts.getDay());
                        intent.putExtra("hour", ts.getHour());
                        intent.putExtra("temp", ts.getTemp());
                        intent.putExtra("ID", currentItem);
                        intent.putExtra("Position", position);
                        startActivityForResult(intent, requestCode);
                    }
                });
            }
        });
    }

    //refresh ListView when back button is pressed in Add Thermostat Activity
    @Override
    public void onResume() {
        super.onResume();
        thermostatAdapter.notifyDataSetChanged();
    }

    //method to display additional info @ bottom of the screen
    private void setFooterText(ThermostatSetting ts) {

        TextView footer = findViewById(R.id.footer);
        String day = ts.getDay();
        String hour = ts.getHour();
        String temp = String.valueOf(ts.getTemp());
        String date = ts.getDate();

        String combo = "On " + day + " @ " + hour + " temperature set to " + temp +
                "°C \n date created: " + date;

        footer.setText(combo);
    }

    // Called on Database change
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //notify adapter
        thermostatAdapter.notifyDataSetChanged();

        // create connection to database and notify listview that adapter has been changed
        try {
            ThermostatDBConn conn = new ThermostatDBConn();
            conn.setInfo(data.getExtras());
            conn.execute(String.valueOf(resultCode));
            thermostatAdapter.notifyDataSetChanged();
        }catch (NullPointerException n){ Log.e(ACTIVITY_NAME, "NullPointerException", n); }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        super.onOptionsItemSelected(menuItem);

        //create help menu instructions
        //structure of this AlertDialog box credited to Chris Labelle
        switch(menuItem.getItemId()){
            case R.id.mi_help:
                AlertDialog.Builder custom = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout rootTag = (LinearLayout)inflater.inflate(R.layout.dialog_box, null);

                TextView author = rootTag.findViewById(R.id.author);
                String authorText = author.getText().toString();
                authorText = authorText + " Steven Adema";
                TextView version = rootTag.findViewById(R.id.versionNum);
                String versionNum = version.getText().toString();
                versionNum += " 1.0";

                TextView inst = rootTag.findViewById(R.id.instructions);
                String instructions = inst.getText().toString();
                instructions = "\n" + instructions + "\n" +
                        "1. Add a new Thermostat Setting:\n" +
                        "Click on the 'plus' icon at the bottom of the screen.  This will open an new screen where you can create a new thermostat rule by day and time. \n" +
                        "\n2. Edit a Thermostat Setting\n" +
                        "Click the pencil icon on the bottom right of the screen.  This will open a new screen where you can edit the currently selected rule.\n" +
                        "\n3. Delete a Thermostat Setting \n" +
                        "Click on the trash symbol at the bottom center of the screen. \n This will summon a popup alert to confirm your decision.  Press DELETE or CANCEL to enact the desired operation\n";

                author.setText(authorText);
                version.setText(versionNum);
                inst.setText(instructions);
                custom.setView(rootTag);
                custom.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }  //close info
                });

                AlertDialog alert = custom.create();
                alert.show();
                break;
        }

        return true;
    }

    // ==================== ThermostatDBConn Class Start ================================= //

    private class ThermostatDBConn extends AsyncTask<String, Integer, String> {

        private SQLiteDatabase sqLiteDatabase;
        private DatabaseHelper dbHelper;
        private int caseCode;
        private Bundle info;
        private Cursor cursor;
        long id; int temp; int position; String day; String hour; long timestamp;

        //instantiate loading bar
        ProgressBar progressBar = findViewById(R.id.thermostatProgressBar);

        @Override
        protected String doInBackground(String... args) {
            progressBar.setProgress(30);
            dbHelper = new DatabaseHelper(Thermostat.this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            cursor = query();
            // cursor = sort();

            caseCode = 0;
            try { caseCode = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) { Log.e(ACTIVITY_NAME, "NumberFormatException", e);}

            switch (caseCode) {

                //get current entries
                case 1:
                    getExistingEntries();
                    progressBar.setProgress(60);
                    break;

                //get thermostatsetting metrics for arraylist
                case 3:
                    day = info.getString("day");
                    hour = info.getString("hour");
                    temp = info.getInt("temp");
                    timestamp = info.getLong("Timestamp");

                    writeDB(day, hour, temp, timestamp);
                    cursor.moveToLast();
                    thermostatArrayList.add(new ThermostatSetting(day, hour, temp, timestamp));
                    thermostatAdapter.notifyDataSetChanged();  //new
                    break;

                //update database
                case 4:
                    position = info.getInt("Position");
                    day = info.getString("day");
                    hour = info.getString("hour");
                    temp = info.getInt("temp");
                    id = info.getLong("ID");
                    updateDB(position, day, hour, temp, id);
            }
            return "";

        }

        protected void onProgressUpdate(Integer... args) {
            progressBar.setProgress(args[0]);
        }

        // Update GUI on database change
        @Override
        protected void onPostExecute(String result) {
            thermostatAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            thermostatLV.setVisibility(View.VISIBLE);
        }

        // Select * from database
        public void getExistingEntries() {

            // sleep to show progress bar
            try {
                Thread.sleep(1500, 2000);
            } catch (InterruptedException e) {Log.e(ACTIVITY_NAME, "InteruptedException", e);}

            // brint cursor to top of table row
            cursor.moveToFirst();

            // Loop to get retrieve database fields
            while (!cursor.isAfterLast()) {
                thermostatArrayList.add(new ThermostatSetting(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.THERMOSTAT_DAY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.THERMOSTAT_HOUR)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.THERMOSTAT_TEMP)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.THERMOSTAT_DATE))
                ));
                cursor.moveToNext();
            }
        }
        // ------------- START of Methods for deleting, editing and updating Thermostat Table ----------- //

        // method for deleting row
        public void deleteDB(ThermostatSetting ts){
            sqLiteDatabase.delete(DatabaseHelper.THERMOSTAT_TABLE, DatabaseHelper.THERMOSTAT_DATE +"=?", new String[]{Double.toString(ts.getTimestamp())});
            thermostatArrayList.remove(ts);
        }

        // method for editing rows
        public void updateDB(int position, String day, String hour, Integer temp, long id){
            ThermostatSetting ts = thermostatArrayList.get(position);

            // query to update thermostat table
            String query = "UPDATE " + DatabaseHelper.THERMOSTAT_TABLE + " SET " +
                    DatabaseHelper.THERMOSTAT_DAY + " = '" + day + "', " +
                    DatabaseHelper.THERMOSTAT_HOUR + " = '" + hour + "', " +
                    DatabaseHelper.THERMOSTAT_TEMP + " = " + temp + " WHERE " +
                    DatabaseHelper.ID + " = '" + id + "'";

            sqLiteDatabase.execSQL(query);

            ts.setDay(day);
            ts.setHour(hour);
            ts.setTemp(temp);
        }

        //method for adding new rows
        public void writeDB(String day, String hour, Integer temp, long timestamp){
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.THERMOSTAT_DAY, day);
            values.put(DatabaseHelper.THERMOSTAT_HOUR, hour);
            values.put(DatabaseHelper.THERMOSTAT_TEMP, temp);
            values.put(DatabaseHelper.THERMOSTAT_DATE, timestamp);
            sqLiteDatabase.insert(DatabaseHelper.THERMOSTAT_TABLE, "null", values);
        }


        // Get all data from THERMOSTAT_TABLE
        public Cursor query() {
            String table = getTableAsString(sqLiteDatabase, "THERMOSTAT_TABLE");
            Log.i("zxc", table);
            return cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.THERMOSTAT_TABLE, null);
        }

        //method to print the Thermostat Table in Logcat
        //credit to user A.Wan on stackoverflow https://stackoverflow.com/questions/27003486/printing-all-rows-of-a-sqlite-database-in-android
        //
        public String getTableAsString(SQLiteDatabase db, String tableName) {
            Log.d("xvc", "getTableAsString called");
            String tableString = String.format("Table %s:\n", tableName);
            Cursor allRows  = db.rawQuery("SELECT * FROM " + DatabaseHelper.THERMOSTAT_TABLE,null);
            if (allRows.moveToFirst() ){
                String[] columnNames = allRows.getColumnNames();
                do {
                    for (String name: columnNames) {
                        tableString += String.format("%s: %s\n", name,
                                allRows.getString(allRows.getColumnIndex(name)));
                    }
                    tableString += "\n";
                } while (allRows.moveToNext());
            }

            return tableString;
        }

        // sort THERMOSTAT_TABLE and order by day of the week, then hour
        public Cursor sort() {
            return cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.THERMOSTAT_TABLE +
                    " ORDER BY \n" +
                    "     CASE\n" +
                    "          WHEN day = 'Sunday' THEN 1\n" +
                    "          WHEN day = 'Monday' THEN 2\n" +
                    "          WHEN day = 'Tuesday' THEN 3\n" +
                    "          WHEN day = 'Wednesday' THEN 4\n" +
                    "          WHEN day = 'Thursday' THEN 5\n" +
                    "          WHEN day = 'Friday' THEN 6\n" +
                    "          WHEN day = 'Saturday' THEN 7\n" +
                    "     END ASC, hour DESC;", null);
        }

        // --------------------- END of Methods for deleting, editing and updating Thermostat Table ---------- //

        // Get Row from THERMOSTAT_TABLE where ID matches passed id.
        public ThermostatSetting selectById(int id) {
            ThermostatSetting ts;
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.THERMOSTAT_TABLE + " WHERE " + DatabaseHelper.ID + " IS " + id, null);
            cursor.moveToFirst();

            // get info from the only row with matching IDs
            ts = new ThermostatSetting(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.THERMOSTAT_DAY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.THERMOSTAT_HOUR)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.THERMOSTAT_TEMP)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.THERMOSTAT_DATE))
            );
            return ts;
        }

        // method to retrieve ID of ThermostatSetting setting by row number
        public long idbyPos(int index){
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.THERMOSTAT_TABLE, null);
            cursor.moveToPosition(index);
            long foundID = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ID));
            return foundID;
        }

        // package the bundle
        public void setInfo(Bundle info){
            this.info = info;
        }

    }

    // ThermostatSetting object to hold in database
    public class ThermostatSetting {
        private int id;
        private String day;
        private String hour;
        private Integer temp;
        private String date;
        private long timestamp;

        //default constructor
        public ThermostatSetting(){}

        //parameterized constructor
        public ThermostatSetting(String day, String hour, Integer temp, long timestamp){
            this.day = day;
            this.hour = hour;
            this.temp = temp;
            this.timestamp = timestamp;
            convertTimestampToDate();
        }

        public String getDay() {
            return day;
        }
        public void setDay(String day) { this.day = day; }

        public String getHour() {
            return hour;
        }
        public void setHour(String hour) {
            this.hour = hour;
        }

        public Integer getTemp() {
            return temp;
        }
        public void setTemp(Integer temp) {
            this.temp = temp;
        }

        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getId(){
            return id;
        }
        public void setId(int id){
            this.id = id;
        }

        // method to convert long to date format
        private void convertTimestampToDate() {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd yyyy 'at' HH:mm:ss z");
            this.date = dateFormatter.format(new Date(this.timestamp));
        }
    }

}

//====================== Thermostat Class END ===========================================//
