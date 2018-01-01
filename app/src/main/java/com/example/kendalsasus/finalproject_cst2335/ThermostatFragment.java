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
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

/* @author Steven Adema
    dateCreated: 28-Dec-2017
 */
public class ThermostatFragment extends Fragment {

    public boolean boolData;
    public int position;
    public int requestCode;
    public long id;
    public String day;
    public String hour;
    public Integer temp;
    public Spinner daySpinner;
    public EditText hourET;
    public EditText tempET;
    public FrameLayout frame;
    public String ACTIVITY_NAME = "ThermostatFragment";

    //default constructor
    public ThermostatFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle bundle){


        frame = (FrameLayout) inflater.inflate(R.layout.activity_thermostat_add_entry, view, false);
        Button submitBTN = frame.findViewById(R.id.submitBTN);
        daySpinner = frame.findViewById(R.id.thermostatSpinner);
        hourET = frame.findViewById(R.id.thermostat_hour_ET);
        tempET = frame.findViewById(R.id.thermostat_temp_ET);

        if(requestCode == 2){ updateThermostatSetting(); }

        submitBTN.setOnClickListener(new View.OnClickListener() {
            boolean bool;
            public void onClick(View v) {
                Bundle newBundle = addThermostatEntry(frame);
                Intent intent = new Intent(frame.getContext(), Thermostat.class);
                intent.putExtras(newBundle);

                // ADD button selected
                if(requestCode == 1) {
                    getActivity().setResult(3, intent);
                }
                // EDIT button selected
                else if(requestCode == 2) {
                    getActivity().setResult(4, intent);
                }

                if(boolData) {
                    if (bool) { //data accepted, remove fragment
                        getActivity().getFragmentManager().beginTransaction().remove(ThermostatFragment.this).commit();
                    } else { getActivity().finish();  }
                }

            }
        }); //end of submitBTN.setOnClickListener

        return frame;
    } //end of cnCreateView()

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        requestCode = 0;

        if(bundle != null) {
            requestCode = bundle.getInt("requestCode");

            try{  id = bundle.getLong("ID");
            }catch(NullPointerException n){ Log.e(ACTIVITY_NAME, "NullPointerException at id", n); }
            try{  position = bundle.getInt("Position");
            }catch(NullPointerException n){ Log.e(ACTIVITY_NAME, "NullPointerException at position", n); }
            try{  day = bundle.getString("day");
            }catch(NullPointerException n){ Log.e(ACTIVITY_NAME, "NullPointerException at day", n); }
            try{  hour = bundle.getString("hour");
            }catch(NullPointerException n){ Log.e(ACTIVITY_NAME, "NullPointerException at hour", n); }
            try{ temp = bundle.getInt("temp");
            } catch(NullPointerException n){ Log.e(ACTIVITY_NAME, "NullPointerException at temp", n); }
        }
        Log.i("Passed key", ""+requestCode);

    } //end of OnAttach

    @Override
    public void onDestroy(){ super.onDestroy(); }

    private Bundle addThermostatEntry(View view) {
        Bundle info = new Bundle();

        //instantiate new data fields for Thermostat Setting
        String dayUpdated = "";
        String hourUpdated = "";
        Integer tempUpdated = 0;

        try {  dayUpdated = daySpinner.getSelectedItem().toString();
        } catch (NumberFormatException n) { Log.e(ACTIVITY_NAME, "NumberFormatException at day", n); }
        try {  hourUpdated = hourET.getText().toString().trim();
        } catch (NumberFormatException n) { Log.e(ACTIVITY_NAME, "NumberFormatException at hour", n); }
        try {  tempUpdated = Integer.parseInt(tempET.getText().toString().trim());
        } catch (NumberFormatException n) { Log.e(ACTIVITY_NAME, "NumberFormatException at int", n); }

        //todo: get conditional to work
        if (dayUpdated.matches("") || hourUpdated.matches("") || tempUpdated == null ) {
            Toast toast = Toast.makeText(getActivity(), "Please fill out all fields to submit", Toast.LENGTH_SHORT);
            toast.show();
            boolData = false;
        } else {  boolData = true; }

        //store EditText info in bundle
        info.putString("day", dayUpdated);
        info.putString("hour", hourUpdated);
        info.putInt("temp", tempUpdated);
        info.putLong("Timestamp", System.currentTimeMillis());

        if (requestCode == 2) { // for EDIT button selected
            info.putLong("ID", id);
            info.putInt("Position", position);
        }
        return info;

    }  //end of addThermostatEntry

    //method to populate fragment with info from the selected ThermostatSetting
    public void updateThermostatSetting(){

        daySpinner.setSelection(getIndex(daySpinner, day));
        hourET.setText(hour);
        tempET.setText(String.format("%d",temp));
    }

    //method to get index of selected day from the spinner
    private int getIndex(Spinner spinner, String day)
    {
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(day)){
                index = i;
                break;
            }
        }
        return index;
    }

} //end of ThermostatFragement class
