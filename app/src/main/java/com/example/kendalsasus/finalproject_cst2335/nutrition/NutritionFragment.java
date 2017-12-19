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
import android.widget.Toast;

import com.example.kendalsasus.finalproject_cst2335.R;

/**
 * Created by Chris on 2017-12-09.
 */

public class NutritionFragment extends Fragment {

    private final static String FRAGMENT_NAME = "NutritionFragment";
    private boolean isTablet;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(FRAGMENT_NAME, "In onCreateView for Fragment");
        final View addNutritionView = inflater.inflate(R.layout.activity_add_nutrition_entry, container, false);

        addNutritionEntry(addNutritionView);

        return addNutritionView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void addNutritionEntry(final View layoutView) {

        final EditText etAddNutritionItem = layoutView.findViewById(R.id.add_nutrition_item_edit_text);
        Button addNutritionEntryButton = layoutView.findViewById(R.id.add_nutrition_entry_button);
        addNutritionEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View handlerView) {
                // is the EditText for Food Item empty?
                if(etAddNutritionItem.getText() == null || etAddNutritionItem.getText().equals("")) {
                    // if so, alert the user
                    Toast.makeText(
                            handlerView.getContext(),
                            "You cannot leave Food Item field empty.",
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
}
