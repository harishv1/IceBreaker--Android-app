package com.example.faceeventapp;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CurrentEventFragment extends Fragment {
//    Event event = getActivity().current_event;

    private Event current_event;

    private TextView event_name;
    private TextView event_venue;
    private TextView event_date;
    private TextView event_description;
    private TextView event_category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current_event, container, false);

        final GlobalUser global_user = (GlobalUser) getActivity().getApplicationContext();
        String user_name = global_user.getUsername();
        String event = global_user.getEvent();
        try{
            current_event = new ReadCurrentEvent().execute(event,user_name).get();
        }
        catch(Exception e){

        }

        event_name = (TextView) rootView.findViewById(R.id.EventName);
        event_venue = (TextView) rootView.findViewById(R.id.EventLoc);
        event_description = (TextView) rootView.findViewById(R.id.description);
        event_category = (TextView) rootView.findViewById(R.id.Category);
        event_date = (TextView) rootView.findViewById(R.id.EventDate);

        event_name.setText("Event Name : " + current_event.getEventName());
        Log.d("EventName",current_event.getEventName());
        event_venue.setText("At : " + current_event.getEventVenue());
        Log.d("EventVenue",current_event.getEventVenue());
        event_description.setText("Event Description : " + current_event.getEventDescription());
        Log.d("EventDesc",current_event.getEventDescription());
        event_category.setText("Event Category : " + current_event.getEventCategory());
        event_date.setText("On : " + current_event.getEventDate());


        return rootView;
    }

    public void setCurrentEvent(Event event)
    {
        current_event = event;
    }
}
