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

        switch(passedInfo.getInt("processCode")) {

            case 1:
                nutritionFragmentView = inflater.inflate(R.layout.activity_nutrition_details, container, false);
                showFoodDetails(nutritionFragmentView, passedInfo);
                break;

            case 10:
                nutritionFragmentView = inflater.inflate(R.layout.activity_add_nutrition_entry, container, false);
                addNutritionEntry(nutritionFragmentView);
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
        addNutritionEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View handlerView) {
                // is the EditText for Food Item empty?
                if(etAddNutritionItem.getText().toString().matches("")) {
                    // if so, alert the user
                    Toast.makeText(
                            handlerView.getContext(),
                            getActivity().getString(R.string.blank_food_item_entry_warning),
                            Toast.LENGTH_LONG
                    ).show();
                }
                else {
                    // otherwise, store info from System.currentTimeMillis and Food Item
                    // EditText in a bundle
                    Bundle formValues = getNewEntryInfo(layoutView);
                    formValues.putString("Item", etAddNutritionItem.getText().toString());
                    formValues.putLong("Timestamp", System.currentTimeMillis());

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
        info.putDouble("Calories", calories);
        info.putDouble("Fat", fat);
        info.putDouble("Carbohydrates", carbs);

        return info;
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
