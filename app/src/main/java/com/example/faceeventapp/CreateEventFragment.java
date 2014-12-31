package com.example.faceeventapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;


public class CreateEventFragment extends Fragment {

    private TextView event_name;
    private TextView event_time;
    private TextView event_venue;
    private TextView event_description;
    private TextView event_date;
    private RadioGroup cat;
    private Button create_event;
    private RadioButton event_category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

        event_name = (TextView) rootView.findViewById(R.id.eventName);
        event_time = (TextView) rootView.findViewById(R.id.eventTime);
        event_date = (TextView) rootView.findViewById(R.id.eventDate);
        event_description = (TextView) rootView.findViewById(R.id.eventDescription);
        event_venue = (TextView) rootView.findViewById(R.id.eventVenue);

        cat = (RadioGroup) rootView.findViewById(R.id.radioCategory);
        event_category = (RadioButton) rootView.findViewById(cat.getCheckedRadioButtonId());

        create_event = (Button) rootView.findViewById(R.id.createButton);
        create_event.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                buttonClicked(v);
            }
        });

        return rootView;
    }

    public void buttonClicked(View view)
    {
        Event event = new Event();
        event.setEventName(event_name.getText().toString());
        event.setEventDate(event_date.getText().toString(),event_time.getText().toString());
        event.setEventVenue(event_venue.getText().toString());
        event.setEventDescription(event_description.getText().toString());
        event.setEventCategory(event_category.getText().toString());

        try {
            new StoreToEvent().execute(event).get();
            Toast.makeText(getActivity(), "Event " + event.getEventName()
                    + " added", Toast.LENGTH_LONG).show();
        }catch(Exception e)
        {

        }
    }

}
