package com.example.faceeventapp;

/**
 * Created by rashmitonge on 12/21/14.
 */
import java.util.List;

import com.amazonaws.services.simpledb.model.SelectRequest;

import android.os.AsyncTask;

public class ReadAllEventUsers extends AsyncTask<String, Void, User[]> {



    public static User [] getAllEventUsers(String event_name) throws Exception
    {
        SelectRequest selectRequest=  new SelectRequest("select * from user_event where event_name = '" +
                event_name + "'").withConsistentRead(true);

        List<com.amazonaws.services.simpledb.model.Item> items  = Connection.getAwsSimpleDB().select(selectRequest).getItems();

        try
        {
            com.amazonaws.services.simpledb.model.Item temp1;
            int size= items.size();
            User [] userList= new  User[size];

            for(int i=0; i<size;i++)
            {
                temp1= ((com.amazonaws.services.simpledb.model.Item)items.get( i ));

                List<com.amazonaws.services.simpledb.model.Attribute> tempAttribute= temp1.getAttributes();
                userList[i]= new User();
                for(int j=0; j< tempAttribute.size();j++)
                {
                    if(tempAttribute.get(j).getName().equals("user_name"))
                    {
                        userList[i].username = tempAttribute.get(j).getValue();
                    }
                }
            }
            return userList;
        }
        catch( Exception eex)
        {
            throw new Exception("FIRST EXCEPTION", eex);
        }
    }

    @Override
    protected User [] doInBackground(String... params) {
        // TODO Auto-generated method stub
        try {
            return getAllEventUsers(params[0]);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
