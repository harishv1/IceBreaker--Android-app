package com.example.faceeventapp;

/**
 * Created by rashmitonge on 12/21/14.
 */
import java.util.List;

import com.amazonaws.services.simpledb.model.SelectRequest;

import android.os.AsyncTask;
import android.util.Log;

public class ReadCurrentEvent extends AsyncTask<String, Void, Event> {



    public static Event getCurrentEvent(String event_name,String user_name) throws Exception
    {
        SelectRequest selectRequest1=  new SelectRequest
                ("select * from event_info where event_name='" + event_name + "'").withConsistentRead(true);

        List<com.amazonaws.services.simpledb.model.Item> items1  =
                Connection.getAwsSimpleDB().select(selectRequest1).getItems();

        try
        {
            com.amazonaws.services.simpledb.model.Item temp1;
            Event event = getEventDetails(((com.amazonaws.services.simpledb.model.Item)items1.get(0)));
            SelectRequest selectRequest =  new SelectRequest
                    ("select * from user_event where event_name='" + event_name + "'").withConsistentRead(true);
            List<com.amazonaws.services.simpledb.model.Item> items  =
                    Connection.getAwsSimpleDB().select(selectRequest).getItems();
            try {
                if(items.isEmpty()){return event;}

                int size = items.size();

                for (int i = 0; i < size; i++) {
                    temp1 = ((com.amazonaws.services.simpledb.model.Item) items.get(i));

                    List<com.amazonaws.services.simpledb.model.Attribute> tempAttribute = temp1.getAttributes();
                    for (int j = 0; j < tempAttribute.size(); j++) {
                        if (tempAttribute.get(j).getName().equals("user_name")) {
                            event.addAttendee(tempAttribute.get(j).getValue());
                            if(tempAttribute.get(j).getValue().equals(user_name))
                            {
                                event.setCurrentUserStatus(true);
                            }
                        }

                    }
                }
            }
            catch(Exception e)
            {

            }
            return event;
        }
        catch( Exception eex)
        {
            throw new Exception("FIRST EXCEPTION", eex);
        }
    }

    public static Event getEventDetails(com.amazonaws.services.simpledb.model.Item item)
    {
        Event event = new Event();
        List<com.amazonaws.services.simpledb.model.Attribute> attr = item.getAttributes();
        for(int j=0;j<attr.size();j++)
        {
            if(attr.get(j).getName().equals("event_name"))
            {
                event.setEventName(attr.get(j).getValue());
            }
            else if(attr.get(j).getName().equals("event_venue"))
            {
                event.setEventVenue(attr.get(j).getValue());
            }
            else if(attr.get(j).getName().equals("event_date"))
            {
                event.setEventDate(attr.get(j).getValue());
            }
            else if(attr.get(j).getName().equals("event_description"))
            {
                event.setEventDescription(attr.get(j).getValue());
            }
            else if(attr.get(j).getName().equals("event_category"))
            {
                event.setEventCategory(attr.get(j).getValue());
            }
        }
        return event;
    }

    @Override
    protected Event doInBackground(String... params) {
        // TODO Auto-generated method stub
        try {
            return getCurrentEvent(params[0],params[1]);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
