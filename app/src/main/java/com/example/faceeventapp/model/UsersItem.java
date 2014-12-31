package com.example.faceeventapp.model;

import android.util.Log;

import com.example.faceeventapp.Event;
import com.example.faceeventapp.ReadAllEventUsers;
import com.example.faceeventapp.ReadUser;
import com.example.faceeventapp.ReadUserInternal;
import com.example.faceeventapp.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rashmitonge on 12/21/14.
 */
public class UsersItem {
    private User[] userList;
    public int length;

    public UsersItem(String event_name)
    {
        try {
            userList = new ReadAllEventUsers().execute(event_name).get();
//            eventList = new Event[5];
//            Event event = new Event();
//            event.setEventName("test");
//            event.setEventCategory("fashion");
//            for(int i=0;i<5;i++){
//                eventList[i] = event;
//            }
            length = userList.length;
            for(int i=0;i<length;i++)
            {
//                Log.d("Rashmi", "Beofre user : " + userList[i].username);
                userList[i] = new ReadUserInternal().execute(userList[i]).get();
//                Log.d("Rashmi", "After user : " + userList[i].username);

            }
        }catch(Exception e)
        {

        }
    }

    public User getUserItem(int position)
    {
        return userList[position];
    }
}
