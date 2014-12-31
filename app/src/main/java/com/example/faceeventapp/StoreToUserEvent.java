package com.example.faceeventapp;

/**
 * Created by rashmitonge on 12/21/14.
 */
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;

import android.os.AsyncTask;
import android.util.Log;

public class StoreToUserEvent extends AsyncTask<String, Void, Void> {



    public static void setUserEvent(String event_name,String user_name)
    {
        try {
            Connection.getAwsSimpleDB().createDomain(new CreateDomainRequest("user_event"));
            List<ReplaceableAttribute> attribute = new ArrayList<ReplaceableAttribute>(1);
            attribute.add(new ReplaceableAttribute().withName("user_name").withValue(user_name));
            attribute.add(new ReplaceableAttribute().withName("event_name").withValue(event_name));
            attribute.add(new ReplaceableAttribute().withName("user_event_id").withValue(user_name + "_" + event_name));
            Connection.awsSimpleDB.putAttributes(new
                    PutAttributesRequest("user_event", user_name + "_" + event_name, attribute));
        }
        catch(Exception e)
        {

        }
    }


    @Override
    protected Void doInBackground(String... params) {
        // TODO Auto-generated method stub
        setUserEvent(params[0], params[1]);
        return null;
    }

}

