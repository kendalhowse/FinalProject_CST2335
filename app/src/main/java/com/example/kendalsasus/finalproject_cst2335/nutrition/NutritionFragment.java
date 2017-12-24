package com.example.kendalsasus.finalproject_cst2335.nutrition;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.kendalsasus.finalproject_cst2335.R;
/**
 * Created by Chris on 2017-12-09.
 */

public class NutritionFragment extends Fragment {

    private final static String FRAGMENT_NAME = "NutritionFragment";
    private View nutritionFragmentView;
    private boolean isTablet;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(FRAGMENT_NAME, "In onCreateView for Fragment");
        Bundle passedInfo = getArguments();

        int processCode = 0;
        try {
            processCode = passedInfo.getInt("processCode");
            Log.i(FRAGMENT_NAME, "I've got a process code!");
        } catch(NullPointerException e) {}

        switch(processCode) {

            case 1:
                nutritionFragmentView = inflater.inflate(R.layout.activity_nutrition_details, container, false);
                showFoodDetails(nutritionFragmentView, passedInfo);
                break;

            case 10:
                nutritionFragmentView = inflater.inflate(R.layout.activity_add_nutrition_entry, container, false);
                addNutritionEntry(nutritionFragmentView);
                break;

            case 11:
                nutritionFragmentView = inflater.inflate(R.layout.activity_add_nutrition_entry, container, false);
                editNutritionEntry(nutritionFragmentView, passedInfo);
                break;
        }
        return nutritionFragmentView;
    }

    private void showFoodDetails(final View layoutView, Bundle info) {
            TextView itemDetails = layoutView.findViewById(R.id.nutrition_item_details);
            TextView calorieDetails = layoutView.findViewById(R.id.nutrition_calories_details);
            TextView fatDetails = layoutView.findViewById(R.id.nutrition_fat_details);
            TextView carbDetails = layoutView.findViewById(R.id.nutrition_carbs_details);
            TextView dateDetails = layoutView.findViewById(R.id.nutrition_date_details);
            itemDetails.setText(info.getString("itemDetails"));
            calorieDetails.setText(getActivity().getString(R.string.nutrition_details_calories) + " " + info.getString("calorieDetails") + " g");
            fatDetails.setText(getActivity().getString(R.string.nutrition_details_fat) + " " + info.getString("fatDetails") + " g");
            carbDetails.setText(getActivity().getString(R.string.nutrition_details_carbs) + " " + info.getString("carbDetails") + " g");
            dateDetails.setText(getActivity().getString(R.string.nutrition_details_date) +  " " + info.getString("dateDetails"));
    }

    public void addNutritionEntry(final View layoutView) {

        final EditText etAddNutritionItem = layoutView.findViewById(R.id.add_nutrition_item_edit_text);
        Button addNutritionEntryButton = layoutView.findViewById(R.id.add_nutrition_entry_button);
        addNutritionEntryButton.setText(getActivity().getString(R.string.nutrition_submit_button));
        addNutritionEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View handlerView) {
                // is the EditText for Food Item empty?
                if(etAddNutritionItem.getText().toString().matches("")) {
                    // if so, alert the user
                    Toast.makeText(
                            handlerView.getContext(),
                            getActivity().getString(R.string.edit_nutrition_blank_food_item_entry_warning),
                            Toast.LENGTH_LONG
                    ).show();
                }
                else {
                    // otherwise, store info from System.currentTimeMillis and Food Item
                    // EditText in a bundle
                    Bundle formValues = getNewEntryInfo(layoutView);
                    formValues.putString("item", etAddNutritionItem.getText().toString().trim());
                    formValues.putLong("timestamp", System.currentTimeMillis());

                    Intent completedForm = new Intent(handlerView.getContext(), NutritionTracker.class);
                    completedForm.putExtras(formValues);
                    getActivity().setResult(10, completedForm);

                    // if the device is a tablet
                    if(isTablet) {
                        // remove the fragment
                        getActivity().getFragmentManager().beginTransaction().remove(NutritionFragment.this).commit();
                    }
                    else {
                        // otherwise finish the activity
                        getActivity().finish();
                    }
                }
            }
        });
    }

    private Bundle getNewEntryInfo(View view) {
        Bundle info = new Bundle();
        // get reference to EditTexts in add_nutrition_entry layout
        EditText etAddNutritionCalories = view.findViewById(R.id.add_nutrition_calories_edit_text);
        EditText etAddNutritionFat = view.findViewById(R.id.add_nutrition_fat_edit_text);
        EditText etAddNutritionCarbs = view.findViewById(R.id.add_nutrition_carbs_edit_text);

        // initialize them to 0, if there's an error with input, or user doesn't enter anything, they stay as 0
        double calories = 0;
        double fat = 0;
        double carbs = 0;
        try{calories = Double.parseDouble(etAddNutritionCalories.getText().toString());} catch(Exception e) {}
        try{fat = Double.parseDouble(etAddNutritionFat.getText().toString());} catch(Exception e) {}
        try{carbs = Double.parseDouble(etAddNutritionCarbs.getText().toString());} catch(Exception e) {}

        // store the values of the EditTexts in a bundle
        info.putDouble("calories", calories);
        info.putDouble("fat", fat);
        info.putDouble("carbohydrates", carbs);

        return info;
    }

    private void editNutritionEntry(final View layoutView, final Bundle info) {
        final EditText etAddNutritionItem = layoutView.findViewById(R.id.add_nutrition_item_edit_text);
        final EditText etAddNutritionCalories = layoutView.findViewById(R.id.add_nutrition_calories_edit_text);
        final EditText etAddNutritionFat = layoutView.findViewById(R.id.add_nutrition_fat_edit_text);
        final EditText etAddNutritionCarbs = layoutView.findViewById(R.id.add_nutrition_carbs_edit_text);

        // fill the edit texts with the current value of the details for the entry
        etAddNutritionItem.setText(info.getString("item"));
        etAddNutritionCalories.setText(info.getString("calories"));
        etAddNutritionFat.setText(info.getString("fat"));
        etAddNutritionCarbs.setText(info.getString("carbohydrates"));

        // Make the edit text headers visible, so that user knows what field they're changing value of
        layoutView.findViewById(R.id.add_nutrition_item_text_view).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.add_nutrition_calories_text_view).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.add_nutrition_fat_text_view).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.add_nutrition_carbs_text_view).setVisibility(View.VISIBLE);

        Button submitButton = layoutView.findViewById(R.id.add_nutrition_entry_button);
        submitButton.setText(getActivity().getString(R.string.nutrition_commit_changes_button));
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View handlerView) {
                String item = etAddNutritionItem.getText().toString().trim();

                // did the user even change anything?
                if(isEntryChanged(info, item, etAddNutritionCalories.getText().toString().trim(),
                        etAddNutritionFat.getText().toString().trim(), etAddNutritionCarbs.getText().toString().trim())) {
                    // get the value of the stuff they entered (and the stuff that was already there)
                    Bundle formValues = getNewEntryInfo(layoutView);
                    formValues.putString("item", item);
                    formValues.putInt("selectedEntry", info.getInt("selectedEntry"));

                    Intent completedForm = new Intent(handlerView.getContext(), NutritionTracker.class);
                    completedForm.putExtras(formValues);
                    // pass on the values to NutritionTracker to be used in database update
                    getActivity().setResult(11, completedForm);

                    // if the device is a tablet
                    if(isTablet) {
                        // remove the fragment
                        getActivity().getFragmentManager().beginTransaction().remove(NutritionFragment.this).commit();
                    }
                    else {
                        // otherwise finish the activity
                        getActivity().finish();
                    }
                }
                else {
                    // let the user know that they didn't even change anything
                    Toast.makeText(
                            handlerView.getContext(),
                            getActivity().getString(R.string.nutrition_no_changes_toast),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }

    private boolean isEntryChanged(Bundle info, String... values) {
        boolean isEntryChanged = false;
        if( // if kept value the same or input nothing for item field
                (!values[0].matches("") && !values[0].equals(info.getString("item"))) ||
                !values[1].equals(info.getString("calories")) ||
                !values[2].equals(info.getString("fat")) ||
                !values[3].equals(info.getString("carbohydrates"))

        ) {
            isEntryChanged = true;
            Log.i(FRAGMENT_NAME, "SOMETHING CHANGED");
        }
        else {
            Log.i(FRAGMENT_NAME, "NOTHING CHANGED");
        }
        return isEntryChanged;
    }

    public void setTablet(boolean isTablet) { this.isTablet = isTablet; }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
