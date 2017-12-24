package com.example.kendalsasus.finalproject_cst2335.nutrition;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private TextView tvTotalCalories;
    private int selectedEntry;
    private NutritionAdapter nutritionAdapter; // used to access ListView Adapter.
    private String sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrtion_tracker);

        // initialize ArrayList and ListView fields
        foods = new ArrayList<>();
        foodList = findViewById(R.id.food_list);

        // initialize ImageButtons relating to Add, Delete, and Edit.
        ImageButton nutritionAddEntryButton = findViewById(R.id.nutrition_add_entry_button);
        final ImageButton nutritionDeleteEntryButton = findViewById(R.id.nutrition_delete_entry_button);
        final ImageButton nutritionEditEntryButton = findViewById(R.id.nutrition_edit_entry_button);
        tvTotalCalories = findViewById(R.id.nutrition_total_calories_textView);

        nutritionAddEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // load the add_nutrition fragment if user clicks Add.
                Intent addIntent = new Intent(NutritionTracker.this, AddNutritionEntry.class);
                Bundle info = new Bundle();
                info.putInt("processCode", 10);
                addIntent.putExtras(info);
                startActivityForResult(addIntent, 0);
            }
        });
        nutritionDeleteEntryButton.setEnabled(false); // disable delete button initially
        nutritionDeleteEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do this if user clicks on Delete
                warnUserOfDeletion();
            }
        });
        nutritionEditEntryButton.setEnabled(false); // disable edit button initially
        nutritionEditEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do this if user clicks on Edit.
                startActivityForResult(prepareEditIntent(), 0);
            }
        });
        // run everytime, gets entries that have been sitting in database before app start.
        new NutritionQuery().execute("1");

        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedEntry = i;
                // now that entry is selected, enable delete and edit buttons
                nutritionDeleteEntryButton.setEnabled(true);
                nutritionEditEntryButton.setEnabled(true);
                //foodList.smoothScrollToPosition(i);
                inflateNutritionDetailsFragment(i);
            }
        });
    }

    private Intent prepareEditIntent() {
        Intent editIntent = new Intent(NutritionTracker.this, AddNutritionEntry.class);
        Food food = foods.get(selectedEntry);
        Bundle fragmentInfo = new Bundle();
        fragmentInfo.putInt("processCode", 11);
        fragmentInfo.putString("item", food.getFoodItem());
        fragmentInfo.putString("calories", String.valueOf(food.getCalories()));
        fragmentInfo.putString("fat", String.valueOf(food.getFat()));
        fragmentInfo.putString("carbohydrates", String.valueOf(food.getCarbohydrates()));
        fragmentInfo.putInt("selectedEntry", selectedEntry);
        editIntent.putExtras(fragmentInfo);
        return editIntent;
    }

    // displays a dialog box confriming deletion of selected entry
    private void warnUserOfDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NutritionTracker.this);
        builder.setTitle(getApplication().getString(R.string.nutrition_wish_to_delete))
                .setPositiveButton(getApplication().getString(R.string.nutrition_wish_to_delete_confirmation), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // delete from database if user clicks on Delete.
                        new NutritionQuery().execute("-10");
                    }
                })
                .setNegativeButton(getApplication().getString(R.string.nutrition_wish_to_delete_rejection), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .create()
                .show();
    }

    // inflates a fragment showing the details of food item that use clicked on
    private void inflateNutritionDetailsFragment(int i) {
        if(!foods.isEmpty()) {
            Food food = foods.get(i);
            Bundle fragmentInfo = new Bundle();
            fragmentInfo.putInt("processCode", 1);
            fragmentInfo.putString("itemDetails", food.getFoodItem());
            fragmentInfo.putString("calorieDetails", String.valueOf(food.getCalories()));
            fragmentInfo.putString("fatDetails", String.valueOf(food.getFat()));
            fragmentInfo.putString("carbDetails", String.valueOf(food.getCarbohydrates()));
            fragmentInfo.putString("dateDetails", food.getWordedDate());
            NutritionFragment detailsFragment = new NutritionFragment();
            detailsFragment.setArguments(fragmentInfo);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nutrition_details_fragment, detailsFragment);
            transaction.commit();
        }
    }

    private void setTotalCaloriesTextView() {
        String currentDate = new SimpleDateFormat("MMddyyyy").format(new Date(System.currentTimeMillis()));
        double totalCalories = 0;
        for(Food food : foods) {
            if(food.getBasicDate().equals(currentDate)) {
                totalCalories += food.getCalories();
            }
        }
        tvTotalCalories.setText(getApplicationContext().getString(R.string.nutrition_daily_calories_eaten) + " " + String.valueOf(totalCalories) + " g");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode) {
            case 10:
                NutritionQuery nutritionQuery = new NutritionQuery();
                nutritionQuery.setBundle(data.getExtras());
                nutritionQuery.execute(String.valueOf(resultCode));
                break;
            case 11:
                nutritionQuery = new NutritionQuery();
                nutritionQuery.setBundle(data.getExtras());
                nutritionQuery.execute(String.valueOf(resultCode));
                break;
        }
    }

