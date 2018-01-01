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

import java.util.Date;

/**
 * Created by Melissa Rajala on 2017-12-30.
 */

public class ActivityFragment extends Fragment {

    FrameLayout frame;
    Spinner activity;
    EditText duration;
    EditText comments;
    Button submitBtn;

    String activityTypeValue;
    String durationValue;
    String commentValue;
    String stringDate;

    String editActivity;
    String editDuration;
    String editComment;
    long editId;
    int requestCode;

    ActivityTracker newActivity;

    //default constructor
    public ActivityFragment(){

    }

    public ActivityFragment(ActivityTracker activity){
        newActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle edit = getArguments();
//        requestCode = 0;

        if (edit != null){
            editId = edit.getLong("ID");
            editActivity = edit.getString("Type");
            editDuration = edit.getString("Duration");
            editComment = edit.getString("Comment");
            requestCode = edit.getInt("RequestCode");
        }

        Log.i("ActivityFragment", "Activity to edit = " + editActivity);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        frame = (FrameLayout) inflater.inflate(R.layout.activity_add_activity_entry, container, false);

        //initialize all text fields in fragment frame
        activity = (Spinner) frame.findViewById(R.id.activityType);
        duration = (EditText) frame.findViewById(R.id.activityDuration);
        comments = (EditText) frame.findViewById(R.id.activityComments);
        submitBtn = (Button) frame.findViewById(R.id.submitBtn);

        if (requestCode == 20){
            setEditValues();
        }


        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activityTypeValue = activity.getSelectedItem().toString();
                durationValue = duration.getText().toString();
                commentValue = comments.getText().toString();
                Date date = new Date();
                stringDate = date.toString();


                Intent intent = new Intent(frame.getContext(), ActivityTracker.class);
                Bundle extras = new Bundle();
                extras.putString("activityType", activityTypeValue);
                extras.putString("activityDuration", durationValue);
                extras.putString("activityComment", commentValue);
                extras.putString("activityDate", stringDate);
                intent.putExtras(extras);
                getActivity().setResult(10, intent);
                getActivity().finish();
                Log.i("ActivityFragment", "Activity Type: " + activityTypeValue);
                Log.i("ActivityFragment", "Activity Duration: " + durationValue);
                Log.i("ActivityFragment", "Activity Comment: " + commentValue);
                Log.i("ActivityFragment", "Activity Date: " + stringDate);

            }
        });
        return frame;
    }

    public void setEditValues(){
        int activitySelection = 0;

        //check which activity it is
        if (editActivity.equals("Running")){
            activitySelection = 0;
        } else if (editActivity.equals("Walking")) {
            activitySelection = 1;
        } else if (editActivity.equals("Biking")) {
            activitySelection = 2;
        } else if (editActivity.equals("Swimming")) {
            activitySelection = 3;
        } else {
            activitySelection = 4;
        }

        //set all fields as they were to be edited
        activity.setSelection(activitySelection);
        duration.setText(editDuration);
        comments.setText(editComment);
    }

}
