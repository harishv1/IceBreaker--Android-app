package com.example.faceeventapp.model;

import android.util.Log;

import com.example.faceeventapp.Event;
import com.example.faceeventapp.ReadAllEvents;
import com.example.faceeventapp.ReadAllUserEvents;
import com.example.faceeventapp.ReadCurrentEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rashmitonge on 12/21/14.
 */
public class EventsItem {
    private Event [] eventList;
    public int length;

    public EventsItem()
    {
        try {
            eventList = new ReadAllEvents().execute().get();
//            eventList = new Event[5];
//            Event event = new Event();
//            event.setEventName("test");
//            event.setEventCategory("fashion");
//            for(int i=0;i<5;i++){
//                eventList[i] = event;
//            }
            length = eventList.length;
        }catch(Exception e)
        {

        }
    }

    public EventsItem(String user_name)
    {
        try{
            eventList = new ReadAllUserEvents().execute(user_name).get();
            for(int i=0;i<eventList.length;i++)
            {
//                Log.d("Rashmi",eventList[i].getEventName());
                eventList[i] =
                        new ReadCurrentEvent().execute(eventList[i].getEventName(),user_name).get();
//                Log.d("Rashmi",eventList[i].getEventName());
            }
            length = eventList.length;
        }catch(Exception e)
        {

        }
        //Log.d("Rashmi",""+eventList.length);
    }

    public Event getEventItem(int position)
    {
        return eventList[position];
    }
}
