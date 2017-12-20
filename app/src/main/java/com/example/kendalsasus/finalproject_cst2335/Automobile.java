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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* @author Kendal Howse */
public class Automobile extends MainActivity {

    public ListView listView = null;
    public ImageButton addButton = null;
    public ImageButton deleteButton = null;
    public ImageButton editButton = null;
    public Button avgButton = null;
    public List<Auto> gasList = new ArrayList<>();
    public GasListAdapter gasAdapter;
    public AutoDatabaseConnection adbc;
    public int requestCode;

    public Automobile(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automobile);

        //initialize the variables from layouts
        listView = findViewById(R.id.listView);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        avgButton = findViewById(R.id.avgLPerMonthButton);

        //connect to the database from AsyncTask
        adbc = new AutoDatabaseConnection();
        adbc.execute("1");

        //displays the arrayList from database
        gasAdapter = new GasListAdapter(this);
        listView.setAdapter(gasAdapter);

        avgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changes the visibility of unnecessary layouts
                LinearLayout avgL = findViewById(R.id.avgLayoutHolder);
                avgL.setVisibility(View.INVISIBLE);

                LinearLayout dataL = findViewById(R.id.labelLayoutHolder);
                dataL.setVisibility(View.INVISIBLE);

                LinearLayout monthL = findViewById(R.id.monthlyDisplay);
                monthL.setVisibility(View.VISIBLE);

                avgButton.setVisibility(View.INVISIBLE);

                //gets the info from database and displays it per month
                TextView displayLeft = findViewById(R.id.avgLPerMonth1);
                displayLeft.setText(adbc.getAvgPerMonth()[0]);

