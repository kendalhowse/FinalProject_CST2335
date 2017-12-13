package com.example.kendalsasus.finalproject_cst2335;



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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

        foods = new ArrayList<>();

        foodList = findViewById(R.id.food_list);
        Button nutritionAddEntryButton = (Button) findViewById(R.id.nutrition_add_entry_button);

        nutritionAddEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(NutritionTracker.this, AddNutritionEntry.class), 0);
            }
        });

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
        private NutritionAdapter nutritionAdapter;
        private int processCode;
        private Bundle info;

        @Override
        protected String doInBackground(String... args) {

            DatabaseHelper dbHelper = new DatabaseHelper(NutritionTracker.this);
            database = dbHelper.getWritableDatabase();
            dbCursor = nutritionTableQuery();

            processCode = 0;
            try {
                processCode = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {}

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
            while(!dbCursor.isAfterLast()) {

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
            foodEntry.setText(getItem(index).getFoodItem());
            return result;
        }
    }

    private class Food {
        private String item;
        private double calories;
        private double fat;
        private double carbohydrates;
        private long timestamp;
        private String date;

        public Food(long id, String item, long timestamp) {
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

