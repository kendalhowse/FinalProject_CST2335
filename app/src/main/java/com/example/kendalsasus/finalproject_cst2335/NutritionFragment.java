package com.example.kendalsasus.finalproject_cst2335;

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

import java.util.Calendar;
import java.util.Date;

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

                if(etAddNutritionItem.getText() == null || etAddNutritionItem.getText().equals("")) {

                    // MAKE A TOAST WARNING HERE... THAT FIELD CAN'T BE EMPTY
                }
                else {

                    Bundle formValues = getNewEntryInfo(layoutView);
                    formValues.putString("Item", etAddNutritionItem.getText().toString());
                    formValues.putLong("Timestamp", System.currentTimeMillis());

                    Intent completedForm = new Intent(handlerView.getContext(), NutritionTracker.class);
                    completedForm.putExtras(formValues);
                    getActivity().setResult(10, completedForm);

                    if(isTablet) {
                        getActivity().getFragmentManager().beginTransaction().remove(NutritionFragment.this).commit();
                    }
                    else {
                        getActivity().finish();
                    }
                }
            }
        });
    }

    private Bundle getNewEntryInfo(View view) {
        Bundle info = new Bundle();
        EditText etAddNutritionCalories = view.findViewById(R.id.add_nutrition_calories_edit_text);
        EditText etAddNutritionFat = view.findViewById(R.id.add_nutrition_fat_edit_text);
        EditText etAddNutritionCarbs = view.findViewById(R.id.add_nutrition_carbs_edit_text);

        double calories = 0;
        double fat = 0;
        double carbs = 0;
        try{calories = Double.parseDouble(etAddNutritionCalories.getText().toString());} catch(Exception e) {}
        try{fat = Double.parseDouble(etAddNutritionFat.getText().toString());} catch(Exception e) {}
        try{carbs = Double.parseDouble(etAddNutritionCarbs.getText().toString());} catch(Exception e) {}

        info.putDouble("Calories", calories);
        info.putDouble("Fat", fat);
        info.putDouble("Carbohydrates", carbs);

        return info;
    }

    public void setTablet(boolean isTablet) { this.isTablet = isTablet; }
}