                TextView displayRight = findViewById(R.id.avgLPerMonth2);
                displayRight.setText(adbc.getAvgPerMonth()[1]);

            }
        });

        //to add a new gas entry
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                requestCode = 1;
                //starts AddGasEntry class where fragment is created
                Intent intent = new Intent(Automobile.this, AddGasEntry.class);
                //intent code adds an entry to the data base in AsyncTask class
                intent.putExtra("requestCode", requestCode);
                startActivityForResult(intent, requestCode);

                //sets appropriate visibility
                LinearLayout avgL = findViewById(R.id.avgLayoutHolder);
                avgL.setVisibility(View.VISIBLE);
                avgButton.setVisibility(View.VISIBLE);

                LinearLayout dataL = findViewById(R.id.labelLayoutHolder);
                dataL.setVisibility(View.INVISIBLE);

                LinearLayout monthL = findViewById(R.id.monthlyDisplay);
                monthL.setVisibility(View.INVISIBLE);

            }
        });

        //displays data about clicked item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                LinearLayout avgL = findViewById(R.id.avgLayoutHolder);
                avgL.setVisibility(View.INVISIBLE);
                avgButton.setVisibility(View.INVISIBLE);

                LinearLayout monthL = findViewById(R.id.monthlyDisplay);
                monthL.setVisibility(View.INVISIBLE);

                LinearLayout dataL = findViewById(R.id.labelLayoutHolder);
                dataL.setVisibility(View.VISIBLE);


                final long idHere = id;
                final Auto auto = gasList.get(position);
                final TextView dateField = findViewById(R.id.dateField);
                dateField.setText(auto.getDate());

                final TextView gasField = findViewById(R.id.gasField);
                gasField.setText(String.valueOf(auto.getLitresPurchased()));

                final TextView priceField = findViewById(R.id.priceField);
                priceField.setText(String.valueOf(auto.getPrice()));

                final TextView kmField = findViewById(R.id.kmField);
                kmField.setText(String.valueOf(auto.getOdometer()));

                //if an item is clicked, can be deleted with the delete button
                deleteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Automobile.this).create();
                        alertDialog.setTitle("Delete");
                        alertDialog.setMessage("Are you sure you wish to delete this entry?");
                        //dialog closes if cancel is pressed, and data is not deleted
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        //deletes the selected item if user presses delete in dialog
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adbc.deleteFromDatabase(auto);
                                gasAdapter.notifyDataSetChanged();

                                //set TextView back avg info
                                LinearLayout avgL = findViewById(R.id.avgLayoutHolder);
                                avgL.setVisibility(View.VISIBLE);

                                avgButton.setVisibility(View.VISIBLE);

                                LinearLayout dataL = findViewById(R.id.labelLayoutHolder);
                                dataL.setVisibility(View.INVISIBLE);

                                //confirms deletion
                                Snackbar.make(deleteButton, "Entry deleted", Snackbar.LENGTH_SHORT).show();

                            }
                        });

                        alertDialog.show();
                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCode = 2;
                        Auto auto = adbc.selectById((int) idHere);
                        //starts AddGasEntry class where fragment is created
                        Intent intent = new Intent(Automobile.this, AddGasEntry.class);
                        //intent code adds an entry to the data base in AsyncTask class
                        intent.putExtra("requestCode", requestCode);
                        intent.putExtra("litres", auto.getLitresPurchased());
                        intent.putExtra("price", auto.getPrice());
                        intent.putExtra("odometer", auto.getOdometer());
                        intent.putExtra("ID", idHere);
                        intent.putExtra("Position", position);
                        startActivityForResult(intent, requestCode);

                        LinearLayout avgL = findViewById(R.id.avgLayoutHolder);
                        avgL.setVisibility(View.VISIBLE);

                        avgButton.setVisibility(View.VISIBLE);

                        LinearLayout dataL = findViewById(R.id.labelLayoutHolder);
                        dataL.setVisibility(View.INVISIBLE);

                    }
                });

            }
        });

    }

    //result from adding to, and editing data base
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //adds to database

        gasAdapter.notifyDataSetChanged();
        try {
            AutoDatabaseConnection conn = new AutoDatabaseConnection();
            conn.setInfo(data.getExtras());
            conn.execute(String.valueOf(resultCode));
            gasAdapter.notifyDataSetChanged();
        }catch (NullPointerException n){

        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        //includes super class' method so toolbar still functions
        super.onOptionsItemSelected(menuItem);
        //sets help icon for automobile class
        switch(menuItem.getItemId()){
            case R.id.mi_help:
                AlertDialog.Builder custom = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout rootTag = (LinearLayout)inflater.inflate(R.layout.dialog_box, null);

                //author info
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

                //sets the text of the dialog box
                author.setText(authorText);
                version.setText(versionNum);
                inst.setText(instructions);

                custom.setView(rootTag);
                custom.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing, closes info
                    }
                });

                AlertDialog alert = custom.create();
                alert.show();
                break;
            }

        return true;
    }

    //connects to the database using AsyncTask
    private class AutoDatabaseConnection extends AsyncTask<String, Integer, String> {

        private SQLiteDatabase db;
        private Cursor results;
        private DatabaseHelper dbh;
        private int caseNum;
        private Bundle info;
        ProgressBar progressBar = findViewById(R.id.progressBar);
        private TextView avgPrice = findViewById(R.id.avgPriceMonthDisplay);
        private TextView lastMonthGas = findViewById(R.id.avgLLastMonthDisplay);
        double litresPurchased;
        double price;
        double odometer;
        long timestamp;
        long id;
        int position;


        //connects to, or adds to database
        @Override
        protected String doInBackground(String... args) {
            progressBar.setProgress(25);
            dbh = new DatabaseHelper(Automobile.this);
            db = dbh.getWritableDatabase();
            results = autoQuery();

            caseNum = 0;
            //gets passed number of result code
            try {
                caseNum = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
            }

            switch (caseNum) {
                case 1: //gets existing entries
                    getExistingEntries();
                    progressBar.setProgress(50);
                    break;
                case 10:
                    litresPurchased = info.getDouble("Gas");
                    price = info.getDouble("Price");
                    odometer = info.getDouble("Odometer");
                    timestamp = info.getLong("Timestamp");

                    writeToDatabase(litresPurchased, price, odometer, timestamp);
                    results.moveToLast();

                    gasList.add(new Auto(litresPurchased, price, odometer, timestamp));
                    break;
                case 20:
                    id = info.getLong("ID");
                    litresPurchased = info.getDouble("Gas");
                    price = info.getDouble("Price");
                    odometer = info.getDouble("Odometer");
                    position = info.getInt("Position");
                    updateDatabase(position, litresPurchased, price, odometer, id);



            }
            return "";

        }

        //update the progress, show progress bar
        @Override
        protected void onProgressUpdate(Integer... args) {

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        //updates the UI after data changes
        @Override
        protected void onPostExecute(String result) {


            avgPrice.setText(Double.toString(getLastMonthAvgPrice()));
            lastMonthGas.setText(Double.toString(getLastMonthGasPurchases()));
            gasAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }

        //selects all entries from the database and adds to the gasList
        public void getExistingEntries() {

            //sleeps to show the progress bar
            try {
                Thread.sleep(2500, 6000);
            } catch (InterruptedException e) {

            }
            results.moveToFirst();

            while (!results.isAfterLast()) {

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
        public Cursor autoQuery() {
            return results = db.rawQuery("SELECT * FROM " + DatabaseHelper.AUTO_TABLE, null);

        }

        public Auto selectById(int id) {
          Auto auto;
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.AUTO_TABLE + " WHERE " + DatabaseHelper.ID + " IS " + id, null);
            cursor.moveToFirst();

                auto = new Auto(
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.GAS)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.GAS_PRICE)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.ODOMETER)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GAS_DATE))
                );

            return auto;
        }

        public void updateDatabase(int position, double litres, double price, double odometer, long id){
            Auto auto = gasList.get(position);

           String sql = "UPDATE " + DatabaseHelper.AUTO_TABLE + " SET " +
                   DatabaseHelper.GAS + " = " + litres + ", " +
                   DatabaseHelper.GAS_PRICE + " = " + price + ", " +
                   DatabaseHelper.ODOMETER + " = " + odometer + " WHERE " +
                   DatabaseHelper.GAS_DATE + " = " + auto.getTimestamp();
            db.execSQL(sql);

            auto.setLitresPurchased(litres);
            auto.setPrice(price);
            auto.setOdometer(odometer);

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

        //deletes from database
        public void deleteFromDatabase(Auto auto){

            db.delete(DatabaseHelper.AUTO_TABLE, DatabaseHelper.GAS_DATE +"=?", new String[]{Double.toString(auto.getTimestamp())});
            gasList.remove(auto);

        }

        public long getIDFromPosition(int position){
            results = db.rawQuery("SELECT * FROM " + DatabaseHelper.AUTO_TABLE, null);


            results.moveToPosition(position);
            long num = results.getLong(results.getColumnIndex(DatabaseHelper.ID));

            return num;

        }
        //calculates the average gas price for the last month of data
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

            avg = avg/count;
            avg = Math.round(avg * 100d) / 100d;
            return avg;

        }

        public double getLastMonthGasPurchases(){
            long timestamp = System.currentTimeMillis();
            double amt = 0;

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM");
            String date = dateFormatter.format(new Date(timestamp));

            //search for everything with same month
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.AUTO_TABLE, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                String databaseDate = dateFormatter.format(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GAS_DATE))));
                if(databaseDate.equals(date)){
                    amt += cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.GAS));
                    cursor.moveToNext();
                }
            }

            return Math.round(amt * 100d) / 100d;

        }

        public String[] getAvgPerMonth(){

            String [] showMonth = new String[2];
            String [] monthArr = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            double [] numArr = new double[12];
            double amt = 0;
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM");
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.AUTO_TABLE, null);
            cursor.moveToFirst();

            for(int i = 0; i < monthArr.length; i++) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    String databaseDate = dateFormatter.format(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GAS_DATE))));
                    if (databaseDate.equals(monthArr[i])) {
                        amt += cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.GAS));

                    }
                    cursor.moveToNext();
                }
                numArr[i] = Math.round(amt * 100d) / 100d;
            }

            for (int i = 0; i < monthArr.length;i++){
                if(i == 0){
                    showMonth[0] = monthArr[i] + ": " + numArr[i] + "\n";
                }
                else if(i == 1){
                    showMonth[1] = monthArr[i] + ": " + numArr[i] + "\n";
                }
                else if(i%2 == 0) {
                    showMonth[0] += monthArr[i] + ": " + numArr[i] + "\n";
                }
                else{
                    showMonth[1] += monthArr[i] + ": " + numArr[i] + "\n";
                }
            }

            return showMonth;
        }

        //sets the bundle
        public void setInfo(Bundle info){
            this.info = info;
        }

    }

    //displays the listview
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

        public long getItemId(int position){
             return adbc.getIDFromPosition(position);

        }



    }

    //auto class used to create objects from database results
    public class Auto {
        private int id;
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

        public void setLitresPurchased(double litresPurchased) {
            this.litresPurchased = litresPurchased;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getOdometer() {
            return odometer;
        }

        public void setOdometer(double odometer) {
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

        public int getId(){
            return id;
        }

        public void setId(int id){
            this.id = id;
        }

        //converts the long to a date format
        private void convertTimestampToDate() {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd yyyy 'at' HH:mm:ss z");
            this.date = dateFormatter.format(new Date(this.timestamp));
        }
    }





}
