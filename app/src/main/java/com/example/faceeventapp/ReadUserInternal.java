package com.example.faceeventapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.ContextWrapper;

import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.Item;

import java.io.File;
import java.util.List;

/**
 * Created by rashmitonge on 12/20/14.
 */
public class ReadUserInternal extends AsyncTask<User,Void,User> {

    public static User checkLogin(User user) throws Exception
    {
        User profile = new User();
        SelectRequest selectRequest=  new SelectRequest("select * from users_info where user_name = '" +
                user.username + "'").withConsistentRead(true);

        List<Item> items  = Connection.getAwsSimpleDB().select(selectRequest).getItems();

        try {

            com.amazonaws.services.simpledb.model.Item temp1 =
                    ((com.amazonaws.services.simpledb.model.Item)items.get(0));
            List<com.amazonaws.services.simpledb.model.Attribute> tempAttribute= temp1.getAttributes();
            for(int i=0;i<tempAttribute.size();i++)
            {
                if(tempAttribute.get(i).getName().equals("interests"))
                {
                    profile.interests = tempAttribute.get(i).getValue();
                }
                if(tempAttribute.get(i).getName().equals("technology"))
                {
                    profile.technology = tempAttribute.get(i).getValue();
                }
                if(tempAttribute.get(i).getName().equals("fashion"))
                {
                    profile.fashion = tempAttribute.get(i).getValue();
                }
                if(tempAttribute.get(i).getName().equals("sports"))
                {
                    profile.sports = tempAttribute.get(i).getValue();
                }
                if(tempAttribute.get(i).getName().equals("food"))
                {
                    profile.food = tempAttribute.get(i).getValue();
                }
                if(tempAttribute.get(i).getName().equals("user_name"))
                {
                    profile.username = tempAttribute.get(i).getValue();

                }
            }
            return profile;
        }
        catch( Exception e)
        {
            throw new Exception("First Exception",e);
        }

    }

    @Override
    protected User doInBackground(User... params) {
        // TODO Auto-generated method stub
        try {
            return checkLogin(params[0]);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
