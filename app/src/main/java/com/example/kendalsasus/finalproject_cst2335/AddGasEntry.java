package com.example.kendalsasus.finalproject_cst2335;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddGasEntry extends AppCompatActivity {
    Automobile auto = new Automobile();
    int requestCode;



    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gas_entry);


        Log.i("Phone", "This is a phone");
        Bundle info = getIntent().getExtras();
        FragmentTransaction ft = getFragmentManager().beginTransaction().addToBackStack(null);
        GasFragment gasFragment = new GasFragment(auto);
        gasFragment.setArguments(info);
        ft.add(R.id.addFrameLayout, gasFragment);
        ft.commit();


       /* submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                EditText gasEntry = findViewById(R.id.gasAmount);
                EditText priceEntry = findViewById(R.id.cost);
                EditText odometerEntry = findViewById(R.id.km);

                String newGas = gasEntry.getText().toString();
                String newPrice = priceEntry.getText().toString();
                String newOdometer = odometerEntry.getText().toString();


                if(newGas == null || newGas.equals("")){
                    newGas = "0";
                }
                if(newPrice == null || newPrice.equals("")){
                    newPrice = "0";
                }
                if(newOdometer == null || newOdometer.equals("")){
                    newOdometer = "0";
                }

                ContentValues newData = new ContentValues();
                newData.put(DatabaseHelper.GAS, newGas);
                newData.put(DatabaseHelper.GAS_PRICE, newPrice);
                newData.put(DatabaseHelper.ODOMETER, newOdometer);


                Automobile.db.insert(DatabaseHelper.AUTO_TABLE, "", newData);



                Cursor getDate = Automobile.db.rawQuery("SELECT " + DatabaseHelper.GAS_DATE + " FROM " + DatabaseHelper.AUTO_TABLE, null);
                getDate.moveToLast();

                //gasList.clear();

                while(!getDate.isAfterLast() ) {

                    auto.getGasList().add(getDate.getString(getDate.getColumnIndex(DatabaseHelper.GAS_DATE)));
                    getDate.moveToNext();
                }

                Intent intent = new Intent(AddGasEntry.this, Automobile.class);
                startActivity(intent);

            }
        });*/

    }

}