// ============== CLASS NUTRITION QUERY START ==================================================================================================================

    private class NutritionQuery extends AsyncTask<String, Integer, String> {

        private SQLiteDatabase database;
        private Cursor dbCursor;
        private ProgressBar progressBar;
        private int processCode;
        private Bundle info; // used to pass information accross objects and into database

        @Override
        protected String doInBackground(String... args) {

            DatabaseHelper dbHelper = new DatabaseHelper(NutritionTracker.this);
            database = dbHelper.getWritableDatabase();
            dbCursor = nutritionTableQuery();
            progressBar = findViewById(R.id.nutrition_progress_bar);

            processCode = 0;
            try {
                processCode = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {}

            // process code depends on what button user clicks
            switch (processCode) {
                // user presses delete button
                case -10:
                    dbCursor.moveToPosition(selectedEntry);
                    publishProgress(15);
                    long id = dbCursor.getLong(dbCursor.getColumnIndex(DatabaseHelper.ID));
                    publishProgress(40);
                    removeFromDatabase(String.valueOf(id));
                    publishProgress(60);
                    dbCursor = nutritionTableQuery();
                    publishProgress(80);
                    foods.clear();
                    publishProgress(90);
                    loadPreFoods();
                    publishProgress(100);
                    break;
                // activity is launched and previous set data is displayed
                case 1:
                    loadPreFoods();
                    break;
                // submit button is pressed in add_nutrition fragment
                case 10:
                    String item = info.getString("item");
                    double calories = info.getDouble("calories");
                    double fat = info.getDouble("fat");
                    double carbs = info.getDouble("carbohydrates");
                    long timestamp = info.getLong("timestamp");
                    foods.add(new Food(item, calories, fat, carbs, timestamp));
                    publishProgress(50);
                    writeToDatabase(item, calories, fat, carbs, timestamp);
                    publishProgress(100);
                    break;

                case 11:
                    dbCursor.moveToPosition(info.getInt("selectedEntry"));
                    id = dbCursor.getLong(dbCursor.getColumnIndex(DatabaseHelper.ID));
                    item = info.getString("item");
                    calories = info.getDouble("calories");
                    fat = info.getDouble("fat");
                    carbs = info.getDouble("carbohydrates");
                    updateDatabase(String.valueOf(id), item, calories, fat, carbs);
                    publishProgress(25);
                    dbCursor = nutritionTableQuery();
                    publishProgress(50);
                    foods.clear();
                    publishProgress(75);
                    loadPreFoods();
                    publishProgress(100);
                    break;
            }
            database.close();
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... args) {

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        // update views on GUI thread
        @Override
        protected void onPostExecute(String result) {

            switch (processCode) {
                // user presses delete button
                case -10:
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.nutrition_details_fragment, new NutritionFragment());
                    transaction.commit();
                    nutritionAdapter.notifyDataSetChanged();
                    findViewById(R.id.nutrition_delete_entry_button).setEnabled(false);
                    findViewById(R.id.nutrition_edit_entry_button).setEnabled(false);
                    Snackbar.make(findViewById(R.id.nutrition_button_layout), getApplication().getString(R.string.nutrition_snackbar_delete_entry), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                // activity is launched and previous set data is displayed
                case 1:
                    nutritionAdapter = new NutritionAdapter(NutritionTracker.this);
                    foodList.setAdapter(nutritionAdapter);
                    break;
                // submit button is pressed in add_nutrition fragment
                case 10:
                    nutritionAdapter.notifyDataSetChanged();
                    Snackbar.make(findViewById(R.id.nutrition_button_layout), getApplication().getString(R.string.nutrition_snackbar_add_entry), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                // commit changes button is pressed in add_nutrition fragment
                case 11:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.nutrition_details_fragment, new NutritionFragment());
                    transaction.commit();
                    nutritionAdapter.notifyDataSetChanged();
                    findViewById(R.id.nutrition_delete_entry_button).setEnabled(false);
                    findViewById(R.id.nutrition_edit_entry_button).setEnabled(false);
                    Snackbar.make(findViewById(R.id.nutrition_button_layout), getApplication().getString(R.string.nutrition_snackbar_update_entry), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
            }
            progressBar.setVisibility(View.INVISIBLE);
            setTotalCaloriesTextView();
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
            try {
                database.insert(DatabaseHelper.NUTRITION_TABLE, "NULL MESSAGE", values);
                Log.i(ACTIVITY_NAME, "Entry successfully added to database.");
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "Unable to add entry to database");
                e.printStackTrace();
            }
        }

        private void removeFromDatabase(String id) {
            try {
                database.execSQL("DELETE FROM " + DatabaseHelper.NUTRITION_TABLE + " WHERE " + DatabaseHelper.ID + " = " + id);
                Log.i(ACTIVITY_NAME, "Entry has been successfully deleted.");
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "Unable to delete from database.");
                e.printStackTrace();
            }
        }

        private void updateDatabase(String id, String item, double calories, double fat, double carbohydrates) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.NUTRITION_ITEM, item);
            values.put(DatabaseHelper.NUTRITION_CALORIES, calories);
            values.put(DatabaseHelper.NUTRITION_FAT, fat);
            values.put(DatabaseHelper.NUTRITION_CARBS, carbohydrates);
            try {
                database.update(DatabaseHelper.NUTRITION_TABLE, values, DatabaseHelper.ID + " = " + id, null);
                Log.i(ACTIVITY_NAME, "Successfully updated database.");
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "Unable to update database.");
            }
        }
    }
