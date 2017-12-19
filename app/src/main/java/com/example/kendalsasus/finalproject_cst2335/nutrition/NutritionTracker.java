package com.example.kendalsasus.finalproject_cst2335.nutrition;



import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kendalsasus.finalproject_cst2335.DatabaseHelper;
import com.example.kendalsasus.finalproject_cst2335.MainActivity;
import com.example.kendalsasus.finalproject_cst2335.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NutritionTracker extends MainActivity {

    /* @author Chris Labelle */

    private final static String ACTIVITY_NAME = "NutritionTracker";
    private ArrayList<Food> foods;
    private ListView foodList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrtion_tracker);

        // initialize ArrayList and ListView fields
        foods = new ArrayList<>();
        foodList = findViewById(R.id.food_list);

        // initialize ImageButtons relating to Add, Delete, and Edit.
        ImageButton nutritionAddEntryButton = findViewById(R.id.nutrition_add_entry_button);
        ImageButton nutritionDeleteEntryButton = findViewById(R.id.nutrition_delete_entry_button);
        ImageButton nutritionEditEntryButton = findViewById(R.id.nutrition_edit_entry_button);

        nutritionAddEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // load the add_nutrition fragment if user clicks Add.
                startActivityForResult(new Intent(NutritionTracker.this, AddNutritionEntry.class), 0);
            }
        });

        nutritionDeleteEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete from database if user clicks on Delete.
                new NutritionQuery().execute("-1");
            }

        });

        nutritionEditEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do this if user clicks on Edit.
            }
        });

        // run everytime, gets entries that have been sitting in database before app start.
        new NutritionQuery().execute("1");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == 10) {
            NutritionQuery nutritionQuery = new NutritionQuery();
            nutritionQuery.setBundle(data.getExtras());
            nutritionQuery.execute(String.valueOf(resultCode));
        }
    }



    private class NutritionQuery extends AsyncTask<String, Integer, String> {

        private SQLiteDatabase database;
        private Cursor dbCursor;
        private NutritionAdapter nutritionAdapter; // used to access ListView Adapter.
        private int processCode;
        private Bundle info; // used to pass information accross objects and into database

        @Override
        protected String doInBackground(String... args) {

            DatabaseHelper dbHelper = new DatabaseHelper(NutritionTracker.this);
            database = dbHelper.getWritableDatabase();
            dbCursor = nutritionTableQuery();

            processCode = 0;
            try {
                processCode = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {}

            // process code depends on what button user clicks
            // 10 is if submit button is pressed in add_nutrition fragment
            switch (processCode) {
                case 1 :
                    loadPreFoods();
                    break;
                case 10 :
                    String item = info.getString("Item");
                    double calories = info.getDouble("Calories");
                    double fat = info.getDouble("Fat");
                    double carbs = info.getDouble("Carbohydrates");
                    long timestamp = info.getLong("Timestamp");
                    foods.add(new Food(item, calories, fat, carbs, timestamp));
                    writeToDatabase(item, calories, fat, carbs, timestamp);
                    break;
            }

            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... args) {

        }

        @Override
        protected void onPostExecute(String result) {
            nutritionAdapter = new NutritionAdapter(NutritionTracker.this);
            switch (processCode) {
                case 1 :
                    foodList.setAdapter(nutritionAdapter);
                    break;
                case 10 :
                    nutritionAdapter.notifyDataSetChanged();
                    break;
            }
            database.close();
        }

        public void setBundle(Bundle bundle) {
            this.info = bundle;
        }

        private void loadPreFoods() {

            dbCursor.moveToFirst();
            while(!dbCursor.isAfterLast()) { // load up the foods ArrayList

                foods.add(new Food(
                        dbCursor.getString(dbCursor.getColumnIndex(DatabaseHelper.NUTRITION_ITEM)),
                        dbCursor.getDouble(dbCursor.getColumnIndex(DatabaseHelper.NUTRITION_CALORIES)),
                        dbCursor.getDouble(dbCursor.getColumnIndex(DatabaseHelper.NUTRITION_FAT)),
                        dbCursor.getDouble(dbCursor.getColumnIndex(DatabaseHelper.NUTRITION_CARBS)),
                        dbCursor.getLong(dbCursor.getColumnIndex(DatabaseHelper.NUTRITION_DATE))
                ));

                dbCursor.moveToNext();
            }
        }

        // provides a cursor to go through all records in the Nutrition table of the database
        private Cursor nutritionTableQuery() {
            return database.query(DatabaseHelper.NUTRITION_TABLE,
                    new String[]{DatabaseHelper.ID,
                            DatabaseHelper.NUTRITION_ITEM,
                            DatabaseHelper.NUTRITION_CALORIES,
                            DatabaseHelper.NUTRITION_FAT,
                            DatabaseHelper.NUTRITION_CARBS,
                            DatabaseHelper.NUTRITION_DATE},
                    null, null, null, null, null);
        }

        // adds an entry to Nutrition table in the database
        private void writeToDatabase(String item, double calories, double fat, double carbohydrates, long timestamp) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.NUTRITION_ITEM, item);
            values.put(DatabaseHelper.NUTRITION_CALORIES, calories);
            values.put(DatabaseHelper.NUTRITION_FAT, fat);
            values.put(DatabaseHelper.NUTRITION_CARBS, carbohydrates);
            values.put(DatabaseHelper.NUTRITION_DATE, timestamp);
            database.insert(DatabaseHelper.NUTRITION_TABLE, "NULL MESSAGE", values);
        }
    }

    // Used to present the list view in Nutrition Tracker
    private class NutritionAdapter extends ArrayAdapter<Food> {

        private NutritionQuery nutritionQuery;

        public NutritionAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getCount() { return foods.size(); }

        @Override
        public Food getItem(int index) { return foods.get(index);}

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            LayoutInflater inflater = NutritionTracker.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.list_row, null);
            TextView foodEntry = result.findViewById(R.id.row_entry);
            foodEntry.setText(getItem(index).getFoodItem() + "\n" + getItem(index).getDate());
            return result;
        }
    }

    // DAO for Nutrition table in database
    private class Food {
        private String item;
        private double calories;
        private double fat;
        private double carbohydrates;
        private long timestamp;
        private String date;

        public Food(String item, long timestamp) {
            this(item, 0, 0, 0, timestamp);
        }

        public Food(String item, double calories, double fat, double carbohydrates, long timestamp) {
            this.item = item;
            this.calories = calories;
            this.fat = fat;
            this.carbohydrates = carbohydrates;
            this.timestamp = timestamp;
            convertTimestampToDate();
        }

        public String getFoodItem() { return this.item; }
        public double getCalories() { return this.calories; };
        public double getFat() { return this.fat; }
        public double getCarbohydrates() { return this.carbohydrates; }
        public long getTimestamp() { return this.timestamp; }
        public String getDate() { return this.date; }

        private void convertTimestampToDate() {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd yyyy 'at' HH:mm:ss z");
            this.date = dateFormatter.format(new Date(this.timestamp));
        }
    }
}
