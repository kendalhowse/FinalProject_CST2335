package com.example.kendalsasus.finalproject_cst2335;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * Created by Kendal's Asus on 2017-12-12.
 */

public class GasFragment extends Fragment {

    boolean isTablet;
    FrameLayout frameLayout;
    boolean dataAccepted;
    Automobile automobile;
    int requestCode;
    long id;
    Double litres;
    Double price;
    Double odometer;
    EditText gasEntry;
    EditText priceEntry;
    EditText odometerEntry;
    int position;

    public GasFragment(Automobile auto){
       automobile = auto;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle passedInfo = getArguments();

        requestCode = 0;

        if(passedInfo != null) {
            requestCode = passedInfo.getInt("requestCode");


            try{
                id = passedInfo.getLong("ID");
            }catch(NullPointerException n){

            }
            try{
                position = passedInfo.getInt("Position");
            }catch(NullPointerException n){

            }

            try{
                litres = passedInfo.getDouble("litres");
            }catch(NullPointerException n){

            }
            try{
                price = passedInfo.getDouble("price");
            }catch(NullPointerException n){

            }
            try{
                odometer = passedInfo.getDouble("odometer");
            }catch(NullPointerException n){

            }
        }
        Log.i("Passed key", ""+requestCode);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){

        frameLayout = (FrameLayout) inflater.inflate(R.layout.activity_add_gas_entry, container, false);
        Button submit = frameLayout.findViewById(R.id.submitButton);



        gasEntry = frameLayout.findViewById(R.id.gasAmount);
        priceEntry = frameLayout.findViewById(R.id.cost);
        odometerEntry = frameLayout.findViewById(R.id.km);

        if(requestCode == 2){
            updateGasInfo();

        }


            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle newEntry = addGasInfo(frameLayout);
                    Intent sendInfo = new Intent(frameLayout.getContext(), Automobile.class);
                    sendInfo.putExtras(newEntry);
                    if(requestCode == 1) {
                        getActivity().setResult(10, sendInfo);
                    }
                    else if(requestCode == 2) {
                        getActivity().setResult(20, sendInfo);
                    }

                    if(dataAccepted) {

                        if (isTablet) {
                            getActivity().getFragmentManager().beginTransaction().remove(GasFragment.this).commit();
                        } else {
                            getActivity().finish();
                        }
                    }

                }
            });


        return frameLayout;
    }


    private Bundle addGasInfo(View view){
        Bundle info = new Bundle();
        double newGas = 0;
        double newPrice = 0;
        double newOdometer = 0;
        // this.frameLayout = frameLayout;
/*


        gasEntry = view.findViewById(R.id.gasAmount);
        priceEntry = view.findViewById(R.id.cost);
        odometerEntry = view.findViewById(R.id.km);*/

        try {
            newGas = Double.parseDouble(gasEntry.getText().toString());
        } catch (NumberFormatException n) {

        }
        try {
            newPrice = Double.parseDouble(priceEntry.getText().toString());
        } catch (NumberFormatException n) {

        }
        try {
            newOdometer = Double.parseDouble(odometerEntry.getText().toString());
        } catch (NumberFormatException n) {

        }

        if(newGas == 0.0 && newPrice == 0.0 && newOdometer == 0.0){

            Toast toast = Toast.makeText(getActivity(), "Please enter at least one field", Toast.LENGTH_SHORT);
            toast.show();
            dataAccepted = false;
        }
        else {
            dataAccepted = true;
        }
        info.putDouble("Gas", newGas);
        info.putDouble("Price", newPrice);
        info.putDouble("Odometer", newOdometer);
        info.putLong("Timestamp", System.currentTimeMillis());

        if(requestCode == 2){
            info.putLong("ID", id);
            info.putInt("Position", position);
        }


        return info;
    }

    public void setTablet(boolean isTablet){
        this.isTablet = isTablet;
    }

    public void updateGasInfo(){

        gasEntry.setText(Double.toString(litres));
        priceEntry.setText(Double.toString(price));
        odometerEntry.setText(Double.toString(odometer));
    }

}
