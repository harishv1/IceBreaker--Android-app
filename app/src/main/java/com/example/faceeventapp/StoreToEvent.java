package com.example.faceeventapp;

import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class StoreToEvent extends AsyncTask<Event, Void, Void>  {

    // 2. Create Domain and save movie information in domain
    public static void saveEvent(Event event)
    {
        try {

            Connection.getAwsSimpleDB().createDomain(new CreateDomainRequest( "event_info"));
            List<ReplaceableAttribute> attribute= new ArrayList<ReplaceableAttribute>(1);
            attribute.add(new ReplaceableAttribute().withName("event_name").withValue(event.getEventName()));
            attribute.add(new ReplaceableAttribute().withName("event_date").withValue(event.getEventDate()));
            attribute.add(new ReplaceableAttribute().withName("event_description").withValue(event.getEventDescription()));
            attribute.add(new ReplaceableAttribute().withName("event_category").withValue(event.getEventCategory()));
            attribute.add(new ReplaceableAttribute().withName("event_venue").withValue(event.getEventVenue()));

            Connection.awsSimpleDB.putAttributes(new PutAttributesRequest("event_info", event.getEventName(), attribute));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected Void doInBackground(Event... params) {
        // TODO Auto-generated method stub
        saveEvent(params[0]);
        return null;
    }




}