// ================== CLASS NUTRITION QUERY END ==================================================================================================================================



// ================== CLASS NUTRITION ADAPTER START ===============================================================================================================================
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
            foodEntry.setText(getItem(index).getFoodItem());
            TextView foodDate = result.findViewById(R.id.row_sub_entry);
            foodDate.setText(getItem(index).getWordedDate());
            return result;
        }
    }
// ========================= CLASS NUTRITION ADAPTER END =====================================================================================================================================



    // DAO for Nutrition table in database
    private class Food {
        private String item;
        private double calories;
        private double fat;
        private double carbohydrates;
        private long timestamp;
        private String wordedDate;
        private String basicDate;

        public Food(String item, long timestamp) {
            this(item, 0, 0, 0, timestamp);
        }

        public Food(String item, double calories, double fat, double carbohydrates, long timestamp) {
            this.item = item;
            this.calories = calories;
            this.fat = fat;
            this.carbohydrates = carbohydrates;
            this.timestamp = timestamp;
            this.wordedDate = getFormattedDate("MMMM dd yyyy 'at' HH:mm:ss z");
            this.basicDate = getFormattedDate("MMddyyyy");
        }

        public String getFoodItem() { return this.item; }
        public double getCalories() { return this.calories; };
        public double getFat() { return this.fat; }
        public double getCarbohydrates() { return this.carbohydrates; }
        public long getTimestamp() { return this.timestamp; }
        public String getWordedDate() { return this.wordedDate; }
        public String getBasicDate() { return this.basicDate; }

        private String getFormattedDate(String dateFormat) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
            String date = dateFormatter.format(new Date(this.timestamp));
            return date;
        }
    }
}

