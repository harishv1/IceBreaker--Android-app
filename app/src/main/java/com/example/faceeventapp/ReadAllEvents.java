package com.example.faceeventapp;

/**
 * Created by rashmitonge on 12/21/14.
 */
import java.util.List;

import com.amazonaws.services.simpledb.model.SelectRequest;

import android.os.AsyncTask;
import android.util.Log;

public class ReadAllEvents extends AsyncTask<Void, Void, Event[]> {



    public static Event [] getAllEvents() throws Exception
    {
        SelectRequest selectRequest=  new SelectRequest("select * from event_info").withConsistentRead(true);

        List<com.amazonaws.services.simpledb.model.Item> items  = Connection.getAwsSimpleDB().select(selectRequest).getItems();

        try
        {
            com.amazonaws.services.simpledb.model.Item temp1;
            int size= items.size();
            Event [] eventList= new  Event[size];

            for(int i=0; i<size;i++)
            {
                temp1= ((com.amazonaws.services.simpledb.model.Item)items.get( i ));

                List<com.amazonaws.services.simpledb.model.Attribute> tempAttribute= temp1.getAttributes();
                eventList[i]= new Event();
                for(int j=0; j< tempAttribute.size();j++)
                {
                    if(tempAttribute.get(j).getName().equals("event_name"))
                    {
                        eventList[i].setEventName(tempAttribute.get(j).getValue());
                    }
                    else if(tempAttribute.get(j).getName().equals("event_venue"))
                    {
                        eventList[i].setEventVenue(tempAttribute.get(j).getValue());
      				}
                    else if(tempAttribute.get(j).getName().equals("event_date"))
                    {
                        Log.d("Rashmi", tempAttribute.get(j).getValue());
                        eventList[i].setEventDate(tempAttribute.get(j).getValue());
                    }
                    else if(tempAttribute.get(j).getName().equals("event_description"))
                    {
                        eventList[i].setEventDescription(tempAttribute.get(j).getValue());
                    }
                    else if(tempAttribute.get(j).getName().equals("event_category"))
                    {
                        eventList[i].setEventCategory(tempAttribute.get(j).getValue());
                    }
                }
            }
            return eventList;
        }
        catch( Exception eex)
        {
            throw new Exception("FIRST EXCEPTION", eex);
        }
    }

    @Override
    protected Event[] doInBackground(Void... params) {
        // TODO Auto-generated method stub
        try {
            return getAllEvents();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
