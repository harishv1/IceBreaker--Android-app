package com.example.faceeventapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by rashmitonge on 12/20/14.
 */
public class Event {
    private String name;
    private Date date;
    private String description;
    private String venue;
    private String category;
    private DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH-mm");
    private List Attendee = new ArrayList<String>();
    private Boolean currentUserStatus = false;

    public void setEventName(String event_name)
    {
        name = event_name;
    }
    public void setEventDate(String event_date,String event_time)
    {
        try {
            date = df.parse(event_date + " " + event_time);
        }catch(Exception e)
        {

        }
    }

    public void setEventDate(String event_date)
    {
        try {
            date = df.parse(event_date);
        }
        catch(Exception e)
        {

        }
    }
    public void setEventVenue(String event_venue)
    {
        venue = event_venue;
    }
    public void setEventDescription(String event_description)
    {
        description = event_description;
    }
    public void setEventCategory(String event_category)
    {
        category = event_category;

    }
    public void addAttendee(String user_name)
    {
        Attendee.add(user_name);
    }
    public void setCurrentUserStatus(Boolean status)
    {
        currentUserStatus = status;
    }

    public String getEventName()
    {
        return name;
    }
    public String getEventDate()
    {
        return df.format(date);
    }
    public String getEventVenue()
    {
        return venue;
    }
    public String getEventDescription()
    {
        return description;
    }
    public String getEventCategory()
    {
        return category;
    }
    public List getAllAttendees()
    {
        return Attendee;
    }
    public Boolean getCurrentUserStatus()
    {
        return currentUserStatus;
    }

}
