package com.example.kendalsasus.finalproject_cst2335;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Automobile extends MainActivity {

    ListView listView = null;
    ImageButton addButton = null;
    ImageButton deleteButton = null;
    ImageButton editButton = null;
    List<Auto> gasList = new ArrayList<>();
    GasListAdapter gasAdapter;
    AutoDatabaseConnection adbc;
    //private ProgressBar progressBar;

    public double getAvgMonthPrice() {
        return avgMonthPrice;
    }

    public void setAvgMonthPrice(double avgMonthPrice) {
        this.avgMonthPrice = avgMonthPrice;
    }

    double avgMonthPrice;

    public List<Auto> getGasList(){
        return gasList;
    }

    /* @author Kendal Howse */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automobile);


        listView = findViewById(R.id.listView);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        adbc = new AutoDatabaseConnection();
        adbc.execute("1");



        /*TextView avgPrice = findViewById(R.id.avgPriceMonth);
        String newAvg = avgPrice.getText().toString() + "\t" + avgMonthPrice;
        avgPrice.setText(newAvg);*/



        //creates the database to be used to get past data entries

        //gets data from the database


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                Intent intent = new Intent(Automobile.this, AddGasEntry.class);
                startActivityForResult(intent, 10);

            }
        });



        gasAdapter = new GasListAdapter(this);
        //ArrayAdapter<> gasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gasList);
        listView.setAdapter(gasAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Auto auto = gasList.get(position);
                final TextView dateField = findViewById(R.id.dateValue);
                dateField.setText(auto.getDate());

                final TextView gasField = findViewById(R.id.gasValue);
                gasField.setText(String.valueOf(auto.getLitresPurchased()));

                final TextView priceField = findViewById(R.id.priceValue);
                priceField.setText(String.valueOf(auto.getPrice()));

                final TextView kmField = findViewById(R.id.odometerValue);
                kmField.setText(String.valueOf(auto.getOdometer()));

                deleteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Automobile.this).create();
                        alertDialog.setTitle("Delete");
                        alertDialog.setMessage("Are you sure you wish to delete this entry?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adbc.deleteFromDatabase(auto);
                                gasAdapter.notifyDataSetChanged();

                                //set TextView back to blank
                                dateField.setText("");
                                gasField.setText("");
                                priceField.setText("");
                                kmField.setText("");


                                //would like to change to snackbar but wont import
                                Toast toast = Toast.makeText(Automobile.this, "Entry deleted.", Toast.LENGTH_SHORT);
                                toast.show();





                            }
                        });

                        alertDialog.show();
                    }
                });


                // TextView tv = (TextView) view;
                /*final String sqlDate = tv.getText().toString();

                //get info from database based on the listview item clicked
                Cursor getDate = db.rawQuery("SELECT * FROM " + DatabaseHelper.AUTO_TABLE + " WHERE " + DatabaseHelper.GAS_DATE + " is " +  "'" + sqlDate + "'", null);
                if (getDate != null) {*/


                  /*  getDate.moveToFirst();

                    TextView dateField = findViewById(R.id.dateValue);
                    dateField.setText(getDate.getString(getDate.getColumnIndex(DatabaseHelper.GAS_DATE)));

                    TextView gasField = findViewById(R.id.gasValue);
                    gasField.setText(getDate.getString(getDate.getColumnIndex(DatabaseHelper.GAS)));

                    TextView priceField = findViewById(R.id.priceValue);
                    priceField.setText(getDate.getString(getDate.getColumnIndex(DatabaseHelper.GAS_PRICE)));

                    TextView kmField = findViewById(R.id.odometerValue);
                    kmField.setText(getDate.getString(getDate.getColumnIndex(DatabaseHelper.ODOMETER)));

*/
            }

                /*deleteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Automobile.this).create();
                        alertDialog.setTitle("Delete");
                        alertDialog.setMessage("Are you sure you wish to delete this entry?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              // Cursor delete =  db.rawQuery("DELETE FROM " + DatabaseHelper.AUTO_TABLE  + " WHERE " + DatabaseHelper.GAS_DATE + " is " +  "'" + sqlDate + "'", null);
                               //delete.getCount();
                                db.delete(DatabaseHelper.AUTO_TABLE, DatabaseHelper.GAS_DATE +"=?", new String[]{sqlDate});
                               gasList.remove(position);
                                GasListAdapter gasAdapter = new GasListAdapter(Automobile.this); listView.setAdapter(updateAdapter);
                               Toast toast = Toast.makeText(Automobile.this, "Entry deleted.", Toast.LENGTH_SHORT);
                               toast.show();

                               //set all textFields back to empty
                                TextView dateField = findViewById(R.id.dateValue);
                                dateField.setText("");

                                TextView gasField = findViewById(R.id.gasValue);
                                gasField.setText("");

                                TextView priceField = findViewById(R.id.priceValue);
                                priceField.setText("");

                                TextView kmField = findViewById(R.id.odometerValue);
                                kmField.setText("");
                            }
                        });

                        alertDialog.show();
                    }
                });


                editButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view1){
                        //this will have to use update sql statement
                        //but first will have to open the add activity?
                    }
                });
*/


        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == 10) {
            AutoDatabaseConnection conn = new AutoDatabaseConnection();
            conn.setInfo(data.getExtras());
            conn.execute(String.valueOf(resultCode));
            gasAdapter.notifyDataSetChanged();

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.mi_help:
                AlertDialog.Builder custom = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                // custom.setView(inflater.inflate(R.layout.dialog_box, null));
                LinearLayout rootTag = (LinearLayout)inflater.inflate(R.layout.dialog_box, null);
                //author stuff
                TextView author = rootTag.findViewById(R.id.author);
                String authorText = author.getText().toString();
                authorText = authorText + " Kendal Howse";
                //version number
                TextView version = rootTag.findViewById(R.id.versionNum);
                String versionNum = version.getText().toString();
                versionNum += " 1.0";
                //instructions
                TextView inst = rootTag.findViewById(R.id.instructions);
                String instructions = inst.getText().toString();
                instructions = "\n" + instructions + "\n" +
                        "1. Add gas entry\n" +
                        "Click on the 'plus' icon at the bottom of the screen.  This will take you to a new screen where you can add all necessary data. \n" +
                        "\n2. Display gas entry data\n" +
                        "To display more data for a gas entry, simply click on an entry in the list view and more information such as amount of gas purchased and price will be shown.\n" +
                        "\n3. Delete a gas entry \n" +
                        "To delete an entry, click on the item in the list view, and click the trash can icon at the bottom of the screen\n" +
                        "\n4. Edit an entry\n" +
                        "To edit an entry, click on the item you wish to modify, and then click the pencil icon.  This will bring up a screen similar to the 'add' screen. Simply edit the values you wish to change and " +
                        "click submit.";

                author.setText(authorText);
                version.setText(versionNum);
                inst.setText(instructions);

                custom.setView(rootTag);
                custom.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });

                AlertDialog alert = custom.create();
                alert.show();
                Log.d("Toolbar", "Option 3 selected");
                break;
        }
        return true;
    }

    private class AutoDatabaseConnection extends AsyncTask<String, Integer, String>{

        private SQLiteDatabase db;
        private Cursor results;
        private DatabaseHelper dbh;
        private int caseNum;
        private Bundle info;
        ProgressBar progressBar = findViewById(R.id.progressBar);
        private TextView textView = findViewById(R.id.avgPriceMonth);




        @Override
        protected String doInBackground(String ... args){
            progressBar.setProgress(25);
            dbh = new DatabaseHelper(Automobile.this);
            db = dbh.getWritableDatabase();
            results = autoQuery();


            caseNum = 0;
            try {
                caseNum = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {}

            switch(caseNum){
                case 1: //gets existing entries
                    getExistingEntries();
                    progressBar.setProgress(50);
                    break;
                case 10:
                    double litresPurchased = info.getDouble("Gas");
                    double price = info.getDouble("Price");
                    double odometer = info.getDouble("Odometer");
                    long timestamp = info.getLong("Timestamp");

                    writeToDatabase(litresPurchased, price, odometer, timestamp);
                    results.moveToLast();

                    gasList.add(new Auto(litresPurchased, price, odometer, timestamp));
                    break;

            }
            return "";

        }

        @Override
        protected void onProgressUpdate(Integer... args) {

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //gasAdapter.notifyDataSetChanged();
            setAvgMonthPrice(getLastMonthAvgPrice());
            String newAvg = textView.getText().toString() + "\t" + avgMonthPrice;
            textView.setText(newAvg);
            gasAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }

        public void getExistingEntries(){
            //selects all entries from the database and adds to the gasList
            try{
                Thread.sleep(2500, 6000);
            }catch(InterruptedException e){

            }
            results.moveToFirst();

            while(!results.isAfterLast() ) {

                gasList.add(new Auto(
                        results.getDouble(results.getColumnIndex(DatabaseHelper.GAS)),
                        results.getDouble(results.getColumnIndex(DatabaseHelper.GAS_PRICE)),
                        results.getDouble(results.getColumnIndex(DatabaseHelper.ODOMETER)),
                        results.getLong(results.getColumnIndex(DatabaseHelper.GAS_DATE))
                ));
                results.moveToNext();
            }
        }

        //query whole auto table
        public Cursor autoQuery(){
            return results = db.rawQuery("SELECT * FROM " + DatabaseHelper.AUTO_TABLE, null);

        }
        //insert into database
        public void writeToDatabase(double litres, double price, double odometer, long timestamp){
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.GAS, litres);
            values.put(DatabaseHelper.GAS_PRICE, price);
            values.put(DatabaseHelper.ODOMETER, odometer);
            values.put(DatabaseHelper.GAS_DATE, timestamp);
            db.insert(DatabaseHelper.AUTO_TABLE, "null", values);
        }

        public void deleteFromDatabase(Auto auto){
            //Long.toString(auto.getTimestamp())
            db.delete(DatabaseHelper.AUTO_TABLE, DatabaseHelper.GAS_DATE +"=?", new String[]{"0"});
            gasList.remove(auto);

        }

        public double getLastMonthAvgPrice(){
            long timestamp = System.currentTimeMillis();
            double avg = 0;
            double count = 0;
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM");
            String date = dateFormatter.format(new Date(timestamp));

            //search for everything with same month
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.AUTO_TABLE, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                String databaseDate = dateFormatter.format(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GAS_DATE))));
                if(databaseDate.equals(date)){
                    avg += cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.GAS_PRICE));
                    count++;
                    cursor.moveToNext();
                }
            }

            return avg = avg/count;

        }

        public void setInfo(Bundle info){
            this.info = info;
        }

    }
    //this method will return an average of all gas prices
    /*public double getAvgGasPrice(){
        int numRecords = results.getCount();
        double avgPrice = 0;

        results = db.rawQuery("SELECT " + DatabaseHelper.GAS_PRICE + " FROM " + DatabaseHelper.AUTO_TABLE);
        results.moveToFirst();

        while(!results.isAfterLast() ) {

            gasList.add(results.getString(results.getColumnIndex(DatabaseHelper.GAS_DATE)));
            results.moveToNext();
        }

        return avgPrice;

    }*/

    private class GasListAdapter extends ArrayAdapter<Auto> {

        public GasListAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount() {
            return gasList.size();
        }

        public Auto getItem(int position){
            return gasList.get(position);
        }


        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = Automobile.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.list_row, null);
            TextView gasEntry = result.findViewById(R.id.row_entry);
            gasEntry.setText(getItem(position).getDate());
            return result;
        }



    }

    public class Auto {
        private double litresPurchased;
        private double price;
        private double odometer;
        private String date;
        private long timestamp;

        public Auto(){
            litresPurchased = 0;
            price = 0;
            odometer = 0;
            date = "";
        }

        public Auto(double litresPurchased, double price, double odometer, long timestamp){
            this.litresPurchased = litresPurchased;
            this.price = price;
            this.odometer = odometer;
            this.timestamp = timestamp;
            convertTimestampToDate();
        }

        public double getLitresPurchased() {
            return litresPurchased;
        }

        public void setLitresPurchased(int litresPurchased) {
            this.litresPurchased = litresPurchased;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public double getOdometer() {
            return odometer;
        }

        public void setOdometer(int odometer) {
            this.odometer = odometer;
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

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        private void convertTimestampToDate() {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd yyyy 'at' HH:mm:ss z");
            this.date = dateFormatter.format(new Date(this.timestamp));
        }
    }





}
